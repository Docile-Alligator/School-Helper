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

public class AddClassActivity extends AppCompatActivity {

    private Uri mCurrentEventUri;
    private CoordinatorLayout mCoordinatorLayout;

    TextView mSubjectTextView;
    TextView mTeacherTextView;
    EditText mRoomEditText;
    TextView mDayTextView;
    TextView mTimeFromTextView;
    TextView mTimeToTextView;
    EditText mRemarksEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_class);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Intent intent = getIntent();
        mCurrentEventUri = intent.getData();

        mCoordinatorLayout = (CoordinatorLayout) findViewById(R.id.activity_add_to_timetable_coordinator_layout);

        mSubjectTextView = (TextView) findViewById(R.id.activity_add_to_timetable_subject);
        mTeacherTextView = (TextView) findViewById(R.id.activity_add_to_timetable_teacher);
        mRoomEditText = (EditText) findViewById(R.id.activity_add_to_timetable_room);
        mDayTextView = (TextView) findViewById(R.id.activity_add_to_timetable_day);
        mTimeFromTextView = (TextView) findViewById(R.id.activity_add_to_timetable_time_from);
        mTimeToTextView = (TextView) findViewById(R.id.activity_add_to_timetable_time_to);
        mRemarksEditText = (EditText) findViewById(R.id.activity_add_to_timetable_remarks);

        if(mCurrentEventUri != null) {
            setTitle("Edit Class");
            new QueryClassAsyncTask().execute();
        }

        mSubjectTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogFragment subjectPickerFragment = SubjectPickerFragment.newInstance(R.id.activity_add_to_timetable_subject);
                subjectPickerFragment.show(getFragmentManager(), "subjectPicker");
            }
        });

        mTeacherTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogFragment teacherPickerFragment = TeacherPickerFragment.newInstance(R.id.activity_add_to_timetable_teacher);
                teacherPickerFragment.show(getFragmentManager(), "teacherPicker");
            }
        });

        mDayTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogFragment dayPickerFragment = DayPickerFragment.newInstance(R.id.activity_add_to_timetable_day);
                dayPickerFragment.show(getFragmentManager(), "dayPicker");
            }
        });

        mTimeFromTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogFragment timePickerFragment = TimePickerFragment.newInstance(R.id.activity_add_to_timetable_time_from);
                timePickerFragment.show(getFragmentManager(), "timePicker");
            }
        });

        mTimeToTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogFragment timePickerFragment = TimePickerFragment.newInstance(R.id.activity_add_to_timetable_time_to);
                timePickerFragment.show(getFragmentManager(), "timePicker");
            }
        });
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        String subject = mSubjectTextView.getText().toString();
        String teacher = mTeacherTextView.getText().toString();
        String day = mDayTextView.getText().toString();
        String timeFrom = mTimeFromTextView.getText().toString();
        String timeTo = mTimeToTextView.getText().toString();
        outState.putString("subject", subject);
        outState.putString("teacher", teacher);
        outState.putString("day", day);
        outState.putString("timeFrom", timeFrom);
        outState.putString("timeTo", timeTo);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        String subject = savedInstanceState.getString("subject");
        String teacher = savedInstanceState.getString("teacher");
        String day = savedInstanceState.getString("day");
        String timeFrom = savedInstanceState.getString("timeFrom");
        String timeTo = savedInstanceState.getString("timeTo");
        mSubjectTextView.setText(subject);
        mTeacherTextView.setText(teacher);
        mDayTextView.setText(day);
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

                String subject = mSubjectTextView.getText().toString().trim();
                String teacher = mTeacherTextView.getText().toString().trim();
                String room = mRoomEditText.getText().toString().trim();
                String day = mDayTextView.getText().toString().trim();
                String timeFrom = mTimeFromTextView.getText().toString().trim();
                String timeTo = mTimeToTextView.getText().toString().trim();
                String remarks = mRemarksEditText.getText().toString().trim();

                if(day.equals("") || timeFrom.equals("") || timeTo.equals("") || subject.equals("")) {
                    Snackbar.make(mCoordinatorLayout, "Day, time interval and subject are required", Snackbar.LENGTH_LONG).show();
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

                if(startTime.after(endTime) || endTime.before(startTime)) {
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

                SchoolHelperDatabaseHelper dbHelper = new SchoolHelperDatabaseHelper(this);
                Cursor subjectCursor = dbHelper.getReadableDatabase().query(SchoolHelperDatabaseHelper.TABLE_SUBJECTS,
                        new String[]{SchoolHelperDatabaseHelper.DB_ID, SchoolHelperDatabaseHelper.SUBJECT_COLOR},
                        SchoolHelperDatabaseHelper.SUBJECT_NAME + "=?",
                        new String[]{subject},
                        null, null, null);


                ContentValues contentValues = new ContentValues();
                contentValues.put(SchoolHelperDatabaseHelper.CLASS_TEACHER, teacher);
                contentValues.put(SchoolHelperDatabaseHelper.CLASS_ROOM, room);
                contentValues.put(SchoolHelperDatabaseHelper.CLASS_DAY, day);
                contentValues.put(SchoolHelperDatabaseHelper.CLASS_TIME_FROM, timeFrom);
                contentValues.put(SchoolHelperDatabaseHelper.CLASS_TIME_TO, timeTo);
                contentValues.put(SchoolHelperDatabaseHelper.CLASS_REMARKS, remarks);

                if(subjectCursor.moveToFirst()) {
                    int color = subjectCursor.getInt(subjectCursor.getColumnIndexOrThrow(SchoolHelperDatabaseHelper.SUBJECT_COLOR));
                    contentValues.put(SchoolHelperDatabaseHelper.CLASS_COLOR, color);
                }

                if(subjectCursor.moveToFirst()) {
                    int subjectId = subjectCursor.getInt(subjectCursor.getColumnIndexOrThrow(SchoolHelperDatabaseHelper.DB_ID));
                    contentValues.put(SchoolHelperDatabaseHelper.SUBJECT_FOREIGN_KEY, subjectId);
                }

                if(mCurrentEventUri != null) {
                    int rowsUpdated = getContentResolver().update(mCurrentEventUri, contentValues, null, null);
                    if(rowsUpdated < 1) {
                        Snackbar.make(mCoordinatorLayout, "Error updating the event", Snackbar.LENGTH_LONG).show();
                        return false;
                    } else {
                        AlarmManager mAlarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
                        Intent intent = new Intent (this, NotificationService.class);
                        intent.setData(mCurrentEventUri);
                        PendingIntent mAlarmIntent = PendingIntent.getBroadcast(this, 0, intent, 0);
                        mAlarmManager.cancel(mAlarmIntent);

                        setNotificationAlarm(day, timeFrom, mCurrentEventUri);
                    }
                } else {
                    Uri newUri = getContentResolver().insert(SchoolHelperDatabaseHelper.CLASSES_CONTENT_URI, contentValues);
                    if(newUri == null) {
                        Snackbar.make(mCoordinatorLayout, "Error saving the event", Snackbar.LENGTH_LONG).show();
                        return false;
                    } else {
                        setNotificationAlarm(day, timeFrom, newUri);
                    }
                }

                subjectCursor.close();
                dbHelper.close();

                finish();
                return true;
            case android.R.id.home:
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == SubjectPickerFragment.NEW_SUBJECT && resultCode == RESULT_OK) {
            String newSubject = data.getExtras().getString(SubjectPickerFragment.ADD_SUBJECT_KEY);
            mSubjectTextView.setText(newSubject);
        } else if(requestCode == TeacherPickerFragment.NEW_TEACHER && resultCode == RESULT_OK) {
            String newTeacher = data.getExtras().getString(TeacherPickerFragment.ADD_TEACHER_KEY);
            mTeacherTextView.setText(newTeacher);
        }
    }

    private class QueryClassAsyncTask extends AsyncTask<Void, Void, Void> {
        Cursor classCursor;

        @Override
        protected Void doInBackground(Void... voids) {
            if(mCurrentEventUri != null) {
                classCursor = getContentResolver().query(mCurrentEventUri, null, null, null, null);
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            if(classCursor.moveToFirst()) {
                String room = classCursor.getString(classCursor.getColumnIndexOrThrow(SchoolHelperDatabaseHelper.CLASS_ROOM));
                String day = classCursor.getString(classCursor.getColumnIndexOrThrow(SchoolHelperDatabaseHelper.CLASS_DAY));
                String timeFrom = classCursor.getString(classCursor.getColumnIndexOrThrow(SchoolHelperDatabaseHelper.CLASS_TIME_FROM));
                String timeTo = classCursor.getString(classCursor.getColumnIndexOrThrow(SchoolHelperDatabaseHelper.CLASS_TIME_TO));
                String teacher = classCursor.getString(classCursor.getColumnIndexOrThrow(SchoolHelperDatabaseHelper.CLASS_TEACHER));
                String remarks = classCursor.getString(classCursor.getColumnIndexOrThrow(SchoolHelperDatabaseHelper.CLASS_REMARKS));

                if(classCursor.moveToFirst()) {
                    String subject = Utils.getSubject(AddClassActivity.this, classCursor);
                    mSubjectTextView.setText(subject);
                }

                mRoomEditText.setText(room);
                mDayTextView.setText(day);
                mTimeFromTextView.setText(timeFrom);
                mTimeToTextView.setText(timeTo);
                mTeacherTextView.setText(teacher);
                mRemarksEditText.setText(remarks);
            }

            if(classCursor != null) {
                classCursor.close();
            }
        }
    }

    private void setNotificationAlarm(String day, String time, Uri newUri) {
        if (PreferenceManager.getDefaultSharedPreferences(this).getBoolean(SettingsUtils.NOTIFICATION_SWITCH_KEY, false)) {
            Calendar calendar = Calendar.getInstance();
            try {
                Date tempDayOfWeek = new SimpleDateFormat("EEEE", Locale.getDefault()).parse(day);
                Calendar tempCalendar = Calendar.getInstance();
                tempCalendar.setTime(tempDayOfWeek);
                int dayOfWeek = tempCalendar.get(Calendar.DAY_OF_WEEK);
                calendar.set(Calendar.DAY_OF_WEEK, dayOfWeek);
            } catch (ParseException e) {
                e.printStackTrace();
                return;
            }

            Date timeFrom;

            try {
                if(day.equals("")) {
                    return;
                } else {
                    if(!time.equals("")) {
                        timeFrom = new SimpleDateFormat(TimePickerFragment.TIME_FORMAT, Locale.getDefault())
                                .parse(time);
                    } else {
                        return;
                    }
                }
            } catch (ParseException e) {
                e.printStackTrace();
                return;
            }

            Calendar tempCalendar = Calendar.getInstance();
            tempCalendar.setTime(timeFrom);
            int hour = tempCalendar.get(Calendar.HOUR_OF_DAY);
            int minute = tempCalendar.get(Calendar.MINUTE);

            calendar.set(Calendar.HOUR_OF_DAY, hour);
            calendar.set(Calendar.MINUTE, minute);
            calendar.set(Calendar.SECOND, 0);

            int timeOffset = Integer.parseInt(PreferenceManager.getDefaultSharedPreferences(this).getString(SettingsUtils.NOTIFICATION_CLASS_TIME_KEY, ""));
            calendar.add(Calendar.MINUTE, 0 - timeOffset);

            Calendar now = Calendar.getInstance();

            if(calendar.after(now)) {
                Intent intent = new Intent (this, NotificationService.class);
                intent.setData(newUri);
                intent.putExtra(NotificationUtils.NOTIFICATION_IDENTIFIER, NotificationUtils.CLASS_NOTIFICATION_IDENTIFIER);
                PendingIntent mAlarmIntent = PendingIntent.getService(this, 0, intent, 0);

                AlarmManager mAlarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
                mAlarmManager.setInexactRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), AlarmManager.INTERVAL_DAY, mAlarmIntent);
            }
        }
    }
}
