package com.example.psequeda.saccorona;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

public class Info_menu extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info_menu);

        TextView txtinfo = (TextView)findViewById(R.id.textView3);
        txtinfo.setText("Version del proyecto 1.8. \n\n Hecho por el Departamento de Desarrollo. \n\n La Corona Del Golfo S.A. DE C.V.");
    }
}
