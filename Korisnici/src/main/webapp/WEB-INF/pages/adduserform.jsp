<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<%@page import="korisnik.beans.KorisnikBean,korisnik.service.KorisnikDAO"%> 
<!DOCTYPE html>  
<html>  
<head>  
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">  
<title>Korisnici</title>  
<link rel="stylesheet" href="tabela.css">
</head>  
<body>  
 
<h1>Kreiranje naloga klijenta </h1>  
<form action="?action=submitadd" method="POST">
	<table>  
		<tr>
			<td>Korisnicko ime:</td>
			<td><input type="text" name="username" required="required"/></td>
		</tr>  
		<tr>
			<td>Lozinka:</td>
			<td><input type="password" name="password" required="required"/></td>
		</tr>
		<tr>
			<td>Root:</td>
			<td><input type=text name="root"  required="required"/></td>
		</tr>
		<tr>
			<td>Domen:</td>
			<td><input type="text" name="domen" required="required" pattern="^((25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\.){3}(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)$"/></td>
		</tr> 
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
			<td style="float:right;" colspan="2"><input type="submit" value="Dodaj korisnika"/></td>
		</tr>  
	</table>  
</form>  
  
</body>  
</html>  