package token.service;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import connectionpool.ConnectionPool;
import korisnik.dto.Korisnik;

public class TokenDAO {
	
	public static Connection getConnection() {
		Connection con = null;
		try {
			con = ConnectionPool.getInstance().checkOut();
		} catch (Exception e) {
			System.out.println("Doslo je do greske...");
		}
		return con;
	}

	public static Korisnik login(String username, String password) {
		Korisnik korisnik=null;
		try {
			Connection con = getConnection();
			PreparedStatement ps = con.prepareStatement("select * from nalog where username=? and password =?");
			ps.setString(1, username);
			ps.setString(2, password);
			ResultSet rs = ps.executeQuery();
			if (rs.next()) {
				 korisnik = new Korisnik();
				 System.out.println("Dobro procita");
				// u.getKorisnik().setToken(rs.getString("token"));
				korisnik.setUsername(rs.getString("username"));
				korisnik.setPassword(rs.getString("password"));
				korisnik.setRoot(rs.getString("root"));
				korisnik.setDomen(rs.getString("domen"));
				korisnik.setOperacije(rs.getString("operacije"));
				korisnik.setIdrole(Integer.parseInt(rs.getString("idrole")));
			}
		} catch (Exception e) {
			System.out.println("Doslo je do greske...");
		}

		return korisnik;
	}

	public static void upisiTokenUBazu(String username, String token) {
		
		try 
		{
			Connection con = getConnection();
			PreparedStatement ps = con.prepareStatement("update token set token=? where username=?");
			ps.setString(1, token);
			ps.setString(2, username);
			ps.executeUpdate();
		} catch (Exception e) 
		{
			System.out.println(e);
		}
		
	}

}
