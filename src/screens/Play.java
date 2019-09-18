package screens;

import creatures.Player;
import items.management.Inventory;
import screens.components.Label;
import screens.components.Message;
import terrain.Dungeon;

import static main.Run.P;
import static main.ScreenManager.*;

public class Play extends AScreen {

    private static final float ATTRIBUTES_SIZE = sm.getHeight() * P.getFloat("message attributes size");
    private static final int ATTRIBUTES_BACK_COLOR, ATTRIBUTES_FILL_COLOR, PROGRESS_FILL_COLOR;
    private static final float CHANGE_MESSAGE_LOCATION_FACTOR = 0.2f;
    private static final int XP_PER_DUNGEON = P.getInt("xp per dungeon");

    static {
        int red, green, blue;
        red = P.getInt("attributes fill color red");
        green = P.getInt("attributes fill color green");
        blue = P.getInt("attributes fill color blue");
        ATTRIBUTES_FILL_COLOR = sm.color(red, green, blue);

        red = P.getInt("attributes back color red");
        green = P.getInt("attributes back color green");
        blue = P.getInt("attributes back color blue");
        ATTRIBUTES_BACK_COLOR = sm.color(red, green, blue);

        red = P.getInt("progress fill color red");
        green = P.getInt("progress fill color green");
        blue = P.getInt("progress fill color blue");
        PROGRESS_FILL_COLOR = sm.color(red, green, blue);
    }

    private Dungeon d;
    // Labels for score, attributes, action description.
    private Message attributes, progress;

    public float getAttributesPixelHeight() {
        return attributes.getPixelHeight();
    }

    public Play() {
        player = new Player();
        inventory = new Inventory();
        d = new Dungeon(1);
        attributes = new Message(0, 0, ATTRIBUTES_SIZE, ATTRIBUTES_FILL_COLOR, ATTRIBUTES_BACK_COLOR);
        progress = new Message(0, 0, ATTRIBUTES_SIZE, PROGRESS_FILL_COLOR, ATTRIBUTES_BACK_COLOR);
        labels.add(attributes);
        labels.add(progress);
    }

    private void checkCompleted() {
        if (!d.isCompleted()) return;

        d.monsterManager.forceStopCombat();
        player.gainXP(XP_PER_DUNGEON);
        d = new Dungeon(d.DEPTH + 1);
    }

    private void calculateY(Message m) {
        boolean currentlyOnTop = m.getY() == 0;
        boolean closeToPlayer;
        if (currentlyOnTop) {
            closeToPlayer = player.getY() < sm.getHeight() * CHANGE_MESSAGE_LOCATION_FACTOR;
            if (closeToPlayer)
                m.setY(sm.getHeight() - m.getPixelHeight());
        }
        else {
            closeToPlayer = player.getY() > sm.getHeight() * (1 - CHANGE_MESSAGE_LOCATION_FACTOR);
            if (closeToPlayer)
                m.setY(0);
        }
    }

    private void updateProgress() {
        String prog = "Level: " + player.getLevel() +
                "    XP: " + player.getXP() + "/" + player.getXPThreshold() +
                "    Dungeon Depth: " + d.DEPTH +
                "    Gold: " + inventory.getGold();
        progress.setText(prog);
        calculateY(progress);
    }

    private void updateAttributes() {
        String health = "Health: " + player.getCurrentHealth() + "/" + player.getMaxHealth();
        String stats = health +
                "    Strength: " + player.getStrength() +
                "    Agility: " + player.getAgility() +
                "    Awareness: " + player.getAwareness();

        StringBuilder sb = new StringBuilder();
        // Put spaces, so that Weapon is aligned with Strength and Armor with Agility
        for (int i = 0; i < health.length(); i++)
            sb.append(" ");
        sb.append(player.getWeaponDesc());
        sb.append("    ");
        sb.append(player.getArmorDesc());
        String items = sb.toString();

        attributes.setText(stats);
        attributes.addLine(items);
        float x = sm.getWidth() - attributes.getWidth();
        attributes.setX(x);

        calculateY(attributes);
    }

    @Override
    public void render() {
        super.render();
        d.render();
        player.react();
        player.render();
        checkCompleted();
        updateProgress();
        updateAttributes();
        labels.forEach(Label::render);
    }
}
