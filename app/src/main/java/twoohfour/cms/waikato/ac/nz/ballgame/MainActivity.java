package twoohfour.cms.waikato.ac.nz.ballgame;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    private final int GAME_ACTIVITY_REQUEST_CODE = 1;
    GameState.Level level = GameState.Level.Random;

    //region Activity Overrides

    /**
     * Executed when the activity is created
     * @param savedInstanceState Saved state
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final Spinner levelSelect = (Spinner)findViewById(R.id.spinner_level_select);
        final Button playButton = (Button)findViewById(R.id.gameButton);
        // Use anon class. Quick and easy
        levelSelect.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                String selected = levelSelect.getSelectedItem().toString();
                String buttonText = "Play " + selected + "!";
                playButton.setText(buttonText);

                try {
                    level = GameState.getLevelFromString(selected);
                } catch (IllegalArgumentException ex) {
                    level = GameState.Level.Random;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }

    /**
     * Creates menus in Action Bar
     * @param menu Menu to create
     * @return Success
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }
    //endregion

    /**
     * Handle selection of an item in the Action Bar
     * @param item Item that was clicked
     * @return Whether handling was successful
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            // Start preferences activity
            startActivity(new Intent(this, PreferencesActivity.class));
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * Starts the game
     * @param button Button that was clicked to initiate this method
     */
    public void onGameButtonClicked(View button) {
        Intent i = new Intent(this, GameActivity.class);
        i.putExtra(GameActivity.EXTRA_LEVEL, level);
        
        startActivityForResult(i, GAME_ACTIVITY_REQUEST_CODE);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        //super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK)
            if (requestCode == GAME_ACTIVITY_REQUEST_CODE) {
                TextView scoreView = (TextView)findViewById(R.id.scoreTextView);
                int score = 0;

                try {
                    score = Integer.parseInt(scoreView.getText().toString());
                } catch (NumberFormatException ex) {
                    Log.w("MainActivity Score", "Unable to parse score, setting to 0");
                }

                score += data.getIntExtra(GameActivity.EXTRA_SCORE, 0);

                scoreView.setText(Integer.toString(score));
            }
        }

}
