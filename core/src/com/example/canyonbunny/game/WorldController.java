package com.example.canyonbunny.game;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Polygon;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Disposable;
import com.example.canyonbunny.game.objects.BunnyHead;
import com.example.canyonbunny.game.objects.Carrot;
import com.example.canyonbunny.game.objects.Feather;
import com.example.canyonbunny.game.objects.GoldCoin;
import com.example.canyonbunny.game.objects.Rock;
import com.example.canyonbunny.screens.DirectedGame;
import com.example.canyonbunny.screens.MenuScreen;
import com.example.canyonbunny.screens.transitions.ScreenTransitionSlide;
import com.example.canyonbunny.util.AudioManager;
import com.example.canyonbunny.util.CameraHelper;
import com.example.canyonbunny.util.Constants;
import com.sun.scenario.effect.impl.prism.PrImage;

public class WorldController extends InputAdapter
    implements Disposable {

    private static final String TAG = WorldController.class.getName();
    private DirectedGame game;
    public CameraHelper cameraHelper;

    public Level level;
    public int lives;
    public int score;

    // collisions rects
    private Rectangle r1 = new Rectangle();
    private Rectangle r2 = new Rectangle();

    private float timeLeftGameOverDelay;

    public float livesVisual;
    public float scoreVisual;

    private boolean goalReached;
    public World b2world;

    private boolean accelerometerAvailable;

    /// ---

    public WorldController(DirectedGame game) {
        this.game = game;
        init();
    }

    private void init() {
        accelerometerAvailable = Gdx.input.isPeripheralAvailable(Input.Peripheral.Accelerometer);
        cameraHelper = new CameraHelper();
        lives = Constants.LIVES_START;
        livesVisual = lives;
        timeLeftGameOverDelay = 0;
        initLevel();
        backtomenu = false;
    }

    private void initLevel() {
        score = 0;
        scoreVisual = score;
        goalReached = false;
        level = new Level(Constants.LEVEL_01);
        cameraHelper.setTarget(level.bunnyHead);
        initPhysics();
    }

    private void initPhysics() {
        if (b2world != null) {
            b2world.dispose();
        }

        b2world = new World(new Vector2(0, -9.81f), true);

        // Rocks
        Vector2 origin = new Vector2();
        for(Rock rock : level.rocks) {
            BodyDef bodyDef = new BodyDef();
            bodyDef.type = BodyDef.BodyType.KinematicBody;
            bodyDef.position.set(rock.position);
            Body body = b2world.createBody(bodyDef);
            rock.body = body;

            PolygonShape polygonShape = new PolygonShape();
            origin.x = rock.bounds.width / 2.0f;
            origin.y = rock.bounds.height / 2.0f;
            polygonShape.setAsBox(rock.bounds.width / 2.0f, rock.bounds.height / 2.0f, origin, 0);

            FixtureDef fixtureDef = new FixtureDef();
            fixtureDef.shape = polygonShape;
            body.createFixture(fixtureDef);
            polygonShape.dispose();
        }
    }

    public void update(float deltaTime) {
        handleDebugInput(deltaTime);
        if (isGameOver() || goalReached) {
            timeLeftGameOverDelay -= deltaTime;
            if (timeLeftGameOverDelay < 0) {
                // Issue 01 fixed
                backtomenu = true;
                return;
                //backToMenu();
            }
        }
        else {
            handleInputGame(deltaTime);
        }
        level.update(deltaTime);
        testCollisions();
        b2world.step(deltaTime, 8, 3);
        cameraHelper.update(deltaTime);
        if (!isGameOver() && isPlayerInWater()) {
            AudioManager.instance.play(Assets.instance.sounds.liveLost);
            --lives;
            if (isGameOver()) {
                timeLeftGameOverDelay = Constants.TIME_DELAY_GAME_OVER;
            }
            else {
                initLevel();
            }
        }
        level.mountains.updateScrollPosition(cameraHelper.getPosition());
        if (livesVisual > lives) {
            livesVisual = Math.max(lives, livesVisual - 1 * deltaTime);
        }
        if (scoreVisual < score) {
            scoreVisual = Math.min(score, scoreVisual + 250 * deltaTime);
        }
    }

    private void handleInputGame(float deltaTime) {
        if (cameraHelper.hasTarget(level.bunnyHead)) {
            // player movement
            if (Gdx.input.isKeyPressed(Input.Keys.LEFT)) {
                level.bunnyHead.velocity.x = -level.bunnyHead.terminalVelocity.x;
            }
            else if (Gdx.input.isKeyPressed(Input.Keys.RIGHT)) {
                level.bunnyHead.velocity.x = level.bunnyHead.terminalVelocity.x;
            }
            else {
                // execute auto-forward movement on non-desktop platform
                /*
                if (Gdx.app.getType() != Application.ApplicationType.Desktop) {
                    level.bunnyHead.velocity.x = level.bunnyHead.terminalVelocity.x;
                 */
                /// use accelerometer
                if (accelerometerAvailable) {
                    // normalize accelerometer values form -10 10 to -1, 1
                    // which translate to rotation -90, 90 degrees
                    float amount = Gdx.input.getAccelerometerY() / 10.f;
                    amount *= 90.f;
                    // is angle of rotation inside dead zone
                    if (Math.abs(amount) < Constants.ACCEL_ANGLE_DEAD_ZONE) {
                        amount = 0;
                    }
                    else  {
                        // use the defined max angle of rotation instead of full 90 degrees
                        // for max velocity
                        amount /= Constants.ACCEL_MAX_ANGLE_MAX_MOVEMENT;
                    }
                    level.bunnyHead.velocity.x = level.bunnyHead.terminalVelocity.x * amount;
                }
                else if (Gdx.app.getType() != Application.ApplicationType.Desktop) {
                    level.bunnyHead.velocity.x = level.bunnyHead.terminalVelocity.x;
                }
            }

            // jump
            if (Gdx.input.isTouched() || Gdx.input.isKeyPressed(Input.Keys.SPACE) ) {
                level.bunnyHead.setJumping(true);
            }
            else {
                level.bunnyHead.setJumping(false);
            }
        }
    }

    private void handleDebugInput(float deltaTime) {
        if (Gdx.app.getType() != Application.ApplicationType.Desktop) {
            return;
        }
        if (!cameraHelper.hasTarget(level.bunnyHead)) {
            // camera move controls
            float camMoveSpeed = 5 * deltaTime;
            float camMoveSpeedAccelerationFactor = 5;
            if (Gdx.input.isKeyPressed(Input.Keys.SHIFT_LEFT)) {
                camMoveSpeed *= camMoveSpeedAccelerationFactor;
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
    }

    private void moveCamera(float x, float y) {
        x += cameraHelper.getPosition().x;
        y += cameraHelper.getPosition().y;
        cameraHelper.setPosition(x, y);
    }

    @Override
    public boolean keyUp(int keycode) {
        if (keycode == Input.Keys.R) {
            init();
            Gdx.app.debug(TAG, "Game world resetted");
        }
        // toggle camera follow
        else if (keycode == Input.Keys.ENTER) {
            cameraHelper.setTarget(cameraHelper.hasTarget() ? null : level.bunnyHead);
            Gdx.app.debug(TAG, "Camera follow enabled: " + cameraHelper.hasTarget());
        }
        else if (keycode == Input.Keys.ESCAPE || keycode == Input.Keys.BACK) {
            backtomenu = true;
            //backToMenu();
        }

        return false;
    }

    private void testCollisions() {
        r1.set(level.bunnyHead.position.x, level.bunnyHead.position.y, level.bunnyHead.bounds.width,
                level.bunnyHead.bounds.height);

        // bunny head <-> rocks tests
        for(Rock rock : level.rocks) {
            r2.set(rock.position.x, rock.position.y, rock.bounds.width, rock.bounds.height);
            if (!r1.overlaps(r2)) {
                continue;
            }
            onCollisionBunnyHeadWithRock(rock);
            // IMPORTANT: must do all collisions for valid edge testing on rocks.
        }

        // Bunny head <-> gold coins
        for(GoldCoin goldCoin : level.goldCoins) {
            if (goldCoin.collected) {
                continue;
            }
            r2.set(goldCoin.position.x, goldCoin.position.y, goldCoin.bounds.width,
                    goldCoin.bounds.height);
            if (!r1.overlaps(r2)) {
                continue;
            }
            onCollisionBunnyWithGoldCoin(goldCoin);
            break;
        }

        // bunny <-> feather
        for(Feather feather : level.feathers) {
            if (feather.collected) {
                continue;
            }
            r2.set(feather.position.x, feather.position.y, feather.bounds.width,
                    feather.bounds.height);
            if (!r1.overlaps(r2)) {
                continue;
            }
            onCollisionBunnyWithFeather(feather);
            break;
        }

        // bunny <-> goal
        if (!goalReached) {
            r2.set(level.goal.bounds);
            r2.x += level.goal.position.x;
            r2.y += level.goal.position.y;
            if (r1.overlaps(r2)) {
                onCollisionBunnyWithGoal();
            }
        }
    }

    private void onCollisionBunnyHeadWithRock(Rock rock) {
        BunnyHead bunnyHead = level.bunnyHead;
        float heightDifference = Math.abs(bunnyHead.position.y - (rock.position.y + rock.bounds.height));
        if (heightDifference > 0.25f) {
            boolean hitRightEdge = bunnyHead.position.x > (rock.position.x + rock.bounds.width / 2.0f);
            if (hitRightEdge) {
                bunnyHead.position.x = rock.position.x + rock.bounds.width;
            }
            else {
                bunnyHead.position.x = rock.position.x - bunnyHead.bounds.width;
            }
            return;
        }

        switch (bunnyHead.jumpState) {
            case GROUNDED:
                break;

            case FALLING:
            case JUMP_FALLING:
                bunnyHead.position.y = rock.position.y + bunnyHead.bounds.height + bunnyHead.origin.y;
                bunnyHead.jumpState = BunnyHead.JUMP_STATE.GROUNDED;
                break;

            case JUMP_RISING:
                bunnyHead.position.y = rock.position.y + bunnyHead.bounds.height + bunnyHead.origin.y;
                break;
        }
    }

    private void onCollisionBunnyWithGoldCoin(GoldCoin goldCoin) {
        goldCoin.collected = true;
        AudioManager.instance.play(Assets.instance.sounds.pickupCoin);
        score += goldCoin.getScore();
        Gdx.app.log(TAG, "Gold coin collected");
    }

    private void onCollisionBunnyWithFeather(Feather feather) {
        feather.collected = true;
        AudioManager.instance.play(Assets.instance.sounds.pickupFeather);
        score += feather.getScore();
        level.bunnyHead.setFeatherPowerup(true);
        Gdx.app.log(TAG,"Feather collected");
    }

    public boolean isGameOver() {
        return lives < 0;
    }

    public boolean isPlayerInWater() {
        return level.bunnyHead.position.y < -5;
    }

    public void backToMenu() {
        ScreenTransitionSlide transition = ScreenTransitionSlide.init(0.75f,
                ScreenTransitionSlide.DOWN, false, Interpolation.bounceOut);
        // switch to menu screen
        game.setScreen(new MenuScreen(game), transition);
        backtomenu = false;
    }

    public  boolean backtomenu = true;

    public boolean isBackToMenu() {
        return backtomenu;
    }

    private void spawnCarrots(Vector2 pos, int numCarrots, float radius) {
        float carrotShapeScale = 0.5f;
        for(int i = 0; i < numCarrots; ++i) {
            Carrot carrot = new Carrot();
            // random spawn
            float x = MathUtils.random(-radius, radius);
            float y = MathUtils.random(5.0f, 15.0f);
            float rotation = MathUtils.random(0.0f, 360.f) * MathUtils.degreesToRadians;
            float carrotScale = MathUtils.random(0.5f, 1.5f);
            carrot.scale.set(carrotScale, carrotScale);

            BodyDef bodyDef = new BodyDef();
            bodyDef.position.set(pos);
            bodyDef.position.add(x, y);
            bodyDef.angle = rotation;
            Body body = b2world.createBody(bodyDef);
            body.setType(BodyDef.BodyType.DynamicBody);
            carrot.body = body;

            PolygonShape polygonShape = new PolygonShape();
            float halfWidth = carrot.bounds.width / 2.0f * carrotScale;
            float halfHeight = carrot.bounds.height / 2.0f * carrotScale;
            polygonShape.setAsBox(halfWidth * carrotShapeScale, halfHeight * carrotShapeScale);

            FixtureDef fixtureDef = new FixtureDef();
            fixtureDef.shape = polygonShape;
            fixtureDef.density = 50;
            fixtureDef.restitution = .5f;
            fixtureDef.friction = .5f;
            body.createFixture(fixtureDef);
            polygonShape.dispose();

            level.carrots.add(carrot);
        }
    }

    private void onCollisionBunnyWithGoal() {
        goalReached = true;
        timeLeftGameOverDelay = Constants.TIME_DELAY_GAME_FINISHED;
        Vector2 centerPosBunnyHead = new Vector2(level.bunnyHead.position);
        centerPosBunnyHead.x += level.bunnyHead.bounds.width;
        spawnCarrots(centerPosBunnyHead, Constants.CARROTS_SPAWN_MAX, Constants.CARROTS_SPAWN_RADIUS);
    }

    @Override
    public void dispose() {
        if (b2world != null) {
            b2world.dispose();
        }
    }


}
