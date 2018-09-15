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
import com.example.canyonbunny.screens.MenuScreen;

public class CanyonBunnyMain extends Game {

	@Override
	public void create () {
		Gdx.app.setLogLevel(Application.LOG_INFO);
		// load assets
		Assets.instance.init(new AssetManager());
		// start game at menu screen
		setScreen(new MenuScreen(this));
	}
}
