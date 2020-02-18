/*
 * Dots and Boxes
 * Submitted for the Degree of B.Sc. in Computer Science, 2010/2011
 * University of Strathclyde
 * Department of Computer and Information Sciences
 * @author Philip Rodgers
 */
package dnb;

import controller.GameController;

public class DotsAndBoxes {

	/**
	 * This is just the entry point that kick-starts the game
	 * @param args unused
	 */
	public static void main(String[] args) {
		
		/*
		 * All we need to do is create the controller, it will
		 * assume control and do everything else need to start
		 * the game.
		 */
		new GameController();
		
	}

}
