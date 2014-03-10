package controllers;

import play.*;
import play.mvc.*;
import server.Server;
import views.html.*;

public class Application extends Controller {

    public static Result index() {
    	Server server = new Server();
        return ok(index.render(server.pwd()));
    }

}
