package items.individual;

import processing.core.PApplet;

import static main.ScreenManager.player;

public class Armor extends AItem {

    public final int defenseBonus, agilityPenalty;
    public final boolean magical;

    public Armor(String name, String invDescription, int value, float dropChance, float renderFactor, int color, int defenseBonus, int agilityPenalty, boolean magical) {
        super(name, invDescription, value, dropChance, color, renderFactor);
        this.defenseBonus = defenseBonus;
        this.agilityPenalty = agilityPenalty;
        this.magical = magical;
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof Armor))
            return false;

        Armor other = (Armor) obj;
        if (!super.equals(other)) return false;

        // Two armors are the same if they have the same bonuses.
        return this.defenseBonus == other.defenseBonus && this.agilityPenalty == other.agilityPenalty;
    }

    @Override
    public AItem copy() {
        return new Armor(name, invDescription, value, dropChance, renderFactor, color, defenseBonus, agilityPenalty, magical);
    }

    @Override
    public void use(int inventoryIndex) {
        player.equipArmor(this, inventoryIndex);
    }

    @Override
    public String asInventoryEntry() {
        String prefixDB = defenseBonus < 0 ? "  " : "  +";
        String prefixAP = agilityPenalty < 0 ? "   +" : "   -";
        int ap = PApplet.abs(agilityPenalty);
        return super.asInventoryEntry() + prefixDB + defenseBonus + " Defense," + prefixAP + ap + " Agility";
    }
}
