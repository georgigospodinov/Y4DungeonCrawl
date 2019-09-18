package creatures.monsters;

import screens.GameOver;
import terrain.Dungeon;
import terrain.grid.Cell;

import java.util.ArrayList;
import java.util.LinkedHashSet;

import static creatures.monsters.Monster.*;
import static main.Run.P;
import static main.Run.R;
import static main.ScreenManager.player;
import static main.ScreenManager.sm;

public class MonsterManager {
    private static final float SPECIAL_SPAWN_CHANCE_PER_DEPTH = P.getFloat("special spawn chance per depth");
    private static final float BASE_GARGOYLE_SPAWN_CHANCE = P.getFloat("gargoyle spawn chance");
    private static final float SPECTER_SPAWN_CHANCE = P.getFloat("specter spawn chance") / BASE_GARGOYLE_SPAWN_CHANCE;
    private static final float WEREWOLF_SPAWN_CHANCE = P.getFloat("werewolf spawn chance") / BASE_GARGOYLE_SPAWN_CHANCE;
    private static final float ARCANIST_SPAWN_CHANCE = P.getFloat("arcanist spawn chance") / BASE_GARGOYLE_SPAWN_CHANCE;
    private static final float GARGOYLE_SPAWN_CHANCE = P.getFloat("gargoyle spawn chance") / BASE_GARGOYLE_SPAWN_CHANCE;

    private final LinkedHashSet<Monster> monsters = new LinkedHashSet<>();
    private final Dungeon d;
    private Combat combat;

    public Monster getCombatant() {
        for (Monster m : monsters) {
            if (m.isInCombat())
                return m;
        }

        return null;
    }

    public MonsterManager(Dungeon d) {
        this.d = d;
    }

    public void render() {
        monsters.forEach(monster -> {
            LinkedHashSet<Cell> cells = monster.getCells();
            LinkedHashSet<Cell> visible = new LinkedHashSet<>();
            player.forEachCellInSight(visible::add);
            int counter = 0;
            for (Cell c : cells)
                if (visible.contains(c))
                    counter++;

            // Need to see at least half the cells, occupied by this monster.
            if (counter > 0 && counter >= cells.size() / 2)
                monster.render();
        });
    }

    public void moveMonsters() {
        monsters.forEach(monster -> {
            // If combat has been started by a monster, do not move.
            // Note that combat can start in the middle of the loop.
            if (combat != null) return;

            monster.movementSelection();
            if (monster.euclidean(player) < Cell.WIDTH)
                combat = new Combat(monster, monsters);
        });

        if (combat == null) return;

        combat.turn();
        if (!combat.isOver()) return;

        Monster m = combat.endCombat();
        combat = null;
        if (m.isDead()) {
            monsters.remove(m);
            player.gainXP(m.getXP());
        }
        else if (player.isDead()) {
            GameOver over = new GameOver(d.DEPTH);
            sm.setScreen(over);
        }
    }

    public void forceStopCombat() {
        if (combat == null) return;

        combat.endCombat();
        combat = null;
    }

    private Monster sampleMonster(boolean specter, boolean werewolf, boolean arcanist, boolean gargoyle) {
        String name = "";
        if (specter) name += "Specter ";
        if (werewolf) name += "Werewolf ";
        if (arcanist) name += "Arcanist ";
        if (gargoyle) name += "Gargoyle ";
        if (name.equals("")) name = "Goblin ";
        name = name.substring(0, name.length() - 1);
        return new Monster(name, specter, werewolf, arcanist, gargoyle);
    }

    private ArrayList<Monster> getPossible(int maxThreatLevel, float roll, float specialChance) {
        ArrayList<Monster> possible = new ArrayList<>();
        if (maxThreatLevel > SPECTER_XP_REWARD + WEREWOLF_XP_REWARD + ARCANIST_XP_REWARD + GARGOYLE_XP_REWARD)
            if (roll < SPECTER_SPAWN_CHANCE * WEREWOLF_SPAWN_CHANCE * ARCANIST_SPAWN_CHANCE * GARGOYLE_SPAWN_CHANCE * specialChance)
                possible.add(sampleMonster(true, true, true, true));

        if (maxThreatLevel > ARCANIST_XP_REWARD + SPECTER_XP_REWARD + WEREWOLF_XP_REWARD)
            if (roll < ARCANIST_SPAWN_CHANCE * SPECTER_SPAWN_CHANCE * WEREWOLF_SPAWN_CHANCE * specialChance)
                possible.add(sampleMonster(true, true, true, false));

        if (maxThreatLevel > ARCANIST_XP_REWARD + SPECTER_XP_REWARD + GARGOYLE_XP_REWARD)
            if (roll < ARCANIST_SPAWN_CHANCE * SPECTER_SPAWN_CHANCE * GARGOYLE_SPAWN_CHANCE * specialChance)
                possible.add(sampleMonster(true, false, true, true));

        if (maxThreatLevel > ARCANIST_XP_REWARD + WEREWOLF_XP_REWARD + GARGOYLE_XP_REWARD)
            if (roll < ARCANIST_SPAWN_CHANCE * WEREWOLF_SPAWN_CHANCE * GARGOYLE_SPAWN_CHANCE * specialChance)
                possible.add(sampleMonster(false, true, true, true));

        if (maxThreatLevel > SPECTER_XP_REWARD + WEREWOLF_XP_REWARD + GARGOYLE_XP_REWARD)
            if (roll < SPECTER_SPAWN_CHANCE * WEREWOLF_SPAWN_CHANCE * GARGOYLE_SPAWN_CHANCE * specialChance)
                possible.add(sampleMonster(true, true, false, true));

        if (maxThreatLevel > ARCANIST_XP_REWARD + SPECTER_XP_REWARD)
            if (roll < ARCANIST_SPAWN_CHANCE * SPECTER_SPAWN_CHANCE * specialChance)
                possible.add(sampleMonster(true, false, true, false));

        if (maxThreatLevel > ARCANIST_XP_REWARD + WEREWOLF_XP_REWARD)
            if (roll < ARCANIST_SPAWN_CHANCE * WEREWOLF_SPAWN_CHANCE * specialChance)
                possible.add(sampleMonster(false, true, true, false));

        if (maxThreatLevel > ARCANIST_XP_REWARD + GARGOYLE_XP_REWARD)
            if (roll < ARCANIST_SPAWN_CHANCE * GARGOYLE_SPAWN_CHANCE * specialChance)
                possible.add(sampleMonster(false, false, true, true));

        if (maxThreatLevel > SPECTER_XP_REWARD + WEREWOLF_XP_REWARD)
            if (roll < SPECTER_SPAWN_CHANCE * WEREWOLF_SPAWN_CHANCE * specialChance)
                possible.add(sampleMonster(true, true, false, false));

        if (maxThreatLevel > SPECTER_XP_REWARD + GARGOYLE_XP_REWARD)
            if (roll < SPECTER_SPAWN_CHANCE * GARGOYLE_SPAWN_CHANCE * specialChance)
                possible.add(sampleMonster(true, false, false, true));

        if (maxThreatLevel > WEREWOLF_XP_REWARD + GARGOYLE_XP_REWARD)
            if (roll < WEREWOLF_SPAWN_CHANCE * GARGOYLE_SPAWN_CHANCE * specialChance)
                possible.add(sampleMonster(false, true, false, true));

        if (maxThreatLevel > ARCANIST_XP_REWARD)
            if (roll < ARCANIST_SPAWN_CHANCE * specialChance)
                possible.add(sampleMonster(false, false, true, false));

        if (maxThreatLevel > SPECTER_XP_REWARD)
            if (roll < SPECTER_SPAWN_CHANCE * specialChance)
                possible.add(sampleMonster(true, false, false, false));

        if (maxThreatLevel > WEREWOLF_XP_REWARD)
            if (roll < WEREWOLF_SPAWN_CHANCE * specialChance)
                possible.add(sampleMonster(false, true, false, false));

        if (maxThreatLevel > GARGOYLE_XP_REWARD)
            if (roll < GARGOYLE_SPAWN_CHANCE * specialChance)
                possible.add(sampleMonster(false, false, false, true));

        possible.add(sampleMonster(false, false, false, false));
        return possible;
    }

    public int rollMonster(Cell c, int maxThreatLevel) {
        float specialChance = d.DEPTH * SPECIAL_SPAWN_CHANCE_PER_DEPTH;
        float roll = R.nextFloat();
        ArrayList<Monster> possible = getPossible(maxThreatLevel, roll, specialChance);
        int index = R.nextInt(possible.size());
        Monster m = possible.get(index);
        m.setLocation(c);
        m.setStarting(c);
        m.setDungeon(d);
        monsters.add(m);
        return m.getXP();
    }

    public boolean occupies(Cell c) {
        for (Monster m : monsters)
            if (m.getCells().contains(c))
                return true;

        return false;
    }
}
