package minusk.miner;

import minusk.miner.world.World;

/**
 * Created by MinusKelvin on 2015-07-11.
 */
class Miner {
    private static boolean looping = true;

    public static final World world = new World();

    public static void main(String[] args) {
        Graphics.initialize();

        while (looping) {
            Graphics.beginFrame();

            world.render();

            Graphics.endFrame();
        }
    }

    public static void endLoop() {
        looping = false;
    }
}
