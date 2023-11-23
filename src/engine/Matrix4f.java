package engine;

import static org.lwjgl.system.MemoryStack.stackPush;

import java.nio.FloatBuffer;

import org.lwjgl.system.MemoryStack;

/**
 * 4x4 matrix of floats. It is in column major order:
 *     m00  m10  m20  m30
 *     m01  m11  m21  m31
 *     m02  m12  m22  m32
 *     m03  m13  m23  m33
 */
public final class Matrix4f {
    private float m00;
    private float m01;
    private float m02;
    private float m03;
    private float m10;
    private float m11;
    private float m12;
    private float m13;
    private float m20;
    private float m21;
    private float m22;
    private float m23;
    private float m30;
    private float m31;
    private float m32;
    private float m33;
        
    public Matrix4f(float m00, float m01, float m02, float m03, float m10, 
        float m11, float m12, float m13, float m20, float m21, float m22, 
        float m23, float m30, float m31, float m32, float m33) 
    {
        this.m00 = m00;
        this.m01 = m01;
        this.m02 = m02;
        this.m03 = m03;
        this.m10 = m10;
        this.m11 = m11;
        this.m12 = m12;
        this.m13 = m13;
        this.m20 = m20;
        this.m21 = m21;
        this.m22 = m22;
        this.m23 = m23;
        this.m30 = m30;
        this.m31 = m31;
        this.m32 = m32;
        this.m33 = m33;
    }

    /**
     * Initializes a unit matrix.
     */
    public Matrix4f() {
        m00 = 1.0f;
        m01 = 0.0f;
        m02 = 0.0f;
        m03 = 0.0f;
        m10 = 0.0f;
        m11 = 1.0f;
        m12 = 0.0f;
        m13 = 0.0f;
        m20 = 0.0f;
        m21 = 0.0f;
        m22 = 1.0f;
        m23 = 0.0f;
        m30 = 0.0f;
        m31 = 0.0f;
        m32 = 0.0f;
        m33 = 1.0f;
    }

    public Matrix4f orthographic(float left, float right, float bottom, 
        float top, float near, float far) 
    {
        m00 = 2.0f / (right - left);
        m11 = 2.0f / (top - bottom);
        m22 = 2.0f / (far - near);
        m30 = -(right + left) / (right - left);
        m31 = -(top + bottom) / (top - bottom);
        m32 = -(far + near) / (far - near);
    
        return this;
    }

    public Matrix4f scale(float x, float y, float z) {
        m00 *= x;
        m11 *= y;
        m22 *= z;

        return this;
    }

    public Matrix4f scale(float scalar) {
        m00 *= scalar;
        m11 *= scalar;
        m22 *= scalar;

        return this;
    }

    public Matrix4f increaseScale(float x) {
        m00 += x;
        m11 += x;
        m22 += x;

        return this;
    }

    public Matrix4f setScale(float x, float y, float z) {
        m00 = x;
        m11 = y;
        m22 = z;

        return this;
    }

    public Matrix4f translate(float x, float y, float z) {
        m30 += x;
        m31 += y;
        m32 += z;

        return this;
    }

    public Matrix4f setPosition(float x, float y, float z) {
        m30 = x; 
        m31 = y; 
        m32 = z;

        return this;
    }

    public float getXPosition() {
        return m30;
    }

    public float getYPosition() {
        return m31;
    }

    public float getZPosition() {
        return m32;
    }

    public void set(Matrix4f otherMatrix) {
        this.m00 = otherMatrix.m00;
        this.m01 = otherMatrix.m01;
        this.m02 = otherMatrix.m02;
        this.m03 = otherMatrix.m03;
        this.m10 = otherMatrix.m10;
        this.m11 = otherMatrix.m11;
        this.m12 = otherMatrix.m12;
        this.m13 = otherMatrix.m13;
        this.m20 = otherMatrix.m20;
        this.m21 = otherMatrix.m21;
        this.m22 = otherMatrix.m22;
        this.m23 = otherMatrix.m23;
        this.m30 = otherMatrix.m30;
        this.m31 = otherMatrix.m31;
        this.m32 = otherMatrix.m32;
        this.m33 = otherMatrix.m33;
    }

    /**
     * Returns matrix in column major order.
     */
    public float[] toFloatArray() {
        return new float[] {
            m00,  m01,  m02,  m03,
            m10,  m11,  m12,  m13,
            m20,  m21,  m22,  m23,
            m30,  m31,  m32,  m33,
        };
    }

    public FloatBuffer toFloatBuffer() {
        try (MemoryStack stack = stackPush()) {
            FloatBuffer buffer = stack.mallocFloat(4 * 4);
            
            buffer.put(m00);
            buffer.put(m01);
            buffer.put(m02);
            buffer.put(m03);
            buffer.put(m10);
            buffer.put(m11);
            buffer.put(m12);
            buffer.put(m13);
            buffer.put(m20);
            buffer.put(m21);
            buffer.put(m22);
            buffer.put(m23);
            buffer.put(m30);
            buffer.put(m31);
            buffer.put(m32);
            buffer.put(m33);

            buffer.flip();

            return buffer;
        }
    }

    public String toString() {
        return m00 + ", " + m10 + ", " + m20 + ", " + m30 + ",\n" 
            + m01 + ", " + m11 + ", " + m21 + ", " + m31 + ",\n"
            + m02 + ", " + m12 + ", " + m22 + ", " + m32 + ",\n"
            + m03 + ", " + m13 + ", " + m23 + ", " + m33 + ",";
    }
}