package tk.gibbs.mnk;

import java.util.ArrayList;

public class AI {
  final private double MAX_TIME;
  final private boolean first_play;

  static public long start_time;
  private BoardState highest;
  private int current_depth = 0;

  // TODO: make sure there aren't any more patterns we care about
  // TODO: update values
  public static Pattern[] patterns;

  public AI (double max_time, boolean first_play) {
    this.MAX_TIME = max_time;
    this.first_play = first_play;

    // ThreadSearch thread_search1 = new ThreadSearch();
    // ThreadSearch thread_search2 = new ThreadSearch();
    // ThreadSearch thread_search3 = new ThreadSearch();
    // ThreadSearch thread_search4 = new ThreadSearch();

    if (patterns == null) {
      final Pattern[] unrotatedPatterns = new Pattern[]{
        // unbounded 2 in a row
        // ∙ ✕ ✕ ∙
        new Pattern(
          new BoardTile[]{
            new BoardTile(0, 0, AbstractTileState.PLAYER),
            new BoardTile(1, 0, AbstractTileState.PLAYER),
            new BoardTile(-1, 0),
            new BoardTile(2, 0),
          }, 50
        ),
        // unbounded 1 and 1 in a row
        // ∙ ✕ ∙ ✕ ∙
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
        //   ∙
        //   ✕
        // ∙ ✕ ✕ ∙
        //   ∙
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
        // ∙ ✕ ✕ ✕ ∙
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
        // ∙ ✕ ✕ ✕ ◯
        new Pattern(
          new BoardTile[]{
            new BoardTile(0, 0, AbstractTileState.PLAYER),
            new BoardTile(1, 0, AbstractTileState.PLAYER),
            new BoardTile(2, 0, AbstractTileState.PLAYER),
            new BoardTile(3, 0),
            new BoardTile(-1, 0, AbstractTileState.OPPONENT),
          }, 1000
        ),
        // THIS IS ALREADY COVERED BY CHECKING TERMINAL STATE
        // 4 in a row
        // ✕ ✕ ✕ ✕
        // new Pattern(
        //   new BoardTile[]{
        //     new BoardTile(0, 0, AbstractTileState.PLAYER),
        //     new BoardTile(1, 0, AbstractTileState.PLAYER),
        //     new BoardTile(2, 0, AbstractTileState.PLAYER),
        //     new BoardTile(3, 0, AbstractTileState.PLAYER),
        //   }, 1000000
        // ),
      };

      ArrayList<Pattern> rotatedPatterns = new ArrayList<>();
      for (Pattern pattern : unrotatedPatterns) {
        // System.out.println(pattern);
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
    // return alphaBetaSearch(state);
    // return dumbSearch(state);
    return twoPlieSearch(state);
    // return nPlieSearch(state);
  }

  private BoardState twoPlieSearch (BoardState state) {
    double first_plie_weight = 0.75;
    successors_exhaustive(state);
    double best_heuristic = Double.MIN_VALUE;
    BoardState best_board = state.children.peek();
    for (BoardState s : state.children) {
      successors_exhaustive(s);
      // Get the average of s's children
      double sum = 0;
      for (BoardState t : s.children) {
        sum += t.heuristic_value;
      }
      sum /= s.children.size();
      // Take weighted average 75% 2nd level + 25% 1st level
      sum = first_plie_weight * sum + s.heuristic_value * (1 - first_plie_weight);
      if (sum > best_heuristic) {
        best_heuristic = sum;
        best_board = s;
      }
    }
    return best_board;
  }

  private BoardState nPlieSearch (BoardState state) {
    int n = 2;
    double weight = 0.8;
    successors_exhaustive(state);
    double best_heuristic = Double.MIN_VALUE;
    BoardState best_board = state.children.peek();
    for (BoardState s : state.children) {
      double average = weight * s.heuristic_value;
      average += (1 - weight) * getAverage(s, n, weight);
      if (average > best_heuristic) {
        best_heuristic = average;
        best_board = s;
      }
    }
    return best_board;
  }

  private double getAverage (BoardState state, int n, double weight) {
    successors_exhaustive(state);
    double sum = 0;
    for (BoardState s : state.children) {
      // barChart(s.depth);
      if (n == 0) {
        sum += s.heuristic_value;
      } else {
        sum += getAverage(s, n - 1, weight);
      }
    }
    sum /= state.children.size();
    return weight * state.heuristic_value + (1 - weight) * sum;
  }

  private BoardState dumbSearch (BoardState state) {
    if (state.depth > 1) {
      successors_exhaustive(state);
    } else {
      successors(state);
    }
    return state.children.poll();
  }

  private BoardState alphaBetaSearch (BoardState state) {
    int v = maxValue(state, Integer.MIN_VALUE, Integer.MAX_VALUE);
    return this.highest;
  }

  private int maxValue (BoardState state, int alpha, int beta) {
    if (cutoffTest(state)) {
      return state.heuristic_value;
    }
    int v = Integer.MIN_VALUE;
    BoardState s = state.children.peek();
    successors(state);
    // Only look at the top 25% of boards
    int size = state.children.size() / 4;
    while (state.children.size() > size) {
      s = state.children.poll();
      // printDepth(state);
      // outl(s.heuristic_value + "");
      v = Math.max(v, minValue(s, alpha, beta));
      if (v >= beta) {
        this.highest = s;
        return v;
      }
      alpha = Math.max(alpha, v);
    }
    this.highest = s;
    return v;
  }

  private int minValue (BoardState state, int alpha, int beta) {
    if (cutoffTest(state)) {
      return state.heuristic_value;
    }
    int v = Integer.MAX_VALUE;
    BoardState s = state.children.peek();
    successors(state);
    // Only look at the top 25% of boards
    int size = state.children.size() / 4;
    while (state.children.size() > size) {
      s = state.children.poll();
      v = Math.min(v, maxValue(s, alpha, beta));
      if (v <= alpha) {
        return v;
      }
      beta = Math.min(beta, v);
    }
    return v;
  }

  // Generate array of all possible next moves
  public static BoardState[] successors_exhaustive (BoardState state) {
    TileState next_player = state.last_player;
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

  public static BoardState[] successors (BoardState state) {
    TileState next_player = state.last_player;
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
    if (state.terminal_state) {
      return true;
    }
    if (System.nanoTime() - start_time >= this.MAX_TIME) {
      return true;
    }
    if (state.depth - this.current_depth > 10) {
      return true;
    }
    return false;
  }


  private static int last_seen_number = -1;
  private static int count = 0;
  // PRINTS A HORIZONTAL REPRESENTATION OF THE NUMBER. MAX IS 64
  public static void barChart (int value) {
    // Constraints on n
    value = value > 64 ? 64 : value;

    if (value == last_seen_number) {
      // if (count == 2) {
      //   String text = " " + value + " SUPPRESSING ";
      //   System.out.println(ANSIColors.CYAN_BACKGROUND_BRIGHT + text + ANSIColors.PURPLE_BACKGROUND_BRIGHT + leftPad(value - text.length()) + ANSIColors.RESET);
      // }
      count++;
    } else if (count != 0) {
      // We've just switched to a new number, so alert the user how many times that number was missed
      String text = " " + last_seen_number + ANSIColors.BLACK_BACKGROUND + ANSIColors.WHITE_BOLD + " x" + count;
      System.out.println(text + ANSIColors.PURPLE_BACKGROUND_BRIGHT + leftPad(value - text.length()) + ANSIColors.RESET);
      text = " " + value + " ";
      System.out.println(ANSIColors.CYAN_BACKGROUND + ANSIColors.WHITE_BOLD + text + ANSIColors.RED_BACKGROUND_BRIGHT + leftPad(value - text.length()) + ANSIColors.RESET);
      count = 0;
    } else {
      String text = " " + value + " ";
      System.out.println(ANSIColors.CYAN_BACKGROUND + ANSIColors.WHITE_BOLD + text + ANSIColors.RED_BACKGROUND_BRIGHT + leftPad(value - text.length()) + ANSIColors.RESET);
    }
    last_seen_number = value;
  }

  public static String leftPad (int n) {
    if (n < 0) {
      return "";
    }
    java.lang.StringBuilder sb = new java.lang.StringBuilder();
    for (int i = 0; i < n; i++) {
      sb.append(' ');
    }
    return sb.toString();
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
