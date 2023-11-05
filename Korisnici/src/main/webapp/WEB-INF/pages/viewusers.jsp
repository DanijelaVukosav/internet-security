<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1"%>
<%@page import="korisnik.dto.Korisnik"%>
<%@page import="korisnik.beans.KorisnikBean"%>

<%@page import="korisnik.service.*,java.util.*"%>

<jsp:useBean id="listaKorisnika"
	type="korisnik.beans.ListaKorisnikaBean" scope="session" />
<!DOCTYPE html>

<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<link rel="stylesheet" href="tabela.css">
<script type="text/javascript" src="sesije.js"></script>
<title>Korisnici</title>
<style>
input[type=submit], button {
	background-color: darkred;
	color: white;
	border-color: darkred;
}
</style>
</head>
<body onload="setTimeoutForLogout()">
	<script type="text/javascript">
		window.addEventListener('beforeunload', function(e) {
			localStorage.setItem('k_sni_session', "_");
		});
	</script>

	<h1>Lista korisnika</h1>
	<div>
		<a href="?action=add">
			<button style="background-color: darkred; color: white;">
				Dodaj klijenta</button>
		</a>
	</div>
	<div>
		<a href="?action=addAdmin">
			<button style="background-color: darkred; color: white;">
				Dodaj administratora</button>
		</a>
	</div>
	<span style="float: right;"> <a href="?action=logout">
			<button  onclick="logoutSSO()" style="background-color: darkred; color: white;">
				Odjava sa sistema</button>
	</a>
	</span>
	<br />
	<div id="notification">
		<%=session.getAttribute("notification") != null ? session.getAttribute("notification").toString() : ""%>
	</div>
	<br />
	<table border="1" width="90%">
		<tr>
			<th>Korisnicko ime</th>
			<th>Root</th>
			<th>Domen</th>
			<th>Operacije</th>
			<th>Edit</th>
			<th>Delete</th>
		</tr>
		<%
		for (Korisnik korisnik : listaKorisnika.getKorisnici()) {
			if (korisnik.getIdrole() == 2)
				out.print("<tr style=\"background-color:darksalmon;\">");
			else
				out.print("<tr style=\"background-color:blanchedalmond;\">");
		%>
		<td>
			<%
			out.print(korisnik.getUsername());
			%>
		</td>
		<td>
			<%
			out.print(korisnik.getRoot());
			%>
		</td>
		<td>
			<%
			out.print(korisnik.getDomen());
			%>
		</td>
		<td>
			<%
			out.print(korisnik.getOperacije());
			%>
		</td>
		<td><form method="POST" action="?action=edit">
				<input type="text" hidden="hidden" name="username"
					value="<%=korisnik.getUsername()%>" required="required" /><input
					type="submit" value="Azuriraj" />
			</form></td>
		<td><form method="POST" action="?action=delete">
				<input type="text" hidden="hidden" name="username"
					value="<%=korisnik.getUsername()%>" required="required" /><input
					type="submit" value="Izbrisi" />
			</form></td>
		</tr>
		<%
		}
		%>
	</table>

</body>
</html>
