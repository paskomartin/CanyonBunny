package com.example.canyonbunny.util;

public class Constants {
    // visible game world is 5 meters wide
    public static final float VIEWPORT_WIDTH = 5.0f;
    // visible game world is 5 meters tall
    public static final float VIEWPORT_HEIGHT = 5.0f;
    // GUI width
    public static final float VIEWPORT_GUI_WIDTH = 800.0f;
    // GUI height
    public static final float VIEWPORT_GUI_HEIGHT = 480.0f;

    public static  final String TEXTURE_ATLAS_OBJECTS = "images/canyonbunny.pack.atlas";

    public static final String LEVEL_01 = "levels/level-01.png";

    public static final int LIVES_START = 3;

    public static final float ITEM_FEATHER_POWERUP_DURATION = 9.0f;

    public static final float TIME_DELAY_GAME_OVER = 3;

    public static final String TEXTURE_ATLAS_UI = "images/canyonbunny-ui.pack.atlas";

    public static final String TEXTURE_ATLAS_LIBGDX_UI = "images/uiskin.atlas";

    public static final String SKIN_LIBGDX_UI = "images/uiskin.json";

    public static final String SKIN_CANYONBUNNY_UI = "images/canyonbunny-ui.json";

    public static final String PREFERENCES = "canyonbunny.prefs";

    public static final int CARROTS_SPAWN_MAX = 100;

    public static final float CARROTS_SPAWN_RADIUS = 3.5f;

    public static final float TIME_DELAY_GAME_FINISHED = 6;

    public static final String shaderMonochromeVertex = "shaders/monochrome.vs";

    public static final String getShaderMonochromeFragment = "shaders/monochrome.fs";

    public static final float ACCEL_ANGLE_DEAD_ZONE = 5.0f;

    public static final float ACCEL_MAX_ANGLE_MAX_MOVEMENT = 20.0f;
}
