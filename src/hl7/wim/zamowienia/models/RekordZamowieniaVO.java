/*
 * To change this template, choose Tools | Templates and open the template in
 * the editor.
 */
package hl7.wim.zamowienia.models;

import java.util.List;

/**
 *
 * @author k.skowronski
 */
public class RekordZamowieniaVO {
    
    public String typZamowienia;
    public String numerZamowienia;
    public String dataZlecenia;
    
    public String kierunekKosztowID;  // w CliniNEt nazywa się to J.o.
    public String kierunekKosztowNazwa;
    public String kierunekKosztowKodOddzialu;
    
    public List<StanZywionychVO> stanyZywionych;
  
}
