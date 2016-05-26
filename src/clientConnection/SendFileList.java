package clientConnection;

import java.io.Serializable;
import java.util.List;

import clientConnection.db_operations.db_operations;

/**
 * 
 * @author Mateusz
 * Send list of files saved at server's database
 */

public class SendFileList implements Serializable 
{

	/**
	 * Compiler generated UID
	 */
	private static final long serialVersionUID = 6791632052881941490L;
	private db_operations db;
	private List<String> path_list;
	
	/**
	 *  TODO Docelowo serwer katalogowy ma uzyskaæ listê plików z bazy danych serwera katalogowego
	 */
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
		
		ret = path_list.toString();
		
		return ret;
	}
}
