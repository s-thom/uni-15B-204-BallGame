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
public class PlayerSprite extends CircleSprite implements ICollides {

    public PlayerSprite(float x, float y) {
        this(x, y, 1);
    }

    public PlayerSprite(float x, float y, float weight) {
        super(x, y, 0.5f, 0.5f, weight);
        _paint.setColor(Color.RED);
        _friction = 1;
    }


    /**
     * Draw the player
     * @param canvas Canvas to draw on
     * @param scale Scale at which to draw
     */
    @Override
    public void draw(Canvas canvas, float scale, PointF offset) {
        float radius = getWidth() / 2;
        canvas.drawCircle((getXPos() + radius + offset.x) * scale, (getYPos() + radius + offset.y) * scale, radius * scale, _paint);
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
}
