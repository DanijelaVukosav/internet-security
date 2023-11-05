package korisnik.service;

import java.security.MessageDigest;
import java.sql.*;
import java.util.ArrayList;
import java.util.Random;

import korisnik.dto.Korisnik;

import connectionpool.ConnectionPool;
import controller.Role;
import korisnik.beans.ListaKorisnikaBean;

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
			String hashPass=sha256(password);
			System.out.println(hashPass);
			Connection con = getConnection();
			PreparedStatement ps = con.prepareStatement("select * from nalog where username=? and password =?");
			ps.setString(1, username);
			ps.setString(2, hashPass);
			ResultSet rs = ps.executeQuery();
			if (rs.next()) {
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


	public static boolean delete(String usernameKorisnika) {
		int status = 0;
		try {
			Connection con = getConnection();
			PreparedStatement ps = con.prepareStatement("delete from nalog where username=?");
			ps.setString(1, usernameKorisnika);
			status = ps.executeUpdate();
		} catch (Exception e) {
			System.out.println("Doslo je do greske prilikom brisanja korisnika!");
		}
		return status == 1 ? true : false;
	}

	
	public static Korisnik getRecordByUsername(String username) {
		Korisnik korisnik = null;
		try {
			Connection con = getConnection();
			PreparedStatement ps = con.prepareStatement("select * from nalog where username=?");
			ps.setString(1, username);
			ResultSet rs = ps.executeQuery();
			if (rs.next()) {
				korisnik = new Korisnik();
				// u.getKorisnik().setToken(rs.getString("token"));
				korisnik.setUsername(rs.getString("username"));
				korisnik.setRoot(rs.getString("root"));
				korisnik.setDomen(rs.getString("domen"));
				korisnik.setIdrole(rs.getInt("idrole"));
				korisnik.setOperacije(rs.getString("operacije"));
			}
		} catch (Exception e) {
			System.out.println("Doslo je do greske prilikom dohvatanja korisnika " + username);
		}
		return korisnik;
	}

	public static boolean loginWithToken(Korisnik korisnik, String token) {
		try {
			Connection con = getConnection();
			PreparedStatement ps = con.prepareStatement("select token from token where username=?");
			ps.setString(1, korisnik.getUsername());
			ResultSet rs = ps.executeQuery();
			if (rs.next()) {
				String userToken = rs.getString("token");
				if (token.equals(userToken)) {
					korisnik.setToken(userToken);

					setDefaultToken(korisnik);
					return true;
				}
			}
		} catch (Exception e) {
			System.out.println("Doslo je do greske...");
		}
		return false;
	}

	public static ListaKorisnikaBean dohvatiSveKorisnike() {
		ArrayList<Korisnik> list = new ArrayList<Korisnik>();

		try {
			Connection con = getConnection();
			PreparedStatement ps = con.prepareStatement("select * from nalog where idrole!=?");
			ps.setInt(1, Role.ADMIN_SISTEMA.getValue());
			ResultSet rs = ps.executeQuery();
			while (rs.next()) {
				Korisnik korisnik = new Korisnik();
				// u.setToken(rs.getString("token"));
				korisnik.setUsername(rs.getString("username"));
				korisnik.setRoot(rs.getString("root"));
				korisnik.setOperacije(rs.getString("operacije"));
				korisnik.setDomen(rs.getString("domen"));
				korisnik.setIdrole(rs.getInt("idrole"));
				list.add(korisnik);
			}
		} catch (Exception e) {
			System.out.println("Doslo je do greske prilikom ucitavanja korisnika");
		}
		return new ListaKorisnikaBean(list);
	}

	public static boolean editUser(String staroIme, Korisnik azuriraniKorisnik) {
		try {
			Connection con = getConnection();
			PreparedStatement ps = null;
			if (azuriraniKorisnik.getPassword() != null && azuriraniKorisnik.getPassword() != "") {
				String hashPass=sha256(azuriraniKorisnik.getPassword());
				ps = con.prepareStatement(
						"update nalog set username=?,password=?,root=?,operacije=?,domen=? where username=?");
				ps.setString(1, azuriraniKorisnik.getUsername());
				ps.setString(2, hashPass);
				ps.setString(3, azuriraniKorisnik.getRoot());
				ps.setString(4, azuriraniKorisnik.getOperacije());
				ps.setString(5, azuriraniKorisnik.getDomen());
				ps.setString(6, staroIme);
			} else {
				ps = con.prepareStatement("update nalog set username=?,root=?,operacije=?,domen=? where username=?");
				ps.setString(1, azuriraniKorisnik.getUsername());
				ps.setString(2, azuriraniKorisnik.getRoot());
				ps.setString(3, azuriraniKorisnik.getOperacije());
				ps.setString(4, azuriraniKorisnik.getDomen());
				ps.setString(5, staroIme);

			}
			int status = ps.executeUpdate();
			if (status == 1)
				return true;
		} catch (Exception e) {
			System.out.println(e.getMessage() );
			e.printStackTrace();
			System.out.println("Doslo je do greske prilikom azuriranja korisnika!");
		}
		return false;
	}

	public static boolean insertUser(Korisnik noviKorisnik) {
		try 
		{
			String hashPass=sha256(noviKorisnik.getPassword());
			Connection con = getConnection();
			PreparedStatement ps = null;
			ps = con.prepareStatement("insert into nalog values (?,?,?,?,?,?)");
			ps.setString(1, noviKorisnik.getUsername());
			ps.setString(2, hashPass);
			ps.setString(3, noviKorisnik.getRoot());
			ps.setString(4, noviKorisnik.getDomen());
			ps.setString(5, noviKorisnik.getOperacije());
			ps.setInt(6, noviKorisnik.getIdrole());

			int status = ps.executeUpdate();
			if (status == 1)
			{
				insertToken(noviKorisnik);
				return true;
			}
		} catch (Exception e) {
			System.out.println("Doslo je do greske prilikom dodavanja korisnika!");
		}
		return false;
	}

	public static Korisnik loginWithSessionID(String sessionFromStorage) 
	{
		Korisnik korisnik = null;
		try 
		{
			Connection con = getConnection();
			PreparedStatement ps = con.prepareStatement("select * from logindokumenti where sessionID=?");
			ps.setString(1, sessionFromStorage);
			ResultSet rs = ps.executeQuery();
			if (rs.next()) {
				return loginWithUsername(rs.getString("username"));
			}
		} catch (Exception e) {
			System.out.println("Doslo je do greske...");
		}

		return korisnik;
	}

	private static Korisnik loginWithUsername(String username) {
		Korisnik korisnik = new Korisnik();
		try {
			Connection con = getConnection();
			PreparedStatement ps = con.prepareStatement("select * from nalog where username=?");
			ps.setString(1, username);
			ResultSet rs = ps.executeQuery();
			if (rs.next()) {
				korisnik.setUsername(rs.getString("username"));
				korisnik.setPassword(rs.getString("password"));
				korisnik.setRoot(rs.getString("root"));
				korisnik.setDomen(rs.getString("domen"));
				korisnik.setOperacije(rs.getString("operacije"));
				korisnik.setIdrole(Integer.parseInt(rs.getString("idrole")));
				setToken(korisnik);
			}
		} catch (Exception e) {
			System.out.println("Doslo je do greske...");
		}

		return korisnik;

	}
	private static void insertToken(Korisnik korisnik) {
		try 
		{
			Connection con = getConnection();
			PreparedStatement ps = null;
			ps = con.prepareStatement("insert into token values (?,?)");
			ps.setString(1, korisnik.getUsername());
			ps.setString(2, "true");

			ps.executeUpdate();
		} catch (Exception e) {
			System.out.println("Doslo je do greske prilikom dodavanja korisnika!");
		}

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

	private static void setToken(Korisnik korisnik) {
		try {
			Connection con = getConnection();
			PreparedStatement ps = con.prepareStatement("select token from token where username=?");
			ps.setString(1, korisnik.getUsername());
			ResultSet rs = ps.executeQuery();
			if (rs.next()) {

				korisnik.setToken(rs.getString("token"));
			}
		} catch (Exception e) {
			System.out.println("Doslo je do greske...");
		}

	}

	public static void AddLogin(String username, String sessionId) {
		try {
			Connection con = getConnection();
			PreparedStatement ps = con.prepareStatement("select * from loginkorisnici where username=?");
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
	public static void DeleteSession(String sessionId) {
		try {
			Connection con = getConnection();
			PreparedStatement ps = con.prepareStatement("delete from loginkorisnici where sessionID=?");
			ps.setString(1, sessionId);
			ps.executeUpdate();

		} catch (Exception e) {
			System.out.println("Doslo je do greske...");
		}

	}

	private static void InsertLogin(String username, String sessionId) {
		try {
			Connection con = getConnection();
			PreparedStatement ps = null;
			ps = con.prepareStatement("insert into loginkorisnici values (?,?)");
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
			ps = con.prepareStatement("update loginkorisnici  set sessionID=? where username=?");
			ps.setString(1, sessionId);
			ps.setString(2, username);

			ps.executeUpdate();
		} catch (Exception e) {
			System.out.println("Doslo je do greske prilikom dodavanja korisnika!");
		}
	}
}