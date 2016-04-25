package fapquest;
import java.awt.AlphaComposite;
import java.awt.Composite;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.RenderingHints;
import java.util.ArrayList;

import javax.swing.ImageIcon;
import javax.swing.JFrame;

public class OpponentVisual {
	
	private Image img_frame_corner_top_right;
	private Image img_frame_corner_top_left;
	private Image img_frame_corner_bottom_right;
	private Image img_frame_corner_bottom_left;
	private Image img_frame_bottom;
	private Image img_frame_top;
	private Image img_frame_left;
	private Image img_frame_right;
	
	private Image img_frame_avatar;
	private Image img_frame_beatbar;
	
	private Image img_visual;
	
	private int x;
	private int y;
	private int width;
	private int height;
	private int frame_decal;
	
	private int x_old;
	private int y_old;
	private int width_old;
	private int height_old;
	private float alpha_old;
	private Image img_visual_old;
	private int img_type_old;
	private ImageFrame[] gif_frames_old;
	private ArrayList<Integer> frames_pattern_old;
	private int current_beat_frames_old;
	private int cd_current_beat_frames_old;
	private int nb_action_old;
	private int cd_nb_action_old;
	
	private int max_width;
	private int max_height;
	private int top_decal;
	
	private int state;
	private int cd_framesToFadeIn;
	private int framesToFadeIn;
	private int cd_framesToFadeOut;
	private int framesToFadeOut;
	private float alpha;
	
	public static final int HIDDEN = 0; 
	public static final int FADEIN = 1; 
	public static final int TRANSITION = 2; 
	public static final int FADEOUT = 4; 
	public static final int SHOW = 3; 
	
	private ImageFrame[] gif_frames;
	private ArrayList<Integer> frames_pattern;
	
	private int current_beat_frames;
	private int cd_current_beat_frames;
	private int nb_action;
	private int cd_nb_action;
	
	private int img_type;
	public static final int IMG_TYPE_IMAGE = 0;
	public static final int IMG_TYPE_GIF = 1; 
	
	private JFrame frame;
	
	private int comp_rule;
	private Composite comp;
	private Composite comp_transition;
	private Composite comp_noAlpha;
	
	public OpponentVisual(JFrame frame){
		this.frame=frame;
		
		img_frame_corner_top_right = new ImageIcon("data/skins/default/hud_visual_corner_top_right.png").getImage();
		img_frame_corner_top_left = new ImageIcon("data/skins/default/hud_visual_corner_top_left.png").getImage();
		img_frame_corner_bottom_right = new ImageIcon("data/skins/default/hud_visual_corner_bottom_right.png").getImage();
		img_frame_corner_bottom_left = new ImageIcon("data/skins/default/hud_visual_corner_bottom_left.png").getImage();
		img_frame_bottom = new ImageIcon("data/skins/default/hud_visual_bottom.png").getImage();
		img_frame_top = new ImageIcon("data/skins/default/hud_visual_top.png").getImage();
		img_frame_left = new ImageIcon("data/skins/default/hud_visual_left.png").getImage();
		img_frame_right = new ImageIcon("data/skins/default/hud_visual_right.png").getImage();
		
		img_frame_avatar = new ImageIcon("data/skins/default/hud_frame.png").getImage();
		img_frame_beatbar = new ImageIcon("data/skins/default/beatbar_frame_left.png").getImage();
		
		frame_decal=15;
		
		top_decal = img_frame_avatar.getHeight(null) + frame_decal*2;
		
		state = HIDDEN;
		alpha=0.0f;
		alpha_old=1.0f;

		comp_rule = AlphaComposite.SRC_OVER;
		comp = AlphaComposite.getInstance(comp_rule , alpha);
		comp_transition = AlphaComposite.getInstance(comp_rule , alpha_old);
		comp_noAlpha = AlphaComposite.getInstance(comp_rule , 1.0f);
	}
	
	public void update(){
		max_width = frame.getContentPane().getSize().width - frame_decal*2;
		max_height = frame.getContentPane().getSize().height - frame_decal*5 - img_frame_avatar.getHeight(null) - img_frame_beatbar.getHeight(null);
		
		if(img_visual != null && img_type == IMG_TYPE_IMAGE){
			//Update Position
			if(img_visual.getWidth(null) < max_width && img_visual.getHeight(null) < max_height){
				width = img_visual.getWidth(null);
				height = img_visual.getHeight(null);
			}else if(img_visual.getWidth(null) >= max_width && (img_visual.getHeight(null)*max_width)/img_visual.getWidth(null) < max_height){
				width = max_width;
				height = (img_visual.getHeight(null)*max_width)/img_visual.getWidth(null);
			}else{
				width = (img_visual.getWidth(null)*max_height)/img_visual.getHeight(null);
				height = max_height;
			}
		}else if(gif_frames != null && img_type == IMG_TYPE_GIF){
			//Update Position
			ImageFrame first_img = this.gif_frames[0];
			
			max_width = frame.getContentPane().getSize().width - frame_decal*2;
			max_height = frame.getContentPane().getSize().height - frame_decal*5 - img_frame_avatar.getHeight(null) - img_frame_beatbar.getHeight(null);

			if(first_img.getWidth() < max_width && first_img.getHeight() < max_height){
				width = first_img.getWidth();
				height = first_img.getHeight();
			}else if(first_img.getWidth() >= max_width && (first_img.getHeight()*max_width)/first_img.getWidth() < max_height){
				width = max_width;
				height = (first_img.getHeight()*max_width)/first_img.getWidth();
			}else{
				width = (first_img.getWidth()*max_height)/first_img.getHeight();
				height = max_height;
			}

			x = (max_width - width)/2;
			y = top_decal + (max_height - height)/2;
			
			//Update GIF
			cd_current_beat_frames++;
			if(cd_current_beat_frames >= frames_pattern.get(current_beat_frames).intValue()){
				cd_current_beat_frames = 0;
				current_beat_frames++;
				cd_nb_action++;
				if(cd_nb_action >= nb_action){
					cd_nb_action=0;
				}
				if(current_beat_frames >= frames_pattern.size()){
					current_beat_frames=0;
				}
			}
		}

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
		}else if(state == TRANSITION){
			cd_framesToFadeIn++;
			alpha = (float)cd_framesToFadeIn/(float)framesToFadeIn;
			if(alpha > 1.0f){
				alpha=1.0f;
				state=SHOW;
			}
			cd_framesToFadeOut--;
			alpha_old = (float)cd_framesToFadeOut/(float)framesToFadeOut;
			if(alpha_old < 0.0f){
				alpha_old=0;
			}
			
			comp = AlphaComposite.getInstance(comp_rule , alpha);
			comp_transition = AlphaComposite.getInstance(comp_rule , alpha_old);
			
			if(gif_frames_old != null && img_type_old == IMG_TYPE_GIF){
				//Update GIF
				cd_current_beat_frames_old++;
				if(cd_current_beat_frames_old >= frames_pattern_old.get(current_beat_frames_old).intValue()){
					cd_current_beat_frames_old = 0;
					current_beat_frames_old++;
					cd_nb_action_old++;
					if(cd_nb_action_old >= nb_action_old){
						cd_nb_action_old=0;
					}
					if(current_beat_frames_old >= frames_pattern_old.size()){
						current_beat_frames_old=0;
					}
				}
			}
		}
		
		x = (max_width - width)/2 + frame_decal;
		y = top_decal + (max_height - height)/2;
	}
	
	public void draw(Graphics g) {
		if(state != HIDDEN){
			Graphics2D g2d = (Graphics2D) g;

			if(state == TRANSITION){
				g2d.setComposite(comp_transition);

				if(img_visual_old != null && img_type_old == IMG_TYPE_IMAGE){
					g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION,RenderingHints.VALUE_INTERPOLATION_BILINEAR);
					g2d.drawImage(img_visual_old, x_old, y_old, width_old, height_old, null);
				}else if(gif_frames_old != null && img_type_old == IMG_TYPE_GIF && frames_pattern_old!=null){
					g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION,RenderingHints.VALUE_INTERPOLATION_BILINEAR);

					int index_img_gif=(cd_current_beat_frames_old*(gif_frames_old.length/nb_action_old))/frames_pattern_old.get(current_beat_frames_old).intValue();

					if(index_img_gif >= gif_frames_old.length){
						index_img_gif = gif_frames_old.length - 1;
					}
					g2d.drawImage(gif_frames_old[index_img_gif+cd_nb_action_old*(gif_frames_old.length/nb_action_old)].getImage(), x_old, y_old, width_old, height_old, null);
				}

				//Draw Frame Corner
				g.drawImage(img_frame_corner_top_left, x_old - frame_decal, y_old - frame_decal, null);
				g.drawImage(img_frame_corner_top_right, x_old + width_old - img_frame_corner_top_right.getWidth(null) + frame_decal, y_old - frame_decal, null);
				g.drawImage(img_frame_corner_bottom_right, x_old + width_old - img_frame_corner_bottom_right.getWidth(null) + frame_decal, y_old + height_old - img_frame_corner_bottom_right.getHeight(null) + frame_decal, null);
				g.drawImage(img_frame_corner_bottom_left, x_old - frame_decal, y_old + height_old - img_frame_corner_bottom_left.getHeight(null) + frame_decal, null);

				//Draw Frame Border
				g2d.drawImage(img_frame_bottom, x_old - frame_decal + img_frame_corner_bottom_left.getWidth(null), y_old + height_old + frame_decal - img_frame_bottom.getHeight(null), width_old - img_frame_corner_bottom_right.getWidth(null) - img_frame_corner_bottom_left.getWidth(null) + frame_decal*2, img_frame_bottom.getHeight(null), null);
				g2d.drawImage(img_frame_top, x_old - frame_decal + img_frame_corner_top_left.getWidth(null), y_old - frame_decal, width_old - img_frame_corner_top_right.getWidth(null) - img_frame_corner_top_left.getWidth(null) + frame_decal*2, img_frame_top.getHeight(null), null);
				g2d.drawImage(img_frame_left, x_old - frame_decal, y_old - frame_decal + img_frame_corner_top_left.getHeight(null), img_frame_left.getWidth(null), height_old + frame_decal*2 - img_frame_corner_top_left.getHeight(null) - img_frame_corner_bottom_left.getHeight(null) , null);
				g2d.drawImage(img_frame_right, x_old + frame_decal + width_old - img_frame_right.getWidth(null), y_old - frame_decal + img_frame_corner_top_right.getHeight(null), img_frame_right.getWidth(null), height_old + frame_decal*2 - img_frame_corner_top_right.getHeight(null) - img_frame_corner_bottom_right.getHeight(null) , null);
			}

			g2d.setComposite(comp);

			if(img_visual != null && img_type == IMG_TYPE_IMAGE){
				g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION,RenderingHints.VALUE_INTERPOLATION_BILINEAR);
				g2d.drawImage(img_visual, x, y, width, height, null);
			}else if(gif_frames != null && img_type == IMG_TYPE_GIF && frames_pattern!=null){
				g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION,RenderingHints.VALUE_INTERPOLATION_BILINEAR);

				int index_img_gif=(cd_current_beat_frames*(gif_frames.length/nb_action))/frames_pattern.get(current_beat_frames).intValue();

				g2d.drawImage(gif_frames[index_img_gif+cd_nb_action*(gif_frames.length/nb_action)].getImage(), x, y, width, height, null);
			}

			//Draw Frame Corner
			g.drawImage(img_frame_corner_top_left, x - frame_decal, y - frame_decal, null);
			g.drawImage(img_frame_corner_top_right, x + width - img_frame_corner_top_right.getWidth(null) + frame_decal, y - frame_decal, null);
			g.drawImage(img_frame_corner_bottom_right, x + width - img_frame_corner_bottom_right.getWidth(null) + frame_decal, y + height - img_frame_corner_bottom_right.getHeight(null) + frame_decal, null);
			g.drawImage(img_frame_corner_bottom_left, x - frame_decal, y + height - img_frame_corner_bottom_left.getHeight(null) + frame_decal, null);

			//Draw Frame Border
			g2d.drawImage(img_frame_bottom, x - frame_decal + img_frame_corner_bottom_left.getWidth(null), y + height + frame_decal - img_frame_bottom.getHeight(null), width - img_frame_corner_bottom_right.getWidth(null) - img_frame_corner_bottom_left.getWidth(null) + frame_decal*2, img_frame_bottom.getHeight(null), null);
			g2d.drawImage(img_frame_top, x - frame_decal + img_frame_corner_top_left.getWidth(null), y - frame_decal, width - img_frame_corner_top_right.getWidth(null) - img_frame_corner_top_left.getWidth(null) + frame_decal*2, img_frame_top.getHeight(null), null);
			g2d.drawImage(img_frame_left, x - frame_decal, y - frame_decal + img_frame_corner_top_left.getHeight(null), img_frame_left.getWidth(null), height + frame_decal*2 - img_frame_corner_top_left.getHeight(null) - img_frame_corner_bottom_left.getHeight(null) , null);
			g2d.drawImage(img_frame_right, x + frame_decal + width - img_frame_right.getWidth(null), y - frame_decal + img_frame_corner_top_right.getHeight(null), img_frame_right.getWidth(null), height + frame_decal*2 - img_frame_corner_top_right.getHeight(null) - img_frame_corner_bottom_right.getHeight(null) , null);

			g2d.setComposite(comp_noAlpha);
		}
	}
	
	public void loadVisual(Image img){
		if(img != null){
			img_type = IMG_TYPE_IMAGE;
			this.img_visual=img;

			max_width = frame.getContentPane().getSize().width - frame_decal*2;
			max_height = frame.getContentPane().getSize().height - frame_decal*5 - img_frame_avatar.getHeight(null) - img_frame_beatbar.getHeight(null);

			if(img_visual.getWidth(null) < max_width && img_visual.getHeight(null) < max_height){
				width = img_visual.getWidth(null);
				height = img_visual.getHeight(null);
			}else if(img_visual.getWidth(null) >= max_width && (img_visual.getHeight(null)*max_width)/img_visual.getWidth(null) < max_height){
				width = max_width;
				height = (img_visual.getHeight(null)*max_width)/img_visual.getWidth(null);
			}else{
				width = (img_visual.getWidth(null)*max_height)/img_visual.getHeight(null);
				height = max_height;
			}

			x = (max_width - width)/2 + frame_decal;
			y = top_decal + (max_height - height)/2;
		}
	}

	public void loadFadeOut(int frames) {
		state = FADEOUT;
		framesToFadeOut = frames;
		cd_framesToFadeOut = framesToFadeOut;
		alpha = 1.0f;
		
		comp = AlphaComposite.getInstance(comp_rule , alpha);
	}
	
	public void loadFadeIn() {
		state = FADEIN;
		framesToFadeIn = Opponent.FRAME_FADEIN;
		cd_framesToFadeIn = 0;
		alpha = 0.0f;
		
		comp = AlphaComposite.getInstance(comp_rule , alpha);
	}
	
	public void loadTransition(Image img) {
		state = TRANSITION;
		framesToFadeOut = Opponent.FRAME_TRANSITION;
		cd_framesToFadeOut = framesToFadeOut;
		framesToFadeIn = Opponent.FRAME_TRANSITION;
		cd_framesToFadeIn = 0;

		width_old=width;
		height_old=height;
		x_old=x;
		y_old=y;
		alpha_old=alpha;
		
		img_type_old = img_type;
		
		if(img_type_old == IMG_TYPE_IMAGE){
			img_visual_old = img_visual;
		}else if(img_type_old == IMG_TYPE_GIF){
			gif_frames_old=this.gif_frames;
			frames_pattern_old = frames_pattern;
			current_beat_frames_old=current_beat_frames;
			cd_current_beat_frames_old=cd_current_beat_frames;
			nb_action_old=this.nb_action;
			cd_nb_action_old = cd_nb_action;
		}
		
		alpha = 0;
		
		comp = AlphaComposite.getInstance(comp_rule , alpha);
		comp_transition = AlphaComposite.getInstance(comp_rule , alpha_old);
		
		loadVisual(img);
	}
	
	public void loadTransition(ImageFrame[] gif_frames, ArrayList<Integer> frames_pattern, int nb_action) {
		state = TRANSITION;
		framesToFadeOut = Opponent.FRAME_TRANSITION;
		cd_framesToFadeOut = framesToFadeOut;
		framesToFadeIn = Opponent.FRAME_TRANSITION;
		cd_framesToFadeIn = 0;

		width_old=width;
		height_old=height;
		x_old=x;
		y_old=y;
		alpha_old=alpha;
		img_type_old = img_type;
		
		if(img_type_old == IMG_TYPE_IMAGE){
			img_visual_old = img_visual;
		}else if(img_type_old == IMG_TYPE_GIF){
			gif_frames_old=this.gif_frames;
			frames_pattern_old = this.frames_pattern;
			current_beat_frames_old=current_beat_frames;
			cd_current_beat_frames_old=cd_current_beat_frames;
			nb_action_old=this.nb_action;
			cd_nb_action_old = cd_nb_action;
		}
		
		alpha = 0;
		
		comp = AlphaComposite.getInstance(comp_rule , alpha);
		comp_transition = AlphaComposite.getInstance(comp_rule , alpha_old);
		
		loadGIF(gif_frames, frames_pattern, nb_action);
	}
	
	public void loadGIF(ImageFrame[] gif_frames, ArrayList<Integer> frames_pattern, int nb_action){
		img_type = IMG_TYPE_GIF;
		this.gif_frames = gif_frames;
		this.frames_pattern=frames_pattern;
		this.nb_action=nb_action;

		if(this.gif_frames != null){
			ImageFrame first_img = this.gif_frames[0];
			
			max_width = frame.getContentPane().getSize().width - frame_decal*2;
			max_height = frame.getContentPane().getSize().height - frame_decal*5 - img_frame_avatar.getHeight(null) - img_frame_beatbar.getHeight(null);

			if(first_img.getWidth() < max_width && first_img.getHeight() < max_height){
				width = first_img.getWidth();
				height = first_img.getHeight();
			}else if(first_img.getWidth() >= max_width && (first_img.getHeight()*max_width)/first_img.getWidth() < max_height){
				width = max_width;
				height = (first_img.getHeight()*max_width)/first_img.getWidth();
			}else{
				width = (first_img.getWidth()*max_height)/first_img.getHeight();
				height = max_height;
			}

			x = (max_width - width)/2 + frame_decal;
			y = top_decal + (max_height - height)/2;
			
			current_beat_frames = 0;
			cd_current_beat_frames = 0;
			
			cd_nb_action=0;
		}
	}

	public void reset() {
		state = HIDDEN;
		alpha=0.0f;
		alpha_old=1.0f;

		comp = AlphaComposite.getInstance(comp_rule , alpha);
		comp_transition = AlphaComposite.getInstance(comp_rule , alpha_old);
		
		gif_frames = null;
		img_visual = null;
	}
}
