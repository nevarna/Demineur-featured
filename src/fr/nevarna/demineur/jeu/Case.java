
public class Case  {
    private int valeur ; 
    private int nbMines ; 
    private boolean vu ; 

    public Case (){
	this.valeur = 0 ;
	this.nbMines = 0 ; 
	this.vu = false ;
    }

    public void choisi (){
	this.vu = true ; 
    }

    public boolean presDuneMine(){
	if (nbMines !=0){
	    return true ; 
	}
	return false ;
    }
    public String toString () {
	String affich ="" ;
	if(vu)
	    affich += "t" ;
	else 
	    affich += "f";
	affich +="|" +valeur + "|";
	if(nbMines == -1 ) 
	    affich += "B" ;
	else 
	    affich += nbMines  ; 
	return affich ; 
    }


    public int point ( int multi ) {
	if(nbMines == 0 ) 
	    return multi ; 
	else 
	    return multi * nbMines  ; 
    }

    public String affich () {
	String affich = "" ;
	if (!vu){
	    return "X" ; 
	}
	else
	    affich += nbMines ;
	return affich; 	
    }

    public void setMine () {
	this.valeur = 1 ; 
    }

    public boolean estMine() {
	if(this.valeur == 1)
	    return  true ;
	return false ;
    }

    public void setNbMine(int val){
	this.nbMines = val ;
	if (valeur == 1)
	    this.nbMines = -1 ;  
    }



    public boolean getVu () {
	return this.vu ; 
    }
}
