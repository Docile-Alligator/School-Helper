package ml.janewon.schoolhelper;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/**
 * Created by alex on 11/9/17.
 */

public class BootCompleteReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        Intent notificationService = new Intent(context, NotificationPendingService.class);
        context.startService(notificationService);
    }
}
