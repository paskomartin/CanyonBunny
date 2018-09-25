package com.example.canyonbunny.game.objects;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.example.canyonbunny.game.Assets;

public class Goal extends AbstractGameObject {
    private TextureRegion regGoal;

    public Goal() {
        init();
    }

    private void init() {
        dimension.set(3.0f, 3.0f);
        regGoal = Assets.instance.levelDecoration.goal;
        // collision rect
        bounds.set(1, Float.MIN_VALUE, 10, Float.MAX_VALUE);
        origin.set(dimension.x / 2, 0);
    }

    @Override
    public void render(SpriteBatch batch) {
        batch.draw(regGoal.getTexture(), position.x - origin.x, position.y - origin.y,
                origin.x, origin.y, dimension.x, dimension.y, scale.x, scale.y, rotation,
                regGoal.getRegionX(), regGoal.getRegionY(), regGoal.getRegionWidth(), regGoal.getRegionHeight(),
                false, false);
    }
}
