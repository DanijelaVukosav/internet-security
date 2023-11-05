function posaljiSesiju()
{
	const sessionID = localStorage.getItem('k_sni_session');
	console.log("Sess ID: ",sessionID);
	var xhttp = new XMLHttpRequest();
 	xhttp.open("GET", "https://localhost:8443/Dokumenti?action=SSO&session="+sessionID, {session:sessionID});
	xhttp.send();
	if(xhttp.status==404)
	{
		logout1();
	}
}
var timeout;

function setTimeoutForLogout()
{
	var session_id = /SESS\w*ID=([^;]+)/i.test(document.cookie) ? RegExp.$1 : false;
	localStorage.setItem('d_sni_session',session_id);
	console.log("Postavi tajmer sesije ", session_id);
	timeout = setTimeout(callLogout, 1000 * 60 *28);
}

function callLogout() {
	console.log("Pozove callLogout");
	var xhttp = new XMLHttpRequest();
 	xhttp.open("GET", "https://localhost:8443/Dokumenti?action=logout", false);
	xhttp.send();
	localStorage.setItem('d_sni_session',"_");
}
function logoutSSO()
{
	const sessionID = localStorage.getItem('k_sni_session');
	var xhttp = new XMLHttpRequest();
 	xhttp.open("GET", "https://localhost:8443/Korisnici?action=sessionLogout&sessionID="+sessionID, false);
	xhttp.send();
	localStorage.setItem('k_sni_session',"_");
}