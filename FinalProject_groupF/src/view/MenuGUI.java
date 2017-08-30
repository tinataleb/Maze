/**********************************************************************
* AUTHOR: Curtis Spears
* COURSE: CS 113 Data Structures & Algorithms
* SECTION: Mon/Wed 11 - 12:50 PM 
* LAST MODIFIED: 12/08/2016
***********************************************************************
* TITLE:				MenuGUI
* PROGRAM DESCRIPTION: JFrame for GUI. Starts menu and adds GUI for maze (Jpanel) 
* 					   to center. Also contains useful methods for user - options menu. 
* 					   Allows user to change skin of their character and for new 
* 					   character to be imported using a .jpg (resizes automatically).
***********************************************************************/

package view;

//Imports ************************************************************************************\
//awt
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.Image;
//swing
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
//io
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;
//created packages
import controller.MazeComponent;


//NOTE: CONTAINS ImagePanel Inner Class
public class MenuGUI extends JFrame {

	//Constants ******************************************************************************************
	private static final long serialVersionUID = 1L; //default serial id
	private static final String BACKGROUND_IMG_NAME = "Menu.jpg";
	private static final int SKIN_ICON_WIDTH = 50;
	private static final int SKIN_ICON_HEIGHT = 50;
	
	//Component/Instance ********************************************************************************* 	
	private GUI maze = new GUI();
	
	//Constructors ***************************************************************************************
	/** MenuGUI : MenuGUI Default constructor
	 * @param n/a
	 * @return n/a
	 */
	public MenuGUI() {
		
		//JFrame Setup
		this.setBounds(maze.getBounds());
		this.setTitle("Maze Final");
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		//ImagePanel
		this.add(new ImagePanel(BACKGROUND_IMG_NAME));
		
		//Buttons & Panel
		//panel
		JPanel buttonPanel = new JPanel();
		
		//new game button
		JButton newGameButton = new JButton("New Game");
		newGameButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				newGame();
                revalidate();
                repaint();
			}
		});
		buttonPanel.add(newGameButton);
		
		//options button
		JButton optionsButton = new JButton("  Options  ");
		optionsButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				options();
			}
		});
		buttonPanel.add(optionsButton);
		
		//add panel to JFrame
		this.add(buttonPanel, BorderLayout.SOUTH);
		
		
	}
	
	//Button Actions ***********************************************************************
	/** newGame : starts new game by displaying GUI for maze
	 * @param n/a
	 * @return void
	 */
	public void newGame() {
		JPanel panel = new JPanel();
		panel.add(maze);
		this.getContentPane().removeAll();
		this.setBounds(0,0,maze.getWidth(), maze.getHeight() + 100);
		this.add(panel);
		this.pack();
		maze.reset();
	}
	
	/** options : displays options menu
	 * @param n/a
	 * @return void
	 */
	public void options() {
		JFrame options = new MenuGUI.OptionsGUI(maze.getComponent());
		options.setVisible(true);
	}
	
	/****************************************************************************************************************
	 * OptionsGUI Panel Inner Class **************************************************************************************
	 ****************************************************************************************************************/
	 public static class OptionsGUI extends JFrame {
		 
		//Serializable Constant
		private static final long SERIAL_VERSION_UID = 1L;
		//Instances (used to access current maze being used for dynamic changes)
		 MazeComponent maze;
		 public OptionsGUI(MazeComponent maze) {
			//this Jframe setup
			this.maze = maze;
			this.setLayout(new FlowLayout());
			this.setBounds(0,0,300, 300);
			this.getContentPane().setBackground(Color.WHITE);
			this.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
			
			//JLabel
			this.add(new JLabel("Player Skins:  "));
			
			//Buttons ***********************************
			//Skin 1 Button
			//image 
			Image icon1Image = null;
			try{
				icon1Image = ImageIO.read(new File(MazeComponent.SKIN_1_FILE));
			} catch (IOException ex) {
				ex.printStackTrace();
			}
			icon1Image = icon1Image.getScaledInstance(SKIN_ICON_WIDTH, SKIN_ICON_HEIGHT, Image.SCALE_SMOOTH);
			//icon
			ImageIcon icon1 = new ImageIcon(icon1Image);
			//button
			JButton skin1Button = new JButton(icon1);
			skin1Button.setPreferredSize(new Dimension(SKIN_ICON_WIDTH, SKIN_ICON_HEIGHT));
			//add listener 
			skin1Button.addActionListener(new ActionListener() {
				
				@Override
				public void actionPerformed(ActionEvent e) {
					optionsSkin(MazeComponent.SKIN_1_FILE);
				}

			});
			this.add(skin1Button);
			//Skin 2 Button
			//image 
			Image icon2Image = null;
			try{
				icon2Image = ImageIO.read(new File(MazeComponent.SKIN_2_FILE));
			} catch (IOException ex) {
				ex.printStackTrace();
			}
			icon2Image = icon2Image.getScaledInstance(SKIN_ICON_WIDTH, SKIN_ICON_HEIGHT, Image.SCALE_SMOOTH);
			//icon
			ImageIcon icon2 = new ImageIcon(icon2Image);
			//button
			JButton skin2Button = new JButton(icon2);
			skin2Button.setPreferredSize(new Dimension(SKIN_ICON_WIDTH, SKIN_ICON_HEIGHT));
			//add listener 
			skin2Button.addActionListener(new ActionListener() {
				
				@Override
				public void actionPerformed(ActionEvent e) {
					optionsSkin(MazeComponent.SKIN_2_FILE);
				}

			});
			this.add(skin2Button);
			//Skin 3 Button
			//image 
			Image icon3Image = null;
			try{
				icon3Image = ImageIO.read(new File(MazeComponent.SKIN_3_FILE));
			} catch (IOException ex) {
				ex.printStackTrace();
			}
			icon3Image = icon3Image.getScaledInstance(SKIN_ICON_WIDTH, SKIN_ICON_HEIGHT, Image.SCALE_SMOOTH);
			//icon
			ImageIcon icon3 = new ImageIcon(icon3Image);
			//button
			JButton skin3Button = new JButton(icon3);
			skin3Button.setPreferredSize(new Dimension(SKIN_ICON_WIDTH, SKIN_ICON_HEIGHT));
			//add listener 
			skin3Button.addActionListener(new ActionListener() {
				
				@Override
				public void actionPerformed(ActionEvent e) {
					optionsSkin(MazeComponent.SKIN_3_FILE);
				}

			});
			this.add(skin3Button);
			this.setVisible(true);
		}
					
	 	//options buttons
		 /** optionsSkin : changes skin and resets player position
		  * @param fileName
		  * @return void
		  */
		private void optionsSkin(String fileName) {
			System.out.println("Changed Player (Options)");
			maze.setSkin(fileName);
			maze.setFocusable(true);
			maze.requestFocus();
		}
	 }
	
	
	/****************************************************************************************************************
	 * Image Panel Inner Class **************************************************************************************
	 ****************************************************************************************************************/
	private class ImagePanel extends JPanel{

		//Constants *******************************************
		private static final long serialVersionUID = 1L; 
		//Image/Instance***************************************
	    private Image image;
	
	    //Constructors ****************************************
	    /** ImagePanel : default constructor - puts image in JPanel
	     * @param n/a
	     * @return n/a
	     */
	    public ImagePanel(String fileName) {
	       try {                
	          image = ImageIO.read(new File(fileName)); //read in
	       } catch (IOException ex) {
	            ex.printStackTrace();
	       }
	    }
	
	    //Graphics *******************************************
	    /** paintComponent : overrides default paintComponent method, scales given image
	     * @param Graphics g
	     * @return void
	     */
	    @Override
	    protected void paintComponent(Graphics g) {
	        super.paintComponent(g);
	        image = image.getScaledInstance(getWidth(), getHeight(),Image.SCALE_SMOOTH);
	        g.drawImage(image, 0, 0, this); // see javadoc for more info on the parameters            
	    }
	
	}
}
