package components;

import Java2D.KeyListener;
import Java2D.Window;
import scenes.LevelEditorScene;

import static org.lwjgl.glfw.GLFW.GLFW_KEY_E;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_R;

public class PivotSystem extends Component{
    private Spritesheet pivotSpritesheet;
    private int usingPivot = 0;

    public PivotSystem(Spritesheet pivotSpritesheet) {
        this.pivotSpritesheet = pivotSpritesheet;
    }

    @Override
    public void start() {
        gameObject.addComponent(new PositionTransformPivot(pivotSpritesheet.getSprite(1), (LevelEditorScene) Window.getScene()));
        gameObject.addComponent(new ScaleTransformPivot(pivotSpritesheet.getSprite(2), (LevelEditorScene) Window.getScene()));
    }

    @Override
    public void editUpdate(float dt) {
        if (usingPivot == 0) {
            gameObject.getComponent(PositionTransformPivot.class).setUsing();
            gameObject.getComponent(ScaleTransformPivot.class).setNotUsing();
        } else if (usingPivot == 1) {
            gameObject.getComponent(PositionTransformPivot.class).setNotUsing();
            gameObject.getComponent(ScaleTransformPivot.class).setUsing();
        }

        if (KeyListener.isKeyPressed(GLFW_KEY_E)) {
            usingPivot = 0;
        } else if (KeyListener.isKeyPressed(GLFW_KEY_R)) {
            usingPivot = 1;
        }
    }
}
