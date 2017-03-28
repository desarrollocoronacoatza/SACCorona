package data.modelo;

/**
 * Created by psequeda on 08/08/2016.
 */



public class clientes {


    public int ClaCte;
    public String NomCte;
    public String AliasCte;
    public String Calle;
    public String Col;
    public String ClaCd;
    public int ClaZona;
    public int ClaEmp;
    public String NomGiroCom;
    public String Canal;
    public String Segmento;
    public int Evalua;
    public int POP;

    public clientes (int ClaCte,String NomCte,String AliasCte,String Calle,String Col,String ClaCd,int ClaZona,int ClaEmp,
                     String NomGiroCom,String Canal,String Segmento,int Evalua,int POP)
    {
        this.ClaCte=ClaCte;
        this.NomCte=NomCte;
        this.AliasCte=AliasCte;
        this.Calle=Calle;
        this.Col=Col;
        this.ClaCd=ClaCd;
        this.ClaZona=ClaZona;
        this.ClaEmp=ClaEmp;
        this.NomGiroCom=NomGiroCom;
        this.Canal=Canal;
        this.Segmento=Segmento;
        this.Evalua=Evalua;
        this.POP=POP;
    }

    public int getClaCte ()
    {
        return ClaCte;
    }

    public String getNomCte(){return NomCte;}
    public String getAliasCte(){return AliasCte;}
    public String getCalle(){return Calle;}
    public String getCol(){return Col;}
    public String getClaCd(){return ClaCd;}
    public int getClaZona(){return ClaZona;}
    public int getClaEmp(){return ClaEmp;}
    public String getCanal(){return Canal;}
    public String getSegmento(){return Segmento;}
    public String getNomGiroCom(){return NomGiroCom;}



}
