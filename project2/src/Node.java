import java.util.Random;

public class Node implements Comparable<Node> {
  public final int[] board;
  public final int cost;
  public static int nodes_created = 0;

  // Initialize our node with a board and cost
  public Node (int[] board) {
    this.board = board;
    this.cost = countAttacking(board);
    this.nodes_created++;
  }

  public Node (int n) {
    this(generateBoard(n));
  }

  // Generate a random board
  // Returns array of length n
  // Each array placeholder represents a column
  // Each value represents a row
  // For example, [1, 3, 0, 2] can be represented as:
  //     . . Q .
  //     Q . . .
  //     . . . Q
  //     . Q . .
  private static int[] generateBoard (int n) {
    int[] board = new int[n];
    Random random = new Random();
    for (int i = 0; i < n; i++) {
      board[i] = random.nextInt(n);
    }

    return board;
  }

  // Count how many queens can attack each other
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
