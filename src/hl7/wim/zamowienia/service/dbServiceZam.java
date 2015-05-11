/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package hl7.wim.zamowienia.service;

import hl7.NewHibernateUtil;
import hl7.wim.zamowienia.models.RekordZamowieniaVO;
import hl7.wim.zamowienia.models.StanZywionychVO;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.Logger;
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
    
    
    
    public static void WgrajStanyZywionych( String posilek, List<RekordZamowieniaVO> zamowieniaWDniu )
    {
        
        for ( RekordZamowieniaVO rz : zamowieniaWDniu )
        {
            for ( StanZywionychVO sz : rz.stanyZywionych )
            {
                try
                {
                    Session session = NewHibernateUtil.getSessionFactory().openSession();
                    session.beginTransaction();

                    String str = "begin nap_hl7_tools.WGRAJ_STAN_ZYW_W_DNIU_PLAN('" + rz.kierunekKosztowNazwa  + "'"
                            + ",'" + sz.dietaNazwa + "'"
                            + ",'" + sz.dataRealizacji.substring(0,4)+"-"+sz.dataRealizacji.substring(4,6)+"-"+sz.dataRealizacji.substring(6,8) + "'"
                            + ",'1'"
                            + "," + sz.liczbaPosilkow + ");end;";
                    session.createSQLQuery(str).executeUpdate();
                    session.getTransaction().commit();

                    //System.out.println("Dok id: " + str );

                }catch (HibernateException he) {
                    he.printStackTrace();
                }
                
                
            }
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
            
            return stanZywionych;
            
        } catch (HibernateException he) {
            he.printStackTrace();
            return null;
        }
        
        
    }
    
    
}
