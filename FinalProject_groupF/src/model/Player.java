/**********************************************************************
* AUTHOR: Tina Taleb
* COURSE: CS 113 Data Structures & Algorithms
* SECTION: Mon/Wed 11 - 12:50 PM 
* LAST MODIFIED: 12/08/16 7:00PM
***********************************************************************
* TITLE:			MyComponentListener
* CLASS DESCRIPTION: Interface for listener for component.
***********************************************************************/

package model;

//Imports
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Point;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

public class Player {
	//Constants *********************************************************************************************
	public static final Color DEFAULT_COLOR = Color.RED;
	
	//Variables *********************************************************************************************
	private int x, y;
	private boolean isMovable = true;
	
	//Constructors *******************************************************************************************
	/** Player : default constructor
	 * @param n/a
	 * @return n/a
	 */
	public Player(){
		this.x = 0;
		this.y = 0;
	}
	
	/** Player : filled x,y coordinates constructor
	 * @param int x //starting x coordinate
	 * @param int y //starting y coordinate
	 * @return n/a
	 */
	public Player(int x,int y){
		this.x = x;
		this.y = y;
	}
	
	/** Player : filled point constructor (uses x,y coordinates in Point)
	 * @param Point point //
	 * @return n/a
	 */
	public Player(Point point){
		this.x = point.x;
		this.y = point.y;
	}
	
	//Mutators *********************************************************************************************
	/** setX : mutator for x coordinate instance variable
	 * @param int x
	 * @return void
	 */
	
	public void setX(int x){
		this.x = x;
	}
	
	/** setY : mutator for y coordinate instance variable
	 * @param int y
	 * @return void
	 */
	public void setY(int y){
		this.y = y;
	}
	
	
	//Accessors ********************************************************************************************
	/** getC : accessor for x coordinate instance variable
	 * @param int x
	 * @return void
	 */
	public int getX(){
		return this.x;
	}
	
	/** getY : accessor for y coordinate instance variable
	 * @param int y
	 * @return void
	 */
	public int getY(){
		return this.y;
	}
	
	/** getPosition : accessor for Point position using x,y instance variable
	 * @param n/a
	 * @return Point point
	 */
	public Point getPosition(){
		return new Point(x, y);
	}
	
	
	//Movement Methods **********************************************************************************************
	/** moveLeft : moves player left
	 * @param n/a
	 * @return void
	 */
	public void moveLeft(){
		x--;
	}
	
	/** moveRight : moves player right
	 * @param n/a
	 * @return void
	 */
	public void moveRight(){
		x++;
	}
	
	/** moveUp : moves player Up
	 * @param n/a
	 * @return void
	 */
	public void moveUp(){
		y--;
	}
	
	/** moveDown : moves player down
	 * @param n/a
	 * @return void
	 */
	public void moveDown(){
		y++;
	}
	
	/** setMovable : sets boolean determining whether player is able to move
	 * @param boolean isMovable
	 * @return void
	 */
	public void setMovable(boolean isMovable){
		this.isMovable = isMovable;
	}
	
	/** isMovable : return boolean determining whether player is able to move
	 * @param n/a
	 * @return boolean isMovable
	 */
	public boolean isMovable(){
		return this.isMovable;
	}
	
	
	
}