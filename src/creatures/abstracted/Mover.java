package creatures.abstracted;

import processing.core.PApplet;
import processing.core.PVector;
import terrain.Dungeon;
import terrain.grid.Cell;

import java.util.LinkedHashSet;

import static main.ScreenManager.sm;
import static processing.core.PApplet.PI;
import static processing.core.PApplet.atan2;
import static processing.core.PConstants.CENTER;

public abstract class Mover {

    protected static final int NO_COLOR = -1;
    public static final float ORIENTATION_INCREMENT = PI / 32;
    public static final float SAT_RADIUS = 0.1f;
    public static final float MAX_SPEED = 8f;

    private PVector position, velocity = new PVector();
    protected float speed;
    public final float width, height;
    private float orientation = 0;
    private int strokeColor, fillColor;
    protected Dungeon d;

    public int getX() {
        return (int) position.x;
    }

    public int getY() {
        return (int) position.y;
    }

    public int getLeftCellX() {
        return (int) (position.x / Cell.WIDTH);
    }

    public int getRightCellX() {
        return (int) ((position.x + width) / Cell.WIDTH);
    }

    public int getUpCellY() {
        return (int) (position.y / Cell.HEIGHT);
    }

    public int getDownCellY() {
        return (int) ((position.y + height) / Cell.HEIGHT);
    }

    public LinkedHashSet<Cell> getCells() {
        LinkedHashSet<Cell> cells = new LinkedHashSet<>();
        int x1 = getLeftCellX();
        int y1 = getUpCellY();
        int x2 = getRightCellX();
        int y2 = getDownCellY();

        cells.add(new Cell(x1, y1));
        cells.add(new Cell(x1, y2));
        cells.add(new Cell(x2, y1));
        cells.add(new Cell(x2, y2));

        return cells;
    }

    public void setDungeon(Dungeon d) {
        this.d = d;
    }

    public void setLocation(Cell c) {
        float x = c.x * Cell.WIDTH + width / 2;
        float y = c.y * Cell.HEIGHT + height / 2;
        position = new PVector(x, y);
    }

    public void setStrokeColor(int strokeColor) {
        this.strokeColor = strokeColor;
    }

    public void setFillColor(int fillColor) {
        this.fillColor = fillColor;
    }

    public float euclidean(Cell c) {
        // Distance from centers
        float x1 = c.x * Cell.WIDTH + Cell.WIDTH / 2;
        float y1 = c.y * Cell.HEIGHT + Cell.HEIGHT / 2;
        float x2 = position.x + width / 2;
        float y2 = position.y + height / 2;
        return PApplet.dist(x1, y1, x2, y2);
    }

    public float euclidean(Mover other) {
        return this.position.dist(other.position);
    }

    public boolean withinView(Cell cell) {
        float x = cell.x * Cell.WIDTH + Cell.WIDTH / 2;
        float y = cell.y * Cell.HEIGHT + Cell.HEIGHT / 2;
        PVector c = new PVector(x, y);
        PVector a = PVector.sub(position, c);
        float ang = a.heading();
        if (-PI / 2 < orientation && orientation < PI / 2)
            return orientation - PI / 2 <= ang && ang <= orientation + PI / 2;
        else if (orientation >= PI / 2)
            return orientation - PI / 2 <= ang || ang <= orientation - PI * 2 + PI / 2;
        else if (orientation <= -PI / 2)
            return orientation + 2 * PI - PI / 2 <= ang || ang <= orientation + PI / 2;

        return false;
    }

    public Mover(Cell c) {
        this.width = Cell.WIDTH / 2;
        this.height = Cell.HEIGHT / 2;
        float x = c.x * Cell.WIDTH + width / 2;
        float y = c.y * Cell.HEIGHT + height / 2;
        position = new PVector(x, y);
    }

    private void calculateVelocity(PVector toTarget) {
        velocity = toTarget.copy();
        if (toTarget.mag() > MAX_SPEED) {
            velocity.normalize();
            velocity.mult(MAX_SPEED);
        }
    }

    private boolean inScreenX() {
        return position.x >= 0 && position.x + width <= sm.getWidth();
    }

    private boolean inScreenY() {
        return position.y >= 0 && position.y + height <= sm.getHeight();
    }

    private void bounceOffScreenEdge() {
        // Apply an impulse to bounce off the edge of the screen

        if (!inScreenX())
            velocity.x = -velocity.x;
        if (!inScreenY())
            velocity.y = -velocity.y;

        while (!inScreenX() || !inScreenY())
            position.add(velocity);

        if (notInDungeon()) {
            velocity.x = -velocity.x;
            velocity.y = -velocity.y;
            while (notInDungeon())
                position.add(velocity);
        }
    }

    private void normalizeOrientation() {
        if (orientation > PI)
            orientation -= 2 * PI;
        else if (orientation < -PI)
            orientation += 2 * PI;
    }

    private void reorient(float targetOrientation) {
        // Will take a frame extra at the PI boundary
        if (PApplet.abs(targetOrientation - orientation) <= ORIENTATION_INCREMENT) {
            orientation = targetOrientation;
            return;
        }

        // if it's less than me, then how much if up to PI less, decrease otherwise increase
        if (targetOrientation < orientation) {
            if (orientation - targetOrientation < PI)
                orientation -= ORIENTATION_INCREMENT;
            else orientation += ORIENTATION_INCREMENT;
        }
        else {
            if (targetOrientation - orientation < PI)
                orientation += ORIENTATION_INCREMENT;
            else orientation -= ORIENTATION_INCREMENT;
        }
    }

    public void spinTowards(Mover other) {
        float x = other.position.x - this.position.x;
        float y = other.position.y - this.position.y;
        reorient(atan2(y, x));
    }

    public void integrate(PVector toTarget) {
        float distance = toTarget.mag();

        // If close enough, done.
        if (distance < SAT_RADIUS) return;

        calculateVelocity(toTarget);
        position.add(velocity);
        bounceOffScreenEdge();

        //move a bit towards velocity: turn vel into orientation
        float targetOrientation = atan2(velocity.y, velocity.x);
        reorient(targetOrientation);

        normalizeOrientation();
    }

    public void move(float x, float y) {
        PVector direction = new PVector(x, y).normalize();
        direction.mult(speed);
        integrate(direction);
    }

    public void moveLeft() {
        move(-1, 0);
    }

    public void moveRight() {
        move(1, 0);
    }

    public void moveUp() {
        move(0, -1);
    }

    public void moveDown() {
        move(0, 1);
    }

    // Overridden by specter monster.
    public boolean notInDungeon() {
        return !d.inDungeon(this);
    }

    public void render() {
        sm.stroke(strokeColor);
        if (fillColor == NO_COLOR) sm.noFill();
        else sm.fill(fillColor);
        sm.rect(position.x, position.y, width, height);
        // Show orientation
        float w2 = width / 2, h2 = height / 2;
        float newxe = position.x + w2 + w2 * PApplet.cos(orientation);
        float newye = position.y + h2 + h2 * PApplet.sin(orientation);
        sm.fill(0);
        sm.ellipseMode(CENTER);
        sm.ellipse(newxe, newye, w2, h2);
    }
}
