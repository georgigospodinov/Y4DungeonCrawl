package items;

import util.PrintFormatting;

import java.util.LinkedList;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

import static util.PrintFormatting.print;

public class Test {

    public static void main(String[] args) {
        LinkedList<Integer> nums = new LinkedList<>();
        nums.add(10);
        nums.add(20);
        nums.add(30);
        nums.add(null);
        print(nums);

        nums.remove(1);
        print(nums);

        Function<Integer, Float> myFunction = Test::half;
        Consumer<Integer> myConsumer = PrintFormatting::print;
        Supplier<Float> mySupplier = () -> 12f;
        print(myFunction.apply(17));
    }

    private static Float half(Integer integer) {
        return integer / 2f;
    }
}
