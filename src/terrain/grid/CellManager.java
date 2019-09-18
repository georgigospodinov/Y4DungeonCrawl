package terrain.grid;

import processing.core.PApplet;

import java.util.function.Consumer;

import static main.Run.R;
import static main.ScreenManager.player;

public class CellManager {
    private int width, height;
    private final Cell[][] cells;

    public CellManager(int width, int height) {
        this.width = width;
        this.height = height;
        cells = new Cell[height][width];
        for (int i = 0; i < cells.length; i++)
            for (int j = 0; j < cells[i].length; j++)
                cells[i][j] = new Cell(j, i);
    }

    public void makeRoom(int x, int y) {
        cells[y][x].setFree();
    }

    public boolean isFree(int x, int y) {
        if (y < 0 || y >= height) return false;
        if (x < 0 || x >= width) return false;
        return cells[y][x].isFree();
    }

    public boolean isFree(Cell c) {
        return isFree(c.x, c.y);
    }

    public boolean isRoom(int x, int y) {
        // c needs to be free
        if (!isFree(x, y)) return false;

        // And at least one triple of diagonal cell and the two common neighbours.
        if (isFree(x - 1, y - 1) && isFree(x - 1, y) && isFree(x, y - 1)) return true;
        if (isFree(x - 1, y + 1) && isFree(x - 1, y) && isFree(x, y + 1)) return true;
        if (isFree(x + 1, y - 1) && isFree(x + 1, y) && isFree(x, y - 1)) return true;
        if (isFree(x + 1, y + 1) && isFree(x + 1, y) && isFree(x, y + 1)) return true;

        return false;
    }

    public void render() {
        for (Cell[] row : cells)
            for (Cell c : row)
                c.hide();

        player.forEachCellInSight(Cell::show);
        for (Cell[] row : cells)
            for (Cell c : row)
                c.render();
    }

    public Cell getRandomRoom() {
        int i, j;
        do {
            i = R.nextInt(height);
            j = R.nextInt(width);
        }
        while (!isRoom(j, i));

        return cells[i][j];
    }

    public void forEachInSlice(int x, int y, int w, int h, Consumer<Cell> action) {
        for (int j = 0; j < h; j++) {
            int rowIndex = y + j;
            if (rowIndex < 0 || rowIndex >= cells.length) continue;

            for (int i = 0; i < w; i++) {
                int colIndex = x + i;
                if (colIndex < 0 || colIndex >= cells[rowIndex].length) continue;

                Cell c = cells[rowIndex][colIndex];
                action.accept(c);
            }
        }
    }

    public boolean directlyVisible(int x1, int y1, int x2, int y2) {
        int xDiff = PApplet.abs(x2 - x1), yDiff = PApplet.abs(y2 - y1);
        int row = y1, col = x1;
        if (xDiff >= yDiff) {
            int r = yDiff != 0 ? xDiff / yDiff : 0;
            // Move r cells in x, then 1 in y
            for (int i = 0; i < yDiff; i++) {
                for (int j = 0; j < r; j++) {
                    if (x2 > x1) col++;
                    if (x1 > x2) col--;
                    if (!isFree(col, row)) return false;
                }
                if (y2 > y1) row++;
                if (y1 > y2) row--;
                if (!isFree(col, row)) return false;
            }

            while (col != x2) {
                if (x2 > x1) col++;
                if (x1 > x2) col--;
                if (!isFree(col, row)) return false;
            }
        }
        else {
            int r = xDiff != 0 ? yDiff / xDiff : 0;
            for (int i = 0; i < xDiff; i++) {
                for (int j = 0; j < r; j++) {
                    if (y2 > y1) row++;
                    if (y1 > y2) row--;
                    if (!isFree(col, row)) return false;
                }
                if (x2 > x1) col++;
                if (x1 > x2) col--;
                if (!isFree(col, row)) return false;
            }

            while (row != y2) {
                if (y2 > y1) row++;
                if (y1 > y2) row--;
                if (!isFree(col, row)) return false;
            }
        }
        return true;
    }

}
