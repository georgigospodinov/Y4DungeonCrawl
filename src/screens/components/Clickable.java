package screens.components;

public interface Clickable {
    boolean within(float x, float y);

    void click();

    void render();
}
