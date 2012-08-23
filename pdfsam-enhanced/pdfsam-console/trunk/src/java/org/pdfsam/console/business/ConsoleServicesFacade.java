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
 * 
 * @author Andrea Vacondio
 * 
 */
public class ConsoleServicesFacade {

    private static final Logger LOG = Logger.getLogger(ConsoleServicesFacade.class.getPackage().getName());

    public static final String VERSION = "2.4.1e";
    public static final String CREATOR = "pdfsam-console (Ver. " + ConsoleServicesFacade.VERSION + ")";

    private CmdParseManager cmdParserManager;
    private CmdExecuteManager cmdExecuteManager;

    public ConsoleServicesFacade() {
        cmdParserManager = new CmdParseManager();
        cmdExecuteManager = new CmdExecuteManager();
    }

    /**
     * execute parsedCommand
     * 
     * @param parsedCommand
     * @throws ConsoleException.
     */
    public synchronized void execute(AbstractParsedCommand parsedCommand) throws ConsoleException {
        cmdExecuteManager.execute(parsedCommand);
    }

    /**
     * parse and validate the input arguments
     * 
     * @param inputArguments
     *            input string arguments
     * @return the parsed command
     * @throws Exception.
     */
    public synchronized AbstractParsedCommand parseAndValidate(String[] inputArguments) throws Exception {
        try {
            AbstractParsedCommand retVal = null;
            if (cmdParserManager.parse(inputArguments)) {
                retVal = cmdParserManager.validate();
            } else {
                LOG.error("Parse failed.");
            }
            return retVal;
        } catch (ConsoleException ce) {
            throw new Exception(ce);
        }
    }

    /**
     * Adds an observer that observe the execution. No duplicate allowed.
     * 
     * @param observer
     * @throws NullPointerException
     *             if the observer is null
     */
    public synchronized void addExecutionObserver(Observer observer) throws NullPointerException {
        cmdExecuteManager.addObserver(observer);
    }

    /**
     * @return the License String
     */
    public static String getLicense() {
        StringBuffer sb = new StringBuffer();

        sb.append(ConsoleServicesFacade.CREATOR + "  Copyright (C) 2007  Andrea Vacondio\n");
        sb.append("This library is provided under dual license.\n");
        sb
                .append("You may choose the terms of the Lesser General Public License version 2.1 or the General Public License version 2\n");
        sb.append("License at your discretion.\n\n");
        sb.append("This library is free software; you can redistribute it and/or\n");
        sb.append("modify it under the terms of the GNU Lesser General Public\n");
        sb.append("License as published by the Free Software Foundation;\n");
        sb.append("version 2.1 of the License.\n\n");
        sb.append("This library is distributed in the hope that it will be useful,\n");
        sb.append("but WITHOUT ANY WARRANTY; without even the implied warranty of\n");
        sb.append("MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU\n");
        sb.append("Lesser General Public License for more details.\n\n");
        sb.append("You should have received a copy of the GNU Lesser General Public\n");
        sb.append("License along with this library; if not, write to the Free Software\n");
        sb.append("Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301  USA\n\n\n");
        sb.append("This program is free software: you can redistribute it and/or modify\n");
        sb.append("it under the terms of the GNU General Public License as published by\n");
        sb.append("the Free Software Foundation,version 2 of the License\n\n");
        sb.append("This program is distributed in the hope that it will be useful,\n");
        sb.append("but WITHOUT ANY WARRANTY; without even the implied warranty of\n");
        sb.append("MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the\n");
        sb.append("GNU General Public License for more details.\n\n");
        sb.append("You should have received a copy of the GNU General Public License\n");
        sb.append("along with this program.  If not, see <http://www.gnu.org/licenses/>..\n");
        sb.append("This is free software, and you are welcome to redistribute it\n");
        sb.append("under certain conditions;\n");

        return sb.toString();
    }
}
