/*
 * Dots and Boxes
 * Submitted for the Degree of B.Sc. in Computer Science, 2010/2011
 * University of Strathclyde
 * Department of Computer and Information Sciences
 * @author Philip Rodgers
 */
package view;

import javax.swing.JPanel;

import data.Line;
import players.Player;


public interface GameStateView {
	
	/**
	 * GameStateView objects are helper objects that are used
	 * to build a GUI from a GameState object.  Each GameState
	 * implementation must have a matching GameStateView.
	 * Ideally this functionality should be in the GameState
	 * itself, but it is kept separate for reasons of efficiency.
	 * Only the original GameState needs a view, the clones do not.
	 * @return the JPanel view of the GameState
	 */
	JPanel getView();
	
	/**
	 * If there is a human Player using this view, the Player must
	 * be passed a reference to the view in order to get the result
	 * of clicking on a line in the view.
	 * @param player
	 */
	void addPlayer(Player player);

	/**
	 * This is the feedback mechanism for getting a human player's
	 * move choice from the view into the HumanPlayer code via the
	 * GameController.
	 * @param line
	 */
	void sendLine(Line line);
}
