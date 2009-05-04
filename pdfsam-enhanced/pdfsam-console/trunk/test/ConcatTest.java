import java.io.File;
import java.io.FileOutputStream;

import org.pdfsam.console.business.ConsoleServicesFacade;
import org.pdfsam.console.business.pdf.writers.PdfSimpleConcatenator;

import com.lowagie.text.Document;
import com.lowagie.text.pdf.PdfReader;
import com.lowagie.text.pdf.RandomAccessFileOrArray;


public class ConcatTest {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		try{
		PdfReader pdfReader = new PdfReader(new RandomAccessFileOrArray("/home/torakiki/pdfsam-1.1.0-tutorial.pdf"),null);
		pdfReader.removeUnusedObjects();
		pdfReader.consolidateNamedDestinations();
		int pdfNumberOfPages = pdfReader.getNumberOfPages();
		Document pdfDocument = new Document(pdfReader.getPageSizeWithRotation(1));
         // step 2: we create a writer that listens to the document
		PdfSimpleConcatenator pdfWriter = new PdfSimpleConcatenator(pdfDocument, new FileOutputStream(new File("/home/torakiki/pdfsam-1.1.0-tutorial_new.pdf")), true);
		 // step 3: we open the document
        pdfDocument.addCreator(ConsoleServicesFacade.CREATOR);
        pdfDocument.open();
        pdfReader.selectPages("33,32,31,30,29,28,27,26,25,24,23,22,21,20,19,18,17,16,15,14,13,12,11,10,9,8,7,6,5,4,3,2,1");
		pdfWriter.addDocument(pdfReader); 
		//fix 03/07
		//pdfReader = null;
        pdfReader.close();
        pdfWriter.freeReader(pdfReader);
        pdfDocument.close();        	        
		pdfWriter.close();
		}catch(Exception e){
			e.printStackTrace();
		}
	}

}
