package tk.gibbs.mnk;

import java.util.ArrayList;

public class AI {

  private final double MAX_TIME;

  public AI (double max_time) {
    this.MAX_TIME = max_time;
  }

  public BoardState move (BoardState state) {
    long start_time = System.nanoTime();

    BoardState next_state = alphaBetaSearch(state);
    System.out.println(next_state);
    return next_state;


    // if (System.nanoTime() - start_time >= MAX_TIME) {
    //   return state; // TODO pick the best one
    // }
  }

  private BoardState alphaBetaSearch (BoardState state) {
    int v = maxValue(state, Integer.MIN_VALUE, Integer.MAX_VALUE);
    System.out.println("maxValue returned v = " + v);
    return new BoardState();
    // "return the action in successors(state) with value v"
  }

  private int maxValue (BoardState state, int alpha, int beta) {
    if (terminalTest(state)) {
      return utility(state);
    }
    int v = Integer.MIN_VALUE;
    for (BoardState s : successors(state)) {
      v = max(v, minValue(s, alpha, beta));
      if (v >= beta) {
        return v;
      }
      alpha = max(alpha, v);
    }
    return v;
  }

  private int minValue (BoardState state, int alpha, int beta) {
    if (terminalTest(state)) {
      return utility(state);
    }
    int v = Integer.MAX_VALUE;
    for (BoardState s : successors(state)) {
      v = min(v, maxValue(s, alpha, beta));
      if (v <= alpha) {
        return v;
      }
      beta = min(beta, v);
    }
    return v;
  }

  private int max (int a, int b) {
    return a < b ? b : a;
  }

  private int min (int a, int b) {
    return a > b ? b : a;
  }

  private BoardState[] successors (BoardState state) {
    ArrayList<BoardState> states = new ArrayList<BoardState>(64);
    for (int i = 0; i < state.board.length; i++) {
      for (int j = 0; j < state.board[i].length; j++) {
        if (state.board[i][j] == TileState.EMPTY) {
          states.add(state.copyBoardWithMove(TileState.O, i, j));
        }
      }
    }
    return states.toArray(new BoardState[states.size()]);
  }

  
  private boolean terminalTest (BoardState state) {
    TileState[][] board = state.board;
    int x = state.last_move_x;
    int y = state.last_move_y;
    TileState player = board[x][y];

    // Check left
    if (x > 2) {
      if (player == board[x - 1][y] && 
          player == board[x - 2][y] && 
          player == board[x - 3][y]) {
        return true;
      }
    }
    // Check right
    if (x < 5) {
      if (player == board[x + 1][y] && 
          player == board[x + 2][y] && 
          player == board[x + 3][y]) {
        return true;
      }
    }
    // Check up
    if (y > 2) {
      if (player == board[x][y - 1] && 
          player == board[x][y - 2] && 
          player == board[x][y - 3]) {
        return true;
      }
    }
    // Check down
    if (y < 5) {
      if (player == board[x][y + 1] && 
          player == board[x][y + 2] && 
          player == board[x][y + 3]) {
        return true;
      }
    }
    return false;
  }

  private int utility (BoardState state) {
    // Count how many 2 in a row
    int score = 0;
    for (int i = 1; i < state.board.length; i++) {
      for (int j = 1; j < state.board[i].length; j++) {
        if (state.board[i][j] == state.board[i - 1][j] ||
            state.board[i][j] == state.board[i][j - 1]) {
          score++;
        }
      }
    }
    return score;
  }
}
