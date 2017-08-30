/**********************************************************************
* AUTHOR: Yushi Sato
* COURSE: CS 113 Data Structures & Algorithms
* SECTION: Mon/Wed 11 - 12:50 PM 
***********************************************************************
* TITLE:			Maze Component
* CLASS DESCRIPTION: Controls Maze model by implementing 
* 					 various methods for user. 
***********************************************************************/

package controller;

//Imports ****************************************************************************
//awt
import java.awt.Dimension;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
//swing
import javax.swing.JComponent;
import javax.swing.SwingUtilities;
//model
import model.Maze;
import model.Player;

public class MazeComponent extends JComponent implements KeyListener, Runnable{
	//Constants ***********************************************************************************************************
	private static final long serialVersionUID = 1L;
	public static final String SKIN_1_FILE = "PlayerGoomba.jpg";
	public static final String SKIN_2_FILE = "PlayerPacman.jpg";
	public static final String SKIN_3_FILE = "PlayerLink.jpg";
	public static final String DEFAULT_SKIN = SKIN_1_FILE;
	
	//Variables ***********************************************************************************************************
	//maze
	private Maze maze;
	//player class
	private Player player;
	//animation status
	private int animationStatus = 0;
	private int animationLastStatus = 0;
	private int animCnt = 0;					//	store animation age in its life cycle
	private int animCnt2 = 0;					//	store animation age in its life cycle
	public static final int ANIMATION_NULL = 0;
	public static final int ANIMATION_START = 11;
	public static final int ANIMATION_GOAL = 12;
	public static final int ANIMATION_PLAYER_MOVING = 13;
	public static final int ANIMATION_FIND_ME = 14;
	//Player Image
	private String skinFile = DEFAULT_SKIN; //defaults
	public Image skin;
	
	//Constructors ******************************************************************************************************
	/** MazeComponent : default constructor for MazeComponent 
	 * @param n/a
	 * @return n/a
	 */
	public MazeComponent() {
		maze = new Maze();
		this.setFocusable(true);
		this.requestFocus();
		this.init();
		
		this.player = new Player(maze.getStart());
		startMaze();
	}
	
	
	//Maze Animation ****************************************************************************************************
	/** startMaze : resets maze and starts animations for maze for start 
	 * @param n/a
	 * @return void
	 */
	public void startMaze() {
		resetMaze();
		animCnt=0;
		animCnt2=0;
		startAnimation(ANIMATION_START);
	}
	
	/** goalMaze : starts animations for maze for when goal is reached 
	 * @param n/a
	 * @return void
	 */
	public void goalMaze() {
		startAnimation(ANIMATION_GOAL);
	}
	
	/** findMe : starts animations for find me 
	 * @param n/a
	 * @return void
	 */
	public void findMe() {
		startAnimation(ANIMATION_FIND_ME);
		this.setFocusable(true);
		this.requestFocus();
	}
	
	
	//Maze Operations *************************************************************************************************
	/** run : runs maze dynamically 
	 * @param n/a
	 * @return void
	 */
	@Override
	public void run() {
		while(true) {
			SwingUtilities.invokeLater(new Runnable() {
				public void run() {
					repaint();
				}
			});
			try {	Thread.sleep(10);	}
			catch(Exception err) {}
		}
	}
	
	/** reset : resets player to start 
	 * @param n/a
	 * @return void
	 */
	public void reset() {
		this.player.setX(maze.getStart().x);
		this.player.setY(maze.getStart().y);
		this.setFocusable(true);
		this.requestFocus();
	}
	
	/** reset : resets maze
	 * @param n/a
	 * @return void
	 */
	public void resetMaze() {
		this.maze = new Maze();
		
		this.player = new Player(maze.getStart()); //create new player at the new startss
		//this.player.setColor(color); //set color to previous color for consistency
		maze.hideSolution();
		this.setFocusable(true);
		this.requestFocus();
	}
	
	/** showSolution : displays solution path for maze
	 * @param n/a
	 * @return void
	 */
	public void showSolution() {
		maze.showSolution();
		this.setFocusable(true);
		this.requestFocus();
	}

	
	//Accessors & Mutators ***********************************************************************************************
	/** setMaze : mutator for maze instance variable
	 * @param Maze mae
	 * @return void
	 */
	public void setMaze(Maze maze) {
		this.maze = maze;
	}
	
	/** getMaze : accessor for maze instance variable
	 * @param n/a
	 * @return Maze maze
	 */
	public Maze getMaze() {
		return this.maze;
	}
	
	/** getPlayer : accessor for player instance variable
	 * @param n/a
	 * @return Player player
	 */
	public Player getPlayer() {
		return this.player;
	}
	
	/** getPreferredSize : gets preferred size for maze depending on its size.
	 * @param n/a
	 * @return Dimension size 
	 */
	@Override
	public Dimension getPreferredSize() {
		return new Dimension(this.getMaze().getWidth()*Maze.X+20 , this.getMaze().getHeight()*Maze.Y+70);
	}
	
	//Graphics & Animation *************************************************************************************************
	/** paintComponent : standard paint component method for graphics, draws maze, player, goal/start, includes animations
	 * @param Graphics gr
	 * @return void
	 */
	public void paintComponent(Graphics gr) {
		gr.setColor(Color.WHITE);
		gr.fillRect(0, 0, maze.getWidth()*Maze.X, maze.getHeight()*Maze.Y); //fill maze with white
		maze.draw(gr); //draw maze 
		drawPlayer(gr, skinFile);//draw player
		if(maze.isPlayerAtGoal(player.getPosition())) { //if player reaches goal
			goalMaze();
		}
		if(isAnimationContinue()) {
			animate(gr);
			animCnt++;
			animCnt2 = animCnt%100000;
		}
	}
	
	/** drawCircle : draws circle of given radius at given coordinates
	 * @param Graphics gr
	 * @param int r //radius
	 * @param int x //coordinate
	 * @param int y //coordinate
	 */
	private void drawCircle(Graphics gr,int r,int x,int y) {
		if(r>=0) {
			int cx = x-r;
			int cy = y-r;
			gr.drawOval(cx, cy, r+r, r+r);
		}
	}
	
	
	/** animate : displays given animation 
	 * @param Graphics gr
	 * @return void
	 */
	private void animate(Graphics gr) {
		switch (animationStatus) {
			//Animation at start of game (to locate player)
			case ANIMATION_START:
				if(animCnt2 < 100) {
					gr.setColor(new Color(230,138,32));
					drawCircle(gr, (100 - animCnt2), player.getX()*Maze.X + Maze.X/2, player.getY()*Maze.Y + Maze.Y/2);
					drawCircle(gr, (105 - animCnt2), player.getX()*Maze.X + Maze.X/2, player.getY()*Maze.Y + Maze.Y/2);
				}else{
					startAnimation(ANIMATION_NULL);
				}
				
				break;
			//Animation once goal is reached
			case ANIMATION_GOAL:
				if(animCnt2 < 100) {
					gr.setColor(new Color(0,76,153));
					drawCircle(gr, animCnt2, player.getX()*Maze.X + Maze.X/2, player.getY()*Maze.Y + Maze.Y/2);
					drawCircle(gr, animCnt2 + 5, player.getX()*Maze.X + Maze.X/2, player.getY()*Maze.Y + Maze.Y/2);
				}else{
					startAnimation(ANIMATION_NULL);
					startMaze();
				}
				break;
			//Animation when find me button pressed (to find player)
			case ANIMATION_FIND_ME:
				if(animCnt2 < 100) {
					gr.setColor(new Color(230,138,32));
					drawCircle(gr, animCnt2, player.getX()*Maze.X + Maze.X/2, player.getY()*Maze.Y + Maze.Y/2);
					drawCircle(gr, animCnt2 + 5, player.getX()*Maze.X + Maze.X/2, player.getY()*Maze.Y + Maze.Y/2);
				}else{
					startAnimation(ANIMATION_NULL);
				}
				break;
			case ANIMATION_PLAYER_MOVING:
				break;
			default:
				break;
		}
	}
	
	/** startAnimation : start given animation
	 * @param int animation
	 * @return void
	 */
	private void startAnimation(int animation) {
		if(animationLastStatus != animation) {
			animationLastStatus = animation;
			animationStatus = animation;
			animCnt = 0;
			animCnt2 = 0;
		}
	}
	
	/** isAnimationContinue : returns boolean on the animation status (continues or not)
	 * @param n/a
	 * @return boolean isAnimationContinue
	 */
	private boolean isAnimationContinue() {
		return animationLastStatus == animationStatus;
	}
	
	//Key Methods ***************************************************************************************
	/** init : adds key listener and starts new thread 
	 * @param n/a
	 * @return void
	 */
	public void init() {
		this.addKeyListener(this);
		new Thread(this).start();
	}
	
	/** getKeyEvent : gets given key event 
	 * @param KeyEvent e
	 * @return void
	 */
	public void getKeyEvent(KeyEvent e) {
		switch (e.getKeyCode()) {
			//left arrow key or A
			case KeyEvent.VK_A : 
			case KeyEvent.VK_LEFT:
				if(maze.isLeftPath(player.getPosition())) {
					player.moveLeft();
				}
				break;
			//right arrow key or d
			case KeyEvent.VK_D : 
			case KeyEvent.VK_RIGHT:
				if(maze.isRightPath(player.getPosition())) {
					player.moveRight();
				}
				break;
			//up arrow key or w
			case KeyEvent.VK_W :
			case KeyEvent.VK_UP:
				if(maze.isUpPath(player.getPosition())) {
					player.moveUp();
				}
				break;
			//down arrow key or s
			case KeyEvent.VK_S :
			case KeyEvent.VK_DOWN:
				if(maze.isDownPath(player.getPosition())) {
					player.moveDown();
				}
				break;
			default:
				break;
		}
	}
	
	/** keyPressed : gets triggered key event e
	 * @param KeyEvent e
	 * @return void
	 */
	@Override
	public void keyPressed(KeyEvent e) {
		// TODO Auto-generated method stub
		getKeyEvent(e);
	}

	/** keyReleased : gets triggered release of key event e
	 * @param KeyEvent e
	 * @return void
	 */
	@Override
	public void keyReleased(KeyEvent e) {
		// TODO Auto-generated method stub
		
	}

	/** keyTyped : gets triggered typed key event
	 * @param KeyEvent e
	 * @return void
	 */
	@Override
	public void keyTyped(KeyEvent e) {
		// TODO Auto-generated method stub
	}

	//Player *****************************************************************************************
	//Player 
	/** draw : draws player 
	 * @param Graphics gr
	 * @return void
	 */
	public void drawPlayer(Graphics gr, String fileName){
		//obtain skin
		try {                
	          skin = ImageIO.read(new File(fileName));
	     } catch (IOException ex) {
	            ex.printStackTrace();
	     }
		 //draw image	
		 gr.drawImage(skin, player.getX()*Maze.X , player.getY()*Maze.Y , Maze.X , Maze.Y, this); // see javadoc for more info on the parameters 
	}
	
	/** setSkin : sets skin to image in file
	 * @param String fileName
	 * @return void
	 */
	public void setSkin(String fileName) {
		this.skinFile = fileName;
	}
	
}