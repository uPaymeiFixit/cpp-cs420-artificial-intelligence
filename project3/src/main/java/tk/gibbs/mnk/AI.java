package tk.gibbs.mnk;

import java.util.ArrayList;

public class AI {

  private final double MAX_TIME;
  public static long start_time;
  private BoardState highest;

  public AI (double max_time) {
    this.MAX_TIME = max_time;
  }

  // Return the best move given the state
  public BoardState move (BoardState state) {
    start_time = System.nanoTime();
    BoardState next_state = alphaBetaSearch(state);
    return next_state;
  }

  private BoardState alphaBetaSearch (BoardState state) {
    int v = maxValue(state, Integer.MIN_VALUE, Integer.MAX_VALUE);
    // out("maxValue returned " + v + "\n");
    // "return the action in successors(state) with value v"
    return this.highest;
  }

  private int maxValue (BoardState state, int alpha, int beta) {
    if (terminalTest(state)) {
      return utility(TileState.X, state);
    }
    int v = Integer.MIN_VALUE;
    BoardState w = state;
    for (BoardState s : successors(state)) {
      w = s;
      v = max(v, minValue(s, alpha, beta));
      if (v >= beta) {
        this.highest = w;
        return v;
      }
      alpha = max(alpha, v);
    }
    this.highest = w;
    return v;
  }

  private int minValue (BoardState state, int alpha, int beta) {
    if (terminalTest(state)) {
      return utility(TileState.O, state);
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

  // Generate array of all possible next moves
  private BoardState[] successors_exhaustive (BoardState state) {
    TileState next_player = state.board[state.last_move_x][state.last_move_y];
    next_player = next_player == TileState.X ? TileState.O : TileState.X;

    ArrayList<BoardState> states = new ArrayList<BoardState>(64);
    for (int i = 0; i < state.board.length; i++) {
      for (int j = 0; j < state.board[i].length; j++) {
        if (state.board[i][j] == TileState.EMPTY) {
          states.add(state.copyBoardWithMove(next_player, i, j));
        }
      }
    }
    return states.toArray(new BoardState[states.size()]);
  }

  private BoardState[] successors (BoardState state) {
    TileState next_player = state.board[state.last_move_x][state.last_move_y];
    next_player = next_player == TileState.X ? TileState.O : TileState.X;

    ArrayList<BoardState> states = new ArrayList<BoardState>(64);
    TileState[][] board = state.board;
    for (int i = 0; i < board.length; i++) {
      for (int j = 0; j < board[i].length; j++) {
        if (board[i][j] != TileState.EMPTY) {
          // Look left
          if (i != 0) {
            if (board[i - 1][j] == TileState.EMPTY) {
              states.add(state.copyBoardWithMove(next_player, i - 1, j));
            }
          }
          // Look right
          if (i != 7) {
            if (board[i + 1][j] == TileState.EMPTY) {
              states.add(state.copyBoardWithMove(next_player, i + 1, j));
            }
          }
          // Look up
          if (j != 0) {
            if (board[i][j - 1] == TileState.EMPTY) {
              states.add(state.copyBoardWithMove(next_player, i, j - 1));
            }
          }
          // Look down
          if (j != 7) {
            if (board[i][j + 1] == TileState.EMPTY) {
              states.add(state.copyBoardWithMove(next_player, i, j + 1));
            }
          }
        }
      }
    }
    return states.toArray(new BoardState[states.size()]);
  }

  // Return whether or not we've run out of time or passed 4 layers
  private boolean cutoffTest (BoardState state) {
    if (System.nanoTime() - this.start_time >= this.MAX_TIME) {
      return true;
    } 
    // if (state.depth > 4) {
    //   return true;
    // }
    return false;
  }
  
  // Return whether or not this is a winning board (or cutoff point is reached)
  private boolean terminalTest (BoardState state) {
    return cutoffTest(state) || state.terminal_state;
  }

  // Temporary heuristic: Count how many 2 in a row / column there are
  private int utility (TileState player, BoardState state) {
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

  // TEMPORARY DEBUG
  public static void out (String string) {
    System.out.print(ANSIColors.RED_BACKGROUND + string.replace("\n", "\n\t") + ANSIColors.RESET);
  }
  // TEMPORARY DEBUG
  public static void outl (String string) {
    System.out.println(ANSIColors.RED_BACKGROUND + string.replace("\n", "\n\t") + ANSIColors.RESET);
  }
}
