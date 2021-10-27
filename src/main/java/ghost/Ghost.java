package ghost;

import processing.core.PApplet;
import processing.core.PImage;

import java.util.*;
import java.lang.Math;

public class Ghost extends Character {
    public String name;
    public PImage ghostImage;
    public PImage ghostImageFrightened;
    public int[] scatterCoords;
    public boolean frightened;
    public long frightenedLength;
    public int frightenedEnd;
    public int[] targetLocation;
    public boolean alive;

    Ghost(int x, int y, String ghostName, PApplet app) {
        super(x, y, app);
        this.name = ghostName;

        this.frightened = false;
        this.targetLocation = null;

        this.ghostImage = app.loadImage("../../../resources/main/" + ghostName + ".png");
        this.ghostImageFrightened = this.app.loadImage("../../../resources/main/frightened.png");

        this.alive = true;
        this.setScatterCoords();
    }

    public void die() {
        this.alive = false;
    }

    /**
     * @param game
     * @return Return directions minus the opposite direction the Ghost is currently
     *         facing as it can't go backwards
     */
    public ArrayList<String> getPossibleDirections(Game game) {
        ArrayList<String> validDirections = new ArrayList<String>();
        ArrayList<String> possibleDirections = new ArrayList<String>();

        if (this.direction.equals("Down") == false) {
            validDirections.add("Up");
        }
        if (this.direction.equals("Up") == false) {
            validDirections.add("Down");
        }
        if (this.direction.equals("Right") == false) {
            validDirections.add("Left");
        }
        if (this.direction.equals("Left") == false) {
            validDirections.add("Right");
        }

        for (String direction : validDirections) {
            Cell cell = game.getAdjacentCell(this.x, this.y, direction);
            if (cell instanceof Wall == false) {
                possibleDirections.add(direction);
            }
        }

        return possibleDirections;
    }

    public void setScatterCoords() {
        int[] topLeft = new int[] { 0, 0 };
        int[] topRight = new int[] { 0, 27 };
        int[] bottomLeft = new int[] { 35, 0 };
        int[] bottomRight = new int[] { 35, 27 };

        // Find this ghosts disignated corner
        int[] corner = null;
        if (this.name.equals("ambusher")) {
            corner = topRight;
        } else if (this.name.equals("chaser")) {
            corner = topLeft;
        } else if (this.name.equals("ignorant")) {
            corner = bottomLeft;
        } else if (this.name.equals("whim")) {
            corner = bottomRight;
        }

        this.scatterCoords = corner;
    }

    /**
     * Get the chase target coordinates based on Ghost type
     * 
     * @param game
     * @return Target coordinates
     */
    private int[] getChaseTarget(Game game) {
        int[] target;
        if (this.name == "chaser") {
            target = game.waka.getCoordinates();
        } else if (this.name == "ambusher") {
            Cell nextCell = game.getAdjacentCell(game.waka.x, game.waka.y, game.waka.getDirection());
            for (int i = 0; i < 2; i++) {
                try {
                    nextCell = game.getAdjacentCell(nextCell.x, nextCell.y, game.waka.getDirection());

                } catch (Exception e) {
                    break;
                }
                i++;
            }

            target = nextCell.getCoordinates();
        } else if (this.name == "ignorant") {
            double x = game.waka.x - this.x;
            double y = game.waka.y - this.y;
            double distance = Math.pow(Math.pow(y, 2) + Math.pow(x, 2), 0.5);
            if (distance < 8) {
                target = game.waka.getCoordinates();
            } else {
                // target scatter corner
                target = this.scatterCoords;
            }
        } else {
            // two grid spacesahead of chaser's target then double the vector
            Cell nextCell = game.getAdjacentCell(game.waka.x, game.waka.y, game.waka.direction);
            try {
                nextCell = game.getAdjacentCell(nextCell.x, nextCell.y, game.waka.direction);
            } catch (Exception e) {
                // edge of map
            }

            Ghost chaser = null;
            for (Ghost gh : game.ghosts) {
                if (gh.name == "chaser") {
                    chaser = gh;
                }
            }

            int xDisplacement = (nextCell.x - chaser.x) * 2;
            int yDisplacement = (nextCell.y - chaser.y) * 2;

            target = new int[] { chaser.x + xDisplacement, chaser.y + yDisplacement };
        }

        return target;
    }

    /**
     * Return the direction of the adjacent cell that is the shortest distance from
     * the target. Shortest distance is based on straight-line distance
     * 
     * @return direction
     */
    private String chooseIntersection(int[] targetCoordinate, Game game) {
        ArrayList<String> possibleDirections = getPossibleDirections(game);

        double shortestDistance = -1;
        String shortest = null;

        for (String direction : possibleDirections) {

            Cell cell = game.getAdjacentCell(this.x, this.y, direction);

            double x = cell.x - targetCoordinate[0];
            double y = cell.y - targetCoordinate[1];

            double distance = Math.pow(Math.pow(y, 2) + Math.pow(x, 2), 0.5);

            if (shortest == null) {
                shortest = direction;
                shortestDistance = distance;
            } else if (distance < shortestDistance) {
                shortest = direction;
                shortestDistance = distance;
            }
        }

        return shortest;
    }

    ///// MODES (Scatter : Chase : Frighten) /////
    public void enterFrightenMode(int frameCount) {
        // return back to normal modes and images
        System.out.println("FRIGHTENED");
        System.out.println("Frame Count begin: " + frameCount);
        this.frightened = true;

        int frightenedLength = (int) this.frightenedLength;
        int frameRate = App.FRAME_RATE;

        this.frightenedEnd = frameCount + (frightenedLength * frameRate);

        System.out.println("Frame Count Ends: " + this.frightenedEnd);
    }

    private void updateNextDirection(Game game) {
        if (this.frightened == true) {
            // Select random direction
            Random rand = new Random();
            ArrayList<String> directions = getPossibleDirections(game);
            int randomNum = rand.nextInt(directions.size());
            String randomDirection = directions.get(randomNum);
            this.setDirection(randomDirection);
        } else if (game.scatterMode == true) {
            this.targetLocation = this.scatterCoords;
            this.setDirection(chooseIntersection(this.scatterCoords, game));
        } else {
            int[] targetCoordinate = this.getChaseTarget(game);
            this.setDirection(this.chooseIntersection(targetCoordinate, game));
            this.targetLocation = targetCoordinate;
        }
    }

    public void tick(Game game) {
        this.updateNextDirection(game);
        this.move();
    }

    public void draw(PApplet app, int offset, boolean showLine) {
        int lineColor = 0xFFFFFFFF;

        // Check if frightened has ended
        if (this.frightened && (this.frightenedEnd == app.frameCount)) {
            this.frightened = false;
        }

        if (this.alive == true) {
            int[] coordinates = getDrawCoordinates(offset, true);

            int xPos = coordinates[0];
            int yPos = coordinates[1];

            // Draw line
            if (showLine == true && this.frightened == false && this.targetLocation != null) {
                app.stroke(lineColor);
                app.strokeWeight(2);
                app.line(xPos + 16, yPos + 16, targetLocation[0] * Cell.PX_SIZE + 8,
                        targetLocation[1] * Cell.PX_SIZE + 8);
            }

            if (this.frightened) {
                app.image(this.ghostImageFrightened, xPos, yPos);
            } else {
                app.image(this.ghostImage, xPos, yPos);
            }

        }

    }
}