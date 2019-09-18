package screens;

import screens.components.Clickable;
import screens.components.Label;
import screens.components.Message;

import static main.Run.P;
import static main.ScreenManager.player;
import static main.ScreenManager.sm;

public class Overlay extends AScreen {

    private static final float STATUS_SIZE = sm.getHeight() * P.getFloat("message status size");
    private static final int FILL_COLOR;
    private static final int BACK_COLOR;

    static {
        int red, green, blue;
        red = P.getInt("overlay fill color red");
        green = P.getInt("overlay fill color green");
        blue = P.getInt("overlay fill color blue");
        FILL_COLOR = sm.color(red, green, blue);

        red = P.getInt("overlay back color red");
        green = P.getInt("overlay back color green");
        blue = P.getInt("overlay back color blue");
        BACK_COLOR = sm.color(red, green, blue);
    }

    public final AScreen backScreen;
    private final Message msg;

    public void determineMessagePosition() {
        boolean down = player.getY() > sm.getHeight() / 2;
        int y = down ? 0 : sm.getHeight() / 2;

        // Do not overlay player attributes.
        if (backScreen instanceof Play) {
            float adjustment = ((Play) backScreen).getAttributesPixelHeight();
            if (down) y += adjustment;
        }

        msg.setY(y);
    }

    public void setText(String text) {
        msg.setText(text);
        determineMessagePosition();
    }

    public void addLine(String line) {
        msg.addLine(line);
        determineMessagePosition();
    }

    public Overlay(AScreen backScreen) {
        this.backScreen = backScreen;
        // X and Y coordinates get changed immediately. Colors are default
        msg = new Message(0, 0, STATUS_SIZE, FILL_COLOR, BACK_COLOR);
        determineMessagePosition();
        labels.add(msg);
    }

    @Override
    public void render() {
        backScreen.render();
        clickables.forEach(Clickable::render);
        labels.forEach(Label::render);
        transformCursor();
    }
}
