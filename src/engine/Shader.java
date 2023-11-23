package engine;

import static org.lwjgl.opengl.GL20C.GL_COMPILE_STATUS;
import static org.lwjgl.opengl.GL20C.GL_FRAGMENT_SHADER;
import static org.lwjgl.opengl.GL20C.GL_LINK_STATUS;
import static org.lwjgl.opengl.GL20C.GL_VERTEX_SHADER;
import static org.lwjgl.opengl.GL20C.glAttachShader;
import static org.lwjgl.opengl.GL20C.glCompileShader;
import static org.lwjgl.opengl.GL20C.glCreateProgram;
import static org.lwjgl.opengl.GL20C.glCreateShader;
import static org.lwjgl.opengl.GL20C.glGetProgramInfoLog;
import static org.lwjgl.opengl.GL20C.glGetProgrami;
import static org.lwjgl.opengl.GL20C.glGetShaderInfoLog;
import static org.lwjgl.opengl.GL20C.glGetShaderi;
import static org.lwjgl.opengl.GL20C.glLinkProgram;
import static org.lwjgl.opengl.GL20C.glShaderSource;
import static org.lwjgl.opengl.GL20C.glUseProgram;
import static org.lwjgl.opengl.GL20C.glValidateProgram;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Manages a OpenGL shader. 
 */
public final class Shader {
    private String vertexString;
    private String fragmentString;
    private int vertexHandle;
    private int fragmentHandle;
    private int program;

    /**
     * Seperates and compiles a shader.
     * 
     * @param path file path to a shader file
     */
    public Shader(String path) {
        program = glCreateProgram();

        parseShader(path);

        vertexHandle = glCreateShader(GL_VERTEX_SHADER);
        glShaderSource(vertexHandle, vertexString);
        glCompileShader(vertexHandle);
        if (glGetShaderi(vertexHandle, GL_COMPILE_STATUS) != 1) {
            System.err.println(glGetShaderInfoLog(vertexHandle));
            System.exit(1);
        }  

        // Fragment shader.
        fragmentHandle = glCreateShader(GL_FRAGMENT_SHADER);
        glShaderSource(fragmentHandle, fragmentString);
        glCompileShader(fragmentHandle);
        if (glGetShaderi(fragmentHandle, GL_COMPILE_STATUS) != 1) {
            System.err.println(glGetShaderInfoLog(fragmentHandle));
            System.exit(1);
        }

        glAttachShader(program, vertexHandle);
        glAttachShader(program, fragmentHandle);
        glLinkProgram(program);
        if (glGetProgrami(program, GL_LINK_STATUS) != 1) {
            System.err.println(glGetProgramInfoLog(program));
            System.exit(1);
        }
        glValidateProgram(program);
    }

    /** 
     * Seperates the single shader file into multiple strings.
     */
    private void parseShader(String path) {
        Path pathTest = Paths.get(path);
        if (Files.notExists(pathTest)) {
            throw new RuntimeException("Path does not exist.");
        }
        try {
            BufferedReader reader = new BufferedReader(new FileReader(path));
            String line = reader.readLine();
            
            // 0 shared, 1 vertex, 2 fragment.
            int mode = 0;
            String shared = "";
            String vertex = "";
            String fragment = "";
            while (line != null) {
                if (line.length() == 0) {
                    line = reader.readLine();
                }
                if (line.equals("#shared")) {
                    mode = 0;
                    line = reader.readLine();
                }
                if (line.equals("#vertex")) {
                    mode = 1;
                    line = reader.readLine();
                }
                if (line.equals("#fragment")) {
                    mode = 2;
                    line = reader.readLine();
                }
                if (mode == 0) {
                    shared += line + "\n";
                }
                if (mode == 1) {
                    vertex += line + "\n";
                }
                if (mode == 2) {
                    fragment += line + "\n";
                }
                line = reader.readLine();
            }
            vertexString = shared + vertex;
            fragmentString = shared + fragment;

            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Enables the shader.
     */
    public void use() {
        glUseProgram(program);
    }
}