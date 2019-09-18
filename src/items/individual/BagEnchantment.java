package items.individual;

import static main.Run.P;
import static main.ScreenManager.inventory;
import static main.ScreenManager.sm;

public class BagEnchantment extends AItem {

    private static final int BAG_ENCHANTMENT_COLOR;
    private static final float BAG_ENCHANTMENT_RENDER_FACTOR = P.getFloat("bag enchantment render factor");

    static {
        int red, green, blue;
        red = P.getInt("bag enchantment color red");
        green = P.getInt("bag enchantment color green");
        blue = P.getInt("bag enchantment color blue");
        BAG_ENCHANTMENT_COLOR = sm.color(red, green, blue);
    }

    public BagEnchantment(int value) {
        super("Bag Enchantment", "Expand the bag with extra-dimensional space, so that you can store more items.",
                value, 1, BAG_ENCHANTMENT_COLOR, BAG_ENCHANTMENT_RENDER_FACTOR);
    }

    @Override
    public AItem copy() {
        return new BagEnchantment(value);
    }

    @Override
    public void use(int inventoryIndex) {
        inventory.set(inventoryIndex, null);
        inventory.increaseCapacity(value);
    }

    @Override
    public String asInventoryEntry() {
        return super.asInventoryEntry() + "  +" + value + " inventory capacity";
    }
}
