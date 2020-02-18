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
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Vector;

import javax.swing.JComboBox;
import javax.swing.JPanel;
import javax.swing.UIManager;

import tools.Tools;

public class GameSizeSelectPanel extends JPanel implements ActionListener {

	private static final long serialVersionUID = -5685431877104226841L;
	private JComboBox w, h;
	private int width, height;
	private Vector<Integer> widths;
	private Vector<Integer> heights;
	private Font font, nums;

	public GameSizeSelectPanel(Dimension size) {
		super();
		setOpaque(false);
		
		width = 5;
		height = 5;
		
		widths = new Vector<Integer>();
		for(int i = size.width ; i >= 1 ; i--) {
			widths.add(i);
		}
		
		heights = new Vector<Integer>();
		for(int i = size.height ; i >= 1 ; i--) {
			heights.add(i);
		}
		
		try {
			UIManager.setLookAndFeel(
					UIManager.getCrossPlatformLookAndFeelClassName());
		} catch (Exception e) {}
		
		font = Tools.getPencilFont();
		nums = Tools.getSnakeFont();
		
		w = new JComboBox(widths);
		w.setName("w");
		w.setSelectedItem(width);
		w.addActionListener(this);
		h = new JComboBox(heights);
		h.setName("h");
		h.setSelectedItem(height);
		h.addActionListener(this);
		setLayout(null);
		add(w);
		add(h);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		JComboBox jcb = (JComboBox)e.getSource();
		if(jcb.getName().equals("w")) {
			width = (Integer) jcb.getSelectedItem();
			this.repaint();
		} else if (jcb.getName().equals("h")) {
			height = (Integer) jcb.getSelectedItem();
			this.repaint();
		}
	}
	
	public Dimension getSelectedSize() {
		return new Dimension(width, height);
	}
	
	public void paintComponent(Graphics g) {
		Graphics2D g2 = (Graphics2D)g;
		w.setBounds((int) (getWidth()*0.3), (int) (getHeight()*0.33), 
				(int) (getWidth() * 0.1), getHeight()/3);
		h.setBounds((int) (getWidth()*0.65), (int) (getHeight()*0.33), 
				(int) (getWidth() * 0.1), getHeight()/3);

		g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON);
		g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
				RenderingHints.VALUE_INTERPOLATION_BICUBIC);
		
		
		int fontSize = Math.min( (int) (getHeight() * 0.2),  (int) (getWidth() * 0.04));
		g2.setColor(Color.DARK_GRAY);
		font = new Font(font.getName(), font.getStyle(), fontSize);
		nums = new Font(nums.getName(), nums.getStyle(), (int) (fontSize*1.8));
		w.setFont(nums);
		h.setFont(nums);
		g.setFont(font);
		g2.setFont(font);
		g2.drawString("Game Size:", (int) (getWidth() * 0.08), (int) (getHeight() * 0.55));
		g2.drawString((((Integer)w.getSelectedItem() == 1) ? "box" : "boxes") + " wide by", 
				(int) (getWidth() * 0.43), (int) (getHeight() * 0.55));
		g2.drawString((((Integer)h.getSelectedItem() == 1) ? "box" : "boxes") + " high", 
				(int) (getWidth() * 0.77), (int) (getHeight() * 0.55));
	}
}