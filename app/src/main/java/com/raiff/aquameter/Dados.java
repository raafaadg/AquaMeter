package com.raiff.aquameter;


public class Dados {
    private String dadosTime;
    private String dadosGPS;
    private String dadosCont;

    public Dados() {}
    public Dados(String dadosTime,String dadosGPS, String dadosCont) {
        this.dadosTime = dadosTime;
        this.dadosGPS = dadosGPS;
        this.dadosCont = dadosCont;
    }

    public void setDadosTime(String dadosTime) {
        this.dadosTime = dadosTime;
    }
    public String getDadosTime() {
        return this.dadosTime;
    }

    public void setDadosGPS(String dadosGPS) {
        this.dadosGPS = dadosGPS;
    }
    public String getDadosGPS() {
        return this.dadosGPS;
    }

    public void setDadosCont(String dadosCont) {
        this.dadosCont = dadosCont;
    }
    public String getDadosCont() {
        return this.dadosCont;
    }
}
