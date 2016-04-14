package mainPackage;

import java.awt.AlphaComposite;
import java.awt.Composite;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;

import javax.swing.ImageIcon;
import javax.swing.JFrame;

public class Instruction {
	private JFrame frame;
	private World world;
	private Image img;
	private Image img_button_unpressed;
	private Image img_button_over;
	private Image img_button_pressed;
	
	private int x;
	private int y;
	private int width;
	private int height;
	private int decal;
	
	private int state;
	
	public static final int HIDDEN = 0; 
	public static final int SHOW = 2; 
	public static final int FADEOUT = 3; 
	
	public static final int FRAME_FADEOUT = 60; 
	
	private Composite comp;
	private int comp_rule;
	private int cd_fade_out;
	
	private Composite comp_noAlpha;
	
	private InstructionButton button_close;
	
	public Instruction(JFrame frame, World world){
		this.frame = frame;
		this.world = world;
		img = new ImageIcon("data/instructions/instructions.png").getImage();
		img_button_unpressed = new ImageIcon("data/instructions/instruction_button_unpressed.png").getImage();
		img_button_over = new ImageIcon("data/instructions/instruction_button_over.png").getImage();
		img_button_pressed = new ImageIcon("data/instructions/instruction_button_pressed.png").getImage();
		
		decal = 25;
		
		state = SHOW;
		
		float alpha=0.0f;
		
		comp_rule = AlphaComposite.SRC_OVER;
		comp = AlphaComposite.getInstance(comp_rule , alpha);
		
		cd_fade_out = FRAME_FADEOUT;
		
		comp_noAlpha = AlphaComposite.getInstance(comp_rule , 1.0f);
		
		button_close = new InstructionButton(img_button_unpressed, img_button_over, img_button_pressed);
	}
	
	public void update(){
		int max_width = frame.getContentPane().getSize().width - decal*2;
		int max_height = frame.getContentPane().getSize().height - decal*2;

		if(img.getWidth(null) < max_width && img.getHeight(null) < max_height){
			width = img.getWidth(null);
			height = img.getHeight(null);
		}else if(img.getWidth(null) >= max_width && (img.getHeight(null)*max_width)/img.getWidth(null) < max_height){
			width = max_width;
			height = (img.getHeight(null)*max_width)/img.getWidth(null);
		}else{
			width = (img.getWidth(null)*max_height)/img.getHeight(null);
			height = max_height;
		}

		x = (max_width - width)/2 + decal;
		y = (max_height - height)/2 + decal;
		
		if(state == FADEOUT){
			cd_fade_out--;
			float alpha = (float)cd_fade_out/(float)FRAME_FADEOUT;
			if(cd_fade_out <= 0){
				alpha = 0;
				cd_fade_out = 0;
				state = HIDDEN;
				world.start();
			}
			comp = AlphaComposite.getInstance(comp_rule , alpha);
		}
		
		int button_size = width/12;
		int x_button = x + (width*5)/6;
		int y_button = y + height/17;
		button_close.update(x_button, y_button, button_size, button_size);
	}
	
	public void draw(Graphics g) {
		if(state != HIDDEN){
			Graphics2D g2d = (Graphics2D) g;
			
			if(state == FADEOUT){
				g2d.setComposite(comp);
			}
			
			g2d.drawImage(img, x, y, width, height, null);
			
			button_close.draw(g2d);
			
			g2d.setComposite(comp_noAlpha);

		}
	}
	
	public InstructionButton getButtonClose(){
		return button_close;
	}
	
	public void fadeOut(){
		state = FADEOUT;
	}
	
	public void reset(){
		state = SHOW;
		
		float alpha=0.0f;

		comp = AlphaComposite.getInstance(comp_rule , alpha);
		
		cd_fade_out = FRAME_FADEOUT;
	}
}
