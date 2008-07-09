package org.pdfsam.guiclient;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.SpringLayout;

import org.pdfsam.guiclient.commons.panels.JVisualPdfPageSelectionPanel;

public class Test {

	private static JFrame clientGUI;
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		try {
			clientGUI = new JFrame();
			JPanel mainPanel = new JPanel();
			SpringLayout layout = new SpringLayout();
			mainPanel.setLayout(layout);
			JVisualPdfPageSelectionPanel selectionPanel = new JVisualPdfPageSelectionPanel();
			
			layout.putConstraint(SpringLayout.SOUTH, selectionPanel, 200, SpringLayout.NORTH, mainPanel);
			layout.putConstraint(SpringLayout.EAST, selectionPanel, -5, SpringLayout.EAST, mainPanel);
			layout.putConstraint(SpringLayout.NORTH, selectionPanel, 5, SpringLayout.NORTH, mainPanel);
			layout.putConstraint(SpringLayout.WEST, selectionPanel, 5, SpringLayout.WEST, mainPanel);
			
			mainPanel.add(selectionPanel);
			clientGUI.getContentPane().add(mainPanel);
			clientGUI.setVisible(true);
		} catch (Throwable t) {
			t.printStackTrace();
		}
	}

}
