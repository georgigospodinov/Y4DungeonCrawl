package items.management;

import items.individual.*;
import processing.core.PApplet;
import terrain.grid.Cell;
import util.Props;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.function.Consumer;

import static items.management.Inventory.INITIAL_CAPACITY;
import static main.Run.*;
import static main.ScreenManager.sm;

public class ItemLoader {
    private static final String ITEMS_ROOT = "assets/items/";
    private static final String DESCRIPTION_FILE = "des";
    private static final int MAX_TORCH_RANGE = P.getInt("max torch range");

    private final Props current = new Props();
    private final LinkedHashSet<AItem> items = new LinkedHashSet<>();

    public ItemLoader() {
        loadItems("armors", this::loadArmor);
        loadItems("weapons", this::loadWeapon);
        loadItems("consumables", this::loadConsumable);
    }

    private void loadArmor(String filename) {
        try {
            current.load(filename);
        }
        catch (FileNotFoundException e) {
            L.log(e);
            return;
        }

        String name = current.getString("name");
        String invDesc = current.getString("inventory description");
        int value = current.getInt("value");
        float dropChance = current.getFloat("drop chance");
        float renderFactor = current.getFloat("render factor");
        int red = current.getInt("color red");
        int green = current.getInt("color green");
        int blue = current.getInt("color blue");
        int color = sm.color(red, green, blue);
        int defenseBonus = current.getInt("defense bonus");
        int agilityPenalty = current.getInt("agility penalty");
        boolean magical = current.getString("magical").equals("yes");
        Armor a = new Armor(name, invDesc, value, dropChance, renderFactor, color, defenseBonus, agilityPenalty, magical);
        items.add(a);

        current.clear();
    }

    private void loadWeapon(String filename) {
        try {
            current.load(filename);
        }
        catch (FileNotFoundException e) {
            L.log(e);
            return;
        }

        String name = current.getString("name");
        String invDesc = current.getString("inventory description");
        int value = current.getInt("value");
        float dropChance = current.getFloat("drop chance");
        float renderFactor = current.getFloat("render factor");
        int red = current.getInt("color red");
        int green = current.getInt("color green");
        int blue = current.getInt("color blue");
        int color = sm.color(red, green, blue);
        int attackBonus = current.getInt("attack bonus");
        String enemyText = current.getString("enemy text");
        String attackDescription = current.getString("attack description");
        String onDamage = current.getString("on damage");
        String noDamage = current.getString("no damage");
        String kill = current.getString("kill");
        boolean magical = current.getString("magical").equals("yes");
        Weapon w = new Weapon(name, invDesc, value, dropChance, color, renderFactor,
                attackBonus, enemyText, attackDescription, onDamage, noDamage, kill, magical);
        items.add(w);

        current.clear();
    }

    private void loadConsumable(String filename) {
        try {
            current.load(filename);
        }
        catch (FileNotFoundException e) {
            L.log(e);
            return;
        }

        String name = current.getString("name");
        String invDesc = current.getString("inventory description");
        int value = current.getInt("value");
        float dropChance = current.getFloat("drop chance");
        float renderFactor = current.getFloat("render factor");
        int red = current.getInt("color red");
        int green = current.getInt("color green");
        int blue = current.getInt("color blue");
        int color = sm.color(red, green, blue);
        Consumable c = new Consumable(name, invDesc, value, dropChance, color, renderFactor);
        items.add(c);

        current.clear();
    }

    private void loadItems(String pathname, Consumer<String> loader) {
        File folder = new File(ITEMS_ROOT + pathname);
        File[] dirs = folder.listFiles();
        if (dirs == null) return;

        for (File dir : dirs) {
            File[] fs = dir.listFiles((file, s) -> s.endsWith(DESCRIPTION_FILE));
            if (fs == null) continue;

            loader.accept(fs[0].getPath());
        }
    }

    private ArrayList<AItem> getPossibleItems(int value, float roll, int depth) {
        ArrayList<AItem> possible = new ArrayList<>();
        items.forEach(i -> {
            if (i.value <= value && roll < i.dropChance * depth)
                possible.add(i);
        });
        if (value <= 1) return possible;

        int amount = (int) (value * (1 - roll));
        if (amount < 1) amount = 1;
        Gold g = new Gold(amount);
        possible.add(g);

        int torchValue = (int) (PApplet.min(value, MAX_TORCH_RANGE) * (1 - roll));
        if (torchValue < 1) torchValue = 1;
        Torch t = new Torch(torchValue);
        possible.add(t);

        int enchantment = (int) (PApplet.min(value, INITIAL_CAPACITY) * (1 - roll));
        if (enchantment < 1) enchantment = 1;
        BagEnchantment b = new BagEnchantment(enchantment);
        possible.add(b);

        return possible;
    }

    public AItem getRandomItem(Cell c, int maxValue, int depth) {
        float roll = R.nextFloat();
        ArrayList<AItem> possible = getPossibleItems(maxValue, roll, depth);
        if (possible.size() == 0) return null;
        int index = R.nextInt(possible.size());
        AItem item = possible.get(index).copy();
        item.setCell(c);
        return item;
    }
}
