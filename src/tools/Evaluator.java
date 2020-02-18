package tools;

import gameStates.GameState;

import java.awt.Dimension;
import java.awt.Point;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

import data.Chain;
import data.Coin;
import data.Line;

public class Evaluator {

	private Random rng = new Random();
	private boolean opening = true;

	public double[] evaluate (GameState state) {

		GameState game = state.clone();
		
//		if (opening) {
			makeSafeMoves(game);
//		}
		
		List<Coin> graph = buildGraph(game);
		
//		System.out.println("Evaluating");
//		printGraph(graph);
		
		List<Chain> chains = getChains(graph);
		int nLongChains = 0;
		int sharedBoxes = 0;
		int longTotal = 0;
		int nLoops = 0;
		int loopTotal = 0;
		int player = game.getPlayer();
		int boxTotal = 0;
		boolean p1wins = true;
		
		for (Chain chain : chains) {
//			printChain(chain);
//			System.out.println("Chain size: " + chain.size());
			boxTotal += chain.size();
			if(chain.size() > 2) {
				if(chain.isLoop()) {
					nLoops++;
					loopTotal += chain.size();
				} else {
					nLongChains++;
					longTotal += chain.size();
				}
			} else {
//				if(chain.size() == 2 && chain.isCapturable()) {
//					nLongChains++;
//					longTotal += chain.size();
//				} else {
					sharedBoxes += chain.size();
//				}
			}
		}
		
//		System.out.println("Box total: " + boxTotal);
//		System.out.println(nLongChains + " long chains");
		
		if((nLongChains + game.nDoublecrosses())%2 == 0) {
			p1wins = true;
		} else {
			p1wins = false;
		}

		double[] rewards = new double[] {game.player1Score(), game.player2Score()};

		
		if(chains.size() == 1) {
			rewards[player-1] += chains.get(0).size();
		} else if (chains.size() > 1) {		
			rewards[0] += sharedBoxes / 2;
			rewards[1] += sharedBoxes / 2;
			rewards[player-1] += sharedBoxes % 2;
			if (p1wins) {
				rewards[1] += 2 * (nLongChains -1);
				rewards[0] += longTotal - (2 * (nLongChains -1));
				rewards[1] += 4 * nLoops;
				rewards[0] += loopTotal - (4 * nLoops);
			} else {
				rewards[0] += 2 * (nLongChains -1);
				rewards[1] += longTotal - (2 * (nLongChains -1));
				rewards[0] += 4 * nLoops;
				rewards[1] += loopTotal - (4 * nLoops);
			}
		}

//		Tools.show("clone", game);
		return rewards;
	}
	
	private void makeSafeMoves(GameState game) {		
		Line safe = null;
		int count = 0;
		do {
			count++;
			safe = getSafeMove(game);
			game.addLine(safe);
		} while (safe != null);
		opening = count > 1;
	}

	private List<Chain> getChains(List<Coin> coins) {
//		System.out.println("Evaluator.getChains()");
		List<Coin> open = new LinkedList<Coin>(coins);
		List<Chain> chains = new LinkedList<Chain>();
		List<Chain> forks = new LinkedList<Chain>();
		List<Coin> agenda = new LinkedList<Coin>();
		List<Coin> visited = new LinkedList<Coin>();
		Chain chain;
		
		while(!open.isEmpty()) {
			chain = new Chain();
			agenda.add(open.remove(0));
			boolean fork = false;
			while(!agenda.isEmpty()) {
				Coin coin = agenda.remove(0);
				open.remove(coin);
				if (!visited.contains(coin)) {
					chain.add(coin);
					visited.add(coin);
					List<Coin> neighbours = neighboursAsList(coin.getNeighbours());
					fork |= neighbours.size() > 2;
					for(Coin neighbour : neighbours) {
						if(!agenda.contains(neighbour)) agenda.add(neighbour);
					}
				}
			}
			
			if (fork) forks.add(chain);
			else chains.add(chain);
		}

//		System.out.println("Number of chains = " + chains.size());
//		System.out.println("Number of forks  = " + forks.size());

			for(Chain forking : forks) {
				chains.addAll(forking.splitForks());
			}
		
		return chains;
	}

	private List<Coin> neighboursAsList(Coin[] neighbours) {
		List<Coin> list = new LinkedList<Coin>();
		for (Coin neighbour : neighbours) {
			if (neighbour != null) {
				list.add(neighbour);
			}
		}
		return list;
	}
	
	private List<Coin> buildGraph(GameState game) {
		int[] state = (int[]) game.getState();
		List<Coin> graph = new LinkedList<Coin>();
		int width = game.getSize().width;
		int height = game.getSize().height;
		Coin[][] grid = new Coin[width][height];
		
		for(int y = 0 ; y < height ; y++) {
			for(int x = 0 ; x < width ; x++) {
				Coin coin = new Coin(new Point(x,y),new Dimension(width, height));
				grid[x][y] = coin;
				if (x > 0) coin.setNeighbour(3, grid[x-1][y]);
				if (y > 0) coin.setNeighbour(0, grid[x][y-1]);
				
				int nodeVal = state[y] >> (4*x);
				if ((nodeVal & 4) == 4) coin.cutString(3);
				if ((nodeVal & 8) == 8) coin.cutString(0);
				
				if (x == width-1) {
					nodeVal = state[y] >> (4*(x+1));
					if ((nodeVal & 4) == 4) coin.cutString(1);				
				}
				
				if (y == height-1) {
					nodeVal = state[y+1] >> (4*x);
					if ((nodeVal & 8) == 8) coin.cutString(2);				
				}
				
				graph.add(coin);
			}
		}
		List<Coin> available = new LinkedList<Coin>();
		for(Coin coin : graph) {
			if (coin.getStrings().size() > 0) available.add(coin); 
		}
		return available;
	}
	
	public static void printChain(Chain chain) {
		printGraph(chain.getCoins());
	}
	
	private static void printGraph(List<Coin> chain) {
		if (chain == null) return;
		char[][] buffer = new char[15][15];
		
		for (Coin coin : chain) {
			for(int x = 0 ; x < 3 ; x++) {
				for(int y = 0 ; y < 3 ; y++) {
					char[][] cChars = coin.getTextView();
					buffer[(coin.getPosition().x * 3) + x]
						  [(coin.getPosition().y * 3) + y] = cChars[x][y];
				}
			}
		}
		
		System.out.println("--------------------------");
		for(int y = 0 ; y < 15 ; y++) {
			for(int x = 0 ; x < 15 ; x++) {
				System.out.print(buffer[x][y]);
			}
			System.out.println();
		}
		System.out.println("--------------------------");
	}

	private Line getSafeMove(GameState gs) {
		List<Line> lines = gs.getRemainingLines();
		
		//Grab free boxes
		//TODO: Leave a double cross if not opening
		for (Line line : lines) {
			if (gs.moveScore(line) > 0) {
				return line;
			}
		}
		// Try not to give away any boxes
		List<Line> safeMoves = new LinkedList<Line>();
		for (Line line : lines) {
			boolean safe = true;
			GameState clone = gs.clone();
			clone.addLine(line);
			List<Line> oppenentMoves = clone.getRemainingLines();
			for (Line opMove : oppenentMoves) {
				if (clone.moveScore(opMove) > 0) {
					safe = false;
				}
			}
			if (safe)
				safeMoves.add(line);
		}
		// Pick a safe move, if there are any
		if (safeMoves.size() > 0) {
			return safeMoves.get(rng.nextInt(safeMoves.size()));
		} else {
			return null;
		}
	}	
}
