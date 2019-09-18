package terrain;

import creatures.abstracted.Mover;
import creatures.monsters.MonsterManager;
import items.management.ItemManager;
import processing.core.PApplet;
import terrain.grid.Cell;
import terrain.grid.CellManager;

import java.util.LinkedHashSet;

import static main.Run.P;
import static main.ScreenManager.player;
import static main.ScreenManager.sm;

public class Dungeon {
    private static final float SPLIT_CHANCE_PER_DEPTH = P.getFloat("split chance per depth");
    private static final int THREAT_PER_DEPTH = P.getInt("threat per depth");
    private static final int ITEM_VALUE_PER_DEPTH = P.getInt("item value per depth");

    public final int DEPTH;
    public final CellManager cellManager;
    private final Region root;
    public final MonsterManager monsterManager = new MonsterManager(this);
    public final ItemManager itemManager = new ItemManager();
    private Cell start, end;

    private void setStartAndEnd() {
        Cell bestStart = null, bestEnd = null;
        int bestLength = -1, pathLength;
        int counter = 0;
        int min = (int) PApplet.sqrt(root.width * root.height);
        boolean condition, pathFound, minTried;
        AStar search = new AStar(this);
        do {
            start = cellManager.getRandomRoom();
            end = cellManager.getRandomRoom();

            // Count how many pairs of cells have been tried.
            counter++;
            search.setStartAndEnd(start, end);
            pathLength = search.shortestPath();
            if (pathLength > bestLength) {
                bestStart = start;
                bestEnd = end;
                bestLength = pathLength;
            }

            pathFound = bestLength > 0;
            minTried = counter > min;

            condition = minTried && pathFound;
        }
        while (!condition);

        start = bestStart;
        end = bestEnd;
        bestEnd.setEnd();
    }

    private boolean isSpecial(Cell c) {
        if (c.equals(start) || c.equals(end)) return true;

        return itemManager.occupies(c) || monsterManager.occupies(c);

    }

    private void rollMonsters() {
        int t = 0;
        Cell c;
        int totalThreat = THREAT_PER_DEPTH * DEPTH;
        while (t < totalThreat) {
            do c = cellManager.getRandomRoom();
            while (isSpecial(c));

            int max = totalThreat - t;
            t += monsterManager.rollMonster(c, max);
        }
    }

    private void rollItems() {
        int t = 0;
        Cell c;
        int totalValue = ITEM_VALUE_PER_DEPTH * DEPTH;
        while (t < totalValue) {
            do c = cellManager.getRandomRoom();
            while (isSpecial(c));

            int max = totalValue - t;
            t += itemManager.rollItem(c, max, DEPTH);
        }
    }

    public boolean isCompleted() {
        return player.getCells().contains(end);
    }

    public Dungeon(int depth) {
        this.DEPTH = depth;
        int w = (int) (sm.getWidth() / Cell.WIDTH);
        int h = (int) (sm.getHeight() / Cell.HEIGHT);
        cellManager = new CellManager(w, h);
        root = new Region(0, 0, w, h, cellManager);
        root.maybeDivide(SPLIT_CHANCE_PER_DEPTH * DEPTH);
        root.fill();

        setStartAndEnd();
        rollMonsters();
        rollItems();
        player.setDungeon(this);
        player.setLocation(start);
    }

    public boolean inDungeon(Mover mc) {
        LinkedHashSet<Cell> cells = mc.getCells();
        for (Cell c : cells) {
            if (!cellManager.isFree(c))
                return false;
        }

        return true;
    }

    public void render() {
        cellManager.render();
        itemManager.render();
        monsterManager.moveMonsters();
        monsterManager.render();
    }
}
