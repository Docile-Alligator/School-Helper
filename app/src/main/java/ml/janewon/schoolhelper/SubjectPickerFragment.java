package ml.janewon.schoolhelper;


import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.Fragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.widget.TextView;

import java.util.ArrayList;


/**
 * A simple {@link Fragment} subclass.
 */
public class SubjectPickerFragment extends DialogFragment {

    public static final int NEW_SUBJECT = 8;
    public static final String ADD_SUBJECT_KEY = "addSubjectKey";
    private static final String TEXTVIEW_KEY = "textView";
    TextView mSubjectText;

    public static SubjectPickerFragment newInstance(int textView) {
        SubjectPickerFragment subjectPickerFragment = new SubjectPickerFragment();
        Bundle args = new Bundle();
        args.putInt(TEXTVIEW_KEY, textView);
        subjectPickerFragment.setArguments(args);
        return subjectPickerFragment;
    }

    public SubjectPickerFragment() {
        // Required empty public constructor
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        mSubjectText = (TextView) getActivity().findViewById(getArguments().getInt(TEXTVIEW_KEY));

        SchoolHelperDatabaseHelper dbHelper = new SchoolHelperDatabaseHelper(getActivity());
        Cursor cursor = dbHelper.getReadableDatabase().query(SchoolHelperDatabaseHelper.TABLE_SUBJECTS,
                new String[]{SchoolHelperDatabaseHelper.SUBJECT_NAME},
                null, null, null, null, null);
        final ArrayList<String> subjects = new ArrayList<>();
        while(cursor.moveToNext()) {
            String subject = cursor.getString(cursor.getColumnIndexOrThrow(SchoolHelperDatabaseHelper.SUBJECT_NAME));
            subjects.add(subject);
        }
        subjects.add("Create a New Subject...");
        String[] subjectsArray = subjects.toArray(new String[0]);

        builder.setTitle(R.string.fragment_subject_picker_text)
                .setItems(subjectsArray, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if(which == subjects.size() - 1) {
                            Intent intent = new Intent(getActivity(), AddSubjectActivity.class);
                            getActivity().startActivityForResult(intent, NEW_SUBJECT);
                        } else {
                            mSubjectText.setText(subjects.get(which));
                        }
                    }
                });

        cursor.close();
        dbHelper.close();
        return builder.create();
    }
}
