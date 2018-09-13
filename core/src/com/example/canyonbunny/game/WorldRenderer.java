package com.example.canyonbunny.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Disposable;
import com.example.canyonbunny.util.Constants;

public class WorldRenderer implements Disposable {
    private OrthographicCamera camera;
    private SpriteBatch batch;
    private WorldController worldController;
    private OrthographicCamera cameraGUI;

    public WorldRenderer(WorldController worldController) {
        this.worldController = worldController;
        init();
    }

    private void init() {
        batch = new SpriteBatch();
        camera = new OrthographicCamera(Constants.VIEWPORT_WIDTH, Constants.VIEWPORT_HEIGHT);
        camera.position.set(0, 0, 0);
        camera.update();

        cameraGUI = new OrthographicCamera(Constants.VIEWPORT_GUI_WIDTH, Constants.VIEWPORT_GUI_HEIGHT);
        cameraGUI.position.set(0, 0, 0);
        cameraGUI.setToOrtho(true); // flip y-axis
        cameraGUI.update();
    }

    public void render() {
        renderWorld(batch);
        renderGui(batch);
    }

    private void renderWorld(SpriteBatch batch) {
        worldController.cameraHelper.applyTo(camera);
        batch.setProjectionMatrix(camera.combined);
        batch.begin();
        worldController.level.render(batch);
        batch.end();
    }

    private void renderGui(SpriteBatch batch) {
        batch.setProjectionMatrix(cameraGUI.combined);
        batch.begin();
        renderGuiScore(batch);
        renderGuiFeatherPowerup(batch);
        renderGuiExtraLive(batch);
        renderGuiFpsCounter(batch);
        renderGuiGameOverMessage(batch);
        batch.end();
    }

    private void renderGuiScore(SpriteBatch batch) {
        float x = -15;
        float y = -15;

        batch.draw(Assets.instance.goldCoin.goldCoin, x, y, 50, 50, 100, 100, 0.35f, -0.35f,0);
        Assets.instance.fonts.defaultBig.draw(batch, "" + worldController.score, x + 75, y + 37);
    }

    private void renderGuiExtraLive(SpriteBatch batch) {
        float x = cameraGUI.viewportWidth - 50 - Constants.LIVES_START * 50;
        float y = -15;
        for (int i = 0; i < Constants.LIVES_START; ++i) {
            if (worldController.lives <= i) {
                batch.setColor(0.5f, 0.5f, 0.5f, 0.5f);
            }
            batch.draw(Assets.instance.bunny.head, x + i * 50, y, 50, 50, 120, 100, 0.35f, -0.35f, 0);
            batch.setColor(1, 1, 1, 1);
        }
    }

    private void renderGuiFpsCounter(SpriteBatch batch) {
        float x = cameraGUI.viewportWidth - 55;
        float y = cameraGUI.viewportHeight - 15;
        int fps = Gdx.graphics.getFramesPerSecond();
        BitmapFont font = Assets.instance.fonts.defaultNormal;
        if (fps >= 45) {
            font.setColor(0, 1, 0, 1);
        }
        else if (fps >= 30) {
            font.setColor(1, 1, 0, 1);
        }
        else {
            font.setColor(1, 0, 0, 1);
        }
        font.draw(batch, "FPS: " + fps, x, y);
        font.setColor(1, 1, 1, 1);
    }

    private void renderGuiGameOverMessage(SpriteBatch batch) {
        float x = cameraGUI.viewportWidth / 2;
        float y = cameraGUI.viewportHeight / 2;
        if (worldController.isGameOver()) {
            BitmapFont fontGameOver = Assets.instance.fonts.defaultBig;
            fontGameOver.setColor(1, 0.75f, 0.25f, 1);
            fontGameOver.draw(batch, "GAME OVER", x, y, 0, Align.center,true );
            fontGameOver.setColor(1, 1, 1, 1);
        }
    }

    private void renderGuiFeatherPowerup(SpriteBatch batch) {
        float x = -15;
        float y = 30;
        float timeLeftFeatherPowerup = worldController.level.bunnyHead.timeLeftFeatherPowerup;
        if (timeLeftFeatherPowerup > 0) {
            // start icon fade in/out if the left power-up time is less than 4 seconds.
            // the fade interval is set to 5 changes per second
            if (timeLeftFeatherPowerup < 4) {
                if ( ((int)(timeLeftFeatherPowerup * 5) % 2) != 0) {
                    batch.setColor(1, 1, 1, 0.5f);
                }
            }
            batch.draw(Assets.instance.feather.feather, x, y, 50, 50, 100, 100, 0.33f,
                    -0.35f, 0);
            batch.setColor(1, 1, 1, 1);
            Assets.instance.fonts.defaultSmall.draw(batch, "" + (int)timeLeftFeatherPowerup,
                    x + 60, y + 57);
        }
    }

    public void resize(int width, int height) {
        // calculates
        //the aspect ratio between our desired visible world height and the currently available
        //display height.
        // page 125
        camera.viewportWidth = (Constants.VIEWPORT_HEIGHT / height) * width;
        camera.update();

        // aspect ratio
        cameraGUI.viewportWidth = (Constants.VIEWPORT_GUI_HEIGHT / (float)height) * (float)width;
        cameraGUI.viewportHeight = Constants.VIEWPORT_GUI_HEIGHT;
        cameraGUI.position.set(cameraGUI.viewportWidth / 2, cameraGUI.viewportHeight / 2, 0);
        cameraGUI.update();

    }

    @Override
    public void dispose() {
        batch.dispose();
    }
}
