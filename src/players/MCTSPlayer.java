package players;

import gameStates.GameState;

import java.util.LinkedList;
import java.util.List;
import java.util.Random;

import data.Line;

public class MCTSPlayer extends AbstractPlayer {

	private int nPlayers;
	private TreeNode root;
	private int rewardDivisor;
	private long timeLimit;

	public MCTSPlayer() {
		this.nPlayers = 2;
		rewardDivisor = 8;
		timeLimit = 20000;
	}

	@Override
	public Line makeMove(GameState game) {
		long startTime = System.currentTimeMillis();
		root = new TreeNode(null, game);
		int count = 0;
		while (System.currentTimeMillis()-startTime < timeLimit) {
			root.selectAction();
			count++;
		}
		System.out.println(count);
		double bestValue = Double.NEGATIVE_INFINITY;
		Line bestMove = null;
		System.out.println("Player " + game.getPlayer());
		for (TreeNode child : root.children) {
			double value = child.rewards[game.getPlayer() - 1] / child.nVisits;
			System.out.println(child.move + " : " + value * rewardDivisor + " ("
					+ (int)(child.nVisits) + ")");
			if (value > bestValue) {
				bestValue = value;
				bestMove = child.move;
			}
		}
		System.out.println("----------------------------------------------");

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
				rewards = new double[] { 
						game.player1Score()/rewardDivisor,
						game.player2Score()/rewardDivisor };
			} else {
				cur.expand();
				TreeNode newNode = cur.select();
				game.addLine(newNode.move);
				visited.add(newNode);
				rewards = rollOut(newNode);
			}

//			System.out.println(visited.size());
//			System.out.println(rewards[0] + " : " + rewards[1] + " (" + game.getRemainingLines().size() + ")");

			for (TreeNode node : visited) {
				game.undo();
				node.updateStats(rewards);
			}
			this.updateStats(rewards);
		}

		public void expand() {
			List<Line> moves = game.getRemainingLines();
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
			double[] rewards = game.getRolloutRewards();
			return new double[]{rewards[0]/rewardDivisor, rewards[1]/rewardDivisor};
		}

		public void updateStats(double[] rewards) {
			nVisits++;
			for (int i = 0; i < rewards.length; i++) {
				this.rewards[i] += rewards[i];
			}
		}
	}

	@Override
	public String getName() {
		return "MCTS Player";
	}
}
