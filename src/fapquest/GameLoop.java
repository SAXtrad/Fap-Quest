package fapquest;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.Semaphore;

import javax.swing.JFrame;

public class GameLoop extends Thread{
	private JFrame frame;
	private boolean running;
	private Beatbar beatbar;
	private World world;
	private Menu menu;
	
	private int state;
	
	public static final int MENU = 0;
	public static final int GAME = 1;
	
	public GameLoop (JFrame frame, Beatbar beatbar, World world, Menu menu){
		this.frame=frame;
		running=true;
		this.beatbar=beatbar;
		this.world = world;
		this.menu = menu;
	}
	
	@Override
	public void run() {

		final Semaphore mutexRefresh = new Semaphore(0);
        final Semaphore mutexRefreshing = new Semaphore(1);
        int refresh = 0;

        Timer timRefresh = new Timer();
        timRefresh.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                if(mutexRefreshing.tryAcquire()) {
                    mutexRefreshing.release();
                    mutexRefresh.release();
                }
            }
        }, 0, 1000/50);

        // The timer is started and configured for 50fps
        Date startDate = new Date();
        while(running) { // Refreshing loop
        	
            try {
				mutexRefresh.acquire();
				mutexRefreshing.acquire();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

            // Refresh 
            refresh += 1;
            
            update();
            draw();

            if(refresh % 50 == 0) {
                Date endDate = new Date();
                //System.out.println(String.valueOf(50.0*1000/(endDate.getTime() - startDate.getTime())) + " fps.");
                startDate = new Date();
            }

            mutexRefreshing.release();
        }
	}
	
	private synchronized void update(){
		if(state == MENU){
			menu.update();
		}else if(state == GAME){
			world.update();
			beatbar.update();
		}
	}

	private synchronized void draw(){
		if(state == MENU){
			menu.repaint();
		}else if(state == GAME){
			world.repaint();
			beatbar.repaint();
		}
	}

	public void setState(int state) {
		this.state=state;
	}

	public synchronized void loadPlay() {
		frame.getContentPane().remove(menu);

		world.reset();
		beatbar.reset();

		frame.add(world);
		frame.add(beatbar);
		
		state = GAME;
	}
	
	public synchronized void loadMenu() {
		frame.getContentPane().remove(world);
		frame.getContentPane().remove(beatbar);
		
		int chara_lvl = world.getCharacter().getLevel();
		if(chara_lvl > menu.getRecord()){
			menu.setRecord(chara_lvl);
		}
		
		frame.add(menu);
		menu.setBounds(0, 0, frame.getContentPane().getSize().width, frame.getContentPane().getSize().height);

		menu.fadeIn();
		
		state = MENU;
	}
}
