package controllers;

import models.User;
import play.*;
import play.data.*;
import play.mvc.*;
import server.Server;
import views.html.*;
import static play.data.Form.*;


public class Application extends Controller {

	public static Result index() {
		Server server = new Server();
		return ok(index.render(server.pwd()));
	}

	public static Result login() {
		return ok(login.render(form(Login.class)));
	}
	
	public static Result authenticate() {
	    Form<Login> loginForm = form(Login.class).bindFromRequest();
	    return ok();
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
