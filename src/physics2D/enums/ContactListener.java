package physics2D.enums;

import Java2D.GameObject;
import components.Component;
import org.jbox2d.callbacks.ContactImpulse;
import org.jbox2d.collision.Manifold;
import org.jbox2d.collision.WorldManifold;
import org.jbox2d.dynamics.contacts.Contact;
import org.joml.Vector2f;

public class ContactListener implements org.jbox2d.callbacks.ContactListener {
    @Override//2
    public void beginContact(Contact contact) {
        GameObject obj1 = (GameObject) contact.getFixtureA().getUserData();
        GameObject obj2 = (GameObject) contact.getFixtureB().getUserData();
        WorldManifold worldManifold = new WorldManifold();
        contact.getWorldManifold(worldManifold);
        Vector2f normal1Dir = new Vector2f(worldManifold.normal.x, worldManifold.normal.y);
        Vector2f normal2Dir = new Vector2f(normal1Dir).negate();

        for (Component c : obj1.getAllComponents()) {
            c.beginCollision(obj2, contact, normal1Dir);

        }
        for (Component c : obj1.getAllComponents()) {
            c.beginCollision(obj1, contact, normal2Dir);
        }
    }

    @Override//3
    public void endContact(Contact contact) {
        GameObject obj1 = (GameObject) contact.getFixtureA().getUserData();
        GameObject obj2 = (GameObject) contact.getFixtureB().getUserData();
        WorldManifold worldManifold = new WorldManifold();
        contact.getWorldManifold(worldManifold);
        Vector2f normal1Dir = new Vector2f(worldManifold.normal.x, worldManifold.normal.y);
        Vector2f normal2Dir = new Vector2f(normal1Dir).negate();

        for (Component c : obj1.getAllComponents()) {
            c.endCollision(obj2, contact, normal1Dir);

        }
        for (Component c : obj1.getAllComponents()) {
            c.endCollision(obj1, contact, normal2Dir);
        }
    }

    @Override//1
    public void preSolve(Contact contact, Manifold manifold) {
        GameObject obj1 = (GameObject) contact.getFixtureA().getUserData();
        GameObject obj2 = (GameObject) contact.getFixtureB().getUserData();
        WorldManifold worldManifold = new WorldManifold();
        contact.getWorldManifold(worldManifold);
        Vector2f normal1Dir = new Vector2f(worldManifold.normal.x, worldManifold.normal.y);
        Vector2f normal2Dir = new Vector2f(normal1Dir).negate();

        for (Component c : obj1.getAllComponents()) {
            c.preSolve(obj2, contact, normal1Dir);

        }
        for (Component c : obj1.getAllComponents()) {
            c.preSolve(obj1, contact, normal2Dir);
        }
    }

    @Override//4
    public void postSolve(Contact contact, ContactImpulse contactImpulse) {
        GameObject obj1 = (GameObject) contact.getFixtureA().getUserData();
        GameObject obj2 = (GameObject) contact.getFixtureB().getUserData();
        WorldManifold worldManifold = new WorldManifold();
        contact.getWorldManifold(worldManifold);
        Vector2f normal1Dir = new Vector2f(worldManifold.normal.x, worldManifold.normal.y);
        Vector2f normal2Dir = new Vector2f(normal1Dir).negate();

        for (Component c : obj1.getAllComponents()) {
            c.postSolve(obj2, contact, normal1Dir);

        }
        for (Component c : obj1.getAllComponents()) {
            c.postSolve(obj1, contact, normal2Dir);
        }
    }
}
