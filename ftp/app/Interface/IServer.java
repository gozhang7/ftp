package Interface;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.rmi.Remote;
import java.rmi.RemoteException;

public interface IServer extends Remote {

	public void get(String f) throws IOException, FileNotFoundException;
	
	public long GetFileSize(String f) throws IOException, FileNotFoundException;
	
	public int CheckFileStatus(String f) throws IOException, FileNotFoundException;

	public int put(String[] args) throws IOException, FileNotFoundException;

	public String pwd();

	public int cd(String d) throws IOException;

	public String[] dir();
	
	public String[] dirl();
	
	public int MakeDirectory(String name) throws IOException;
	
	public int Rm(String[] inputs) throws IOException;
	
	public int Mv(String[] inptus) throws IOException;

}
