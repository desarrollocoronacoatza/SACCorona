package com.example.psequeda.saccorona;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Typeface;
import android.support.v7.widget.util.SortedListAdapterCallback;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.ImageButton;
import android.widget.TextView;

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
import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by psequeda on 13/11/2015.
 */
public class AdaptadorSolicitudes extends BaseExpandableListAdapter {

    private Activity context;
    //private Context context;
    private List<String> solicitudes;
    private Map<String, List<String>> datossolicitudes;
    private ResponseObject responseList = new ResponseObject();

    public AdaptadorSolicitudes(Context context, ResponseObject groups) {
        this.context = (Activity) context;
        this.responseList = groups;
    }
    @Override
    public long getChildId(int groupPosition, int childPosition) {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        // TODO Auto-generated method stub
       // return null;
      return  responseList.getRows().get(groupPosition);
      //  return responseList.get(groupPosition);
       // ArrayList<ResponseObject> chList = responseList.get(groupPosition);

        //return chList.get(childPosition);
    }

    @Override
    public View getChildView(final int groupPosition, int childPosition,
                             boolean isLastChild, View convertView, ViewGroup parent) {

        String sComentario= responseList.getRows().get(groupPosition).getComentarios();
        final String sOT= responseList.getRows().get(groupPosition).getOT();
        final String sFolioOT= responseList.getRows().get(groupPosition).getFolioOT();
        String sConcepto = responseList.getRows().get(groupPosition).getConcepto();
        String sFecha=responseList.getRows().get(groupPosition).getFechaCaptura();
        String sDepartamento =responseList.getRows().get(groupPosition).getDepartamento();
        final String sClaveUsuario= responseList.getRows().get(groupPosition).getClaveUsuario();
       // String ID = responseList.get(groupPosition).getRows().get(childPosition)
         //       .getOT();
        //String username = responseList.get(groupPosition).getRows()
         //       .get(childPosition).getFolioOT();

        LayoutInflater inflater = context.getLayoutInflater();

        if (convertView == null) {
            convertView = inflater.inflate(R.layout.child_item_solicitudes, null);
        }

        TextView item = (TextView) convertView.findViewById(R.id.lbcomentarioss);
        TextView Concepto = (TextView)convertView.findViewById(R.id.lbconcetoo);
        TextView Fecha= (TextView)convertView.findViewById(R.id.lbfecha);
        TextView Departamento = (TextView) convertView.findViewById(R.id.lbdepartamento);

        item.setText(sComentario);
        Concepto.setText(sConcepto);
        Fecha.setText(sFecha);
        Departamento.setText(sDepartamento);
        Button button = (Button)convertView.findViewById(R.id.btncerrarOT);

        final View finalConvertView = convertView;
        button.setOnClickListener(
                new Button.OnClickListener() {
                    public void onClick(View v) {

                         final String sComentarioCierre = ((EditText) finalConvertView.findViewById(R.id.txtcierre)).getText().toString();
                        if (sComentarioCierre.isEmpty()) {
                            AlertDialog.Builder alert = new AlertDialog.Builder(context);
                            alert.setTitle("SAC");
                            alert.setMessage("!!Introduce el comentario de Cierre!!");
                            alert.setPositiveButton("OK", null);
                            alert.show();
                            return;
                        }

                        AlertDialog.Builder builder = new AlertDialog.Builder(context);
                        builder.setMessage("Desea Cerrar la OT?")
                                .setCancelable(false)
                                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                    public void onClick(final DialogInterface dialog, int id) {
                                        RequestQueue requestQueue = Volley.newRequestQueue(context);
                                        final ProgressDialog pDialog = new ProgressDialog(context);
                                        pDialog.setMessage("Loading...");
                                        pDialog.show();
                                        String tag_json_obj = "rows";
                                        String JSON_URL = "http://www.servicioscorona.mx/SACCoronaCoatza/JSONDemo/CerrarOT";

                                        JSONObject params = new JSONObject();
                                        try {
                                            params.put("ClaveUsuario",sClaveUsuario);
                                            params.put("ComentarioCierre",sComentarioCierre);
                                            params.put("FolioOTs",sFolioOT);
                                            //((EditText)findViewById(R.id.txtusuario)).getText().toString());
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
                                                        pDialog.dismiss();
                                                        if (   response.optString("Folio").compareTo("0")!=0)
                                                        {
                                                            dialog.cancel();
                                                            AlertDialog.Builder alert = new AlertDialog.Builder(context);
                                                            alert.setTitle("SAC");
                                                            alert.setMessage("!!Se Actualizo la informacion!!");
                                                            alert.setPositiveButton("OK", null);
                                                            alert.show();
                                                            responseList.getRows().remove(groupPosition);
                                                            notifyDataSetChanged();

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
                                })
                                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        dialog.cancel();
                                    }
                                });
                        AlertDialog alert = builder.create();
                        alert.show();
                    }
                }
        );


        return convertView;

    }

    @Override
    public int getChildrenCount(int groupPosition) {
        //return responseList.get(groupPosition).getRows().size();
        //return responseList.getRows().size();
        return 1;
    }

    @Override
    public Object getGroup(int groupPosition) {
        // TODO Auto-generated method stub
        return responseList.getRows().get(groupPosition);
    }

    @Override
    public int getGroupCount() {
        return responseList.getRows().size();
    }

    @Override
    public long getGroupId(int groupPosition) {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded,
                             View convertView, ViewGroup parent) {
        ResponseObject.Rows group = (ResponseObject.Rows) getGroup(groupPosition);
        if (convertView == null) {
            LayoutInflater inf = (LayoutInflater) context
                    .getSystemService(context.LAYOUT_INFLATER_SERVICE);
            convertView = inf.inflate(R.layout.group_items_solicitudes, null);
        }
        TextView tv = (TextView) convertView.findViewById(R.id.lbsolicitud);
        tv.setText(group.getOT());
        return convertView;
    }

    @Override
    public boolean hasStableIds() {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public boolean isChildSelectable(int arg0, int arg1) {
        // TODO Auto-generated method stub
        return false;
    }

   /* public AdaptadorSolicitudes(Activity context, List<String> soli_citudes,
                                Map<String, List<String>> datos_solicitudes) {
        this.context = context;
        this.datossolicitudes = datos_solicitudes;
        this.solicitudes = soli_citudes;
    }*/


    /*public Object getChild(int groupPosition, int childPosition) {
        return datossolicitudes.get(solicitudes.get(groupPosition)).get(childPosition);
    }*/

   /* public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }

    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    public int getChildrenCount(int groupPosition) {
        return datossolicitudes.get(solicitudes.get(groupPosition)).size();
    }

    public View getChildView(final int groupPosition, final int childPosition,
                             boolean isLastChild, View convertView, ViewGroup parent) {

       // final ExpandListChild child = (ExpandListChild) getChild(groupPosition, childPosition);

        final String solicitud_detalle = (String) getChild(groupPosition, childPosition);
        LayoutInflater inflater = context.getLayoutInflater();

        if (convertView == null) {
            convertView = inflater.inflate(R.layout.child_item_solicitudes, null);
        }

        TextView item = (TextView) convertView.findViewById(R.id.lbcomentarios);


        item.setText(solicitud_detalle);
        return convertView;
    }



    public Object getGroup(int groupPosition) {
        return solicitudes.get(groupPosition);
    }

    public int getGroupCount() {
        return solicitudes.size();
    }

    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    public View getGroupView(int groupPosition, boolean isExpanded,
                             View convertView, ViewGroup parent) {
        String orden = (String) getGroup(groupPosition);
        if (convertView == null) {
            LayoutInflater infalInflater = (LayoutInflater) context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = infalInflater.inflate(R.layout.group_items_solicitudes,
                    null);
        }
        TextView item = (TextView) convertView.findViewById(R.id.lbsolicitud);
        item.setTypeface(null, Typeface.BOLD);
        item.setText(orden);
        return convertView;
    }





    public boolean hasStableIds() {
        return true;
    }*/
}
