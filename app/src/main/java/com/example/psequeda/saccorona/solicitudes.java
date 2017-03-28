package com.example.psequeda.saccorona;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ExpandableListActivity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class solicitudes extends  AppCompatActivity {

    List<String> groupList;
    List<String> childList;
    Map<String, List<String>> laptopCollection;
    ExpandableListView expListView;
    String sClaCte;

    String selected;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_solicitudes);

        final String nEmpresa=getIntent().getExtras().getString("ClaEmp");
        final String sClaveUsuario=getIntent().getExtras().getString("ClaveUsuario");



        ///INSERTAMOS LA ORDEN DE TRABAJO
        Button buttonOT = (Button)findViewById(R.id.btnenviarOT);
        buttonOT.setOnClickListener(new Button.OnClickListener() {
                                      public void onClick(View v) {

                                          String sConcepto = ((EditText) findViewById(R.id.txtconcepto)).getText().toString();
                                          String sComentario = ((EditText) findViewById(R.id.txtcomentario)).getText().toString();

                                          if (sConcepto.isEmpty() || sComentario.isEmpty() || sClaveUsuario.isEmpty() || nEmpresa.isEmpty() || sClaCte.isEmpty()) {

                                              AlertDialog.Builder alert = new AlertDialog.Builder(solicitudes.this);
                                              alert.setTitle("SAC");
                                              alert.setMessage("Verifique que todos los campos estes completos!!!");
                                              alert.setPositiveButton("OK", null);
                                              alert.show();
                                              return;
                                          }

                                          RequestQueue requestQueueOT = Volley.newRequestQueue(solicitudes.this);

                                          final ProgressDialog pDialog = new ProgressDialog(solicitudes.this);
                                          pDialog.setMessage("Loading...");
                                          pDialog.show();
                                          String tag_json_obj = "rows";
                                          String JSON_URLOT = "http://www.servicioscorona.mx/SACCoronaCoatza/JSONDemo/InsertarOT";

                                          Spinner cmbdeptoss = (Spinner)findViewById(R.id.cmbdeptos);

                                          JSONObject params = new JSONObject();
                                          try {
                                              params.put("ClaEmp", nEmpresa);//((EditText)findViewById(R.id.txtusuario)).getText().toString());
                                              params.put("ClaCte",sClaCte );
                                              params.put("ClaDepto",cmbdeptoss.getSelectedItemPosition()+1);
                                              params.put("Concepto",sConcepto);
                                              params.put("Comentario",sComentario);
                                              params.put("ClaveUsuario",sClaveUsuario);
                                          } catch (JSONException e) {
                                              e.printStackTrace();
                                          }
                                          JsonObjectRequest jsonObjReqOT = new JsonObjectRequest(Request.Method.POST,
                                                  JSON_URLOT, params,
                                                  new Response.Listener<JSONObject>() {

                                                      ///CACHAMOS LA RESPUESTA DEL CLIENTE
                                                      @Override
                                                      public void onResponse(JSONObject response) {
                                                          pDialog.dismiss();
                                                         response.toString();
                                                          AlertDialog.Builder alert = new AlertDialog.Builder(solicitudes.this);
                                                          alert.setTitle("SAC");
                                                          alert.setMessage("Solicitud Folio: " + response.optString("Folio") + "!!!");
                                                          alert.setPositiveButton("OK", null);
                                                          alert.show();
                                                            finish();
                                                          return;
                                                      }
                                                  }, new Response.ErrorListener() {

                                              @Override
                                              public void onErrorResponse(VolleyError error) {
                                                 // VolleyLog.d("", "Error: " + error.getMessage());
                                                  pDialog.dismiss();
                                                  AlertDialog.Builder alert = new AlertDialog.Builder(solicitudes.this);
                                                  alert.setTitle("SAC");
                                                  alert.setMessage(error.getMessage());
                                                  alert.setPositiveButton("OK", null);
                                                  alert.show();
                                                  return;

                                              }
                                          });

                                          jsonObjReqOT.setShouldCache(false);
                                          requestQueueOT.add(jsonObjReqOT);
                                      }
                                  });


        //LLENAMOS EL SPINNER CON LOS DEPARTAMENTOS

        Spinner spinner = (Spinner) findViewById(R.id.cmbdeptos);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.departamentos, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);

        ///CONSULTA DEL CLIENTE
        Button button = (Button)findViewById(R.id.btnconsultar);
        button.setOnClickListener(new Button.OnClickListener(){
            public void onClick(View v)
            {
                RequestQueue requestQueue = Volley.newRequestQueue(solicitudes.this);
                final ProgressDialog pDialog = new ProgressDialog(solicitudes.this);
                pDialog.setMessage("Loading...");
                pDialog.show();
                String tag_json_obj = "rows";
                String JSON_URL = "http://www.servicioscorona.mx/SACCoronaCoatza/JSONDemo/GetInfoClientess";

                JSONObject params = new JSONObject();
                try {
                    params.put("ClaEmp",nEmpresa);//((EditText)findViewById(R.id.txtusuario)).getText().toString());
                    params.put("ClaCte",((EditText)findViewById(R.id.txtcodigo)).getText().toString());// ((EditText)findViewById(R.id.txtcontrasena)).getText().toString());
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.POST,
                        JSON_URL, params,
                        new Response.Listener<JSONObject>() {

                            ///CACHAMOS LA RESPUESTA DEL CLIENTE
                            @Override
                            public void onResponse(JSONObject response) {
                                String nUsuario= response.optString("NomCte");
                                String nClaCte=response.optString("ClaCte");
                                sClaCte=nClaCte;
                                String nAliasCte=response.optString("AliasCte");
                                String nDireccion=response.optString("Direccion");
                                String nCriterioBoni="Criterio: "+response.optString("CriterioBoni");
                                String nLimCredito = "Limite Credito: "+ response.optString("LimCredito");
                                String nUltimaCompra="Ultima Compra :"+ response.optString("FechaUltimaCompra");
                                pDialog.dismiss();
                                AlertDialog.Builder alert = new AlertDialog.Builder(solicitudes.this);
                                alert.setTitle("SAC");
                                if (nUsuario.compareTo("")!=0)
                                {
                                    //CREAMOS DE NUEVO EL PADRE
                                   // groupList.clear();
                                   // childList.clear();
                                   // laptopCollection.clear();
                            ///LENAOMS EL LISTVIEW CON LOS DATOS DEL CLIENTE
                                    groupList = new ArrayList<String>();
                                    groupList.add(nUsuario);
                                    String[] hpModels = {nClaCte,nAliasCte,nDireccion,nCriterioBoni,nLimCredito,nUltimaCompra};

                                    laptopCollection = new LinkedHashMap<String, List<String>>();

                                    for (String laptop : groupList) {
                                        if (laptop.equals(nUsuario)) {
                                            loadChild(hpModels);
                                        }
                                        laptopCollection.put(laptop, childList);
                                    }
                                    expListView = (ExpandableListView) findViewById(R.id.laptop_list);
                                    final ExpandableListAdapter expListAdapter = new ExpandableListAdapter(
                                            solicitudes.this, groupList, laptopCollection);
                                    expListView.setAdapter(expListAdapter);

                                    expListView.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {

                                        public boolean onChildClick(ExpandableListView parent, View v,
                                                                    int groupPosition, int childPosition, long id) {
                                            selected  = (String) expListAdapter.getChild(
                                                    groupPosition, childPosition);
                                            Toast.makeText(getBaseContext(), selected, Toast.LENGTH_LONG)
                                                    .show();

                                            return true;
                                        }
                                    });

                                    ExpandableListView mListView = (ExpandableListView) findViewById(R.id.laptop_list);
                                    ExpandableListAdapter adapters = new ExpandableListAdapter(solicitudes.this,
                                            groupList,laptopCollection);
                                    mListView.setAdapter(adapters);
                                  /*  mListView.setOnGroupClickListener(new ExpandableListView.OnGroupClickListener() {

                                        @Override
                                        public boolean onGroupClick(ExpandableListView parent, View v,
                                                                    int groupPosition, long id) {
                                            setListViewHeight(parent, groupPosition);
                                            return false;
                                        }
                                    });*/


                                    return;
                                }
                                else
                                {
                                    ///SI NO EXISTE EL CLIENTE QUE SE LLENE EN VACIO
                                    groupList = new ArrayList<String>();
                                    groupList.add("NOEXISTE");
                                    String[] hpModels = {""};

                                    laptopCollection = new LinkedHashMap<String, List<String>>();

                                    for (String laptop : groupList) {
                                        if (laptop.equals("NOEXISTE")) {
                                            loadChild(hpModels);
                                        }
                                        laptopCollection.put(laptop, childList);
                                    }
                                    expListView = (ExpandableListView) findViewById(R.id.laptop_list);
                                    final ExpandableListAdapter expListAdapter = new ExpandableListAdapter(
                                            solicitudes.this, groupList, laptopCollection);
                                    expListView.setAdapter(expListAdapter);

                                  /*  expListView.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {

                                        public boolean onChildClick(ExpandableListView parent, View v,
                                                                    int groupPosition, int childPosition, long id) {
                                            selected  = (String) expListAdapter.getChild(
                                                    groupPosition, childPosition);
                                            Toast.makeText(getBaseContext(), selected, Toast.LENGTH_LONG)
                                                    .show();

                                            return true;
                                        }
                                    });*/



                                    alert.setMessage("Cliente no Registrado!!");
                                    alert.setPositiveButton("OK", null);
                                    alert.show();
                                }
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

        });


    }

    private void createGroupList() {
        groupList = new ArrayList<String>();
       groupList.add("");

    }

    private void createCollection() {
        // preparing laptops collection(child)
        String[] hpModels = { ""};
        /*String[] hpModels = { "HP Pavilion G6-2014TX", "ProBook HP 4540",
                "HP Envy 4-1025TX" };
        String[] hclModels = { "HCL S2101", "HCL L2102", "HCL V2002" };
        String[] lenovoModels = { "IdeaPad Z Series", "Essential G Series",
                "ThinkPad X Series", "Ideapad Z Series" };
        String[] sonyModels = { "VAIO E Series", "VAIO Z Series",
                "VAIO S Series", "VAIO YB Series" };
        String[] dellModels = { "Inspiron", "Vostro", "XPS" };
        String[] samsungModels = { "NP Series", "Series 5", "SF Series" };*/

        laptopCollection = new LinkedHashMap<String, List<String>>();

        for (String laptop : groupList) {
            if (laptop.equals("")) {
                loadChild(hpModels);
            }
            /*else if (laptop.equals("Dell"))
                loadChild(dellModels);
            else if (laptop.equals("Sony"))
                loadChild(sonyModels);
            else if (laptop.equals("HCL"))
                loadChild(hclModels);
            else if (laptop.equals("Samsung"))
                loadChild(samsungModels);
            else
                loadChild(lenovoModels);*/

            laptopCollection.put(laptop, childList);
        }
    }

    private void loadChild(String[] laptopModels) {
        childList = new ArrayList<String>();
        for (String model : laptopModels)
            childList.add(model);
    }

    private void setGroupIndicatorToRight() {
        /* Get the screen width */
        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        int width = dm.widthPixels;

        expListView.setIndicatorBounds(width - getDipsFromPixel(35), width
                - getDipsFromPixel(5));
    }

    // Convert pixel to dip
    public int getDipsFromPixel(float pixels) {
        // Get the screen's density scale
        final float scale = getResources().getDisplayMetrics().density;
        // Convert the dps to pixels, based on density scale
        return (int) (pixels * scale + 0.5f);
    }

    private void setListViewHeight(ExpandableListView listView,
                                   int group) {
        ExpandableListAdapter listAdapter = (ExpandableListAdapter) listView.getExpandableListAdapter();
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
                  //  listItem.measure(desiredWidth, View.MeasureSpec.UNSPECIFIED);

                    listItem.measure(desiredWidth, View.MeasureSpec.AT_MOST);
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
