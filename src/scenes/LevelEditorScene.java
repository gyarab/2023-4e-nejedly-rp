package scenes;

import Java2D.*;
import components.*;
import imgui.ImGui;
import imgui.ImVec2;
import org.joml.Vector2f;
import physics2D.components.BoxCollider;
import physics2D.components.CircleCollider;
import physics2D.components.Rigidbody;
import physics2D.enums.BodyType;
import util.AssetPool;

import static org.lwjgl.opengl.GL30.glGenVertexArrays;

public class LevelEditorScene extends Scene {

    Spritesheet sprites;
    protected GameObject activeGameObject = null;

    public GameObject levelEditorStuff = this.createGameObject("LeverEditor");
    public LevelEditorScene() {

    }

    @Override
    public void init() {
        loadResources();

        sprites = AssetPool.getSpritesheet("assets/images/Block_Spritesheet.png");
        Spritesheet pivotSpritesheet = AssetPool.getSpritesheet("assets/images/CenterAndArrows.png");

        this.camera = new Camera(new Vector2f(-250, 0));
        levelEditorStuff.addComponent(new MouseControls());
        levelEditorStuff.addComponent(new KeyControls());
        levelEditorStuff.addComponent(new GridLines());
        levelEditorStuff.addComponent(new EditorCamera(this.camera));
        levelEditorStuff.addComponent(new PivotSystem(pivotSpritesheet));

        levelEditorStuff.start();
    }

    @Override
    public void update(float dt) {

    }

    private void loadResources() {
        AssetPool.getShader("assets/shaders/default.glsl");

        AssetPool.addSpritesheet("assets/images/Block_Spritesheet.png", new Spritesheet(AssetPool.getTexture("assets/images/Block_Spritesheet.png"),
                16, 16, 22, 0));
        AssetPool.addSpritesheet("assets/images/player.png", new Spritesheet(AssetPool.getTexture("assets/images/player.png"),
                16, 16, 1, 0));

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
    public void editUpdate(float dt) {
        levelEditorStuff.editUpdate(dt);
        this.camera.adjustProjection();
        activeGameObject = levelEditorStuff.getComponent(MouseControls.class).getActiveObject();

        for (int i = 0; i < gameObjects.size(); i++) {
            GameObject go = gameObjects.get(i);
            go.editUpdate(dt);

            if (go.isDead()) {
                gameObjects.remove(i);
                this.renderer.destroyGameObject(go);
                this.physics2D.destroyGameObject(go);
                i--;
            }
        }

    }

    @Override
    public void render() {
        this.renderer.render();
    }

    public GameObject getActiveGameObject() {
        return activeGameObject;
    }

    public void setActiveGameObject(GameObject activeGameObject) {
        this.activeGameObject = activeGameObject;
    }


    @Override
    public void imgui() {
        super.imgui();

        ImGui.begin("Lever Editor Stuff");
        levelEditorStuff.imgui();
        ImGui.end();

        ImGui.begin("Objects");

        if (ImGui.beginTabBar("WindowTabBar")) {
            if (ImGui.beginTabItem("BuildingMaterials")) {
                ImVec2 windowPos = new ImVec2();
                ImGui.getWindowPos(windowPos);
                ImVec2 windowSize = new ImVec2();
                ImGui.getWindowSize(windowSize);
                ImVec2 itemSpacing = new ImVec2();
                ImGui.getStyle().getItemSpacing(itemSpacing);

                float windowX2 = windowPos.x + windowSize.x;
                for (int i = 0; i < sprites.size(); i++) {
                    Sprite sprite = sprites.getSprite(i);
                    float spriteWidth = sprite.getWidth() * 2;
                    float spriteHeight = sprite.getHeight() * 2;
                    int id = sprite.getTexId();
                    Vector2f[] texCoords = sprite.getTexCoords();

                    ImGui.pushID(i);
                    if (ImGui.imageButton(id, spriteWidth, spriteHeight, texCoords[2].x, texCoords[0].y, texCoords[0].x, texCoords[2].y)) {

                        GameObject object = GameObjGen.generateSpriteObject(sprite, 0.25f, 0.25f);
                        if (i >=1 && i != 2 && i < 11) {
                            Rigidbody rigidbody = new Rigidbody();
                            rigidbody.setBodyType(BodyType.Static);
                            object.addComponent(rigidbody);
                            BoxCollider boxCollider = new BoxCollider();
                            boxCollider.setHalfSize(new Vector2f(0.25f, 0.25f));
                            object.addComponent(boxCollider);
                            object.addComponent(new Ground());
                        } else if (i == 0){
                            object = GameObjGen.generateVictoryPosition(sprite);
                        } else if (i == 2) {
                            object = GameObjGen.generateDefeatPosition(sprite);
                        }
                            levelEditorStuff.getComponent(MouseControls.class).pickupObject(object);
                    }
                    ImGui.popID();

                    ImVec2 lastButtonPos = new ImVec2();
                    ImGui.getItemRectMax(lastButtonPos);
                    float lastButtonX2 = lastButtonPos.x;
                    float nextButtonX2 = lastButtonX2 + itemSpacing.x + spriteWidth;
                    if (i + 1 < sprites.size() && nextButtonX2 < windowX2) {
                        ImGui.sameLine();
                    }
                }
                ImGui.endTabItem();
            }

            if (ImGui.beginTabItem("Player")) {
                Spritesheet playerSprite = AssetPool.getSpritesheet("assets/images/player.png");
                Sprite sprite = playerSprite.getSprite(0);
                float spriteWidth = sprite.getWidth() * 2;
                float spriteHeight = sprite.getHeight() * 2;
                int id = sprite.getTexId();
                Vector2f[] texCoords = sprite.getTexCoords();
                ImGui.pushID("player");
                if (ImGui.imageButton(id, spriteWidth, spriteHeight, texCoords[2].x, texCoords[0].y, texCoords[0].x, texCoords[2].y)) {
                    GameObject object = GameObjGen.generatePlayer();
                    levelEditorStuff.getComponent(MouseControls.class).pickupObject(object);
                }
                ImGui.sameLine();
                ImGui.popID();
                ImGui.endTabItem();
            }
            ImGui.endTabBar();
        }

        ImGui.end();

        if (activeGameObject != null) {
            ImGui.begin("Properties");

            if (ImGui.beginPopupContextWindow("AddComponent")) {
                if (ImGui.menuItem("Add Rigidbody")) {
                    if (activeGameObject.getComponent(Rigidbody.class) == null) {
                        activeGameObject.addComponent(new Rigidbody());
                    }
                }

                if (ImGui.menuItem("Add Box Collider")) {
                    if (activeGameObject.getComponent(BoxCollider.class) == null && activeGameObject.getComponent(CircleCollider.class) == null) {
                        activeGameObject.addComponent(new BoxCollider());
                    }
                }
                if (ImGui.menuItem("Add Circle Collider")) {
                    if (activeGameObject.getComponent(CircleCollider.class) == null && activeGameObject.getComponent(BoxCollider.class) == null) {
                        activeGameObject.addComponent(new CircleCollider());
                    }
                }

                ImGui.endPopup();
            }
            activeGameObject.imgui();
            ImGui.end();
        }
    }
}
