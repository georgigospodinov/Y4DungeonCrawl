package screens;

import items.individual.AItem;
import screens.components.ScrollList;

import static creatures.Player.SHOW_INVENTORY;
import static main.Run.P;
import static main.ScreenManager.*;
import static processing.core.PConstants.DOWN;
import static processing.core.PConstants.ENTER;
import static processing.core.PConstants.UP;

public class InventoryScreen extends AScreen {

    public static final float INVENTORY_HEIGHT = sm.getHeight() * P.getFloat("inventory height");
    public static final int DELETE_ITEM = P.getInt("delete item key");

    private final ScrollList list = new ScrollList(INVENTORY_HEIGHT);
    private final AScreen prevScreen;

    private void updateList() {
        list.clear();
        String[] lines = inventory.buildInvList();
        for (String line : lines)
            list.addEntry(line);
    }

    public InventoryScreen(AScreen prevScreen) {
        this.prevScreen = prevScreen;
        updateList();
    }

    @Override
    public void render() {
        super.render();
        list.render();
    }

    public void reactTo(int keyCode) {
        if (keyCode == SHOW_INVENTORY) {
            sm.setScreen(prevScreen);
            return;
        }

        int index = list.getSelected();
        if (keyCode == DELETE_ITEM)
            inventory.remove(index);

        switch (keyCode) {
            case UP:
                list.moveSelect(-1);
                break;
            case DOWN:
                list.moveSelect(1);
                break;
            case ENTER:
                if (player.isInCombat() && player.usedItem())
                    break;
                AItem lastUsed = inventory.get(index);
                player.setLastUsed(lastUsed);
                if (lastUsed != null)
                    lastUsed.use(index);
                break;
        }

        updateList();
    }
}
