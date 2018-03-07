package tk.gibbs.mnk;

import java.util.ArrayList;

public class AI {
  final private double MAX_TIME;
  static public long start_time;
  private BoardState highest;
  private int current_depth = 0;

  // TODO: make sure there aren't any more patterns we care about
  // TODO: update values
  public static Pattern[] patterns;

  public AI (double max_time) {
    this.MAX_TIME = max_time;

    if (patterns == null) {
      final Pattern[] unrotatedPatterns = new Pattern[]{
        // unbounded 2 in a row
        new Pattern(
          new BoardTile[]{
            new BoardTile(0, 0, AbstractTileState.PLAYER),
            new BoardTile(1, 0, AbstractTileState.PLAYER),
            new BoardTile(-1, 0),
            new BoardTile(2, 0),
          }, 100
        ),
        // unbounded 1 and 1 in a row
        new Pattern(
          new BoardTile[]{
            new BoardTile(0, 0, AbstractTileState.PLAYER),
            new BoardTile(2, 0, AbstractTileState.PLAYER),
            new BoardTile(-1, 0),
            new BoardTile(1, 0),
            new BoardTile(3, 0),
          }, 100
        ),
        // 2 L unbounded 2 in a row
        new Pattern(
          new BoardTile[]{
            new BoardTile(0, 0, AbstractTileState.PLAYER),
            new BoardTile(1, 0, AbstractTileState.PLAYER),
            new BoardTile(0, 1, AbstractTileState.PLAYER),
            new BoardTile(-1, 0),
            new BoardTile(0, -1),
            new BoardTile(2, 0),
            new BoardTile(0, 2),
          }, 500
        ),
        // unbounded 3 in a row
        new Pattern(
          new BoardTile[]{
            new BoardTile(0, 0, AbstractTileState.PLAYER),
            new BoardTile(1, 0, AbstractTileState.PLAYER),
            new BoardTile(2, 0, AbstractTileState.PLAYER),
            new BoardTile(-1, 0),
            new BoardTile(3, 0),
          }, 5000
        ),
        // single bounded 3 in a row
        new Pattern(
          new BoardTile[]{
            new BoardTile(0, 0, AbstractTileState.PLAYER),
            new BoardTile(1, 0, AbstractTileState.PLAYER),
            new BoardTile(2, 0, AbstractTileState.PLAYER),
            new BoardTile(3, 0),
            new BoardTile(-1, 0, AbstractTileState.OPPONENT),
          }, 20
        ),
        // 4 in a row
        new Pattern(
          new BoardTile[]{
            new BoardTile(0, 0, AbstractTileState.PLAYER),
            new BoardTile(1, 0, AbstractTileState.PLAYER),
            new BoardTile(2, 0, AbstractTileState.PLAYER),
            new BoardTile(3, 0, AbstractTileState.PLAYER),
          }, 1000000
        ),
      };

      ArrayList<Pattern> rotatedPatterns = new ArrayList<>();
      for (Pattern pattern : unrotatedPatterns) {
        System.out.println(pattern);
        rotatedPatterns.add(pattern);
        rotatedPatterns.add(new Pattern(pattern.rotate(), pattern.value));
        rotatedPatterns.add(new Pattern(pattern.rotate(2), pattern.value));
        rotatedPatterns.add(new Pattern(pattern.rotate(3), pattern.value));
      }
      patterns = rotatedPatterns.toArray(new Pattern[0]);
    }

  }

  // Return the best move given the state
  public BoardState move (BoardState state) {
    this.current_depth = state.depth;
    start_time = System.nanoTime();
    BoardState next_state = alphaBetaSearch(state);
    return next_state;
  }

  private BoardState alphaBetaSearch (BoardState state) {
    int v = maxValue(state, Integer.MIN_VALUE, Integer.MAX_VALUE);
    // out(state.children.peek().toString());
    // out("maxValue returned " + v + "\n");
    // "return the action in successors(state) with value v"
    return this.highest;
  }

  private int maxValue (BoardState state, int alpha, int beta) {
    if (terminalTest(state)) {
      return state.heuristic_value;
    }
    int v = Integer.MIN_VALUE;
    BoardState w = state;
    successors(state);
    // Only look at the top 25% of boards
    int size = state.children.size() / 4;
    while (state.children.size() > size) {
    // for (BoardState s : state.children) {
      BoardState s = state.children.poll();
      // printDepth(state);
      // outl(s.heuristic_value + "");
      w = s;
      v = Math.max(v, minValue(s, alpha, beta));
      if (v >= beta) {
        this.highest = w;
        return v;
      }
      alpha = Math.max(alpha, v);
    }
    this.highest = w;
    return v;
  }

  private int minValue (BoardState state, int alpha, int beta) {
    if (terminalTest(state)) {
      return state.heuristic_value;
    }
    int v = Integer.MAX_VALUE;
    successors(state);
    int size = state.children.size() / 4;
    while (state.children.size() > size) {
    // for (BoardState s : successors(state)) {
      BoardState s = state.children.poll();
      v = Math.min(v, maxValue(s, alpha, beta));
      if (v <= alpha) {
        return v;
      }
      beta = Math.min(beta, v);
    }
    return v;
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
    if (System.nanoTime() - start_time >= this.MAX_TIME) {
      return true;
    }
    if (state.depth - this.current_depth > 10) {
      return true;
    }
    return false;
  }

  // Return whether or not this is a winning board (or cutoff point is reached)
  private boolean terminalTest (BoardState state) {
    return cutoffTest(state) || state.terminal_state;
  }


  // TEMPORARY DEBUG
  private void printDepth (BoardState state) {
    java.lang.StringBuilder sb = new java.lang.StringBuilder();
    int x = state.depth > 64 ? 64 : state.depth;
    for (int i = 0; i < x; i++) {
      sb.append(' ');
    }
    sb.append(state.depth);
    outl(sb.toString());
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
