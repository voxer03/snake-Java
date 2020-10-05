import java.awt.Canvas;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URL;
import java.util.LinkedList;
import java.util.Random;

import javax.swing.JOptionPane;

public class SnakeCanvas
extends Canvas
implements Runnable,KeyListener{
	
	private final int BOX_HEIGHT=15;
	private final int BOX_WIDTH=15;
	private final int GRID_HEIGHT=35;
	private final int GRID_WIDTH=35; 
	private int SNAKE_SPEED=80;
	private int SCORE=0;
	private String HIGH_SCORE="";
	
	private Thread runThread;
	
	
	private LinkedList<Point> snake;
	private Point fruit;
	
	private int direction=Direction.NO_DIRECTION;
	
	//private Graphics globalGraphics;
	private boolean isInMenu=true;
	private Image menuImage=null;
	public void paint (Graphics g) {
		
		if(runThread==null) {
			this.addKeyListener(this);
			runThread=new Thread(this);
			runThread.start();
		}
		if(isInMenu) {
			drawMenu(g);
		}
		else {
			if(snake==null) {
				snake =new LinkedList<Point>();
				generateDefaultSnake();
				placeFruit();
			}
			if(HIGH_SCORE.equals("")) {
				HIGH_SCORE=this.getHighScore();
			}
			drawFruit(g);
			drawGrid(g);
			drawSnake(g);
			drawScore(g);
		}
	}
	
	public void update(Graphics g) {
		Graphics offScreenGraphics ;
		BufferedImage offScreen=null;
		Dimension d=this.getSize();
		
		offScreen = new BufferedImage(d.width,d.height,BufferedImage.TYPE_INT_ARGB);
		offScreenGraphics =offScreen.getGraphics();
		offScreenGraphics.setColor(this.getBackground());
		offScreenGraphics.fillRect(0, 0, d.width, d.height);
		offScreenGraphics.setColor(this.getForeground());
		paint(offScreenGraphics);
		
		g.drawImage(offScreen,0,0,this);
	}
	
	public void checkScore() {
		if(HIGH_SCORE=="") {
			return;
		}
		if(SCORE>Integer.parseInt(HIGH_SCORE.split(":")[1])) {
			String name=JOptionPane.showInputDialog("New HighScore\n Enter Your name:");
			HIGH_SCORE=name+":"+SCORE;
			
			File scoreFile=new File("highscore.dat");
			if(scoreFile.exists()) {
				try {
					scoreFile.createNewFile();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			FileWriter writeFile=null;
			BufferedWriter writer=null;
			try {
				writeFile=new FileWriter (scoreFile);
				writer=new BufferedWriter(writeFile);
				writer.write(this.HIGH_SCORE);
				
			}
			catch(Exception e) {
				e.printStackTrace();
			}
			finally {
				try {
					if(writer!=null)
						writer.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	}
	
	public void placeFruit() {
		Random rand=new Random();
		
		int randomX=rand.nextInt(GRID_WIDTH);
		int randomY=rand.nextInt(GRID_HEIGHT);
		
		Point randomPoint= new Point(randomX,randomY);
		while(snake.contains(randomPoint)) {
			 randomX=rand.nextInt(GRID_WIDTH);
			 randomY=rand.nextInt(GRID_HEIGHT);
			 randomPoint= new Point(randomX,randomY);
		}
		fruit = randomPoint;
	}
	

	public void generateDefaultSnake() {
		SCORE=0;
		snake.clear();
		snake.add(new Point(3,0));
		snake.add(new Point(2,0));
		snake.add(new Point(1,0));
		snake.add(new Point(0,0));
		direction=Direction.NO_DIRECTION;
		
	}
	
	public void drawMenu(Graphics g) {
		if(this.menuImage== null) {
			try {
				URL imagePath = SnakeCanvas.class.getResource("Start.png");
				this.menuImage = Toolkit.getDefaultToolkit().getImage(imagePath);
				
			}
			catch(Exception e) {
				e.printStackTrace();
			}
		}
		g.drawImage(menuImage,0,0,520,600,this);
		
	}
	
	public void drawScore(Graphics g) {
		
		g.drawString("Score: "+SCORE,0,BOX_HEIGHT*GRID_HEIGHT+15);
		g.drawString("High Score: "+ HIGH_SCORE,BOX_WIDTH*GRID_WIDTH-400,BOX_HEIGHT*GRID_HEIGHT+15);
	}
	

	public void drawGrid(Graphics g) {
		g.drawRect(0, 0, GRID_WIDTH*BOX_WIDTH,GRID_HEIGHT*BOX_HEIGHT);
		g.setColor(Color.WHITE);
		for (int x=BOX_WIDTH;x<GRID_WIDTH*BOX_WIDTH;x+=BOX_WIDTH) {
			g.drawLine(x, 0, x,BOX_HEIGHT*GRID_HEIGHT );
		}
		
		for(int y=BOX_HEIGHT;y<GRID_HEIGHT*BOX_HEIGHT;y+=BOX_HEIGHT) {
			g.drawLine(0, y,GRID_WIDTH*BOX_WIDTH , y);
		}	
	}
	
	public void drawSnake(Graphics g) {

		
		for(Point p: snake) {
			if(p.equals(snake.peekFirst())) {
				g.setColor(Color.RED);
			}
			else {
				g.setColor(Color.GREEN);
			}
			g.fillRect(p.x * BOX_WIDTH,p.y*BOX_HEIGHT,BOX_WIDTH,BOX_HEIGHT);
		}
		g.setColor(Color.WHITE);
	} 
	
	public void drawFruit(Graphics g) {
		g.setColor(Color.RED);
		g.fillOval(fruit.x * BOX_WIDTH, fruit.y*BOX_HEIGHT, BOX_WIDTH, BOX_HEIGHT);
		g.setColor(Color.WHITE);
	}
	
	public void move() {
		Point head = snake.peekFirst();
		Point newPoint = head;
		switch(direction) {
		case Direction.NORTH :
			newPoint=new Point(head.x,head.y - 1); 
			break;
		case Direction.SOUTH: 
			newPoint =new Point(head.x,head.y + 1);
			break;
		case Direction.WEST :
			newPoint =new Point (head.x - 1,head.y);
			break;
		case Direction.EAST:
			newPoint = new Point(head.x + 1,head.y);
			break;
		}
		snake.remove(snake.peekLast());
		
		if(newPoint.equals(fruit)) {
			SCORE+=10;
			Point addPoint=(Point) newPoint.clone();
			switch(direction) {
			case Direction.NORTH :
				newPoint=new Point(head.x,head.y - 1); 
				break;
			case Direction.SOUTH: 
				newPoint =new Point(head.x,head.y + 1);
				break;
			case Direction.WEST :
				newPoint =new Point (head.x - 1,head.y);
				break;
			case Direction.EAST:
				newPoint = new Point(head.x + 1,head.y);
				break;
			}
			snake.push(addPoint);
			placeFruit();
			
		}
		else if(newPoint.x < 0 || newPoint.x > GRID_WIDTH-1) {
			checkScore();
			generateDefaultSnake();
			return;
		}
		else if(newPoint.y < 0 || newPoint.y > GRID_HEIGHT-1) {
			checkScore();
			generateDefaultSnake();
			return;
		}
		else if(snake.contains(newPoint)) {
			checkScore();
			generateDefaultSnake();
			return;
		}
		snake.push(newPoint);
	}
	@Override
	public void run() {
		while(true) {
			if(!isInMenu)
				move();
			repaint();
			
			try {
				Thread .currentThread();
				Thread.sleep(SNAKE_SPEED);
			}
			catch(Exception e) {
				e.printStackTrace();
			}
		}
	}
	@Override
	public void keyTyped(KeyEvent e) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void keyPressed(KeyEvent e) {
		switch(e.getKeyCode()) {
		case KeyEvent.VK_UP: 
			if(direction!= Direction.SOUTH)
				direction=Direction.NORTH;
			break;
		case KeyEvent.VK_DOWN:
			if(direction!=Direction.NORTH)
				direction=Direction.SOUTH;
			break;
		case KeyEvent.VK_LEFT:
			if(direction!=Direction.EAST)
				direction=Direction.WEST;
			break;
		case KeyEvent.VK_RIGHT:
			if(direction!=Direction.WEST)
				direction=Direction.EAST;
			break;
		case KeyEvent.VK_ESCAPE:
			pause();
		case KeyEvent.VK_ENTER:
			if(isInMenu) {
				isInMenu=false;
				repaint();
			}
			break;
		}
		
	}
	@Override
	public void keyReleased(KeyEvent e) {
		// TODO Auto-generated method stub
		
	}
	public void pause() {
		//JOptionPane.showMessageDialog(this, "Game Paused Press ok to continue", "Pause", JOptionPane.INFORMATION_MESSAGE);
		int option=JOptionPane.showConfirmDialog(this, "Suchit");
		
		
	}
	public String getHighScore() {
		FileReader readFile=null;
		BufferedReader reader = null;
		try {
			readFile=new FileReader("HighScore.dat");
			reader = new BufferedReader(readFile);
			return reader.readLine();
		}
		catch(Exception e) {
			return "Nobody:0";
		}
		finally {
			try {
				if(reader!=null)
					reader.close();
			}
			catch(Exception e){
				e.printStackTrace();
			}
		}
	}
}
