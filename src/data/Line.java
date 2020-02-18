/*
 * Dots and Boxes
 * Submitted for the Degree of B.Sc. in Computer Science, 2010/2011
 * University of Strathclyde
 * Department of Computer and Information Sciences
 * @author Philip Rodgers
 */
package data;

/**
 * The Class Line.  Lines are immutable objects that
 * are used to represent moves in the game.  Lines go
 * between two adjacent dots, and so have four co-
 * ordinates.
 */
public final class Line implements Comparable<Line> {

	public final int ax, ay, bx, by;
	protected final int id;

	/**
	 * Construct a Line object for use in Dots and Boxes.
	 * All parameters must be no more than 8 to be a valid
	 * Line in the Dots and Boxes game, however 7 bits are
	 * used for each parameter to encode the unique id so
	 * each parameter can have a maximum value of 127 before
	 * bad things happen.  That's a big game of Dots and Boxes!  
	 * @param ax - the x coordinate of the start dot
	 * @param ay - the y coordinate of the start dot
	 * @param bx - the x coordinate of the end dot
	 * @param by - the y coordinate of the end dot
	 */
	public Line (int ax, int ay, int bx, int by) {
		this.ax = ax;
		this.ay = ay;
		this.bx = bx;
		this.by = by;
		/*
		 * id is a unique identifier for the line.  It is used to 
		 * both check for equality and to provide a means of ordering
		 */
		id = (ax << 21) | (ay << 14) | (bx << 7) | by;
	}
	
	@Override
	public int compareTo(Line that) {
		return this.id - that.id;
	}
	
	@Override
	public String toString() {
		return "("+ax+","+ay+") to ("+bx+","+by+")";
	}
	
	@Override
	public boolean equals(Object that) {
		if (this == that) return true;
		if (!(that instanceof Line)) return false;
		return this.id == ((Line)that).id;
	}
	
	@Override
	public int hashCode() {
		return id;
	}
}
