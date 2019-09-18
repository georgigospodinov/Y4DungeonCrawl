package creatures;

import creatures.abstracted.Stated;
import creatures.monsters.Monster;
import items.individual.*;
import screens.InventoryScreen;
import terrain.grid.Cell;

import java.util.HashMap;
import java.util.HashSet;

import static main.Run.P;
import static main.ScreenManager.inventory;
import static main.ScreenManager.sm;

public class Player extends Stated {

    private static final int PLAYER_COLOR;
    private static final int MOVE_LEFT = P.getInt("move left");
    private static final int MOVE_UP = P.getInt("move up");
    private static final int MOVE_RIGHT = P.getInt("move right");
    private static final int MOVE_DOWN = P.getInt("move down");
    public static final int SHOW_INVENTORY = P.getInt("show inventory");
    private static final int ATTACK_BUTTON = P.getInt("attack button");
    private static final int NEXT_BUTTON = P.getInt("next button");
    private static final int USE_BUTTON = P.getInt("use button");
    private static final int XP_PER_LEVEL = P.getInt("xp per level");
    private static final int HP_PER_LEVEL = P.getInt("hp per level");
    private static final int PLAYER_BONUS_HEALTH = P.getInt("player bonus health");
    private static final float AGILITY_JUMP_FACTOR = P.getFloat("agility jump factor");

    static {
        int red, green, blue;
        red = P.getInt("player color red");
        green = P.getInt("player color green");
        blue = P.getInt("player color blue");
        PLAYER_COLOR = sm.color(red, green, blue);
    }

    private final HashMap<Integer, Runnable> buttonReactions = new HashMap<>();
    private final HashSet<Integer> pressedButtons = new HashSet<>();
    private boolean nextPressed;
    private int xp = 0, level = 0;
    private Armor armor;
    private Weapon weapon;
    private AItem lastUsed;

    public boolean isNextPressed() {
        return nextPressed;
    }

    public void consumeNextPressed() {
        pressedButtons.remove(NEXT_BUTTON);
        this.nextPressed = false;
    }

    public int getXP() {
        return xp;
    }

    public int getLevel() {
        return level;
    }

    public int getXPThreshold() {
        return (level + 1) * XP_PER_LEVEL;
    }

    public void gainXP(int amount) {
        xp += amount;
        int threshold = getXPThreshold();
        if (xp >= threshold) {
            gainLevel();
            xp -= threshold;
        }
    }

    private void gainLevel() {
        level++;

        switch (level % 4) {
            case 1:
                setStrength(getStrength() + 1);
                break;
            case 2:
                setAgility(getAgility() + 1);
                break;
            case 3:
                setAwareness(getAwareness() + 1);
                break;
            case 0:
                increaseMaxHealth(HP_PER_LEVEL);
                break;
        }
        increaseMaxHealth(HP_PER_LEVEL);
    }

    public boolean usedItem() {
        return lastUsed != null;
    }

    public void setLastUsed(AItem lastUsed) {
        if (isInCombat())
            this.lastUsed = lastUsed;
    }

    private void showInventory() {
        InventoryScreen is = new InventoryScreen(sm.getScreen());
        sm.setScreen(is);
    }

    private void setReactions() {
        buttonReactions.put(MOVE_LEFT, this::moveLeft);
        buttonReactions.put(MOVE_UP, this::moveUp);
        buttonReactions.put(MOVE_RIGHT, this::moveRight);
        buttonReactions.put(MOVE_DOWN, this::moveDown);
        buttonReactions.put(SHOW_INVENTORY, this::showInventory);
    }

    public void startReacting(int keyCode) {
        pressedButtons.add(keyCode);
    }

    public void stopReacting(int keyCode) {
        pressedButtons.remove(keyCode);
    }

    public void stopReacting() {
        pressedButtons.clear();
    }

    public void react() {
        if (isInCombat()) return;

        pressedButtons.forEach(i -> {
            Runnable r = buttonReactions.get(i);
            if (r != null) r.run();
        });
    }

    public Player() {
        super("ME", new Cell(-1, -1));
        setReactions();
        setStrokeColor(0);
        setFillColor(PLAYER_COLOR);
        increaseMaxHealth(PLAYER_BONUS_HEALTH);
    }

    public String getEnemyText() {
        return weapon == null ? "You face a " : weapon.enemyText;
    }

    public String getAttackDescription() {
        return weapon == null ? "Punch." : weapon.attackDescription;
    }

    private String attackAction() {
        Monster mon = d.monsterManager.getCombatant();
        int dmg = makeAttack(mon);
        actionSuccessful = true;

        if (mon.isDead())
            return weapon == null ? "It's all beat up now..." : weapon.kill;

        if (dmg > 0)
            return (weapon == null ? "Good punch" : weapon.onDamage) + " " + dmg + " damage.";

        return weapon == null ? "Punching is not good enough." : weapon.noDamage;
    }

    private int getJumpDist() {
        int rd = (int) (getAgility() * AGILITY_JUMP_FACTOR);
        if (rd > MAX_SPEED / Cell.WIDTH) {
            rd = (int) (MAX_SPEED / Cell.WIDTH);
        }
        return rd < 1 ? 1 : rd;
    }

    private boolean failedJumpTo(Cell c) {
        if (d.cellManager.isFree(c) && !d.monsterManager.occupies(c)) {
            setLocation(c);
            return false;
        }
        return true;
    }

    private String jumpWest() {
        int rd = getJumpDist();
        int x;
        Cell c;
        for (x = 1; x <= rd; x++) {
            c = new Cell(getLeftCellX() - x, getUpCellY());
            if (failedJumpTo(c)) break;
        }
        actionSuccessful = x != 1;

        return actionSuccessful ? "You jumped west!" : "There is an obstacle to the west!";
    }

    private String jumpNorth() {
        int rd = getJumpDist();
        int y;
        Cell c;
        for (y = 1; y <= rd; y++) {
            c = new Cell(getLeftCellX(), getUpCellY() - y);
            if (failedJumpTo(c)) break;
        }
        actionSuccessful = y != 1;

        return actionSuccessful ? "You jumped north!" : "There is an obstacle to the north!";
    }

    private String jumpEast() {
        int rd = getJumpDist();
        int x;
        Cell c;
        for (x = 1; x <= rd; x++) {
            c = new Cell(getRightCellX() + x, getUpCellY());
            if (failedJumpTo(c)) break;
        }
        actionSuccessful = x != 1;

        return actionSuccessful ? "You jumped east!" : "There is an obstacle to the east!";
    }

    private String jumpSouth() {
        int rd = getJumpDist();
        int y;
        Cell c;
        for (y = 1; y <= rd; y++) {
            c = new Cell(getLeftCellX(), getDownCellY() + y);
            if (failedJumpTo(c)) break;
        }
        actionSuccessful = y != 1;

        return actionSuccessful ? "You jumped south!" : "There is an obstacle to the south!";
    }

    @Override
    public String act() {
        if (pressedButtons.contains(ATTACK_BUTTON))
            return attackAction();

        if (pressedButtons.contains(MOVE_LEFT))
            return jumpWest();
        if (pressedButtons.contains(MOVE_UP))
            return jumpNorth();
        if (pressedButtons.contains(MOVE_RIGHT))
            return jumpEast();
        if (pressedButtons.contains(MOVE_DOWN))
            return jumpSouth();

        if (pressedButtons.contains(USE_BUTTON))
            showInventory();

        actionSuccessful = usedItem();
        if (!actionSuccessful)
            return null;

        String actionResult = "You spend your turn ";
        if (lastUsed instanceof Armor || lastUsed instanceof Weapon)
            actionResult += "equipping";
        else if (lastUsed instanceof Consumable)
            actionResult += "drinking";
        else actionResult += "using";
        actionResult += " " + lastUsed.name + ".";
        lastUsed = null;
        return actionResult;
    }

    public void requestNext() {
        if (pressedButtons.contains(NEXT_BUTTON))
            nextPressed = true;
    }

    public String getArmorDesc() {
        if (armor == null) return "No Armor";
        int db = armor.defenseBonus;
        String prefix = db < 0 ? "" : "+";
        return prefix + db + " " + armor.name;
    }

    public int getArmorBonus() {
        return armor == null ? 0 : armor.defenseBonus;
    }

    @Override
    public int defense() {
        return super.defense() + getArmorBonus();
    }

    public void equipArmor(Armor a, int inventoryIndex) {
        // Return previous armor to inventory.
        inventory.set(inventoryIndex, armor);

        // Restore penalized agility.
        if (armor != null)
            setAgility(getAgility() + armor.agilityPenalty);

        // Set new armor.
        armor = a;

        // Penalize agility.
        setAgility(getAgility() - armor.agilityPenalty);
    }

    public String getWeaponDesc() {
        if (weapon == null) return "No Weapon";
        else if (weapon instanceof Torch)
            return "+" + weapon.value + " " + weapon.name;
        else return "+" + weapon.attackBonus + " " + weapon.name;
    }

    public int getWeaponBonus() {
        return weapon == null ? 0 : weapon.attackBonus;
    }

    @Override
    public int attack() {
        return super.attack() + getWeaponBonus();
    }

    @Override
    public boolean attackIsMagical() {
        return weapon != null && weapon.magical;
    }

    public void equipWeapon(Weapon w, int inventoryIndex) {
        inventory.set(inventoryIndex, weapon);
        if (weapon instanceof Torch) {
            Torch t = (Torch) weapon;
            setAwareness(getAwareness() - t.value);
        }
        weapon = w;
        if (weapon instanceof Torch) {
            Torch t = (Torch) weapon;
            setAwareness(getAwareness() + t.value);
        }
    }

}
