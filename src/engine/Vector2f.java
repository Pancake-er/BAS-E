package engine;

/**
 * 2D vector math class.
 */
public final class Vector2f {
    private float x;
    private float y;

    public Vector2f(float x, float y) {
        this.x = x;
        this.y = y;
    }

    public Vector2f add(Vector2f otherVector2) {
        x += otherVector2.x;
        y += otherVector2.y;

        return this;
    }

    public Vector2f add(float x, float y) {
        this.x += x;
        this.y += y;

        return this;
    }

    public Vector2f subtract(Vector2f otherVector2) {
        x -= otherVector2.x;
        y -= otherVector2.y;

        return this;
    }

    public Vector2f subtract(float x, float y) {
        this.x -= x;
        this.y -= y;

        return this;
    }

    public Vector2f multiply(float scalar) {
        x *= scalar;
        y *= scalar;

        return this;
    }

    public float getX() {
        return x;
    }

    public float getY() {
        return y;
    }

    public void setX(float x) {
        this.x = x;
    }

    public void setY(float y) {
        this.y = y;
    }

    public void set(Vector2f otherVector2) {
        x = otherVector2.x;
        y = otherVector2.y;
    }

    public void set(float x, float y) {
        this.x = x;
        this.y = y;
    }

    public float magnitude() {
        return (float)Math.sqrt(Math.pow(x, 2.0) + Math.pow(y, 2.0));
    }
    
    public String toString() {
        return "(" + x + ", " + y + ")";
    }
}
