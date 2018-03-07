package tk.gibbs.mnk;

import java.util.Queue;
import java.util.LinkedList;

public class ThreadSearch implements Runnable {

  private Thread thread = new Thread(this);
  private BoardState root;

  public ThreadSearch () {
    this(new BoardState());
  }

  public ThreadSearch (BoardState root) {
    this.root = root;
    thread.start();
  }

  public void run () {
    Queue<BoardState> queue = new LinkedList<>();
    queue.add(root);

    while (queue.size() != 0) {
      if (AI.start_time == 0) {
        BoardState x = queue.remove();
        if (x.children.size() == 0 && !x.terminal_state) {
          AI.successors_exhaustive(x);
          for (BoardState y : x.children) {
            queue.add(y);
          }
        }
      } else {
        try {
          Thread.sleep(50);
        } catch (Exception exception) {
          // Do nothing
        }
      }
    }
  }
}
