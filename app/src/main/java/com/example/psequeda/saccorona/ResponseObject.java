package com.example.psequeda.saccorona;

import java.util.List;

/**
 * Created by psequeda on 13/11/2015.
 */
public class ResponseObject {

    public void setRows(List<Rows> rows) {
        this.rows = rows;
    }



    private List<Rows> rows ;

    public  List<Rows> getRows()
    {

        return rows;
    }





    public static class Total
    {
        public String getTotal() {
            return total;
        }

        public void setTotal(String total) {
            this.total = total;
        }

        public String total;

    }


    public static  class Rows {




        public String getOT() {
            return OT;
        }

        public void setOT(String OT) {
            this.OT = OT;
        }

        public String getFolioOT() {
            return FolioOT;
        }

        public void setFolioOT(String folioOT) {
            FolioOT = folioOT;
        }

        public String getConcepto() {
            return Concepto;
        }

        public void setConcepto(String concepto) {
            Concepto = concepto;
        }

        public String getComentarios() {
            return Comentarios;
        }

        public void setComentarios(String comentarios) {
            Comentarios = comentarios;
        }

        public String getFechaCaptura() {
            return FechaCaptura;
        }

        public void setFechaCaptura(String fechaCaptura) {
            FechaCaptura = fechaCaptura;
        }

        public String getDepartamento() {
            return Departamento;
        }

        public void setDepartamento(String departamento) {
            Departamento = departamento;
        }

        private String OT;
        private String FolioOT;
        private String Concepto;
        private String Comentarios;
        private String FechaCaptura;
        private String Departamento;

        public String getClaveUsuario() {
            return ClaveUsuario;
        }

        public void setClaveUsuario(String claveUsuario) {
            ClaveUsuario = claveUsuario;
        }

        private String ClaveUsuario;




    }

}


