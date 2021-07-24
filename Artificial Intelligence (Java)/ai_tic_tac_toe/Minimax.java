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


public class Minimax
{
	private static String AI_LETTER = "O";
	private static String PLAYER_LETTER = "X";
	
	/**
	 *
	 * This will recursively call Minimax depending on the current player, if the
	 * current player is O, the algorithm will find the MAX available board and if
	 * the current player is X, the algorithm will find the MIN avaiLable board.
	 * 
	 * We assume that the human player is X and that the AI is O
	 * 
	 * The terminal state check is done at the start before recursively calling
	 * Minimax, the terminal checks are checkWinner for player X(Human) and O(AI) and
	 * if the board state is full, if either of the conditions gets satisfied then
	 * it will return the value as decided if winner is AI(O), assign +1, if
	 * winner is User(X) assign -1 and if the state is draw assign 0 and return
	 *
	 * @param state
	 *            board for which the Minimax will be called recursively
	 * @param player
	 *            player for whom the game state should be generated
	 * @return boolean true/false
	 **/
	public static int miniMax(GameState state, String player)
	{
		GameAI.setTotalCount(GameAI.getTotalCount() + 1);
		
		if(state.checkWinner(state.getBoardState(),PLAYER_LETTER)) return -1;
		else if(state.checkWinner(state.getBoardState(),AI_LETTER)) return 1; 
		else if(state.boardFullCheck(state.getBoardState())) return 0;
		
		double min = Double.POSITIVE_INFINITY;
		double max = Double.NEGATIVE_INFINITY;
		double value = 0;
		
		ArrayList<GameState> currentSuccessors = state.generateSuccessors(state, player);
		for(GameState child : currentSuccessors){
			if(player == AI_LETTER){
				child.printBoardStateMax(child.getBoardState());
				value = miniMax(child,PLAYER_LETTER);
				if(value > max) max = value;
			}
			else if(player == PLAYER_LETTER){
				child.printBoardStateMin(child.getBoardState());
				value = miniMax(child,AI_LETTER);
				if(value < min) min = value;
			}
		}
		
		if(player == AI_LETTER) return (int)max;
		else return (int)min;

	}
}