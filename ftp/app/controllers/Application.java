package controllers;

import java.io.IOException;

import play.Logger;

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

	public static Result logout() {
		return ok(login.render(form(Login.class)));
	}

	public static Result authenticate() {
		Form<Login> loginForm = form(Login.class).bindFromRequest();
		if (loginForm.hasErrors()) {
			return badRequest(login.render(loginForm));
		} else {
			Server serverInstance = null;
			String currentUserEmail = loginForm.get().email;
			session().clear();
			session("email", currentUserEmail);
			String prefix = rootPath
					+ "/"
					+ User.find.where().eq("email", currentUserEmail)
							.findUnique().name + "-" + currentUserEmail;
			try {
				serverInstance = new Server(currentUserEmail, prefix);
<<<<<<< HEAD
				serverInstance.save();
			} catch (Exception e) {
				String errorMessage = "Opps, something is broken!\n"
						+ "ERROR message: " + e.getMessage() + "\n";
				return internalServerError(errorMessage);
			}
			String pwd = serverInstance.pwd();
			String[] dirDetialList = serverInstance.dirl();
			return ok(index.render(pwd, dirDetialList));
=======
			} catch (Exception e) {
				String errorMessage = "Opps, something is broken!\n"
						+ "ERROR message: " + e.getMessage() + "\n";
				return internalServerError(errorMessage);
			}
			String pwd = serverInstance.pwd();
			String[] fileLists;
			try {
				fileLists = serverInstance.dirl();
			} catch (IOException e) {
				String errorMessage = "Opps, something is broken!\n"
						+ "ERROR message: " + e.getMessage() + "\n";
				return internalServerError(errorMessage);
			}
			return ok(index.render(pwd, fileLists));
>>>>>>> 891545dc451b84c3a79a9bf9cee4f106eb575c0c
		}
	}

	public static Result guest() {
		return ok();
	}

	public static Result register() {
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
