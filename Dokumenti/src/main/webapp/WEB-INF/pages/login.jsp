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
		<script type="text/javascript" src="sesije.js"></script>
	</head>
	<body>
		
		<div class="wrapper fadeInDown">
		  <div id="formContent">
		    <!-- Tabs Titles -->
		
		    <!-- Icon -->
		    <div class="fadeIn first">
		      <h4>Prijava na sistem</h4>
		    </div>
		
		    <!-- Login Form -->
		    <form  action="?action=login" method="post">
		      <input type="text" id="username" class="fadeIn second" name="username" placeholder="Korisnicko ime">
		      <input type="password" id="password" class="fadeIn second" name="password" placeholder="Lozinka">      
		      <input type="submit" class="fadeIn fourth" value="Prijava">
		    </form>
		
		    <!-- Remind Passowrd -->
		    <div id="notification">
		      <%=session.getAttribute("notification")!=null?session.getAttribute("notification").toString():""%>
		    </div>
		
		  </div>
		</div>
	</body>
</html>