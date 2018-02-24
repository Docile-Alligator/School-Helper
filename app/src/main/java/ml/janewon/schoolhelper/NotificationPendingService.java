package ml.janewon.schoolhelper;

import android.app.AlarmManager;
import android.app.IntentService;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Build;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;


/**
 * An {@link IntentService} subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 * <p>
 * TODO: Customize class - update intent actions and extra parameters.
 */
public class NotificationPendingService extends IntentService {

    public NotificationPendingService() {
        super("NotificationPendingService");
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        Locale locale = getLocale();
        String dateString = new SimpleDateFormat(DatePickerFragment.DATE_FORMAT, locale).format(Calendar.getInstance().getTime());

        SQLiteDatabase db = new SchoolHelperDatabaseHelper(this).getReadableDatabase();
        String[] assignmentProjection = new String[]{
                SchoolHelperDatabaseHelper.DB_ID, SchoolHelperDatabaseHelper.SUBJECT_FOREIGN_KEY,
                SchoolHelperDatabaseHelper.ASSIGNMENT_TITLE, SchoolHelperDatabaseHelper.ASSIGNMENT_DUE_DATE,
                SchoolHelperDatabaseHelper.ASSIGNMENT_DUE_TIME
        };

        Cursor assignmentsCursor = db.query(SchoolHelperDatabaseHelper.TABLE_ASSIGNMENTS,
                assignmentProjection,
                SchoolHelperDatabaseHelper.ASSIGNMENT_DUE_DATE + "=?",
                new String[]{dateString},
                null, null, null);
        sendAssignmentNotification(assignmentsCursor);

        String[] examProjection = new String[]{
                SchoolHelperDatabaseHelper.DB_ID, SchoolHelperDatabaseHelper.EXAM_NAME,
                SchoolHelperDatabaseHelper.SUBJECT_FOREIGN_KEY, SchoolHelperDatabaseHelper.EXAM_DATE,
                SchoolHelperDatabaseHelper.EXAM_TIME_FROM, SchoolHelperDatabaseHelper.EXAM_TIME_TO
        };

        Cursor examsCursor = db.query(SchoolHelperDatabaseHelper.TABLE_EXAMS,
                examProjection,
                SchoolHelperDatabaseHelper.EXAM_DATE + "=?",
                new String[]{dateString},
                null, null, null);

        sendExamNotification(examsCursor);

        String[] classProjection = new String[]{
                SchoolHelperDatabaseHelper.DB_ID, SchoolHelperDatabaseHelper.SUBJECT_FOREIGN_KEY,
                SchoolHelperDatabaseHelper.CLASS_ROOM, SchoolHelperDatabaseHelper.CLASS_DAY,
                SchoolHelperDatabaseHelper.CLASS_TIME_FROM, SchoolHelperDatabaseHelper.CLASS_TIME_TO
        };

        Cursor classesCursor = db.query(SchoolHelperDatabaseHelper.TABLE_CLASS,
                classProjection,
                null, null, null, null, null);

        sendClassNotification(classesCursor);

        assignmentsCursor.close();
        examsCursor.close();
        classesCursor.close();
        db.close();
    }

    private void sendAssignmentNotification(Cursor cursor) {
        while(cursor.moveToNext()) {
            String subject = Utils.getSubject(this, cursor);
            String title = cursor.getString(cursor.getColumnIndexOrThrow(SchoolHelperDatabaseHelper.ASSIGNMENT_TITLE));
            String dueDate = cursor.getString(cursor.getColumnIndexOrThrow(SchoolHelperDatabaseHelper.ASSIGNMENT_DUE_DATE));
            String dueTimeString = cursor.getString(cursor.getColumnIndexOrThrow(SchoolHelperDatabaseHelper.ASSIGNMENT_DUE_TIME));

            Locale locale = getLocale();
            try {
                Calendar currentTime = Calendar.getInstance();
                //Date dueTime = new SimpleDateFormat(DatePickerFragment.DATE_FORMAT + " " + TimePickerFragment.TIME_FORMAT, locale).parse(dueDate + " " + dueTimeString);
                Calendar dueTime = Calendar.getInstance();
                dueTime.setTime(new SimpleDateFormat(DatePickerFragment.DATE_FORMAT + " " + TimePickerFragment.TIME_FORMAT, locale).parse(dueDate + " " + dueTimeString));
                int timeOffset = Integer.parseInt(PreferenceManager.getDefaultSharedPreferences(this).getString(SettingsUtils.NOTIFICATION_ASSIGNMENT_TIME_KEY, ""));
                dueTime.add(Calendar.MINUTE, 0 - timeOffset);

                if (currentTime.after(dueTime)) {
                    continue;
                }

                int id = cursor.getInt(cursor.getColumnIndexOrThrow(SchoolHelperDatabaseHelper.DB_ID));
                Uri currentUri = Uri.withAppendedPath(SchoolHelperDatabaseHelper.ASSIGNMENTS_CONTENT_URI, Integer.toString(id));

                Intent intent = new Intent(this, NotificationService.class);
                intent.setData(currentUri);
                intent.putExtra(NotificationUtils.NOTIFICATION_IDENTIFIER, NotificationUtils.NOTIFICATION_ASSIGNMENT_WITHOUT_CURSOR);
                intent.putExtra(NotificationUtils.NOTIFICATION_ASSIGNMENT_SUBJECT_KEY, subject);
                intent.putExtra(NotificationUtils.NOTIFICATION_ASSIGNMENT_TITLE_KEY, title);
                intent.putExtra(NotificationUtils.NOTIFICATION_ASSIGNMENT_DUE_DATE_KEY, dueDate);
                intent.putExtra(NotificationUtils.NOTIFICATION_ASSIGNMENT_DUE_TIME_KEY, dueTime);

                AlarmManager mAlarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
                PendingIntent pendingIntent = PendingIntent.getService(getApplicationContext(), 0, intent, 0);

                mAlarmManager.set(AlarmManager.RTC_WAKEUP, dueTime.getTime().getTime(), pendingIntent);

            } catch (ParseException e) {
                e.printStackTrace();
            }

        }
    }

    private void sendExamNotification(Cursor cursor) {
        while(cursor.moveToNext()) {
            String name = cursor.getString(cursor.getColumnIndexOrThrow(SchoolHelperDatabaseHelper.EXAM_NAME));
            String subject = Utils.getSubject(this, cursor);
            String date = cursor.getString(cursor.getColumnIndexOrThrow(SchoolHelperDatabaseHelper.EXAM_DATE));
            String timeFrom = cursor.getString(cursor.getColumnIndexOrThrow(SchoolHelperDatabaseHelper.EXAM_TIME_FROM));
            String timeTo = cursor.getString(cursor.getColumnIndexOrThrow(SchoolHelperDatabaseHelper.EXAM_TIME_TO));

            Locale locale = getLocale();

            try {
                Calendar currentTime = Calendar.getInstance();
                Calendar dueTime = Calendar.getInstance();
                dueTime.setTime(new SimpleDateFormat(DatePickerFragment.DATE_FORMAT + " " + TimePickerFragment.TIME_FORMAT, locale).parse(date + " " + timeFrom));
                int timeOffset = Integer.parseInt(PreferenceManager.getDefaultSharedPreferences(this).getString(SettingsUtils.NOTIFICATION_EXAM_TIME_KEY, ""));
                dueTime.add(Calendar.MINUTE, 0 - timeOffset);
                //Date dueTime = new SimpleDateFormat(DatePickerFragment.DATE_FORMAT + " " + TimePickerFragment.TIME_FORMAT, locale).parse(date + " " + timeFrom);

                if (currentTime.after(dueTime)) {
                    continue;
                }

                int id = cursor.getInt(cursor.getColumnIndexOrThrow(SchoolHelperDatabaseHelper.DB_ID));
                Uri currentUri = Uri.withAppendedPath(SchoolHelperDatabaseHelper.EXAMS_CONTENT_URI, Integer.toString(id));

                Intent intent = new Intent(this, NotificationService.class);
                intent.setData(currentUri);
                intent.putExtra(NotificationUtils.NOTIFICATION_IDENTIFIER, NotificationUtils.NOTIFICATION_EXAM_WITHOUT_CURSOR);
                intent.putExtra(NotificationUtils.NOTIFICATION_EXAM_NAME_KEY, name);
                intent.putExtra(NotificationUtils.NOTIFICATION_EXAM_SUBJECT_KEY, subject);
                intent.putExtra(NotificationUtils.NOTIFICATION_EXAM_DATE_KEY, date);
                intent.putExtra(NotificationUtils.NOTIFICATION_EXAM_TIME_FROM_KEY, timeFrom);
                intent.putExtra(NotificationUtils.NOTIFICATION_EXAM_TIME_TO_KEY, timeTo);

                PendingIntent pendingIntent = PendingIntent.getService(getApplicationContext(), 0, intent, 0);
                AlarmManager mAlarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
                mAlarmManager.set(AlarmManager.RTC_WAKEUP, dueTime.getTime().getTime(), pendingIntent);
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
    }

    private void sendClassNotification(Cursor cursor) {
        while(cursor.moveToNext()) {
            String subject = Utils.getSubject(this, cursor);
            String room = cursor.getString(cursor.getColumnIndexOrThrow(SchoolHelperDatabaseHelper.CLASS_ROOM));
            String day = cursor.getString(cursor.getColumnIndexOrThrow(SchoolHelperDatabaseHelper.CLASS_DAY));
            String timeFrom = cursor.getString(cursor.getColumnIndexOrThrow(SchoolHelperDatabaseHelper.CLASS_TIME_FROM));
            String timeTo = cursor.getString(cursor.getColumnIndexOrThrow(SchoolHelperDatabaseHelper.CLASS_TIME_TO));

            Locale locale = getLocale();

            try {
                Date dayOfWeek = new SimpleDateFormat("EEEE", locale).parse(day);
                Calendar dayOfWeekCalendar = Calendar.getInstance();
                dayOfWeekCalendar.setTime(dayOfWeek);

                int dayOfWeekIndex = dayOfWeekCalendar.get(Calendar.DAY_OF_WEEK);
                int currentDayOfWeek = Calendar.getInstance().get(Calendar.DAY_OF_WEEK);

                Calendar triggerTime = Calendar.getInstance();

                if (dayOfWeekIndex - currentDayOfWeek > 0) {
                    triggerTime.add(Calendar.DAY_OF_WEEK, dayOfWeekIndex - currentDayOfWeek);
                } else if (dayOfWeekIndex - currentDayOfWeek < 0) {
                    triggerTime.add(Calendar.DAY_OF_WEEK, 7 + dayOfWeekIndex - currentDayOfWeek);
                }

                SimpleDateFormat timeFormat = new SimpleDateFormat(TimePickerFragment.TIME_FORMAT, locale);

                Date eventTime = timeFormat.parse(timeFrom);
                Calendar eventTimeCalendar = Calendar.getInstance();
                eventTimeCalendar.setTime(eventTime);

                int hour = eventTimeCalendar.get(Calendar.HOUR_OF_DAY);
                int minute = eventTimeCalendar.get(Calendar.MINUTE);

                triggerTime.set(Calendar.HOUR_OF_DAY, hour);
                triggerTime.set(Calendar.MINUTE, minute);

                int timeOffset = Integer.parseInt(PreferenceManager.getDefaultSharedPreferences(this).getString(SettingsUtils.NOTIFICATION_CLASS_TIME_KEY, ""));
                triggerTime.add(Calendar.MINUTE, 0 - timeOffset);

                Calendar currentTime = Calendar.getInstance();

                if (currentTime.after(triggerTime)) {
                    continue;
                }

                int id = cursor.getInt(cursor.getColumnIndexOrThrow(SchoolHelperDatabaseHelper.DB_ID));
                Uri currentUri = Uri.withAppendedPath(SchoolHelperDatabaseHelper.CLASSES_CONTENT_URI, Integer.toString(id));

                Intent intent = new Intent(this, NotificationService.class);
                intent.setData(currentUri);
                intent.putExtra(NotificationUtils.NOTIFICATION_IDENTIFIER, NotificationUtils.NOTIFICATION_CLASS_WITHOUT_CURSOR);
                intent.putExtra(NotificationUtils.NOTIFICATION_CLASS_SUBJECT_KEY, subject);
                intent.putExtra(NotificationUtils.NOTIFICATION_CLASS_ROOM_KEY, room);
                intent.putExtra(NotificationUtils.NOTIFICATION_CLASS_DAY_KEY, day);
                intent.putExtra(NotificationUtils.NOTIFICATION_CLASS_TIME_FROM_KEY, timeFrom);
                intent.putExtra(NotificationUtils.NOTIFICATION_CLASS_TIME_TO_KEY, timeTo);

                PendingIntent pendingIntent = PendingIntent.getService(getApplicationContext(), 0, intent, 0);
                AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
                alarmManager.set(AlarmManager.RTC_WAKEUP, triggerTime.getTimeInMillis(), pendingIntent);
            } catch (ParseException e) {
                e.printStackTrace();
            }

        }
    }

    private Locale getLocale() {
        Locale locale;
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            locale = getResources().getConfiguration().getLocales().get(0);
        } else {
            locale = getResources().getConfiguration().locale;
        }

        return locale;
    }
}
