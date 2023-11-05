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
 
<h1>Kreiranje naloga administratora </h1>  
<form action="?action=submitAddAdmin" method="POST">
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
			<td style="float:right;" colspan="2"><input type="submit" value="Dodaj korisnika"/></td>
		</tr>  
	</table>  
</form>  
  
</body>  
</html>  