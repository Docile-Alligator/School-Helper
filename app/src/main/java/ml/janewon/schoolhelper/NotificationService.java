package ml.janewon.schoolhelper;

import android.app.IntentService;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;


/**
 * An {@link IntentService} subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 * <p>
 * TODO: Customize class - update intent actions and extra parameters.
 */
public class NotificationService extends IntentService {

    public NotificationService() {
        super("NotificationService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            Uri currentUri = intent.getData();
            String[] projection;
            Cursor cursor;
            switch (intent.getExtras().getInt(NotificationUtils.NOTIFICATION_IDENTIFIER)) {
                case NotificationUtils.ASSIGNMENT_NOTIFICATION_IDENTIFIER:
                    projection = new String[]{SchoolHelperDatabaseHelper.DB_ID,SchoolHelperDatabaseHelper.SUBJECT_FOREIGN_KEY,
                            SchoolHelperDatabaseHelper.ASSIGNMENT_TITLE, SchoolHelperDatabaseHelper.ASSIGNMENT_DUE_DATE,
                            SchoolHelperDatabaseHelper.ASSIGNMENT_DUE_TIME};
                    cursor  = getContentResolver().query(currentUri, projection,
                            null, null, null);

                    if(cursor != null) {
                        while(cursor.moveToNext()) {
                            String subject = Utils.getSubject(this, cursor);
                            String title = cursor.getString(cursor.getColumnIndexOrThrow(SchoolHelperDatabaseHelper.ASSIGNMENT_TITLE));
                            String dueDate = cursor.getString(cursor.getColumnIndexOrThrow(SchoolHelperDatabaseHelper.ASSIGNMENT_DUE_DATE));
                            String dueTime = cursor.getString(cursor.getColumnIndexOrThrow(SchoolHelperDatabaseHelper.ASSIGNMENT_DUE_TIME));

                            if(!subject.equals("")) {
                                NotificationUtils.sendAssignmentNotification(this, currentUri, subject, title, dueDate, dueTime);
                            }
                        }

                        cursor.close();
                    }
                    break;

                case NotificationUtils.EXAM_NOTIFICATION_IDENTIFIER:
                    projection = new String[]{SchoolHelperDatabaseHelper.DB_ID, SchoolHelperDatabaseHelper.EXAM_NAME,
                            SchoolHelperDatabaseHelper.SUBJECT_FOREIGN_KEY, SchoolHelperDatabaseHelper.EXAM_DATE,
                            SchoolHelperDatabaseHelper.EXAM_TIME_FROM, SchoolHelperDatabaseHelper.EXAM_TIME_TO};

                    cursor = getContentResolver().query(currentUri, projection, null, null, null);

                    if(cursor != null) {
                        while(cursor.moveToNext()) {
                            String name = cursor.getString(cursor.getColumnIndexOrThrow(SchoolHelperDatabaseHelper.EXAM_NAME));
                            String subject = Utils.getSubject(this, cursor);
                            String date = cursor.getString(cursor.getColumnIndexOrThrow(SchoolHelperDatabaseHelper.EXAM_DATE));
                            String timeFrom = cursor.getString(cursor.getColumnIndexOrThrow(SchoolHelperDatabaseHelper.EXAM_TIME_FROM));
                            String timeTo = cursor.getString(cursor.getColumnIndexOrThrow(SchoolHelperDatabaseHelper.EXAM_TIME_TO));

                            if(!subject.equals("")) {
                                NotificationUtils.sendExamNotification(this, currentUri, subject, name, date, timeFrom, timeTo);
                            }
                        }

                        cursor.close();
                    }
                    break;

                case NotificationUtils.CLASS_NOTIFICATION_IDENTIFIER:
                    projection = new String[]{SchoolHelperDatabaseHelper.DB_ID, SchoolHelperDatabaseHelper.SUBJECT_FOREIGN_KEY,
                            SchoolHelperDatabaseHelper.CLASS_ROOM, SchoolHelperDatabaseHelper.CLASS_DAY,
                            SchoolHelperDatabaseHelper.CLASS_TIME_FROM, SchoolHelperDatabaseHelper.CLASS_TIME_TO};

                    cursor = getContentResolver().query(currentUri, projection, null, null, null);

                    if(cursor != null) {
                        while(cursor.moveToNext()) {
                            String subject = Utils.getSubject(this, cursor);
                            String room = cursor.getString(cursor.getColumnIndexOrThrow(SchoolHelperDatabaseHelper.CLASS_ROOM));
                            String day = cursor.getString(cursor.getColumnIndexOrThrow(SchoolHelperDatabaseHelper.CLASS_DAY));
                            String timeFrom = cursor.getString(cursor.getColumnIndexOrThrow(SchoolHelperDatabaseHelper.CLASS_TIME_FROM));
                            String timeTo = cursor.getString(cursor.getColumnIndexOrThrow(SchoolHelperDatabaseHelper.CLASS_TIME_TO));

                            if(!subject.equals("")) {
                                NotificationUtils.sendClassNotification(this, currentUri, subject, room, day, timeFrom, timeTo);
                            }
                        }

                        cursor.close();
                    }
                    break;

                case NotificationUtils.NOTIFICATION_ASSIGNMENT_WITHOUT_CURSOR:
                    String subject = intent.getExtras().getString(NotificationUtils.NOTIFICATION_ASSIGNMENT_SUBJECT_KEY);
                    String title = intent.getExtras().getString(NotificationUtils.NOTIFICATION_ASSIGNMENT_TITLE_KEY);
                    String dueDate = intent.getExtras().getString(NotificationUtils.NOTIFICATION_ASSIGNMENT_DUE_DATE_KEY);
                    String dueTime = intent.getExtras().getString(NotificationUtils.NOTIFICATION_ASSIGNMENT_DUE_TIME_KEY);

                    NotificationUtils.sendAssignmentNotification(this, currentUri, subject, title, dueDate, dueTime);
                    break;

                case NotificationUtils.NOTIFICATION_EXAM_WITHOUT_CURSOR:
                    String name = intent.getExtras().getString(NotificationUtils.NOTIFICATION_EXAM_NAME_KEY);
                    String examSubject = intent.getExtras().getString(NotificationUtils.NOTIFICATION_EXAM_SUBJECT_KEY);
                    String date = intent.getExtras().getString(NotificationUtils.NOTIFICATION_EXAM_DATE_KEY);
                    String timeFrom = intent.getExtras().getString(NotificationUtils.NOTIFICATION_EXAM_TIME_FROM_KEY);
                    String timeTo = intent.getExtras().getString(NotificationUtils.NOTIFICATION_EXAM_TIME_TO_KEY);

                    NotificationUtils.sendExamNotification(this, currentUri, examSubject, name, date, timeFrom, timeTo);
                    break;

                case NotificationUtils.NOTIFICATION_CLASS_WITHOUT_CURSOR:
                    String classSubject = intent.getExtras().getString(NotificationUtils.NOTIFICATION_CLASS_SUBJECT_KEY);
                    String room = intent.getExtras().getString(NotificationUtils.NOTIFICATION_CLASS_ROOM_KEY);
                    String day = intent.getExtras().getString(NotificationUtils.NOTIFICATION_CLASS_DAY_KEY);
                    String classTimeFrom = intent.getExtras().getString(NotificationUtils.NOTIFICATION_CLASS_TIME_FROM_KEY);
                    String classTimeTo = intent.getExtras().getString(NotificationUtils.NOTIFICATION_CLASS_TIME_TO_KEY);

                    NotificationUtils.sendClassNotification(this, currentUri, classSubject, room, day, classTimeFrom, classTimeTo);
                    break;
            }
        }
    }
}
