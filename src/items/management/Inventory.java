package items.management;

import items.individual.AItem;
import items.individual.Gold;

import java.util.ArrayList;

import static main.Run.P;

public class Inventory {

    public static final int INITIAL_CAPACITY = P.getInt("initial capacity");
    private final ArrayList<AItem> items = new ArrayList<>();
    private final Gold g = new Gold(0);
    private int capacity = INITIAL_CAPACITY;

    public boolean isFull() {
        return items.size() >= capacity;
    }

    public int getGold() {
        return g.amount;
    }

    /**
     * Adds the specified item to the inventory.
     * Adding will fail if the inventory is full or if the specified item is null.
     * Repeat items are allowed (you can have a bunch of leather armors).
     *
     * @param item {@link AItem} to be added to the inventory
     * @return true if the item was successfully added, false if the item was null or if the inventory is full
     */
    public boolean add(AItem item) {
        if (item == null) return false;
        if (item instanceof Gold) {
            g.amount += ((Gold) item).amount;
            return true;
        }
        if (isFull()) return false;

        items.add(item);
        return true;
    }

    public String[] buildInvList() {
        int size = items.size();
        String[] list = new String[size];

        for (int i = 0; i < size; i++) {
            AItem item = items.get(i);
            list[i] = i + ". " + item.asInventoryEntry();
        }
        return list;
    }

    public void use(int index) {
        if (index < 0 || index >= items.size()) return;
        items.get(index).use(index);
    }

    public void remove(int index) {
        if (index < 0 || index >= items.size()) return;
        items.remove(index);
    }

    public void set(int index, AItem item) {
        if (index < 0 || index >= items.size()) return;

        if (item == null)
            items.remove(index);
        else items.set(index, item);
    }

    public AItem get(int index) {
        if (index < 0 || index >= items.size()) return null;
        return items.get(index);
    }

    public void increaseCapacity(int amount) {
        if (amount < 1) return;

        // Avoid integer overflow.
        int nextCapacity = capacity + amount;
        if (nextCapacity < 0) return;
        capacity += amount;
    }

}
