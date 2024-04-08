package physics2D.components;

import components.Component;
import org.joml.Vector2f;
import rendering.Draw;

public class CircleCollider extends Component {
    private float radius = 0.12f;
    private Vector2f offset = new Vector2f();
    public float getRadius() {
        return radius;
    }

    public Vector2f getOffset() {
        return offset;
    }

    public void setRadius(float radius) {
        this.radius = radius;
    }

    @Override
    public void start() {

    }

    @Override
    public void editUpdate(float dt) {
        Vector2f center = new Vector2f(this.gameObject.transform.position).add(this.offset);
        Draw.addCircle(center, radius);
    }

    @Override
    public void update(float dt) {

    }
}
