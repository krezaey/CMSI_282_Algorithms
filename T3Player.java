/** ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 *  File name       :  T3Player.java
 *  @author keziahr :  Keziah Camille Rezaey
 *  Due Date        :  2020-02-28
 * ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 */

package t3;

import java.util.*;

/**
 * Artificial Intelligence responsible for playing the game of T3!
 * Implements the alpha-beta-pruning mini-max search algorithm
 */
public class T3Player {
      
    /**
     * Workhorse of an AI T3Player's choice mechanics that, given a game state,
     * makes the optimal choice from that state as defined by the mechanics of
     * the game of Tic-Tac-Total.
     * Note: In the event that multiple moves have equivalently maximal minimax
     * scores, ties are broken by move col, then row, then move number in ascending
     * order (see spec and unit tests for more info). The agent will also always
     * take an immediately winning move over a delayed one (e.g., 2 moves in the future).
     * @param state The state from which the T3Player is making a move decision.
     * @return The T3Player's optimal action.
     */
    public T3Action choose (T3State state) {
        return alphaBeta(state, Integer.MIN_VALUE, Integer.MAX_VALUE, true).bestAction;
    }
    
    /**
     * Helper methods that implements alpha-beta pruning with Minimax. Returns Player instance
     * tracking the best score and action for search.
     * @param state T3State
     * @param int a alpha
     * @param int b beta
     * @param maxPlayer boolean true / false if player is max node or not
     * @return Player<score, bestAction>
     */     
    private Player alphaBeta(T3State state, int a, int b, boolean maxPlayer) {
        int alpha = a;
        int beta = b;     
        Player current = new Player(0, null);
                
        if (state.isWin() && maxPlayer) {
            current.score = -1;
            return current;
        }
        if (state.isWin() && !maxPlayer) {
            current.score = 1;
            return current;
        }
        if (state.isTie()) {
            current.score = 0;
            return current;
        }
        if (maxPlayer) {
            current.score = Integer.MIN_VALUE;
            for (Map.Entry<T3Action, T3State> move : state.getTransitions().entrySet()) {
                Player child = alphaBeta(move.getValue(), alpha, beta, false);
                if (state.getNextState(move.getKey()).isWin()) {
                    child.score = 1;
                    child.bestAction = move.getKey();
                    return child;
                }
                if (child.score > current.score) { 
                    current.score = child.score;
                    current.bestAction = move.getKey(); 
                }
                alpha = Math.max(alpha, current.score);
                if (beta <= alpha) { break; }
            }
            return current;
        }
        else {
            current.score = Integer.MAX_VALUE;
            for (Map.Entry<T3Action, T3State> move : state.getTransitions().entrySet()) {
                Player child = alphaBeta(move.getValue(), alpha, beta, true);
                if (state.getNextState(move.getKey()).isWin()) {
                    child.score = -1;
                    child.bestAction = move.getKey();
                    return child;
                }
                if (child.score < current.score) { 
                    current.score = child.score;
                    current.bestAction = move.getKey(); 
                }
                beta = Math.min(beta, current.score);
                if (beta <= alpha) { break; }
            }
            return current;
        }  
    }    
    
    /**
     * Player class that tracks the score and bestAction for a particular node.
     */
    private class Player {
        
        private int score;
        private T3Action bestAction;
        
        Player(int score, T3Action action) {
            this.score = score;
            this.bestAction = action;
        }
    }
}

