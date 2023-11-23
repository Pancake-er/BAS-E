package engine;

/**
 * Physics simulator.
 */
public final class PhysicsEntity {
    private Vector2f position;
    private Vector2f velocity = new Vector2f(0.0f, 0.0f);
    private Vector2f acceleration = new Vector2f(0.0f, 9.81f);;
    private Vector2f size;
    private long lastEpoch = System.nanoTime();
    private float deltaTime = 0.0f;
    private final float PIXELS_PER_METER = 64.0f;
    // Wether to do movement or not. Still collidable to others.
    private boolean doKinematics;
    private boolean collidable;
    private static int nextID;
    // Used to prevent collisions with yourself.
    private int ID;
    // Used to prevent jitter against surfaces.
    private boolean collidingTop = false;
    private boolean collidingRight = false;
    private boolean collidingBottom = false;
    private boolean collidingLeft = false;

    /**
     * Initializes an entity. One meter is equivalent to whatever value 
     * PIXELS_PER_METER is set to.
     * 
     * @param xPosition x position
     * @param yPosition y position
     * @param width width of the collider
     * @param height height of the collider
     * @param doKinematics wether the entity should be able to move
     * @param collidable wether the entity should be hittable or pass-able 
     *     through
     */
    public PhysicsEntity(float xPosition, float yPosition, float width, 
        float height, boolean doKinematics, boolean collidable) 
    {
        position = new Vector2f(xPosition, yPosition);
        size = new Vector2f(width, height);
        this.doKinematics = doKinematics;
        this.collidable = collidable;
        ID = nextID;
        nextID++;
    }

    private boolean checkOverlapping(Vector2f position, Vector2f size) {
        return this.position.getX() <= position.getX() + size.getX()
            && this.position.getX() + this.size.getX() >= position.getX() 
            && this.position.getY() <= position.getY() + size.getY()
            && this.position.getY() + this.size.getY() >= position.getY();
    }

    private void resolveCollisions(PhysicsEntity[] physicsEntities) {
        collidingTop = false;
        collidingRight = false;
        collidingBottom = false;
        collidingLeft = false;
        for (int i = 0; i < physicsEntities.length; i++) {
            if (!physicsEntities[i].collidable || ID == physicsEntities[i].ID) {
                continue;
            }
            if (checkOverlapping(physicsEntities[i].position, 
                physicsEntities[i].size))
            {
                Vector2f center = new Vector2f(position.getX() + size.getX() 
                    / 2.0f, position.getY() + size.getY() / 2.0f);

                Vector2f otherCenter = new Vector2f(
                    physicsEntities[i].position.getX() 
                    + physicsEntities[i].size.getX() / 2.0f, 
                    physicsEntities[i].position.getY() 
                    + physicsEntities[i].size.getY() / 2.0f);

                Vector2f depth = new Vector2f(size.getX() / 2.0f 
                    + physicsEntities[i].size.getX() / 2.0f 
                    - Math.abs(center.getX() - otherCenter.getX()), size.getY() 
                    / 2.0f + physicsEntities[i].size.getY() / 2.0f 
                    - Math.abs(center.getY() - otherCenter.getY()));

                if (depth.getX() < depth.getY()) {
                    if (position.getX() < physicsEntities[i].position.getX()) {
                        position.subtract(depth.getX(), 0.0f);
                        collidingRight = true;
                    }
                    else {
                        position.add(depth.getX(), 0.0f);
                        collidingLeft = true;
                    }
                }
                else if (depth.getX() > depth.getY()) {
                    if (position.getY() < physicsEntities[i].position.getY()) {
                        position.subtract(0.0f, depth.getY());
                        collidingBottom = true;
                    }
                    else {
                        position.add(0.0f, depth.getY());
                        collidingTop = true;
                    }
                }
            }
        }
    }

    /**
     * Updates position based on velocity and acceleration on a fixed time step 
     * and resolves collisions if this is collidable.
     * 
     * @param physicsEntities entities to check against. If itself is included 
     *     it is ignored.
     */
    public void update(PhysicsEntity[] physicsEntities) {
        if (doKinematics) {
            long currentEpoch = System.nanoTime();
            deltaTime = (currentEpoch - lastEpoch) / 1000000000.0f;
            lastEpoch = currentEpoch;

            velocity.add(acceleration.getX() * deltaTime, 
                acceleration.getY() * deltaTime);

            if (collidingTop && velocity.getY() < 0.0f) {
                velocity.setY(0.0f);
            }
            if (collidingRight && velocity.getX() > 0.0f) {
                velocity.setX(0.0f);
            }
            if (collidingBottom && velocity.getY() > 0) {
                velocity.setY(0.0f);
            }
            if (collidingLeft && velocity.getX() < 0) {
                velocity.setX(0.0f);
            }

            position.add(velocity.getX() * deltaTime * PIXELS_PER_METER, 
                velocity.getY() * deltaTime * PIXELS_PER_METER);
        }
        if (this.collidable) {
            resolveCollisions(physicsEntities);
        }
    }

    public Vector2f getPosition() {
        return position;
    }

    public Vector2f getVelocity() {
        return velocity;
    }

    /**
     * Returns wether this entity is colliding with something on the bottom.
     */
    public boolean getCollidingBottom() {
        return collidingBottom;
    }

    public void setCollidable(boolean collidable) {
        this.collidable = collidable;
    }
}
