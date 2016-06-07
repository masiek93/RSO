package Requests;
import java.io.Serializable;
import java.util.List;

/**
 * Wylistowanie plików w bazie - komunikacja z klientem
 * @author Mateusz
 *
 */
public class FileListRequest implements Serializable{

	/**
	 * Generated
	 */
	private static final long serialVersionUID = 3935226890315892551L;
	
	private static final String SEND_FILE_LIST = "send_file_list";
	
	private List<String> pathList;
	
	public String getMessage()
	{
		return SEND_FILE_LIST;
	}
	
	public void setPathList(List<String> _pathList)
	{
		pathList = _pathList;
	}
	
	public String getPathList()
	{
		String wholeList = null;
		
		int size = pathList.size();
		int i = 0;
		while(i < size)
		{
			wholeList += pathList.get(i);
			i++;
		}
		
		return wholeList;
	}
	
	public List<String> getFileList()
	{
		return pathList;
	}
}
