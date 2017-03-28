
package data.sqllite;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Build;


/**
 * Created by psequeda on 08/08/2016.
 */
public class BD extends  SQLiteOpenHelper  {

    private static final String NOMBRE_BASE_DATOS ="SAC.db";

    private static final int VERSION_ACTUAL=44;

    private final Context contexto;

    public interface Tablas
    {
        String CLIENTES="cliente";
    }

    public BD(Context contexto) {
        super(contexto, NOMBRE_BASE_DATOS, null, VERSION_ACTUAL);
        this.contexto = contexto;
    }

    @Override
    public void onOpen(SQLiteDatabase db) {
        super.onOpen(db);
        if (!db.isReadOnly()) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                db.setForeignKeyConstraintsEnabled(true);
            } else {
                db.execSQL("PRAGMA foreign_keys=ON");
            }
        }
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
      /*  valores.put("ClaCte", cliente.ClaCte);
        valores.put("NomCte", cliente.NomCte);
        valores.put("AliasCte", cliente.AliasCte);
        valores.put("Calle", cliente.Calle);
        valores.put("Col", cliente.Col);
        valores.put("ClaZona", cliente.ClaZona);
        valores.put("ClaEmp", cliente.ClaEmp);
        valores.put("ClaCd",cliente.ClaCd);
        valores.put("Canal",cliente.Canal);
        valores.put("Segmento",cliente.Segmento);
        valores.put("NomGiroCom",cliente.NomGiroCom);*/

        db.execSQL("create table clientes(ClaCte integer , NomCte text, AliasCte text, Calle text,Col text,ClaZona integer,ClaEmp integer ,ClaCd text,Canal text,Segmento text,NomGiroCom text,Evalua integer,POP integer)");
        db.execSQL("create table Parametros(ClaveParametro text,Valor text)");

        db.execSQL("create table TipoEvaluacion(IdTipoEvaluacion integer,NombreTipoEvaluacion text,TipoEncuesta integer)");
        db.execSQL("create table TipoSubEvaluacion(IdTipoEvaluacion integer,IdTipoSubEvaluacion integer,NombreTipoSubEvaluacion text,TipoEncuesta integer)");
        db.execSQL("create table TipoSubEvaluacionPuntos(IdTipoEvaluacion integer,IdTipoSubEvaluacion integer,IdTipoSubEvaluacionPunto integer,NombrePunto text,ValorMinimo integer,ValorMaximo integer,TipoEncuesta integer)");

        db.execSQL("create table Evaluacion(IdEvaluacion INTEGER PRIMARY KEY   AUTOINCREMENT, ClaCte integer, Fecha text, ClaZona integer,Comentarios text,ClaveUsuario integer,StatusEnvio integer,ClaEmp integer,StatusNegocio integer,LecturaCodigo integer,TipoEncuesta integer)");
        db.execSQL("create table Evaluacion_Ejecucion(IdEvaluacion integer,IdTipoEvaluacion integer,IdTipoSubEvaluacion integer,IdTipoSubEvaluacionPunto integer,Puntaje integer)");
        db.execSQL("create table EvaluacionCompleta(IdEvaluacion integer)");

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        db.execSQL("DROP TABLE IF EXISTS clientes");
        db.execSQL("DROP TABLE IF EXISTS Parametros");
        db.execSQL("DROP TABLE IF EXISTS TipoEvaluacion");
        db.execSQL("DROP TABLE IF EXISTS TipoSubEvaluacion");
        db.execSQL("DROP TABLE IF EXISTS TipoSubEvaluacionPuntos");

        db.execSQL("DROP TABLE IF EXISTS Evaluacion");
        db.execSQL("DROP TABLE IF EXISTS Evaluacion_Ejecucion");
        db.execSQL("DROP TABLE IF EXISTS EvaluacionCompleta");
        onCreate(db);

    }




}
