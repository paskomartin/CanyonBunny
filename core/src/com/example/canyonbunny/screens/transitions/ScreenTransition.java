package com.example.canyonbunny.screens.transitions;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public interface ScreenTransition {
    float getDuration();
    void render(SpriteBatch batch, Texture currScreen, Texture nextScreen, float alpha);
}
