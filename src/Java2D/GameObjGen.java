package Java2D;

import components.*;
import org.joml.Vector2f;
import physics2D.components.BoxCollider;
import physics2D.components.CircleCollider;
import physics2D.components.Rigidbody;
import physics2D.enums.BodyType;
import util.AssetPool;

public class GameObjGen {

    public static GameObject generateSpriteObject(Sprite sprite, float sizeX, float sizeY) {
        GameObject block = Window.getScene().createGameObject("Generated_Block");
        block.transform.scale.x = sizeX;
        block.transform.scale.y = sizeY;
        SpriteRenderer renderer = new SpriteRenderer();
        renderer.setSprite(sprite);
        block.addComponent(renderer);

        return block;
    }

    public static GameObject generatePlayer() {
        Spritesheet playerSprite = AssetPool.getSpritesheet("assets/images/player.png");
        GameObject player = generateSpriteObject(playerSprite.getSprite(0), 0.25f, 0.25f);
        player.setName("player");
        player.setUid(1);

        CircleCollider circleCollider = new CircleCollider();
        circleCollider.setRadius(0.12f);
        Rigidbody rigidbody = new Rigidbody();
        rigidbody.setBodyType(BodyType.Dynamic);
        rigidbody.setContinuousCollision(false);
        rigidbody.setFixedRotation(true);
        rigidbody.setMass(25.0f);

        player.addComponent(rigidbody);
        player.addComponent(circleCollider);
        player.addComponent(new PlayerController());
        return player;
    }

    public static GameObject generateVictoryPosition(Sprite sprite) {
        GameObject block = generateSpriteObject(sprite, 1, 1);

        block.addComponent(new Victory());
        Rigidbody rigidbody = new Rigidbody();
        rigidbody.setBodyType(BodyType.Dynamic);
        rigidbody.setFixedRotation(true);
        rigidbody.setContinuousCollision(false);
        block.addComponent(rigidbody);

        BoxCollider box =new BoxCollider();
        box.setHalfSize(new Vector2f(1, 1));
        block.addComponent(box);
        return block;
    }
    public static GameObject generateDefeatPosition(Sprite sprite) {
        GameObject block = generateSpriteObject(sprite, 1, 0.5f);

        block.addComponent(new Defeat());
        Rigidbody rigidbody = new Rigidbody();
        rigidbody.setBodyType(BodyType.Static);
        rigidbody.setFixedRotation(true);
        rigidbody.setContinuousCollision(false);
        block.addComponent(rigidbody);

        BoxCollider box =new BoxCollider();
        box.setHalfSize(new Vector2f(1, 0.5f));
        block.addComponent(box);
        return block;
    }
}
