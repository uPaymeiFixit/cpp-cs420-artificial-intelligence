public class Node implements Comparable<Node> {
  private int[] board; // This is our state
  public Node parent_node;
  public int moves; // This is our depth (path cost, g(n))
  public int total_cost; // This is our f(n) = g(n) + h(n)
  public int key; 

  public Node (int[] board, Node parent_node, int moves) {
    this.board = board;
    this.parent_node = parent_node;
    this.moves = moves;
    this.key = generateKey();
  }

  // Move blank tile up
  public Node actionUp () {
    int size = (int) Math.sqrt(this.board.length);
    int index_of_blank = indexOfZero();
    // Check if blank is already at top
    if (index_of_blank < size) {
      return null;
    }
    int index_of_above = index_of_blank - size;
    int[] new_board = this.board.clone();

    // Swap blank tile with tile above it
    new_board[index_of_blank] = new_board[index_of_above];
    new_board[index_of_above] = 0;
    return new Node(new_board, this, moves + 1);
  }

  // Move blank tile down
  public Node actionDown () {
    int size = (int) Math.sqrt(this.board.length);
    int index_of_blank = indexOfZero();
    // Check if blank is already at bottom
    if (index_of_blank >= this.board.length - size) {
      return null;
    }
    int index_of_below = index_of_blank + size;
    int[] new_board = this.board.clone();

    // Swap blank tile with tile above it
    new_board[index_of_blank] = new_board[index_of_below];
    new_board[index_of_below] = 0;
    return new Node(new_board, this, moves + 1);
  }

  // Move blank tile left
  public Node actionLeft () {
    int size = (int) Math.sqrt(this.board.length);
    int index_of_blank = indexOfZero();
    // Check if blank is already at left
    if (index_of_blank % size == 0) {
      return null;
    }
    int index_of_left = index_of_blank - 1;
    int[] new_board = this.board.clone();

    // Swap blank tile with tile above it
    new_board[index_of_blank] = new_board[index_of_left];
    new_board[index_of_left] = 0;
    return new Node(new_board, this, moves + 1);
  }

  // Move blank tile right
  public Node actionRight () {
    int size = (int) Math.sqrt(this.board.length);
    int index_of_blank = indexOfZero();
    // Check if blank is already at right
    if (index_of_blank % size == size - 1) {
      return null;
    }
    int index_of_right = index_of_blank + 1;
    int[] new_board = this.board.clone();

    // Swap blank tile with tile above it
    new_board[index_of_blank] = new_board[index_of_right];
    new_board[index_of_right] = 0;
    return new Node(new_board, this, moves + 1);
  }

  // Return index of blank tile
  private int indexOfZero () {
    for (int i = 0; i < this.board.length; i++) {
      if (this.board[i] == 0) {
        return i;
      }
    }
    return -1;
  }

  // Returns number of tiles not in goal state except for blank tile
  public int heuristic1 () {
    int misplaced_tiles = 0;
    for (int i = 1; i < this.board.length; i++) {
      if (this.board[i] != i) {
        misplaced_tiles++;
      }
    }
    return misplaced_tiles;
  }

  // Return sum of distances tiles are from their goal states
  public int heuristic2 () {
    int total_distance = 0;
    for (int i = 0; i < this.board.length; i++) {
      int row_distance = Math.abs(i / 3 - this.board[i] / 3);
      int col_distance = Math.abs(i % 3 - this.board[i] / 3);
      total_distance += row_distance + col_distance;
    }
    return total_distance;
  }

  // Print state
  public void print () {
    System.out.println("┌───────┐");
    for (int i = 0; i < 3; i++) {
      System.out.print("│ ");
      for (int j = 0; j < 3; j++) {
        if (this.board[3 * i + j] == 0) {
          System.out.print("  ");
        } else {
          System.out.print(this.board[3 * i + j] + " ");
        }
      }
      System.out.println("│");
    }
    System.out.println("└───────┘");
  }

  // Detect goal state
  public boolean isGoalState () {
    int offset = 1;
    for (int i = 0; i < this.board.length; i++) {
      if (this.board[i] == 0) {
        offset = 0;
      }
      if (this.board[i] != i + offset) {
        return false;
      }
    }
    return true;
  }

  @Override
  public int compareTo (Node node) {
    return this.total_cost - node.total_cost;
  }

  // Generate integer unique to this state
  private int generateKey () {
    String unique_key = "";
    for (int tile : this.board) {
      unique_key += tile;
    }
    return Integer.parseInt(unique_key);
  }


}
