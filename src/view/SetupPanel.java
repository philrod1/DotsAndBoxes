/*
 * Dots and Boxes
 * Submitted for the Degree of B.Sc. in Computer Science, 2010/2011
 * University of Strathclyde
 * Department of Computer and Information Sciences
 * @author Philip Rodgers
 */
package view;

import gameStates.GameState;
import players.Player;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.RenderingHints;
import java.awt.event.MouseListener;
import java.util.List;

import javax.swing.ImageIcon;
import javax.swing.JPanel;

import tools.Tools;

public class SetupPanel extends JPanel {

	private static final long serialVersionUID = 8577420944795877624L;
	private final ImageIcon icon;
	private Font font;
	private GoButton goButton;
	private PlayerSelectPanel ps1, ps2;
	private StateSelectPanel state;
	private GameSizeSelectPanel size;
	
	public SetupPanel (MouseListener ml, List<Player> players, List<GameState> states) {
		setOpaque(false);
		addMouseListener(ml);
		icon = Tools.getPaper();
		font = Tools.getScratchFont();
		setLayout(new GridBagLayout());
		goButton = new GoButton();
		goButton.addMouseListener(ml);
		ps1 = new PlayerSelectPanel(1, players);
		ps2 = new PlayerSelectPanel(2, players);
		state = new StateSelectPanel(states);
		size = new GameSizeSelectPanel(new Dimension(7, 7));
		layoutItems();
	}
	
	private void layoutItems() {
		GridBagConstraints c = new GridBagConstraints();
		
		c.weightx = 0.25;
		c.weighty = 0.2;
		c.gridwidth = 1;
		c.gridx = 0;
		c.gridy = 0;
		c.fill = GridBagConstraints.BOTH;
		this.add(new NullPanel(), c);
		
		c.weightx = 0.25;
		c.weighty = 0.2;
		c.gridwidth = 2;
		c.gridx = 1;
		c.gridy = 0;
		c.fill = GridBagConstraints.BOTH;
		this.add(new NullPanel(), c);
		
		c.weightx = 0.25;
		c.weighty = 0.2;
		c.gridwidth = 1;
		c.gridx = 2;
		c.gridy = 0;
		c.fill = GridBagConstraints.BOTH;
		this.add(new NullPanel(), c);
		
		c.weightx = 0.25;
		c.weighty = 0.2;
		c.gridwidth = 1;
		c.gridx = 3;
		c.gridy = 0;
		c.fill = GridBagConstraints.BOTH;
		this.add(new NullPanel(), c);
		
		
		c.weightx = 0.5;
		c.weighty = 0.2;
		c.gridwidth = 2;
		c.gridx = 0;
		c.gridy = 1;
		c.fill = GridBagConstraints.BOTH;
		this.add(ps1, c);
		
		c.weightx = 0.5;
		c.weighty = 0.2;
		c.gridwidth = 2;
		c.gridx = 2;
		c.gridy = 1;
		c.fill = GridBagConstraints.BOTH;
		this.add(ps2, c);
		
		c.weightx = 0.25;
		c.weighty = 0.2;
		c.gridwidth = 4;
		c.gridx = 0;
		c.gridy = 2;
		c.fill = GridBagConstraints.BOTH;
		this.add(state, c);
		
		c.weightx = 0.25;
		c.weighty = 0.2;
		c.gridwidth = 4;
		c.gridx = 0;
		c.gridy = 3;
		c.fill = GridBagConstraints.BOTH;
		this.add(size, c);
		
		c.weightx = 0.25;
		c.weighty = 0;
		c.gridwidth = 1;
		c.gridx = 0;
		c.gridy = 4;
		c.fill = GridBagConstraints.HORIZONTAL;
		this.add(new NullPanel(), c);
		
		c.weightx = 0.25;
		c.weighty = 0;
		c.gridwidth = 1;
		c.gridx = 1;
		c.gridy = 4;
		c.fill = GridBagConstraints.HORIZONTAL;
		this.add(new NullPanel(), c);
		
		c.weightx = 0.25;
		c.weighty = 0;
		c.gridwidth = 1;
		c.gridx = 2;
		c.gridy = 4;
		c.fill = GridBagConstraints.HORIZONTAL;
		this.add(new NullPanel(), c);
		
		c.weightx = 0.25;
		c.weighty = 0;
		c.gridwidth = 1;
		c.gridx = 3;
		c.gridy = 4;
		c.fill = GridBagConstraints.HORIZONTAL;
		this.add(new NullPanel(), c);
		
		c.weightx = 0.25;
		c.weighty = 0.2;
		c.gridwidth = 1;
		c.gridx = 0;
		c.gridy = 5;
		c.fill = GridBagConstraints.BOTH;
		this.add(new NullPanel(), c);
		
		c.weightx = 0.25;
		c.weighty = 0.2;
		c.gridwidth = 2;
		c.gridx = 1;
		c.gridy = 5;
		this.add(goButton, c);
	}

	public Dimension getSize() {
		return size.getSelectedSize();
	}
	
	public Player getPlayerOne() {
		return ps1.getSelectedPlayer();
	}
	
	public Player getPlayerTwo() {
		return ps2.getSelectedPlayer();
	}
	
	public GameState getGameState() {
		return state.getSelectedState();
	}
	
	@Override
	public void paint(Graphics g) {
		g.drawImage(icon.getImage(), 0, 0, null);
		g.setColor(new Color(255,255,255,50));
		g.fillRect(0, 0, getWidth(), getHeight());
		super.paint(g);
		Graphics2D g2 = (Graphics2D)g;
		g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON);
		g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
				RenderingHints.VALUE_INTERPOLATION_BICUBIC);
		g2.setColor(Color.DARK_GRAY);
		font = new Font(font.getName(), font.getStyle(), 
				Math.min(getWidth()/9, getHeight()/9));
		g2.setFont(font);
		g2.drawString("SETUP", getWidth()/3, getHeight()/8);
	}
}
