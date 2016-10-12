/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package hl7.wim.zamowienia.models;

/**
 *
 * @author k.skowronski
 */
public class KomentarzVO {
    
    public String _NTE;
    
    public String nrKolejnySegmentu;
    
    public String zrodlo;
    
    public String uwagiDoDtety;
    
    public String kodPosilku;

    public String getNTE() {
        return _NTE;
    }

    public void setNTE(String _NTE) {
        this._NTE = _NTE;
    }

    public String getNrKolejnySegmentu() {
        return nrKolejnySegmentu;
    }

    public void setNrKolejnySegmentu(String nrKolejnySegmentu) {
        this.nrKolejnySegmentu = nrKolejnySegmentu;
    }

    public String getZrodlo() {
        return zrodlo;
    }

    public void setZrodlo(String zrodlo) {
        this.zrodlo = zrodlo;
    }

    public String getUwagiDoDtety() {
        return uwagiDoDtety;
    }

    public void setUwagiDoDtety(String uwagiDoDtety) {
        this.uwagiDoDtety = uwagiDoDtety;
    }

    public String getKodPosilku() {
        return kodPosilku;
    }

    public void setKodPosilku(String kodPosilku) {
        this.kodPosilku = kodPosilku;
    }
    
    
    
}
