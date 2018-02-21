public class Main {


  private static int[] generateBoard (int n) {
    Random random = new Random();
    int[] board = new int[n];
    for (int i = 0; i < n; i++) {
      board[i] = random.nextInt(n);
    }
    return board;
  }

  private static Result runHillClimbing (int[] initial_state, boolean print_paths) {
    Node neighbor;
    List<Node> neighbors;
    Node current = new Node(initial_state);
    if (current.cost == 0) {
      System.out.println("skipped");

      return new Result (current, 0, 0, System.nanoTime());
    }

    int moves = 0;
    int search_cost = 0;
    final long start_time = System.nanoTime();
    while (true) {
      neighbors = current.generateSuccessors();
      search_cost += neighbors.size();
      neighbor = neighbors.get(0);
      if (current.cost <= neighbor.cost) {
        return new Result(current, moves, search_cost, start_time);
      }
      current = neighbor;
      moves++;
    }
  }

  private static Result renGenetic (List<Node> initial_states, double mutation_chance) {
    Node[] crossover;
    Node highest_fitness;
    List<Node> crossed_over = new ArrayList(initial_states.size());
    List<Node> best_of_current_generation;
    List<Node> next_generation = initial_states;

    int generations = 0;
    final long start_time = System.nanoTime();
    while (true) {
      best_of_current_generation = Genetic.select(next_generation);
      for (Node n : best_of_current_generation) {
        if (n.cost == 0) {
          return new Result(n, generations, 0, start_time);
        }
      }

      crossed_over.clear();
      highest_fitness = best_of_current_generation.get(0);
      for (int i = 1; i < best_of_current_generation.size(); i++) {
        crossover = Genetic.crossover(highest_fitness, best_of_current_generation.get(i));
        crossed_over.add(crossover[0]);
        crossed_over.add(crossover[1]);
      }
      next_generation.clear();
      for (Node n : crossed_over) {
        next_generation.add(Genetic.mutate(n, mutation_chance));
      }
      generations++;
    }
  }



}
