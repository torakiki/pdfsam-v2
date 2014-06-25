/*
 * Created on 12-Oct-2007
 * Copyright (C) 2006 by Andrea Vacondio.
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
package org.pdfsam.console.business.parser.validators;

import java.io.File;
import java.util.ArrayList;
import java.util.regex.Pattern;

import jcmdline.CmdLineHandler;
import jcmdline.FileParam;
import jcmdline.IntParam;
import jcmdline.LongParam;
import jcmdline.PdfFileParam;
import jcmdline.StringParam;
import jcmdline.dto.PdfFile;

import org.pdfsam.console.business.dto.commands.AbstractParsedCommand;
import org.pdfsam.console.business.dto.commands.SplitParsedCommand;
import org.pdfsam.console.business.parser.validators.interfaces.AbstractCmdValidator;
import org.pdfsam.console.exceptions.console.ConsoleException;
import org.pdfsam.console.exceptions.console.ParseException;
import org.pdfsam.console.utils.FileUtility;
import org.pdfsam.console.utils.ValidationUtility;

/**
 * CmdValidator for the split command
 * 
 * @author Andrea Vacondio
 */
public class SplitCmdValidator extends AbstractCmdValidator {

    public AbstractParsedCommand validateArguments(CmdLineHandler cmdLineHandler) throws ConsoleException {

        SplitParsedCommand parsedCommandDTO = new SplitParsedCommand();

        if (cmdLineHandler != null) {
            // -o
            FileParam oOption = (FileParam) cmdLineHandler.getOption(SplitParsedCommand.O_ARG);
            if ((oOption.isSet())) {
                File outFile = oOption.getFile();
                ValidationUtility.assertValidDirectory(outFile);
                parsedCommandDTO.setOutputFile(outFile);
            } else {
                throw new ParseException(ParseException.ERR_NO_O);
            }

            // -p
            StringParam pOption = (StringParam) cmdLineHandler.getOption(SplitParsedCommand.P_ARG);
            if (pOption.isSet()) {
                parsedCommandDTO.setOutputFilesPrefix(pOption.getValue());
            }

            // -f
            PdfFileParam fOption = (PdfFileParam) cmdLineHandler.getOption(SplitParsedCommand.F_ARG);
            if (fOption.isSet()) {
                PdfFile inputFile = fOption.getPdfFile();
                ValidationUtility.assertValidPdfExtension(inputFile.getFile().getName());
                parsedCommandDTO.setInputFile(FileUtility.getPdfFile(inputFile));
            } else {
                throw new ParseException(ParseException.ERR_NO_F);
            }

            // -s
            StringParam sOption = (StringParam) cmdLineHandler.getOption(SplitParsedCommand.S_ARG);
            if (sOption.isSet()) {
                parsedCommandDTO.setSplitType(sOption.getValue());
            } else {
                throw new ParseException(ParseException.ERR_NO_S);
            }

            // -b
            LongParam bOption = (LongParam) cmdLineHandler.getOption(SplitParsedCommand.B_ARG);
            if (SplitParsedCommand.S_SIZE.equals(parsedCommandDTO.getSplitType())) {
                if (bOption.isSet()) {
                    parsedCommandDTO.setSplitSize(new Long(bOption.longValue()));
                } else {
                    throw new ParseException(ParseException.ERR_NO_B);
                }
            } else {
                if (bOption.isSet()) {
                    throw new ParseException(ParseException.ERR_B_NOT_NEEDED);
                }
            }

            // -bl
            IntParam blOption = (IntParam) cmdLineHandler.getOption(SplitParsedCommand.BL_ARG);
            StringParam bregexpOption = (StringParam) cmdLineHandler.getOption(SplitParsedCommand.BREGEXP_ARG);
            if (SplitParsedCommand.S_BLEVEL.equals(parsedCommandDTO.getSplitType())) {
                if (blOption.isSet()) {
                    parsedCommandDTO.setBookmarksLevel(new Integer(blOption.intValue()));
                } else {
                    throw new ParseException(ParseException.ERR_NO_BL);
                }
                if(bregexpOption.isSet()){
                    parsedCommandDTO.setBookmarkRegexp(bregexpOption.getValue());
                }
            } else {
                if (blOption.isSet()) {
                    throw new ParseException(ParseException.ERR_BL_NOT_NEEDED);
                }
                if(bregexpOption.isSet()){
                    throw new ParseException(ParseException.ERR_BREGEXP_NOT_NEEDED);
                }
            }

            // -n
            StringParam nOption = (StringParam) cmdLineHandler.getOption(SplitParsedCommand.N_ARG);
            if (SplitParsedCommand.S_NSPLIT.equals(parsedCommandDTO.getSplitType())
                    || SplitParsedCommand.S_SPLIT.equals(parsedCommandDTO.getSplitType())) {
                if (nOption.isSet()) {
                    String nValue = nOption.getValue().trim().replaceAll(",", "-").replaceAll(" ", "-");
                    if (SplitParsedCommand.S_NSPLIT.equals(parsedCommandDTO.getSplitType())) {
                        Pattern p = Pattern.compile("([0-9]+)*");
                        if (!(p.matcher(nValue).matches())) {
                            throw new ParseException(ParseException.ERR_N_NOT_NUM);
                        }
                    }
                    if (SplitParsedCommand.S_SPLIT.equals(parsedCommandDTO.getSplitType())) {
                        Pattern p = Pattern.compile("([0-9]+)([-][0-9]+)*");
                        if (!(p.matcher(nValue).matches())) {
                            throw new ParseException(ParseException.ERR_N_NOT_NUM_OR_SEQ);
                        }
                    }
                    parsedCommandDTO.setSplitPageNumbers(getSplitPageNumbers(nValue));
                } else {
                    throw new ParseException(ParseException.ERR_NO_N);
                }
            } else {
                if (nOption.isSet()) {
                    throw new ParseException(ParseException.ERR_N_NOT_NEEDED);
                }
            }
        } else {
            throw new ConsoleException(ConsoleException.CMD_LINE_HANDLER_NULL);
        }
        return parsedCommandDTO;
    }

    /**
     * Converts a string like num-num-num... in an Integer array
     * 
     * @param nValue
     * @return integer array
     * @throws ParseException
     */
    private Integer[] getSplitPageNumbers(String nValue) throws ParseException {
        ArrayList retVal = new ArrayList();
        try {
            String[] limits = nValue.split("-");
            for (int i = 0; i < limits.length; i++) {
                retVal.add(new Integer(limits[i]));
            }
        } catch (NumberFormatException nfe) {
            throw new ParseException(ParseException.ERR_N_NOT_NUM_OR_SEQ, nfe);
        }
        return (Integer[]) retVal.toArray(new Integer[0]);
    }
}
