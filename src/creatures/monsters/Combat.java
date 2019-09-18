package creatures.monsters;

import screens.Overlay;
import terrain.grid.Cell;

import java.util.LinkedHashSet;

import static main.ScreenManager.player;
import static main.ScreenManager.sm;
import static util.PrintFormatting.NEW_LINE;

public class Combat {
    private final Monster monster;
    private final LinkedHashSet<Monster> monsters;
    private boolean playersTurn, actionTaken = false, actionResultDescribed;
    private Overlay status;

    private String getActionOptions() {
        return "'A' - " + player.getAttackDescription() + NEW_LINE +
                "'U' - Use an item." + NEW_LINE +
                "Arrow keys - Jump in direction.";
    }

    private void setActionList() {
        if (!playersTurn) return;

        status.setText(player.getEnemyText() + monster.name + ".");
        status.addLine(getActionOptions());
    }

    private void setActionResult(String msg) {
        if (msg == null) return;

        status.setText(msg + NEW_LINE + "Press Enter to continue...");
        actionResultDescribed = true;
    }

    public Combat(Monster monster, LinkedHashSet<Monster> monsters) {
        this.monsters = monsters;
        this.monster = monster;
        playersTurn = player.getAgility() >= monster.getAgility();
        player.stopReacting();
        player.setInCombat(true);
        monster.setInCombat(true);
        status = new Overlay(sm.getScreen());
        setActionList();
        sm.setScreen(status);
        monster.spinTowards(player);
        player.spinTowards(monster);
    }

    private void endTurn() {
        actionTaken = false;
        actionResultDescribed = false;
        monsters.forEach(Monster::movementSelection);
        monster.spinTowards(player);
        player.spinTowards(monster);
        playersTurn = !playersTurn;
        setActionList();
    }

    public void turn() {
        // No action taken
        if (!actionTaken && !actionResultDescribed) {
            String msg = playersTurn ? player.act() : monster.act();
            actionTaken = playersTurn ? player.isActionSuccessful() : monster.isActionSuccessful();
            setActionResult(msg);
        }

        // Action failed, needs acknowledgement
        if (!actionTaken && actionResultDescribed && !player.isNextPressed()) {
            player.requestNext();
        }

        // Action failed and acknowledged
        if (!actionTaken && actionResultDescribed && player.isNextPressed()) {
            player.consumeNextPressed();
            actionResultDescribed = false;
            setActionList();
        }

        // Action succeeded, needs acknowledgement
        if (actionTaken && !player.isNextPressed()) {
            player.requestNext();
        }

        // Action succeeded and acknowledged
        if (actionTaken && player.isNextPressed()) {
            player.consumeNextPressed();
            endTurn();
        }
    }

    public boolean isOver() {
        boolean deadCombatant = player.isDead() || monster.isDead();
        boolean distanceIncreased = player.euclidean(monster) > Cell.WIDTH;
        return !actionTaken && (deadCombatant || distanceIncreased);
    }

    public Monster endCombat() {
        player.setInCombat(false);
        monster.setInCombat(false);
        sm.setScreen(status.backScreen);
        return monster;
    }
}
