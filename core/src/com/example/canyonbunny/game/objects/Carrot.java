package com.example.canyonbunny.game.objects;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.example.canyonbunny.game.Assets;

public class Carrot extends AbstractGameObject {
    private TextureRegion regCarrot;

    public Carrot() {
        init();
    }

    private void init() {
        dimension.set(0.25f, 0.5f);
        regCarrot = Assets.instance.levelDecoration.carrot;

        // bounding box collision detection
        bounds.set(0, 0, dimension.x, dimension.y);
        origin.set(dimension.x / 2, dimension.y / 2);
    }

    public void render(SpriteBatch batch) {
        batch.draw(regCarrot.getTexture(), position.x - origin.x,
                position.y - origin.y, origin.x, origin.y, dimension.x, dimension.y,
                scale.x, scale.y, rotation, regCarrot.getRegionX(), regCarrot.getRegionY(),
                regCarrot.getRegionWidth(), regCarrot.getRegionHeight(), false, true);
    }
}
