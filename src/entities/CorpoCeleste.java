package entities;

import java.awt.Image;
import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

import javax.swing.ImageIcon;

public class CorpoCeleste {
	private static final BigDecimal G = new BigDecimal("6.674").multiply(new BigDecimal("0.00000000001"));
	private static final MathContext MF = new MathContext(10, RoundingMode.HALF_DOWN);
	private BigDecimal x = new BigDecimal("0");
	private BigDecimal y = new BigDecimal("0");
	private BigDecimal velX = new BigDecimal("0");
	private BigDecimal velY = new BigDecimal("0");
	private String nome;
	private CorpoCeleste ref;

	private Image imagem;
	private BigDecimal raio;
	private BigDecimal massa;

	private boolean isVisivel;

	public CorpoCeleste() {

	}

	public CorpoCeleste(String pathImage, BigDecimal raio, BigDecimal massa, CorpoCeleste queOrbita) {
		this.ref = queOrbita;
		load(pathImage, raio, massa);

		this.x = new BigDecimal("750").subtract(this.raio);
		this.y = new BigDecimal("400").subtract(this.raio);
	}

	public void load(String pathImage, BigDecimal raio, BigDecimal massa) {
		this.nome = pathImage.substring(4, pathImage.length() - 4);
		this.nome = Character.toUpperCase(nome.charAt(0)) + nome.substring(1);

		ImageIcon referencia = new ImageIcon(pathImage);
		raio = raio.multiply(new BigDecimal("0.01"));
//		this.imagem = referencia.getImage().getScaledInstance(raio.intValue() * 2, raio.intValue() * 2,
//				Image.SCALE_DEFAULT);
		this.imagem = referencia.getImage();

		this.raio = raio;
		this.massa = massa.multiply(new BigDecimal("0.00000000000005"));

		this.isVisivel = true;
	}

	public void setPosition(String x, String y) {
		this.x = new BigDecimal(x).multiply(new BigDecimal("0.00005")).subtract(this.raio)
				.add(ref != null ? ref.x : new BigDecimal("0"));
		this.y = new BigDecimal(y).multiply(new BigDecimal("0.00005")).subtract(this.raio)
				.add(ref != null ? ref.y : new BigDecimal("0"));
	}

	public void setVelocity(String velX, String velY) {
		this.velX = new BigDecimal(velX).add(ref != null ? ref.velX : new BigDecimal("0"));
		this.velY = new BigDecimal(velY).add(ref != null ? ref.velY : new BigDecimal("0"));
	}

	public static void updateAll(List<CorpoCeleste> corposCelestes, BigDecimal multiplicador) {
//		Fgravitacional = G * M * m / d^2
//		F = m * aceleracao
//		aceleracao = forcaGravitacional / m

		int qtdCc = corposCelestes.size();
		List<BigDecimal> fxs = new ArrayList<>(qtdCc);
		List<BigDecimal> fys = new ArrayList<>(qtdCc);
		CorpoCeleste cc, occ; // Corpo Celeste, Outro Corpo Celeste

		for (int i = 0; i < qtdCc; i++) {
			fxs.add(new BigDecimal("0"));
			fys.add(new BigDecimal("0"));
		}

		for (int i = 0; i < qtdCc; i++) {
			cc = corposCelestes.get(i);
			if (cc.getMassa().intValue() == 0)
				continue;

//			System.out.println("\nUpdate (" + cc + ")");
//			System.out.printf("   Posicao(\"%s\", \"%s\")%n",
//					cc.x.add(cc.raio).subtract(cc.ref == null ? new BigDecimal("0") : cc.ref.getCentroX())
//							.round(new MathContext(4)),
//					cc.y.add(cc.raio).subtract(cc.ref == null ? new BigDecimal("0") : cc.ref.getCentroY())
//							.round(new MathContext(4)));
//			System.out.printf("   Velocidade(\"%s\", \"%s\")%n",
//					cc.velX.subtract(cc.ref == null ? new BigDecimal("0") : cc.ref.velX).round(new MathContext(4)),
//					cc.velY.subtract(cc.ref == null ? new BigDecimal("0") : cc.ref.velY).round(new MathContext(4)));

			for (int j = 0; j < qtdCc; j++) {
				if (i == j)
					continue;
				occ = corposCelestes.get(j);

				BigDecimal dis, disX, disY;
				BigDecimal f, fX, fY;

				dis = distancia(cc, occ);
				disX = distanciaX(cc, occ);
				disY = distanciaY(cc, occ);

				try {
					f = G.multiply(cc.getMassa()).multiply(occ.massa).divide(dis.pow(2), MF);
					fX = f.multiply(disX.divide(dis, MF));
					fY = f.multiply(disY.divide(dis, MF));
				} catch (ArithmeticException e) {
					f = new BigDecimal("0");
					fX = new BigDecimal("0");
					fY = new BigDecimal("0");
				}

				if (occ.getCentroX().compareTo(cc.getCentroX()) < 0) {
					fX = fX.negate();
				}
				if (occ.getCentroY().compareTo(cc.getCentroY()) < 0) {
					fY = fY.negate();
				}

//			System.out.printf("Aceleracao (%.2f, %.2f)%n", aX.doubleValue(), aY.doubleValue());

				fX = fX.divide(new BigDecimal("2"), MF);
				fY = fY.divide(new BigDecimal("2"), MF);

				fxs.set(i, fxs.get(i).add(fX));
				fys.set(i, fys.get(i).add(fY));

				fxs.set(j, fxs.get(j).subtract(fX));
				fys.set(j, fys.get(j).subtract(fY));

			}
		}

		for (int i = 0; i < qtdCc; i++) {
			cc = corposCelestes.get(i);
			cc.velX = cc.velX.add(fxs.get(i).divide(cc.getMassa(), MF));
			cc.velY = cc.velY.add(fys.get(i).divide(cc.getMassa(), MF));

			cc.x = cc.x.add(cc.velX.multiply(multiplicador));
			cc.y = cc.y.add(cc.velY.multiply(multiplicador));

			if (colide(cc, corposCelestes)) {
				cc.x = cc.x.subtract(cc.velX);
				cc.y = cc.y.subtract(cc.velY);

				cc.velX = new BigDecimal("0");
				cc.velY = new BigDecimal("0");
			}
		}
	}

	private static boolean colide(CorpoCeleste cc, List<CorpoCeleste> ccs) {
		for (int i = 0; i < ccs.size(); i++) {
			if (ccs.get(i).equals(cc))
				continue;
			else if (cc.colideCom(ccs.get(i)))
				return true;
		}
		return false;
	}

	public BigDecimal distancia(CorpoCeleste corpoCeleste) {
		return corpoCeleste.distanciaX(this).pow(2).add(corpoCeleste.distanciaY(this).pow(2)).sqrt(MF);
	}

	public BigDecimal distanciaX(CorpoCeleste corpoCeleste) {
		return corpoCeleste.getCentroX().subtract(this.getCentroX()).abs();
	}

	public BigDecimal distanciaY(CorpoCeleste corpoCeleste) {
		return corpoCeleste.getCentroY().subtract(this.getCentroY()).abs();
	}

	private static BigDecimal distancia(CorpoCeleste cc1, CorpoCeleste cc2) {
		return distanciaX(cc1, cc2).pow(2).add(distanciaY(cc1, cc2).pow(2)).sqrt(MF);
	}

	public static BigDecimal distanciaX(CorpoCeleste cc1, CorpoCeleste cc2) {
		return cc1.getCentroX().subtract(cc2.getCentroX()).abs();
	}

	public static BigDecimal distanciaY(CorpoCeleste cc1, CorpoCeleste cc2) {
		return cc1.getCentroY().subtract(cc2.getCentroY()).abs();
	}

	public boolean colideCom(CorpoCeleste cc) {
		return distancia(this, cc).compareTo(this.raio.add(cc.raio)) < 0;
	}

	public BigDecimal getX() {
		return x;
	}

	public void setX(BigDecimal x) {
		this.x = x;
	}

	public BigDecimal getY() {
		return y;
	}

	public void setY(BigDecimal y) {
		this.y = y;
	}

	public BigDecimal getVelX() {
		return velX;
	}

	public void setVelX(BigDecimal velX) {
		this.velX = velX;
	}

	public BigDecimal getVelY() {
		return velY;
	}

	public void setVelY(BigDecimal velY) {
		this.velY = velY;
	}

	public BigDecimal getCentroX() {
		return x.add(raio);
	}

	public void setCentroX(BigDecimal centroX) {
		this.x = centroX.subtract(this.raio);
	}

	public BigDecimal getCentroY() {
		return y.add(raio);
	}

	public void setCentroY(BigDecimal centroY) {
		this.x = centroY.subtract(this.raio);
	}

	public Image getImagem() {
		return imagem;
	}

	public void setImagem(Image imagem) {
		this.imagem = imagem;
	}

	public BigDecimal getRaio() {
		return raio;
	}

	public void setRaio(BigDecimal raio) {
		this.raio = raio;
	}

	public BigDecimal getMassa() {
		return massa;
	}

	public void setMassa(BigDecimal massa) {
		this.massa = massa;
	}

	public boolean isVisivel() {
		return isVisivel;
	}

	public void setVisivel(boolean isVisivel) {
		this.isVisivel = isVisivel;
	}

	public CorpoCeleste getRef() {
		return ref;
	}

	public void setRef(CorpoCeleste ref) {
		this.ref = ref;
	}

	public String toString() {
		return this.nome;
	}
}
