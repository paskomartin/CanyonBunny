package com.example.canyonbunny.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.Array;
import com.example.canyonbunny.game.objects.AbstractGameObject;
import com.example.canyonbunny.game.objects.Clouds;
import com.example.canyonbunny.game.objects.Mountains;
import com.example.canyonbunny.game.objects.Rock;
import com.example.canyonbunny.game.objects.WaterOverlay;

public class Level {
    public static final String TAG = Level.class.getName();

    public enum BLOCK_TYPE {
        EMPTY(0, 0, 0),                     // BLACK
        ROCK(0, 255, 0),                    // GREEN
        PLAYER_SPAWNPOINT(255, 255, 255),   // WHITE
        ITEM_FEATHER(255, 0, 255),          // PURPLE
        ITEM_GOLD_COID(255,255, 0);         // YELLOW

        private int color;

        private BLOCK_TYPE(int r, int g, int b) {
            color = r << 24 | g << 16 | b << 8 | 0xFF;
        }

        public boolean sameColor(int color) {
            return this.color == color;
        }

        public int getColor() {
            return color;
        }
    }

    // objects
    public Array<Rock> rocks;

    // decorations
    public Clouds clouds;
    public Mountains mountains;
    public WaterOverlay waterOverlay;

    public Level(String filename) {
        init(filename);
    }

    private void init(String filename) {
        // objects
        rocks = new Array<Rock>();

        // load image file that represents the level data
        Pixmap pixmap = new Pixmap(Gdx.files.internal(filename));

        // scan pixels from top-left to bottom-right;
        int lastPixel = -1;

        for (int pixelY = 0; pixelY < pixmap.getHeight(); ++pixelY) {
            for (int pixelX = 0; pixelX < pixmap.getWidth(); ++pixelX) {
                AbstractGameObject obj = null;
                float offsetHeight = 0;
                // height grows from bottom to top
                float baseHeight = pixmap.getHeight() - pixelY;
                // get color of current pixel as 32-bit RGBA value
                int currentPixel = pixmap.getPixel(pixelX, pixelY);
                // find matching clor value to identity block type at (x, y)
                // point and create the corresponding game object if there is a match

                // empty space
                if (BLOCK_TYPE.EMPTY.sameColor(currentPixel)) {
                    // nothing to do
                }
                // rock
                else if(BLOCK_TYPE.ROCK.sameColor(currentPixel)) {
                    if (lastPixel != currentPixel) {
                        obj = new Rock();
                        float heightIncreaseFactor = 0.25f;
                        offsetHeight = -2.5f;
                        obj.position.set(pixelX, baseHeight * obj.dimension.y * heightIncreaseFactor + offsetHeight);
                        rocks.add((Rock) obj);
                    }
                    else {
                        rocks.get(rocks.size - 1).increaseLength(1);
                    }
                }
                // player spawn point
                else if (BLOCK_TYPE.PLAYER_SPAWNPOINT.sameColor(currentPixel)) {

                }
                else if (BLOCK_TYPE.ITEM_FEATHER.sameColor(currentPixel)) {

                }
                else if (BLOCK_TYPE.ITEM_GOLD_COID.sameColor(currentPixel)) {

                }
                // unknow object/pixel color
                else {
                    int r = 255 & (currentPixel >>> 24);
                    int g = 255 & (currentPixel >>> 16);
                    int b = 255 & (currentPixel >>> 8);
                    int a = 255 & currentPixel;
                    Gdx.app.error(TAG, "Unknown object at x<" + pixelX + "> y<" + pixelX +
                    ">: r<" + r + "> g<" + g + "> b<" + b + "> a<" + a + ">" );
                }
                lastPixel = currentPixel;
            }
        }

        // decoration
        int pixmapWidth = pixmap.getWidth();
        clouds = new Clouds(pixmapWidth);
        clouds.position.set(0, 2);
        mountains = new Mountains(pixmapWidth);
        mountains.position.set(-1, -1);
        waterOverlay = new WaterOverlay(pixmapWidth);
        waterOverlay.position.set(0, -3.75f);

        // free memory
        pixmap.dispose();
        Gdx.app.debug(TAG, "Level '" + filename + "' loaded");
    }


    public void render(SpriteBatch batch) {
        mountains.render(batch);
        for (Rock rock : rocks) {
            rock.render(batch);
        }

        waterOverlay.render(batch);

        clouds.render(batch);
    }
}
