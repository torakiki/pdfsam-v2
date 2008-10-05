/*
 * Created on 22-feb-2005
 *
 * Ritorna il LookAndFeel specificato dal file di configurazione
 * Copyright (C) 2006 by Andrea Vacondio.
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
package org.pdfsam.guiclient.utils;

import java.util.LinkedList;

import javax.swing.UIManager;

import org.pdfsam.guiclient.dto.StringItem;

import com.jgoodies.looks.plastic.PlasticLookAndFeel;
import com.jgoodies.looks.plastic.theme.BrownSugar;
import com.jgoodies.looks.plastic.theme.DarkStar;
import com.jgoodies.looks.plastic.theme.DesertBlue;
import com.jgoodies.looks.plastic.theme.DesertGreen;
import com.jgoodies.looks.plastic.theme.DesertRed;
import com.jgoodies.looks.plastic.theme.ExperienceBlue;
import com.jgoodies.looks.plastic.theme.ExperienceGreen;
import com.jgoodies.looks.plastic.theme.Silver;
import com.jgoodies.looks.plastic.theme.SkyBlue;
import com.jgoodies.looks.plastic.theme.SkyBluer;
import com.jgoodies.looks.plastic.theme.SkyGreen;
import com.jgoodies.looks.plastic.theme.SkyKrupp;
import com.jgoodies.looks.plastic.theme.SkyPink;
import com.jgoodies.looks.plastic.theme.SkyYellow;

/**
 * ThemeSelector utility. It provides functions to let the user select the GUI theme
 * @author Andrea Vacondio
 */
public class ThemeUtility {

	/**
	 * @param lafNumber
	 * @return le LookAndFeel
	 */
	public static String getLAF(int lafNumber){
		String ThemeSelected;
		switch (lafNumber) {
	         case 1:  
	         	ThemeSelected = UIManager.getSystemLookAndFeelClassName(); 
	         break;
	         case 2:
	         	ThemeSelected = "javax.swing.plaf.metal.MetalLookAndFeel";
	         break;
	         case 3:
	         	ThemeSelected = "com.jgoodies.looks.plastic.Plastic3DLookAndFeel";
	         break;	
	         case 4:
	         	ThemeSelected = "com.jgoodies.looks.plastic.PlasticLookAndFeel";
	         break;	
	         case 5:
	         	ThemeSelected = "com.jgoodies.looks.plastic.PlasticXPLookAndFeel";
	         break;	
	         case 6:
	         	ThemeSelected = "com.jgoodies.looks.windows.WindowsLookAndFeel";
	         break;
	         default: 
	         	ThemeSelected = UIManager.getCrossPlatformLookAndFeelClassName();
	         break;
		}
		return ThemeSelected;
	}
	
    /**
     * 
     * @return a LinkedList of ListItem objects with the availales LAF
     */
	public static LinkedList<StringItem> getLAFList(){
	    LinkedList<StringItem> retval = new LinkedList<StringItem>();
	    retval.add(new StringItem("0","Java"));
	    retval.add(new StringItem("1","System"));
	    retval.add(new StringItem("2","Metal"));
	    retval.add(new StringItem("3","Plastic3D"));
	    retval.add(new StringItem("4","Plastic"));
	    retval.add(new StringItem("5","PlasticXP"));
	    retval.add(new StringItem("6","Windows"));
	    return retval;	    
	}
    
    /**
     * 
     * @return  LinkedList of ListItem objects with the available Themes form Plastic
     */
    public static LinkedList<StringItem> getThemeList(){
        LinkedList<StringItem> retval = new LinkedList<StringItem>();
        retval.add(new StringItem("0","None"));
        retval.add(new StringItem("1","DesertBlue"));
        retval.add(new StringItem("2","DesertRed"));
        retval.add(new StringItem("3","Silver"));
        retval.add(new StringItem("4","SkyPink"));
        retval.add(new StringItem("5","SkyKrupp"));
        retval.add(new StringItem("6","SkyYellow"));
        retval.add(new StringItem("7","SkyGreen"));
        retval.add(new StringItem("8","DarkStar"));
        retval.add(new StringItem("9","BrownSugar"));
        retval.add(new StringItem("10","DesertGreen"));
        retval.add(new StringItem("11","ExperienceBlue"));
        retval.add(new StringItem("12","ExperienceGreen"));
        retval.add(new StringItem("13","SkyBlue"));
        retval.add(new StringItem("14","SkyBluer"));
        return retval;
        
    }
 /**
  * 
  * @param lafNumber
  * @return true if the LookAndFeel is Plastic type 
  */   
    public static boolean isPlastic(int lafNumber){
    	return ((lafNumber >= 3) && (lafNumber <= 5));            
    }
    
    /**
     * Sets the theme
     * @param themeNumber Theme number
     */
    public static void setTheme(int themeNumber){
            switch (themeNumber) {
                 case 1: PlasticLookAndFeel.setPlasticTheme(new DesertBlue()); 
                 		 break;
                 case 2: PlasticLookAndFeel.setPlasticTheme(new DesertRed());
                 		 break;
                 case 3: PlasticLookAndFeel.setPlasticTheme(new Silver());
                 		 break; 
                 case 4: PlasticLookAndFeel.setPlasticTheme(new SkyPink());
                 		 break; 
                 case 5: PlasticLookAndFeel.setPlasticTheme(new SkyKrupp());
                 		 break; 
                 case 6: PlasticLookAndFeel.setPlasticTheme(new SkyYellow());
                 		 break;
                 case 7: PlasticLookAndFeel.setPlasticTheme(new SkyGreen());
                 		 break;
                 case 8: PlasticLookAndFeel.setPlasticTheme(new DarkStar());
                 		 break;
                 case 9: PlasticLookAndFeel.setPlasticTheme(new BrownSugar());
                 		 break;
                 case 10: PlasticLookAndFeel.setPlasticTheme(new DesertGreen());
                 		 break;
                 case 11: PlasticLookAndFeel.setPlasticTheme(new ExperienceBlue());
                 		 break;
                 case 12: PlasticLookAndFeel.setPlasticTheme(new ExperienceGreen());
                 		 break;
                 case 13: PlasticLookAndFeel.setPlasticTheme(new SkyBlue());
                 		 break;
                 case 14: PlasticLookAndFeel.setPlasticTheme(new SkyBluer());
                 		 break;
                 default:                     
                	 	 break;
            }
    }
}
