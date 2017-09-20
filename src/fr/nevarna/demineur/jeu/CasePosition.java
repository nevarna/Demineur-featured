package fr.nevarna.demineur.jeu;

public class CasePosition {
	private int x;
	private int y;

	public CasePosition(int x, int y) {
		this.x = x;
		this.y = y;
	}

	public boolean estCorrect() {
		return x >= 0 && x < Demineur.LONGUEUR && y >= 0 && y < Demineur.LARGEUR;
	}

	public int getX() {
		return x;
	}

	public void setX(int x) {
		this.x = x;
	}

	public int getY() {
		return y;
	}

	public void setY(int y) {
		this.y = y;
	}
	
	
}
