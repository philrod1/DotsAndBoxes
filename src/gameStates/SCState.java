/*
 * Dots and Boxes
 * Submitted for the Degree of B.Sc. in Computer Science, 2010/2011
 * University of Strathclyde
 * Department of Computer and Information Sciences
 * @author Philip Rodgers
 */
package gameStates;

import java.awt.Dimension;
import java.awt.Point;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

import players.MaskingNim;
import tools.Tools;
import data.Coin;
import data.Line;

public class SCState implements GameState {
	
	private final int NORTH = 0, EAST = 1, SOUTH = 2, WEST = 3;
	
	private Coin[][] graph;
	private int[][] winners;
	private Dimension size;
	private List<Line> remainingLines;
	private int value;
	private int[] state;
	private int player;
	private Line lastMove;
	private int linesLeft = 0;
	private int doublecrosses = 0;
	
	/**
	 * Create a new instance of SCState
	 */
	public SCState () {
		/* 
		 * Having a constructor with no arguments allows the controller
		 * instantiate the object without giving a size.  The controller
		 * must call the init() method to specify the size. This makes
		 * the model usable.
		 */
	}
	
	private SCState (
			Dimension size, 
			Coin[][] graph, 
			int[][] winners, 
			List<Line> remainingLines, 
			int value,
			int player, 
			Line lastMove,
			int doublecrosses) {
		this.size = size;
		this.graph = graph;
		this.winners = winners;
		this.remainingLines = remainingLines;
		this.value = value;
		state = new int[size.height+1];
		this.player = player;
		this.lastMove = lastMove;
		linesLeft = remainingLines.size();
		this.doublecrosses = doublecrosses;
	}
	
	@Override
	public void init(Dimension size) {
		this.size = size;
		remainingLines = buildGraph();
		state = new int[size.height+1];
		player = 1;
		MaskingNim mn = new MaskingNim(graph);
		doublecrosses = 0;
		System.out.println(Long.toBinaryString(mn.buildMask()));
	}

	@Override
	public int addLine(Line line) {
		/*
		 * In this method, we calculate which string of which coin
		 * is to be cut by examining the position of the line.  Once
		 * we know which string to cut, we take the return value of
		 * that cut and calculate which (if any) boxes were captured.
		 * -1 = Error
		 *  0 = No boxes captured
		 *  1 = First box captured only
		 *  2 = Second box captured only
		 *  3 = Both boxes captured (double-cross)
		 *  We use this information to update the array of box winners
		 *  Any of these cases not picked up by a switch statement
		 *  will pick flagged as an error eventually
		 */
		if (line == null) return -1;
		try {
			int result;
			if(line.ax == line.bx) {
				if(line.ax < size.width) {
					result = graph[line.ax][line.ay].cutString(WEST);
					switch(result) {
					case 1: winners[line.ax][line.ay] = player;
							break;
					case 2: winners[line.ax-1][line.ay] = player;
							break;
					case 3: winners[line.ax][line.ay] = player;
							winners[line.ax-1][line.ay] = player;
							doublecrosses++;
							break;
					}
				} else {
					result = graph[line.ax-1][line.ay].cutString(EAST);
					switch(result) {
					case 1: winners[line.ax-1][line.ay] = player;
							break;
					}
				}
			} else {
				if(line.ay < size.height) {
					result =  graph[line.ax][line.ay].cutString(NORTH);
					switch(result) {
					case 1: winners[line.ax][line.ay] = player; 
							break;
					case 2: winners[line.ax][line.ay-1] = player;
							break;
					case 3: winners[line.ax][line.ay] = player;
							winners[line.ax][line.ay-1] = player;
							doublecrosses++;
							break;
					}
				} else {
					result = graph[line.ax][line.ay-1].cutString(SOUTH);
					switch(result) {
					case 1: winners[line.ax][line.ay-1] = player;
							break;
					}
				}
			}
			remainingLines.remove(line);
			linesLeft--;
			result = (result > 1) ? result - 1 : result;  	// Decode the move score from the result
			value += (player == 1) ? result : -result;
			if (result == 0) {
				player = (player==1) ? 2 : 1;				// A score of 0 means the next player is up
			}
			lastMove = line;
			return result;
		} catch (NullPointerException e) {
			return -1;
		}
	}

	@Override
	public int moveScore(Line line) {
		try {
			if(line.ax == line.bx) {
				if(line.ax < size.width)
					return graph[line.ax][line.ay].moveScore(WEST);
				else
					return graph[line.ax-1][line.ay].moveScore(EAST);
			} else {
				if(line.ay < size.height)
					return graph[line.ax][line.ay].moveScore(NORTH);
				else
					return graph[line.ax][line.ay-1].moveScore(SOUTH);
			}
		} catch (NullPointerException e) {
			return -1;
		}
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
	public Object getState() {
		updateState();
		return state;
	}
	
	@Override
	public GameState clone() {
		/*
		 * Cloning the graph with a deep copy is slower than
		 * cloning a BinaryGameState.  SCStates also use more
		 * memory.  This is why the Nimber and Mini-Max
		 * algorithms do not use clones.
		 */
		int w = size.width;
		int h = size.height;
		
		Dimension cSize = new Dimension(size.width, size.height);
		List<Line> cLines = new LinkedList<Line>(remainingLines);
		
		Coin[][] cGraph = new Coin[w][h];
		int[][] cWinners = new int[w][h];
		
		for(int y = 0 ; y < h ; y++) {
			for(int x = 0 ; x < w ; x++) {
				cGraph[x][y] = graph[x][y].clone();
				cWinners[x][y] = winners[x][y];
			}
		}
		for(int y = 0 ; y < h ; y++) {
			for(int x = 0 ; x < w ; x++) {
				if(graph[x][y].hasNeighbour(EAST)) {
					cGraph[x][y].setNeighbour(EAST, cGraph[x+1][y]);
				}
				if(graph[x][y].hasNeighbour(SOUTH)) {
					cGraph[x][y].setNeighbour(SOUTH, cGraph[x][y+1]);
				}
			}
		}
		GameState clone = 
			new SCState(cSize, cGraph, cWinners, cLines, value, player, lastMove, doublecrosses);
		return clone;
	}

	@Override
	public Dimension getSize() {
		return size;
	}

	/**
	 * This method is called by init().  It creates the graph
	 * data structure of the new game.
	 * @return the new graph
	 */
	private List<Line> buildGraph() {
		int w = size.width;
		int h = size.height;
		List<Line> lines = new LinkedList<Line>();
		
		// Create a 2D array for the graph and for the box winners
		graph = new Coin[w][h];
		winners = new int[w][h];
		
		// Populate the graph with new Coins and build up the list of moves
		for(int y = 0 ; y < h ; y++) {
			for(int x = 0 ; x < w ; x++) {
				graph[x][y] = new Coin(new Point(x,y),size);
				lines.add(new Line(x,y,x+1,y));
				linesLeft++;
				lines.add(new Line(x, y, x, y+1));
				linesLeft++;
				if(x == w-1) {
					lines.add(new Line(x+1,y,x+1,y+1));
					linesLeft++;
				}
				if(y == h-1) {
					lines.add(new Line(x,y+1,x+1,y+1));
					linesLeft++;
				}
			}
		}
		
		// Create the connections within the graph.  The Coin having its
		// neighbor set will reciprocate, making this a two-way association.
		for(int y = 0 ; y < h ; y++) {
			for(int x = 0 ; x < w ; x++) {
				if (x < w-1) graph[x][y].setNeighbour(EAST, graph[x+1][y]);
				if (y < h-1) graph[x][y].setNeighbour(SOUTH, graph[x][y+1]);
			}
		}
		
		return lines;
	}
	
	/**
	 * Rather than writing a whole new GameStateView for each new
	 * GameState (as was originally planned), it was much easier to
	 * create a method to convert the graph state into an integer
	 * array state that BinaryGameStateView could display.  The View
	 * classes are quite complicated, and I know that
	 * BinaryGameStateView works correctly.
	 */
	private void updateState() {

		int w = size.width;
		int h = size.height;
		for(int y = 0 ; y < h ; y++) {
			for(int x = 0 ; x < w ; x++) {
				Coin coin = graph[x][y];
				List<Integer> lines = coin.getStrings(); 
				if(!lines.contains(NORTH)) {
					state[y] |= (8 << (4*x)); 
				}
				if(!lines.contains(WEST)) {
					state[y] |= (4 << (4*x)); 
				}
				if(y == h-1 && !lines.contains(SOUTH)) {
					state[y+1] |= (8 << (4*x)); 
				}
				if(x == w-1 && !lines.contains(EAST)) {
					state[y] |= (4 << (4*(x+1)));
				}
				state[y] |= (winners[x][y] << (4*x));
			}
		}
	}

	@Override
	public String toString() {
		updateState();
		String value = "";
		for(int i = 0 ; i < state.length ; i++) {
			value = value + "" + Tools.pad(Integer.toHexString(state[i]));
		}
		return value + (char)(player + 48);
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
	public int player1Score() {
		int score = 0;
		for(int y = 0 ; y < size.height ; y++) {
			for(int x = 0 ; x < size.width; x++) {
				if(winners[x][y] == 1) {
					score++;
				}
			}
		}
		return score;
	}

	@Override
	public int player2Score() {
		int score = 0;
		for(int y = 0 ; y < size.height ; y++) {
			for(int x = 0 ; x < size.width; x++) {
				if(winners[x][y] == 2) {
					score++;
				}
			}
		}
		return score;
	}

	@Override
	public void reset() {
		remainingLines = buildGraph();
		state = new int[size.height+1];
		player = 1;
		value = 0;
	}

	@Override
	public Dimension maxSize() {
		return new Dimension(20,20);
	}

	@Override
	public String getName() {
		return "Strings and Coins";
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
		return "This GameState stores game data as a graph.";
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

	@Override
	public int getLinesLeft() {
		return linesLeft;
	}

	@Override
	public void undo() {
		// TODO Auto-generated method stub
		
	}


	@Override
	public double[] getRolloutRewards() {
		GameState rollout = this.clone();
		List<Line> lines = rollout.getRemainingLines();
		Random rng = new Random();
		while(lines.size() > 0) {
			rollout.addLine(lines.remove(rng.nextInt(lines.size())));
		}
		return new double[]{rollout.player1Score(),rollout.player2Score()};
	}

	@Override
	public int nDoublecrosses() {
		return doublecrosses;
	}
}
