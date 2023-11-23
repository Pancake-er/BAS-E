package main;

import static org.lwjgl.glfw.Callbacks.glfwFreeCallbacks;
import static org.lwjgl.glfw.GLFW.GLFW_FALSE;
import static org.lwjgl.glfw.GLFW.GLFW_RESIZABLE;
import static org.lwjgl.glfw.GLFW.GLFW_TRUE;
import static org.lwjgl.glfw.GLFW.GLFW_VISIBLE;
import static org.lwjgl.glfw.GLFW.glfwCreateWindow;
import static org.lwjgl.glfw.GLFW.glfwDefaultWindowHints;
import static org.lwjgl.glfw.GLFW.glfwDestroyWindow;
import static org.lwjgl.glfw.GLFW.glfwInit;
import static org.lwjgl.glfw.GLFW.glfwMakeContextCurrent;
import static org.lwjgl.glfw.GLFW.glfwPollEvents;
import static org.lwjgl.glfw.GLFW.glfwSetFramebufferSizeCallback;
import static org.lwjgl.glfw.GLFW.glfwShowWindow;
import static org.lwjgl.glfw.GLFW.glfwSwapBuffers;
import static org.lwjgl.glfw.GLFW.glfwSwapInterval;
import static org.lwjgl.glfw.GLFW.glfwTerminate;
import static org.lwjgl.glfw.GLFW.glfwWindowHint;
import static org.lwjgl.glfw.GLFW.glfwWindowShouldClose;
import static org.lwjgl.opengl.GL11C.GL_BLEND;
import static org.lwjgl.opengl.GL11C.GL_COLOR_BUFFER_BIT;
import static org.lwjgl.opengl.GL11C.GL_DEPTH_BUFFER_BIT;
import static org.lwjgl.opengl.GL11C.GL_ONE_MINUS_SRC_ALPHA;
import static org.lwjgl.opengl.GL11C.GL_SRC_ALPHA;
import static org.lwjgl.opengl.GL11C.glBlendFunc;
import static org.lwjgl.opengl.GL11C.glClear;
import static org.lwjgl.opengl.GL11C.glClearColor;
import static org.lwjgl.opengl.GL11C.glEnable;
import static org.lwjgl.opengl.GL11C.glViewport;
import static org.lwjgl.system.MemoryUtil.NULL;

import org.lwjgl.glfw.GLFWFramebufferSizeCallback;
import org.lwjgl.opengl.GL;

import engine.Animation;
import engine.GameUtils;
import engine.Renderer;
import engine.TextureAtlas;

public final class Main {
    private long window;
    private Renderer renderer;
    private TextureAtlas textureAtlas;
    private Animation exampleAnimation;

    Main() {
        setup();
        gameLoop();
        cleanup();
    }

    private void setup() {
        if (!glfwInit()) {
            throw new RuntimeException("Unable to initialize GLFW");
        }

        // Configures GLFW.
        glfwDefaultWindowHints();
        glfwWindowHint(GLFW_VISIBLE, GLFW_FALSE);
        glfwWindowHint(GLFW_RESIZABLE, GLFW_TRUE);

        window = glfwCreateWindow(1200, 800, "BAS-E Example", NULL, NULL);
        if (window == NULL) {
            throw new RuntimeException("Failed to create the GLFW window");
        }

        // Specifies which window we're working with.                                                                 
        glfwMakeContextCurrent(window);

        // Enables v-sync.
        glfwSwapInterval(1);

        glfwShowWindow(window);

        GL.createCapabilities();

        glClearColor(0f, 0f, 0f, 0f);

        // Needed for textures to have transparency.
        glEnable(GL_BLEND);
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);

        renderer = new Renderer(GameUtils.getWindowWidth(window),
            GameUtils.getWindowHeight(window), "./shaders/shader.glsl");

        // Updates viewport and renderer when GLFW window size changes.
        glfwSetFramebufferSizeCallback(window, new GLFWFramebufferSizeCallback() 
        {
            @Override
            public void invoke(long window, int width, int height) {
                glViewport(0, 0, width, height);
                renderer.setWindowSize(width, height);
            }
        });

        textureAtlas = new TextureAtlas("./res/atlas.png");
        textureAtlas.bind();
        exampleAnimation = new Animation(new float[] {16.0f, 128.0f, 24.0f, 
            128.0f}, 8.0f, 8.0f, 2.0f);
    }

    private void gameLoop() {
        while (!glfwWindowShouldClose(window)) {
            glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
            glClearColor(0.529f, 0.808f, 0.922f, 1.0f);
            
            exampleAnimation.drawFrame(renderer, 0.0f, 0.0f, 0.0f, 128.0f, 
                128.0f, 1.0f, 1.0f, 1.0f, 1.0f, textureAtlas);
            renderer.flush();

            glfwSwapBuffers(window);

            glfwPollEvents();
        }
    }

    private void cleanup() {
        renderer.freeBufferMemory();
        glfwFreeCallbacks(window);
        glfwDestroyWindow(window);
        glfwTerminate();
    }
    
    public static void main(String[] args) {
        new Main();
    }
}