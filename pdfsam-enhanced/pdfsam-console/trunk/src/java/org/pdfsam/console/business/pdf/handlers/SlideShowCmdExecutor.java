/*
 * Created on 06-Mar-2008
 * Copyright (C) 2008 by Andrea Vacondio.
 *
 *
 * This library is provided under dual licenses.
 * You may choose the terms of the Lesser General Public License version 2.1 or the General Public License version 2
 * License at your discretion.
 * 
 * 
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation;
 * version 2.1 of the License.
 * 
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301  USA
 * 
 * 
 * 
 * This program is free software; you can redistribute it and/or modify it under the terms of the 
 * GNU General Public License as published by the Free Software Foundation; 
 * either version 2 of the License.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; 
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. 
 * See the GNU General Public License for more details.
 * You should have received a copy of the GNU General Public License along with this program; 
 * if not, write to the Free Software Foundation, Inc., 
 *  59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 */
package org.pdfsam.console.business.pdf.handlers;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;

import org.apache.log4j.Logger;
import org.dom4j.Document;
import org.dom4j.Node;
import org.dom4j.io.SAXReader;
import org.pdfsam.console.business.ConsoleServicesFacade;
import org.pdfsam.console.business.dto.Transition;
import org.pdfsam.console.business.dto.WorkDoneDataModel;
import org.pdfsam.console.business.dto.commands.AbstractParsedCommand;
import org.pdfsam.console.business.dto.commands.SlideShowParsedCommand;
import org.pdfsam.console.business.pdf.handlers.interfaces.AbstractCmdExecutor;
import org.pdfsam.console.exceptions.console.ConsoleException;
import org.pdfsam.console.exceptions.console.SlideShowException;
import org.pdfsam.console.utils.FileUtility;
import org.pdfsam.console.utils.PrefixParser;

import com.lowagie.text.pdf.PdfReader;
import com.lowagie.text.pdf.PdfStamper;
import com.lowagie.text.pdf.PdfStream;
import com.lowagie.text.pdf.PdfTransition;
import com.lowagie.text.pdf.PdfWriter;
import com.lowagie.text.pdf.RandomAccessFileOrArray;
/**
 * Command executor for the slideshow command
 * @author Andrea Vacondio
 */
public class SlideShowCmdExecutor extends AbstractCmdExecutor {

	private final Logger log = Logger.getLogger(SlideShowCmdExecutor.class.getPackage().getName());
	
	private Hashtable transitionsMappingMap = null;
	
	public void execute(AbstractParsedCommand parsedCommand) throws ConsoleException {
		
		if((parsedCommand != null) && (parsedCommand instanceof SlideShowParsedCommand)){	
			
			SlideShowParsedCommand inputCommand = (SlideShowParsedCommand) parsedCommand;
			PdfReader pdfReader;	
			PdfStamper pdfStamper;
			PrefixParser prefixParser;
			setPercentageOfWorkDone(0);			
			Transitions transitions = new Transitions();
			transitions.setDefaultTransition(inputCommand.getDefaultTransition());
			transitions.setTransitions(inputCommand.getTransitions());			
			transitions = parseXmlInput(inputCommand.getInputXmlFile(), transitions);
			try{
				prefixParser = new PrefixParser(inputCommand.getOutputFilesPrefix(), inputCommand.getInputFile().getFile().getName());				
				File tmpFile = FileUtility.generateTmpFile(inputCommand.getOutputFile());
				
				pdfReader = new PdfReader(new RandomAccessFileOrArray(inputCommand.getInputFile().getFile().getAbsolutePath()),inputCommand.getInputFile().getPasswordBytes());
				
				//version
				log.debug("Creating a new document.");
				Character pdfVersion = inputCommand.getOutputPdfVersion(); 
				if(pdfVersion != null){
					pdfStamper = new PdfStamper(pdfReader, new FileOutputStream(tmpFile), inputCommand.getOutputPdfVersion().charValue());
				}else{
					pdfStamper = new PdfStamper(pdfReader, new FileOutputStream(tmpFile), pdfReader.getPdfVersion());
				}
				
				//creator
				HashMap meta = pdfReader.getInfo();
				meta.put("Creator", ConsoleServicesFacade.CREATOR);
				
				//compression
				if(inputCommand.isCompress()){
					pdfStamper.setFullCompression();
					pdfStamper.getWriter().setCompressionLevel(PdfStream.BEST_COMPRESSION);
		        }				
				pdfStamper.setMoreInfo(meta);
				
				//fullscreen
				if(inputCommand.isFullScreen()){
					pdfStamper.setViewerPreferences(PdfWriter.PageModeFullScreen);
				}
				
				//sets transitions
				if(transitions.getDefaultTransition()==null){
					setTransitionsWithoutDefault(transitions, pdfStamper);				
				}else{
					int totalPages = pdfReader.getNumberOfPages();
					setTransitionsWithDefault(transitions, totalPages, pdfStamper);
				}
				
				pdfStamper.close();
				File outFile = new File(inputCommand.getOutputFile() ,prefixParser.generateFileName());
	    		if(FileUtility.renameTemporaryFile(tmpFile, outFile, inputCommand.isOverwrite())){
	    			log.debug("File "+outFile.getCanonicalPath()+" created.");
                } 
	    		pdfReader.close();
	    		log.info("Slide show options set.");
			}catch(Exception e){    		
				throw new SlideShowException(e);
			}finally{
				setWorkCompleted();
			}
		}else{
			throw new ConsoleException(ConsoleException.ERR_BAD_COMMAND);
		}
	}
	
	/**
	 * sets the transitions on the stamper setting the default value if no transition has been set for the page.
	 * @param defaultTransition
	 * @param totalPages
	 * @param transitions
	 * @param pdfStamper
	 */
	private void setTransitionsWithDefault(Transitions transitions, int totalPages, PdfStamper pdfStamper){
		log.debug("Setting transitions with a default value.");
		Transition[] trans = transitions.getTransitions();
		
		Hashtable transitionsMap = new Hashtable(0) ;
		if(trans!=null){
			transitionsMap = new Hashtable(trans.length);
			for(int i=0; i<trans.length; i++){
				transitionsMap.put(new Integer(trans[i].getPageNumber()), trans[i]);
			}
		}		
		
		for(int j=1; j<=totalPages; j++){
			Object rawTransition = transitionsMap.get(new Integer(j));
			if(rawTransition != null){
				Transition transition = (Transition) rawTransition;
				pdfStamper.setDuration(transition.getDuration(), transition.getPageNumber());
				pdfStamper.setTransition(new PdfTransition(getITextTransition(transition.getTransition()),transition.getTransitionDuration()), transition.getPageNumber());
			}else{
				pdfStamper.setDuration(transitions.getDefaultTransition().getDuration(), j);
				pdfStamper.setTransition(new PdfTransition(getITextTransition(transitions.getDefaultTransition().getTransition()),transitions.getDefaultTransition().getTransitionDuration()), j);
			}
			setPercentageOfWorkDone(((j)*WorkDoneDataModel.MAX_PERGENTAGE)/totalPages);
		}
	}
	
	/**
	 * sets the transitions on the stamper when no default transition is set
	 * @param defaultTransition
	 * @param transitions
	 * @param pdfStamper
	 */
	private void setTransitionsWithoutDefault(Transitions transitions, PdfStamper pdfStamper){
		log.debug("Setting transitions without a default value.");
		Transition[] trans = transitions.getTransitions();
		if(trans!=null && trans.length>0){
			for(int i=0; i<trans.length; i++){
				pdfStamper.setDuration(trans[i].getDuration(), trans[i].getPageNumber());
				pdfStamper.setTransition(new PdfTransition(getITextTransition(trans[i].getTransition()),trans[i].getTransitionDuration()), trans[i].getPageNumber());
				setPercentageOfWorkDone(((i+1)*WorkDoneDataModel.MAX_PERGENTAGE)/trans.length);
			}		
		}else{
			log.warn("No transition to set.");
		}
	}

	/**
	 * Maps a pdfsam transition to a iText transition.
	 * If an error occur the PdfTransition.BLINDH is the default transition.
	 * @param transition
	 * @return
	 */
	private int getITextTransition(String transition){
		int retVal = PdfTransition.BLINDH;
		if(transitionsMappingMap == null){
			transitionsMappingMap = new Hashtable(20);
			transitionsMappingMap.put(Transition.T_BLINDH, new Integer(PdfTransition.BLINDH));
			transitionsMappingMap.put(Transition.T_BLINDV, new Integer(PdfTransition.BLINDV));
			transitionsMappingMap.put(Transition.T_BTWIPE, new Integer(PdfTransition.BTWIPE));
			transitionsMappingMap.put(Transition.T_DGLITTER, new Integer(PdfTransition.DGLITTER));
			transitionsMappingMap.put(Transition.T_DISSOLVE, new Integer(PdfTransition.DISSOLVE));
			transitionsMappingMap.put(Transition.T_INBOX, new Integer(PdfTransition.INBOX));
			transitionsMappingMap.put(Transition.T_LRGLITTER, new Integer(PdfTransition.LRGLITTER));
			transitionsMappingMap.put(Transition.T_LRWIPE, new Integer(PdfTransition.LRWIPE));
			transitionsMappingMap.put(Transition.T_OUTBOX, new Integer(PdfTransition.OUTBOX));
			transitionsMappingMap.put(Transition.T_RLWIPE, new Integer(PdfTransition.RLWIPE));
			transitionsMappingMap.put(Transition.T_SPLITHIN, new Integer(PdfTransition.SPLITHIN));
			transitionsMappingMap.put(Transition.T_SPLITHOUT, new Integer(PdfTransition.SPLITHOUT));
			transitionsMappingMap.put(Transition.T_SPLITVIN, new Integer(PdfTransition.SPLITVIN));
			transitionsMappingMap.put(Transition.T_SPLITVOUT, new Integer(PdfTransition.SPLITVOUT));
			transitionsMappingMap.put(Transition.T_TBGLITTER, new Integer(PdfTransition.TBGLITTER));
			transitionsMappingMap.put(Transition.T_TBWIPE, new Integer(PdfTransition.TBWIPE));
		}
		Object iTextTransition = transitionsMappingMap.get(transition);
		if(iTextTransition != null){
			retVal = ((Integer)iTextTransition).intValue();
		}
		return retVal;
	}
	
	/**
	 * Parse the input xml file adding the informations to the Transitions object
	 * @param inputFile
	 * @param transitions
	 * @return
	 */
	private Transitions parseXmlInput(File inputFile, Transitions transitions) throws SlideShowException{
		Transitions retVal = transitions;
		if(inputFile!=null){
			try{
				log.debug("Parsing xml transitions file "+inputFile.getAbsolutePath());		
				SAXReader reader = new SAXReader();
				Document document = reader.read(inputFile);
				Node rootNode = document.selectSingleNode("/transitions");
				if(rootNode != null){
					Node defType = rootNode.selectSingleNode("@defaulttype");
					Node defTransDur = rootNode.selectSingleNode("@defaulttduration");
					Node defDur = rootNode.selectSingleNode("@defaultduration");
					if(defType != null && defTransDur != null && defDur != null){
						if(transitions.getDefaultTransition() != null){
							throw new SlideShowException(SlideShowException.ERR_DEFAULT_TRANSITION_ALREADY_SET);
						}else{
							transitions.setDefaultTransition(new Transition(Transition.EVERY_PAGE, new Integer(defTransDur.getText().trim()).intValue(), defType.getText().trim(), new Integer(defDur.getText().trim()).intValue()));
						}
					}
					List transitionsList = document.selectNodes("/transitions/transition");
					for (int i = 0; transitionsList != null && i < transitionsList.size(); i++) {
						Node transitionNode = (Node) transitionsList.get(i);
						Node type = transitionNode.selectSingleNode("@type");
						Node transDuration = transitionNode.selectSingleNode("@tduration");
						Node duration = transitionNode.selectSingleNode("@duration");
						Node page = transitionNode.selectSingleNode("@pagenumber");
						if(type != null && transDuration != null && duration != null && page != null){							
							transitions.addTransition(new Transition(new Integer(page.getText().trim()).intValue(), new Integer(transDuration.getText().trim()).intValue(), type.getText().trim(), new Integer(duration.getText().trim()).intValue()));							
						}else{
							throw new SlideShowException(SlideShowException.ERR_READING_TRANSITION, new String[] {i+""});
						}
					}
				}else{
					throw new SlideShowException(SlideShowException.ERR_READING_XML_TRANSITIONS_FILE);
				}
			}
			catch(Exception e){
				throw new SlideShowException(SlideShowException.ERR_READING_XML_TRANSITIONS_FILE, e);
			}
		}
		return retVal;
	}
	
	/**
	 * Model for the transitions set
	 * @author Andrea Vacondio
	 *
	 */
	private class Transitions {
		
		private Transition defaultTransition;
		private List transitions;
		
		/**
		 * @param defaultTransition
		 * @param transitions
		 */
		public Transitions(Transition defaultTransition,Transition[] transitions) {
			this.defaultTransition = defaultTransition;
			this.transitions = Arrays.asList(transitions);
		}

		public Transitions() {
			this.defaultTransition = null;
			this.transitions = null;
		}				

		/**
		 * @param defaultTransition
		 * @param transitions
		 */
		public Transitions(Transition defaultTransition, List transitions) {
			super();
			this.defaultTransition = defaultTransition;
			this.transitions = transitions;
		}

		/**
		 * @return the defaultTransition
		 */
		public Transition getDefaultTransition() {
			return defaultTransition;
		}

		/**
		 * @param defaultTransition the defaultTransition to set
		 */
		public void setDefaultTransition(Transition defaultTransition) {
			this.defaultTransition = defaultTransition;
		}

		/**
		 * @return the transitions
		 */
		public Transition[] getTransitions() {
			Transition[] retVal = null;
			if(transitions != null){
				retVal = (Transition[]) transitions.toArray(new Transition[transitions.size()]);
			}
			return retVal;
		}

		/**
		 * @param transitions the transitions to set. If transitions is null nothing happen.
		 */
		public void setTransitions(Transition[] transitions) {
			if(transitions != null){
				this.transitions = Arrays.asList(transitions);
			}
		}

		/**
		 * @param transitions the transitions to set
		 */
		public void setTransitions(List transitions) {
			this.transitions = transitions;
		}
		
		/**
		 * add the transition to the list if not null
		 * @param transition
		 * @return
		 */
		public boolean addTransition(Transition transition){
			boolean retVal = false;
			if(transition!= null){
				if (transitions == null){
					this.transitions = new ArrayList();
				}
				retVal = this.transitions.add(transition);
			}
			return retVal;
		}
	}
}
