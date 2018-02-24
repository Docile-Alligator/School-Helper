package ml.janewon.schoolhelper;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

public class ViewClassDetailActivity extends AppCompatActivity {

    public static final String CLASS_SUBJECT_KEY = "classSubject";

    private Uri mCurrentClassUri;

    private CoordinatorLayout mCoordinatorLayout;

    private TextView mTeacherTextView;
    private TextView mRoomTextView;
    private TextView mDayTextView;
    private TextView mTimeTextView;
    private TextView mRemarksTextView;
    private View mDivider;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_class_detail);
        Toolbar toolbar = (Toolbar) findViewById(R.id.activity_view_class_detail_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Intent intent = getIntent();
        mCurrentClassUri = intent.getData();
        if(intent.hasExtra(CLASS_SUBJECT_KEY)) {
            setTitle(intent.getExtras().getString(CLASS_SUBJECT_KEY));
        }

        mCoordinatorLayout = (CoordinatorLayout) findViewById(R.id.activity_view_class_detail_coordinator_layout);

        mTeacherTextView = (TextView) findViewById(R.id.activity_view_class_teacher);
        mRoomTextView = (TextView) findViewById(R.id.activity_view_class_room);
        mDayTextView = (TextView) findViewById(R.id.activity_view_class_day);
        mTimeTextView = (TextView) findViewById(R.id.activity_view_class_time);
        mRemarksTextView = (TextView) findViewById(R.id.activity_view_class_remarks);
        mDivider = findViewById(R.id.activity_view_class_divider);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.activity_view_class_fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ViewClassDetailActivity.this, AddClassActivity.class);
                intent.setData(mCurrentClassUri);
                startActivity(intent);
                finish();
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        new QueryClassAsyncTask().execute();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.button_delete_data, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
            case R.id.action_delete:
                int rowsDeleted = getContentResolver().delete(mCurrentClassUri, null, null);
                if(rowsDeleted < 1) {
                    Snackbar.make(mCoordinatorLayout, "Error deleting the class", Snackbar.LENGTH_LONG).show();
                    return false;
                } else {
                    AlarmManager mAlarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
                    Intent intent = new Intent (this, NotificationService.class);
                    intent.setData(mCurrentClassUri);
                    PendingIntent mAlarmIntent = PendingIntent.getBroadcast(this, 0, intent, 0);
                    mAlarmManager.cancel(mAlarmIntent);

                    finish();
                    return true;
                }
            default:
                return false;
        }
    }

    private class QueryClassAsyncTask extends AsyncTask<Void, Void, Void> {

        private Cursor classCursor;
        private Cursor subjectCursor;

        @Override
        protected Void doInBackground(Void... voids) {
            classCursor = getContentResolver().query(mCurrentClassUri, null, null, null, null);
            subjectCursor = null;
            if(classCursor.moveToFirst()) {
                int subjectId = classCursor.getInt(classCursor.getColumnIndexOrThrow(SchoolHelperDatabaseHelper.SUBJECT_FOREIGN_KEY));
                Uri subjectUri = Uri.withAppendedPath(SchoolHelperDatabaseHelper.SUBJECTS_CONTENT_URI, Integer.toString(subjectId));
                subjectCursor = getContentResolver().query(subjectUri, new String[]{SchoolHelperDatabaseHelper.SUBJECT_NAME}, null, null, null);
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            if(classCursor.moveToFirst()) {
                String teacher = classCursor.getString(classCursor.getColumnIndexOrThrow(SchoolHelperDatabaseHelper.CLASS_TEACHER));
                String room = classCursor.getString(classCursor.getColumnIndexOrThrow(SchoolHelperDatabaseHelper.CLASS_ROOM));
                String day = classCursor.getString(classCursor.getColumnIndexOrThrow(SchoolHelperDatabaseHelper.CLASS_DAY));
                String timeFrom = classCursor.getString(classCursor.getColumnIndexOrThrow(SchoolHelperDatabaseHelper.CLASS_TIME_FROM));
                String timeTo = classCursor.getString(classCursor.getColumnIndexOrThrow(SchoolHelperDatabaseHelper.CLASS_TIME_TO));
                String remarks = classCursor.getString(classCursor.getColumnIndexOrThrow(SchoolHelperDatabaseHelper.CLASS_REMARKS));

                if(subjectCursor.moveToFirst()) {
                    String subject = subjectCursor.getString(subjectCursor.getColumnIndexOrThrow(SchoolHelperDatabaseHelper.SUBJECT_NAME));
                    setTitle(subject);
                }


                if(teacher.equals("")) {
                    mTeacherTextView.setVisibility(View.GONE);
                } else {
                    mTeacherTextView.setVisibility(View.VISIBLE);
                    mTeacherTextView.setText(teacher);
                }

                if(room.equals("")) {
                    mRoomTextView.setVisibility(View.GONE);
                } else {
                    mRoomTextView.setVisibility(View.VISIBLE);
                    mRoomTextView.setText(room);
                }

                if(remarks.equals("")) {
                    mDivider.setVisibility(View.GONE);
                    mRemarksTextView.setVisibility(View.GONE);
                } else {
                    mDivider.setVisibility(View.VISIBLE);
                    mRemarksTextView.setVisibility(View.VISIBLE);
                    mRemarksTextView.setText(remarks);
                }

                mDayTextView.setText(day);
                String time = timeFrom + " ~ " + timeTo;
                mTimeTextView.setText(time);
            }

            classCursor.close();
            if(subjectCursor != null) {
                subjectCursor.close();
            }
        }
    }
}
