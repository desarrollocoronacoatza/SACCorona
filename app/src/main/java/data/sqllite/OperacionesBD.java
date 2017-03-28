
package data.sqllite;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;

import data.modelo.clientes;

/**
 * Created by psequeda on 08/08/2016.
 */
public class OperacionesBD {

public static BD basededatos;

    public static OperacionesBD instancia= new OperacionesBD();

    public OperacionesBD(){}

    public static OperacionesBD obtenerInstancia(Context contexto) {
        if (basededatos == null) {
            basededatos = new BD(contexto);
        }
        return instancia;
    }

    public void insertarParametro(String ClaParametro,String Valor)
    {
     SQLiteDatabase db = basededatos.getWritableDatabase();
        ContentValues valores = new ContentValues();
        valores.put("ClaveParametro",ClaParametro);
        valores.put("Valor",Valor);
        db.insert("Parametros", null, valores);

    }

    public void ActualizarStatusEncuesta(int IdEncuesta)
    {

        SQLiteDatabase db = basededatos.getWritableDatabase();
        ContentValues valores = new ContentValues();

        valores.put("StatusEnvio", 1);

        db.update("Evaluacion", valores, "IdEvaluacion = " + Integer.toString(IdEncuesta), null);

    }
    //OBTENER EL STATUS ENVIO
    public int ObtenerStatusEnvio(){

        String countQuery="Select * from Evaluacion where StatusEnvio=0";
        SQLiteDatabase db = basededatos.getReadableDatabase();
        Cursor cursor=db.rawQuery(countQuery, null);
        int cnt= cursor.getCount();
        cursor.close();
        return cnt;



    }

    public  JSONArray ObtenerEncuestasEnvio()
    {

        SQLiteDatabase db=basededatos.getReadableDatabase();

        Cursor cursor=db.rawQuery("select e1.IdEvaluacion,e1.ClaCte,e1.Fecha,e1.ClaZona,e1.Comentarios,e1.ClaveUsuario,e2.IdTipoEvaluacion, " +
                " e2.IdTipoSubEvaluacion,e2.IdTipoSubEvaluacionPunto,e2.Puntaje,e1.ClaEmp,e1.StatusNegocio,e1.LecturaCodigo,e1.TipoEncuesta " +
                " from Evaluacion e1 inner join Evaluacion_Ejecucion e2 on e2.IdEvaluacion=e1.IdEvaluacion" +
                " inner join EvaluacionCompleta e3 on e3.IdEvaluacion=e1.IdEvaluacion " +
                " where e1.StatusEnvio=0", null);


        JSONObject obj = new JSONObject();

        JSONArray resultSet = new JSONArray();

        cursor.moveToFirst();
        while (cursor.isAfterLast() == false) {

            int totalColumn = cursor.getColumnCount();



            JSONObject rowObject = new JSONObject();

            for( int i=0 ;  i< totalColumn ; i++ )
            {
                if( cursor.getColumnName(i) != null )
                {
                    try
                    {
                        if( cursor.getString(i) != null )
                        {
                          //  Log.d("TAG_NAME", cursor.getString(i) );
                            rowObject.put(cursor.getColumnName(i) ,  cursor.getString(i) );
                        }
                        else
                        {
                            rowObject.put( cursor.getColumnName(i) ,  "" );
                        }
                    }
                    catch( Exception e )
                    {
                        //Log.d("TAG_NAME", e.getMessage()  );
                    }
                }
            }
            JSONObject encabezado = new JSONObject();
            //resultSet.put(rowObject);
            try {
                encabezado.put("Encabezado",rowObject);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            resultSet.put(encabezado);
            cursor.moveToNext();
        }
        cursor.close();
        //Log.d("TAG_NAME", obj.toString() );
        return resultSet;

    }

    public boolean isTableExists(String tableName) {

        SQLiteDatabase db = basededatos.getReadableDatabase();
        Cursor cursor = db.rawQuery("select DISTINCT tbl_name from sqlite_master where tbl_name = '" + tableName + "'", null);
        if(cursor!=null) {
            if(cursor.getCount()>0) {
                cursor.close();
                return true;
            }
            cursor.close();
        }
        return false;
    }


    public int ObtenerNumeroEncuestasCliente(String ClaCte, String ClaEmp,String Fecha)
    {

        String countQuery="Select IdEvaluacion from Evaluacion where ClaCte="+ClaCte+" and ClaEmp="+ClaEmp+" and Fecha='"+Fecha+"'";
        SQLiteDatabase db = basededatos.getReadableDatabase();
        Cursor cursor=db.rawQuery(countQuery,null);
        int cnt= cursor.getCount();
        cursor.close();
        return cnt;
    }

    public int getNumClientes() {

        String countQuery = "SELECT  * FROM clientes " ;
        SQLiteDatabase db = basededatos.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);
        int cnt = cursor.getCount();
        cursor.close();
        return cnt;
    }

    public static boolean doesDatabaseExist(Context context, String dbName) {
        File dbFile = context.getDatabasePath(dbName);
        return dbFile.exists();
    }

    public void insertarTipoSubEncuestaPunto (int IdTipoEvaluacion,int IdTipoSubEvaluacion,int IdTipoSubEvaluacionPunto,String NombrePunto,
    int ValorMinimo,int ValorMaximo,int TipoEncuesta)
    {
        SQLiteDatabase db = basededatos.getWritableDatabase();
        ContentValues valores = new ContentValues();
        valores.put("IdTipoSubEvaluacionPunto",IdTipoSubEvaluacionPunto);
        valores.put("IdTipoSubEvaluacion",IdTipoSubEvaluacion);
        valores.put("IdTipoEvaluacion",IdTipoEvaluacion);
        valores.put("NombrePunto",NombrePunto);
        valores.put("ValorMinimo",ValorMinimo);
        valores.put("ValorMaximo",ValorMaximo);
        valores.put("TipoEncuesta",TipoEncuesta);
        db.insert("TipoSubEvaluacionPuntos",null,valores);


    }

    public void insertarTipoSubEncuesta (int IdTipoEvaluacion,int IdTipoSubEvaluacion,String NombreTipoSubEvaluacion,int TipoEncuesta)
    {
            SQLiteDatabase db = basededatos.getWritableDatabase();
        ContentValues valores = new ContentValues();
        valores.put("IdTipoEvaluacion",IdTipoEvaluacion);
        valores.put("IdTipoSubEvaluacion",IdTipoSubEvaluacion);
        valores.put("NombreTipoSubEvaluacion",NombreTipoSubEvaluacion);
        valores.put("TipoEncuesta",TipoEncuesta);
        db.insert("TipoSubEvaluacion",null,valores);

    }

    public void insertarTipoEncuesta (int IdTipoEvaluacion ,String NombreTipoEvaluacion,int TipoEncuesta)
    {
        SQLiteDatabase db = basededatos.getWritableDatabase();
        ContentValues valores = new ContentValues();

        valores.put("IdTipoEvaluacion",IdTipoEvaluacion);
        valores.put("NombreTipoEvaluacion",NombreTipoEvaluacion);
        valores.put("TipoEncuesta",TipoEncuesta);
        db.insert("TipoEvaluacion",null,valores);

    }

    public void insertarCliente(clientes cliente) {
        SQLiteDatabase db = basededatos.getWritableDatabase();

        // Generar Pk
        //String idCabeceraPedido = CabecerasPedido.generarIdCabeceraPedido();

        ContentValues valores = new ContentValues();
        valores.put("ClaCte", cliente.ClaCte);
        valores.put("NomCte", cliente.NomCte);
        valores.put("AliasCte", cliente.AliasCte);
        valores.put("Calle", cliente.Calle);
        valores.put("Col", cliente.Col);
        valores.put("ClaZona", cliente.ClaZona);
        valores.put("ClaEmp", cliente.ClaEmp);
        valores.put("ClaCd",cliente.ClaCd);
        valores.put("Canal",cliente.Canal);
        valores.put("Segmento",cliente.Segmento);
        valores.put("NomGiroCom",cliente.NomGiroCom);
        valores.put("Evalua",cliente.Evalua);
        valores.put("POP",cliente.POP);

        // Insertar cabecera
       // db.insertOrThrow(BD.Tablas.CLIENTES, null, valores);
        db.insert("clientes",null,valores);

    }

    public Cursor obtenerClientes() {
        SQLiteDatabase db = basededatos.getReadableDatabase();

        String sql =  " select * from ( select s3.IdTipoEvaluacion,s1.NombreTipoEvaluacion,s3.IdTipoSubEvaluacion,s2.NombreTipoSubEvaluacion, " +
                " s3.IdTipoSubEvaluacionPunto, " +
                "UPPER(s3.NombrePunto) NombrePunto,s3.ValorMaximo,s3.ValorMinimo from TipoEvaluacion s1 " +
                "inner join TipoSubEvaluacion s2 on s2.IdTipoEvaluacion=s1.IdTipoEvaluacion " +
                "inner join TipoSubEvaluacionPuntos s3 on s3.IdTipoSubEvaluacion=s2.IdTipoSubEvaluacion " +
                " where  s3.IdTipoSubEvaluacion=1 and s3.IdTipoEvaluacion=1 "+
                " UNION "+
                "select s3.IdTipoEvaluacion,s1.NombreTipoEvaluacion,s3.IdTipoSubEvaluacion,s2.NombreTipoSubEvaluacion, " +
                " s3.IdTipoSubEvaluacionPunto, " +
                "UPPER(s3.NombrePunto) NombrePunto,s3.ValorMaximo,s3.ValorMinimo from TipoEvaluacion s1 " +
                "inner join TipoSubEvaluacion s2 on s2.IdTipoEvaluacion=s1.IdTipoEvaluacion " +
                "inner join TipoSubEvaluacionPuntos s3 on s3.IdTipoSubEvaluacion=s2.IdTipoSubEvaluacion " +
                " where  s3.TipoEncuesta=2 ) t "+
                " order by IdTipoEvaluacion,IdTipoSubEvaluacion,IdTipoSubEvaluacionPunto ";

        return db.rawQuery(sql, null);
    }

    public Cursor ObtenerTipoEncuesta(int TipoEncuesta)
    {
        SQLiteDatabase db = basededatos.getReadableDatabase();
        String sql ="select IdTipoEvaluacion,NombreTipoEvaluacion from TipoEvaluacion where TipoEncuesta="+Integer.toString(TipoEncuesta)+"";
        return db.rawQuery(sql,null);

    }

    public Cursor ObtenerCombos(int TipoEncuesta)
    {
        SQLiteDatabase db = basededatos.getReadableDatabase();
        String sql="";
        if (TipoEncuesta==1) {
             sql = "select IdTipoSubEvaluacionPunto from TipoSubEvaluacionPuntos where TipoEncuesta="+Integer.toString(TipoEncuesta)+" ";
        }
        else
        {
            sql="select IdTipoSubEvaluacionPunto from TipoSubEvaluacionPuntos where IdTipoEvaluacion=1 and IdTipoSubEvaluacion=1 " +
                    " UNION " +
                    " select IdTipoSubEvaluacionPunto from TipoSubEvaluacionPuntos where TipoEncuesta="+Integer.toString(TipoEncuesta)+" ";
        }
        return db.rawQuery(sql,null);

    }

    public Cursor ObtenerCombosIds(int IdTipoEvaluacion,int TipoEncuesta)
    {
        SQLiteDatabase db = basededatos.getReadableDatabase();
        String sql="";

             sql = "select IdTipoSubEvaluacionPunto from TipoSubEvaluacionPuntos where IdTipoEvaluacion=" + Integer.toString(IdTipoEvaluacion) + " and IdTipoSubEvaluacionPunto>6 and TipoEncuesta="+Integer.toString(TipoEncuesta)+" ";

        return db.rawQuery(sql,null);

    }

    public int ObtenerIdTipoEvaluacion (int IdTipoSubEvaluacionPunto)
    {
        SQLiteDatabase db= basededatos.getReadableDatabase();
        String sql="select IdTipoEvaluacion from TipoSubEvaluacionPuntos where IdTipoSubEvaluacionPunto="+Integer.toString(IdTipoSubEvaluacionPunto)+" ";
        Cursor query = db.rawQuery(sql, null);

         if (query!=null)
         {

             query.moveToFirst();
             return query.getInt(query.getColumnIndex("IdTipoEvaluacion"));
         }
        return 0;
    }

    public Cursor ObtenerPreguntas(int TipoEncuesta)
    {
        SQLiteDatabase db = basededatos.getReadableDatabase();
        String sql="";
        if (TipoEncuesta==1) {
             sql = "select s3.IdTipoEvaluacion,s1.NombreTipoEvaluacion,s3.IdTipoSubEvaluacion,s2.NombreTipoSubEvaluacion, " +
                    " s3.IdTipoSubEvaluacionPunto, " +
                    "UPPER(s3.NombrePunto) NombrePunto,s3.ValorMaximo,s3.ValorMinimo from TipoEvaluacion s1 " +
                    "inner join TipoSubEvaluacion s2 on s2.IdTipoEvaluacion=s1.IdTipoEvaluacion " +
                    "inner join TipoSubEvaluacionPuntos s3 on s3.IdTipoSubEvaluacion=s2.IdTipoSubEvaluacion " +
                     " where s3.TipoEncuesta=1  "+
                    " order by s3.IdTipoEvaluacion,s3.IdTipoSubEvaluacion,s3.IdTipoSubEvaluacionPunto ";
        }
        else
        {
            sql = " select * from ( select s3.IdTipoEvaluacion,s1.NombreTipoEvaluacion,s3.IdTipoSubEvaluacion,s2.NombreTipoSubEvaluacion, " +
                    " s3.IdTipoSubEvaluacionPunto, " +
                    "UPPER(s3.NombrePunto) NombrePunto,s3.ValorMaximo,s3.ValorMinimo from TipoEvaluacion s1 " +
                    "inner join TipoSubEvaluacion s2 on s2.IdTipoEvaluacion=s1.IdTipoEvaluacion " +
                    "inner join TipoSubEvaluacionPuntos s3 on s3.IdTipoSubEvaluacion=s2.IdTipoSubEvaluacion " +
                    " where  s3.IdTipoSubEvaluacion=1 and s3.IdTipoEvaluacion=1 "+
                    " UNION "+
                    "select s3.IdTipoEvaluacion,s1.NombreTipoEvaluacion,s3.IdTipoSubEvaluacion,s2.NombreTipoSubEvaluacion, " +
                    " s3.IdTipoSubEvaluacionPunto, " +
                    "UPPER(s3.NombrePunto) NombrePunto,s3.ValorMaximo,s3.ValorMinimo from TipoEvaluacion s1 " +
                    "inner join TipoSubEvaluacion s2 on s2.IdTipoEvaluacion=s1.IdTipoEvaluacion " +
                    "inner join TipoSubEvaluacionPuntos s3 on s3.IdTipoSubEvaluacion=s2.IdTipoSubEvaluacion " +
                    " where  s3.TipoEncuesta=2 ) t "+
                    " order by IdTipoEvaluacion,IdTipoSubEvaluacion,IdTipoSubEvaluacionPunto ";

        }
       // String sql="select * from Evaluacion";
        return db.rawQuery(sql,null);
    }

    public int InsertarEncuestaEnCabezado(int ClaCte,String Fecha,int ClaZona,String Comentarios,int ClaveUsuario,int ClaEmp,int StatusNegocio,int LecturaCodigo,int TipoEncuesta)
    {
        SQLiteDatabase db = basededatos.getWritableDatabase();
        ContentValues valores = new ContentValues();

        valores.put("ClaCte",Integer.toString(ClaCte));
        valores.put("Fecha",Fecha);
        valores.put("ClaZona",Integer.toString(ClaZona));
        valores.put("Comentarios",Comentarios);
        valores.put("ClaveUsuario",Integer.toString(ClaveUsuario));
        valores.put("StatusEnvio",0);
        valores.put("ClaEmp",ClaEmp);
        valores.put("StatusNegocio",StatusNegocio);
        valores.put("LecturaCodigo",LecturaCodigo);
        valores.put("TipoEncuesta",TipoEncuesta);

        long id =db.insert("Evaluacion",null,valores);


        return (int)id;
    }

    public void InsertarEncuestaDetalle (int IdEvaluacion,int IdTipoSubEvaluacionPunto,int Puntaje)
    {
        SQLiteDatabase db = basededatos.getWritableDatabase();

        SQLiteStatement statement = db.compileStatement("SELECT IdTipoEvaluacion FROM TipoSubEvaluacionPuntos WHERE IdTipoSubEvaluacionPunto = " + Integer.toString(IdTipoSubEvaluacionPunto) + "");
        int IdTipoEvaluacion =(int) statement.simpleQueryForLong();
        statement = db.compileStatement("SELECT IdTipoSubEvaluacion FROM TipoSubEvaluacionPuntos WHERE IdTipoSubEvaluacionPunto = " + Integer.toString(IdTipoSubEvaluacionPunto) + "");
        int IdTipoSubEvaluacion = (int)statement.simpleQueryForLong();

        ContentValues valores = new ContentValues();

        valores.put("IdEvaluacion",IdEvaluacion);
        valores.put("IdTipoEvaluacion",IdTipoEvaluacion);
        valores.put("IdTipoSubEvaluacion",IdTipoSubEvaluacion);
        valores.put("IdTipoSubEvaluacionPunto",IdTipoSubEvaluacionPunto);
        valores.put("Puntaje",Puntaje);
        db.insert("Evaluacion_Ejecucion",null,valores);


    }

    public void InsetarEncuestaCompleta (int IdEvaluacion)
    {
        SQLiteDatabase db = basededatos.getWritableDatabase();
        ContentValues valores = new ContentValues();
        valores.put("IdEvaluacion",IdEvaluacion);
        db.insert("EvaluacionCompleta",null,valores);
    }

    public void onDelete(){
        SQLiteDatabase db = basededatos.getWritableDatabase();

        db.delete("Evaluacion", null, null);
        db.delete("clientes", null, null);
        db.delete("Parametros", null, null);
        db.delete("TipoEvaluacion", null, null);
        db.delete("TipoSubEvaluacionPuntos", null, null);
        db.delete("Evaluacion_Ejecucion", null, null);
        db.delete("EvaluacionCompleta", null, null);
        db.delete("TipoSubEvaluacion", null, null);
    }


    public SQLiteDatabase getDb() {
        return basededatos.getWritableDatabase();
    }
}
