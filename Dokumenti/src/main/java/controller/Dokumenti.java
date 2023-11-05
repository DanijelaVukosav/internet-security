package controller;

import java.io.*;
import java.util.regex.Pattern;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.servlet.http.Part;


import dokumenti.beans.FileTreeBean;
import dokumenti.beans.MyDirectory;
import korisnik.beans.KorisnikBean;
import korisnik.service.AkcijaDAO;

/**
 * Servlet implementation class Dokumenti
 */
@WebServlet("/Dokumenti")
@MultipartConfig(fileSizeThreshold = 1024 * 1024 * 1, // 1 MB
		maxFileSize = 1024 * 1024 * 10, // 10 MB
		maxRequestSize = 1024 * 1024 * 15 // 15 MB
)
public class Dokumenti extends HttpServlet {
	private static final long serialVersionUID = 1L;
	//private static final String SQL = "((WHERE|OR)[ ]+[\\(]*[ ]*([\\(]*[0-9]+[\\)]*)[ ]*=[ ]*[\\)]*[ ]*\\3)|AND[ ]+[\\(]*[ ]*([\\(]*1[0-9]+|[2-9][0-9]*[\\)]*)[ ]*[\\(]*[ ]*=[ ]*[\\)]*[ ]*\4";
	private static final String XSS = "<(?:[^>=]|='[^']*'|=\"[^\"]*\"|=[^'\"][^\\s>]*)*>";
	

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public Dokumenti() {
		super();
		// TODO Auto-generated constructor stub
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		String address = "/WEB-INF/pages/redirectToIndex.jsp";
		RequestDispatcher dispatcher = request.getRequestDispatcher(address);
		dispatcher.forward(request, response);
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		request.setCharacterEncoding("UTF-8");
		String address = "/WEB-INF/pages/login.jsp";
		String action = request.getParameter("action");
		HttpSession session = request.getSession();
		KorisnikBean korisnikBean = (KorisnikBean) session.getAttribute("korisnik");
		session.setAttribute("notification", "");

		try {
			if (korisnikBean == null || korisnikBean.getKorisnik().getToken() == null)
			{
				address = "/WEB-INF/pages/redirectToIndex.jsp";
			}
			else if (korisnikBean.getKorisnik().getToken() == null) 
			{
				address = "/WEB-INF/pages/login.jsp";
			}
			else if (action == null || action.equals("")) 
			{
				File directory = new File("");
				if (korisnikBean.getKorisnik().getIdrole() != Role.ADMIN_SISTEMA.getValue()) 
				{
					directory = new File(getServletContext().getRealPath("WEB-INF"
							+ File.separator + "CR" + File.separator + korisnikBean.getKorisnik().getRoot()));//File.separator + 
				}
				else 
				{
					directory = new File(getServletContext()
							.getRealPath("WEB-INF" + File.separator + "CR"));//File.separator + 
				}

				MyDirectory direktorijum = new FileTreeBean().generateDirectoryTree(
						new MyDirectory(korisnikBean.getKorisnik().getRoot(), directory),
						directory.getAbsolutePath());
				session.setAttribute("stabloFajlova", direktorijum);

				address = "/WEB-INF/pages/index.jsp";
			} 
			else if (action.equals("download")) 
			{

				int ARBITARY_SIZE = 1048;
				String file = request.getParameter("id");
				if (file == null || file.equals("")) 
				{
					session.setAttribute("notification",
							"Greska.. Potrebno je oznaciti zeljenu stranicu prije izvrsavanja akcije.");
					
					address = "/WEB-INF/pages/index.jsp";
				} 
				else if (ChechXSS(file) || !korisnikBean.getKorisnik().getOperacije().contains("R")) 
				{
					AkcijaDAO.save("DOWNLOAD", korisnikBean.getKorisnik().getUsername(),
							"Korisnik je pokusao preuzeti datoteku " + file + ".");
					session.setAttribute("notification",
							"Vas pokusaj maliciozne aktivnosti je zabiljezen! Za svaku malicioznu aktivnost sekrivicno odgovara!");
					address = "/WEB-INF/pages/index.jsp";
				}
				else if (!file.startsWith(korisnikBean.getKorisnik().getRoot()) && korisnikBean.getKorisnik().getIdrole()!=Role.ADMIN_SISTEMA.getValue()) 
				{
					AkcijaDAO.save("DOWNLOAD", korisnikBean.getKorisnik().getUsername(),
							"Korisnik je pokusao preuzeti datoteku " + file + ".");
					session.setAttribute("notification",
							"Izmijenjena je putanja fajla .Vas pokusaj maliciozne aktivnosti je zabiljezen! Za svaku malicioznu aktivnost sekrivicno odgovara!");
					address = "/WEB-INF/pages/index.jsp";
				} 
				else
				{
					String fileName = file.substring(file.lastIndexOf(File.separator) + 1);
					response.setHeader("Content-disposition", "attachment; filename=" + fileName);

					try (InputStream in = request.getServletContext().getResourceAsStream(
							File.separator + "WEB-INF" + File.separator + "CR" + File.separator + file);
							OutputStream out = response.getOutputStream()) 
					{

						byte[] buffer = new byte[ARBITARY_SIZE];

						int numBytesRead;
						while ((numBytesRead = in.read(buffer)) > 0) 
						{
							out.write(buffer, 0, numBytesRead);
						}
						AkcijaDAO.save("DOWNLOAD", korisnikBean.getKorisnik().getUsername(),
								"Korisnik je preuzeo datoteku " + file + ".");

					} catch (Exception ex) 
					{
						AkcijaDAO.save("DOWNLOAD", korisnikBean.getKorisnik().getUsername(),
								"Korisnik je pokusao preuzeti datoteku " + file + ", ali je doslo do greske.");

					}
					address = "/WEB-INF/pages/index.jsp";
					return;
					// response.sendRedirect("/WEB-INF/pages/index.jsp");
					// return;
				}
			}
			else if (action.equals("delete"))
			{

				String fileName = request.getParameter("id");

				if (fileName == null || fileName.equals("")) 
				{
					session.setAttribute("notification",
							"Greska.. Potrebno je oznaciti zeljenu stranicu prije izvrsavanja akcije.");
					address = "/WEB-INF/pages/index.jsp";
				}
				else if (ChechXSS(fileName) || !korisnikBean.getKorisnik().getOperacije().contains("D")) 
				{
					AkcijaDAO.save("DELETE", korisnikBean.getKorisnik().getUsername(),
							"Korisnik je pokusao izbrisati datoteku " + fileName + ".");
					session.setAttribute("notification",
							"Vas pokusaj maliciozne aktivnosti je zabiljezen! Za svaku malicioznu aktivnost sekrivicno odgovara!");
					address = "/WEB-INF/pages/index.jsp";
				} 
				else if (!fileName.startsWith(korisnikBean.getKorisnik().getRoot()) && korisnikBean.getKorisnik().getIdrole()!=Role.ADMIN_SISTEMA.getValue())
				{
					AkcijaDAO.save("DELETE", korisnikBean.getKorisnik().getUsername(),
							"Korisnik je pokusao izbrisati datoteku " + fileName + ".");
					session.setAttribute("notification",
							"Izmijenjena je putanja fajla .Vas pokusaj maliciozne aktivnosti je zabiljezen! Za svaku malicioznu aktivnost sekrivicno odgovara!");
					address = "/WEB-INF/pages/index.jsp";
				}
				else 
				{
					File file = new File(getServletContext()
							.getRealPath(File.separator + "WEB-INF" + File.separator + "CR" + File.separator + fileName));
					if (file != null && file.exists() && file.isDirectory()
							&& korisnikBean.getKorisnik().getIdrole() == Role.KLIJENT.getValue()) 
					{
						session.setAttribute("notification", "Direktorijum ne moze biti izbrisan");
						AkcijaDAO.save("DELETE", korisnikBean.getKorisnik().getUsername(),
								"Korisnik je pokusao izbrisati datoteku " + fileName + ".");

					} else if (file.exists()) 
					{
						AkcijaDAO.save("DELETE", korisnikBean.getKorisnik().getUsername(),
								"Korisnik je izbrisao datoteku " + fileName + ".");

						file.delete();
						session.setAttribute("notification", "Fajl je uspjesno izbrisan!");
						File directory = new File("");
						if (korisnikBean.getKorisnik().getIdrole() != Role.ADMIN_SISTEMA.getValue()) 
						{
							directory = new File(getServletContext().getRealPath("WEB-INF"
									+ File.separator + "CR" + File.separator + korisnikBean.getKorisnik().getRoot()));//File.separator + 
						} else 
						{
							directory = new File(getServletContext()
									.getRealPath("WEB-INF" + File.separator + "CR"));//File.separator + 
						}

						MyDirectory direktorijum = new FileTreeBean().generateDirectoryTree(
								new MyDirectory(korisnikBean.getKorisnik().getRoot(), directory),
								directory.getAbsolutePath());
						session.setAttribute("stabloFajlova", direktorijum);
					}

					address = "/WEB-INF/pages/index.jsp";
				}
			} 
			else if (action.equals("upload")) 
			{

				String dirPath = request.getParameter("folderPath");
				String fileName = request.getParameter("fileName");

				if (dirPath == null || dirPath.equals("") || fileName == null || fileName.equals(""))
				{
					AkcijaDAO.save("UPLOAD", korisnikBean.getKorisnik().getUsername(),
							"Korisnik je pokusao poslati datoteku " + fileName + ".");
					session.setAttribute("notification", "Greska.. Potrebno je oznaciti folder prije izbora fajla.");
					address = "/WEB-INF/pages/index.jsp";
				} 
				else if (!korisnikBean.getKorisnik().getOperacije().contains("C") || ChechXSS(fileName) || ChechXSS(dirPath))
				{
					AkcijaDAO.save("UPLOAD", korisnikBean.getKorisnik().getUsername(),
							"Korisnik je pokusao poslati datoteku " + fileName + " bez dozvole.");
					session.setAttribute("notification",
							"Vas pokusaj maliciozne aktivnosti je zabiljezen! Za svaku malicioznu aktivnost sekrivicno odgovara!");
					address = "/WEB-INF/pages/index.jsp";
				}
				else 
				{

					File directory = new File("");
					if (dirPath.equals("_") && korisnikBean.getKorisnik().getIdrole()!=Role.ADMIN_SISTEMA.getValue()) {
						directory = new File(getServletContext()
								.getRealPath(File.separator + "WEB-INF" + File.separator + "CR" + File.separator + dirPath));

					} else {
						directory = new File(getServletContext()
								.getRealPath("WEB-INF" + File.separator + "CR"));//File.separator + 
					}
					
					try {

						System.out.println("Putanja foldera u koji se dodaje > "+directory.getAbsolutePath());
						Part filePart = request.getPart("file");
						System.out.println("p-" + filePart + "-");

						if (filePart == null) {
							session.setAttribute("notification", "Izaberite fajl");
							address = "/WEB-INF/pages/index.jsp";
						} 
						else 
						{
							dirPath=dirPath.equals("_")?"": (dirPath + File.separator);
							InputStream fileContent = filePart.getInputStream();
							File uploadFile = new File(
									getServletContext().getRealPath(File.separator + "WEB-INF" + File.separator + "CR"
											+ File.separator + dirPath) + File.separator + fileName);
							
							if (uploadFile.exists() == true
										&& !korisnikBean.getKorisnik().getOperacije().contains("U")) 
							{
								AkcijaDAO.save("UPLOAD", korisnikBean.getKorisnik().getUsername(),
											"Korisnik je pokusao poslati datoteku " + fileName
													+ ", sa nazivom koji se poklapa sa drugom datotekom.");
								session.setAttribute("notification","Nemate pravo izmjene postojece datoteke u folderu!");
								address = "/WEB-INF/pages/index.jsp";
							}
							else 
							{
								uploadFile.deleteOnExit();
								OutputStream out = new FileOutputStream(uploadFile);
								int ARBITARY_SIZE = 1048;
								byte[] buffer = new byte[ARBITARY_SIZE];

								int numBytesRead;
								while ((numBytesRead = fileContent.read(buffer)) > 0)
								{
										out.write(buffer, 0, numBytesRead);
									
								}
								out.close();
								session.setAttribute("notification", "Fajl je uspjesno kreiran!");
								AkcijaDAO.save("UPLOAD", korisnikBean.getKorisnik().getUsername(),
											"Korisnik je uspjesno postavio datoteku " + fileName + " u " + dirPath + ".");

								File rootdirectory = new File("");
								if (korisnikBean.getKorisnik().getIdrole() != Role.ADMIN_SISTEMA.getValue()) 
								{
										rootdirectory = new File(getServletContext().getRealPath("WEB-INF"
												+ File.separator + "CR" + File.separator + korisnikBean.getKorisnik().getRoot()));//File.separator + 
								} 
								else 
								{
										rootdirectory = new File(getServletContext()
												.getRealPath("WEB-INF" + File.separator + "CR"));//File.separator + 
								}

								MyDirectory direktorijum = new FileTreeBean().generateDirectoryTree(
											new MyDirectory(korisnikBean.getKorisnik().getRoot(), rootdirectory),
											rootdirectory.getAbsolutePath());
								session.setAttribute("stabloFajlova", direktorijum);
								address = "/WEB-INF/pages/index.jsp";
							}
							

						}
					} 
					catch (Exception e) 
					{
						AkcijaDAO.save("UPLOAD", korisnikBean.getKorisnik().getUsername(),
								"Korisnik je pokusao poslati datoteku " + fileName + ", ali je doslo do greske.");
						session.setAttribute("notification", "Doslo je do greske! Pokusaj ponovo..");
						address = "/WEB-INF/pages/index.jsp";
					}
				}
			} else if (action.equals("update")) 
			{

				String newFileName = request.getParameter("newFileUpdate");
				String oldFileName = request.getParameter("oldFileUpdate");
				System.out.println("NEW " + newFileName);
				System.out.println("OLD " + oldFileName);

				if (newFileName == null || newFileName.equals("") || oldFileName == null || oldFileName.equals("")) 
				{
					session.setAttribute("notification", "Greska.. Potrebno je oznaciti datoteku koju zelite izmijeniti.");
					address = "/WEB-INF/pages/index.jsp";
				} 
				else if (!korisnikBean.getKorisnik().getOperacije().contains("U")) 
				{
					AkcijaDAO.save("UPDATE", korisnikBean.getKorisnik().getUsername(),
							"Korisnik je pokusao azurirati datoteku " + oldFileName + ", bez dozvole za akciju.");
					session.setAttribute("notification",
							"Vas pokusaj maliciozne aktivnosti je zabiljezen! Za svaku malicioznu aktivnost sekrivicno odgovara!");
					address = "/WEB-INF/pages/index.jsp";
				}
				else 
				{
					File originalFile = new File(getServletContext().getRealPath(
							File.separator + "WEB-INF" + File.separator + "CR" + File.separator + oldFileName));

					try 
					{

						System.out.println(originalFile.getAbsolutePath());
						Part filePart = request.getPart("fileUpdate");
						System.out.println("p-" + filePart + "-");

						if (filePart == null) 
						{
							session.setAttribute("notification", "Izaberite fajl!");
							address = "/WEB-INF/pages/index.jsp";
						} else
						{

							if (ChechXSS(oldFileName) || ChechXSS(newFileName) ) {
								AkcijaDAO.save("UPDATE", korisnikBean.getKorisnik().getUsername(),
										"Korisnik je pokusao azurirati datoteku " + oldFileName + ".");
								session.setAttribute("notification",
										"Vas pokusaj maliciozne aktivnosti je zabiljezen! Za svaku malicioznu aktivnost sekrivicno odgovara!");
								address = "/WEB-INF/pages/index.jsp";
							} 
							else 
							{
								InputStream fileContent = filePart.getInputStream();
								File uploadFile = new File(
										originalFile.getParentFile().getAbsolutePath() + File.separator + newFileName);
								System.out.println(
										"Apsolutna putanja uploadovanog fajla  =>  " + uploadFile.getAbsolutePath());
								if (uploadFile.exists() == true && !uploadFile.getName().equals(originalFile.getName())) 
								{
									AkcijaDAO.save("UPDATE", korisnikBean.getKorisnik().getUsername(),
											"Korisnik je pokusao azurirati datoteku " + oldFileName + " sa " + newFileName
													+ ", ali se naziv nove datoteke poklapa sa drugom datotekom.");
									session.setAttribute("notification",
											"Postoji druga datoteka sa imenom izmijenjene datoteke, pa ne mozete izvrsiti ovu akciju nad "
													+ originalFile.getName() + " datotekom!");
									address = "/WEB-INF/pages/index.jsp";
								} 
								else 
								{
									System.out.println(originalFile.delete());
									uploadFile.deleteOnExit();
									OutputStream out = new FileOutputStream(uploadFile);
									int ARBITARY_SIZE = 1048;
									byte[] buffer = new byte[ARBITARY_SIZE];

									int numBytesRead;
									while ((numBytesRead = fileContent.read(buffer)) > 0) 
									{
										out.write(buffer, 0, numBytesRead);
									}
									out.close();
									AkcijaDAO.save("UPDATE", korisnikBean.getKorisnik().getUsername(),
											"Korisnik je azurirao datoteku " + oldFileName + " sa " + newFileName + ".");
									session.setAttribute("notification", "Fajl je uspjesno izmijenjen!");
									File rootdirectory = new File("");
									if (korisnikBean.getKorisnik().getIdrole() != Role.ADMIN_SISTEMA.getValue())
									{
										rootdirectory = new File(getServletContext().getRealPath("WEB-INF"
												+ File.separator + "CR" + File.separator + korisnikBean.getKorisnik().getRoot()));//File.separator + 
									} 
									else 
									{
										rootdirectory = new File(getServletContext()
												.getRealPath("WEB-INF" + File.separator + "CR"));//File.separator + 
									}

									MyDirectory direktorijum = new FileTreeBean().generateDirectoryTree(
											new MyDirectory(korisnikBean.getKorisnik().getRoot(), rootdirectory),
											rootdirectory.getAbsolutePath());
									session.setAttribute("stabloFajlova", direktorijum);
									address = "/WEB-INF/pages/index.jsp";
								}
							}

						}
					} 
					catch (Exception e) 
					{
						AkcijaDAO.save("UPDATE", korisnikBean.getKorisnik().getUsername(),
								"Korisnik je pokusao azurirati datoteku " + oldFileName + " sa " + newFileName
										+ ", ali je doslo do greske.");
						session.setAttribute("notification", "Doslo je do greske! Pokusaj ponovo..");
						address = "/WEB-INF/pages/index.jsp";
					}
				}
			} else if (action.equals("move")) 
			{

				String fileName = request.getParameter("fileName");
				String folderName = request.getParameter("folderName");
				if (korisnikBean.getKorisnik().getIdrole() != Role.ADMIN_DOKUMENTA.getValue()
						&& korisnikBean.getKorisnik().getIdrole() != Role.ADMIN_SISTEMA.getValue()) 
				{
					AkcijaDAO.save("MOVE", korisnikBean.getKorisnik().getUsername(),
							"Korisnik je pokusao premjestiti datoteku " + fileName + " u " + folderName
									+ ", sa ulogom klijenta.");
					session.setAttribute("notification",
							"Vas pokusaj maliciozne aktivnosti je zabiljezen! Za svaku malicioznu aktivnost se krivicno odgovara!");
					address = "/WEB-INF/pages/index.jsp";
				}
				else if (fileName == null || fileName.equals("") || folderName == null || folderName.equals("")) {
					session.setAttribute("notification",
							"Greska.. Potrebno je oznaciti zeljenu datoteku i folder u koji ce biti premjestena datoteka.");
					address = "/WEB-INF/pages/index.jsp";
				} 
				else if (ChechXSS(folderName) || ChechXSS(fileName)) 
				{
					AkcijaDAO.save("MOVE", korisnikBean.getKorisnik().getUsername(),
							"Korisnik je pokusao premjestiti datoteku " + fileName + " u " + folderName + ".");
					session.setAttribute("notification",
							"Vas pokusaj maliciozne aktivnosti je zabiljezen! Za svaku malicioznu aktivnost se krivicno odgovara!");
					address = "/WEB-INF/pages/index.jsp";
				} else if ((!fileName.startsWith(korisnikBean.getKorisnik().getRoot())
						|| !folderName.startsWith(korisnikBean.getKorisnik().getRoot())) && korisnikBean.getKorisnik().getIdrole() != Role.ADMIN_SISTEMA.getValue())
				{
					AkcijaDAO.save("MOVE", korisnikBean.getKorisnik().getUsername(),
							"Korisnik je pokusao premjestiti datoteku " + fileName + " u " + folderName + ".");
					session.setAttribute("notification",
							"Izmijenjena je putanja fajla .Vas pokusaj maliciozne aktivnosti je zabiljezen! Za svaku malicioznu aktivnost sekrivicno odgovara!");
					address = "/WEB-INF/pages/index.jsp";
				}
				else 
				{
					File originalFile = new File(getServletContext()
							.getRealPath(File.separator + "WEB-INF" + File.separator + "CR" + File.separator + fileName));
					File originalFolder = new File("");
					if (folderName.equals("_") && korisnikBean.getKorisnik().getIdrole()!=Role.ADMIN_SISTEMA.getValue()) 
					{
						originalFolder = new File(getServletContext()
								.getRealPath(File.separator + "WEB-INF" + File.separator + "CR" + File.separator + folderName));

					} 
					else 
					{
						originalFolder = new File(getServletContext()
								.getRealPath("WEB-INF" + File.separator + "CR"));//File.separator + 
					}
					if ((originalFile != null && !originalFile.exists())
							|| (originalFolder != null && !originalFolder.exists()))
					{
						AkcijaDAO.save("MOVE", korisnikBean.getKorisnik().getUsername(),
								"Korisnik je pokusao premjestiti datoteku " + fileName + " u " + folderName + ".");
						session.setAttribute("notification", "Navedeni podaci su pogresni!");

					}
					else if (originalFile != null && originalFile.isDirectory()) 
					{
						AkcijaDAO.save("MOVE", korisnikBean.getKorisnik().getUsername(),
								"Korisnik je pokusao premjestiti direktorijum " + fileName + " u " + folderName + ".");
						session.setAttribute("notification", "Direktorijum ne moze biti premjesten u drugi direktorijum!");

					}
					else if (originalFolder != null && !originalFolder.isDirectory()) 
					{
						AkcijaDAO.save("MOVE", korisnikBean.getKorisnik().getUsername(),
								"Korisnik je pokusao premjestiti datoteku " + fileName + " u " + folderName + ".");
						session.setAttribute("notification", "Odrediste mora biti direktorijum!");

					}
					else if (originalFile.exists() && originalFolder.exists()) 
					{
						File newFile = new File("");
						if (folderName.equals("_") && korisnikBean.getKorisnik().getIdrole()!=Role.ADMIN_SISTEMA.getValue()) 
						{
							newFile = new File(getServletContext().getRealPath(File.separator + "WEB-INF" + File.separator
									+ "CR"+ File.separator + originalFile.getName()));

						} 
						else 
						{
							newFile = new File(getServletContext().getRealPath(File.separator + "WEB-INF" + File.separator
									+ "CR" + File.separator + folderName + File.separator + originalFile.getName()));
						}
						
						folderName=folderName.equals("_")?"":(folderName+File.separator);
						if (newFile.exists()) 
						{
							Integer integer = 1;
							while (newFile.exists())
								newFile = new File(getServletContext()
										.getRealPath(File.separator + "WEB-INF" + File.separator + "CR" + File.separator
												+ folderName + (integer++) + originalFile.getName()));

						}
						try (InputStream in = new BufferedInputStream(new FileInputStream(originalFile));
								OutputStream out = new BufferedOutputStream(new FileOutputStream(newFile))) {

							byte[] buffer = new byte[1024];
							int lengthRead;
							while ((lengthRead = in.read(buffer)) > 0) {
								out.write(buffer, 0, lengthRead);
								out.flush();
							}
						}
						boolean boolean1 = originalFile.delete();
						System.out.println("Izbrise : " + boolean1);
						AkcijaDAO.save("MOVE", korisnikBean.getKorisnik().getUsername(),
								"Korisnik je uspjesno premjestio datoteku " + newFile.getName() + " u " + folderName + ".");
						session.setAttribute("notification", "Fajl je uspjesno premjesten!");
						File rootdirectory = new File("");
						if (korisnikBean.getKorisnik().getIdrole() != Role.ADMIN_SISTEMA.getValue()) 
						{
							rootdirectory = new File(getServletContext().getRealPath("WEB-INF"
									+ File.separator + "CR" + File.separator + korisnikBean.getKorisnik().getRoot()));//File.separator + 
						}
						else
						{
							rootdirectory = new File(getServletContext()
									.getRealPath("WEB-INF" + File.separator + "CR"));//File.separator + 
						}

						MyDirectory direktorijum = new FileTreeBean().generateDirectoryTree(
								new MyDirectory(korisnikBean.getKorisnik().getRoot(), rootdirectory),
								rootdirectory.getAbsolutePath());
						session.setAttribute("stabloFajlova", direktorijum);

					}

					address = "/WEB-INF/pages/index.jsp";
				}
			} 
			else if (action.equals("createFolder")) 
			{

				String dirPath = request.getParameter("createFolderPath");
				String dirName = request.getParameter("createFolderName");

				if (dirPath == null || dirPath.equals("") || dirName == null || dirName.equals("")) {
					session.setAttribute("notification",
							"Greska.. Potrebno je oznaciti direktorijum u koji zelite napraviti direktorijum sa unesenim imenom.");
					address = "/WEB-INF/pages/index.jsp";
				} else if (korisnikBean.getKorisnik().getIdrole() != Role.ADMIN_DOKUMENTA.getValue()
						&& korisnikBean.getKorisnik().getIdrole() != Role.ADMIN_SISTEMA.getValue()) {
					AkcijaDAO.save("CREATE_FOLDER", korisnikBean.getKorisnik().getUsername(),
							"Korisnik je pokusao kreirati direktorijum " + dirName + " u direktorijumu " + dirPath
									+ ", sa ulogom klijenta.");
					session.setAttribute("notification",
							"Vas pokusaj maliciozne aktivnosti je zabiljezen! Za svaku malicioznu aktivnost sekrivicno odgovara!");
					address = "/WEB-INF/pages/index.jsp";
				} else if (ChechXSS(dirPath) || ChechXSS(dirName)) {
					AkcijaDAO.save("CREATE_FOLDER", korisnikBean.getKorisnik().getUsername(),
							"Korisnik je pokusao kreirati direktorijum " + dirName + " u direktorijumu " + dirPath + ".");
					session.setAttribute("notification",
							"Vas pokusaj maliciozne aktivnosti je zabiljezen! Za svaku malicioznu aktivnost se krivicno odgovara!");
					address = "/WEB-INF/pages/index.jsp";
				} else {
					try {
						dirPath=dirPath.equals("_")?"":(File.separator+ dirPath);
						File directory = new File(getServletContext().getRealPath("WEB-INF" + File.separator + "CR" + dirPath));//File.separator + 
						File newDirectory = new File(getServletContext().getRealPath("WEB-INF"
								+ File.separator + "CR"+ dirPath + File.separator  + dirName));//File.separator +

						if (!directory.exists())
							throw new Exception();
						else if (newDirectory.exists()) 
						{
							AkcijaDAO.save("CREATE_FOLDER", korisnikBean.getKorisnik().getUsername(),
									"Korisnik je pokusao kreirati direktorijum " + dirName + " u direktorijumu " + dirPath
											+ ", ali takav direktorijum vec postoji.");
							session.setAttribute("notification", "Takav direktorijum vec postoji.. unesite drugo ime.");
							address = "/WEB-INF/pages/index.jsp";
						}
						else if (newDirectory.mkdir()) 
						{
							AkcijaDAO.save("CREATE_FOLDER", korisnikBean.getKorisnik().getUsername(),
									"Korisnik je uspjesno kreirao direktorijum " + dirName + " u direktorijumu " + dirPath
											+ ".");

							new AktivnostiKorisnika().sendMail(korisnikBean.getKorisnik().getUsername(), "kreirao",
									newDirectory.getName());

							session.setAttribute("notification", "Uspjesno ste kreirali direktorijum.");
							File rootdirectory = new File("");
							if (korisnikBean.getKorisnik().getIdrole() != Role.ADMIN_SISTEMA.getValue()) 
							{
								rootdirectory = new File(getServletContext().getRealPath(File.separator + "WEB-INF"
										+ File.separator + "CR" + File.separator + korisnikBean.getKorisnik().getRoot()));
							} else
							{
								rootdirectory = new File(getServletContext()
										.getRealPath(File.separator + "WEB-INF" + File.separator + "CR"));
							}

							MyDirectory direktorijum = new FileTreeBean().generateDirectoryTree(
									new MyDirectory(korisnikBean.getKorisnik().getRoot(), rootdirectory),
									rootdirectory.getAbsolutePath());
							session.setAttribute("stabloFajlova", direktorijum);
							address = "/WEB-INF/pages/index.jsp";
						} 
						else
						{
							AkcijaDAO.save("CREATE_FOLDER", korisnikBean.getKorisnik().getUsername(),
									"Korisnik je pokusao kreirati direktorijum " + dirName + " u direktorijumu " + dirPath
											+ ".");
							session.setAttribute("notification",
									"Nije moguce kreirati takav direktorijum. Pokusajte ponovo.");
							address = "/WEB-INF/pages/index.jsp";
						}

					} 
					catch (Exception e) 
					{
						AkcijaDAO.save("CREATE_FOLDER", korisnikBean.getKorisnik().getUsername(),
								"Korisnik je pokusao kreirati direktorijum " + dirName + " u direktorijumu " + dirPath
										+ ".");
						session.setAttribute("notification", "Doslo je do greske! Pokusaj ponovo..");
						address = "/WEB-INF/pages/index.jsp";
					}
				}
			} 
			else if (action.equals("deleteFolder")) 
			{

				String dirPath = request.getParameter("deleteFolder");

				if (korisnikBean.getKorisnik().getIdrole() != Role.ADMIN_DOKUMENTA.getValue()
						&& korisnikBean.getKorisnik().getIdrole() != Role.ADMIN_SISTEMA.getValue()) {
					AkcijaDAO.save("DELETE_FOLDER", korisnikBean.getKorisnik().getUsername(),
							"Korisnik je pokusao izbrisati direktorijum " + dirPath + ", sa ulogom klijenta.");
					session.setAttribute("notification",
							"Vas pokusaj maliciozne aktivnosti je zabiljezen! Za svaku malicioznu aktivnost sekrivicno odgovara!");
					address = "/WEB-INF/pages/index.jsp";
				} else if (dirPath == null) {
					session.setAttribute("notification",
							"Greska.. Potrebno je oznaciti direktorijum  koji zelite izbrisati.");
					address = "/WEB-INF/pages/index.jsp";
				}else if (dirPath.equals("") || dirPath.equals("_")) 
				{
						session.setAttribute("notification",
								"Greska.. Potrebno je oznaciti direktorijum  koji zelite izbrisati.");
						address = "/WEB-INF/pages/index.jsp";
					
				}
				 else if (ChechXSS(dirPath)) 
				 {
					AkcijaDAO.save("DELETE_FOLDER", korisnikBean.getKorisnik().getUsername(),
							"Korisnik je pokusao izbrisati direktorijum " + dirPath + ".");
					session.setAttribute("notification",
							"Vas pokusaj maliciozne aktivnosti je zabiljezen! Za svaku malicioznu aktivnost se krivicno odgovara!");
					address = "/WEB-INF/pages/index.jsp";
				} 
				 else
				 {
					try {
						File directory = new File(getServletContext().getRealPath(
								File.separator + "WEB-INF" + File.separator + "CR" +File.separator + dirPath));

						if (!directory.exists())
						{
							throw new Exception();
						}
						else {
							deleteDirectory(directory);
							if (!directory.exists())
							{
								AkcijaDAO.save("DELETE_FOLDER", korisnikBean.getKorisnik().getUsername(),
										"Korisnik je izbrisao direktorijum " + dirPath + ".");

								new AktivnostiKorisnika().sendMail(korisnikBean.getKorisnik().getUsername(), "izbrisao",
										directory.getName());
								session.setAttribute("notification", "Uspjesno ste izbrisali direktorijum.");
								
								File rootdirectory = new File("");
								if (korisnikBean.getKorisnik().getIdrole() != Role.ADMIN_SISTEMA.getValue()) 
								{
									rootdirectory = new File(getServletContext().getRealPath(File.separator + "WEB-INF"
											+ File.separator + "CR" + File.separator + korisnikBean.getKorisnik().getRoot()));
								} else 
								{
									rootdirectory = new File(getServletContext()
											.getRealPath(File.separator + "WEB-INF" + File.separator + "CR"));
								}
								MyDirectory direktorijum = new FileTreeBean().generateDirectoryTree(
										new MyDirectory(korisnikBean.getKorisnik().getRoot(), rootdirectory),
										rootdirectory.getAbsolutePath());
								session.setAttribute("stabloFajlova", direktorijum);
								address = "/WEB-INF/pages/index.jsp";
							} else 
							{
								AkcijaDAO.save("DELETE_FOLDER", korisnikBean.getKorisnik().getUsername(),
										"Korisnik je pokusao izbrisati direktorijum " + dirPath
												+ ", ali je doslo do greske.");
								session.setAttribute("notification",
										"Nije moguce izbrisati direktorijum. Pokusajte ponovo.");
								address = "/WEB-INF/pages/index.jsp";
							}
						}

					} 
					catch (Exception e)
					{
						AkcijaDAO.save("DELETE_FOLDER", korisnikBean.getKorisnik().getUsername(),
								"Korisnik je pokusao izbrisati direktorijum " + dirPath + ", ali je doslo do greske.");
						session.setAttribute("notification", "Doslo je do greske! Pokusaj ponovo..");
						address = "/WEB-INF/pages/index.jsp";
					}
				}
			}
			else 
			{
				System.out.println("Akcija: " + action + "  sa rolom "
						+ Boolean.toString(korisnikBean.getKorisnik().getIdrole() == Role.ADMIN_SISTEMA.getValue()));
				address = "/WEB-INF/pages/404.jsp";
			}
		} catch (Exception e)
		{
			session.invalidate();
			System.out.println("Greska u kontroleru aplikacije Dokumenti.");
			address = "/WEB-INF/pages/redirectToIndex.jsp";
		} 

		RequestDispatcher dispatcher = request.getRequestDispatcher(address);
		dispatcher.forward(request, response);

	}

	private void deleteDirectory(File directory) {

		if (directory.isDirectory()) {
			File[] files = directory.listFiles();

			if (files != null) {
				for (File file : files) {

					deleteDirectory(file);
				}
			}
		}

		if (directory.delete()) {
			System.out.println(directory + " is deleted");
		} else {
			System.out.println("Directory not deleted");
		}
	}
	private static boolean ChechXSS(String input) {
		return Pattern.compile(XSS).matcher(input).find() || (input.contains("..")|| input.length()>200);
	}

}
