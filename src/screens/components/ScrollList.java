package screens.components;

import processing.core.PApplet;

import java.util.ArrayList;

import static main.ScreenManager.sm;
import static screens.components.Label.HIGHLIGHT_COLOR;
import static screens.components.Label.TEXT_SIZE_FACTOR;

public class ScrollList {

    private ArrayList<String> entries = new ArrayList<>();
    private final float SINGLE_HEIGHT;
    private final int MAX_ENTRIES_ON_SCREEN;
    private int selected = -1;

    public int getSelected() {
        return selected;
    }

    public void moveSelect(int i) {
        selected += i;
        if (selected < 0)
            selected = entries.size() - 1;

        if (selected >= entries.size())
            selected = 0;
    }

    public ScrollList(float height) {
        this.SINGLE_HEIGHT = height;
        this.MAX_ENTRIES_ON_SCREEN = (int) (sm.getHeight() / SINGLE_HEIGHT);
    }

    public void addEntry(String entry) {
        entries.add(entry);
    }

    public void clear() {
        entries.clear();
    }

    private int getStartIndex() {
        if (selected == -1) return 0;

        int index = 0;
        while (index < selected)
            index += MAX_ENTRIES_ON_SCREEN;

        if (index > selected)
            index -= MAX_ENTRIES_ON_SCREEN;

        return index;
    }

    public void render() {
        int start = getStartIndex();
        int end = PApplet.min(entries.size(), start + MAX_ENTRIES_ON_SCREEN);
        for (int i = start; i < end; i++) {
            String l = entries.get(i);

            sm.textSize(SINGLE_HEIGHT * TEXT_SIZE_FACTOR);
            sm.fill(i == selected ? HIGHLIGHT_COLOR : 0);
            float y = (i - start + 1) * SINGLE_HEIGHT * TEXT_SIZE_FACTOR;
            sm.text(l, 0, y);
        }
    }
}
