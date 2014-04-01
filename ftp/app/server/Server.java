package server;

import interfaces.IServer;

import java.io.File;

import java.io.FileInputStream;

import java.io.FileNotFoundException;

import java.io.IOException;

import java.rmi.RemoteException;

import java.util.Enumeration;

import java.util.Stack;

import javax.persistence.Entity;
import javax.persistence.Id;

import play.Logger;
import play.db.ebean.*;

/**
 * 
 * 
 * 
 * @author Gong Zhang
 */

@Entity
public class Server extends Model implements IServer

{
	@Id
	public String boundingUserEmail;
	private static int counter = 0;
	static final long serialVersionUID = 0L;
	static final int OperationFailed = -8;
	static final int TargetCantBeAccessed = -6;
	static final int NotADirectory = -5;
	static final int AlreadyInRoot = -3;
	static final int TargetNotFound = -2;
	static final int TargetAlreadyExists = -1;
	static final int OK = 0;

	static final int MAX_PATH_LEN = 1024;
	private Stack<String> currentWorkingDir = new Stack<String>();
	private final String pathPrefix;

	public Server(String userEmail, String prefix) throws IOException, Exception
	{
		this.boundingUserEmail = userEmail;
		this.pathPrefix = prefix + "/";
		if (validPrefix() == TargetNotFound) {
			if (createUserDirectory(pathPrefix) != OK) {
				Logger.error("Client " + getUserNameFromPrefix()
						+ ": Failed to create user directory!");
				throw new Exception(
						"Failed to create user directory! The path is "
								+ prefix);
			}
		}
		Logger.info("Client " + getUserNameFromPrefix()
				+ " has bound to a server instance.");
		counter++;
	}
	
	// A finder of server instance
    public static Finder<String,Server> find = new Finder<String,Server>(
            String.class, Server.class
        );

	public String getUserNameFromPrefix() {

		String res;

		String[] arr = this.pathPrefix.split("/");

		res = arr[arr.length - 1];

		return res;

	}

	public int validPrefix() throws FileNotFoundException, IOException {

		return checkTargetExistsOrNot(pathPrefix);

	}

	// For security concern, a file`s name can not contain "/"

	private boolean valid(String s)

	{

		// File names should not contain "/".

		return (s.indexOf('/') < 0);

	}

	// A thread for upload file

	private class GetInputThread implements Runnable

	{

		public void run()

		{

			try

			{

			} catch (Exception e)

			{

				Logger.error(e.getMessage());

			}

		}

	}

	private class GetOutputThread implements Runnable

	{

		public void run()

		{

			try

			{

			} catch (Exception e)

			{

				Logger.error(e.getMessage());

			}

		}

	}

	public void get(String file) throws IOException, FileNotFoundException

	{

		FileInputStream f = new FileInputStream(path() + file);

		new Thread(new GetInputThread()).start();

	}

	@Override
	public long getFileSize(String f) throws IOException, FileNotFoundException

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

				return TargetNotFound;

			}

		}

		return fileSize;

	}

	public synchronized int put(String[] args) throws IOException,
			FileNotFoundException

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

				if (currentWorkingDir.size() > 0)

					currentWorkingDir.pop();

				else

					return AlreadyInRoot;

			} else if (".".equals(dir))

			{

				;

			} else

			{

				File f = new File(path() + dir);

				if (!f.exists())

					return TargetNotFound;

				else if (!f.isDirectory())

					return NotADirectory;

				else

					currentWorkingDir.push(dir);

			}

		}

		return OK;

	}

	public String pwd()

	{

		// List the current working directory.

		String p = "/";

		for (Enumeration<String> e = currentWorkingDir.elements(); e
				.hasMoreElements();)

		{

			p = p + e.nextElement() + "/";

		}

		return p;

	}

	private String path()

	{

		return pathPrefix + pwd();

	}

	@Override
	public int checkTargetExistsOrNot(String f) throws IOException,
			FileNotFoundException

	{

		File targetFile = new File(path() + f);

		if (targetFile.exists())

			return TargetAlreadyExists;

		return TargetNotFound;

	}

	@Override
	public int makeDirectory(String name) throws IOException

	{

		if (valid(name))

		{

			File file = new File(path() + name);

			if (file.exists())

				return TargetAlreadyExists;

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
	public int createUserDirectory(String name) throws IOException

	{

		File file = new File(name);

		if (file.exists())

			return OK;

		else

		{

			if (!file.mkdir())

				return OperationFailed;

		}

		return OK;

	}

	@Override
	public int rm(String[] inputs) throws IOException, RemoteException

	{

		if (valid(inputs[1]))

		{

			File file = new File(path() + inputs[1]);

			int res = checkTargetExistsOrNot(inputs[1]);

			if (res == TargetNotFound)

				return TargetNotFound;

			else if (res == TargetAlreadyExists)

				if (file.isDirectory())

					return NotADirectory;

				else if (!file.delete())

					return OperationFailed;

		} else

			return OperationFailed;

		return OK;

	}

	@Override
	public int mv(String[] inputs) throws IOException, RemoteException

	{

		String srcFile = inputs[1];

		String tagFile = inputs[2];

		if (checkTargetExistsOrNot(srcFile) == TargetNotFound)

		{

			return TargetNotFound;

		}

		else if (checkTargetExistsOrNot(tagFile) == TargetAlreadyExists)

		{

			return TargetAlreadyExists;

		}

		else

		{

			File src = new File(path() + srcFile);

			if (src.renameTo(new File(path() + tagFile)))

				return OK;

			else

				return OperationFailed;

		}

	}

	@Override
	public String[] dirl() throws IOException

	{

		String[] fnames = new File(path()).list();

		String[] res = new String[fnames.length];

		for (int i = 0; i < fnames.length; i++)

		{

			File file = new File(path() + fnames[i]);

			res[i] = fnames[i] + "," + file.length() + ","
					+ file.lastModified() + "," + java.nio.file.Files.getOwner(file.toPath());

		}

		return res;

	}

}
