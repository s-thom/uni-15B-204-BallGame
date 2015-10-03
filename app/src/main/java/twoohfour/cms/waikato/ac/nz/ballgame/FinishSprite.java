package twoohfour.cms.waikato.ac.nz.ballgame;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.RectF;

/**
 * Created by Stuart on 30/09/2015.
 */
public class FinishSprite extends GenericSprite {

    public FinishSprite(float xPos, float yPos) {
        super(xPos, yPos);
        _paint.setColor(Color.rgb(35, 210, 75));
    }

    @Override
    public void draw(Canvas canvas, float scale) {

        RectF r = new RectF(_rect.left * scale, _rect.top * scale, _rect.right * scale, _rect.bottom * scale);
        canvas.drawRect(r, _paint);
    }

    @Override
    public void update(GameState state) {
        super.update(state);
    }

    @Override
    public boolean isCollidedWith(GenericSprite sprite) {
        return _rect.contains(sprite.getRectangle());
    }
}
