/*
 * The University of North Carolina at Charlotte
 * ITCS 3153 - Intro to Artificial Intelligence
 * 
 * Programming Assignment 2 - Adversarial Search
 * 
 * Based on code from Dilip Kumar Subramanian
 * 
 * Modified by Julio C. Bahamon
 */

import java.util.ArrayList;
import java.util.Random;

public class GameAI
{
	private static String AI_LETTER = "O";
	private static String PLAYER_LETTER = "X";
	public static int BOARD_SIZE = 3;
	static int drawset = 0;
	private static int totalCount;

	String[] boardState;


	GameAI()
	{
		boardState = new String[BOARD_SIZE * BOARD_SIZE];
	}
	
	
	public void updateState(String[] state)
	{
		boardState = state;
		
		Log.debug("Printing the board state after player plays");
		showBoardState(boardState);
	}
	
	
	public String[] getNextMove()
	{
		GameState state = new GameState(BOARD_SIZE, boardState);

		// Calling Minimax algorithm
		Log.debug("Starting minimax...");
		
		findBestBoardMinimax(state);
		
		return boardState;
	}
	
	
	/*
	 * This method shows the state of the game board.
	 * 
	 * The buttons are numbered from zero to 8, hence
	 * the layout is as follows:
	 * 
	 * 		[0] [1] [2]
	 * 		[3] [4] [5]
	 *		[6] [7] [8]
	 */
	private static void showBoardState(String[] state)
	{
		StringBuilder sb = new StringBuilder();
		
		sb.append("Board State:");
		
		for (int row = 0; row < BOARD_SIZE; row++)
		{
			sb.append("\n\t");
			for (int col = 0; col < BOARD_SIZE; col++)
			{
				sb.append(String.format("[%s] ", state[(row*BOARD_SIZE)+col]));
			}
		}
		
		Log.debug(sb.toString());
	}
	
	
	/**
	 *
	 * Updates the boardState with the play the AI thinks is most suitable according
	 * to the algorithm, here the algorithm is simple Minimax, which will generate
	 * the whole tree and assign the utility value at the leaf nodes. The values
	 * assigned are +1000 for winner of AI, -1000 for winner of user and 0 if draw
	 * The values are then backed up to the original list of available
	 * generatedBoards and the best of the board is selected.
	 *
	 * @param state
	 *            Board state of the current boardState
	 * @return void
	 *
	 **/
	public void findBestBoardMinimax(GameState state)
	{
		ArrayList<GameState> currentSuccessors = state.generateSuccessors(state, AI_LETTER);
		GameState bestMove = currentSuccessors.get(0);
		int highestUtility = Minimax.miniMax(bestMove,PLAYER_LETTER);
		int utility;
		
		for(GameState child : currentSuccessors){
			utility = Minimax.miniMax(child, PLAYER_LETTER);
			if (highestUtility < utility){
				highestUtility = utility;
				bestMove = child;
			}
		}
		
		boardState = bestMove.getBoardState();
		showBoardState(boardState);
		
	}
	

	static void showTotalCount()
	{
		Log.debug("Total states generated/explored " + getTotalCount());
	}


	public static int getTotalCount()
	{
		return totalCount;
	}


	public static void setTotalCount(int totalCount)
	{
		GameAI.totalCount = totalCount;
	}
}