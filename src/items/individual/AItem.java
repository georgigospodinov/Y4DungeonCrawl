package items.individual;

import terrain.AObject;
import terrain.grid.Cell;

import static main.ScreenManager.sm;
import static processing.core.PConstants.CORNER;

public abstract class AItem extends AObject {

    public final String name, invDescription;
    public final int value, color;
    public final float dropChance, renderFactor;

    public abstract AItem copy();

    public abstract void use(int inventoryIndex);

    public AItem(String name, String invDescription, int value, float dropChance, int color, float renderFactor) {
        super(-10, -10);
        this.name = name;
        this.invDescription = invDescription;
        this.value = value;
        this.dropChance = dropChance;
        this.color = color;
        this.renderFactor = renderFactor;
    }

    public String asInventoryEntry() {
        return name + " - " + invDescription;
    }

    @Override
    public String toString() {
        return "name=\"" + name + "\"" +
                ", invDescription=\"" + invDescription + "\"" +
                ", value=" + value +
                ", dropChance=" + dropChance +
                ", x=" + x +
                ", y=" + y;
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof AItem)) return false;

        AItem other = (AItem) obj;
        return this.name.equals(other.name) &&
                this.invDescription.equals(other.invDescription);
    }

    @Override
    public int hashCode() {
        return name.hashCode();
    }

    @Override
    public void render() {
        sm.stroke(color);
        sm.fill(color);
        float a = x * Cell.WIDTH + (1 - renderFactor) * Cell.WIDTH / 2;
        float b = y * Cell.HEIGHT + (1 - renderFactor) * Cell.HEIGHT / 2;
        sm.ellipseMode(CORNER);
        sm.ellipse(a, b, Cell.WIDTH * renderFactor, Cell.HEIGHT * renderFactor);
    }
}
