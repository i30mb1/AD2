//package n7.ad2.tournaments.internal;
//
//import android.app.NotificationManager;
//import android.app.PendingIntent;
//import android.content.Context;
//import android.content.Intent;
//import android.graphics.Bitmap;
//import android.graphics.BitmapFactory;
//import android.net.Uri;
//
//import androidx.annotation.NonNull;
//import androidx.core.app.NotificationCompat;
//import androidx.work.Worker;
//import androidx.work.WorkerParameters;
//
//import n7.ad2.R;
//import n7.ad2.ui.MainActivity;
//
//import static android.content.Context.NOTIFICATION_SERVICE;
//
//public class ScheduleGameWorker extends Worker {
//    public ScheduleGameWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
//        super(context, workerParams);
//    }
//
//    @NonNull
//    @Override
//    public Result doWork() {
//        String message = getInputData().getString("message");
//
//        Intent notificationIntent = new Intent(getApplicationContext(), MainActivity.class);
//        PendingIntent pendingIntent = PendingIntent.getActivity(getApplicationContext(), 0, notificationIntent, 0);
//
//        BitmapFactory.Options options = new BitmapFactory.Options();
//        Bitmap largeIcon = BitmapFactory.decodeResource(getApplicationContext().getResources(), R.mipmap.ic_launcher_red, options);
//
////        Vibrator v = (Vibrator) getApplicationContext().getSystemService(Context.VIBRATOR_SERVICE);
////        v.vibrate(1000);
//
////        NotificationChannel channel_all = new NotificationChannel("AD2", "AD2", NotificationManager.IMPORTANCE_HIGH);
////        channel_all.enableVibration(true);
//        NotificationCompat.Builder b = new NotificationCompat.Builder(getApplicationContext(), "AD2");
//        b.setAutoCancel(true)
//                .setContentTitle(getApplicationContext().getString(R.string.notification_game_title))
//                .setContentText(message)
//                .setContentIntent(pendingIntent)
//                .setContentInfo(getApplicationContext().getString(R.string.notification_game_info))
//                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
//                .setPriority(NotificationCompat.PRIORITY_HIGH)
//                .setSound(Uri.parse("android.resource://" + getApplicationContext().getPackageName() + "/" + R.raw.does_this_unit_have_a_soul))
//                .setSmallIcon(R.mipmap.ic_launcher_red)
//                .setLargeIcon(largeIcon);
//
//        NotificationManager notificationManager = (NotificationManager) getApplicationContext().getSystemService(NOTIFICATION_SERVICE);
////        NotificationCompat.InboxStyle big = new NotificationCompat.InboxStyle(b);
////        mgr.notify(0, big.setSummaryText("o").addLine("1").addLine("2").build());
//        if (notificationManager != null) {
//            notificationManager.notify(7, b.build());
//            return Result.success();
//        } else return Result.failure();
//    }
//}
