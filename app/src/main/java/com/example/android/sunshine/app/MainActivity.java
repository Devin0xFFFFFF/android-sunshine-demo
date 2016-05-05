package com.example.android.sunshine.app;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;


public class MainActivity extends ActionBarActivity {

    private static final String LOG_TAG = MainActivity.class.getSimpleName();

    private static final String FORECASTFRAGMENT_TAG = "FORECAST_FRAGMENT";

    private String mLocation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mLocation = Utility.getPreferredLocation(this);

        setContentView(R.layout.activity_main);
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, new ForecastFragment(), FORECASTFRAGMENT_TAG)
                    .commit();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            Intent settingsIntent = new Intent(this, SettingsActivity.class);
            startActivity(settingsIntent);
            return true;
        }
        else if(id == R.id.action_view_location)
        {
            showPreferredLocation();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void showPreferredLocation()
    {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        String locationPref = preferences.getString(getString(R.string.pref_location_key), getString(R.string.pref_location_default));

        Uri locationUri = Uri.parse("geo:0,0").buildUpon()
                .appendQueryParameter("q", locationPref)
                .build();

        showMap(locationUri);
    }

    private void showMap(Uri geoLocation) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(geoLocation);
        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivity(intent);
        }
    }

    @Override
    public void onPause()
    {
        super.onPause();
    }

    @Override
    public void onStop()
    {
        super.onStop();
    }

    @Override
    public void onResume()
    {
        super.onResume();

        String preferredLocation = Utility.getPreferredLocation(this);

        if(preferredLocation != null && !preferredLocation.equals(mLocation))
        {
            ForecastFragment ff = (ForecastFragment)getSupportFragmentManager().findFragmentByTag(FORECASTFRAGMENT_TAG);
            if(ff != null)
            {
                ff.onLocationChanged();
            }
            mLocation = preferredLocation;
        }
    }

    @Override
    public void onStart()
    {
        super.onStart();
    }

    @Override
    public void onDestroy()
    {
        super.onDestroy();
    }
}
