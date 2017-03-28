package data.sqllite;

/**
 * Created by psequeda on 16/08/2016.
 */
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.v4.content.WakefulBroadcastReceiver;

public class AlarmReceiver1 extends WakefulBroadcastReceiver {

    private AlarmManager alarmManager;
    private PendingIntent pendingIntent;

    @Override
    public void onReceive(Context context, Intent intent) {

        // Inicia el Servicio
        Intent intentService1 = new Intent(context, BackgroundService.class);
        startWakefulService(context, intentService1);

    }

    public void establecerAlarma(Context context, long intervalo) {

        alarmManager = (AlarmManager) context
                .getSystemService(Context.ALARM_SERVICE);

        Intent intent = new Intent(context, AlarmReceiver1.class);
        pendingIntent = PendingIntent.getBroadcast(context, 0, intent, 0);

        // Establece el Tipo de Alarma
        alarmManager.setInexactRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP,
                0, intervalo, pendingIntent);

    }

    public void cancelarAlarma(Context context) {

        alarmManager = (AlarmManager) context
                .getSystemService(Context.ALARM_SERVICE);

        Intent intent = new Intent(context, AlarmReceiver1.class);
        pendingIntent = PendingIntent.getBroadcast(context, 0, intent, 0);

        alarmManager.cancel(pendingIntent);

    }

}