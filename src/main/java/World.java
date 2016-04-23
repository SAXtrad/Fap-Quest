package fapquest;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JPanel;

public class World extends JPanel implements MouseMotionListener, MouseListener {
	private static final long serialVersionUID = 1L;

	private JFrame frame;
	private GameLoop game;
	private Beatbar beatbar;
	private Instruction instruction;
	
	private Character chara;
	private Opponent opponent;
	
	private Image img_background;
	private Image img_foreground;
	private Image img_beatbar;
	
	private int frame_width;
	private int frame_height;
	
	private int x_background;
	private int y_background;
	private float width_background;
	private float height_background;
	
	private int x_foreground;
	private int y_foreground;
	private int width_foreground;

	private int state;
	
	public static final int HIDDEN = 0; 
	public static final int FADEIN = 1; 
	public static final int SHOW = 2; 
	public static final int FADEOUT = 3;

	private int cd_framesToFade;
	private float alpha;
	private Color color_fade;
	
	private int next_page;
	
	public World(JFrame frame){
		this.frame=frame;
		img_background = new ImageIcon("data/world/forest/background.png").getImage();
		img_foreground = new ImageIcon("data/world/forest/foreground.png").getImage();
		img_beatbar = new ImageIcon("data/skins/default/beatbar_frame_left.png").getImage();
		
		width_foreground = (img_foreground.getWidth(null)*4)/5;
		
		chara = new Character(frame);
		opponent = new Opponent(frame, chara);

		state = FADEIN;
		
		alpha = 1.0f;
		cd_framesToFade = Menu.FRAME_FADEIN;
		color_fade = new Color(0, 0, 0, alpha);
		
		instruction = new Instruction(frame, this);
		
		addMouseMotionListener(this);
		addMouseListener(this);
	}
	
	public void update(){
		frame_width = frame.getContentPane().getSize().width;
		frame_height = frame.getContentPane().getSize().height;
		
		setBounds(0, 0, frame_width, frame_height - img_beatbar.getHeight(null));
		
		height_background = (frame_height*3)/4;
		width_background = img_background.getWidth(null)*(height_background/(float)img_background.getHeight(null));
		
		y_foreground = frame_height - img_beatbar.getHeight(null) - img_foreground.getHeight(null);
		y_background = (frame_height - img_beatbar.getHeight(null) - (int)height_background)/2;
		
		opponent.update();
		instruction.update();
		chara.update();
		
		if(state == FADEIN){
			cd_framesToFade--;
			if(cd_framesToFade <= 0){
				cd_framesToFade = 0;
				state = SHOW;
				alpha = 0;
			}else{
				alpha = (float)cd_framesToFade/(float)Menu.FRAME_FADEIN;
			}
			color_fade = new Color(0, 0, 0, alpha);
		}else if(state == FADEOUT){
			cd_framesToFade++;
			if(cd_framesToFade >= Menu.FRAME_FADEOUT){
				cd_framesToFade = Menu.FRAME_FADEIN;
				state = HIDDEN;
				alpha = 1.0f;
				if(next_page == MenuButton.TYPE_MENU){
					game.loadMenu();
				}
			}else{
				alpha = (float)cd_framesToFade/(float)Menu.FRAME_FADEOUT;
			}
			color_fade = new Color(0, 0, 0, alpha);
		}
	}
	
	private void doDrawing(Graphics g) {
		if(state != HIDDEN){
			Graphics2D g2d = (Graphics2D) g;
			for(int i=0; x_background+ i*width_background < frame_width ; i++){
				g2d.drawImage(img_background, x_background+ i*(int)width_background, y_background, (int)width_background, (int)height_background, null);
			}

			chara.draw(g);
			opponent.draw(g);

			for(int i=0; x_foreground + i*width_foreground < frame_width + width_foreground;i++){
				int x = x_foreground + i*width_foreground - img_foreground.getWidth(null)/10;
				g.drawImage(img_foreground, x, y_foreground, null);
			}

			opponent.drawVisual(g);
			
			instruction.draw(g);
			
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
	
	public void load(int type){
		beatbar.load(MenuButton.TYPE_MENU);
		next_page = type;
		state = FADEOUT;
	}
	
	public Opponent getOpponent(){
		return opponent;
	}
	
	public void setBeatbar(Beatbar beatbar){
		this.beatbar=beatbar;
		chara.setBeatbar(beatbar);
	}
	
	public void move(){
		x_foreground = (x_foreground - 5)%width_foreground;
		x_background = (int) ((x_background - 1)%width_background);
		
		opponent.move();
	}

	public Character getCharacter() {
		return chara;
	}
	
	public void start() {
		beatbar.start();
	}
	
	@Override
	public void mousePressed(MouseEvent arg0) {
		instruction.getButtonClose().press(arg0.getPoint().getX(), arg0.getPoint().getY());
		chara.getButtonICame().press(arg0.getPoint().getX(), arg0.getPoint().getY());
	}

	@Override
	public void mouseReleased(MouseEvent arg0) {
		if(instruction.getButtonClose().unpress(arg0.getPoint().getX(), arg0.getPoint().getY())){
			instruction.fadeOut();
		}
		if(chara.getButtonICame().unpress(arg0.getPoint().getX(), arg0.getPoint().getY())){
			load(MenuButton.TYPE_MENU);
		}
	}

	@Override
	public void mouseDragged(MouseEvent arg0) {
		instruction.getButtonClose().over(arg0.getPoint().getX(), arg0.getPoint().getY());
		chara.getButtonICame().over(arg0.getPoint().getX(), arg0.getPoint().getY());
	}

	@Override
	public void mouseMoved(MouseEvent arg0) {
		instruction.getButtonClose().over(arg0.getPoint().getX(), arg0.getPoint().getY());
		chara.getButtonICame().over(arg0.getPoint().getX(), arg0.getPoint().getY());
	}

	@Override
	public void mouseClicked(MouseEvent arg0) {
		
	}

	@Override
	public void mouseEntered(MouseEvent arg0) {
		
	}

	@Override
	public void mouseExited(MouseEvent arg0) {
		
	}

	public void setGameLoop(GameLoop gameLoop) {
		this.game = gameLoop;
	}

	public void reset() {
		width_foreground = (img_foreground.getWidth(null)*4)/5;
		
		chara.reset();
		opponent.reset();

		state = FADEIN;
		
		alpha = 1.0f;
		cd_framesToFade = Menu.FRAME_FADEIN;
		color_fade = new Color(0, 0, 0, alpha);
		
		instruction.reset();
		
		chara.getButtonICame().unpress();
	}
}
