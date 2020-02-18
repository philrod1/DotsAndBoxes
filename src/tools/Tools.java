/*
 * Dots and Boxes
 * Submitted for the Degree of B.Sc. in Computer Science, 2010/2011
 * University of Strathclyde
 * Department of Computer and Information Sciences
 * @author Philip Rodgers
 */
package tools;

import gameStates.GameState;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.util.List;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.plaf.basic.BasicComboBoxRenderer;

import view.BinaryGameStateView;



public class Tools {
	
	public static final int V_MASK = 0x44444444;
	public static final int H_MASK = 0x88888888;
	public static final int L_MASK = 0xCCCCCCCC;
	public static final int P_MASK = 0x33333333;
	
	public static String fileSeperator() {
		return System.getProperty("file.separator");
	}
	
	public static String pad(String hexString) {
		String pad = "";
		for(int i = 0 ; i < (8-(hexString.length())) ; i++) {
			pad += "0";
		}
		return pad + hexString;
	}
	

	/*
	 * Methods for calculating the transpositions of game states
	 * represented by integer arrays 
	 */
	
	public static int[][] getAll(int[] state) {
		int[][] states = new int[8][state.length];
		states[0] = state;
		states[1] = rotate(state);
		states[2] = rotate(states[1]);
		states[3] = rotate(states[2]);
		states[4] = flipHor(state);
		states[5] = flipVert(state);
		states[6] = flipDiag1(state);
		states[7] = flipDiag2(state);
		return states;
	}
	
	public static int[] flipHor(int[] state) {
		int size = state.length;
		int[] newState = new int[size];
		for(int i = 0 ; i < size ; i++) {
			newState[i] = 0;
			for(int x = 0 ; x < size ; x++) {
				newState[i] <<= 4;
				newState[i] |= ((state[i] >> (x*4)) & 15);
			}
			newState[i] = (newState[i] & V_MASK) | ((newState[i] & ~V_MASK) >> 4);
		}
		return newState;
	}
	
	public static int[] flipDiag1(int[] state) {
		int size = state.length-1;
		int[] newState = new int[size+1];
		for(int i = 0 ; i <= size ; i++) {
			newState[i] = 0;
			for(int x = 0 ; x <= size ; x++) {
				newState[i] <<= 4;
				newState[i] |= ((state[x] >> ((size-i)*4))&15);
			}
		}
		for(int i = 0 ; i <= size ; i++) {
			if(i < size){
				newState[i] = ((newState[i+1] & P_MASK) >> 4)
						    | ((newState[i]   & V_MASK) >> 3) 
						    | ((newState[i+1] & H_MASK) >> 1);
			} else {
				newState[i] = ((newState[i]   & V_MASK) >> 3);
			}
		}
		return newState;
	}
	
	public static int[] flipDiag2(int[] state) {
		int size = state.length-1;
		int[] newState = new int[size+1];
		for(int i = 0 ; i <= size ; i++) {
			newState[i] = 0;
			for(int x = size ; x >= 0 ; x--) {
				newState[i] <<= 4;
				newState[i] |= ((state[x] >> ((i)*4))&15);
			}
			newState[i] = ((newState[i] &  P_MASK) 
				 	| ((newState[i] &  V_MASK)<<1)
				 	| ((newState[i] &  H_MASK)>>1));
		}
		return newState;
	}
	
	public static int[] flipVert(int[] state) {
		int size = state.length-1;
		int[] newState = new int[size+1];
		for(int i = 0 ; i <= size ; i++) {
			if(i==size) {
				newState[i] = state[size-i] & H_MASK;
			} else {
				newState[i] = ((state[size-i] & H_MASK)
						| ((state[size-(i+1)] & ~H_MASK)));
			}
		}
		return newState;
	}
	
	public static int[] rotate(int[] state) {
		int size = state.length;
		int[] newState = new int[size];
		for(int i = 0 ; i < size ; i++) {
			newState[i] = getLine(i,state,size);
		}
		return newState;
	}

	private static int getLine(int index, int[] state, int size) {
		int line = 0;
		for(int i = 0 ; i < size ; i++) {
			line |= ((state[i]>>(index*4)) & 15) << (((size-1)-i)*4);
		}
		return ((line & P_MASK)>>4) | ((line & H_MASK)>>1) | ((line & V_MASK)>>3);
	}
	
	/**
	 * Show a game state in a frame.  This was used a lot during testing
	 * @param title the title of the frame
	 * @param state the state to be displayed.
	 */
	public static void show(String title, GameState state) {
		BinaryGameStateView gsv = new BinaryGameStateView(state);
		JFrame frame = new JFrame(title);
		frame.setMinimumSize(new Dimension(200,200));
		frame.setPreferredSize(new Dimension(400,400));
		frame.getContentPane().add(gsv.getView());
		frame.pack();
		//frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setVisible(true);
	}
	
	/**
	 * Add tooltips to a JComboBox
	 * @param tooltips a List of Strings
	 * @return A Combo-box renderer that includes tooltips
	 */
	public static BasicComboBoxRenderer getComboBoxRenderer(final List<String> tooltips) {
		return new BasicComboBoxRenderer() {
			
			private static final long serialVersionUID = -5577759413851791671L;

			public Component getListCellRendererComponent(JList list,
					Object value, int index, boolean isSelected,
					boolean cellHasFocus) {
				if (isSelected) {
					setBackground(list.getSelectionBackground());
					setForeground(list.getSelectionForeground());
					if (-1 < index) {
						list.setToolTipText(tooltips.get(index));
					}
				} else {
					setBackground(list.getBackground());
					setForeground(list.getForeground());
				}
				setFont(list.getFont());
				setText((value == null) ? "" : value.toString());
				return this;
			}

		};
	}
	
	/*
	 * Font and image loaders here for convenience.
	 */
	
	public static Font getSnakeFont() {
		return new Font("Snake",0,12);
	}

	public static Font getScratchFont() {
		return new Font("Scratch",0,12);
	}
	
	public static Font getBumpyRoadFont() {
		if(System.getProperty("os.name").startsWith("Win")) {
			return new Font("Bumpy Road Regular",0,12);
		} else {
			return new Font("BumpyRoad",0,12);
		}
	}
	
	public static Font getPencilFont() {
		if(System.getProperty("os.name").startsWith("Win")) {
			return new Font("pencilPete FONT",0,12);
		} else {
			return new Font("PencilPeteFONT",0,12);
		}
	}
	
	public static ImageIcon getCircle() {
		return new ImageIcon("img" + fileSeperator() + "circle.png");
	}
	
	public static ImageIcon getPaper() {
		return new ImageIcon("img" + fileSeperator() + "paper.jpg");
	}
	
	public static ImageIcon getScribble() {
		return new ImageIcon("img" + fileSeperator() + "scribble.png");
	}
	
	public static ImageIcon getStickyNote() {
		return new ImageIcon("img" + fileSeperator() + "sticky.png");
	}
}
