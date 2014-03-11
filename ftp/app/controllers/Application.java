package controllers;

import models.*;
import play.*;
import play.data.*;
import play.mvc.*;
import server.Server;
import views.html.*;
import static play.data.Form.*;


public class Application extends Controller {
	
	protected static String rootPath = "/home/ftp";

	public static Result login() {
		return ok(login.render(form(Login.class)));
	}
	
	public static Result authenticate() {
	    Form<Login> loginForm = form(Login.class).bindFromRequest();
	    
	    if (loginForm.hasErrors()) {
	        return badRequest(login.render(loginForm));
	    } else {
	    	String currentUserEmail = loginForm.get().email;
	        session().clear();
	        session("email", currentUserEmail);
	        String prefix = rootPath + "/" + User.find.where().eq("email", currentUserEmail).findUnique().name + "-" + currentUserEmail;
	        Server serverInstance = new Server(prefix);
	        return ok(index.render(prefix));
	    }
	}
	
	public static Result guest() {
		return ok(index.render("Welcome!"));
	}
	
	public static Result register() {
		return ok(index.render("Under constructing!"));
	}

	public static class Login {
		public String email;
		public String password;
		
		public String validate() {
		    if (User.authenticate(email, password) == null) {
		      return "Invalid user or password";
		    }
		    return null;
		}
	}

}
