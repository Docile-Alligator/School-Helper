package ml.janewon.schoolhelper;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by alex on 9/24/17.
 */

public class TeacherCursorAdapter extends CursorAdapter {

    public TeacherCursorAdapter(Context context, Cursor c) {
        super(context, c, 0);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        View newView = LayoutInflater.from(context).inflate(R.layout.item_teacher, parent, false);
        newView.setTag(new ViewHolder(newView));

        return newView;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        ViewHolder viewHolder = (ViewHolder) view.getTag();
        CircleImageView photoCircleImageView = viewHolder.photoCircleImageView;
        TextView nameTextView = viewHolder.nameTextView;

        String photoPath = cursor.getString(cursor.getColumnIndexOrThrow(SchoolHelperDatabaseHelper.TEACHER_PHOTO));
        String name = cursor.getString(cursor.getColumnIndexOrThrow(SchoolHelperDatabaseHelper.TEACHER_NAME));
        String surname = cursor.getString(cursor.getColumnIndexOrThrow(SchoolHelperDatabaseHelper.TEACHER_SURNAME));

        if(photoPath != null) {
            Uri photoUri = Uri.parse(photoPath);
            Glide.with(context).load(photoUri).into(photoCircleImageView);
        } else {
            Glide.with(context).load(R.drawable.ic_account_circle_grey600_48dp).into(photoCircleImageView);
        }

        String fullName;
        if(!name.equals("")) {
            if(surname.equals("")) {
                fullName = name;
            } else {
                fullName = name + " " + surname;
            }
        } else {
            fullName = surname;
        }

        nameTextView.setText(fullName);
    }

    static class ViewHolder {
        private CircleImageView photoCircleImageView;
        private TextView nameTextView;

        ViewHolder(View v) {
            photoCircleImageView = (CircleImageView) v.findViewById(R.id.item_teacher_photo);
            nameTextView = (TextView) v.findViewById(R.id.item_teacher_name);
        }
    }
}
