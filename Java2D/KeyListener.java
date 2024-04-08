package Java2D;

import static org.lwjgl.glfw.GLFW.*;

public class KeyListener {
    private static KeyListener keyListenerInstance;
    //https://www.glfw.org/docs/3.3/group__keys.html konci okolo 350
    private boolean keyPressed[] = new boolean[350];
    private boolean keyPressStart[] = new boolean[350];

    private KeyListener() {

    }

    public static KeyListener getInstance() {
        if (KeyListener.keyListenerInstance == null){
            KeyListener.keyListenerInstance = new KeyListener();
        }
        return KeyListener.keyListenerInstance;
    }

    public static void keyCallback(long window, int key, int scancode, int action, int mods){
        if (key != -1) {
            if (action == GLFW_PRESS) {
                getInstance().keyPressed[key] = true;
                getInstance().keyPressStart[key] = true;
            } else if (action == GLFW_RELEASE) {
                getInstance().keyPressed[key] = false;
                getInstance().keyPressStart[key] = false;
            }
       }
    }

    public static boolean isKeyPressed(int keyCode) {
        if (keyCode < getInstance().keyPressed.length) {
          return getInstance().keyPressed[keyCode];
        } else {
            return false;
        }
    }

    public static boolean isKeyPressedStart(int keyCode) {
        boolean key = getInstance().keyPressStart[keyCode];
        if (key) {
            getInstance().keyPressStart[keyCode] = false;
        }
        return key;
    }
}
