/*
 * Created on 03-Feb-2006
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
package it.pdfsam.plugin.coverfooter.type;
/**
 * Item class for Merge table
 * @author  Andrea Vacondio
 * @see     javax.swing.table.AbstractTableModel
 * @see     it.pdfsam.plugin.coverfooter.model.CoverFooterTableModel
 */
public class CoverFooterItemType {

    private String file_name;
    private String file_path;
    private String num_pages;
    private String page_selection;
    private boolean encrypted;
 
    //colums order
    public final static int FILENAME = it.pdfsam.plugin.coverfooter.model.CoverFooterTableModel.FILENAME;
    public final static int PATH = it.pdfsam.plugin.coverfooter.model.CoverFooterTableModel.PATH;
    public final static int PAGES = it.pdfsam.plugin.coverfooter.model.CoverFooterTableModel.PAGES;
    public final static int PAGESELECTION = it.pdfsam.plugin.coverfooter.model.CoverFooterTableModel.PAGESELECTION;
    
    /**
     * Constructor
     */
    public CoverFooterItemType(){
        this.file_name = null;
        this.file_path = null;
        this.num_pages = null;
        this.page_selection = "all";
        this.encrypted = false;
    }
 
    /**
     * Constructor
     * @param string_rappresentation The string rappresentation of the object
     */
    public CoverFooterItemType(String string_rappresentation){
        try{
            String[] values = string_rappresentation.split("::");
            this.file_name = values[0];
            this.file_path = values[1];
            this.num_pages = values[2];
            this.page_selection = values[3];
            this.encrypted = Boolean.valueOf(values[4]).booleanValue();         
        }
        catch(Exception e){
            this.file_name = null;
            this.file_path = null;
            this.num_pages = null;
            this.page_selection = "all";
            this.encrypted = false;
        }
    }
        
    /**
     * Constructor
     * @param f_name File name to show in talbe
     * @param f_path File path to show in table
     * @param n_pages Number of pages of the pdf
     * @param p_selection Page selection to merge
     * @param isencrypted True id pdf encrypted
     */
    public CoverFooterItemType(String f_name, String f_path, String n_pages, String p_selection, boolean isencrypted){
        this.file_name = f_name;
        this.file_path = f_path;
        this.num_pages = n_pages;
        this.page_selection = p_selection;
        this.encrypted = isencrypted;
    }

    /**
     * Constructor
     * @param item MergeItemType to duplicate
     */
    public CoverFooterItemType(CoverFooterItemType item){
        this.file_name = item.getFileName();
        this.file_path = item.getFilePath();
        this.num_pages = item.getNumPages();
        this.page_selection = item.getPageSelection();
        this.encrypted = item.isEncrypted();
    }    
    /**
     * @return Returns the file_name.
     */
    public String getFileName() {
        return file_name;
    }
    /**
     * @param file_name The file_name to set.
     */
    public void setFileName(String file_name) {
        this.file_name = file_name;
    }
    /**
     * @return Returns the file_path.
     */
    public String getFilePath() {
        return file_path;
    }
    /**
     * @param file_path The file_path to set.
     */
    public void setFilePath(String file_path) {
        this.file_path = file_path;
    }
    
    /**
     * 
     * @param item_number
     * @return item value based on input number
     */
    public String getValue(int item_number) {
        switch(item_number){
                case FILENAME:
                     return getFileName();

                case PATH:
                    return getFilePath();
                    
                case PAGES:
                    return getNumPages();

                case PAGESELECTION:
                    return getPageSelection();

                default: return null;     
        }   
    }
    
    /**
     * @return Returns a string rappresentation of the object: "|" separated.
     */
  /*  public String getStringRappresentation() {
        return this.getFileName()+"::"+this.getFilePath()+"::"+this.getNumPages()+"::"+this.getPageSelection()+"::"+Boolean.toString(this.isEncrypted());
    }*/
    
    /**
     * @return Returns the num_pages.
     */
    public String getNumPages() {
        return num_pages;
    }

    /**
     * @param num_pages The num_pages to set.
     */
    public void setNumPages(String num_pages) {
        this.num_pages = num_pages;
    }

    /**
     * @return Returns the encrypted.
     */
    public boolean isEncrypted() {
        return encrypted;
    }

    /**
     * @param encrypted The encrypted to set.
     */
    public void setEncrypted(boolean encrypted) {
        this.encrypted = encrypted;
    }

    /**
     * @return Returns the page_selection.
     */
    public String getPageSelection() {
        return page_selection;
    }

    /**
     * @param page_selection The page_selection to set.
     */
    public void setPageSelection(String page_selection) {
        this.page_selection = page_selection;
    }
}
