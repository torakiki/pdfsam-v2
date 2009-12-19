import junit.framework.TestCase;
import org.pdfsam.console.business.dto.commands.AbstractParsedCommand;
import org.pdfsam.console.business.dto.commands.ConcatParsedCommand;
import org.pdfsam.console.business.parser.CmdParseManager;


public class CmdParserTest extends TestCase {

	public void testParse() {
		try{
			/*String[] input = new String[]{"-overwrite", "-o", "c:\test.pdf", "-f1", "f:/pdf/xhtml1.pdf","-f2","f:/pdf/xhtml11.pdf", "-reversesecond", "mix"};
			CmdParseManager cmd = new CmdParseManager(input);
			boolean parsed = cmd.parse();
			assertEquals(parsed,true);
			AbstractParsedCommand pcmd = cmd.validate();
			assertEquals(pcmd.getCommand(),MixParsedCommand.COMMAND_MIX);
		
			String[] input2 = new String[]{"-h"};
			CmdParseManager cmd2 = new CmdParseManager(input2);
			boolean parsed2 = cmd2.parse();
			assertEquals(parsed2,true);
			AbstractParsedCommand pcmd2 = cmd2.validate();
			assertEquals(pcmd2.getCommand(),ConcatParsedCommand.COMMAND_CONCAT);		
			*/
			String[] input3 = new String[]{"-f", "f:/pdf/xhtml1.pdf", "-o", "c:/tmp", "-s", "SIZE", "-b", "34894245876","split"};
			CmdParseManager cmd3 = new CmdParseManager(input3);
			boolean parsed3 = cmd3.parse();
			assertEquals(parsed3,true);
			AbstractParsedCommand pcmd3 = cmd3.validate();
			assertEquals(pcmd3.getCommand(),ConcatParsedCommand.COMMAND_SPLIT);	
		}
		catch(Exception e){
			e.printStackTrace();
		}
	}

}
