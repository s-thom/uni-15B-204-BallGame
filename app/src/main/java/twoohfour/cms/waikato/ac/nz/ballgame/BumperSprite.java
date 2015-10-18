package twoohfour.cms.waikato.ac.nz.ballgame;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.PointF;

/**
 * Created by Stuart on 30/09/2015.
 */
public class BumperSprite extends CircleSprite implements ICollidable {

    private int _cooldown = 0;

    /**
     * Construct a bouncy sprite
     * @param leftPos How far away are we from the left wall
     * @param topPos How far down from the top wall
     */
    public BumperSprite(int leftPos, int topPos) {
        super(leftPos, topPos, 0.5f, 0.5f);
    }

    @Override
    public void draw(Canvas canvas, float scale, PointF offset) {
        float radius = getWidth() / 2;

        if (_cooldown > 0)
            _paint.setColor(Color.rgb(170, 50, 150));
        else
            _paint.setColor(Color.rgb(140, 20, 115));

        canvas.drawCircle((getXPos() + radius + offset.x) * scale, (getYPos() + radius + offset.y) * scale, radius * scale, _paint);
    }

    @Override
    public void update(GameState state) {

        if (_cooldown > 0)
            _cooldown--;

        super.update(state);
    }

    @Override
    public void reflect(GenericSprite sprite) {
        PointF sprMotion = sprite.getMotion();

        float oldSpeed = (float)Math.sqrt(Math.pow(sprMotion.x, 2) + Math.pow(sprMotion.y, 2));

        float newX = (GameState.RANDOM.nextFloat() * 2) - 1;
        float newY = (GameState.RANDOM.nextFloat() * 2) - 1;

        float tempSpeed = (float)Math.sqrt(Math.pow(newX, 2) + Math.pow(newY, 2));
        float ratio = oldSpeed / tempSpeed;

        newX *= ratio * getBounciness();
        newY *= ratio * getBounciness();

        sprite.setMotion(newX, newY);

        _cooldown = 30;
    }
}
