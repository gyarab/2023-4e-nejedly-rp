package physics2D.components;

import components.Component;
import components.Victory;
import org.joml.Vector2f;
import rendering.Draw;

public class BoxCollider extends Component {
    private Vector2f halfSize = new Vector2f(1);
    private Vector2f position = new Vector2f();
    private Vector2f offset = new Vector2f();

    public Vector2f getHalfSize() {
        return halfSize;
    }

    public void setHalfSize(Vector2f halfSize) {
        this.halfSize = halfSize;
    }

    public Vector2f getPosition() {
        return position;
    }

    public Vector2f getOffset() {
        return offset;
    }

    @Override
    public void editUpdate(float dt) {
        Vector2f center = new Vector2f(this.gameObject.transform.position.add(this.offset));
        if (gameObject.getComponent(Victory.class) != null) Draw.addBox2D(center, this.halfSize, this.gameObject.transform.rotation);
    }
}
