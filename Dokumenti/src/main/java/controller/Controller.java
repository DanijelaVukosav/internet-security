package controller;

import java.io.File;
import java.io.IOException;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import java.util.Timer;
import java.util.regex.Pattern;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;


import dokumenti.beans.FileTreeBean;
import dokumenti.beans.MyDirectory;
import korisnik.beans.KorisnikBean;
import korisnik.dto.Akcija;
import korisnik.service.AkcijaDAO;
import korisnik.service.KorisnikDAO;

/**
 * Servlet implementation class Controller
 */
@WebServlet("/Controller")
public class Controller extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private String googleKey = "649956311101-varqv5j1qqv4jrguvfhjsfdogl81goun.apps.googleusercontent.com";
	private static final String SQL = "((WHERE|OR)[ ]+[\\(]*[ ]*([\\(]*[0-9]+[\\)]*)[ ]*=[ ]*[\\)]*[ ]*\\3)|AND[ ]+[\\(]*[ ]*([\\(]*1[0-9]+|[2-9][0-9]*[\\)]*)[ ]*[\\(]*[ ]*=[ ]*[\\)]*[ ]*\4";
	private static final String XSS = "<(?:[^>=]|='[^']*'|=\"[^\"]*\"|=[^'\"][^\\s>]*)*>";
	
	private int brojSesijaPoAdresi=100;
	private int periodProvjereSesija=5;
	public static HashMap<String, Set<String>> adreseSesija=new HashMap<>();
	/**
	
	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	//@Override
	//public void init (ServletConfig config) throws ServletException
   static  {
		System.out.println("Postavi tajmer");
		Timer timer = new Timer(true);
		DoSTask dosTask = new DoSTask(adreseSesija);
		timer.schedule(dosTask, 0, 2 * 1000);
       
   }
	@Override
	public void service(HttpServletRequest request, HttpServletResponse response) 
			   throws ServletException, IOException 
	{
		
		String addressReq= request.getRemoteAddr();
		HttpSession session=request.getSession();
		String sessionID=request.getSession().getId();
		System.out.println(" Service session: "+sessionID);
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
			throws ServletException, IOException {
		// TODO Auto-generated method stub
		request.setCharacterEncoding("UTF-8");

		String address = "/WEB-INF/pages/login.jsp";
		String action = request.getParameter("action") == null ? "" : request.getParameter("action");
		HttpSession session = request.getSession();
		KorisnikBean korisnikBean = (KorisnikBean) session.getAttribute("korisnik");
		System.out.println("Adresa: "+ request.getRemoteAddr());
		try 
		{
			System.out.println("Token : " + korisnikBean);
			session.setAttribute("notification", "");

			if (action.equals("SSO") && korisnikBean == null) {
				String sessionFromStorage = request.getParameter("session");
				if( ChechXSSAndSQLInjection(sessionFromStorage))
				{
					session.setAttribute("notification", "Pokusaj maliciozne aktivnosti je zabiljezen!");
					session.invalidate();
					address = "/WEB-INF/pages/login.jsp";
					
				}
				else
				{
					korisnikBean = new KorisnikBean();
					if (korisnikBean.loginWithSessionID(sessionFromStorage)) {
						KorisnikDAO.AddLogin(korisnikBean.getKorisnik().getUsername(), session.getId());
	
						session.setAttribute("korisnik", korisnikBean);
						response.setStatus(200);
	
						AkcijaDAO.save("SSO", korisnikBean.getKorisnik().getUsername(),
								"Korisnik se prijavio na sistema SSO mehanizmom");
	
						return;
					} else {
						response.setStatus(404);
					}
				}

			} else if(action.equals("sessionLogout"))
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
			else if (action.equals("prviKorak")) {
				if (korisnikBean != null && korisnikBean.getKorisnik().getToken()!=null) {
					
					File directory = new File("");
					if (korisnikBean.getKorisnik().getIdrole() != Role.ADMIN_SISTEMA.getValue()) {
						directory = new File(getServletContext().getRealPath(File.separator + "WEB-INF"
								+ File.separator + "CR" + File.separator + korisnikBean.getKorisnik().getRoot()));
					} else {
						directory = new File(getServletContext()
								.getRealPath(File.separator + "WEB-INF" + File.separator + "CR"));
					}
					if (!directory.exists()) {
						directory.mkdirs();
					}
					MyDirectory direktorijum = new FileTreeBean().generateDirectoryTree(
							new MyDirectory(korisnikBean.getKorisnik().getRoot(), directory), directory.getAbsolutePath());
					session.setAttribute("stabloFajlova", direktorijum);
					address = "/WEB-INF/pages/index.jsp";
				} else {
					address = "/WEB-INF/pages/login.jsp";
				}
			} 
			else if ((action == null || action.equals("")) || (korisnikBean == null && !action.equals("login"))) {
				address = "/WEB-INF/pages/redirectToIndex.jsp";
			} 
			/*else if (korisnikBean!=null && korisnikBean.getKorisnik().getToken()== null && !action.equals("login") && !action.equals("token") && !action.equals("oauth2")) {
				address = "/WEB-INF/pages/redirectToIndex.jsp";
			} */
			/*else if (korisnikBean != null && korisnikBean.getKorisnik().getIdrole() == Role.KLIJENT.getValue()
					&& provjeriIPAdresu(korisnikBean.getKorisnik().getDomen(), request.getRemoteAddr())) {
				address = "/WEB-INF/pages/redirectToIndex.jsp";
			} 
			*/
			else if (action.equals("logout") && korisnikBean != null) {
				AkcijaDAO.save("LOGOUT", korisnikBean.getKorisnik().getUsername(), "Korisnik se odjavio sa sistema");
				session.invalidate();
				KorisnikDAO.UpdateLogin(korisnikBean.getKorisnik().getUsername(), "");
				address = "/WEB-INF/pages/redirectToIndex.jsp";
			}
			/*
			 * else if(!action.equals("login") && korisnikBean==null) { address =
			 * "/WEB-INF/pages/login.jsp"; }
			 */
			else if (action.equals("index") && korisnikBean.getKorisnik().getIdrole() == Role.ADMIN_SISTEMA.getValue()  && korisnikBean.getKorisnik().getToken()!=null) {
				File rootdirectory = new File("");
				if (korisnikBean.getKorisnik().getIdrole() != Role.ADMIN_SISTEMA.getValue()) {
					rootdirectory = new File(getServletContext().getRealPath(File.separator + "WEB-INF"
							+ File.separator + "CR" + File.separator + korisnikBean.getKorisnik().getRoot()));
				} else {
					rootdirectory = new File(getServletContext()
							.getRealPath(File.separator + "WEB-INF" + File.separator + "CR"));
				}
				MyDirectory direktorijum = new FileTreeBean().generateDirectoryTree(
						new MyDirectory(korisnikBean.getKorisnik().getRoot(), rootdirectory), rootdirectory.getAbsolutePath());
				session.setAttribute("stabloFajlova", direktorijum);
				address = "/WEB-INF/pages/index.jsp";
			} 
			else if (action.equals("akcijeKorisnika")
					&& korisnikBean.getKorisnik().getIdrole() == Role.ADMIN_SISTEMA.getValue()  && korisnikBean.getKorisnik().getToken()!=null) {
				ArrayList<Akcija> akcije = AkcijaDAO.dohvatiSveAkcije();
				session.setAttribute("akcijeKorisnika", akcije);
				address = "/WEB-INF/pages/akcijeKorisnika.jsp";
			} 
			else if (action.equals("login")) {
				System.out.println("Udje u if");
				String username = request.getParameter("username");
				String password = request.getParameter("password");
				if( ChechXSSAndSQLInjection(username) || ChechXSSAndSQLInjection(password))
				{
					session.setAttribute("notification", "Pokusaj maliciozne aktivnosti je zabiljezen!");
					session.invalidate();
					address = "/WEB-INF/pages/redirectToIndex.jsp";
					
				}
				else
				{
					korisnikBean = new KorisnikBean();
					if (korisnikBean.login(username, password)) {
						if (korisnikBean != null && korisnikBean.getKorisnik().getIdrole() == Role.KLIJENT.getValue()
								&& !provjeriIPAdresu(korisnikBean.getKorisnik().getDomen(), request.getRemoteAddr())) {
							AkcijaDAO.save("LOGIN", username,
									"Korisnik se pokusao prijaviti sa adrese " + request.getRemoteAddr() + ".");
							session.setAttribute("notification", "Pristup sa ove IP adrese nije dozvoljen.");
							address = "/WEB-INF/pages/login.jsp";
						} else {
							System.out.println("prijavi");
							session.setAttribute("korisnik", korisnikBean);
							AkcijaDAO.save("LOGIN", korisnikBean.getKorisnik().getUsername(),
									"Korisnik je prosao prvi korak autentikacije.");
							address = "/WEB-INF/pages/token.jsp";
						}
	
					} else {
						AkcijaDAO.save("LOGIN", username, "Korisnik je unio pogresne korisnicke kredencijale.");
						session.setAttribute("notification", "Pogresni parametri za pristup");
						address = "/WEB-INF/pages/login.jsp";
					}
				}
			} else if (action.equals("token") || action.equals("oauth2")) {
				if (korisnikBean != null && korisnikBean.getKorisnik().getToken() == null) {
					if (action.equals("token")) {
						String token = request.getParameter("token");
						if(ChechXSSAndSQLInjection(token))
						{
							session.setAttribute("notification", "Pokusaj maliciozne aktivnosti je zabiljezen!");
							session.invalidate();
							address = "/WEB-INF/pages/redirectToIndex.jsp";
							
						}
						else if (korisnikBean.loginWithToken(token)) {
							AkcijaDAO.save("TOKEN", korisnikBean.getKorisnik().getUsername(),
									"Korisnik se uspjesno prijavio na sistem.");

							request.changeSessionId();
							session = request.getSession();

							KorisnikDAO.AddLogin(korisnikBean.getKorisnik().getUsername(), session.getId());

							session.setAttribute("korisnik", korisnikBean);

							File directory = new File("");
							if (korisnikBean.getKorisnik().getIdrole() != Role.ADMIN_SISTEMA.getValue()) {
								directory = new File(getServletContext().getRealPath(File.separator + "WEB-INF"
										+ File.separator + "CR" + File.separator + korisnikBean.getKorisnik().getRoot()));
							} else {
								directory = new File(getServletContext()
										.getRealPath(File.separator + "WEB-INF" + File.separator + "CR"));
							}
							if (!directory.exists()) {
								directory.mkdirs();
							}

							MyDirectory direktorijum = new FileTreeBean().generateDirectoryTree(
									new MyDirectory(korisnikBean.getKorisnik().getRoot(), directory),
									directory.getAbsolutePath());
							session.setAttribute("stabloFajlova", direktorijum);

							address = "/WEB-INF/pages/index.jsp";
						} else {
							AkcijaDAO.save("TOKEN", korisnikBean.getKorisnik().getUsername(),
									"Korisnik je unio pogresnu vrijednost tokena -> " + token + " .");

							session.setAttribute("notification", "Pogresni parametri za pristup");
							address = "/WEB-INF/pages/token.jsp";
						}
					} else {
						StringBuilder oauthUrl = new StringBuilder().append("https://accounts.google.com/o/oauth2/auth")
								.append("?client_id=").append(googleKey) // the client id from the api console registration
								.append("&response_type=code").append("&scope=openid%20email") // scope is the api
																								// permissions we are
																								// requesting
								.append("&redirect_uri=https://localhost:8443/Dokumenti/?action=googleAccount")

								.append("&access_type=offline").append("&approval_prompt=force");
						response.sendRedirect(oauthUrl.toString());
						return;
					}
				} else {
					address = "/WEB-INF/pages/redirectToIndex.jsp";
				}
			} 
			else if (action.equals("googleAccount") && korisnikBean != null) 
			{
				System.out.println("Udje u google account");
				korisnikBean.getKorisnik().setToken("true");
				AkcijaDAO.save("O_AUTH_2", korisnikBean.getKorisnik().getUsername(),
						"Korisnik se uspjesno prijavio na sistem.");

				request.changeSessionId();
				session = request.getSession();

				KorisnikDAO.AddLogin(korisnikBean.getKorisnik().getUsername(), session.getId());

				session.setAttribute("korisnik", korisnikBean);

				File directory = new File("");
				if (korisnikBean.getKorisnik().getIdrole() != Role.ADMIN_SISTEMA.getValue()) {
					directory = new File(getServletContext().getRealPath("WEB-INF"
							+ File.separator + "CR" + File.separator + korisnikBean.getKorisnik().getRoot()));//File.separator + 
				} else {
					directory = new File(getServletContext()
							.getRealPath("WEB-INF" + File.separator + "CR"));//File.separator + 
				}
				if (!directory.exists()) {
					directory.mkdir();
				}

				MyDirectory direktorijum = new FileTreeBean().generateDirectoryTree(
						new MyDirectory(korisnikBean.getKorisnik().getRoot(), directory),
						directory.getAbsolutePath());
				session.setAttribute("stabloFajlova", direktorijum);

				address = "/WEB-INF/pages/index.jsp";
			} else {
				address = "/WEB-INF/pages/redirectToIndex.jsp";
			}
			Cookie cookie = new Cookie("SESS_ID", session.getId());
			cookie.setMaxAge(15);
			cookie.setSecure(true);
			response.addCookie(cookie);
		} catch (Exception e) {
			session.invalidate();
			System.out.println("Greska u kontroleru aplikacije Dokumenti.");
			address = "/WEB-INF/pages/redirectToIndex.jsp";
		}

		RequestDispatcher dispatcher = request.getRequestDispatcher(address);
		dispatcher.forward(request, response);
	}

	private boolean provjeriIPAdresu(String domen, String remoteAddr) {
		try {
			InetAddress remote = InetAddress.getByName(remoteAddr);
			InetAddress[] i = InetAddress.getAllByName(domen);

			for (InetAddress ipAddress : i) {
				if (ipAddress.equals(remote)) {
					return true;
				}
			}
		} catch (Exception e) {
			e.getMessage();
		}
		return false;
	}
	private static boolean ChechXSSAndSQLInjection(String input) {
		return Pattern.compile(XSS).matcher(input).find() || Pattern.compile(SQL).matcher(input).find() || input.length()>50;
	}

}
