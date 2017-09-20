package fr.nevarna.demineur.jeu;

import static fr.nevarna.demineur.jeu.Affichage.*;

/**
 * Classe correspondant à une case du plateau
 * @author nevarna
 *
 */
public class Case {
	private int nombrePoint;
	private int nombreMinesAlentours;
	private boolean decouvert;
	private boolean estUneMine;
	private CasePosition casePosition;

	public static int VALEUR_BOMBE = -1;

	public Case(int x, int y) {
		casePosition = new CasePosition(x, y);
	}

	public void decouvrir() {
		this.decouvert = true;
	}

	public boolean presDuneMine() {
		return nombreMinesAlentours != 0;
	}

	@Override
	public String toString() {
		return (decouvert ? TRUE : FALSE) + SEPARATEUR + nombrePoint + SEPARATEUR + (estUneMine ? BOMBE : nombreMinesAlentours);
	}

	public int pointGagner(int multiplieur) {
		return nombreMinesAlentours == 0 ? multiplieur : multiplieur * nombreMinesAlentours;
	}

	public String affichageCase() {
		return decouvert ? BOMBE : nombreMinesAlentours + "";

	}

	public void setMine() {
		estUneMine = true;
		nombrePoint = 1;
	}

	public boolean estUneMine() {
		return estUneMine;
	}

	public void setNombrePoint(int valeurCase) {
		this.nombreMinesAlentours = estUneMine ? VALEUR_BOMBE : valeurCase;
	}

	public boolean getDecouvert() {
		return this.decouvert;
	}

	public CasePosition getCasePosition() {
		return casePosition;
	}

	public void setCasePosition(CasePosition casePosition) {
		this.casePosition = casePosition;
	}
}
