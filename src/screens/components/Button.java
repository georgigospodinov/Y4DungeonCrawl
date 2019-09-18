package screens.components;

import main.ScreenManager;

import java.util.LinkedHashSet;

import static main.ScreenManager.sm;
import static processing.core.PConstants.ARROW;
import static screens.AScreen.COORDINATE_FIX;

public class Button extends Label implements Clickable {

    private LinkedHashSet<Runnable> actions = new LinkedHashSet<>();

    public Button(String text, float x, float y, float height) {
        super(text, x, y, height);
    }

    @Override
    public void render() {
        if (within(sm.mouseX, sm.mouseY)) {
            sm.stroke(0);
            sm.fill(HIGHLIGHT_COLOR);
        }
        else {
            sm.stroke(255);
            sm.noFill();
        }

        if (x + width > ScreenManager.sm.getWidth()) {
            x = ScreenManager.sm.getWidth() - width - COORDINATE_FIX;
        }
        if (y + height > ScreenManager.sm.getHeight()) {
            y = ScreenManager.sm.getHeight() - height - COORDINATE_FIX;
        }
        sm.rect(x, y, width, height);

        super.render();

    }

    public void onClick(Runnable action) {
        actions.add(action);
    }

    @Override
    public void click() {
        for (Runnable a : actions) a.run();
        sm.cursor(ARROW);
    }

    @Override
    public boolean within(float x, float y) {
        return this.x <= x && x <= this.x + this.width &&
                this.y <= y && y <= this.y + this.height;
    }
}
