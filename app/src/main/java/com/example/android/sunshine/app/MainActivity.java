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

import com.example.android.sunshine.app.sync.SunshineSyncAdapter;


public class MainActivity extends ActionBarActivity implements ForecastFragment.Callback {

    private static final String LOG_TAG = MainActivity.class.getSimpleName();

    private static final String DETAILFRAGMENT_TAG = "DFTAG";

    private String mLocation;
    private boolean mTwoPane;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mLocation = Utility.getPreferredLocation(this);

        setContentView(R.layout.activity_main);

        if(findViewById(R.id.weather_detail_container) != null)
        {
            mTwoPane = true;

            if (savedInstanceState == null) {
                getSupportFragmentManager().beginTransaction()
                        .add(R.id.weather_detail_container, new DetailFragment(), DETAILFRAGMENT_TAG)
                        .commit();
            }
        }
        else
        {
            mTwoPane = false;
            getSupportActionBar().setElevation(0f);
        }

        ForecastFragment forecastFragment =  ((ForecastFragment)getSupportFragmentManager()
                .findFragmentById(R.id.fragment_forecast));
        forecastFragment.setUseTodayLayout(!mTwoPane);

        SunshineSyncAdapter.initializeSyncAdapter(this);
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

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onResume()
    {
        super.onResume();

        String preferredLocation = Utility.getPreferredLocation(this);

        if(preferredLocation != null && !preferredLocation.equals(mLocation))
        {
            ForecastFragment ff = (ForecastFragment)getSupportFragmentManager().findFragmentById(R.id.fragment_forecast);
            if(ff != null)
            {
                ff.onLocationChanged();
            }

            DetailFragment df = (DetailFragment)getSupportFragmentManager().findFragmentByTag(DETAILFRAGMENT_TAG);
            if(df != null)
            {
                df.onLocationChanged(preferredLocation);
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

    @Override
    public void onItemSelected(Uri contentUri) {
        if (mTwoPane) {
            // In two-pane mode, show the detail view in this activity by
            // adding or replacing the detail fragment using a
            // fragment transaction.
            Bundle args = new Bundle();
            args.putParcelable(DetailFragment.DETAIL_URI, contentUri);
            args.putParcelable(DetailFragment.POSITION, contentUri);

            DetailFragment fragment = new DetailFragment();
            fragment.setArguments(args);

            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.weather_detail_container, fragment, DETAILFRAGMENT_TAG)
                    .commit();
        } else {
            Intent intent = new Intent(this, DetailActivity.class)
                    .setData(contentUri);
            startActivity(intent);
        }
    }
}
