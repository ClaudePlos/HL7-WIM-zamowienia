/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package hl7.wim.zamowienia.service;

import hl7.wim.zamowienia.models.KomentarzVO;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;
import org.jl7.hl7.HL7Message;
import org.jl7.hl7.HL7Parser;
import org.jl7.hl7.HL7Segment;
import hl7.wim.zamowienia.models.RekordZamowieniaVO;
import hl7.wim.zamowienia.models.StanZywionychVO;
import java.util.AbstractList;
        
/**
 *
 * @author k.skowronski
 */
public class DokServiceZam {
    
    private static volatile DokServiceZam instance = null;
    
    private Logger logger = null;
    
    public static DokServiceZam getInstance() {
        if (instance == null) {
          instance = new DokServiceZam();
        }
        return instance;
    }
  //  private HibernateEntityManager HibernateEntityManager;

    public DokServiceZam() {
        logger = Logger.getLogger( this.getClass().getCanonicalName());
        //logger.addHandler( WindowHandler.getInstance() );
    }
    
    
    public String parsuj( String msg)
    {
        String typZleceniaORC = null;
        String dataOperacjiORC = null;
        String kodMagazynuORC = null;
        String kodJoZamawiajacejORC = null;
        String priorytetZamORC = null;
        String numerIdClninetORC = null;
        String imieZamORC = null;
        String nazwiskoZamORC = null;
        
        String NTE01 = null;
        String NTE02 = null;
        String NTE03 = null;
        
        String ORC01 = null;
        String ORC02 = null;
        String ORC09 = null;
        String ORC13_0 = null;
        String ORC13_8 = null;
        String ORC13_9 = null;
        
        String RQD01 = null;
        String RQD02_0 = null;
        String RQD02_1 = null;
        String RQD02_2 = null;
        String RQD05 = null;
        String RQD06 = null;
        String RQD07 = null;
        String RQD10 = null;
        
        String komentarzNTE = null;

        String kodJoZamawiajacejBLG = null;
        
        HL7Message msg7 = HL7Parser.parseMessage(msg,true);
        
        String posilek = null;
        
        List<RekordZamowieniaVO> zamowienia = new ArrayList<RekordZamowieniaVO>();
        RekordZamowieniaVO zm = null; 
        StanZywionychVO stanZyw = null;
        
        try{
            System.out.println(msg7.getCount()+" lini w plik");
            for ( int i = 0; i< msg7.getCount(); i++ ) 
            {
                
                HL7Segment actSeg = msg7.get(i);
                
                if (actSeg.getSegmentType().equals("ORC"))
                {
                    zm = new RekordZamowieniaVO();
                    
                    ORC01 = actSeg.get(1).toString(); // typ zamówienia 
                    ORC02 = actSeg.get(2).get(0).get(0).toString();
                    
                     // 01 - sprawdzam czy dokument jest RWD lub ZWEW
                         if ( actSeg.get(1).toString().equals("NW") || actSeg.get(1).toString().equals("OC") || actSeg.get(1).toString().equals("SC"))
                         {
                          System.out.println("To jest dokument RWD lub ZWEW." ); 
                         }
                         else
                         {
                          System.out.println("To nie jest dokument RWD ani ZWEW, przenosze go do wyjaśnienia." );   
                          return "BLAD";  // !!! jeżeli tu robie return to dalszy kod nie jest sprawdzany
                         }
                          
                    zm.typZamowienia = ORC01;
                    zm.numerZamowienia = ORC02;     
                         
                          if ( "NW".equals(ORC01) )  // 02 tylko dla nowego zamówienia bo jak jest anulowanie zlecenia to tych danych brak
                          {
                            //priorytetZamORC = actSeg.get(7).get(0).get(5).toString();
                            ORC09 = actSeg.get(9).toString();
                            ORC13_0 = actSeg.get(13).get(0).get(0).toString();
                            ORC13_8 = actSeg.get(13).get(0).get(8).toString();
                            ORC13_9 = actSeg.get(13).get(0).get(9).toString();
                            
                            zm.dataZlecenia = ORC09;
                            zm.kierunekKosztowID = ORC13_0;
                            zm.kierunekKosztowNazwa =  ORC13_8;      
                            zm.kierunekKosztowKodOddzialu = ORC13_9;
                          }
                          else if ( "XO".equals(ORC01) ) // korekta
                          {
                            /*zm = new RekordZamowieniaVO();
                            zamowienia.add(zm);
                            zm.typZlecenia =  typZleceniaORC;
                            zm.idZamZClininet = numerIdClninetORC;  */
                          }
                          else
                          {
                              System.out.println("Plik nie jest zamowieniem, omijam go. Może to RWD"  ); 
                              return "TO_NIE_RWD";
                          }
                    
                    zm.stanyZywionych = new ArrayList<StanZywionychVO>();
                    zamowienia.add(zm);
                }
                
                   
                      
                if (actSeg.getSegmentType().equals("RQD") )
                { 
                    RQD01 = actSeg.get(1).toString();;
                    RQD02_0 = actSeg.get(2).get(0).get(0).toString();
                    RQD02_1 = actSeg.get(2).get(0).get(1).toString();
                    RQD02_2 = actSeg.get(2).get(0).get(2).toString();
                    RQD05 = actSeg.get(5).toString();;
                    RQD06 = actSeg.get(6).toString();;
                    RQD07 = actSeg.get(7).toString();;
                    RQD10 = actSeg.get(10).toString();;
                    
                    stanZyw = new StanZywionychVO();
                    stanZyw.nrKolejnySegmentu = RQD01;
                    stanZyw.dietaID = RQD02_0;
                    stanZyw.dietaNazwa = RQD02_1;
                    stanZyw.dietaKod = RQD02_2;
                    stanZyw.liczbaPosilkow = RQD05;
                    
                    stanZyw.jednostka = RQD06;
                    stanZyw.kodJednostkiKosztowej = RQD07;
                    stanZyw.dataRealizacji = RQD10;
                    
                    stanZyw.komentarz = new ArrayList<KomentarzVO>();
                    
                    zm.stanyZywionych.add(stanZyw); 
                }
                      
                if (actSeg.getSegmentType().equals("NTE") )
                {
                    NTE01 =  actSeg.get(1).toString(); 
                    NTE02 =  actSeg.get(2).toString();
                    NTE03 =  actSeg.get(3).toString();
                    
                    if (NTE03.intern().indexOf("CN_Warszawa_WIM") == -1)
                    {
                       KomentarzVO kom = new KomentarzVO();
                       kom.nrKolejnySegmentu = NTE01;
                       kom.zrodlo = NTE02;
                       kom.uwagiDoDtety = NTE03;

                       stanZyw.komentarz.add(kom); 
                    }
                    else
                    {
                        posilek = actSeg.get(3).get(0).get(1).toString();
                    }
                    
                            
                    System.out.println("NTE01: " + NTE01 + " NTE02: " + NTE02 + " NTE03: " + NTE03);
                }   
                
                
                
            }
            
            
            
            
        }catch ( Exception e) {
            System.out.println("Wystapil blad parsowania:" + e.getLocalizedMessage() );
        }

        /*List<Object> stanyZywionych = dbServiceZam.getInstance().pobierzStanyZywionych("2015-04-01");
        
        for ( Object test : stanyZywionych)
        {
             System.out.println( test.toString() );
        }*/
        
        dbServiceZam.getInstance().WgrajStanyZywionych( posilek, zamowienia );
        
        return "OK";
    }
    
}
