package ml.janewon.schoolhelper;

import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.view.inputmethod.InputMethodManager;

/**
 * Created by alex on 2/17/18.
 */

class Utils {
    static String getSubject(Context context, Cursor cursor) {
        int subjectId = cursor.getInt(cursor.getColumnIndexOrThrow(SchoolHelperDatabaseHelper.SUBJECT_FOREIGN_KEY));
        Uri subjectUri = Uri.withAppendedPath(SchoolHelperDatabaseHelper.SUBJECTS_CONTENT_URI, Integer.toString(subjectId));
        Cursor subjectCursor = context.getContentResolver().query(subjectUri,
                new String[]{SchoolHelperDatabaseHelper.SUBJECT_NAME},
                null, null, null);
        String subject = "";
        if(subjectCursor != null && subjectCursor.moveToFirst()) {
            subject = subjectCursor.getString(subjectCursor.getColumnIndexOrThrow(SchoolHelperDatabaseHelper.SUBJECT_NAME));
        }

        if(subjectCursor != null) {
            subjectCursor.close();
        }
        return subject;
    }

    static void hideKeyboard(Activity activity) {
        InputMethodManager inputManager = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
        if(inputManager != null) {
            inputManager.hideSoftInputFromWindow(activity.getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }
}
