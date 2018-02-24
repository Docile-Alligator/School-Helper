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

public class ViewAssignmentDetailActivity extends AppCompatActivity {

    public static final String ASSIGNMENT_TITLE_KEY = "assignmentTitle";

    private CoordinatorLayout mCoordinatorLayout;

    private TextView mDetailTextView;
    private TextView mSubjectTextView;
    private TextView mDueTextView;

    private View border;

    private Uri mCurrentAssignmentUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_assignment_detail);
        Toolbar toolbar = (Toolbar) findViewById(R.id.activity_view_assignment_detail_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Intent intent = getIntent();
        mCurrentAssignmentUri = intent.getData();
        if(savedInstanceState == null && intent.hasExtra(ASSIGNMENT_TITLE_KEY)) {
            setTitle(intent.getExtras().getString(ASSIGNMENT_TITLE_KEY));
        }

        mCoordinatorLayout = (CoordinatorLayout) findViewById(R.id.activity_view_assignment_coordinator_layout);

        mDetailTextView = (TextView) findViewById(R.id.activity_view_assignment_detail);
        mSubjectTextView = (TextView) findViewById(R.id.activity_view_assignment_subject);
        mDueTextView = (TextView) findViewById(R.id.activity_view_assignment_due);
        border = findViewById(R.id.activity_view_assignment_detail_border);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.activity_view_assignment_fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ViewAssignmentDetailActivity.this, AddAssignmentActivity.class);
                intent.setData(mCurrentAssignmentUri);
                startActivity(intent);
                finish();
            }
        });

    }

    @Override
    protected void onResume() {
        super.onResume();
        new QueryAssignmentAsyncTask().execute();
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
                int rowsDeleted = getContentResolver().delete(mCurrentAssignmentUri, null, null);
                if(rowsDeleted < 1) {
                    Snackbar.make(mCoordinatorLayout, "Error deleting the assignment.", Snackbar.LENGTH_LONG);
                    return false;
                } else {
                    AlarmManager mAlarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
                    Intent intent = new Intent (this, NotificationService.class);
                    intent.setData(mCurrentAssignmentUri);
                    PendingIntent mAlarmIntent = PendingIntent.getBroadcast(this, 0, intent, 0);
                    mAlarmManager.cancel(mAlarmIntent);

                    finish();
                    return true;
                }
        }
        return super.onOptionsItemSelected(item);
    }

    private class QueryAssignmentAsyncTask extends AsyncTask<Void, Void, Void> {

        Cursor assignmentCursor;

        @Override
        protected Void doInBackground(Void... voids) {
            assignmentCursor = getContentResolver().query(mCurrentAssignmentUri, null, null, null, null);
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            if(assignmentCursor.moveToFirst()) {
                String title = assignmentCursor.getString(assignmentCursor.getColumnIndexOrThrow(SchoolHelperDatabaseHelper.ASSIGNMENT_TITLE));
                String detail = assignmentCursor.getString(assignmentCursor.getColumnIndexOrThrow(SchoolHelperDatabaseHelper.ASSIGNMENT_DETAIL));
                String dueDate = assignmentCursor.getString(assignmentCursor.getColumnIndexOrThrow(SchoolHelperDatabaseHelper.ASSIGNMENT_DUE_DATE));
                String dueTime = assignmentCursor.getString(assignmentCursor.getColumnIndexOrThrow(SchoolHelperDatabaseHelper.ASSIGNMENT_DUE_TIME));
                String subject = Utils.getSubject(ViewAssignmentDetailActivity.this, assignmentCursor);

                setTitle(title);

                if(dueDate.equals("") && dueTime.equals("")) {
                    mDueTextView.setVisibility(View.GONE);
                } else {
                    mDueTextView.setVisibility(View.VISIBLE);
                    String due = dueDate + " " + dueTime;
                    mDueTextView.setText(due);
                }

                if(detail.equals("")) {
                    border.setVisibility(View.GONE);
                    mDetailTextView.setVisibility(View.GONE);
                } else {
                    border.setVisibility(View.VISIBLE);
                    mDetailTextView.setVisibility(View.VISIBLE);
                    mDetailTextView.setText(detail);
                }

                mSubjectTextView.setText(subject);
            }

            if(assignmentCursor != null) {
                assignmentCursor.close();
            }
        }
    }
}
