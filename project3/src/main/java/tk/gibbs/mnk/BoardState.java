package tk.gibbs.mnk;

public class BoardState {
  final public TileState[][] board;
  final public String hash;
  final public int heuristicVal;
  final public int last_move_x;
  final public int last_move_y;
  final public int depth;

  // TileState[][] getBoard () { return board; }
  // String getHash () { return hash; }
  // int getHeuristicVal () { return heuristicVal; }
  // int getLastMoveX () { return last_move_x; }
  // int getLastMoveY () { return last_move_y; }

  BoardState () {
    // Initilaize with empty board
    this(generateEmptyBoard());
  }

  BoardState (TileState[][] board) {
    this(board, 0, 0, 0);
  }

  BoardState (TileState[][] board, int last_move_x, int last_move_y, int depth) {
    this.board = board;
    this.last_move_x = 0;
    this.last_move_y = 0;
    this.depth = depth;
    // TODO: calculate heuristicVal & hash
    this.heuristicVal = 0;
    this.hash = "hash";
  }

  static TileState[][] generateEmptyBoard () {
    TileState[][] board = new TileState[8][8];
    for (int i = 0; i < board.length; i++) {
      for (int n = 0; n < board[i].length; n++) {
        board[i][n] = TileState.EMPTY;
      }
    }
    return board;
  }

  BoardState copyBoardWithMove (TileState player, int col, int row) {
    assert player != TileState.EMPTY;
    // } else if (col < 0 || col >= board.length) {
    //   throw new Error(String.format("col %s is outside of board", col));
    // } else if (row < 0 || row >= board[col].length) {
    //   throw new Error(String.format("row %s is outside of board", row));
    // }

    TileState[][] updatedBoard = new TileState[this.board.length][];
    for (int i = 0; i < this.board.length; i++) {
      updatedBoard[i] = this.board[i].clone();
    }

    updatedBoard[col][row] = player;

    return new BoardState(updatedBoard, col, row, this.depth + 1);
  }

  @Override
  public String toString () {
    String out = "  ";

    // print column headers
    for (int i = 1; i <= board[0].length; i++) {
      out += ANSIColors.BLUE + i + ANSIColors.RESET + " ";
    }
    out += "\n";

    for (int i = 0; i < board.length; i++) {
      // print row leader
      out += ANSIColors.PURPLE + (char)(65 + i) + ANSIColors.RESET + " ";

      for (int n = 0; n < board[i].length; n++) {
        switch (board[i][n]) {
          case X:
            out += String.format("%s✕%s ", ANSIColors.RED, ANSIColors.RESET);
            break;
          case O:
            out += String.format("%s◯%s ", ANSIColors.GREEN, ANSIColors.RESET);
            break;
          case EMPTY:
            out += String.format("%s∙%s ", ANSIColors.BLACK_BRIGHT, ANSIColors.RESET);
            break;
        }
      }
      out += "\n";
    }

    return out;
  }
}
