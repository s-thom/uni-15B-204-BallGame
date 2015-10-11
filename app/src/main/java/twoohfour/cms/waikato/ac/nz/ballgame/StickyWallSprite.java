package twoohfour.cms.waikato.ac.nz.ballgame;

import android.graphics.Color;

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
    public StickyWallSprite(int leftPos, int topPos, int width, int height) {
        super(leftPos, topPos, width, height);
        _paint.setColor(Color.MAGENTA);
    }

    @Override
    public float getBounciness() {
        return 0;
    }
}
