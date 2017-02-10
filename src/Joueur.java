//AUTEUR  : Navarna 
public class Joueur{
	private String pseudo ;
	private String mdp ; 
	private int point ; 
	private boolean etat ; 
	private boolean actif  ; 
	private int pointPartie ; 
	private int mineTrouver ;
	private int caseTrouver ; 
	public Joueur (String pseudo , String mdp , int point){
		this.pseudo = pseudo ;
		this.mdp = mdp ;
		this.point = point ;
		this.etat = true ;  
		this.pointPartie = 0 ;
		this.mineTrouver = 0  ; 
		this.caseTrouver = 0 ;
		this.actif  = true  ;  
	}

	public int getPoint (){
		return this.point ;
	}

	public void setPoint (int point){
		this.point = point ;
	}

	public void ajouterPoint(int point){
		this.point += point ;  
	}

	public void supprimerPoint (int point) {
		this.point-= point ; 
	}

	public String getPseudo(){
		return pseudo ;
	} 
	public void setPseudo (String pseudo){
		this.pseudo = pseudo ;
	}

	public String getMdp () {
		return this.mdp ;
	}

	public void setMdp() {
		this.mdp  = mdp ; 
	}

	public void setEtat (boolean etat){
		this.etat = etat ;
	}

	public boolean getEtat (){
		return this.etat ;
	}

	public String toString () {
		String affiche = "pseudo : " + this.pseudo + " mdp : " + mdp + " -> " + point ;
		return affiche;
	}

	public int getMine () {
		return this.mineTrouver ; 
	}

	public void mineIncrement (int m) {
		this.mineTrouver += m ; 
	}

	public void setMine (int m){
		this.mineTrouver = m  ; 
	}

	public void setPointPartie (int p) {
		this.pointPartie =  p ; 
	}

	public void ajouterPointPartie (int p ){
		this.pointPartie += p ; 
	}

	public int getPointPartie (){
		return this.pointPartie ; 
	}

	public int getCaseTrouver (){
		return this.caseTrouver ; 
	}

	public void setCaseTrouver (int c){
		this.caseTrouver = c ; 
	}

	public void caseIncremente (int c ){
		this.caseTrouver += c ; 
	}

	public void resetPartie () {
		this.pointPartie = 0 ; 
		this.mineTrouver = 0 ; 
		this.caseTrouver = 0 ; 
	}

	public boolean getActif (){
		return this.actif ; 
	}

	public void setActif (boolean a){
		this.actif = a ; 
	}
} 
