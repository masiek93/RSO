package directoryServer;

import java.awt.List;
import java.io.Serializable;

import directoryServer.db_operations.db_operations;

/**
 * 
 * @author Mateusz
 *Docelowo serwer katalogowy ma uzyska� listy plik�w od serwer�w plikowych
 *i scali� je w jedn� list� plik�w
 */

public class SendFileList implements Serializable 
{

	private db_operations db;
	private List path_list;
	
	SendFileList()
	{
		db = new db_operations();
		generateFileList();
	}
	
	public void generateFileList()
	{
		path_list = db.generate_path_list(3);
	}
	
	public List getFileList()
	{
		return path_list;
	}
}
