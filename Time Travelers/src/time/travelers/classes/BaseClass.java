package time.travelers.classes;

public class BaseClass{
	public int hp;
	public int atk;
	public int def;
	public int crit;
	public int spd;
	
	public BaseClass(){
		hp=0;
		atk=0;
		def=0;
		crit=0;
		spd=0;
	}
	
	public int currentHp(int newHp){
		hp=hp-newHp;
		return hp;
	}
	
	public void currentSpd(int newSpd){
		spd=newSpd;
	}
	
	public void resetSpd(){
		spd=10;
	}
}




