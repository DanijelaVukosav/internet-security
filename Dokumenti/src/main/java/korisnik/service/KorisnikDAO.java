package korisnik.service;

import java.security.MessageDigest;
import java.sql.*;
import java.util.Random;

import korisnik.dto.Korisnik;

import connectionpool.ConnectionPool;

public class KorisnikDAO {

	public static Connection getConnection() {
		Connection con = null;
		try {
			con = ConnectionPool.getInstance().checkOut();
		} catch (Exception e) {
			System.out.println("Doslo je do greske...");
		}
		return con;
	}

	private static String sha256(final String base) {
	    try{
	        final MessageDigest digest = MessageDigest.getInstance("SHA-256");
	        final byte[] hash = digest.digest(base.getBytes("UTF-8"));
	        final StringBuilder hexString = new StringBuilder();
	        for (int i = 0; i < hash.length; i++) {
	            final String hex = Integer.toHexString(0xff & hash[i]);
	            if(hex.length() == 1) 
	              hexString.append('0');
	            hexString.append(hex);
	        }
	        return hexString.toString();
	    } catch(Exception ex){
	       throw new RuntimeException(ex);
	    }
	}

	public static Korisnik login(String username, String password) {
		Korisnik korisnik = new Korisnik();
		try {
			Connection con = getConnection();
			String hashPass=sha256(password);
			PreparedStatement ps = con.prepareStatement("select * from nalog where username=? and password =?");
			ps.setString(1, username);
			ps.setString(2, hashPass);
			ResultSet rs = ps.executeQuery();
			if (rs.next()) {
				korisnik.setUsername(rs.getString("username"));
				korisnik.setPassword(rs.getString("password"));
				korisnik.setRoot(rs.getString("root"));
				korisnik.setDomen(rs.getString("domen"));
				korisnik.setOperacije(rs.getString("operacije"));
				korisnik.setIdrole(Integer.parseInt(rs.getString("idrole")));
				korisnik.setToken(null);
			}
		} catch (Exception e) {
			System.out.println("Doslo je do greske...");
		}

		return korisnik;
	}

	public static boolean loginWithToken(Korisnik korisnik, String token) {
		try 
		{
			Connection con = getConnection();
			PreparedStatement ps = con.prepareStatement("select token from token where username=?");
			ps.setString(1, korisnik.getUsername());
			ResultSet rs = ps.executeQuery();
			if (rs.next()) {
				String userToken = rs.getString("token");
				if (token.trim().equals(userToken.trim())) 
				{
					korisnik.setToken(userToken);
					setDefaultToken(korisnik);
					return true;
				}
			}
		} catch (Exception e) {
			System.out.println(e.getMessage());
			System.out.println("Doslo je do greske...");
		}
		return false;
	}
	private static void setDefaultToken(Korisnik korisnik) {
		try 
		{
			Connection con = getConnection();
			String token=Integer.toString(new Random().nextInt(1000000));
			PreparedStatement ps = null;
			ps = con.prepareStatement("update token set token=? where username=?");
			ps.setString(2, korisnik.getUsername());
			ps.setString(1, token);

			ps.executeUpdate();
		} catch (Exception e) {
			System.out.println("Doslo je do greske prilikom dodavanja korisnika!");
		}

	}


	public static Korisnik loginWithSessionID(String sessionFromStorage) {
		Korisnik korisnik = null;
		try 
		{
			Connection con = getConnection();
			PreparedStatement ps = con.prepareStatement("select * from loginkorisnici where sessionID=?");
			ps.setString(1, sessionFromStorage);
			ResultSet rs = ps.executeQuery();
			if (rs.next())
			{
				return loginWithUsername(rs.getString("username"));
			}
		} catch (Exception e) {
			System.out.println("Doslo je do greske...");
		}

		return korisnik;
	}

	private static Korisnik loginWithUsername(String username) 
	{
		Korisnik korisnik = new Korisnik();
		try 
		{
			Connection con = getConnection();
			PreparedStatement ps = con.prepareStatement("select * from nalog where username=?");
			ps.setString(1, username);
			ResultSet rs = ps.executeQuery();
			if (rs.next()) 
			{
				korisnik.setUsername(rs.getString("username"));
				korisnik.setPassword(rs.getString("password"));
				korisnik.setRoot(rs.getString("root"));
				korisnik.setDomen(rs.getString("domen"));
				korisnik.setOperacije(rs.getString("operacije"));
				korisnik.setIdrole(Integer.parseInt(rs.getString("idrole")));
				setTokenToUser(korisnik);
			}
		} 
		catch (Exception e) 
		{
			System.out.println("Doslo je do greske...");
		}

		return korisnik;

	}

	private static void setTokenToUser(Korisnik korisnik) 
	{
		try 
		{
			Connection con = getConnection();
			PreparedStatement ps = con.prepareStatement("select token from token where username=?");
			ps.setString(1, korisnik.getUsername());
			ResultSet rs = ps.executeQuery();
			if (rs.next()) 
			{

				korisnik.setToken(rs.getString("token"));
			}
		}
		catch (Exception e) 
		{
			System.out.println("Doslo je do greske...");
		}

	}

	public static void AddLogin(String username, String sessionId) {
		try 
		{
			Connection con = getConnection();
			PreparedStatement ps = con.prepareStatement("select * from logindokumenti where username=?");
			ps.setString(1, username);
			ResultSet rs = ps.executeQuery();
			if (rs.next()) {
				UpdateLogin(username, sessionId);
			} else {
				InsertLogin(username, sessionId);
			}

		} catch (Exception e) {
			System.out.println("Doslo je do greske...");
		}

	}

	private static void InsertLogin(String username, String sessionId) {
		try {
			Connection con = getConnection();
			PreparedStatement ps = null;
			ps = con.prepareStatement("insert into logindokumenti values (?,?)");
			ps.setString(2, sessionId);
			ps.setString(1, username);

			ps.executeUpdate();
		} catch (Exception e) {
			System.out.println("Doslo je do greske prilikom dodavanja korisnika!");
		}

	}

	public static void UpdateLogin(String username, String sessionId) {
		try {
			Connection con = getConnection();
			PreparedStatement ps = null;
			ps = con.prepareStatement("update logindokumenti  set sessionID=? where username=?");
			ps.setString(1, sessionId);
			ps.setString(2, username);

			ps.executeUpdate();
		} catch (Exception e) {
			System.out.println("Doslo je do greske prilikom dodavanja korisnika!");
		}
	}
	public static void DeleteSession(String sessionId) {
		try {
			Connection con = getConnection();
			PreparedStatement ps = con.prepareStatement("delete from logindokumenti where sessionID=?");
			ps.setString(1, sessionId);
			ps.executeUpdate();

		} catch (Exception e) {
			System.out.println("Doslo je do greske...");
		}

	}

}