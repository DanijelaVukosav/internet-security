package controller;
import java.util.Properties;  
import javax.mail.*;  
import javax.mail.internet.*;  

public class AktivnostiKorisnika {
	String host="mail.javatpoint.com";  
	  final String user="mm2023012@gmail.com";//change accordingly  
	  final String password="marija2020";//change accordingly  
	    
	  String to="danijela99vukosav@gmail.com";//change accordingly  
	  public void sendMail(String username,String akcija, String folderName)
	  {
		   //Get the session object  
		  /* Properties props = new Properties();  
		   props.put("mail.smtp.host", "smtp.gmail.com");    
	          props.put("mail.smtp.socketFactory.port", "465");    
	          props.put("mail.smtp.socketFactory.class",    
	                    "javax.net.ssl.SSLSocketFactory");    
	          props.put("mail.smtp.auth", "true");    
	          props.put("mail.smtp.port", "465");  
	          props.put("mail.smtp.ssl.trust", "smtp.gmail.com");
	          props.put("mail.imap.ssl.enable", "true");
		     
		   Session session = Session.getDefaultInstance(props,  
		    new javax.mail.Authenticator() 
		   {  
		      protected PasswordAuthentication getPasswordAuthentication() 
		      {  
		    return new PasswordAuthentication(user,password);  
		      }  
		    });  
		  */
		  // or IP address
	        final String host = "localhost";
	 
	        // Get system properties
	        Properties props = new Properties();            
	         
	        // enable authentication
	        props.put("mail.smtp.auth", "true");              
	         
	        // enable STARTTLS
	        props.put("mail.smtp.starttls.enable", "true");  
	        props.put("mail.smtp.ssl.trust", "smtp.gmail.com");
	         
	        // Setup mail server
	        props.put("mail.smtp.host", "smtp.gmail.com");    
	         
	        // TLS Port
	        props.put("mail.smtp.port", "587");               
	 
	        // creating Session instance referenced to
	        // Authenticator object to pass in
	        // Session.getInstance argument
	        Session session = Session.getInstance(props,
	          new javax.mail.Authenticator() {
	            
	            //override the getPasswordAuthentication method
	            protected PasswordAuthentication
	                           getPasswordAuthentication() {
	                                        
	                return new PasswordAuthentication(user,
	                                                 password);
	            }
	          });
		   //Compose the message  
		    try {  
		     MimeMessage message = new MimeMessage(session);  
		     message.setFrom(new InternetAddress(user));  
		     message.addRecipient(Message.RecipientType.TO,new InternetAddress(to));  
		     message.setSubject("Aktivnost korisnika");  
		     message.setText("Korisnik "+ username+ " je "+akcija+" folder: "+folderName+".");  
		       
		    //send the message  
		     Transport.send(message);  
		  
		     System.out.println("message sent successfully...");  
		   
		     } catch (MessagingException e) {e.printStackTrace();}  
	  
	 
	}  

}
