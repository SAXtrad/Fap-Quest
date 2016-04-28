package fapquest;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.logging.Logger;

import javax.swing.JFrame;
import javax.swing.JOptionPane;

public class ExceptionHandler implements Thread.UncaughtExceptionHandler {
	Logger logger;
	JFrame frame;
	public ExceptionHandler(Logger logger, JFrame frame) {
		this.logger = logger;
		this.frame = frame;
	}
	
	@Override
	public void uncaughtException(Thread t, Throwable e) {
		String stackTrace = null;
		try (StringWriter sw = new StringWriter()) {
			try (PrintWriter pw = new PrintWriter(sw)) {
				e.printStackTrace(pw);
				stackTrace = sw.toString();	
			}
		} catch (IOException ex) {
			ex.printStackTrace();
		}
		Main.logger.severe("Uncaught exception on thread " + t + ": " + e.toString() + "\n" + stackTrace);
		
		JOptionPane.showMessageDialog(
			      frame, "An error has occurred. See log for more information.", 
			      "Error", JOptionPane.ERROR_MESSAGE
			    );
	}

}
