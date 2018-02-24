package ml.janewon.schoolhelper;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;

/**
 * Created by alex on 1/21/18.
 */

class NotificationUtils {
    static final String NOTIFICATION_IDENTIFIER = "identifier";
    static final String NOTIFICATION_GROUP_INTENT_KEY = "NGIK";

    static final int NOTIFICATION_GROUP_ASSIGNMENT_ID = 0;
    static final int NOTIFICATION_GROUP_EXAM_ID = 1;
    static final int NOTIFIACATION_GROUP_CLASS_ID = 2;

    static final String NOTIFICATION_ASSIGNMENT_SUBJECT_KEY = "NASK";
    static final String NOTIFICATION_ASSIGNMENT_TITLE_KEY = "NATK";
    static final String NOTIFICATION_ASSIGNMENT_DUE_DATE_KEY = "NADDK";
    static final String NOTIFICATION_ASSIGNMENT_DUE_TIME_KEY = "NADTK";

    static final String NOTIFICATION_EXAM_NAME_KEY = "NENK";
    static final String NOTIFICATION_EXAM_SUBJECT_KEY = "NESK";
    static final String NOTIFICATION_EXAM_DATE_KEY = "NEDK";
    static final String NOTIFICATION_EXAM_TIME_FROM_KEY = "NETFK";
    static final String NOTIFICATION_EXAM_TIME_TO_KEY = "NETTK";

    static final String NOTIFICATION_CLASS_SUBJECT_KEY = "NCSK";
    static final String NOTIFICATION_CLASS_ROOM_KEY = "NCRK";
    static final String NOTIFICATION_CLASS_DAY_KEY = "NCDK";
    static final String NOTIFICATION_CLASS_TIME_FROM_KEY = "NCTFK";
    static final String NOTIFICATION_CLASS_TIME_TO_KEY = "NCTTK";

    static final int NOTIFICATION_ASSIGNMENT_WITHOUT_CURSOR = 10;
    static final int NOTIFICATION_EXAM_WITHOUT_CURSOR = 15;
    static final int NOTIFICATION_CLASS_WITHOUT_CURSOR = 25;

    static final int ASSIGNMENT_NOTIFICATION_IDENTIFIER = 0;
    static final int EXAM_NOTIFICATION_IDENTIFIER = 1;
    static final int CLASS_NOTIFICATION_IDENTIFIER = 2;

    private static PendingIntent getMainActivityPendingIntent(Context context, int categoryId) {
        Intent mainActivityIntent = new Intent(context, MainActivity.class);
        mainActivityIntent.putExtra(NOTIFICATION_GROUP_INTENT_KEY, categoryId);
        TaskStackBuilder mainActivityTB = TaskStackBuilder.create(context);
        mainActivityTB.addNextIntent(mainActivityIntent);
        return mainActivityTB.getPendingIntent(categoryId, PendingIntent.FLAG_UPDATE_CURRENT);
    }

    static void sendAssignmentNotification(Context context, Uri uri, String subject, String title, String dueDate, String dueTime) {
        Intent nextIntent = new Intent(context, ViewAssignmentDetailActivity.class);
        nextIntent.setData(uri);
        TaskStackBuilder taskStackBuilder = TaskStackBuilder.create(context);
        taskStackBuilder.addParentStack(MainActivity.class);
        taskStackBuilder.addNextIntent(nextIntent);
        PendingIntent pendingIntent = taskStackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
        PendingIntent mainActivityPendingIntent = getMainActivityPendingIntent(context, NotificationUtils.NOTIFICATION_GROUP_ASSIGNMENT_ID);

        Notification notification;
        Notification groupBuilder;
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        if(Build.VERSION.SDK_INT > 20) {
            groupBuilder = new Notification.Builder(context)
                    .setContentTitle(subject)
                    .setContentText("Assignment: " + dueTime)
                    .setSubText("Assignment")
                    .setColor(context.getResources().getColor(R.color.colorPrimary))
                    .setGroupSummary(true)
                    .setGroup("ASSIGNMENT")
                    .setSmallIcon(R.mipmap.ic_launcher)
                    .setContentIntent(mainActivityPendingIntent)
                    .setAutoCancel(true)
                    .build();

            notification = new Notification.Builder(context)
                    .setSmallIcon(R.drawable.ic_assignment_black_24dp)
                    .setContentTitle("Assignment: " + subject)
                    .setColor(context.getResources().getColor(R.color.colorPrimary))
                    .setContentText(title + ", Due Time: " + dueTime)
                    .setStyle(new Notification.BigTextStyle().bigText(title + ", Due Time: " + dueTime + "\n" + dueDate))
                    .setContentIntent(pendingIntent)
                    .setAutoCancel(true)
                    .setGroup("ASSIGNMENT")
                    .setPriority(Notification.PRIORITY_MAX)
                    .setDefaults(Notification.DEFAULT_VIBRATE)
                    .setDefaults(Notification.DEFAULT_SOUND)
                    .setShowWhen(true)
                    .build();

            notificationManager.notify(NotificationUtils.NOTIFICATION_GROUP_ASSIGNMENT_ID, groupBuilder);
        } else {
            notification = new Notification.Builder(context)
                    .setSmallIcon(R.drawable.ic_assignment_black_24dp)
                    .setContentTitle(subject)
                    .setSubText("Assignment")
                    .setContentText("Due Time: " + dueDate + " - " + dueTime)
                    .setContentIntent(pendingIntent)
                    .setAutoCancel(true)
                    .setPriority(Notification.PRIORITY_MAX)
                    .setDefaults(Notification.DEFAULT_VIBRATE)
                    .setDefaults(Notification.DEFAULT_SOUND)
                    .build();
        }
        notificationManager.notify((int)(Math.random() * 5000), notification);
    }

    static void sendExamNotification(Context context, Uri uri, String subject, String name, String date, String timeFrom, String timeTo) {
        Intent nextIntent = new Intent(context, ViewExamDetailActivity.class);
        nextIntent.setData(uri);
        TaskStackBuilder taskStackBuilder = TaskStackBuilder.create(context);
        taskStackBuilder.addParentStack(MainActivity.class);
        taskStackBuilder.addNextIntent(nextIntent);
        PendingIntent pendingIntent = taskStackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
        PendingIntent mainActivityPendingIntent = getMainActivityPendingIntent(context, NotificationUtils.NOTIFICATION_GROUP_EXAM_ID);

        Notification notification;
        Notification groupBuilder;
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        if(Build.VERSION.SDK_INT > 20) {
            groupBuilder = new Notification.Builder(context)
                    .setContentTitle(subject)
                    .setContentText("Exam: " + timeFrom + " ~ " + timeTo)
                    .setSubText("Exam")
                    .setColor(context.getResources().getColor(R.color.colorPrimary))
                    .setGroupSummary(true)
                    .setGroup("EXAM")
                    .setSmallIcon(R.mipmap.ic_launcher)
                    .setContentIntent(mainActivityPendingIntent)
                    .setAutoCancel(true)
                    .build();

            notification = new Notification.Builder(context)
                    .setSmallIcon(R.drawable.ic_school_black_24dp)
                    .setContentTitle("Exam: " + subject)
                    .setColor(context.getResources().getColor(R.color.colorPrimary))
                    .setContentText(name + ": " + timeFrom + " ~ " + timeTo)
                    .setStyle(new Notification.BigTextStyle().bigText(name + ": " + timeFrom + " ~ " + timeTo + "\n" + date))
                    .setContentIntent(pendingIntent)
                    .setAutoCancel(true)
                    .setGroup("EXAM")
                    .setPriority(Notification.PRIORITY_MAX)
                    .setDefaults(Notification.DEFAULT_VIBRATE)
                    .setDefaults(Notification.DEFAULT_SOUND)
                    .setShowWhen(true)
                    .build();

            notificationManager.notify(NotificationUtils.NOTIFICATION_GROUP_EXAM_ID, groupBuilder);
        } else {
            notification = new Notification.Builder(context)
                    .setSmallIcon(R.drawable.ic_school_black_24dp)
                    .setContentTitle(subject)
                    .setSubText("Exam")
                    .setContentText(name + "Time: " + date + ", " + timeFrom + " ~ " + timeTo)
                    .setContentIntent(pendingIntent)
                    .setAutoCancel(true)
                    .setPriority(Notification.PRIORITY_MAX)
                    .setDefaults(Notification.DEFAULT_VIBRATE)
                    .setDefaults(Notification.DEFAULT_SOUND)
                    .build();
        }

        notificationManager.notify((int)(Math.random() * 5000), notification);
    }

    static void sendClassNotification(Context context, Uri uri, String subject, String room, String day, String timeFrom, String timeTo) {
        Intent nextIntent = new Intent(context, ViewClassDetailActivity.class);
        nextIntent.setData(uri);
        TaskStackBuilder taskStackBuilder = TaskStackBuilder.create(context);
        taskStackBuilder.addParentStack(MainActivity.class);
        taskStackBuilder.addNextIntent(nextIntent);
        PendingIntent pendingIntent = taskStackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);

        PendingIntent mainActivityPendingIntent = getMainActivityPendingIntent(context, NotificationUtils.NOTIFIACATION_GROUP_CLASS_ID);

        Notification.Builder notification;
        Notification groupBuilder;
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        if(Build.VERSION.SDK_INT > 20) {
            groupBuilder = new Notification.Builder(context)
                    .setContentTitle(subject)
                    .setContentText("Class: " + timeFrom + " ~ " + timeTo)
                    .setSubText("Class")
                    .setColor(context.getResources().getColor(R.color.colorPrimary))
                    .setGroupSummary(true)
                    .setGroup("CLASS")
                    .setSmallIcon(R.mipmap.ic_launcher)
                    .setContentIntent(mainActivityPendingIntent)
                    .setAutoCancel(true)
                    .build();

            notification = new Notification.Builder(context)
                    .setSmallIcon(R.drawable.ic_event_note_black_24dp)
                    .setContentTitle("Class: " + subject)
                    .setColor(context.getResources().getColor(R.color.colorPrimary))
                    .setContentText(timeFrom + " ~ " + timeTo)
                    .setStyle(new Notification.BigTextStyle().bigText(timeFrom + " ~ " + timeTo + "\n" + day + "\n" + room))
                    .setContentIntent(pendingIntent)
                    .setAutoCancel(true)
                    .setGroup("CLASS")
                    .setPriority(Notification.PRIORITY_MAX)
                    .setDefaults(Notification.DEFAULT_VIBRATE)
                    .setDefaults(Notification.DEFAULT_SOUND)
                    .setShowWhen(true);

            if(room.equals("")) {
                notification.setStyle(new Notification.BigTextStyle().bigText(timeFrom + " ~ " + timeTo + "\n" + day));
            } else {
                notification.setStyle(new Notification.BigTextStyle().bigText(timeFrom + " ~ " + timeTo + "\n" + day + "\n" + room));
            }

            notificationManager.notify(NotificationUtils.NOTIFIACATION_GROUP_CLASS_ID, groupBuilder);
        } else {
            notification = new Notification.Builder(context)
                    .setSmallIcon(R.drawable.ic_event_note_black_24dp)
                    .setContentTitle(subject)
                    .setSubText("Class")
                    .setContentIntent(pendingIntent)
                    .setAutoCancel(true)
                    .setPriority(Notification.PRIORITY_MAX)
                    .setDefaults(Notification.DEFAULT_VIBRATE)
                    .setDefaults(Notification.DEFAULT_SOUND);

            if(room.equals("")) {
                notification.setContentText(timeFrom + " ~ " + timeTo);
            } else {
                notification.setContentText(room + ", " + timeFrom + " ~ " + timeTo);
            }
        }

        notificationManager.notify((int)(Math.random() * 5000), notification.build());
    }
}
