import java.util.Collections;
import java.util.Arrays;
import java.util.Random;
import java.util.PriorityQueue;

public class Genetic {
  private final int MAX_GENERATIONS = 5000;
  public long cpu_time = 0;
  public int generations = 0;
  public final Node solution;

  public Genetic (Node[] initial_states, double mutation_probability) {
    long time = System.nanoTime() / 1000000;
    this.solution = this.search(initial_states, mutation_probability);
    this.cpu_time = System.nanoTime() / 1000000 - time;
  }

  // Begins the genetic algorithm search
  private Node search (Node[] initial_states, double mutation_probability) {
    PriorityQueue<Node> population = new PriorityQueue<>(Arrays.asList(initial_states));
    return this.geneticAlgorithm(population, mutation_probability);
  }

  // Select, reproduce, and mutate genes
  private Node geneticAlgorithm (PriorityQueue<Node> population, double mutation_probability) {
    Random random = new Random();
    do {
      PriorityQueue<Node> new_population = new PriorityQueue<>();
      for (int i = 1; i < population.size(); i++) {
        Node x = population.poll();
        Node y = population.peek();
        Node[] child = this.reproduce(x, y);
        if (random.nextDouble() < mutation_probability) {
          child[0] = mutate(child[0]);
        }
        if (random.nextDouble() < mutation_probability) {
          child[1] = mutate(child[1]);
        }
        new_population.offer(child[0]);       
        new_population.offer(child[1]);       
      } 
      population = new_population;
    } while (++this.generations < this.MAX_GENERATIONS && population.peek().cost != 0);

    return population.poll();
  }

  // Change the position of one queen
  private Node mutate (Node node) {
    Random random = new Random();
    int[] mutated = Arrays.copyOf(node.board, node.board.length);
    final int position = random.nextInt(mutated.length);
    mutated[position] = random.nextInt(mutated.length);
    return new Node(mutated);
  }

  // Swap some portion of x with the rest of y
  private Node[] reproduce (Node x, Node y) {
    final int n = x.board.length;
    final int c = (new Random()).nextInt(n);

    int[] x_board = new int[n];
    System.arraycopy(x.board, 0, x_board, 0, c);
    System.arraycopy(y.board, c, x_board, c, n - c);
    Node x_node = new Node(x_board);

    int[] y_board = new int[n];
    System.arraycopy(y.board, 0, y_board, 0, c);
    System.arraycopy(x.board, c, y_board, c, n - c);
    Node y_node = new Node(y_board);

    return new Node[] {x_node, y_node};
  }

}
