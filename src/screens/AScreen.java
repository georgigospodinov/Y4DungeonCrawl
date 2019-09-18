package screens;

import screens.components.Clickable;
import screens.components.Label;

import java.util.HashSet;

import static main.Run.P;
import static main.ScreenManager.sm;
import static processing.core.PConstants.ARROW;
import static processing.core.PConstants.HAND;

public abstract class AScreen {

    static final float DETAILS_SIZE = sm.getHeight() * P.getFloat("details size");
    static final float DETAILS_X = sm.getWidth() * P.getFloat("details x");
    static final float DETAILS_Y = sm.getHeight() * P.getFloat("details y");
    static final float BACK_SIZE = sm.getHeight() * P.getFloat("back size");
    static final float BACK_X = sm.getWidth() * P.getFloat("back x");
    static final float BACK_Y = sm.getHeight() * P.getFloat("back y");

    public static final float COORDINATE_FIX = 3;
    public static final int DEFAULT_COLOR;

    static {
        int red = P.getInt("default color red");
        int green = P.getInt("default color green");
        int blue = P.getInt("default color blue");
        DEFAULT_COLOR = sm.color(red, green, blue);
    }

    protected HashSet<Clickable> clickables = new HashSet<>();
    protected HashSet<Label> labels = new HashSet<>();

    protected void transformCursor() {
        for (Clickable c : clickables) {
            if (c.within(sm.mouseX, sm.mouseY)) {
                sm.cursor(HAND);
                return;
            }
        }
        sm.cursor(ARROW);
    }

    public void click() {
        for (Clickable c : clickables) {
            if (c.within(sm.mouseX, sm.mouseY)) {
                c.click();
                return;
            }
        }
    }

    public void render() {
        sm.background(DEFAULT_COLOR);
        clickables.forEach(Clickable::render);
        labels.forEach(Label::render);
        transformCursor();
    }

    @Override
    public String toString() {
        return this.getClass().getSimpleName();
    }
}
