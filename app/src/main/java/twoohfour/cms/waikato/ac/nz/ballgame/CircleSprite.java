package twoohfour.cms.waikato.ac.nz.ballgame;

import android.graphics.PointF;
import android.graphics.RectF;

/**
 * Created by Stuart on 18/10/2015.
 */
public abstract class CircleSprite extends GenericSprite {

    public CircleSprite() {
        super(0, 0);
    }

    public CircleSprite(float x, float y) {
        super(x, y, 1, 1);
    }

    public CircleSprite(float x, float y, float weight) {
        super(x, y, 1, 1, weight);
    }

    public CircleSprite(float x, float y, float w, float h) {
        super(x, y, w, h, 0, 0);
    }

    public CircleSprite(float x, float y, float w, float h, float weight) {
        super(x, y, w, h, 0, 0, weight);
    }

    public CircleSprite(float x, float y, float w, float h, float dx, float dy) {
        super(x, y, w, h, dx, dy, 1);
    }

    public CircleSprite(float x, float y, float w, float h, float dx, float dy, float weight) {
        super(x, y, w, h, dx, dy, weight, 0);
    }

    public CircleSprite(float x, float y, float w, float h, float dx, float dy, float weight, float friction) {
        super(x, y, w, h, dx, dy, weight, friction);
    }

    @Override
    public boolean intersects(GenericSprite sprite) {
        float myRad = _rect.height() / 2;

        if (sprite instanceof RectSprite){

            return sprite.getRectangle().intersects(_rect.left, _rect.top, _rect.right, _rect.bottom);

//            RectF otherRect = sprite.getRectangle();
//
//            if (_rect.right + myRad < otherRect.left)
//                return false;
//            if (_rect.left - myRad > otherRect.right)
//                return false;
//            if (_rect.bottom + myRad < otherRect.top)
//                return false;
//            if (_rect.top - myRad > otherRect.bottom)
//                return false;
//
//            return true;
        } else if (sprite instanceof CircleSprite){
            RectF otherRect = sprite.getRectangle();
            float otherRad = otherRect.height() / 2;

            float diff = (float)Math.abs(Math.sqrt(Math.pow(_rect.centerX() - otherRect.centerX(),2) + Math.pow(_rect.centerY() - otherRect.centerY(),2)));

            return Math.abs(myRad - otherRad) > diff;
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
