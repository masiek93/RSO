package DirectoryServerActions;

import java.util.List;

import clientConnection.db_operations.db_operations;

/**
 * Obs�uga listy plik�w dost�pnych dla serwera katalogowego w lokalnej DB
 * @author Mateusz
 *
 */
public class SendFileList {
	
	private db_operations db;
	private List<String> path_list;

	/**
	 *  TODO Docelowo serwer katalogowy ma uzyska� list� plik�w z bazy danych serwera katalogowego
	 */
	public SendFileList()
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
