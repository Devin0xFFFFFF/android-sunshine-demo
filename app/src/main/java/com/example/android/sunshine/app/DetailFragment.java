package com.example.android.sunshine.app;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.ShareActionProvider;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.android.sunshine.app.R;
import com.example.android.sunshine.app.Utility;
import com.example.android.sunshine.app.data.WeatherContract;

public class DetailFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor>{

    private static final String LOG_TAG  = DetailFragment.class.getSimpleName();

    private static final String FORECAST_SHARE_HASHTAG = "#SunshineApp";

    private static final int DETAIL_LOADER_ID = 0;

    public static final String[]DETAIL_COLUMNS = {
            // In this case the id needs to be fully qualified with a table name, since
            // the content provider joins the location & weather tables in the background
            // (both have an _id column)
            // On the one hand, that's annoying.  On the other, you can search the weather table
            // using the location set by the user, which is only in the Location table.
            // So the convenience is worth it.
            WeatherContract.WeatherEntry.TABLE_NAME + "." + WeatherContract.WeatherEntry._ID,
            WeatherContract.WeatherEntry.COLUMN_DATE,
            WeatherContract.WeatherEntry.COLUMN_SHORT_DESC,
            WeatherContract.WeatherEntry.COLUMN_MAX_TEMP,
            WeatherContract.WeatherEntry.COLUMN_MIN_TEMP,
            WeatherContract.WeatherEntry.COLUMN_HUMIDITY,
            WeatherContract.WeatherEntry.COLUMN_PRESSURE,
            WeatherContract.WeatherEntry.COLUMN_WIND_SPEED,
            WeatherContract.WeatherEntry.COLUMN_DEGREES,
            WeatherContract.WeatherEntry.COLUMN_WEATHER_ID,
    };

    static final int COL_ID = 0;
    static final int COL_WEATHER_DATE = 1;
    static final int COL_WEATHER_DESC = 2;
    static final int COL_WEATHER_MAX_TEMP = 3;
    static final int COL_WEATHER_MIN_TEMP = 4;
    static final int COL_WEATHER_HUMIDITY = 5;
    static final int COL_WEATHER_PRESSURE = 6;
    static final int COL_WEATHER_WIND_SPEED = 7;
    static final int COL_WEATHER_DEGREES = 8;
    static final int COL_WEATHER_ID = 9;

    static final String DETAIL_URI = "URI";
    public static final String POSITION = "POS";

    private String mForecastStr;
    private ShareActionProvider mShareActionProvider;
    private DetailViewHolder mViewHolder;

    private Uri mUri;

    public DetailFragment() {
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        Bundle arguments = getArguments();
        if (arguments != null) {
            mUri = arguments.getParcelable(DetailFragment.DETAIL_URI);
        }

        View rootView = inflater.inflate(R.layout.fragment_detail, container, false);

        mViewHolder = new DetailViewHolder(rootView);

        return rootView;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        // Inflate the menu; this adds items to the action bar if it is present.
        inflater.inflate(R.menu.detailfragment, menu);

        MenuItem item = menu.findItem(R.id.action_share);

        // Fetch and store ShareActionProvider
        mShareActionProvider = (ShareActionProvider) MenuItemCompat.getActionProvider(item);

        if (mShareActionProvider != null) {
            mShareActionProvider.setShareIntent(createShareIntent());
        }
        else
        {
            Log.d(LOG_TAG, "Share Action Provider is null");
        }
    }

    private Intent createShareIntent()
    {
        String shareText = mForecastStr + FORECAST_SHARE_HASHTAG;

        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
        shareIntent.setType("text/plain");
        shareIntent.putExtra(Intent.EXTRA_TEXT, shareText);

        return shareIntent;
    }

    @Override
    public void onActivityCreated(Bundle bundle)
    {
        getLoaderManager().initLoader(DETAIL_LOADER_ID, null, this);
        super.onActivityCreated(bundle);
    }

    void onLocationChanged(String newLocation)
    {
        Uri uri = mUri;

        if(uri != null)
        {
            long date = WeatherContract.WeatherEntry.getDateFromUri(uri);
            Uri updatedUri = WeatherContract.WeatherEntry.buildWeatherLocationWithDate(newLocation, date);
            mUri = updatedUri;
            getLoaderManager().restartLoader(DETAIL_LOADER_ID, null, this);
        }
    }


    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        if(null == mUri)
        {
            return null;
        }

        return new CursorLoader(getActivity(), mUri,
                DETAIL_COLUMNS, null, null, null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        if(!cursor.moveToFirst())
        {
            return;
        }

        Context context = getActivity();

        int weatherId = cursor.getInt(COL_WEATHER_ID);

        mViewHolder.iconView.setImageResource(Utility.getArtResourceForWeatherCondition(weatherId));

        long dateInMillis = cursor.getLong(COL_WEATHER_DATE);
        mViewHolder.dateView.setText(Utility.getFriendlyDayString(context, dateInMillis));

        String description = cursor.getString(COL_WEATHER_DESC);
        mViewHolder.descriptionView.setText(description);

        mViewHolder.iconView.setContentDescription(description);

        boolean isMetric = Utility.isMetric(context);

        double high = cursor.getDouble(COL_WEATHER_MAX_TEMP);
        mViewHolder.highTempView.setText(Utility.formatTemperature(context, high, isMetric));

        double low = cursor.getDouble(COL_WEATHER_MIN_TEMP);
        mViewHolder.lowTempView.setText(Utility.formatTemperature(context, low, isMetric));

        double humidity = cursor.getDouble(COL_WEATHER_HUMIDITY);
        String formattedHumidity = "Humidity: " + humidity + "%";
        mViewHolder.humidityView.setText(formattedHumidity);

        float windSpeed = cursor.getFloat(COL_WEATHER_WIND_SPEED);
        float windDegrees = cursor.getFloat(COL_WEATHER_DEGREES);
        String formattedWind = Utility.getFormattedWind(getActivity(), windSpeed, windDegrees);
        mViewHolder.windView.setText(formattedWind);

        double pressure = cursor.getDouble(COL_WEATHER_PRESSURE);
        String formattedPressure = "Pressure: " + pressure + " hPa";
        mViewHolder.pressureView.setText(formattedPressure);

        updateShare(cursor);
    }

    private void updateShare(Cursor data)
    {
        boolean isMetric = Utility.isMetric(getActivity());

        String dateString = Utility.formatDate(data.getLong(COL_WEATHER_DATE));

        String weatherDescription = data.getString(COL_WEATHER_DESC);

        String high = Utility.formatTemperature(getActivity(),
                data.getDouble(COL_WEATHER_MAX_TEMP), isMetric);

        String low = Utility.formatTemperature(getActivity(),
                data.getDouble(COL_WEATHER_MIN_TEMP), isMetric);

        mForecastStr =  String.format("%s - %s - %s/%s",
                dateString, weatherDescription, high, low);

        if(mShareActionProvider != null)
        {
            mShareActionProvider.setShareIntent(createShareIntent());
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }

    private String formatHighLows(double high, double low) {
        boolean isMetric = Utility.isMetric(getActivity());
        String highLowStr = Utility.formatTemperature(getActivity(), high, isMetric) + "/" + Utility.formatTemperature(getActivity(), low, isMetric);
        return highLowStr;
    }

    public static class DetailViewHolder {
        public final ImageView iconView;
        public final TextView dateView;
        public final TextView descriptionView;
        public final TextView highTempView;
        public final TextView lowTempView;
        public final TextView humidityView;
        public final TextView windView;
        public final TextView pressureView;

        public DetailViewHolder(View view) {
            iconView = (ImageView) view.findViewById(R.id.detail_icon);
            dateView = (TextView) view.findViewById(R.id.detail_date_textview);
            descriptionView = (TextView) view.findViewById(R.id.detail_forecast_textview);
            highTempView = (TextView) view.findViewById(R.id.detail_high_textview);
            lowTempView = (TextView) view.findViewById(R.id.detail_low_textview);
            humidityView = (TextView) view.findViewById(R.id.detail_humidity_textview);
            windView = (TextView) view.findViewById(R.id.detail_wind_textview);
            pressureView = (TextView) view.findViewById(R.id.detail_pressure_textview);
        }
    }
}