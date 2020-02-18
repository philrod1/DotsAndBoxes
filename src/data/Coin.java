/*
 * Dots and Boxes
 * Submitted for the Degree of B.Sc. in Computer Science, 2010/2011
 * University of Strathclyde
 * Department of Computer and Information Sciences
 * @author Philip Rodgers
 */
package data;

import java.awt.Dimension;
import java.awt.Point;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

/**
 * The Class Coin.  These objects are the coins in the
 * Strings and Coins representation of the game.
 */
public class Coin {

	private final int NORTH = 0, EAST = 1, SOUTH = 2, WEST = 3;
	private List<Integer> strings;
	private Coin[] neighbours;
	private Point position;
	private Dimension gSize;
	private final long f = Long.MIN_VALUE;
	private long stringMask = 0L;
	public boolean visited = false;
	private int undo = -1;
	
	/**
	 * Instantiates a new coin.
	 *
	 * @param position the position of this Coin in the graph array
	 * @param graphSize the overall size of the graph
	 */
	public Coin(Point position, Dimension graphSize) {
		strings = new LinkedList<Integer>();
		for(int i = 0 ; i < 4 ; i++) {
			strings.add(i);
		}
		neighbours = new Coin[4];
		this.position = position;
		this.gSize = graphSize;
		buildStringMask();
	}
	
	/**
	 * Sets the neighbour of this Coin to be the Coin
	 * passed in as a parameter, in the direction passed in.
	 * It also sets this Coin to be that Coin's neighbour.
	 *
	 * @param direction the direction of the neighbour
	 * @param neighbour the neighbouring Coin
	 */
	public void setNeighbour(int direction, Coin neighbour) {
		neighbours[direction] = neighbour;
		neighbour.putNeighbour((direction+2)%4, this);
	}
	
	/**
	 * Sets the neighbour of this Coin to be the Coin
	 * passed in as a parameter, in the direction passed in.
	 * 
	 * @param direction the direction of the neighbour
	 * @param neighbour the neighbouring Coin
	 */
	private void putNeighbour(int direction, Coin neighbour) {
		neighbours[direction] = neighbour;
	}
	
	/**
	 * Gets the neighbours.
	 *
	 * @return the neighbours
	 */
	public Coin[] getNeighbours() {
		return neighbours;
	}
	
	/**
	 * Gets the strings attached to this Coin.
	 *
	 * @return the strings
	 */
	public List<Integer> getStrings() {
		return strings;
	}
	
	/**
	 * Sets the strings attached to this Coin.
	 *
	 * @param strings the new strings
	 */
	private void setStrings(List<Integer> strings) {
		this.strings = strings;
	}
	
	/**
	 * Gets the position of this Coin in the graph.
	 *
	 * @return the position
	 */
	public Point getPosition() {
		return position;
	}
	
	/**
	 * Checks if this Coin is capturable.
	 *
	 * @return true, if this Coin is capturable
	 */
	public boolean isCapturable() {
		return strings.size() == 1;
	}
	
	/**
	 * Checks if this Coin is capturable in a specific
	 * direction.
	 *
	 * @param direction the specified direction
	 * @return true, if is this Coin is capturable in
	 * the specified direction
	 */
	private boolean isCapturable(int direction) {
		return strings.size() == 1 && strings.contains(direction);
	}
	
	/**
	 * Checks if this Coin is capturable when the graph is
	 * overlaid with the given mask.
	 *
	 * @param mask the overlay mask for the graph
	 * @return true, if the Coin is capturable when the
	 * graph is overlaid with the given mask.
	 */
	public boolean isCapturable(long mask) {
		List<Integer> ss = getStrings(mask);
		return ss.size() == 1;
	}
	
	/**
	 * Checks if the Coin is capturable in the
	 * specified direction when the graph is overlaid
	 * with the given mask.
	 *
	 * @param direction the specified direction
	 * @param mask the overlay mask for the graph
	 * @return true, if the Coin is capturable in the
	 * specified direction when the graph is overlaid
	 * with the given mask.
	 */
	private boolean isCapturable(int direction, long mask) {
		List<Integer> ss = getStrings(mask);
		return ss.size() == 1 && ss.contains(direction);
	}
	
	/**
	 * Checks if this Coin forms part of a loony position.
	 *
	 * @return true, if this Coin forms part of a loony
	 * position.
	 */
	public boolean isLoony() {
		if(strings.size() != 2) return false;
		int d1 = strings.get(0);
		int d2 = strings.get(1);
		if(neighbours[d1] != null && neighbours[d1].isCapturable((d1+2)%4) && moveScore(d2) == 0) {
			return true;
		}
		if(neighbours[d2] != null && neighbours[d2].isCapturable((d2+2)%4) && moveScore(d1) == 0) {
			return true;
		}
		return false;
	}
	
	/**
	 * Checks if this Coin forms part of a loony position,
	 * when the graph is overlaid with the given mask.
	 *
	 * @param mask the overlay mask for the graph
	 * @return true, if this Coin forms part of a loony
	 * position, when the graph is overlaid with the given
	 * mask.
	 */
	public boolean isLoony(long mask) {
		
		List<Integer> ss = getStrings(mask);
		
		if(ss.size() != 2) return false;
		int d1 = ss.get(0);
		int d2 = ss.get(1);
		if(neighbours[d1] != null 
				&& neighbours[d1].isCapturable((d1+2)%4,mask) 
				&& moveScore(d2,mask) == 0) {
			return true;
		}
		if(neighbours[d2] != null 
				&& neighbours[d2].isCapturable((d2+2)%4,mask) 
				&& moveScore(d1,mask) == 0) {
			return true;
		}
		return false;
	}
	
	/**
	 * Gets a list of strings that are both attached to this Coin,
	 * and present in the mask.
	 *
	 * @param mask the overlay mask for the graph
	 * @return the strings that would be attached to this
	 * Coin, if the graph were overlaid by the mask.
	 */
	public List<Integer> getStrings(long mask) {
		List<Integer> ss = new LinkedList<Integer>();
		for(int direction = NORTH ; direction <= WEST ; direction++) {
			if(((mask & getDirMask(direction)) >>> 1) > 0) {
				ss.add(direction);
			}
		}
		return ss;
	}

	/**
	 * Cut string.  This when a Player makes a move that
	 * cuts a string attached to this Coin.  It will also
	 * cut the string from the neighbour back to this coin.
	 *
	 * @param direction the direction of the string to be cut
	 * @return an int encoding the result of the cut. -1 = error,
	 * 0 = no score, 1 = first box won only, 2 = second box won 
	 * only and 3 = both boxes won (double-cross)
	 */
	public int cutString (Integer direction) {
		if (!strings.contains(direction)) return -1;
		strings.remove(direction);
		Coin neighbour = neighbours[direction];
		int myScore = (strings.size() == 0) ? 1 : 0;
		int neighbourScore = (neighbour==null) ? 0 : neighbour.removeString((direction+2)%4);
		neighbours[direction] = null;
		return myScore + neighbourScore;
	}
	
	/**
	 * Cuts the string attaching this Coin to the
	 * neighbour in the specified direction.
	 *
	 * @param direction the direction of the neighbour
	 * @return an int encoding the result of the cut.
	 */
	public int removeString (Integer direction) {
		neighbours[direction] = null;
		strings.remove(direction);
		return (strings.size() == 0) ? 2 : 0;
	}
	
	/**
	 * Capture this Coin, and possibly its neighbour, if
	 * this Coin is capturable.
	 *
	 * @return a List containing this Coin, and possibly
	 * its neighbour, if this Coin is capturable.
	 */
	public List<Coin> capture() {
		removeString(1);
		List<Coin> captured = new LinkedList<Coin>();
		if(isCapturable()) {
			int result = moveScore(strings.get(0));
			switch(result){
			case 1: captured.add(this); break;
			case 3: captured.add(this); captured.add(neighbours[strings.get(0)]); break;
			}
			cutString(strings.get(0));
		}
		return captured;
	}
	
	/**
	 * Capture this Coin, and possibly its neighbour, if
	 * this Coin is capturable when the graph is overlaid
	 * with the mask.
	 *
	 * @param mask the overlay mask
	 * @return the new mask with the strings connecting
	 * the capturable Coin(s) removed
	 */
	public long capture(long mask) {
		List<Integer> ss = getStrings(mask);
		if(ss.size() == 1) {
			mask &= ~getDirMask(ss.get(0));
		}
		return mask;
	}
	
	/**
	 * Check the result of a possible string cut.
	 * No changes are made.
	 *
	 * @param direction the direction of the string to be cut
	 * @return the resulting score if the dummy string was cut
	 */
	public int moveScore (Integer direction) {
		if(!strings.contains(direction)) return -1;
		Coin neighbor = neighbours[direction];
		return ((strings.size() == 1) ? 1 : 0)
			+ ((neighbor==null) ? 0 : neighbor.checkScore((direction+2)%4));
	}
	
	/**
	 * Get the single box score of cutting a string in the
	 * given direction.  No changes are made.
	 *
	 * @param direction the direction of the string to be cut
	 * @return the resulting single box score if the dummy
	 * string was cut.
	 */
	private int checkScore (Integer direction) {
		return (isCapturable(direction)) ? 1 : 0;
	}
	
	/**
	 * Check the result of a possible string cut, when the graph
	 * is overlaid by the mask.  No changes are made.
	 *
	 * @param direction the direction of the string to be cut
	 * @param mask the overlay mask
	 * @return the resulting score if the dummy string was cut
	 */
	public int moveScore (Integer direction, long mask) {
		
		List<Integer> ss = getStrings(mask);
		
		if(!ss.contains(direction)) return -1;
		Coin neighbor = neighbours[direction];
		return ((ss.size() == 1) ? 1 : 0)
			+ ((neighbor==null) ? 0 : neighbor.checkScore((direction+2)%4,mask));
	}
	
	/**
	 * Check the single box score of a possible string cut,
	 * when the graph is overlaid by the mask.  No changes
	 * are made.
	 *
	 * @param direction the direction of the string to be cut
	 * @param mask the overlay mask
	 * @return the resulting single box score if the dummy
	 * string was cut
	 */
	private int checkScore (Integer direction, long mask) {
		return (isCapturable(direction,mask)) ? 1 : 0;
	}
	
	/**
	 * Create a safe deep-copy clone of this Coin.
	 * 
	 * @return a safe deep-copy clone of this Coin
	 */
	public Coin clone() {
		Coin clone = new Coin(new Point(position.x, position.y),gSize);
		List<Integer> cloneStrings = new LinkedList<Integer>();
		for(Integer i : strings) {
			cloneStrings.add(new Integer(i.intValue()));
		}
		clone.setStrings(cloneStrings);
		return clone;
	}
	
	/**
	 * Checks if this Coin has a neighbour in the given direction
	 *
	 * @param direction the direction of the possible neighbour
	 * @return true, if this Coin has a neighbour in the given
	 * direction
	 */
	public boolean hasNeighbour(int direction) {
		return neighbours[direction] != null;
	}
	
	/**
	 * Checks if this Coin has a neighbour in the given direction,
	 * if the graph is overlaid with the mask
	 *
	 * @param direction the direction of the possible neighbour
	 * @param mask the mask
	 * @return true, if this Coin has a neighbour in the given
	 * direction, if the graph is overlaid with the mask
	 */
	public boolean hasNeighbour(int direction, long mask) {
		return hasNeighbour(direction) && (((getDirMask(direction) & mask) >>> 1 )  > 0);
	}
	
	/**
	 * Gets the neighbour of this Coin in the given direction
	 *
	 * @param direction the direction of the neighbour
	 * @return the neighbour of this Coin in the given direction
	 */
	public Coin getNeighbour(int direction) {
		return neighbours[direction];
	}
	
	@Override
	public String toString() {
		String output = "(" + position.x + "," + position.y + ") [";
		for(Coin neighbor : neighbours) {
			if(neighbor == null) output += "null, ";
			else output += "(" + neighbor.position.x +","+ neighbor.position.y + "), ";
		}
		output = output.substring(0,output.length()-2) + "] " + strings.toString();
		return output;
	}
	
	@Override
	public boolean equals(Object that) {
		if (this == that) return true;
		if (!(that instanceof Coin)) return false;
		return this.position.x == ((Coin)that).position.x && this.position.y == ((Coin)that).position.y;
	}
	
	/**
	 * Gets the string mask that represents this Coin
	 *
	 * @return the string mask that represents this Coin
	 */
	public long getStringMask() {
		buildStringMask();
		return stringMask;
	}
	
	/**
	 * Builds the string mask that represents this Coin
	 */
	private void buildStringMask() {
		int w = gSize.width;
		int h = gSize.height;
		int x = position.x;
		int y = position.y;
		boolean lastX = x == w-1;
		boolean lastY = y == h-1;
		long north = 0L;
		long west = 0L;
		long east = 0L;
		long south = 0L;
		
		
		if(strings.contains(NORTH)) {
			north = getDirMask(NORTH);
		}
		
		if(strings.contains(WEST)) {
			west = getDirMask(WEST);
		}
		
		if(lastY && strings.contains(SOUTH)) {
			south = getDirMask(SOUTH);
		}
		
		if(lastX && strings.contains(EAST)) {
			east = getDirMask(EAST);
		}
		
		stringMask = north | south | east | west;
	}
	
	/**
	 * Gets the mask that represents the string in the given
	 * direction for this Coin
	 *
	 * @param direction the direction of the string
	 * @return the mask that represents the string in the given
	 * direction for this Coin
	 */
	public long getDirMask(int direction) {
		
		int w = gSize.width;
		int h = gSize.height;
		int x = position.x;
		int y = position.y;
		
		switch(direction) {
		case NORTH: return f >>> (x+y*w);
		case EAST:  return f >>> (w*(h+1)) + x + (y*(w+1)) + 1;
		case SOUTH: return f >>> (x+(y+1)*w);
		case WEST:  return f >>> (w*(h+1)) + x + (y*(w+1));
		}
		
		return 0L;
	}
	
	/**
	 * Check if this Coin is captured, if the graph is 
	 * overlaid with the mask
	 *
	 * @param mask the overlay mask
	 * @return true, if this Coin is captured, if the graph
	 * is overlaid with the mask
	 */
	public boolean captured (long mask) {
		return ((getStringMask() & mask) >>> 1) == 0;
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		return (position.x << 16) | position.y;
	}

	/**
	 * Gets the unique string count.  Most coins will only
	 * have 2 unique strings, NORTH and WEST,  Coins down
	 * the right side of the graph will also have a unique
	 * string to the EAST, and Coins along the bottom of the
	 * graph will also have a unique string to the SOUTH.
	 *
	 * @return the unique string count
	 */
	public int getUniqueStringCount() {
		boolean lastX = position.x == gSize.width-1;
		boolean lastY = position.y == gSize.height-1;
		if (lastX && lastY) return 4;
		if (lastX || lastY) return 3;
		return 2;
	}
	
	public boolean isSafe (long mask) {
		//This isn't really safe.  We need to fix this at some point
		List<Integer> ss = getStrings(mask);
		return ss.size() > 2;
	}

	public Line getRemainingLine(long mask) {
		int string = getStrings(mask).get(0);
		switch (string) {
		case 0:	return new Line(position.x, position.y, position.x+1, position.y);
		case 1: return new Line(position.x+1, position.y, position.x+1, position.y+1);
		case 2:	return new Line(position.x, position.y+1, position.x+1, position.y+1);
		case 3: return new Line(position.x, position.y, position.x, position.y+1);
		}
		return null;
	}
	
	public char[][] getTextView () {
		int count = 0;
		char[][] chars = new char[][]{{' ',' ',' '},{' ',' ',' '},{' ',' ',' '}};
		if(strings.size() == 0) {
			return chars;
		} else {
			for(int i :strings) {
					count++;
					switch(i) {
					case 0: chars[1][0] = '|'; break;
					case 1: chars[2][1] = '-'; break;
					case 2: chars[1][2] = '|'; break;
					case 3: chars[0][1] = '-'; break;
					}
			}
			if(count > 2) {
				chars[1][1] = '#';
			} else {
				chars[1][1] = 'O';
			}
		}
		return chars;
	}
	
	public void unCutString(int string, Coin neighbour) {
		strings.add(string);
		setNeighbour(string, neighbour);
		neighbour.unCut((string+2)%4);
	}
	
	private void unCut(int string) {
		strings.add(string);
	}

//	public int chainLength(int direction, Coin start) {
//		if (this.equals(start)) return 0;
//		Coin neighbour = null;
//		int dir = 0;
//		for(int string : strings) {
//			if(string != (direction+2)%4) {
//				neighbour = neighbours[string];
//				dir = string;
//			}
//		}
//		if(neighbour == null) return 1;
//		return 1 + neighbour.chainLength(dir, start);
//	}
//	
//	public Chain getChain(int direction, Coin start) {
//		Chain chain = new Chain();
//		if (strings.size() < 3) {
//			chain.add(this);
////			System.out.println(this);
//			for (int dir : strings) {
//				if (dir != (direction + 2) % 4) {
////					System.out.println("Trying " + dir);
//					Coin neighbour = neighbours[dir];
//					if (neighbour != null) {
//						chain.addAll(neighbour.getChain(dir, start));
//					}
//				}
//			}
//		} else {
//			if(start.equals(this)) {
//				chain.add(this);
////				removeString(direction);
//			}
//		}
//		return chain;
//	}

	public Chain getChain(int dir) {
		Chain chain = new Chain();
		if(strings.size() > 2) {
			undo = (dir+2)%4;
			removeString(undo);
			chain.setUndo(this);
			return chain;
		}
		chain.add(this);
		
		for(int string : strings) {
			if ((string+2)%4 != dir) {
				if(neighbours[string] != null) {
					chain.addAll(neighbours[string].getChain(string));
				}
			}
		}
		
		return chain;
	}
	
	public void undoRemoveString() {
//		System.out.println("Undo " + undo);
		if (undo  > -1) {
			strings.add(undo);
			undo = -1;
		}
	}

	public void unRemoveString(int i) {
		strings.add(i);
	}
	
//	public List<Coin> getChain (int direction) {
//		List<Coin> chain = new LinkedList<Coin>();
//		Queue<Coin> agenda = new LinkedList<Coin>();
//		agenda.add(this);
//		while (!agenda.isEmpty()) {
//			Coin coin = agenda.poll();
//			chain.add(coin);
//			for(int dir : coin.getStrings()) {
//				if (!(dir == (direction+2)%4 
//						|| agenda.contains(coin) 
//						|| chain.contains(coin))) {
//					agenda.add(coin.getNeighbour(dir));
//				}
//			}
//		}
//		return chain;
//	}
}
