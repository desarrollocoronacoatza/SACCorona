package com.example.psequeda.saccorona;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import data.modelo.clientes;
import data.sqllite.OperacionesBD;

public class PDV extends AppCompatActivity  {

   OperacionesBD datos;

    int LecturaCodigo;
    int POP=0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pdv);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //OCULTAMOS BOTONES

        final String ClaveUsuario=getIntent().getExtras().getString("ClaveUsuario");
        final String ClaEmpp=getIntent().getExtras().getString("ClaEmp");
        final String nPermiso = getIntent().getExtras().getString("Permiso");


        //LLENAMOS EL SPINNER CON LOS DEPARTAMENTOS

        Spinner spinner = (Spinner) findViewById(R.id.cmbempresa);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.empresas, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);

        //ACTUALIZACION DE LOS CLIENTES DE LA BASE DE DATOS

        datos = OperacionesBD.obtenerInstancia(getApplicationContext());

        if (datos.isTableExists("Parametros2")==true)
        {

            String sexiste="";
        }

//  getApplicationContext().deleteDatabase("SAC.db");
        if (datos.doesDatabaseExist(PDV.this,"SAC.db")==true) {


            if (datos.getNumClientes()<=0)
            //  new TareaPruebaDatos().execute();
            {
                if(!verificarConexion(PDV.this)){
                    AlertDialog.Builder alert = new AlertDialog.Builder(PDV.this);
                    alert.setTitle("SAC");
                    alert.setMessage("Conectate a una red WI-FI para descargar los datos.");
                    alert.setPositiveButton("OK", null);
                    alert.show();

                    return;
                    //Toast.makeText(getBaseContext(), "Comprueba tu conexion a internet, saliendo...", Toast.LENGTH_SHORT).show();

                }

                //ACTUALIZACION DE LA BASE DE DATOS
                RequestQueue requestQueue = Volley.newRequestQueue(PDV.this);
                final ProgressDialog pDialog = new ProgressDialog(PDV.this);
                pDialog.setMessage("Loading...");
                pDialog.show();
                String tag_json_obj = "rows";
                String JSON_URL = "http://www.servicioscorona.mx/SACCoronaCoatza/JSONDemo/GetClientes";
                String JSON_URL_TIPOENCUESTA= "http://www.servicioscorona.mx/SACCoronaCoatza/JSONDemo/GetTipoEvaluacion";
                String JSON_URL_TIPOSUBENCUESTA="http://www.servicioscorona.mx/SACCoronaCoatza/JSONDemo/GetTipoSubEvaluacion";
                String JSON_URL_TIPOSUBENCUESTA_PUNTO="http://www.servicioscorona.mx/SACCoronaCoatza/JSONDemo/GetTipoSubEvaluacionPunto";

                JSONObject paramss = new JSONObject();
                try {
                    paramss.put("ClaEmp", 0);//((EditText)findViewById(R.id.txtusuario)).getText().toString());
                    //params.put("ClaCte",((EditText)findViewById(R.id.txtcodigo)).getText().toString());// ((EditText)findViewById(R.id.txtcontrasena)).getText().toString());
                } catch (JSONException e) {
                    e.printStackTrace();
                }


                //OBTENEMOS LOS PUNTOS DE LA EVALUACION
                JSONObject paramTSubEncuestaPunto = new JSONObject();
                JsonObjectRequest jsonTipoSubEncuestaPunto = new JsonObjectRequest(Request.Method.POST,JSON_URL_TIPOSUBENCUESTA_PUNTO,paramTSubEncuestaPunto,
                        new Response.Listener<JSONObject>()
                        {
                            public void onResponse (JSONObject response)
                            {
                                    JSONArray jsonArray = response.optJSONArray("rows");
                                datos.getDb().beginTransaction();
                                for (int i=0;i<jsonArray.length();i++)
                                {
                                    JSONObject jsonItem = jsonArray.optJSONObject(i);

                                    datos.insertarTipoSubEncuestaPunto(jsonItem.optInt("IdTipoEvaluacion"),jsonItem.optInt("IdTipoSubEvaluacion"),
                                            jsonItem.optInt("IdTipoSubEvaluacionPunto"),jsonItem.optString("NombrePunto"),jsonItem.optInt("ValorMinimo"),
                                            jsonItem.optInt("ValorMaximo"),jsonItem.optInt("TipoEncuesta"));
                                }
                                datos.getDb().setTransactionSuccessful();
                                datos.getDb().endTransaction();
                            }

                        },new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        pDialog.dismiss();
                        VolleyLog.d("", "Error: " + error.getMessage());
                        // pDialog.hide();
                    }
                });


                //OBTENEMOS LOS SUBTIPOS DE ENCUESTA
                JSONObject paramTSubEncuesta = new JSONObject();
                JsonObjectRequest jsonTipoSubEncuesta = new JsonObjectRequest(Request.Method.POST,JSON_URL_TIPOSUBENCUESTA,paramTSubEncuesta,
                        new Response.Listener<JSONObject>(){

                            public void onResponse(JSONObject response){
                            ArrayList<ResponseObject.Rows> lLista = new ArrayList<>();
                                JSONArray jsonArray = response.optJSONArray("rows");
                                datos.getDb().beginTransaction();

                                for (int i=0;i<jsonArray.length();i++)
                                {
                                    JSONObject jsonItem = jsonArray.optJSONObject(i);
                                    datos.insertarTipoSubEncuesta(jsonItem.optInt("IdTipoEvaluacion"),jsonItem.optInt("IdTipoSubEvaluacion"),jsonItem.optString("NombreTipoSubEvaluacion"),jsonItem.optInt("TipoEncuesta"));

                                }

                                    datos.getDb().setTransactionSuccessful();
                                datos.getDb().endTransaction();
                            }

                        },new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        pDialog.dismiss();
                        VolleyLog.d("", "Error: " + error.getMessage());
                        // pDialog.hide();
                    }
                }
                        );


                ///OBTENEMOS LOS TIPOS DE ENCUESTA

                JSONObject paramTEncuesta = new JSONObject();
                JsonObjectRequest jsonTipoEncuesta = new JsonObjectRequest(Request.Method.POST,JSON_URL_TIPOENCUESTA,paramTEncuesta,
                        new Response.Listener<JSONObject>()
                {
                    public void onResponse (JSONObject response){
                        ArrayList<ResponseObject.Rows> lLista = new ArrayList<>();
                        JSONArray jsonArray = response.optJSONArray("rows");
                        datos.getDb().beginTransaction();

                        for (int i=0;i<jsonArray.length();i++)
                        {
                            JSONObject jsonItem = jsonArray.optJSONObject(i);
                            datos.insertarTipoEncuesta(jsonItem.optInt("IdTipoEvaluacion"), jsonItem.optString("NombreTipoEvaluacion"),jsonItem.optInt("TipoEncuesta"));
                        }
                        datos.getDb().setTransactionSuccessful();
                        datos.getDb().endTransaction();
                    }

                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        pDialog.dismiss();
                        VolleyLog.d("", "Error: " + error.getMessage());
                        // pDialog.hide();
                    }
                });


                ///OBTENEMOS LOS CLIENTES
                JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.POST,
                        JSON_URL, paramss,
                        new Response.Listener<JSONObject>() {

                            ///CACHAMOS LA RESPUESTA DEL CLIENTE
                            @Override
                            public void onResponse(JSONObject response) {
                                ArrayList<ResponseObject.Rows> lLista = new ArrayList<>();
                                String xprueba = "";
                                JSONArray jsonVideoArray = response.optJSONArray("rows");
                                datos.getDb().beginTransaction();
                                // Inserción Clientes


                                for (int i = 0; i < jsonVideoArray.length(); i++) {
                                    JSONObject jsonItems = jsonVideoArray.optJSONObject(i);

                                    //db.execSQL("create table clientes(ClaCte text, NomCte text, AliasCte text, Calle text,Col text,ClaZona text,ClaEmp text,ClaCd text,Canal text,Segmento text,NomGiroCom text)");
                                    datos.insertarCliente(new clientes(jsonItems.optInt("ClaCte"), jsonItems.optString("NomCte"), jsonItems.optString("Negocio"), jsonItems.optString("Calle"),
                                            jsonItems.optString("Col"), jsonItems.optString("ClaCd"), jsonItems.optInt("ClaZona"), jsonItems.optInt("ClaEmp"), jsonItems.optString("Canal"),
                                            jsonItems.optString("Segmento"), jsonItems.optString("NomGiroCom"),jsonItems.optInt("Evalua"),jsonItems.optInt("POP")));
                                    //  datos.insertarCliente(new clientes("4", "Pedro", "Sequeda", "Almendras", "Almendros", "Coatza", "1", "2"));

                                }
                                datos.getDb().setTransactionSuccessful();
                                datos.getDb().endTransaction();


                                String nUsuario = "";
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

                jsonObjReq.setShouldCache(false);
                requestQueue.add(jsonTipoEncuesta);

                jsonObjReq.setShouldCache(false);
                requestQueue.add(jsonTipoSubEncuesta);

                jsonObjReq.setShouldCache(false);
                requestQueue.add(jsonTipoSubEncuestaPunto);
            }
        }


        //EVENTO ON SELECTED CHANGED DE EMPRESAS

        ((Spinner)findViewById(R.id.cmbempresa)).setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {

                ((TextView) findViewById(R.id.txtClaCte)).setText("");
                ((TextView) findViewById(R.id.txtNomCte)).setText("");
                ((TextView) findViewById(R.id.txtNegocio)).setText("");
                ((TextView) findViewById(R.id.txtCalle)).setText("");
                ((TextView) findViewById(R.id.txtColonia)).setText("");
                ((TextView) findViewById(R.id.txtZona)).setText("");
                ((TextView) findViewById(R.id.txtCanal)).setText("");
                ((TextView) findViewById(R.id.txtSegmento)).setText("");
                ((TextView) findViewById(R.id.txtGiro)).setText("");
                ((TextView) findViewById(R.id.txtCiudad)).setText("");
                ((Button) findViewById(R.id.btnND)).setVisibility(View.INVISIBLE);
                ((Button) findViewById(R.id.btnEvaluar)).setVisibility(View.INVISIBLE);
                LecturaCodigo = 0;
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                // your code here
            }

        });



        //ENVIAR LA INFORMACION PENDIENTE

        Button btnPendiente = (Button)findViewById(R.id.btnEnviarPendiente);
        btnPendiente.setOnClickListener(new Button.OnClickListener() {

            public void onClick(View v) {

                if(!verificarConexion(PDV.this)){
                    AlertDialog.Builder alert = new AlertDialog.Builder(PDV.this);
                    alert.setTitle("SAC");
                    alert.setMessage("Conectate a una red WI-FI para enviar las evaluaciones.");
                    alert.setPositiveButton("OK", null);
                    alert.show();

                    return;
                    //Toast.makeText(getBaseContext(), "Comprueba tu conexion a internet, saliendo...", Toast.LENGTH_SHORT).show();

                }

                AlertDialog.Builder builder = new AlertDialog.Builder(PDV.this);

                builder.setTitle("Confirmar");
                builder.setMessage("¿Esta Seguro de Enviar la Informacion?");

                builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int which) {

                        // Do nothing, but close the dialog
                        dialog.dismiss();
                        if(datos.ObtenerStatusEnvio()==0){

                          AlertDialog.Builder alert = new AlertDialog.Builder(PDV.this);
                            alert.setTitle("SAC");
                            alert.setMessage("No tiene evaluaciones para enviar.");
                            alert.setPositiveButton("OK", null);
                            alert.show();
                            return;

                        }
                        Button btn = (Button) findViewById(R.id.btnEnviarPendiente);
                        btn.setEnabled(false);

                        //aqui inserto un loading
                        final ProgressDialog pDialog = new ProgressDialog(PDV.this);
                        pDialog.setMessage("Loading...");
                        pDialog.show();
                        datos = OperacionesBD.obtenerInstancia(getApplicationContext());

                        Log.d("Mesaageeee", datos.ObtenerEncuestasEnvio().toString());

                        String sEncuestas = datos.ObtenerEncuestasEnvio().toString();

                        //if (!sEncuestas.equals("[]")) {

                        //cambio prueba mario
                            ///PROBANDO CADENA JSON
                            RequestQueue requestQueue = Volley.newRequestQueue(PDV.this);
                            String JSON_URL_TIPOSUBENCUESTA_PUNTO = "http://www.servicioscorona.mx/SACCoronaCoatza/JSONDemo/InsertarEncuestasEjecucion";
                            JSONObject paramss = new JSONObject();
                            try {
                                paramss.put("Cadena",datos.ObtenerEncuestasEnvio().toString());//((EditText)findViewById(R.id.txtusuario)).getText().toString());
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

                                        //aqui quito el loading
                                            pDialog.dismiss();


                                            AlertDialog.Builder alert = new AlertDialog.Builder(PDV.this);
                                            alert.setTitle("SAC");
                                            alert.setMessage("Se envio La informacion correctamente.");
                                            alert.setPositiveButton("OK", null);
                                            alert.show();

                                            Button btn = (Button) findViewById(R.id.btnEnviarPendiente);
                                            btn.setEnabled(true);
                                            return;
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

                        //}

                        return;

                    }
                });

                builder.setNegativeButton("NO", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // Do nothing
                        dialog.dismiss();
                    }
                });
                AlertDialog alert = builder.create();
                alert.show();

            }
        });



        ///EVENTO PARA IR AL A EVALUACION

        Button btnEvaluar = (Button)findViewById(R.id.btnEvaluar);
        btnEvaluar.setOnClickListener(new Button.OnClickListener(){

            public void onClick (View v){

                /*
                if(!verificarConexion(PDV.this)){
                    AlertDialog.Builder alert = new AlertDialog.Builder(PDV.this);
                    alert.setTitle("SAC");
                    alert.setMessage("Favor de conectarse a una red Wi-Fi....");
                    alert.setPositiveButton("OK", null);
                    alert.show();
                    //Toast.makeText(getBaseContext(), "Comprueba tu conexion a internet, saliendo...", Toast.LENGTH_SHORT).show();
                    return;
                }
*/

                SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy");
                String currentDateandTime = format.format(new Date());

                if (datos.ObtenerNumeroEncuestasCliente((String)(((TextView)findViewById(R.id.txtClaCte)).getText().toString().split(":"))[1].trim(),
                        (String)(((Spinner)findViewById(R.id.cmbempresa)).getSelectedItem().toString().split("-"))[0].trim(),currentDateandTime)>0  )
                {
                    AlertDialog.Builder alert = new AlertDialog.Builder(PDV.this);
                    alert.setTitle("SAC");
                    alert.setMessage("El Cliente ya tiene una encuesta registrada el dia de hoy");
                    alert.setPositiveButton("OK", null);
                    alert.show();
                    return;
                }


                int iTipoEncuesta=0;
                String sGiro=((TextView)findViewById(R.id.txtGiro)).getText().toString();

                if (sGiro.equals("NEGOCIOS PROPIOS"))
                {
                    iTipoEncuesta=2;

                }
                else
                {
                    iTipoEncuesta=1;
                }

                Intent intent = new Intent(PDV.this, PdvEjecucion.class);
                intent.putExtra("CodigoC", (String) (((TextView) findViewById(R.id.txtClaCte)).getText().toString().split(":"))[1].trim());
                intent.putExtra("Cliente",  ((TextView) findViewById(R.id.txtNomCte)).getText());
                intent.putExtra("Zona",(String)(((TextView)findViewById(R.id.txtZona)).getText().toString().split(":"))[1].trim());
                intent.putExtra("ClaveUsuario",ClaveUsuario);
                intent.putExtra("ClaEmp",(String)(((Spinner)findViewById(R.id.cmbempresa)).getSelectedItem().toString().split("-"))[0].trim());
                intent.putExtra("ClaEmpp",ClaEmpp);
                intent.putExtra("LecturaCodigo",LecturaCodigo);
                intent.putExtra("StatusNegocio",1);
                intent.putExtra("Permiso",nPermiso);
                intent.putExtra("Canal",((TextView) findViewById(R.id.txtCanal)).getText());
                intent.putExtra("TipoEncuesta",iTipoEncuesta);
                intent.putExtra("POP",POP);
                LecturaCodigo=0;
                startActivity(intent);
                return;
            }
        });



        //EVENTO PARA CONSULTAR EL CLIENTE EN LA BASE DE DATOS
        Button btBuscar = (Button)findViewById(R.id.btnconsultar);
        btBuscar.setOnClickListener(new Button.OnClickListener() {
            public void onClick(View v) {

                ((TextView) findViewById(R.id.txtClaCte)).setText("");
                ((TextView) findViewById(R.id.txtNomCte)).setText("");
                ((TextView) findViewById(R.id.txtNegocio)).setText("");
                ((TextView) findViewById(R.id.txtCalle)).setText("");
                ((TextView) findViewById(R.id.txtColonia)).setText("");
                ((TextView) findViewById(R.id.txtZona)).setText("");
                ((TextView) findViewById(R.id.txtCanal)).setText("");
                ((TextView) findViewById(R.id.txtSegmento)).setText("");
                ((TextView) findViewById(R.id.txtGiro)).setText("");
                ((TextView) findViewById(R.id.txtCiudad)).setText("");
                ((Button)findViewById(R.id.btnND)).setVisibility(View.INVISIBLE);
                ((Button)findViewById(R.id.btnEvaluar)).setVisibility(View.INVISIBLE);


                //BD basededatos = new BD(getApplicationContext());
                //basededatos.getWritableDatabase();



                //SQLiteDatabase bd = basededatos.getWritableDatabase(); //Create and/or open a database that will be used for reading and writing.
                datos = OperacionesBD.obtenerInstancia(getApplicationContext());
                SQLiteDatabase bd = datos.getDb();

                EditText txtClaCte = (EditText) findViewById(R.id.txtcodigo);



                String Clacte = txtClaCte.getText().toString();

                if (Clacte.equals(""))
                {

                    AlertDialog.Builder alert = new AlertDialog.Builder(PDV.this);
                    alert.setTitle("SAC");
                    alert.setMessage("Introduce o Captura un Codigo de Cliente");
                    alert.setPositiveButton("OK", null);
                    alert.show();
                    return;
                }


                int Evalua=0;

                //(String)(((Spinner)findViewById(R.id.cmbempresa)).getSelectedItem().toString().split("-"))[0];
               // String[] sEmpresa=
                Cursor fila = bd.rawQuery(  //devuelve 0 o 1 fila //es una consulta
                        "select NomCte,AliasCte,Calle,Col,ClaCd,Canal,Segmento,NomGiroCom,ClaZona,Evalua,POP from clientes where ClaCte=" + Clacte + " and ClaEmp="+(String)(((Spinner)findViewById(R.id.cmbempresa)).getSelectedItem().toString().split("-"))[0].trim()+" ", null);
                if (fila.moveToFirst()) {  //si ha devuelto 1 fila, vamos al primero (que es el unico)
                    ((TextView) findViewById(R.id.txtClaCte)).setText("Codigo: "+Clacte);
                    ((TextView) findViewById(R.id.txtNomCte)).setText(fila.getString(0));
                    ((TextView) findViewById(R.id.txtNegocio)).setText(fila.getString(1));
                    ((TextView) findViewById(R.id.txtCalle)).setText(fila.getString(2));
                    ((TextView) findViewById(R.id.txtColonia)).setText(fila.getString(3));
                    ((TextView) findViewById(R.id.txtCiudad)).setText(fila.getString(4));
                    ((TextView) findViewById(R.id.txtCanal)).setText(fila.getString(5));
                    ((TextView) findViewById(R.id.txtSegmento)).setText(fila.getString(6));
                    ((TextView) findViewById(R.id.txtGiro)).setText(fila.getString(7));

                    Evalua=fila.getInt(9);
                    POP=fila.getInt(10);
                    ((TextView) findViewById(R.id.txtZona)).setText("Zona: " + Integer.toString(fila.getInt(8)));

                    if (Evalua!=1) //SI EL CLIENTE ES NO EVALUABLE SE OCULTAN LOS BOTONES PARA EVLUARLO
                    {
                        ((Button) findViewById(R.id.btnND)).setVisibility(View.VISIBLE);
                        ((Button) findViewById(R.id.btnEvaluar)).setVisibility(View.VISIBLE);

                        ((TextView) findViewById(R.id.txtNomCte)).requestFocus();
                    }
                    else //SE ENVIA UN MENSAJE DE QUE EL CLIENTE ES NO EVALUABLE
                    {

                        AlertDialog.Builder alert = new AlertDialog.Builder(PDV.this);
                        alert.setTitle("SAC");
                        alert.setMessage("El Cliente Seleccionado no se debe Evaluar...");
                        alert.setPositiveButton("OK", null);
                        alert.show();
                        return;
                    }
                } else {

                    ((Button)findViewById(R.id.btnND)).setVisibility(View.INVISIBLE);
                    ((Button)findViewById(R.id.btnEvaluar)).setVisibility(View.INVISIBLE);


                    AlertDialog.Builder alert = new AlertDialog.Builder(PDV.this);
                    alert.setTitle("SAC");
                    alert.setMessage("El Codigo de Cliente No Existe en la BD...");
                    alert.setPositiveButton("OK", null);
                    alert.show();
                    return;
                }

                bd.close();

            }

        });


        ///EVENTO PARA NEGOCIO CERRADO
        Button btnND = (Button)findViewById(R.id.btnND);
        btnND.setOnClickListener(new Button.OnClickListener(){
            public void onClick(View v)
            {
                AlertDialog.Builder builder = new AlertDialog.Builder(PDV.this);



                   Log.d("Clientes", "Clientes");
                   DatabaseUtils.dumpCursor(datos.obtenerClientes());

                //VALIDAMOS QUE NO TENGA UNA ENCUESTA CAPTURADA EL CLIENTE

                SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy");
                String currentDateandTime = format.format(new Date());

                if (datos.ObtenerNumeroEncuestasCliente((String)(((TextView)findViewById(R.id.txtClaCte)).getText().toString().split(":"))[1].trim(),
                        (String)(((Spinner)findViewById(R.id.cmbempresa)).getSelectedItem().toString().split("-"))[0].trim(),currentDateandTime)>0  )
                {
                    AlertDialog.Builder alert = new AlertDialog.Builder(PDV.this);
                    alert.setTitle("SAC");
                    alert.setMessage("El Cliente ya tiene una encuesta registrada el dia de hoy");
                    alert.setPositiveButton("OK", null);
                    alert.show();
                    return;
                }



                builder.setTitle("Confirmar");
                builder.setMessage("¿Esta Seguro de Enviar la Informacion Negocio Cerrado?");

                builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int which) {

                        // Do nothing, but close the dialog
                        dialog.dismiss();


                        //OBTENEMOS EL TIPO DE ENCUESTA

                        int iTipoEncuesta = 0;
                        if (((TextView) findViewById(R.id.txtGiro)).getText().equals("OTROS (NEGOCIOS PROPIOS)")) {
                            iTipoEncuesta = 2;

                        } else {
                            iTipoEncuesta = 1;
                        }


                        SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy");
                        String currentDateandTime = format.format(new Date());
                        //INSERTAMOS LA ENCUESTA DE NEGOCIO CERRADO
                        int IdEncuesta = datos.InsertarEncuestaEnCabezado(Integer.parseInt((String) (((TextView) findViewById(R.id.txtClaCte)).getText().toString().split(":"))[1].trim()),
                                currentDateandTime, Integer.parseInt((String) (((TextView) findViewById(R.id.txtZona)).getText().toString().split(":"))[1].trim()),
                                "NEGOCIO CERRADO", Integer.parseInt(ClaveUsuario),
                                Integer.parseInt((String) (((Spinner) findViewById(R.id.cmbempresa)).getSelectedItem().toString().split("-"))[0].trim()),
                                0, LecturaCodigo, iTipoEncuesta);
                        //INERTAMOS EL DETALLE DE LA ENCUESTA CON UN VALOR 0 PARA SER ENVIAADA
                        datos.InsertarEncuestaDetalle(IdEncuesta, 1, 0);
                        datos.InsetarEncuestaCompleta(IdEncuesta);

                        ((TextView) findViewById(R.id.txtClaCte)).setText("");
                        ((TextView) findViewById(R.id.txtNomCte)).setText("");
                        ((TextView) findViewById(R.id.txtNegocio)).setText("");
                        ((TextView) findViewById(R.id.txtCalle)).setText("");
                        ((TextView) findViewById(R.id.txtColonia)).setText("");
                        ((TextView) findViewById(R.id.txtZona)).setText("");
                        ((TextView) findViewById(R.id.txtCanal)).setText("");
                        ((TextView) findViewById(R.id.txtSegmento)).setText("");
                        ((TextView) findViewById(R.id.txtGiro)).setText("");
                        ((TextView) findViewById(R.id.txtCiudad)).setText("");
                        ((Button) findViewById(R.id.btnND)).setVisibility(View.INVISIBLE);
                        ((Button) findViewById(R.id.btnEvaluar)).setVisibility(View.INVISIBLE);

                        LecturaCodigo = 0;
                        ((EditText) findViewById(R.id.txtcodigo)).setText("");
                        ((EditText) findViewById(R.id.txtcodigo)).requestFocus();


                    }
                });
                builder.setNegativeButton("NO", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // Do nothing
                        dialog.dismiss();
                    }
                });
                AlertDialog alert = builder.create();
                alert.show();

                /* intent.putExtra("CodigoC",(String)(((TextView)findViewById(R.id.txtClaCte)).getText().toString().split(":"))[1].trim());
                intent.putExtra("Cliente",  ((TextView) findViewById(R.id.txtNomCte)).getText());
                intent.putExtra("Zona",(String)(((TextView)findViewById(R.id.txtZona)).getText().toString().split(":"))[1].trim());
                intent.putExtra("ClaveUsuario",ClaveUsuario);
                intent.putExtra("ClaEmp",(String)(((Spinner)findViewById(R.id.cmbempresa)).getSelectedItem().toString().split("-"))[0].trim());
                intent.putExtra("ClaEmpp",ClaEmpp);
                intent.putExtra("LecturaCodigo",LecturaCodigo);
                intent.putExtra("StatusNegocio",1);*/

                    //   Log.d("Msg", datos.ObtenerEncuestasEnvio().toString());
/*

                String sEncuestas = datos.ObtenerEncuestasEnvio().toString();

                if (!sEncuestas.equals("[]")) {


                    ///PROBANDO CADENA JSON
                    RequestQueue requestQueue = Volley.newRequestQueue(PDV.this);
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
*/

                }

            });


        //EVENTO DEL BOTON PARA SCANEAR EL CODGIO DEL CLIENTE

        Button btCodigo = (Button)findViewById(R.id.btnscan);
        btCodigo.setOnClickListener(new Button.OnClickListener(){

            public void onClick(View v){


                IntentIntegrator integrator = new IntentIntegrator(PDV.this);
                integrator.setDesiredBarcodeFormats(IntentIntegrator.ALL_CODE_TYPES);

                integrator.setPrompt("Scan a QR/Bar code");
                integrator.setCameraId(0);
                integrator.setBeepEnabled(false);
                integrator.initiateScan();

             //   Log.d("Clientes", "Clientes");
             //   DatabaseUtils.dumpCursor(datos.obtenerClientes());


            }

        });


        //EVENTO DEL BOTON ACTUALIZARTODO

        Button btActAll = (Button)findViewById(R.id.btnActodo);
        btActAll.setOnClickListener(new Button.OnClickListener(){

            public void onClick(View v) {

                if(datos.ObtenerStatusEnvio()>0){

                    AlertDialog.Builder alert = new AlertDialog.Builder(PDV.this);
                    alert.setTitle("SAC");
                    alert.setMessage("Aun tiene Evaluaciones Pendientes por enviar");
                    alert.setPositiveButton("OK", null);
                    alert.show();
                    return;

                }else{

                    datos.onDelete();

                    //ACTUALIZACION DE LA BASE DE DATOS
                    RequestQueue requestQueue = Volley.newRequestQueue(PDV.this);
                    final ProgressDialog pDialog = new ProgressDialog(PDV.this);
                    pDialog.setMessage("Loading...");
                    pDialog.show();
                    String tag_json_obj = "rows";
                    String JSON_URL = "http://www.servicioscorona.mx/SACCoronaCoatza/JSONDemo/GetClientes";
                    String JSON_URL_TIPOENCUESTA= "http://www.servicioscorona.mx/SACCoronaCoatza/JSONDemo/GetTipoEvaluacion";
                    String JSON_URL_TIPOSUBENCUESTA="http://www.servicioscorona.mx/SACCoronaCoatza/JSONDemo/GetTipoSubEvaluacion";
                    String JSON_URL_TIPOSUBENCUESTA_PUNTO="http://www.servicioscorona.mx/SACCoronaCoatza/JSONDemo/GetTipoSubEvaluacionPunto";

                    JSONObject paramss = new JSONObject();
                    try {
                        paramss.put("ClaEmp", 0);//((EditText)findViewById(R.id.txtusuario)).getText().toString());
                        //params.put("ClaCte",((EditText)findViewById(R.id.txtcodigo)).getText().toString());// ((EditText)findViewById(R.id.txtcontrasena)).getText().toString());
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }


                    //OBTENEMOS LOS PUNTOS DE LA EVALUACION
                    JSONObject paramTSubEncuestaPunto = new JSONObject();
                    JsonObjectRequest jsonTipoSubEncuestaPunto = new JsonObjectRequest(Request.Method.POST,JSON_URL_TIPOSUBENCUESTA_PUNTO,paramTSubEncuestaPunto,
                            new Response.Listener<JSONObject>()
                            {
                                public void onResponse (JSONObject response)
                                {
                                    JSONArray jsonArray = response.optJSONArray("rows");
                                    datos.getDb().beginTransaction();
                                    for (int i=0;i<jsonArray.length();i++)
                                    {
                                        JSONObject jsonItem = jsonArray.optJSONObject(i);

                                        datos.insertarTipoSubEncuestaPunto(jsonItem.optInt("IdTipoEvaluacion"),jsonItem.optInt("IdTipoSubEvaluacion"),
                                                jsonItem.optInt("IdTipoSubEvaluacionPunto"),jsonItem.optString("NombrePunto"),jsonItem.optInt("ValorMinimo"),
                                                jsonItem.optInt("ValorMaximo"),jsonItem.optInt("TipoEncuesta"));
                                    }
                                    datos.getDb().setTransactionSuccessful();
                                    datos.getDb().endTransaction();
                                }

                            },new Response.ErrorListener() {

                        @Override
                        public void onErrorResponse(VolleyError error) {
                            pDialog.dismiss();
                            VolleyLog.d("", "Error: " + error.getMessage());
                            // pDialog.hide();
                        }
                    });


                    //OBTENEMOS LOS SUBTIPOS DE ENCUESTA
                    JSONObject paramTSubEncuesta = new JSONObject();
                    JsonObjectRequest jsonTipoSubEncuesta = new JsonObjectRequest(Request.Method.POST,JSON_URL_TIPOSUBENCUESTA,paramTSubEncuesta,
                            new Response.Listener<JSONObject>(){

                                public void onResponse(JSONObject response){
                                    ArrayList<ResponseObject.Rows> lLista = new ArrayList<>();
                                    JSONArray jsonArray = response.optJSONArray("rows");
                                    datos.getDb().beginTransaction();

                                    for (int i=0;i<jsonArray.length();i++)
                                    {
                                        JSONObject jsonItem = jsonArray.optJSONObject(i);
                                        datos.insertarTipoSubEncuesta(jsonItem.optInt("IdTipoEvaluacion"),jsonItem.optInt("IdTipoSubEvaluacion"),jsonItem.optString("NombreTipoSubEvaluacion"),jsonItem.optInt("TipoEncuesta"));

                                    }

                                    datos.getDb().setTransactionSuccessful();
                                    datos.getDb().endTransaction();
                                }

                            },new Response.ErrorListener() {

                        @Override
                        public void onErrorResponse(VolleyError error) {
                            pDialog.dismiss();
                            VolleyLog.d("", "Error: " + error.getMessage());
                            // pDialog.hide();
                        }
                    }
                    );


                    ///OBTENEMOS LOS TIPOS DE ENCUESTA

                    JSONObject paramTEncuesta = new JSONObject();
                    JsonObjectRequest jsonTipoEncuesta = new JsonObjectRequest(Request.Method.POST,JSON_URL_TIPOENCUESTA,paramTEncuesta,
                            new Response.Listener<JSONObject>()
                            {
                                public void onResponse (JSONObject response){
                                    ArrayList<ResponseObject.Rows> lLista = new ArrayList<>();
                                    JSONArray jsonArray = response.optJSONArray("rows");
                                    datos.getDb().beginTransaction();

                                    for (int i=0;i<jsonArray.length();i++)
                                    {
                                        JSONObject jsonItem = jsonArray.optJSONObject(i);
                                        datos.insertarTipoEncuesta(jsonItem.optInt("IdTipoEvaluacion"), jsonItem.optString("NombreTipoEvaluacion"),jsonItem.optInt("TipoEncuesta"));
                                    }
                                    datos.getDb().setTransactionSuccessful();
                                    datos.getDb().endTransaction();
                                }

                            }, new Response.ErrorListener() {

                        @Override
                        public void onErrorResponse(VolleyError error) {
                            pDialog.dismiss();
                            VolleyLog.d("", "Error: " + error.getMessage());
                            // pDialog.hide();
                        }
                    });


                    ///OBTENEMOS LOS CLIENTES
                    JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.POST,
                            JSON_URL, paramss,
                            new Response.Listener<JSONObject>() {

                                ///CACHAMOS LA RESPUESTA DEL CLIENTE
                                @Override
                                public void onResponse(JSONObject response) {
                                    ArrayList<ResponseObject.Rows> lLista = new ArrayList<>();
                                    String xprueba = "";
                                    JSONArray jsonVideoArray = response.optJSONArray("rows");
                                    datos.getDb().beginTransaction();
                                    // Inserción Clientes


                                    for (int i = 0; i < jsonVideoArray.length(); i++) {
                                        JSONObject jsonItems = jsonVideoArray.optJSONObject(i);

                                        //db.execSQL("create table clientes(ClaCte text, NomCte text, AliasCte text, Calle text,Col text,ClaZona text,ClaEmp text,ClaCd text,Canal text,Segmento text,NomGiroCom text)");
                                        datos.insertarCliente(new clientes(jsonItems.optInt("ClaCte"), jsonItems.optString("NomCte"), jsonItems.optString("Negocio"), jsonItems.optString("Calle"),
                                                jsonItems.optString("Col"), jsonItems.optString("ClaCd"), jsonItems.optInt("ClaZona"), jsonItems.optInt("ClaEmp"), jsonItems.optString("Canal"),
                                                jsonItems.optString("Segmento"), jsonItems.optString("NomGiroCom"),jsonItems.optInt("Evalua"),jsonItems.optInt("POP")));
                                        //  datos.insertarCliente(new clientes("4", "Pedro", "Sequeda", "Almendras", "Almendros", "Coatza", "1", "2"));

                                    }
                                    datos.getDb().setTransactionSuccessful();
                                    datos.getDb().endTransaction();


                                    String nUsuario = "";
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

                    jsonObjReq.setShouldCache(false);
                    requestQueue.add(jsonTipoEncuesta);

                    jsonObjReq.setShouldCache(false);
                    requestQueue.add(jsonTipoSubEncuesta);

                    jsonObjReq.setShouldCache(false);
                    requestQueue.add(jsonTipoSubEncuestaPunto);

                    AlertDialog.Builder alert = new AlertDialog.Builder(PDV.this);
                    alert.setTitle("SAC");
                    alert.setMessage("Se ha actualizado correctamente.");
                    alert.setPositiveButton("OK", null);
                    alert.show();
                    return;
                }
            }

        });


        ///EVENTO DE BARRA BORRAR EL TEXT VIEW DEL CODIGO

        EditText editText = (EditText)findViewById(R.id.txtcodigo);

        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                //here is your code

                if (count == 0) {

                    ((TextView) findViewById(R.id.txtClaCte)).setText("");
                    ((TextView) findViewById(R.id.txtNomCte)).setText("");
                    ((TextView) findViewById(R.id.txtNegocio)).setText("");
                    ((TextView) findViewById(R.id.txtCalle)).setText("");
                    ((TextView) findViewById(R.id.txtColonia)).setText("");
                    ((TextView) findViewById(R.id.txtZona)).setText("");
                    ((TextView) findViewById(R.id.txtCanal)).setText("");
                    ((TextView) findViewById(R.id.txtSegmento)).setText("");
                    ((TextView) findViewById(R.id.txtGiro)).setText("");
                    ((TextView) findViewById(R.id.txtCiudad)).setText("");
                    ((Button) findViewById(R.id.btnND)).setVisibility(View.INVISIBLE);
                    ((Button) findViewById(R.id.btnEvaluar)).setVisibility(View.INVISIBLE);

                    LecturaCodigo = 0;
                }

            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count,
                                          int after) {
                // TODO Auto-generated method stub
                String sf = "";
            }

            @Override
            public void afterTextChanged(Editable s) {
                // TODO Auto-generated method stub
                String sd = "";
            }
        });




    }
    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        IntentResult scanResult = IntentIntegrator.parseActivityResult(requestCode, resultCode, intent);
        if (scanResult != null) {
            String re = scanResult.getContents();
           // TextView txtResultado = (TextView)findViewById(R.id.txtcodigo);
           // txtResultado.setText(re);
            EditText txt = (EditText)findViewById(R.id.txtcodigo);
            txt.setText(re);

            LecturaCodigo=1;
            Button btBuscar = (Button)findViewById(R.id.btnconsultar);
            btBuscar.performClick();
        }
        // else continue with any other code you need in the method

    }

    public static boolean verificarConexion(Context ctx){
        boolean bConectado = false;
        ConnectivityManager connec = (ConnectivityManager) ctx.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo redes = connec.getNetworkInfo(ConnectivityManager.TYPE_WIFI);

        if(redes.getState() == NetworkInfo.State.CONNECTED){
            bConectado = true;
        }

        return bConectado;
    }

}
