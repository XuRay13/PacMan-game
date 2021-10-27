// package ghost;

// import org.junit.jupiter.api.Test;
// import static org.junit.jupiter.api.Assertions.*;

// import java.beans.Transient;

// class WakaTest {

// private Game createGame() {
// Game game = new Game();
// game.readConfiguration("TestConfig.json");
// game.initializeGame(null);
// }

// @Test
// public void testGhostMove() {
// Ghost ghost = new Ghost(5, 5, "Chaser", null);
// ghost.move();
// assertTrue(ghost.getCoordinates() == new int[] { 5, 4 });
// ghost.changeDirection("Right");
// ghost.move();
// assertTrue(ghost.getCoordinates() == new int[] { 5, 5 });
// ghost.changeDirection("Up");
// ghost.move();
// assertTrue(ghost.getCoordinates() == new int[] { 4, 5 });
// ghost.changeDirection("Down");
// ghost.move();
// assertTrue(ghost.getCoordinates() == new int[] { 5, 5 });

// }

// @Test
// public void testPossibleDirections() {
// Ghost ghost = new Ghost(2, 6, "Chaser", null);
// assertEquals(new String[] { "Left" }, ghost.getPossibleDirections(game));
// ghost.move();
// assertEquals(new String[] { "Left", "Up", "Down" },
// ghost.getPossibleDirections(game));

// }
// }

// // @Test
// // public testChooseIntersection() {

// // }

// // @Test
// // public testScatterMode() {

// // }

// // @Test
// // public testChaseMode() {

// // }

// // @Test
// // public testFrightenMode() {

// // }