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
import java.awt.event.MouseListener;

import javax.swing.ImageIcon;
import javax.swing.JPanel;

import tools.Tools;

public class ButtonPanel extends JPanel {

	private static final long serialVersionUID = 4187492679549759710L;
	private boolean drawCircle = false;
	private boolean scribbleOut = false;
	private Font font;
	private ImageIcon circle, scribble;
	
	public ButtonPanel (String name, MouseListener ml) {
		super();
		font = Tools.getBumpyRoadFont();
		circle = Tools.getCircle();
		scribble =Tools.getScribble();
		addMouseListener(ml);
		setOpaque(false);
		setBackground(new Color(0,0,0,0));
		setName(name);
	}
	
	public void drawCircle(boolean drawCircle) {
		this.drawCircle = drawCircle;
	}
	
	public void scribbleOut(boolean scribbleOut) {
		this.scribbleOut = scribbleOut;
	}
	
	public boolean isScribbledOut() {
		return scribbleOut;
	}

	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		Graphics2D g2 = (Graphics2D)g;
		double fontSize = Math.min(getHeight() * 0.7, getWidth() * 0.175);
		g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON);
		g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
				RenderingHints.VALUE_INTERPOLATION_BICUBIC);
		font = font.deriveFont((float) fontSize);
		g2.setFont(font);
		g.setFont(font);
		g2.drawString(getName(),(int)(getWidth()*0.3),(int)(getHeight()*0.7));
		
		if(scribbleOut) {
			g2.drawImage(
					scribble.getImage(),
					(int)(getWidth()*0.25),
					(int)(getHeight()*0.2), 
					(int)(getWidth()*0.45),
					(int)(getHeight()*0.5), 
					null);
		} else {
			if(drawCircle) {
				g2.drawImage(
						circle.getImage(),
						(int)(getWidth()*0.12),
						(int)(getHeight()*0.1), 
						(int)(getWidth()*0.7),
						(int)(getHeight()*0.7), 
						null);
			}
		}
	}
}
