import javax.swing.* ;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Toolkit;
import java.net.URL;

public class snakeApplet 
extends JFrame {
	private SnakeCanvas c;
	private Image iconImage=null;
	public snakeApplet() {
		super("Snake");
		this.getContentPane().setBackground(Color.WHITE);
		c=new SnakeCanvas();
		
		c.setVisible(true);
		c.setPreferredSize(new Dimension(480,480));
		c.setFocusable(true);
		URL imagePath = snakeApplet.class.getResource("snake.png");
		this.setIconImage(Toolkit.getDefaultToolkit().getImage(imagePath));
		this.add(c);
		this.setSize(480,480);
		this.setResizable(false);
		this.setVisible(true);
		this.setDefaultCloseOperation(EXIT_ON_CLOSE);
	}
	
	public void paint (Graphics g) {
		this.setSize(new Dimension(544,600));
	}
	
	public static void main(String...s) {
		new snakeApplet();
	}
}
