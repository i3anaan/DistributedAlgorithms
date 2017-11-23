package group27_distributed.assignment1c;

public class Clock {

	 int stamp = 0;
	public int nextStamp(){
		stamp++;
		return this.stamp;
	}
	public int recvStamp(int stamp) {
		this.stamp = Math.max(this.stamp, stamp) +1;
		return this.stamp; // maybe return this.stamp-1 ?
	}
}
