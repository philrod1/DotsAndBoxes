/*
 * Dots and Boxes
 * Submitted for the Degree of B.Sc. in Computer Science, 2010/2011
 * University of Strathclyde
 * Department of Computer and Information Sciences
 * @author Philip Rodgers
 */
package players;

import data.Line;
import gameStates.GameState;

public interface Player {

	/**
	 * Players are passed clone of the current GameState and
	 * must return the Line that the Player wishes to draw.
	 * How that is achieved is up to the implementations.
	 * @param gs is a clone of the current GameState.
	 * @return a Line object representing the desired move.
	 */
	Line makeMove(GameState gs);
	
	/**
	 * This is how HumanPlayers get the move selection from
	 * mouse clicks made in the view.
	 * @param line a Line object representing the user's
	 * choice of move
	 */
	void sendLine(Line line);
	
	/**
	 * Use this method to give a brief description of this
	 * Player.  This description will be used for the tool-
	 * tip on the setup page.
	 * @return String description of this player
	 */
	String getDescription();
	
	/**
	 * This method can be used to set the player position,
	 * in case Players need to know if they are MIN or MAX
	 * @param number the player position (1 or 2)
	 */
	void setPlayerNumber(int number) ;
	
	/**
	 * Get the human readable name for this Player. 
	 * @return the human readable name for this Player
	 */
	String getName();
	
	/**
	 * If a game is restarted, any data structures used by
	 * AI players will need to be reset.
	 */
	void reset();
	
	/**
	 * This method is used to notify any threads being used
	 * by Players that they are being interrupted.
	 */
	void interrupt();
}
