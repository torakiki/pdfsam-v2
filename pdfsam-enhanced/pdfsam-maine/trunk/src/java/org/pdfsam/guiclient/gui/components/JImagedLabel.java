package org.pdfsam.guiclient.gui.components;

import java.awt.Color;
import java.awt.Graphics;

import javax.swing.ImageIcon;
import javax.swing.JLabel;

public class JImagedLabel extends JLabel {

	private boolean drawRedCross = false;
	
	
	
	/**
	 * @return the drawRedCross
	 */
	public boolean isDrawRedCross() {
		return drawRedCross;
	}



	/**
	 * @param drawRedCross the drawRedCross to set
	 */
	public void setDrawRedCross(boolean drawRedCross) {
		this.drawRedCross = drawRedCross;
	}



	public void paintComponent(Graphics g){
		super.paintComponent(g);
		g.drawImage(((ImageIcon)getIcon()).getImage(), 1, 0, getWidth()-2, getHeight()-15, null);
		if(drawRedCross){
			g.setColor(Color.red);	
			g.drawLine(0,getHeight(),getWidth(),0); 
			g.drawLine(getWidth(),getHeight(),0,0);
		}	
		g.dispose();
	}
}
