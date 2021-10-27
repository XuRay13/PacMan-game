package ghost;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import processing.core.PApplet;

class WakaTest {

  @Test
  public void testMoveIntoWall() {
    Waka waka = new Waka(0, 0, "Right", null);
    waka.go = false;
    waka.move();
    // assertArrayEquals("Should stop moving when hit wall" , new int[]{0, 0},
    // waka.getCoordinates());
  }

  @Test
  public void testMoves() {
    Waka waka = new Waka(0, 0, "Right", null);
    waka.move();

    assertTrue(waka.y == 1);

    waka.setDirection("Down");
    waka.move();

    assertTrue(waka.x == 1);
  }

  @Test
  public void testDrawCoordinates() {
    PApplet app = new PApplet();
    app.setup();
    Waka waka = new Waka(2, 1, "Right", app);
    int[] coordinates = waka.getDrawCoordinates(2, true);

    assertEquals(coordinates[0], 30);
    assertEquals(coordinates[1], 14);
  }

  @Test
  public void testWakaHitsOtherCells() {

  }
}