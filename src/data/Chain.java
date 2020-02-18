package data;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Stack;

import tools.Evaluator;

public class Chain {
	private List<Coin> coins;
	private boolean isLoop;
	private int size = 0;
	private boolean changed;
	private Coin undoCoin;
	private Random rng = new Random();
	
	public Chain() {
		isLoop = false;
		changed = false;
		coins = new LinkedList<Coin>();
	}
	
	public Chain(List<Coin> coins) {
		this.coins = coins;
		size = coins.size();
		isLoop = isLoopy();
		changed = false;
	}
	
	public void fixCutStringIfAny() {
		if (undoCoin != null) {
			undoCoin.undoRemoveString();
			undoCoin = null;
		}
	}
	
	public void setUndo(Coin coin) {
		undoCoin = coin;
	}
	
	public void add(Coin coin) {
		if(!coins.contains(coin)) {
			coins.add(coin);
			changed = true;
			size++;
		}
	}
	
	public void addAll(List<Coin> coins) {
		for(Coin coin : coins) {
			add(coin);
		}
	}
	
	public void addAll(Chain chain) {
		undoCoin = chain.undoCoin;
		for(Coin coin : chain.getCoins()) {
			add(coin);
		}
	}
	
	public List<Coin> getCoins() {
		return coins;
	}

	public boolean remove (Coin coin) {
		if(coins.contains(coin)) {
			size--;
			changed = true;
			return coins.remove(coin);
		} else {
			return false;
		}
	}
	
	public int size() {
		return size;
	}
	
	public boolean isCapturable() {
		for (Coin coin : coins) {
			if (coin.isCapturable()) return true;
		}
		return false;
	}
	
	public boolean isLoony() {
		for (Coin coin : coins) {
			if (coin.isLoony()) return true;
		}
		return false;
	}
	
	public boolean isLoop() {
//		if (changed) {
//			isLoop = isLoopy();
//			changed = false;
//		}
//		return isLoop;
		return isLoopy();
	}
	

	private boolean isLoopy() {
		if(coins.size() < 4) return false;
		List<Coin> open = new LinkedList<Coin>(coins);
		List<Coin> visited = new LinkedList<Coin>();
		Stack<Coin> agenda = new Stack<Coin>();
		agenda.push(open.remove(0));
		while (!agenda.isEmpty()) {
			Coin coin = agenda.pop();
//			System.out.println(coin);
			if (visited.contains(coin)) {
//				System.out.println("### LOOP ###");
				return true;
			}
			visited.add(coin);
			Coin[] neighbours = coin.getNeighbours();
			for (Coin neighbour : neighbours) {
				if (neighbour != null && !visited.contains(neighbour)) {
					agenda.push(neighbour);
				}
			}
		}
		return false;
	}

	public void removeAll(Chain chain) {
		removeAll(chain.getCoins());
	}

	private void removeAll(List<Coin> coins) {
		this.coins.removeAll(coins);
		changed = true;
		size = coins.size();
	}
	
	public List<Chain> splitForks() {
		List<Chain> chains = new LinkedList<Chain>();
		int shortestLength = Integer.MAX_VALUE;
		int dir = 0;
		Coin neigh = null;
		
		List<Integer> dirOptions = null;
		Map<Integer,Coin> neighbourOptions = null;
		
		Coin fork = getFork();
		if (fork == null) {
			chains.addAll(decomposeSubgraphs());
		} else {
//			System.out.println("Forking...");
//			System.out.println(fork);
			
			Coin[] neighbours = fork.getNeighbours();
			
			for(int i = 0 ; i < 4 ; i++) {
				Coin neighbour = neighbours[i];
				if(neighbour != null) {
					fork.cutString(i);
					Chain chain = neighbour.getChain(i);
//					System.out.println("Cutting");
//					Evaluator.printChain(chain);
//					this.removeAll(chain);
//					System.out.println("Leaving");
//					Evaluator.printChain(this);
					if(chain.size() < shortestLength) {
						dirOptions = new LinkedList<Integer>();
						neighbourOptions = new HashMap<Integer, Coin>();
						shortestLength = chain.size();
						neighbourOptions.put(i, neighbour);
						dirOptions.add(i);
					} else if (chain.size() == shortestLength) {
						neighbourOptions.put(i, neighbour);
						dirOptions.add(i);
					}
//					this.addAll(chain);
					fork.unCutString(i, neighbour);
					chain.fixCutStringIfAny();
//					System.out.println("Then back to...");
//					Evaluator.printChain(this);
				}
			}
			int size = dirOptions.size();
			int nextInt = rng.nextInt(size);
			dir = dirOptions.get(nextInt);
			fork.cutString(dir);
			Chain chain = neighbourOptions.get(dir).getChain(dir);
//			System.out.println("Choosing");
//			Evaluator.printChain(chain);
//			System.out.println("Leaving");
//			Evaluator.printChain(this);
			chains.add(chain);
			this.removeAll(chain);
//			System.out.println(this.isLoop());
			if (this.size > 0) {
				chains.addAll(splitForks());
			}
		}
		
		return chains;
	}
	
	private List<Chain> decomposeSubgraphs() {
		List<Coin> open = new LinkedList<Coin>(coins);
		List<Coin> visited = new LinkedList<Coin>();
		List<Coin> agenda = new LinkedList<Coin>();
		List<Chain> chains = new LinkedList<Chain>();
		
		while(!open.isEmpty()) {
			Chain chain = new Chain();
			agenda.add(open.remove(0));
			while(!agenda.isEmpty()) {
				Coin coin = agenda.remove(0);
				open.remove(coin);
				if (!visited.contains(coin)) {
					chain.add(coin);
					visited.add(coin);
					List<Coin> neighbours = neighboursAsList(coin.getNeighbours());
					for(Coin neighbour : neighbours) {
						if(!agenda.contains(neighbour)) agenda.add(neighbour);
					}
				}
			}
			chains.add(chain);
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

	private Coin getFork() {
		for(Coin coin : coins) {
			if (coin.getStrings().size() > 2) return coin;
		}
		return null;
	}
}
