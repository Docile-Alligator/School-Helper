package ml.janewon.schoolhelper;

import android.app.AlarmManager;
import android.app.DialogFragment;
import android.app.PendingIntent;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;


public class AddAssignmentActivity extends AppCompatActivity {

    private Uri mCurrentAssignmentUri;

    CoordinatorLayout coordinatorLayout;
    EditText mTitleEditText;
    EditText mDetailEditText;
    TextView mSubjectTextView;
    TextView mDueDateTextView;
    TextView mDueTimeTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_assignment);
        Intent intent = getIntent();
        mCurrentAssignmentUri = intent.getData();

        coordinatorLayout = (CoordinatorLayout) findViewById(R.id.activity_add_assignment_coordinator_layout);
        mTitleEditText = (EditText) findViewById(R.id.activity_add_assignment_title);
        mDetailEditText = (EditText) findViewById(R.id.activity_add_assignment_detail);
        mSubjectTextView = (TextView) findViewById(R.id.activity_add_assignment_subject);
        mDueDateTextView = (TextView) findViewById(R.id.activity_add_assignment_due_date);
        mDueTimeTextView = (TextView) findViewById(R.id.activity_add_assignment_due_time);

        if(mCurrentAssignmentUri != null) {
            setTitle("Edit Assignment");
            new QueryAssignmentAsyncTask().execute();
        }

        mSubjectTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogFragment subjectPickerFragment = SubjectPickerFragment.newInstance(R.id.activity_add_assignment_subject);
                subjectPickerFragment.show(getFragmentManager(), "subjectPicker");
            }
        });

        mDueDateTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogFragment datePickerFragment = DatePickerFragment.newInstance(R.id.activity_add_assignment_due_date);
                datePickerFragment.show(getFragmentManager(), "datePicker");
            }
        });

        mDueTimeTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogFragment timePickerFragment = TimePickerFragment.newInstance(R.id.activity_add_assignment_due_time);
                timePickerFragment.show(getFragmentManager(), "timePicker");
            }
        });
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        String subject = mSubjectTextView.getText().toString();
        String dueDate = mDueDateTextView.getText().toString();
        String dueTime = mDueTimeTextView.getText().toString();
        outState.putString("subject", subject);
        outState.putString("dueDate", dueDate);
        outState.putString("dueTime", dueTime);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        String subject = savedInstanceState.getString("subject");
        String dueDate = savedInstanceState.getString("dueDate");
        String dueTime = savedInstanceState.getString("dueTime");
        mSubjectTextView.setText(subject);
        mDueDateTextView.setText(dueDate);
        mDueTimeTextView.setText(dueTime);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.button_save_data, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_save:
                //Hide Virtual Keyboard
                InputMethodManager inputManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                inputManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);

                String subject = mSubjectTextView.getText().toString().trim();
                String title = mTitleEditText.getText().toString().trim();

                if(subject.equals("") || title.equals("")) {
                    Snackbar.make(coordinatorLayout, "Subject and title are required", Snackbar.LENGTH_LONG).show();
                    return false;
                }

                String detail = mDetailEditText.getText().toString().trim();
                String dueDate = mDueDateTextView.getText().toString().trim();
                String dueTime = mDueTimeTextView.getText().toString().trim();

                SchoolHelperDatabaseHelper dbHelper = new SchoolHelperDatabaseHelper(this);
                Cursor subjectCursor = dbHelper.getReadableDatabase().query(SchoolHelperDatabaseHelper.TABLE_SUBJECTS,
                        new String[]{SchoolHelperDatabaseHelper.DB_ID},
                        SchoolHelperDatabaseHelper.SUBJECT_NAME + "=?",
                        new String[]{subject},
                        null, null, null);

                ContentValues contentValues = new ContentValues();
                contentValues.put(SchoolHelperDatabaseHelper.ASSIGNMENT_TITLE, title);
                contentValues.put(SchoolHelperDatabaseHelper.ASSIGNMENT_DUE_DATE, dueDate);
                contentValues.put(SchoolHelperDatabaseHelper.ASSIGNMENT_DUE_TIME, dueTime);
                contentValues.put(SchoolHelperDatabaseHelper.ASSIGNMENT_DETAIL, detail);

                if(subjectCursor.moveToFirst()) {
                    int subjectId = subjectCursor.getInt(subjectCursor.getColumnIndexOrThrow(SchoolHelperDatabaseHelper.DB_ID));
                    contentValues.put(SchoolHelperDatabaseHelper.SUBJECT_FOREIGN_KEY, subjectId);
                }

                if(mCurrentAssignmentUri == null) {
                    Uri newUri = getContentResolver().insert(SchoolHelperDatabaseHelper.ASSIGNMENTS_CONTENT_URI, contentValues);

                    if(newUri == null) {
                        Snackbar.make(coordinatorLayout, "Error saving the assignment", Snackbar.LENGTH_LONG).show();
                        return false;
                    } else {
                        if(!(dueDate.equals("") || dueTime.equals(""))) {
                            setNotificationAlarm(dueDate, dueTime, newUri);
                        }
                    }
                } else {
                    int rowsUpdated = getContentResolver().update(mCurrentAssignmentUri, contentValues, null, null);
                    if(!(dueDate.equals("") || dueTime.equals(""))) {
                        AlarmManager mAlarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
                        Intent intent = new Intent (this, NotificationService.class);
                        intent.setData(mCurrentAssignmentUri);
                        intent.putExtra(NotificationUtils.NOTIFICATION_IDENTIFIER, NotificationUtils.ASSIGNMENT_NOTIFICATION_IDENTIFIER);
                        PendingIntent mAlarmIntent = PendingIntent.getService(this, 0, intent, 0);
                        mAlarmManager.cancel(mAlarmIntent);

                        setNotificationAlarm(dueDate, dueTime, mCurrentAssignmentUri);
                    }
                    if(rowsUpdated == 0) {
                        Snackbar.make(coordinatorLayout, "Error updating the assignment", Snackbar.LENGTH_LONG).show();
                        return false;
                    }
                }

                subjectCursor.close();
                dbHelper.close();

                finish();
                return true;
            case android.R.id.home:
                Utils.hideKeyboard(this);
                finish();
                return true;
            default:
                return false;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == SubjectPickerFragment.NEW_SUBJECT && resultCode == RESULT_OK) {
            String newSubject = data.getExtras().getString(SubjectPickerFragment.ADD_SUBJECT_KEY);
            mSubjectTextView.setText(newSubject);
        }
    }

    private void setNotificationAlarm(String date, String time, Uri newUri) {
        if (PreferenceManager.getDefaultSharedPreferences(this).getBoolean(SettingsUtils.NOTIFICATION_SWITCH_KEY, false)) {
            Date dueTime = null;

            try {
                if(date.equals("")) {
                    return;
                } else {
                    if(!time.equals("")) {
                        dueTime = new SimpleDateFormat(DatePickerFragment.DATE_FORMAT + " " + TimePickerFragment.TIME_FORMAT, Locale.getDefault())
                                .parse(date + " " + time);
                    } else {
                        dueTime = new SimpleDateFormat(DatePickerFragment.DATE_FORMAT, Locale.getDefault()).parse(date);
                    }
                }
            } catch (ParseException e) {
                e.printStackTrace();
            }

            Calendar calendar = Calendar.getInstance();
            calendar.setTime(dueTime);
            int timeOffset = Integer.parseInt(PreferenceManager.getDefaultSharedPreferences(this).getString(SettingsUtils.NOTIFICATION_ASSIGNMENT_TIME_KEY, ""));
            calendar.add(Calendar.MINUTE, 0 - timeOffset);

            Calendar now = Calendar.getInstance();
            if(calendar.after(now)) {
                Intent intent = new Intent (this, NotificationService.class);
                intent.setData(newUri);
                intent.putExtra(NotificationUtils.NOTIFICATION_IDENTIFIER, NotificationUtils.ASSIGNMENT_NOTIFICATION_IDENTIFIER);
                PendingIntent mAlarmIntent = PendingIntent.getService(this, 0, intent, 0);

                AlarmManager mAlarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
                mAlarmManager.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), mAlarmIntent);
            }
        }
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
                String subject = Utils.getSubject(AddAssignmentActivity.this, assignmentCursor);

                mTitleEditText.setText(title);
                mDetailEditText.setText(detail);
                mDueDateTextView.setText(dueDate);
                mDueTimeTextView.setText(dueTime);
                mSubjectTextView.setText(subject);
            }
            if(assignmentCursor != null) {
                assignmentCursor.close();
            }
        }
    }
}
