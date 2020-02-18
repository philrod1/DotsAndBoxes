/*
 * Dots and Boxes
 * Submitted for the Degree of B.Sc. in Computer Science, 2010/2011
 * University of Strathclyde
 * Department of Computer and Information Sciences
 * @author Philip Rodgers
 */
package players;

import java.awt.Dimension;
import java.awt.Point;

import data.Coin;
import data.Line;
import gameStates.GameState;
import tools.Evaluator;

public class HumanPlayer extends AbstractPlayer {
	
	public HumanPlayer() {
		super();
		eva = new Evaluator();
	}

	private Line line;
	private Evaluator eva;
	
	@Override
	public synchronized Line makeMove(GameState gs) {
		interrupted = false;
		line = null;

		double[] results = eva.evaluate(gs);
		System.out.println(results[0] + " - " + results[1]);
		
		/*
		 *  Humans make moves by clicking on the GUI.
		 *  This method waits for the user's choice to
		 *  come through from the GUI then returns it
		 *  to the GameController.
		 */
		while(line == null || interrupted) {
			try {
				wait(500);
			} catch (InterruptedException e) {}
		}
		return line;
	}
	
	@Override
	public String getDescription() {
		return "This player is used for interactive games";
	}

	@Override
	public synchronized void sendLine(Line line) {
		this.line = line;
		notify();
	}

	@Override
	public String getName() {
		return "Human";
	}
	
	@Override
	public void reset() {
		interrupt();
	}
	
	private Coin[][] buildGraph(GameState gs) {
		int width = gs.getSize().width;
		int height = gs.getSize().height;
		int[] state = (int[]) gs.getState();
		Coin[][] graph = new Coin[width][height];
		int[][] winners = new int[width][height];
		
		for(int y = 0 ; y < height ; y++) {
			for(int x = 0 ; x < width ; x++) {
				graph[x][y] = new Coin(new Point(x,y),new Dimension(width, height));
			}
		}
		
		for(int y = 0 ; y < height ; y++) {
			for(int x = 0 ; x < width ; x++) {
				if (x < width-1) graph[x][y].setNeighbour(1, graph[x+1][y]);
				if (y < height-1) graph[x][y].setNeighbour(2, graph[x][y+1]);
			}
		}
		
		for(int y = 0 ; y <= height ; y++) {
			for(int x = 0 ; x <= width ; x++) {
				
				int gameNode = state[y] >> (4*x);
			
				if(x<width && y < height) winners[x][y] = gameNode & 3;
				
				if (x == width && (gameNode & 4) == 4) {
					graph[x-1][y].cutString(1);
				}
				
				if (y == height && (gameNode & 8) == 8) {
					graph[x][y-1].cutString(2);
				}
				
				if (x < width && (gameNode & 4) == 4) {
					graph[x][y].cutString(3);
				}
				
				if (y < height && (gameNode & 8) == 8) {
					graph[x][y].cutString(0);
				}
			}
		}
		return graph;
	}
}
