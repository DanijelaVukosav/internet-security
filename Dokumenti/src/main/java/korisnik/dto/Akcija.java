package korisnik.dto;

import java.sql.Date;

public class Akcija 
{

	private String tipAkcije;
	private String username;
	private  Date vrijeme;
	private String akcija;
	
	public Akcija() {
		super();
		// TODO Auto-generated constructor stub
	}
	public Akcija(String tipAkcije, String username, String akcija) {
		super();
		this.tipAkcije = tipAkcije;
		this.username = username;
		this.akcija = akcija;
	}
	public Akcija(String tipAkcije, String username, Date vrijeme, String akcija) {
		super();
		this.tipAkcije = tipAkcije;
		this.username = username;
		this.vrijeme = vrijeme;
		this.akcija = akcija;
	}
	public String getTipAkcije() {
		return tipAkcije;
	}
	public void setTipAkcije(String tipAkcije) {
		this.tipAkcije = tipAkcije;
	}
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	public Date getVrijeme() {
		return vrijeme;
	}
	public void setVrijeme(Date vrijeme) {
		this.vrijeme = vrijeme;
	}
	public String getAkcija() {
		return akcija;
	}
	public void setAkcija(String akcija) {
		this.akcija = akcija;
	}
	
	
}
