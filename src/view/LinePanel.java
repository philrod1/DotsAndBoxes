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
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.JPanel;

import data.Line;


public class LinePanel extends JPanel {

	private static final long serialVersionUID = 412247577381611100L;
	private boolean hover, clicked, vertical;
	private Line line;
	private NodePanel np;
	
	public LinePanel (int x, int y, boolean vertical, NodePanel np) {
		super();
		this.np = np;
		this.vertical = vertical;
		if(vertical) {
			line = new Line(x,y,x,y+1);
		} else {
			line = new Line(x,y,x+1,y);
		}
		hover = false;
		clicked = false;
		addMouseListener(new LineMouseListener(this));
		setOpaque(false);
		setBackground(new Color(0,0,0,0));
	}
	
	@Override
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		Graphics2D g2 = (Graphics2D)g;
		   g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
		                        RenderingHints.VALUE_ANTIALIAS_ON);
		int arc = Math.min(getWidth(), getHeight());
		if(!clicked) {
		   if(hover) {
			   g2.setColor(new Color(100,100,100,100));
			   g2.fillRoundRect(0, 0, getWidth(), getHeight(), arc, arc);
		   }
		} else {
			g2.setColor(Color.DARK_GRAY);
			g2.fillRoundRect(0, 0, getWidth(), getHeight(), arc, arc);
		}
	}
	
	public boolean isVertical() {
		return vertical;
	}
	
	public Line getLine() {
		return line;
	}
	
	public void setClicked(boolean clicked) {
		this.clicked = clicked;
	}
	
	private class LineMouseListener implements MouseListener {
		
		private final LinePanel lp;
		
		private LineMouseListener(LinePanel lp) {
			this.lp = lp;
		}

        public void mouseEntered(MouseEvent e) {
            lp.hover = true;
            lp.invalidate();
            lp.repaint();
        }
        
		public void mouseExited(MouseEvent e) {
			lp.hover = false;
        	lp.invalidate();
        	lp.repaint();
		}
		
        public void mousePressed(MouseEvent e) {
        	np.sendLine(line);
        }

		@Override
		public void mouseClicked(MouseEvent e) {}
		@Override
		public void mouseReleased(MouseEvent e) {}
    }
}
