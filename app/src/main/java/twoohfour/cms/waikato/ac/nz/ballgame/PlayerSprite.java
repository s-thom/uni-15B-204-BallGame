package twoohfour.cms.waikato.ac.nz.ballgame;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.PointF;
import android.graphics.RectF;
import android.util.Log;

/**
 * Created by Stuart on 25/09/2015.
 */
public class PlayerSprite extends GenericSprite {

    public PlayerSprite(float x, float y) {
        this(x, y, 1);
    }

    public PlayerSprite(float x, float y, float weight) {
        super(x, y, 0.5f, 0.5f, weight);
        _paint.setColor(Color.RED);
    }


    /**
     * Draw the player
     * @param canvas Canvas to draw on
     * @param scale Scale at which to draw
     */
    @Override
    public void draw(Canvas canvas, float scale) {
        float radius = getWidth() / 2;
        canvas.drawCircle((getXPos() + radius) * scale, (getYPos() + radius) * scale, radius * scale, _paint);
    }

    /**
     * Update the player
     * @param state Current state of the game
     */
    @Override
    public void update(GameState state) {
        // Update the player's motion
        addMotion(state.getGravity()[0], state.getGravity()[1]);

        super.update(state);
    }

    @Override
    public boolean isCollidedWith(GenericSprite sprite) {
        return super.isCollidedWith(sprite);
    }
}
