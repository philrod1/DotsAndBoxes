/*
 * Dots and Boxes
 * Submitted for the Degree of B.Sc. in Computer Science, 2010/2011
 * University of Strathclyde
 * Department of Computer and Information Sciences
 * @author Philip Rodgers
 */
package players;

import gameStates.GameState;

import java.awt.Dimension;
import java.awt.Point;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

import data.Coin;
import data.Line;

public class MediumAI extends AbstractPlayer {

	private Random rng;
	private boolean opening = true;
	private Coin[][] graph;
	private int width, height;
	private int[] state;
	
	public MediumAI() {
		super();
		rng = new Random();
	}

	@Override
	public Line makeMove(GameState gs) {
		try {
			Thread.sleep(500);
		} catch (InterruptedException e) {}
		List<Line> lines = gs.getRemainingLines();
		interrupted = false;
		if(opening) {
			//Take any capturable boxes - very greedy!
			for(Line line : lines) {
				if (gs.moveScore(line) > 0) {
					if (interrupted) return null;
					return line;
				}
			}


			//Try not to give away any boxes
			List<Line> safeMoves = new LinkedList<Line>();
			for(Line line : lines) {
				boolean safe = true;
				GameState clone = gs.clone();
				clone.addLine(line);
				List<Line> oppenentMoves = clone.getRemainingLines();
				for(Line opMove : oppenentMoves) {
					if(clone.moveScore(opMove) > 0) {
						safe = false;
					}
				}
				if (safe) safeMoves.add(line);
			}
			
			// Pick a safe move, if there are any
			if (safeMoves.size() > 0) {
				if (interrupted) return null;
				return safeMoves.get(rng.nextInt(safeMoves.size()));
			}
			opening = false;
			if (interrupted) return null;
			return makeMove(gs);
		} else {
			// We can be gready at this stage (<6)
			if(gs.getRemainingLines().size() < 6) {
				if (interrupted) return null;
				return getBestLine(gs);
			}
			
			// Double-cross if we can
			Line doubleCross = getDoubleCross(gs);
			if(doubleCross != null) {
				if (interrupted) return null;
				return doubleCross;
			}
			else {
				for(Line line : lines) {
					if (gs.moveScore(line) > 0) {
						if (interrupted) return null;
						return line;
					}
				}
				if (interrupted) return null;
				return smallestChain(gs);
			}
		}
	}

	/**
	 * This used to be a Mini-Max algorithm, but gready
	 * is simpler, and just as effective at the very
	 * end of a game.
	 * @param gs GameState of the current node
	 * @return a line that scores at least 1 point
	 */
	private Line getBestLine(GameState gs) {
		for(Line line : gs.getRemainingLines()) {
			if (gs.moveScore(line) > 0) {
				if (interrupted) return null;
				return line;
			}
		}
		return gs.getRemainingLines().get(0);
	}

	/**
	 * This method uses nim-values to spot a double-cross manoeuvre
	 * @param gs GameState of the current node
	 * @return either a double-cross move or null if no such move was found
	 */
	private Line getDoubleCross(GameState gs) {
		int v = oneMoveValue(gs);
		boolean foundDC = false;
		Line dc = null;
		if(v == 1 || v == 0 || v == 2) {
			if(isLoony(gs)) {
				for(Line line : gs.getRemainingLines()) {
					GameState clone = gs.clone();
					int score = clone.addLine(line);
					if(score > 0 && isLoony(clone)) {
						if (interrupted) return null;
						return line;
					}
					if(score == 0 && !isLoony(clone)) {
						if(dc == null){
							foundDC = true;
							dc = line;
						} else {
							foundDC = false;
						}
					}
				}
			}
			if (interrupted) return null;
			if (foundDC) return dc;
		}
		return null;
	}

	@Override
	public String getDescription() {
		return "This player will play at level " +
		"that beginners will find challenging.";
	}

	@Override
	public String getName() {
		return "Medium AI";
	}

	@Override
	public void reset() {
		opening = true;
		interrupted = true;
	}
	
	private int oneMoveValue(GameState gs) {
		int v = 0;
		for(Line line : gs.getRemainingLines()) {
			v += gs.moveScore(line);
		}
		return v;
	}

//	private Line smallestChain(GameState s) {
//		Line best = null;
//		int smallest = Integer.MAX_VALUE;
//		List<Line> lines = s.getRemainingLines();
//		for (Line line : lines) {
//			GameState clone = s.clone();
//			clone.addLine(line);
//			int thisChain = chainLength(clone);
//			if (thisChain < smallest) {
//				smallest = thisChain;
//				best = line;
//			}
//		}
//		return best;
//	}

	private int chainLength(GameState s) {
		List<Line> lines = s.getRemainingLines();
		
		for (Line line : lines) {
			int moveScore = s.moveScore(line);
			if (moveScore > 0) {
				s.addLine(line);
				return moveScore + chainLength(s);
			}
		}
		return 0;
	}
	
	private Coin[][] buildGraph() {
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
	
	private boolean isLoony(GameState gs) {
		state = (int[]) gs.getState();
		width = gs.getSize().width;
		height = gs.getSize().height;
		graph = buildGraph();
		for(Coin[] row : graph) {
			for(Coin coin : row) {
				if (coin.isLoony()) return true;
			}
		}
		return false;
	}
	
	public List<Line> getMoves(GameState gs) {
//		try {
//			Thread.sleep(500);
//		} catch (InterruptedException e) {}

		List<Line> moves = new LinkedList<Line>();
		List<Line> lines = gs.getRemainingLines();
		interrupted = false;
		if(opening) {
			//Take any capturable boxes - very greedy!
			for(Line line : lines) {
				if (gs.moveScore(line) > 0) {
					if (interrupted) return null;
					moves.add(line);
					return moves;
				}
			}


			//Try not to give away any boxes
			List<Line> safeMoves = new LinkedList<Line>();
			for(Line line : lines) {
				boolean safe = true;
//				GameState clone = gs.clone();
//				clone.addLine(line);
//				List<Line> oppenentMoves = clone.getRemainingLines();
//				for(Line opMove : oppenentMoves) {
//					if(clone.moveScore(opMove) > 0) {
//						safe = false;
//					}
//				}
				if (safe) safeMoves.add(line);
			}
			
			// Pick a safe move, if there are any
			if (safeMoves.size() > 0) {
				if (interrupted) return null;
				return safeMoves;
			}
			opening = false;
			if (interrupted) return null;
			return getMoves(gs);
		} else {
			// We can be gready at this stage (<6)
			if(gs.getRemainingLines().size() < 6) {
				if (interrupted) return null;
				moves.add(getBestLine(gs));
				return moves;
			}
			
			// Double-cross if we can
			Line doubleCross = getDoubleCross(gs);
			if(doubleCross != null) {
				if (interrupted) return null;
				moves.add(doubleCross);
				return moves;
			}
			else {
				for(Line line : lines) {
					if (gs.moveScore(line) > 0) {
						if (interrupted) return null;
						moves.add(line);
						return moves;
					}
				}
				if (interrupted) return null;
				moves.add(smallestChain(gs));
				return moves;
			}
		}
	}
	
	private Line smallestChain(GameState s) {
		List<Line> best = new LinkedList<Line>();
		int smallest = Integer.MAX_VALUE;
		List<Line> lines = s.getRemainingLines();
		for (Line line : lines) {
			GameState clone = s.clone();
			clone.addLine(line);
			int thisChain = chainLength(clone);
			if (thisChain < smallest) {
				smallest = thisChain;
				best = new LinkedList<Line>();
				best.add(line);
			} else if (thisChain == smallest) {
				best.add(line);
			}
		}
		if (smallest == 2) {
			return pickMiddleLine(best, s);
		}
		return best.get(rng.nextInt(best.size()));
	}

	private Line pickMiddleLine(List<Line> best, GameState s) {
//		System.out.println("Picking middle");
		GameState clone = s.clone();
		int score = 0;
		for(Line next : best) {
			score = 0;
			clone.addLine(next);
			for (Line line : best) {
				if(line != next) {
					score += clone.moveScore(line);
				}
			}
			if (score == 2) return next;
			clone.undo();
		}
		return best.get(rng.nextInt(best.size()));
	}

}
