import javafx.scene.paint.Color;
enum Piece {
  // Enum! Various piece shapes!
  LINE(new boolean[]{false, true, false, false, false, true, false, false, false, true, false, false, false, true, false, false}, Tile.colors[1]),
  SQUARE(new boolean[]{false, false, false, false, false, true, true, false, false, true, true, false, false, false, false, false}, Tile.colors[2]),
  S(new boolean[]{false, true, false, false, false, true, true, false, false, false, true, false, false, false, false, false}, Tile.colors[3]),
  Z(new boolean[]{false, false, true, false, false, true, true, false, false, true, false, false, false, false, false, false}, Tile.colors[4]),
  T(new boolean[]{false, false, true, false, false, true, true, false, false, false, true, false, false, false, false, false}, Tile.colors[5]),
  L(new boolean[]{false, true, false, false, false, true, false, false, false, true, true, false, false, false, false, false}, Tile.colors[6]),
  J(new boolean[]{false, false, true, false, false, false, true, false, false, true, true, false, false, false, false, false}, Tile.colors[7]);
  // this will hold the tiles in the piece
  public boolean[] tiles;
  // this is the color of the shape.
  public Color color;
  // Create a new piece with the tiles provided and color.
  Piece(boolean[] tiles, Color color) {
    this.tiles = tiles;
    this.color = color;
  }
  // Rotate this piece
  public void rotate() {
    // make an array to hold the new rotated piece
    boolean[] rotated = new boolean[16];
    for(int i = 0; i < 16; i++) {
      // do some math to swap the pieces depending on where they currently are.
      // math checks out, check it out on a graph with
      //
      rotated[(int)Math.floor(4 * (( i % 4 ) + 1) - ((1 + i) / 4.0))] = tiles[i];
    }
    tiles = rotated;
  }
  static Piece random() {
    Piece piece = Piece.values()[(int)(Math.random()*Piece.values().length)];
    return piece;
  }
}
