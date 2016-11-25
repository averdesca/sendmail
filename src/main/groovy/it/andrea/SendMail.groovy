package it.andrea

import java.io.File;
import java.util.*
import javax.mail.*
import javax.mail.internet.*
import javax.activation.*

class SendMail {

	static main(args) {
		
		String pathFileUnico = ""
		String configPath = ""
		
		def cli = new CliBuilder(usage: 'sendmail -[hfc] [path]')
		
		// Create the list of options.
		cli.with {
			h longOpt: 'help', 'Show usage information'
			f longOpt: 'input-file',  args: 1, argName: 'inputF',  'Path of input file'
			c longOpt: 'config-file',  args: 1, argName: 'configF',  'Path of config file'
			}
		
		def options = cli.parse(args)
		if (!options) {
			return
		}
		// Show usage text when -h or --help option is used.
		if (options.h) {
			cli.usage()
			// Will output:
			// usage: showdate.groovy -[chflms] [date] [prefix]
			//  -c,--format-custom <format>   Format date with custom format defined by "format"
			//  -f,--format-full              Use DateFormat#FULL format
			//  -h,--help                     Show usage information
			//  -l,--format-long              Use DateFormat#LONG format
			//  -m,--format-medium            Use DateFormat#MEDIUM format
			//  -s,--format-short             Use DateFormat#SHORT format
			return
		}
		
		
		if (options."input-file") {  // Using short option.
			pathFileUnico = options && options.f? options.f.toString():"/tmp/input.properties"
		}else{
		pathFileUnico = "/tmp/input.properties"
		}
	
	
	if (options.'config-file') {  // Using long option.
			configPath = options &&  options.c ? options.c.toString():"/tmp/config.properties"
		}else{
		configPath = "/tmp/config.properties"
		}
		
			 
			
			def fileUnico = new File(pathFileUnico);


			def config = new ConfigSlurper().parse(new File(configPath).toURI().toURL())
			
			def input = new ConfigSlurper().parse(new File(pathFileUnico).toURI().toURL())

			List list = []

			Properties props = new Properties();
			
			props.put("mail.smtp.auth", "false");
			//props.put("mail.smtp.starttls.enable", "true");
			props.put("mail.smtp.host", config.host);
			props.put("mail.smtp.user", config.username);
			props.put("mail.smtp.password", config.password);
			props.put("mail.smtp.port", config.port);
			
			
	  //	  Properties props = new Properties();
	  //	  props.put("mail.smtp.host", "smtp.gmail.com");
	  //	  props.put("mail.smtp.user", username);
	  //	  props.put("mail.smtp.password", password);
	  //	  props.put("mail.smtp.socketFactory.port", "465");
	  //	  props.put("mail.smtp.socketFactory.class",
	  //			  "javax.net.ssl.SSLSocketFactory");
	  //	  props.put("mail.smtp.auth", "true");
	  //	  props.put("mail.smtp.port", "465");
			
				  
			def mailProp=[:]
				
	 
				
				mailProp.from = input.from
				mailProp.to=input.to.split(',').collect{it as String}
						mailProp.replyTo=input.replyto
						mailProp.subject=input.subject
						mailProp.message=input.message
				
			
				  
	  
		  sendMail(props, mailProp,config.username, config.password)
	   
			
			
			
	}
	
	static void sendMail(props , mailProp, username , password) {
		// Get system properties
	//    Properties properties = System.getProperties()
				Properties properties = props
	
		// Setup mail server
		//properties.setProperty("mail.smtp.host", mailProp.host)
	
		// Get the default Session object.
	//   Session session = Session.getInstance(props,
	//		  new javax.mail.Authenticator() {
	//			protected PasswordAuthentication getPasswordAuthentication() {
	//				return new PasswordAuthentication(username, password);
	//			}
	//		  });
	
				Session session = Session.getDefaultInstance(props);
		try {
	  // Create a default MimeMessage object.
	  new MimeMessage(session).with { message ->
		// From, Subject and Content
		from = new InternetAddress( mailProp.from )
		subject = mailProp.subject
		setContent mailProp.message , 'text/html'
		setReplyTo(new InternetAddress(mailProp.replyTo))
		// Add recipients
		mailProp.to.each({address -> message.addRecipient( Message.RecipientType.BCC, new InternetAddress( address ) )})
	
		// Send the message
		Transport.send( message)
	
		println "Sent successfully"
	  }
	}
	catch( MessagingException mex ) {
		mex.printStackTrace()
	}
	  }

}
