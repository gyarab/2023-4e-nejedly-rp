package physics2D.components;

import components.Component;
import org.joml.Vector2f;
import physics2D.components.CircleCollider;

public class CicrclePlayerCollider extends Component {
    private transient CircleCollider circle = new CircleCollider();

    public float radius = 0.1f;
    Vector2f offset = new Vector2f();

    public CircleCollider getCircle() {
        return circle;
    }

    @Override
    public void start() {
        this.circle.gameObject = this.gameObject;
        this.circle.setRadius(radius);
    }


}
