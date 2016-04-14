package mainPackage;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.WritableRaster;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.metadata.IIOMetadata;
import javax.imageio.metadata.IIOMetadataNode;
import javax.swing.ImageIcon;
import javax.swing.JFrame;

import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class BeatPattern {
	private JFrame frame;
	private int [] pattern;
	private int [] original_pattern;
	private Image img_element;
	
	private float total_width;
	private float beat_width;
	
	private int x_marker;
	
	private float width_to_marker;
	private int y;
	
	private int type;
	private float speed;

	private Image img_visual;
	private int img_visual_type;
	private ImageFrame[] gif_visual_frames;
	private boolean[] soundToPlay;
	private ArrayList<Integer> gif_frames_pattern;
	private int nb_action;	

	public static final int STATE_NORMAL = 0;
	public static final int STATE_FADEIN = 1;
	public static final int STATE_ONMARKER = 2;
	
	public int costInStamina;
	
	public BeatPattern(JFrame frame, int [] pattern, int[] original_pattern, int bpm, float speed, int x_last_pattern, Image img_element, int type, String nameImg){
		this.frame=frame;
		this.pattern=pattern;
		this.original_pattern=original_pattern;
		this.type=type;
		this.speed=speed;
		
		beat_width=(3000.0f*(float)speed)/((float)bpm*4.0f);
		total_width=beat_width*(float)pattern.length;
		
		x_marker = frame.getContentPane().getSize().width/2;
		width_to_marker=x_last_pattern-x_marker;
		
		y = (40 - img_element.getHeight(null))/2;
		
		this.img_element = img_element;

		String sync_start_marker = "[@";
		String sync_end_marker = "@]";
		
		boolean no_sync = false;
		
		soundToPlay = new boolean[pattern.length];
		for(int i=0; i < pattern.length; i++){
			soundToPlay[i] = pattern[i] == 1;
		}
				
		costInStamina = pattern.length / original_pattern.length;
		
		if(nameImg != null){
			if(nameImg.toLowerCase().contains(sync_start_marker) && nameImg.toLowerCase().contains(sync_end_marker) && nameImg.toLowerCase().endsWith("gif")){
				String s_nb_action = nameImg.substring(nameImg.indexOf(sync_start_marker)+sync_start_marker.length(), nameImg.indexOf(sync_end_marker));

				nb_action = Integer.parseInt(s_nb_action);

				no_sync = nb_action == 0;
			}else{
				if(type == Beatbar.INSTRUCTION || type == Beatbar.END || type == Beatbar.FADEOUT){
					no_sync = true;
				}else{
					nb_action = 1;
				}
			}
			
			gif_frames_pattern = new ArrayList<Integer>();

			gif_frames_pattern.add(new Integer(Opponent.FRAME_TRANSITION));

			int nb_frame_per_pattern=0;
			int nb_frame_per_pattern_optimal = (int)((total_width/(pattern.length/original_pattern.length))/speed);

			float width=0;

			for(int i=0; i < pattern.length;i++){				
				if(pattern[i] == 1 && width != 0){
					//Manage Extra Frame
					nb_frame_per_pattern+=(int)(width/speed);
					int extra_frame = 0;
					if(i%original_pattern.length == 0){
						extra_frame = nb_frame_per_pattern_optimal - nb_frame_per_pattern;
						nb_frame_per_pattern=0;
					}

					Integer nb_frames = new Integer((int)(width/speed) + extra_frame);

					gif_frames_pattern.add(nb_frames);
					width=0;
				}
				width+=beat_width;
			}

			nb_frame_per_pattern+=width/speed;
			int extra_frame = nb_frame_per_pattern_optimal - nb_frame_per_pattern;

			Integer nb_frames = new Integer((int)(width/speed) + extra_frame);
			gif_frames_pattern.add(nb_frames);

			int somme=0;
			String s="{";
			for(int i=0; i<gif_frames_pattern.size();i++){
				somme+=gif_frames_pattern.get(i).intValue();
				s=s+gif_frames_pattern.get(i).intValue()+",";
			}

			s+="}";

			//System.out.println("somme="+somme+" total_width="+(total_width/speed));
			//System.out.println("tab="+s);
			
			if(nameImg.toLowerCase().endsWith("gif") && !no_sync){
				
				img_visual_type = OpponentVisual.IMG_TYPE_GIF;

				new Thread(new Runnable() {
					public void run() {
						try {
							readGif(nameImg);
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
				}).start(); 

			}else{
				img_visual_type = OpponentVisual.IMG_TYPE_IMAGE;
				new Thread(new Runnable() {
					public void run() {
						img_visual = new ImageIcon(nameImg).getImage();
					}
				}).start();
			}
		}
	}
	
	public int update(){
		int framesFadeIn = Opponent.FRAME_FADEIN;
		if(type == Beatbar.PRE_INTRODUCTION){
			framesFadeIn = OpponentTitle.FRAME_FADEIN;
		}
		
		int onFadeIn = (int)(width_to_marker - speed*framesFadeIn);
		int onMarker = (int)(width_to_marker);
		
		x_marker = frame.getContentPane().getSize().width/2;
		
		width_to_marker-=speed;
		
		if(width_to_marker - speed*framesFadeIn <= 0 && onFadeIn > 0){
			return STATE_FADEIN;
		}else if(width_to_marker <= 0 && onMarker > 0){
			return STATE_ONMARKER;
		}else{
			return STATE_NORMAL;
		}

	}
	
	public void draw(Graphics g){
		for(int i=0;i<pattern.length;i++){
			if(pattern[i] == 1){
				g.drawImage(img_element, (int)(x_marker+width_to_marker+i*beat_width), y, null);
			}
		}
	}
	
	public int getLastX(){
		return (int)(x_marker+width_to_marker+total_width);
	}

	public boolean istoKill(){
		boolean isToKill = x_marker+width_to_marker+total_width < 0;
		if(isToKill){
			if(gif_visual_frames != null){
				for(int i=0;i<gif_visual_frames.length;i++){
					gif_visual_frames[i].getImage().flush();
				}
			}
			if(img_visual != null){
				img_visual.flush();
			}
		}
		
		return isToKill;
	}
	
	public int getType(){
		return type;
	}
	
	public int getFrames(){
		return (int)(total_width/speed);
	}
	
	private void readGif(String gif_filename) throws IOException{
	    ArrayList<ImageFrame> frames = new ArrayList<ImageFrame>(2);

	    ImageReader reader = (ImageReader) ImageIO.getImageReadersByFormatName("gif").next();
	    reader.setInput(ImageIO.createImageInputStream(new File(gif_filename)));

	    int lastx = 0;
	    int lasty = 0;

	    int width = -1;
	    int height = -1;

	    IIOMetadata metadata = reader.getStreamMetadata();

	    Color backgroundColor = null;

	    if(metadata != null) {
	        IIOMetadataNode globalRoot = (IIOMetadataNode) metadata.getAsTree(metadata.getNativeMetadataFormatName());

	        NodeList globalColorTable = globalRoot.getElementsByTagName("GlobalColorTable");
	        NodeList globalScreeDescriptor = globalRoot.getElementsByTagName("LogicalScreenDescriptor");

	        if (globalScreeDescriptor != null && globalScreeDescriptor.getLength() > 0){
	            IIOMetadataNode screenDescriptor = (IIOMetadataNode) globalScreeDescriptor.item(0);

	            if (screenDescriptor != null){
	                width = Integer.parseInt(screenDescriptor.getAttribute("logicalScreenWidth"));
	                height = Integer.parseInt(screenDescriptor.getAttribute("logicalScreenHeight"));
	            }
	        }

	        if (globalColorTable != null && globalColorTable.getLength() > 0){
	            IIOMetadataNode colorTable = (IIOMetadataNode) globalColorTable.item(0);

	            if (colorTable != null) {
	                String bgIndex = colorTable.getAttribute("backgroundColorIndex");

	                IIOMetadataNode colorEntry = (IIOMetadataNode) colorTable.getFirstChild();
	                while (colorEntry != null) {
	                    if (colorEntry.getAttribute("index").equals(bgIndex)) {
	                        int red = Integer.parseInt(colorEntry.getAttribute("red"));
	                        int green = Integer.parseInt(colorEntry.getAttribute("green"));
	                        int blue = Integer.parseInt(colorEntry.getAttribute("blue"));

	                        backgroundColor = new Color(red, green, blue);
	                        break;
	                    }

	                    colorEntry = (IIOMetadataNode) colorEntry.getNextSibling();
	                }
	            }
	        }
	    }

	    BufferedImage master = null;
	    boolean hasBackround = false;

	    for (int frameIndex = 0;; frameIndex++) {
	        BufferedImage image;
	        try{
	            image = reader.read(frameIndex);
	        }catch (IndexOutOfBoundsException io){
	            break;
	        }

	        if (width == -1 || height == -1){
	            width = image.getWidth();
	            height = image.getHeight();
	        }

	        IIOMetadataNode root = (IIOMetadataNode) reader.getImageMetadata(frameIndex).getAsTree("javax_imageio_gif_image_1.0");
	        IIOMetadataNode gce = (IIOMetadataNode) root.getElementsByTagName("GraphicControlExtension").item(0);
	        NodeList children = root.getChildNodes();

	        int delay = Integer.valueOf(gce.getAttribute("delayTime"));

	        String disposal = gce.getAttribute("disposalMethod");

	        if (master == null){
	            master = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
	            master.createGraphics().setColor(backgroundColor);
	            master.createGraphics().fillRect(0, 0, master.getWidth(), master.getHeight());

	        hasBackround = image.getWidth() == width && image.getHeight() == height;

	            master.createGraphics().drawImage(image, 0, 0, null);
	        }else{
	            int x = 0;
	            int y = 0;

	            for (int nodeIndex = 0; nodeIndex < children.getLength(); nodeIndex++){
	                Node nodeItem = children.item(nodeIndex);

	                if (nodeItem.getNodeName().equals("ImageDescriptor")){
	                    NamedNodeMap map = nodeItem.getAttributes();

	                    x = Integer.valueOf(map.getNamedItem("imageLeftPosition").getNodeValue());
	                    y = Integer.valueOf(map.getNamedItem("imageTopPosition").getNodeValue());
	                }
	            }

	            if (disposal.equals("restoreToPrevious")){
	                BufferedImage from = null;
	                for (int i = frameIndex - 1; i >= 0; i--){
	                    if (!frames.get(i).getDisposal().equals("restoreToPrevious") || frameIndex == 0){
	                        from = frames.get(i).getImage();
	                        break;
	                    }
	                }

	                {
	                    ColorModel model = from.getColorModel();
	                    boolean alpha = from.isAlphaPremultiplied();
	                    WritableRaster raster = from.copyData(null);
	                    master = new BufferedImage(model, raster, alpha, null);
	                }
	            }else if (disposal.equals("restoreToBackgroundColor") && backgroundColor != null){
	                if (!hasBackround || frameIndex > 1){
	                    master.createGraphics().fillRect(lastx, lasty, frames.get(frameIndex - 1).getWidth(), frames.get(frameIndex - 1).getHeight());
	                }
	            }
	            master.createGraphics().drawImage(image, x, y, null);

	            lastx = x;
	            lasty = y;
	        }

	        {
	            BufferedImage copy;

	            {
	                ColorModel model = master.getColorModel();
	                boolean alpha = master.isAlphaPremultiplied();
	                WritableRaster raster = master.copyData(null);
	                copy = new BufferedImage(model, raster, alpha, null);
	            }
	            frames.add(new ImageFrame(copy, delay, disposal, image.getWidth(), image.getHeight()));
	        }

	        master.flush();
	    }
	    reader.dispose();

	    this.gif_visual_frames=frames.toArray(new ImageFrame[frames.size()]);
	}
	
	public ImageFrame[] getGIFFrames(){
		return gif_visual_frames;
	}
	
	public ArrayList<Integer> getFramesPattern(){
		return gif_frames_pattern;
	}
	
	public int getGIFNbActions(){
		return nb_action;
	}
	
	public int getImgType(){
		return img_visual_type;
	}
	
	public Image getImgVisual(){
		return img_visual;
	}

	public int[] getOriginalPattern() {
		return original_pattern;
	}

	public boolean beatOnMarker() {
		boolean beatOnMarker = false;
		for(int i=0; i < pattern.length && !beatOnMarker;i++){
			if((width_to_marker + beat_width*(float)i) < 0 && soundToPlay[i]){
				soundToPlay[i] = false;
				beatOnMarker = true;
			}
		}

		return beatOnMarker;
	}

	public float getCostInStamina() {
		// TODO Auto-generated method stub
		return costInStamina;
	}
}
