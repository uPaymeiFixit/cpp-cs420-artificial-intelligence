import java.util.Arrays;
import java.util.PriorityQueue;

public class RandomRestartHillClimbing {
  private final int MAX_RESTARTS = 25;
  public int nodes_created = 0;
  public long cpu_time = 0;

  public RandomRestartHillClimbing (int n) {
    long time = System.nanoTime() / 1000000;
    Node solution = this.search(n);
    this.cpu_time = System.nanoTime() / 1000000 - time;

    System.out.println(solution);
  }

  // Begins the random restart hill climbing algorithm
  private Node search (int n) {
    Node best = new Node(n);
    // Restart until we have solved the puzzle maxed restarts
    for (int restarts = 0; restarts < MAX_RESTARTS; restarts++) {
      // Find a local max
      Node local_max = this.hillClimbing(new Node(n));
      if (local_max.cost < best.cost) {
        best = local_max;
      }
      if (best.cost == 0) {
        break;
      }
    }
    return best;
  }

  // Begins searching for a local max
  public Node hillClimbing (Node current) {
    while (true) {
      Node neighbor = this.generateSuccessors(current).poll();
      if (current.cost <= neighbor.cost) {
        return current;
      }
      current = neighbor;
    }
  }

  // Returns a priority queue of generated nodes
  public PriorityQueue<Node> generateSuccessors (Node current) {
    int[] board = Arrays.copyOf(current.board, current.board.length);
    PriorityQueue<Node> successors = new PriorityQueue<>();
    for (int i = 0; i < board.length; i++) {
      int original_value = board[i];
      for (int j = 0; j < board.length; j++) {
        if (j == original_value) {
          continue;
        }
        board[i] = j;
        int[] copy = Arrays.copyOf(board, board.length);
        successors.offer(new Node(copy));
      }
      board[i] = original_value;
    }
    return successors;
  }

}
