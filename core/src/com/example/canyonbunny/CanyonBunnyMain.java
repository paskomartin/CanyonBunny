package com.example.canyonbunny;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.math.Interpolation;
import com.example.canyonbunny.game.Assets;
import com.example.canyonbunny.screens.DirectedGame;
import com.example.canyonbunny.screens.MenuScreen;
import com.example.canyonbunny.screens.transitions.ScreenTransition;
import com.example.canyonbunny.screens.transitions.ScreenTransitionSlice;
import com.example.canyonbunny.util.AudioManager;
import com.example.canyonbunny.util.GamePreferences;

public class CanyonBunnyMain extends DirectedGame {

	@Override
	public void create () {
		Gdx.app.setLogLevel(Application.LOG_DEBUG);
		// load assets
		Assets.instance.init(new AssetManager());

		// load preferences for audio settings and start playing music
		GamePreferences.instance.load();
		AudioManager.instance.play(Assets.instance.music.song01);

		// start game at menu screen
		ScreenTransition transition = ScreenTransitionSlice.init(2, ScreenTransitionSlice.UP_DOWN,
				10, Interpolation.pow5Out);
		setScreen(new MenuScreen(this), transition);
	}
}
