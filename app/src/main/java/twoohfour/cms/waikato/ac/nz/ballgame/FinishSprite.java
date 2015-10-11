package twoohfour.cms.waikato.ac.nz.ballgame;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.PointF;
import android.graphics.RectF;

/**
 * Created by Stuart on 30/09/2015.
 */
public class FinishSprite extends GenericSprite {

    public FinishSprite(float xPos, float yPos) {
        super(xPos, yPos);
        _paint.setColor(Color.rgb(35, 210, 75));
    }

    public FinishSprite(float xPos, float yPos, float width, float height) {
        super(xPos, yPos, width, height);
        _paint.setColor(Color.rgb(35, 210, 75));
    }

    @Override
    public void draw(Canvas canvas, float scale, PointF offset) {

        RectF r = new RectF((_rect.left + offset.x) * scale, (_rect.top + offset.y) * scale, (_rect.right + offset.x) * scale, (_rect.bottom + offset.y) * scale);
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
