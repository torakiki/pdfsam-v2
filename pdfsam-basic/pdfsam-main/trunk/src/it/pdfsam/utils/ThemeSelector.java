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
package it.pdfsam.utils;

import it.pdfsam.types.ListItem;

import java.util.LinkedList;

import javax.swing.UIManager;

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
import com.jgoodies.looks.plastic.theme.SkyGreen;
import com.jgoodies.looks.plastic.theme.SkyKrupp;
import com.jgoodies.looks.plastic.theme.SkyPink;
import com.jgoodies.looks.plastic.theme.SkyYellow;

/**
 * ThemeSelector class, it provides functions to let the user select the GUI theme
 * A GUI for this is not yet implemented
 * @author Andrea Vacondio
 */
public class ThemeSelector {
    
    /*
     * 
     */
	public String getLAF(String LAF_number){
		String ThemeSelected;
		try{
			switch (Integer.parseInt(LAF_number)) {
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
		catch(NumberFormatException e){
            return UIManager.getCrossPlatformLookAndFeelClassName();
        }
	}
	
    /**
	 * Prints the available LAFs
	 */
	public static void availableLAF(){
	    UIManager.LookAndFeelInfo[] info = UIManager.getInstalledLookAndFeels();
	    for (int i=0; i<info.length; i++) {
	        // Get the name of the look and feel that is suitable for display to the user
	        String humanReadableName = info[i].getName();    
	        String className = info[i].getClassName();
	        System.out.println(humanReadableName + " " +className);
	    }
    }
	
    /**
     * 
     * @return a LinkedList of ListItem objects with the availales LAF
     */
	public static LinkedList getLAFList(){
	    LinkedList retval = new LinkedList();
	    retval.add(new ListItem("0","Java"));
	    retval.add(new ListItem("1","System"));
	    retval.add(new ListItem("2","Metal"));
	    retval.add(new ListItem("3","Plastic3D"));
	    retval.add(new ListItem("4","Plastic"));
	    retval.add(new ListItem("5","PlasticXP"));
	    retval.add(new ListItem("6","Windows"));
	    return retval;
	    
	}
    
    /**
     * 
     * @return  LinkedList of ListItem objects with the availales Themes form Plastic
     */
    public static LinkedList getThemeList(){
        LinkedList retval = new LinkedList();
        retval.add(new ListItem("0","None"));
        retval.add(new ListItem("1","DesertBlue"));
        retval.add(new ListItem("2","DesertRed"));
        retval.add(new ListItem("3","Silver"));
        retval.add(new ListItem("4","SkyPink"));
        retval.add(new ListItem("5","SkyKrupp"));
        retval.add(new ListItem("6","SkyYellow"));
        retval.add(new ListItem("7","SkyGreen"));
        retval.add(new ListItem("8","DarkStar"));
        retval.add(new ListItem("9","BrownSugar"));
        retval.add(new ListItem("10","DesertGreen"));
        retval.add(new ListItem("11","ExperienceBlue"));
        retval.add(new ListItem("12","ExperienceGreen"));
        retval.add(new ListItem("13","SkyBlue"));
        return retval;
        
    }
 /**
  * 
  * @param LAF_number
  * @return true if the LookAndFeel is Plastic type 
  */   
    public static boolean isPlastic(String LAF_number){
        try{
            int tn = Integer.parseInt(LAF_number);
            return ((tn >= 3) && (tn <= 5))? true: false;            
        }
        catch (NumberFormatException e){
            return false;
        }
    }
    
    /**
     * Sets the theme
     * @param theme_number Theme number
     * @return true if no exception in threw
     */
    public boolean setTheme(String theme_number){
        try{
            switch (Integer.parseInt(theme_number)) {
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
                 default:                     
                 break;
            }
            return true;
        }
        catch(NumberFormatException e){
            return false;
        }
    }
}
