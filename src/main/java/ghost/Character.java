package ghost;

import processing.core.PApplet;

public abstract class Character {
  public static final int PX_SIZE = 32;

  public String direction;
  public int x;
  public int y;
  public int[] startingCoordinate;
  PApplet app;

  public Character(int x, int y, PApplet app) {
    this.x = x;
    this.y = y;
    this.startingCoordinate = new int[] { x, y };
    this.direction = "Left";
    this.app = app;
  }

  public abstract void tick(Game game);

  /**
   * Return Character to it's initial position
   */
  public void returnToStarting() {
    this.x = this.startingCoordinate[0];
    this.y = this.startingCoordinate[1];
    this.setDirection("Left");
  }

  public String getDirection() {
    return this.direction;
  }

  public void setDirection(String direction) {
    this.direction = direction;
  }

  public int[] getCoordinates() {
    return new int[] { x, y };
  }

  /**
   * Since each tick move characters between cells, we still want to draw
   * characters moving each frame between cells. Calculate the offset based on
   * subtick
   * 
   * @param subtick frames since last tick
   * @param go      character is moving
   * @return
   */
  public int[] getDrawCoordinates(int subtick, boolean go) {
    int xOffset = -4; // centering offset since waka is 2x space cells
    int yOffset = -4;
    if (go && this.direction.equals("Left")) {
      xOffset += Cell.PX_SIZE - subtick;
    } else if (go && this.direction.equals("Right")) {
      xOffset += subtick - Cell.PX_SIZE;
    } else if (go && this.direction.equals("Up")) {
      yOffset += Cell.PX_SIZE - subtick;
    } else if (go && this.direction.equals("Down")) {
      yOffset += subtick - Cell.PX_SIZE;
    }

    int xPos = this.x * Cell.PX_SIZE + xOffset;
    int yPos = this.y * Cell.PX_SIZE + yOffset;

    return new int[] { xPos, yPos };
  }

  /**
   * Move character 1 cell in the direction it's facing
   */
  public void move() {
    if (this.direction.equals("Left")) {
      this.x -= 1;
    } else if (this.direction.equals("Right")) {
      this.x += 1;
    } else if (this.direction.equals("Up")) {
      this.y -= 1;
    } else if (this.direction.equals("Down")) {
      this.y += 1;
    }
  }
}
