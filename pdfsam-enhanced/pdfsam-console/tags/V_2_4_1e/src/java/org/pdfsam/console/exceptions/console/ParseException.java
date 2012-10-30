/*
 * Created on 20-Jun-2007
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
package org.pdfsam.console.exceptions.console;

/**
 * Exception thrown while parsing input arguments
 * 
 * @author Andrea Vacondio
 * 
 */
public class ParseException extends ConsoleException {

    public static final int ERR_PARSE = 0x01;
    // public static final int ERR_OUT_NOT_PDF = 0x02;
    public static final int ERR_NO_OUT = 0x03;
    public static final int ERR_NO_F_OR_L_OR_D = 0x04;
    public static final int ERR_BOTH_F_OR_L_OR_D = 0x05;
    public static final int ERR_NOT_CSV_OR_XML = 0x06;
    public static final int ERR_IN_NOT_PDF = 0x07;
    // public static final int ERR_ILLEGAL_U = 0x08;
    // public static final int ERR_OUT_NOT_DIR = 0x09;
    public static final int ERR_NO_S = 0x0A;
    public static final int ERR_N_NOT_NUM = 0x0B;
    public static final int ERR_N_NOT_NUM_OR_SEQ = 0x0C;
    public static final int ERR_NO_N = 0x0D;
    public static final int ERR_N_NOT_NEEDED = 0x0E;
    public static final int ERR_NO_O = 0x0F;
    public static final int ERR_NO_F1 = 0x10;
    public static final int ERR_NO_F2 = 0x11;
    public static final int ERR_B_NOT_NEEDED = 0x12;
    public static final int ERR_NO_B = 0x13;
    public static final int ERR_NO_F = 0x14;
    // public static final int ERR_D_NOT_DIR = 0x15;
    public static final int ERR_NOT_XML = 0x16;
    public static final int ERR_BL_NOT_NEEDED = 0x17;
    public static final int ERR_NO_BL = 0x18;
    public static final int ERR_NO_F_OR_D = 0x19;
    public static final int ERR_BOTH_F_OR_D = 0x1A;
    public static final int ERR_STEP_ZERO_OR_NEGATIVE = 0x1B;
    public static final int ERR_BREGEXP_NOT_NEEDED = 0x1C;

    private static final long serialVersionUID = -3982153307443637295L;

    public ParseException(final int exceptionErrorCode, final String[] args, final Throwable e) {
        super(exceptionErrorCode, args, e);
    }

    public ParseException(final int exceptionErrorCode, final Throwable e) {
        super(exceptionErrorCode, e);
    }

    public ParseException(final int exceptionErrorCode) {
        super(exceptionErrorCode);
    }

    public ParseException(final Throwable e) {
        super(e);
    }

    public ParseException(final int exceptionErrorCode, final String[] args) {
        super(exceptionErrorCode, args);
    }

}
