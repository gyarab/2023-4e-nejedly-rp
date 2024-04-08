package components;

import Java2D.*;
import org.joml.Vector2f;
import org.joml.Vector4f;
import scenes.LevelEditorScene;

import static org.lwjgl.glfw.GLFW.GLFW_KEY_DELETE;
import static org.lwjgl.glfw.GLFW.GLFW_MOUSE_BUTTON_LEFT;

public class Pivot extends Component{

    private Vector4f xAxisColor = new Vector4f(1, 0.4f, 0.4f, 1);
    private Vector4f xAxisColorHover = new Vector4f(1, 0, 0, 1);
    private Vector4f yAxisColor = new Vector4f(0.4f, 1, 0.4f, 1);
    private Vector4f yAxisColorHover = new Vector4f(0, 1, 0, 1);

    private GameObject xAxisObject;
    private GameObject yAxisObject;
    private SpriteRenderer xAxisSprite;
    private SpriteRenderer yAxisSprite;
    protected GameObject activeGameObject = null;

    private Vector2f xAxisOffset = new Vector2f(25f / 80f, -6f/80f);
    private Vector2f yAxisOffset = new Vector2f(-7f / 80f, 21f/80f);

    private float pivotArrowWidth = 16f /80f;
    private float pivotArrowHeight = 48f / 80f;
    private LevelEditorScene levelEditorScene;
    protected boolean xAxisActive = false;
    protected boolean yAxisActive = false;

    private boolean using = false;

    public Pivot(Sprite arrowSprite, LevelEditorScene editorScene) {
        this.xAxisObject = GameObjGen.generateSpriteObject(arrowSprite, pivotArrowWidth, pivotArrowHeight);
        this.yAxisObject = GameObjGen.generateSpriteObject(arrowSprite, pivotArrowWidth, pivotArrowHeight);
        this.xAxisSprite = this.xAxisObject.getComponent(SpriteRenderer.class);
        this.yAxisSprite = this.yAxisObject.getComponent(SpriteRenderer.class);
        this.levelEditorScene = editorScene;
        Window.getScene().addGameObjectToScene(this.xAxisObject);
        Window.getScene().addGameObjectToScene(this.yAxisObject);
    }

    @Override
    public void start() {
        this.xAxisObject.disableSerialization();
        this.yAxisObject.disableSerialization();
        this.xAxisObject.transform.rotation = -90;
        this.xAxisObject.transform.zIndex = 100;
        this.yAxisObject.transform.zIndex = 100;

    }

    @Override
    public void update(float dt) {
        if (using) {
            this.setInactive();
        }
    }



    @Override
    public void editUpdate(float dt) {
        if (!using) return;

        this.activeGameObject = this.levelEditorScene.getActiveGameObject();
        if (this.activeGameObject != null) {
            this.setActive();

           if (KeyListener.isKeyPressedStart(GLFW_KEY_DELETE)) {
               this.activeGameObject.destroy();
               this.levelEditorScene.setActiveGameObject(null);
               this.setInactive();
               return;
           }
        } else {
            setInactive();
            return;
        }

        boolean xAxisHot = checkXHoverState();
        boolean yAxisHot = checkYHoverState();

        if ((xAxisHot || xAxisActive) && MouseListener.isDragging() && MouseListener.mouseButtonDown(GLFW_MOUSE_BUTTON_LEFT)) {
            xAxisActive = true;
            yAxisActive = false;
        } else if ((yAxisHot || yAxisActive) && MouseListener.isDragging() && MouseListener.mouseButtonDown(GLFW_MOUSE_BUTTON_LEFT))  {
            xAxisActive = false;
            yAxisActive = true;
        } else {
            xAxisActive = false;
            yAxisActive = false;
        }

        if (this.activeGameObject != null) {
            this.xAxisObject.transform.position.set(this.activeGameObject.transform.position);
            this.yAxisObject.transform.position.set(this.activeGameObject.transform.position);
            this.xAxisObject.transform.position.add(this.xAxisOffset);
            this.yAxisObject.transform.position.add(this.yAxisOffset);
        }

    }

    private void setActive() {
        this.xAxisSprite.setColor(xAxisColor);
        this.yAxisSprite.setColor(yAxisColor);
    }

    protected void setInactive() {
        this.activeGameObject = null;
        this.xAxisSprite.setColor(new Vector4f(0, 0, 0, 0));
        this.yAxisSprite.setColor(new Vector4f(0, 0, 0, 0));
    }

    private boolean checkXHoverState() {
        Vector2f mousePos = new Vector2f(MouseListener.getWorldPos());
        if (xAxisObject.transform.position.x - (pivotArrowHeight / 2.0f) <= mousePos.x && mousePos.x <= xAxisObject.transform.position.x + (pivotArrowHeight / 2.0f) &&
                xAxisObject.transform.position.y + (pivotArrowWidth / 2.0f) >= mousePos.y && mousePos.y >= xAxisObject.transform.position.y - (pivotArrowWidth / 2.0f)) {
            xAxisSprite.setColor(xAxisColorHover);
            return true;
        }

        xAxisSprite.setColor(xAxisColor);
        return false;
    }

    private boolean checkYHoverState() {
        Vector2f mousePos = new Vector2f(MouseListener.getWorldPos());
        if (yAxisObject.transform.position.x - (pivotArrowWidth / 2.0f) <= mousePos.x && mousePos.x <= yAxisObject.transform.position.x + (pivotArrowWidth / 2.0f) &&
                yAxisObject.transform.position.y - (pivotArrowHeight / 2.0f)  <= mousePos.y && mousePos.y <= yAxisObject.transform.position.y + (pivotArrowHeight / 2.0f) ) {
            yAxisSprite.setColor(yAxisColorHover);
            return true;
        }

        yAxisSprite.setColor(yAxisColor);
        return false;
    }

    public void setUsing() {
        this.using = true;
    }

    public void setNotUsing() {
        this.using = false;
        this.setInactive();
    }
}

