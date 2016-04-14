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

public class OpponentHUD {
	
	private Image img_frame_avatar;
	private Image img_avatar;
	private Image img_frame_bar_stamina;
	private Image img_frame_bar_bg;
	private Image img_stamina_bar_top;
	private Image img_stamina_bar_middle;
	
	private int x_frame;
	private int y_frame;
	
	private float cd_stamina;
	private float stamina_width;
	private float stamina;
	private float max_stamina;
	private int stamina_frames;
	
	private JFrame frame;
	private int frame_width;
	
	private String name;
	
	private int state;
	private int cd_framesToFadeIn;
	private int framesToFadeIn;
	private int cd_framesToFadeOut;
	private int framesToFadeOut;
	private float alpha;

	public static final int HIDDEN = 0; 
	public static final int FADEIN = 1; 
	public static final int SHOW = 2; 
	public static final int FADEOUT = 4; 
	
	private Composite comp;
	private int comp_rule;
	
	private Composite comp_noAlpha;
	
	private Font font;
	
	public OpponentHUD(JFrame frame){
		this.frame = frame;
		
		img_frame_avatar = new ImageIcon("data/skins/default/hud_frame.png").getImage();
		img_avatar = new ImageIcon("data/skins/default/hud_avatar.png").getImage();
		img_frame_bar_stamina = new ImageIcon("data/skins/default/hud_barframe_stamina.png").getImage();
		img_stamina_bar_top = new ImageIcon("data/skins/default/hud_barframe_stamina_top.png").getImage();
		img_stamina_bar_middle = new ImageIcon("data/skins/default/hud_barframe_stamina_middle.png").getImage();
		img_frame_bar_bg = new ImageIcon("data/skins/default/hud_barframe_background.png").getImage();
		
		x_frame = frame_width - 15 - img_frame_avatar.getWidth(null);
		y_frame = 15;
		
		max_stamina = 100;
		stamina = 50;
		
		name="Opponent Name";
		
		state = HIDDEN;
		alpha=0.0f;
		
		comp_rule = AlphaComposite.SRC_OVER;
		comp = AlphaComposite.getInstance(comp_rule , alpha);
		
		cd_framesToFadeIn = 0;
		
		comp_noAlpha = AlphaComposite.getInstance(comp_rule , 1.0f);
		
		try {
			font = Font.createFont(Font.TRUETYPE_FONT, new File("data/skins/default/JUNGLEFEVER.TTF"));
		} catch (FontFormatException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void update(){
		frame_width = frame.getContentPane().getSize().width;
		
		x_frame = frame_width - 15 - img_frame_avatar.getWidth(null);
		y_frame = 15;
		
		if(state == FADEOUT){
			cd_framesToFadeOut--;
			alpha = (float)cd_framesToFadeOut/(float)framesToFadeOut;
			if(alpha < 0.0f){
				alpha=0;
				state=HIDDEN;
			}
			comp = AlphaComposite.getInstance(comp_rule , alpha);
		}else if(state == FADEIN){
			cd_framesToFadeIn++;
			alpha = (float)cd_framesToFadeIn/(float)framesToFadeIn;
			if(alpha > 1.0f){
				alpha=1.0f;
				state=SHOW;
			}
			comp = AlphaComposite.getInstance(comp_rule , alpha);
		}
		
		cd_stamina++;
		if(cd_stamina < stamina_frames){
			stamina -= (stamina_width/stamina_frames);
			if(stamina < 0){
				stamina=0;
			}
		}
		
		
	}
	
	public void draw(Graphics g) {
		if(state != HIDDEN){
			int decalage = (img_frame_avatar.getHeight(null) - img_frame_bar_stamina.getHeight(null)*3)/4;
			int x = x_frame + (img_frame_avatar.getWidth(null)*1)/20;
			int y = y_frame + decalage;

			Graphics2D g2d = (Graphics2D) g;

			g2d.setComposite(comp);

			g2d.setFont(font.deriveFont(Font.PLAIN, 25f)); 
			g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
			g2d.setColor(Color.WHITE);
			FontMetrics fm = g2d.getFontMetrics();
			g2d.drawString(name, x-15 - fm.stringWidth(name), y+20);

			//Draw Stamina Bar
			y += img_frame_bar_stamina.getHeight(null) + decalage;
			g.drawImage(img_frame_bar_bg, x - img_frame_bar_stamina.getWidth(null), y, null);

			int bar_width = (int)((float)stamina*((float)(img_frame_bar_stamina.getWidth(null) - img_stamina_bar_top.getWidth(null))/(float)max_stamina));
			g2d.drawImage(img_stamina_bar_middle, x - bar_width, y, bar_width, img_frame_bar_stamina.getHeight(null), null);
			g.drawImage(img_stamina_bar_top, x - bar_width - img_stamina_bar_top.getWidth(null), y, null);
			g.drawImage(img_frame_bar_stamina, x - img_frame_bar_stamina.getWidth(null), y, null);

			//Draw Avatar
			int decal = 10;
			g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION,RenderingHints.VALUE_INTERPOLATION_BILINEAR);
			g2d.drawImage(img_avatar, x_frame + decal, y_frame + decal, img_frame_avatar.getWidth(null)-decal*2, img_frame_avatar.getHeight(null)-decal*2, null);
			g.drawImage(img_frame_avatar, x_frame, y_frame, null);

			g2d.setComposite(comp_noAlpha);
		}
	}

	public void setName(String name){
		this.name=name;
	}

	public void setMaxStamina(int max_stamina) {
		this.max_stamina=max_stamina;
		this.stamina = max_stamina;
	}
	
	public void setStamina(float costInStamina, int frames) {
		stamina_frames=frames;
		stamina_width=costInStamina;
		cd_stamina=0;
	}
	
	public void loadFadeOut(int frames) {
		state = FADEOUT;
		framesToFadeOut = frames;
		cd_framesToFadeOut = framesToFadeOut;
		alpha = 1.0f;
	}
	
	public void loadFadeIn() {
		state = FADEIN;
		framesToFadeIn = Opponent.FRAME_FADEIN;
		cd_framesToFadeIn = 0;
		alpha = 0.0f;
	}

	public void setAvatar(Image img_avatar) {
		if(this.img_avatar != null){
			this.img_avatar.flush();
		}
		
		this.img_avatar=img_avatar;
	}

	public void reset() {
		x_frame = frame_width - 15 - img_frame_avatar.getWidth(null);
		y_frame = 15;
		
		max_stamina = 100;
		stamina = 50;
		
		name="Opponent Name";
		
		state = HIDDEN;
		alpha=0.0f;
		
		comp_rule = AlphaComposite.SRC_OVER;
		comp = AlphaComposite.getInstance(comp_rule , alpha);
		
		cd_framesToFadeIn = 0;
	}
}
