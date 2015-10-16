package twoohfour.cms.waikato.ac.nz.ballgame;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PointF;
import android.graphics.RectF;

/**
 * Created by Stuart on 11/10/2015.
 */
public class DeathSprite extends GenericSprite {
    /**
     * Construct a DEATH sprite
     * @param leftPos How far away are we from the left wall
     * @param topPos How far down from the top wall
     * @param width Width of my Wall
     * @param height Height of Wall
     */
    public DeathSprite(float leftPos, float topPos, float width, float height) {
        super(leftPos, topPos, width, height);
        _paint.setColor(Color.RED);
    }


    /**
     * Draw the wall
     * @param canvas Canvas to draw on
     * @param scale Scale at which to draw
     */
    @Override
    public void draw(Canvas canvas, float scale, PointF offset) {
        RectF r = new RectF((_rect.left + offset.x) * scale, (_rect.top + offset.y) * scale, (_rect.right + offset.x) * scale, (_rect.bottom + offset.y) * scale);

        _paint.setStyle(Paint.Style.FILL);
        _paint.setColor(Color.RED);
        canvas.drawRect(r, _paint);

        // border
        _paint.setStyle(Paint.Style.STROKE);
        _paint.setColor(Color.BLACK);
        canvas.drawRect(r, _paint);

    }

    //region IBouncable methods
    @Override
    public boolean isCollidedWith(GenericSprite sprite) {
        return super.isCollidedWith(sprite);
    }


    //endregion
}
