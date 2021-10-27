package ghost;

import processing.core.PApplet;

public class App extends PApplet {

    public static final int WIDTH = 448;
    public static final int HEIGHT = 576;

    public static final int FRAME_RATE = 60;

    public Game game;

    public App() {
        // Set up your objects
    }

    public void settings() {
        size(WIDTH, HEIGHT);
    }

    public void setup() {
        frameRate(FRAME_RATE);

        // Load images
        this.game = new Game(this);
        this.game.readConfiguration("config.json");
        this.game.initializeGame(this);
    }

    public void draw() {
        background(0, 0, 0);
        this.game.draw(this);
    }

    public void keyPressed() {
        // System.out.println("changed to " + this.keyCode);

        if (this.keyCode == 37) {
            System.out.println("move left");
            this.game.queueDirection("Left");

        } else if (this.keyCode == 39) {
            System.out.println("move right");
            this.game.queueDirection("Right");

        } else if (this.keyCode == 38) {
            System.out.println("move up");
            this.game.queueDirection("Up");

        } else if (this.keyCode == 40) {
            System.out.println("move down");
            this.game.queueDirection("Down");

        } else if (this.keyCode == 32) {
            game.showLine = !game.showLine;
        }
    }

    public static void main(String[] args) {
        PApplet.main("ghost.App");
    }

}
