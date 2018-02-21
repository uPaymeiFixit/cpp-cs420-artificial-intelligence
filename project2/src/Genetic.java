import java.util.Collections;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

public class Genetic {
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
    return this.geneticAlgorithm(initial_states, mutation_probability);
  }

  private Node geneticAlgorithm (Node[] population, double mutation_probability) {
    ArrayList<Node> new_population = new ArrayList<>(Arrays.asList(population));
    while (true) {
      ArrayList<Node> best_of_current_generation = this.randomSelection(new_population);
      for (Node node : best_of_current_generation) {
        if (node.cost == 0) {
          return node;
        }
      }
      ArrayList<Node> crossed_over = new ArrayList<>(population.length);
      Node highest_fitness = best_of_current_generation.get(0);
      for (int i = 0; i < best_of_current_generation.size(); i++) {
        Node[] children = this.reproduce(highest_fitness, best_of_current_generation.get(i));
        crossed_over.add(children[0]);
        crossed_over.add(children[1]);
      }
      new_population.clear();
      for (Node node : crossed_over) {
        if ((new Random()).nextDouble() < mutation_probability) {
          node = this.mutate(node);
        }
        new_population.add(node);
      }
      this.generations++;
    }
  }

  // private Node geneticAlgorithm (Node[] population, double mutation_probability) {
  //   Node[] new_population = new Node[population.length];
  //   while (true) {
  //     for (int i = 0; i < population.length; i++) {
  //       if (population[i].cost == 0) {
  //         return population[i];
  //       }
  //       Node x = this.randomSelection(population);
  //       Node y = this.randomSelection(population);
  //       Node child = this.reproduce(x, y)[0];
  //       if ((new Random()).nextDouble() < mutation_probability) {
  //         child = this.mutate(child);
  //       }
  //       new_population[i] = child;
  //     }
  //     population = new_population;
  //     this.generations++;
  //   }
  // }

  // // Randomly select a node, but favor fit nodes
  // private Node randomSelection (Node[] population) {
  //   int sum_cost = sumCost(population);
  //   int random = (new Random()).nextInt(sum_cost);
  //   int sum = 0;
  //   for (int i = 0; i < population.length; i++) {
  //     sum += population[i].cost;
  //     if (sum >= random) {
  //       return population[i];
  //     }
  //   }
  //   return null;
  // }

  // // Sum of the costs of the population
  // private int sumCost (Node[] population) {
  //   int sum = 0;
  //   for (Node node : population) {
  //     sum += node.cost;
  //   }
  //   return sum;
  // }

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

  private static ArrayList<Node> randomSelection (ArrayList<Node> pool) {
    Collections.sort(pool);
    ArrayList<Node> selection = new ArrayList<>();
    for (int i = 0; i <= pool.size() >> 1; i++) {
      selection.add(pool.get(i));
    }
    return selection;
  }

}
