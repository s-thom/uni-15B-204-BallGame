package twoohfour.cms.waikato.ac.nz.ballgame;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.PointF;

/**
 * Created by Stuart on 11/10/2015.
 */
public class StickyWallSprite extends WallSprite {

    /**
     * Construct the wall obsticals
     * @param leftPos How far away are we from the left wall
     * @param topPos How far down from the top wall
     * @param width Width of my Wall
     * @param height Height of Wall
     */
    public StickyWallSprite(float leftPos, float topPos, float width, float height) {
        super(leftPos, topPos, width, height);
        _border.setColor(Color.GREEN);
    }

    @Override
    public float getBounciness() {
        return 0;
    }
}
