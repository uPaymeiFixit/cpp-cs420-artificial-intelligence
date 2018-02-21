public static class Genetic {

    static List<Node> select (List<Node> pool) {
        Collections.sort(pool, (Node n1, Node n2) -> {
            return n1.cost - n2.cost;
        });

        List<Node> selection = ArrayList<>();
        for (int i = 0; i <= pool.size() >> 1; i++) {
            selection.add(pool.get(i));
        }

        return selection;
    }

    static Node[] crossover (Node n1, Node n2) {
        assert n1.board.length == n2.board.length;
        final int n = n1.board.length;
        Random rand = new Random();
        final int crossover = rand.nextInt(n - 1);

        int[] new_board1 = new int[n];
        System.arraycopy(n1.board, 0, new_board1, 0, crossover);
        System.arraycopy(n2.board, crossover, new_board1, crossover, n - crossover);
        Node n12 = new Node(new_board1);

        int[] new_board2 = new int[n];
        System.arraycopy(n2.board, 0, new_board2, 0, crossover);
        System.arraycopy(n1.board, crossover, new_board2, crossover, n - crossover);
        Node n21 = new Node(new_board1);

        return new Node[] {n12, n21};
    }

    static Node mutate (Node n, double chance) {
        assert 0 <= chance && chance <= 1;
        Random rand = new Random();
        if (chance < rand.nextDouble()) {
            return n;
        }

        int[] mutated = Arrays.copyOf(n,board, n,board.length);
        final int pos = rand.nextInt(mutated.length);
        mutated[pos] = rand.nextInt(mutated.length);
        return new Node(mutated);
    }
}
