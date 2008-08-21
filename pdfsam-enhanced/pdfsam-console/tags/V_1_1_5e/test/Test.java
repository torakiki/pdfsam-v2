import org.pdfsam.console.business.dto.commands.AbstractParsedCommand;
import org.pdfsam.console.business.dto.commands.ConcatParsedCommand;
import org.pdfsam.console.business.parser.CmdParseManager;
import org.pdfsam.console.business.pdf.CmdExecuteManager;


public class Test {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		try{
		String[] input = new String[]{"-pdfversion", "s", "-o", "/media/LACIE/test/testmerge.pdf", "-f", "/media/LACIE/test/001_test.pdf", "-f", "/media/LACIE/test/053_test.pdf","-f","/media/LACIE/test/103_test.pdf", "-u", "all:all:all:","-overwrite", "concat"};
		CmdParseManager cmd = new CmdParseManager(input);
		boolean parsed = cmd.parse();
		AbstractParsedCommand pcmd = cmd.validate();
		CmdExecuteManager cem = new CmdExecuteManager();
		cem.execute(pcmd);
/*		
		String[] input3 = new String[]{"-pdfversion", "3", "-f", "/media/LACIE/test/test.pdf", "-o", "/media/LACIE/test", "-s", "SIZE", "-b", "1000000","split"};
		CmdParseManager cmd3 = new CmdParseManager(input3);
		boolean parsed3 = cmd3.parse();
		AbstractParsedCommand pcmd3 = cmd3.validate();
		CmdExecuteManager cem = new CmdExecuteManager();
		cem.execute(pcmd3);
*/
		}catch(Exception e){
			e.printStackTrace();
		}
	}

}
