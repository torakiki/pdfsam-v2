/*
 * Created on 02-Nov-2007
 * Copyright (C) 2007 by Andrea Vacondio.
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
package org.pdfsam.console.business;

import java.util.Observer;

import org.apache.log4j.Logger;
import org.pdfsam.console.business.dto.commands.AbstractParsedCommand;
import org.pdfsam.console.business.parser.CmdParseManager;
import org.pdfsam.console.business.pdf.CmdExecuteManager;
import org.pdfsam.console.exceptions.console.ConsoleException;
/**
 * Facade for the console services
 * @author Andrea Vacondio
 *
 */
public class ConsoleServicesFacade {
	
	private final Logger log = Logger.getLogger(ConsoleServicesFacade.class.getPackage().getName());
	
    public static final String VERSION = "2.0.3e"; 
    public static final String CREATOR = "pdfsam-console (Ver. " +ConsoleServicesFacade.VERSION+ ")";   
    public static final String LICENSE =
		ConsoleServicesFacade.CREATOR+"  Copyright (C) 2007  Andrea Vacondio\n"+
		"This library is provided under dual licenses.\n"+
		"You may choose the terms of the Lesser General Public License version 2.1 or the General Public License version 2\n"+
		"License at your discretion.\n\n"+
		"This library is free software; you can redistribute it and/or\n"+
		"modify it under the terms of the GNU Lesser General Public\n"+
		"License as published by the Free Software Foundation;\n"+
		"version 2.1 of the License.\n\n"+
		"This library is distributed in the hope that it will be useful,\n"+
		"but WITHOUT ANY WARRANTY; without even the implied warranty of\n"+
		"MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU\n"+
		"Lesser General Public License for more details.\n\n"+
		"You should have received a copy of the GNU Lesser General Public\n"+
		"License along with this library; if not, write to the Free Software\n"+
		"Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301  USA\n\n\n"+
		"This program is free software: you can redistribute it and/or modify\n"+
		"it under the terms of the GNU General Public License as published by\n"+
		"the Free Software Foundation,version 2 of the License\n\n"+
		"This program is distributed in the hope that it will be useful,\n"+
		"but WITHOUT ANY WARRANTY; without even the implied warranty of\n"+
		"MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the\n"+
		"GNU General Public License for more details.\n\n"+
		"You should have received a copy of the GNU General Public License\n"+
		"along with this program.  If not, see <http://www.gnu.org/licenses/>..\n"+
		"This is free software, and you are welcome to redistribute it\n"+
		"under certain conditions;\n";
	
    private CmdParseManager cmdParserManager;
    private CmdExecuteManager cmdExecuteManager;
    
	public ConsoleServicesFacade() {
		cmdParserManager = new CmdParseManager();
		cmdExecuteManager = new CmdExecuteManager();
	}
    /**
     * execute parsedCommand
     * @param parsedCommand
     * @throws Exception. This way we don't have dependencies from emp4j client side
     */
	public synchronized void execute(AbstractParsedCommand parsedCommand) throws Exception{
		try{
			cmdExecuteManager.execute(parsedCommand);
		}catch(ConsoleException ce){
			throw new Exception(ce);
		}
	}
    
	/**
	 * parse and validate the input arguments 
	 * @param inputArguments input string arguments
	 * @return the parsed command
     * @throws Exception. This way we don't have dependencies from emp4j client side
	 */
	public synchronized AbstractParsedCommand parseAndValidate(String[] inputArguments) throws Exception{
		try{
			AbstractParsedCommand retVal = null;
			if (cmdParserManager.parse(inputArguments)){
				retVal = cmdParserManager.validate();
			}else{
				log.error("Parse failed.");
			}
			return retVal;
		}catch(ConsoleException ce){
			throw new Exception(ce);
		}
	}
	
	/**
	 * Adds an observer that observe the execution. No duplicate allowed.
	 * @param observer
	 * @throws NullPointerException if the observer is null
	 */
	public synchronized void addExecutionObserver(Observer observer) throws NullPointerException{
		cmdExecuteManager.addObserver(observer);
	}
}
