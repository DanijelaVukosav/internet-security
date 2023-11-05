package korisnik.beans;

import java.io.Serializable;
import korisnik.dto.Korisnik;
import korisnik.service.*;

public class KorisnikBean implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private Korisnik korisnik = new Korisnik();

	public KorisnikBean() {
	}

	public boolean login(String username, String password) {
		try 
		{
			if ((korisnik = KorisnikDAO.login(username, password)).getUsername() != null) 
			{
				return true;
			}

		} catch (Exception e) {}
		return false;
	}

	public boolean loginWithToken(String token)
	{
		try 
		{
			if (KorisnikDAO.loginWithToken(korisnik, token)) {
				return true;
			}

		} catch (Exception e) {}

		return false;
	}


	public Korisnik getKorisnik() {
		return korisnik;
	}

	public boolean loginWithSessionID(String sessionFromStorage) {
		try 
		{
			if ((this.korisnik = KorisnikDAO.loginWithSessionID(sessionFromStorage)) != null) 
			{
				return true;
			}

		} catch (Exception e) {}
		return false;
	}

}
