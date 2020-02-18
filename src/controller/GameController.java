/*
 * Dots and Boxes
 * Submitted for the Degree of B.Sc. in Computer Science, 2010/2011
 * University of Strathclyde
 * Department of Computer and Information Sciences
 * @author Philip Rodgers
 */
package controller;

import gameStates.GameState;
import view.GameStateView;
import players.Player;

import java.awt.Dimension;
import java.awt.Font;
import java.awt.GraphicsEnvironment;
import java.awt.event.InputEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.File;
import java.lang.reflect.Constructor;
import java.net.URL;
import java.util.LinkedList;
import java.util.List;

import javax.swing.JFrame;

import data.Line;

import tools.Tools;
import view.BinaryGameStateView;
import view.ButtonPanel;
import view.GlassPane;
import view.GoButton;
import view.OptionPanel;
import view.SetupPanel;

public class GameController {

	private GameState gs;
	private GameStateView gsv;
	private Player[] players;
	private int[] scores;
	private int p;
	private GlassPane gp;
	private JFrame frame;
	private ButtonPanel[] buttons;
	private Thread gameThread;
	private SetupPanel setupPanel;
	private MouseListener ml;
	private boolean interrupted;
	
	/**
	 * The constructor creates the view and dynamically loads
	 * GameStates and Players.  Once the view is ready and visible,
	 * the user controls the game using the mouse.  Mouse listeners
	 * call the methods to play the game.
	 */
	public GameController() {
		// Create the frame
		frame = new JFrame("Dots and Boxes");
		frame.setMinimumSize(new Dimension(200,200));
		frame.setPreferredSize(new Dimension(700,700));
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		//Load the Players and GameStates
		List<Player> players = getPlayers();
		List<GameState> gameStates = getGameStates();

		//Build the Setup screen
		setupPanel = new SetupPanel(new SetupMouseListener(),players,gameStates);
		frame.getContentPane().add(setupPanel);
		
		//Build the pause menu
		ml = new OptionMouseListener();
		buttons = makeButtons(
				new String[]{"Continue","Restart","Setup","Quit"}, ml);
		OptionPanel op = new OptionPanel(buttons);
		gp = new GlassPane(op);
		frame.setGlassPane(gp);
		
		//Load the custon fonts
		String[] fonts = new String[]{"snake","bumpyroad","pencil","scratch"};
		try {
		     GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
		     for(String font : fonts) {
		    	 Font f = Font.createFont(Font.TRUETYPE_FONT, new File("fonts" + Tools.fileSeperator() + font + ".ttf"));
//		    	 System.out.println(f.getName());
		    	 ge.registerFont(f);
		     }
		} catch (Exception ioe){}
		
		//Display the view
		frame.pack();
		frame.setVisible(true);
	}
	
	/*
	 * This method creates the buttons for the pause menu
	 * and attaches the MouseListener to theme.
	 */
	private ButtonPanel[] makeButtons(String[] names, MouseListener ml) {
		buttons = new ButtonPanel[names.length];
		for(int i = 0 ; i < names.length ; i++) {
			buttons[i] = new ButtonPanel(names[i], ml);
		}
		return buttons;
	}

	/*
	 * Once a game has been set up, this is the method that
	 * plays the game.  Its responsibilities are get moves
	 * from the Players and add them to the GameState. This
	 * loops until there are no more moves left, or the game
	 * is interrupted.
	 */
	private void play() {
		p = 0;
		interrupted = false;
		buttons[0].scribbleOut(false);
		List<Line> lines = gs.getRemainingLines();
		while(lines.size() > 0 || interrupted) {
			Line line = players[p].makeMove(gs.clone());
//			try {
//				Thread.sleep(1000);
//			} catch (InterruptedException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
			if (line == null) return;
			int result = gs.addLine(line);
			switch (result) {
				case 0:		p = (p+1)%2; updateView(); break;
				case -1:	break;
				default:	scores[p] += result; updateView();
			}
		}
		buttons[0].scribbleOut(true);
	}
	
	/*
	 * This method deals with the administrative tasks of taking
	 * the users game options from the setup screen and initialising
	 * the required data structures to match those options.  Once
	 * the initialisations are done, it calls the start() method to
	 * setup the game Thread.
	 */
	private void go() {
		Player p1 = setupPanel.getPlayerOne();
		Player p2 = setupPanel.getPlayerTwo();
		if(p2 == p1) {
			p2 = loadPlayer(p2.getClass().getName());
		}
		scores = new int[]{0,0};
		p1.setPlayerNumber(1);
		p2.setPlayerNumber(2);
		players = new Player[]{p1,p2};
		gs = setupPanel.getGameState();
		gs.init(setupPanel.getSize());
		gsv = new BinaryGameStateView(gs);
		gsv.addPlayer(p1);
		gsv.addPlayer(p2);
		gsv.getView().addMouseListener(ml);
		frame.getContentPane().remove(setupPanel);
		frame.getContentPane().add(gsv.getView());
		frame.setPreferredSize(frame.getSize());
		frame.pack();
		start();
	}

	/*
	 * A convenient method for updating the view
	 */
	private void updateView() {
		gsv.getView().invalidate();
		gsv.getView().repaint();
	}
	
	/*
	 * A convenient method for showing or hiding the pause menu.
	 * The use of unsafe thread methods should be OK in this
	 * game, as it is only the game loop threads that are being
	 * paused.  The main control thread is never suspended.
	 */
	@SuppressWarnings("deprecation")
	private void showMenu(boolean show) {
		if (show) {
			gameThread.suspend();
		} else {
			gameThread.resume();
		}
		gp.setEnabled(show);
		gp.setVisible(show);
	}
	
	/*
	 * This method sets up a Thread to run the game logic in.  Using
	 * Threads enables the game to be paused and resumed.
	 */
	public void start() {
		gameThread = new Thread(new Runnable() {
			@Override
			public void run() {
				play();
			}
		});
		frame.getContentPane().remove(setupPanel);
		frame.getContentPane().add(gsv.getView());
		frame.setPreferredSize(frame.getSize());
		frame.pack();
		interrupted = false;
		gameThread.start();
	}
	
	/*
	 * This method builds up a List of Players by reading the contents
	 * of the directory where the Player class files will be.  The
	 * classes are then dynamically loaded using Java Reflection.
	 * This method enables Players to be added at run time.
	 */
	private List<Player> getPlayers() {
		List<Player> players = new LinkedList<Player>();
		URL url = Player.class.getResource("");
		File dir = new File(url.getFile().replaceAll("%20", " "));
		for(String file : dir.list()) {
			String classpath = "players." + file.substring(0,file.length()-6);
			System.out.println(classpath);
			Player player = null;
			if(!file.contains("$")) {  // A file name with '$' in it is usually a private inner class.
				try {
					player = loadPlayer(classpath);
				} catch (Throwable e) {}
			}
			if (player != null) players.add(player);
		}
		return players;
	}
	
	/*
	 * This method dynamically loads a single Player from
	 * a supplied Class name.
	 */
	private Player loadPlayer(String className) {
		Player player = null;
		try {
			Class<?> theClass = Class.forName(className);
			Constructor<?>[] cons = theClass.getConstructors();
			player = (Player) cons[0].newInstance();
		} catch (Throwable e) {}
		return player;
	}
	
	/*
	 * This method creates the List of GameStates available by
	 * dynamically loading then using Java Reflection.
	 */
	private List<GameState> getGameStates() {
		List<GameState> gss = new LinkedList<GameState>();
		URL url = GameState.class.getResource("");
		File dir = new File(url.getFile().replaceAll("%20", " "));
		for(String file : dir.list()) {
			try {
				Class<?> theClass = Class.forName("gameStates." + file.substring(0,file.length()-6));
				Constructor<?>[] cons = theClass.getConstructors();
				gs = (GameState) cons[0].newInstance();
			} catch (Throwable e) {}
			if (gs != null) gss.add(gs);
		}
		return gss;
	}
	
	/*
	 * This is a convenience method for reseting a game
	 */
	private void reset() {
		updateView();
		frame.invalidate();
		frame.repaint();
		p = 0;
		scores[0] = 0;
		scores[1] = 0;
		players[0].reset();
		players[1].reset();
	}

				  ///////////////////////////////
	/////////////// MouseListener definitions ///////////////
				///////////////////////////////
	
	/*
	 * This is the mouse listened that handles interaction
	 * with the setup screen.
	 */
	private class SetupMouseListener extends MouseAdapter {

		/*
		 * This method causes the 'GO!' option to be circled
		 * when the mouse hovers over it.  This simply feedback
		 * for the user, telling them that a click will cause
		 * the game to start.
		 * @see java.awt.event.MouseListener#mouseEntered(java.awt.event.MouseEvent)
		 */
        public void mouseEntered(MouseEvent e) {
        	if(e.getSource() instanceof GoButton) {
	        	GoButton source = (GoButton) e.getSource();
	        	source.hover(true);
	        	source.invalidate();
	        	source.repaint();
        	}
        }
        
        /*
         * This method makes the circle of the 'GO!' option
         * disappear when the mouse is no longer hovering over it.
         * @see java.awt.event.MouseListener#mouseExited(java.awt.event.MouseEvent)
         */
		public void mouseExited(MouseEvent e) {
			if(e.getSource() instanceof GoButton) {
	        	GoButton source = (GoButton) e.getSource();
	        	source.hover(false);
	        	source.invalidate();
	        	source.repaint();
        	}
		}
		
		/*
		 * This method will start the game if the mouse button is clicked
		 * on the 'GO!' option.
		 * @see java.awt.event.MouseListener#mousePressed(java.awt.event.MouseEvent)
		 */
        public void mousePressed(MouseEvent e) {
        	if(e.getSource() instanceof GoButton) {
	        	go();
        	}
        }
    }
	
	/*
	 * This MouseListener deals with the pause menu
	 */
	private class OptionMouseListener extends MouseAdapter {

		/*
		 * This method highlights the option that the mouse
		 * is hovering over.
		 * @see java.awt.event.MouseListener#mouseEntered(java.awt.event.MouseEvent)
		 */
        public void mouseEntered(MouseEvent e) {
        	if(e.getSource() instanceof ButtonPanel) {
	        	ButtonPanel source = (ButtonPanel) e.getSource();
	        	source.drawCircle(true);
	        	source.invalidate();
	        	source.repaint();
        	}
        }
        
        /*
         * This method removes the highlight when the mouse
         * is no longer hovering over an option.
         * @see java.awt.event.MouseListener#mouseExited(java.awt.event.MouseEvent)
         */
		public void mouseExited(MouseEvent e) {
			if(e.getSource() instanceof ButtonPanel) {
	        	ButtonPanel source = (ButtonPanel) e.getSource();
	        	source.drawCircle(false);
	        	source.invalidate();
	        	source.repaint();
        	}
		}
		
		/*
		 * This method handles in-game (not setup screen) mouse clicks
		 * @see java.awt.event.MouseListener#mousePressed(java.awt.event.MouseEvent)
		 */
		public void mousePressed(MouseEvent e) {
        	
        	if((e.getModifiers() & InputEvent.BUTTON3_MASK) == InputEvent.BUTTON3_MASK) {
        		showMenu(true);
        		return;
        	}
        	if(e.getSource() instanceof ButtonPanel) {
	        	if(((ButtonPanel)e.getSource()).getName().equals("Restart")) {
	        		interrupted = true;
	        		gs.reset();
	        		showMenu(false);
	        		reset();
	        		gameThread.interrupt();
	        		start();
	        		return;
	        	}
	        	if(((ButtonPanel)e.getSource()).getName().equals("Quit")) {
	        		System.exit(0);
	        	}
	        	if(((ButtonPanel)e.getSource()).getName().equals("Continue")) {
	        		if(!((ButtonPanel)e.getSource()).isScribbledOut()) {
	        			showMenu(false);
	        		}
	        		return;
	        	}
	        	if(((ButtonPanel)e.getSource()).getName().equals("Setup")) {
	        		interrupted = true;
	        		gameThread.interrupt();
	        		showMenu(false);
	        		gs.reset();
	        		reset();
	        		frame.getContentPane().remove(gsv.getView());
	        		frame.getContentPane().add(setupPanel);
	        		frame.setPreferredSize(frame.getSize());
	        		frame.pack();
	        	}
        	}
        }
    }
}
