/*
 * Dots and Boxes
 * Submitted for the Degree of B.Sc. in Computer Science, 2010/2011
 * University of Strathclyde
 * Department of Computer and Information Sciences
 * @author Philip Rodgers
 */
package gameStates;

import java.awt.Dimension;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import java.util.Stack;

import tools.Tools;
import data.Line;

public class BinaryGameState implements GameState {

	private int[] state;
	private int width, height;
	private int value;
	private int player;
	private List<Line> remainingLines;
	private Line lastMove;
	private int linesLeft = 0;
	private Stack<Undo> undoStack;
	private int doublecrosses = 0;
	
	/**
	 * Create a fresh BinaryGameState
	 */
	public BinaryGameState () {
		/* 
		 * Having a constructor with no arguments allows the controller to
		 * instantiate the object without specifying a size.  The controller
		 * must call the init() method to specify the size to make the
		 * model usable.
		 */
	}

	/**
	 * Private constructor used to create a clone BinaryGameState
	 * @param width
	 * @param height
	 * @param state
	 * @param value
	 * @param player
	 * @param lastMove
	 * @param remainingLines
	 */
	private BinaryGameState (int width, int height, 
			int[] state, int value, 
			int player, Line lastMove, 
			List<Line> remainingLines, Stack<Undo> undoStack, int doublecrosses) {
		this.state = state;
		this.width = width;
		this.height = height;
		this.value = value;
		this.player = player;
		this.lastMove = lastMove;
		this.remainingLines = remainingLines;
		linesLeft = remainingLines.size();
		this.undoStack = undoStack;
		this.doublecrosses = doublecrosses;
	}
	
	public void init (Dimension size) {
		this.width = size.width;
		this.height = size.height;
		this.state = new int[height+1];
		this.value = 0;
		this.remainingLines = buildLineList();
		undoStack = new Stack<Undo>();
		player = 1;
	}
	
	/**
	 * This method is used to build the initial List of all Lines in a game.
	 * This should only be called by the init method (not a clone constructor).
	 * @return the list of all lines available in the game
	 */
	private List<Line> buildLineList() {
		List<Line> remainingLines = new LinkedList<Line>();
		for(int y = 0 ; y <= height ; y++) {
			for(int x = 0 ; x <= width ; x++) {
				if (x<width) {
					remainingLines.add(new Line(x,y,x+1,y));		// Horizontal Line
					linesLeft++;
				}
				if (y<height) {
					remainingLines.add(new Line(x,y,x,y+1));		// Vertical Line
					linesLeft++;
				}
			}
		}
		return remainingLines;
	}
	
	@Override
	public int addLine(Line line) {
		if (line == null) return -1;
		int index, bit, offset;
		Undo undo = new Undo(line, player, state.clone());
		/*
		 * First we need to calculate the position
		 * of the bit that represents the line that is
		 * getting added
		 */
		if(line.ay == line.by) { 					// Horizontal Line
			index = line.ay;								// Either y will do - they are the same
			if(line.ax == line.bx) {		
				return -1;									// This is a point, not a line.  Return an error
			} else {
				bit = 8;									// The most significant of the 4 bits represents the Horizontal line
				offset = 4 * Math.min(line.ax, line.bx);	// The offset is the location of the 4 node bits within the integer 32 bits
			}
		} else {									// Vertical Line
			index = Math.min(line.ay, line.by);				// All lines are from low to high so the correct index is the lowest y position
			if(line.ax == line.bx) {
				bit = 4;									// The third of the four bits represents the vertical line
				offset = 4 * line.ax;						// The offset is simply the x position on the line times 4
			} else {
				return -1;									// This is a point, not a line.  Return an error
			}
		}
		/*
		 * Now we have the position of our bit, we set that bit.
		 */
		if ((state[index] & (bit << offset)) == 0) {		// Check the bit is not already set
			state[index] |= (bit << offset);				// It is not, so we set it
			remainingLines.remove(line);
			linesLeft--;
			int result = claimBoxes(index,bit,offset,undo);
			if(result == 0) {
				player = (player % 2) + 1;					// A non scoring move means next player
			}
			if (result>1) doublecrosses++;
			lastMove = line;								// The move worked, so record it in case someone asks
			undoStack.push(undo);
			return result;									// Return the number of boxes scored by player
		} else {
			return -1;		// The bit was already set, so the move is not legal.
		}
	}
	
	@Override
	public int moveScore(Line line) {
		if (line == null) return -1;
		int index, bit, offset;
		/*
		 * First we need to calculate is the position
		 * of the bit that represents the line that is
		 * getting added
		 */
		if(line.ay == line.by) { 					// Horizontal Line
			index = line.ay;								// Any y will be the correct index
			if(line.ax == line.bx) {		
				return -1;									// This is a point, not a line.  Return an error
			} else {
				bit = 8;									// The most significant of the 4 bits represents the Horizontal line
				offset = 4 * Math.min(line.ax, line.bx);	// The offset is the location of the 4 node bits within the integer 32 bits
			}
		} else {									// Vertical Line
			index = Math.min(line.ay, line.by);				// All lines are from low to high so the correct index is the lowest y position
			if(line.ax == line.bx) {
				bit = 4;									// The third of the four bits represents the vertical line
				offset = 4 * line.ax;						// The offset is simply the x position on the line times 4
			} else {
				return -1;									// This is a point, not a line.  Return an error
			}
		}
		/*
		 * Now we have the position of our bit, we set that bit.
		 */
		if ((state[index] & (bit << offset)) == 0) {
			int score = 0;
			if (bit == 8) { 												// Horizontal line
				if (index > 0 && ((state[index-1] >> offset) & 12) == 12	// Check that the three other lines are
						&& ((state[index-1] >> offset+4) & 4) > 0) {		// set to complete the box above the line
					score++;
				}
				if (((state[index] >> offset) & 4) > 0						// Check the lines that complete the box below the line
						&& ((state[index+1] >> offset) & 8) > 0
						&& ((state[index] >> offset+4) & 4) > 0) {
					score++;
				}
				return score;
			}
			
			if (bit == 4) {													// Vertical line
				if (((state[index] >> offset) & 8) > 0						// Check the lines that complete the box to the side of the line
						&& ((state[index] >> offset+4) & 4) > 0	
						&& ((state[index+1] >> offset) & 8) > 0) {			// Set the player bit for that box
					score++;
				}
				if (((state[index] >> offset-4) & 12) == 12					// Check the lines that complete the box to the other side of the line
						&& ((state[index+1] >> offset-4) & 8) > 0) {		// Set the player bit for that box
					score++;
				}
				return score;
			}
			return -1;  // This should never happen!  This method should only be called with
						// a bit value of either 4 or 8.  Nothing else makes sense.
		} else {
			return -1;	// The bit was already set, so the move is not legal.
		}
	}

	/**
	 * This method calculates the number of boxes won by the player
	 * when a line is added.  Each new line could potentially win
	 * two boxes.  A horizontal line could complete the box of its
	 * own node, and also the box of the node above.  A vertical line
	 * could complete the box of its own node, and also that of the
	 * box opposite.
	 * @param index the index of the state integer in the array
	 * @param bit the line being added.  Either horizontal (8) or vertical (4) 
	 * @param offset  the position of the box bits within the integer
	 * @return
	 */
	private int claimBoxes(int index, int bit, int offset, Undo undo) {
		int score = 0;
		if (bit == 8) { 												// Horizontal line
			if (index > 0 && ((state[index-1] >> offset) & 12) == 12	// Check that the three other lines are
					&& ((state[index-1] >> offset+4) & 4) > 0) {		// set to complete the box above the line
				state[index-1] |= player << offset;						// index-1 and offset locate the node.  Set the player bit for that node's box.
				score++;
				value = (player==1) ? value + 1 : value -1;				// If a box is won here, we need to update the state value
			}
			if (((state[index] >> offset) & 4) > 0						// Check the lines that complete the box below the line
					&& ((state[index+1] >> offset) & 8) > 0
					&& ((state[index] >> offset+4) & 4) > 0) {
				state[index] |= player << offset;						// Set the player bits for that box
				score++;
				value = (player==1) ? value + 1 : value -1;
				
			}
			return score;
		}
		
		if (bit == 4) {													// Vertical line
			if (((state[index] >> offset) & 8) > 0						// Check the lines that complete the box to the side of the line
					&& ((state[index] >> offset+4) & 4) > 0	
					&& ((state[index+1] >> offset) & 8) > 0) {
				state[index] |= player << offset;						// Set the player bit for that box
				score++;
				value = (player==1) ? value + 1 : value -1;	
			}
			if (((state[index] >> offset-4) & 12) == 12					// Check the lines that complete the box to the other side of the line
					&& ((state[index+1] >> offset-4) & 8) > 0) {
				state[index] |= player << offset-4;						// Set the player bit for that box
				score++;
				value = (player==1) ? value + 1 : value -1;	
			}
			return score;
		}
		return -1;  // This should never happen!  This method should only be called with
					// a bit value of either 4 or 8.  Nothing else makes sense.
	}
	
	@Override
	public List<GameState> expand() {
		List<GameState> states = new LinkedList<GameState>();
		for(Line line : remainingLines) {
			GameState clone = this.clone();
			clone.addLine(line);
			states.add(clone);
		}
		return states;
	}
	
	@Override
	public Object getState() {
		int[] clone = new int[state.length];
		for(int i = 0 ; i < state.length ; i++) {
			clone[i] = state[i];
		}
		return clone;
	}
	
	@Override
	public GameState clone() {
		int[] newState = new int[state.length];		// Create a new Array for the state integers
		for(int i = 0 ; i < state.length ; i++) {
			newState[i] = state[i];					// Copy all the current values into the new Array
		}
		Stack<Undo> newUndoStack = new Stack<Undo>();
		newUndoStack.addAll(undoStack);
		BinaryGameState clone = 
			new BinaryGameState(
					width,
					state.length-1,
					newState,
					value,
					player,
					lastMove,
					new LinkedList<Line>(remainingLines), newUndoStack, doublecrosses);  // We also need a new List of remaining lines.
															// The Lines are immutable and so are safe to be
															// passed by reference.
		return clone;
	}

	@Override
	public List<Line> getRemainingLines() {
		return remainingLines;
	}

	@Override
	public int getValue() {
		return value;
	}
    
	@Override
	public Dimension getSize() {
		return new Dimension(width,height);
	}
	
	@Override
	public String toString() {
		String value = "";
		for(int i = 0 ; i < state.length ; i++) {
			value = value + "" + Tools.pad(Integer.toHexString(state[i]));
		}
		return value + (char)(player + 48);
	}
	
	@Override
	public void reset() {
		this.state = new int[height+1];
		this.value = 0;
		remainingLines = buildLineList();
		player = 1;
	}

	
	@Override
	public int player1Score() {
		int score = 0;
		for(int y = 0 ; y < height ; y++) {
			for(int x = 0 ; x < width; x++) {
				if(((state[y] >> (4*x)) & 3) == 1) {
					score++;
				}
			}
		}
		return score;
	}

	@Override
	public int player2Score() {
		int score = 0;
		for(int y = 0 ; y < height ; y++) {
			for(int x = 0 ; x < width; x++) {
				if(((state[y] >> (4*x)) & 3) == 2) {
					score++;
				}
			}
		}
		return score;
	}

	@Override
	public Dimension maxSize() {
		return new Dimension(7,20);
	}

	@Override
	public String getName() {
		return "Binary Representation";
	}
	
	@Override
	public int getPlayer() {
		return player;
	}

	@Override
	public Line lastMove() {
		return lastMove;
	}

	@Override
	public String getDescription() {
		return "This GameState stores game data as binary.";
	}

	@Override
	public int rollout() {
		GameState rollout = this.clone();
		List<Line> lines = rollout.getRemainingLines();
		Random rng = new Random();
		while(lines.size() > 0) {
			rollout.addLine(lines.remove(rng.nextInt(lines.size())));
		}
		
		return rollout.getValue();
	}
	
	public int getLinesLeft() {
		return linesLeft;
	}

	private class Undo {
		private Line line;
		private int p;
		private int[] oldState;
		private Undo (Line line, int p, int[] oldState) {
			this.line = line;
			this.p = p;
			this.oldState = oldState;
		}
		private void undo() {
			linesLeft++;
			remainingLines.add(line);
			player = p;
			state = oldState;
		}
	}

	@Override
	public void undo() {
		undoStack.pop().undo();
	}

	@Override
	public double[] getRolloutRewards() {
		GameState rollout = this.clone();
		List<Line> lines = rollout.getRemainingLines();
		Random rng = new Random();
		while(lines.size() > 0) {
			rollout.addLine(lines.remove(rng.nextInt(lines.size())));
		}
//		System.out.println(rollout.player1Score() + " + " + rollout.player2Score() + " = " + (rollout.player1Score()+rollout.player2Score()));
		return new double[]{rollout.player1Score(),rollout.player2Score()};
	}

	@Override
	public int nDoublecrosses() {
		return doublecrosses;
	}

}
