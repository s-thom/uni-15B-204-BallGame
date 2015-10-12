package twoohfour.cms.waikato.ac.nz.ballgame;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.PointF;
import android.graphics.Rect;
import android.graphics.RectF;

/**
 * Created by Stuart on 25/09/2015.
 */
public abstract class ImageSprite extends GenericSprite {

    //region Variables
    Bitmap _img;
    Rect _imgRect;
    //endregion

    public ImageSprite(Bitmap d) {
        this(d, 0, 0);
    }

    public ImageSprite(Bitmap d, float x, float y) {
        this(d, x, y, d.getWidth(), d.getHeight());
    }

    public ImageSprite(Bitmap d, float x, float y, float weight) {
        this(d, x, y, d.getWidth(), d.getHeight(), weight);
    }

    public ImageSprite(Bitmap d, float x, float y, float w, float h) {
        this(d, x, y, w, h, 0, 0);
    }

    public ImageSprite(Bitmap d, float x, float y, float w, float h, float weight) {
        this(d, x, y, w, h, 0, 0, weight);
    }

    public ImageSprite(Bitmap d, float x, float y, float w, float h, float dx, float dy) {
        this(d, x, y, w, h, dx, dy, 1);

        _img = d;
        _imgRect = new Rect(0, 0, d.getWidth(), d.getHeight());
    }

    public ImageSprite(Bitmap d, float x, float y, float w, float h, float dx, float dy, float weight) {
        super(x, y, w, h, dx, dy, weight);

        _img = d;
        _imgRect = new Rect(0, 0, d.getWidth(), d.getHeight());
    }

    //region Getters & Setters

    /**
     * Get the image used by this sprite
     * @return Image
     */
    public Bitmap getBitmap() {
        return _img;
    }

    /**
     * Set the source rectangle for this sprite
     * @param r New image source rectangle
     */
    public void setImgSrcRect(Rect r) {
        _imgRect = r;
    }
    //endregion

    /**
     * Draw the image sprite
     * @param canvas Canvas to draw on
     * @param scale Scale at which to draw
     */
    @Override
    public void draw(Canvas canvas, float scale, PointF offset) {
        RectF r = new RectF((_rect.left + offset.x) * scale, (_rect.top + offset.y) * scale, (_rect.right + offset.x) * scale, (_rect.bottom + offset.y) * scale);
        canvas.drawBitmap(_img, _imgRect, r, _paint);
    }
}
