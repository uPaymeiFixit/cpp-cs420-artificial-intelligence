import java.util.PriorityQueue;
import java.util.Collections;
import java.util.HashSet;
import java.util.ArrayList;
import java.util.Scanner;

public class Astar {
  private PriorityQueue<Node> frontier_nodes = new PriorityQueue<>();
  private HashSet<Integer> explored_keys = new HashSet<>();
  private HashSet<Integer> frontier_keys = new HashSet<>();
  private ArrayList<Node> solution = new ArrayList<>();
  private int heuristic_mode;
  public int nodes_created = -2;
  public int step_cost = 0;
  public long cpu_time = 0;

  public Astar (Node node, int heuristic_mode) {
    this.heuristic_mode = heuristic_mode;
    
    long time = System.nanoTime() / 1000000;
    
    add(node);
    search();

    this.cpu_time = System.nanoTime() / 1000000 - time;
  }

  private void explore (Node node) {
    add(node.actionUp());
    add(node.actionDown());
    add(node.actionLeft());
    add(node.actionRight());
  }

  private void add (Node node) {
    // If we have never seen this configuration before
    if (node != null && !frontier_keys.contains(node.key) && !explored_keys.contains(node.key)) {
      if (heuristic_mode == 1) {
        node.total_cost = node.heuristic1() + node.moves;
      } else {
        node.total_cost = node.heuristic2() + node.moves;
      }
      frontier_nodes.offer(node);
      frontier_keys.add(node.key);

      nodes_created++;
    }
  }

  public void search () {
    Node node;
    do {
      // Move node from frontier to explored
      node = frontier_nodes.poll();
      frontier_keys.remove(node);
      explored_keys.add(node.key);
      // Explore node
      explore(node);
    } while (!node.isGoalState());

    getSolution(node);
  }

  private ArrayList<Node> getSolution (Node node) {
    solution.add(node);
    while (node.parent_node != null) {
      solution.add(node.parent_node);
      node = node.parent_node;
    }
    this.step_cost = solution.size() - 1;
    return solution;
  }

  public void printSolution () {
    Collections.reverse(solution);
    for (Node node : solution) {
      node.print();
    }
  }

  private void pause () {
    (new Scanner(System.in)).nextLine();
  }

}
