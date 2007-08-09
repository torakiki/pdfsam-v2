/*
 * Created on 14-Nov-2006
 * 
 * This program is free software; you can redistribute it and/or modify it under the terms of the 
 * GNU General Public License as published by the Free Software Foundation; 
 * either version 2 of the License.
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; 
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. 
 * See the GNU General Public License for more details.
 * You should have received a copy of the GNU General Public License along with this program; 
 * if not, write to the Free Software Foundation, Inc., 
 *  59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 */
package it.pdfsam.plugin.coverfooter.thread;

import it.pdfsam.console.MainConsole;
import it.pdfsam.console.tools.HtmlTags;
import it.pdfsam.gnu.gettext.GettextResource;
import it.pdfsam.panels.LogPanel;
import it.pdfsam.plugin.coverfooter.GUI.CoverFooterMainGUI;

import java.util.List;
import java.util.ResourceBundle;

/**
 * Thread to merge pdf items
 * @author Andrea Vacondio
 * 
 */
public class DoJobThread implements Runnable {
		private MainConsole mc;
		private CoverFooterMainGUI container;
		private List args;
		private ResourceBundle i18n_messages;
		
		public DoJobThread(MainConsole mc, CoverFooterMainGUI container, List args, ResourceBundle i18n_messages){
			this.mc = mc;
			this.container = container;
			this.args = args;
			this.i18n_messages = i18n_messages;
		}
		
		public void run() {
			String[] myStringArray = (String[])args.toArray(new String[args.size()]);
            synchronized(mc){
				try{
		            container.addWipText(GettextResource.gettext(i18n_messages,"Please wait while all files are processed.."));
		            String out_msg = mc.mainAction(myStringArray, true);
		            container.removeWipText(GettextResource.gettext(i18n_messages,"Please wait while all files are processed.."));
					container.fireLogPropertyChanged("Command Line: "+args.toString() , LogPanel.LOG_DETAILEDINFO);
					container.fireLogPropertyChanged(out_msg , LogPanel.LOG_INFO);
                }catch(Exception any_ex){
                	container.removeWipTextAll();
                    container.fireLogPropertyChanged("Command Line: "+args.toString()+"<br>Exception "+HtmlTags.disable(any_ex.toString()), LogPanel.LOG_ERROR);
                }
			}
        }
}

