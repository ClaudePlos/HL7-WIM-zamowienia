/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package hl7.wim.zamowienia.models;

import java.util.List;

/**
 *
 * @author k.skowronski
 */
public class StanZywionychVO {
    
    public String nrKolejnySegmentu;
    
    public String dietaID;
    public String dietaNazwa;
    public String dietaKod;
    
    public String liczbaPosilkow;
    public String jednostka;
    
    public String kodJednostkiKosztowej; // ten sam kod co w rekordzie zamowienia 
    
    public String dataRealizacji;
    
    public List<KomentarzVO> komentarz;
    
}
