package time.travelers.combat;
import time.travelers.classes.*;

public class Combat {
	public int damage;
	
	public Combat(){
		
	}
	
	public int battleCalc(BaseClass test1,BaseClass test2){
		if(test1.atk>test2.def){
			System.out.println("atk");
			damage=test1.atk-test2.def;
			if(isCritical(test1)){
				damage=damage*2;
				System.out.println("CRIT");
			}
		}
			if(test1.atk<test2.def){
				damage=test2.def-test1.atk;
				if(isCritical(test2)){
					damage=damage*-2;
				}
			}
			if(test1.atk==test2.def){
				double randNumber = Math.random();
				double d = randNumber * 2;
				int randomInt = (int)d;
				if(randomInt==1){	
					damage=1;
				}
				else {
					damage=0;
				}
			}
		return damage;
	}
	
	public boolean isCritical(BaseClass test){
		double randNumber = Math.random();
		double d = randNumber * 100;
		int randomInt = (int)d+1;
		if(randomInt<=test.crit){
			return true;
		}
		else {
			return false;
		}
	}
}
