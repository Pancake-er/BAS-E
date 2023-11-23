package engine;

import java.time.Instant;

/**
 * Holds/updates the state of an animation and draws it.
 */
public final class Animation {
    private float[] frameXYPositions;
    private float frameWidth;
    private float frameHeight;
    private int currentFrame;
    private float fps;
    private long lastEpoch = 0;
    private boolean pause = false;

    /**
     * Initializes the animation.
     * 
     * @param frameXYPositions the texture position in pixels. The array looks 
     *     like [x1, y1, x2, y2, etc.].
     * @param frameWidth texture width for all frames in pixels
     * @param frameHeight texture height for all frames in pixels
     * @param fps how long a frame should last in fps
     */
    public Animation(float[] frameXYPositions, float frameWidth, 
        float frameHeight, float fps) 
    {  
        this.frameXYPositions = frameXYPositions;
        this.frameWidth = frameWidth;
        this.frameHeight = frameHeight;
        this.fps = fps;
    }

    /**
     * Changes the frame if enough time has passed and adds a frame to the 
     * batch.
     * 
     * @param renderer renderer to add to
     * @param xPosition x position in pixels
     * @param yPosition y position in pixels
     * @param width width in pixels
     * @param height height in pixels
     * @param red red amount 0-1
     * @param green green amount 0-1
     * @param blue blue amount 0-1
     * @param alpha opacity 0-1
     */
    public void drawFrame(Renderer renderer, float xPosition, float yPosition, 
        float zPosition, float width, float height, float red, float green, 
        float blue, float alpha, TextureAtlas textureAtlas) 
    {   
        if (!pause) {
            long epoch = Instant.now().toEpochMilli();
            if (epoch - lastEpoch > 1000 / fps) {
                lastEpoch = epoch;
                currentFrame += 2;
                if (currentFrame == frameXYPositions.length) {
                    currentFrame = 0;
                }
            }
        }
        renderer.addQuad(xPosition, yPosition, zPosition, width, height, 
            frameXYPositions[currentFrame], frameXYPositions[currentFrame + 1], 
            frameWidth, frameHeight, red, green, blue, alpha, textureAtlas);
    }

    /**
     * Flips all the frames horizontally.
     */
    public void flipHorizontally() {
        for (int i = 0; i < frameXYPositions.length; i += 2) {
            frameXYPositions[i] = frameXYPositions[i] + frameWidth;
        }
        frameWidth = -frameWidth;
    }

    public int getCurrentFrame() {
        return currentFrame;
    }

    /**
     * Sets the frame pair.
     * 
     * @param frame frame number, not the frame x index
     */
    public void setFrame(int frame) {
        currentFrame = frame * 2;
    }

    /**
     * Pause frame updating.
     */
    public void setPause(boolean pause) {
        this.pause = pause;
    }

    public void setFps(float fps) {
        this.fps = fps;
    }

    public void setFramePositions(int frame, float xPosition, float yPosition) {
        frameXYPositions[frame] = xPosition;
        frameXYPositions[frame + 1] = yPosition;
    }
}
