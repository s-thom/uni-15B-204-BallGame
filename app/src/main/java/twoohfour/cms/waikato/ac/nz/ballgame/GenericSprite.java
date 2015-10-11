package twoohfour.cms.waikato.ac.nz.ballgame;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PointF;
import android.graphics.RectF;

/**
 * Created by timhampton on 25/09/15.
 */
public abstract class GenericSprite {

    //region Variables
    protected RectF _rect;
    protected Paint _paint;

    protected PointF _motion;
    protected float MAX_VELOCITY = 0.2f;
    protected float _weightModifier = 1; // This is 1 / Weight, so it can be multiplied straight into motion calculations
    protected float _friction = 0;
    protected final float FRICTION_CONSTANT = 0.05f;
    //endregion

    public GenericSprite() {
        this(0, 0);
    }

    public GenericSprite(float x, float y) {
        this(x, y, 1, 1);
    }

    public GenericSprite(float x, float y, float weight) {
        this(x, y, 1, 1, weight);
    }

    public GenericSprite(float x, float y, float w, float h) {
        this(x, y, w, h, 0, 0);
    }

    public GenericSprite(float x, float y, float w, float h, float weight) {
        this(x, y, w, h, 0, 0, weight);
    }

    public GenericSprite(float x, float y, float w, float h, float dx, float dy) {
        this(x, y, w, h, dx, dy, 1);
    }

    public GenericSprite(float x, float y, float w, float h, float dx, float dy, float weight) {
        this(x, y, w, h, dx, dy, weight, 1);
    }

    public GenericSprite(float x, float y, float w, float h, float dx, float dy, float weight, float friction) {
        _rect = new RectF(x, y, x + w, y + h);
        _paint = new Paint();
        _paint.setColor(Color.WHITE);
        _friction = friction;

        setWeight(weight);

        _motion = new PointF(dx, dy);
        // Make sure speed is valid
        limitVelocity();
    }

    //region Getters & Setters

    /**
     * Gets the bounding rectangle for this sprite
     * @return Bounding box
     */
    public RectF getRectangle() {
        return new RectF(_rect);
    }

    /**
     * Gets the X position of the top-left corner
     * @return X position
     */
    public float getXPos() {
        return _rect.left;
    }

    /**
     * Gets the Y position of the top-left corner
     * @return Y Position
     */
    public float getYPos() {
        return _rect.top;
    }

    /**
     * Gets the width of the sprite
     * @return Width
     */
    public float getWidth() {
        return _rect.width();
    }

    /**
     * Gets the height of the sprite
     * @return Height
     */
    public float getHeight() {
        return _rect.height();
    }

    /**
     * Gets the motion of the sprite
     * @return PointF containing motion values
     */
    public PointF getMotion() {
        return _motion;
    }

    /**
     * Sets the bounding rectangle of the sprite
     * @param r New bounding box
     */
    public void setRectangle(RectF r) {
        _rect = r;
    }

    /**
     * Set the paint of this sprite
     * May cause strange behaviour if used
     * with Images
     * @param colorCode Integer representing the color
     */
    public void setPaint(int colorCode) {
        _paint.setColor(colorCode);
    }

    /**
     * Sets the x position of the top-left corner
     * @param xPos X position
     */
    public void setXPos(float xPos) {
        float oldWidth = _rect.width();
        _rect.left = xPos;
        setWidth(oldWidth);
    }

    /**
     * Sets the y position of the top-left corner
     * @param yPos Y position
     */
    public void setYPos(float yPos) {
        float oldHeight = _rect.height();
        _rect.top = yPos;
        setHeight(oldHeight);
    }

    /**
     * Sets the width of the sprite
     * @param width Width
     */
    public void setWidth(float width) {
        _rect.right = _rect.left + width;
    }

    /**
     * Sets the height of the sprite
     * @param height Height
     */
    public void setHeight(float height) {
        _rect.bottom = _rect.top + height;
    }

    /**
     * Sets the weight of this sprite
     * @param weight Weight
     */
    public void setWeight(float weight) {
        _weightModifier = 1 / weight;
    }

    /**
     * Sets the motion of the sprite
     * @param dx Change in x
     * @param dy Change in y
     */
    public void setMotion(float dx, float dy) {
        _motion = new PointF(dx * _weightModifier, dy * _weightModifier);

        // Make sure we're withing the speed limit
        limitVelocity();
    }

    /**
     * Add the specified values to the motion
     * @param dx Change in x
     * @param dy Change in y
     */
    public void addMotion(float dx, float dy) {
        _motion.x += dx * _weightModifier;
        _motion.y += dy * _weightModifier;

        // Make sure we're not going too fast
        limitVelocity();
    }

    /**
     * Moves the sprite in the X direction
     * @param x Change in X position
     */
    public void moveX(float x) {
        _rect.left += x;
        _rect.right += x;
    }

    /**
     * Moves the sprite in the Y direction
     * @param y Change in Y position
     */
    public void moveY(float y) {
        _rect.top += y;
        _rect.bottom += y;
    }
    //endregion

    /**
     * Ensures the current velocity is within the limits
     * Prevents the 'super-fast diagonal' bug where
     * the max speed is sqrt(2) * limit
     */
    protected void limitVelocity() {
        // If the current velocity is too great
        float currVelocity = (float)Math.sqrt(Math.pow(Math.abs(_motion.x), 2) + Math.pow(Math.abs(_motion.y), 2));
        if (currVelocity > MAX_VELOCITY){
            float scaleF = Math.abs(MAX_VELOCITY /currVelocity);
            _motion.x *= scaleF;
            _motion.y *= scaleF;
        }
    }

    /**
     * Slow down the sprite according to friction
     */
    protected void doFriction() {
        if (_friction == 0)
            return;

        float currVelocity = (float)Math.sqrt(Math.pow(Math.abs(_motion.x), 2) + Math.pow(Math.abs(_motion.y), 2));
        float frictionFactor = (_friction * FRICTION_CONSTANT) * currVelocity * (1 / _weightModifier);
        float scaleF = 1 - frictionFactor;
        _motion.x *= scaleF;
        _motion.y *= scaleF;
    }

    /**
     * Whether the sprite is collided with the given sprite
     * @param sprite Sprite to check collision with
     * @return Is collided?
     */
    public boolean isCollidedWith(GenericSprite sprite){
        PointF sprMotion = sprite.getMotion();
        RectF sprRect = sprite.getRectangle();

        // Do a simple rectangle intersection check
        if (RectF.intersects(_rect, sprRect) && !_rect.contains(sprRect))
            // Ensure sprite is not heading away from this
            if ((sprMotion.x < 0 && _rect.left < sprRect.right) ||
                    (sprMotion.x > 0 && _rect.right > sprRect.left) ||
                    (sprMotion.y < 0 && _rect.top < sprRect.bottom) ||
                    (sprMotion.y > 0 && _rect.bottom > sprRect.top))
                return true;
            else
                return false;
        else
            return false;


    }

    /**
     * Draw the sprite at the given scale
     * @param canvas Canvas to draw on
     * @param scale Scale at which to draw
     */
    public abstract void draw(Canvas canvas, float scale, PointF offset);

    /**
     * Update the sprite
     * @param state Current state of the game
     */
    public void update(GameState state) {
        moveX(_motion.x);
        moveY(_motion.y);
        doFriction();
    }

}
