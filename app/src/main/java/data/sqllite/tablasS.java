package data.sqllite;

/**
 * Created by psequeda on 08/08/2016.
 */
public class tablasS {


    interface ColumnasClientes
    {
        String CLACTE="ClaCte";
        String NOMCTE="NomCte";
        String ALIASCTE="AliasCte";
        String CALLE="Calle";
        String COL="Col";
        String CLAZONA="ClaZona";
        String CLAEMP="ClaEmp";

    }

    public static class CabecerasClientes implements ColumnasClientes {

    }

    private tablasS(){}
}
