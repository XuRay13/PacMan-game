package ghost;

import processing.core.PApplet;
import processing.core.PImage;

public class Wall extends Cell {

    public String wallType;
    public PImage wallImage;

    public Wall(int x, int y, String wallType, PApplet app) {
        super(x, y);

        this.wallImage = app.loadImage("../../../resources/main/" + wallType + ".png");
    }

    public void draw(PApplet app) {
        app.image(this.wallImage, this.x * Cell.PX_SIZE, this.y * Cell.PX_SIZE);
    }
}