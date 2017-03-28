package com.example.psequeda.saccorona;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import data.sqllite.OperacionesBD;

public class PdvEjecucion extends AppCompatActivity {

    private TextView tvDateValue;
    private int year;
    private int month;
    private int day;
    private Activity context;

    OperacionesBD datos;
    boolean iBandera;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pdv_ejecucion);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        final String CodigoCliente=getIntent().getExtras().getString("CodigoC");
        final String NombreCliente=getIntent().getExtras().getString("Cliente");
        final String Zona=getIntent().getExtras().getString("Zona");
        final String ClaveUsuario =getIntent().getExtras().getString("ClaveUsuario");
        final String ClaEmp = getIntent().getExtras().getString("ClaEmp");
        final String ClaEmpp=getIntent().getExtras().getString("ClaEmpp");
        final String nPermiso = getIntent().getExtras().getString("Permiso");
        final int LecturaCodigo=getIntent().getExtras().getInt("LecturaCodigo");
        final int StatusNegocio=getIntent().getExtras().getInt("StatusNegocio");
        final String Canal =getIntent().getExtras().getString("Canal");
        final int TipoEncuesta=getIntent().getExtras().getInt("TipoEncuesta");
        final int POP= getIntent().getExtras().getInt("POP");

        if (nPermiso.compareTo("1")!=0) {
            ((Button) findViewById(R.id.btnCalendar)).setVisibility(View.INVISIBLE);
        }else{

            ((Button) findViewById(R.id.btnCalendar)).setVisibility(View.VISIBLE);
        }

        tvDateValue = (TextView) findViewById(R.id.txtFecha);

        context = this;
        //definicion del boton calendario
        final Button btnOpenPopup = (Button) findViewById(R.id.btnCalendar);
        btnOpenPopup.setOnClickListener(new Button.OnClickListener() {

            @Override
            /**
             * Al pulsar sobre el boton se abrira la ventana con el date picker
             */
            public void onClick(View arg0) {
                showDatePickerDialog(arg0);
            }
        });


                ((TextView) findViewById(R.id.txtCliente)).setText(CodigoCliente + " - " + NombreCliente);

                SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy");
                String currentDateandTime = format.format(new Date());
                ((TextView) findViewById(R.id.txtFecha)).setText(currentDateandTime);

        //CONEXION A LA BASE DE DATOS
        datos = OperacionesBD.obtenerInstancia(getApplicationContext());


        //AGREGAMOS LAS ETIQUETAS DE LOS TOTALES

        Cursor cTEncuesta = datos.ObtenerTipoEncuesta(TipoEncuesta);
        if (cTEncuesta!=null)
        {
            cTEncuesta.moveToFirst();
        }




        LinearLayout linearLayout = (LinearLayout)findViewById(R.id.LTotales);

        //AGREGAMOS ETIQUETAS VACIAS

        TextView txtTotal1 = new TextView(this);
        txtTotal1.setText("         ");
        linearLayout.addView(txtTotal1);

        TextView txtTotal = new TextView(this);
        txtTotal.setText("         ");
        linearLayout.addView(txtTotal);
        //AGREGAMO LAS ETIQUETAS

        int idTotales = 100+cTEncuesta.getInt(cTEncuesta.getColumnIndex("IdTipoEvaluacion"));


        TextView txtTotales = new TextView(this);
        txtTotales.setText("Total "+cTEncuesta.getString(cTEncuesta.getColumnIndex("NombreTipoEvaluacion"))+": 0");
        txtTotales.setId(idTotales);
        txtTotales.setTypeface(null, Typeface.BOLD_ITALIC);
        linearLayout.addView(txtTotales);

        while (cTEncuesta.moveToNext()) {

            idTotales = 100+cTEncuesta.getInt(cTEncuesta.getColumnIndex("IdTipoEvaluacion"));
            TextView txtTotale = new TextView(this);
            txtTotale.setText("Total "+cTEncuesta.getString(cTEncuesta.getColumnIndex("NombreTipoEvaluacion"))+": 0");
            txtTotale.setId(idTotales);
            txtTotale.setTypeface(null, Typeface.BOLD_ITALIC);
            linearLayout.addView(txtTotale);


        }

        Cursor cPreguntas = datos.ObtenerPreguntas(TipoEncuesta);

        if (cPreguntas != null) {
            cPreguntas.moveToFirst();
        }




        int iIdTipoEvaluacion=cPreguntas.getInt(cPreguntas.getColumnIndex( "IdTipoEvaluacion"));
        int iIdTipoSubEvaluacion = cPreguntas.getInt(cPreguntas.getColumnIndex( "IdTipoSubEvaluacion"));
        int iIdTipoPunto= cPreguntas.getInt(cPreguntas.getColumnIndex( "IdTipoSubEvaluacionPunto"));
        String sNombreTipoEvaluacion=cPreguntas.getString(cPreguntas.getColumnIndex("NombreTipoEvaluacion"));
        String sNombreTipoSubEvaluacion=cPreguntas.getString(cPreguntas.getColumnIndex("NombreTipoSubEvaluacion"));
        String sNombrePunto=cPreguntas.getString(cPreguntas.getColumnIndex("NombrePunto"));

        // valMax=
        // valMin= ;

        int ContEnc=0;
        int ContSubEnc=0;
        int valMax=cPreguntas.getInt(cPreguntas.getColumnIndex("ValorMaximo"));;
        int valMin=cPreguntas.getInt(cPreguntas.getColumnIndex("ValorMinimo"));
        int valMintmp=0;

        TableLayout ll = (TableLayout) findViewById(R.id.tableLayout1);
        ll.removeAllViews();
        int contador=0;
        while(cPreguntas.moveToNext()){



            TableRow row= new TableRow(this);
            TableRow.LayoutParams lp = new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT);
            row.setLayoutParams(lp);


            TextView lTipoEncuesta = new TextView(this);

            if (ContEnc<=0) {
                lTipoEncuesta.setText(sNombrePunto);

                //AGREGAMOS DOS FILAS EN BLANCO PARA EL SPACIO
                //FILA 1
                TableRow rowempty1 = new TableRow(this);
                TableRow.LayoutParams lpEmpty1 = new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT);
                rowempty1.setLayoutParams(lpEmpty1);
                TextView lEmpty1 = new TextView(this);
                lEmpty1.setText("          ");
                rowempty1.addView(lEmpty1);
                ll.addView(rowempty1,contador);
                contador++;
                //FILA 2
                TableRow rowempty2 = new TableRow(this);
                TableRow.LayoutParams lpEmpty2 = new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT);
                rowempty1.setLayoutParams(lpEmpty2);
                TextView lEmpty2 = new TextView(this);
                lEmpty2.setText("          ");
                rowempty2.addView(lEmpty2);
                ll.addView(rowempty2, contador);
                contador++;


                //NUEVO ENCABEZADO
                TableRow rowe= new TableRow(this);
                TableRow.LayoutParams lpe = new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT);
                rowe.setLayoutParams(lpe);
                TextView lTipoEncuestaEnc = new TextView(this);
                lTipoEncuestaEnc.setText(sNombreTipoEvaluacion);
                lTipoEncuestaEnc.setTypeface(Typeface.DEFAULT_BOLD);
                lTipoEncuestaEnc.setPaintFlags(lTipoEncuestaEnc.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
                rowe.addView(lTipoEncuestaEnc);
                ll.addView(rowe,contador);

                contador++;
                //NUEVO SUBENCABEZADO
                TableRow rows= new TableRow(this);
                TableRow.LayoutParams lpes = new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT);
                rows.setLayoutParams(lpes);
                TextView lTipoEncuestaEncSub = new TextView(this);
                lTipoEncuestaEncSub.setText(sNombreTipoSubEvaluacion);
                lTipoEncuestaEncSub.setTypeface(null,Typeface.BOLD_ITALIC);
                rows.addView(lTipoEncuestaEncSub);
                ll.addView(rows,contador);
                contador++;
                ContEnc++;


            }
            else
            {
                if (ContEnc<=1) {
                    lTipoEncuesta.setText(sNombrePunto);

                    //FILA EN BLANCO
                    TableRow rowempty1 = new TableRow(this);
                    TableRow.LayoutParams lpEmpty1 = new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT);
                    rowempty1.setLayoutParams(lpEmpty1);
                    TextView lEmpty1 = new TextView(this);
                    lEmpty1.setText("          ");

                    rowempty1.addView(lEmpty1);
                    ll.addView(rowempty1,contador);
                    contador++;

                    //SUB EVALUACION
                    TableRow rows= new TableRow(this);
                    TableRow.LayoutParams lpes = new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT);
                    rows.setLayoutParams(lpes);
                    TextView lTipoEncuestaEncSub = new TextView(this);
                    lTipoEncuestaEncSub.setText(sNombreTipoSubEvaluacion);
                    lTipoEncuestaEncSub.setTypeface(null,Typeface.BOLD_ITALIC);
                    rows.addView(lTipoEncuestaEncSub);
                    ll.addView(rows,contador);
                    contador++;
                }
                else
                {
                    lTipoEncuesta.setText(sNombrePunto);
                }
            }

            ContEnc++;


            //AGREGAMOS EL COMBOBOX
            Spinner spinner = new Spinner(this);


            //OBTENEMOS EL VALOR MINIMO Y MAXIMO DE CADA PREGUNTA


            // valMax= cPreguntas.getInt(cPreguntas.getColumnIndex("ValorMaximo"));
            // valMin= cPreguntas.getInt(cPreguntas.getColumnIndex("ValorMinimo"));

            //13,14,15 COMJUNICACION DE PRECIO
             valMintmp=valMin;
            //ARREGLO PARA CADA SPINNER
            List<String> spinnerArray =  new ArrayList<String>();

            if ((iIdTipoPunto<=6) || (iIdTipoPunto==30)) {
                while (valMin <= valMax) {

                    spinnerArray.add(Integer.toString(valMin));
                    valMin++;
                }
            }
            else
            {

                spinnerArray.add(Integer.toString(valMin));
                spinnerArray.add(Integer.toString(valMax));
                spinnerArray.add("N/A");

            }


            ArrayAdapter<String> adapter = new ArrayAdapter<String>(
                    this, android.R.layout.simple_spinner_item, spinnerArray);
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spinner.setId(iIdTipoPunto);
            spinner.setAdapter(adapter);



            ///AGREGAMOS EL EVENTO ONCHANGED DEL SPINNER PARA PODER SUMAR CUANDO CAMBIE LA CALIFICACION

            spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {


                    // your code here
                    //OBTENEMOS EL ID DEL COMBO
                    int idCombo = (int) parentView.getId();
                    //OBTENEMOS EL ID DEL TIMPO DE ENCUESTA EN BASE AL ID DEL COMBO
                    int IdTipoEvaluacion = datos.ObtenerIdTipoEvaluacion(idCombo);
                    //OBTENEMOS LA TABLA DE LOS COMBOS PARA SACARLOS


                    if (idCombo<=6)
                        return;

                    TableLayout tLista = (TableLayout) findViewById(R.id.tableLayout1);

                    int iCalificacionTotal = 0;
                    //OBTENEMOS LAS PREGUNTAS DEL MISMO TIPO PARA SUMAR
                    Cursor cCombos = datos.ObtenerCombosIds(IdTipoEvaluacion,TipoEncuesta);
                    //OBTENEMOS EL PRIMER REGISTRO Y SUMAMOS
                    if (cCombos != null) {
                        cCombos.moveToFirst();
                        String sCalificacion = ((Spinner) tLista.findViewById((int) cCombos.getInt(cCombos.getColumnIndex("IdTipoSubEvaluacionPunto")))).getSelectedItem().toString();

                        if (!sCalificacion.equals("N/A"))
                        {
                            iCalificacionTotal = iCalificacionTotal + Integer.parseInt(sCalificacion);
                        }


                    } else {
                        return;
                    }
                    if (cCombos.getCount()<=0)
                        return;
                    //SUMANMOS LOS DEMAS REGISTROS
                    while (cCombos.moveToNext()) {
                        String sCalificacion = ((Spinner) tLista.findViewById((int) cCombos.getInt(cCombos.getColumnIndex("IdTipoSubEvaluacionPunto")))).getSelectedItem().toString();
                        if (!sCalificacion.equals("N/A")) {

                            iCalificacionTotal = iCalificacionTotal + Integer.parseInt(sCalificacion);
                        }

                    }
                    //OBTENEMOS EL LAYOUT PRINCIPAL PARA OBTENER LOS LABELS DE LOS TOTALES
                    LinearLayout lPrincipal = (LinearLayout) findViewById(R.id.LTotales);
                    int ids = 100 + IdTipoEvaluacion;
                    //OBTENEMOS EL NOMBRE DEL LABEL QUE SE SUMARA
                    String sTextoM = ((TextView) lPrincipal.findViewById((int) ids)).getText().toString().split(":")[0];
                    //PLANCHAMOS EL TOTAL EN SU CORRESPONDIENTE LABEL
                    ((TextView) lPrincipal.findViewById((int) ids)).setText(sTextoM + ": " + Integer.toString(iCalificacionTotal));


                }

                @Override
                public void onNothingSelected(AdapterView<?> parentView) {
                    // your code here
                }

            });



            //BLQOUEAMOS LOS SPINNER DEPENDIENDO EL TIPO DE ENCUESTA
/*
            if (Canal.equals("BOTELLA CERRADA"))
            {
                if ((iIdTipoPunto==20) || (iIdTipoPunto==21))
                {

                    spinner.setEnabled(false);
                }
            }
            else
            {
                if ((iIdTipoPunto==7) || (iIdTipoPunto==10) || (iIdTipoPunto==11)|| (iIdTipoPunto==31))
                {

                    spinner.setEnabled(false);
                }

            }
*/
            /*if (POP==1)
            {
                if ((iIdTipoPunto==13) || (iIdTipoPunto==14) || (iIdTipoPunto==15) )
                {
                    spinner.setSelection(1);
                    spinner.setEnabled(false);
                }

            }
            */
            //AGREGAMOS LOS OBJETOS A SUS CORRESPOINDENTES PADRES




            row.addView(lTipoEncuesta);
            row.addView(spinner);
            ll.addView(row,contador);


            contador++;
            int idTipoE=iIdTipoEvaluacion;
            int idTipoSubE=iIdTipoSubEvaluacion;

            iIdTipoEvaluacion=cPreguntas.getInt(cPreguntas.getColumnIndex( "IdTipoEvaluacion"));
            iIdTipoSubEvaluacion = cPreguntas.getInt(cPreguntas.getColumnIndex( "IdTipoSubEvaluacion"));
            iIdTipoPunto = cPreguntas.getInt(cPreguntas.getColumnIndex( "IdTipoSubEvaluacionPunto"));
            sNombreTipoEvaluacion = cPreguntas.getString(cPreguntas.getColumnIndex("NombreTipoEvaluacion"));
            sNombreTipoSubEvaluacion = cPreguntas.getString(cPreguntas.getColumnIndex("NombreTipoSubEvaluacion"));
            sNombrePunto=cPreguntas.getString(cPreguntas.getColumnIndex("NombrePunto"));
            valMax= cPreguntas.getInt(cPreguntas.getColumnIndex("ValorMaximo"));
            valMin= cPreguntas.getInt(cPreguntas.getColumnIndex("ValorMinimo"));

            if (idTipoE!=iIdTipoEvaluacion)
            {
                ContEnc=0;
            }
            else {
                if ((idTipoSubE != iIdTipoSubEvaluacion) && (ContEnc!=1)) {
                    ContEnc = 1;

                }
            }

        }


        //AGREGAMOS EL ULTIMO SPINNER



        Spinner spinner = new Spinner(this);
        List<String> spinnerArray =  new ArrayList<String>();


        if ((iIdTipoPunto<=6) || (iIdTipoPunto==30)) {
            while (valMintmp<=valMax)
            {

                spinnerArray.add(Integer.toString(valMintmp));
                valMintmp++;
            }
        }
        else
        {

            spinnerArray.add(Integer.toString(valMintmp));
            spinnerArray.add(Integer.toString(valMax));
            spinnerArray.add("N/A");

        }





        ArrayAdapter<String> adapter = new ArrayAdapter<String>(
                this, android.R.layout.simple_spinner_item, spinnerArray);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setId(iIdTipoPunto);
        spinner.setAdapter(adapter);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {


                // your code here
                //OBTENEMOS EL ID DEL COMBO
                int idCombo = (int) parentView.getId();
                //OBTENEMOS EL ID DEL TIMPO DE ENCUESTA EN BASE AL ID DEL COMBO
                int IdTipoEvaluacion = datos.ObtenerIdTipoEvaluacion(idCombo);
                //OBTENEMOS LA TABLA DE LOS COMBOS PARA SACARLOS


                if (idCombo<=6)
                    return;

                TableLayout tLista = (TableLayout) findViewById(R.id.tableLayout1);
                int iCalificacionTotal = 0;
                //OBTENEMOS LAS PREGUNTAS DEL MISMO TIPO PARA SUMAR
                Cursor cCombos = datos.ObtenerCombosIds(IdTipoEvaluacion,TipoEncuesta);
                //OBTENEMOS EL PRIMER REGISTRO Y SUMAMOS
                if (cCombos != null) {
                    cCombos.moveToFirst();
                    String sCalificacion = ((Spinner) tLista.findViewById((int) cCombos.getInt(cCombos.getColumnIndex("IdTipoSubEvaluacionPunto")))).getSelectedItem().toString();
                    if (!sCalificacion.equals("N/A")) {
                        iCalificacionTotal = iCalificacionTotal + Integer.parseInt(sCalificacion);
                    }
                } else {
                    return;
                }

                if (cCombos.getCount()<=0)
                    return;
                //SUMANMOS LOS DEMAS REGISTROS
                while (cCombos.moveToNext()) {
                    String sCalificacion = ((Spinner) tLista.findViewById((int) cCombos.getInt(cCombos.getColumnIndex("IdTipoSubEvaluacionPunto")))).getSelectedItem().toString();
                    if (!sCalificacion.equals("N/A")) {
                        iCalificacionTotal = iCalificacionTotal + Integer.parseInt(sCalificacion);
                    }

                }
                //OBTENEMOS EL LAYOUT PRINCIPAL PARA OBTENER LOS LABELS DE LOS TOTALES
                LinearLayout lPrincipal = (LinearLayout) findViewById(R.id.Lprincipal);
                int ids = 100 + IdTipoEvaluacion;
                //OBTENEMOS EL NOMBRE DEL LABEL QUE SE SUMARA
                String sTextoM = ((TextView) lPrincipal.findViewById((int) ids)).getText().toString().split(":")[0];
                //PLANCHAMOS EL TOTAL EN SU CORRESPONDIENTE LABEL
                ((TextView) lPrincipal.findViewById((int) ids)).setText(sTextoM + ": " + Integer.toString(iCalificacionTotal));


            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                // your code here
            }

        });


        //AGREGAMOS LA ULTIMA FILA DE LA ENCUESTA.....
        TableRow row= new TableRow(this);
        TableRow.LayoutParams lp = new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.WRAP_CONTENT);


       // TableRow tr = new TableRow(this);
       // tr.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.FILL_PARENT, TableRow.LayoutParams.WRAP_CONTENT));


        row.setLayoutParams(lp);

        TextView lTipoEncuesta = new TextView(this);
        lTipoEncuesta.setText(sNombrePunto);

        row.addView(lTipoEncuesta);
        row.addView(spinner);
        ll.addView(row, contador);



        /// EVENTO DEL BOTON ENVIAR - GUARDAR

        Button btnGuardar = (Button)findViewById(R.id.btnEnviar);
        btnGuardar.setOnClickListener(new Button.OnClickListener(){
            public void onClick(View v){



                SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy");
                String currentDateandTime = format.format(new Date());

                if (datos.ObtenerNumeroEncuestasCliente(CodigoCliente,ClaEmp,currentDateandTime)>0)
               // if (datos.ObtenerNumeroEncuestasCliente((String)(((TextView)findViewById(R.id.txtClaCte)).getText().toString().split(":"))[1].trim(),
                 //       (String)(((Spinner)findViewById(R.id.cmbempresa)).getSelectedItem().toString().split("-"))[0].trim(),currentDateandTime)>0  )
                {
                    AlertDialog.Builder alert = new AlertDialog.Builder(PdvEjecucion.this);
                    alert.setTitle("SAC");
                    alert.setMessage("El Cliente ya tiene una encuesta registrada el dia de hoy");
                    alert.setPositiveButton("OK", null);
                    alert.show();
                    return;
                }



                AlertDialog.Builder builder = new AlertDialog.Builder(PdvEjecucion.this);

                builder.setTitle("Confirmar");
                builder.setMessage("¿Esta Seguro de Almacenar la Informacion?");

                builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int which) {

                        // Do nothing, but close the dialog
                        dialog.dismiss();

                        SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy");
                        String currentDateandTime = format.format(new Date());
                       int IdEncuesta=  datos.InsertarEncuestaEnCabezado(Integer.parseInt(CodigoCliente), ((TextView) findViewById(R.id.txtFecha)).getText().toString(), Integer.parseInt(Zona), ((TextView) findViewById(R.id.txtComentarios)).getText().toString(), Integer.parseInt(ClaveUsuario),Integer.parseInt(ClaEmp),StatusNegocio,LecturaCodigo,TipoEncuesta);
                        TableLayout tLista = (TableLayout)findViewById(R.id.tableLayout1);


                        Cursor cCombos = datos.ObtenerCombos(TipoEncuesta);
                        if (cCombos!=null)
                        {
                            cCombos.moveToFirst();

                        }
                        else {return;}

                        String sCalificacion=((Spinner) tLista.findViewById((int) cCombos.getInt(cCombos.getColumnIndex("IdTipoSubEvaluacionPunto")))).getSelectedItem().toString();
                        if (!sCalificacion.equals("N/A")) {
                            datos.InsertarEncuestaDetalle(IdEncuesta, cCombos.getInt(cCombos.getColumnIndex("IdTipoSubEvaluacionPunto")), Integer.parseInt(sCalificacion));
                        }

                        while (cCombos.moveToNext())
                        {
                            sCalificacion=((Spinner) tLista.findViewById((int) cCombos.getInt(cCombos.getColumnIndex("IdTipoSubEvaluacionPunto")))).getSelectedItem().toString();
                            if (!sCalificacion.equals("N/A")) {
                                datos.InsertarEncuestaDetalle(IdEncuesta, cCombos.getInt(cCombos.getColumnIndex("IdTipoSubEvaluacionPunto")), Integer.parseInt(sCalificacion));
                            }
                        }

                        datos.InsetarEncuestaCompleta(IdEncuesta);

                        AlertDialog.Builder alert = new AlertDialog.Builder(PdvEjecucion.this);
                        alert.setTitle("SAC");
                        alert.setMessage("Se Almaceno la Encuesta Numero: " + Integer.toString(IdEncuesta));
                        alert.setPositiveButton("OK", null);
                        alert.show();


                        Intent intent = new Intent(PdvEjecucion.this, menu.class);
                        intent.putExtra("ClaveUsuario", ClaveUsuario);
                        intent.putExtra("ClaEmp", ClaEmpp);
                        intent.putExtra("Permiso", nPermiso);
                       /* intent.putExtra("CodigoC",(String)(((TextView)findViewById(R.id.txtClaCte)).getText().toString().split(":"))[1].trim());
                        intent.putExtra("Cliente",  ((TextView) findViewById(R.id.txtNomCte)).getText());
                        intent.putExtra("Zona", (String) (((TextView) findViewById(R.id.txtZona)).getText().toString().split(":"))[1].trim());

                        intent.putExtra("ClaEmp", (String) (((Spinner) findViewById(R.id.cmbempresa)).getSelectedItem().toString().split("-"))[0].trim());*/
                        intent.setFlags(intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent);
                        finish();

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


        //DOS FILAS EN BLANCO AL FINAL DE LA TABLA PARA DAR ESPACIO A LOS TOTALES
      /*  TableRow rowempty1 = new TableRow(this);
        TableRow.LayoutParams lpEmpty1 = new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT);
        rowempty1.setLayoutParams(lpEmpty1);
        TextView lEmpty1 = new TextView(this);
        lEmpty1.setText("          ");
        rowempty1.addView(lEmpty1);
        ll.addView(rowempty1,contador);

        //FILA 2
        TableRow rowempty2 = new TableRow(this);
        TableRow.LayoutParams lpEmpty2 = new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT);
        rowempty1.setLayoutParams(lpEmpty2);
        TextView lEmpty2 = new TextView(this);
        lEmpty2.setText("          ");
        rowempty2.addView(lEmpty2);
        ll.addView(rowempty2, contador);
*/


       // adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
       // Spinner sItems = (Spinner) findViewById(R.id.spinner);
       // sItems.setAdapter(adapter);



    }
    public void showDatePickerDialog(View v){

        DialogFragment newFragment = new DatePickerFragment();
        newFragment.show(context.getFragmentManager(), "datePicker");
    }

    public class DatePickerFragment extends DialogFragment implements DatePickerDialog.OnDateSetListener{

        @Override
        public Dialog onCreateDialog(Bundle saveInstanceState){
            final Calendar c = Calendar.getInstance();
            try{
                String format = "dd/MM/yyyy";
                SimpleDateFormat sdf = new SimpleDateFormat(format, Locale.US);
                c.setTime(sdf.parse(String.valueOf(tvDateValue.getText())));
            } catch (ParseException e){

            }
                    int year = c.get(Calendar.YEAR);
                    int month = c.get(Calendar.MONTH);
                    int day = c.get(Calendar.DAY_OF_MONTH);

            return new DatePickerDialog(getActivity(),this, year, month, day);
        }
        /**
         * Recupera el valor seleccionado en el componente DatePicker e inserta el valor en el
         * TextView tvDate
         *
         * @param view
         * @param year
         * @param month
         * @param day
         */
        public void onDateSet(DatePicker view, int year, int month, int day){
            try{
                final Calendar c = Calendar.getInstance();
                c.set(year, month, day);
                String format = "dd/MM/yyyy";
                SimpleDateFormat sdf = new SimpleDateFormat(format, Locale.US);
                tvDateValue.setText(sdf.format(c.getTime()));
            }catch (Exception e){
                e.printStackTrace();
            }
        }
    }
}
