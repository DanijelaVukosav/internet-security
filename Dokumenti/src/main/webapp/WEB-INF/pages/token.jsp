<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
	<head>
		<link href="//maxcdn.bootstrapcdn.com/bootstrap/4.0.0/css/bootstrap.min.css" rel="stylesheet" id="bootstrap-css">
		<script src="//maxcdn.bootstrapcdn.com/bootstrap/4.0.0/js/bootstrap.min.js"></script>
		<script src="//cdnjs.cloudflare.com/ajax/libs/jquery/3.2.1/jquery.min.js"></script>
		<title>Login</title>
		<link href="login.css" type="text/css" rel="stylesheet">
	</head>
	<body>
		
		<div class="wrapper fadeInDown">
		  <div id="formContent">
		    <!-- Tabs Titles -->
		
		    <!-- Icon -->
		    <div class="fadeIn first">
		      <h4>Unesite token generisan mobilnom aplikacijom</h4>
		    </div>
		
		    <!-- Login Form -->
		    <form  action="?action=token" method="post">
		      <input type="text" id="token" class="fadeIn second" name="token" placeholder="Token">     
		      <input type="submit" class="fadeIn fourth" value="Prijava">
		    </form>
		
		    <!-- Remind Passowrd -->
		    <div id="notification">
		      <%=session.getAttribute("notification")!=null?session.getAttribute("notification").toString():""%>
		    </div>
		    <div>
		    <h4> <a href="?action=" >Povratak na pocetnu stranicu</a> </h4>
		    </div>
		    <form method="POST" action="?action=oauth2">
		 	<input type="submit" value="Google nalog" name="submit" /><br />
			<br />
		</form>
		
		  </div>
		</div>
	</body>
</html>