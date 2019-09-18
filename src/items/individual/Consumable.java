package items.individual;

import static main.ScreenManager.inventory;
import static main.ScreenManager.player;

public class Consumable extends AItem {

    public Consumable(String name, String invDescription, int value, float dropChance, int color, float renderFactor) {
        super(name, invDescription, value, dropChance, color, renderFactor);
    }

    @Override
    public AItem copy() {
        return new Consumable(name, invDescription, value, dropChance, color, renderFactor);
    }

    @Override
    public void use(int inventoryIndex) {
        inventory.remove(inventoryIndex);
        player.receiveHeal(value);
    }
}
