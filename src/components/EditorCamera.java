package components;

import Java2D.Camera;
import Java2D.KeyListener;
import Java2D.MouseListener;
import org.joml.Vector2f;

import static org.lwjgl.glfw.GLFW.GLFW_KEY_KP_DECIMAL;
import static org.lwjgl.glfw.GLFW.GLFW_MOUSE_BUTTON_MIDDLE;

public class EditorCamera extends Component{

    private float dragDelay = 0.11f;
    private final Camera levelEditorCamera;
    private Vector2f clickStartPos;
    private float dragSens = 30.0f;
    private float scrollSens = 0.1f;
    private float lerpTime = 0.0f;
    private boolean reset = false;

    public EditorCamera(Camera levelEditorCamera) {
        this.levelEditorCamera = levelEditorCamera;
        this.clickStartPos = new Vector2f();
    }

    @Override
    public void editUpdate(float dt) {
        if (MouseListener.mouseButtonDown(GLFW_MOUSE_BUTTON_MIDDLE) && dragDelay > 0) {
            this.clickStartPos = new Vector2f(MouseListener.getWorldPosX(), MouseListener.getWorldPosY());
            dragDelay -= dt;
            return;
        } else if (MouseListener.mouseButtonDown(GLFW_MOUSE_BUTTON_MIDDLE)) {
            Vector2f delta = new Vector2f(MouseListener.getWorldPosX(), MouseListener.getWorldPosY()).sub(this.clickStartPos);
            levelEditorCamera.position.sub(delta.mul(dt).mul(dragSens));
            this.clickStartPos.lerp(new Vector2f(MouseListener.getWorldPosX(), MouseListener.getWorldPosY()), dt);
        }

        if (dragDelay <= 0.0f && !MouseListener.mouseButtonDown(GLFW_MOUSE_BUTTON_MIDDLE)) {
            dragDelay = 0.1f;
        }

        if (MouseListener.getScrollY() != 0.0f) {
            float addValue = (float) Math.pow(Math.abs(MouseListener.getScrollY() * scrollSens), (1 / levelEditorCamera.getZoom()));
            addValue *= -Math.signum(MouseListener.getScrollY());
            levelEditorCamera.addZoom(addValue);
        }

        if (KeyListener.isKeyPressed(GLFW_KEY_KP_DECIMAL)) {
            reset = true;
        }

        if (reset) {
            levelEditorCamera.position.lerp(new Vector2f(), lerpTime);
            levelEditorCamera.setZoom(this.levelEditorCamera.getZoom() + ((1.0f - levelEditorCamera.getZoom()) * lerpTime));
            this.lerpTime += 0.1f * dt;
            if (Math.abs(levelEditorCamera.position.x) <= 5.0f && Math.abs(levelEditorCamera.position.y)  <= 5.0f) {
                this.lerpTime = 0.0f;
                levelEditorCamera.position.set(0f, 0f);
                this.levelEditorCamera.setZoom(1);
                reset = false;
            }
        }
    }

    @Override
    public void update(float dt) {
        if (MouseListener.mouseButtonDown(GLFW_MOUSE_BUTTON_MIDDLE) && dragDelay > 0) {
            this.clickStartPos = new Vector2f(MouseListener.getWorldPosX(), MouseListener.getWorldPosY());
            dragDelay -= dt;
            return;
        } else if (MouseListener.mouseButtonDown(GLFW_MOUSE_BUTTON_MIDDLE)) {
            Vector2f delta = new Vector2f(MouseListener.getWorldPosX(), MouseListener.getWorldPosY()).sub(this.clickStartPos);
            levelEditorCamera.position.sub(delta.mul(dt).mul(dragSens));
            this.clickStartPos.lerp(new Vector2f(MouseListener.getWorldPosX(), MouseListener.getWorldPosY()), dt);
        }

        if (dragDelay <= 0.0f && !MouseListener.mouseButtonDown(GLFW_MOUSE_BUTTON_MIDDLE)) {
            dragDelay = 0.1f;
        }

        if (MouseListener.getScrollY() != 0.0f) {
            float addValue = (float) Math.pow(Math.abs(MouseListener.getScrollY() * scrollSens), (1 / levelEditorCamera.getZoom()));
            addValue *= -Math.signum(MouseListener.getScrollY());
            levelEditorCamera.addZoom(addValue);
        }

    }
}
