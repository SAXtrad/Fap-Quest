package fapquest;

public class ImageAttack {
	private int costInStamina;
	private String name;
	
	public ImageAttack(String name, int costInStamina){
		this.costInStamina=costInStamina;
		this.name=name;
	}
	
	public String getName(){
		return name;
	}

	public int getCostInStamina(){
		return costInStamina;
	}
}
