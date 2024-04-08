package components;

import Java2D.GameObject;
import Java2D.KeyListener;
import Java2D.Window;
import org.jbox2d.dynamics.contacts.Contact;
import org.joml.Vector2f;
import org.lwjgl.glfw.GLFW;
import physics2D.components.Rigidbody;

import static org.lwjgl.glfw.GLFW.*;

public class PlayerController extends Component{
    public float walkSpeed = 0.5f;
    public float jump = 0.5f;

    public float friction = 0.5f;

    public Vector2f tvelocity = new Vector2f(1f, 1.1f);

    public transient boolean onGround = false;
    private transient float jumpDelay = 0.0f;
    private transient float jumpDelayTime = 0.1f;

    private transient Rigidbody rb;


    private transient float playerRadius = 0.12f;
    private transient int jumpTime = 0;
    private transient Vector2f acceleration = new Vector2f();
    private transient Vector2f velocity = new Vector2f();
    private transient float lastYPos;

    private transient boolean isDead = false;

    @Override
    public void start() {
        isDead = false;
        this.rb = gameObject.getComponent(Rigidbody.class);
        this.rb.setGravityScale(0.02f);
    }

    @Override
    public void update(float dt) {
        if (KeyListener.isKeyPressed(GLFW.GLFW_KEY_RIGHT) || KeyListener.isKeyPressed(GLFW_KEY_D)) {
            this.acceleration.x = walkSpeed;

            if (this.velocity.x < 0) {
                this.velocity.x += friction;
            }
        } else if (KeyListener.isKeyPressed(GLFW_KEY_LEFT) || KeyListener.isKeyPressed(GLFW_KEY_A)) {
            this.acceleration.x = -walkSpeed;

            if (this.velocity.x > 0) {
                this.velocity.x -= friction;
            }
        } else {
            this.acceleration.x = 0;
            if (this.velocity.x > 0) {
                this.velocity.x = Math.max(0,this.velocity.x - friction);
            } else if (this.velocity.x < 0) {
                this.velocity.x = Math.min(0,this.velocity.x + friction);
            }
        }
        if (velocity.y == 0) {
            onGround = true;
        } else {
            onGround = false;
        }

         if ( (KeyListener.isKeyPressed(GLFW_KEY_UP) || KeyListener.isKeyPressed(GLFW_KEY_W) || KeyListener.isKeyPressed(GLFW_KEY_SPACE)) &&
                 ( jumpTime > 0 || (onGround ) || jumpDelay > 0 )) {
             if ((onGround || jumpDelay > 0) && jumpTime == 0) {
                 jumpTime = 14;
                 this.velocity.y = jump * 0.03f;
             } else if (jumpTime > 0) {
                 jumpTime--;
                 this.velocity.y = ((jumpTime / 10f) * jump);
             } else {
                 this.velocity.y = 0;
             }
             jumpDelay = 0;
             onGround = false;

        } else if (!onGround) {
             if (this.jumpTime > 0) {
                 this.velocity.y *= 0.35f;
                 this.jumpTime = 0;
             }
             jumpDelay -=dt;
             this.acceleration.y = Window.getPhysics().getGravity().y * 0.6f;
         }

         if (lastYPos == gameObject.transform.position.y && jumpDelay < 0){
             onGround = true;
             this.velocity.y = 0;
             jumpDelay= jumpDelayTime;
         }
        lastYPos = gameObject.transform.position.y;


        this.velocity.x += this.acceleration.x * dt;
        this.velocity.y += this.acceleration.y * dt;
        this.velocity.x = Math.max(Math.min(this.velocity.x, this.tvelocity.x), - tvelocity.x);
        this.velocity.y = Math.max(Math.min(this.velocity.y, this.tvelocity.y), -tvelocity.y);
        this.rb.setVelocity(this.velocity);
        this.rb.setAngularVelocity(0);
    }

    public void win() {
        isDead = true;
        System.out.println("You Won!!!!");
        Window.changeScene(0);
    }

    public boolean isDead() {
        return isDead;
    }

    public void defeat() {
        isDead = true;
        System.out.println("You Lost :(");
        Window.changeScene(0);
    }
    @Override
    public void beginCollision(GameObject collidingObj, Contact contact, Vector2f hitsNormal) {
        if (isDead) return;

        if (collidingObj.getComponent(Ground.class) != null) {
            if (Math.abs(hitsNormal.x) > 0.8f) {
                this.velocity.x = 0;
            } else if (hitsNormal.y > 0.8f) {
                this.velocity.y = 0;
                this.acceleration.y = 0;
                this.jumpTime = 0;

            }
        }
    }
}
