import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;
import java.util.Scanner;
import java.io.InputStream;

public class NQueen {
  public static void main (String[] args) {
    int mode = askMode();
    switch (mode) {
      case 1:
        getSolution(generateBoard());
        break;
      case 2:
        getSolution(askBoard());
        break;
      case 3:
        getStats(askTrials());
        break;
    }
  }

  private static void getSolution (int[] board) {
    Node node = new Node(board, null, 0);
    Astar astar1 = new Astar(node, 1);
    Astar astar2 = new Astar(node, 2);
    System.out.println("Heuristic 1:" + 
      "\n  CPU Time: " + astar1.cpu_time + 
      "ms\nNodes Created: " + astar1.nodes_created +
      "\n  Step Cost: " + astar1.step_cost + 
      "\n  Solution:"
    );
    astar1.printSolution();
    System.out.println("Heuristic 2:" + 
      "\n  CPU Time: " + astar2.cpu_time + 
      "ms\nNodes Created: " + astar2.nodes_created +
      "\n  Step Cost: " + astar2.step_cost + 
      "\n  Solution:"
    );
    astar2.printSolution();
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

  private static void getStats (int n) {

    int[] h1_nodes_created = new int[100];
    int[] h2_nodes_created = new int[100];
    int[] h1_trials = new int[100];
    int[] h2_trials = new int[100];
    long[] h1_cpu_time = new long[100];
    long[] h2_cpu_time = new long[100];

    int min = 1000;
    int max = 0;

    for (int i = 0; i < n; i++) {
      int[] board = generateBoard();
      Node node = new Node(board, null, 0);
      Astar astar1 = new Astar(node, 1);
      Astar astar2 = new Astar(node, 2);

      h1_nodes_created[astar1.step_cost] += astar1.nodes_created;
      h1_cpu_time[astar1.step_cost] += astar1.cpu_time;
      h1_trials[astar1.step_cost]++;

      h2_nodes_created[astar2.step_cost] += astar2.nodes_created;
      h2_cpu_time[astar2.step_cost] += astar2.cpu_time;
      h2_trials[astar2.step_cost]++;

      min = astar1.step_cost < min ? astar1.step_cost : min;
      min = astar2.step_cost < min ? astar2.step_cost : min;
      max = astar1.step_cost > max ? astar1.step_cost : max;
      max = astar2.step_cost > max ? astar2.step_cost : max;
    }

    System.out.println("┌────┬─────────────────┬─────────────────┬─────────────────┐");
    System.out.println("│    │ Nodes Generated │    Run Time     │ Number of Cases │");
    System.out.println("│ d  ├────────┬────────┼────────┬────────┼────────┬────────┤");
    System.out.println("│    │ A*(h1) │ A*(h2) │ A*(h1) │ A*(h2) │ A*(h1) │ A*(h2) │");
    System.out.println("├────┼────────┼────────┼────────┼────────┼────────┼────────┤");
    for (int d = min; d <= max; d++) {
      System.out.printf("│ %2d ", d);
      if (h1_trials[d] != 0) {
        System.out.printf("│ %6d ", h1_nodes_created[d] / h1_trials[d]);
      } else {
        System.out.printf("│        ");
      }
      if (h2_trials[d] != 0) {
        System.out.printf("│ %6d ", h2_nodes_created[d] / h2_trials[d]);
      } else {
        System.out.printf("│        ");
      }
      if (h1_trials[d] != 0) {
        System.out.printf("│ %4dms ", h1_cpu_time[d] / h1_trials[d]);
      } else {
        System.out.printf("│        ");
      }
      if (h2_trials[d] != 0) {
        System.out.printf("│ %4dms ", h2_cpu_time[d] / h2_trials[d]);
      } else {
        System.out.printf("│        ");
      }
      System.out.printf("│ %6d ", h1_trials[d]);
      System.out.printf("│ %6d │\n", h2_trials[d]);
    }
    System.out.println("└────┴────────┴────────┴────────┴────────┴────────┴────────┘");
  }

  // Prompt the user to select a mode
  private static int askMode () {
    Scanner scanner = new Scanner(System.in);
    int mode = 0;
    while (mode < 1 || mode > 3) {
      System.out.print("Please enter 1 or 2 to choose puzzle mode:\n"
        + "  1. Random 8-puzzle solution\n"
        + "  2. Custom 8-puzzle solution\n"
        + "  3. \"n\" random 8-puzzle statistics\n: "
      );
      while (!scanner.hasNextInt()) scanner.next();
      mode = scanner.nextInt();
    }
    return mode;
  }

  // Prompt the user to create a board
  private static int[] askBoard () {
    Scanner scanner = new Scanner(System.in);
    int[] board = new int[9];
    do {
      System.out.println("To create an 8-puzzle, enter digits 0-8");
      for (int i = 0; i < 9; i++) {
        while (!scanner.hasNextInt()) scanner.next();
        board[i] = scanner.nextInt();
      }
    } while (!validBoardInput(board));
    return board;
  }

  // Validate several aspects of our user input board
  private static boolean validBoardInput (int[] board) {
    for (int i = 0; i < board.length; i++) {
      // Check for out of range tiles
      if (board[i] < 0 || board[i] >= board.length) {
        System.out.println("Input not in range of [0-" + (board.length - 1) + "]: " + board[i]);
        return false;
      }

      // Check for duplicate tiles
      for (int j = 0; j < i; j++) {
        if (board[i] == board[j]) {
          System.out.println("Duplicate input: " + board[i]);
          return false;
        }
      }
    }

    // Check if board is solvable
    if (!isSolvable(board)) {
      System.out.println("Board is not solvable. Try again.");
      return false;
    }
    return true;
  }

  private static int[] generateBoard () {
    int[] board = new int[9];
    do {
      Random random = new Random();
      ArrayList<Integer> pieces = new ArrayList<Integer>(Arrays.asList(0, 1, 2, 3, 4, 5, 6, 7, 8));

      for (int i = 0; i < 9; i++) {
        board[i] = pieces.remove(random.nextInt(pieces.size()));
      }
    } while (!isSolvable(board));

    return board;
  }

  private static boolean isSolvable (int[] board) {
    boolean solvable = true;
		for (int i = 0; i < 8; i++) {
			if(board[i] == 0 || board[i] == 1) {
				continue;
			}
			for (int j = i + 1; j < 9; j++) {
				if ((board[i] > board[j]) && (board[j] != 0)) {
          solvable = !solvable;
				}
			}
		}
		return solvable;
	}


}
