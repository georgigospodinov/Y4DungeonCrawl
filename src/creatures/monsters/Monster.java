package creatures.monsters;

import creatures.abstracted.Stated;
import processing.core.PApplet;
import terrain.AStar;
import terrain.grid.Cell;

import java.util.LinkedHashSet;

import static main.Run.P;
import static main.Run.R;
import static main.ScreenManager.player;
import static main.ScreenManager.sm;
import static util.PrintFormatting.NEW_LINE;

public class Monster extends Stated {

    public static final int SPECTER_XP_REWARD = P.getInt("specter xp reward");
    public static final int WEREWOLF_XP_REWARD = P.getInt("werewolf xp reward");
    public static final int ARCANIST_XP_REWARD = P.getInt("arcanist xp reward");
    public static final int GARGOYLE_XP_REWARD;

    private static final int MIN_WANDER_DURATION = P.getInt("min wander duration");
    private static final int MAX_ROLL_WANDER_DURATION = P.getInt("max wander duration") - MIN_WANDER_DURATION;
    private static final int SPECTER_COLOR, WEREWOLF_COLOR, ARCANIST_COLOR, GARGOYLE_COLOR, GOBLIN_COLOR;
    private static final int GARGOYLE_STRENGTH_BONUS = P.getInt("gargoyle strength bonus");
    private static final int GARGOYLE_HEALTH_BONUS = P.getInt("gargoyle health bonus");
    private static final int WEREWOLF_AWARENESS_BONUS = P.getInt("werewolf awareness bonus");
    private static final int WEREWOLF_AGILITY_BONUS = P.getInt("werewolf agility bonus");
    private static final int WEREWOLF_STRENGTH_BONUS = P.getInt("werewolf strength bonus");
    private static final int SPECTER_HAUNT_DISTANCE = P.getInt("specter haunt distance");
    private static final int SPECTER_AGILITY_BONUS = P.getInt("specter agility bonus");

    private static final int LEFT = 0;
    private static final int UP = 1;
    private static final int RIGHT = 2;
    private static final int DOWN = 3;

    static {
        GARGOYLE_XP_REWARD = P.getInt("gargoyle xp reward");

        int red, green, blue;
        red = P.getInt("specter color red");
        green = P.getInt("specter color green");
        blue = P.getInt("specter color blue");
        SPECTER_COLOR = sm.color(red, green, blue);

        red = P.getInt("werewolf color red");
        green = P.getInt("werewolf color green");
        blue = P.getInt("werewolf color blue");
        WEREWOLF_COLOR = sm.color(red, green, blue);

        red = P.getInt("arcanist color red");
        green = P.getInt("arcanist color green");
        blue = P.getInt("arcanist color blue");
        ARCANIST_COLOR = sm.color(red, green, blue);

        red = P.getInt("gargoyle color red");
        green = P.getInt("gargoyle color green");
        blue = P.getInt("gargoyle color blue");
        GARGOYLE_COLOR = sm.color(red, green, blue);

        red = P.getInt("goblin color red");
        green = P.getInt("goblin color green");
        blue = P.getInt("goblin color blue");
        GOBLIN_COLOR = sm.color(red, green, blue);
    }

    private boolean chasingPlayer;
    private final boolean horizontalPatrol;
    private Cell starting;
    private final float maxRange;
    private int counter = Integer.MAX_VALUE, direction = -1;
    private boolean specter, werewolf, arcanist, gargoyle;

    public void setStarting(Cell starting) {
        this.starting = starting;
    }

    private void setColor() {
        int sum = 0, count = 0;
        if (specter) {
            sum += SPECTER_COLOR;
            count++;
        }
        if (werewolf) {
            sum += WEREWOLF_COLOR;
            count++;
        }
        if (arcanist) {
            sum += ARCANIST_COLOR;
            count++;
        }
        if (gargoyle) {
            sum += GARGOYLE_COLOR;
            count++;
        }

        // Did we get at least one of types.
        sum = count == 0 ? GOBLIN_COLOR : sum / count;
        setStrokeColor(sum);

        if (specter)
            setFillColor(NO_COLOR);
        else setFillColor(sum);
    }

    private void applyBonuses() {
        if (specter) {
            int agi = getAgility() + SPECTER_AGILITY_BONUS;
            setAgility(agi);
        }
        if (werewolf) {
            int str = getStrength() + WEREWOLF_STRENGTH_BONUS;
            setStrength(str);
            int agi = getAgility() + WEREWOLF_AGILITY_BONUS;
            setAgility(agi);
            int awa = getAwareness() + WEREWOLF_AWARENESS_BONUS;
            setAwareness(awa);
        }
        if (gargoyle) {
            int str = getStrength() + GARGOYLE_STRENGTH_BONUS;
            setStrength(str);
            increaseMaxHealth(GARGOYLE_HEALTH_BONUS);
        }
    }

    public Monster(String name, boolean specter, boolean werewolf, boolean arcanist, boolean gargoyle) {
        super(name, new Cell(-1, -1));
        this.specter = specter;
        this.werewolf = werewolf;
        this.arcanist = arcanist;
        this.gargoyle = gargoyle;
        this.horizontalPatrol = R.nextBoolean();

        float randomFactor = R.nextFloat() / 2f + 0.5f;
        float cellDiagonal = PApplet.sqrt(Cell.WIDTH * Cell.WIDTH + Cell.HEIGHT * Cell.HEIGHT);
        maxRange = SPECTER_HAUNT_DISTANCE * randomFactor * cellDiagonal;
        setColor();
        applyBonuses();
    }

    private void dirMove() {
        switch (direction) {
            case LEFT:
                moveLeft();
                break;
            case UP:
                moveUp();
                break;
            case RIGHT:
                moveRight();
                break;
            case DOWN:
                moveDown();
                break;
        }
    }

    private void moveByPath(Cell src, Cell dst) {
        AStar divination = new AStar(d);
        divination.setStartAndEnd(src, dst);
        Cell next = divination.firstStep();
        if (next == null) {
            direction = R.nextInt(4);
            dirMove();
            return;
        }

        if (next.x > src.x)
            moveRight();
        else if (next.x < src.x)
            moveLeft();

        if (next.y > src.y)
            moveDown();
        else if (next.y < src.y)
            moveUp();
    }

    private void pursue() {
        int x1 = this.getLeftCellX(), y1 = this.getUpCellY();
        Cell src = new Cell(x1, y1);
        int x2 = player.getLeftCellX(), y2 = player.getUpCellY();
        Cell dst = new Cell(x2, y2);

        moveByPath(src, dst);
    }

    private void haunt() {
        if (euclidean(starting) > maxRange) {
            if (getLeftCellX() > starting.x)
                direction = LEFT;
            else if (getRightCellX() < starting.x)
                direction = RIGHT;
            else if (getUpCellY() > starting.y)
                direction = UP;
            else if (getDownCellY() < starting.y)
                direction = DOWN;
        }
        else if (counter > MIN_WANDER_DURATION) {
            int lastDirection = direction;
            do direction = R.nextInt(4);
            while (direction == lastDirection);
            counter = 0;
        }

        dirMove();
        counter++;
    }

    public void wander() {
        if (counter > MIN_WANDER_DURATION + R.nextInt(MAX_ROLL_WANDER_DURATION)) {
            int lastDirection = direction;
            do direction = R.nextInt(4);
            while (direction == lastDirection);
            counter = 0;
        }

        dirMove();
        counter++;
    }

    public void patrol() {
        if (counter > MAX_ROLL_WANDER_DURATION) {
            direction = direction < 1 ? 1 : 0;
            counter = 0;
        }

        if (direction == 0) {
            if (horizontalPatrol)
                moveLeft();
            else moveUp();
        }
        else {
            if (horizontalPatrol)
                moveRight();
            else moveDown();
        }
        counter++;
    }

    public void movementSelection() {
        if (moveToPlayerIfVisible())
            return;

        if (arcanist)
            pursue();
        else if (specter)
            haunt();
        else if (werewolf)
            wander();
        else if (!gargoyle)
            patrol();
        // Gargoyles stand still.
    }

    public boolean moveToPlayerIfVisible() {
        chasingPlayer = false;
        LinkedHashSet<Cell> playerCells = player.getCells();
        forEachCellInSight(dst -> {
            if (!playerCells.contains(dst)) return;
            if (chasingPlayer) return;  // Check so that the monster does not move multiple times per frame.

            chasingPlayer = true;
            int x1 = this.getLeftCellX(), y1 = this.getUpCellY();
            Cell src = new Cell(x1, y1);
            moveByPath(src, dst);
        });

        return chasingPlayer;
    }

    @Override
    public boolean attackIsMagical() {
        return arcanist;
    }

    @Override
    public boolean defenseIsMagical() {
        return arcanist;
    }

    @Override
    public boolean notInDungeon() {
        // Specter can move through walls, therefore is always in the dungeon (or never notInDungeon).
        if (specter) return false;

        return super.notInDungeon();
    }

    @Override
    public int makeAttack(Stated target) {
        int dmg = super.makeAttack(target);

        // Werewolf heals on hit.
        if (werewolf)
            receiveHeal(dmg / 2);
        return dmg;
    }

    @Override
    public int takeDamage(int amount, boolean magical) {
        // Specters are not affected by non-magical damage.
        if (specter && !magical)
            return 0;

        return super.takeDamage(amount, magical);
    }

    @Override
    public String act() {
        int dmg = makeAttack(player);
        actionSuccessful = true;
        String description;
        if (dmg == 0)
            description = "The " + name + " could not hit you!";
        else description = "The " + name + " hit you for " + dmg + " damage!";

        if (werewolf && dmg > 0)
            description += NEW_LINE + "It healed for " + (dmg / 2) + " health!";
        return description;
    }

    public int getXP() {
        int xp = 1;

        if (specter)
            xp += SPECTER_XP_REWARD;
        if (werewolf)
            xp += WEREWOLF_XP_REWARD;
        if (arcanist)
            xp += ARCANIST_XP_REWARD;
        if (gargoyle)
            xp += GARGOYLE_XP_REWARD;

        return xp;
    }

    @Override
    public String toString() {
        return "name=\"" + name + "\"";
    }
}
