package screens;

import screens.components.Button;
import screens.components.Message;

import static main.ScreenManager.sm;

public class Lore extends AScreen {

    public Lore() {
        Message m = new Message("Since you're not brave enough to go and figure this out on your own...", DETAILS_X, DETAILS_Y, DETAILS_SIZE);
        m.addLine("You are about to explore the dark lair of the Arcanists. They are practitioners of dark transmutation rituals.");
        m.addLine("They take good old goblins and transform them into powerful mindless minions.");
        m.addLine("It is up to YOU to kill all abominations and stop them from unleashing chaos upon our world.");
        m.addLine("You must also loot any and all items that you find in the Arcanists' lair. " +
                "They should not have the materials to create more of those horrible creatures.");
        m.addLine("Once your quest is complete, you will be rewarded beyond your wildest dreams.*");
        m.addLine("Here is the information that our spies have gathered over the last centuries:");
        m.addLine("Specters can pass through walls and can only be harmed by magical items.");
        m.addLine("Werewolves can sense you from far away and will eat your flesh to regenerate themselves.");
        m.addLine("Arcanists can use magic to find you wherever you are and to empower their attacks and defense.");
        m.addLine("Gargoyles can take a lot of hits and can hit you hard in return.");
        m.addLine("Note that you may see monsters that are horrible stitches of these type, " +
                "created by the aforementioned dark transmutation rituals.");
        m.addLine("Beware the SWAG, for it is the disturbing combination of all monsters.");
        m.addLine("");
        m.addLine("");
        m.addLine("*Note: You will receive your payment only upon defeating all the Arcanists and thereby bringing an end to this war.");
        m.addLine("So far, it has lasted 752 years, so do not be discouraged if you fail and die at the hands of SWAG.");
        labels.add(m);

        Button back = new Button("I am ready to take the quest!", BACK_X, BACK_Y, BACK_SIZE);
        back.onClick(() -> sm.setScreen(new Menu()));
        clickables.add(back);
    }
}
