package fapquest;
import java.awt.Color;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.logging.FileHandler;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

import javax.swing.*;

public class Main {

	static Logger logger = Logger.getLogger("fapquest");
	
	public static void main(String s[]) {
		// Set up logger
		try {
			FileHandler fileHandler = new FileHandler("fapquest.log");
			fileHandler.setFormatter(new SimpleFormatter());
			logger.addHandler(fileHandler);
		} catch (SecurityException | IOException e) {
			e.printStackTrace();
		}

		JFrame frame = new JFrame("Fap Quest");
		
		Thread.setDefaultUncaughtExceptionHandler(new ExceptionHandler(logger, frame));
		
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
