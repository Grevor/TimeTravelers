package time.travelers.main;

public class Main {

	
	
	public static void main(String[] args) throws InterruptedException {
		GameWindow gw = new GameWindow();
		
		while(gw != null && gw.isVisible()) {
			gw.tick(100);
			Thread.sleep(100);
		}
	}
	
	
}
