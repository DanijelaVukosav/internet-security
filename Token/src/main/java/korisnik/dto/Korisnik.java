package korisnik.dto;

import java.io.Serializable;

public class Korisnik implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String username;
	private String password;
	private String root;
	private String domen;
	private String operacije;
	private int idrole;
	private String token;
	
	
	public Korisnik() {
		super();
		// TODO Auto-generated constructor stub
	}

	public Korisnik(String username, String password, String root, String domen, String operacije, int idrole, String token) {
		super();
		this.username = username;
		this.password = password;
		this.root = root;
		this.domen = domen;
		this.operacije = operacije;
		this.idrole = idrole;
		this.token=token;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getRoot() {
		return root;
	}

	public void setRoot(String root) {
		this.root = root;
	}

	public String getDomen() {
		return domen;
	}

	public void setDomen(String domen) {
		this.domen = domen;
	}

	public String getOperacije() {
		return operacije;
	}

	public void setOperacije(String operacije) {
		this.operacije = operacije;
	}

	public int getIdrole() {
		return idrole;
	}

	public void setIdrole(int idrole) {
		this.idrole = idrole;
	}

	public String getToken() {
		return token;
	}

	public void setToken(String token) {
		this.token = token;
	}
	
	

}
