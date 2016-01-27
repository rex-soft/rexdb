
public class TestSyn {

	private static boolean ready;
	private static int number;
	
	private static class ReaderThread extends Thread{
		public void run(){
			while(!ready)
				Thread.yield();
			System.out.println(number);
		}
	}
	
	public static void main(String[] a){
		new ReaderThread().start();
		number=42;
		ready=true;
	}
}
