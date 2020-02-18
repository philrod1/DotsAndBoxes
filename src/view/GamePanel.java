/*
 * Dots and Boxes
 * Submitted for the Degree of B.Sc. in Computer Science, 2010/2011
 * University of Strathclyde
 * Department of Computer and Information Sciences
 * @author Philip Rodgers
 */
package view;

import gameStates.GameState;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.LayoutManager;
import java.awt.RenderingHints;

import javax.swing.ImageIcon;
import javax.swing.JPanel;

import tools.Tools;

public class GamePanel extends JPanel {

	private static final long serialVersionUID = 8577420944795877624L;
	private final ImageIcon icon;
	private final GameState gs;
	private int currentPlayer = 0;
	private Font font;
	private String[] players;
	
	public GamePanel (NodePanel[][] nodes, GameState gs, String[] players) {
		this.players = players;
		this.gs = gs;
		Dimension size = gs.getSize();
		LayoutManager layout = new GridBagLayout();
		GridBagConstraints c = new GridBagConstraints();
		setLayout(layout);
		setOpaque(false);
		icon = Tools.getPaper();
		font = loadFont();
		for(int y = 0 ; y <= size.height+1 ; y++) {
			for(int x = 0 ; x <= size.width+1 ; x++) {
				if(x==0 || y==0) {
					if(x==0 && y==0) {
						c.weightx = 0.05;
						c.weighty = 0.05;
						c.gridx = x;
						c.gridy = y;
						c.fill = GridBagConstraints.BOTH;
						this.add(new NullPanel(), c);
						c.weightx = (0.9 / size.width);
						c.weighty = (0.9 / size.height);
						c.gridx = x+1;
						c.gridy = y+1;
						c.fill = GridBagConstraints.BOTH;
						this.add(new NullPanel(), c);
					}
				} else {
					c.weightx = (0.9 / size.width);
					c.weighty = (0.9 / size.height);
					c.gridx = x+1;
					c.gridy = y+1;
					c.fill = GridBagConstraints.BOTH;
					this.add(nodes[x-1][y-1], c);
				}
			}
		}
		c.weightx = 0.06;
		c.weighty = 0.06;
		c.gridx = size.width+3;
		c.gridy = size.height+3;
		c.fill = GridBagConstraints.BOTH;
		this.add(new NullPanel(), c);
	}
	
	@Override
	public void paint(Graphics g) {
		Graphics2D g2 = (Graphics2D)g;
		g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON);
		g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
				RenderingHints.VALUE_INTERPOLATION_BICUBIC);
		g2.drawImage(icon.getImage(), 0, 0, null);
		g2.setColor(new Color(255,255,255,50));
		g2.fillRect(0, 0, getWidth(), getHeight());
		super.paint(g2);
		g2.setColor(Color.DARK_GRAY);
		font = new Font(font.getName(), font.getStyle(), Math.min(getWidth()/14, getHeight()/14));
		g2.setFont(font);
		g2.drawString(""+players[0], getWidth()/16, getHeight()/16);
		g2.drawString("Score: " + gs.player1Score(), getWidth()/16, getHeight()/9);
		g2.drawString(""+players[1], (getWidth()/20) * 14, getHeight()/16);
		g2.drawString("Score: " + gs.player2Score(), (getWidth()/20) * 14, getHeight()/9);
		if(gs!=null) {
			font = new Font(font.getName(), font.getStyle(), Math.min(getWidth()/10, getHeight()/10));
			g2.setFont(font);
			if(gs.getRemainingLines().size() == 0) {
				g2.setFont(font);
				g2.setColor(Color.DARK_GRAY);
				int value = gs.getValue();
				if(value > 0) {
					if(value ==1) {
						g2.drawString("Player 1 wins by 1 box!", getWidth()/4, (int)(getHeight()*0.94));
					} else {
						g2.drawString("Player 1 wins by " + value + " boxes!", getWidth()/5, (int)(getHeight()*0.94));
					}
				} else if (value < 0) {
					if(value == -1) {
						g2.drawString("Player 2 wins by 1 box!", getWidth()/4, (int)(getHeight()*0.94));
					} else {
						g2.drawString("Player 2 wins by " + (-value) + " boxes!", getWidth()/5, (int)(getHeight()*0.94));
					}
				} else {
					g2.drawString("It's a draw!", getWidth()/3, (int)(getHeight()*0.94));
				}
			} else {
				g2.drawString("Player " + currentPlayer + " to go.", getWidth()/3, (int)(getHeight()*0.94));
			}
		}
	}

	public void setPlayer(int currentPlayer) {
		this.currentPlayer = currentPlayer;
	}
	
	private Font loadFont() {
		return Tools.getSnakeFont();
	}
}
