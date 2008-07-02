package org.pdfsam.guiclient;

import java.awt.BorderLayout;

import javax.swing.JFrame;

import org.pdfsam.guiclient.commons.panels.JVisualPdfPageSelectionPanel;

public class Test {

	private static JFrame clientGUI;
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		try {
			clientGUI = new JFrame();
			clientGUI.getContentPane().add(new JVisualPdfPageSelectionPanel(), BorderLayout.PAGE_START);
			
			clientGUI.setVisible(true);
		} catch (Throwable t) {
			t.printStackTrace();
		}
	}

}
