package com.raiff.aquameter;


public class Dados {
    // fields
    //private int dadosID;
    private String dadosTime;
    private String dadosCont;

    // constructors
    public Dados() {}
    public Dados(String dadosTime, String dadosCont) {
        //this.dadosID = dadosID;
        this.dadosTime = dadosTime;
        this.dadosCont = dadosCont;
    }
    //public void setDadosID(int dadosID) {
    //    this.dadosID = dadosID;
    //}
    //public int getDadosID() {
     //   return this.dadosID;
   // }

    public void setDadosTime(String dadosTime) {
        this.dadosTime = dadosTime;
    }
    public String getDadosTime() {
        return this.dadosTime;
    }

    public void setDadosCont(String dadosCont) {
        this.dadosCont = dadosCont;
    }
    public String getDadosCont() {
        return this.dadosCont;
    }
}
