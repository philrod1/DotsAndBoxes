/*
 * Dots and Boxes
 * Submitted for the Degree of B.Sc. in Computer Science, 2010/2011
 * University of Strathclyde
 * Department of Computer and Information Sciences
 * @author Philip Rodgers
 */
package view;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;

import javax.swing.JPanel;

import data.Line;



public class NodePanel extends JPanel {
	
	private LinePanel v, h;
	private BoxPanel box;
	private JPanel dot;
	private GameStateView gsv;
	private static final long serialVersionUID = -3207831965551285149L;
	
	public NodePanel (int x, int y, Dimension size, GameStateView gsv) {
		super();
		this.gsv = gsv;
		JPanel padBox = new JPanel();
		padBox.setOpaque(true);
		padBox.setBackground(new Color(0,0,0,0));
		JPanel padLine = new JPanel();
		padLine.setOpaque(true);
		padLine.setBackground(new Color(0,0,0,0));
		boolean lastx = x==size.width;
		boolean lasty = y==size.height;
		setLayout(new GridBagLayout());
		setOpaque(false);
		setBackground(new Color(0,0,0,255));
		GridBagConstraints c;
		
		v = new LinePanel(x, y, true, this);
		h = new LinePanel(x, y, false, this);
		box = new BoxPanel();
		dot = new DotPanel();
		
		c = new GridBagConstraints();
		
		c.weightx = 0.05;
		c.weighty = 0.05;
		c.gridx = 0;
		c.gridy = 0;
		c.fill = GridBagConstraints.BOTH;
		this.add(dot, c);
		
		c.weightx = 0.95;
		c.weighty = 0;
		c.gridx = 1;
		c.gridy = 0;
		c.gridwidth = 9;	
		c.fill = GridBagConstraints.BOTH;
		
		if(lastx) {	
			this.add(padLine,c);
		} else {
			this.add(h,c);
		}
		
		c.weightx = 0.05;
		c.weighty = 0.95;
		c.gridx = 0;
		c.gridy = 1;
		c.gridheight = 9;
		c.fill = GridBagConstraints.BOTH;
		
		if(lasty) {
			this.add(padLine,c);
		} else {
			this.add(v,c);
		}
		
		c.weightx = 1;
		c.weighty = 1;
		c.gridx = 1;
		c.gridy = 1;
		c.gridwidth = 9;
		c.gridheight = 9;
		c.fill = GridBagConstraints.BOTH;
			
		if(lastx || lasty) {	
			this.add(padBox,c);
		} else {
			this.add(box,c);
		}
	}
	
	public void setPlayer(int player) {
		box.setPlayer(player);
	}
	
	public LinePanel getVertical() {
		return v;
	}
	
	public LinePanel getHorizontal() {
		return h;
	}
	
	public BoxPanel getBox() {
		return box;
	}

	public void sendLine(Line line) {
		gsv.sendLine(line);
	}
}
