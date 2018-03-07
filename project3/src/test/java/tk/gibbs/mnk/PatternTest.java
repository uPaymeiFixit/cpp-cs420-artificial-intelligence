package tk.gibbs.mnk;

/*
 * This Java source file was generated by the Gradle 'init' task.
 */
import org.junit.Test;
import static org.junit.Assert.*;

public class PatternTest {
  @Test public void testPatternRotation () {
    Pattern unbounded = new Pattern(
      new BoardTile[]{
        new BoardTile(0, 0, TileState.X),
        new BoardTile(1, 0, TileState.X),
        new BoardTile(-1, 0),
        new BoardTile(2, 0),
      }, 10
    );

    BoardTile[] unboundedRotate90 = new BoardTile[]{
      new BoardTile(0, 0, TileState.X),
      new BoardTile(0, -1, TileState.X),
      new BoardTile(0, 1),
      new BoardTile(0, -2),
    };

    BoardTile[] unboundedRotate180 = new BoardTile[]{
      new BoardTile(0, 0, TileState.X),
      new BoardTile(-1, 0, TileState.X),
      new BoardTile(1, 0),
      new BoardTile(-2, 0),
    };

    BoardTile[] rotated = unbounded.rotate();
    for (int i = 0; i < unboundedRotate90.length; i++) {
      assertTrue("90° Rotated patterns should match", unboundedRotate90[i].equals(rotated[i]));
    }
    rotated = unbounded.rotate(2);
    for (int i = 0; i < unboundedRotate180.length; i++) {
      assertTrue("180° Rotated patterns should match", unboundedRotate180[i].equals(rotated[i]));
    }
  }
}