package items.individual;

import processing.core.PApplet;

import static main.ScreenManager.sm;

public class Gold extends AItem {

    private static final int GOLD_COLOR = sm.color(255, 215, 0);
    private static final float GOLD_SIZE_FACTOR = 0.05f;

    public int amount;

    public Gold(int amount) {
        super("Gold", "A heap of gold.", amount, 1, GOLD_COLOR, PApplet.min(1, amount * GOLD_SIZE_FACTOR));
        this.amount = amount;
    }

    @Override
    public AItem copy() {
        return new Gold(amount);
    }

    @Override
    public void use(int inventoryIndex) {
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof Gold)) return false;
        Gold other = (Gold) obj;
        return this.amount == other.amount;
    }
}
