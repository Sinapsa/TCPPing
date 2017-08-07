
public class ReceivedMsgData {
	
	private int seqNum;
	private int secondReceived;
	private int A_B;
	private int B_A;
	private int A_B_A;  
	
	
	public ReceivedMsgData(int seqNum, int secondReceived, int A_B, int B_A, int A_B_A) {
		super();
		this.seqNum = seqNum;
		this.A_B = A_B;
		this.B_A = B_A;
		this.A_B_A = A_B_A;
		this.secondReceived = secondReceived;
	}


	public int getSeqNum() {
		return seqNum;
	}


	public void setSeqNum(int seqNum) {
		this.seqNum = seqNum;
	}


	public int getSecondReceived() {
		return secondReceived;
	}


	public void setSecondReceived(int secondReceived) {
		this.secondReceived = secondReceived;
	}


	public int getA_B() {
		return A_B;
	}


	public void setA_B(int a_B) {
		A_B = a_B;
	}


	public int getB_A() {
		return B_A;
	}


	public void setB_A(int b_A) {
		B_A = b_A;
	}


	public int getA_B_A() {
		return A_B_A;
	}


	public void setA_B_A(int a_B_A) {
		A_B_A = a_B_A;
	}

	
	
	
	
	
	

}
