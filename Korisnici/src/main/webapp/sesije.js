function posaljiSesiju()
{
	const sessionID = localStorage.getItem('d_sni_session');
	console.log("Sess ID: ",sessionID);
	var xhttp = new XMLHttpRequest();
 	xhttp.open("GET", "https://localhost:8443/Korisnici/?action=SSO&session="+sessionID, {session:sessionID});
	xhttp.send();
    console.log(xhttp.responseText);
	if(xhttp.status==404)
	{
		logout1();
	}
	
}
var timeout;

function setTimeoutForLogout()
{
	var session_id = /SESS\w*ID=([^;]+)/i.test(document.cookie) ? RegExp.$1 : false;
	localStorage.setItem('k_sni_session',session_id);
	console.log("Postavi tajmer sesije ", session_id);
	timeout = setTimeout(logout1, 1000 * 60 *2);
}

function logout1() {
	console.log("Pozove logout");
	var xhttp = new XMLHttpRequest();
 	xhttp.open("GET", "https://localhost:8443/Korisnici/?action=logout", false);
	xhttp.send();
    console.log(xhttp.responseText);
	localStorage.setItem('k_sni_session',"_");
}

function stopTimeout() {
	logout1();
  clearTimeout(timeout);
}
function logoutSSO()
{
	const sessionID = localStorage.getItem('d_sni_session');
	var xhttp = new XMLHttpRequest();
 	xhttp.open("GET", "https://localhost:8443/Dokumenti?action=sessionLogout&sessionID="+sessionID, false);
	xhttp.send();
	localStorage.setItem('d_sni_session',"_");
}