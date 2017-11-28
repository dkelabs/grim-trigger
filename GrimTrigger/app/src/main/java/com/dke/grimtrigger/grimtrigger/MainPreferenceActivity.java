package com.dke.grimtrigger.grimtrigger;

import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.preference.PreferenceFragment;
import android.preference.Preference.OnPreferenceChangeListener;

public class MainPreferenceActivity extends PreferenceActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getFragmentManager()
                .beginTransaction()
                .replace(android.R.id.content, new MainPreferenceFragment(), "main-preference-fragment")
                .commit();


    }

    public static class MainPreferenceFragment extends PreferenceFragment {
        @Override
        public void onCreate(final Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            // Main Preferences
            addPreferencesFromResource(R.xml.preferences);
            // Testing preferences to show when testing_mode_preference is true
            addPreferencesFromResource(R.xml.testing_preferences);

        }
    }
}
