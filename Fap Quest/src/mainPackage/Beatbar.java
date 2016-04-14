package mainPackage;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.io.File;
import java.util.ArrayList;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JPanel;

import kuusisto.tinysound.Sound;
import kuusisto.tinysound.TinySound;

public class Beatbar extends JPanel {
	private static final long serialVersionUID = 1L;
	private ArrayList<BeatPattern> beatpatterns;
	private World world;
	private Image img_beatbar_wave_off;
	private Image img_beatbar_wave_on;
	private Image img_beatbar_left;
	private Image img_beatbar_middle;
	private Image img_beatbar_right;
	private Image img_beatbar_marker;
	private JFrame frame;
	private int frame_width;
	private int frame_height;
	private int bpm;
	private int speed;
	private int [] patternPreIntroduction = {1,0,0,0,0,0,0,0};
	private int [] patternIntroduction = {1,0,0,0};
	private int [] patternEnd = {1,0,0,0,0,0,0,0};
	private int [][] patternAttack = {
			{1,0,0,0,1,0,0,0,1,0,0,0,1,0,0,0}, // 1- 1
			//{1,0,1,0,0,0,0,0,1,0,1,0,0,0,0,0}, // 1,2 - 1,2
			{1,0,0,0,1,0,0,0,1,0,1,0,1,0,0,0}, // 1,2 - 1,2,3
			{1,0,1,0,1,0,0,0,1,0,1,0,1,0,0,0}, // 1,2,3 - 1,2,3
			{1,0,1,0,0,0,1,0,1,0,1,0,1,0,0,0}, // 1,2 - 1,2,3,4
			{1,0,0,0,1,0,1,0,1,0,1,0,1,0,0,0}, // 1 - 1,2,3,4,5
			{1,0,1,0,1,0,1,0,1,0,1,0,1,0,0,0}, // 1,2,3,4,5,6,7
			{1,0,1,0,1,0,1,0,1,0,1,0,1,0,1,0}}; // double-time
	
	private int nb_img_attack;
	private int cd_nb_img_attack;

	public int state;
	public static final int INSTRUCTION = -4;
	public static final int WALKING = -3;
	public static final int NO_OPPONENT = -2;
	public static final int PRE_INTRODUCTION = -1;
	public static final int INTRODUCTION = 0;
	public static final int NEW_IMAGE = 2;
	public static final int BERSERK = 3;
	public static final int END = 4;
	public static final int FADEOUT = 5;
	
	public int start_x_beats = 540;
	
	private Sound sound_beat;
	private Sound sound_sword;
	
	private int state_menu;
	
	public static final int MENU_HIDDEN = 0; 
	public static final int MENU_FADEIN = 1; 
	public static final int MENU_SHOW = 2; 
	public static final int MENU_FADEOUT = 3; 

	private int cd_framesToFade;
	private float alpha;
	private Color color_fade;
	
	private int next_page;
	
	public Beatbar(JFrame frame, World world){
		this.frame=frame;
		this.world=world;

		beatpatterns = new ArrayList<BeatPattern>();
		img_beatbar_left = new ImageIcon("data/skins/default/beatbar_frame_left.png").getImage();
		img_beatbar_right = new ImageIcon("data/skins/default/beatbar_frame_right.png").getImage();
		img_beatbar_middle = new ImageIcon("data/skins/default/beatbar_frame_middle.png").getImage();
		img_beatbar_marker = new ImageIcon("data/skins/default/beatbar_marker.png").getImage();
		
		img_beatbar_wave_off = new ImageIcon("data/skins/default/beatbar_wave_off.png").getImage();
		img_beatbar_wave_on = new ImageIcon("data/skins/default/beatbar_wave_on.png").getImage();
		
		bpm = 100;
		speed = 4;
		
		nb_img_attack=3;
		
		state=INSTRUCTION;
		
		File file_beat = new File("data/sounds/beat.wav");
		File file_sword = new File("data/sounds/taunt_shield_up.wav");
		
		TinySound.init();
		
		sound_beat = TinySound.loadSound(file_beat);
		sound_sword = TinySound.loadSound(file_sword);
		
		state_menu = MENU_FADEIN;
		
		alpha = 1.0f;
		cd_framesToFade = Menu.FRAME_FADEIN;
		color_fade = new Color(0, 0, 0, alpha);
	}
	
	public void update(){
		frame_width = frame.getContentPane().getSize().width;
		frame_height = frame.getContentPane().getSize().height;
		start_x_beats = frame.getContentPane().getSize().width;
		
		setBounds(0, frame_height - img_beatbar_left.getHeight(null), frame_width, img_beatbar_left.getHeight(null));
		
		//Move the elements
		if(state == NO_OPPONENT || state == END || state == FADEOUT){
			world.move();
		}

		//Event Management
		for(int i = 0 ; i < beatpatterns.size() ; i++){
			int state_beatpatterns = beatpatterns.get(i).update();
			
			if(beatpatterns.get(i).beatOnMarker()){
				if(beatpatterns.get(i).getType() == Beatbar.PRE_INTRODUCTION){
					sound_sword.play();
				}else{
					sound_beat.play();
				}
			}
			
			if(state_beatpatterns == BeatPattern.STATE_FADEIN){
				if(beatpatterns.get(i).getType() == Beatbar.PRE_INTRODUCTION){
					world.getOpponent().getTitle().fadeIn();
					world.getOpponent().updateHUD();
				}else if(beatpatterns.get(i).getType() == Beatbar.INTRODUCTION){
					if(beatpatterns.get(i).getImgType() == OpponentVisual.IMG_TYPE_IMAGE){
						world.getOpponent().getVisual().loadVisual(beatpatterns.get(i).getImgVisual());
						world.getOpponent().loadFadeIn();
					}else if(beatpatterns.get(i).getImgType() == OpponentVisual.IMG_TYPE_GIF){
						world.getOpponent().getVisual().loadGIF(beatpatterns.get(i).getGIFFrames(), beatpatterns.get(i).getFramesPattern(), beatpatterns.get(i).getGIFNbActions());
						world.getOpponent().loadFadeIn();
					}
				}else if(beatpatterns.get(i).getType() == Beatbar.NEW_IMAGE){
					if(beatpatterns.get(i).getImgType() == OpponentVisual.IMG_TYPE_IMAGE){
						world.getOpponent().getVisual().loadTransition(beatpatterns.get(i).getImgVisual());
					}else if(beatpatterns.get(i).getImgType() == OpponentVisual.IMG_TYPE_GIF){
						world.getOpponent().getVisual().loadTransition(beatpatterns.get(i).getGIFFrames(), beatpatterns.get(i).getFramesPattern(), beatpatterns.get(i).getGIFNbActions());
					}
				}else if(beatpatterns.get(i).getType() == Beatbar.END){
					if(beatpatterns.get(i).getImgType() == OpponentVisual.IMG_TYPE_IMAGE){
						world.getOpponent().getVisual().loadTransition(beatpatterns.get(i).getImgVisual());
					}else if(beatpatterns.get(i).getImgType() == OpponentVisual.IMG_TYPE_GIF){
						world.getOpponent().getVisual().loadTransition(beatpatterns.get(i).getGIFFrames(), beatpatterns.get(i).getFramesPattern(), beatpatterns.get(i).getGIFNbActions());
					}
				}else if(beatpatterns.get(i).getType() == Beatbar.FADEOUT){
					world.getOpponent().loadFadeOut(beatpatterns.get(i).getFrames());
				}
			}else if(state_beatpatterns == BeatPattern.STATE_ONMARKER){
				state = beatpatterns.get(i).getType();
				if(beatpatterns.get(i).getType() == Beatbar.NEW_IMAGE){
					world.getOpponent().setStaminaForHUD(beatpatterns.get(i).getCostInStamina(), beatpatterns.get(i).getFrames());
				}else if(beatpatterns.get(i).getType() == Beatbar.END){
					world.getCharacter().giveExp(world.getOpponent().getLevel());
					world.getCharacter().walk();
				}else if(beatpatterns.get(i).getType() == Beatbar.INTRODUCTION){
					world.getOpponent().getTitle().fadeOut();
				}else if(beatpatterns.get(i).getType() == Beatbar.PRE_INTRODUCTION){
					world.getCharacter().stopWalking();
				}
			}
		}
		
		//Add new Beats
		if(beatpatterns.size() > 0){
			if(beatpatterns.get(beatpatterns.size()-1).getLastX() < frame_width){
				if(beatpatterns.get(beatpatterns.size()-1).getType() == Beatbar.INTRODUCTION){
					//world.getOpponent().loadNewAttack();
					ImageAttack img = world.getOpponent().getNameNewImage();
					
					//System.out.println("imgName = " + img.getName());
					int lvl_opponent = world.getOpponent().getLevel();
					if(lvl_opponent >= patternAttack.length){
						lvl_opponent = patternAttack.length-1;
					}
					nb_img_attack = 2 + (int) (Math.random() * 1);
					
					int index_pattern_attack = (int) (Math.random() * lvl_opponent);
					loadBeats(Beatbar.NEW_IMAGE, patternAttack[index_pattern_attack], img.getCostInStamina(), img.getName());
					cd_nb_img_attack++;
				}else if(beatpatterns.get(beatpatterns.size()-1).getType() == Beatbar.NEW_IMAGE){

					if(world.getOpponent().getStamina() > 0){
						//world.getOpponent().loadNewAttack();
						ImageAttack img = world.getOpponent().getNameNewImage();

						if(cd_nb_img_attack >= nb_img_attack){
							int lvl_opponent = world.getOpponent().getLevel();
							if(lvl_opponent >= patternAttack.length){
								lvl_opponent = patternAttack.length-1;
							}
							int index_pattern_attack = (int) (Math.random() * lvl_opponent);
							nb_img_attack = 2 + (int) (Math.random() * 1);
							
							loadBeats(Beatbar.NEW_IMAGE, patternAttack[index_pattern_attack], img.getCostInStamina(), img.getName());
							
							cd_nb_img_attack = 0;
						}else{
							loadBeats(Beatbar.NEW_IMAGE, beatpatterns.get(beatpatterns.size()-1).getOriginalPattern(), img.getCostInStamina(), img.getName());
						}

						cd_nb_img_attack++;
					}else{
						String imgName = world.getOpponent().getNameEndImage();
						loadEndBeats(imgName);
						loadFadeOutBeats();
					}

				}else if(beatpatterns.get(beatpatterns.size()-1).getType() == Beatbar.FADEOUT){
					//String imgName = world.getOpponent().getNameImgIntroduction();
					world.getOpponent().loadSilhouetteNextOpponent(beatpatterns.get(beatpatterns.size()-1).getLastX());
					world.getOpponent().loadNextOpponent();
					loadBeats(Beatbar.PRE_INTRODUCTION, patternPreIntroduction, 1, null);
				}else if(beatpatterns.get(beatpatterns.size()-1).getType() == Beatbar.PRE_INTRODUCTION){
					String imgName = world.getOpponent().getNameImgIntroduction();
					//world.getOpponent().loadSilhouetteNextOpponent(beatpatterns.get(beatpatterns.size()-1).getLastX()-frame_width/2);
					loadBeats(Beatbar.INTRODUCTION, patternIntroduction, 4, imgName);
				}
			}
		}

		//Delete the elements off screen
		for(int i = 0 ; i < beatpatterns.size() ; i++){
			if(beatpatterns.get(i).istoKill()){
				beatpatterns.remove(i);
			}
		}
		
		if(state_menu == MENU_FADEIN){
			cd_framesToFade--;
			if(cd_framesToFade <= 0){
				cd_framesToFade = 0;
				state_menu = MENU_SHOW;
				alpha = 0;
			}else{
				alpha = (float)cd_framesToFade/(float)Menu.FRAME_FADEIN;
			}
			color_fade = new Color(0, 0, 0, alpha);
		}else if(state_menu == MENU_FADEOUT){
			cd_framesToFade++;
			if(cd_framesToFade >= Menu.FRAME_FADEOUT){
				cd_framesToFade = Menu.FRAME_FADEIN;
				state_menu = MENU_HIDDEN;
				alpha = 1.0f;
				if(next_page == MenuButton.TYPE_MENU){
					//Do Nothing
				}
			}else{
				alpha = (float)cd_framesToFade/(float)Menu.FRAME_FADEOUT;
			}
			color_fade = new Color(0, 0, 0, alpha);
		}
	}
	
	private void doDrawing(Graphics g) {
		if(state_menu != MENU_HIDDEN){
			Graphics2D g2d = (Graphics2D) g;
			g2d.drawImage(img_beatbar_wave_off, 0, 0, frame_width, img_beatbar_left.getHeight(null), null);


			for(int i = 0 ; i < beatpatterns.size() ; i++){
				beatpatterns.get(i).draw(g);
			}

			g.drawImage(img_beatbar_marker, (frame_width - img_beatbar_marker.getWidth(null))/2, 0, null);

			for(int i = 0 ; i*img_beatbar_left.getWidth(null) + img_beatbar_left.getWidth(null) < frame_width - img_beatbar_left.getWidth(null) ; i++){
				int x = img_beatbar_left.getWidth(null) + i*img_beatbar_left.getWidth(null);
				int y = 0;
				g.drawImage(img_beatbar_middle, x, y, null);
			}

			g.drawImage(img_beatbar_left, 0, 0, null);
			g.drawImage(img_beatbar_right, frame_width - img_beatbar_right.getWidth(null), 0, null);

			if(state_menu == MENU_FADEIN || state_menu == MENU_FADEOUT){
				g2d.setColor(color_fade);
				g2d.fillRect(0, 0, frame_width, frame_height);
			}

			g.dispose();
		}
	}
	
	@Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        doDrawing(g);
    }
	
	public int getBPM(){
		return bpm;
	}
	
	public void setBPM(int bpm){
		this.bpm=bpm;
	}
	
	public void loadBeats(int type, int [] pattern, int nb, String imgName){
		int [] fullPatternIntroduction = new int [nb*pattern.length];
		for(int i=0;i<nb;i++){
			for(int j=0;j<pattern.length;j++){
				fullPatternIntroduction[i*pattern.length+j]=pattern[j];
			}
		}
		
		int x_last_pattern = 0;
		if(beatpatterns.isEmpty()){
			x_last_pattern = start_x_beats;
		}else{
			x_last_pattern = beatpatterns.get(beatpatterns.size()-1).getLastX() + 1;
		}
		
		
		BeatPattern b = new BeatPattern(frame, fullPatternIntroduction, pattern, bpm, speed, x_last_pattern, img_beatbar_wave_on, type, imgName);
		beatpatterns.add(b);
	}
	
	public void loadEndBeats(String imgName){
		int nb = 2;
		int [] fullPatternEnd = new int [nb*patternEnd.length];
		for(int i=0;i<nb;i++){
			for(int j=0;j<patternEnd.length;j++){
				fullPatternEnd[i*patternEnd.length+j]=patternEnd[j];
			}
		}
		
		
		BeatPattern b = new BeatPattern(frame, fullPatternEnd, patternEnd, bpm, speed, beatpatterns.get(beatpatterns.size()-1).getLastX() + 1, img_beatbar_wave_on, Beatbar.END, imgName);
		beatpatterns.add(b);
	}
	
	public void loadFadeOutBeats(){
		int nb = 2;
		int [] fullPatternEnd = new int [nb*patternEnd.length];
		for(int i=0;i<nb;i++){
			for(int j=0;j<patternEnd.length;j++){
				fullPatternEnd[i*patternEnd.length+j]=patternEnd[j];
			}
		}
		
		BeatPattern b = new BeatPattern(frame, fullPatternEnd, patternEnd, bpm, speed, beatpatterns.get(beatpatterns.size()-1).getLastX() + 1, img_beatbar_wave_on, Beatbar.FADEOUT, null);
		beatpatterns.add(b);
	}
	
	public void load(int type){
		next_page = type;
		state_menu = MENU_FADEOUT;
	}

	public void start() {
		state = WALKING;
		world.getCharacter().startMoving();
	}
	
	public void startBeatbar(){
		state = NO_OPPONENT;
		world.getOpponent().loadSilhouetteNextOpponent(start_x_beats);
		world.getOpponent().loadNextOpponent();
		loadBeats(Beatbar.PRE_INTRODUCTION, patternPreIntroduction, 1, null);
	}

	public void reset() {
		bpm = 100;
		
		state_menu = MENU_FADEIN;
		
		state=INSTRUCTION;
		
		alpha = 1.0f;
		cd_framesToFade = Menu.FRAME_FADEIN;
		color_fade = new Color(0, 0, 0, alpha);
		beatpatterns.clear();
	}
}
