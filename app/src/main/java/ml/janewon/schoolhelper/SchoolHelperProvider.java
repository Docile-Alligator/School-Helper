package ml.janewon.schoolhelper;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

/**
 * Created by alex on 10/1/17.
 */

public class SchoolHelperProvider extends ContentProvider {

    private static final int ASSIGNMENTS = 1;
    private static final int ASSIGNMENT_ID = 2;
    private static final int EXAMS = 3;
    private static final int EXAM_ID = 4;
    private static final int SUBJECTS = 5;
    private static final int SUBJECT_ID = 6;
    private static final int TEACHERS = 7;
    private static final int TEACHER_ID = 8;
    private static final int CLASSES = 9;
    private static final int CLASS_ID = 10;

    private SchoolHelperDatabaseHelper mDbHelper;

    // Creates a UriMatcher object.
    private static final UriMatcher sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

    static {
        sUriMatcher.addURI(SchoolHelperDatabaseHelper.CONTENT_AUTHORITY, SchoolHelperDatabaseHelper.TABLE_ASSIGNMENTS, ASSIGNMENTS);
        sUriMatcher.addURI(SchoolHelperDatabaseHelper.CONTENT_AUTHORITY, SchoolHelperDatabaseHelper.TABLE_ASSIGNMENTS + "/#", ASSIGNMENT_ID);

        sUriMatcher.addURI(SchoolHelperDatabaseHelper.CONTENT_AUTHORITY, SchoolHelperDatabaseHelper.TABLE_EXAMS, EXAMS);
        sUriMatcher.addURI(SchoolHelperDatabaseHelper.CONTENT_AUTHORITY, SchoolHelperDatabaseHelper.TABLE_EXAMS + "/#", EXAM_ID);

        sUriMatcher.addURI(SchoolHelperDatabaseHelper.CONTENT_AUTHORITY, SchoolHelperDatabaseHelper.TABLE_SUBJECTS, SUBJECTS);
        sUriMatcher.addURI(SchoolHelperDatabaseHelper.CONTENT_AUTHORITY, SchoolHelperDatabaseHelper.TABLE_SUBJECTS + "/#", SUBJECT_ID);

        sUriMatcher.addURI(SchoolHelperDatabaseHelper.CONTENT_AUTHORITY, SchoolHelperDatabaseHelper.TABLE_TEACHERS, TEACHERS);
        sUriMatcher.addURI(SchoolHelperDatabaseHelper.CONTENT_AUTHORITY, SchoolHelperDatabaseHelper.TABLE_TEACHERS + "/#", TEACHER_ID);

        sUriMatcher.addURI(SchoolHelperDatabaseHelper.CONTENT_AUTHORITY, SchoolHelperDatabaseHelper.TABLE_CLASS, CLASSES);
        sUriMatcher.addURI(SchoolHelperDatabaseHelper.CONTENT_AUTHORITY, SchoolHelperDatabaseHelper.TABLE_CLASS + "/#", CLASS_ID);
    }

    @Override
    public boolean onCreate() {
        mDbHelper = new SchoolHelperDatabaseHelper(getContext());
        return true;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection, @Nullable String[] selectionArgs, @Nullable String sortOrder) {
        SQLiteDatabase db = mDbHelper.getReadableDatabase();
        Cursor cursor;

        switch (sUriMatcher.match(uri)) {
            case ASSIGNMENTS:
                cursor = db.query(SchoolHelperDatabaseHelper.TABLE_ASSIGNMENTS,
                        projection,
                        selection,
                        selectionArgs,
                        null, null,
                        sortOrder);
                break;
            case ASSIGNMENT_ID:
                selection = SchoolHelperDatabaseHelper.DB_ID + "=?";
                selectionArgs = new String[] {String.valueOf(uri.getLastPathSegment())};

                cursor = db.query(SchoolHelperDatabaseHelper.TABLE_ASSIGNMENTS,
                        projection,
                        selection,
                        selectionArgs,
                        null, null,
                        sortOrder);
                break;

            case EXAMS:
                cursor = db.query(SchoolHelperDatabaseHelper.TABLE_EXAMS,
                        projection,
                        selection,
                        selectionArgs,
                        null, null,
                        sortOrder);
                break;
            case EXAM_ID:
                selection = SchoolHelperDatabaseHelper.DB_ID + "=?";
                selectionArgs = new String[]{String.valueOf(uri.getLastPathSegment())};

                cursor = db.query(SchoolHelperDatabaseHelper.TABLE_EXAMS,
                        projection,
                        selection,
                        selectionArgs,
                        null, null,
                        sortOrder);
                break;

            case SUBJECTS:
                cursor = db.query(SchoolHelperDatabaseHelper.TABLE_SUBJECTS,
                        projection,
                        selection,
                        selectionArgs,
                        null, null,
                        sortOrder);
                break;
            case SUBJECT_ID:
                selection = SchoolHelperDatabaseHelper.DB_ID + "=?";
                selectionArgs = new String[]{String.valueOf(uri.getLastPathSegment())};

                cursor = db.query(SchoolHelperDatabaseHelper.TABLE_SUBJECTS,
                        projection,
                        selection,
                        selectionArgs,
                        null, null,
                        sortOrder);
                break;

            case TEACHERS:
                cursor = db.query(SchoolHelperDatabaseHelper.TABLE_TEACHERS,
                        projection,
                        selection,
                        selectionArgs,
                        null, null,
                        sortOrder);
                break;
            case TEACHER_ID:
                selection = SchoolHelperDatabaseHelper.DB_ID + "=?";
                selectionArgs = new String[]{String.valueOf(uri.getLastPathSegment())};

                cursor = db.query(SchoolHelperDatabaseHelper.TABLE_TEACHERS,
                        projection,
                        selection,
                        selectionArgs,
                        null, null,
                        sortOrder);
                break;

            case CLASSES:
                cursor = db.query(SchoolHelperDatabaseHelper.TABLE_CLASS,
                        projection,
                        selection,
                        selectionArgs,
                        null, null,
                        sortOrder);
                break;
            case CLASS_ID:
                selection = SchoolHelperDatabaseHelper.DB_ID + "=?";
                selectionArgs = new String[]{String.valueOf(uri.getLastPathSegment())};

                cursor = db.query(SchoolHelperDatabaseHelper.TABLE_CLASS,
                        projection,
                        selection,
                        selectionArgs,
                        null, null,
                        sortOrder);
                break;
            default:
                throw new IllegalArgumentException("Unknown URI " + uri);
        }

        // Set notification URI on the Cursor,
        // so we know what content URI the Cursor was created for.
        // If the data at this URI changes, then we know we need to update the Cursor.
        cursor.setNotificationUri(getContext().getContentResolver(), uri);

        // Return the cursor
        return cursor;
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        switch (sUriMatcher.match(uri)) {
            case ASSIGNMENTS:
                return SchoolHelperDatabaseHelper.ASSIGNMENTS_LIST_TYPE;
            case ASSIGNMENT_ID:
                return SchoolHelperDatabaseHelper.ASSIGNMENTS_ITEM_TYPE;
            case EXAMS:
                return SchoolHelperDatabaseHelper.EXAMS_LIST_TYPE;
            case EXAM_ID:
                return SchoolHelperDatabaseHelper.EXAMS_ITEM_TYPE;
            case SUBJECTS:
                return SchoolHelperDatabaseHelper.SUBJECTS_LIST_TYPE;
            case SUBJECT_ID:
                return SchoolHelperDatabaseHelper.SUBJECTS_ITEM_TYPE;
            case TEACHERS:
                return SchoolHelperDatabaseHelper.TEACHERS_LIST_TYPE;
            case TEACHER_ID:
                return SchoolHelperDatabaseHelper.TEACHERS_ITEM_TYPE;
            case CLASSES:
                return SchoolHelperDatabaseHelper.CLASS_LIST_TYPE;
            case CLASS_ID:
                return SchoolHelperDatabaseHelper.CLASS_ITEM_TYPE;
            default:
                throw new IllegalArgumentException("Unknown URI" + uri);
        }
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues values) {
        SQLiteDatabase db = mDbHelper.getWritableDatabase();
        long id;
        switch (sUriMatcher.match(uri)) {
            case ASSIGNMENTS:
                id = db.insert(SchoolHelperDatabaseHelper.TABLE_ASSIGNMENTS, null, values);
                if(id == -1) {
                    return null;
                }
                break;
            case EXAMS:
                id = db.insert(SchoolHelperDatabaseHelper.TABLE_EXAMS, null, values);
                if(id == -1) {
                    return null;
                }
                break;
            case SUBJECTS:
                id = db.insert(SchoolHelperDatabaseHelper.TABLE_SUBJECTS, null, values);
                if(id == -1) {
                    return null;
                }
                break;
            case TEACHERS:
                id = db.insert(SchoolHelperDatabaseHelper.TABLE_TEACHERS, null, values);
                if(id == -1) {
                    return null;
                }
                break;
            case CLASSES:
                id = db.insert(SchoolHelperDatabaseHelper.TABLE_CLASS, null, values);
                if(id == -1) {
                    return null;
                }
                break;

            default:
                throw new IllegalArgumentException("Unknown URI");
        }

        // Notify all listeners that the data has changed for the pet content URI
        getContext().getContentResolver().notifyChange(uri, null);

        db.close();
        return ContentUris.withAppendedId(uri, id);
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArgs) {
        SQLiteDatabase db = new SchoolHelperDatabaseHelper(getContext()).getWritableDatabase();
        int rowsDeleted;
        switch (sUriMatcher.match(uri)) {
            case ASSIGNMENTS:
                rowsDeleted = db.delete(SchoolHelperDatabaseHelper.TABLE_ASSIGNMENTS, selection, selectionArgs);
                break;
            case ASSIGNMENT_ID:
                selection = SchoolHelperDatabaseHelper.DB_ID + "=?";
                selectionArgs = new String[]{String.valueOf(uri.getLastPathSegment())};
                rowsDeleted = db.delete(SchoolHelperDatabaseHelper.TABLE_ASSIGNMENTS, selection, selectionArgs);
                break;

            case EXAMS:
                rowsDeleted = db.delete(SchoolHelperDatabaseHelper.TABLE_EXAMS, selection, selectionArgs);
                break;
            case EXAM_ID:
                selection = SchoolHelperDatabaseHelper.DB_ID + "=?";
                selectionArgs = new String[]{uri.getLastPathSegment()};
                rowsDeleted = db.delete(SchoolHelperDatabaseHelper.TABLE_EXAMS, selection, selectionArgs);
                break;

            case SUBJECTS:
                rowsDeleted = db.delete(SchoolHelperDatabaseHelper.TABLE_SUBJECTS, selection, selectionArgs);
                break;
            case SUBJECT_ID:
                selection = SchoolHelperDatabaseHelper.DB_ID + "=?";
                selectionArgs = new String[]{uri.getLastPathSegment()};
                rowsDeleted = db.delete(SchoolHelperDatabaseHelper.TABLE_SUBJECTS, selection, selectionArgs);
                break;

            case TEACHERS:
                rowsDeleted = db.delete(SchoolHelperDatabaseHelper.TABLE_TEACHERS, selection, selectionArgs);
                break;
            case TEACHER_ID:
                selection = SchoolHelperDatabaseHelper.DB_ID + "=?";
                selectionArgs = new String[]{uri.getLastPathSegment()};
                rowsDeleted = db.delete(SchoolHelperDatabaseHelper.TABLE_TEACHERS, selection, selectionArgs);
                break;

            case CLASSES:
                rowsDeleted = db.delete(SchoolHelperDatabaseHelper.TABLE_CLASS, selection, selectionArgs);
                break;
            case CLASS_ID:
                selection = SchoolHelperDatabaseHelper.DB_ID + "=?";
                selectionArgs = new String[]{uri.getLastPathSegment()};
                rowsDeleted = db.delete(SchoolHelperDatabaseHelper.TABLE_CLASS, selection, selectionArgs);
                break;

            default:
                throw new IllegalArgumentException("Unknown URI");
        }

        // If 1 or more rows were deleted, then notify all listeners that the data at the
        // given URI has changed
        if(rowsDeleted != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }

        db.close();
        return rowsDeleted;
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues values, @Nullable String selection, @Nullable String[] selectionArgs) {
        SQLiteDatabase db = new SchoolHelperDatabaseHelper(getContext()).getWritableDatabase();
        int rowsUpdated;

        switch (sUriMatcher.match(uri)) {
            case ASSIGNMENTS:
                rowsUpdated = db.update(SchoolHelperDatabaseHelper.TABLE_ASSIGNMENTS, values, selection, selectionArgs);
                break;
            case ASSIGNMENT_ID:
                selection = SchoolHelperDatabaseHelper.DB_ID + "=?";
                selectionArgs = new String[]{uri.getLastPathSegment()};
                rowsUpdated = db.update(SchoolHelperDatabaseHelper.TABLE_ASSIGNMENTS, values, selection, selectionArgs);
                break;

            case EXAMS:
                rowsUpdated = db.update(SchoolHelperDatabaseHelper.TABLE_ASSIGNMENTS, values, selection, selectionArgs);
                break;
            case EXAM_ID:
                selection = SchoolHelperDatabaseHelper.DB_ID + "=?";
                selectionArgs = new String[]{uri.getLastPathSegment()};
                rowsUpdated = db.update(SchoolHelperDatabaseHelper.TABLE_EXAMS, values, selection, selectionArgs);
                break;

            case SUBJECTS:
                rowsUpdated = db.update(SchoolHelperDatabaseHelper.TABLE_SUBJECTS, values, selection, selectionArgs);
                break;
            case SUBJECT_ID:
                selection = SchoolHelperDatabaseHelper.DB_ID + "=?";
                selectionArgs = new String[]{uri.getLastPathSegment()};
                rowsUpdated = db.update(SchoolHelperDatabaseHelper.TABLE_SUBJECTS, values, selection, selectionArgs);
                break;

            case TEACHERS:
                rowsUpdated = db.update(SchoolHelperDatabaseHelper.TABLE_TEACHERS, values, selection, selectionArgs);
                break;
            case TEACHER_ID:
                selection = SchoolHelperDatabaseHelper.DB_ID + "=?";
                selectionArgs = new String[]{uri.getLastPathSegment()};
                rowsUpdated = db.update(SchoolHelperDatabaseHelper.TABLE_TEACHERS, values, selection, selectionArgs);
                break;

            case CLASSES:
                rowsUpdated = db.update(SchoolHelperDatabaseHelper.DB_ID, values, selection, selectionArgs);
                break;
            case CLASS_ID:
                selection = SchoolHelperDatabaseHelper.DB_ID + "=?";
                selectionArgs = new String[]{uri.getLastPathSegment()};
                rowsUpdated = db.update(SchoolHelperDatabaseHelper.TABLE_CLASS, values, selection, selectionArgs);
                break;

            default:
                throw new IllegalArgumentException("Unknown URI");
        }

        // If 1 or more rows were updated, then notify all listeners that the data at the
        // given URI has changed
        if(rowsUpdated != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }

        db.close();
        return rowsUpdated;
    }
}
