package terrain.grid;

import terrain.AObject;

import static main.Run.P;
import static main.ScreenManager.sm;

public class Cell extends AObject {
    public static final float WIDTH = sm.getWidth() * P.getFloat("cell width");
    public static final float HEIGHT = sm.getHeight() * P.getFloat("cell height");
    public static final int ROOM_COLOR, EXIT_COLOR, WALL_COLOR, SEEN_COLOR;

    static {
        int red, green, blue;
        int seenRed, seenGreen, seenBlue;

        red = P.getInt("room color red");
        green = P.getInt("room color green");
        blue = P.getInt("room color blue");
        ROOM_COLOR = sm.color(red, green, blue);

        seenRed = red / 2;
        seenGreen = green / 2;
        seenBlue = blue / 2;

        red = P.getInt("wall color red");
        green = P.getInt("wall color green");
        blue = P.getInt("wall color blue");
        WALL_COLOR = sm.color(red, green, blue);

        seenRed += red / 2;
        seenGreen += green / 2;
        seenBlue += blue / 2;
        SEEN_COLOR = sm.color(seenRed, seenGreen, seenBlue);

        red = P.getInt("exit color red");
        green = P.getInt("exit color green");
        blue = P.getInt("exit color blue");
        EXIT_COLOR = sm.color(red, green, blue);
    }

    private boolean end;
    private boolean free = false;
    private boolean visible = false;
    private boolean seen = false;

    public void show() {
        this.visible = true;
        this.seen = true;
    }

    public void hide() {
        this.visible = false;
    }

    public void setEnd() {
        end = true;
    }

    public void setFree() {
        free = true;
    }

    public boolean isFree() {
        return free;
    }

    public Cell(int x, int y) {
        super(x, y);
    }

    public void render() {
        if (!visible) {
            sm.fill(seen ? SEEN_COLOR : WALL_COLOR);
            sm.stroke(seen ? SEEN_COLOR : WALL_COLOR);
        }
        else if (end) {
            sm.fill(EXIT_COLOR);
            sm.stroke(EXIT_COLOR);
        }
        else if (free) {
            sm.fill(ROOM_COLOR);
            sm.stroke(ROOM_COLOR);
        }

        sm.rect(x * WIDTH, y * HEIGHT, WIDTH, HEIGHT);
    }

}
