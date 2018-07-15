import javafx.scene.paint.Color;
import java.io.IOException;
import java.io.ObjectStreamException;
import java.io.ObjectOutputStream;
import java.io.ObjectInputStream;

public class Tile implements java.io.Serializable {
  static final long serialVersionUID = 0L;
  public boolean active = false;
  public Color color = Color.BLACK;
  // Set colors for different pieces
  public static Color[] colors = new Color[]{
    Color.BLACK,
    Color.CYAN,
    Color.YELLOW,
    Color.RED,
    Color.GREEN,
    Color.PURPLE,
    Color.BLUE,
    Color.ORANGE,
  };
  Tile() {}
  Tile(boolean active, Color color) {
    this.active = active;
    this.color = color;
  }
  // interface for Serializable
  private void writeObject(ObjectOutputStream out) throws IOException {
    // Write active, and rgb values
    out.writeObject(active);
    out.writeObject(color.getRed());
    out.writeObject(color.getGreen());
    out.writeObject(color.getBlue());
  }
  private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
    // read active, and turn rgb values into a color
    active = (boolean) in.readObject();
    double r = (double) in.readObject();
    double g = (double) in.readObject();
    double b = (double) in.readObject();
    color = Color.color(r,g,b);
  }
  private void readObjectNoData() throws ObjectStreamException { }
}
