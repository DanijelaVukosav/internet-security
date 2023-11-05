<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<%@page import="korisnik.beans.KorisnikBean,korisnik.dto.Korisnik,controller.Role"%> 

<jsp:useBean id="editKorisnik"
	type="korisnik.dto.Korisnik" scope="request" />
<!DOCTYPE html>  
<html>  
<head>  
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">  
<title>Korisnici</title>  
<link rel="stylesheet" href="tabela.css">
</head>  
<body>  
 
<h1>Azuriraj klijenta </h1>  
<form action="?action=submitedit" method="POST">
	<input type="text" name="usernameOld" value="<%=editKorisnik.getUsername() %>" required="required" hidden="hidden"/>
	<table>  
		<tr>
			<td>Korisnicko ime:</td>
			<td><input type="text" name="username" value="<%=editKorisnik.getUsername() %>" required="required"/></td>
		</tr>  
		<tr>
			<td>Lozinka:</td>
			<td><input type="password" name="password"/></td>
		</tr>
		<tr>
			<td>Root:</td>
			<td><input type=text name="root" value="<%=editKorisnik.getRoot() %>"   required="required"/></td>
		</tr>
		<%if(editKorisnik.getIdrole()==Role.KLIJENT.getValue()){ %>
		<tr>
			<td>Domen:</td>
			<td><input type="text" name="domen" required="required"  value="<%=editKorisnik.getDomen() %>" /></td>
		</tr> 
		<%} %>
		<tr>
			<td>Operacije:</td>
			<td>
				  <input type="checkbox" id="create" name="create" value="C">
 				  <label for="create"> Kreiranje</label><br>
				  <input type="checkbox" id="update" name="update" value="U">
				  <label for="update"> Azuriranje</label><br>
				  <input type="checkbox" id="retrive" name="retrive" value="R">
				  <label for="retrive"> Pregled</label><br>
				  <input type="checkbox" id="delete" name="delete" value="D">
				  <label for="delete"> Brisanje</label><br>
		    </td>
		</tr>  
		<tr>
			<td style="float:right;" colspan="2"><input type="submit" value="Izmijeni korisnika"/></td>
		</tr>  
	</table>  
</form>  
  
</body>  
</html>  