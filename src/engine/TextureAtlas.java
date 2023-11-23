package engine;

import static org.lwjgl.opengl.GL11C.GL_NEAREST;
import static org.lwjgl.opengl.GL11C.GL_RGBA;
import static org.lwjgl.opengl.GL11C.GL_TEXTURE_2D;
import static org.lwjgl.opengl.GL11C.GL_TEXTURE_MAG_FILTER;
import static org.lwjgl.opengl.GL11C.GL_TEXTURE_MIN_FILTER;
import static org.lwjgl.opengl.GL11C.GL_UNSIGNED_BYTE;
import static org.lwjgl.opengl.GL11C.glBindTexture;
import static org.lwjgl.opengl.GL11C.glGenTextures;
import static org.lwjgl.opengl.GL11C.glTexImage2D;
import static org.lwjgl.opengl.GL11C.glTexParameterf;
import static org.lwjgl.opengl.GL13C.GL_TEXTURE0;
import static org.lwjgl.opengl.GL13C.glActiveTexture;
import static org.lwjgl.opengl.GL20C.glUniform1i;
import static org.lwjgl.system.MemoryStack.stackPush;

import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.lwjgl.stb.STBImage;
import org.lwjgl.system.MemoryStack;

/**
 * Creates an OpenGL texture.
 */
public final class TextureAtlas {
    private IntBuffer widthBuffer;
    private IntBuffer heightBuffer;
    private int width;
    private int height;
    private IntBuffer channels;
    private ByteBuffer data;
    private int id;

    /**
     * Creates a new texture using an image.
     * 
     * @param path source image path
     */
    public TextureAtlas(String path) {
        Path pathTest = Paths.get(path);
        if (Files.notExists(pathTest)) {
            throw new RuntimeException("Path does not exist.");
        }
        try (MemoryStack stack = stackPush()) {
            widthBuffer = stack.mallocInt(1);
            heightBuffer = stack.mallocInt(1);
            channels = stack.mallocInt(1);

            // Decodes an image into a byte buffer.
            data = STBImage.stbi_load(path, widthBuffer, heightBuffer, channels,
                4);
            if(data == null) {
                throw new RuntimeException(STBImage.stbi_failure_reason());
            }

            // Creates and binds new OpenGL texture.
            id = glGenTextures();

            glActiveTexture(GL_TEXTURE0);
            // The location must be the same as u_textureAtlas' in the shader.
            glUniform1i(1, 0);

            glBindTexture(GL_TEXTURE_2D, id);

            glTexParameterf(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST);
            glTexParameterf(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST);

            width = widthBuffer.get();
            height = heightBuffer.get();

            glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA, width, height, 0, GL_RGBA,
                GL_UNSIGNED_BYTE, data);

            STBImage.stbi_image_free(data);

            glBindTexture(GL_TEXTURE_2D, 0);
        }
    }

    /**
     * Binds this texture for the current batch.
     */
    public void bind() {
        glBindTexture(GL_TEXTURE_2D, id);
    }

    public int getId() {
        return id;
    }

    public int getWidth() {
        return width;
    }
    
    public int getHeight() {
        return height;
    }
}