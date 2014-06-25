/*
 * Created on 02-Nov-2007
 * Copyright (C) 2007 by Andrea Vacondio.
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
package org.pdfsam.console;

import org.apache.log4j.ConsoleAppender;
import org.apache.log4j.FileAppender;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.PatternLayout;
import org.pdfsam.console.business.ConsoleServicesFacade;
import org.pdfsam.console.business.dto.commands.AbstractParsedCommand;

/**
 * Shell client for the console
 * 
 * @author Andrea Vacondio
 * 
 */
public class ConsoleClient {

    private static final Logger LOG = Logger.getLogger(ConsoleClient.class.getPackage().getName());

    // config properties
    private static final String consoleLogLevelProperty = "pdfsam.log.console.level";
    private static final String fileLogLevelProperty = "pdfsam.log.file.level";
    private static final String filenameLogLevelProperty = "pdfsam.log.file.filename";

    private static final String CONSOLE_APPENDER_NAME = "CONSOLE";
    private static final String FILE_APPENDER_NAME = "FILE";

    private static ConsoleServicesFacade serviceFacade;

    /**
     * @param args
     */
    public static void main(String[] args) {
        initLoggingFramework();
        try {
            if (args == null || args.length == 0) {
                args = new String[] { "-help" };
            }
            serviceFacade = new ConsoleServicesFacade();
            if (serviceFacade != null) {
                AbstractParsedCommand parsedCommand = serviceFacade.parseAndValidate(args);
                if (parsedCommand != null) {
                    serviceFacade.execute(parsedCommand);
                }
            } else {
                LOG.fatal("Unable to reach services, service facade is null.");
            }
        } catch (Throwable t) {
            LOG.fatal("Error executing ConsoleClient", t);
            System.exit(1);
        }
    }

    /**
     * initialization of the logging framework
     */
    private static void initLoggingFramework() {
        try {
            String consoleLevel = System.getProperty(consoleLogLevelProperty, "DEBUG");
            String fileLevel = System.getProperty(fileLogLevelProperty, "DEBUG");
            String fileName = System.getProperty(filenameLogLevelProperty);

            // console appender level configuration
            ConsoleAppender consoleAppender = (ConsoleAppender) Logger.getRootLogger().getAppender(
                    CONSOLE_APPENDER_NAME);
            if (consoleAppender != null) {
                Level consoleThreshold = Level.toLevel(consoleLevel, Level.DEBUG);
                consoleAppender.setThreshold(consoleThreshold);
                LOG.debug("Console LOG level set to " + consoleThreshold);
            }

            if (fileName != null) {
                PatternLayout layout = new PatternLayout("%d{ABSOLUTE} %-5p %x %m%n");
                FileAppender fileAppender = new FileAppender(layout, fileName, false);
                fileAppender.setName(FILE_APPENDER_NAME);
                Level fileThreshold = Level.toLevel(fileLevel, Level.DEBUG);
                fileAppender.setThreshold(fileThreshold);
                Logger.getRootLogger().addAppender(fileAppender);
                LOG.debug("Added fileAppender (" + fileName + ") at level " + fileThreshold);
            }
        } catch (Exception e) {
            System.err.println("Error configuring logging framework: " + e.getMessage());
        }
    }

}
