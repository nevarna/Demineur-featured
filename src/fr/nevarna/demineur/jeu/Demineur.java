package fr.nevarna.demineur.jeu;

//AUTEUR  : Navarna 
import java.lang.Math;
import java.util.*;

public class Demineur {
	public static final int LARGEUR = 16;
	public static final int LONGUEUR = 30;
	public static final int MINES = 99;
	public static final int NBCASE = LONGUEUR * LARGEUR;
	private Case[][] plateau;
	private int decouvert;
	private int mines;
	private int tour;
	private int multiplieur;

	public Demineur() {
		this.mines = MINES;
		initialisePlateau();
	}

	public void initialisePlateau() {
		this.plateau = new Case[LONGUEUR][LARGEUR];
		creationDefault();
		genere();
		miseAjourCase();
	}

	public boolean estDecouvert(CasePosition casePosition) {
		return this.plateau[casePosition.getX()][casePosition.getY()].getDecouvert();
	}
	

	public synchronized List<Case> jouer(CasePosition casePosition) {
		List<Case> casesDecouvertes = new ArrayList<>();
		jouer(casesDecouvertes, casePosition);
		return casesDecouvertes;
	}
	
	public void jouer (List<Case> cases , CasePosition casePosition) {
		Case caseChoisi = this.plateau[casePosition.getX()][casePosition.getY()];
		caseChoisi.decouvrir();
		cases.add(caseChoisi);
		this.decouvert++;
		if (!caseChoisi.presDuneMine()) {
			getCasesAlentours(casePosition.getX(), casePosition.getY()).stream().filter(CasePosition::estCorrect).forEach(c -> jouer(cases,c));
		}
		tour++;
	}

	public boolean jeufini() {
		return this.decouvert == NBCASE - MINES;
	}

	public int getdecouvert() {
		return this.decouvert;
	}

	public int getMines() {
		return this.mines;
	}

	public void creationDefault() {
		for (int i = 0; i < LONGUEUR; i++) {
			for (int j = 0; j < LARGEUR; j++) {
				this.plateau[i][j] = new Case(i, j);
			}
		}
	}

	public void genere() {
		int nombreMinesPlateau = 0;
		while (nombreMinesPlateau < MINES) {
			int randX = (int) (Math.random() * LONGUEUR);
			int randY = (int) (Math.random() * LARGEUR);
			if (!this.plateau[randX][randY].estUneMine()) {
				this.plateau[randX][randY].setMine();
				nombreMinesPlateau++;
			}
		}
	}

	public void miseAjourCase() {
		for (int i = 0; i < LONGUEUR; i++) {
			for (int j = 0; j < LARGEUR; j++) {
				int valeurCase = calculMine(i, j);
				this.plateau[i][j].setNombrePoint(valeurCase);
			}
		}
	}

	public int calculMine(int x, int y) {
		return (int) getCasesAlentours(x, y).stream().filter(CasePosition::estCorrect).filter(c -> this.plateau[c.getX()][c.getY()].estUneMine()).count();
	}

	public void affichage() {
		for (int i = 0; i < LONGUEUR; i++) {
			for (int j = 0; j < LARGEUR; j++)
				System.out.print(this.plateau[i][j] + " ");
			System.out.println();
		}

	}

	public static int getLongueur() {
		return LONGUEUR;
	}

	public static int getLargeur() {
		return LARGEUR;
	}

	public String afficheCase(int x, int y) {
		return this.plateau[x][y].affichageCase();
	}

	public int completion() {
		return (this.decouvert / (NBCASE - MINES)) * 100;
	}

	public void incrementMultiplieur() {
		this.multiplieur++;
	}

	public void decrementeMultiplieur() {
		this.multiplieur--;
	}

	public int getMultiplieur() {
		return this.multiplieur;
	}

	public void setMultiplieur(int m) {
		this.multiplieur = m;
	}

	public List<CasePosition> getCasesAlentours(int x, int y) {
		List<CasePosition> caseAlentours = new ArrayList<>();
		int[] positionsX = new int[] { x - 1, x, x + 1 };
		int[] positionsY = new int[] { y - 1, y, y + 1 };
		for (int positionX : positionsX) {
			for (int positionY : positionsY) {
				caseAlentours.add(new CasePosition(positionX, positionY));
			}
		}
		return caseAlentours;
	}

}
