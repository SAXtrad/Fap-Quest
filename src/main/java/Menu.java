package fapquest;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontFormatException;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JPanel;

public class Menu extends JPanel implements MouseMotionListener, MouseListener {
	private static final long serialVersionUID = 1L;
	private JFrame frame;
	private GameLoop game;
	private Image img_background;
	private Image img_logo;
	private Image img_button_unpressed;
	private Image img_button_over;
	private Image img_button_pressed;
	
	private ArrayList<MenuButton> buttons;
	private MenuRecord record;
	
	private int frame_width;
	private int frame_height;
	
	private int x_logo;
	private int y_logo;
	private int height_logo;
	private int width_logo;
	
	private int x_buttons;
	private int y_buttons;
	private int height_buttons;
	private int width_buttons;
	
	private Font font;
	
	private int state;
	
	public static final int HIDDEN = 0; 
	public static final int FADEIN = 1; 
	public static final int SHOW = 2; 
	public static final int FADEOUT = 3; 
	
	public static final int FRAME_FADEOUT = 30; 
	public static final int FRAME_FADEIN = 30; 
	private int cd_framesToFade;
	private float alpha;
	private Color color_fade;
	
	private int next_page;
	
	public Menu(JFrame frame){
		this.frame=frame;
		img_background = new ImageIcon("data/menu/menu_background.png").getImage();
		img_logo = new ImageIcon("data/menu/menu_logo.png").getImage();
		img_button_unpressed = new ImageIcon("data/menu/menu_button_unpressed.png").getImage();
		img_button_over = new ImageIcon("data/menu/menu_button_over.png").getImage();
		img_button_pressed = new ImageIcon("data/menu/menu_button_pressed.png").getImage();
		
		try {
			font = Font.createFont(Font.TRUETYPE_FONT, new File("data/skins/default/JUNGLEFEVER.TTF"));
		} catch (FontFormatException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		buttons = new ArrayList<MenuButton>();
		
		MenuButton button = new MenuButton(img_button_unpressed, img_button_over, img_button_pressed, "Play", MenuButton.TYPE_PLAY, font);
		buttons.add(button);
		//button = new MenuButton(img_button_unpressed, img_button_over, img_button_pressed, "Options", MenuButton.TYPE_OPTIONS, font);
		//buttons.add(button);
		button = new MenuButton(img_button_unpressed, img_button_over, img_button_pressed, "Quit", MenuButton.TYPE_QUIT, font);
		buttons.add(button);
		
		record = new MenuRecord(frame, font);
		
		addMouseMotionListener(this);
		addMouseListener(this);
		
		state = FADEIN;
		
		alpha = 1.0f;
		cd_framesToFade = FRAME_FADEIN;
		color_fade = new Color(0, 0, 0, alpha);
	}
	
	public void update(){
		frame_width = frame.getContentPane().getSize().width;
		frame_height = frame.getContentPane().getSize().height;
		
		//Set Logo
		int max_width = (frame_width - frame_width/4)/2;
		int max_height = frame_height - frame_height/4;
		
		if(img_logo.getWidth(null) < max_width && img_logo.getHeight(null) < max_height){
			width_logo = img_logo.getWidth(null);
			height_logo = img_logo.getHeight(null);
		}else if(img_logo.getWidth(null) >= max_width && (img_logo.getHeight(null)*max_width)/img_logo.getWidth(null) < max_height){
			width_logo = max_width;
			height_logo = (img_logo.getHeight(null)*max_width)/img_logo.getWidth(null);
		}else{
			width_logo = (img_logo.getWidth(null)*max_height)/img_logo.getHeight(null);
			height_logo = max_height;
		}
		
		x_logo = (max_width - width_logo)/2 + frame_width/12;
		y_logo = (max_height - height_logo)/2 + frame_height/8;
		
		if(img_button_unpressed.getWidth(null) < max_width && img_button_unpressed.getHeight(null)*buttons.size() < max_height){
			width_buttons = img_button_unpressed.getWidth(null);
			height_buttons = img_button_unpressed.getHeight(null);
		}else if(img_button_unpressed.getWidth(null) >= max_width && (img_button_unpressed.getHeight(null)*buttons.size()*max_width)/img_button_unpressed.getWidth(null) < max_height){
			width_buttons = max_width;
			height_buttons = (img_button_unpressed.getHeight(null)*max_width)/img_button_unpressed.getWidth(null);
		}else{
			width_buttons = (img_button_unpressed.getWidth(null)*max_height)/(img_button_unpressed.getHeight(null)*buttons.size());
			height_buttons = max_height/buttons.size();
		}
		
		x_buttons = (max_width - width_buttons)/2 + (frame_width*4)/7;
		y_buttons = (max_height - height_buttons*buttons.size())/2 + frame_height/8;
		
		for(int i=0 ; i < buttons.size() ; i++){
			buttons.get(i).update(x_buttons, y_buttons + height_buttons*i, width_buttons, height_buttons);
		}
		
		record.update();
		
		if(state == FADEIN){
			cd_framesToFade--;
			if(cd_framesToFade <= 0){
				cd_framesToFade = 0;
				state = SHOW;
				alpha = 0;
			}else{
				alpha = (float)cd_framesToFade/(float)FRAME_FADEIN;
			}
			color_fade = new Color(0, 0, 0, alpha);
		}else if(state == FADEOUT){
			cd_framesToFade++;
			if(cd_framesToFade >= FRAME_FADEOUT){
				cd_framesToFade = FRAME_FADEIN;
				state = HIDDEN;
				alpha = 1.0f;
				if(next_page == MenuButton.TYPE_PLAY){
					game.loadPlay();
				}
			}else{
				alpha = (float)cd_framesToFade/(float)FRAME_FADEOUT;
			}
			color_fade = new Color(0, 0, 0, alpha);
		}
	}

	private void doDrawing(Graphics g) {
		if(state != HIDDEN){
			Graphics2D g2d = (Graphics2D) g;

			g2d.drawImage(img_background, 0, 0, frame_width, frame_height, null);
			g2d.drawImage(img_logo, x_logo, y_logo, width_logo, height_logo, null);

			for(int i=0 ; i < buttons.size() ; i++){
				buttons.get(i).draw(g2d, 35f);
			}
			
			record.draw(g2d);

			if(state == FADEIN || state == FADEOUT){
				g2d.setColor(color_fade);
				g2d.fillRect(0, 0, frame_width, frame_height);
			}

			g2d.dispose();
		}
	}
	
	@Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        doDrawing(g);
    }

	@Override
	public void mouseMoved(MouseEvent arg0) {
		for(int i=0 ; i < buttons.size() ; i++){
			buttons.get(i).over(arg0.getPoint().getX(), arg0.getPoint().getY());
		}
	}

	@Override
	public void mousePressed(MouseEvent arg0) {
		// TODO Auto-generated method stub
		for(int i=0 ; i < buttons.size() ; i++){
			buttons.get(i).press(arg0.getPoint().getX(), arg0.getPoint().getY());
		}
	}

	@Override
	public void mouseReleased(MouseEvent arg0) {
		for(int i=0 ; i < buttons.size() ; i++){
			if(state == SHOW || state == FADEIN){
				if(buttons.get(i).unpress(arg0.getPoint().getX(), arg0.getPoint().getY())){
					if(buttons.get(i).getType() == MenuButton.TYPE_PLAY){
						load(MenuButton.TYPE_PLAY);
					}else if(buttons.get(i).getType() == MenuButton.TYPE_OPTIONS){

					}else if(buttons.get(i).getType() == MenuButton.TYPE_QUIT){
						System.exit(0);
					}
				}
			}
		}
	}
	
	public void load(int type){
		next_page = type;
		state = FADEOUT;
	}
	
	@Override
	public void mouseClicked(MouseEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseEntered(MouseEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseExited(MouseEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseDragged(MouseEvent arg0) {
		for(int i=0 ; i < buttons.size() ; i++){
			buttons.get(i).over(arg0.getPoint().getX(), arg0.getPoint().getY());
		}
	}
	
	public void setGameLoop(GameLoop game){
		this.game=game;
	}

	public void fadeIn() {
		state = FADEIN;
		for(int i=0 ; i < buttons.size() ; i++){
			buttons.get(i).unpress();
		}
	}
	
	public int getRecord(){
		return record.getRecord();
	}
	
	public void setRecord(int new_record){
		record.setRecord(new_record);
	}
}
