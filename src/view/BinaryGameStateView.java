/*
 * Dots and Boxes
 * Submitted for the Degree of B.Sc. in Computer Science, 2010/2011
 * University of Strathclyde
 * Department of Computer and Information Sciences
 * @author Philip Rodgers
 */
package view;

import gameStates.GameState;
import players.Player;

import java.awt.Dimension;
import java.util.LinkedList;
import java.util.List;

import javax.swing.JPanel;

import data.Line;



public class BinaryGameStateView implements GameStateView {

	private final GameState gs;
	private final Dimension size;
	private final NodePanel[][] nodes;
	private final GamePanel gp;
	private List<Player> players;
	private int[] state;
	private String[] names;
	
	public BinaryGameStateView(GameState gs) {
		this.gs = gs;
		names = new String[2];
		size = gs.getSize();
		nodes = buildNodes();
		gp = new GamePanel(nodes, gs, names);
		players = new LinkedList<Player>();
		state = new int[size.height+1];
		updateState();
	}
	
	private void updateState() {
		String string = gs.toString();
		String hex = string.substring(0, string.length()-1);
		int currentPlayer = Integer.parseInt(string.substring(string.length()-1));
		gp.setPlayer(currentPlayer);
		for(int i = 0 ; i < state.length ; i++) {
			state[i] = Integer.parseInt(hex.substring(i*8, (i+1)*8), 16);
		}
	}

	private NodePanel[][] buildNodes() {
		NodePanel[][] nps = new NodePanel[size.width+1][size.height+1];
		for(int y = 0 ; y <= size.height ; y++) {
			for(int x = 0 ; x <= size.width ; x++) {
				nps[x][y] = new NodePanel(x, y, size, this);
			}
		}
		return nps;
	}

	private void updateNodes() {
		updateState();
		for(int y = 0 ; y <= size.height ; y++) {
			for(int x = 0 ; x <= size.width; x++) {
				int row = state[y];
				nodes[x][y].getBox().setPlayer((row >> (x*4)) & 3);
				int lineValue = (row >> (x*4)) & 12;
				switch(lineValue) {
				case 12:	nodes[x][y].getVertical().setClicked(true);
							nodes[x][y].getHorizontal().setClicked(true);
							break;
				case 8:		nodes[x][y].getVertical().setClicked(false);
							nodes[x][y].getHorizontal().setClicked(true);
							break;
				case 4:		nodes[x][y].getVertical().setClicked(true);
							nodes[x][y].getHorizontal().setClicked(false);
							break;
				default:	nodes[x][y].getVertical().setClicked(false);
							nodes[x][y].getHorizontal().setClicked(false);
				}
			}
		}
	}

	@Override
	public JPanel getView() {
		updateNodes();
		return gp;
	}

	@Override
	public void addPlayer(Player player) {
		names[players.size()] = player.getName();
		players.add(player);
	}

	@Override
	public synchronized void sendLine(Line line) {
		for(Player player : players) {
			player.sendLine(line);
		}
	}
}
