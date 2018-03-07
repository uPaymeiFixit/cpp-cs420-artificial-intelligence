package tk.gibbs.mnk;

public class BoardTile {
  int x;
  int y;
  TileState state;

  BoardTile (int x, int y) { this(x, y, TileState.EMPTY); }
  BoardTile (int x, int y, TileState state) {
    this.x = x;
    this.y = y;
    this.state = state;
  }

  public boolean equals(BoardTile obj) {
    return
      this.x == obj.x &&
      this.y == obj.y &&
      this.state == obj.state;
  }

  @Override
  public String toString () {
    return String.format("%s(%s,%s)", this.state, this.x, this.y);
  }
}
