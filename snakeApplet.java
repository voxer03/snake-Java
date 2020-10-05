import javax.swing.* ;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;

public class snakeApplet 
extends JFrame {
	private SnakeCanvas c;
	
	public snakeApplet() {
		super("Snake");
		this.getContentPane().setBackground(Color.GRAY);
		c=new SnakeCanvas();
		
		c.setVisible(true);
		c.setPreferredSize(new Dimension(480,480));
		c.setFocusable(true);
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
