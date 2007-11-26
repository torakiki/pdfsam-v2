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
package org.pdfsam.guiclient.gui.frames;


import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Toolkit;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.border.EtchedBorder;

import org.pdfsam.guiclient.GuiClient;
import org.pdfsam.guiclient.business.TextPaneAppender;
import org.pdfsam.guiclient.business.listeners.ExitActionListener;
import org.pdfsam.guiclient.gui.panels.JBackgroundedPanel;
/**
 * 
 * Splash screen.
 * @author Andrea Vacondio
 * @see it.pdfsam.GUI.MainGUI
 * 
 */
public class JSplashScreen extends JFrame {

	private static final long serialVersionUID = 3664676940782142274L;

	private final JLabel labelProgress = new JLabel();
	private final JLabel labelVersion = new JLabel();
	private final JProgressBar progressBar = new JProgressBar();
	private final JBackgroundedPanel topPanel = new JBackgroundedPanel("/images/splashscreen.png");		
	private final JPanel bottomPanel = new JPanel();		
	private final JButton exitButton = new JButton();
	private final JScrollPane logPane = new JScrollPane();
	
	private String initMessage;
	
	public JSplashScreen(String title, String initMessage){
		this.setTitle(title);
		this.initMessage = initMessage;
		init();
	}
	
	private void init(){
		setUndecorated(true);		
		
		topPanel.setLayout(new BoxLayout(topPanel, BoxLayout.Y_AXIS));	
		topPanel.setBorder(new EtchedBorder(EtchedBorder.LOWERED));
		progressBar.setBorderPainted(true);
		progressBar.setOrientation(JProgressBar.HORIZONTAL);
		progressBar.setValue(0);	
		progressBar.setAlignmentX(Component.LEFT_ALIGNMENT);
		labelProgress.setFont(new Font("SansSerif", Font.PLAIN, 10));
		labelProgress.setPreferredSize(new Dimension(300, 15));
		labelProgress.setText(initMessage);
		labelProgress.setAlignmentX(Component.LEFT_ALIGNMENT);
		labelVersion.setText("pdfsam "+GuiClient.VERSION);
		labelVersion.setPreferredSize(new Dimension(300, 15));
		labelVersion.setFont(new Font("SansSerif", Font.BOLD, 10));
		labelVersion.setAlignmentY(JLabel.BOTTOM_ALIGNMENT);
		labelVersion.setAlignmentX(Component.LEFT_ALIGNMENT);
		topPanel.add(labelProgress);
		topPanel.add(progressBar);
		topPanel.add(Box.createVerticalGlue());
		topPanel.add(labelVersion);
		topPanel.setOpaque(false);
		
		bottomPanel.setLayout(new BoxLayout(bottomPanel, BoxLayout.LINE_AXIS));
		bottomPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
		bottomPanel.setBorder(new EtchedBorder(EtchedBorder.LOWERED));
		exitButton.setText("Exit");		
		exitButton.setActionCommand(ExitActionListener.EXIT_COMMAND);
		exitButton.addActionListener(new ExitActionListener());		
		bottomPanel.add(Box.createHorizontalGlue());
		bottomPanel.add(exitButton);

		logPane.setViewportView(TextPaneAppender.getTextPaneInstance());
		logPane.setPreferredSize(new Dimension(300, 65));
		
		getContentPane().add(topPanel, BorderLayout.PAGE_START);
		getContentPane().add(bottomPanel, BorderLayout.PAGE_END);		
		getContentPane().add(logPane, BorderLayout.CENTER);	
		
		center(this,300,200);
		
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
        
        frame.setBounds(centreX.intValue(), centreY.intValue(), width, height);
    }
    
    /**
     * Delegate to label
     * @param message
     */
    public void setText(String message){
    	if(labelProgress != null){
    		labelProgress.setText(message);
    	}
    }
    
	/**
	 * Delegate to progressBar
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
