import java.util.Scanner;
import java.util.Random;
import java.util.List;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;//REMOVE

public class Driver {

	static final int BOARD_DIM = 15;
	static final double PERCENT_BLOCKED = 0.1;
	static final double NUM_BLOCKED = (BOARD_DIM * BOARD_DIM) * PERCENT_BLOCKED;
	static int[][] board;
	static int stepNum;
	
	public static void main(String[] args) {
		Scanner in = new Scanner(System.in);
		boolean play = true;
		boolean validInput = false;
		String input;
		int sPos[] = new int[2];
		int gPos[] = new int[2];
		
		System.out.println("**Easiest to see when console maximized**");
		
		while(play){
			while(!validInput){
				//Get Start/Goal location from user
				System.out.println("\nEnter any number 0-14\n");
				System.out.print("Enter START X Position: ");
				sPos[1] = in.nextInt();
				System.out.print("Enter START Y Position: ");
				sPos[0] = in.nextInt();
				System.out.print("Enter GOAL X Position: ");
				gPos[1] = in.nextInt();
				System.out.print("Enter GOAL Y Position: ");
				gPos[0] = in.nextInt();
				if(gPos[1] == sPos[1] && gPos[0] == sPos[0]){						//If goal and start are same, shame user
					System.out.println("\nCongrats! Start = Goal ... wow ... Try again");
					continue;														//Must enter new positions -- restart loop
				}
				if(validInput(sPos[1],sPos[0],gPos[1],gPos[0]))validInput = true;
				else validInput = false;
			}
				
			TileNode start = new TileNode(sPos,null);
			TileNode goal = new TileNode(gPos,null);
			
			//Main program flow
			createBoard(sPos,gPos);
			drawBoard();														//Show initial state
			showPath(aStarSearch(start,goal));
			
			//Ask to restart
			System.out.print("\nType 'y' to restart: ");
			input = in.next();
			if(input.equals("y")) play = true;
			else play = false;
		}
		in.close();
	}
	
	/**
	 * Some inspiration for code found on AIMA site at:
	 * https://github.com/aimacode/aima-python/blob/master/search.py
	 * Examined the following:
	 * Class: SimpleProblemSolvingAgentProgram
	 * Methods: best_first_graph_search and astar_search
	 * 
	 * @param node
	 * @param goal
	 * @return path
	 */
	public static List<TileNode> aStarSearch(TileNode node, TileNode goal){
		List<TileNode> frontier = new ArrayList<TileNode>();		//Adjacent to explored nodes
		List<TileNode> visited = new ArrayList<TileNode>();			//Explored nodes
		frontier.add(node);
		
		while(frontier.size() > 0){									//While there are nodes that can be explored
			
			//Select node to examine from frontier
			node = frontier.get(0);
			for(TileNode n : frontier) if(n.f < node.f) node = n;	//Choose lowest F(n) value node from frontier
			
			//If node is goal
			if(node.equals(goal)){
				 List<TileNode> path = new ArrayList<TileNode>();	//Path to be returned
				 while(node != null){
					 path.add(node);
					 node = node.parent;
				 }
				 return path;										//Path is found -- return
			}
			
			//Move node from frontier to visited list
			frontier.remove(node);
			visited.add(node);
			
			//Add to frontier if possible
			int leftPos[] = {node.location[0] - 1, node.location[1]};
			int topPos[] = {node.location[0], node.location[1] + 1};
			int rightPos[] = {node.location[0] + 1, node.location[1]};
			int lowerPos[] = {node.location[0], node.location[1] - 1};
			int childrenPos[][] = {leftPos,topPos,rightPos,lowerPos};							//Potential children locations
			for(int i = 0; i < 4; i++){
				
				if(validSpace(childrenPos[i])){													//If on board and not blocked
					
					TileNode child = new TileNode(childrenPos[i],node);							//Create new node
					
					if(!onList(visited,child) && !onList(frontier,child)){						//If not already on a list
						child.calcFunctions(goal.location, node.g);								//Calculate functions g,h,f
						frontier.add(child);													//Add to frontier
					}
				}
			}
		}//end of while loop
		
		return null; //no path found
	}
	
	public static void createBoard(int[] sPos, int[]gPos){
		board = new int[BOARD_DIM][BOARD_DIM];		//0 represents free space
		board[sPos[0]][sPos[1]] = 1;				//1 represents start location
		board[gPos[0]][gPos[1]] = 2;				//2 represents goal location
		
		stepNum = 0; 								//Set number of steps to 0
		
		Random random = new Random(); 
		int blockX;
		int blockY;
		for(int i = 0; i < (int)NUM_BLOCKED; i++){	//Creates blocked spaces
			blockX = random.nextInt(BOARD_DIM);
			blockY = random.nextInt(BOARD_DIM);
			while(board[blockX][blockY] != 0){		//While random tile has a non-zero value(blocked,start,goal) -> re-randomize
				blockX = random.nextInt(BOARD_DIM);
				blockY = random.nextInt(BOARD_DIM);
			}
			board[blockX][blockY] = -1;				//-1 represents blocked location
		}
	}
	
	public static void drawBoard(){
		
		if(stepNum == 0) System.out.print("\nKEY: \nO -> Agent  \nG -> Goal  \nX -> Blocked\n\nInitial Board\n\n");
		else System.out.print("\nStep Number " + stepNum + "\n\n");
		
		for(int i = 0; i < BOARD_DIM; i++){
			for(int j = 0; j < BOARD_DIM; j++){
				switch(board[i][j]){
					case -1:
						System.out.print('X');		//Blocked tile
						break;
					case 0:
						System.out.print('-');		//Empty tile
						break;
					case 1:
						System.out.print('O');		//Actor tile
						break;
					case 2:
						System.out.print('G');		//Goal tile
						break;
					case 3:
						System.out.print('*');		//Traversed tile
						break;
					default:
						System.out.print('E');		//Error
				}
			}
			System.out.println();
		}
		
		stepNum++;
		
	}
	
	public static boolean onList(List<TileNode> list, TileNode value){
		for(int i = 0; i < list.size(); i++){
			if(list.get(i).equals(value)) return true;							//If node exists in list of nodes return true
		}
		return false;															//Else return false
	}

	public static boolean validSpace(int[] location){
		if(location[0] >= 0 && location[0] < BOARD_DIM && location[1] >= 0 && location[1] < BOARD_DIM)		//If location is in board dimensions check if blocked
			if(board[location[0]][location[1]] != -1) return true;											//If tile not blocked return true
		return false;																						//Else return false
	}
	
	public static void showPath(List<TileNode> path){
		
		if(path == null){														//If no path found skip the rest
			System.out.println("\nNo Path Found");
			return;
		}
		
		for(int i = path.size() - 2; i >= 0; i--){
			board[path.get(i+1).location[0]][path.get(i+1).location[1]] = 3;	//3 represents agent has previously been on tile
			board[path.get(i).location[0]][path.get(i).location[1]] = 1;		//1 represents current agent location
			
			//Pause to show progression
			try {
				Thread.sleep(200);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			
			drawBoard();														//Show updated board
		}
		System.out.println("\nSteps shown sequentialy above\n*: indicates path traversed\nO: indicates agent location\nX: indicates blocked location\nG: indicates goal before reached");
	}
	
	public static boolean validInput(int i1, int i2, int i3, int i4){
		int[] inputChain = {i1,i2,i3,i4};
		for(int i = 0; i < 4; i++){
			if(inputChain[i] < 0 || inputChain[i] >= BOARD_DIM){				//Input must be in board dimensions
				System.out.println("\nInvalid Number -- try again");
				return false;
			}
		}
		return true;
	}
	
}
