/*
 * Dots and Boxes
 * Submitted for the Degree of B.Sc. in Computer Science, 2010/2011
 * University of Strathclyde
 * Department of Computer and Information Sciences
 * @author Philip Rodgers
 */
package view;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;

import javax.swing.ImageIcon;
import javax.swing.JPanel;

import tools.Tools;

public class GoButton extends JPanel {

	private static final long serialVersionUID = -8746908173473227400L;
	private boolean hover = false;
	private Font font;
	private ImageIcon circle;

	public GoButton () {
		super();
		setOpaque(false);
		setBackground(new Color(0,0,0,0));
		font = Tools.getScratchFont();
		font.getName();
		circle = Tools.getCircle();
		setName("GO");
	}
	
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		Graphics2D g2 = (Graphics2D)g;
		int fontSize = Math.min( (int) (getHeight() * 0.6),  (int) (getWidth() * 0.2));
		g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
		                        RenderingHints.VALUE_ANTIALIAS_ON);
		g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
				RenderingHints.VALUE_INTERPOLATION_BICUBIC);
		g2.setColor(Color.DARK_GRAY);
		font = new Font(font.getName(), font.getStyle(), fontSize);
		g2.setFont(font);
		g.setFont(font);
		g2.drawString("GO!", (int) (getWidth() * 0.33), (int) (getHeight() * 0.7));
		if(hover) {
			g2.drawImage(circle.getImage(),(int)(getWidth()*0.1),(int)(getHeight()*0.1), 
					(int)(getWidth()*0.8),(int)(getHeight()*0.8), null);
		}
	}
	
	public void hover(boolean hover) {
		this.hover = hover;
	}
}
