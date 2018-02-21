import java.util.PriorityQueue;
import java.util.Collections;
import java.util.HashSet;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.Arrays;
import java.util.Random;

public class RandomRestartHillClimbing {
  public int nodes_created = 0;
  public long cpu_time = 0;

  public RandomRestartHillClimbing (Node[] initial_states) {
    long time = System.nanoTime() / 1000000;

    final int N = initial_states[0].board.length;

    // START SEARCH
    Node r = null;
    int[] board;
    for (int i = 0; i < initial_states.length; i++) {
      board = generateRandomBoard(N);
      r = runHillClimbing(board);
      if (r.cost != 0) {
        continue;
      }
      System.out.println("Solution found!");
      System.out.println(r);
    }
    System.out.println("No solution found :(");
    System.out.println(r);
    // END SEARCH

    this.cpu_time = System.nanoTime() / 1000000 - time;
  }


  private Node runHillClimbing(int[] initialState) {
    Node neighbor;
    ArrayList<Node> neighbors;
    Node current = new Node(initialState);
    if (current.cost == 0) {
      System.out.format("Search skipped, initial state is already a goal state.%n");
      System.out.println(current);
      System.out.println();

      return current;
    }

    int moves = 0;
    int searchCost = 0;
    final long START_TIME = System.nanoTime();
    while (true) {

      neighbors = generateSuccessors(current.board);
      searchCost += neighbors.size();
      neighbor = neighbors.get(0);
      if (current.cost <= neighbor.cost) {
        return current;
      }

      current = neighbor;
      moves++;
    }
  }

  ArrayList<Node> generateSuccessors(int[] BOARD) {
    // int cost;
    int originalValue;
    int[] copy;
    int[] board = Arrays.copyOf(BOARD, BOARD.length);
    ArrayList<Node> successors = new ArrayList<>((BOARD.length*BOARD.length)-BOARD.length);
    for (int i = 0; i < board.length; i++) {
      originalValue = board[i];
      for (int j = 0; j < board.length; j++) {
        if (j == originalValue) {
          continue;
        }

        board[i] = j;
        // cost = countAttacking(board);
        copy = Arrays.copyOf(board, board.length);
        successors.add(new Node(copy));
      }

      board[i] = originalValue;
    }

    if (successors.size() != (BOARD.length*BOARD.length)-BOARD.length) {
      System.out.println("PROBLEM!!!!");
    }

    Collections.sort(successors, (Node n1, Node n2) -> {
      return n1.cost - n2.cost;
    });

    return successors;
  }

  // function Hill-Climbing(problem) returns a state that is a local maximum
  //   inputs: problem, a problem
  //   local variables: current, a node
  //                    neighbor, a node
  //   current <- Make-Node(Initial-State[problem])
  //   loop do
  //     neighbor <- a highest-valued successor of current
  //     if Value[neighbor] ≤ Value[current] then return State[current]
  //     current <- neighbor


  // public Node HillClimbing (Node current) {
  //   // Node current = generateNode(problem.board.length);
  //   while (true) {
  //     Node neighbor = generateSuccessors(current).poll();
  //     if (neighbor.cost < current.cost) {
  //       return current;
  //     }
  //     current = neighbor;
  //   }
  // }

  // public PriorityQueue<Node> generateSuccessors (Node current) {
  //   int original_value;
  //   int[] copy;
  //   int[] board = Arrays.copyOf(current.board, current.board.length);
  //   PriorityQueue<Node> successors = new PriorityQueue<>();
  //   for (int i = 0; i < board.length; i++) {
  //     original_value = board[i];
  //     for (int j = 0; j < board.length; j++) {
  //       if (j == original_value) {
  //         continue;
  //       }

  //       board[i] = j;
  //       copy = Arrays.copyOf(board, board.length);
  //       successors.offer(new Node(copy));
  //     }
  //     board[i] = original_value;
  //   }

  //   return successors;
  // }

  private static int[] generateRandomBoard(final int N) {
    int[] BOARD = new int[N];
    Random RAND = new Random();
    for (int i = 0; i < BOARD.length; i++) {
      BOARD[i] = RAND.nextInt(BOARD.length);
    }

    return BOARD;
  }

}
