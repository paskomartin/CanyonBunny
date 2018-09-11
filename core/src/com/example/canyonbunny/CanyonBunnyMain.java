package com.example.canyonbunny;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.example.canyonbunny.game.Assets;
import com.example.canyonbunny.game.WorldController;
import com.example.canyonbunny.game.WorldRenderer;

public class CanyonBunnyMain extends Game { //ApplicationAdapter {
	private static final String TAG = CanyonBunnyMain.class.getName();
	private WorldController worldController;
	private WorldRenderer worldRenderer;
	private boolean paused;

	@Override
	public void create () {
		// TODO: set log level to DEBUG
		Gdx.app.setLogLevel(Application.LOG_DEBUG);

		// load assets
		Assets.instance.init(new AssetManager());
		worldController = new WorldController();
		worldRenderer = new WorldRenderer(worldController);
		paused = false;
	}

	@Override
	public void render () {
		// update world
		if (!paused) {
			worldController.update(Gdx.graphics.getDeltaTime());
		}
		Gdx.gl.glClearColor(100/255.f, 149/255.f, 237/255.f, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

		worldRenderer.render();
	}

	@Override
	public void resize(int width, int height) {
		worldRenderer.resize(width, height);
	}

	@Override
	public void pause() {
		paused = true;
	}

	@Override
	public void resume() {
		Assets.instance.init(new AssetManager());
		paused = false;
	}

	@Override
	public void dispose () {
		worldRenderer.dispose();
		Assets.instance.dispose();
	}
}
