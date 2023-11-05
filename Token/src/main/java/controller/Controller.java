package controller;

import java.io.IOException;

import java.util.regex.Pattern;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;


import token.service.TokenDAO;


/**
 * Servlet implementation class Controller
 */
@WebServlet("/Controller")
public class Controller extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private static final String SQL = "((WHERE|OR)[ ]+[\\(]*[ ]*([\\(]*[0-9]+[\\)]*)[ ]*=[ ]*[\\)]*[ ]*\\3)|AND[ ]+[\\(]*[ ]*([\\(]*1[0-9]+|[2-9][0-9]*[\\)]*)[ ]*[\\(]*[ ]*=[ ]*[\\)]*[ ]*\4";
	private static final String XSS = "<(?:[^>=]|='[^']*'|=\"[^\"]*\"|=[^'\"][^\\s>]*)*>";
	
	

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
			throws ServletException, IOException 
	{
		HttpSession session = request.getSession();
		try 
		{
			String action = request.getParameter("action");
			session.setAttribute("notification", "");
			if (action == null || action.equals("")) 
			{
				
				String username = request.getParameter("username");
				String password = request.getParameter("password");
				String token = request.getParameter("token");
				System.out.println(password);
				if(ChechXSSAndSQLInjection(username))
					return;
				if(ChechXSSAndSQLInjection(password))
					return;
				if(ChechXSSAndSQLInjection(token))
					return;
				if(username != null && token != null)
				{
					if(TokenDAO.login(username, password)!= null)
					{
						TokenDAO.upisiTokenUBazu(username,token);
					}
				}
				session.invalidate();
				return;
		}
		} 
		catch (Exception e) 
		{
			System.out.println("Greska kod pravljenja novog tokena");
			session.invalidate();
		}
	}
	private static boolean ChechXSSAndSQLInjection(String input) 
	{
		return Pattern.compile(XSS).matcher(input).find() || Pattern.compile(SQL).matcher(input).find();
	}

}
