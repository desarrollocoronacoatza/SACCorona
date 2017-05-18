package com.example.psequeda.saccorona;

import android.content.Context;
import android.content.Intent;
import android.graphics.Point;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Display;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;

public class menu extends AppCompatActivity {



    // Intervalo de 5 Segundos


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        Display display =    getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        int width = size.x;
        int height = size.y;
        // android:layout_margin="@dimen/activity_horizontal_margin"
        if ((width>480) && (height>800))
        {
            //comentado
           // ImageButton btn = (ImageButton)findViewById(R.id.btnconsultasolicitud);
           // btn.layout();

        }




        final String nEmpresa = getIntent().getExtras().getString("ClaEmp");
        final String sClaveUsuario = getIntent().getExtras().getString("ClaveUsuario");
        final String nPermiso = getIntent().getExtras().getString("Permiso");

        ImageButton button = (ImageButton) findViewById(R.id.btnsolicitud);

        button.setOnClickListener(
                new Button.OnClickListener() {
                    public void onClick(View v) {

                        if  (sClaveUsuario.compareTo("3")!=0)
                        {return;}
                        Intent intent = new Intent(menu.this, solicitudes.class);
                        intent.putExtra("ClaEmp", nEmpresa);
                        intent.putExtra("ClaveUsuario", sClaveUsuario);
                        intent.putExtra("Permiso", nPermiso);
                        startActivity(intent);
                        return;
                    }
                }
        );

        ImageButton button2 = (ImageButton) findViewById(R.id.btnconsultasolicitud);

       button2.setOnClickListener(
               new Button.OnClickListener(){
                   public void onClick(View v){

                       if  (sClaveUsuario.compareTo("3")!=0)
                       {return;}
                       Intent intent = new Intent(menu.this, solicitudes_pendientes.class);
                       intent.putExtra("ClaEmp", nEmpresa);
                       intent.putExtra("ClaveUsuario", sClaveUsuario);
                       intent.putExtra("Permiso", nPermiso);
                       startActivity(intent);
                       return;
                   }
               }
       );

        ImageButton button3 = (ImageButton)findViewById(R.id.btnSincronizacion);

        button3.setOnClickListener(
                new Button.OnClickListener(){
                    public void onClick(View v){

                        if  (sClaveUsuario.compareTo("3")!=0)
                        {return;}
                        Intent intent = new Intent(menu.this,SincronizacionPV.class);
                        intent.putExtra("ClaEmp",nEmpresa);
                        intent.putExtra("ClaveUsuario",sClaveUsuario);
                        intent.putExtra("Permiso", nPermiso);
                        startActivity(intent);
                                return;
                    }

                }

        );

        ImageButton button4 = (ImageButton)findViewById(R.id.btnpdv);

        button4.setOnClickListener(
                new Button.OnClickListener(){
                    public void onClick(View v){
                       /*
                        if(!verificarConexion(menu.this)){
                            AlertDialog.Builder alert = new AlertDialog.Builder(menu.this);
                            alert.setTitle("SAC");
                            alert.setMessage("Favor de conectarse a una red Wi-Fi....");
                            alert.setPositiveButton("OK", null);
                            alert.show();
                            //Toast.makeText(getBaseContext(), "Comprueba tu conexion a internet, saliendo...", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        */
                        Intent intent = new Intent(menu.this,PDV.class);
                        intent.putExtra("ClaEmp",nEmpresa);
                        intent.putExtra("ClaveUsuario",sClaveUsuario);
                        intent.putExtra("Permiso", nPermiso);
                        startActivity(intent);
                        return;
                    }

                }
        );

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
