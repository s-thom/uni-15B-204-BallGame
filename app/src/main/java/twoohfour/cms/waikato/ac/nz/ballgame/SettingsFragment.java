package twoohfour.cms.waikato.ac.nz.ballgame;

import android.app.Activity;
import android.net.Uri;
import android.os.Bundle;
import android.app.Fragment;
import android.preference.PreferenceFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


/**
 * Setting fragment, as recommended by
 * http://developer.android.com/guide/topics/ui/settings.html#Fragment
 */
public class SettingsFragment extends PreferenceFragment {

    /**
     * Load in settings from XML
     * @param savedInstanceState Saved state
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // To be honest, I'm not exactly sure how the Fragment system works
        // Guess that's something I should look into.
        addPreferencesFromResource(R.xml.preferences);
    }

}
