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

public class BoxPanel extends JPanel {

	private static final long serialVersionUID = -6928778559768366175L;
	private String player;
	
	public BoxPanel () {
		super();
		player = "";
		setOpaque(false);
		setBackground(new Color(0,0,0,0));
	}

	public void paintComponent(Graphics g) { 
		super.paintComponent(g);
		Graphics2D g2 = (Graphics2D)g;
		   g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
		                        RenderingHints.VALUE_ANTIALIAS_ON);
		int arc = Math.min(getWidth(), getHeight())/6;
		if (player.equals("1")) {
			g2.setColor(new Color(255,0,0,100));
			g2.fillRoundRect(1, 1, getWidth()-1, getHeight()-1,arc,arc);
		} else if (player.equals("2")) {
			g2.setColor(new Color(0,0,255,100));
			g2.fillRoundRect(1, 1, getWidth()-1, getHeight()-1,arc,arc);
		}
	}
	public void setPlayer(int player) {
		this.player = ""+player;
		invalidate();
		repaint();
	}
}
