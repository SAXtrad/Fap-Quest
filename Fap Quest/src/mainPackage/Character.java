package mainPackage;
import java.awt.Graphics;
import java.awt.Image;
import java.util.ArrayList;

import javax.swing.ImageIcon;
import javax.swing.JFrame;

public class Character {
	private CharacterHUD hud;
	private Beatbar beatbar;
	private ArrayList<Image> img_character;
	private Image img_foreground;
	private int cd_chara_animation;
	private JFrame frame;
	private int frame_width;
	private int frame_height;
	
	private int x;
	private int x_start;
	private int x_end;
	private int y;
	private int speed;
	
	private int lvl;
	private int exp;
	private int next_lev;
	private int bpm;
	private int start_bpm;
	
	private int state;
	
	public static final int BEGINNING = -1;
	public static final int STOPPED = 0;
	public static final int MOVING = 1;
	public static final int WALKING = 2;
	
	private int size_path;
	
	public Character(JFrame frame){
		this.frame=frame;
		hud = new CharacterHUD();
		img_character = new ArrayList<Image>();
		
		cd_chara_animation = 0;
		
		speed = 5;
		
		for(int i = 0 ; i < 8 ; i++){
			Image img = new ImageIcon("data/character/character_"+(i+1)+".png").getImage();
			img_character.add(img);
		}
		
		img_foreground = new ImageIcon("data/world/forest/foreground.png").getImage();
		
		start_bpm=100;
		
		lvl=1;
		exp=0;
		next_lev=lvl*(100);
		bpm=start_bpm+(lvl-1)*10;

		size_path = 0;
		
		state = BEGINNING;
	}
	
	public void update(){
		frame_width = frame.getContentPane().getSize().width;
		frame_height = frame.getContentPane().getSize().height;

		if(state != STOPPED && state != BEGINNING){
			cd_chara_animation++;
			if(cd_chara_animation/speed >= img_character.size()){
				cd_chara_animation = 0;
			}
			
			if(state == MOVING){
				size_path -= speed;
				if(size_path <= 0){
					size_path = 0;
					state = WALKING;
					beatbar.startBeatbar();
				}
			}
		}
		
		if(state == BEGINNING){
			x = frame_width/15;
		}else if(state == MOVING){
			x = x_start - size_path;
		}else{
			x = (frame_width*6)/15;
		}
		
		y = frame_height-img_foreground.getHeight(null)-img_character.get(0).getHeight(null);
	}
	
	public void draw(Graphics g) {
		g.drawImage(img_character.get(cd_chara_animation/speed), x, y, null);
		hud.draw(g);
	}
	
	public int getLevel(){
		return lvl;
	}

	public void giveExp(int level_opponent) {
		exp+=60+level_opponent*30;
		
		if(exp >= next_lev){
			lvl++;
			exp-=next_lev;
			next_lev=lvl*(100);
			bpm=start_bpm+(lvl-1)*10;
			
			beatbar.setBPM(bpm);
			hud.setBPM(bpm);
			hud.setLVL(lvl);
			hud.setNextLvl(next_lev);
		}
		
		hud.setExp(exp);
	}
	
	public void setBeatbar(Beatbar beatbar){
		this.beatbar = beatbar;
		beatbar.setBPM(bpm);
	}
	
	public void startMoving(){
		state = MOVING;
		size_path = (frame_width*1)/3;
		x_start = x + (frame_width*1)/3 ;
	}
	
	public void walk(){
		state = WALKING;
	}
	
	public void stopWalking(){
		state = STOPPED;
	}
	
	public MenuButton getButtonICame(){
		return hud.getButtonICame();
	}

	public void reset() {
		cd_chara_animation = 0;
		
		start_bpm=100;
		
		lvl=1;
		exp=0;
		next_lev=lvl*(100);
		bpm=start_bpm+(lvl-1)*10;

		size_path = 0;
		
		state = BEGINNING;
	}
}
