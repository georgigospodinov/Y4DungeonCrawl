package terrain;

import processing.core.PApplet;
import terrain.grid.Cell;

import static main.ScreenManager.sm;

public abstract class AObject {
    public int x, y;

    public void setCell(Cell c) {
        x = c.x;
        y = c.y;
    }

    public Cell getCell() {
        return new Cell(x, y);
    }

    public AObject(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public abstract void render();

    // Manhattan distance.
    public int distance(AObject other) {
        int xDiff = PApplet.abs(this.x - other.x);
        int yDiff = PApplet.abs(this.y - other.y);
        return xDiff + yDiff;
    }

    @Override
    public String toString() {
        return "x=" + x + ", y=" + y;
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof AObject)) return false;

        AObject other = (AObject) obj;
        return this.x == other.x && this.y == other.y;
    }

    @Override
    public int hashCode() {
        int blocksPerRow = (int) (sm.getWidth() / Cell.WIDTH);
        return x + y * blocksPerRow;
    }
}
