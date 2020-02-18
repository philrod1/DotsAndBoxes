/*
 * Dots and Boxes
 * Submitted for the Degree of B.Sc. in Computer Science, 2010/2011
 * University of Strathclyde
 * Department of Computer and Information Sciences
 * @author Philip Rodgers
 */
package view;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.LayoutManager;
import java.awt.RenderingHints;

import javax.swing.ImageIcon;
import javax.swing.JPanel;

import tools.Tools;

public class OptionPanel extends JPanel {

	private static final long serialVersionUID = 6988964718242570466L;
	private ImageIcon sticky;
	private ButtonPanel[] buttons;
	
	public OptionPanel (ButtonPanel[] buttons) {
		super();
		this.buttons = buttons;
		setOpaque(false);
		setBackground(new Color(0,0,0,0));
		
		LayoutManager layout = new GridBagLayout();
		setLayout(layout);
		sticky = Tools.getStickyNote();
		addButtons();
	}

	private void addButtons() {
		GridBagConstraints c = new GridBagConstraints();
		
		c.weightx = 1;
		c.weighty = 0.3;
		c.gridx = 0;
		c.gridy = 0;
		c.fill = GridBagConstraints.BOTH;
		this.add(new NullPanel(), c);
		
		double weighty = 0.65 / buttons.length;
		
		for(int i = 1 ; i <= buttons.length ; i++) {
			c = new GridBagConstraints();
			c.weighty = weighty;
			c.gridy = i;
			c.fill = GridBagConstraints.BOTH;
			this.add(buttons[i-1], c);
		}
		
		
		c.weighty = 0.05;
		c.gridy = 5;
		c.fill = GridBagConstraints.BOTH;
		this.add(new NullPanel(), c);
	}

	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		Graphics2D g2 = (Graphics2D)g;
		g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
		                        RenderingHints.VALUE_ANTIALIAS_ON);
		g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
				RenderingHints.VALUE_INTERPOLATION_BICUBIC);
		
		g2.drawImage(sticky.getImage(), 0, 0, getWidth(), getHeight(), null);

	}
}
