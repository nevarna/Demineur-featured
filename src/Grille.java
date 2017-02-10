//AUTEUR  : Navarna 
public class Grille {
    public int nbLig;
    public int nbCol;
    public char [][] grille;
    
    public Grille(int m, int n){
        nbLig = m;
        nbCol = n;   
        grille = new char[nbLig][nbCol];     
        for(int i=0; i<nbLig; i++){
            for(int j=0; j<nbCol; j++){
                grille[i][j] = 'X';
            }
        }    
    }
    public void  uptchar(int x , int y ,char c){
        char rep;
        if(c == '-'){ //on récupere Bombe
            rep = 'X'; //on transforme
        }else if(c =='X'){ //on récupère Vide
            rep = ' '; //on transforme
        }else{ 
            rep = c;
        } 
        
        grille[x][y] = rep ;

    }

    public void afficher(){
        System.out.println();
        for(int c=1; c < 10; c++){ 
            System.out.print("   "+c);
        }
        for(int c=10; c<31; c++){
            System.out.print("  "+c);
        }
        System.out.println();
        for(int i=0; i<nbLig; i++){ 
            int cpt = i+1;       
            System.out.println("  -----------------------------------------------------------------------------------------------------------------------");          
            for(int j=0; j<nbCol; j++){
                System.out.print(" | ");
                if(grille[i][j] == 'X'){
		    //  System.out.print("\u001B[31m"); // Bombes en rouge
                }
                System.out.print(grille[i][j]);// +"\u001B[0m"); //Blanc par défaut   
            }
            System.out.println(" | "+cpt);
        }
        System.out.println("  -----------------------------------------------------------------------------------------------------------------------");
    }
}
