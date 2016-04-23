package fapquest;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.RenderingHints;

public class MenuButton {
	private Image img_button_unpressed;
	private Image img_button_over;
	private Image img_button_pressed;
	private Font font;
	
	private int x;
	private int y;
	private int width;
	private int height;
	
	private int state;
	
	private String text;
	
	public static final int STATE_UNPRESSED = 0;
	public static final int STATE_PRESSED = 1;
	
	public boolean over;
	
	private int type;
	
	public static final int TYPE_PLAY = 0;
	public static final int TYPE_OPTIONS = 1;
	public static final int TYPE_CHARACTERS = 2;
	public static final int TYPE_QUIT = 3;
	public static final int TYPE_MENU = 4;
	
	public MenuButton(Image img_button_unpressed, Image img_button_over, Image img_button_pressed, String text, int type, Font font){
		this.img_button_unpressed=img_button_unpressed;
		this.img_button_over=img_button_over;
		this.img_button_pressed=img_button_pressed;
		this.text = text;
		this.type=type;
		this.font=font;
		
		state = STATE_UNPRESSED;
	}
	
	public void update(int x, int y, int width, int height){
		this.x=x;
		this.y=y;
		this.width=width;
		this.height=height;
	}
	
	public void draw(Graphics2D g2d, float font_size){
		if(state == STATE_UNPRESSED){
			g2d.drawImage(img_button_unpressed, x, y, width, height, null);
		}else if(state == STATE_PRESSED){
			g2d.drawImage(img_button_pressed, x, y, width, height, null);
		}
		
		g2d.setFont(font.deriveFont(Font.PLAIN, font_size)); 
		g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
		g2d.setColor(Color.WHITE);
		FontMetrics fm = g2d.getFontMetrics();
		g2d.drawString(text, x + (width - fm.stringWidth(text))/2, y + (height - fm.getHeight())/2 + height/4 + ((35.0f-font_size)*5)/12); //(height*19)/32
		
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
	
	public int getType(){
		return type;
	}

	public void unpress() {
		state = STATE_UNPRESSED;
		over = false;
	}
}
