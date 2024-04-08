package Java2D;

import org.joml.Vector4f;
import org.lwjgl.Version;
import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.glfw.GLFWVidMode;
import org.lwjgl.opengl.GL;
import org.lwjgl.system.MemoryStack;
import physics2D.Physics2D;
import rendering.*;
import scenes.LevelEditorScene;
import scenes.LevelScene;
import scenes.Scene;
import util.AssetPool;

import java.nio.IntBuffer;

import static org.lwjgl.glfw.Callbacks.glfwFreeCallbacks;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL11.GL_DEPTH_BUFFER_BIT;
import static org.lwjgl.system.MemoryStack.stackPush;
import static org.lwjgl.system.MemoryUtil.NULL;

public class Window {
    //Singleton
    private int width;
    private int height;
    private String title;
    private long glfwWindow;
    private ImGuiIntegration imGuiIntegration;

    public float r, g, b, a;

    private static Window window = null;
    private static Scene currentScene;
    private static boolean isEditor = true;

    private Window() {
        this.width = 1920;
        this.height = 1080;
        this.title = "2D Game";
        this.r = 1;
        this.g = 1;
        this.b = 1;
        this.a = 1;
    }
    // Prepinani scen reseno jako v projektu z druheho rocniku
    // Pouzije se scena na zaklade cisla
    public static void changeScene(int newScene) {
        if (currentScene != null) {

            currentScene.destroy();

        }
        switch (newScene) {
            case 0:
                isEditor = true;
                currentScene = new LevelEditorScene();

                break;
            case 1:
                isEditor = false;
                currentScene = new LevelScene();
                break;
            default: assert false : "Unknown scene'" + newScene + "'";
        }

        currentScene.load();
        currentScene.init();
        currentScene.start();
    }

    public static Physics2D getPhysics() {
        return currentScene.getPhysics();
    }
    public static Window getInstance() {
        if (Window.window == null) {
            Window.window = new Window();
        }

        return Window.window;
    }

    public static Scene getScene() {
        return currentScene;
    }

    /*
    Zapnuti programu
     */
    public void run() {
        System.out.println("Hello LWJGL " + Version.getVersion() + "!"); // ujisteni se ze LWJGL funguje

        init();
        loop();

        // Free the window callbacks and destroy the window
        glfwFreeCallbacks(glfwWindow);
        glfwDestroyWindow(glfwWindow);

        // Terminate GLFW and free the error callback
        glfwTerminate();
        glfwSetErrorCallback(null).free();
    }
/*
Inicializace GLFW a vytvoreni okna
 */
    private void init() {
        // Setup an error callback. The default implementation
        // will print the error message in System.err.
        // Kam bude GLFW vyhazovat errory, muze byt volano pred inicializaci
        GLFWErrorCallback.createPrint(System.err).set();

        // Initialize GLFW. Most GLFW functions will not work before doing this., Inicializace GLFW
        if ( !glfwInit() )
            throw new IllegalStateException("Unable to initialize GLFW");

        // Configure GLFW, zakladni nastaveni parametru pro vytovreni okna
        glfwDefaultWindowHints(); // optional, the current window hints are already the default
        glfwWindowHint(GLFW_VISIBLE, GLFW_FALSE); // the window will stay hidden after creation
        glfwWindowHint(GLFW_RESIZABLE, GLFW_TRUE); // the window will be resizable
        glfwWindowHint(GLFW_MAXIMIZED, GLFW_FALSE); // the window will not be maximized - po prechodu na 2K monitor zacalo delat problemy

        // Create the window
        glfwWindow = glfwCreateWindow(this.width, this.height, this.title, NULL, NULL);
        if ( glfwWindow == NULL )
            throw new RuntimeException("Failed to create the GLFW window");

        // misto vypisovani pres lambda expression v ukazce kodu, kterou jsem vyuzil, pouziji method reference za pouziti metod z dokumentace GLFW(odkaz na dokumentaci u metod callbacku)
        glfwSetCursorPosCallback(glfwWindow, MouseListener::mousePosCallback);
        glfwSetMouseButtonCallback(glfwWindow, MouseListener::mouseButtonCallback);
        glfwSetScrollCallback(glfwWindow, MouseListener::mouseScrollCallback);
        glfwSetKeyCallback(glfwWindow, KeyListener::keyCallback);
        glfwSetWindowSizeCallback(glfwWindow, (w, newWidth, newHeight) ->{
            Window.setWidth(newWidth);
            Window.setHeight(newHeight);
        });
        // Setup a key callback. It will be called every time a key is pressed, repeated or released. Zmackni a uvloni Escape pro vypnuti
        // ODEBRAT!!!!
        // glfwSetKeyCallback(glfwWindow, (window, key, scancode, action, mods) -> {
        //     if ( key == GLFW_KEY_ESCAPE && action == GLFW_RELEASE )
        //         glfwSetWindowShouldClose(window, true); // We will detect this in the rendering loop
        // });

        // Get the thread stack and push a new frame, vycentrovani
        try ( MemoryStack stack = stackPush() ) {
            IntBuffer pWidth = stack.mallocInt(1); // int*
            IntBuffer pHeight = stack.mallocInt(1); // int*

            // Get the window size passed to glfwCreateWindow
            glfwGetWindowSize(glfwWindow, pWidth, pHeight);

            // Get the resolution of the primary monitor
            GLFWVidMode vidmode = glfwGetVideoMode(glfwGetPrimaryMonitor());

            // Center the window
            glfwSetWindowPos(
                    glfwWindow,
                    (vidmode.width() - pWidth.get(0)) / 2,
                    (vidmode.height() - pHeight.get(0)) / 2
            );
        } // the stack frame is popped automatically

        // Make the OpenGL context current
        glfwMakeContextCurrent(glfwWindow);

        // Enable v-sync
        glfwSwapInterval(1);
        // Make the window visible
        glfwShowWindow(glfwWindow);
        // This line is critical for LWJGL's interoperation with GLFW's
        // OpenGL context, or any context that is managed externally.
        // LWJGL detects the context that is current in the current thread,
        // creates the GLCapabilities instance and makes the OpenGL
        // bindings available for use.
        GL.createCapabilities(); // nutne presunout z originalniho kodu do init pro spravnou funkcnost...3 hodiny hledani chyby :/

        glEnable(GL_BLEND);
        glBlendFunc(GL_ONE, GL_ONE_MINUS_SRC_ALPHA);
        this.imGuiIntegration = new ImGuiIntegration(glfwWindow);
        this.imGuiIntegration.initImGui();

        glViewport(0,0, 1920, 1080);
        Window.changeScene(0);
    }
/*
Hlavni smycka celeho programu
 */
    private void loop() {
        float beginTime = (float) glfwGetTime();
        float endTime;
        float dt = -1.0f;


        Shader defaultShader = AssetPool.getShader("assets/shaders/default.glsl");
        // Set the clear color


        // Run the rendering loop until the user has attempted to close
        // the window or has pressed the ESCAPE key.

        while ( !glfwWindowShouldClose(glfwWindow) ) {

        //     glDisable(GL_BLEND);
        //    texturePicking.enableWriting();


        //    glClearColor(0.0f, 0.0f, 0.0f, 0.0f);
        //    glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
        //
        //    Renderer.bindShader(pickingShader);
        //    currentScene.render();

        //    if (MouseListener.mouseButtonDown(GLFW_MOUSE_BUTTON_LEFT)) {
        //        int x = (int) MouseListener.getScreenX();
        //        int y = (int) MouseListener.getScreenY();
        //        System.out.println(texturePicking.readPixel(x,y));
        //        System.out.println(MouseListener.getScreenX() + ", " + MouseListener.getScreenY());
        //    }

        //    texturePicking.disableWriting();
            glEnable(GL_BLEND);

            Draw.beginFrame();
            Vector4f clearColor = currentScene.camera().clearColor;
            glClearColor(clearColor.x, clearColor.y, clearColor.z, clearColor.w);
            glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT); // clear the framebuffer

            if (dt >= 0) {
                Renderer.bindShader(defaultShader);
                if (!isEditor) {
                    currentScene.update(dt);
                } else currentScene.editUpdate(dt);
                currentScene.render();
                Draw.draw();
            }

            this.imGuiIntegration.update(dt, currentScene);
            glfwSwapBuffers(glfwWindow); // swap the color buffers
            MouseListener.endFrame();


            // Poll for window events. The key callback above will only be
            // invoked during this call.
            glfwPollEvents();

            endTime = (float) glfwGetTime();
            dt = endTime - beginTime; // delta time jedne smycky
            beginTime = endTime;
        }

        currentScene.save();
    }

    public static int getWidth() {
        return getInstance().width;
    }

    public static int getHeight() {
        return getInstance().height;
    }

    public static void setWidth(int width) {
        getInstance().width = width;
    }

    public static void setHeight(int height) {
        getInstance().height = height;
    }
}
