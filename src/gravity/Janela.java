package gravity;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Toolkit;

import javax.swing.JFrame;

import entities.Universe;

@SuppressWarnings("serial")
public class Janela extends JFrame {
	private static final Dimension size = Toolkit.getDefaultToolkit().getScreenSize();
	public static final int WIDTH = (int) (size.getWidth() * 0.9);
	public static final int HEIGHT = (int) (size.getHeight() * 0.9);

	public Janela() {
		add(new Universe());
		setTitle("Gravity");
		setSize(WIDTH, HEIGHT); // w h
		setBackground(Color.BLACK);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); // fechar quando clicar no X
		setLocationRelativeTo(null); // centro da tela
		//setResizable(false);
		setVisible(true);
	}

	public static void main(String[] args) {
		new Janela();
	}
}
