package players;

import gameStates.GameState;

import java.awt.Dimension;
import java.awt.Point;
import java.util.Random;
import java.util.Scanner;

import data.Coin;
import data.Line;

public class TestPlayer extends AbstractPlayer {

	private Random rng = new Random();
	@Override
	public Line makeMove(GameState gs) {

		
		Coin[][] graph = buildGraph(gs);
		MaskingNim mn = new MaskingNim(graph);
		long mask = mn.buildMask();
		if(mn.isSafe(mask)) {
			System.out.println("Safe.");
		} else {
			System.out.println("Not safe.");
		}
		if(mn.isLoony(mask)) {
			System.out.println("Loony.");
		} else {
			System.out.println("Not Loony.");
		}
		
		System.out.println();
		
		GameState clone = gs.clone();
		for(Line line : gs.getRemainingLines()) {
			int result = clone.addLine(line);
			Coin[][] g2 = buildGraph(gs);
			MaskingNim m = new MaskingNim(g2);
			long mask2 = m.buildMask();
			System.out.print(line + "  (" + result + ")  ");
			if(m.isSafe(mask2)) {
				System.out.print("Safe.  ");
			} else {
				System.out.print("Not safe.  ");
			}
			if(m.isLoony(mask2)) {
				System.out.println("Loony.");
			} else {
				System.out.println("Not Loony.");
			}
			clone.undo();
		}
		
		System.out.println();
		
		Scanner scanner = new Scanner(System.in);
		scanner.nextLine();
		
			if(mn.isSafe(mask) || !mn.isLoony()) {
				
				for(Coin[] row : graph) {
					for(Coin coin : row) {
						if (coin.isCapturable(mask)) {
							Line line = coin.getRemainingLine(mask);
							mask = mn.makeMove(line, mask);
							System.out.println("< Capturing " + line);
							return line;
						}
					}
				}
				
			} else {
				
				if(mn.isLoony(mask)) {
					for(Coin[] row : graph) {
						for(Coin coin : row) {
							if (coin.isCapturable(mask)) {
								Line line = coin.getRemainingLine(mask);
								long mask2 = mn.makeMove(line, mask);
								if(mn.isLoony(mask2)) {
									System.out.println("> Capturing " + line);
									return line;
								}
							}
						}
					}
				}					
			}
			
			
			System.out.println("Easy move");
			Player easy = new EasyAI();
			
			System.out.println();
			return easy.makeMove(gs);
	}

	@Override
	public String getName() {
		return "Test Player";
	}
	

	
	private Coin[][] buildGraph(GameState gs) {
		int width = gs.getSize().width;
		int height = gs.getSize().height;
		int[] state = (int[]) gs.getState();
		Coin[][] graph = new Coin[width][height];
		int[][] winners = new int[width][height];
		
		for(int y = 0 ; y < height ; y++) {
			for(int x = 0 ; x < width ; x++) {
				graph[x][y] = new Coin(new Point(x,y),new Dimension(width, height));
			}
		}
		
		for(int y = 0 ; y < height ; y++) {
			for(int x = 0 ; x < width ; x++) {
				if (x < width-1) graph[x][y].setNeighbour(1, graph[x+1][y]);
				if (y < height-1) graph[x][y].setNeighbour(2, graph[x][y+1]);
			}
		}
		
		for(int y = 0 ; y <= height ; y++) {
			for(int x = 0 ; x <= width ; x++) {
				
				int gameNode = state[y] >> (4*x);
			
				if(x<width && y < height) winners[x][y] = gameNode & 3;
				
				if (x == width && (gameNode & 4) == 4) {
					graph[x-1][y].cutString(1);
				}
				
				if (y == height && (gameNode & 8) == 8) {
					graph[x][y-1].cutString(2);
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

}
