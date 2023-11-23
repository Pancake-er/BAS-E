package engine;

import static org.lwjgl.opengl.GL11C.GL_FLOAT;
import static org.lwjgl.opengl.GL11C.GL_TRIANGLES;
import static org.lwjgl.opengl.GL11C.GL_UNSIGNED_INT;
import static org.lwjgl.opengl.GL11C.glDrawElements;
import static org.lwjgl.opengl.GL15C.GL_ARRAY_BUFFER;
import static org.lwjgl.opengl.GL15C.GL_DYNAMIC_DRAW;
import static org.lwjgl.opengl.GL15C.GL_ELEMENT_ARRAY_BUFFER;
import static org.lwjgl.opengl.GL15C.GL_STATIC_DRAW;
import static org.lwjgl.opengl.GL15C.glBindBuffer;
import static org.lwjgl.opengl.GL15C.glBufferData;
import static org.lwjgl.opengl.GL15C.glBufferSubData;
import static org.lwjgl.opengl.GL15C.glGenBuffers;
import static org.lwjgl.opengl.GL20C.glEnableVertexAttribArray;
import static org.lwjgl.opengl.GL20C.glUniformMatrix4fv;
import static org.lwjgl.opengl.GL20C.glVertexAttribPointer;
import static org.lwjgl.opengl.GL30C.glBindVertexArray;
import static org.lwjgl.opengl.GL30C.glGenVertexArrays;

import java.nio.ByteBuffer;
import java.nio.IntBuffer;

import org.lwjgl.system.MemoryUtil;

/**
 * A batch renderer.
 */
public final class Renderer {
    private int vertexId;
    private int indexId;
    private int vaoId;
    private final int MAX_QUAD_COUNT = 1000;
    private final int MAX_INDEX_COUNT = MAX_QUAD_COUNT * 6;
    private final int VERTEX_BYTE_SIZE = 4 * 9;
    private final int VERTICES_SIZE = (MAX_QUAD_COUNT * 4) * VERTEX_BYTE_SIZE;
    private ByteBuffer vertices = MemoryUtil.memCalloc(VERTICES_SIZE);
    private Shader shader;
    private Matrix4f matrix = new Matrix4f();
    private float windowWidth;
    private float windowHeight;

    /**
     * Initializes everything needed for the batch rendering. (Not GLFW stuff.)
     * 
     * @param windowWidth window width in pixels
     * @param windowHeight window height in pixels
     * @param shaderPath path to the shader file
     */
    public Renderer(float windowWidth, float windowHeight, String shaderPath) {
        makeAndUseVAO();

        shader = new Shader(shaderPath);
        shader.use();

        this.windowWidth = windowWidth;
        this.windowHeight = windowHeight;
        matrix.orthographic(0.0f, windowWidth, windowHeight, 0.0f, 0.0f, 10.0f);
        // The location must be the same as u_viewProjection' in the shader.
        glUniformMatrix4fv(0, false, matrix.toFloatBuffer());

        vertexId = glGenBuffers();
        glBindBuffer(GL_ARRAY_BUFFER, vertexId);
        glBufferData(GL_ARRAY_BUFFER, vertices, GL_DYNAMIC_DRAW);

        glEnableVertexAttribArray(0);
        glVertexAttribPointer(0, 3, GL_FLOAT, false, VERTEX_BYTE_SIZE, 0);
        glEnableVertexAttribArray(1);
        glVertexAttribPointer(1, 2, GL_FLOAT, false, VERTEX_BYTE_SIZE, 3 * 4);
        glEnableVertexAttribArray(2);
        glVertexAttribPointer(2, 4, GL_FLOAT, false, VERTEX_BYTE_SIZE, 5 * 4);

        IntBuffer indicesBuffer = MemoryUtil.memAllocInt(MAX_INDEX_COUNT);
        int indexOffset = 0;
        for (int i = 0; i < MAX_INDEX_COUNT; i += 6) {
            indicesBuffer.put(0 + indexOffset);
            indicesBuffer.put(1 + indexOffset);
            indicesBuffer.put(2 + indexOffset);
            indicesBuffer.put(2 + indexOffset);
            indicesBuffer.put(3 + indexOffset);
            indicesBuffer.put(0 + indexOffset);
            
            indexOffset += 4;
        }
        indicesBuffer.flip();

        indexId = glGenBuffers();
        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, indexId);
        glBufferData(GL_ELEMENT_ARRAY_BUFFER, indicesBuffer, GL_STATIC_DRAW);
        MemoryUtil.memFree(indicesBuffer);
        
        glBindBuffer(GL_ARRAY_BUFFER, 0);
        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, 0);
    }

    private void makeAndUseVAO() {
        vaoId = glGenVertexArrays();
        glBindVertexArray(vaoId);
    }

    private void addVertex(float xPosition, float yPosition, float zPosition, 
        float xTextureCoord, float yTextureCoord, float red, float green, 
        float blue, float alpha) 
    {
        vertices.putFloat(xPosition);
        vertices.putFloat(yPosition);
        vertices.putFloat(zPosition);
        vertices.putFloat(xTextureCoord);
        vertices.putFloat(yTextureCoord);
        vertices.putFloat(red);
        vertices.putFloat(green);
        vertices.putFloat(blue);
        vertices.putFloat(alpha);
    }

    /**
     * Adds a textured colored rectangle to the batch and flushes it if its 
     * full.
     * 
     * Coordinates and sizes are in pixels, (0, 0) at the top left corner of the
     * window, and increase down and to the right.
     * 
     * Color and opacity are between 0 and 1, with 0 being none of that 
     * color and 1 being all of it.
     * 
     * @param xPosition x position in pixels
     * @param yPosition y position in pixels
     * @param zPosition z position. Can be used for depth testing if it is 
     *     enabled, but transparency may not work as expected. Otherwise should 
     *     be set to 0.0f.
     * @param width width in pixels
     * @param height height in pixels
     * @param xTextureCoord x texture position in pixels
     * @param yTextureCoord y texture position in pixels
     * @param textureWidth texture width in pixels
     * @param textureHeight texture height in pixels
     * @param red red amount 0-1
     * @param green green amount 0-1
     * @param blue blue amount 0-1
     * @param alpha opacity 0-1
     * @param textureAtlas currently bound texture atlas
     */
    public void addQuad(float xPosition, float yPosition, float zPosition, 
        float width, float height, float xTextureCoord, float yTextureCoord, 
        float textureWidth, float textureHeight, float red, float green, 
        float blue, float alpha, TextureAtlas textureAtlas) 
    {
        float xTextureCoordRatio = xTextureCoord / textureAtlas.getWidth();
        float yTextureCoordRatio = yTextureCoord / textureAtlas.getHeight();
        float textureWidthRatio = textureWidth / textureAtlas.getWidth();
        float textureHeightRatio = textureHeight / textureAtlas.getHeight();

        addVertex(xPosition, yPosition, zPosition, xTextureCoordRatio, 
            yTextureCoordRatio, red, green, blue, alpha);
        addVertex(xPosition + width, yPosition, zPosition, xTextureCoordRatio 
            + textureWidthRatio, yTextureCoordRatio, red, green, blue, alpha);
        addVertex(xPosition + width, yPosition + height, zPosition, 
            xTextureCoordRatio + textureWidthRatio, yTextureCoordRatio 
            + textureHeightRatio, red, green, blue, alpha);
        addVertex(xPosition, yPosition + height, zPosition, xTextureCoordRatio,
            yTextureCoordRatio + textureHeightRatio, red, green, blue, alpha);
        
        if (vertices.position() == vertices.capacity()) {
            flush();
        }
    }

    /**
     * Draws the batch and resets buffers.
     */
    public void flush() {
        if (vertices.position() != 0) {
            glUniformMatrix4fv(0, false, matrix.toFloatBuffer());

            glBindBuffer(GL_ARRAY_BUFFER, vertexId);
            glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, indexId);
            
            vertices.flip();
            glBufferSubData(GL_ARRAY_BUFFER, 0, vertices);
            vertices.clear();

            glDrawElements(GL_TRIANGLES, MAX_INDEX_COUNT, GL_UNSIGNED_INT, 0);

            glBindBuffer(GL_ARRAY_BUFFER, 0);
            glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, 0);
        }
    }

    /**
     * Cleans up memory.
     */
    public void freeBufferMemory() {
        MemoryUtil.memFree(vertices);
    }

    public float getWindowWidth() {
        return windowWidth;
    }

    public float getWindowHeight() {
        return windowHeight;
    }

    /**
     * Sets the camera's center to the position in pixels given.
     * 
     * Camera position must be set before drawing anything to prevent 
     * bugs with moving the camera between flushes (Since for large 
     * batches, flushes can happen before the explicit flush call).
     * 
     * @param x x position in pixels
     * @param y y position in pixels
     * @param z z position in pixels
     */
    public void setCameraPosition(float x, float y, float z) {
        matrix.setPosition(x / (windowWidth / -2.0f), y / (windowHeight / 2.0f), 
            z);
    }

    /**
     * Updates window size fields used for camera calculations and does an 
     * orthographic projection to maintain accurate coordinates.
     * 
     * @param windowWidth window width in pixels
     * @param windowHeight window height in pixels
     */
    public void setWindowSize(float windowWidth, float windowHeight) {
        this.windowWidth = windowWidth;
        this.windowHeight = windowHeight;
        matrix.orthographic(0.0f, windowWidth, windowHeight, 0.0f, 0.0f, 10.0f);
    }
}