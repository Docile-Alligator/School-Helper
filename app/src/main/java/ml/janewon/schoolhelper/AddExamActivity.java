package ml.janewon.schoolhelper;

import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.DialogFragment;
import android.app.PendingIntent;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
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

public class AddExamActivity extends AppCompatActivity {

    private Uri mCurrentExamUri;

    EditText mNameEditText;
    TextView mSubjectTextView;
    TextView mDateTextView;
    TextView mTimeFromTextView;
    TextView mTimeToTextView;
    EditText mScoreEditText;
    EditText mTypeEditText;
    EditText mDetailEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_exam);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mNameEditText = (EditText) findViewById(R.id.activity_add_exam_name);
        mSubjectTextView = (TextView) findViewById(R.id.activity_add_exam_subject);
        mDateTextView = (TextView) findViewById(R.id.activity_add_exam_date);
        mTimeFromTextView = (TextView) findViewById(R.id.activity_add_exam_time_from);
        mTimeToTextView = (TextView) findViewById(R.id.activity_add_exam_time_to);
        mScoreEditText = (EditText) findViewById(R.id.activity_add_exam_score);
        mTypeEditText = (EditText) findViewById(R.id.activity_add_exam_type);
        mDetailEditText = (EditText) findViewById(R.id.activity_add_exam_detail);

        mSubjectTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogFragment subjectPickerFragment = SubjectPickerFragment.newInstance(R.id.activity_add_exam_subject);
                subjectPickerFragment.show(getFragmentManager(), "subjectPicker");
            }
        });

        mDateTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogFragment datePickerFragment = DatePickerFragment.newInstance(R.id.activity_add_exam_date);
                datePickerFragment.show(getFragmentManager(), "datePicker");
            }
        });

        mTimeFromTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogFragment timePickerFragment = TimePickerFragment.newInstance(R.id.activity_add_exam_time_from);
                timePickerFragment.show(getFragmentManager(), "timeFromPicker");
            }
        });

        mTimeToTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogFragment timePickerFragment = TimePickerFragment.newInstance(R.id.activity_add_exam_time_to);
                timePickerFragment.show(getFragmentManager(), "timeToPicker");
            }
        });

        Intent intent = getIntent();
        mCurrentExamUri = intent.getData();
        if (mCurrentExamUri != null) {
            setTitle("Edit Exam");
            new QueryExamAsyncTask().execute();
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        String subject = mSubjectTextView.getText().toString();
        String date = mDateTextView.getText().toString();
        String timeFrom = mTimeFromTextView.getText().toString();
        String timeTo = mTimeToTextView.getText().toString();
        outState.putString("subject", subject);
        outState.putString("date", date);
        outState.putString("timeFrom", timeFrom);
        outState.putString("timeTo", timeTo);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        String subject = savedInstanceState.getString("subject");
        String date = savedInstanceState.getString("date");
        String timeFrom = savedInstanceState.getString("timeFrom");
        String timeTo = savedInstanceState.getString("timeTo");
        mSubjectTextView.setText(subject);
        mDateTextView.setText(date);
        mTimeFromTextView.setText(timeFrom);
        mTimeToTextView.setText(timeTo);
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

                String name = mNameEditText.getText().toString().trim();
                String subject = mSubjectTextView.getText().toString().trim();
                String date = mDateTextView.getText().toString().trim();
                String timeFrom = mTimeFromTextView.getText().toString().trim();
                String timeTo = mTimeToTextView.getText().toString().trim();
                String score = mScoreEditText.getText().toString().trim();
                String type = mTypeEditText.getText().toString().trim();
                String detail = mDetailEditText.getText().toString().trim();

                if (date.equals("") || timeFrom.equals("") || timeTo.equals("") || subject.equals("") || name.equals("")) {
                    Snackbar.make(findViewById(R.id.activity_add_exam_coordinator_layout), "Name, subject, date and time are required", Snackbar.LENGTH_LONG).show();
                    return false;
                }

                Date startTime;
                Date endTime;

                try {
                    startTime = new SimpleDateFormat(TimePickerFragment.TIME_FORMAT, Locale.US).parse(timeFrom);
                    endTime = new SimpleDateFormat(TimePickerFragment.TIME_FORMAT, Locale.US).parse(timeTo);
                } catch (ParseException e) {
                    e.printStackTrace();
                    return false;
                }

                if (startTime.after(endTime)) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(this);
                    builder.setMessage("Please change the start time to be before the end time.")
                            .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.cancel();
                                }
                            })
                            .show();

                    return false;
                }

                ContentValues contentValues = new ContentValues();
                contentValues.put(SchoolHelperDatabaseHelper.EXAM_NAME, name);
                contentValues.put(SchoolHelperDatabaseHelper.EXAM_DATE, date);
                contentValues.put(SchoolHelperDatabaseHelper.EXAM_TIME_FROM, timeFrom);
                contentValues.put(SchoolHelperDatabaseHelper.EXAM_TIME_TO, timeTo);
                contentValues.put(SchoolHelperDatabaseHelper.EXAM_SCORE, score);
                contentValues.put(SchoolHelperDatabaseHelper.EXAM_TYPE, type);
                contentValues.put(SchoolHelperDatabaseHelper.EXAM_DETAIL, detail);

                SchoolHelperDatabaseHelper dbHelper = new SchoolHelperDatabaseHelper(this);
                Cursor subjectCursor = dbHelper.getReadableDatabase().query(SchoolHelperDatabaseHelper.TABLE_SUBJECTS,
                        new String[]{SchoolHelperDatabaseHelper.DB_ID},
                        SchoolHelperDatabaseHelper.SUBJECT_NAME + "=?",
                        new String[]{subject},
                        null, null, null);

                if(subjectCursor.moveToFirst()) {
                    int subjectId = subjectCursor.getInt(subjectCursor.getColumnIndexOrThrow(SchoolHelperDatabaseHelper.DB_ID));
                    contentValues.put(SchoolHelperDatabaseHelper.SUBJECT_FOREIGN_KEY, subjectId);
                }

                if (mCurrentExamUri == null) {
                    Uri newUri = getContentResolver().insert(SchoolHelperDatabaseHelper.EXAMS_CONTENT_URI, contentValues);

                    if (newUri == null) {
                        Snackbar.make(getWindow().getDecorView(), "Error saving the exam", Snackbar.LENGTH_LONG).show();
                        return false;
                    } else {
                        setNotificationAlarm(date, timeFrom, newUri);
                    }
                } else {
                    int rowsUpdated = getContentResolver().update(mCurrentExamUri, contentValues, null, null);

                    if (rowsUpdated < 1) {
                        Snackbar.make(getWindow().getDecorView(), "Error updating the exam", Snackbar.LENGTH_LONG).show();
                        return false;
                    } else {
                        AlarmManager mAlarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
                        Intent intent = new Intent (this, NotificationService.class);
                        intent.setData(mCurrentExamUri);
                        PendingIntent mAlarmIntent = PendingIntent.getService(this, 0, intent, 0);
                        mAlarmManager.cancel(mAlarmIntent);

                        setNotificationAlarm(date, timeFrom, mCurrentExamUri);
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

    private class QueryExamAsyncTask extends AsyncTask<Void, Void, Void> {
        private Cursor examCursor;

        @Override
        protected Void doInBackground(Void... voids) {
            if(mCurrentExamUri != null) {
                examCursor = getContentResolver().query(mCurrentExamUri, null, null, null, null);
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            if(examCursor.moveToFirst()) {
                String name = examCursor.getString(examCursor.getColumnIndexOrThrow(SchoolHelperDatabaseHelper.EXAM_NAME));
                String date = examCursor.getString(examCursor.getColumnIndexOrThrow(SchoolHelperDatabaseHelper.EXAM_DATE));
                String timeFrom = examCursor.getString(examCursor.getColumnIndexOrThrow(SchoolHelperDatabaseHelper.EXAM_TIME_FROM));
                String timeTo = examCursor.getString(examCursor.getColumnIndexOrThrow(SchoolHelperDatabaseHelper.EXAM_TIME_TO));
                String score = examCursor.getString(examCursor.getColumnIndexOrThrow(SchoolHelperDatabaseHelper.EXAM_SCORE));
                String type = examCursor.getString(examCursor.getColumnIndexOrThrow(SchoolHelperDatabaseHelper.EXAM_TYPE));
                String detail = examCursor.getString(examCursor.getColumnIndexOrThrow(SchoolHelperDatabaseHelper.EXAM_DETAIL));
                String subject = Utils.getSubject(AddExamActivity.this, examCursor);

                mNameEditText.setText(name);
                mScoreEditText.setText(score);
                mDateTextView.setText(date);
                mTimeFromTextView.setText(timeFrom);
                mTimeToTextView.setText(timeTo);
                mTypeEditText.setText(type);
                mDetailEditText.setText(detail);
                mSubjectTextView.setText(subject);
            }

            examCursor.close();
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
            int timeOffset = Integer.parseInt(PreferenceManager.getDefaultSharedPreferences(this).getString(SettingsUtils.NOTIFICATION_EXAM_TIME_KEY, ""));
            calendar.add(Calendar.MINUTE, 0 - timeOffset);
            Calendar now = Calendar.getInstance();
            if(calendar.after(now)) {
                Intent intent = new Intent (this, NotificationService.class);
                intent.setData(newUri);
                intent.putExtra(NotificationUtils.NOTIFICATION_IDENTIFIER, NotificationUtils.EXAM_NOTIFICATION_IDENTIFIER);
                PendingIntent mAlarmIntent = PendingIntent.getService(this, 0, intent, 0);

                AlarmManager mAlarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
                mAlarmManager.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), mAlarmIntent);
            }
        }
    }
}
