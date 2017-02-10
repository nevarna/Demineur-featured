//AUTEUR  : Navarna 
import java.lang.Math ; 
import java.util.*;
public class Demineur {
    static int LARGEUR = 16 ;
    static int LONGUEUR = 30 ; 
    static int MINES = 99 ; 
    static int NBCASE = LONGUEUR * LARGEUR ;
    private Case [][] plateau ; 
    private int decouvert =0  ; 
    private int mines  = 0 ; 
    private int tour  = 0 ; 
    public List <String> envoie =null ; 
    private int multiplieur = 0 ; 

    public Demineur () {
	this.decouvert = 0 ;
	this.mines = MINES ;
	this.tour = 0;
	initialisePlateau() ;
	envoie = Collections.synchronizedList(new ArrayList<String>());
    }

    public void initialisePlateau () {
	this.plateau = new Case [LONGUEUR][LARGEUR];
	creationDefault() ;
	genere();
	miseAjourCase() ;
    }


    public synchronized int jouer (int x , int y, String nom){
	if((x >=0 )&& (x <LONGUEUR)&&(y >=0)&&(y< LARGEUR)){
	    if(!this.plateau[x][y].getVu()){
		this.plateau[x][y].choisi() ;
		this.decouvert ++ ;
		String nouveau = "SQRD#"+x+"#"+y+"#"+this.plateau[x][y].affich() +"#"+this.plateau[x][y].point(this.multiplieur) + "#"+ nom ; 
		envoie.add(nouveau) ; 
		if(this.plateau[x][y].presDuneMine()){
		    tour ++ ;
		    return 0 ; 
		}
		else {
		    jouer (x-1, y,nom);
		    jouer (x+1 , y,nom);
		    jouer (x,y+1,nom);
		    jouer (x,y-1,nom); 
		    tour ++ ;
		    return 0 ; 
		}  
				
	    }
	    return 1 ; 
	}
	return 2 ; 
    }

    public synchronized int jouer (int x , int y){
	if((x >=0 )&& (x <LONGUEUR)&&(y >=0)&&(y< LARGEUR)){
	    if(!this.plateau[x][y].getVu()){
		this.plateau[x][y].choisi() ;
		this.decouvert ++ ;
		if(this.plateau[x][y].estMine()){
		    tour ++ ;
		    return 0 ; 
		}
		else if(this.plateau[x][y].presDuneMine()){
		    tour ++ ;
		    return 0 ; 
		}
		else {
		    jouer (x-1, y);
		    jouer (x+1 , y);
		    jouer (x,y+1);
		    jouer (x,y-1); 
		    tour ++ ;
		    return 0 ; 
		}  
				
	    }
	    return 1 ; 
	}
	return 2 ; 
    }

    public boolean estfini () {
	if(this.decouvert == NBCASE - MINES)
	    return true ;
	return false  ;

    }
    public int getdecouvert () {
	return this.decouvert;
    }

    public int getMines () {
	return this.mines ;
    }

    public void creationDefault() {
	for (int i = 0 ; i < LONGUEUR ; i++){
	    for (int j =0 ; j < LARGEUR ; j++){
		this.plateau[i][j] = new Case () ;
	    }
	}
    }

    public void genere (){
	int mis = 0 ;
	while (mis < MINES){
	    int randX = (int) (Math.random() * LONGUEUR) ; 
	    int randY = (int) (Math.random() * LARGEUR) ; 
	    if(!this.plateau[randX][randY].estMine()){
		this.plateau[randX][randY].setMine();
		mis ++;
	    }
	}
    }

    public void miseAjourCase(){
	for (int i = 0 ; i < LONGUEUR ; i ++){
	    for (int j= 0 ; j < LARGEUR; j++){
		int val = calculMine(i,j);
		this.plateau[i][j].setNbMine(val);
	    }
	}
    }

    public int calculMine(int x, int y){
	int reponse = 0 ;
	if(x >0){
	    if(y > 0 )
		if(this.plateau[x-1][y-1].estMine())
		    reponse ++ ; 
	    if(y < LARGEUR-1)
		if(this.plateau[x-1][y+1].estMine())
		    reponse ++ ; 
	    if(this.plateau[x-1][y].estMine())
		reponse ++ ; 
	}
	if(x < LONGUEUR-1){
	    if(y > 0 )
		if(this.plateau[x+1][y-1].estMine())
		    reponse ++ ; 
	    if(y < LARGEUR-1)
		if(this.plateau[x+1][y+1].estMine())
		    reponse ++ ; 
	    if(this.plateau[x+1][y].estMine())
		reponse ++ ; 
	}
	if(y >0){
	    if(this.plateau[x][y-1].estMine())
		reponse ++ ; 
	}
	if(y< LARGEUR-1){
	    if(this.plateau[x][y+1].estMine())
		reponse ++ ; 
	}
	return reponse ; 
    }

    public void affichage () {
	for (int i = 0 ; i < LONGUEUR ; i++){
	    for (int j = 0 ; j < LARGEUR ; j++)
		System.out.print(this.plateau[i][j] + " ");
	    System.out.println() ;
	}

    }

    public static int getLongueur () {
	return LONGUEUR ; 
    }

    public static int getLargeur (){
	return LARGEUR ; 
    }

    public String afficheCase (int x , int y) {
	return this.plateau[x][y].affich() ; 
    }

    public int completion () {
	int reponse = (this.decouvert / (NBCASE - MINES)) * 100  ;
	return reponse ; 
    }

    public void incrementMultiplieur() {
	this.multiplieur ++ ; 
    }

    public void decrementeMultiplieur () {
	this.multiplieur -- ; 
    }

    public int getMultiplieur() {
	return this.multiplieur ; 
    }

    public void setMultiplieur(int m) {
	this.multiplieur= m ;
    }
    
}
