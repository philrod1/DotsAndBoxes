package players;

import gameStates.GameState;

import java.util.List;
import java.util.Random;

import data.Line;

public class RandomPlayer extends AbstractPlayer {

	private Random rng = new Random();

	@Override
	public Line makeMove(GameState gs) {
		
		try {
			Thread.sleep(500);
		} catch (InterruptedException e) {}
		
		List<Line> lines = gs.getRemainingLines();
		return lines.get(rng.nextInt(lines.size()));
	}
	
	@Override
	public String getDescription() {
		return "This player simply picks at random from all available moves";
	}

	@Override
	public String getName() {
		return "Random Player";
	}

}
