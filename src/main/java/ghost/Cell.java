package ghost;

import processing.core.PApplet;

public class Cell {
    public static final int PX_SIZE = 16;

    public int x;
    public int y;

    public Cell(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public int[] getCoordinates() {
        return new int[] { x, y };
    }

    public void draw(PApplet app) {

    }
}