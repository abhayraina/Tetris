import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.Button;
import javafx.stage.Stage;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.animation.AnimationTimer;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.KeyEvent;
import javafx.scene.control.Label;

public class Tetris extends Application {
  public static void main(String[] args) {
    // Start the application
    launch(args);
  }

  @Override
  public void start(Stage window) {
    // Create a horizontal split, this will contain the canvas and button column
    HBox root = new HBox();

    // Create a canvas, for drawing our game.
    Canvas canvas = new Canvas(260,360);

    // Get our graphics context to pass to the game later
    GraphicsContext ctx = canvas.getGraphicsContext2D();

    // Create our info panel
    VBox info = new VBox(10);

    // Make the info panel expand to fit the remaining space of the window.
    info.prefWidthProperty().bind(root.widthProperty());

    // Make the children aligned in the center
    info.setAlignment(Pos.CENTER);

    // Create a save button.
    Button save = new Button();

    // Create a load button.
    Button load = new Button();

    // Create a pause button.
    Button pause = new Button();

    // Create a new game button.
    Button newGame = new Button();

    // Create a score label
    Label scores = new Label("");

    // Create the game's instance
    Game game = new Game(save,load,pause,scores);

    // Make our buttons do what they're supposed to do!
    save.setText("Save");
    save.setOnAction(new EventHandler<ActionEvent>() {
      @Override
      public void handle(ActionEvent event) {
        game.save();
      }
    });

    newGame.setText("New Game");
    newGame.setOnAction(new EventHandler<ActionEvent>() {
      @Override
      public void handle(ActionEvent event) {
        game.reset();
      }
    });

    pause.setText("Pause");
    pause.setOnAction(new EventHandler<ActionEvent>() {
      @Override
      public void handle(ActionEvent event) {
        game.togglePause();
      }
    });

    load.setText("Load");
    load.setOnAction(new EventHandler<ActionEvent>() {
      @Override
      public void handle(ActionEvent event) {
        game.load();
      }
    });


    // Create a timer to update the game, and draw.
    new AnimationTimer() {
      @Override
      public void handle(long now) {
        game.update();
        game.draw(ctx);
      }
    }.start();

    // Add our save and pause buttons to the info panel
    info.getChildren().addAll(scores,newGame,save,load,pause);

    // Add the canvas and info panel to the root.
    root.getChildren().addAll(canvas,info);

    // Create our scene, and provide the root to it.
    Scene scene = new Scene(root, 480, 360);
    scene.setOnKeyPressed(new EventHandler<KeyEvent>() {
      @Override
      public void handle(KeyEvent event) {
        game.keyDown(event);
      }
    });

    scene.setOnKeyReleased(new EventHandler<KeyEvent>() {
      @Override
      public void handle(KeyEvent event) {
        game.keyUp(event);
      }
    });
    // Set our window up
    window.setTitle("Tetris");
    window.setScene(scene);
    window.show();
  }
}
