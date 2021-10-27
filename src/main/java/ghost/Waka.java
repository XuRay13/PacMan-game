package ghost;

import processing.core.PApplet;
import processing.core.PImage;

public class Waka extends Character {
    public boolean go;
    public boolean wakaMouthClosed;

    public PImage wakaImage;
    public PImage wakaImageClosed;

    public Waka(int x, int y, String direction, PApplet app) {
        super(x, y, app);
        this.direction = direction;
        this.go = true;
        this.wakaMouthClosed = false;

        this.wakaImage = app.loadImage("../../../resources/main/" + this.direction + ".png");
        this.wakaImageClosed = app.loadImage("../../../resources/main/playerClosed.png");
    }

    public void setDirection(String direction) {
        this.direction = direction;
        this.go = true;
        this.wakaImage = this.app.loadImage("../../../resources/main/" + this.direction + ".png");
    }

    ///// LOGIC /////
    // waka changes to next queued direction if possible (not a wall)
    public void wakaNextDirection(Game game) {
        Cell nextDirection = game.getAdjacentCell(this.x, this.y, game.queuedDirection);

        if ((nextDirection instanceof Wall) != true) {
            this.setDirection(game.queuedDirection);
        }
    }

    // running into other cell such as wall/fruit/ghost
    public void wakaNextCell(Game game) {
        int[] wakaCurrent = this.getCoordinates();

        Cell nextCell = game.getAdjacentCell(wakaCurrent[0], wakaCurrent[1], this.direction);
        nextCell.getClass().getName();

        // Hits a WALL
        if (nextCell instanceof Wall) {
            this.go = false;

            // Hits a FRUIT
        } else if (nextCell instanceof Fruit) {
            Fruit fruit = (Fruit) game.getAdjacentCell(wakaCurrent[0], wakaCurrent[1], this.direction);
            if (fruit.eaten != true) {
                if (fruit.superFruit == true) {
                    System.out.println("superfruit eaten");
                    for (Ghost g : game.ghosts) {
                        int frameCount = this.app.frameCount;
                        g.enterFrightenMode(frameCount);

                    }
                }
                fruit.eat();
                game.remainingFruits--;
            }
        }

        // Hits one of the GHOST
        for (Ghost ghost : game.ghosts) {
            if ((this.x == ghost.x && this.y == ghost.y) || (nextCell.x == ghost.x && nextCell.y == ghost.y)) {
                if (ghost.frightened == true) {
                    ghost.die();

                } else if (ghost.alive == true) {
                    // waka dies
                    game.wakaDies();
                }
            }
        }
    }

    public void tick(Game game) {
        this.wakaNextDirection(game);
        this.wakaNextCell(game);
        if (this.go == true) {
            this.move();
        }
    }

    public void draw(PApplet app, int subtick) {
        if (app.frameCount % 8 == 0) {
            this.wakaMouthClosed = !this.wakaMouthClosed;
        }

        // Don't move on subticks if not moving
        int offset = this.go ? subtick : 0;
        int[] coordinates = this.getDrawCoordinates(offset, this.go);

        if (this.wakaMouthClosed) {
            app.image(this.wakaImageClosed, coordinates[0], coordinates[1]);
        } else {
            app.image(this.wakaImage, coordinates[0], coordinates[1]);
        }
    }
}