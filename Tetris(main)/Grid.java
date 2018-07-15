import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import java.io.IOException;
import java.io.ObjectStreamException;
import java.io.ObjectOutputStream;
import java.io.ObjectInputStream;

public class Grid implements java.io.Serializable {
  static final long serialVersionUID = 0L;
  // Make a 2d array of Tiles!
  private Tile[][] tiles = new Tile[9][16];
  Grid() {
    // initialize the tiles to new tiles.
    for(int x = 0; x < 9; x++) {
      for(int y = 0; y < 16; y++) {
        tiles[x][y] = new Tile();
      }
    }
  }
  // Serializable interface.
  private void writeObject(ObjectOutputStream out) throws IOException {
    // Write the tiles!
    out.writeObject(tiles);
  }
  private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
    // Read the tiles! It's that simple!
    tiles = (Tile[][]) in.readObject();
  }
  // This is probably fine.
  private void readObjectNoData() throws ObjectStreamException { }
  // All we need to do on update of the grid is...
  public void update(Game game) {
    // check if there's any lines to be cleared!
    int lines = check();
    if (lines > 0) {
      game.score += 20*lines;
    }
  }
  // Helper function to get a tile at a certain position
  public Tile getTile(int x, int y) {
    // if it's outside of the bounds just return a solid pink tile.
    if ((x < 0 || x > 8) || (y < 0 || y > 15)) {
      // There's probably better ways to go about this but it works.
      return new Tile(true, Color.PINK);
    }
    // Otherwise just return the actual tile.
    return tiles[x][y];
  }
  // Check if there's any lines to be cleared
  public int check() {
    // Go downward through the array.
    int lines = 0;
    for(int y = 0; y < 16; y++) {
      // this is our sentinel variable.
      boolean clear = true;
      // loop through this horizontal row.
      for(int x = 0; x < 9; x++) {
        Tile tile = getTile(x,y);
        // if this tile isn't active
        if (!tile.active) {
          // then set clear to false
          clear = false;
          // we don't need to check the rest of this row
          // it's already set to not clear. So let's just break.
          break;
        }
      }
      // if we only hit active tiles
      if (clear) {
        // then this row should be cleared!
        clearLine(y);
        lines += 1;
      }
    }
    return lines;
  }
  // Remove a row from the grid!
  public void clearLine(int y) {
    // Go upwards up the grid, starting from the row provided
    for(int row = y; row >= 0; row--) {
      // go through each tile in the row
      for(int x = 0; x < 9; x++) {
        // this will be the row above
        int grabY = row-1;
        Tile t;
        if (grabY < 0) {
          // if the row is above the grid, then just set it to a false tile
          t = new Tile(false, Color.PINK);
        } else {
          // otherwise use the actual tile
          t = getTile(x, grabY);
        }
        // set the current row's tile to the one above it.
        tiles[x][row] = t;
      }
    }
  }
  // Draw the grid!
  public void draw(GraphicsContext ctx, int posX, int posY) {
    // for each row
    for(int y = 0; y < 16; y++) {
      // for each column
      for(int x = 0; x < 9; x++) {
        // grab the tile
        Tile tile = getTile(x,y);
        if (tile.active) {
          // if it's active, set the color to it's color
          ctx.setFill(tile.color);
        } else {
          // otherwise just use a gray
          ctx.setFill(Color.GRAY);
        }
        // draw a square!
        ctx.fillRect(posX + x*20,posY + y*20,19,19);
      }
    }
  }
}
