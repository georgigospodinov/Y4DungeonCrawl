package screens.components;

import static main.Run.P;
import static main.ScreenManager.sm;

public class Label {
    static final float TEXT_SIZE_FACTOR = 0.8f;
    static final int HIGHLIGHT_COLOR;

    static {
        int red = P.getInt("highlight color red");
        int green = P.getInt("highlight color green");
        int blue = P.getInt("highlight color blue");
        HIGHLIGHT_COLOR = sm.color(red, green, blue);
    }

    protected String text;
    protected float x, y;
    protected float width, height;

    public float getX() {
        return x;
    }

    public void setX(float x) {
        this.x = x;
    }

    public float getY() {
        return y;
    }

    public void setY(float y) {
        this.y = y;
    }

    public void setText(String text) {
        this.text = text;
        calcWidth();
    }

    public float getWidth() {
        return width;
    }

    private void calcWidth() {
        float size = height * TEXT_SIZE_FACTOR;
        sm.textSize(size);
        this.width = sm.textWidth(text);
    }

    public Label(String text, float x, float y, float height) {
        this.x = x;
        this.y = y;
        this.height = height;
        setText(text);
    }

    public void render() {
        sm.textSize(height * TEXT_SIZE_FACTOR);
        sm.fill(0);
        sm.text(text, x, y + height * TEXT_SIZE_FACTOR);
    }
}
