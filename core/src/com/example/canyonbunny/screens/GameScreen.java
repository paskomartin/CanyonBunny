package com.example.canyonbunny.screens;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.GL20;
import com.example.canyonbunny.game.Assets;
import com.example.canyonbunny.game.WorldController;
import com.example.canyonbunny.game.WorldRenderer;
import com.example.canyonbunny.util.GamePreferences;

public class GameScreen extends AbstractGameScreen {
    private static final String TAG = GameScreen.class.getName();
    private WorldController worldController;
    private WorldRenderer worldRenderer;

    private boolean paused;


    public GameScreen(DirectedGame game) {
        super(game);
    }

    @Override
    public void render(float deltaTime) {
        if (!paused) {
            worldController.update(deltaTime);
        }

        Gdx.gl.glClearColor(0x64 / 255.f, 0x95 / 255.f, 0xed / 255.f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        worldRenderer.render();

        // Issue 01 - fixed with desktop application
        // screen should be change only here - on end of render frame
        if (worldController.isBackToMenu()) {
            worldController.backToMenu();
        }
    }

    @Override
    public void resize(int width, int height) {
        worldRenderer.resize(width, height);
    }

    @Override
    public void show() {
        GamePreferences.instance.load();
        worldController = new WorldController(game);
        worldRenderer = new WorldRenderer(worldController);
        Gdx.input.setCatchBackKey(true);
    }

    @Override
    public void hide() {
        worldController.dispose();
        worldRenderer.dispose();
        Gdx.input.setCatchBackKey(false);
    }

    @Override
    public void pause() {
        paused = true;
    }

    @Override
    public void resume() {
        super.resume();
        paused = false;
    }

    @Override
    public InputProcessor getInputProcessor() {
        return worldController;
    }
}
