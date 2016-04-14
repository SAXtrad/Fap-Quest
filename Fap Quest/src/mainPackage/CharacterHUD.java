package mainPackage;
import java.awt.Color;
import java.awt.Font;
import java.awt.FontFormatException;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.RenderingHints;
import java.io.File;
import java.io.IOException;

import javax.swing.ImageIcon;

public class CharacterHUD {
	
	private Image img_frame_avatar;
	private Image img_avatar;
	private Image img_frame_bar_exp;
	private Image img_frame_bar_arousal;
	private Image img_frame_bar_bg;
	private Image img_exp_bar_top;
	private Image img_exp_bar_middle;
	private Image img_arousal_bar_top;
	private Image img_arousal_bar_middle;
	
	private MenuButton button_i_came;
	
	private int x_frame;
	private int y_frame;
	
	private int exp;
	private int next_lev;
	private int lvl;
	private int bpm;
	
	private int arousal;
	private int max_arousal;
	
	private Font font;
	
	public CharacterHUD(){
		img_frame_avatar = new ImageIcon("data/skins/default/hud_frame.png").getImage();
		img_avatar = new ImageIcon("data/skins/default/hud_avatar.png").getImage();
		img_frame_bar_exp = new ImageIcon("data/skins/default/hud_barframe_exp.png").getImage();
		img_frame_bar_arousal = new ImageIcon("data/skins/default/hud_barframe_arousal.png").getImage();
		img_exp_bar_top = new ImageIcon("data/skins/default/hud_barframe_exp_top.png").getImage();
		img_exp_bar_middle = new ImageIcon("data/skins/default/hud_barframe_exp_middle.png").getImage();
		img_arousal_bar_top = new ImageIcon("data/skins/default/hud_barframe_arousal_top.png").getImage();
		img_arousal_bar_middle = new ImageIcon("data/skins/default/hud_barframe_arousal_middle.png").getImage();
		img_frame_bar_bg = new ImageIcon("data/skins/default/hud_barframe_background.png").getImage();

		x_frame = 15;
		y_frame = 15;
		
		int start_bpm=100;
		
		lvl=1;
		exp=0;
		next_lev=lvl*(100);
		bpm=start_bpm+(lvl-1)*10;
		
		arousal=50;
		max_arousal=200;
		
		try {
			font = Font.createFont(Font.TRUETYPE_FONT, new File("data/skins/default/JUNGLEFEVER.TTF"));
		} catch (FontFormatException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		Image img_button_unpressed = new ImageIcon("data/skins/default/hud_button_unpressed.png").getImage();
		Image img_button_over = new ImageIcon("data/skins/default/hud_button_over.png").getImage();
		Image img_button_pressed = new ImageIcon("data/skins/default/hud_button_pressed.png").getImage();
		
		button_i_came = new MenuButton(img_button_unpressed, img_button_over, img_button_pressed, "I Came !", 0, font);
		button_i_came.update(x_frame*2 + img_frame_avatar.getWidth(null), y_frame + ((img_frame_avatar.getHeight(null) - img_frame_bar_exp.getHeight(null)*3)/4)*3 + img_frame_bar_exp.getHeight(null)*2, img_button_unpressed.getWidth(null), img_button_unpressed.getHeight(null));
	}
	
	public void update(){
		
	}
	
	public void draw(Graphics g) {
		int decalage = (img_frame_avatar.getHeight(null) - img_frame_bar_exp.getHeight(null)*3)/4;
		int x = x_frame + (img_frame_avatar.getWidth(null)*19)/20;
		int y = y_frame + decalage;
		
		Graphics2D g2d = (Graphics2D) g;

		String title="Hero Level "+lvl+" (BPM : "+bpm+")";

		g2d.setFont(font.deriveFont(Font.PLAIN, 25f)); 
		g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
		g2d.setColor(Color.WHITE);
		g2d.drawString(title, x+15, y+20);

		//Draw Exp Bar
		y += img_frame_bar_exp.getHeight(null) + decalage;
		g.drawImage(img_frame_bar_bg, x, y, null);
	
		int bar_width = (int)((float)exp*((float)(img_frame_bar_exp.getWidth(null) - img_exp_bar_top.getWidth(null))/(float)next_lev));
		g2d.drawImage(img_exp_bar_middle, x, y, bar_width, img_exp_bar_middle.getHeight(null), null);
		g.drawImage(img_exp_bar_top, x + bar_width, y, null);
		g.drawImage(img_frame_bar_exp, x, y, null);

		//Draw Arousal Bar
		/*y += img_frame_bar_arousal.getHeight(null) + decalage;
		g.drawImage(img_frame_bar_bg, x, y, null);
		
		bar_width = (int)((float)arousal*((float)(img_frame_bar_arousal.getWidth(null) - img_arousal_bar_top.getWidth(null))/(float)max_arousal));
		g2d.drawImage(img_arousal_bar_middle, x, y, bar_width, img_arousal_bar_middle.getHeight(null), null);
		g.drawImage(img_arousal_bar_top, x + bar_width, y, null);
		
		g.drawImage(img_frame_bar_arousal, x, y, null);*/
		
		//Draw Avatar
		g.drawImage(img_avatar, x_frame, y_frame, null);
		g.drawImage(img_frame_avatar, x_frame, y_frame, null);

		button_i_came.draw(g2d, 15.0f);
	}
	
	public void setExp(int exp){
		this.exp=exp;
	}
	
	public void setNextLvl(int next_lev){
		this.next_lev=next_lev;
	}
	
	public void setLVL(int lvl){
		this.lvl=lvl;
	}
	
	public void setBPM(int bpm){
		this.bpm=bpm;
	}

	public MenuButton getButtonICame(){
		return button_i_came;
	}
}
