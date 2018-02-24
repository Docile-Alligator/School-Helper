package ml.janewon.schoolhelper;

import android.content.Context;
import android.database.Cursor;
import android.graphics.drawable.GradientDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;

/**
 * Created by alex on 9/22/17.
 */

public class SubjectCursorAdapter extends CursorAdapter {
    SubjectCursorAdapter(Context context, Cursor c) {
        super(context, c, 0);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        View newView = LayoutInflater.from(context).inflate(R.layout.item_subject, parent, false);
        newView.setTag(new ViewHolder(newView));

        return newView;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        ViewHolder viewHolder = (ViewHolder) view.getTag();
        View colorView = viewHolder.colorView;
        TextView subjectNameTextView = viewHolder.subjectNameTextView;

        int color = cursor.getInt(cursor.getColumnIndexOrThrow(SchoolHelperDatabaseHelper.SUBJECT_COLOR));
        String name = cursor.getString(cursor.getColumnIndexOrThrow(SchoolHelperDatabaseHelper.SUBJECT_NAME));

        GradientDrawable circle = viewHolder.circle;
        circle.setColor(color);
        colorView.setBackground(circle);
        subjectNameTextView.setText(name);
    }

    static class ViewHolder {
        private View colorView;
        private TextView subjectNameTextView;
        GradientDrawable circle;

        ViewHolder(View v) {
            colorView = v.findViewById(R.id.item_subject_color);
            subjectNameTextView = (TextView) v.findViewById(R.id.item_subject_name);
            circle = (GradientDrawable) v.getResources().getDrawable(R.drawable.circle);
        }
    }
}
