package mainPackage;
import java.awt.Color;

import javax.swing.*;

public class Main {

	public static void main(String s[]) {
		
		JFrame frame = new JFrame("Fap Quest");
		
		World world = new World(frame);
		world.setBackground(Color.BLACK);
		
		Beatbar beatbar = new Beatbar(frame, world);
		
		world.setBeatbar(beatbar);

		Menu menu = new Menu(frame);		
		
		GameLoop gameLoop = new GameLoop(frame, beatbar, world, menu);
		
		menu.setGameLoop(gameLoop);
		world.setGameLoop(gameLoop);
		
		gameLoop.setState(GameLoop.MENU);
		
		gameLoop.start();
		
		frame.add(menu);
		//frame.add(world);
		//frame.add(beatbar);

		frame.setSize(1080, 720);
		frame.setLocationRelativeTo(null);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setVisible(true);
	}

}
