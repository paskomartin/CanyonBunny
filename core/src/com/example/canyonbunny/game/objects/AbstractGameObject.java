package com.example.canyonbunny.game.objects;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;

public abstract class AbstractGameObject {
    public Vector2 position;
    public Vector2 dimension;
    public Vector2 origin;
    public Vector2 scale;
    public float rotation;

    public Vector2 velocity;
    public Vector2 terminalVelocity;
    public Vector2 friction;
    public Vector2 acceleration;
    public Rectangle bounds;

    public AbstractGameObject() {
        position = new Vector2();
        dimension = new Vector2(1, 1);
        origin = new Vector2();
        scale = new Vector2(1, 1);
        rotation = 0;

        velocity = new Vector2();
        terminalVelocity = new Vector2();
        friction = new Vector2();
        acceleration = new Vector2();
        bounds = new Rectangle();
    }

    public void update(float deltaTime) {
        updateMotionX(deltaTime);
        updateMotionY(deltaTime);
        position.x += velocity.x * deltaTime;
        position.y += velocity.y * deltaTime;
    }


    protected void updateMotionX(float deltaTime) {
        if (velocity.x != 0) {
            // apply friction
            if (velocity.x > 0) {
                velocity.x = Math.max(velocity.x - friction.x * deltaTime, 0);
            } else {
                velocity.x = Math.min(velocity.x + friction.x * deltaTime, 0);
            }
        }

        // apply acceleration
        velocity.x += acceleration.x * deltaTime;
        velocity.x = MathUtils.clamp(velocity.x, -terminalVelocity.x, terminalVelocity.x);
    }

    protected void updateMotionY(float deltaTime) {
        if (velocity.y != 0) {
            //friction
            if (velocity.y > 0) {
                velocity.y = Math.max(velocity.y - friction.y * deltaTime, 0);
            }
            else {
                velocity.y = Math.min(velocity.y + friction.y * deltaTime, 0);
            }
        }

        // acceleration
        velocity.y += acceleration.y * deltaTime;
        velocity.y = MathUtils.clamp(velocity.y, -terminalVelocity.y, terminalVelocity.y);
    }


    public abstract void render(SpriteBatch batch);

}
