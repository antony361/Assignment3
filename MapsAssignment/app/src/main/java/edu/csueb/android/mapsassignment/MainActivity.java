package edu.csueb.android.mapsassignment;

import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentActivity;
import androidx.loader.app.LoaderManager;
import androidx.loader.content.CursorLoader;
import androidx.loader.content.Loader;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class MainActivity extends FragmentActivity implements OnMapReadyCallback,
        GoogleMap.OnMapClickListener, GoogleMap.OnMapLongClickListener, LoaderManager.LoaderCallbacks<Cursor> {

    private GoogleMap map;
    private static final int LOCATION_LOADER = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        map = googleMap;
        map.setOnMapClickListener(this);
        map.setOnMapLongClickListener(this);

        getSupportLoaderManager().initLoader(LOCATION_LOADER, null, this);
    }

    @Override
    public void onMapClick(LatLng latLng) {
        map.addMarker(new MarkerOptions().position(latLng));
        new LocationInsertTask().execute(latLng);
    }

    @Override
    public void onMapLongClick(LatLng latLng) {
        map.clear();
        new LocationDeleteTask().execute();
    }

    private class LocationInsertTask extends AsyncTask<LatLng, Void, Void> {
        @Override
        protected Void doInBackground(LatLng... latLngs) {
            LatLng latLng = latLngs[0];
            ContentValues values = new ContentValues();
            values.put(LocationsDB.COLUMN_LATITUDE, latLng.latitude);
            values.put(LocationsDB.COLUMN_LONGITUDE, latLng.longitude);
            values.put(LocationsDB.COLUMN_ZOOM, map.getCameraPosition().zoom);

            getContentResolver().insert(LocationsContentProvider.CONTENT_URI, values);
            return null;
        }
    }

    private class LocationDeleteTask extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... voids) {
            getContentResolver().delete(LocationsContentProvider.CONTENT_URI, null, null);
            return null;
        }
    }

    @NonNull
    @Override
    public Loader<Cursor> onCreateLoader(int id, @Nullable Bundle args) {
        return new CursorLoader(this, LocationsContentProvider.CONTENT_URI,
                null, null, null, null);
    }

    @Override
    public void onLoadFinished(@NonNull Loader<Cursor> loader, Cursor data) {
        map.clear();
        if (data != null && data.moveToFirst()) {
            do {
                double latitude = data.getDouble(data.getColumnIndexOrThrow(LocationsDB.COLUMN_LATITUDE));
                double longitude = data.getDouble(data.getColumnIndexOrThrow(LocationsDB.COLUMN_LONGITUDE));
                float zoom = data.getFloat(data.getColumnIndexOrThrow(LocationsDB.COLUMN_ZOOM));
                LatLng latLng = new LatLng(latitude, longitude);
                map.addMarker(new MarkerOptions().position(latLng));
                map.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, zoom));
            } while (data.moveToNext());
        }
    }

    @Override
    public void onLoaderReset(@NonNull Loader<Cursor> loader) {
        map.clear();
    }
}
