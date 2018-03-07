package tk.gibbs.mnk;

import java.awt.Rectangle;

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

  @Override
  public String toString () {
    String out = "";

    Rectangle boundingBox = new Rectangle(0, 0, 0, 0);

    for (int i = 0; i < this.points.length; i++) {
      if (!boundingBox.contains(this.points[i].x, this.points[i].y)) {
        if (this.points[i].x > boundingBox.x + boundingBox.width) {
          boundingBox.width = this.points[i].x - boundingBox.x;
        }
        if (this.points[i].x < boundingBox.x) {
          boundingBox.x = this.points[i].x;
        }
        if (this.points[i].y > boundingBox.y + boundingBox.height) {
          boundingBox.height = this.points[i].y - boundingBox.y;
        }
        if (this.points[i].y < boundingBox.y) {
          boundingBox.y = this.points[i].y;
        }
      }
    }

    for (int i = 0; i <= boundingBox.height; i++) {
      for (int j = 0; j <= boundingBox.width; j++) {
        BoardTile tile = null;

        for (BoardTile t : this.points) {
          if (t.x == boundingBox.x + j && t.y == boundingBox.y + i) {
            tile = t;
            break;
          }
        }

        if (tile == null) {
          out += String.format("%s?%s ", ANSIColors.BLACK_BRIGHT, ANSIColors.RESET);
        } else if (tile.state == TileState.EMPTY) {
          out += String.format("%s∙%s ", ANSIColors.WHITE, ANSIColors.RESET);
        } else if (tile.state == TileState.X) {
          out += String.format("%s✕%s ", ANSIColors.GREEN, ANSIColors.RESET);
        } else if (tile.state == TileState.O) {
          out += String.format("%s◯%s ", ANSIColors.RED, ANSIColors.RESET);
        }
      }
      out += "\n";
    }

    return out;
  }
}
