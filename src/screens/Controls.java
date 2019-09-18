package screens;

import screens.components.Button;
import screens.components.Message;

import static main.ScreenManager.sm;

public class Controls extends AScreen {

    public Controls() {
        Message m = new Message("Since you're not brave enough to go and figure this out on your own...", DETAILS_X, DETAILS_Y, DETAILS_SIZE);
        m.addLine("Arrow keys - move & navigate inventory");
        m.addLine("A - Attack (when in combat)");
        m.addLine("U - Use an item (when in combat)");
        m.addLine("I - open/close Inventory");
        m.addLine("D - Destroy an item (when in inventory)");
        m.addLine("Enter - use an item (when in inventory)");
        m.addLine("Note: Changes to the configuration files are not reflected.");
        labels.add(m);

        Button back = new Button("I am ready to take the quest!", BACK_X, BACK_Y, BACK_SIZE);
        back.onClick(() -> sm.setScreen(new Menu()));
        clickables.add(back);
    }
}
