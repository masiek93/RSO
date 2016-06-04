package pl.edu.pw.elka.rso;

public class ForwardFileMessage extends FileMessage {
		/**
	 * 
	 */
	private static final long serialVersionUID = 3838502937045588745L;
		private int destinationPort;
		private int destinationFilePort;
		private String destinationAddress;
		public int getDestinationPort() {
			return destinationPort;
		}
		public void setDestinationPort(int destinationPort) {
			this.destinationPort = destinationPort;
		}
		public String getDestinationAddress() {
			return destinationAddress;
		}
		public void setDestinationAddress(String destinationAddress) {
			this.destinationAddress = destinationAddress;
		}
		public int getDestinationFilePort() {
			return destinationFilePort;
		}
		public void setDestinationFilePort(int destinationFilePort) {
			this.destinationFilePort = destinationFilePort;
		}
}
