package engine;

import java.util.Random;

/**
 * Provides methods for noise.
 */
public final class Noise {
    private final int POINTS_COUNT = 1024;
    private float[] points = new float[POINTS_COUNT];
    private int seed;

    /**
     * Generates a noise wave of values between 0.0f and 1.0f as an array.
     * 
     * @param seed
     */
    public Noise(int seed) {
        this.seed = seed;
        Random generator = new Random(seed);
        for (int i = 0; i < points.length; i++) {
            points[i] = generator.nextFloat();
        }
    }

    /**
     * Cosine interpolates y value from the noise array. To get a frequency and 
     * amplitude, use as: consineInterpolate(x / frequency) * amplitude.
     * 
     * @param x x position
     * @return y value of x. Between 0 and 1.
     */
    public float cosineInterpolate(float x) {
        int aIndex = (int)x;
        int bIndex = (int)x + 1;
        // Loop around;
        if (bIndex == POINTS_COUNT - 1) {
            aIndex = aIndex % POINTS_COUNT;
            bIndex = bIndex % POINTS_COUNT;
        }
        float xPosition = x - aIndex;
        float ft = xPosition * (float)Math.PI;
        float f = (1.0f - (float)Math.cos(ft)) * 0.5f;
        return points[aIndex] * (1.0f - f) + points[bIndex] * f;
    }

    public String toString() {
        String string = "[";
        for (int i = 0; i < points.length; i++) {
            string += points[i] + ", ";
        }
        string += "]";
        return string;
    }

    public int getSeed() {
        return seed;
    }
}
