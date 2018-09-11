package com.example.canyonbunny.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.example.canyonbunny.util.CameraHelper;

import java.security.Key;

public class WorldController extends InputAdapter{
    private static final String TAG = WorldController.class.getName();

    public CameraHelper cameraHelper;

    public Sprite[] testSprites;
    public int selectedSprite;


    public WorldController() {
        init();
    }

    private void init() {
        Gdx.input.setInputProcessor(this);
        cameraHelper = new CameraHelper();
        initTestObjects();
    }

    private void initTestObjects() {
        testSprites = new Sprite[5];
        // create a list of texture regions
        Array<TextureRegion> regions = new Array<TextureRegion>();
        regions.add(Assets.instance.bunny.head);
        regions.add(Assets.instance.feather.feather);
        regions.add(Assets.instance.goldCoin.goldCoin);
        // create new sprites using a random texture region
        for (int i = 0; i < testSprites.length; ++i) {
            Sprite spr = new Sprite(regions.random());
            // define sprite size to be 1m x 1m in game world
            spr.setSize(1, 1);
            // set origin to the center
            spr.setOrigin(spr.getWidth() / 2.0f, spr.getHeight() / 2.0f);
            float x = MathUtils.random(-2.0f, 2.0f);
            float y = MathUtils.random(-2.0f, 2.0f);
            spr.setPosition(x, y);
            testSprites[i] = spr;
        }
        selectedSprite = 0;

    }

    @Deprecated
    private Pixmap createProceduralPixmap(int width, int height) {
        Pixmap pixmap = new Pixmap(width, height, Pixmap.Format.RGBA8888);
        // red 50% opacity
        pixmap.setColor(1, 0, 0, 0.5f);
        pixmap.fill();
        // draw x shape on square
        pixmap.setColor(1, 1, 0, 1);
        pixmap.drawLine(0, 0, width, height);
        pixmap.drawLine(width, 0, 0, height);
        // draw border square
        pixmap.setColor(0, 1, 1, 1);
        pixmap.drawRectangle(0,0, width, height);
        return pixmap;
    }

    public void update(float deltaTime) {
        handleDebugInput(deltaTime);
        updateTestObjects(deltaTime);
        cameraHelper.update(deltaTime);
    }

    private void handleDebugInput(float deltaTime) {
        float sprMoveSpeed = 5 * deltaTime;
        if (Gdx.input.isKeyPressed(Input.Keys.A)) {
            moveSelectedSprite(-sprMoveSpeed, 0);
        }
        if (Gdx.input.isKeyPressed(Input.Keys.D)) {
            moveSelectedSprite(sprMoveSpeed, 0);
        }
        if (Gdx.input.isKeyPressed(Input.Keys.W)) {
            moveSelectedSprite(0, sprMoveSpeed);
        }
        if (Gdx.input.isKeyPressed(Input.Keys.S)) {
            moveSelectedSprite(0, -sprMoveSpeed);
        }

        // camera move controls
        float camMoveSpeed = 5 * deltaTime;
        float camMoveSpeeAccelerationFactor = 5;
        if (Gdx.input.isKeyPressed(Input.Keys.SHIFT_LEFT)) {
            camMoveSpeed *= camMoveSpeeAccelerationFactor;
        }
        if (Gdx.input.isKeyPressed(Input.Keys.LEFT)) {
            moveCamera(-camMoveSpeed, 0);
        }
        if (Gdx.input.isKeyPressed(Input.Keys.RIGHT)) {
            moveCamera(camMoveSpeed, 0);
        }
        if (Gdx.input.isKeyPressed(Input.Keys.UP)) {
            moveCamera(0, camMoveSpeed);
        }
        if (Gdx.input.isKeyPressed(Input.Keys.DOWN)) {
            moveCamera(0, -camMoveSpeed);
        }
        if (Gdx.input.isKeyPressed(Input.Keys.BACKSPACE)) {
            cameraHelper.setPosition(0, 0);
        }

        // camera zoom controls
        float camZoomSpeed = 1 * deltaTime;
        float camZoomSpeedAccelerationFactor = 5;
        if (Gdx.input.isKeyPressed(Input.Keys.SHIFT_LEFT)) {
            camZoomSpeed *= camZoomSpeedAccelerationFactor;
        }
        if (Gdx.input.isKeyPressed(Input.Keys.COMMA)) {
            cameraHelper.addZoom(camZoomSpeed);
        }
        if (Gdx.input.isKeyPressed(Input.Keys.PERIOD)) {
            cameraHelper.addZoom(-camZoomSpeed);
        }
        if (Gdx.input.isKeyPressed(Input.Keys.SLASH)) {
            cameraHelper.setZoom(1);
        }
    }

    private void moveCamera(float x, float y) {
        x += cameraHelper.getPosition().x;
        y += cameraHelper.getPosition().y;
        cameraHelper.setPosition(x, y);
    }

    private void moveSelectedSprite(float x, float y) {
        testSprites[selectedSprite].translate(x, y);

    }

    private void updateTestObjects(float deltaTime) {
        float rotation = testSprites[selectedSprite].getRotation();
        // rotate sprite by 90 degrees per second
        rotation += 90 * deltaTime;
        rotation %= 360;
        testSprites[selectedSprite].setRotation(rotation);
    }

    @Override
    public boolean keyUp(int keycode) {
        if (keycode == Input.Keys.R) {
            init();
            Gdx.app.debug(TAG, "Game world resetted");
        }
        else if (keycode == Input.Keys.SPACE) {
            selectedSprite = (selectedSprite + 1) % testSprites.length;
            if (cameraHelper.hasTarget()) {
                cameraHelper.setTarget(testSprites[selectedSprite]);
            }
            Gdx.app.debug(TAG, "Sprite #" + selectedSprite + " selected");
        }
        else if (keycode == Input.Keys.ENTER) {
            cameraHelper.setTarget(cameraHelper.hasTarget() ? null : testSprites[selectedSprite]);
            Gdx.app.debug(TAG, "Camera follow enabled: " + cameraHelper.hasTarget());
        }

        return false;
    }
}
