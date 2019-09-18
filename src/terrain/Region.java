package terrain;

import terrain.grid.Cell;
import terrain.grid.CellManager;

import java.util.function.Consumer;

import static main.Run.P;
import static main.Run.R;

public class Region {

    public static final int MIN_ROOM_SIZE = P.getInt("min room size");
    public static final int MIN_SPLIT_SIZE = 2 * MIN_ROOM_SIZE + 4;
    public static final float SPLIT_CHANCE_MULTIPLIER = P.getFloat("split chance multiplier");

    // Coordinates and size are in Cells.
    public final int x, y;  // Top left coordinates
    public final int width, height;  // Size
    protected boolean divided, vertical;  // Divided?  Vertical or Horizontal
    protected Region a, b;  // Halves
    protected CellManager cells;

    public void forEachRoom(Consumer<Cell> action) {
        cells.forEachInSlice(x, y, width, height, cell -> {
            if (cell.isFree())
                action.accept(cell);
        });
    }

    private Cell closestFromA, closestFromB;
    private int distance;

    public void findClosest() {
        closestFromA = null;
        closestFromB = null;
        a.forEachRoom(ac -> b.forEachRoom(bc -> {
            int dist = ac.distance(bc);
            // If no path, definitely take this one.
            if (closestFromA == null) {
                closestFromA = ac;
                closestFromB = bc;
                distance = dist;
                return;
            }

            // If the distance is worse, ignore this pair.
            if (dist > distance) return;

            // If the distance is the same, randomly determine weather to take the new one.
            if (dist == distance && R.nextBoolean()) return;

            closestFromA = ac;
            closestFromB = bc;
            distance = dist;
        }));
    }

    protected void connectVertical() {
        findClosest();

        int eastX = closestFromA.x;
        int eastY = closestFromA.y;
        int westX = closestFromB.x;
        int westY = closestFromB.y;

        while (eastX < westX)  // Move horizontally (right)
            cells.makeRoom(++eastX, eastY);

        while (eastY < westY)  // Move vertically (down)
            cells.makeRoom(eastX, ++eastY);

        while (eastY > westY)  // Move vertically (up)
            cells.makeRoom(eastX, --eastY);

    }

    protected void connectHorizontal() {
        findClosest();

        int southX = closestFromA.x;
        int southY = closestFromA.y;
        int northX = closestFromB.x;
        int northY = closestFromB.y;

        while (southY < northY)  // Move vertically (down)
            cells.makeRoom(southX, ++southY);

        while (southX < northX)  // Move horizontally (right)
            cells.makeRoom(++southX, southY);

        while (southX > northX)  // Move horizontally (left)
            cells.makeRoom(--southX, southY);

    }

    public void fill() {
        if (divided) {
            a.fill();
            b.fill();
            if (vertical) connectVertical();
            else connectHorizontal();
        }
        else createRoom();
    }

    public Region(int x, int y, int width, int height, CellManager cells) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.cells = cells;
    }

    protected void createRoom() {
        int maxW = width - MIN_ROOM_SIZE - 1;
        int roomWidth = R.nextInt(maxW) + MIN_ROOM_SIZE;
        int maxX = width - roomWidth - 1;
        int roomX = R.nextInt(maxX) + x + 1;

        int maxH = height - MIN_ROOM_SIZE - 1;
        int roomHeight = R.nextInt(maxH) + MIN_ROOM_SIZE;
        int maxY = height - roomHeight - 1;
        int roomY = R.nextInt(maxY) + y + 1;

        cells.forEachInSlice(roomX, roomY, roomWidth, roomHeight, Cell::setFree);
    }

    protected void cut() {
        boolean canVertical = width >= MIN_SPLIT_SIZE;
        boolean canHorizontal = height >= MIN_SPLIT_SIZE;
        if (!canVertical && !canHorizontal) return;

        if (canVertical && canHorizontal)
            vertical = R.nextBoolean();
        else vertical = canVertical;
    }

    public void divide() {
        cut();
        if (vertical) {  // Divide width into 2
            int aw = width / 2;
            int bw = width - aw;
            a = new Region(x, y, aw, height, cells);
            b = new Region(x + aw, y, bw, height, cells);
        }
        else {  // Divide height into 2
            int ah = height / 2;
            int bh = height - ah;
            a = new Region(x, y, width, ah, cells);
            b = new Region(x, y + ah, width, bh, cells);
        }
        divided = true;
    }

    public void maybeDivide(float splitChance) {
        if (width < MIN_SPLIT_SIZE && height < MIN_SPLIT_SIZE) return;

        if (R.nextFloat() > splitChance) return;

        divide();
        a.maybeDivide(splitChance * SPLIT_CHANCE_MULTIPLIER);
        b.maybeDivide(splitChance * SPLIT_CHANCE_MULTIPLIER);
    }

    @Override
    public String toString() {
        return "x=" + x + ", y=" + y + ", width=" + width + ", height=" + height +
                ", divided=" + divided + ", vertical=" + vertical;
    }

}
