package twoohfour.cms.waikato.ac.nz.ballgame;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PointF;
import android.graphics.RectF;

/**
 * Created by Tim Hampton on 25/09/2015.
 */
public class WallSprite extends GenericSprite implements IBouncable {



    /**
     * Construct the wall obsticals
     * @param leftPos How far away are we from the left wall
     * @param topPos How far down from the top wall
     * @param width Width of my Wall
     * @param height Height of Wall
     */
    public WallSprite(int leftPos, int topPos, int width, int height) {
        super(leftPos, topPos, width, height);
        _paint.setColor(Color.MAGENTA);

    }


    /**
     * Draw the wall
     * @param canvas Canvas to draw on
     * @param scale Scale at which to draw
     */
    @Override
    public void draw(Canvas canvas, float scale) {
        RectF r = new RectF(_rect.left * scale, _rect.top * scale, _rect.right * scale, _rect.bottom * scale);

        _paint.setStyle(Paint.Style.FILL);
        _paint.setColor(Color.DKGRAY);
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

    @Override
    public float getBounciness() {
        return 1;
    }

    @Override
    public void bounceFrom(GenericSprite sprite) {

        RectF othrRect = sprite.getRectangle();
        PointF othrMotion = sprite.getMotion();

        // Each check here makes sure the player sprite is intersecting the right part of the given sprite
        // This is a heck of a lot easier than the previous way I was doing it.
        if ((othrRect.bottom > _rect.top && othrRect.top < _rect.top && othrMotion.y > 0) || (othrRect.top < _rect.bottom && othrRect.bottom > _rect.bottom && othrMotion.y < 0))
            othrMotion.y *= -1 * getBounciness();
        if ((othrRect.right > _rect.left && othrRect.left < _rect.left &&  othrMotion.x > 0) || (othrRect.left < _rect.right && othrRect.right > _rect.right && othrMotion.x < 0))
            othrMotion.x *= -1 * getBounciness();
    }

    //endregion
}
