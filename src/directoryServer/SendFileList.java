package directoryServer;

import java.io.Serializable;
import java.util.List;

import directoryServer.db_operations.db_operations;

/**
 * 
 * @author Mateusz
 *Docelowo serwer katalogowy ma uzyska� listy plik�w od serwer�w plikowych
 *i scali� je w jedn� list� plik�w
 */

public class SendFileList implements Serializable 
{

	/**
	 * Compiler generated UID
	 */
	private static final long serialVersionUID = 6791632052881941490L;
	private db_operations db;
	private List<String> path_list;
	
	SendFileList()
	{
		db = new db_operations();
		generateFileList();
	}
	
	public void generateFileList()
	{
		path_list = db.generate_path_list(3);
	}
	
	public List<String> getFileList()
	{
		return path_list;
	}
	
	public String toString()
	{
		String ret = null;
		StringBuilder sb = new StringBuilder(); //co to robi?
		
		ret = path_list.toString();
		
		return ret;
	}
}
