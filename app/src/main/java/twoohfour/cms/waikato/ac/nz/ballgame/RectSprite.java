package twoohfour.cms.waikato.ac.nz.ballgame;

import android.graphics.PointF;
import android.graphics.RectF;

/**
 * Created by Stuart on 18/10/2015.
 */
public abstract class RectSprite extends GenericSprite {

    public RectSprite() {
        super(0, 0);
    }

    public RectSprite(float x, float y) {
        super(x, y, 1, 1);
    }

    public RectSprite(float x, float y, float weight) {
        super(x, y, 1, 1, weight);
    }

    public RectSprite(float x, float y, float w, float h) {
        super(x, y, w, h, 0, 0);
    }

    public RectSprite(float x, float y, float w, float h, float weight) {
        super(x, y, w, h, 0, 0, weight);
    }

    public RectSprite(float x, float y, float w, float h, float dx, float dy) {
        super(x, y, w, h, dx, dy, 1);
    }

    public RectSprite(float x, float y, float w, float h, float dx, float dy, float weight) {
        super(x, y, w, h, dx, dy, weight, 0);
    }

    public RectSprite(float x, float y, float w, float h, float dx, float dy, float weight, float friction) {
        super(x, y, w, h, dx, dy, weight, friction);
    }

    @Override
    public boolean intersects(GenericSprite sprite) {
        if (sprite instanceof RectSprite)
            return sprite.getRectangle().intersects(_rect.left, _rect.top, _rect.right, _rect.bottom);
        else if (sprite instanceof CircleSprite){

            return sprite.getRectangle().intersects(_rect.left, _rect.top, _rect.right, _rect.bottom);

//            RectF otherRect = sprite.getRectangle();
//            float rad = otherRect.height() / 2;
//
//            if (otherRect.right + rad < _rect.left)
//                return false;
//            if (otherRect.left - rad > _rect.right)
//                return false;
//            if (otherRect.bottom + rad < _rect.top)
//                return false;
//            if (otherRect.top - rad > _rect.bottom)
//                return false;
//
//            return true;
        } else
            throw new IllegalArgumentException("Sprites must extend a shaped sprite");
    }

    @Override
    public void reflect(GenericSprite sprite) {
        float bounceStrength = getBounciness() * sprite.getBounciness();

        if (sprite instanceof RectSprite || sprite instanceof CircleSprite){
            RectF othrRect = sprite.getRectangle();
            PointF othrMotion = sprite.getMotion();

            boolean bounceX = ((othrRect.right > _rect.left && othrRect.left < _rect.left &&  othrMotion.x > 0) || (othrRect.left < _rect.right && othrRect.right > _rect.right && othrMotion.x < 0));
            boolean bounceY = ((othrRect.bottom > _rect.top && othrRect.top < _rect.top && othrMotion.y > 0) || (othrRect.top < _rect.bottom && othrRect.bottom > _rect.bottom && othrMotion.y < 0));

            // Each check here makes sure the player sprite is intersecting the right part of the given sprite
            // This is a heck of a lot easier than the previous way I was doing it.
            if (bounceX && bounceY) {
                othrMotion.y *= -0.5f * bounceStrength;
                othrMotion.x *= -0.5f * bounceStrength;
            } else if (bounceY)
                othrMotion.y *= -1 * bounceStrength;
            else if (bounceX)
                othrMotion.x *= -1 * bounceStrength;
        } else
            throw new IllegalArgumentException("Sprites must extend a shaped sprite");
    }
}
