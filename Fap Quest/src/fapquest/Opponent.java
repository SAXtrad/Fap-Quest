package fapquest;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.io.File;
import java.util.ArrayList;

import javax.swing.ImageIcon;
import javax.swing.JFrame;

public class Opponent {
	private JFrame frame;
	private OpponentHUD hud;
	private OpponentTitle title;
	private Image img_silhouette;
	private Image img_foreground;
	private int silhouette_height;
	private int size_path;
	private OpponentVisual visual;
	
	private int frame_width;
	private int frame_height;
	
	private String name;
	private String nameCut;
	private Character chara;
	
	private int speed;

	private ArrayList<String> opponent_names;
	private ArrayList<String> attack_imgs;
	
	private int stamina;
	private int max_stamina;
	
	private int lvl_opponent;
	private int max_lvl;
	private int max_img;
	
	public static final int FRAME_TRANSITION = 30; 
	public static final int FRAME_FADEIN = 30; 
	
	public Opponent(JFrame frame, Character chara){
		this.chara=chara;
		this.frame=frame;
		hud = new OpponentHUD(frame);
		title = new OpponentTitle(frame);
		visual = new OpponentVisual(frame);
		Image img_chara = new ImageIcon("data/character/character_1.png").getImage();
		silhouette_height = img_chara.getHeight(null);
		
		img_foreground = new ImageIcon("data/world/forest/foreground.png").getImage();
		
		size_path = 540;
		
		speed =4;
		
		frame_width=1080;
		frame_height=720;
		
		attack_imgs = new ArrayList<String>();
		opponent_names = new ArrayList<String>();
	}

	public void update(){
		frame_width = frame.getContentPane().getSize().width;
		frame_height = frame.getContentPane().getSize().height;
		
		hud.update();
		title.update();
		visual.update();
	}
	
	public void draw(Graphics g) {
		hud.draw(g);

		Graphics2D g2d = (Graphics2D) g;
		int x = frame_width/2 + size_path;
		int y = frame_height-img_foreground.getHeight(null)-silhouette_height;
		if(img_silhouette != null){
			g2d.drawImage(img_silhouette, x, y, (img_silhouette.getWidth(null)*silhouette_height)/img_silhouette.getHeight(null), silhouette_height, null);
		}

	}
	
	public void drawVisual(Graphics g) {		
		visual.draw(g);
		title.draw(g);
	}
	
	public void loadNextOpponent(){
		lvl_opponent = chara.getLevel() +(int) (Math.random() * 5) - 2;
		if(lvl_opponent < 1){
			lvl_opponent = 1;
		}else if(lvl_opponent > max_lvl){
			lvl_opponent = max_lvl;
		}
		
		max_stamina=8+4*lvl_opponent;
		stamina=max_stamina;
	}
	
	public void updateHUD(){
		hud.setName(nameCut+" Level "+lvl_opponent);
		hud.setMaxStamina(max_stamina);
		
		File opponent_repertory = new File("opponents/"+name+"/");
		String [] opponent_attacks; 

		opponent_attacks=opponent_repertory.list(); 
		boolean avatar = false;
		String avatarFileName = "";
		for(int i=0;i<opponent_attacks.length && !avatar;i++){ 
			if(opponent_attacks[i].toLowerCase().contains("avatar")){
				avatar = true;
				avatarFileName = opponent_attacks[i];
			}
		}
		
		Image img_avatar = null;
		if(avatar){
			img_avatar = new ImageIcon("opponents/"+name+"/"+avatarFileName).getImage();
		}else{
			img_avatar = new ImageIcon("data/skins/default/avatar_bydefault.png").getImage();
		}
		
		hud.setAvatar(img_avatar);
	}
	

	public void loadSilhouetteNextOpponent(int x_beat){
		if(opponent_names.isEmpty()){
			File opponent_repertories = new File("opponents/");
			String [] opponents; 

			opponents=opponent_repertories.list(); 
			for(int i=0;i<opponents.length;i++){ 
				File current_opponent = new File("opponents/"+opponents[i]);
				if(current_opponent.isDirectory()){
					boolean contain_intro = false;
					boolean contain_end = false;
					boolean contain_scenes = false;
					String [] opponent_content = current_opponent.list();
					for(int j=0;j<opponent_content.length;j++){
						if(opponent_content[j].matches("Introduction")){
							contain_intro = true;
						}else if(opponent_content[j].matches("End")){
							contain_end = true;
						}else if(opponent_content[j].matches("Scenes")){
							contain_scenes = true;
						}
					}

					if(contain_intro && contain_end && contain_scenes){
						opponent_names.add(opponents[i]);
					}
				}
			}
		}
		
		size_path=x_beat - frame_width/2;
		
		int random_opponent = (int) (Math.random() * opponent_names.size());

		name = opponent_names.get(random_opponent);
		
		setNameCut();

		File opponent_repertory = new File("opponents/"+name+"/");
		String [] opponent_attacks; 

		opponent_attacks=opponent_repertory.list(); 
		boolean silhouette = false;
		String silhouetteFileName = "";
		for(int i=0;i<opponent_attacks.length && !silhouette;i++){
			if(opponent_attacks[i].toLowerCase().contains("silhouette")){
				silhouette = true;
				silhouetteFileName = opponent_attacks[i];
			}
		}
		
		int nb_image = 0;
		for(int i=0;i<opponent_attacks.length;i++){
			//if(!(opponent_attacks[i].toLowerCase().matches("introduction") || opponent_attacks[i].toLowerCase().matches("end") || opponent_attacks[i].toLowerCase().contains("silhouette") || opponent_attacks[i].toLowerCase().contains("avatar"))){
				File opponent_attack_repertory = new File("opponents/"+name+"/Scenes");

				nb_image += opponent_attack_repertory.list().length;

			//}
		}
		
		max_img = nb_image;
		max_lvl = nb_image/2;
		
		//System.out.println("nb_image=" + nb_image + " max_lvl=" + max_lvl);

		if(img_silhouette != null){
			img_silhouette.flush();
		}

		if(silhouette){
			img_silhouette = new ImageIcon("opponents/"+name+"/"+silhouetteFileName).getImage();
		}else{
			img_silhouette = new ImageIcon("data/skins/default/silhouette_bydefault.png").getImage();
		}

		title.setOpponentName(nameCut);
		opponent_names.remove(random_opponent);
		attack_imgs.clear();
	}
	
	public void setNameCut(){
		if(name.contains(" (")){
			nameCut = name.substring(0, name.indexOf(" ("));
		}else if(name.contains("(")){
			nameCut = name.substring(0, name.indexOf("("));
		}else if(name.contains(" [")){
			nameCut = name.substring(0, name.indexOf(" ["));
		}else if(name.contains("[")){
			nameCut = name.substring(0, name.indexOf("["));
		}else if(name.contains(" {")){
			nameCut = name.substring(0, name.indexOf(" {"));
		}else if(name.contains("{")){
			nameCut = name.substring(0, name.indexOf("{"));
		}else{
			nameCut = name;
		}
	}
	
	public String getNameImgIntroduction(){
		File introduction_repertory = new File("opponents/"+name+"/Introduction/");
		String [] introduction_imgs; 

		introduction_imgs=introduction_repertory.list(); 

		int random_introduction_img = (int) (Math.random() * introduction_imgs.length);
		
		return "opponents/"+name+"/Introduction/"+introduction_imgs[random_introduction_img];
	}
	
	public int getSpeed(){
		return speed;
	}
	
	public ImageAttack getNameNewImage(){
		if(attack_imgs.isEmpty()){
			//System.out.println("opponents/"+name+"/scenes/");
			File current_attack_repertory = new File("opponents/"+name+"/Scenes");
			String [] opponent_attack_imgs; 

			opponent_attack_imgs=current_attack_repertory.list(); 
			for(int i=0;i<opponent_attack_imgs.length;i++){ 
				attack_imgs.add(opponent_attack_imgs[i]);

			}
		}
		int random_attack_img = (int) (Math.random() * attack_imgs.size());
		String name_random_attack_img = attack_imgs.get(random_attack_img);
		attack_imgs.remove(random_attack_img);
		
		String img_name = new String("opponents/"+name+"/Scenes/"+name_random_attack_img);
		
		int cost_in_stamina = 3 + (int)(Math.random()*2);
		
		if(stamina < 9){
			if(stamina/2 > 2){
				cost_in_stamina = stamina/2;
			}else{
				cost_in_stamina = stamina;
			}
		}else if(cost_in_stamina < max_stamina/max_img){
			cost_in_stamina = max_stamina/max_img;
		}
		
		ImageAttack img = new ImageAttack(img_name, cost_in_stamina);
		
		//System.out.println("stamina = "+stamina+" cost_in_stamina = "+cost_in_stamina);
		
		stamina-=cost_in_stamina;

		return img;
	}
	
	public String getNameEndImage(){
		File end_repertory = new File("opponents/"+name+"/End/");
		String [] end_imgs; 

		end_imgs=end_repertory.list(); 

		int random_end_img = (int) (Math.random() * end_imgs.length);
		
		return "opponents/"+name+"/End/"+end_imgs[random_end_img];
	}
	
	public void loadFadeOut(int frames){
		visual.loadFadeOut(frames);
		hud.loadFadeOut(frames);
	}
	
	public void loadFadeIn(){
		visual.loadFadeIn();
		hud.loadFadeIn();
	}
	
	public int getStamina(){
		return stamina;
	}
	
	public void move(){
		size_path-=speed;
	}

	public void setStaminaForHUD(float costInStamina, int frames) {
		hud.setStamina(costInStamina, frames);
	}

	public int getLevel() {
		return lvl_opponent;
	}
	
	public OpponentVisual getVisual(){
		return visual;
	}
	
	public OpponentTitle getTitle(){
		return title;
	}

	public void reset() {
		size_path = 540;
		
		speed =4;
		
		frame_width=1080;
		frame_height=720;

		attack_imgs = new ArrayList<String>();
		opponent_names = new ArrayList<String>();
		
		hud.reset();
		visual.reset();
	}
}
