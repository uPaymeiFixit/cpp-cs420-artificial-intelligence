package tk.gibbs.mnk;

public class BoardState {
  public TileState[][] board = new TileState[8][8];
  public String hash;
  public int heuristicVal;

  BoardState () {
    // Initilaize empty board
    for (int i = 0; i < board.length; i++) {
      for (int n = 0; n < board[i].length; n++) {
        board[i][n] = TileState.EMPTY;
      }
    }
  }

  BoardState (TileState[][] board) {
    this.board = board;
    // TODO: calculate heuristicVal & hash
  }

  BoardState copyBoardWithMove (TileState player, int col, int row) {
    if (player == TileState.EMPTY) {
      throw new Error("TileState.EMPTY is not a valid player");
    } else if (col < 0 || col >= board.length) {
      throw new Error(String.format("col %s is outside of board", col));
    } else if (row < 0 || row >= board[col].length) {
      throw new Error(String.format("row %s is outside of board", row));
    }

    TileState[][] updatedBoard = new TileState[this.board.length][];
    for (int i = 0; i < this.board.length; i++) {
      updatedBoard[i] = this.board[i].clone();
    }

    updatedBoard[col][row] = player;

    return new BoardState(updatedBoard);
  }

  @Override
  public String toString () {
    String out = "  ";

    // print column headers
    for (int i = 0; i < board[0].length; i++) {
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
