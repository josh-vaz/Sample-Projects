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
import java.util.Arrays;


public class GameState
{
	private int boardSize;
	private String[] boardState;
	private int value;


	// Constructor for new Board
	public GameState(int boardSize, String[] boardState)
	{
		this.boardState = boardState;
		this.boardSize = boardSize;
	}


	public GameState()
	{
	}


	// Getters and Setters for the board
	public int getBoardSize()
	{
		return boardSize;
	}


	public void setBoardSize(int boardSize)
	{
		this.boardSize = boardSize;
	}


	public String[] getBoardState()
	{
		return boardState;
	}


	public void setBoardState(String[] boardState)
	{
		this.boardState = boardState;
	}


	public int getValue()
	{
		return value;
	}


	public void setValue(int value)
	{
		this.value = value;
	}


	/**
	 *
	 * Returns true if the currentPlayer is the winner 
	 * 
	 * Checks the rows, columns and diagonals for the winner 
	 * and return true if the winner is found, otherwise returns false
	 *
	 * @param state
	 *            Board state of the current boardState
	 * @param currentPlayer
	 *            the currentPlayer to check winner for
	 * @return boolean
	 *
	 **/
	public boolean checkWinner(String[] state, String currentPlayer)
	{
		int row = 0, col = 0, i = 0;
		String[][] stateArray = convertToArray(state);
		int n = stateArray.length;
		
		//	Checks full rows
		for (row = 0; row < n; row++)
		{
			col = 0; 
				
			while (stateArray[row][col] == currentPlayer)
			{
				col++;
				i++;
				
				if (i == n)
				{
					return true;
				}
			}
			
			i = 0;
		}

		
		//	Checks full columns
		for (col = 0; col < n; col++)
		{
			row = 0; 
			
			while (stateArray[row][col] == currentPlayer)
			{
				row++;
				i++;
				if (i == n)
				{
					return true;
				}
			}
			
			i = 0;
		}

			
		//	Checks diagonals - upper left to bottom right
		col = 0; 
		row = 0; 
		
		while (stateArray[row][col] == currentPlayer)
		{
			row++;
			col++;
			i++;
			
			if (i == n)
			{
				return true;
			}
		}
			
		i = 0;
	

		//	Checks diagonals - upper right to bottom left
		for (col = n - 1; col > 0 + (n - 2); col--)
		{
			row = 0;
			
			while (stateArray[row][col] == currentPlayer)
			{
				row++;
				col--;
				i++;
				
				if (i == n)
				{
					return true;
				}
			}
			
			i = 0;
		}

		return false;
	}


	/**
	 *
	 * Converts the boardState(represented as an array of Strings 
	 * into a String matrix of boardSize * boardSize for the 
	 * checkWinner function
	 *
	 * @param boardState
	 *            boardState which needs to be converted to matrix
	 * @return a String matrix
	 **/
	public String[][] convertToArray(String[] boardState)
	{
		String[][] boardStateArray = new String[GameAI.BOARD_SIZE][GameAI.BOARD_SIZE];

		int next = 0;
		
		for (int i = 0; i < GameAI.BOARD_SIZE; i++)
		{
			for (int j = 0; j < GameAI.BOARD_SIZE; j++)
			{
				boardStateArray[i][j] = boardState[next++];
			}
		}
		return boardStateArray;
	}


	/**
	 *
	 * Returns the boolean true for the boardState is full and if no more moves are
	 * available else returns false
	 *
	 * @param state
	 *            Board state of the current boardState
	 * @return boolean
	 *
	 **/
	public boolean boardFullCheck(String[] state)
	{
		for (int i = 0; i < state.length; i++)
		{
			if (state[i].equals("-"))
				return false;
		}
		return true;
	}


	/**
	 *
	 * Returns the ArrayList of the Boards for the current boardState and the
	 * currentPlayer
	 *
	 * @param board
	 *            Board state of the current boardState
	 * @param currentPlayer
	 *            the currentPlayer to generate the future moves
	 * @return ArrayList of the Boards, i.e., the next possible states a player can
	 *         play
	 *
	 **/
	ArrayList<GameState> generateSuccessors(GameState board, String currentPlayer)
	{
		ArrayList<GameState> tmpBoard = new ArrayList<GameState>();
		
		Integer[] globalIndex = new Integer[GameAI.BOARD_SIZE * GameAI.BOARD_SIZE
				- countFullSquares(board.getBoardState())];
		int count = 0;
		String[] tmpState = board.getBoardState();

		int iterateValue = GameAI.BOARD_SIZE * GameAI.BOARD_SIZE - countFullSquares(board.getBoardState());
		
		for (int i = 0; i < iterateValue; i++)
		{
			int[] indices = findAvaibleIndex(tmpState, globalIndex);
			globalIndex[count++] = indices[0];
			String[] maybeState = new String[board.getBoardState().length];
			for (int s = 0; s < maybeState.length; s++)
				maybeState[s] = board.getBoardState()[s];

			maybeState[indices[0]] = currentPlayer;
			tmpState = maybeState;
			GameState newBoard = new GameState(GameAI.BOARD_SIZE, tmpState);
			tmpBoard.add(newBoard);
			tmpState = newBoard.getBoardState();
		}
		return tmpBoard;
	}


	/**
	 *
	 * Returns the available index for generating the new Boards
	 *
	 * @param mboardState
	 *            Board state to generate the available index
	 * @param globalI
	 *            globalIndex for which the states are already generated
	 * @return the Array of integers where the board states are yet to be generated
	 *
	 **/
	int[] findAvaibleIndex(String[] mboardState, Integer[] globalI)
	{
		int[] tmpBoardState = new int[mboardState.length];
		int index = 0;

		for (int i = 0; i < mboardState.length; i++)
		{
			if (mboardState[i].equals("-"))
			{
				if (!(Arrays.asList(globalI).contains(i)))
					tmpBoardState[index++] = i;

			}
		}
		return tmpBoardState;
	}


	/**
	 *
	 * Prints the boardState of the current/requested boardState
	 *
	 * @param state
	 *            current/requested boardState to be printed
	 * @return void
	 **/
	public void printBoardState(String[] state)
	{
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < state.length; i++)
		{
			sb.append(state[i]);
		}
		Log.debug("Printing the board state " + sb.toString());
	}


	/**
	 *
	 * Prints the boardState of the current/requested boardState
	 *
	 * @param state
	 *            current/requested boardState to be printed
	 * @return void
	 **/
	public void printBoardStateMax(String[] state)
	{
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < state.length; i++)
		{
			sb.append(state[i]);
		}
		Log.debug("Board state MAX: " + sb.toString());
	}


	/**
	 *
	 * Prints the boardState of the current/requested boardState
	 *
	 * @param state
	 *            current/requested boardState to be printed
	 * @return void
	 **/
	public void printBoardStateMin(String[] state)
	{
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < state.length; i++)
		{
			sb.append(state[i]);
		}
		Log.debug("Board state MIN: " + sb.toString());
	}


	/**
	 *
	 * Returns the count for the number of X's and O's already set in the given
	 * board state
	 *
	 * @param state
	 *            current/requested boardState to be printed
	 * @return count for the number of x's and o's
	 **/
	public int countFullSquares(String[] state)
	{
		int count = 0;
		for (int i = 0; i < state.length; i++)
		{
			if (state[i] != null)
				if (state[i].equals("X") || state[i].equals("O"))
					count++;
		}
		return count;
	}
}