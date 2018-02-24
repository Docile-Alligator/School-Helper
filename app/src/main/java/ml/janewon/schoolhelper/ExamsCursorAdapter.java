package ml.janewon.schoolhelper;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;

/**
 * Created by alex on 9/29/17.
 */

public class ExamsCursorAdapter extends CursorAdapter {
    ExamsCursorAdapter(Context context, Cursor c) {
        super(context, c, 0);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        View newView = LayoutInflater.from(context).inflate(R.layout.item_exam, parent, false);
        newView.setTag(new ViewHolder(newView));

        return newView;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        ViewHolder viewHolder = (ViewHolder) view.getTag();
        TextView nameTextView = viewHolder.nameTextView;
        TextView dateTextView = viewHolder.dateTextView;
        TextView subjectTextView = viewHolder.subjectTextView;
        TextView timeTextView = viewHolder.timeTextView;

        String name = cursor.getString(cursor.getColumnIndexOrThrow(SchoolHelperDatabaseHelper.EXAM_NAME));
        String date = cursor.getString(cursor.getColumnIndexOrThrow(SchoolHelperDatabaseHelper.EXAM_DATE));
        String timeFrom = cursor.getString(cursor.getColumnIndexOrThrow(SchoolHelperDatabaseHelper.EXAM_TIME_FROM));
        String timeTo = cursor.getString(cursor.getColumnIndexOrThrow(SchoolHelperDatabaseHelper.EXAM_TIME_TO));
        String subject = Utils.getSubject(context, cursor);
        String timeInterval = timeFrom + " ~ " + timeTo;

        nameTextView.setText(name);
        dateTextView.setText(date);
        subjectTextView.setText(subject);
        timeTextView.setText(timeInterval);
    }

    static class ViewHolder {
        private TextView nameTextView;
        private TextView dateTextView;
        private TextView subjectTextView;
        private TextView timeTextView;

        ViewHolder(View v) {
            nameTextView = (TextView) v.findViewById(R.id.item_exam_name);
            dateTextView = (TextView) v.findViewById(R.id.item_exam_date);
            subjectTextView = (TextView) v.findViewById(R.id.item_exam_subject);
            timeTextView = (TextView) v.findViewById(R.id.item_exam_time);
        }
    }
}