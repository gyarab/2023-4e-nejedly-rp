package components;

import Java2D.GameObject;
import Java2D.KeyListener;
import Java2D.MouseListener;
import Java2D.Window;

import static org.lwjgl.glfw.GLFW.GLFW_KEY_ESCAPE;
import static org.lwjgl.glfw.GLFW.GLFW_MOUSE_BUTTON_LEFT;

public class MouseControls extends Component{

    GameObject holdingObject = null;
    GameObject activeObject = null;

    GameObject selectedGameObject = null;

    public void pickupObject(GameObject go) {
        this.holdingObject = go;
        Window.getScene().addGameObjectToScene(go);
    }

    public void setActiveObject(int Uid) {
        this.activeObject = Window.getScene().getGameObject(Uid);
    }

    public GameObject getActiveObject() {
        return activeObject;
    }

    public void place() {
        this.holdingObject = null;
    }

    @Override
    public void editUpdate(float dt){
        if (selectedGameObject == null) {

            for (int i = 0; i < Window.getScene().getGameObjects().size(); i++) {
                GameObject potencioanlGameObject = Window.getScene().getGameObjects().get(i);
                float xPos = potencioanlGameObject.transform.position.x;
                float xScale = potencioanlGameObject.transform.scale.x;
                float yPos = potencioanlGameObject.transform.position.y;
                float yScale = potencioanlGameObject.transform.scale.y;
                if ((xPos <= MouseListener.getWorldPosX() && MouseListener.getWorldPosX() <= (xPos + xScale)) && (yPos <= MouseListener.getWorldPosY() && MouseListener.getWorldPosY() <=(yPos + yScale))) {
                    selectedGameObject = potencioanlGameObject;
                }
            }
        }

        if (selectedGameObject != null) {
            float xPos = selectedGameObject.transform.position.x;
            float yPos = selectedGameObject.transform.position.y;
            float xScale = selectedGameObject.transform.scale.x;
            float yScale = selectedGameObject.transform.scale.y;
            if (xPos <= MouseListener.getWorldPosX() && MouseListener.getWorldPosX() <= xPos + xScale && yPos <= MouseListener.getWorldPosY() && MouseListener.getWorldPosY() <= yPos + yScale && MouseListener.mouseButtonDown(GLFW_MOUSE_BUTTON_LEFT)
            && (selectedGameObject.isSerializing() && !MouseListener.isDragging())) {
                activeObject = selectedGameObject;
            } else if (MouseListener.mouseButtonDown(GLFW_MOUSE_BUTTON_LEFT) && !MouseListener.isDragging()) {
                activeObject = null;
                 xPos = 0;
                 yPos = 0;
                 xScale = 0;
                 yScale = 0;
                for (int i = 0; i < Window.getScene().getGameObjects().size(); i++) {
                    GameObject potencioanlGameObject = Window.getScene().getGameObjects().get(i);
                    xPos = potencioanlGameObject.transform.position.x;
                    xScale = potencioanlGameObject.transform.scale.x;
                    yPos = potencioanlGameObject.transform.position.y;
                    yScale = potencioanlGameObject.transform.scale.y;
                    if (xPos <= MouseListener.getWorldPosX() && MouseListener.getWorldPosX() <= xPos + xScale && yPos <= MouseListener.getWorldPosY() && MouseListener.getWorldPosY() <= yPos + yScale) {
                        selectedGameObject = potencioanlGameObject;
                    }
                }
            }
        }
        if (holdingObject != null) {
            holdingObject.transform.position.x = MouseListener.getWorldPosX();
            holdingObject.transform.position.y = MouseListener.getWorldPosY();
            holdingObject.transform.position.x = ((int)Math.floor(holdingObject.transform.position.x / 0.25f) * 0.25f) + 0.25f/2.0f;
            holdingObject.transform.position.y = ((int)Math.floor(holdingObject.transform.position.y / 0.25f) * 0.25f) + 0.25f/2.0f;

            if (MouseListener.mouseButtonDown(GLFW_MOUSE_BUTTON_LEFT)) {
                place();
            }

            if (KeyListener.isKeyPressed(GLFW_KEY_ESCAPE)) {
                holdingObject.destroy();
                holdingObject = null;
            }
        }// else if (MouseListener.mouseButtonDown(GLFW_MOUSE_BUTTON_LEFT)) {
         //   System.out.println(MouseListener.getOrthoX());
         //   System.out.println(MouseListener.getOrthoY());
        //}
    }
}
