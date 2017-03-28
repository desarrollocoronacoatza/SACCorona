package com.example.psequeda.saccorona;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

public class MainActivity extends AppCompatActivity {


    public static final String JSON_URL = "http://www.servicioscorona.mx/SACCoronaCoatza/JSONDemo/ValidarUsuario";
    //Context context;
    //private static final int INTERVALO_SERVICE_1 = 600000;    tiempo para que se se envie automaticamente las evaluaciones

    //AlarmReceiver1 mAlarmReceiver1;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        //getApplicationContext().deleteDatabase("SAC.db");

      //context = MainActivity.this;
      //mAlarmReceiver1 = new AlarmReceiver1();
     //mAlarmReceiver1.establecerAlarma(context, INTERVALO_SERVICE_1);


     //   ((EditText)findViewById(R.id.txtusuario)).setText("Pedro Sequeda");
       // ((EditText)findViewById(R.id.txtcontrasena)).setText("yuririabf");

        Button button = (Button)findViewById(R.id.btningresar);

        button.setOnClickListener(
                new Button.OnClickListener() {
                    public void onClick(View v) {

                        String sUsuario=((EditText)findViewById(R.id.txtusuario)).getText().toString();
                        String sContra =((EditText)findViewById(R.id.txtcontrasena)).getText().toString();
                        if (sUsuario.isEmpty() || sContra.isEmpty())
                        {
                            AlertDialog.Builder alert = new AlertDialog.Builder(MainActivity.this);
                            alert.setTitle("SAC");
                            alert.setMessage("Introduce correctamente el usuario y contraseña");
                            alert.setPositiveButton("OK", null);
                            alert.show();
                            return;
                        }
                            ////insertamos registros en la base de datos probando....


                        sendRequest();
                    }
                }
        );
/*
        if(!verificarConexion(this)){
            AlertDialog.Builder alert = new AlertDialog.Builder(MainActivity.this);
            alert.setTitle("SAC");
            alert.setMessage("no tiene conexion a internet, saliendo....");
            alert.setPositiveButton("OK", null);
            alert.show();
            //Toast.makeText(getBaseContext(), "Comprueba tu conexion a internet, saliendo...", Toast.LENGTH_SHORT).show();
            return;
        }
        */
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        switch (item.getItemId()){

            case R.id.action_settings:
                Intent intent = new Intent(this,Info_menu.class);
                startActivity(intent);
                return true;
        }

        return super.onOptionsItemSelected(item);
    }


    private void sendRequest(){

        // Tag used to cancel the request

        try

        {
            RequestQueue requestQueue = Volley.newRequestQueue(this);


            final ProgressDialog pDialog = new ProgressDialog(this);
            pDialog.setMessage("Loading...");
            pDialog.show();

            // Tag used to cancel the request
            String tag_json_obj = "rows";


            JSONObject params = new JSONObject();
            try {
                params.put("sNombreUsuario", ((EditText) findViewById(R.id.txtusuario)).getText().toString());
                params.put("sContrasena", ((EditText) findViewById(R.id.txtcontrasena)).getText().toString());
            } catch (JSONException e) {
                e.printStackTrace();
            }


            JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.POST,
                    JSON_URL, params,
                    new Response.Listener<JSONObject>() {

                        @Override
                        public void onResponse(JSONObject response) {
                            String nUsuario = response.optString("NombreUsuario");
                            String nClaEmp = response.optString("ClaEmp");
                            String nClaveUsuario = response.optString("ClaveUsuario");
                            String nStatusUsuario=response.optString("StatusUsuario");
                            String nPermiso = response.optString("Permiso");
                            pDialog.dismiss();
                            AlertDialog.Builder alert = new AlertDialog.Builder(MainActivity.this);
                            alert.setTitle("SAC");
                            if (nUsuario.compareTo("0") != 0) {

                                if (nStatusUsuario.compareTo("1")==0) {
                                    Intent intent = new Intent(MainActivity.this, menu.class);
                                    intent.putExtra("ClaEmp", nClaEmp);
                                    intent.putExtra("ClaveUsuario", nClaveUsuario);
                                    intent.putExtra("Permiso", nPermiso);
                                    startActivity(intent);
                                    return;
                                }
                            } else

                            {
                                alert.setMessage("Error en Usuario o Contraseña");
                                alert.setPositiveButton("OK", null);
                                alert.show();
                            }

                            // response.getString("internalName");

                            // Log.d("", response.toString());
                            // pDialog.hide();
                        }
                    }, new Response.ErrorListener() {

                @Override
                public void onErrorResponse(VolleyError error) {
                    VolleyLog.d("", "Error: " + error.getMessage());
                     pDialog.hide();
                    Intent intent = new Intent(MainActivity.this, menu.class);
                    intent.putExtra("ClaEmp", "0");
                    intent.putExtra("ClaveUsuario", "0");
                    intent.putExtra("Permiso", "0");
                    startActivity(intent);
                    return;

                }
            }) {

          /*  @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("sNombreUsuario", "ERNESTO");
                params.put("sContrasena", "1987");

                return params;
            }*/

            };

            jsonObjReq.setShouldCache(false);
            requestQueue.add(jsonObjReq);
        }
        catch (Exception ex)
        {
            Intent intent = new Intent(MainActivity.this, menu.class);
            intent.putExtra("ClaEmp", "0");
            intent.putExtra("ClaveUsuario", "0");
            intent.putExtra("Permiso", "0");
            startActivity(intent);
            return;

        }
        finally {

        }
// Adding request to request queue
        // AppController.getInstance().addToRequestQueue(jsonObjReq, tag_json_obj);


// add the request object to the queue to be executed
        // ApplicationController.getInstance().addToRequestQueue(req);
// Adding request to request queue
        // RequestQueue requestQueue = Volley.newRequestQueue(this);
        // requestQueue.add(tag_json_obj);

// Adding request to request queue




        //////////////////////////////////
    /*   StringRequest stringRequest = new StringRequest(JSON_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        showJSON(response);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(MainActivity.this,error.getMessage(),Toast.LENGTH_LONG).show();
                    }
                });


        requestQueue.add(stringRequest);*/
    }


}
