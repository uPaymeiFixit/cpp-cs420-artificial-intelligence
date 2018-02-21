import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;
import java.util.Scanner;
import java.io.InputStream;

public class NQueen {
  public static void main (String[] args) {

    final int[] size = askSizeRange();
    final int trials = askTrials();

    for (int n = size[0]; n <= size[1]; n++) {
      Node[] population = new Node[trials];
      for (int i = 0; i < trials; i++) {
        final int[] board = generateBoard(n);
        population[i] = new Node(board);
      }
      // Genetic genetic = new Genetic(population, 0.5);
      RandomRestartHillClimbing rrhc = new RandomRestartHillClimbing(population);
      // getSolution(board);
    }

    // getStats();

  }

  // private static void getSolution (int[] board) {
  //   Node node = new Node(board, null, 0);
  //   Astar astar1 = new Astar(node, 1);
  //   Astar astar2 = new Astar(node, 2);
  //   System.out.println("Heuristic 1:" +
  //     "\n  CPU Time: " + astar1.cpu_time +
  //     "ms\nNodes Created: " + astar1.nodes_created +
  //     "\n  Step Cost: " + astar1.step_cost +
  //     "\n  Solution:"
  //   );
  //   astar1.printSolution();
  //   System.out.println("Heuristic 2:" +
  //     "\n  CPU Time: " + astar2.cpu_time +
  //     "ms\nNodes Created: " + astar2.nodes_created +
  //     "\n  Step Cost: " + astar2.step_cost +
  //     "\n  Solution:"
  //   );
  //   astar2.printSolution();
  // }

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


  // private static void getStats (int n) {

  //   int[] h1_nodes_created = new int[100];
  //   int[] h2_nodes_created = new int[100];
  //   int[] h1_trials = new int[100];
  //   int[] h2_trials = new int[100];
  //   long[] h1_cpu_time = new long[100];
  //   long[] h2_cpu_time = new long[100];

  //   int min = 1000;
  //   int max = 0;

  //   for (int i = 0; i < n; i++) {
  //     int[] board = generateBoard();
  //     Node node = new Node(board, null, 0);
  //     Astar astar1 = new Astar(node, 1);
  //     Astar astar2 = new Astar(node, 2);

  //     h1_nodes_created[astar1.step_cost] += astar1.nodes_created;
  //     h1_cpu_time[astar1.step_cost] += astar1.cpu_time;
  //     h1_trials[astar1.step_cost]++;

  //     h2_nodes_created[astar2.step_cost] += astar2.nodes_created;
  //     h2_cpu_time[astar2.step_cost] += astar2.cpu_time;
  //     h2_trials[astar2.step_cost]++;

  //     min = astar1.step_cost < min ? astar1.step_cost : min;
  //     min = astar2.step_cost < min ? astar2.step_cost : min;
  //     max = astar1.step_cost > max ? astar1.step_cost : max;
  //     max = astar2.step_cost > max ? astar2.step_cost : max;
  //   }

  //   System.out.println("┌────┬─────────────────┬─────────────────┬─────────────────┐");
  //   System.out.println("│    │ Nodes Generated │    Run Time     │ Number of Cases │");
  //   System.out.println("│ n  ├────────┬────────┼────────┬────────┼────────┬────────┤");
  //   System.out.println("│    │  RRHC  │  Gen   │  RRHC  │  Gen   │  RRHC  │  Gen   │");
  //   System.out.println("├────┼────────┼────────┼────────┼────────┼────────┼────────┤");
  //   for (int d = min; d <= max; d++) {
  //     System.out.printf("│ %2d ", d);
  //     if (h1_trials[d] != 0) {
  //       System.out.printf("│ %6d ", h1_nodes_created[d] / h1_trials[d]);
  //     } else {
  //       System.out.printf("│        ");
  //     }
  //     if (h2_trials[d] != 0) {
  //       System.out.printf("│ %6d ", h2_nodes_created[d] / h2_trials[d]);
  //     } else {
  //       System.out.printf("│        ");
  //     }
  //     if (h1_trials[d] != 0) {
  //       System.out.printf("│ %4dms ", h1_cpu_time[d] / h1_trials[d]);
  //     } else {
  //       System.out.printf("│        ");
  //     }
  //     if (h2_trials[d] != 0) {
  //       System.out.printf("│ %4dms ", h2_cpu_time[d] / h2_trials[d]);
  //     } else {
  //       System.out.printf("│        ");
  //     }
  //     System.out.printf("│ %6d ", h1_trials[d]);
  //     System.out.printf("│ %6d │\n", h2_trials[d]);
  //   }
  //   System.out.println("└────┴────────┴────────┴────────┴────────┴────────┴────────┘");
  // }

  // Returns an array of queen positions
  private static int[] generateBoard (int n) {
    int[] board = new int[n];
    Random random = new Random();
    for (int i = 0; i < n; i++) {
      board[i] = random.nextInt(n);
    }

    return board;
  }

}
