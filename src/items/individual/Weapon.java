package items.individual;

import static main.ScreenManager.player;

public class Weapon extends AItem {

    public final int attackBonus;
    public final String enemyText, attackDescription, onDamage, noDamage, kill;
    public final boolean magical;

    public Weapon(String name, String invDescription, int value, float dropChance, int color, float renderFactor,
                  int attackBonus, String enemyText, String attackDescription, String onDamage, String noDamage, String kill, boolean magical) {
        super(name, invDescription, value, dropChance, color, renderFactor);
        this.attackBonus = attackBonus;
        this.enemyText = enemyText;
        this.attackDescription = attackDescription;
        this.onDamage = onDamage;
        this.noDamage = noDamage;
        this.kill = kill;
        this.magical = magical;
    }

    @Override
    public AItem copy() {
        return new Weapon(name, invDescription, value, dropChance, color, renderFactor,
                attackBonus, enemyText, attackDescription, onDamage, noDamage, kill, magical);
    }

    @Override
    public void use(int inventoryIndex) {
        player.equipWeapon(this, inventoryIndex);
    }

    @Override
    public String asInventoryEntry() {
        return super.asInventoryEntry() + "  +" + attackBonus + " Attack";
    }
}
