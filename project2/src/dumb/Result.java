public final class Result {
    final Node terminal_state;
    final int num_moves;
    final int search_cost;
    final long start_time;
    final long end_time;

    Result (Node terminal_state, int num_moves, int search_cost, long start_time) {
        this.terminal_state = terminal_state;
        this.num_moves = num_moves;
        this.search_cost = search_cost;
        this.start_time = start_time;
        this.end_time = System.nanoTime();
    }
}
