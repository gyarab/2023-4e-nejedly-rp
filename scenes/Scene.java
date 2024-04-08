package scenes;

import Java2D.*;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import components.Component;
import components.ComponentDeserializer;
import components.Transform;
import imgui.ImGui;
import imgui.flag.ImGuiWindowFlags;
import physics2D.Physics2D;
import rendering.Renderer;

import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/*
Pattern pro sceny
 */
public abstract class Scene {

    protected Renderer renderer = new Renderer();
    protected Physics2D physics2D = new Physics2D();
    protected Camera camera;
    private boolean isRunning = false;
    protected List<GameObject> gameObjects = new ArrayList<>();
    protected boolean levelLoaded = false;

    public Scene() {

    }

    public void init() {

    }
    
    public void start() {
        for (GameObject go :
                gameObjects) {
            go.start();
            this.renderer.add(go);
            this.physics2D.add(go);
        }
        isRunning = true;
    }

    public void destroy() {
        for (GameObject go :
                gameObjects) {
            go.destroy();
        }
    }
    
    public void addGameObjectToScene( GameObject go) {
        if (!isRunning) {
            gameObjects.add(go);
        } else {
            gameObjects.add(go);
            go.start();
            this.renderer.add(go);
            this.physics2D.add(go);
        }
    }

    public <T extends Component> GameObject getGameObject(Class<T> c) {
        for (GameObject go : gameObjects) {
            if (go.getComponent(c) != null) {
                return go;
            }
        }

        return null;
    }

    public GameObject getGameObject(int gameObjectId) {
        Optional<GameObject> result = gameObjects.stream().filter(gameObject -> gameObject.getUid() == gameObjectId).findFirst();
        return result.orElse(null);
    }


    public abstract void update(float dt);

    public abstract void editUpdate(float dt);

    public abstract void render();

    public Camera camera() {
        return this.camera;
    }

    private boolean isPlaying = false;

    public Physics2D getPhysics() {
        return this.physics2D;
    }

    public void imgui() {
        ImGui.begin("StartStop", ImGuiWindowFlags.MenuBar);
        ImGui.beginMenuBar();
        if (ImGui.menuItem("Play", "",false, (!isPlaying))) {
            isPlaying = true;
            if (isPlaying) {
                Window.getScene().save();
                Window.changeScene(1);
            }
        }
        if (ImGui.menuItem("Stop", "",true, !isPlaying)) {
            isPlaying = false;
            Window.changeScene(0);
        }
        ImGui.endMenuBar();
        ImGui.end();
    }

    public GameObject createGameObject(String name) {
        GameObject go = new GameObject(name);
        go.addComponent(new Transform());
        go.transform = go.getComponent(Transform.class);
        return go;
    }

    public void save(){
        Gson gson = new GsonBuilder().setPrettyPrinting().registerTypeAdapter(Component.class, new ComponentDeserializer()).registerTypeAdapter(GameObject.class, new GameObjectDeserializer()).create();

        try {
            FileWriter writer = new FileWriter("level1.txt");
            List<GameObject> objsToSerialize = new ArrayList<>();
            for (GameObject gameObject : this.gameObjects) {
                if (gameObject.isSerializing()) {
                    objsToSerialize.add(gameObject);
                }
            }
            writer.write(gson.toJson(objsToSerialize));
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void load() {
        Gson gson = new GsonBuilder().setPrettyPrinting().registerTypeAdapter(Component.class, new ComponentDeserializer()).registerTypeAdapter(GameObject.class, new GameObjectDeserializer()).create();

        String inFile = "";
        try {
            inFile = new String(Files.readAllBytes(Paths.get("level1.txt")));
        } catch (IOException e) {
            System.out.println("Creating new saveFile");
        }

        if (!inFile.equals("")) {
            int maxGoId = -1;
            int maxCompId = -1;
            GameObject[] objs = gson.fromJson(inFile, GameObject[].class);
            for (int i = 0; i < objs.length; i++){
                addGameObjectToScene(objs[i]);

                for (Component c : objs[i].getAllComponents()) {
                    if (c.getUid() > maxCompId) {
                        maxCompId = c.getUid();
                    }
                }
                if (objs[i].getUid() > maxGoId) {
                    maxGoId = objs[i].getUid();
                }
            }
            maxGoId++;
            maxCompId++;
            GameObject.init(maxGoId);
            Component.init(maxCompId);
            this.levelLoaded = true;
        }

    }

    public List<GameObject> getGameObjects() {
        return gameObjects;
    }
}
