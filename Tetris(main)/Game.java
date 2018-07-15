import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.control.Button;
import javafx.scene.input.KeyEvent;
import java.io.FileOutputStream;
import java.io.FileInputStream;
import java.io.ObjectOutputStream;
import java.io.ObjectInputStream;
import javafx.scene.control.Label;
import java.util.Arrays;

public class Game {
  // Grid for the game.
  public Grid grid = new Grid();
  // The player's peice
  public Player player = new Player();
  // Button array for each of our buttons
  public boolean[] btn = new boolean[7];
  // whether or not the game is paused
  private boolean paused = false;
  // current score
  public long score = 0L;
  // highscore board
  private long[] highscores = new long[]{0,0,0,0,0,0};
  Button btnPause;
  Button btnSave;
  Button btnLoad;
  Label scoreLabel;

  Game(Button save, Button load, Button pause, Label scoreLabel) {
    btnPause = pause;
    btnSave = save;
    btnLoad = load;
    this.scoreLabel = scoreLabel;
    load();
    reset();
    // Give the player the grid so it can easily play with it.
    // player.setGrid(grid);
  }
  public void reset() {
    grid = new Grid();
    player = new Player();
    player.setGrid(grid);
    save();
  }
  public void load() {
    // We might have read/write errors! No amount of coding can prevent this, so catch it.
    try {
      // Open the save file.
//      FileInputStream saveFile = new FileInputStream("tetris.sav");
//
//      // Create an ObjectInputStream to get objects from save file.
//      ObjectInputStream save = new ObjectInputStream(saveFile);
//      highscores = (long[]) save.readObject();
//      grid = (Grid) save.readObject();
//      player = (Player) save.readObject();
//      // give player the grid
//      player.setGrid(grid);
//      // close the file
//      save.close();
    } catch(Exception e) { e.printStackTrace(); }

  }
  public void save() {
    // New try block for read/write errors.
    try {
      // Open a file
      FileOutputStream saveFile = new FileOutputStream("tetris.sav");

      // Create an ObjectOutputStream to put objects into save file.
      ObjectOutputStream save = new ObjectOutputStream(saveFile);

      // Now we do the save.
      save.writeObject(highscores);
      save.writeObject(grid);
      save.writeObject(player);
      // close the file
      save.close();
    } catch(Exception e) { e.printStackTrace(); }

  }
  public void togglePause() {
    // toggle the boolean
    paused = !paused;
    // make the button reflect this fact
    btnPause.setText((paused)?"Un-Pause":"Pause");
  }
  public void keyDown(KeyEvent event) {
    // update our button array with whatever button was pressed
    switch (event.getCode()) {
      case UP:    btn[0] = true; break;
      case DOWN:  btn[1] = true; break;
      case LEFT:  btn[2]  = true; break;
      case RIGHT: btn[3]  = true; break;
      case Z: btn[4]  = true; break;
      case X: btn[5]  = true; break;
      case ESCAPE:
        if (btn[6] != true) {
          btn[6] = true;
          // if the button was just pressed, toggle pause.
          togglePause();
        }
        break;
    }
  }
  public void keyUp(KeyEvent event) {
    // Update our button array this time making them false.
    switch (event.getCode()) {
      case UP:    btn[0] = false; break;
      case DOWN:  btn[1] = false; break;
      case LEFT:  btn[2]  = false; break;
      case RIGHT: btn[3]  = false; break;
      case Z: btn[4]  = false; break;
      case X: btn[5]  = false; break;
      case ESCAPE: btn[6]  = false; break;
    }
  }
  public void update() {
    // On update, if we're not paused
    if(!paused) {
      // update the player and grid!
      if (!player.update(this)) {
        // if player returns false, the player tried to place a piece outside of the grid.
        // which means the player hit out of bounds, and lost the game

        highscores[0] = score;
        Arrays.sort(highscores);

        reset();
      }
      grid.update(this);
      // set the scores
      String s = String.format("Score: %s\n",score);
      for (int i = 5; i >= 0; i--) {
        s = s + String.format("%d: %d\n",6-i,highscores[i]);
      }
      scoreLabel.setText(s);

    }
  }
  public void draw(GraphicsContext ctx) {
    // clear the screen
    ctx.setFill(Color.BLACK);
    ctx.fillRect(0,0,480,480);
    // Draw the grid
    grid.draw(ctx,50,20);
    // draw the player
    player.draw(ctx,50,20);
  }
}
