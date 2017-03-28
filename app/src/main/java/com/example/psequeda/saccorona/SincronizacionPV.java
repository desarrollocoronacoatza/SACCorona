package com.example.psequeda.saccorona;

import android.app.ProgressDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ExpandableListView;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

public class SincronizacionPV extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sincronizacion_pv);

        final String sClaveUsuario=getIntent().getExtras().getString("ClaveUsuario");

        //GetInfSincronizacion

        RequestQueue requestQueue = Volley.newRequestQueue(SincronizacionPV.this);
        final ProgressDialog pDialog = new ProgressDialog(SincronizacionPV.this);
        pDialog.setMessage("Loading...");
        pDialog.show();
        String tag_json_obj = "rows";
        String JSON_URL = "http://www.servicioscorona.mx/SACCoronaCoatza/JSONDemo/GetInfSincronizacion";

        JSONObject params = new JSONObject();
        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.POST,
                JSON_URL, params,
                new Response.Listener<JSONObject>() {

                    ///CACHAMOS LA RESPUESTA DEL CLIENTE
                    @Override
                    public void onResponse(JSONObject response) {
                        ArrayList<ResponseObject.Rows> lLista = new ArrayList<>();
                        String xprueba="";
                        String UN="";
                        String NomUN="";
                        String Fecha="";
                        JSONArray jsonVideoArray = response.optJSONArray("rows");
                        String registro="";
                        for(int i = 0; i < jsonVideoArray.length(); i++) {
                            // (JSONObject)people.get(i);
                            JSONObject jsonItems = jsonVideoArray.optJSONObject(i);

                            UN= (String) jsonItems.optString("ClaUn");
                            NomUN=(String)jsonItems.optString("NomUn");
                            Fecha=(String)jsonItems.optString("FechaUltimaSincronizacion");

                          registro=registro.concat(UN.concat(" ").concat(NomUN).concat(" ").concat(Fecha).concat("\n"));

                          //

                        }



                        TextView txtS = (TextView) findViewById(R.id.txtSincronizacion);
                        txtS.setText(registro);
                        String nUsuario="";
                        pDialog.dismiss();
                    }
                }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                pDialog.dismiss();
                VolleyLog.d("", "Error: " + error.getMessage());
                // pDialog.hide();
            }
        });

        jsonObjReq.setShouldCache(false);
        requestQueue.add(jsonObjReq);


    }
}
