/*
 * Dots and Boxes
 * Submitted for the Degree of B.Sc. in Computer Science, 2010/2011
 * University of Strathclyde
 * Department of Computer and Information Sciences
 * @author Philip Rodgers
 */
package players;

import data.Line;

/**
 * This abstract class makes Player implementations a bit neater
 */
public abstract class AbstractPlayer implements Player {

	protected int player;
	protected boolean interrupted;
	
	public AbstractPlayer() {}

	@Override
	public void sendLine(Line line) {}
	
	@Override
	public String getDescription() {
		return "No information available for this player";
	}
	
	@Override
	public void setPlayerNumber(int player) {
		this.player = player;
	}
	
	@Override
	public void reset() {}
	
	@Override
	public void interrupt() {
		interrupted = true;
	}
	
}
