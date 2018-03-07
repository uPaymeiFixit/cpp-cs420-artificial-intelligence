package tk.gibbs.mnk;

public class Pattern {
  /**
   * List of points in pattern. Always starts at (0,0)
   */
  final BoardTile[] points;
  final int value;

  Pattern (BoardTile[] points, int value) {
    this.points = points;
    this.value = value;
  }

  BoardTile[] rotate () { return rotate(1); }
  BoardTile[] rotate (int timesCW) { return rotate(timesCW, this.points); }
  private BoardTile[] rotate (int timesCW, BoardTile[] points) {
    if (timesCW == 0) return points;
    else {
      final BoardTile[] rotated = new BoardTile[points.length];
      for (int i = 0; i < points.length; i++) {
        rotated[i] = new BoardTile(
          points[i].y,
          -points[i].x,
          points[i].state
        );
      }
      return rotate(--timesCW, rotated);
    }
  }
}
