package twoohfour.cms.waikato.ac.nz.ballgame;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewStub;
import android.widget.GridLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.UnknownHostException;
import java.util.Collections;
import java.util.Hashtable;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class GameActivity extends AppCompatActivity implements SensorEventListener {

    //region Variables

    // Intent extras
    public static final String EXTRA_LEVEL = "nz.ac.waikato.cms.twohofour.ballgame.LEVEL";
    public static final String EXTRA_SCORE = "nz.ac.waikato.cms.twohofour.ballgame.SCORE";

    // Misc. constants
    private final int UPDATES_PER_SECOND = 30;

    // Sensors
    private final float MAX_GRAVITY = 4.5f; // Tilting the device past this point will have no effect
    private final float SENSOR_THRESHOLD = 0.2f;
    private final float SPEED = 0.001f; // A constant to affect acceleration
    private float sensitivity;
    private Sensor accel;
    private SensorManager sensorManager;

    // Debug features
    private boolean debug = false;
    private boolean debugButtons = false;

    // Ready states
    private boolean _amReady = false;
    private boolean _othersReady = true;

    // Multiplayer
    private boolean _isMp;
    private MultiplayerNetwork _network;

    // Miscellaneous objects
    private DrawableView _view;
    private GameState _state;
    private Hashtable _otherPlayers;
    private NetThread _netThread;
    private Timer _updateTimer;

    //endregion

    //region Activity override methods

    /**
     * Initialises the GameActivity
     *
     * @param savedInstanceState Saved state
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // Create, and load layout
        super.onCreate(savedInstanceState);

        retrievePreferences();

        // Get the accelerometer
        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        accel = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);

        // Try hide the action bar
        try {
            getSupportActionBar().hide();
        } catch (NullPointerException ex) {
            Log.e("GameActivity", "Couldn't hide action bar");
        }
    }

    /**
     * Called when the activity is shown again
     */
    @Override
    protected void onResume() {
        super.onResume();

        // Register the accelerometer
        sensorManager.registerListener(this, accel, SensorManager.SENSOR_DELAY_GAME);

        retrievePreferences();

        // String is the ip associated with that player
        _otherPlayers = new Hashtable<String, MultiPlayerGhostSprite>();

        // Set layouts
        setContentView(R.layout.activity_game);


        _view = (DrawableView) findViewById(R.id.draw_view);

        if (_isMp) {
            // Make an empty level for the 'lobby'
            _state = GameState.GENERATE(GameState.Level.Empty, this);
        } else {
            // Start level straight away
            startLevel();
        }
        _view.setState(_state);

        // Debug options
        // Expand views if needed
        if (debug) {
            ViewStub s = (ViewStub) findViewById(R.id.debug_stub);
            LinearLayout debugLayout = (LinearLayout) s.inflate();

            if (debugButtons) {
                ViewStub vs = (ViewStub) findViewById(R.id.debug_buttons_stub);
                GridLayout debugButtonsLayout = (GridLayout) vs.inflate();
            }
        }

        // Set title
        TextView levelLabel = (TextView) findViewById(R.id.level_name);
        levelLabel.setText(_state.getTitle());

        // Start update loop
        _updateTimer = new Timer("Update");
        _updateTimer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                doUpdate();
            }
        }, 0, 1000 / UPDATES_PER_SECOND);

        // Start network
        // Yes, this still happens in 'singleplayer'
        // Yep, singleplayer isn't actually singleplayer
        // It just bypasses a bit of waiting
        _netThread = new NetThread();
        _netThread.start();
    }

    /**
     * Callen when the activity is hidden
     */
    @Override
    protected void onPause() {
        super.onPause();

        // Unregister accelerometer
        sensorManager.unregisterListener(this);

        // STOP EVERYTHING
        _network.unregisterListener(_netThread);
        _updateTimer.cancel();
        _netThread.stopTimer();
        _netThread = null;

        // Tell network you're leaving
        // Can't be done on UI thread
        Thread quick = new Thread(new Runnable() {
            @Override
            public void run() {
                _network.sendCode(104, "");
            }
        });
        quick.start();

        // Stop the activity
        finish();
    }


    //endregion

    /**
     * Gets the sensitivity value from preferences
     */
    private void retrievePreferences() {
        // Get sensitivity value
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        String sensPref = sharedPref.getString(getResources().getString(R.string.key_pref_sensitivity), getResources().getString(R.string.pref_sensitivity_default_value));

        // Get multiplayer preference
        _isMp = sharedPref.getBoolean(getResources().getString(R.string.key_pref_mp), true);

        // Get debug preferences
        debug = sharedPref.getBoolean(getResources().getString(R.string.key_pref_debug), false);
        if (debug)
            debugButtons = sharedPref.getBoolean(getResources().getString(R.string.key_pref_debug_buttons), false);

        // Parse sensitivity preference
        try {
            sensitivity = Float.parseFloat(sensPref);
        } catch (NumberFormatException ex2) {
            Log.e("GameActivity", "Unable to load sensitivity default");
            sensitivity = 1f; // It's *maaaggiiiiicccc*
        }
    }

    //region Main code

    /**
     * Update loop
     * Handles all updating of the game
     */
    private void doUpdate() {
        // Allows us to handle updates separately
        // from drawing if we want to
        // Principle of game design
        // Stops things going wrong when FPS forced to different values

        if (_amReady && _othersReady) {

            // Any UI changes must be done on the original thread
            // Timer make their own, so we have to do this
            this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    startLevel();
                }
            });
        }

        // Ensure that we have the lock on the state
        synchronized (_state) {
            _state.update();
            GameState.State _mode = _state.getState();

            if (_mode == GameState.State.Spectating) {
                Intent i = new Intent();
                i.putExtra(EXTRA_SCORE, _state.getScore());

                setResult(RESULT_OK, i);
                finish();
            }
        }
    }

    public void startLevel() {

        // Get a GameState.Level from intent system
        GameState.Level levelNum = (GameState.Level) getIntent().getSerializableExtra(EXTRA_LEVEL);
        if (levelNum == null)
            levelNum = GameState.Level.Random;
        // Save state
        _state = GameState.GENERATE(levelNum, this);

        List<GenericSprite> spriteList = _state.getSprites();
        for (Object s : _otherPlayers.values()) {
            spriteList.add((GenericSprite) s);
        }

        _view.setState(_state);

        _amReady = false;// Forces this condition to only be true once
        //Force recalcualtion of view positions
        _view.setVisibility(View.GONE);
        _view.setVisibility(View.VISIBLE);

        findViewById(R.id.buttonStartGame).setVisibility(View.GONE);

        ((TextView) findViewById(R.id.level_name)).setText(_state.getTitle());
    }


    //endregion

    //region Debug Handlers
    public void onGravButtonClicked(View button) {
        float gravChangeFactor = 0.05f;

        int id = button.getId();

        float[] currGrav;
        synchronized (_state) {
            currGrav = _state.getGravity();
        }

        if (id == R.id.button_grav_up)
            currGrav[1] -= gravChangeFactor;
        else if (id == R.id.button_grav_down)
            currGrav[1] += gravChangeFactor;
        else if (id == R.id.button_grav_left)
            currGrav[0] -= gravChangeFactor;
        else if (id == R.id.button_grav_right)
            currGrav[0] += gravChangeFactor;

        synchronized (_state) {
            _state.setGravity(currGrav[0], currGrav[1], currGrav[2]);
        }
    }
    //endregion

    //region Sensor handlers

    /**
     * Accelerometer changed
     *
     * @param sensorEvent Event fired
     */
    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        // Make sure we are are actually getting stuff from the gyro
        if (sensorEvent.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            // Calculate gravity values
            // Affected by sensitivity
            // TODO: 'zero'ing of sensor

            float gravX;
            float gravY;
            float gravZ;

            // Prevent sensor changing things if buttons are in use
            if (!debugButtons) {
                gravX = sensorEvent.values[0];
                gravY = sensorEvent.values[1];
                gravZ = sensorEvent.values[2];

                // Limit gravity values
                if (Math.abs(gravX) > MAX_GRAVITY)
                    gravX = MAX_GRAVITY * (gravX / Math.abs(gravX)); // `x / abs(x) == +/-1` depending on original sign
                else if (Math.abs(gravX) < SENSOR_THRESHOLD)
                    gravX = 0;
                if (Math.abs(gravY) > MAX_GRAVITY)
                    gravY = MAX_GRAVITY * (gravY / Math.abs(gravY));
                else if (Math.abs(gravY) < SENSOR_THRESHOLD)
                    gravY = 0;
                if (Math.abs(gravZ) > MAX_GRAVITY)
                    gravZ = MAX_GRAVITY * (gravZ / Math.abs(gravZ));
                else if (Math.abs(gravZ) < SENSOR_THRESHOLD)
                    gravZ = 0;

                // Factor in multipliers
                gravX = gravX * SPEED * sensitivity * -1; // For whatever reason, the x needs to be flipped.
                gravY = gravY * SPEED * sensitivity;
                gravZ = gravZ * SPEED * sensitivity;
            } else {
                float[] gravValues;
                synchronized (_state) {
                    gravValues = _state.getGravity();
                }

                gravX = gravValues[0];
                gravY = gravValues[1];
                gravZ = gravValues[2];
            }

            synchronized (_state) {
                // Store gravity values
                _state.setGravity(gravX, gravY, gravZ);
            }


            if (debug) {
                // Get text views
                TextView x = (TextView) findViewById(R.id.x);
                TextView y = (TextView) findViewById(R.id.y);
                TextView z = (TextView) findViewById(R.id.z);
                // Set text
                x.setText("x: " + gravX);
                y.setText("y: " + gravY);
                z.setText("z: " + gravZ);


                // Show payer position on screen
                // Get text views
                TextView px = (TextView) findViewById(R.id.player_x);
                TextView py = (TextView) findViewById(R.id.player_y);
                TextView pdx = (TextView) findViewById(R.id.player_dx);
                TextView pdy = (TextView) findViewById(R.id.player_dy);
                // Set text
                synchronized (_state) {
                    PlayerSprite player = _state.getPlayer();
                    px.setText("x: " + player.getXPos());
                    py.setText("y: " + player.getYPos());
                    pdx.setText("dx: " + player.getMotion().x);
                    pdy.setText("dy: " + player.getMotion().y);
                }
            }
        }
    }


    /**
     * Sensor accuracy changed
     * Currently unused
     *
     * @param sensor ID of sensor that changed
     * @param i      New accuracy value
     */
    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }
    //endregion

    public void onStartGameButton(View button) {
        button.setEnabled(false);
        _amReady = true;

        // Network must be done off the UI thread
        Thread quick = new Thread(new Runnable() {
            @Override
            public void run() {
                _network.sendCode(103, "");
            }
        });

        quick.start();
    }

    /**
     * Threaded object that takes care of the sending of player position
     */
    private class NetThread extends Thread implements MultiplayerEventListener {

        private String _myIP;
        private Timer _netTimer;

        private final int NETWORK_UPDATES_PER_SECOND = 20;

        /**
         * Initializes the thread
         */
        @Override
        public void run() {
            try {

                _myIP = getIPAddress();
                if (_myIP == null)
                    _myIP = InetAddress.getLocalHost().getHostAddress();
            } catch (UnknownHostException e) {
                // TODO: Implement nicer error message
                finish();
            }

            _network = new MultiplayerNetwork();
            _network.registerListener(this);

            // Start net loop
            _netTimer = new Timer("Network");
            _netTimer.scheduleAtFixedRate(new TimerTask() {
                @Override
                public void run() {
                    synchronized (_state) {
                        PlayerSprite player = _state.getPlayer();
                        synchronized (_network) {
                            _network.sendCode(102, player.getXPos() + "," + player.getYPos());
                        }
                    }

                }
            }, 0, 1000 / NETWORK_UPDATES_PER_SECOND);

        }

        @Override
        public void onNetworkError(Exception e, String text) {
            // Handle Errors
            // TODO:  Implement nice error checking for various errors on Network.
            System.err.println(e.getMessage() + " " + text);
            finish();
        }

        @Override
        public void message(int statusCode, String event, InetAddress from) {

        /*
        THE STUART TABLE
        There's a couple of hang overs from previous versions of networking
        For example, codes start at 102

        | Code                | Description                        |
        |---------------------|------------------------------------|
        | 102 <x,y>           | Update Location of Client.         |
        | 103                 | I am ready                         |
        | 104                 | I am leaving                       |

        */
            synchronized (_otherPlayers) {
                // Ignore messages from ourselves
                String otherAddress = from.getHostAddress();
                if (!otherAddress.equals(_myIP)) {

                    // If this is an ip we haven't seen, add it
                    if (!_otherPlayers.containsKey(otherAddress)) {
                        addPlayerToHashtable(otherAddress);
                        _othersReady = false;
                    }
                    MultiPlayerGhostSprite s = (MultiPlayerGhostSprite) _otherPlayers.get(otherAddress);

                    switch (statusCode) {
                        case 102:
                            // Parse values
                            String[] messageSplit = event.split(",");
                            float[] posArray = new float[2];
                            try {
                                posArray[0] = Float.parseFloat(messageSplit[0]);
                                posArray[1] = Float.parseFloat(messageSplit[1]);
                            } catch (Exception ex) {
                                posArray[0] = 0;
                                posArray[1] = 0;
                            }

                            // Update sprite position
                            s.setXPos(posArray[0]);
                            s.setYPos(posArray[1]);
                            break;
                        case 103:
                            // Set sprite to ready
                            s.setReady();

                            // Check overall readiness
                            boolean tempReady = true;
                            for (Object sprite : _otherPlayers.values()) {
                                if (!((MultiPlayerGhostSprite) sprite).isReady()) {
                                    tempReady = false;
                                    break;
                                }
                            }
                            if (tempReady)
                                _othersReady = true;
                            break;
                        case 104:
                            // Remove sprite from lists
                            _otherPlayers.remove(otherAddress);
                            synchronized (_state) {
                                _state.getSprites().remove(s);
                            }

                            // Check overall readiness
                            boolean maybeReady = true;
                            for (Object sprite : _otherPlayers.values()) {
                                if (!((MultiPlayerGhostSprite) sprite).isReady()) {
                                    maybeReady = false;
                                    break;
                                }
                            }
                            if (maybeReady)
                                _othersReady = true;
                            break;
                        default:
                            Log.e("Net", "Unknown code " + statusCode + " received with message " + event);
                            break;
                    }
                }
            }
        }

        private void addPlayerToHashtable(String address) {
            MultiPlayerGhostSprite newSprite = new MultiPlayerGhostSprite(0, 0);
            _otherPlayers.put(address, newSprite);
            synchronized (_state) {
                _state.getSprites().add(newSprite);
            }
        }

        /**
         * Big thanks to http://stackoverflow.com/questions/6064510/how-to-get-ip-address-of-the-device
         * for this. I wouldn't have been able to figure it out myself.
         * Get IP address from first non-localhost interface
         *
         * @return address or empty string
         */
        public String getIPAddress() {
            try {
                List<NetworkInterface> interfaces = Collections.list(NetworkInterface.getNetworkInterfaces());
                for (NetworkInterface intf : interfaces) {
                    List<InetAddress> addrs = Collections.list(intf.getInetAddresses());
                    for (InetAddress addr : addrs) {
                        if (!addr.isLoopbackAddress()) {
                            String sAddr = addr.getHostAddress().toUpperCase();
                            if (sAddr.matches("\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}"))
                                return sAddr;
                        }
                    }
                }
            } catch (Exception ex) {
            } // for now eat exceptions
            return null;
        }

        public void stopTimer() {
            _netTimer.cancel();
        }
    }
}
