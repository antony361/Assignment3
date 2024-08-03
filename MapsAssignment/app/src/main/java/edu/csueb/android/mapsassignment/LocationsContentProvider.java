package edu.csueb.android.mapsassignment;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class LocationsContentProvider extends ContentProvider {

    private static final String AUTHORITY = "edu.csueb.android.mapsassignment.locations";
    private static final String PATH_LOCATIONS = "locations";
    public static final Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY + "/" + PATH_LOCATIONS);

    private static final int LOCATIONS = 1;
    private static final int LOCATION_ID = 2;

    private static final UriMatcher uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

    static {
        uriMatcher.addURI(AUTHORITY, PATH_LOCATIONS, LOCATIONS);
        uriMatcher.addURI(AUTHORITY, PATH_LOCATIONS + "/#", LOCATION_ID);
    }

    private LocationsDB dbHelper;

    @Override
    public boolean onCreate() {
        dbHelper = new LocationsDB(getContext());
        return true;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection,
                        @Nullable String[] selectionArgs, @Nullable String sortOrder) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        switch (uriMatcher.match(uri)) {
            case LOCATIONS:
                return db.query(LocationsDB.TABLE_NAME, projection, selection, selectionArgs, null, null, sortOrder);
            case LOCATION_ID:
                selection = LocationsDB.COLUMN_ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                return db.query(LocationsDB.TABLE_NAME, projection, selection, selectionArgs, null, null, sortOrder);
            default:
                throw new IllegalArgumentException("Unknown URI: " + uri);
        }
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues values) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        long id;
        switch (uriMatcher.match(uri)) {
            case LOCATIONS:
                id = db.insert(LocationsDB.TABLE_NAME, null, values);
                if (id != -1) {
                    return ContentUris.withAppendedId(CONTENT_URI, id);
                }
                return null;
            default:
                throw new IllegalArgumentException("Unknown URI: " + uri);
        }
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArgs) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        switch (uriMatcher.match(uri)) {
            case LOCATIONS:
                return db.delete(LocationsDB.TABLE_NAME, selection, selectionArgs);
            case LOCATION_ID:
                selection = LocationsDB.COLUMN_ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                return db.delete(LocationsDB.TABLE_NAME, selection, selectionArgs);
            default:
                throw new IllegalArgumentException("Unknown URI: " + uri);
        }
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues values, @Nullable String selection, @Nullable String[] selectionArgs) {
        throw new UnsupportedOperationException("Update operation is not supported");
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        throw new UnsupportedOperationException("Not implemented");
    }
}
