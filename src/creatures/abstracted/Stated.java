package creatures.abstracted;

import terrain.grid.Cell;

import java.util.LinkedHashSet;
import java.util.function.Consumer;

import static main.Run.P;

public abstract class Stated extends Mover {

    public static final int DEFAULT_MAX_HEALTH = P.getInt("default max health");
    public static final int DEFAULT_STRENGTH = P.getInt("default strength");
    public static final int DEFAULT_AGILITY = P.getInt("default agility");
    public static final int DEFAULT_AWARENESS = P.getInt("default awareness");
    public static final float SHORT_RANGE_VISION_FACTOR = P.getFloat("short range vision factor");

    public final String name;
    private int maxHealth, currentHealth;
    private int strength = DEFAULT_STRENGTH, agility = DEFAULT_AGILITY;
    private int awareness = DEFAULT_AWARENESS;
    private boolean inCombat;
    protected boolean actionSuccessful;
//    private int intellect for spells.

    public void increaseMaxHealth(int amount) {
        maxHealth += amount;
        currentHealth += amount;
    }

    public int getMaxHealth() {
        return maxHealth;
    }

    public int getCurrentHealth() {
        return currentHealth;
    }

    public int getStrength() {
        return strength;
    }

    public void setStrength(int strength) {
        this.strength = strength;
    }

    public int getAgility() {
        return agility;
    }

    public void setAgility(int agility) {
        this.agility = agility;
        super.speed = agility < 0 ? 0 : agility;
    }

    public int getAwareness() {
        return awareness;
    }

    public void setAwareness(int awareness) {
        this.awareness = awareness;
    }

    public boolean isInCombat() {
        return inCombat;
    }

    public void setInCombat(boolean inCombat) {
        this.inCombat = inCombat;
    }

    public boolean isActionSuccessful() {
        return actionSuccessful;
    }

    public boolean isDead() {
        return currentHealth == 0;
    }

    public Stated(String name, Cell c) {
        super(c);
        this.name = name;
        this.maxHealth = DEFAULT_MAX_HEALTH;
        currentHealth = maxHealth;
        super.speed = agility;
    }

    public abstract String act();

    public boolean attackIsMagical() {
        return false;
    }

    public boolean defenseIsMagical() {
        return false;
    }

    public int attack() {
        return strength;
    }

    public int defense() {
        return agility;
    }

    public int takeDamage(int amount, boolean magical) {
        currentHealth -= amount;
        if (currentHealth < 0) {
            amount += currentHealth;
            currentHealth = 0;
        }
        return amount;
    }

    public int receiveHeal(int amount) {
        currentHealth += amount;
        if (currentHealth > maxHealth)
            currentHealth = maxHealth;
        return amount;
    }

    public int makeAttack(Stated target) {
        int damage = this.attack();
        // Non-magical defense cannot stop magical attacks
        if (attackIsMagical() && !defenseIsMagical())
            damage -= target.agility;
        else damage -= target.defense();

        if (damage <= 0) return 0;
        return target.takeDamage(damage, this.attackIsMagical());
    }

    public void forEachCellInSight(Consumer<Cell> action) {
        int x = getLeftCellX() - awareness;
        int y = getUpCellY() - awareness;
        int w = awareness * 2 + 1;
        int h = awareness * 2 + 1;
        float longRange = awareness * (Cell.HEIGHT + Cell.WIDTH) / 2;
        LinkedHashSet<Cell> cells = getCells();
        d.cellManager.forEachInSlice(x, y, w, h, c -> {
            boolean atLeastOne = false;
            for (Cell cell : cells)
                atLeastOne |= d.cellManager.directlyVisible(cell.x, cell.y, c.x, c.y);

            if (!atLeastOne) return;

            float range = longRange;
            if (withinView(c))
                range = longRange * SHORT_RANGE_VISION_FACTOR;

            if (euclidean(c) <= range)
                action.accept(c);

        });
    }

}
