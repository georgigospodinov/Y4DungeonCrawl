package main;

import creatures.Player;
import items.management.Inventory;
import items.management.ItemLoader;
import processing.core.PApplet;
import screens.AScreen;
import screens.InventoryScreen;
import screens.Menu;

import static main.Run.P;

public class ScreenManager extends PApplet {

    public static ScreenManager sm;
    public static Player player;
    public static ItemLoader itemLoader;
    public static Inventory inventory;

    private int width, height;
    private AScreen screen;

    public AScreen getScreen() {
        return screen;
    }

    public void setScreen(AScreen screen) {
        this.screen = screen;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    @Override
    public void settings() {
        sm = this;
        String fs = P.getString("full screen");
        if (fs.equals("true"))
            fullScreen();

        //<editor-fold desc="reused code from last practical">
        try {
            String w = P.getString("game width");
            switch (w) {
                case "displayWidth":
                    width = displayWidth;
                    break;
                case "displayHeight":
                    width = displayHeight;
                    break;
                default:
                    throw new Exception();
            }
        }
        catch (Exception e) {
            width = P.getInt("game width");
        }
        try {
            String h = P.getString("game height");
            switch (h) {
                case "displayHeight":
                    height = displayHeight;
                    break;
                case "displayWidth":
                    height = displayWidth;
                    break;
                default:
                    throw new Exception();
            }

            // Account for the top and bottom margin
            if (fs.equals("true"))
                height -= 50;
        }
        catch (Exception e) {
            height = P.getInt("game height");
        }

        if (!fs.equals("true"))
            size(width, height);
        //</editor-fold>

    }

    @Override
    public void draw() {
        if (screen == null)
            screen = new Menu();
        if (itemLoader == null)
            itemLoader = new ItemLoader();

        surface.setTitle("Dungeon Crawl");
        screen.render();
    }

    @Override
    public void mouseClicked() {
        screen.click();
    }

    @Override
    public void keyPressed() {
        if (screen instanceof InventoryScreen)
            ((InventoryScreen) screen).reactTo(keyCode);
        else if (player != null)
            player.startReacting(keyCode);
    }

    @Override
    public void keyReleased() {
        if (player == null) return;
        player.stopReacting(keyCode);
    }
}
