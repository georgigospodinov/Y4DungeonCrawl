package main;

import processing.core.PApplet;
import util.Logger;
import util.Props;

import java.io.FileNotFoundException;
import java.util.Random;

import static util.PrintFormatting.print;

public class Run {

    public static final Logger L = new Logger("log.txt");
    public static final Random R = new Random();
    public static final Props P = new Props();

    public static void main(String[] args) {
        try {
            P.load("assets/config/game.props");
            P.load("assets/config/screen.props");
            P.load("assets/config/controls.props");
        }
        catch (FileNotFoundException e) {
            L.log(e);
            L.close();
            print("Could not load a configuration file.", "See log.txt for more info.");
            return;
        }

        PApplet.main(new String[]{"main.ScreenManager"});
    }
}
