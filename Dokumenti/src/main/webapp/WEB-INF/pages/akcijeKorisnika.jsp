<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<%@page import="korisnik.dto.Akcija"%>
  
<%@page import="korisnik.service.*,java.util.*"%>  

<jsp:useBean id="akcijeKorisnika" type="java.util.ArrayList<Akcija>" scope="session"/>
<!DOCTYPE html>  
  
<html>  
<head>  
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">  
<link rel="stylesheet" href="tabela.css">
<title>Akcije korisnika</title>  
</head>  
<body>  
  
<a href="?action=index"><h1 id="tree_label">
  Akcije korisnika
	</h1>
</a> 
<span style="float:right;"> <a href="?action=logout"> <button style="background-color:black; color: white;"> Odjava sa sistema</button>   </a>   </span>
 <br/>
<table border="1">  
<tr>
	<th width="15%">Tip akcije</th>
	<th width="15%">Korisnicko ime</th>
	<th width="15%">Vrijeme</th>
	<th width="55%">Opis</th> 
<% for(Akcija akcija:akcijeKorisnika) 
{%>
	<tr>
		<td><% out.print(akcija.getTipAkcije());%></td>
		<td><% out.print(akcija.getUsername());%></td>
		<td><% out.print(akcija.getVrijeme());%></td> 
		<td><% out.print(akcija.getAkcija());%></td> 
	</tr>  
<% }%> 
</table>  
  
</body>  
</html> 