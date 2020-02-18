/*
 * Dots and Boxes
 * Submitted for the Degree of B.Sc. in Computer Science, 2010/2011
 * University of Strathclyde
 * Department of Computer and Information Sciences
 * @author Philip Rodgers
 */
package view;

import java.awt.Color;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.LayoutManager;

import javax.swing.JComponent;
import javax.swing.JPanel;

public class GlassPane extends JComponent {

	private static final long serialVersionUID = 3014027330042331578L;
	OptionPanel op;
	
	public GlassPane(OptionPanel op) {
		this.op = op;
		LayoutManager layout = new GridBagLayout();
		setLayout(layout);
		JPanel padBox = new JPanel();
		padBox.setOpaque(true);
		padBox.setBackground(new Color(0,0,0,0));
		
		JPanel padBox2 = new JPanel();
		padBox2.setOpaque(true);
		padBox2.setBackground(new Color(0,0,0,0));
		
		GridBagConstraints c = new GridBagConstraints();
		
		c.weightx = 0.2;
		c.weighty = 0.2;
		c.gridx = 0;
		c.gridy = 0;
		c.fill = GridBagConstraints.BOTH;
		this.add(padBox, c);
		
		c = new GridBagConstraints();
		
		c.weightx = 0.60;
		c.weighty = 0.60;
		c.gridx = 1;
		c.gridy = 1;
		c.fill = GridBagConstraints.BOTH;
		this.add(op, c);
		
		c = new GridBagConstraints();
		
		c.weightx = 0.2;
		c.weighty = 0.2;
		c.gridx = 2;
		c.gridy = 2;
		c.fill = GridBagConstraints.BOTH;
		this.add(padBox2, c);
	}
}