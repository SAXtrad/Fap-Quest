package fapquest;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.RenderingHints;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintStream;

import javax.swing.ImageIcon;
import javax.swing.JFrame;

public class MenuRecord {
	private JFrame frame;
	private Image img;
	private Font font;
	
	private int x;
	private int y;
	private int width;
	private int height;
	
	private int record;
	private String s_record;
	
	private float font_size;
	private String record_marker;
	
	public MenuRecord (JFrame frame, Font font){
		this.frame=frame;
		this.font = font;
		
		img = new ImageIcon("data/menu/menu_record_frame.png").getImage();
		
		record_marker = "[RECORD] = ";
		
		getRecordFromSaveFile();
		
		s_record =""+record;
	}
	
	public void update(){
		int frame_width = frame.getContentPane().getSize().width;
		int frame_height = frame.getContentPane().getSize().height;
		
		width = frame_width/4;
		height = (img.getHeight(null)*width)/img.getWidth(null);
		
		x = (frame_width - width)/2;
		y = frame_height - (height*11)/10;
		
		font_size = height * 0.37f;
	}
	
	public void draw(Graphics2D g2d){

		g2d.drawImage(img, x, y, width, height, null);

		
		g2d.setFont(font.deriveFont(Font.PLAIN, font_size)); 
		g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
		g2d.setColor(Color.WHITE);
		FontMetrics fm = g2d.getFontMetrics();
		g2d.drawString(s_record, x + width - height + (height - fm.stringWidth(s_record))/2, y + (height*7)/11);
	}
	
	public int getRecord(){
		return record;
	}

	public void setRecord(int record){
		this.record=record;
		this.s_record = "" + record;
		try {
			PrintStream out = new PrintStream(new FileOutputStream("save.dat"));
			out.print(record_marker+s_record);
			out.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void getRecordFromSaveFile(){
		record = 0;

		try {
			BufferedReader br = new BufferedReader(new FileReader("save.dat"));

			try {
				String line = br.readLine();

				while (line != null) {
					if(line.contains(record_marker)){
						String record = line.substring(line.indexOf(record_marker)+record_marker.length(), line.length());
						this.record = Integer.parseInt(record);
					}
					line = br.readLine();
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} finally {
				try {
					br.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		} catch (FileNotFoundException e1) {
			// TODO Auto-generated catch block
			//e1.printStackTrace();
		}
	}
}
