package com.example.psequeda.saccorona;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class solicitudes_pendientes extends AppCompatActivity {

    List<String> groupList;
    List<String> childList;
   // Map<String, List<String>> solicitudesOTs;
    ResponseObject solicitudesOTs;
    ExpandableListView expListView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_solicitudes_pendientes);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

       /* groupList = new ArrayList<String>();
        groupList.add("1* PEDRO SEQUEDA VILLEGAS");
        groupList.add("2* JOSE SEQUEDA");
        String[] hpModels = {"FKJSA FJ ÑLSA JFSAÑFLKJÑLK JSAÑLF JSALF JSAF JÑLSAF JÑLSADF"};
*/
        final String sClaveUsuario=getIntent().getExtras().getString("ClaveUsuario");
        //GetInfSincronizacion

        RequestQueue requestQueue = Volley.newRequestQueue(solicitudes_pendientes.this);
        final ProgressDialog pDialog = new ProgressDialog(solicitudes_pendientes.this);
        pDialog.setMessage("Loading...");
        pDialog.show();
        String tag_json_obj = "rows";
        String JSON_URL = "http://www.servicioscorona.mx/SACCoronaCoatza/JSONDemo/GetOTsUsuario";

        JSONObject params = new JSONObject();
        try {
            params.put("ClaveUsuario",sClaveUsuario);//((EditText)findViewById(R.id.txtusuario)).getText().toString());
            //params.put("ClaCte",((EditText)findViewById(R.id.txtcodigo)).getText().toString());// ((EditText)findViewById(R.id.txtcontrasena)).getText().toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.POST,
                JSON_URL, params,
                new Response.Listener<JSONObject>() {

                    ///CACHAMOS LA RESPUESTA DEL CLIENTE
                    @Override
                    public void onResponse(JSONObject response) {
                        ArrayList<ResponseObject.Rows> lLista = new ArrayList<>();
                        String xprueba="";
                        JSONArray jsonVideoArray = response.optJSONArray("rows");
                        for(int i = 0; i < jsonVideoArray.length(); i++) {

                            JSONObject jsonItems = jsonVideoArray.optJSONObject(i);
                            ResponseObject.Rows  rFILAS = new ResponseObject.Rows();
                            rFILAS.setConcepto(jsonItems.optString("Concepto"));
                            rFILAS.setComentarios(jsonItems.optString("Comentarios"));
                            rFILAS.setOT(jsonItems.optString("OTs"));
                            rFILAS.setDepartamento(jsonItems.optString("Departamento"));
                            rFILAS.setFechaCaptura(jsonItems.optString("FechaCaptura"));
                            rFILAS.setFolioOT(jsonItems.optString("FolioOT"));
                            rFILAS.setClaveUsuario(sClaveUsuario);

                            lLista.add(rFILAS);

                        }

                        ResponseObject rRespuesta = new ResponseObject();
                        rRespuesta.setRows(lLista);
                        solicitudesOTs = rRespuesta;

                        expListView = (ExpandableListView) findViewById(R.id.el_solicitudespendientes);
                        //final AdaptadorSolicitudes expListAdapter = new AdaptadorSolicitudes(
                        //       solicitudes_pendientes.this, groupList, solicitudesOTs);
                        final AdaptadorSolicitudes expListAdapter= new AdaptadorSolicitudes(solicitudes_pendientes.this,solicitudesOTs);
                        expListView.setAdapter(expListAdapter);

                        ExpandableListView mListView = (ExpandableListView) findViewById(R.id.el_solicitudespendientes);
                        // AdaptadorSolicitudes adapters = new AdaptadorSolicitudes(solicitudes_pendientes.this,
                        //        groupList,solicitudesOTs);
                        AdaptadorSolicitudes adapters = new AdaptadorSolicitudes(solicitudes_pendientes.this,solicitudesOTs);
                        mListView.setAdapter(adapters);
                        mListView.setOnGroupClickListener(new ExpandableListView.OnGroupClickListener() {

                            @Override
                            public boolean onGroupClick(ExpandableListView parent, View v,
                                                        int groupPosition, long id) {
                                setListViewHeight(parent, groupPosition);
                                return false;
                            }
                        });

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

    private void setListViewHeight(ExpandableListView listView,
                                   int group) {
        AdaptadorSolicitudes listAdapter = (AdaptadorSolicitudes) listView.getExpandableListAdapter();
        int totalHeight = 0;
        int desiredWidth = View.MeasureSpec.makeMeasureSpec(listView.getWidth(),
                View.MeasureSpec.EXACTLY);
        for (int i = 0; i < listAdapter.getGroupCount(); i++) {
            View groupItem = listAdapter.getGroupView(i, false, null, listView);
            groupItem.measure(desiredWidth, View.MeasureSpec.UNSPECIFIED);

            totalHeight += groupItem.getMeasuredHeight();

            if (((listView.isGroupExpanded(i)) && (i != group))
                    || ((!listView.isGroupExpanded(i)) && (i == group))) {
                for (int j = 0; j < listAdapter.getChildrenCount(i); j++) {
                    View listItem = listAdapter.getChildView(i, j, false, null,
                            listView);
                    listItem.measure(desiredWidth, View.MeasureSpec.UNSPECIFIED);

                    totalHeight += listItem.getMeasuredHeight();

                }
            }
        }

        ViewGroup.LayoutParams params = listView.getLayoutParams();
        int height = totalHeight
                + (listView.getDividerHeight() * (listAdapter.getGroupCount() - 1));
        if (height < 10)
            height = 200;
        params.height = height;
        listView.setLayoutParams(params);
        listView.requestLayout();

    }

}
