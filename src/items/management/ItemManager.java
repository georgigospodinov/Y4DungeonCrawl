package items.management;

import items.individual.AItem;
import terrain.grid.Cell;

import java.util.LinkedHashSet;
import java.util.LinkedList;

import static main.ScreenManager.*;

public class ItemManager {

    private final LinkedHashSet<AItem> items = new LinkedHashSet<>();

    public int rollItem(Cell c, int maxValue, int depth) {
        AItem item = itemLoader.getRandomItem(c, maxValue, depth);
        if (item == null) return maxValue;
        items.add(item);
        return item.value;
    }

    public void render() {
        checkLooting();
        items.forEach(item -> player.forEachCellInSight(cell -> {
            if (cell.equals(item.getCell()))
                item.render();
        }));
    }

    public void checkLooting() {
        LinkedList<AItem> looted = new LinkedList<>();
        player.getCells().forEach(pc -> items.forEach(i -> {
            // If the player is occupying a cell where there is an item. That item will be looted.
            if (pc.equals(i.getCell()))
                looted.add(i);
        }));
        for (AItem loot : looted) {
            if (!inventory.add(loot)) break;
            items.remove(loot);
        }
    }

    public boolean occupies(Cell c) {
        for (AItem i : items)
            if (i.getCell().equals(c))
                return true;

        return false;
    }
}
