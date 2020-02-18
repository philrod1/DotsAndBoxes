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

public class StateSelectPanel extends JPanel implements ActionListener {

	private static final long serialVersionUID = -5685431877104226841L;
	private JComboBox combo;
	private List<GameState> states;
	private Vector<String> names;
	private List<String> tooltips;
	private GameState selected;
	private Font font;

	public StateSelectPanel(List<GameState> states) {
		super();
		setOpaque(false);
		this.states = states;
		selected = states.get(0);
		names = new Vector<String>();
		tooltips = new LinkedList<String>();
		for(GameState s : states) {
			names.add(s.getName());
			tooltips.add(s.getDescription());
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
		add(combo);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		JComboBox jcb = (JComboBox)e.getSource();
		selected = states.get(jcb.getSelectedIndex());
	}
	
	public GameState getSelectedState() {
		return selected;
	}
	
	public void paintComponent(Graphics g) {
		Graphics2D g2 = (Graphics2D)g;
		combo.setBounds(
				(int) (getWidth()*0.5), 
				(int) (getHeight()*0.33), 
				(int) (getWidth() * 0.4), 
				getHeight()/3);

		g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON);
		g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
				RenderingHints.VALUE_INTERPOLATION_BICUBIC);
		
		
		int fontSize = Math.min( 
				(int) (getHeight() * 0.2),  
				(int) (getWidth() * 0.04));
		g2.setColor(Color.DARK_GRAY);
		font = new Font(font.getName(), font.getStyle(), fontSize);
		combo.setFont(font);
		g2.setFont(font);
		g2.drawString("Game State Representation:", 
				(int) (getWidth() * 0.08), 
				(int) (getHeight() * 0.55));
	}
}