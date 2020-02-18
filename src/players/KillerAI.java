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
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.TreeMap;

import data.Coin;
import data.Line;

public class KillerAI extends AbstractPlayer {

	private Random rng;
	private Coin[][] graph;
	private int w, h, width, height;
	private Map<Long,Integer> map;
	private int nStrings = 0;
	private int[] state;
	
	public KillerAI() {
		super();
		rng = new Random();
		map = new TreeMap<Long,Integer>();
	}

	@Override
	public Line makeMove(GameState gs) {
		List<Line> lines = gs.getRemainingLines();
		interrupted = false;

		if(lines.size() > 35 || lines.size() < 4) {
			for(Line line : lines) {
				if (gs.moveScore(line) > 0) {
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
			
			List<Line> options = new LinkedList<Line>();
			
			/*
			 *  Only if it is safe to do so, pick one of the
			 *  parallel neighbours of the opponent's last
			 *  move.  This should avoid loopy games.
			 */
			Line lastMove = gs.lastMove();
			if(lastMove != null) {
				if (lastMove.ax == lastMove.bx) {
					// vertical line
					Line local = new Line(lastMove.ax-1, lastMove.ay, lastMove.ax-1, lastMove.by);
					if(safeMoves.contains(local)) {
						options.add(local);
					}
					local = new Line(lastMove.ax+1, lastMove.ay, lastMove.ax+1, lastMove.by);
					if(safeMoves.contains(local)) {
						options.add(local);
					}
				} else {
					// horizontal line
					Line local = new Line(lastMove.ax, lastMove.ay+1, lastMove.bx, lastMove.by+1);
					if(safeMoves.contains(local)) {
						options.add(local);
					}
					local = new Line(lastMove.ax, lastMove.ay-1, lastMove.bx, lastMove.by-1);
					if(safeMoves.contains(local)) {
						options.add(local);
					}
				}
				if (options.size() > 0) {
					return options.get(rng.nextInt(options.size()));
				}
			}

			// Neither of the neighbour moves is safe, so pick a safe move instead.
			if (safeMoves.size() > 0) {
				return safeMoves.get(rng.nextInt(safeMoves.size()));
			}
		} else {
			//We should be sufficiently into the game to get into some nimber action
			int nimber = nimber(gs);
			if (interrupted) return null;
			
			if (nimber == 1000) {  // Excellent! We have a Loony position.  We should win from here.
				
				List<Line> doubleCrosses = new LinkedList<Line>();
				List<Line> other = new LinkedList<Line>();
				
				for(Line line : lines) {
					
					GameState clone = gs.clone();
					int score = clone.addLine(line);
					int nim = nimber(clone);
					
					if (score > 0 && nim == 1000) {
						return line;
					} else if (score == 0 && nim == 0) {
						doubleCrosses.add(line);
					} else if (score > 0 && nim > 0) {
						other.add(line);
					}
				}
				
				if(doubleCrosses.size() > 0) {
					Line doubleCross = doubleCrosses.get(rng.nextInt(doubleCrosses.size()));
					return doubleCross;
				}
				
				if(other.size() > 0) {
					Line l = other.get(rng.nextInt(other.size()));
					return l;
				}
			}
			
			if (nimber > 0) {
				// This should be a winning position.  Taking any free boxes is safe
				for(Line line : lines) {
					if (gs.moveScore(line) > 0) {
						return line;
					}
				}
				
				// With no free boxes to take, we need to ensure we put our opponent
				// in a zero position.  Preferably without giving away any boxes, but
				// that may not be possible.
				List<Line> giveNothing = new LinkedList<Line>();
				List<Line> giveOne = new LinkedList<Line>();
				List<Line> giveTwo = new LinkedList<Line>();
				
				for(Line line : lines) {
					GameState clone = gs.clone();
					clone.addLine(line);
					int nim = nimber(clone);  //Any non-zero nimbers are bad moves
					int opponentScore = oneMoveValue(clone);
					if(nim == 0) {
						if(opponentScore == 0){
							giveNothing.add(line);
						} else if (opponentScore == 1) {
							giveOne.add(line);
						} else if (opponentScore == 2){
							giveTwo.add(line);
						}
					}
				}
				// Select a move a random from the best possible choices
				if(giveNothing.size() > 0) {
					Line line = giveNothing.get(rng.nextInt(giveNothing.size()));
					return line;
				} else if (giveOne.size() > 0) {
					Line line = giveOne.get(rng.nextInt(giveOne.size()));
					return line;
				} else if (giveTwo.size() > 0) {
					Line line = giveTwo.get(rng.nextInt(giveTwo.size()));
					return line;
				}
			} else /* nimber == 0 */ {  // Bugger.  This is a losing position.
				
				//Take any capturable boxes - we may as well.
				for(Line line : lines) {
					if (gs.moveScore(line) > 0) {
						return line;
					}
				}
				
				// Are there any non-scoring moves that do not leave a
				// Loony position?  Preferably without our opponent scoring.
				
				List<Line> giveNothing = new LinkedList<Line>();
				List<Line> giveOne = new LinkedList<Line>();
				List<Line> giveTwo = new LinkedList<Line>();
				
				for(Line line : lines) {
					GameState clone = gs.clone();
					clone.addLine(line);
					int nim = nimber(clone);
					if(nim < 1000) {
						GameState clone2 = gs.clone();
						clone2.addLine(line);
						int opponentScore = oneMoveValue(clone2);
						if(opponentScore == 0){
							giveNothing.add(line);
						} else if (opponentScore == 1) {
							giveOne.add(line);
						} else if (opponentScore == 2){
							giveTwo.add(line);
						}
					}
				}
				if(giveNothing.size() > 0) {
					Line line = giveNothing.get(rng.nextInt(giveNothing.size()));
					return line;
				} else if (giveOne.size() > 0) {
					Line line = giveOne.get(rng.nextInt(giveOne.size()));
					return line;
				} else if (giveTwo.size() > 0) {
					Line line = giveTwo.get(rng.nextInt(giveTwo.size()));
					return line;
				}
				
				// If all else fails, give them the smallest chain and prey for a mistake.
				return smallestChain(gs);
			}	
		}
		return null;
	}
	
	@Override
	public String getDescription() {
		return "This player will play at level " +
				"that experienced players will find challenging.";
	}

	@Override
	public String getName() {
		return "Killer AI";
	}
	
	@Override
	public void reset() {
		map = new TreeMap<Long,Integer>();
		interrupt();
	}
	
	private int oneMoveValue(GameState gs) {
		int v = 0;
		for(Line line : gs.getRemainingLines()) {
			v += gs.moveScore(line);
		}
		return v;
	}

	private Line smallestChain(GameState s) {
		Line best = null;
		int smallest = Integer.MAX_VALUE;
		List<Line> lines = s.getRemainingLines();
		for (Line line : lines) {
			GameState clone = s.clone();
			clone.addLine(line);
			int thisChain = chainLength(clone);
			if (thisChain < smallest) {
				smallest = thisChain;
				best = line;
			}
		}
		return best;
	}

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
	
	private int nimber(GameState gs) {
		state = (int[]) gs.getState();
		width = gs.getSize().width;
		height = gs.getSize().height;
		graph = buildGraph();
		h = graph[0].length;
		w = graph.length;
		long mask = buildMask();
		return nim(mask);
	}
	
	private long buildMask() {
		nStrings = 0;
		long mask = 0L;
		for(int y = 0 ; y < h ; y++) {
			for(int x = 0 ; x < w ; x++) {
				mask |= graph[x][y].getStringMask();
				if (x == w-1 && y == h-1) nStrings += 4;
				else if (x == w-1 || y == h-1) nStrings += 3;
				else nStrings += 2;
			}
		}
		return mask;
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
	
	private int nim(long mask) {

		if (mask == 0) 				return 0;

		if (map.containsKey(mask)) 	return map.get(mask);

		if (isLoony(mask)) 			return 1000;
		
		if (hasCapturable(mask)) {
			mask = captureAll(mask);
									return nim(mask);
		}

		List<Long> parts = split(mask);
		if(parts.size() == 2) 		return nimSum(nim(parts.get(0)), nim(parts.get(1)));

		int value = getMex(takeEach(mask));
		map.put(mask, value);
									return value;
	}

	private boolean hasCapturable(long mask) {
		for(Coin[] row : graph) {
			for(Coin coin : row) {
				if (coin.isCapturable(mask)) return true;
			}
		}
		return false;
	}

	private boolean isLoony(long mask) {
		for(Coin[] row : graph) {
			for(Coin coin : row) {
				if (coin.isLoony(mask)) return true;
			}
		}
		return false;
	}

	private List<Long> split(long mask) {
		
		long splitters = 0L;
		boolean needNode = true;
		
		List<Coin> agenda = new LinkedList<Coin>();
		
		for(Coin[] row : graph) {
			for(Coin coin : row) {
				coin.visited = false;
				if(needNode && !coin.captured(mask)) {
					agenda.add(coin);
					needNode = false;
				}
			}
		}
		
		while(agenda.size() > 0) {
			Coin node = agenda.remove(0);
			node.visited = true;
			for(int i = 0 ; i < 4 ; i++) {
				if(node.hasNeighbour(i, mask)) {
					Coin neighbor = node.getNeighbour(i);
					if((!neighbor.visited) && (!agenda.contains(neighbor))) agenda.add(neighbor);
				}
			}
			splitters |= (node.getStringMask() & mask);
		}
		
		mask ^= splitters;
		
		List<Long> parts = new LinkedList<Long>();
		
		parts.add(splitters);
		if(mask != 0) {
			parts.add(mask);
		}
		
		return parts;
	}

	private long captureAll(long mask) {
		for(Coin[] row : graph) {
			for(Coin coin : row) {
				long newMask = coin.capture(mask);
				if(newMask != mask) {
					return newMask;
				}
			}
		}
		return mask;
	}

	private List<Long> takeEach(long mask) {
		List<Long> masks = new LinkedList<Long>();
			for(int i = 0 ; i < nStrings ; i++) {
				long cutMask = Long.MIN_VALUE >>> i;
				long newMask = mask & ~cutMask;
				
				if(newMask != mask) {
					masks.add(newMask);
				}
			}
		return masks;
	}

	private int nimSum(int nimber1, int nimber2) {
		return (nimber1 == 1000 || nimber2 == 1000) ? 1000 : nimber1 ^ nimber2;
	}
	
	private int getMex(List<Long> masks) {
		int[] nims = new int[masks.size()];
		for (int i = 0 ; i < nims.length ; i++) {
			nims[i] = nim(masks.get(i));
		}
		int m = mex(nims);
		return m;
	}

	private int mex(int[] nimbers) {
		int mex = 0;
		Arrays.sort(nimbers);
		for(int i = 0 ; i < nimbers.length ; i++) {
			if(nimbers[i] == mex) {
				mex++;
			} else if (nimbers[i] > mex){
				break;
			}
		}
		return mex;
	}
}
