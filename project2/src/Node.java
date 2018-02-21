public class Node implements Comparable<Node> {
  public final int[] board;
  public final int cost;

  public Node (int[] board) {
    this.board = board;
    this.cost = countAttacking(board);
  }

  private static int countAttacking (int[] board) {
    int count = 0;
    for (int i = 0; i < board.length; i++) {
      for (int j = 0; j < i; j++) {
        if (board[j] == board[i] ||
            board[j] - board[i] == i - j ||
            board[i] - board[j] == i - j) {
          count++;
        }
      }
    }
    return count;
  }

  @Override
  public String toString () {
    StringBuilder string_builder = new StringBuilder();
    for (int i = 0; i < this.board.length; i++) {
      for (int j = 0; j < this.board.length; j++) {
        if (this.board[i] == j) {
          string_builder.append("🔴 ");
        } else {
          if ((i + j) % 2 == 0) {
            string_builder.append("⬜️ ");
          } else {
            string_builder.append("⬛️ ");
          }
        }

      }
      string_builder.append('\n');
    }
    return string_builder.toString();
  }

  @Override
  public int compareTo (Node node) {
    return this.cost - node.cost;
  }

}
