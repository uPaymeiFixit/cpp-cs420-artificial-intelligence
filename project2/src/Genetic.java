import java.util.PriorityQueue;
import java.util.Collections;
import java.util.HashSet;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.Arrays;
import java.util.Random;

public class Genetic {
  public int nodes_created = 0;
  public long cpu_time = 0;
  public int generation = 0;


  public Genetic (Node[] initial_states, double mutation_probability) {

    long time = System.nanoTime() / 1000000;

    ArrayList<Node> nodes = new ArrayList<>(Arrays.asList(initial_states));
    search(nodes, mutation_probability);

    this.cpu_time = System.nanoTime() / 1000000 - time;
  }

  // private Node geneticAlgorithm (PriorityQueue<Node> population) {
  //   do {
  //     ArrayList<Node> new_population = new ArrayList<>();
  //     for (int i = 0; i < population.size(); i++) {
  //       int x = random_selection(population, fitness_function);
  //       int y = random_selection(population, fitness_function);
  //       child = reproduce(x, y);
  //       if (small random probability) {
  //         child = mutate(child);
  //       }
  //       add child to new_population
  //     }
  //     population = new_population;
  //   } while (some individual is fit enough or enough time has elapsed)

  //   return the best individual in population according to fitnessFN
  // }


  private Node mutate (Node node, double probability) {
    Random random = new Random();
    if (probability < random.nextDouble()) {
      return node;
    }

    int[] mutated = Arrays.copyOf(node.board, node.board.length);
    final int position = random.nextInt(mutated.length);
    mutated[position] = random.nextInt(mutated.length);
    return new Node(mutated);
  }

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

  public void search (ArrayList<Node> initial_states, double mutation_probability) {

    Node[] crossover;
    Node highest_fitness;
    ArrayList<Node> crossed_over = new ArrayList<>(initial_states.size());
    ArrayList<Node> best_of_current_generation;
    ArrayList<Node> next_generation = initial_states;

    int generations = 0;
    while (true) {
      best_of_current_generation = this.select(next_generation);
      for (Node node : best_of_current_generation) {
        if (node.cost == 0) {
          System.out.println("FOUND A SOLUTION!");
          System.out.println("Generations: " + this.generation);
          System.out.println(node);
          return;
        }
      }
      crossed_over.clear();
      highest_fitness = best_of_current_generation.get(0);
      for (int i = 1; i < best_of_current_generation.size(); i++) {
        crossover = this.reproduce(highest_fitness, best_of_current_generation.get(i));
        crossed_over.add(crossover[0]);
        crossed_over.add(crossover[1]);
      }
      next_generation.clear();
      for (Node node : crossed_over) {
        next_generation.add(this.mutate(node, mutation_probability));
      }
      generation++;

    }
  }

  private static ArrayList<Node> select (ArrayList<Node> pool) {
    Collections.sort(pool);
    ArrayList<Node> selection = new ArrayList<>();
    for (int i = 0; i <= pool.size() >> 1; i++) {
      selection.add(pool.get(i));
    }
    return selection;
  }



  // private ArrayList<Node> getSolution (Node node) {
  //   solution.add(node);
  //   while (node.parent_node != null) {
  //     solution.add(node.parent_node);
  //     node = node.parent_node;
  //   }
  //   this.step_cost = solution.size() - 1;
  //   return solution;
  // }

  // public void printSolution () {
  //   Collections.reverse(solution);
  //   for (Node node : solution) {
  //     node.print();
  //   }
  // }

  // private void pause () {
  //   (new Scanner(System.in)).nextLine();
  // }

}
