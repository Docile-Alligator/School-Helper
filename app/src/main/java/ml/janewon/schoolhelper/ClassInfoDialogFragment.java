package ml.janewon.schoolhelper;


import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.ContentUris;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.drawable.GradientDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;


/**
 * A simple {@link DialogFragment} subclass.
 */
public class ClassInfoDialogFragment extends DialogFragment {

    int rowId;
    int color;

    private TextView mSubjectTextView;
    private TextView mRoomTextView;
    private TextView mTimeTextView;

    private View mColorView;
    private GradientDrawable mCircleDrawable;

    public ClassInfoDialogFragment() {
        // Required empty public constructor
    }

    public static ClassInfoDialogFragment newInstance(int rowId, int color) {
        ClassInfoDialogFragment classInfoDialogFragment = new ClassInfoDialogFragment();
        Bundle args = new Bundle();
        args.putInt("rowId", rowId);
        args.putInt("color", color);
        classInfoDialogFragment.setArguments(args);
        return classInfoDialogFragment;
    }


    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        rowId = getArguments().getInt("rowId");
        color = getArguments().getInt("color");

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View rootView = inflater.inflate(R.layout.fragment_class_info_dialog, null);

        mSubjectTextView = (TextView) rootView.findViewById(R.id.subject_class_info_dialog_fragment);
        mRoomTextView = (TextView) rootView.findViewById(R.id.room_class_info_dialog_fragment);
        mTimeTextView = (TextView) rootView.findViewById(R.id.time_class_info_dialog_fragment);
        mColorView = rootView.findViewById(R.id.color_view_class_info_dialog_fragment);
        mCircleDrawable = (GradientDrawable) getResources().getDrawable(R.drawable.circle);

        new ClassInfoAsyncTask().execute();

        mCircleDrawable.setColor(color);
        mColorView.setBackground(mCircleDrawable);

        builder.setView(rootView)
                .setPositiveButton(R.string.fragment_class_info_dialog_view_details, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                        Intent intent = new Intent(getActivity(), ViewClassDetailActivity.class);
                        Uri classUri = ContentUris.withAppendedId(SchoolHelperDatabaseHelper.CLASSES_CONTENT_URI, rowId);
                        intent.putExtra(ViewClassDetailActivity.CLASS_SUBJECT_KEY, mSubjectTextView.getText());
                        intent.setData(classUri);
                        startActivity(intent);
                    }
                })
                .setNegativeButton(R.string.fragment_class_info_dialog_cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });

        return builder.create();
    }

    private class ClassInfoAsyncTask extends AsyncTask<Void, Void, Void> {

        Cursor classCursor;
        Cursor subjectCursor;

        @Override
        protected Void doInBackground(Void... voids) {
            Uri classUri = Uri.withAppendedPath(SchoolHelperDatabaseHelper.CLASSES_CONTENT_URI, Integer.toString(rowId));
            String[] projection = new String[]{
                    SchoolHelperDatabaseHelper.DB_ID,
                    SchoolHelperDatabaseHelper.SUBJECT_FOREIGN_KEY,
                    SchoolHelperDatabaseHelper.CLASS_ROOM,
                    SchoolHelperDatabaseHelper.CLASS_TIME_FROM,
                    SchoolHelperDatabaseHelper.CLASS_TIME_TO
            };
            classCursor = getContext().getContentResolver().query(classUri, projection, null, null, null);
            subjectCursor = null;

            if(classCursor != null && classCursor.moveToFirst()) {
                int subjectId = classCursor.getInt(classCursor.getColumnIndexOrThrow(SchoolHelperDatabaseHelper.SUBJECT_FOREIGN_KEY));
                Uri subjectUri = Uri.withAppendedPath(SchoolHelperDatabaseHelper.SUBJECTS_CONTENT_URI, Integer.toString(subjectId));
                subjectCursor = getActivity().getContentResolver().query(subjectUri,
                        new String[]{SchoolHelperDatabaseHelper.SUBJECT_NAME},
                        null, null, null);
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            if(classCursor.moveToFirst()) {
                String room = classCursor.getString(classCursor.getColumnIndexOrThrow(SchoolHelperDatabaseHelper.CLASS_ROOM));
                String timeFrom = classCursor.getString(classCursor.getColumnIndexOrThrow(SchoolHelperDatabaseHelper.CLASS_TIME_FROM));
                String timeTo = classCursor.getString(classCursor.getColumnIndexOrThrow(SchoolHelperDatabaseHelper.CLASS_TIME_TO));
                String time = timeFrom + " ~ " + timeTo;

                if(subjectCursor != null && subjectCursor.moveToFirst()) {
                    String subject = subjectCursor.getString(subjectCursor.getColumnIndexOrThrow(SchoolHelperDatabaseHelper.SUBJECT_NAME));
                    mSubjectTextView.setText(subject);
                }
                if(room.equals("")) {
                    mRoomTextView.setVisibility(View.GONE);
                } else {
                    mRoomTextView.setVisibility(View.VISIBLE);
                    mRoomTextView.setText(room);
                }

                mTimeTextView.setText(time);
            }

            classCursor.close();
            if(subjectCursor != null) {
                subjectCursor.close();
            }
        }
    }
}
