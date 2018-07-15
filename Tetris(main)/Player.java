import javafx.scene.canvas.GraphicsContext;
import java.io.IOException;
import java.io.ObjectStreamException;
import java.io.ObjectOutputStream;
import java.io.ObjectInputStream;

public class Player implements java.io.Serializable {
  // This is used by Serializable, to keep track of what version of serialization is used!
  static final long serialVersionUID = 0L;
  // Our grid which should be the one in game.
  public Grid grid;
  // The current piece of the player
  public Piece piece;
  // The next pieces coming up.
  private Piece[] nextPieces = new Piece[4];
  // X position
  int x = 3;
  // Y position
  int y = -3;
  // Current tick, incremented on update.
  int tick = 0;
  // How many times we've hit the ground.
  int hit = 0;
  // This array holds the last time we hit a button.
  int[] inputs = new int[6];
  // Constructor
  Player() {
    // Populate the next pieces with random pieces.
    for(int i = 0; i < 4; i++) { nextPieces[i] = Piece.random(); }
    // Shift the first one into the player's piece
    shiftPiece();
  }
  // Interface for serializable to save our player.
  private void writeObject(ObjectOutputStream out) throws IOException {
    // Write relevant data!
    out.writeObject(piece);
    out.writeObject(x);
    out.writeObject(y);
    out.writeObject(nextPieces);
    out.writeObject(inputs);
  }
  // Interface for serializable to load the player
  private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
    piece = (Piece) in.readObject();
    x = (int) in.readObject();
    y = (int) in.readObject();
    nextPieces = (Piece[]) in.readObject();
    inputs = (int[]) in.readObject();
  }
  private void readObjectNoData() throws ObjectStreamException { }
  // Shifts the next piece into the player's piece.
  private void shiftPiece() {
    // Set the first piece in the array to our current piece.
    piece = nextPieces[0];
    for(int i = 0; i < 3; i++) {
      // Shift the remaining pieces over
      nextPieces[i] = nextPieces[i+1];
    }
    // Set the last piece to a semi-random piece
    setPiece(3);
  }
  // Places a piece into the next pieces array
  private void setPiece(int id) {
    // our chosen piece
    Piece nextPiece;
    do {
      // Get a random piece
      nextPiece = Piece.random();
      // Repeat this until it's not already in the next piece array.
      // This is so that you don't get a long string of z pieces
    } while(upNext(nextPiece));
    // set the piece in the array
    nextPieces[id] = nextPiece;
  }
  // Check if p is within the next pieces array
  private boolean upNext(Piece p) {
    for(int i = 0; i < 4; i++) {
      if(nextPieces[i] == p) {
        return true;
      }
    }
    return false;
  }
  // private
  public boolean rotate(boolean ccwise) {
    piece.rotate();
    int startX = x;
    if (ccwise) { piece.rotate(); piece.rotate(); }
    if (!validate()) {
      // Move right twice, and check if they're valid.
      if(!move(true, false) && !move(true, false)) {
        // Neither position worked, so let's reposition ourself.
        x = startX;
        // Now let's try moving left twice and validating them
        if(!move(false, false) && !move(false, false)) {
          x = startX;
          // Neither position worked either. Let's try going up once.
          y -= 1;
          if (!validate()) {
            // It didn't work either! Rotate all the way back.
            y += 1;
            piece.rotate();
            if(!ccwise) { piece.rotate(); piece.rotate(); }
            // No amount of movement could fix this rotation.
            return false;
          }
        }
      }
    }
    // If any of those validations work, we'll wind up here! It worked!
    return true;
  }
  // move left or right
  public boolean move(boolean right, boolean adjust) {
    // Decide which way to move using ternary to avoid a lot of conditionals
    int movement = (right?1:-1);
    // Move that way
    x += movement;
    // Check if it's valid
    if(!validate()) {
      // if it's not, go back, if we're told to adjust
      if (adjust) { x -= movement; }
      // it didn't work, return false.
      return false;
    }
    // Movement was valid, return true.
    return true;
  }
  // check our input. this is a helper function so that we don't do something every single tick.
  public boolean checkInput(Game game, int id, int delay) {
    // check the game's button array if the button is held down
    if(game.btn[id]) {
      // now, check if the last time this button was true wasn't within (delay) ticks
      if (tick - inputs[id] > delay) {
        // if it was, then udpate the input array
        inputs[id] = tick;
        // return that the button has been pressed
        return true;
      }
    } else {
      // If the button wasn't pressed, then reset the inputs for this id.
      // This is so tapping a button works as you'd expect!
      inputs[id] = -300;
    }
    // either the button wasn't pressed or was held down within our delay window.
    return false;
  }
  public boolean update(Game game) {
    // increment our tick counter
    tick += 5;
    if(checkInput(game,0,300)) {
      // This is our slam button, slam the piece down and place it!
      for(int slam = 1; slam <= 16; slam++) {
        y += 1;
        if (!validate()) {
          game.score += y;
          y -= 1;
          place();
          break;
        }
      }
    } else if(checkInput(game,2,300)) {
      // Move left
      move(false,true);
    } else if (checkInput(game,3,300)) {
      // Move right
      move(true,true);
    } else if (checkInput(game,4,200)) {
      // Rotate counter-clock-wise
      rotate(true);
    } else if (checkInput(game,5,200)) {
      // Rotate clock-wise
      rotate(false);
    }

    if(tick % 300 == 0) {
      // move down every 300 ticks
      y += 1;
      // Check if this placement is okay
      if(!validate()) {
        // We hit something! Go back up.
        y -= 1;
        // Count hits
        hit += 1;
        if(hit > 5) {
          // Hit the ground too many times, place tile.
          if (!place()) {
            return false;
          }
        }
      } else {
        // We went down again, reset our hit counter.
        hit = 0;
      }
    }
    return true;
  }
  public void setGrid(Grid grid) {
    // set the grid for checks.
    this.grid = grid;
  }
  public boolean validate() {
    // for each tile in our piece...
    for(int x = 0; x<4; x++) {
      for(int y = 0; y<4; y++) {
        if (piece.tiles[x+y*4]){
          // if it's above the y boundary, then ignore it
          if (y+this.y < 0) { continue; }
          Tile tile = grid.getTile(x+this.x, y+this.y);
          // otherwise, if it's on a tile that's active on the grid
          if (tile.active) {
            // reject it!
            return false;
          }
        }
      }
    }
    // if we got through all that, the piece is valid.
    return true;
  }
  // place the piece.
  public boolean place() {
    // for each piece in the piece
    for(int x = 0; x<4; x++) {
      for(int y = 0; y<4; y++) {
        if (piece.tiles[x+y*4]){
          // ignore it if we're out of the grid's boundary.
          if (y+this.y < 0) { return false; }
          Tile tile = grid.getTile(x+this.x, y+this.y);
          // set the tile at that grid position to our piece's stuff!
          tile.color = piece.color;
          tile.active = true;
        }
      }
    }
    // reset our position
    y = -3;
    x = 3;
    // get a new piece
    shiftPiece();
    return true;
  }
  public void draw(GraphicsContext ctx, int posX, int posY) {
    // Draw our upcoming pieces!
    for(int i = 0; i < 4; i++) {
      for(int x = 0; x<4; x++) {
        for(int y = 0; y<4; y++) {
          if (nextPieces[i].tiles[x+y*4]){
            // Set the color
            ctx.setFill(nextPieces[i].color);
            // Draw a filled rectangle
            ctx.fillRect(posX - 40 + x*10,posY + 40*i + y*10,9,9);
          }
        }
      }
    }
    // set our position based off of the x and y of our values
    posX += x*20;
    posY += y*20;
    // draw normal
    for(int x = 0; x<4; x++) {
      for(int y = 0; y<4; y++) {
        if (piece.tiles[x+y*4]){
          ctx.setFill(piece.color);
          ctx.fillRect(posX + x*20,posY + y*20,19,19);
        }
      }
    }
    // reset our draw position to the original placement
    posY -= y*20;
    // move the piece to the bottom!
    int startY = y;
    for(int slam = 1; slam <= 16; slam++) {
      y += 1;
      if (!validate()) { y -= 1; break; }
    }
    // move our draw position down again.
    posY += y*20;
    // set our line width for effect
    ctx.setLineWidth(3);
    // Draw a shadow!
    for(int x = 0; x<4; x++) {
      for(int y = 0; y<4; y++) {
        if (piece.tiles[x+y*4]){
          // set the color
          ctx.setStroke(piece.color);
          // draw an outline of our shape!
          ctx.strokeRect(posX + x*20,posY + y*20,19,19);
        }
      }
    }
    y = startY;
  }
}
