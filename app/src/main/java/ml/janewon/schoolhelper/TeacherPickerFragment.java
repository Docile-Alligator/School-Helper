package ml.janewon.schoolhelper;


import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.Fragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 */
public class TeacherPickerFragment extends DialogFragment {

    public static final int NEW_TEACHER = 4;
    public static final String ADD_TEACHER_KEY = "addTeacherKey";

    TextView mTeacherTextView;

    public static TeacherPickerFragment newInstance(int editText) {
        TeacherPickerFragment teacherPickerFragment = new TeacherPickerFragment();
        Bundle args = new Bundle();
        args.putInt("editText", editText);
        teacherPickerFragment.setArguments(args);
        return teacherPickerFragment;
    }

    public TeacherPickerFragment() {
        // Required empty public constructor
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        mTeacherTextView = (TextView) getActivity().findViewById(getArguments().getInt("editText"));

        SchoolHelperDatabaseHelper dbHelper = new SchoolHelperDatabaseHelper(getActivity());
        Cursor cursor = dbHelper.getReadableDatabase().query(SchoolHelperDatabaseHelper.TABLE_TEACHERS,
                new String[]{SchoolHelperDatabaseHelper.TEACHER_NAME, SchoolHelperDatabaseHelper.TEACHER_SURNAME},
                null, null, null, null, null);

        final ArrayList<String> teachers = new ArrayList<>();
        while (cursor.moveToNext()) {
            String name = cursor.getString(cursor.getColumnIndexOrThrow(SchoolHelperDatabaseHelper.TEACHER_NAME));
            String surname = cursor.getString(cursor.getColumnIndexOrThrow(SchoolHelperDatabaseHelper.TEACHER_SURNAME));

            String fullName;
            if(name.equals("")) {
                fullName = surname;
            } else if(surname.equals("")) {
                fullName = name;
            } else {
                fullName = name + " " + surname;
            }

            teachers.add(fullName);
        }
        teachers.add("Create a new teacher...");
        String[] teachersArray = teachers.toArray(new String[0]);

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Select a teacher").setItems(teachersArray, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if(which == teachers.size() - 1) {
                    Intent intent = new Intent(getActivity(), AddTeacherActivity.class);
                    getActivity().startActivityForResult(intent, NEW_TEACHER);
                } else {
                    mTeacherTextView.setText(teachers.get(which));
                }
            }
        });

        cursor.close();
        dbHelper.close();
        return builder.create();
    }
}
