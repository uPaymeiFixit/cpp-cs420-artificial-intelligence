package tk.gibbs.mnk;

// import java.util.HashMap;
import java.util.LinkedList;
import java.util.PriorityQueue;
import java.lang.StringBuilder;

public class BoardState implements Comparable<BoardState> {
  final public TileState[][] board;
  final public int heuristic_value;
  final public TileState last_player;
  final public int last_move_x;
  final public int last_move_y;
  final public int depth;
  final public boolean terminal_state;
  final public PriorityQueue<BoardState> children = new PriorityQueue<>();
  // final public byte hash;
  // final static public HashMap<Byte, BoardState> visited_states = new HashMap<>();

  BoardState () {
    // Initilaize with empty board
    this(generateEmptyBoard());
  }

  BoardState (TileState[][] board) {
    this(board, TileState.X, 0, 0, 0, (byte)0);
  }

  BoardState (
    TileState[][] board, TileState last_player, int last_move_x, int last_move_y, int depth, byte hash
  ) {
    this.board = board;
    this.last_player = last_player;
    this.last_move_x = last_move_x;
    this.last_move_y = last_move_y;
    this.depth = depth;
    this.terminal_state = this.terminalTest();
    this.heuristic_value = this.heuristic();
    // this.hash = hash;
    // BoardState.visited_states.put(this.hash, this);

  }

  private static byte generateKey (TileState[][] board) {
    StringBuilder hash_string = new StringBuilder();
    for (int i = 0; i < board.length; i++) {
      for (int j = 0; j < board[i].length; j++) {
        if (board[i][j] == TileState.X) {
          char y;
          if (j > 2) {
            y = (char)(j + 'a');
          } else {
            y = (char)j;
          }
          hash_string.append(i + "" + y);
        } else if (board[i][j] == TileState.O) {
          char y;
          if (i > 2) {
            y = (char)(i + 'a');
          } else {
            y = (char)i;
          }
          hash_string.append(y + "" + j);
        }
      }
    }
    String s = hash_string.toString();
    AI.outl(s);
    byte b = Byte.parseByte(s);
    AI.outl(b + "");
    return b;
  }

  private static TileState[][] generateEmptyBoard () {
    TileState[][] board = new TileState[8][8];
    for (int i = 0; i < board.length; i++) {
      for (int n = 0; n < board[i].length; n++) {
        board[i][n] = TileState.EMPTY;
      }
    }
    return board;
  }

  public BoardState copyBoardWithMove (TileState player, int col, int row) {
    if (player == TileState.EMPTY) {
      throw new Error("TileState.EMPTY is not a valid player");
    } else if (col < 0 || col >= board.length) {
      throw new Error(String.format("col %s is outside of board", col));
    } else if (row < 0 || row >= board[col].length) {
      throw new Error(String.format("row %s is outside of board", row));
    } else if (board[col][row] != TileState.EMPTY) {
      throw new Error("Somebody is already occupying that spot.");
    }

    TileState[][] updated_board = new TileState[this.board.length][];
    for (int i = 0; i < this.board.length; i++) {
      updated_board[i] = this.board[i].clone();
    }

    updated_board[col][row] = player;
    byte hash = 0; //generateKey(updated_board);
    // BoardState new_board = visited_states.get(hash);
    // if (new_board == null) {
    //   new_board = new BoardState(updated_board, player, col, row, this.depth + 1, hash);
    // }

    BoardState new_board = new BoardState(updated_board, player, col, row, this.depth + 1, hash);
    this.children.offer(new_board);
    return new_board;
  }

  // Return whether or not this is a winning board
  private boolean terminalTest () {
    int x = this.last_move_x;
    int y = this.last_move_y;
    TileState player = this.board[x][y];

    // Move all the way to the left
    int i = x;
    while (--i != -1 && this.board[i][y] == player);
    // Check if the four to the right are the same
    if (i < 4) {
      if (this.board[i + 1][y] == player &&
          this.board[i + 2][y] == player &&
          this.board[i + 3][y] == player &&
          this.board[i + 4][y] == player) {
        return true;
      }
    }

    // Move all the way up
    i = y;
    while (--i != -1 && this.board[x][i] == player);
    // Check if the four down are the same
    if (i < 4) {
      if (this.board[x][i + 1] == player &&
          this.board[x][i + 2] == player &&
          this.board[x][i + 3] == player &&
          this.board[x][i + 4] == player) {
        return true;
      }
    }

    return false;
  }

  private int heuristic () {
    TileState player = this.last_player == TileState.X ? TileState.O : TileState.X;
    int score = 0;

    final Pattern[] foundPatterns = this.findPatterns(player, AI.patterns);
    for (Pattern p : foundPatterns) {
      score += p.value;
    }

    return score;
  }

  Pattern[] findPatterns (TileState player, Pattern[] patterns) {
    // System.out.println("Testing board:");
    // System.out.println(this);

    LinkedList<BoardTile> playerStones = new LinkedList<>();
    LinkedList<BoardTile> opponentStones = new LinkedList<>();

    for (int x = 0; x < this.board.length; x++) {
      for (int y = 0; y < this.board[x].length; y++) {
        if (this.board[x][y] == player) {
          playerStones.add(new BoardTile(x, y, AbstractTileState.PLAYER));
        } else if (this.board[x][y] != TileState.EMPTY) {
          opponentStones.add(new BoardTile(x, y, AbstractTileState.OPPONENT));
        }
      }
    }

    LinkedList<Pattern> foundPatterns = new LinkedList<>();

    for (BoardTile centerStone : playerStones) {
      for (Pattern pattern : patterns) {
        boolean match = true;
        for (int i = 0; i < pattern.points.length; i++) {
          TileState tile;
          try {
            tile = this.board[centerStone.x + pattern.points[i].x][centerStone.y + pattern.points[i].y];
          } catch (ArrayIndexOutOfBoundsException error) {
            match = false;
            break;
          }

          if (tile == player && pattern.points[i].state == AbstractTileState.PLAYER) {
            continue;
          } else if (tile == TileState.EMPTY && pattern.points[i].state == AbstractTileState.EMPTY) {
            continue;
          } else if (tile != player && pattern.points[i].state == AbstractTileState.OPPONENT) {
            continue;
          } else {
            match = false;
            break;
          }
        }
        if (match) {
          // System.out.println("Matched pattern:");
          // System.out.println(this);
          // System.out.println(pattern);
          foundPatterns.add(pattern);
        }
      }
    }

    // int score = 0;
    // for (Pattern p : foundPatterns) {
    //   score += p.value;
    // }
    // if (score > 0) {
    //   System.out.println(String.format("Score: %s", score));
    // }

    return foundPatterns.toArray(new Pattern[0]);
  }

  @Override
  public String toString () {
    String out = "  ";

    // print column headers
    for (int i = 1; i <= board[0].length; i++) {
      out += ANSIColors.BLUE + i + " ";
    }
    out += "\n";

    for (int i = 0; i < board.length; i++) {
      // print row leader
      out += ANSIColors.PURPLE + (char)(65 + i) + " ";

      for (int n = 0; n < board[i].length; n++) {
        switch (board[i][n]) {
          case X:
            out += String.format("%s✕ ", ANSIColors.GREEN);
            break;
          case O:
            out += String.format("%s◯ ", ANSIColors.RED);
            break;
          case EMPTY:
            out += String.format("%s∙ ", ANSIColors.BLACK_BRIGHT);
            break;
        }
      }
      out += ANSIColors.RESET + "\n";
    }

    return out;
  }

  @Override
  public int compareTo(BoardState state) {
    return state.heuristic_value - this.heuristic_value;
  }
}
