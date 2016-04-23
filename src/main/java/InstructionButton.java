package fapquest;

import java.awt.Graphics2D;
import java.awt.Image;

public class InstructionButton {
	private Image img_button_unpressed;
	private Image img_button_over;
	private Image img_button_pressed;
	
	private int x;
	private int y;
	private int width;
	private int height;
	
	private int state;
	
	public static final int STATE_UNPRESSED = 0;
	public static final int STATE_PRESSED = 1;
	
	public boolean over;
	
	public InstructionButton(Image img_button_unpressed, Image img_button_over, Image img_button_pressed){
		this.img_button_unpressed=img_button_unpressed;
		this.img_button_over=img_button_over;
		this.img_button_pressed=img_button_pressed;
		
		state = STATE_UNPRESSED;
	}
	
	public void update(int x, int y, int width, int height){
		this.x=x;
		this.y=y;
		this.width=width;
		this.height=height;
	}
	
	public void draw(Graphics2D g2d){
		if(state == STATE_UNPRESSED){
			g2d.drawImage(img_button_unpressed, x, y, width, height, null);
		}else if(state == STATE_PRESSED){
			g2d.drawImage(img_button_pressed, x, y, width, height, null);
		}
		
		if(over){
			g2d.drawImage(img_button_over, x, y, width, height, null);
		}
	}

	public void over(double x2, double y2) {
		over = x2 > x && x2 <= x + width && y2 > y && y2 <= y + height && state == STATE_UNPRESSED;
	}
	
	public void press(double x2, double y2) {
		if(x2 > x && x2 <= x + width && y2 > y && y2 <= y + height){
			state = STATE_PRESSED;
		}
	}
	
	public boolean unpress(double x2, double y2) {
		if(x2 > x && x2 <= x + width && y2 > y && y2 <= y + height && state == STATE_PRESSED){
			return true;
		}else{
			state = STATE_UNPRESSED;
			return false;
		}
	}
}
