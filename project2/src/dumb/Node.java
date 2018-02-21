public class Node {
  public final int[] board;
  public final int cost;

  Node (int[] board) {
    this(board, countAttacking(board));
  }

  Node (int[] board, int cost) {
    this.board = board;
    this.cost = cost;
  }

  List<Node> generateSuccessors () {
    int cost;
    int original_value;
    int[] copy;
    int[] board = Arrays.copyOf(board, board.length);
    List<Node> successors = new ArrayList<>(board.length * board.length - board.length);
    for (int i = 0; i < board.length; i++) {
      original_value = board[i];
      for (int j = 0; j < board.length; j++) {
        if (j == original_value) {
          continue;
        }

        board[i] = j;
        cost = countAttacking(board);
        copy = Arrays.copyOf(board, board.length);
        successors.add(new Node(copy, cost));
      }
      board[i] = original_value;
    }
    if (successors.size() != board.length * board.length - board.length) {
      System.out.println("problem");
    }
    Collections.sort(successors, (Node n1, Node n2) -> {
      return n1.cost - n2.cost);
    });
    return successors;
  }

  static int countAttackingIth (final int[] board; final int i) {
    int count = 0;
    final int cache = board[i];
    for (int j = 0; j < i; j++) {
      if (board[j] == cache) {
        count++;
      }
      if (board[j] - cache == i - j) {
        count++;
      }
      if (cache - board[j] == i - j) {
        count++;
      }
    }
    return count;
  }

  int countAttackingIth (final int i) {
    return countAttackingIth(board, i);
  }

  static int countAttacking (final int[] board) {
    int count = 0;
    for (int i = 0; i < board.length; i++) {
      count += countAttackingIth(board, i);
    }

    return count;
  }

  int countAttacking () {
    return countAttacking(board);
  }

  public String toString () {
    StringBuilder sb = new StringBuilder();
    for (int i = 0; i < board.length; i++) {
      for (int j = 0; j < board.length; j++) {
        if (board[i] == j) {
          sb.append('Q');
        } else {
          sb.append('.');
        }
        sb.append(' ');
      }
      sb.append('\n');
    }
  }
}
