package ghost;

import processing.core.PApplet;
import processing.core.PImage;

public class Fruit extends Cell {
    public boolean superFruit;
    public boolean eaten;
    public PImage fruitImage;

    public Fruit(int x, int y, PApplet app) {
        super(x, y);
        this.superFruit = false;
        this.eaten = false;

        this.fruitImage = app.loadImage("../../../resources/main/fruit.png");
    }

    public void eat() {
        this.eaten = true;
    }

    public void draw(PApplet app) {
        if (this.eaten != true) {
            if (this.superFruit == true) {
                int doubleSize = PX_SIZE * 2;
                int centeringOffset = PX_SIZE / 2;
                int xPos = (this.x * PX_SIZE) - centeringOffset;
                int yPos = (this.y * PX_SIZE) - centeringOffset;

                app.image(this.fruitImage, xPos, yPos, doubleSize, doubleSize);
            } else {
                app.image(this.fruitImage, this.x * Cell.PX_SIZE, this.y * Cell.PX_SIZE);
            }
        }
    }
}