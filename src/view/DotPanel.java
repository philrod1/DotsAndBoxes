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
import java.awt.RenderingHints;

import javax.swing.JPanel;

public class DotPanel extends JPanel {

	private static final long serialVersionUID = 2801265685869038807L;
	public DotPanel() {
		super();
		setOpaque(false);
		setBackground(new Color(0,0,0,0));
	}
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		Graphics2D g2 = (Graphics2D)g;
		   g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
		                        RenderingHints.VALUE_ANTIALIAS_ON);
		g2.setColor(Color.DARK_GRAY);
		g2.fillOval(0,0,getWidth()-1,getHeight()-1);
	}
}
