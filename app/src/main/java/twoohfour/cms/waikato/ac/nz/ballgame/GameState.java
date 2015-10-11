package twoohfour.cms.waikato.ac.nz.ballgame;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Point;
import android.graphics.PointF;
import android.graphics.RectF;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Created by Stuart on 25/09/2015.
 */
public class GameState {

    //region Variables
    protected float[] _grav;
    protected List<GenericSprite> _sprites;
    protected DrawableView _view;
    protected int[] _viewSize;
    protected Point _levelSize;
    protected int _score;
    protected PlayerSprite _player;
    protected String _title;
    protected int _ticks;
    protected boolean _isComplete;
    protected PointF _offset;

    public enum Level { Random, Scrolling }
    //endregion

    public final static Random RANDOM = new Random();

    /**
     * Class used to pass values between drawing and updating classes
     */
    public GameState(String title, Point levelSize, PointF playerPosition) {
        this(title, levelSize, playerPosition, new ArrayList<GenericSprite>());
    }

    /**
     * Class used to pass values between drawing and updating classes
     */
    public GameState(String title, Point levelSize, PointF playerPosition, List<GenericSprite> sprites) {
        _grav = new float[3];
        _sprites = sprites; // As it's a reference, external class can still add sprites
        _levelSize = levelSize;
        _player = new PlayerSprite(playerPosition.x, playerPosition.y);
        _sprites.add(_player);
        _title = title;
        _offset = new PointF(0, 0);
    }

    //region Getters & Setters

    /**
     * Gets the title of the level to display
     * @return Title
     */
    public String getTitle() {
        return _title;
    }

    /**
     * Gets the gravity value array
     * @return Gravity values
     */
    public float[] getGravity() {
        return _grav;
    }

    /**
     * Gets the DrawableView associated with this GameState
     * @return
     */
    public DrawableView getView() {
        return _view;
    }

    /**
     * Gets the DrawableView's size as an array
     * @return Size values
     */
    public int[] getViewSize() {
        Log.w("DeprecationWarning", "GameState.getViewSize() will be removed in later versions");
        return _viewSize;
    }

    /**
     * Gets the dimensions of the level
     * @return Level size
     */
    public Point getLevelSize() {
        return _levelSize;
    }

    /**
     * Gets the player sprite in this game
     * @return Player
     */
    public PlayerSprite getPlayer() {
        return _player;
    }

    /**
     * Gets a list of all sprites in the GameState
     * @return List of GenericSprite
     */
    public List<GenericSprite> getSprites() {
        return _sprites;
    }

    /**
     * Gets the score of this game
     * @return
     */
    public int getScore() {
        return _score;
    }

    /**
     * Gets the number of updates the game has had
     * @return Number of ticks
     */
    public int getGameTime() {
        return _ticks;
    }

    /**
     * Sets the values of gravity in the array
     * @param x Acceleration in X direction
     * @param y Acceleration in Y direction
     * @param z Acceleration in Z direction
     */
    public void setGravity(float x, float y, float z) {
        _grav[0] = x;
        _grav[1] = y;
        _grav[2] = z;
    }

    /**
     * Sets the view held by this Game State
     * Because of the method in DrawableView, there's a bit of a circular reference
     * This is intentional behaviour. It tries to make sure
     * that the view used by the GameActivity is the same one that's
     * beign given all this information
     * @param view
     */
    public void setView(DrawableView view) {
        if (view != null)
            _view = view;
        else
            throw new IllegalArgumentException("Given view is set to null");
    }

    /**
     * Sets the reported size of the view
     * @param x Width
     * @param y Height
     */
    public void setViewSize(int x, int y) {
        Log.w("DeprecationWarning", "GameState.setViewSize() will be removed in later versions");
        _viewSize = new int[2];
        _viewSize[0] = x;
        _viewSize[1] = y;
    }

    /**
     * Sets the score to the specified value
     * @param score New score
     */
    public void setScore(int score) {
        _score = score;
    }

    /**
     * Adds the given value onto the score
     * @param score Number to add to the score
     */
    public void addScore(int score) {
        _score += score;
    }
    //endregion

    /**
     * Performs checks to see if the game is ready
     * @return Whether the Game is ready for updates / drawing
     */
    public boolean isReady() {
//        if (_viewSize == null)
//            return false;

        return true;
    }

    /**
     * Performs checks to see if the game has been finished
     * @return Whether the Game is complete
     */
    public boolean isComplete() {
        return _isComplete;
    }

    /**
     * Update loop
     * Handles all updating of the game
     */
    public void update() {
        // Allows us to handle updates separately
        // from drawing if we want to
        // Principle of game design
        // Stops things going wrong when FPS forced to different values
        if (isReady()) {
            _ticks++;

            // Do all processing before updating
            for (GenericSprite s : _sprites) {

                boolean hasBounced = false;

                for (GenericSprite t : _sprites){
                    if (t != s) {
                        if (t instanceof IBouncable && s instanceof ICollides && t.isCollidedWith(s) && !hasBounced) {

                            ((IBouncable) t).bounceFrom(s);

                            hasBounced = true;
                        } else if (t instanceof FinishSprite && s instanceof PlayerSprite) {
                            if (t.isCollidedWith(s)){
                                _isComplete = true;
                                addScore(1);
                            }
                        } else if (t instanceof DeathSprite && s instanceof PlayerSprite) {
                            if (t.isCollidedWith(s)) {
                                _isComplete = true;
                            }
                        }
                    }
                }
            }

            for (GenericSprite s : _sprites) {
                s.update(this);
            }
        }
    }

    public void draw(Canvas canvas, float ratio) {

        // Draw all sprites
        _player.draw(canvas, ratio, _offset);
        for (GenericSprite s : _sprites) {
            s.draw(canvas, ratio, _offset);
        }
    }

    /**
     * Generates a level in the form of a GameState object
     * @param l Level to generate
     * @param c Context. Used to get resources
     * @return A new level
     */
    public static GameState GENERATE(Level l, Context c) {
        if (l == Level.Random) {
            int levelX = GameState.RANDOM.nextInt(10) + 10;
            int levelY = GameState.RANDOM.nextInt(10) + 10;
            // Create simple sprites
            List<GenericSprite> sprites = new ArrayList<GenericSprite>();
            // Adding in our walls etc

            sprites.add(new WallSprite(0, -1, levelX, 1));
            sprites.add(new WallSprite(-1, 0, 1, levelY));
            sprites.add(new WallSprite(0, levelY, levelX, 1));
            sprites.add(new WallSprite(levelX, 0, 1, levelY));

            for (int i=0; i<5; i++) {

                // _state.getViewSize()[1]
                //_state.getViewSize()[0]
                int left = GameState.RANDOM.nextInt(levelX - 1) + 1;
                int top = GameState.RANDOM.nextInt(levelY - 1) + 1;
                int width = GameState.RANDOM.nextInt(levelX - left) + 1;
                int height = GameState.RANDOM.nextInt(levelY - top) + 1;

                WallSprite wallSprite = new WallSprite(left, top , width, height);
                sprites.add(wallSprite);

            }

            for (int i=0; i<5; i++) {

                int left = GameState.RANDOM.nextInt(levelX - 1) + 1;
                int top = GameState.RANDOM.nextInt(levelY - 1) + 1;

                BumperSprite bSprite = new BumperSprite(left, top);
                sprites.add(bSprite);

            }

            sprites.add(new FinishSprite(0, levelY - 1));

            GameState newLevel = new GameState(c.getString(R.string.level_random), new Point(levelX, levelY), new PointF(0, 0), sprites);
            return newLevel;
        } else if (l == Level.Scrolling) {
            List<GenericSprite> sprites = new ArrayList<GenericSprite>();

            DeathSprite ds = new DeathSprite(-0.75f, 0, 1, 5);
            ds.setMotion(0.01f, 0);
            sprites.add(ds);

            StickyWallSprite sws = new StickyWallSprite(4.75f, 0, 1, 5);
            sws.setMotion(0.01f, 0f);
            sprites.add(sws);

            FinishSprite fs = new FinishSprite(10, 0, 1, 5);
            sprites.add(fs);

            sprites.add(new WallSprite(0, -1, 10, 1));
            sprites.add(new WallSprite(0, 5, 10, 1));

            //sprites.add(new WallSprite(1, 1, 3, 3));

            return new ScrollingGameState(c.getString(R.string.level_scrolling), new Point(5, 5), new PointF(2.5f, 2.5f), sprites, -0.01f);
        }


        throw new IllegalArgumentException("Level not defined yet");
    }

    /**
     * Gets the GameState.Level that corresponds to the given string
     * @param name String representation of the level name
     * @return Requested GameState.Level
     */
    public static Level getLevelFromString(String name) {
        switch (name) {
            case "Random":
                return Level.Random;
            case "Scrolling":
                return Level.Scrolling;
            default:
                throw new IllegalArgumentException("Level not defined yet");
        }
    }
}
