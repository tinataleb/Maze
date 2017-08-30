/**********************************************************************
* AUTHOR: Yushi Sato
* COURSE: CS 113 Data Structures & Algorithms
* SECTION: Mon/Wed 11 - 12:50 PM 
* LAST MODIFIED: 12/08/16 7:00PM
***********************************************************************
* TITLE:			Maze
* CLASS DESCRIPTION: Model of maze, uses graph for implementation. Includes 
* 					 useful graph methods to implement an auto-generating 
* 					 maze. 
***********************************************************************/

package model;

//Imports *************************************************************************************************
//awt
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Point;
//util
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Map.Entry;
import java.util.Queue;
import java.util.Random;

public class Maze{
	
	//CONSTANTS *******************************************************************************************
	//Block Sizes in pixels
	public static final int X=16;
	public static final int Y=16;
	//Frequency of ONEWAY
	public static final double RATE_OF_ONEWAY = 0.32;
	//Path Constants
	public static final int PATH = 0;
	public static final int SHORTEST_PATH = -10;
	public static final int WAY_RIGHT = 1;
	public static final int WAY_UP = 3;
	public static final int WAY_DOWN = 4;
	public static final int WAY_LEFT = 2;
	//Block Identifiers
	public static final int WALL = 11;
	public static final int START = 8;
	public static final int GOAL = 9;
	//Number of blocks in graph (Has to be odd number, because of the wall generation)
	public static final int DEFAULT_WIDTH = 37;
	public static final int DEFAULT_HEIGHT = 27;
	public static final int DEFAULT_WIDTH2 = 17;
	public static final int DEFAULT_HEIGHT2 = 7;
	//Player Positioning (used to identify surrounding blocks)
	//add from current graph position to identify 
	private static final int[] X_MARKER = {-1,0,1,1,1,0,-1,-1}; 
	private static final int[] Y_MARKER = {-1,-1,-1,0,1,1,1,0};
	
	//Variables *******************************************************************************************
	//Used for multiple random graph generations
	public Random random = new Random();
	//Block Identifiers
	private Point start,goal;
	//Map Data 
	private int width, height;
	private int[][] map;	
	//boolean whether to show solution or not 
	private boolean isSolutionShown = false;
		
	//Constructors *******************************************************************************************************
	/** Maze : default constructor for maze class
	 * @param n/a
	 * @return n/a
	 */
	public Maze(){
		//set dimensions of maze
		this.width = DEFAULT_WIDTH;
		this.height = DEFAULT_HEIGHT;
		int[][] temp = new int[width][height];
		
		//map edge wall generation
		for(int i=0;i<width;i++){
			for(int j=0;j<height;j++){
				if(i==0 || i==width-1){
					temp[i][j] = WALL;
				}else if(j==0 || j==height-1){
					temp[i][j] = WALL;
				}else{
					temp[i][j] = PATH;
				}
			}
		}
		
		//wall generation
		for(int i=1;i<width-1;i++){
			for(int j=1;j<height-1;j++){
				if(i%2 == 0 && j%2 == 0){
					temp[i][j] = WALL;
					if(temp[i][j] == WALL){
						double r = random.nextDouble();
						if(r < 0.25){
							temp[i-1][j] = WALL;
						}else if(r < 0.5){
							temp[i][j-1] = WALL;
						}else if(r < 0.75){
							temp[i+1][j] = WALL;
						}else{
							temp[i][j+1] = WALL;
						}
					}
				}
				
			}
		}
		
		//ensure walls and path are appropriately displayed, with border of wall 
		this.width = width*2-width/2;
		this.height = height*2-height/2;
		this.map = new int[width][height];
		
		//path generation
		for(int i=0;i<temp.length;i++){
			for(int j=0;j<temp[i].length;j++){
				int typePath = temp[i][j];
				if(i%2 == 0 && j%2 == 0){
					map[i*2-i/2][j*2-j/2] =  typePath;
					map[i*2-i/2 + 1][j*2-j/2] =  typePath;
					map[i*2-i/2][j*2-j/2 + 1] =  typePath;
					map[i*2-i/2 + 1][j*2-j/2 + 1] =  typePath;
				}else{
					if(i%2 == 0){
						map[i*2-i/2][j*2-j/2] =  typePath;
						map[i*2-i/2 + 1][j*2-j/2] = typePath;
					}else if(j%2 == 0){
						map[i*2-i/2][j*2-j/2] =  typePath;
						map[i*2-i/2][j*2-j/2 + 1] =  typePath;
					}else{
						map[i*2-i/2][j*2-j/2] =  typePath;
					}
				}
			}
		}
		
		//add start and goal to map
		this.setStartAndGoal();
		
		//one way generation
		for(int i=1;i<width-1;i++){
			for(int j=1;j<height-1;j++){
				if(i%2 == 0 && j%2 == 0){
					
					if(map[i-1][j]==WALL
							&& map[i+1][j]==WALL
							&& map[i][j+1]==PATH
							&& map[i][j-1]==PATH)
					{
						double r = random.nextDouble();
						if(r<RATE_OF_ONEWAY*0.5){
							map[i][j] = WAY_UP;
						}else if(r<RATE_OF_ONEWAY){
							map[i][j] = WAY_DOWN;
						}
					}else if(map[i-1][j]==PATH
							&& map[i+1][j]==PATH
							&& map[i][j+1]==WALL
							&& map[i][j-1]==WALL)
					{
						double r = random.nextDouble();
						if(r<RATE_OF_ONEWAY*0.5){
							map[i][j] = WAY_RIGHT;
						}else if(r<RATE_OF_ONEWAY){
							map[i][j] = WAY_LEFT;
						}
					}
					
				}
				
			}
		}
		
		//one way refinement
		HashTableChain<Point, Boolean> visited = new HashTableChain<>();
		for(int i=1;i<width-1;i++){
			for(int j=1;j<height-1;j++){
				if(!visited.containsKey(new Point(i,j))){
					if(Math.abs(map[i][j]) == WAY_LEFT || Math.abs(map[i][j]) == WAY_RIGHT){
						visited.put(new Point(i, j), true);
						horizontalClone(i, j, visited, map[i][j]);
					}else if(Math.abs(map[i][j]) == WAY_UP || Math.abs(map[i][j]) == WAY_DOWN){
						visited.put(new Point(i, j), true);
						verticalClone(i, j, visited, map[i][j]);
					}
				}
			}
		}
	}
	
	
	//Start & Goal Methods *********************************************************************************************
	/** resetStartAndGoal : resets start and goal, erases current, calculates new 
	 * @param n/a
	 * @return void
	 */
	public void resetStartAndGoal(){
		//traverse map to find start and goal
		for(int i=0;i<width;i++){
			for(int j=0;j<height;j++){
				if(map[i][j] == START || map[i][j] == GOAL){
					map[i][j] = PATH; //when found erase current
				}
			}
		}
		//recalculate and reset start and goal 
		setStartAndGoal();
	}
	
	/** setStartAndGoal : sets and finds appropriate start and goal for mae
	 * @param n/a
	 * @return void
	 */
	private void setStartAndGoal(){
		HashTableChain<String,Point> allSet = new HashTableChain<>();
		
		ArrayList<HashTableChain<String, Point>> list = new ArrayList<>();
		
//		keep track of path
		int lx0 = 1,ly0 = 1;
		
		for(int i=1;i < width-1;i++){
			for(int j=1;j < height-1;j++){
				if(map[i][j] == PATH){
					lx0 = i;
					ly0 = j;
					allSet.put("x"+i+"y"+j,new Point(i, j));
				}
			}
		}
		
		
		HashTableChain<String, Point> set = new HashTableChain<>();
		//find first 0:path point and region that includes the point.
		//	and store region in set.
		if(map[lx0][ly0] == PATH){
			getSameRegion(lx0, ly0, allSet,set);
			list.add(set);
		}
		
		int lastSize = 0;
		//	loop until all points are marked and modified
		while(allSet.size() != lastSize){
			
			//System.out.println(allSet.size());
			lastSize = allSet.size();
			for(String key : allSet.keySet()){
				set = new HashTableChain<>();
				getSameRegion(allSet.get(key).x,allSet.get(key).y , allSet, set);
				list.add(set);
				break;	//	iterate only one element
			}
			
		}
		
		//	leftovers are walls
		for(Entry<String, Point> entry : allSet.entrySet()){
			map[entry.getValue().x][entry.getValue().y] = WALL;
		}
		
		
//		get the largest region
		int maxSize = 0;
		int index = 0;
		int maxIndex = 0;
		for(HashTableChain<String, Point> set2 : list){
			
			//	smaller regions 
			if(set2.size() < 10){
				for(Entry<String, Point> entry : set2.entrySet()){
					if(entry != null) {
						map[entry.getValue().x][entry.getValue().y] = WALL;//100 + index;
					}
				}
			}else{
				if(set2.size() > maxSize){
					maxIndex = index;
					maxSize = set2.size();
				}
			}
			index++;
		}
		
		
		//	set start and goal
		Point start,goal;
		index = 0;
		
		boolean type = random.nextBoolean();
		start = new Point(0, 0);
		goal = new Point(width-1, height-1);
		double min_distance_s = Double.POSITIVE_INFINITY, min_distance_g = Double.POSITIVE_INFINITY;
		for(Entry<String, Point> entry : list.get(maxIndex).entrySet()){
			
			if(type){
				double distance_s = distance(new Point(0, 0), entry.getValue());
				double distance_g = distance(new Point(width-1, height-1),entry.getValue());
				if(distance_s < min_distance_s){
					min_distance_s = distance_s;
					start = entry.getValue();
				}else{
					if(distance_g < min_distance_g){
						min_distance_g = distance_g;
						goal = entry.getValue();
					}
				}
			}else{
				double distance_s = distance(new Point(0, height-1), entry.getValue());
				double distance_g = distance(new Point(width-1, 0),entry.getValue());
				if(distance_s < min_distance_s){
					min_distance_s = distance_s;
					start = entry.getValue();
				}else{
					if(distance_g < min_distance_g){
						min_distance_g = distance_g;
						goal = entry.getValue();
					}
				}
			}
		}
		
		//set start and goal identifiers in map
		map[start.x][start.y] = START;
		map[goal.x][goal.y] = GOAL;
		
		//set start and goal instances
		this.start = new Point(start.x, start.y);
		this.goal = new Point(goal.x, goal.y);
		
		this.findPath(); //find path for given start to goal
	}
	
	
	/** getAdjacent : get adjacent blocks from specified block
	 * @param Point point //coordinate of block
	 * @return ArrayList<Point> adjacent //adjacent blocks array
	 */
	private ArrayList<Point> getAdjacent(Point point){
		ArrayList<Point> arrayList = new ArrayList<>();
		for(int m=1;m<8;m += 2){
			switch (m) {
			case 1:		//	up
				if(map[point.x][point.y-1] < 10 && Math.abs(map[point.x][point.y-1]) != WAY_DOWN){
					arrayList.add(new Point(point.x, point.y-1));
				}
				break;
			case 3:		//	right
				if(map[point.x+1][point.y] < 10 && Math.abs(map[point.x+1][point.y-1]) != WAY_LEFT){
					arrayList.add(new Point(point.x+1, point.y));
				}
				break;
			case 5:		//	down
				if(map[point.x][point.y+1] < 10 && Math.abs(map[point.x][point.y+1]) != WAY_UP){
					arrayList.add(new Point(point.x, point.y+1));
				}
				break;
			case 7:		//	left
				if(map[point.x-1][point.y] < 10 && Math.abs(map[point.x-1][point.y-1]) != WAY_RIGHT){
					arrayList.add(new Point(point.x-1, point.y));
				}
				break;
			default:
				break;
			}
		}
		return arrayList;
	}
	
	//Searching Methods **************************************************************************************************
	/** breadthFirstSearch : traverses maze using breadth first search (wrapper)
	 * @param n/a
	 * @return void
	 */
	public void breadthFirstSearch(){
		Queue<Point> queue = new LinkedList<>();
	}
	
	/** breadthFirstSearch : traverses maze using breadth first search
	 * @param Point node
	 * @param HashSet<Point> unvisited
	 * @param HashSet<Point> visited
	 * @param LinkedList<Point> queue
	 */
	private void breadthFirstSearch(Point node,HashSet<Point> unvisited,HashSet<Point> visited,LinkedList<Point> queue){
		if(visited.contains(node) || queue.contains(node)){
			visited.add(node);
			return;
		}else{
			
		}
	}
		
	//Pathfinding ******************************************************************************************************
	/** findPath : finds path from start to goal using Dijkstra's Algorithm, also
	 * 			   sets one way paths during path finding 
	 * @param n/a
	 * @return void
	 */
	private void findPath(){
		HashSet<Point> S,VmS;
		
		//set of all path already visited
		S = new HashSet<>();
		
		//set of all path not yet visited.
		//V-S(V minus S)
		VmS = new HashSet<>();
		
		//	register path block to V
		for(int i=0;i<width;i++){
			for(int j = 0;j<height;j++){
				//	if it is path-able block,
				if(map[i][j] < 10){
					Point point = new Point(i, j);
					VmS.add(point);
				}
			}
		}
		
		S.add(start);
		VmS.remove(start);
		
		//Hash Maps to hold Dijkstra alg data
		HashTableChain<Point, Integer> d = new HashTableChain<>();	//	distance array with size of V
		HashTableChain<Point, Point> p = new HashTableChain<>();		//	predecessor array with size of V	
	
		for(Point point : VmS){
			d.put(point, Integer.MAX_VALUE/2);		//	set each d = MAX_VALUE
			p.put(point, start);					//	set predecessor = start
		}
		d.put(start, 0);
		p.put(start, new Point(-1, -1));
		
		//	u=start
		Point u = start;
		//	adjacent blocks from start are set as distance 1, predecessor start.
		for(Point adjacent : getAdjacent(start)){
			d.put(adjacent, 1);
			p.put(adjacent, start);
		}

		while(VmS.size() > 0){
			u = getSmallestDVertex(d, VmS);
			if(u != null){
				VmS.remove(u);
				S.add(u);
				if(getAdjacent(u).size()==0){
					System.out.println("No Adjacent");
				}
				for(Point v : getAdjacent(u)){
					int uTov = d.get(u) + 1;
					//System.out.println(""+uTov);
					//System.out.println(v.toString());
					//System.out.println(d.get(v));
					if(uTov < d.get(v)){
						d.put(v,uTov);
						p.put(v, u);
					}
				}
			}else{
				//System.out.println(VmS.size());
			}
			
		}
		
		Point prePoint = new Point(goal);
		Point last = goal;
		while(!prePoint.equals(start)){
			prePoint = p.get(prePoint);
			
			int dx = last.x - prePoint.x;
			int dy = last.y - prePoint.y;
			
//			if point is straight street
			//Randomly generate one way roads while solving algorithm
			if( (map[prePoint.x-1][prePoint.y]==WALL
					&& map[prePoint.x+1][prePoint.y]==WALL
					&& map[prePoint.x][prePoint.y+1] < 10
					&& map[prePoint.x][prePoint.y-1] < 10 ) ||
				(map[prePoint.x-1][prePoint.y] < 10
					&& map[prePoint.x+1][prePoint.y] < 10
					&& map[prePoint.x][prePoint.y+1]==WALL
					&& map[prePoint.x][prePoint.y-1]==WALL) )
			{
				if(random.nextDouble() < RATE_OF_ONEWAY*0.60){
					if(dx>0 && dy==0){
						map[prePoint.x][prePoint.y] = -WAY_RIGHT;
					}else if(dx<0 && dy==0){
						map[prePoint.x][prePoint.y] = -WAY_LEFT;
					}else if(dy>0 && dx==0){
						map[prePoint.x][prePoint.y] = -WAY_DOWN;
					}else if(dy<0 && dx==0){
						map[prePoint.x][prePoint.y] = -WAY_UP;
					}
				}else{
					map[prePoint.x][prePoint.y] = SHORTEST_PATH;
				}
			}else{
				map[prePoint.x][prePoint.y] = SHORTEST_PATH;
			}
			
			//Set goal
			last = new Point(prePoint);
		}
	}

	/** solve : wrapper method for find path, finds path from start to goal
	 * @param n/a
	 * @return void
	 */
	public void solve(){
		findPath();
	}

	
	//Accessors *********************************************************************************************************
	/** getSmallestDAdjacent : get adjacent with smallest distance
	 * @param adjacentList
	 * @param d
	 * @return
	 */
	private Point getSmallestDAdjacent(ArrayList<Point> adjacentList,HashTableChain<Point, Integer> d){
		int smallest = Integer.MAX_VALUE;
		Point smallestAdjacent = null;
		for(Point adjacent : adjacentList){
			int val = d.get(adjacent);
			if(val <= smallest){
				smallestAdjacent = adjacent;
				smallest = val;
			}
		}
		return smallestAdjacent;
	}
	
	/** getSmallestDVertex : gets smallest distance vertex from V-S
	 * @param HashTableChain<Point, Integer> d 
	 * @param HashSet<Point> VmS //v-s
	 * @return
	 */
	private Point getSmallestDVertex(HashTableChain<Point, Integer> d,HashSet<Point> VmS){
		int smallest = Integer.MAX_VALUE;
		Point smallestVetex = null;
		for(Point vertex : VmS){
			int val = d.get(vertex);
			if(val < smallest){
				smallestVetex = new Point(vertex);
				smallest = val;
			}
		}
		if(smallestVetex == null){
			System.out.println("no smallest");
		}
		return smallestVetex;
	}
	
	
	/** getStart : accessor for start Point instance containing x,y coordinates of start
	 * @param n/a
	 * @return Point start
	 */
	public Point getStart(){
		return this.start;
	}
	
	/** getGoal : accessor for goal Point instance containing x,y coordinates of end/goal
	 * @param n/a
	 * @return Point goal
	 * @return
	 */
	public Point getGoal(){
		return this.goal;
	}
	
	/** getWidth : accessor for width instance for maze
	 * @param n/a
	 * @return int width
	 */
	public int getWidth(){
		return this.width;
	}
	
	/** getWidth : accessor for height instance for maze
	 * @param n/a
	 * @return int height
	 */
	public int getHeight(){
		return this.height;
	}
	
	/** getSameRegion : 
	 * @param int x
	 * @param int y
	 * @param HashTableChain<String,Point> allSet
	 * @param HashTableChain<String, Point>set
	 * @return int sameRegion
	 */
	private int getSameRegion(int x,int y,HashTableChain<String,Point> allSet,HashTableChain<String, Point> set){
		int s = 0;
		for(int n=0;n<8;n++){
			int i = x+X_MARKER[n];
			int j = y+Y_MARKER[n];
			
			if(map[i][j] == PATH){
				if( allSet.remove("x"+i+"y"+j) != null ){
					//System.out.println("remove : "+"x"+i+"y"+j);
					set.put("x"+i+"y"+j, new Point(i,j));
					//map[i][j] = -1;
					s += getSameRegion(i, j, allSet,set);
				}else{
					
				}
			}else{
				if(Math.abs(map[i][j]) == WAY_UP && n == 1){
					if( allSet.remove("x"+i+"y"+j) != null ){
						set.put("x"+i+"y"+j, new Point(i,j));
						s += getSameRegion(i, j, allSet,set);
					}
				}else if(Math.abs(map[i][j]) == WAY_RIGHT && n == 3){
					if( allSet.remove("x"+i+"y"+j) != null ){
						set.put("x"+i+"y"+j, new Point(i,j));
						s += getSameRegion(i, j, allSet,set);
					}
				}else if(Math.abs(map[i][j]) == WAY_DOWN && n == 5){
					if( allSet.remove("x"+i+"y"+j) != null ){
						set.put("x"+i+"y"+j, new Point(i,j));
						s += getSameRegion(i, j, allSet,set);
					}
				}else if(Math.abs(map[i][j]) == WAY_LEFT && n == 7){
					if( allSet.remove("x"+i+"y"+j) != null ){
						set.put("x"+i+"y"+j, new Point(i,j));
						s += getSameRegion(i, j, allSet,set);
					}
				}
			}
		}
		return s;
	}

	
	
	//Validation Methods ***********************************************************************************************
	/** isLeftPath : ensures coordinate is not a wall and is a left only path 
	 * @param x
	 * @param y
	 * @return boolean isLeftPath
	 */
	public boolean isLeftPath(int x,int y){
		return map[x-1][y] < 10 && Math.abs(map[x-1][y]) != WAY_RIGHT;
	}
	
	/** isLeftPath : ensures Point is not a wall and is a left only path 
	 * @param Point point
	 * @return boolean isLeftPath
	 */
	public boolean isLeftPath(Point point){
		return isLeftPath(point.x, point.y);
	}
	
	/** isRightPath : ensures coordinate is not a wall and is a right only path 
	 * @param int x
	 * @param int y 
	 * @return boolean isRightPath
	 */
	public boolean isRightPath(int x,int y){
		return map[x+1][y] < 10 && Math.abs(map[x+1][y]) != WAY_LEFT;
	}
	
	/** isRightPath : ensures Point is not a wall and is a right only path 
	 * @param Point point
	 * @return boolean isRightPath
	 */
	public boolean isRightPath(Point point){
		return isRightPath(point.x, point.y);
	}
	
	/** isUpPath : ensures coordinate is not a wall and is an up only path 
	 * @param int x
	 * @param int y 
	 * @return boolean isUpPath
	 */
	public boolean isUpPath(int x,int y){
		return map[x][y-1] < 10 && Math.abs(map[x][y-1]) != WAY_DOWN;
	}
	
	/** isUpPath : checks if Point is not a wall and is an up only path 
	 * @param Point point
	 * @return boolean isUpPath
	 */
	public boolean isUpPath(Point point){
		return isUpPath(point.x, point.y);
	}
	
	/** isDownPath : checks if coordinate is not a wall and is a down only path 
	 * @param int x
	 * @param int y
	 * @return boolean isDownPath
	 */
	public boolean isDownPath(int x,int y){
		return map[x][y+1] < 10 && Math.abs(map[x][y+1]) != WAY_UP;
	}
	
	/** isDownPath : checks if Point is not a wall and is a down only path 
	 * @param Point point
	 * @return boolean isDownPath
	 */
	public boolean isDownPath(Point point){
		return isDownPath(point.x, point.y);
	}
	
	/** isPlayerAtGoal : ensures coordinate is not a wall and is a down only path 
	 * @param Point point
	 * @return boolean isPlayerAtGoal
	 */
	public boolean isPlayerAtGoal(Point point){
		return getGoal().equals(point);
	}
	
	//Cloning Methods *****************************************************************************************************
	/** verticalClone : 
	 * @param int x
	 * @param int y
	 * @param HashTableChain<Point, Boolean> visited
	 * @param int pathType
	 * @return void
	 */
	private void verticalClone(int x,int y,HashTableChain<Point, Boolean> visited,int pathType){
		int y0 = y;
		y --;
		while(map[x][y] < 10 && map[x+1][y]==WALL && map[x-1][y]==WALL){
			map[x][y] = pathType;
			visited.put(new Point(x, y), true);
			y--;
			if(random.nextDouble()<0.3)break;
		}
		y = y0+1;
		while(map[x][y] < 10 && map[x+1][y]==WALL && map[x-1][y]==WALL){
			map[x][y] = pathType;
			visited.put(new Point(x, y), true);
			y++;
			if(random.nextDouble()<0.3)break;
		}
	}
	
	/** horizontalClone : 
	 * @param int x
	 * @param int y
	 * @param HashTableChain<Point, Boolean> visited
	 * @param int pathType
	 * @return void
	 */
	private void horizontalClone(int x,int y,HashTableChain<Point, Boolean> visited,int pathType){
			int x0 = x;
			x--;
			while(map[x][y] < 10 && map[x][y+1]==WALL && map[x][y-1]==WALL){
				map[x][y] = pathType;
				visited.put(new Point(x, y), true);
				x--;
				if(random.nextDouble()<0.3)break;
			}
			x = x0+1;
			while(map[x][y] < 10 && map[x][y+1]==WALL && map[x][y-1]==WALL){
				map[x][y] = pathType;
				visited.put(new Point(x, y), true);
				x++;
				if(random.nextDouble()<0.3)break;
			}
		}
		
		
	//Calculation Methods **********************************************************************************************
	/** distance : calculates distance from point a to point b using standard x-y distance formula
	 * @param Point point
	 * @return double distance
	 */
	private double distance(Point p1,Point p2){
		return Math.sqrt( (p1.x-p2.x)*(p1.x-p2.x) + (p1.y-p2.y)*(p1.y-p2.y) );
	}
	
	
	//Output/Display Methods ***********************************************************************************************
	/** draw : draws out map into maze using squares for each piece in array
	 * @param Graphics gr //java awt graphics (used for drawing on screen)
	 * @return void
	 */
	public void draw(Graphics gr){
		for(int i=0;i<width;i++){
			for(int j=0;j<height;j++){
				
				if(map[i][j] == PATH){
					gr.setColor(Color.white);
					//gr.fillRect((i*2-i/2) *X, (j*2-j/2) *Y, X*2, Y*2);
					gr.fillRect(i*X, j*Y, X, Y);
				}else if(map[i][j] == WALL){
					gr.setColor(Color.black);
					//gr.fillRect((i*2-i/2) *X, (j*2-j/2) *Y, X*2, Y*2);
					gr.fillRect(i*X, j*Y, X, Y);
				}else if(map[i][j] == START){
					gr.setColor(new Color(102,0,51));
					//gr.fillRect((i*2-i/2) *X, (j*2-j/2) *Y, X*2, Y*2);
					gr.fillRect(i*X, j*Y, X, Y);
				}else if(map[i][j] == GOAL){
					gr.setColor(Color.BLUE);
					//gr.fillRect((i*2-i/2) *X, (j*2-j/2) *Y, X*2, Y*2);
					gr.fillRect(i*X, j*Y, X, Y);
				}else if(Math.abs(map[i][j]) == WAY_UP){
					for(int n=0;n<8;n++){
						gr.setColor(new Color(0,100+20*n,0));
						gr.fillRect(i*X+1+n, j*Y+Y-n-6, X-2*(n+1), 2);
					}
				}else if(Math.abs(map[i][j]) == WAY_DOWN){
					for(int n=0;n<8;n++){
						gr.setColor(new Color(0,100+20*n,0));
						gr.fillRect(i*X+1+n, j*Y+6+n, X-2*(n+1), 2);
						
					}
				}else if(Math.abs(map[i][j]) == WAY_RIGHT){
					for(int n=0;n<8;n++){
						gr.setColor(new Color(0,100+20*n,0));
						gr.fillRect(i*X+n+6, j*Y+1+n, 2, Y-2*(n+1));
					}
				}else if(Math.abs(map[i][j]) == WAY_LEFT){
					for(int n=0;n<8;n++){
						gr.setColor(new Color(0,100+20*n,0));
						gr.fillRect(i*X+X-n-6, j*Y+1+n, 2, Y-2*(n+1));
					}
				}
				
				if(map[i][j]<0 && isSolutionShown){
					gr.setColor(new Color(102,0,102));
					gr.fillRect(i*X+3, j*Y+3, X-6, Y-6);
				}
			}
		}
		gr.setColor(new Color(102,0,51));
		gr.fillRect(start.x*X, start.y*Y, X, Y);
		gr.setColor(new Color(0,76,153));
		gr.fillRect(goal.x*X, goal.y*Y, X, Y);
	}
	
	/** showSolution : shows solution if it is not already being shown
	 * @param n/a
	 * @return void
	 */
	public void showSolution(){
		isSolutionShown = !isSolutionShown;
	}
	
	/** hideSolution : hides path found for maze
	 * @param n/a
	 * @return void
	 */
	public void hideSolution(){
		isSolutionShown = false;
	}
	
	
	//Other ***************************************************************************************************************
	//Not currently Implemented
	public void makeTraffic(){
		for(int i=0;i<width;i++){
			for(int j = 0;j<height;j++){
				
			}
		}
	}
		
}