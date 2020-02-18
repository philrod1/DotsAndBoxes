/*
 * Dots and Boxes
 * Submitted for the Degree of B.Sc. in Computer Science, 2010/2011
 * University of Strathclyde
 * Department of Computer and Information Sciences
 * @author Philip Rodgers
 */
package players;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import data.Coin;
import data.Line;

/**
 * The Class MaskingNim.  This Class encapsulates the nimber
 * algorithm.  It started as a section of the Strings and
 * Coins GameState representation, but was separated out into
 * its own Class so that BinaryGameState (or any other GameState
 * representation in the future) could make use of it.
 */
public class MaskingNim {

	private Coin[][] graph;
	private int w, h;
	private Map<Long,Integer> map;
	private int nStrings = 0;
	int count = 0;
	
	/**
	 * Instantiates a new masking nimber calculator for the
	 * graph passed in.
	 *
	 * @param graph the graph that makes up the game representation
	 */
	public MaskingNim(Coin[][] graph) {
		this.graph = graph;
		this.map = new TreeMap<Long, Integer>();
		h = graph[0].length;
		w = graph.length;
	}
	
	/**
	 * Builds a long mask from the graph.
	 *
	 * @return the mask
	 */
	public long buildMask() {
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

	/**
	 * This is the public method called to calculate the
	 * nimber value of the graph.  This method allows a
	 * previously populated hash map of results to be used
	 * for quicker calculations
	 *
	 * @param map the map
	 * @return the nim-value of the graph
	 */
	public int nimber(Map<Long, Integer> map) {
		this.map = map;
		long mask = buildMask();
		return nim(mask);
		
	}
	
	/**
	 * This is the public method called to calculate the
	 * nimber value of the graph.
	 *
	 * @return the nimber value of the graph
	 */
	public int nimber() {
		long mask = buildMask();
		return nim(mask);
	}
	
	/**
	 * Checks if the graph is loony.  Sometimes we only care
	 * if a position is loony or not.  This method provides
	 * the answer without the need for a full nimber calculation.
	 * Checking if a graph is loony is fairly trivial, and
	 * decidable in linear time --- O(n)
	 * 
	 * @return true, if the graph is loony
	 */
	public boolean isLoony() {
		long mask = buildMask();
		return isLoony(mask);
	}
	
	/**
	 * This is the heart of the nimber algorithm.  It is a
	 * recursive function that calculates the nim-value of
	 * a graph.  It uses a mask to overlay the graph with
	 * the changes, so that the graph itself is not
	 * corrupted.  The mask removes the need for expensive
	 * clone() operations, and also makes hashing efficient
	 *
	 * @param mask the mask being used
	 * @return the nim-value of the graph (1000 means loony)
	 */
	private int nim(long mask) {
		/*
		 * A completed game (no more strings) has nimber 0
		 */
		if (mask == 0) 				return 0;
		
		/*
		 * have we been here before?
		 */
		if (map.containsKey(mask)) 	return map.get(mask);
		
		/*
		 * It is easy to decide if a graph is loony.
		 */
		if (isLoony(mask)) 			return 1000;
		
		/*
		 * The nim-value of a graph with capturable coins is
		 * the same as the resulting graph after taking all
		 * the capturable coins
		 */
		if (hasCapturable(mask)) {
			mask = captureAll(mask);
									return nim(mask);
		}
		
		/*
		 * Divide and Conquer!
		 * The nimber calculations are more efficient if the
		 * nim-values of separate subgraphs are are calculated
		 * and combined using XOR.  See nimSum().
		 */
		List<Long> parts = split(mask);
		if(parts.size() == 2) 		return nimSum(nim(parts.get(0)), nim(parts.get(1)));
		
		/*
		 * The value of a graph with no capturable coins is
		 * the mex of all the sub-graphs obtained by cutting
		 * individual strings.  See mex().
		 */
		int value = getMex(takeEach(mask));
		map.put(mask, value);
									return value;
	}

	/**
	 * Checks to see if the graph has any capturable Coins,
	 * when overlaid with the given mask
	 *
	 * @param mask the overlay mask
	 * @return true, if successful
	 */
	public boolean hasCapturable(long mask) {
		for(Coin[] row : graph) {
			for(Coin coin : row) {
				if (coin.isCapturable(mask)) return true;
			}
		}
		return false;
	}
	
	/**
	 * Checks if the graph is loony, when overlaid
	 * with the mask.
	 *
	 * @param mask the overlay mask
	 * @return true, if the graph is loony, when
	 * overlaid with the mask.
	 */
	public boolean isLoony(long mask) {
		for(Coin[] row : graph) {
			for(Coin coin : row) {
				if (coin.isLoony(mask)) return true;
			}
		}
		return false;
	}
	
	/**
	 * Divide and Conquer!
	 * This method has the effect of splitting the graph into 
	 * unconnected subgraphs, using breadth-first search.
	 *
	 * @param mask the overlay mask
	 * @return a list of new masks, each representing a
	 * different subgraph
	 */
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
	
	/**
	 * Updates the overlay mask with all capturable Coins
	 * captured.
	 *
	 * @param mask the overlay mask
	 * @return the updated mask
	 */
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
	
	/**
	 * Creates a List of masks, each one representing the
	 * result of taking one of the remaining moves.
	 *
	 * @param mask the overlay mask
	 * @return a List of new masks
	 */
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
	
	/**
	 * This method calculates the size of the graph
	 * in Coins.
	 *
	 * @return the number of Coins in the graph
	 */
	public int size() {
		return graph.length * graph[0].length;
	}
	
	/**
	 * This method uses XOR to calculate the nim-value of
	 * two subgraphs combined into a single graph.  If
	 * either of the two subgraphs is loony, the combined
	 * graph will also be loony.
	 *
	 * @param nimber1 the nim-value of the first subgraph
	 * @param nimber2 the nim-value of the second subgraph
	 * @return the nim-value of the combined subgraphs
	 */
	private int nimSum(int nimber1, int nimber2) {
		return (nimber1 == 1000 || nimber2 == 1000) ? 1000 : nimber1 ^ nimber2;
	}
	
	/**
	 * This method calculates nim-values for a set of
	 * subgraphs, then returns the minimal excludant of
	 * those nimbers
	 *
	 * @param masks the masks representing the subgraphs
	 * @return the minimal excludant of the subgraphs' nimbers
	 */
	private int getMex(List<Long> masks) {
		int[] nims = new int[masks.size()];
		for (int i = 0 ; i < nims.length ; i++) {
			nims[i] = nim(masks.get(i));
		}
		int m = mex(nims);
		return m;
	}
	
	/**
	 * This method returns the minimal excludant of
	 * a set of positive integers.  The minimal
	 * excludant is the lowest positive integer not
	 * in the set.
	 * @param nimbers
	 * @return
	 */
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

	public long makeMove(Line line, long mask) {
		int bit;
		if(line.ay == line.by){
			bit = line.ax + line.ay * w;
		} else {
			bit = w * (h + 1) + line.ax + line.ay * (w + 1);
		}
//		System.out.println(bit);
		long lineMask = ~(Long.MIN_VALUE >>> bit);
//		System.out.println("Line mask: " + Long.toBinaryString(lineMask));
		return mask & lineMask;
	}

	public boolean isSafe(long mask) {
		for(Coin[] row : graph) {
			for(Coin coin : row) {
				if (coin.isSafe(mask))
					return true;
			}
		}
		return false;
	}
	
	public boolean[] getInfo(long mask) {
		boolean safe = false, capturable = false, loony = false;
		for(Coin[] row : graph) {
			for(Coin coin : row) {
				if (coin.isSafe(mask))
					safe = true;
				if (coin.isCapturable(mask))
					capturable = true;
				if (coin.isLoony(mask))
					loony = true;
				if(safe && capturable && loony) {
					return new boolean[]{true,true,true};
				}
			}
		}
		return new boolean[]{safe,capturable,loony};
	}
}