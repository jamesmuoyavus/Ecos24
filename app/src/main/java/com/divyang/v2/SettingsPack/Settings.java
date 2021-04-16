package com.divyang.v2.SettingsPack;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
//User Added.
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.support.v7.app.ActionBar;
import android.content.SharedPreferences;
import android.support.v7.preference.Preference;
import android.preference.PreferenceManager;
import android.support.v7.preference.PreferenceFragmentCompat;

import com.divyang.v2.R;

public class Settings extends AppCompatActivity
{
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setupActionBar();
        getSupportFragmentManager().beginTransaction().replace(android.R.id.content, new SettingsFragment()).commit();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        switch (item.getItemId())
        {
            // Respond to the action bar's Up/Home button
            case android.R.id.home:
                NavUtils.navigateUpFromSameTask(this);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed()
    {
        NavUtils.navigateUpFromSameTask(this);
    }

    //Function to set up ActionBar to Display UP(Home) Button.
    private void setupActionBar()
    {
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null)
        {
            // Show the Up button in the action bar.
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }

    /////////////////////////////////////////////////////////////////////////////////////////
    /////////////////////////////////SETTINGS FRAGMENT///////////////////////////////////////
    /////////////////////////////////////////////////////////////////////////////////////////
    public static class SettingsFragment extends PreferenceFragmentCompat implements
            SharedPreferences.OnSharedPreferenceChangeListener
    {
        SharedPreferences sharedPreferences;

        @Override
        public void onCreatePreferences(Bundle bundle, String s)
        {
            //add xml
            addPreferencesFromResource(R.xml.setting);

            sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity());

            onSharedPreferenceChanged(sharedPreferences, getString(R.string.KeyUserNamePref));
            onSharedPreferenceChanged(sharedPreferences, getString(R.string.KeyUserImagePref));
        }


        @Override
        public void onResume() {
            super.onResume();
            //unregister the preferenceChange listener
            getPreferenceScreen().getSharedPreferences()
                    .registerOnSharedPreferenceChangeListener(this);
        }

        @Override
        public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key)
        {
            Preference preference = findPreference(key);
            preference.setSummary(sharedPreferences.getString(key, ""));
        }

        @Override
        public void onPause()
        {
            super.onPause();
            //unregister the preference change listener
            getPreferenceScreen().getSharedPreferences().unregisterOnSharedPreferenceChangeListener(this);
        }

        @Override
        public void setDivider(Drawable divider)
        {
            super.setDivider(new ColorDrawable(Color.TRANSPARENT));
        }

        @Override
        public void setDividerHeight(int height)
        {
            super.setDividerHeight(0);
        }
    }
}