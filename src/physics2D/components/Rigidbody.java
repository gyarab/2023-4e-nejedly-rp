package physics2D.components;

import Java2D.Window;
import components.Component;
import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.Body;
import org.joml.Vector2f;
import physics2D.enums.BodyType;

public class Rigidbody extends Component {
    private Vector2f velocity = new Vector2f();
    private float linearFriction = 0.8f;
    private float angularFriction = 0.9f;
    private float mass = 0;
    private BodyType bodyType = BodyType.Dynamic;

    private float friction = 0.1f;
    public float angularVelocity = 0.0f;
    public float gravityScale = 1.0f;
    private boolean isSensor = false;

    public float getFriction() {
        return friction;
    }

    public void setFriction(float friction) {
        this.friction = friction;
    }

    public float getAngularVelocity() {
        return angularVelocity;
    }

    public void setAngularVelocity(float angularVelocity) {
        this.angularVelocity = angularVelocity;
    }

    public float getGravityScale() {
        return gravityScale;
    }

    public void setGravityScale(float gravityScale) {
        this.gravityScale = gravityScale;
        if (body != null) body.setGravityScale(gravityScale);
    }

    public boolean isSensor() {
        return isSensor;
    }

    public void enableSensor() {
        isSensor = true;
        if (body != null) Window.getPhysics().setIsSensor(this);
    }

    public void disableSensor() {
        isSensor = false;
        if (body != null) Window.getPhysics().setNonSensor(this);
    }

    private  boolean fixedRotation = false;
    private boolean continuousCollision = true;

    //https://www.youtube.com/watch?v=e3VSVZn4BHg
    private transient Body body = null;

    @Override
    public void update(float dt) {
        if (body != null) {
            if (this.bodyType == BodyType.Dynamic || this.bodyType == BodyType.Kinematic) {
                this.gameObject.transform.position.set(body.getPosition().x, body.getPosition().y);


                this.gameObject.transform.rotation = (float) Math.toDegrees(body.getAngle());
                Vec2 vel = body.getLinearVelocity();
                this.velocity.set(vel.x, vel.y);
            } else if (this.bodyType == BodyType.Static) {
                this.body.setTransform(new Vec2(this.gameObject.transform.position.x, this.gameObject.transform.position.y), (float) Math.toRadians(gameObject.transform.rotation));
            }
        }
    }

    public Vector2f getVelocity() {
        return velocity;
    }

    public void setVelocity(Vector2f velocity) {
        this.velocity.set(velocity);
        if (body != null) {
            body.setLinearVelocity(new Vec2(velocity.x, velocity.y));
        }
    }

    public void addVelocity(Vector2f force) {
        if (body != null) body.applyForceToCenter(new Vec2(velocity.x, velocity.y));
    }

    public void addImpulse(Vector2f impulse) {
        if (body != null) body.applyLinearImpulse(new Vec2(velocity.x, velocity.y),body.getWorldCenter());
    }

    public float getLinearFriction() {
        return linearFriction;
    }

    public void setLinearFriction(float linearFriction) {
        this.linearFriction = linearFriction;
    }

    public float getAngularFriction() {
        return angularFriction;
    }

    public void setAngularFriction(float angularFriction) {
        this.angularFriction = angularFriction;
        if (body != null) {
            this.body.setAngularVelocity(angularVelocity);
        }
    }

    public float getMass() {
        return mass;
    }

    public void setMass(float mass) {
        this.mass = mass;
    }

    public BodyType getBodyType() {
        return bodyType;
    }

    public void setBodyType(BodyType bodyType) {
        this.bodyType = bodyType;
    }

    public boolean isFixedRotation() {
        return fixedRotation;
    }

    public void setFixedRotation(boolean fixedRotation) {
        this.fixedRotation = fixedRotation;
    }

    public boolean isContinuousCollision() {
        return continuousCollision;
    }

    public void setContinuousCollision(boolean continuousCollision) {
        this.continuousCollision = continuousCollision;
    }

    public Body getBody() {
        return body;
    }

    public void setBody(Body body) {
        this.body = body;
    }
}
