package com.example.canyonbunny.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.actions.SequenceAction;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.CheckBox;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.SelectBox;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Slider;
import com.badlogic.gdx.scenes.scene2d.ui.Stack;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.Window;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.viewport.StretchViewport;
import com.example.canyonbunny.game.Assets;
import com.example.canyonbunny.screens.transitions.ScreenTransition;
import com.example.canyonbunny.screens.transitions.ScreenTransitionFade;
import com.example.canyonbunny.util.AudioManager;
import com.example.canyonbunny.util.CharacterSkin;
import com.example.canyonbunny.util.Constants;
import com.example.canyonbunny.util.GamePreferences;

public class MenuScreen extends AbstractGameScreen {
    private static final String TAG = MenuScreen.class.getName();

    private Stage stage;
    private Skin skinCanyonBunny;

    // menu
    private Image imgBackground;
    private Image imgLogo;
    private Image imgInfo;
    private Image imgCoins;
    private Image imgBunny;
    private Button btnMenuPlay;
    private Button btnMenuOptions;

    // options
    private Window winOptions;
    private TextButton btnWinOptSave;
    private TextButton btnWinOptCancel;
    private CheckBox chkSound;
    private Slider sldSound;
    private CheckBox chkMusic;
    private Slider sldMusic;
    private SelectBox<CharacterSkin> selCharSkin;
    private Image imgCharSkin;
    private CheckBox chkShowFpsCounter;
    private CheckBox chkUseMonochromeShader;

    // debug
    private final float DEBUG_REBUILD_INTERVAL = 5.0f;
    private boolean debugEnabled = false;
    private boolean showDebugMenuRectangles = false;
    private float debugRebuildStage;

    private Skin skinLibgdx;

    // ----

    public MenuScreen(DirectedGame game) {
        super(game);
    }

    @Override
    public void render(float deltaTime) {
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        if (debugEnabled) {
            debugRebuildStage -= deltaTime;
            if (debugRebuildStage <= 0) {
                debugRebuildStage = DEBUG_REBUILD_INTERVAL;
                rebuildStage();
            }
        }

        stage.act(deltaTime);
        stage.draw();
        stage.setDebugAll(showDebugMenuRectangles);
        // depricated
        //Table.drawDebug(stage);

        /*

        if (Gdx.input.isTouched()) {
            game.setScreen(new GameScreen(game));
        }

        */
    }

    @Override
    public void resize(int width, int height) {
        stage.getViewport().update(width, height, true);
    }

    @Override
    public void show() {
        stage = new Stage(new StretchViewport(Constants.VIEWPORT_GUI_WIDTH, Constants.VIEWPORT_GUI_HEIGHT));
        rebuildStage();
    }

    @Override
    public void hide() {
        stage.dispose();
        skinCanyonBunny.dispose();
        skinLibgdx.dispose();
    }

    @Override
    public void pause() {

    }

    private void showMenuButtons(boolean visible) {
        float moveDuration = 1.0f;
        Interpolation moveEasing = Interpolation.swing;
        float delayOptionsButton = 0.25f;

        float moveX = 300 * (visible ? - 1 : 1);
        float moveY = 0;
        final Touchable touchEnabled = visible ? Touchable.enabled : Touchable.disabled;

        btnMenuPlay.addAction(
                Actions.moveBy(moveX,moveY,moveDuration,moveEasing)
        );
        btnMenuOptions.addAction(Actions.sequence(
                Actions.delay(delayOptionsButton),
                Actions.moveBy(moveX,moveY,moveDuration,moveEasing)
        ));

        SequenceAction seq = Actions.sequence();
        if (visible) {
            seq.addAction(Actions.delay(delayOptionsButton + moveDuration));
        }
        seq.addAction(Actions.run(new Runnable() {
            @Override
            public void run() {
                btnMenuPlay.setTouchable(touchEnabled);
                btnMenuOptions.setTouchable(touchEnabled);
            }
        }));
        stage.addAction(seq);
    }

    private void showOptionsWindow(boolean visible, boolean animated) {
        float alphaTo = visible ? 0.8f : 0.0f;
        float duration = animated ? 1.0f : 0.0f;
        Touchable touchEnabled = visible ? Touchable.enabled : Touchable.disabled;
        winOptions.addAction(Actions.sequence(
                Actions.touchable(touchEnabled),
                Actions.alpha(alphaTo, duration)
        ));
    }

    private void rebuildStage() {
        skinCanyonBunny = new Skin( Gdx.files.internal(Constants.SKIN_CANYONBUNNY_UI),
                new TextureAtlas(Constants.TEXTURE_ATLAS_UI));

        skinLibgdx = new Skin(Gdx.files.internal(Constants.SKIN_LIBGDX_UI),
                new TextureAtlas(Constants.TEXTURE_ATLAS_LIBGDX_UI));

        // build all layers
        Table layerBackground = buildBackgroundLayer();
        Table layerObjects = buildObjectsLayer();
        Table layerLogos = buildLogosLayer();
        Table layerControls = buildControlsLayer();
        Table layerOptionsWindow = buildOptionsWindowLayer();

        // assemble stage for menu screen
        stage.clear();
        Stack stack = new Stack();
        stack.setSize(Constants.VIEWPORT_GUI_WIDTH, Constants.VIEWPORT_GUI_HEIGHT);
        stack.add(layerBackground);
        stack.add(layerObjects);
        stack.add(layerLogos);
        stack.add(layerControls);

        stage.addActor(stack);
        stage.addActor(layerOptionsWindow);

    }

    private Table buildBackgroundLayer() {
        Table layer = new Table();
        imgBackground = new Image(skinCanyonBunny, "background");
        layer.add(imgBackground);
        return layer;
    }

    private Table buildObjectsLayer() {
        Table layer = new Table();
        // coins
        imgCoins = new Image(skinCanyonBunny, "coins");
        layer.addActor(imgCoins);
        imgCoins.setOrigin(imgCoins.getWidth() / 2, imgCoins.getHeight() / 2);
        imgCoins.addAction(Actions.sequence(
                Actions.moveTo(135, -20),
                Actions.scaleTo(0, 0),
                Actions.fadeOut(0),
                Actions.delay(2.5f),
                Actions.parallel(
                        Actions.moveBy(0,100,0.5f, Interpolation.swingOut),
                        Actions.scaleTo(1.0f, 1.0f,0.25f, Interpolation.linear),
                        Actions.alpha(1.0f, 0.5f)
                )
        ));
        //imgCoins.setPosition(135, 80);

        // bunny
        imgBunny = new Image(skinCanyonBunny, "bunny");
        layer.addActor(imgBunny);
        imgBunny.addAction(Actions.sequence(
                Actions.moveTo(655,510),
                Actions.delay(4.f),
                Actions.moveBy(-70, -100,0.5f, Interpolation.fade),
                Actions.moveBy(-100, -50, 0.5f, Interpolation.fade),
                Actions.moveBy(-150, -300, 1.f, Interpolation.elasticIn)
        ));
        //imgBunny.setPosition(355, 40);
        return layer;
    }

    private Table buildLogosLayer() {
        Table layer = new Table();
        layer.left().top();
        imgLogo = new Image(skinCanyonBunny, "logo");
        layer.add(imgLogo);
        layer.row().expandY();

        imgInfo = new Image(skinCanyonBunny, "info");
        layer.add(imgInfo).bottom();
        if (debugEnabled) {
            layer.debug();
        }
        return layer;
    }

    private Table buildControlsLayer() {
        Table layer = new Table();
        layer.right().bottom();

        btnMenuPlay = new Button(skinCanyonBunny, "play");
        layer.add(btnMenuPlay);
        btnMenuPlay.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                onPlayClicked();
            }
        });
        layer.row();

        btnMenuOptions = new Button(skinCanyonBunny, "options");
        layer.add(btnMenuOptions);
        btnMenuOptions.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                onOptionsClicked();
            }
        });

        if (debugEnabled) {
            layer.debug();
        }
        return layer;
    }

    private void onPlayClicked() {
        ScreenTransition transition = ScreenTransitionFade.init(0.75f);
        game.setScreen(new GameScreen(game), transition);
    }

    private void onOptionsClicked() {
        loadSettings();
        showMenuButtons(false);
        showOptionsWindow(true, true);
    }

    private Table buildOptionsWindowLayer() {
        winOptions = new Window("Options", skinLibgdx);
        winOptions.add(buildOptWinAudioSettings()).row();
        winOptions.add(buildOptWinSkinSelection()).row();
        winOptions.add(buildOptWinDebug()).row();
        winOptions.add(buildOptWinButtons()).pad(10, 0, 10, 0);

        winOptions.setColor(1, 1, 1, 0.8f);
        //winOptions.setVisible(false);
        showOptionsWindow(false, false);
        if (debugEnabled) {
            winOptions.debug();
        }
        // recalculate wudget size and positions
        winOptions.pack();
        winOptions.setPosition(Constants.VIEWPORT_GUI_WIDTH - winOptions.getWidth() - 50, 50);
        return  winOptions;
    }

    private Table buildOptWinAudioSettings() {
        Table tbl = new Table();
        tbl.pad(10, 10, 0, 10);
        tbl.add(new Label("Audio", skinLibgdx, "default-font", Color.ORANGE)).colspan(3);
        tbl.row();
        tbl.columnDefaults(0).padRight(10);
        tbl.columnDefaults(1).padRight(10);

        chkSound = new CheckBox("", skinLibgdx);
        tbl.add(chkSound);
        tbl.add(new Label("Sound", skinLibgdx));
        sldSound = new Slider(0.0f, 1.0f, 0.1f, false, skinLibgdx);
        tbl.add(sldSound);
        tbl.row();

        chkMusic = new CheckBox("", skinLibgdx);
        tbl.add(chkMusic);
        tbl.add(new Label("Music", skinLibgdx));
        sldMusic = new Slider(0.0f, 1.0f, 0.1f, false, skinLibgdx);
        tbl.add(sldMusic);
        tbl.row();
        return tbl;
    }

    private Table buildOptWinSkinSelection() {
        Table tbl = new Table();
        tbl.pad(10, 10, 0, 10);
        tbl.add(new Label("Character Skin", skinLibgdx, "default-font", Color.ORANGE)).colspan(2);
        tbl.row();

        selCharSkin = new SelectBox<CharacterSkin>(skinLibgdx);
        selCharSkin.setItems(CharacterSkin.values());

        selCharSkin.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                onCharSkinSelected( ((SelectBox<CharacterSkin>)actor).getSelectedIndex() );
            }
        });
        tbl.add(selCharSkin).width(120).padRight(20);
        imgCharSkin = new Image(Assets.instance.bunny.head);
        tbl.add(imgCharSkin).width(50).height(50);
        return  tbl;
    }

    private Table buildOptWinDebug() {
        Table tbl = new Table();
        tbl.pad(10, 10, 0, 10);
        tbl.add(new Label("Debug",skinLibgdx, "default-font", Color.RED)).colspan(3);
        tbl.row();
        tbl.columnDefaults(0).padRight(10);
        tbl.columnDefaults(1).padRight(10);
        chkShowFpsCounter = new CheckBox("", skinLibgdx);
        tbl.add(new Label("Show FPS Counter", skinLibgdx));
        tbl.add(chkShowFpsCounter);
        tbl.row();
        chkUseMonochromeShader = new CheckBox("", skinLibgdx);
        tbl.add(new Label("Use Monochrome Shader", skinLibgdx));
        tbl.add(chkUseMonochromeShader);
        tbl.row();
        return tbl;
    }

    private Table buildOptWinButtons() {
        Table tbl = new Table();
        Label lbl = new Label("", skinLibgdx);
        lbl.setColor(0.75f, 0.75f, 0.75f, 1);
        lbl.setStyle(new Label.LabelStyle(lbl.getStyle()));
        lbl.getStyle().background = skinLibgdx.newDrawable("white");
        tbl.add(lbl).colspan(2).height(1).width(220).pad(0, 0, 0, 1);
        tbl.row();
        lbl = new Label("", skinLibgdx);
        lbl.setColor(0.5f, 0.5f, 0.5f, 1);
        lbl.setStyle(new Label.LabelStyle(lbl.getStyle()));
        lbl.getStyle().background = skinLibgdx.newDrawable("white");
        tbl.add(lbl).colspan(2).height(1).width(220).pad(0, 1, 5, 0);
        tbl.row();

        btnWinOptSave = new TextButton("Save", skinLibgdx);
        tbl.add(btnWinOptSave).padRight(30);
        btnWinOptSave.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                onSaveClicked();
            }
        });

        btnWinOptCancel = new TextButton("Cancel", skinLibgdx);
        tbl.add(btnWinOptCancel);
        btnWinOptCancel.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                onCancelClicked();
            }
        });

        return tbl;
    }

    private void loadSettings() {
        GamePreferences prefs = GamePreferences.instance;
        prefs.load();
        chkSound.setChecked(prefs.sound);
        sldSound.setValue(prefs.volSound);
        chkMusic.setChecked(prefs.music);
        sldMusic.setValue(prefs.volMusic);
        selCharSkin.setSelectedIndex(prefs.charSkin);
        onCharSkinSelected(prefs.charSkin);
        chkShowFpsCounter.setChecked(prefs.showFpsCounter);
        chkUseMonochromeShader.setChecked(prefs.useMonochromeShader);
    }

    private void saveSettings() {
        GamePreferences prefs = GamePreferences.instance;
        prefs.sound = chkSound.isChecked();
        prefs.volSound = sldSound.getValue();
        prefs.music = chkMusic.isChecked();
        prefs.volMusic = sldMusic.getValue();
        prefs.charSkin = selCharSkin.getSelectedIndex();
        prefs.showFpsCounter = chkShowFpsCounter.isChecked();
        prefs.useMonochromeShader = chkUseMonochromeShader.isChecked();
        prefs.save();
    }

    private void onCharSkinSelected(int index) {
        CharacterSkin skin = CharacterSkin.values()[index];
        imgCharSkin.setColor(skin.getColor());
    }
    
    private void onSaveClicked() {
        saveSettings();
        onCancelClicked();
        AudioManager.instance.onSettingUpdated();
    }

    private void onCancelClicked() {
        showMenuButtons(true);
        showOptionsWindow(false, true);
        AudioManager.instance.onSettingUpdated();
    }

    @Override
    public InputProcessor getInputProcessor() {
        return stage;
    }
}
