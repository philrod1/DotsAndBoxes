package players;

import java.awt.Dimension;
import java.awt.Point;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

import data.Coin;
import data.Line;
import gameStates.GameState;
import tools.Evaluator;

public class GuidedMCTS extends AbstractPlayer {

	private int nPlayers;
	private TreeNode root;
	private int rewardDivisor;
	private long timeLimit;
	private Random rng;
	private MediumAI medium;
	private Evaluator eva;

	public GuidedMCTS() {
		this.nPlayers = 2;
		rewardDivisor = 8;
		timeLimit = 30000;
		rng = new Random();
		medium = new MediumAI();
		eva = new Evaluator();
	}
	
	public void setTimeLimit(int seconds) {
		timeLimit = seconds * 1000;
	}
	
	public void setRewardDivisor (int rewardDivisor) {
		this.rewardDivisor = rewardDivisor;
	}

	@Override
	public Line makeMove(GameState game) {
		double[] exp = eva.evaluate(game);
		System.out.println("Epected result: " + exp[0] + " to " + exp[1]);
		List<Line> lines = game.getRemainingLines();
		if (lines.size() > 45) {

			// try {
			// Thread.sleep(500);
			// } catch (InterruptedException e) {}
			//
			// if (interrupted) return null;
			//
			// Take any capturable boxes - very greedy!
			for (Line line : lines) {
				if (game.moveScore(line) > 0) {
					return line;
				}
			}
			// Try not to give away any boxes
			List<Line> safeMoves = new LinkedList<Line>();
			for (Line line : lines) {
				boolean safe = true;
				GameState clone = game.clone();
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
			// return a random safe move
			if (safeMoves.size() > 0) {
				return safeMoves.get(rng.nextInt(safeMoves.size()));
			}

		}

//		List<Line> medMoves = medium.getMoves(game);
//
//		if (medMoves.size() == 1) {
//			return medMoves.get(0);
//		}

		long startTime = System.currentTimeMillis();
		root = new TreeNode(null, game);
		int count = 0;
		while (System.currentTimeMillis() - startTime < timeLimit) {
			root.selectAction();
			count++;
		}
//		System.out.println(count);
		double bestValue = Double.NEGATIVE_INFINITY;
		Line bestMove = null;
//		System.out.println("Player " + game.getPlayer());
		for (TreeNode child : root.children) {
			double value = child.rewards[game.getPlayer() - 1] / child.nVisits;
			System.out.println(child.move + " : " + value * rewardDivisor
					+ " (" + (int) (child.nVisits) + ")");
			if (value > bestValue) {
				bestValue = value;
				bestMove = child.move;
			}
		}
//		System.out.println("----------------------------------------------");

		return bestMove;
	}

	private class TreeNode {
		private GameState game;
		private Random r = new Random();
		private Line move;
		private double epsilon = 1e-6;

		TreeNode[] children;
		double nVisits;
		double[] rewards;

		private TreeNode(Line move, GameState game) {
			this.move = move;
			this.game = game;
			rewards = new double[nPlayers];
		}

		public void selectAction() {

			List<TreeNode> visited = new LinkedList<TreeNode>();
			TreeNode cur = this;

			while (!cur.isLeaf() && game.getRemainingLines().size() > 0) {
				cur = cur.select();
				try {
					game.addLine(cur.move);
				} catch (Exception e) {
					System.out.println(">>" + game);
				}
				visited.add(cur);
			}

			double[] rewards;

			if (game.getRemainingLines().size() == 0) {
				rewards = new double[] { game.player1Score() / rewardDivisor,
						game.player2Score() / rewardDivisor };
			} else {
				cur.expand();
				TreeNode newNode = cur.select();
				game.addLine(newNode.move);
				visited.add(newNode);
				rewards = rollOut(newNode);
			}

			// System.out.println(visited.size());
			// System.out.println(rewards[0] + " : " + rewards[1] + " (" +
			// game.getRemainingLines().size() + ")");

			for (TreeNode node : visited) {
				game.undo();
				node.updateStats(rewards);
			}
			this.updateStats(rewards);
		}

		public void expand() {
			List<Line> moves = medium.getMoves(game);
			children = new TreeNode[moves.size()];
			if (moves.size() == 0) {
				System.out.println("Error expanding " + moves.size()
						+ " children.");
			}
			int i = 0;
			for (Line m : moves) {
				children[i] = new TreeNode(m, game);
				i++;
			}
		}

		private TreeNode select() {
			TreeNode selected = children[0];
			double bestValue = Double.NEGATIVE_INFINITY;
			if (children.length == 0) {
				System.out.println("NO children to select.");
			}

			for (TreeNode c : children) {
				double uctValue = c.rewards[game.getPlayer() - 1]
						/ (c.nVisits + epsilon)
						+ Math.sqrt(Math.log(nVisits + 1)
								/ (c.nVisits + epsilon)) + r.nextDouble()
						* epsilon;
				if (uctValue > bestValue) {
					selected = c;
					bestValue = uctValue;
				}
			}

			return selected;
		}

		public boolean isLeaf() {
			return children == null;
		}

		public double[] rollOut(TreeNode tn) {

			// GameState rollout = game.clone();
			// int n = game.getLinesLeft();
			// for (int i = 0 ; i < n ; i++) {
			// rollout.addLine(medium.makeMove(rollout));
			// }

			// Coin[][] graph = buildGraph(game);
			// MaskingNim mn = new MaskingNim(graph);
			// long mask = mn.buildMask();
			// boolean brk = false;
			// while (rollout.getLinesLeft() > 0) {
			// if(mn.isSafe(mask) || !mn.isLoony()) {
			// for(Coin[] row : graph) {
			// for(Coin coin : row) {
			// if (coin.isCapturable(mask)) {
			// Line line = coin.getRemainingLine(mask);
			// mask = mn.makeMove(line, mask);
			// rollout.addLine(line);
			// brk = true;
			// }
			// if (brk) break;
			// }
			// if (brk) break;
			// }
			// if (brk) break;
			//
			// } else {
			//
			// if(mn.isLoony(mask)) {
			// for(Coin[] row : graph) {
			// for(Coin coin : row) {
			// if (coin.isCapturable(mask)) {
			// Line line = coin.getRemainingLine(mask);
			// long mask2 = mn.makeMove(line, mask);
			// if(mn.isLoony(mask2)) {
			// rollout.addLine(line);
			// mask = mask2;
			// brk = true;
			// }
			// }
			// if (brk) break;
			// }
			// if (brk) break;
			// }
			// if (brk) break;
			// }
			// }
			//
			// rollout.addLine(rollout.getRemainingLines().get(rng.nextInt(rollout.getLinesLeft())));
			// }

//			double[] rewards = game.getRolloutRewards();
			double[] rewards = new double[2];
			int n = 20;
			for(int i = 0 ; i < n ; i++) {
				double[] result = eva.evaluate(game);
				rewards[0] += result[0];
				rewards[1] += result[1];
			}
			rewards[0] /= n;
			rewards[1] /= n;
			return new double[] { rewards[0] / rewardDivisor,
					rewards[1] / rewardDivisor };
		}

		public void updateStats(double[] rewards) {
			nVisits++;
			for (int i = 0; i < rewards.length; i++) {
				this.rewards[i] += rewards[i];
			}
		}
	}

	// private boolean isLoony(GameState gs) {
	// int[] state = (int[]) gs.getState();
	// int width = gs.getSize().width;
	// int height = gs.getSize().height;
	// graph = buildGraph();
	// for(Coin[] row : graph) {
	// for(Coin coin : row) {
	// if (coin.isLoony()) return true;
	// }
	// }
	// return false;
	// }

	private Coin[][] buildGraph(GameState gs) {
		int width = gs.getSize().width;
		int height = gs.getSize().height;
		int[] state = (int[]) gs.getState();
		Coin[][] graph = new Coin[width][height];
		int[][] winners = new int[width][height];

		for (int y = 0; y < height; y++) {
			for (int x = 0; x < width; x++) {
				graph[x][y] = new Coin(new Point(x, y), new Dimension(width,
						height));
			}
		}

		for (int y = 0; y < height; y++) {
			for (int x = 0; x < width; x++) {
				if (x < width - 1)
					graph[x][y].setNeighbour(1, graph[x + 1][y]);
				if (y < height - 1)
					graph[x][y].setNeighbour(2, graph[x][y + 1]);
			}
		}

		for (int y = 0; y <= height; y++) {
			for (int x = 0; x <= width; x++) {

				int gameNode = state[y] >> (4 * x);

				if (x < width && y < height)
					winners[x][y] = gameNode & 3;

				if (x == width && (gameNode & 4) == 4) {
					graph[x - 1][y].cutString(1);
				}

				if (y == height && (gameNode & 8) == 8) {
					graph[x][y - 1].cutString(2);
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

	@Override
	public String getName() {
		return "Guided MCTS";
	}
}
