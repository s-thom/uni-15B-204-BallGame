package twoohfour.cms.waikato.ac.nz.ballgame;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Stuart on 25/09/2015.
 */
public class DrawableView extends View {

    //region Variables
    private float _hwRatio = 1.25f; // Height = x * Width
    private float _viewStateRatio = 0f; // Ratio between GameState's layout and rality

    private GameState _state;
    //endregion

    public DrawableView(Context context) {
        super(context);
    }

    public DrawableView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    /**
     * Stores a reference to the game's state
     *
     * @param s State to hold
     */
    public void setState(GameState s) {
        _state = s;
        s.setView(this);

        Point levelSize = s.getLevelSize();
        _hwRatio = (float) levelSize.y / (float) levelSize.x;
    }

    /**
     * Draws all sprites
     *
     * @param canvas Canvas to draw sprites on
     */
    @Override
    protected void onDraw(Canvas canvas) {
        if (_state != null) {
            _state.draw(canvas, _viewStateRatio);
            invalidate(); // Add another draw routine to the event stack

        }

    }

    /**
     * Sets the height / width of the view
     * In this case, makes the height = HEIGHT_WIDTH_RATIO * width
     *
     * @param widthMeasureSpec  Key to get information about the width of this View
     * @param heightMeasureSpec Key to get information about the height of this View
     */
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        //super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        // With a little help from http://stackoverflow.com/questions/12266899/onmeasure-custom-view-explanation

        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int width = MeasureSpec.getSize(widthMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        int height = MeasureSpec.getSize(heightMeasureSpec);

        int newWidth = width;
        int newHeight = height;

        if (width * _hwRatio > height)
            newWidth = (int) (height / _hwRatio);
        else
            newHeight = (int) (width * _hwRatio);

        // Add width constraints
        if (widthMode == MeasureSpec.EXACTLY)
            //Must be this size
            newWidth = width;
        else if (widthMode == MeasureSpec.AT_MOST)
            //Can't be bigger than...
            newWidth = Math.min(newWidth, width);

        // Add height constraints
        if (heightMode == MeasureSpec.EXACTLY)
            //Must be this size
            newHeight = height;
        else if (heightMode == MeasureSpec.AT_MOST)
            //Can't be bigger than...
            newHeight = Math.min(newHeight, height);


        Point levelSize = _state.getLevelSize();
        _viewStateRatio = (float) newWidth / (float) levelSize.x;
        //_state.setViewSize(newWidth, newHeight);
        setMeasuredDimension(newWidth, newHeight);
    }
}