package ghost;

import java.util.*;
import java.io.*;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import processing.core.PApplet;
import processing.core.PFont;
import processing.core.PImage;

public class Game {
    public ArrayList<ArrayList<Cell>> grid;
    public String mapFile;
    public Waka waka;
    public long lives;
    public long frightenedLength;
    public List<Long> modes;
    public Integer modeIndex;
    public long frameRateEndIterationAt;
    public boolean scatterMode;
    public List<Ghost> ghosts;
    public boolean win;
    public boolean lose;

    public PApplet app;
    public String queuedDirection;
    public int remainingFruits;
    public PFont font;
    public int framesPerTick;
    private PImage lifeImage;

    public boolean showLine;

    // Constructor
    public Game(PApplet app) {
        this.grid = new ArrayList<ArrayList<Cell>>();
        this.modes = new ArrayList<Long>();
        this.modeIndex = null;
        this.queuedDirection = "Left";
        this.ghosts = new ArrayList<Ghost>();
        this.app = app;
        this.scatterMode = true;
        this.frameRateEndIterationAt = 0;
        this.showLine = false;
        this.framesPerTick = 16;

        // app.loadFont("../../../resources/main/PressStart2P-Regular.ttf");
        this.font = app.createFont("../../../resources/main/PressStart2P-Regular.ttf", 16);
        this.lifeImage = app.loadImage("../../../resources/main/Right.png");
        app.textFont(this.font);
        app.textAlign(App.CENTER, App.CENTER);
    }

    ///// METHODS /////

    /**
     * Queue the next players direction.
     * 
     * @param direction
     */
    public void queueDirection(String direction) {
        this.queuedDirection = direction;
    }

    /**
     * Given coordinates and a direction, return the adjacent cell
     * 
     * @param x
     * @param y
     * @param direction
     * @return Adjacent cell
     */
    public Cell getAdjacentCell(int x, int y, String direction) {
        if (direction.equals("Left")) {
            return this.grid.get(y).get(x - 1);

        } else if (direction.equals("Right")) {
            return this.grid.get(y).get(x + 1);

        } else if (direction.equals("Up")) {
            return this.grid.get(y - 1).get(x);

        } else if (direction.equals("Down")) {
            return this.grid.get(y + 1).get(x);
        }

        return null;
    }

    /**
     * Reset characters to starting position and decrease game lives
     */
    public void wakaDies() {
        this.lives--;

        // reset game
        this.queuedDirection = "Left";
        this.waka.returnToStarting();
        for (Ghost ghost : this.ghosts) {
            ghost.returnToStarting();
            ghost.alive = true;
        }
    }

    /**
     * Alternate between Scatter and Chase modes based on time intervals set in
     * config
     */
    private void alternateModes() {
        int frameRate = App.FRAME_RATE;
        int frameCount = (int) this.app.frameCount;

        if (this.modeIndex == null || this.modeIndex == this.modes.size() - 1) {
            this.modeIndex = 0;
            this.frameRateEndIterationAt += (this.modes.get(this.modeIndex) * frameRate);
        }

        if (frameCount == this.frameRateEndIterationAt) {
            this.scatterMode = !this.scatterMode;
            this.modeIndex++;
            this.frameRateEndIterationAt += (this.modes.get(this.modeIndex) * frameRate);
        }

    }

    /**
     * Read configuration file
     * 
     * @param jsonFilename path to config file
     */
    public void readConfiguration(String jsonFilename) {
        try {
            JSONParser jsonParser = new JSONParser();
            FileReader fileReader = new FileReader(jsonFilename);
            Object javaObj = jsonParser.parse(fileReader);

            JSONObject jsonObj = (JSONObject) javaObj;

            this.mapFile = (String) jsonObj.get("map");
            this.lives = (long) jsonObj.get("lives");
            long speed = (long) jsonObj.get("speed");
            this.framesPerTick = 16 / (int) speed;
            this.frightenedLength = (long) jsonObj.get("frightenedLength");

            JSONArray array = (JSONArray) jsonObj.get("modeLengths");
            this.modes = new ArrayList<Long>();
            for (int i = 0; i < array.size(); i++) {
                long mode = (long) array.get(i);
                this.modes.add(mode);
            }

        } catch (IOException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    /**
     * Read in the map file and create the game objects
     * 
     * @param app
     */
    public void initializeGame(PApplet app) {
        // Create Cell objects from mapFile

        try {
            File file = new File(this.mapFile);
            Scanner scan = new Scanner(file);

            int row = 0;
            while (scan.hasNextLine()) {
                String line = scan.nextLine();

                String[] lineSplit = line.split("(?!^)"); // ["0", "0", "0", "0", "0"...]
                ArrayList<Cell> lineCells = new ArrayList<Cell>();
                int col = 0;
                while (col < lineSplit.length) {
                    // Cell cellType;

                    if (lineSplit[col].equals("1")) {
                        Wall cellType = new Wall(col, row, "horizontal", app);
                        lineCells.add(cellType);

                    } else if (lineSplit[col].equals("2")) {
                        Wall cellType = new Wall(col, row, "vertical", app);
                        lineCells.add(cellType);

                    } else if (lineSplit[col].equals("3")) {
                        Wall cellType = new Wall(col, row, "upLeft", app);
                        lineCells.add(cellType);

                    } else if (lineSplit[col].equals("4")) {
                        Wall cellType = new Wall(col, row, "upRight", app);
                        lineCells.add(cellType);

                    } else if (lineSplit[col].equals("5")) {
                        Wall cellType = new Wall(col, row, "downLeft", app);
                        lineCells.add(cellType);

                    } else if (lineSplit[col].equals("6")) {
                        Wall cellType = new Wall(col, row, "downRight", app);
                        lineCells.add(cellType);

                    } else if (lineSplit[col].equals("7")) {
                        Fruit cellType = new Fruit(col, row, app);
                        this.remainingFruits++;
                        lineCells.add(cellType);

                    } else if (lineSplit[col].equals("8")) {
                        Fruit cellType = new Fruit(col, row, app);
                        cellType.superFruit = true;
                        this.remainingFruits++;
                        lineCells.add(cellType);

                    } else if (lineSplit[col].equals("p")) {
                        this.waka = new Waka(col, row, "Left", app);
                        lineCells.add(new Cell(col, row));

                    } else if (lineSplit[col].equals("a")) {
                        Ghost ghost = new Ghost(col, row, "ambusher", app);
                        ghost.frightenedLength = this.frightenedLength;
                        this.ghosts.add(ghost);
                        lineCells.add(new Cell(col, row));

                    } else if (lineSplit[col].equals("c")) {
                        Ghost ghost = new Ghost(col, row, "chaser", app);
                        ghost.frightenedLength = this.frightenedLength;
                        this.ghosts.add(ghost);
                        lineCells.add(new Cell(col, row));

                    } else if (lineSplit[col].equals("i")) {
                        Ghost ghost = new Ghost(col, row, "ignorant", app);
                        ghost.frightenedLength = this.frightenedLength;
                        this.ghosts.add(ghost);
                        lineCells.add(new Cell(col, row));

                    } else if (lineSplit[col].equals("w")) {
                        Ghost ghost = new Ghost(col, row, "whim", app);
                        ghost.frightenedLength = this.frightenedLength;
                        this.ghosts.add(ghost);
                        lineCells.add(new Cell(col, row));

                    } else {
                        Cell cellType = new Cell(col, row);
                        lineCells.add(cellType);
                    }
                    col++;
                }

                this.grid.add(lineCells);
                row++;

            }

            scan.close();

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    public void tick() {
        this.waka.tick(this);
        for (Ghost g : this.ghosts) {
            g.tick(this);
        }

    }

    private void drawLives() {
        for (int i = 0; i < this.lives; i++) {
            int x = i * Character.PX_SIZE;
            int y = App.HEIGHT - Character.PX_SIZE;
            app.image(this.lifeImage, x, y);
        }
    }

    /**
     * Draw each element (walls, fruits, waka, ghosts, lives) of the game Also call
     * tick based on frame count
     */
    public void draw(PApplet app) {
        if (this.lives == 0) {
            this.app.text("GAME OVER", App.WIDTH / 2, App.HEIGHT / 2);
        } else if (this.remainingFruits == 0) {
            this.app.text("YOU WIN", App.WIDTH / 2, App.HEIGHT / 2);
        } else {
            // Get the frames since last tick
            int subtick = app.frameCount % this.framesPerTick;
            if (subtick == 0) {
                this.tick();
            }

            this.alternateModes();

            // Draw lives
            this.drawLives();

            // Draw ghosts
            for (Ghost g : this.ghosts) {
                g.draw(app, subtick, this.showLine);
            }

            // Draw waka
            this.waka.draw(app, subtick);

            // Draw cells
            for (ArrayList<Cell> row : this.grid) {
                for (Cell cell : row) {
                    if (cell != null) {
                        cell.draw(app);
                    }
                }
            }
        }
    }
}