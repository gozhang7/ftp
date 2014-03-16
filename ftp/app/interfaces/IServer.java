package interfaces;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.rmi.Remote;
import java.rmi.RemoteException;

public interface IServer extends Remote {

	public void get(String f) throws IOException, FileNotFoundException;
	
	public long getFileSize(String f) throws IOException, FileNotFoundException;
	
	public int checkTargetExistsOrNot(String f) throws IOException, FileNotFoundException;

	public int put(String[] args) throws IOException, FileNotFoundException;

	public String pwd();

	public int cd(String d) throws IOException;

	public String[] dir();
	
	public String[] dirl();
	
	public int makeDirectory(String name) throws IOException;
	
	public int rm(String[] inputs) throws IOException;
	
	public int mv(String[] inptus) throws IOException;

}
