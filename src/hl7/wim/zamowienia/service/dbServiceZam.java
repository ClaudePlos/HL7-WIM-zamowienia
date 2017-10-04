/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package hl7.wim.zamowienia.service;

import hl7.NewHibernateUtil;
import hl7.wim.zamowienia.models.KomentarzVO;
import hl7.wim.zamowienia.models.RekordZamowieniaVO;
import hl7.wim.zamowienia.models.StanZywionychVO;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.Logger;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;

/**
 *
 * @author k.skowronski 2014-04-16
 */
public class dbServiceZam {
    
    
    private static volatile dbServiceZam instance = null;
    
    private Logger logger = null;
    
    public static dbServiceZam getInstance() {
        if (instance == null) {
          instance = new dbServiceZam();
        }
        return instance;
    }
    
    public static String spradzCzyZamowienieMaMapowania( List<RekordZamowieniaVO> zamowieniaWDniu )
    {
        String ret = "Error";
        Object sprawdzenie = new Object();
        Object sprawdzenieDiety = new Object();
        
        //01 sprawdzenie 
        for ( RekordZamowieniaVO rz : zamowieniaWDniu )
        { 
            
            for ( StanZywionychVO sz : rz.stanyZywionych )
            {
                // spr GZid Obce
                System.out.println( "Sprawdzenie IdObceGZ: " + rz.kierunekKosztowID + " Sprawdzenie IdObceNazwa: " + rz.kierunekKosztowNazwa);
                System.out.println( "Zamówienie na dzień: " +  sz.dataRealizacji.substring(0,4)+"-"+sz.dataRealizacji.substring(4,6)+"-"+sz.dataRealizacji.substring(6,8) );
                Session session = NewHibernateUtil.getSessionFactory().openSession();
                session.beginTransaction();
                Query q = session.createSQLQuery( "select map_id_mapi \n" +
                    "from nap_mapowania where MAP_RODZAJ = 'JEDNOSTKA' and MAP_OPIS = 'WIM - mapowania' and map_id_obce = :IdObceGZ"); 
                sprawdzenie = q.setString("IdObceGZ", rz.kierunekKosztowID).uniqueResult();
                session.getTransaction().commit(); 
                
                if ( sprawdzenie != null )
                    ret = "OK";
                else 
                {
                  ret = "Nie można odnaleźć IdObceGZ: " + rz.kierunekKosztowID + " w tabeli nap_mapowania";  
                  return ret;
                }
                    
                
               // 
                
                // spr dieta id obce
                System.out.println( "Sprawdzenie IdObceDieta: " + sz.dietaID + " dieta nazwa: " + sz.dietaNazwa + " liczba posiłków: " + sz.liczbaPosilkow);
                session.beginTransaction();
                Query q1 = session.createSQLQuery( "select map_id_mapi \n" +
                    "from nap_mapowania \n" +
                    "where MAP_RODZAJ = 'DIETA' and MAP_OPIS = 'WIM - mapowania' and map_id_obce = :IdObceDieta"); 
                sprawdzenieDiety = q1.setString("IdObceDieta", sz.dietaID).uniqueResult();
                session.getTransaction().commit(); 
                session.close();
                
                if ( sprawdzenieDiety != null )
                    ret = "OK";
                else
                {
                   ret = "Nie można odnaleźć DietyIdObce: " + sz.dietaID + " dieta nazwa: " + sz.dietaNazwa  + " w tabeli nap_mapowania";
                   return ret;
                }
                    
                        
                     
            }
            
        }
        
        return "OK";
    }
    
    public static void WgrajStanyZywionych( String posilek, List<RekordZamowieniaVO> zamowieniaWDniu )
    {
        
    String spr = spradzCzyZamowienieMaMapowania( zamowieniaWDniu );
        
    if ( spr.equals("OK") )  /// skasować K do wgrywania 
    {
        
        
        for ( RekordZamowieniaVO rz : zamowieniaWDniu )
        { 
            for ( StanZywionychVO sz : rz.stanyZywionych )
            { 
                System.out.println( "Sprawdzenie IdObceGZ: " + rz.kierunekKosztowID );
                System.out.println( "Sprawdzenie IdDieta: " + sz.dietaID );
                try
                {
                    Session session = NewHibernateUtil.getSessionFactory().openSession();
                    session.beginTransaction();
                    
                    // log
                    String str1 = "insert into nap_test_log(q1,q2,q3,q4,q5,q6,q7,q8,q9) values "
                            + "('" + rz.kierunekKosztowNazwa  + "'"
                            + ",'" + rz.kierunekKosztowID + "'"
                            + ", to_char(sysdate,'YYYY-MM-DD HH24:MM:SS')"
                            + ",'" + sz.dataRealizacji.substring(0,4)+"-"+sz.dataRealizacji.substring(4,6)+"-"+sz.dataRealizacji.substring(6,8) +  "'"
                            + ",'" + rz.kodPosilkuWIM + "'"
                            + ",'" + sz.liczbaPosilkow + "'"
                            + ",'" + rz.typZamowienia + "'"
                            + ",'" + sz.dietaNazwa + "'"
                            + ",'" + sz.dietaID + "')";
                    session.createSQLQuery(str1).executeUpdate();
                    session.getTransaction().commit();
                  
                    
                    session.beginTransaction();
                    // kierunekKosztowID ale tak naprawdę to jes Grupa Zywionych, tak sie zakodowalo mi juz nie zmieniam
                    String str2 = "begin nap_hl7_tools.HL7_WGRAJ_STAN_ZYW_W_DNIU('" + rz.kierunekKosztowID  + "'"
                            + ",'" + sz.dietaID + "'"
                            + ",'" + sz.dataRealizacji.substring(0,4)+"-"+sz.dataRealizacji.substring(4,6)+"-"+sz.dataRealizacji.substring(6,8) + "'"
                            + "," + rz.kodPosilkuWIM
                            + "," + sz.liczbaPosilkow 
                            + ",'" + rz.typZamowienia + "'"
                            + ");end;";
                    session.createSQLQuery(str2).executeUpdate();
                    session.getTransaction().commit(); 
                    
                    session.close();

                    //System.out.println("Dok id: " + str );

                }catch (HibernateException he) {
                    he.printStackTrace();
                }
                
                
            }
        }
     
    }
    else 
    {
       System.out.println( "!!!!!!!!!! " + spr + " !!!!!!!!!");
    }
        
        
    }
    
    
    
    public List<Object> pobierzStanyZywionych( String dzien )  
    { 
        List<Object> stanZywionych = new ArrayList<Object>();
        
        try {
            Session session = NewHibernateUtil.getSessionFactory().openSession();
            session.beginTransaction();
            Query q = session.createSQLQuery( "select id_Stan_zywionych from stany_zywionych where d_obr = :dzien"); // mag medyczny w UCK
            stanZywionych = q.setString("dzien", dzien).list();            
            session.getTransaction().commit();    
            session.close();
            
            return stanZywionych;
            
        } catch (HibernateException he) {
            he.printStackTrace();
            return null;
        }
        
        
    }
    
    
}
