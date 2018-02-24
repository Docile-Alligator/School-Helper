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

public class ViewExamDetailActivity extends AppCompatActivity {

    public static final String EXAM_NAME_KEY = "examName";
    private Uri mCurrentExamUri;

    private CoordinatorLayout mCoordinatorLayout;

    private TextView mSubjectTextView;
    private TextView mTypeTextView;
    private TextView mScoreTextView;
    private TextView mDateTextView;
    private TextView mTimeIntervalTextView;
    private TextView mDetailTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_exam_detail);
        Toolbar toolbar = (Toolbar) findViewById(R.id.activity_view_exam_detail_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Intent intent = getIntent();
        mCurrentExamUri = intent.getData();
        if(savedInstanceState == null && intent.hasExtra(EXAM_NAME_KEY)) {
            setTitle(intent.getExtras().getString(EXAM_NAME_KEY));
        }

        mCoordinatorLayout = (CoordinatorLayout) findViewById(R.id.activity_view_exam_detail_coordinator_layout);

        mSubjectTextView = (TextView) findViewById(R.id.activity_view_exam_subject);
        mTypeTextView = (TextView) findViewById(R.id.activity_view_exam_type);
        mScoreTextView = (TextView) findViewById(R.id.activity_view_exam_score);
        mDateTextView = (TextView) findViewById(R.id.activity_view_exam_date);
        mTimeIntervalTextView = (TextView) findViewById(R.id.activity_view_exam_time_interval);
        mDetailTextView = (TextView) findViewById(R.id.activity_view_exam_detail);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.activity_view_exam_fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ViewExamDetailActivity.this, AddExamActivity.class);
                intent.setData(mCurrentExamUri);
                startActivity(intent);
                finish();
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        new ViewExamAsyncTask().execute();
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
                int rowsDeleted = getContentResolver().delete(mCurrentExamUri, null, null);
                if(rowsDeleted < 1) {
                    Snackbar.make(mCoordinatorLayout, "Error deleting the exam", Snackbar.LENGTH_LONG).show();
                    return false;
                } else {
                    AlarmManager mAlarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
                    Intent intent = new Intent (this, NotificationService.class);
                    intent.setData(mCurrentExamUri);
                    PendingIntent mAlarmIntent = PendingIntent.getBroadcast(this, 0, intent, 0);
                    mAlarmManager.cancel(mAlarmIntent);

                    finish();
                    return true;
                }
            default:
                return false;
        }
    }

    private class ViewExamAsyncTask extends AsyncTask<Void, Void, Void> {
        Cursor examCursor;
        Cursor subjectCursor;

        @Override
        protected Void doInBackground(Void... voids) {
            examCursor = getContentResolver().query(mCurrentExamUri, null, null, null, null);
            subjectCursor = null;
            if(examCursor != null && examCursor.moveToFirst()) {
                int subjectId = examCursor.getInt(examCursor.getColumnIndexOrThrow(SchoolHelperDatabaseHelper.SUBJECT_FOREIGN_KEY));
                Uri subjectUri = Uri.withAppendedPath(SchoolHelperDatabaseHelper.SUBJECTS_CONTENT_URI, Integer.toString(subjectId));
                subjectCursor = getContentResolver().query(subjectUri, new String[]{SchoolHelperDatabaseHelper.SUBJECT_NAME}, null, null, null);
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            if(examCursor.moveToFirst()) {
                String name = examCursor.getString(examCursor.getColumnIndexOrThrow(SchoolHelperDatabaseHelper.EXAM_NAME));
                String type = examCursor.getString(examCursor.getColumnIndexOrThrow(SchoolHelperDatabaseHelper.EXAM_TYPE));
                String score = examCursor.getString(examCursor.getColumnIndexOrThrow(SchoolHelperDatabaseHelper.EXAM_SCORE));
                String date = examCursor.getString(examCursor.getColumnIndexOrThrow(SchoolHelperDatabaseHelper.EXAM_DATE));
                String timeFrom = examCursor.getString(examCursor.getColumnIndexOrThrow(SchoolHelperDatabaseHelper.EXAM_TIME_FROM));
                String timeTo = examCursor.getString(examCursor.getColumnIndexOrThrow(SchoolHelperDatabaseHelper.EXAM_TIME_TO));
                String detail = examCursor.getString(examCursor.getColumnIndexOrThrow(SchoolHelperDatabaseHelper.EXAM_DETAIL));

                if(subjectCursor.moveToFirst()) {
                    String subject = subjectCursor.getString(subjectCursor.getColumnIndexOrThrow(SchoolHelperDatabaseHelper.SUBJECT_NAME));
                    mSubjectTextView.setVisibility(View.VISIBLE);
                    mSubjectTextView.setText(subject);
                }

                if(!name.equals("")) {
                    setTitle(name);
                } else {
                    setTitle(getResources().getString(R.string.activity_view_exam_label));
                }

                String timeInterval = timeFrom + " - " + timeTo;

                mTimeIntervalTextView.setVisibility(View.VISIBLE);
                mTimeIntervalTextView.setText(timeInterval);
                mDateTextView.setVisibility(View.VISIBLE);
                mDateTextView.setText(date);

                if(type.equals("")) {
                    mTypeTextView.setVisibility(View.GONE);
                } else {
                    mTypeTextView.setVisibility(View.VISIBLE);
                    mTypeTextView.setText(type);
                }

                if(score.equals("")) {
                    mScoreTextView.setVisibility(View.GONE);
                } else {
                    mScoreTextView.setVisibility(View.VISIBLE);
                    mScoreTextView.setText(score);
                }


                if(detail.equals("")) {
                    mDetailTextView.setVisibility(View.GONE);
                } else {
                    mDetailTextView.setVisibility(View.VISIBLE);
                    mDetailTextView.setText(detail);
                }
            }
            examCursor.close();
            subjectCursor.close();
        }
    }
}
