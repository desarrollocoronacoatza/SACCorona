package data.sqllite;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.Intent;
import android.database.DatabaseUtils;
import android.media.RingtoneManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.psequeda.saccorona.MainActivity;
import com.example.psequeda.saccorona.R;
import android.support.v4.app.NotificationCompat.Builder;

public class BackgroundService extends IntentService
{
    private static final String TAG = "BackgroundService";
    Context mContext = null;

    OperacionesBD datos;

    final static int SERVICE_NAME = 1;
    int WORK_TYPE;
    public static final int NOTIFICACION_1_SERVICE_1 = 1;

    public BackgroundService()
    {
        super(TAG);
    }

    @Override
    protected void onHandleIntent(Intent intent)
    {

        try

        {


            datos = OperacionesBD.obtenerInstancia(getApplicationContext());

            Log.d("Mesaageeee", datos.ObtenerEncuestasEnvio().toString());

            String sEncuestas = datos.ObtenerEncuestasEnvio().toString();

            if (!sEncuestas.equals("[]")) {


                ///PROBANDO CADENA JSON
                RequestQueue requestQueue = Volley.newRequestQueue(this);
                String JSON_URL_TIPOSUBENCUESTA_PUNTO = "http://www.servicioscorona.mx/SACCoronaCoatza/JSONDemo/InsertarEncuestasEjecucion";
                JSONObject paramss = new JSONObject();
                try {
                    paramss.put("Cadena", datos.ObtenerEncuestasEnvio().toString());//((EditText)findViewById(R.id.txtusuario)).getText().toString());
                    //params.put("ClaCte",((EditText)findViewById(R.id.txtcodigo)).getText().toString());// ((EditText)findViewById(R.id.txtcontrasena)).getText().toString());
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                JsonObjectRequest jsonTipoSubEncuestaPunto = new JsonObjectRequest(Request.Method.POST, JSON_URL_TIPOSUBENCUESTA_PUNTO, paramss,
                        new Response.Listener<JSONObject>() {
                            public void onResponse(JSONObject response) {
                                JSONArray jsonVideoArray = response.optJSONArray("rows");


                                datos.getDb().beginTransaction();
                                // Inserción Clientes


                                for (int i = 0; i < jsonVideoArray.length(); i++) {
                                    JSONObject jsonItems = jsonVideoArray.optJSONObject(i);

                                    datos.ActualizarStatusEncuesta(jsonItems.optInt("IdEvaluacion"));

                                }
                                datos.getDb().setTransactionSuccessful();
                                datos.getDb().endTransaction();


                            }

                        }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        //  pDialog.dismiss();
                        VolleyLog.d("", "Error: " + error.getMessage());
                        // pDialog.hide();
                    }
                });

                jsonTipoSubEncuestaPunto.setShouldCache(false);
                requestQueue.add(jsonTipoSubEncuestaPunto);

            }

            //Log.d("Msg", datos.ObtenerEncuestasEnvio().toString());
          //  DatabaseUtils.dumpCursor(datos.ObtenerEncuestasEnvio().toString());
        }
        catch (Exception ex)
        {
            VolleyLog.d("", "Error: " + ex.getMessage());
        }
        finally {
           // enviarNotificacion(getApplicationContext(), NOTIFICACION_1_SERVICE_1,
           //       "Titulo", "Contenido", MainActivity.class);

            // Finaliza la Ejecución de un 'startWakefulService' Anterior
            AlarmReceiver1.completeWakefulIntent(intent);
        }

       // enviarNotificacion(getApplicationContext(), NOTIFICACION_1_SERVICE_1,
         //     "Titulo", "Contenido", MainActivity.class);

        // Finaliza la Ejecución de un 'startWakefulService' Anterior
      //  AlarmReceiver1.completeWakefulIntent(intent);



    }



    private void enviarNotificacion(Context context, Integer id, String titulo,
                                    String contenido, Class<?> clase) {

        // Configuración de la Notificación
        Notification.Builder builder = new Notification.Builder(context);
        builder.setAutoCancel(true);
        builder.setContentText(contenido);
        builder.setContentTitle(titulo);
        builder.setSmallIcon(R.drawable.ic_launcher);
        builder.setSound(RingtoneManager
                .getDefaultUri(RingtoneManager.TYPE_NOTIFICATION));
        builder.setVibrate(new long[] { 1000, 1000, 1000, 1000, 1000 });

        // Evita Abrir un Nuevo PendingIntent si ya Existe
        Intent intent = new Intent(context, clase);

        android.support.v4.app.TaskStackBuilder taskStackBuilder = android.support.v4.app.TaskStackBuilder.create(context);
        taskStackBuilder.addNextIntent(intent);
        taskStackBuilder.addParentStack(clase);

        PendingIntent pendingIntent = taskStackBuilder.getPendingIntent(0,
                PendingIntent.FLAG_UPDATE_CURRENT);

        builder.setContentIntent(pendingIntent);

        NotificationManager notificationManager = (NotificationManager) context
                .getSystemService(Context.NOTIFICATION_SERVICE);

        // Crea la Notificación
        notificationManager.notify(id, builder.build());

    }




}
