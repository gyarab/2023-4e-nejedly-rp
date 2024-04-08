package components;

import Java2D.Camera;
import Java2D.GameObject;
import Java2D.Window;
import org.joml.Vector2f;
import org.joml.Vector4f;

public class  GameCamera extends Component{
    private transient GameObject player;
    private transient Camera gameCamera;

    private transient Vector4f backGroundColor = new Vector4f(173.0f/255f, 216.0f/255, 230.0f/255, 1); // light blue color


    public GameCamera(Camera cam) {
        this.gameCamera = cam;

    }

    @Override
    public void start() {
        this.player = Window.getScene().getGameObject(PlayerController.class);


    }

    @Override
    public void update(float dt) {

        if (player != null && !player.getComponent(PlayerController.class).isDead()) {
            gameCamera.clearColor.set(backGroundColor);
            gameCamera.position.x = (player.transform.position.x - 2f);
            gameCamera.position.y = (player.transform.position.y - 1f);

        }

    }



}
