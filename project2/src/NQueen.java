import java.util.Random;
import java.util.Scanner;
import java.io.FileNotFoundException;
import java.io.PrintWriter;

public class NQueen {

  public static void main (String[] args) throws FileNotFoundException {
    final int[] size = askSizeRange();
    final int trials = askTrials();
    final double mutation_probability = askProbability();
    final int population_size = askPopulationSize();
    final String output_file_name = askFileName();

    String output = printStats(size[0], size[1], trials, population_size, mutation_probability);
    System.out.print(output);
    if (output_file_name != null) {
      outputToFile(output_file_name, output);
    } 
  }

  private static void outputToFile (String file_name, String output) throws FileNotFoundException {
    PrintWriter print_writer = new PrintWriter(file_name);
    print_writer.print(output);
    print_writer.close();
  }

  private static String printSolutions (int smallest_n, Node[][] genetic, Node[][] rrhc) {
    StringBuilder string_builder = new StringBuilder();
    string_builder.append("┌────┬───────────────────────────────────┬───────────────────────────────────┐\n");
    string_builder.append("│ N  │   Random Restart Hill Climbing    │         Genetic Algorithm         │\n");
    string_builder.append("├────┼───────────────────────────────────┼───────────────────────────────────┤\n");
    for (int i = 0; i < genetic.length; i++) {
      int n = i + smallest_n;

      String left_space = leftPad((int)(35 - 2 * n) / 2, ' ');
      String right_space = leftPad((int)(35 - 2 * n + 1) / 2, ' ');
      string_builder.append("│    │                                   │                                   │\n");
      // Print first three solutions side by side
      for (int j = 0; j < genetic[i].length; j++) {
        if (genetic[i][j] == null) {
          break;
        }
        String[] rrhc_lines = rrhc[i][j].toString().split("\n");
        String[] gen_lines = genetic[i][j].toString().split("\n");
        for (int l = 0; l < n; l++) {
          // Print left bar + N
          if (j == 0 && l == 1) {
            string_builder.append(String.format("│ %2d │", n));
          } else {
            string_builder.append("│    │");
          }
          string_builder.append(left_space + rrhc_lines[l] + right_space + '│' + left_space + gen_lines[l] + right_space + "│\n");
        }
        string_builder.append("│    │                                   │                                   │\n");
      }

      if (i < genetic.length - 1) {
        string_builder.append("├────┼───────────────────────────────────┼───────────────────────────────────┤\n");
      } else {
        string_builder.append("└────┴───────────────────────────────────┴───────────────────────────────────┘\n");
      }

    }
    return string_builder.toString();
  }

  private static String leftPad (int n, char c) {
    StringBuilder string_builder = new StringBuilder();
    for (int i = 0; i < n; i++) {
      string_builder.append(c);
    }
    return string_builder.toString();
  }

  private static String printStats (int min, int max, int trials, int population_size, double mutation_probability) {
    Stats gen_stats = getGeneticSolution(min, max, trials, population_size, mutation_probability);
    Stats rrhc_stats = getRRHCSolution(min, max, trials);

    StringBuilder string_builder = new StringBuilder();
    String solutions = printSolutions(min, gen_stats.solutions, rrhc_stats.solutions);
    string_builder.append(solutions);

    string_builder.append("┌────┬─────────────────────┬─────────────────┬─────────────────┬─────────────┐\n");
    string_builder.append("│    │   Nodes Generated   │    Run Time     │ Restarts / Gens │  % Solved   │\n");
    string_builder.append("│ N  ├──────────┬──────────┼────────┬────────┼────────┬────────┼──────┬──────┤\n");
    string_builder.append("│    │   RRHC   │    Gen   │  RRHC  │  Gen   │  RRHC  │  Gen   │ RRHC │ Gen  │\n");
    string_builder.append("├────┼──────────┼──────────┼────────┼────────┼────────┼────────┼──────┼──────┤\n");
    for (int d = 0; d < max - min + 1; d++) {
      string_builder.append(String.format("│ %2d ", d + min));
      string_builder.append(String.format("│ %8.0f ", rrhc_stats.average_nodes_created[d]));
      string_builder.append(String.format("│ %8.0f ", gen_stats.average_nodes_created[d]));
      string_builder.append(String.format("│ %4dms ", rrhc_stats.average_cpu_time[d]));
      string_builder.append(String.format("│ %4dms ", gen_stats.average_cpu_time[d]));
      string_builder.append(String.format("│ %6.0f ", rrhc_stats.average_restarts[d]));
      string_builder.append(String.format("│ %6.0f ", gen_stats.average_generations[d]));
      string_builder.append(String.format("│ %3.0f%% ", rrhc_stats.percent_solved[d] * 100));
      string_builder.append(String.format("│ %3.0f%% │\n", gen_stats.percent_solved[d] * 100));
    }
    string_builder.append("└────┴──────────┴──────────┴────────┴────────┴────────┴────────┴──────┴──────┘\n");

    return string_builder.toString();
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

  private static String askFileName () {
    Scanner scanner = new Scanner(System.in);
    String n = "";
    System.out.print("Enter output file name\n: ");
    while (!scanner.hasNextLine()) scanner.next();
    n = scanner.next();
    if (n.length() == 0) {
      return "out.txt";
    }
    return n;
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
    int start = 1;
    while (true) {
      System.out.print("Enter the smallest board size you want to solve:\n: ");
      while (!scanner.hasNextInt()) scanner.next();
      start = scanner.nextInt();
      if (start > 1) {
        break;
      }
      System.out.println("Choose a number greater than 1!");
    }

    int end = start;
    while (true) {
      System.out.print("Enter the largest board size you want to solve:\n: ");
      while (!scanner.hasNextInt()) scanner.next();
      end = scanner.nextInt();
      if (end >= start) {
        break;
      }
      System.out.println("Choose a number greater than or equal to the start size!");
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
