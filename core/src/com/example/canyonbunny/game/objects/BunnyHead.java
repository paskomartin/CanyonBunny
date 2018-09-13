package com.example.canyonbunny.game.objects;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.example.canyonbunny.game.Assets;
import com.example.canyonbunny.util.Constants;

import org.omg.CORBA.PUBLIC_MEMBER;

public class BunnyHead extends AbstractGameObject {
    public static final String TAG = BunnyHead.class.getName();

    private final float JUMP_TIME_MAX = 0.3f;
    private final float JUMP_TIME_MIN = 0.1f;
    private final float JUMP_TIME_OFFSET_FLYING = JUMP_TIME_MAX - 0.018f;

    public enum VIEW_DIRECTION {
        LEFT,
        RIGHT
    }

    public enum JUMP_STATE {
        GROUNDED,
        FALLING,
        JUMP_RISING,
        JUMP_FALLING
    }

    private TextureRegion regHead;
    public VIEW_DIRECTION viewDirection;
    public JUMP_STATE jumpState;
    public float timeJumping;
    public boolean hasFeatherPowerup;
    public float timeLeftFeatherPowerup;

    // -----
    public BunnyHead() {
        init();
    }

    public void init() {
        dimension.set(1, 1);
        regHead = Assets.instance.bunny.head;
        origin.set(dimension.x / 2, dimension.y / 2);
        bounds.set(0, 0, dimension.x, dimension.y);
        // physics vals
        terminalVelocity.set(3.0f, 4.0f);
        friction.set(12.0f, 0.0f);
        acceleration.set(0.0f, -25.0f); // gravity
        //
        viewDirection = VIEW_DIRECTION.RIGHT;
        jumpState = JUMP_STATE.FALLING;
        timeJumping = 0;
        // powerups
        hasFeatherPowerup = false;
        timeLeftFeatherPowerup = 0;
    }

    public void setJumping(boolean jumpKeyPressed) {
        switch (jumpState) {
            case GROUNDED:
                if (jumpKeyPressed) {
                    // start counting from beginning
                    timeJumping = 0;
                    jumpState = JUMP_STATE.JUMP_RISING;
                }
                break;

            case JUMP_RISING:
                if (!jumpKeyPressed) {
                    jumpState = JUMP_STATE.JUMP_FALLING;
                }
                break;

            case FALLING:   // falling down
            case JUMP_FALLING: // falling down after jump
                if (jumpKeyPressed && hasFeatherPowerup) {
                    timeJumping = JUMP_TIME_OFFSET_FLYING;
                    jumpState = JUMP_STATE.JUMP_RISING;
                }
                break;
        }

    }

    public void setFeatherPowerup(boolean pickedup) {
        hasFeatherPowerup = pickedup;
        if (pickedup) {
            timeLeftFeatherPowerup = Constants.ITEM_FEATHER_POWERUP_DURATION;
        }
    }

    public boolean hasFeatherPowerup() {
        return hasFeatherPowerup && timeLeftFeatherPowerup > 0;
    }

    @Override
    public void update(float deltaTime) {
        super.update(deltaTime);

        if (velocity.x != 0) {
            viewDirection = velocity.x < 0 ? VIEW_DIRECTION.LEFT : VIEW_DIRECTION.RIGHT;
        }
        if (timeLeftFeatherPowerup > 0) {
            timeLeftFeatherPowerup -= deltaTime;
            if (timeLeftFeatherPowerup < 0) {
                timeLeftFeatherPowerup = 0;
                setFeatherPowerup(false);
            }
        }
    }

    @Override
    protected void updateMotionY(float deltaTime) {
        switch (jumpState) {
            case GROUNDED:
                jumpState = JUMP_STATE.FALLING;
                break;

            case JUMP_RISING:
                // keep track of jump time
                timeJumping += deltaTime;
                // jump time left?
                if (timeJumping <= JUMP_TIME_MAX) {
                    // still jump
                    velocity.y = terminalVelocity.y;
                }
                break;

            case FALLING:
                break;

            case JUMP_FALLING:
                timeJumping += deltaTime;
                // jump to minimal heighr if jump key was pressed too short
                if (timeJumping > 0 && timeJumping <= JUMP_TIME_MIN) {
                    // still jump
                    velocity.y = terminalVelocity.y;
                }
                break;
        }
        if (jumpState != JUMP_STATE.GROUNDED) {
            super.updateMotionY(deltaTime);
        }
    }

    @Override
    public void render(SpriteBatch batch) {
        TextureRegion reg = regHead;
        if (hasFeatherPowerup) {
            batch.setColor(1.0f, 0.8f, 0, 1.0f);
        }
        batch.draw(reg.getTexture(), position.x, position.y, origin.x, origin.y, dimension.x, dimension.y,
                scale.x, scale.y, rotation, reg.getRegionX(), reg.getRegionY(), reg.getRegionWidth(),
                reg.getRegionHeight(), viewDirection == VIEW_DIRECTION.LEFT, false);

        batch.setColor(1, 1, 1, 1);
    }
}