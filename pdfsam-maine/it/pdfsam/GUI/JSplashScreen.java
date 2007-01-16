/*
 * Created on 27-Dec-2006
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
package it.pdfsam.GUI;

import java.awt.Dimension;
import java.awt.Font;
import java.awt.Toolkit;

import javax.swing.BoxLayout;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
/**
 * 
 * Splash screen.
 * @author Andrea Vacondio
 * @see it.pdfsam.GUI.MainGUI
 * 
 */
public class JSplashScreen extends JFrame {

	private static final long serialVersionUID = 3664676940782142274L;

	private final JLabel label = new JLabel();
	private final JProgressBar progressBar = new JProgressBar();
	private final JPanel panel = new JPanel();		
	
	private String initMessage;
	
	public JSplashScreen(String title, String initMessage){
		this.setTitle(title);
		this.initMessage = initMessage;
		init();
	}
	
	private void init(){
		setUndecorated(true);		
		
		panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
		
		progressBar.setBorderPainted(true);
		progressBar.setOrientation(JProgressBar.HORIZONTAL);
		progressBar.setValue(0);
		
		label.setFont(new Font("SansSerif", Font.PLAIN, 10));
		label.setText(initMessage);		
		
		panel.add(label);
		panel.add(progressBar);
		getContentPane().add(panel);

		center(this,300,100);
		
	}
	
    /**
     * Used to center the mai window on the screen
     * @param frame JFrame to center
     * @param width 
     * @param height
     */
    private void center(JFrame frame, int width, int height){
        Dimension framedimension = new Dimension(width,height);        
        Dimension screensize = Toolkit.getDefaultToolkit().getScreenSize();

        Double centreX = new Double((screensize.getWidth() / 2) - (framedimension.getWidth()  / 2));
        Double centreY = new Double((screensize.getHeight() / 2) - (framedimension.getHeight()  / 2));
        
        frame.setBounds(centreX.intValue(), centreY.intValue(), 300, 100);
    }
    
    /**
     * Delegate to label
     * @param message
     */
    public void setText(String message){
    	if(label != null){
    		label.setText(message);
    	}
    }
    
	/**
	 * Delegate to progressBar
	 * @param value
	 */
	public void addBarValue(){
		if(progressBar != null){
			progressBar.setValue(progressBar.getValue()+1);
		}
	}
	
	/**
	 * Delegate to progressBar
	 * @param value
	 */
	public void setMaximumBarValue(int value){
		if(progressBar != null){
			progressBar.setMaximum(value);
		}
	}	
	
    
}
