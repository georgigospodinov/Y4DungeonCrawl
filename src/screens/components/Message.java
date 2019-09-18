package screens.components;

import static main.ScreenManager.sm;

public class Message extends Label {

    public static final float LINE_RECT_FACTOR = 1.15f;
    private static final int NO_COLOR = -1;

    private final int fillColor, backColor;
    private int lineCount;

    private int countLines() {
        String temp = text;
        int count = 0;
        int lastIndex;
        do {
            lastIndex = temp.indexOf("\n");
            if (lastIndex != -1) {
                count++;
                temp = temp.substring(lastIndex + 1);
            }
        }
        while (lastIndex != -1);

        return (count + 1);
    }

    public float getPixelHeight() {
        return height * lineCount * LINE_RECT_FACTOR + 5;
    }

    @Override
    public void setText(String text) {
        super.setText(text);
        lineCount = countLines();
    }

    public void addLine(String line) {
        this.setText(text + "\n" + line);
    }

    public Message(String text, float x, float y, float height, int fillColor, int backColor) {
        super(text, x, y, height);
        this.fillColor = fillColor;
        this.backColor = backColor;
        lineCount = countLines();
    }

    public Message(String text, float x, float y, float height) {
        this(text, x, y, height, NO_COLOR, NO_COLOR);
    }

    public Message(float x, float y, float height, int fillColor, int backColor) {
        this("", x, y, height, fillColor, backColor);
    }

    @Override
    public void render() {
        if (backColor != NO_COLOR) {
            sm.fill(backColor);
            sm.stroke(backColor);
            sm.rect(x, y, width, getPixelHeight());
        }

        sm.fill(fillColor != NO_COLOR ? fillColor : 0);
        sm.textSize(height * TEXT_SIZE_FACTOR);
        sm.text(text, x, y + height * TEXT_SIZE_FACTOR);
    }
}
