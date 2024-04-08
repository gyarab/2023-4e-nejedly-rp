package physics2D;

import Java2D.GameObject;
import components.Transform;
import org.jbox2d.collision.shapes.CircleShape;
import org.jbox2d.collision.shapes.PolygonShape;
import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.*;
import org.joml.Vector2f;
import physics2D.components.BoxCollider;
import physics2D.components.CircleCollider;
import physics2D.components.Rigidbody;
import physics2D.enums.ContactListener;

public class Physics2D {
    private Vec2 gravity = new Vec2(0, -10.0f);
    private World world = new World(gravity);
    private float physicsTime = 0.0f;
    private float physicsTimeStep = 1.0f / 60.0f;
    private int velocityIterations = 8;
    private int positionIterations = 3;

    public Physics2D() {
        world.setContactListener(new ContactListener());
    }

    public Vector2f getGravity() {
        return new Vector2f(world.getGravity().x, world.getGravity().y);
    }

    public void add(GameObject gameObject) {
        Rigidbody rigidbody = gameObject.getComponent(Rigidbody.class);
        if (rigidbody != null && rigidbody.getBody() == null) {
            Transform transform = gameObject.transform;

            BodyDef bodyDef = new BodyDef();
            bodyDef.angle = (float) Math.toRadians(transform.rotation);
            bodyDef.position.set(transform.position.x, transform.position.y);
            bodyDef.angularDamping = rigidbody.getAngularFriction();
            bodyDef.linearDamping = rigidbody.getLinearFriction();
            bodyDef.fixedRotation = rigidbody.isFixedRotation();
            bodyDef.bullet = rigidbody.isContinuousCollision();
            bodyDef.gravityScale = rigidbody.gravityScale;
            bodyDef.angularVelocity = rigidbody.angularVelocity;
            bodyDef.userData = rigidbody.gameObject;

            switch (rigidbody.getBodyType()) {
                case Kinematic:
                    bodyDef.type = BodyType.KINEMATIC;
                    break;
                case Static:
                    bodyDef.type = BodyType.STATIC;
                    break;
                case Dynamic:
                    bodyDef.type = BodyType.DYNAMIC;
                    break;
            }

            Body body = this.world.createBody(bodyDef);
            body.m_mass = rigidbody.getMass();
            rigidbody.setBody(body);
            CircleCollider circleCollider;
            BoxCollider boxCollider;

            if ((circleCollider = gameObject.getComponent(CircleCollider.class)) != null) {
                addCircleCollider(rigidbody, circleCollider);
            } else if ((boxCollider = gameObject.getComponent(BoxCollider.class)) != null) {
                addBox2DColiider(rigidbody, boxCollider);
            }

        }
    }

    public void destroyGameObject(GameObject gameObject) {
        Rigidbody rigidbody = gameObject.getComponent(Rigidbody.class);
        if (rigidbody != null) {
            if (rigidbody.getBody() != null) {
                world.destroyBody(rigidbody.getBody());
                rigidbody.setBody(null);
            }
        }
    }

    public void update(float dt){
        physicsTime += dt;
        if (physicsTime >= 0.0f) {
            physicsTime -= physicsTimeStep;
            world.step(physicsTimeStep, velocityIterations, positionIterations);
        }
    }

    public void setIsSensor(Rigidbody rigidbody) {
        Body body = rigidbody.getBody();
        if (body == null) return;

        Fixture fixture = body.getFixtureList();
        while (fixture != null) {
            fixture.m_isSensor = true;
            fixture = fixture.m_next;
        }
    }

    public void setNonSensor(Rigidbody rigidbody) {
        Body body = rigidbody.getBody();
        if (body == null) return;

        Fixture fixture = body.getFixtureList();
        while (fixture != null) {
            fixture.m_isSensor = false;
            fixture = fixture.m_next;
        }
    }

    public void resetCicrcleCollider(Rigidbody rigidbody, CircleCollider circleCollider) {
        Body body = rigidbody.getBody();
        if (body == null) return;

        int size = fixtureListSize(body);
        for (int i = 0; i < size; i++) {
            body.destroyFixture(body.getFixtureList());
        }

        addCircleCollider(rigidbody, circleCollider);
        body.resetMassData();
    }

    public void resetBox2DCollider(Rigidbody rigidbody, BoxCollider boxCollider) {
        Body body = rigidbody.getBody();
        if (body == null) return;

        int size = fixtureListSize(body);
        for (int i = 0; i < size; i++) {
            body.destroyFixture(body.getFixtureList());
        }

        addBox2DColiider(rigidbody, boxCollider);
        body.resetMassData();
    }

    public void addCircleCollider(Rigidbody rigidbody, CircleCollider circleCollider) {
        Body body = rigidbody.getBody();

        CircleShape shape = new CircleShape();
        shape.setRadius(circleCollider.getRadius());
        shape.m_p.set(circleCollider.getOffset().x, circleCollider.getOffset().y);

        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.shape = shape;
        fixtureDef.density = 1.0f;
        fixtureDef.friction = rigidbody.getFriction();
        fixtureDef.userData = circleCollider.gameObject;
        fixtureDef.isSensor = rigidbody.isSensor();

        body.createFixture(fixtureDef);
    }
    public void addBox2DColiider(Rigidbody rigidbody, BoxCollider boxCollider) {
        Body body = rigidbody.getBody();

        PolygonShape shape = new PolygonShape();
        Vector2f halfSize = new Vector2f(boxCollider.getHalfSize()).mul(0.5f);
        Vector2f offset = boxCollider.getOffset();
        Vector2f pos = new Vector2f(boxCollider.getPosition());
        shape.setAsBox(halfSize.x, halfSize.y, new Vec2(pos.x + offset.x, pos.y + offset.y), 0);

        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.shape = shape;
        fixtureDef.density = 1.0f;
    //    fixtureDef.friction = rigidbody.getFriciton();
        fixtureDef.userData = boxCollider.gameObject;
    //    fixtureDef.isSensor = rigidbody.isSensor();

        body.createFixture(fixtureDef);
    }

    private int fixtureListSize(Body body) {
        int size = 0;
        Fixture fixture = body.getFixtureList();

        while (fixture != null) {
            size++;
            fixture = fixture.m_next;
        }

        return size;
    }
}
