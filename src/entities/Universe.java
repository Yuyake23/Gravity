package entities;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JPanel;
import javax.swing.Timer;

import gravity.Janela;

@SuppressWarnings("serial")
public class Universe extends JPanel implements ActionListener {
	private static final MathContext MF = new MathContext(10, RoundingMode.HALF_DOWN);
	private BigDecimal zoom = new BigDecimal("0.2");
	private Timer timer;
	private BigDecimal px = new BigDecimal("0"), py = new BigDecimal("0");
	private List<CorpoCeleste> corposCelestes;
	private CorpoCeleste ref;
	private int indexRef;
	
	public Universe() {
		this.setFocusable(true);
		this.setDoubleBuffered(true);
		this.setBounds(0, 0, Janela.WIDTH, Janela.HEIGHT);
		this.addKeyListener(new TecladoAdapter());

		CorpoCeleste sol, mercurio, venus, terra, marte, jupiter, saturno, urano, netuno;
		this.corposCelestes = new ArrayList<>();
		sol = new CorpoCeleste("res\\sol.png", new BigDecimal("69634.0"),
				new BigDecimal("1.989").multiply(new BigDecimal("10").pow(30)), null);
		sol.setPosition("0", "0");
		sol.setVelocity("0", "0");
		this.corposCelestes.add(sol);

		// mercurio
		mercurio = new CorpoCeleste("res\\mercurio.png", new BigDecimal("2439.7"),
				new BigDecimal("3.285").multiply(new BigDecimal("10").pow(23)), sol);
		mercurio.setPosition("57910000", "0");
		mercurio.setVelocity("0", "48.92");
		this.corposCelestes.add(mercurio);

		// venus
		venus = new CorpoCeleste("res\\venus.png", new BigDecimal("6051.8"),
				new BigDecimal("4.867").multiply(new BigDecimal("10").pow(24)), sol);
		venus.setPosition("108200000", "0");
		venus.setVelocity("0", "-35.02");
		this.corposCelestes.add(venus);

		// terra
		terra = new CorpoCeleste("res\\terra.png", new BigDecimal("6371"),
				new BigDecimal("5.972").multiply(new BigDecimal("10").pow(24)), sol);
		terra.setPosition("149600000", "0");
		terra.setVelocity("0", "-29.78");
		this.corposCelestes.add(terra);

		// marte
		marte = new CorpoCeleste("res\\marte.png", new BigDecimal("3389.5"),
				new BigDecimal("6.39").multiply(new BigDecimal("10").pow(23)), sol);
		marte.setPosition("227940000", "0");
		marte.setVelocity("0", "-24.07");
		this.corposCelestes.add(marte);

		// Jupiter
		jupiter = new CorpoCeleste("res\\jupiter.png", new BigDecimal("69911"),
				new BigDecimal("1.898").multiply(new BigDecimal("10").pow(27)), sol);
		jupiter.setPosition("778330000", "0");
		jupiter.setVelocity("0", "-13.05");
		this.corposCelestes.add(jupiter);

		// Saturno
		saturno = new CorpoCeleste("res\\saturno.png", new BigDecimal("58232"),
				new BigDecimal("5.683").multiply(new BigDecimal("10").pow(26)), sol);
		saturno.setPosition("1429400000", "0");
		saturno.setVelocity("0", "-9.64");
		this.corposCelestes.add(saturno);

		// Urano
		urano = new CorpoCeleste("res\\urano.png", new BigDecimal("25362"),
				new BigDecimal("8.681").multiply(new BigDecimal("10").pow(25)), sol);
		urano.setPosition("2870990000", "0");
		urano.setVelocity("0", "-6.81");
		this.corposCelestes.add(urano);

		// Netuno
		netuno = new CorpoCeleste("res\\netuno.png", new BigDecimal("24622"),
				new BigDecimal("1.024").multiply(new BigDecimal("10").pow(26)), sol);
		netuno.setPosition("4504300000", "0");
		netuno.setVelocity("0", "-5.43");
		this.corposCelestes.add(netuno);

		this.ref = sol;
		this.indexRef = 0;

		this.timer = new Timer(1000 / 60, this); // delay entre
		this.timer.start();
	}

	@Override
	public void paint(Graphics g) {
		g.setColor(Color.black);
		g.fillRect(0, 0, this.getWidth(), this.getHeight());
		Graphics2D graficos = (Graphics2D) g;
		double zoom = this.zoom.doubleValue();
		int posX = ref.getCentroX().intValue();
		int posY = ref.getCentroY().intValue();

		graficos.setBackground(Color.BLACK);
		graficos.scale(zoom, zoom);
		graficos.translate(Janela.WIDTH / zoom / 2 - posX + px.divide(this.zoom, MF).doubleValue(),
				Janela.HEIGHT / zoom / 2 - posY + py.divide(this.zoom, MF).doubleValue());

		for (int i = corposCelestes.size() - 1; i >= 0; i--) {
			CorpoCeleste cc = corposCelestes.get(i);

			graficos.setColor(Color.LIGHT_GRAY);
			if (cc.getRef() != null) {
				graficos.drawLine(cc.getCentroX().intValue(), cc.getCentroY().intValue(),
						cc.getRef().getCentroX().intValue(), cc.getRef().getCentroY().intValue());
			}

			int d = cc.getRaio().multiply(new BigDecimal("2")).intValue();
			graficos.drawImage(cc.getImagem(), cc.getX().intValue(), cc.getY().intValue(), d, d, this);
//			graficos.drawImage(cc.getImagem(), cc.getX().intValue(), cc.getY().intValue(), this);
		}

		g.dispose();
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		CorpoCeleste.updateAll(corposCelestes, new BigDecimal("1"));
		this.repaint();
	}

	private class TecladoAdapter extends KeyAdapter {
		@Override
		public void keyPressed(KeyEvent e) {
			pressionar(e);
		}

		@Override
		public void keyReleased(KeyEvent e) {
		}

	}

	public void pressionar(KeyEvent tecla) {
		int kc = tecla.getKeyCode();
		if (kc == '=') {
			this.zoom = this.zoom.multiply(new BigDecimal("1.05"));
		} else if (kc == '-') {
			this.zoom = this.zoom.multiply(new BigDecimal("0.95"));
		} else if (kc == KeyEvent.VK_BACK_SPACE) {
			this.px = this.py = new BigDecimal("0");
		} else if (kc == KeyEvent.VK_RIGHT) {
			this.px = this.px.add(new BigDecimal("-8"));
		} else if (kc == KeyEvent.VK_LEFT) {
			this.px = this.px.add(new BigDecimal("8"));
		} else if (kc == KeyEvent.VK_UP) {
			this.py = this.py.add(new BigDecimal("5"));
		} else if (kc == KeyEvent.VK_DOWN) {
			this.py = this.py.add(new BigDecimal("-5"));
		} else if (kc == KeyEvent.VK_SPACE) {
			this.indexRef = ++this.indexRef % this.corposCelestes.size();
			this.ref = this.corposCelestes.get(this.indexRef);
		}
	}

	public void largar(KeyEvent tecla) {
	}
}