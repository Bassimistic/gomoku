package com.veganjay.game.gomoku;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Set;
import java.util.Vector;

import com.veganjay.game.gomoku.GomokuBoard.Piece;

public class GomokuAI {

	// Constants
	public static final int MINMAX_DEPTH = 4;//4;
	
	public int numMoves = 0;
	public int numComparisons;
	
	// Member variables
	private Piece computerPiece;
	private Piece playerPiece;
	
	// Create a simple logger
	private Logger logger = new Logger("AI");
	
	/**
	 * Initialize the AI
	 * @param computerPiece the piece the computer is playing (X or O)
	 */
	public GomokuAI(Piece computerPiece) {
		this.computerPiece = computerPiece;
		
		if (computerPiece == Piece.X) {
			this.playerPiece = Piece.O;
		} else {
			this.playerPiece = Piece.X;
		}
	}
	
	private class Result {
		int score;
		int alpha;
		int beta;
		
		ArrayList<GomokuGameNode> gameNodes;
		public Result() {
			gameNodes = new ArrayList<GomokuGameNode>();
			this.score = 123;
		}
		
		public Move getMove() {
			Move move = null;
			GomokuGameNode result = gameNodes.get(0);
			if (result != null) {
				move = result.getMove();
			}
			return move;
		}
		public int getScore() {
			return score;
		}
		public void setScore(int score) {
			this.score = score;
		}
		
		public void add(GomokuGameNode node) {
			this.gameNodes.add(node);
		}

		public void addAll(Collection<GomokuGameNode> c) {
			this.gameNodes.addAll(c);
		}

	}
	
	private Result minimaxHelper(GomokuGameNode node, int depth, int alpha, int beta, boolean needMax) {
		Result result = null;
		
		Set<GomokuGameNode> childNodes = node.getChildren();
		
		for (GomokuGameNode child : childNodes) {
			if (child.isComputerWin()) {
				result = new Result();
				result.setScore(child.getObjectiveValue(computerPiece));
				result.add(child);
				return result;
			}
		}
		
		result = minimax(node, depth, alpha, beta, needMax);
		
		return result;
	}
	
	private Result minimax(GomokuGameNode node, int depth, int alpha, int beta, boolean needMax) {
		if (depth <= 0 || node.isTerminalNode()) {
			Result result = new Result();
			int score = node.getObjectiveValue(computerPiece);
			result.setScore(score);
			return result;
		}

		Result best = new Result();
		
		for (GomokuGameNode child : node.getChildren()) {
			Result result2 = minimax(child, depth-1, alpha, beta, !needMax);
			
			int score = result2.getScore();
			numComparisons++;
			if (needMax) {  // Do Max
				if (score > alpha) {
					alpha = score;
					best = new Result();
					best.add(child);
					best.addAll(result2.gameNodes);
				}
				if (beta <= alpha) {
					break;
				}
			} else { // Do Min
				if (score < beta) {
					
					best = new Result();
					best.add(child);
					best.addAll(result2.gameNodes);
					
					beta = score;
				}
				if (beta <= alpha) {
					break;
				}
			}
		}
		int retval = needMax ? alpha : beta;		
		best.setScore(retval);
		return best;
		//return needMax ? alpha : beta;
	}
	
	/**
	 * Given a game situation, determine the best move
	 * @param board game board
	 * @return a move (row, col)
	 */
	public Move getMove(GomokuBoard board) {
		Move move = null;
		
		int score = 0;
		int bestScore = Integer.MIN_VALUE;
		Result bestResult = null;
		
		GomokuGameNode bestMove = null;
		GomokuGameNode node = new GomokuGameNode(board, computerPiece);
		
		Set<GomokuGameNode> children = node.getChildren();
		numMoves++;
		numComparisons = 0;

		logger.debug("getMove() entered.");
		
		/*
		for (GomokuGameNode child : children) {
			// Use Minimax to get computer move
			Result result = this.minimax(child, MINMAX_DEPTH, Integer.MIN_VALUE, Integer.MAX_VALUE, false);
			
			score = result.getScore();
			
			// Display the move and score, for debugging
			logger.debug("Move:" + child + ", score=" + score);
			
			// Chose the best move
			if (score >= bestScore) {
				bestScore = score;
				bestMove  = child;
				bestResult = result;
			}
		}


		logger.debug("Number of comparisons="+numComparisons);
		if (bestMove != null) {
			logger.debug("bestMove " + bestMove.getMove() + ", bestScore = " + bestScore);
			logger.debug("bestResult.getMove= " + bestResult.getMove() + " bestResult.score=" + bestResult.getScore());
			move = bestMove.getMove();
			StringBuffer sb = new StringBuffer();
			sb.append("Follow-up moves: [");
			for (GomokuGameNode n: bestResult.gameNodes) {
				sb.append(n);
				sb.append(",");
			}
			sb.append("]");
			logger.debug(sb.toString());
		} else {
			System.err.println("bestMove was null!");
		}
		*/
		Result result = this.minimaxHelper(node, MINMAX_DEPTH, Integer.MIN_VALUE, Integer.MAX_VALUE, true);
		score = result.getScore();
		move  = result.getMove();
		logger.debug("bestMove " + move +  ", bestScore = " + score + " numComparisons=" + numComparisons);

		return move;
	}
} // End class GomokuAI
