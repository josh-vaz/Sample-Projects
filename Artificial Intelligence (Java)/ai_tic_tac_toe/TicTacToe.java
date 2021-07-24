/*
 * The University of North Carolina at Charlotte
 * ITCS 3153 - Intro to Artificial Intelligence
 * 
 * Programming Assignment 2 - Adversarial Search
 */

import java.awt.*;
import java.awt.event.*;
import java.util.concurrent.ExecutionException;

import javax.swing.*;
import javax.swing.SwingWorker;


public class TicTacToe
{
	private static GameAI agent = new GameAI();
	
	private static int BOARD_SIZE = 5;
	private static String PLAYER_LETTER = "X";
	private static String AI_LETTER = "O";
	private static boolean isPlayerTurn = true;
	
	
	private static int[][] winCombinations = new int[][] { 
			{ 0, 1, 2 }, { 3, 4, 5 }, { 6, 7, 8 }, // horizontal wins
			{ 0, 3, 6 }, { 1, 4, 7 }, { 2, 5, 8 }, // vertical wins
			{ 0, 4, 8 }, { 2, 4, 6 } // diagonal wins
	};

	
	private static JButton buttons[] = new JButton[9]; // create 9 buttons


	public static void main(String[] args)
	{
		gamePanel(); // Starts the game
	}


	private static void gamePanel()
	{
		JFrame frame = new JFrame("Tic Tac Toe");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		// Creates a panel with a box like a Tic-Tac-Toe board
		JPanel panel = new JPanel(); 
		panel.setLayout(new GridLayout(3, 3));
		panel.setBorder(BorderFactory.createLineBorder(Color.gray, 3));
		panel.setBackground(Color.white);

		/*
		 * Place buttons on the board
		 * Each tile is a button
		 */
		for (int i = 0; i <= 8; i++)
		{ 
			buttons[i] = new MyButton();
			panel.add(buttons[i]);
		}

		frame.getContentPane().add(panel);
		frame.pack();
		frame.setVisible(true);
		
		// set frame size and start the game
		frame.setSize(500, 500);
	}
	

	public static int letterCount = 0; // used to count letters and keep track of who plays next

	
	/*
	 * The following code creates a customized button class,
	 * based on the JButton class of the Java Swing library 
	 */
	@SuppressWarnings("serial")
	private static class MyButton extends JButton implements ActionListener
	{
		boolean win = false; // there is not a win


		public MyButton()
		{ // creating blank board
			super();
			setFont(new Font(Font.SERIF,Font.BOLD, 125));
			setText(" ");
			addActionListener(this);
		}


		public void actionPerformed(ActionEvent e)
		{ 
			/*
			 * Place X or O's on the board.
			 * We assume that the player starts and is using the X
			 */
			if (isPlayerTurn && getText().equals(" ") && win == false)
			{
				letterCount = letterCount + 1;
				
				//	Sets the button's text property to show the player's move
				setText(PLAYER_LETTER);
			} 
			else
			{
				/*
				 * If the user clicks on a button that is already played
				 * or if it is not her turn, we simply ignore it.
				 */
				return;
			}
						
			
			//	Display debugging information
			Log.debug(String.format("[Current Move: %s] [Total Moves: %d]", PLAYER_LETTER, letterCount));
			agent.updateState( getBoardState() );

			
			// Check whether the game has ended
			win = checkGameEnd();
			
			if (!win)
			{
				isPlayerTurn = false;
			}
					
			
			/* It is the AI's turn to select a move.
			 * Create and run a background task to execute the AI algorithm.
			 * 
			 * For a reference, see https://docs.oracle.com/javase/tutorial/uiswing/concurrency/simple.html
			 */
			if (!isPlayerTurn && win == false)
			{
				SwingWorker<String[], Void> worker = new SwingWorker<String[], Void>()
				{
					@Override
					public String[] doInBackground()
					{
						return agent.getNextMove();
					}
					
					@Override
				    public void done() {
						Log.debug("AI move selected!");
						GameAI.showTotalCount();
						
						//	Update the game board
						letterCount = letterCount + 1;
						
						try
						{
							updateBoardState(get());
							
							// Check whether the game has ended
							if (checkGameEnd() == false)
							{			
								isPlayerTurn = true;
							}
						} 
						catch (InterruptedException e)
						{
							e.printStackTrace();
						} 
						catch (ExecutionException e)
						{
							e.printStackTrace();
						}
					}
				};
				
				worker.execute();	
			}
		}

	
		/*
		 * Checks the board to decide whether the game is over
		 */
		private static boolean checkGameEnd()
		{		
			int playAgain = JOptionPane.DEFAULT_OPTION;	// Stores the users option at the end of a game
			
			//	A minimum of five moves must be played before anyone can win
			if (letterCount < 5)
			{
				return false;
			}
			
			
			boolean win = false;
			
			// Check the game board for winning combinations
			for (int i = 0; i <= 7; i++)
			{
				if (buttons[winCombinations[i][0]].getText().equals(buttons[winCombinations[i][1]].getText())
						&& buttons[winCombinations[i][1]].getText().equals(buttons[winCombinations[i][2]].getText())
						&& buttons[winCombinations[i][0]].getText() != " ")
				{	
					// The current board state is a win for one of the players
					win = true;
				}
			}
			
			
			if (win == true)
			{ 
				// Let the user know who wins and give option to play again
				String winner = isPlayerTurn ? "Contratulations! You win " : "Sorry, the AI wins";
				
				playAgain = JOptionPane.showConfirmDialog(null, winner + " the game!  Do you want to play again?",
						winner, JOptionPane.YES_NO_OPTION);
	
			} 
			else if (letterCount == 9 && win == false)
			{	// Tied game, announce and ask if the user want to play again
				playAgain = JOptionPane.showConfirmDialog(null, "The game was a tie!  Do you want to play again?",
						"Tied game!", JOptionPane.YES_NO_OPTION);
				win = true;
			}
	
			
			if (playAgain == JOptionPane.YES_OPTION && win == true)
			{
				// If the user wants to play again clear all the buttons and start over
				clearButtons();
				agent.updateState( getBoardState() );
				win = false;
				isPlayerTurn = true;
				GameAI.setTotalCount(0);
			} 
			else if (playAgain == JOptionPane.NO_OPTION)
			{
				System.exit(0); // exit game if the user do not want to play again
			}
			
			return win;
		}
	}

	/*
	 * Clear all 8 buttons
	 */
	public static void clearButtons()
	{
		for (int i = 0; i <= 8; i++)
		{
			buttons[i].setText(" ");
		}
		
		letterCount = 0; // reset the count
	}
	
	
	/*
	 * Scans the board and updates the state to record a new move
	 */
	private static String[] getBoardState()
	{
		String[] boardState = new String[BOARD_SIZE * BOARD_SIZE];
		
		for (int tileNum = 0; tileNum < BOARD_SIZE*BOARD_SIZE; tileNum++)
		{
			boardState[tileNum] = buttons[tileNum].getText()	== " " ? "-" : buttons[tileNum].getText();
		}

		return boardState;
	}
	
	
	/*
	 * Updates the game board to show the AI's play.
	 * 
	 * This code assumes that the player uses 'X' and the AI 'O'
	 */
	private static void updateBoardState(String[] newBoardState)
	{
		for (int tileNum = 0; tileNum < BOARD_SIZE*BOARD_SIZE; tileNum++)
		{
			if (newBoardState[tileNum] != "-" && buttons[tileNum].getText() == " ")
			{
				buttons[tileNum].setText(AI_LETTER);
			}
			
		}
	}
}