package items.individual;

import static main.Run.P;
import static main.ScreenManager.sm;

public class Torch extends Weapon {

    private static final int TORCH_COLOR;
    private static final float TORCH_RENDER_FACTOR = P.getFloat("torch render factor");

    static {
        int red, green, blue;
        red = P.getInt("torch color red");
        green = P.getInt("torch color green");
        blue = P.getInt("torch color blue");
        TORCH_COLOR = sm.color(red, green, blue);
    }

    public Torch(int value) {
        super("Torch", "A torch that illuminates some of the darkness", value, 1,
                TORCH_COLOR, TORCH_RENDER_FACTOR, 0, "The torch has illuminated a ",
                "Torch it.", "Hm, torching seems to work",
                "Hope you didn't have your hopes up.", "Torched.", false);
    }

    @Override
    public AItem copy() {
        return new Torch(value);
    }

    @Override
    public String asInventoryEntry() {
        return super.asInventoryEntry() + ",  +" + value + " awareness";
    }
}
