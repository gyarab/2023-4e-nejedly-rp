package Java2D;

import org.joml.Matrix4f;
import org.joml.Vector2f;
import org.joml.Vector4f;

import static org.lwjgl.glfw.GLFW.*;

public class MouseListener {
    private static MouseListener mouseListenerInstance;
    private double scrollX, scrollY;
    private double xPos,yPos, lastX,lastY, worldPosX, worldPosY, lastWorldX, lastWorldY;
    private boolean mouseButtonPressed[] = new boolean[9];
    private boolean isDragging;

    private int mouseButtonsDown = 0;

    private MouseListener() {
        this.scrollX = 0.0;
        this.scrollY = 0.0;
        this.xPos = 0.0;
        this.yPos = 0.0;
        this.lastX = 0.0;
        this.lastY = 0.0;
    }

    public static MouseListener getInstance(){
        if (MouseListener.mouseListenerInstance == null) {
            MouseListener.mouseListenerInstance = new MouseListener();
        }
        return MouseListener.mouseListenerInstance;
    }
    //https://www.glfw.org/docs/latest/input_guide.html#cursor_pos
    public static void mousePosCallback(long window, double xpos, double ypos){
        if (getInstance().mouseButtonsDown > 0) {
            getInstance().isDragging = true;
        }
        getInstance().lastX = getInstance().xPos;
        getInstance().lastY = getInstance().yPos;
        getInstance().lastWorldX = getWorldPosX();
        getInstance().lastWorldY = getWorldPosY();
        getInstance().xPos = xpos;
        getInstance().yPos = ypos;
    }
    //https://www.glfw.org/docs/latest/input_guide.html#input_mouse_button
    public static void mouseButtonCallback(long window, int button, int action, int mods){
        if (action == GLFW_PRESS) {
            getInstance().mouseButtonsDown++;
            if (button < getInstance().mouseButtonPressed.length) {
                getInstance().mouseButtonPressed[button] = true;
            }
        } else if (action == GLFW_RELEASE) {
            getInstance().mouseButtonsDown--;

            if (button < getInstance().mouseButtonPressed.length) {
                getInstance().mouseButtonPressed[button] = false;
                getInstance().isDragging = false;
            }
        }
    }
    //https://www.glfw.org/docs/latest/input_guide.html#scrolling
    public static void mouseScrollCallback(long window, double xoffset, double yoffset){
        getInstance().scrollX = xoffset;
        getInstance().scrollY = yoffset;
    }

    public static void endFrame(){
        getInstance().scrollX = 0;
        getInstance().scrollY = 0;
        getInstance().lastX = getInstance().xPos;
        getInstance().lastY = getInstance().yPos;
        getInstance().lastWorldX = getWorldPosX();
        getInstance().lastWorldY = getWorldPosY();
    }

    public static float getScrollX() {
        return (float) getInstance().scrollX;
    }

    public static float getScrollY() {
        return (float) getInstance().scrollY;
    }


    public static float getXPos(){
        return (float)getInstance().xPos;
    }

    public static float getYPos(){
        return (float)getInstance().yPos;
    }

    public static float getDx() {
        return (float)(getInstance().lastX - getInstance().xPos);
    }

    public static float getDy() {
        return (float)(getInstance().lastY - getInstance().yPos);
    }
    public static float getWorldDx() {
        return (float)(getInstance().lastWorldX - getWorldPosX());
    }
    public static float getWorldDy() {
        return (float)(getInstance().lastWorldY - getWorldPosY());
    }

    public static boolean isDragging(){
        return getInstance().isDragging;
    }

    public static boolean mouseButtonDown(int button){
        if (button < getInstance().mouseButtonPressed.length) {
            return getInstance().mouseButtonPressed[button];
        } else {
            return false;
        }
    }

    public static float getWorldPosX() {
        return getWorldPos().x;
    }

    public static float getWorldPosY() {
        return getWorldPos().y;

    }

    public static Vector2f getWorldPos() {
        float currentX = getXPos();
        currentX = (currentX / (float) Window.getWidth()) * 2.0f - 1.0f;
        float currentY =  Window.getHeight() - getYPos();
        currentY = (currentY / (float) Window.getHeight()) * 2.0f - 1.0f;
        Vector4f tmp = new Vector4f(currentX, currentY, 0, 1);

        Matrix4f invView = new Matrix4f(Window.getScene().camera().getInverseView());
        Matrix4f invProjection = new Matrix4f(Window.getScene().camera().getInverseProjection());
        tmp.mul(invView.mul(invProjection));

        return new Vector2f(tmp.x, tmp.y);
    }
}
