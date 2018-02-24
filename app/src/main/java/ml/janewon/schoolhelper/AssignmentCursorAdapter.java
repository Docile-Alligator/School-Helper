package ml.janewon.schoolhelper;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;

/**
 * Created by alex on 9/27/17.
 */

public class AssignmentCursorAdapter extends CursorAdapter {
    AssignmentCursorAdapter(Context context, Cursor c) {
        super(context, c, 0);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        View newView = LayoutInflater.from(context).inflate(R.layout.item_assignment, parent, false);
        newView.setTag(new ViewHolder(newView));

        return newView;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        ViewHolder viewHolder = (ViewHolder) view.getTag();
        TextView titleTextView = viewHolder.titleTextView;
        TextView subjectTextView = viewHolder.subjectTextView;
        TextView dueDateTextView = viewHolder.dueDateTextView;
        TextView dueTimeTextView = viewHolder.dueTimeTextView;

        titleTextView.setText(cursor.getString(cursor.getColumnIndexOrThrow(SchoolHelperDatabaseHelper.ASSIGNMENT_TITLE)));

        int subjectId = cursor.getInt(cursor.getColumnIndexOrThrow(SchoolHelperDatabaseHelper.SUBJECT_FOREIGN_KEY));
        Uri subjectUri = Uri.withAppendedPath(SchoolHelperDatabaseHelper.SUBJECTS_CONTENT_URI, Integer.toString(subjectId));
        Cursor subjectCursor = context.getContentResolver().query(subjectUri, null, null, null, null);
        if(subjectCursor != null && subjectCursor.moveToFirst()) {
            String subject = subjectCursor.getString(subjectCursor.getColumnIndexOrThrow(SchoolHelperDatabaseHelper.SUBJECT_NAME));
            subjectTextView.setText(subject);
            subjectCursor.close();
        }

        String dueDate = cursor.getString(cursor.getColumnIndexOrThrow(SchoolHelperDatabaseHelper.ASSIGNMENT_DUE_DATE));
        if(!dueDate.equals("")) {
            dueDateTextView.setText(dueDate);
        } else {
            dueDateTextView.setVisibility(View.GONE);
        }

        String dueTime = cursor.getString(cursor.getColumnIndexOrThrow(SchoolHelperDatabaseHelper.ASSIGNMENT_DUE_TIME));
        if(!dueTime.equals("")) {
            dueTimeTextView.setText(dueTime);
        } else {
            dueTimeTextView.setVisibility(View.GONE);
        }
    }

    static class ViewHolder {
        private TextView titleTextView;
        private TextView subjectTextView;
        private TextView dueDateTextView;
        private TextView dueTimeTextView;

        ViewHolder(View v) {
            titleTextView = (TextView) v.findViewById(R.id.item_assignment_title);
            subjectTextView = (TextView) v.findViewById(R.id.item_assignment_subject);
            dueDateTextView = (TextView) v.findViewById(R.id.item_assignment_due_date);
            dueTimeTextView = (TextView) v.findViewById(R.id.item_assignment_due_time);
        }
    }
}
