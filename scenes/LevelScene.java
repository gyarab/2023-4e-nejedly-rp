package scenes;

import Java2D.Camera;
import Java2D.GameObject;
import components.*;
import org.joml.Vector2f;
import util.AssetPool;

public class LevelScene extends Scene {
    public LevelScene() {

    }


    @Override
    public void render() {
        this.renderer.render();
    }
    Spritesheet sprites;
    GameObject levelComponents;

    @Override
    public void init() {
        loadResources();
        sprites = AssetPool.getSpritesheet("assets/images/Block_Spritesheet.png");
        levelComponents = this.createGameObject("Game camera");
        this.camera = new Camera(new Vector2f(-250, 0));
        levelComponents.addComponent(new GameCamera(this.camera));


        levelComponents.start();
        addGameObjectToScene(levelComponents);
    }

    @Override
    public void update(float dt) {
        this.camera.adjustProjection();
        this.physics2D.update(dt);

        for (int i = 0; i < gameObjects.size(); i++) {
            GameObject go = gameObjects.get(i);
            go.update(dt);
            physics2D.update(dt);

            if (go.isDead()) {
                gameObjects.remove(i);
                this.renderer.destroyGameObject(go);
                this.physics2D.destroyGameObject(go);
                i--;
            }
        }
    }

    @Override
    public void editUpdate(float dt) {

    }

    private void loadResources() {
        AssetPool.getShader("assets/shaders/default.glsl");

        AssetPool.addSpritesheet("assets/images/Block_Spritesheet.png", new Spritesheet(AssetPool.getTexture("assets/images/Block_Spritesheet.png"),
                16, 16, 61, 0));

        AssetPool.addSpritesheet("assets/images/CenterAndArrows.png", new Spritesheet(AssetPool.getTexture("assets/images/CenterAndArrows.png"),
                24, 48, 3, 0));
        for (GameObject go : gameObjects) {
            if (go.getComponent(SpriteRenderer.class) != null) {
                SpriteRenderer spr = go.getComponent(SpriteRenderer.class);
                if (spr.getTexture() != null) {
                    spr.setTexture(AssetPool.getTexture(spr.getTexture().getFilepath()));
                }
            }
        }
    }

    @Override
    public void imgui() {
        super.imgui();
    }
}
