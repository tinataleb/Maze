/**********************************************************************
* AUTHOR: Tina Taleb
* COURSE: CS 113 Data Structures & Algorithms
* SECTION: Mon/Wed 11 - 12:50 PM 
* LAST MODIFIED: 12/08/16 7:00PM
***********************************************************************
* TITLE:			GUI
* CLASS DESCRIPTION: GUI for maze class - JPanel. Contains useful
* 					 buttons for user. 
***********************************************************************/

package view;

//Imports  *****************************************************************************************************
//awt
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
//swing
import javax.swing.JButton;
import javax.swing.JPanel;
//created packages
import controller.MazeComponent;
import model.Maze;

public class GUI extends JPanel{
	
	//Variables ************************************************************************************************
	//private static final long serialVersionUID = 1L; //not currently used (not serializable)
	private MazeComponent component = new MazeComponent();
	//Swing Variables
	private JButton buttonNew, buttonReset, buttonSolve, buttonFindMe, buttonOptions;
	private JPanel toolPanel;
	
	//Constructors *********************************************************************************************
	/** GUI : default constructor 
	 * @param n/a
	 * @return n/a
	 */
	public GUI(){
		
		//JFrame Setup
		this.setBounds(0, 0,component.getMaze().getWidth()*Maze.X+20 , component.getMaze().getHeight()*Maze.Y+70);
		this.setLayout(new BorderLayout());
		//Set size of component for maze
		component.setPreferredSize(new Dimension(800,480));
		//Create new panel for buttons
		toolPanel = new JPanel();
	
		//New Game Button ********************************
		buttonNew = new JButton("New Game");
		buttonNew.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				newMaze();
			}
		});
		
		//FIND ME BUTTON *********************************
		buttonFindMe = new JButton("Find Me!");
		buttonFindMe.addActionListener(new ActionListener(){
			
			@Override
			public void actionPerformed(ActionEvent e) {
				findMe();
			}
		});
		
		//RESET BUTTON ***********************************
		buttonReset = new JButton("Reset");
		buttonReset.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				reset();
			}
		});
		
		//SOLVE BUTTON ***********************************
		buttonSolve = new JButton("Solve");
		buttonSolve.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				solve();
			}
		});
		
		//OPTIONS BUTTON *********************************
		buttonOptions = new JButton("Options");
		buttonOptions.addActionListener( new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				options();
			}
		});
		
		//Add Buttons to panel
		toolPanel.add(buttonNew);
		toolPanel.add(buttonReset);
		toolPanel.add(buttonSolve);
		toolPanel.add(buttonFindMe);
		toolPanel.add(buttonOptions);
		
		//add panel and component to JFrame
		this.add(component,BorderLayout.CENTER);
		this.add(toolPanel,BorderLayout.SOUTH);
	}
	
	//Accessors ****************************************************************************************************
	public MazeComponent getComponent() {
		return this.component;
	}
	
	//Button Actions (see MazeComponent for further implementations) ************************************************
	/** newMaze : creates new maze
	 * @param n/a
	 * @return void
	 */
	public void newMaze(){
		component.startMaze();
		buttonSolve.setEnabled(true);
		buttonReset.setEnabled(true);
	}
	
	/** reset : resets player to start
	 * @param n/a
	 * @return void
	 */
	public void reset(){
		System.out.println("reset");
		component.reset();
	}
	
	/** solve : solves maze, shows path to finish
	 * @param n/a
	 * @return void
	 */
	public void solve(){
		component.getMaze().showSolution();
		buttonSolve.setEnabled(false);
		buttonReset.setEnabled(false);
	}
	
	/** solve : displays beacon around player to show position
	 * @param n/a
	 * @return void
	 */
	public void findMe()
	{
		System.out.println("Pushed find me");
		component.findMe();
	}
	
	/** options : displays options menu
	 * @param n/a
	 * @return void
	 */
	public void options() {
		MenuGUI.OptionsGUI options = new MenuGUI.OptionsGUI(component); 
		options.setVisible(true);
	}
	
	//skin helper
	/** setSkin : sets the player's skin to the image
	 * @param String imageFile
	 * @return void
	 */
	public void setSkin(String imageFile) {
		component.setSkin(imageFile);
	}
	
	


}
