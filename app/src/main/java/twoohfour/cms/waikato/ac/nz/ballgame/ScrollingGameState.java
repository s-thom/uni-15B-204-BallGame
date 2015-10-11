package twoohfour.cms.waikato.ac.nz.ballgame;

import android.graphics.Point;
import android.graphics.PointF;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Stuart on 11/10/2015.
 */
public class ScrollingGameState extends GameState {

    protected float _speed;

    /**
     * Class used to pass values between drawing and updating classes
     */
    public ScrollingGameState(String title, Point levelSize, PointF playerPosition) {
        this(title, levelSize, playerPosition, new ArrayList<GenericSprite>());
    }

    /**
     * Class used to pass values between drawing and updating classes
     */
    public ScrollingGameState(String title, Point levelSize, PointF playerPosition, List<GenericSprite> sprites) {
        this(title, levelSize, playerPosition, new ArrayList<GenericSprite>(), -0.01f);
    }

    /**
     * Class used to pass values between drawing and updating classes
     */
    public ScrollingGameState(String title, Point levelSize, PointF playerPosition, List<GenericSprite> sprites, float speed) {
        super(title, levelSize, playerPosition, sprites);
        _speed = speed;
    }

    @Override
    public void update() {
        super.update();
        _offset.x += _speed;
    }
}
