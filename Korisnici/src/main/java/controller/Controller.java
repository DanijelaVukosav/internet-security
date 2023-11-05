package controller;


import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import java.util.Timer;
import java.util.regex.Pattern;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.jasper.tagplugins.jstl.core.If;

import korisnik.beans.KorisnikBean;
import korisnik.beans.ListaKorisnikaBean;
import korisnik.dto.Korisnik;
import korisnik.service.KorisnikDAO;

/**
 * Servlet implementation class Controller
 */
@WebServlet("/Controller")
public class Controller extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private static final String SQL = "((WHERE|OR)[ ]+[\\(]*[ ]*([\\(]*[0-9]+[\\)]*)[ ]*=[ ]*[\\)]*[ ]*\\3)|AND[ ]+[\\(]*[ ]*([\\(]*1[0-9]+|[2-9][0-9]*[\\)]*)[ ]*[\\(]*[ ]*=[ ]*[\\)]*[ ]*\4";
	private static final String XSS = "<(?:[^>=]|='[^']*'|=\"[^\"]*\"|=[^'\"][^\\s>]*)*>";
	
	private int brojSesijaPoAdresi=100;
	private int periodProvjereSesija=5;
	public static HashMap<String, Set<String>> adreseSesija=new HashMap<>();
	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public Controller() {
		super();
		// TODO Auto-generated constructor stub
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	//@Override
	//public void init (ServletConfig config) throws ServletException
   static  {
		System.out.println("Postavi tajmer");
		Timer timer = new Timer(true);
		DoSTask dosTask = new DoSTask();
		timer.schedule(dosTask, 0, 2 * 1000);
       
   }
	@Override
	public void service(HttpServletRequest request, HttpServletResponse response) 
			   throws ServletException, IOException 
	{
		
		String addressReq= request.getRemoteAddr();
		HttpSession session=request.getSession();
		String sessionID=request.getSession().getId();
		if(adreseSesija.containsKey(addressReq))
		{
			Set<String> sessions=adreseSesija.get(addressReq);
			if(sessions.size()<brojSesijaPoAdresi)
			{
				sessions.add(sessionID);
			}
			else 
			{
				
				request.getSession().invalidate();
				return;
			}
			
		}
		else {
			Set<String> set=new HashSet<>();
			set.add(sessionID);
			adreseSesija.put(addressReq, set);
		}
		doPost(request, response);
	}
	
	
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		// TODO Auto-generated method stub
		doPost(request, response);
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException 
	{
		String address = "/WEB-INF/pages/redirectToIndex.jsp";
		HttpSession session = request.getSession();
		try 
		{
			request.setCharacterEncoding("UTF-8");
			String action = request.getParameter("action")==null?"": request.getParameter("action") ;
			
			KorisnikBean korisnikBean = (KorisnikBean) session.getAttribute("korisnik");
			session.setAttribute("notification", "");
			if (action.equals("SSO") && korisnikBean == null) 
			{
				String sessionFromStorage = request.getParameter("session");
				if(ChechXSSAndSQLInjection(sessionFromStorage))
					return;
				korisnikBean = new KorisnikBean();
				
				if (korisnikBean.loginWithSessionID(sessionFromStorage) && korisnikBean.getKorisnik().getIdrole()==Role.ADMIN_SISTEMA.getValue()) 
				{
					KorisnikDAO.AddLogin(korisnikBean.getKorisnik().getUsername(),session.getId());
					session.setAttribute("korisnik", korisnikBean);
					response.setStatus(200);
					return;
				} 
				else 
				{
					response.setStatus(404);
				}
	
			} 
			else if(action.equals("prviKorak"))
			{
				if( korisnikBean != null)
				{
					ListaKorisnikaBean listaKorisnika = KorisnikDAO.dohvatiSveKorisnike();
					session.setAttribute("listaKorisnika", listaKorisnika);
					address = "/WEB-INF/pages/viewusers.jsp";
				}
				else 
				{
					address = "/WEB-INF/pages/login.jsp";
				}
			}
			else if(action.equals("sessionLogout"))
			{
				String sessionID=request.getParameter("sessionID");
				HttpSession session2=HttpSessionCollector.find(sessionID);
				if(session2!=null)
				{
					System.out.println("Invalidira sesiju");
					session2.invalidate();
				}
				return;
			}
			else if (action == null || action.equals("") ||  (korisnikBean==null && !action.equals("login") ) ) 
			{
				address = "/WEB-INF/pages/redirectToIndex.jsp";
			}
			else if(korisnikBean != null && korisnikBean.getKorisnik().getToken() != null  && korisnikBean.getKorisnik().getIdrole() != Role.ADMIN_SISTEMA.getValue())
			{
				session.setAttribute("korisnik", null);
				address = "/WEB-INF/pages/login.jsp";
			}
			else if (action.equals("logout")) 
			{
				session.invalidate();
				KorisnikDAO.UpdateLogin(korisnikBean.getKorisnik().getUsername(), "");
				address = "/WEB-INF/pages/redirectToIndex.jsp";
			} 
			else if (action.equals("login")) 
			{
				if (korisnikBean == null) 
				{
					String username = request.getParameter("username");
					String password = request.getParameter("password");
					if(ChechXSSAndSQLInjection(password) || ChechXSSAndSQLInjection(username))
					{
						session.setAttribute("notification", "Pokusaj maliciozne aktivnosti je zabiljezen!");
						session.invalidate();
						address = "/WEB-INF/pages/login.jsp";
						
					}
					else
					{
						korisnikBean = new KorisnikBean();
						if (korisnikBean.login(username, password) && korisnikBean.getKorisnik().getIdrole() == Role.ADMIN_SISTEMA.getValue()) 
						{
							session.setAttribute("korisnik", korisnikBean);
							address = "/WEB-INF/pages/token.jsp";
						} 
						else 
						{
							session.setAttribute("notification", "Pogresni parametri za pristup");
							address = "/WEB-INF/pages/login.jsp";
						}
						
					}
					
				} else 
				{
					address = "/WEB-INF/pages/redirectToIndex.jsp";
				}
			}
			else if (action.equals("token")) 
			{

				String token = request.getParameter("token");

				if(token != null && ChechXSSAndSQLInjection(token))
				{
					session.setAttribute("notification", "Pokusaj maliciozne aktivnosti je zabiljezen!");
					session.invalidate();
					address = "/WEB-INF/pages/login.jsp";
					
				}
				else if (korisnikBean != null && korisnikBean.getKorisnik().getToken() == null && token != null) 
				{
					if (korisnikBean.loginWithToken(token)) 
					{
						request.changeSessionId();
						session=request.getSession();
						
						KorisnikDAO.AddLogin(korisnikBean.getKorisnik().getUsername(),session.getId());
						
						session.setAttribute("korisnik", korisnikBean);
						
						ListaKorisnikaBean listaKorisnika = KorisnikDAO.dohvatiSveKorisnike();
						session.setAttribute("listaKorisnika", listaKorisnika);
						address = "/WEB-INF/pages/viewusers.jsp";
					} 
					else 
					{
						session.setAttribute("notification", "Pogresni parametri za pristup");
						address = "/WEB-INF/pages/token.jsp";
					}
				} 
				else 
				{
					address = "/WEB-INF/pages/redirecToIndex.jsp";
				}
			} 
			else if (action.equals("edit") && korisnikBean.getKorisnik().getToken() != null) 
			{
				String usernameKorisnika = request.getParameter("username");
	
				if(usernameKorisnika != null && ChechXSSAndSQLInjection(usernameKorisnika))
				{
					session.setAttribute("notification", "Pokusaj maliciozne aktivnosti je zabiljezen!");
					session.invalidate();
					address = "/WEB-INF/pages/login.jsp";
					
				}
				else if (usernameKorisnika != null) {
					Korisnik korisnik = KorisnikDAO.getRecordByUsername(usernameKorisnika);
					if (korisnik != null) {
						request.setAttribute("editKorisnik", korisnik);
						address = "/WEB-INF/pages/editform.jsp";
					} else {
						// session.setAttribute("notification", "Pogresni parametri za pristup");
						address = "/WEB-INF/pages/404.jsp";
					}
				} else {
					ListaKorisnikaBean listaKorisnika = KorisnikDAO.dohvatiSveKorisnike();
					session.setAttribute("listaKorisnika", listaKorisnika);
					address = "/WEB-INF/pages/viewusers.jsp";
				}
			} else if (action.equals("submitedit") && korisnikBean.getKorisnik().getToken() != null) {
				String staroIme = request.getParameter("usernameOld");
				String novoIme = request.getParameter("username");
				String root=request.getParameter("root");
				String password=request.getParameter("password");
				String domen=request.getParameter("domen");
				if(staroIme==null || novoIme== null || root== null)
				{
					session.setAttribute("notification", "Sva polja moraju biti popunjena, pokusajte ponovo!");
					address = "/WEB-INF/pages/viewusers.jsp";
				}
				else if(ChechXSSAndSQLInjection(staroIme) || ChechXSSAndSQLInjection(novoIme) || ChechXSSAndSQLInjection(root) || root.contains(".."))
				{
					session.setAttribute("notification", "Pokusaj maliciozne aktivnosti je zabiljezen!");
					session.invalidate();
					address = "/WEB-INF/pages/login.jsp";
					
				}
				else if(domen!=null && ChechXSSAndSQLInjection(domen))
				{
					session.setAttribute("notification", "Pokusaj maliciozne aktivnosti je zabiljezen!");
					session.invalidate();
					address = "/WEB-INF/pages/login.jsp";
				}
				else if (staroIme != null && novoIme != null) {
					Korisnik azuriraniKorisnik = new Korisnik();
					azuriraniKorisnik.setUsername(novoIme);
					azuriraniKorisnik.setPassword(password);
					azuriraniKorisnik.setRoot(root);
					azuriraniKorisnik.setDomen(domen);
					String operacije = "";
					operacije += request.getParameter("create") != null ? "C" : "";
					operacije += request.getParameter("retrive") != null ? "R" : "";
					operacije += request.getParameter("update") != null ? "U" : "";
					operacije += request.getParameter("delete") != null ? "D" : "";
					azuriraniKorisnik.setOperacije(operacije);
					if (KorisnikDAO.editUser(staroIme, azuriraniKorisnik)) {
						session.setAttribute("notification", "Uspjesno ste azurirali korsinika " + staroIme + "!");
					} else {
						session.setAttribute("notification",
								"Doslo je do greske prilikom azuriranja korsnika " + staroIme + "!");
					}
					ListaKorisnikaBean listaKorisnika = KorisnikDAO.dohvatiSveKorisnike();
					session.setAttribute("listaKorisnika", listaKorisnika);
					address = "/WEB-INF/pages/viewusers.jsp";
				} else {
					session.setAttribute("notification",
							"Doslo je do greske prilikom azuriranja korsinika " + staroIme + "!");
					address = "/WEB-INF/pages/viewusers.jsp";
				}
				ListaKorisnikaBean listaKorisnika = KorisnikDAO.dohvatiSveKorisnike();
				session.setAttribute("listaKorisnika", listaKorisnika);
				address = "/WEB-INF/pages/viewusers.jsp";
			} else if (action.equals("delete") && korisnikBean.getKorisnik().getToken() != null) {
				String usernameKorisnika = request.getParameter("username");
	
				if(usernameKorisnika != null && ChechXSSAndSQLInjection(usernameKorisnika))
				{
					session.setAttribute("notification", "Pokusaj maliciozne aktivnosti je zabiljezen!");
					session.invalidate();
					address = "/WEB-INF/pages/login.jsp";
					
				}
				else if (usernameKorisnika != null) {
	
					if (KorisnikDAO.delete(usernameKorisnika)) {
						session.setAttribute("notification",
								"Uspjesno ste izbrisali korisnika: " + usernameKorisnika + "!");
					} else {
						session.setAttribute("notification",
								"Doslo je do greske prilikom brisanja korisnika: " + usernameKorisnika + "!");
					}
	
				} else {
					session.setAttribute("notification",
							"Doslo je do greske prilikom brisanja korisnika: " + usernameKorisnika + "!");
				}
				ListaKorisnikaBean listaKorisnika = KorisnikDAO.dohvatiSveKorisnike();
				session.setAttribute("listaKorisnika", listaKorisnika);
				address = "/WEB-INF/pages/viewusers.jsp";
			} else if (action.equals("add") && korisnikBean.getKorisnik().getToken() != null) {
				address = "/WEB-INF/pages/adduserform.jsp";
			} else if (action.equals("addAdmin") && korisnikBean.getKorisnik().getToken() != null) {
				address = "/WEB-INF/pages/add-admin-form.jsp";
			} else if (action.equals("submitadd") && korisnikBean.getKorisnik().getToken() != null) {
				String novoIme = request.getParameter("username");
				String root=request.getParameter("root");
				String password=request.getParameter("password");
				String domen=request.getParameter("domen");
				if( ChechXSSAndSQLInjection(novoIme) || ChechXSSAndSQLInjection(root) || ChechXSSAndSQLInjection(domen) || root.contains(".."))
				{
					session.setAttribute("notification", "Pokusaj maliciozne aktivnosti je zabiljezen!");
					session.invalidate();
					address = "/WEB-INF/pages/login.jsp";
					
				}
				else
				{
					Korisnik noviKorisnik = new Korisnik();
					noviKorisnik.setUsername(novoIme);
					noviKorisnik.setPassword(password);
					noviKorisnik.setRoot(root);
					noviKorisnik.setDomen(domen);
					noviKorisnik.setIdrole(Role.KLIJENT.getValue());
					String operacije = "";
					operacije += request.getParameter("create") != null ? "C" : "";
					operacije += request.getParameter("retrive") != null ? "R" : "";
					operacije += request.getParameter("update") != null ? "U" : "";
					operacije += request.getParameter("delete") != null ? "D" : "";
					noviKorisnik.setOperacije(operacije);
					if (KorisnikDAO.insertUser(noviKorisnik)) {
						session.setAttribute("notification",
								"Uspjesno ste dodali korsnika " + noviKorisnik.getUsername() + "!");
					} else {
						session.setAttribute("notification",
								"Doslo je do greske prilikom dodavanja novog korisnika.. Pokusajte ponovo.");
					}
					ListaKorisnikaBean listaKorisnika = KorisnikDAO.dohvatiSveKorisnike();
					session.setAttribute("listaKorisnika", listaKorisnika);
					address = "/WEB-INF/pages/viewusers.jsp";
				}
	
			} else if (action.equals("submitAddAdmin") && korisnikBean.getKorisnik().getToken() != null) {
				String novoIme = request.getParameter("username");
				String root=request.getParameter("root");
				String password=request.getParameter("password");
				if( ChechXSSAndSQLInjection(novoIme) || ChechXSSAndSQLInjection(root))
				{
					session.setAttribute("notification", "Pokusaj maliciozne aktivnosti je zabiljezen!");
					session.invalidate();
					address = "/WEB-INF/pages/login.jsp";
					
				}
				else
				{
					Korisnik noviKorisnik = new Korisnik();
					noviKorisnik.setUsername(novoIme);
					noviKorisnik.setPassword(password);
					noviKorisnik.setRoot(root);
					noviKorisnik.setDomen("");
					noviKorisnik.setIdrole(Role.ADMIN_DOKUMENTA.getValue());
					noviKorisnik.setOperacije("CRUD");
					if (KorisnikDAO.insertUser(noviKorisnik)) {
						session.setAttribute("notification",
								"Uspjesno ste dodali korsnika " + noviKorisnik.getUsername() + "!");
					} else {
						session.setAttribute("notification",
								"Doslo je do greske prilikom dodavanja novog korisnika.. Pokusajte ponovo.");
					}
					ListaKorisnikaBean listaKorisnika = KorisnikDAO.dohvatiSveKorisnike();
					session.setAttribute("listaKorisnika", listaKorisnika);
					address = "/WEB-INF/pages/viewusers.jsp";
				}
	
			} else {
				address = "/WEB-INF/pages/404.jsp";
			}
	
			Cookie cookie = new Cookie("SESS_ID",session.getId()); 
			cookie.setMaxAge(15);
			//cookie.setSecure(true);
			response. addCookie(cookie);
		} 
		catch (Exception e) 
		{
			session.invalidate();
			address = "/WEB-INF/pages/login.jsp";
			System.out.println("DOslo je do greske u kontroleru!");
			e.printStackTrace();
		}

		 
		RequestDispatcher dispatcher = request.getRequestDispatcher(address);
		dispatcher.forward(request, response);
	}
	private static boolean ChechXSSAndSQLInjection(String input) {
		return Pattern.compile(XSS).matcher(input).find() || Pattern.compile(SQL).matcher(input).find() || input.length()>50;
	}


}
