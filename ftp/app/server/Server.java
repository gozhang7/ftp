package server;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.Enumeration;
import java.util.Stack;
import java.util.logging.Logger;

import Interface.IServer;



/**
 * 
 * @author Gong Zhang
 */
public class Server implements IServer
{

	static int counter = 0;

	static final long serialVersionUID = 0L;

	static final int OperationFailed = -8;
	static final int DirAlreadyExists = -7;
	static final int TargetCantBeAccessed = -6;
	static final int NotADirectory = -5;
	static final int NoSuchDirectory = -4;
	static final int AlreadyInRoot = -3;
	static final int FileNotFound = -2;
	static final int FileAlreadyExists = -1;
	static final int OK = 0;

	public static Logger log = Logger
			.getLogger("gzhang.ftp.server");

	/*
	 * ********************************************************************************************
	 * Current working directory.
	 */
	static final int MAX_PATH_LEN = 1024;
	private Stack<String> cwd = new Stack<String>();


	/*
	 * The server can be initialized to only provide subdirectories of a
	 * directory specified at start-up.
	 */
	private final String pathPrefix;

	public Server(String prefix)
	{
		this.pathPrefix = prefix + "/";

		log.info("Client " + counter + " has bound to a server instance.");
		counter++;
	}
	
	public Server()
	{
		pathPrefix = null;
		
		log.info("Client " + counter + " has bound to a server instance.");
		counter++;
	}

	//For security concern, a file`s name can not contain "/"
	private boolean valid(String s)
	{
		// File names should not contain "/".
		return (s.indexOf('/') < 0);
	}

	//A thread for upload file
	private static class GetInputThread implements Runnable
	{
		public void run()
		{

			try
			{
			
			} catch (Exception e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	private static class GetOutputThread implements Runnable
	{

		public void run()
		{

			try
			{

			} catch (Exception e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	public void get(String file) throws IOException, FileNotFoundException
	{
		FileInputStream f = new FileInputStream(path() + file);
		new Thread(new GetInputThread()).start();
	}

	@Override
	public long GetFileSize(String f) throws IOException, FileNotFoundException
	{
		long fileSize = -1;

		if (!valid(f))
		{
			return OperationFailed;
		} else
		{
			try
			{
				FileInputStream in = new FileInputStream(path() + f);
				fileSize = in.getChannel().size();
				in.close();
			} catch (FileNotFoundException e)
			{
				return FileNotFound;
			}
		}

		return fileSize;
	}

	public synchronized int put(String[] args) throws IOException, FileNotFoundException
	{

		return OK;
	}

	public String[] dir()
	{
		// List the contents of the current directory.
		String[] res = new File(path()).list();
		return res;
	}

	public int cd(String dir) throws IOException, RemoteException
	{
		// Change current working directory (".." is parent directory)
		if (!valid(dir))
		{
			return TargetCantBeAccessed;
		} else
		{
			if ("..".equals(dir))
			{
				if (cwd.size() > 0)
					cwd.pop();
				else
					return AlreadyInRoot;
			} else if (".".equals(dir))
			{
				;
			} else
			{
				File f = new File(path() + dir);
				if (!f.exists())
					return NoSuchDirectory;
				else if (!f.isDirectory())
					return NotADirectory;
				else
					cwd.push(dir);
			}
		}

		return OK;
	}

	public String pwd()
	{
		// List the current working directory.
		String p = "/";
		for (Enumeration<String> e = cwd.elements(); e.hasMoreElements();)
		{
			p = p + e.nextElement() + "/";
		}
		
		return p;
	}

	private String path()
	{
		return pathPrefix + pwd();
	}

	static void msg(String m)
	{
		System.out.print(m);
	}

	static void msgln(String m)
	{
		System.out.println(m);
	}

	static void err(Exception e)
	{
		System.err.println("Error : " + e);
		e.printStackTrace();
	}

	@Override
	public int CheckFileStatus(String f) throws IOException, FileNotFoundException
	{
		File targetFile = new File(path() + f);

		if (targetFile.exists())
			return FileAlreadyExists;

		return FileNotFound;
	}

	@Override
	public int MakeDirectory(String name) throws IOException
	{
		if (valid(name))
		{
			File file = new File(path() + name);

			if (file.exists())
				return DirAlreadyExists;
			else
			{
				if (!file.mkdir())
					return OperationFailed;
			}

			return OK;
		} else
			return OperationFailed;
	}

	@Override
	public int Rm(String[] inputs) throws IOException, RemoteException
	{

			if (valid(inputs[1]))
			{
				File file = new File(path() + inputs[1]);

				int res = CheckFileStatus(inputs[1]);
				if (res == FileNotFound)
					return FileNotFound;
				else if (res == FileAlreadyExists)
					if (file.isDirectory())
						return NotADirectory;
					else if (!file.delete())
						return OperationFailed;
			} else
				return OperationFailed;

		return OK;
	}

	@Override
	public int Mv(String[] inputs) throws IOException, RemoteException
	{
			String srcFile = inputs[1];
			String tagFile = inputs[2];
			
			if(CheckFileStatus(srcFile) == FileNotFound)
			{
				return FileNotFound;
			}
			else if(CheckFileStatus(tagFile) == FileAlreadyExists)
			{
				return FileAlreadyExists;
			}
			else 
			{
				File src = new File(path() + srcFile);
				
				if(src.renameTo(new File(path() + tagFile)))
					return OK;
				else
					return OperationFailed;
			}
	}

	@Override
	public String[] dirl()
	{
		String[] fnames = new File(path()).list();
		
		String[] res = new String[fnames.length];
		
		for(int i = 0; i < fnames.length; i++)
		{
			File file = new File(path() + fnames[i]);
			res[i] = fnames[i] + "," + file.length() + "," + file.lastModified();
		}
		
		return res;
	}
}
