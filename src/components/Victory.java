package components;

import Java2D.GameObject;
import org.jbox2d.dynamics.contacts.Contact;
import org.joml.Vector2f;

public class Victory extends Component{

    @Override
    public void beginCollision(GameObject obj, Contact contact, Vector2f hitsNormal) {
        PlayerController playerController = obj.getComponent(PlayerController.class);
        if (playerController != null) {
            contact.setEnabled(false);
            contact.isEnabled();
            playerController.win();
        }

    }
}
