/*
 * Dots and Boxes
 * Submitted for the Degree of B.Sc. in Computer Science, 2010/2011
 * University of Strathclyde
 * Department of Computer and Information Sciences
 * @author Philip Rodgers
 */
package gameStates;

import java.awt.Dimension;
import java.util.List;

import data.Line;


public interface GameState {
	
	/**
	 * Add a line into the game state
	 * @param line - the Line that is to be added
	 * @return either the number of boxes won (0, 1 or 2) or -1 to indicate
	 * that a line is illegal. 
	 */
	int addLine (Line line);
	
	/**
	 * This method is used to get what would be the result of adding a line,
	 * but it does not actually add the line to the game state.
	 * @param line - the Line that we want to get the result for
	 * @return the score that adding that Line would result in
	 */
	int moveScore(Line line);
	
	/**
	 * Get a List of all remaining possible Lines in the game
	 * @return a Collection of all remaining Lines
	 */
	List<Line> getRemainingLines();
	
	/**
	 * The current value of the game state.  This will be from the point
	 * of view of player 1, so a value of -2 will mean that player 2 is
	 * ahead by 2 boxes.
	 * @return the value of the current state to player 1
	 */
	int getValue();
	
	/**
	 * This will return the underlying data structure for use by the
	 * GameStateView helper methods.  Type is Object to allow for any
	 * representations to be used.
	 * @return  the state representation Object
	 */
	Object getState();
	
	/**
	 * The player, be they computer or human, will need a copy of the
	 * current game state to play with.  This copy must not reference
	 * the original to ensure that changes made to the copy do not
	 * interfere with the REAL game state or any previous copy.
	 * @return a true deep copy clone of this game state
	 */
	GameState clone();
	
	/**
	 * Get the grid size (in boxes) of the current GameState
	 * @return the grid size of the game state
	 */
	Dimension getSize();

	/**
	 * This method creates a List of all child states of the current state
	 * @return a List of child states
	 */
	List<GameState> expand();
	
	/**
	 * @return player 1's current score
	 */
	int player1Score();
	
	/**
	 * @return player 2's current score
	 */
	int player2Score();
	
	/**
	 * Return the GameState to its original configuration.  This
	 * is used when a user wants to restart the current game.
	 */
	void reset();
	
	/**
	 * Game representations have limits on how big a game can be
	 * represented.  Conceptually, the BinaryGameState can represent
	 * games up to 7 boxes wide, but of unbounded height.  The SCState
	 * is a graph representation, and so unbounded.  However, both of
	 * these examples use the same nimber calculator, which can only
	 * deal with games up to 5 boxes wide and 5 boxes high.
	 * @return
	 */
	Dimension maxSize();
	
	/**
	 * This method initialises the GameState to a given size.  
	 * It was added to the interface to allow GameState objects
	 * to be constructed without specifying a size in the constructor. 
	 * @param size the Dimension of the game
	 */
	void init(Dimension size);
	
	/**
	 * This is the human readable name for the particular GameState.
	 * It is used on the setup screen to represent the state in the
	 * drop-down list of available states.
	 * @return the name of the state
	 */
	String getName();
	
	/**
	 * The int value of the current player (1 for Player 1 and 2 for
	 * Player 2)
	 * @return the current player
	 */
	int getPlayer();
	
	/**
	 * This method returns the last move that was made.  It is there
	 * to allow AI players to know what move the opponent made without
	 * the need to scan the current GameState and comparing it to the
	 * previous state.
	 * @return the last Line added to the game
	 */
	Line lastMove();
	
	/**
	 * Use this method to give a brief description of this
	 * GameState.  This description will be used for the tool-
	 * tip on the setup page.
	 * @return String description of this game state representation
	 */
	String getDescription();
	
	/**
	 * @return a String representation of the current position.  The
	 * assumption is that the result be the Hex values of the Array
	 * of integers as used in BinaryGameState concatenated in to a
	 * single String
	 */
	@Override
	String toString();
	
	int rollout();
	int getLinesLeft();
	void undo();
	int nDoublecrosses();
	double[] getRolloutRewards();
}
