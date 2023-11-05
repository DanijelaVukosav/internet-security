<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1"%>

<%@page import="dokumenti.beans.*,java.util.*,java.io.*,controller.Role"%>

<jsp:useBean id="stabloFajlova" type="dokumenti.beans.MyDirectory"
	scope="session" />

<jsp:useBean id="korisnik" type="korisnik.beans.KorisnikBean"
	scope="session" />


<!DOCTYPE html>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<link rel="stylesheet" href="tree.css">
<link rel="stylesheet" href="index.css">
<script
	src="https://ajax.googleapis.com/ajax/libs/jquery/3.6.0/jquery.min.js"></script>
<script type="text/javascript" src="tree.js"></script>
<script type="text/javascript" src="treeitem.js"></script>
<script type="text/javascript" src="treeitemclick.js"></script>
<script type="text/javascript" src="sesije.js"></script>
<script type="text/javascript">
	function getFileNameCreate() {
		var fileName = document.getElementById('fileUpload').files[0].name;
		document.getElementById('fileName').value = fileName;

	}
	function getFileNameUpdate() {
		var fileName = document.getElementById('fileUpdate').files[0].name;
		document.getElementById('newFileUpdate').value = fileName;

	}
</script>
<title>Dokumenti</title>
</head>
<body onload="setTimeoutForLogout()">
	<script type="text/javascript">
		window.addEventListener('beforeunload', function(e) {
			localStorage.setItem('d_sni_session', "_");
		});
	</script>
	<a href="?action=index"><h1 id="tree_label">
			<%=stabloFajlova.getDirectoryName()%>
		</h1> </a>

	<div>
		<a href="?action=akcijeKorisnika">
			<button style="background-color: black; color: white;">
				Lista akcija</button>
		</a>
	</div>
	<span style="float: right;"> <a href="?action=logout">
			<button onclick="logoutSSO()" style="background-color: black; color: white;">
				Odjava</button>
	</a>
	</span>
	<div class="row">
		<div class="column left">
			<ul role="tree" aria-labelledby="tree_label">
				<%
				out.println(
						FileTreeBean.napraviHTMLKodStabla(stabloFajlova, new StringBuilder(""), korisnik.getKorisnik().getOperacije()));
				%>


			</ul>


			<p id="notification">
				<b><%=session.getAttribute("notification") != null ? session.getAttribute("notification").toString() : ""%></b>
			</p>
		</div>
		<div class="column right">

			<p style="font-size: 18px;">
				<br />
				<b><label> Selektovana datoteka: &nbsp;</label><label
					id="selectedFile"></label></b> <br />
				<br />
				<b><label> Selektovani direktorijum: &nbsp;</label><label
					id="selectedFolder"></label></b>
			</p>
			<%
			if (korisnik.getKorisnik().getOperacije().contains("R")) {
			%>
			<label style="font-size: 12px;"> -Preuzmite selektovanu
				datoteku-</label><br />
			<form method="POST" action="Dokumenti?action=download">
				<input id="download" name="id" hidden="hidden" type="text" value=""
					readonly=""> <input type="submit" value="Preuzmi" />
			</form>
			<%
			}
			%>
			<%
			if (korisnik.getKorisnik().getOperacije().contains("D")) {
			%>
			<label style="font-size: 12px;"> -Izbrisite selektovanu
				datoteku-</label><br />
			<form method="POST" action="Dokumenti?action=delete">
				<input id="delete" name="id" hidden="hidden" type="text" value=""
					readonly=""> <input type="submit" value="Izbrisi" />
			</form>
			<%
			}
			%>
			<%
			if (korisnik.getKorisnik().getIdrole() == Role.ADMIN_SISTEMA.getValue()
					|| korisnik.getKorisnik().getIdrole() == Role.ADMIN_DOKUMENTA.getValue()) {
			%>
			<label style="font-size: 12px;"> -Premjesti selektovanu
				datoteku u selektovani direktorijum-</label><br />
			<form method="POST" action="Dokumenti?action=move">
				<input id="moveFile" name="fileName" hidden="hidden" type="text"
					value="" readonly=""> <input id="moveFolder"
					name="folderName" hidden="hidden" type="text" value="" readonly="">
				<input type="submit" value="Premjesti" />
			</form>
			<%
			}
			%>
			<%
			if (korisnik.getKorisnik().getOperacije().contains("C")) {
			%>
			<label style="font-size: 12px;"> -Upload odarane datoteke u
				selektovani direktorijum-</label><br />
			<form enctype="multipart/form-data" method="POST"
				action="Dokumenti?action=upload">
				<input id="folderPath" hidden="hidden" name="folderPath" type="text"
					value="" readonly="" size="30"> <input type="submit"
					value="Dodaj novu datoteku" /> <label>Datoteka: </label><input
					id="fileUpload" name="file" required="required" type="file"
					onchange="getFileNameCreate()"> <input id="fileName"
					name="fileName" type="text" value="" readonly="" hidden="hidden">
			</form>
			<%
			}
			%>
			<%
			if (korisnik.getKorisnik().getOperacije().contains("U")) {
			%>
			<label style="font-size: 12px;"> -Zamijeniti selektovanu
				datoteku sa izabranom datotekom-</label><br />
			<form enctype="multipart/form-data" method="POST"
				action="Dokumenti?action=update">
				<input type="submit" value="Izmijeni datoteku" /> <input
					id="newFileUpdate" name="newFileUpdate" type="text" value=""
					readonly="" hidden="hidden"> <input id="oldFileUpdate"
					name="oldFileUpdate" type="text" value="" readonly=""
					hidden="hidden"> <label>Datoteka: </label><input
					id="fileUpdate" name="fileUpdate" required="required" type="file"
					onchange="getFileNameUpdate()">
			</form>
			<%
			}
			%>
			<p>
				<br />
				<b><label>______________________________________________________________________</label></b>
			</p>
			<%
			if (korisnik.getKorisnik().getIdrole() == Role.ADMIN_SISTEMA.getValue()
					|| korisnik.getKorisnik().getIdrole() == Role.ADMIN_DOKUMENTA.getValue()) {
			%>
			<label style="font-size: 12px;"> -Kreirati direktorijum sa
				unesenim imenom u selektovani direktorijum-</label><br />
			<form method="POST" action="Dokumenti?action=createFolder">
				<input id="createFolderPath" hidden="hidden" name="createFolderPath"
					type="text" value="" readonly="" size="30"> <input
					type="submit" value="Kreiraj folder" /> <input
					id="createFolderName" name="createFolderName" type="text" value=""
					size="30">
			</form>
			<label style="font-size: 12px;"> -Izbrisi selektovani
				direktorijum-</label><br />
			<form method="POST" action="Dokumenti?action=deleteFolder">
				<input id="deleteFolder" hidden="hidden" name="deleteFolder"
					type="text" value="" readonly="" size="30"> <input
					type="submit" value="Izbrisi direktorijum" />
			</form>
			<%
			}
			%>
		</div>
	</div>

</body>
</html>
