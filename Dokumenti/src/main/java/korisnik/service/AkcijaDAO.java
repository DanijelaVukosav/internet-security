package korisnik.service;

import java.sql.*;  
import java.util.ArrayList;  

import korisnik.dto.Akcija;

import connectionpool.ConnectionPool;

public class AkcijaDAO 
{
	public static Connection getConnection()
	{  
	    Connection con=null;  
	    try
	    {  
	        con=ConnectionPool.getInstance().checkOut(); 
	    }
	    catch(Exception e){System.out.println("Doslo je do greske...");}  
	    return con;  
	}  
	public static int save(Akcija akcija)
	{  
	    int status=0;  
	    try
	    {  	
	        Connection con=getConnection();  
	        PreparedStatement ps=con.prepareStatement("insert akcija(tipAkcije,username,vrijeme,akcija) values(?,?,now(),?)");  
	        ps.setString(1,akcija.getTipAkcije());  
	        ps.setString(2,akcija.getUsername());
	        ps.setString(3,akcija.getAkcija());
	        status=ps.executeUpdate();  
	    }
	    catch(Exception e)
	    {
	    	System.out.println(e);
	    }  
	    return status;  
	}  
	public static int save(String tipAkcije,String username,String akcija)
	{  
	    int status=0;  
	    try
	    {  	
	        Connection con=getConnection();  
	        PreparedStatement ps=con.prepareStatement("insert akcija(tipAkcije,username,vrijeme,akcija) values(?,?,now(),?)");  
	        ps.setString(1,tipAkcije);  
	        ps.setString(2,username);
	        ps.setString(3,akcija);
	        status=ps.executeUpdate();  
	    }
	    catch(Exception e)
	    {
	    	System.out.println(e);
	    }  
	    return status;  
	}  
	public static ArrayList<Akcija> dohvatiSveAkcije()
	{  
	    ArrayList<Akcija> akcije=new ArrayList<>();
	    try
	    {  	
	        Connection con=getConnection();  
	        PreparedStatement ps=con.prepareStatement("select * from akcija order by vrijeme desc");
	        ResultSet rs=ps.executeQuery();  
	        while(rs.next())
	        {    
	        	Akcija akcija=new Akcija();
	            akcija.setUsername(rs.getString("username"));  
	            akcija.setAkcija(rs.getString("akcija"));  
	            akcija.setTipAkcije(rs.getString("tipAkcije"));  
	            akcija.setVrijeme(rs.getDate("vrijeme"));  
	            akcije.add(akcija);  
	        }   
	    }
	    catch(Exception e)
	    {
	    	System.out.println("Doslo je do greske...");
	    }  
	    
	    return akcije;  
	}  

}
