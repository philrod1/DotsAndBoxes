/*
 * Dots and Boxes
 * Submitted for the Degree of B.Sc. in Computer Science, 2010/2011
 * University of Strathclyde
 * Department of Computer and Information Sciences
 * @author Philip Rodgers
 */
package players;

import gameStates.GameState;

import java.util.LinkedList;
import java.util.List;
import java.util.Random;

import data.Line;

public class EasyAI extends AbstractPlayer {

	private Random rng;
	
	public EasyAI() {
		super();
		rng = new Random();
	}

	@Override
	public Line makeMove(GameState gs) {
		List<Line> lines = gs.getRemainingLines();
		interrupted = false;
		/*
		 *  Sleep for half a second.  This simply stops AI vs. AI
		 *  being over in a flash.
		 */
		try {
			Thread.sleep(500);
		} catch (InterruptedException e) {}
		
		if (interrupted) return null;
		
		//Take any capturable boxes - very greedy!
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
		if (safeMoves.size() > 0) {
			return safeMoves.get(rng.nextInt(safeMoves.size()));
		}
		
		//Give away the smallest chain
		return smallestChain(gs);
	}
	
	@Override
	public String getDescription() {
		return "This player will play at level " +
				"suitable for absolute beginners.";
	}

	@Override
	public String getName() {
		return "Easy AI";
	}
	
	@Override
	public void reset() {
		interrupt();
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
}
