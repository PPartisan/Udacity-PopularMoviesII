package com.github.ppartisan.popularmoviesii.data;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.github.ppartisan.popularmoviesii.data.FavouriteMoviesContract.MoviesEntry;
import com.github.ppartisan.popularmoviesii.data.FavouriteMoviesContract.ExtrasEntry;

public class FavouriteMoviesProvider extends ContentProvider {

    static final int MOVIES = 100;
    static final int EXTRAS = 101;
    static final int MOVIE = 500;
    static final int EXTRA = 501;

    public static final UriMatcher MATCHER = buildUriMatcher();

    private FavouriteMoviesDbHelper db = null;

    @Override
    public boolean onCreate() {
        db = FavouriteMoviesDbHelper.get(getContext());
        return (!(db==null));
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, String[] projection, String selection, String[] whereArgs, String sortOrder) {

        Cursor cursor;

        switch (MATCHER.match(uri)) {
            case MOVIES:
                cursor = getFavourites();
                break;
            case MOVIE:
                cursor = getFavourite(uri, projection);
                break;
            case EXTRA:
                cursor = getExtra(uri);
                break;
            default:
                throw new UnsupportedOperationException("Unknown Uri: " + uri);
        }

        if (getContext() != null) {
            cursor.setNotificationUri(getContext().getContentResolver(), uri);
        }

        return cursor;
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {

        switch (MATCHER.match(uri)) {
            case MOVIES:
                return MoviesEntry.CONTENT_TYPE;
            case MOVIE:
                return MoviesEntry.CONTENT_ITEM_TYPE;
            case EXTRA:
                return ExtrasEntry.CONTENT_ITEM_TYPE;
            case EXTRAS:
                return ExtrasEntry.CONTENT_TYPE;
            default:
                throw new UnsupportedOperationException("Unknown Uri: " + uri);
        }

    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, ContentValues contentValues) {

        final SQLiteDatabase db = this.db.getWritableDatabase();
        Uri result = null;
        long id;

        switch (MATCHER.match(uri)) {
            case MOVIES:
                id = db.insertWithOnConflict(
                        MoviesEntry.TABLE_NAME, null, contentValues, SQLiteDatabase.CONFLICT_REPLACE
                );
                if (id >= 0) result = MoviesEntry.buildFavouriteMovieUri(id);
                break;
            case EXTRAS:
                id = db.insert(ExtrasEntry.TABLE_NAME, null, contentValues);
                if (id >=0) result = ExtrasEntry.buildFavouriteExtraUri(id);
                break;
            default:
                throw new UnsupportedOperationException("Unknown Uri: " + uri);
        }

        if (id < 0) throw new SQLException("Failed to insert row into " + uri);

        if (getContext() != null) getContext().getContentResolver().notifyChange(uri, null);
        db.close();
        return result;
    }

    @Override
    public int delete(@NonNull Uri uri, String selection, String[] whereArgs) {

        final SQLiteDatabase db = this.db.getWritableDatabase();
        String tableName;

        switch (MATCHER.match(uri)) {
            case MOVIES:
                tableName = MoviesEntry.TABLE_NAME;
                break;
            case EXTRAS:
                tableName = ExtrasEntry.TABLE_NAME;
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }

        final int rowsDeleted = db.delete(tableName, selection, whereArgs);
        if (getContext() != null) getContext().getContentResolver().notifyChange(uri, null);
        db.close();
        return rowsDeleted;
    }

    @Override
    public int update(@NonNull Uri uri, ContentValues contentValues, String s, String[] strings) {
        throw new UnsupportedOperationException(
                FavouriteMoviesProvider.class.getName() +" does not support updates");
    }

    private static UriMatcher buildUriMatcher() {

        final UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);
        matcher.addURI(FavouriteMoviesContract.CONTENT_AUTHORITY, FavouriteMoviesContract.PATH_MOVIES, MOVIES);
        matcher.addURI(FavouriteMoviesContract.CONTENT_AUTHORITY, FavouriteMoviesContract.PATH_EXTRAS, EXTRAS);
        matcher.addURI(FavouriteMoviesContract.CONTENT_AUTHORITY, FavouriteMoviesContract.PATH_MOVIES+"/#", MOVIE);
        matcher.addURI(FavouriteMoviesContract.CONTENT_AUTHORITY, FavouriteMoviesContract.PATH_EXTRAS+"/#", EXTRA);

        return matcher;

    }

    private Cursor getFavourites() {

        return db.getReadableDatabase().query(
                MoviesEntry.TABLE_NAME, null, null, null, null, null, null
        );

    }

    private Cursor getFavourite(Uri uri, String[] columns) {

        final String where = MoviesEntry.COLUMN_MOVIE_ID + "=?";
        final String[] whereArgs = new String[] { MoviesEntry.getMovieIdFromUri(uri) };

        return db.getReadableDatabase().query(
                MoviesEntry.TABLE_NAME, columns, where, whereArgs, null, null, null
        );

    }

    private Cursor getExtra(Uri uri) {

        final String where = ExtrasEntry.COLUMN_MOVIE_ID + "=?";
        final String[] whereArgs = new String[] { ExtrasEntry.getMovieIdFromUri(uri) };

        return db.getReadableDatabase().query(
                ExtrasEntry.TABLE_NAME, null, where, whereArgs, null, null, null
        );

    }

}
