package clientConnection;
/**
 * Test cz�ci serwera katalogowego, kt�ra ��czy si� z klientem
 * @author Mateusz
 *
 */

public class testClientConnection {

	public static void main(String[] args) {
		int portNumber = 60010;
		if (args.length > 0) {
			if (args[0] != null) {
				portNumber = Integer.parseInt(args[0]);
			}
		}

		WaitForClient listener = new WaitForClient(portNumber);
		listener.start();
	}

}
