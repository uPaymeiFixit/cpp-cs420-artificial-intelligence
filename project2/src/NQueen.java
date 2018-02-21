import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;
import java.util.Scanner;
import java.io.InputStream;

public class NQueen {

  private static final boolean DEBUG = true;

  public static void main (String[] args) {
    if (DEBUG) {
      printStats(2, 8, 3, 25, 0.5);
    } else {
      final int[] size = askSizeRange();
      final int trials = askTrials();
      final double mutation_probability = askProbability();
      final int population_size = askPopulationSize();

      printStats(size[0], size[1], trials, population_size, mutation_probability);
    }
  }

  private static void printSolutions (int smallest_n, Node[][] genetic, Node[][] rrhc) {
    System.out.println("┌────┬───────────────────────────────────┬───────────────────────────────────┐");
    System.out.println("│ N  │   Random Restart Hill Climbing    │         Genetic Algorithm         │");
    System.out.println("├────┼───────────────────────────────────┼───────────────────────────────────┤");
    for (int i = 0; i < genetic.length; i++) {
      int n = i + smallest_n;

      String left_space = leftPad((int)(35 - 2 * n) / 2, ' ');
      String right_space = leftPad((int)(35 - 2 * n + 1) / 2, ' ');
      System.out.println("│    │                                   │                                   │");
      // Print first three solutions side by side
      for (int j = 0; j < 3; j++) {
        String[] gen_lines = genetic[i][j].toString().split("\n");
        String[] rrhc_lines = rrhc[i][j].toString().split("\n");
        for (int l = 0; l < n; l++) {
          // Print left bar + N
          if (j == 0 && l == 1) {
            System.out.printf("│ %2d │", n);
          } else {
            System.out.print("│    │");
          }
          System.out.print(left_space + gen_lines[l] + right_space + '│' + left_space + rrhc_lines[l] + right_space + "│\n");
        }
        System.out.println("│    │                                   │                                   │");
      }

      if (i < genetic.length - 1) {
        System.out.println("├────┼───────────────────────────────────┼───────────────────────────────────┤");
      } else {
        System.out.println("└────┴───────────────────────────────────┴───────────────────────────────────┘");
      }

    }
  }

  private static String leftPad (int n, char c) {
    StringBuilder string_builder = new StringBuilder();
    for (int i = 0; i < n; i++) {
      string_builder.append(c);
    }
    return string_builder.toString();
  }

  private static void printStats (int min, int max, int trials, int population_size, double mutation_probability) {
    Stats gen_stats = getGeneticSolution(min, max, trials, population_size, mutation_probability);
    Stats rrhc_stats = getRRHCSolution(min, max, trials);

    printSolutions(min, gen_stats.solutions, rrhc_stats.solutions);

    System.out.println("┌────┬─────────────────────┬─────────────────┬─────────────────┬─────────────┐");
    System.out.println("│    │   Nodes Generated   │    Run Time     │ Restarts / Gens │  % Solved   │");
    System.out.println("│ N  ├──────────┬──────────┼────────┬────────┼────────┬────────┼──────┬──────┤");
    System.out.println("│    │   RRHC   │    Gen   │  RRHC  │  Gen   │  RRHC  │  Gen   │ RRHC │ Gen  │");
    System.out.println("├────┼──────────┼──────────┼────────┼────────┼────────┼────────┼──────┼──────┤");
    for (int d = 0; d < max - min + 1; d++) {
      System.out.printf("│ %2d ", d + min);
      System.out.printf("│ %8.0f ", rrhc_stats.average_nodes_created[d]);
      System.out.printf("│ %8.0f ", gen_stats.average_nodes_created[d]);
      System.out.printf("│ %4dms ", rrhc_stats.average_cpu_time[d]);
      System.out.printf("│ %4dms ", gen_stats.average_cpu_time[d]);
      System.out.printf("│ %6.0f ", rrhc_stats.average_restarts[d]);
      System.out.printf("│ %6.0f ", gen_stats.average_generations[d]);
      System.out.printf("│ %3.0f%% ", rrhc_stats.percent_solved[d] * 100);
      System.out.printf("│ %3.0f%% │\n", gen_stats.percent_solved[d] * 100);
    }
    System.out.println("└────┴──────────┴──────────┴────────┴────────┴────────┴────────┴──────┴──────┘");
  }

  private static Stats getGeneticSolution (int min, int max, int trials, int population_size, double mutation_probability) {
    Stats stats = new Stats(max - min + 1);

    for (int n = min; n <= max; n++) {
      for (int i = 0; i < trials; i++) {
        // Generate a random population
        Node[] population = new Node[population_size];
        for (int j = 0; j < population_size; j++) {
          population[j] = new Node(n);
        }

        Genetic genetic = new Genetic(population, mutation_probability);
        stats.average_cpu_time[n - min] += genetic.cpu_time;
        stats.average_generations[n - min] += genetic.generations;
        if (genetic.solution.cost == 0) {
          stats.percent_solved[n - min]++;
        }
        if (i < 3) {
          stats.solutions[n - min][i] = genetic.solution;
        }
      }
      stats.average_cpu_time[n - min] /= trials;
      stats.average_generations[n - min] /= trials;
      stats.percent_solved[n - min] /= trials;
      stats.average_nodes_created[n - min] = Node.nodes_created / trials;
      Node.nodes_created = 0;
    }

    return stats;
  }

  private static Stats getRRHCSolution (int min, int max, int trials) {
    Stats stats = new Stats(max - min + 1);

    for (int n = min; n <= max; n++) {
      for (int i = 0; i < trials; i++) {
        RandomRestartHillClimbing rrhc = new RandomRestartHillClimbing(n);
        stats.average_cpu_time[n - min] += rrhc.cpu_time;
        stats.average_restarts[n - min] += rrhc.restarts;
        if (rrhc.solution.cost == 0) {
          stats.percent_solved[n - min]++;
        }
        if (i < 3) {
          stats.solutions[n - min][i] = rrhc.solution;
        }
      }
      stats.average_cpu_time[n - min] /= trials;
      stats.average_restarts[n - min] /= trials;
      stats.percent_solved[n - min] /= trials;
      stats.average_nodes_created[n - min] = Node.nodes_created / trials;
      Node.nodes_created = 0;
    }

    return stats;
  }

  private static int askTrials () {
    Scanner scanner = new Scanner(System.in);
    int n = 0;
    while (true) {
      System.out.print("How many random boards would you like to test?\n: ");
      while (!scanner.hasNextInt()) scanner.next();
      n = scanner.nextInt();
      if (n > 0) {
        return n;
      }
      System.out.println("Choose a number greater than 0!");
    }
  }

  private static double askProbability () {
    Scanner scanner = new Scanner(System.in);
    double n = 0;
    while (true) {
      System.out.print("What is the probability a mutation occurs?\n: ");
      while (!scanner.hasNextDouble()) scanner.next();
      n = scanner.nextDouble();
      if (n >= 0 && n <= 1) {
        return n;
      }
      System.out.println("Choose a number between 0 and 1!");
    }
  }

  private static int askPopulationSize () {
    Scanner scanner = new Scanner(System.in);
    int n = 0;
    while (true) {
      System.out.print("How large should the initial genetic algorithm population be?\n: ");
      while (!scanner.hasNextInt()) scanner.next();
      n = scanner.nextInt();
      if (n > 0) {
        return n;
      }
      System.out.println("Choose a number greater than 0!");
    }
  }

  private static int[] askSizeRange () {
    Scanner scanner = new Scanner(System.in);
    int start = 0;
    while (true) {
      System.out.print("Enter the smallest board size you want to solve:\n: ");
      while (!scanner.hasNextInt()) scanner.next();
      start = scanner.nextInt();
      if (start > 0) {
        break;
      }
      System.out.println("Choose a number greater than 0!");
    }

    int end = 0;
    while (true) {
      System.out.print("Enter the largest board size you want to solve:\n: ");
      while (!scanner.hasNextInt()) scanner.next();
      end = scanner.nextInt();
      if (end > 0) {
        break;
      }
      System.out.println("Choose a number greater than 0!");
    }

    return new int[] {start, end};
  }

}

class Stats {
  public long[] average_cpu_time;
  public double[] average_nodes_created;
  public double[] average_generations;
  public double[] percent_solved;
  public double[] average_restarts;
  public Node[][] solutions;

  public Stats (int n) {
    this.average_cpu_time = new long[n];
    this.average_nodes_created = new double[n];
    this.average_generations = new double[n];
    this.percent_solved = new double[n];
    this.average_restarts = new double[n];
    this.solutions = new Node[n][3];
  }
}
