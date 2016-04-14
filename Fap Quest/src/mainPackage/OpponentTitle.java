package mainPackage;
import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Composite;
import java.awt.Font;
import java.awt.FontFormatException;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.RenderingHints;
import java.io.File;
import java.io.IOException;

import javax.swing.ImageIcon;
import javax.swing.JFrame;

public class OpponentTitle {
	
	private JFrame frame;
	private Image img_shield;
	private String name_hero;
	private String name_opponent;
	
	private int y_shield;
	private int x_shield;
	
	private int decal_text_width;
	
	private float alpha_shield;
	private float alpha_text;
	
	private int cd_fade_in;
	private int cd_fade_out;
	private int width_path_text;
	private int current_width_path_text;
	
	public static final int FRAME_FADEIN = 5; 
	public static final int FRAME_FADEOUT = 60; 
	
	private int state;
	
	public static final int STATE_HIDDEN = 0; 
	public static final int STATE_FADEIN = 1; 
	public static final int STATE_SHOW = 2; 
	public static final int STATE_FADEOUT = 3; 
	
	private Font font;
	private Composite comp_shield;
	private Composite comp_text;
	private int comp_rule;
	
	public OpponentTitle(JFrame frame){
		this.frame=frame;
		
		img_shield = new ImageIcon("data/skins/default/title_opponent_shield.png").getImage();
		
		name_hero = "HERO";
		name_opponent = "Opponent Name";
		
		x_shield = (frame.getContentPane().getSize().width-img_shield.getWidth(null))/2;
		y_shield = frame.getContentPane().getSize().height/10;
		
		decal_text_width = img_shield.getWidth(null)/7;
		
		state = STATE_HIDDEN;
		
		cd_fade_in = 0;
		cd_fade_out = 0;
		
		alpha_shield = 0;
		alpha_text = 1.0f;
		
		width_path_text = (frame.getContentPane().getSize().width-img_shield.getWidth(null))/2 + decal_text_width;
		
		try {
			font = Font.createFont(Font.TRUETYPE_FONT, new File("data/skins/default/JUNGLEFEVER.TTF"));
		} catch (FontFormatException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		comp_rule = AlphaComposite.SRC_OVER;
		
		comp_shield = AlphaComposite.getInstance(comp_rule , alpha_shield);
		comp_text = AlphaComposite.getInstance(comp_rule , alpha_text);
	}
	
	public void update(){
		if(state != STATE_HIDDEN){
			x_shield = (frame.getContentPane().getSize().width-img_shield.getWidth(null))/2;
			width_path_text = (frame.getContentPane().getSize().width-img_shield.getWidth(null))/2 + decal_text_width;

			if(state == STATE_FADEIN){
				cd_fade_in++;
				alpha_shield = ((float)cd_fade_in)/((float)FRAME_FADEIN);
				current_width_path_text = (int)(width_path_text*((float)FRAME_FADEIN-cd_fade_in)/((float)FRAME_FADEIN));
				if(cd_fade_in >= FRAME_FADEIN){
					alpha_shield = 1.0f;
					state = STATE_SHOW;
				}
				comp_shield = AlphaComposite.getInstance(comp_rule , alpha_shield);
				comp_text = AlphaComposite.getInstance(comp_rule , alpha_text);
			}else if(state == STATE_FADEOUT){
				cd_fade_out++;
				alpha_shield = ((float)FRAME_FADEOUT-cd_fade_out)/((float)FRAME_FADEOUT);
				alpha_text = ((float)FRAME_FADEOUT-cd_fade_out)/((float)FRAME_FADEOUT);
				if(cd_fade_out >= FRAME_FADEOUT){
					alpha_shield = 0.0f;
					alpha_text = 0.0f;
					state = STATE_HIDDEN;
				}
				comp_shield = AlphaComposite.getInstance(comp_rule , alpha_shield);
				comp_text = AlphaComposite.getInstance(comp_rule , alpha_text);
			}
		}
	}
	
	public void draw(Graphics g) {
		if(state != STATE_HIDDEN){

			Graphics2D g2d = (Graphics2D) g;

			g2d.setComposite(comp_shield);

			g2d.drawImage(img_shield, x_shield, y_shield, null);

			g2d.setComposite(comp_text);

			g2d.setFont(font.deriveFont(Font.PLAIN, 40f)); 
			g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
			g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
			g2d.setColor(Color.WHITE);
			FontMetrics fm = g2d.getFontMetrics();

			g2d.drawString(name_hero, x_shield - fm.stringWidth(name_hero) + decal_text_width - current_width_path_text , y_shield +(img_shield.getHeight(null)*3)/5);
			g2d.drawString(name_opponent, x_shield + img_shield.getWidth(null) - decal_text_width + current_width_path_text, y_shield +(img_shield.getHeight(null)*3)/5);
			

		}

	}
	
	public void setOpponentName(String name){
		name_opponent = name.toUpperCase();
	}
	
	public void setYShield(int y_introduction){
		y_shield = y_introduction - img_shield.getHeight(null)/2;
	}
	
	public void fadeIn(){
		state = STATE_FADEIN;
		cd_fade_in = 0;
		alpha_text = 1.0f;
		alpha_shield =0.0f;
		width_path_text = (frame.getContentPane().getSize().width-img_shield.getWidth(null))/2 + decal_text_width;
		y_shield = frame.getContentPane().getSize().height/10;
		
		comp_shield = AlphaComposite.getInstance(comp_rule , alpha_shield);
		comp_text = AlphaComposite.getInstance(comp_rule , alpha_text);
	}
	
	public void fadeOut(){
		state = STATE_FADEOUT;
		cd_fade_out = 0;
		
	}

}
