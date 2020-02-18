/*
 * Dots and Boxes
 * Submitted for the Degree of B.Sc. in Computer Science, 2010/2011
 * University of Strathclyde
 * Department of Computer and Information Sciences
 * @author Philip Rodgers
 */
package view;

import players.Player;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.LinkedList;
import java.util.List;
import java.util.Vector;

import javax.swing.JComboBox;
import javax.swing.JPanel;
import javax.swing.UIManager;

import tools.Tools;

public class PlayerSelectPanel extends JPanel implements ActionListener {

	private static final long serialVersionUID = -5685431877104226841L;
	private int player;
	private JComboBox combo;
	private List<Player> players;
	private Vector<String> names;
	private List<String> tooltips;
	private Player selected;
	private Font font;

	public PlayerSelectPanel(int player, List<Player> players) {
		super();
		setOpaque(false);
		this.player = player;
		this.players = players;
		selected = players.get(0);
		names = new Vector<String>();
		tooltips = new LinkedList<String>();
		for(Player p : players) {
			names.add(p.getName());
			tooltips.add(p.getDescription());
		}
		try {
			UIManager.setLookAndFeel(
					UIManager.getCrossPlatformLookAndFeelClassName());
		} catch (Exception e) {
			
		}
		font = Tools.getPencilFont();
		combo = new JComboBox(names);
		combo.setRenderer(Tools.getComboBoxRenderer(tooltips));
		combo.addActionListener(this);
		setLayout(null);
		combo.setFont(new Font(font.getName(), font.getStyle(), 20));
		add(combo);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		JComboBox jcb = (JComboBox)e.getSource();
		selected = players.get(jcb.getSelectedIndex());
	}
	
	public Player getSelectedPlayer() {
		return selected;
	}
	
	public void paintComponent(Graphics g) {
		Graphics2D g2 = (Graphics2D)g;
		combo.setBounds(
				(int) (getWidth()  * 0.43), 
				(int) (getHeight() * 0.33), 
				(int) (getWidth()  * 0.5 ), 
				       getHeight() / 3   );

		g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON);
		g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
				RenderingHints.VALUE_INTERPOLATION_BICUBIC);
		
		int fontSize = Math.min( 
				(int) (getHeight() * 0.2),  
				(int) (getWidth() * 0.08));
		g2.setColor(Color.DARK_GRAY);
		font = new Font(font.getName(), font.getStyle(), fontSize);
		combo.setFont(font);
		g2.setFont(font);
		g2.drawString("Player " + ((player == 1) ? "One" : "Two") + ":", 
				(int) (getWidth() * 0.05), (int) (getHeight() * 0.55));
	}
	

}
