package korisnik.beans;

import java.util.ArrayList;

import korisnik.dto.Korisnik;

public class ListaKorisnikaBean 
{
	private ArrayList<Korisnik> korisnici= new ArrayList<Korisnik>();

	public ListaKorisnikaBean(ArrayList<Korisnik> korisnici) {
		super();
		this.korisnici = korisnici;
	}

	public ListaKorisnikaBean() {
		super();
	}

	public ArrayList<Korisnik> getKorisnici() {
		return korisnici;
	}

	public void setKorisnici(ArrayList<Korisnik> korisnici) {
		this.korisnici = korisnici;
	}
	
	

}
