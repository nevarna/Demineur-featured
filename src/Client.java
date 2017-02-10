//AUTEUR  : Navarna 
import java.io.*;
import java.net.*;
import java.util.*;


class Lire implements Runnable {
    private Socket sock;
    private PrintWriter pw ; 
    public boolean fermeture = false;
    public boolean connexion = false;
    public int val = 1;

    public Lire (Socket sock , PrintWriter pw){
		this.sock = sock;
		this.pw = pw ; 
    }

    public void ecrireIMOK () {
		pw.println("IMOK") ; 
    }


    public void run (){
	boolean ouvert = true ;
	BufferedReader br = null ;
	boolean imok = false ; 
	try{
	    	br = new BufferedReader(new InputStreamReader(sock.getInputStream()));
	    while(ouvert){
	
		String recu = br.readLine();
		if(fermeture){
		    ouvert = false;
		}
		if(recu != null) {
		    String[] str = recu.split("#");
		    if(recu.equals("IDNO")){
			System.out.println("Username et/ou Password incorrect");
			connexion = false;
		    }
		
		    if(str[0].equals("IDOK")){
			System.out.println("Vous êtes connecté(e)");
			connexion = true;
		    }
		    if (str[0].equals("LMNB")) {
			if (str.length == 2) {
			    int nb_message = Integer.parseInt(str[1]) ;
			    System.out.println("Nombre de partie : " + nb_message ) ;
			    for (int i = 0 ; i <nb_message ; i ++ ) {
				String recu2 = br.readLine();
				if(recu2 != null ) {
				    String [] str2 = recu2.split("#") ;
				    if(str2[0].equals("MATC")&&(str2.length > 3)) {
					String afficher ="Host : " + str2[1] +" "+ str2[2] + "\n"+str2[3]+ "\nTaux de complétion: "+str2[4]+"\n";
					for(int j= 5 ; j < str2.length ; j++ ){
					    afficher +=  "Joueur : " +str2[j] + " -> " + str2[j+1] + " points";
					    j++;
					}
					System.out.println(afficher) ;
				    }
				}
			    }
			}
		    }
		    if (str[0].equals("LANB")) {
			if (str.length == 2) {
			    int nb_message = Integer.parseInt(str[1]) ;
			    System.out.println("Nombre de joueur libre : " + nb_message ) ;
			    for (int i = 0 ; i <nb_message ; i ++ ) {
				String recu2 = br.readLine();
				if(recu2 != null ) {
				    String [] str2 = recu2.split("#") ;
				    if(str2[0].equals("AVAI")&&(str2.length > 2)) {
					String afficher = "Joueur : " +str2[1] + " -> " + str2[2] + " points" ;
					System.out.println(afficher) ;
				    }
				}
			    }
			}
		    }
		    if (str[0].equals("LUNB")) {
			if (str.length == 2) {
			    int nb_message = Integer.parseInt(str[1]) ;
			    System.out.println("Nombre de joueur : " + nb_message ) ;
			    for (int i = 0 ; i <nb_message ; i ++ ) {
				String recu2 = br.readLine();
				if(recu2 != null ) {
				    String [] str2 = recu2.split("#") ;
				    if(str2[0].equals("USER")&&(str2.length > 2)) {
					String afficher ="Joueur : " +str2[1] + " -> " + str2[2] + " points" ;
					System.out.println(afficher) ;
				    }
				}
			    }
			}
		    }
		    if(str[0].equals("NWOK") ) {
			if(str.length > 2 ) {
			    System.out.println("Nouvelle partie sur un host\nIP : "+str[1] +" | Port : " +str[2]);
			}
		    }
		    if(str[0].equals("FULL")) {
			System.out.println("Impossible de créer une nouvelle partie") ;
		    }
		    if(str[0].equals("KICK")) {
			sock.close() ;

		    }
		    if(str[0].equals("NWNO")){
			System.out.println("Nouveau match refusé");
		    }
		    if(str[0].equals("RUOK")){
			ecrireIMOK(); 
			imok = true; 
		    }
		    boolean reessaie = true ; 
		    if(str[0].equals("IDIG")){
			if(str.length > 2 ) {
			    System.out.println("Vous êtes déjà en partie  : IP =" + str[1]+" | port = "+str[2]);
			    connexion = true ;
			    reessaie = false ;  
		    
			}
		    }
		   
		    if((reessaie)&&(!imok))
			val = 1 ;
		    else if (!reessaie) 
			val = 2 ; 
		    if(imok) 
			imok = false ; 
		}
		else { 
		    ouvert = false ; 
		}
	    }
	    br.close() ;
	}catch(IOException ie){
	    ouvert = false ;
	}
    }
}


class LireHost implements Runnable{
    public Socket sockH;
    private PrintWriter pw ; 
    public int val = 1;
    public boolean connexion = false;
    public boolean fermeture = false;
    public int nbLig = 16;
    public int nbCol = 30;
    public Grille grille;
	
    public LireHost(){
	//this.sockH = sockH;
    }
    public void ajoutSocket(Socket sock ,PrintWriter pw) {
	this.sockH = sock ; 
	this.pw = pw ; 
    }

    public void ecrireIMOK () {
	pw.println("IMOK") ; 
    }

    public void run(){
	boolean ouvert = true ;
	BufferedReader br = null ;
	try{
	    grille = new Grille(nbLig,nbCol);
	   br = new BufferedReader(new InputStreamReader(sockH.getInputStream()));
	    while(ouvert){
		
		String recu = br.readLine();
		if(fermeture){
		    ouvert = false;
		}
		//System.out.println(recu) ;
		if(recu != null ) {
		    String[] str = recu.split("#");
		    if(str[0].equals("JNNO")){
			System.out.println("Connexion refusée");
			connexion = false;
			val = 1 ; 
		    }
		    if(str[0].equals("JNOK")){
			connexion = true ; 
			// System.out.println(recu) ; 
			if(str.length == 2){
			    int nb_lignes = Integer.parseInt(str[1]);
			    System.out.println(nb_lignes);		
			    for(int i=0; i<nb_lignes; i++){
				String recuB = br.readLine();
				//System.out.println(recuB) ; 
				if(recuB != null){
				    String [] strB = recuB.split("#");
				    if(strB[0].equals("BDIT")){
					if(strB.length > 2){
					    int numberLine = Integer.parseInt(strB[1]);
					    for(int j=0; j< strB.length-2 ; j++){
					    	grille.uptchar(numberLine,j,strB[j+2].charAt(0));
						//grille.grille[numberLine][j] = strB[j+2].charAt(0);
					    }
					}
				    }
				}
			    }
			}
			val = 1 ; 
		    }
		    if(str[0].equals("LATE")){
			System.out.println("Trop tard ! Case déjà découverte");
			val = 1 ; 
		    }
		    if(str[0].equals("DECO")){
			if(str.length >= 2){
			    System.out.println("Déconnexion de : "+str[1]);
			}
			val = 1 ; 
		    }
		    if(str[0].equals("IGNB")){
			if(str.length == 2){
			    int nbJoueurs = Integer.parseInt(str[1]);
			    System.out.println("Nombre de joueur : "+nbJoueurs);
			    for(int i=0; i<nbJoueurs; i++){
				String recuBis = br.readLine();
				if(recuBis != null) {
				    String [] strBis = recuBis.split("#");
				    if(str[0].equals("IGPL") && (str.length == 6)){
					System.out.println("Détails du joueur : "+str[1]+"\n Points de partie : "+str[2]+" | Points total : "+str[3]+"\n Cases correctement déminées : "+str[4]+" | Mines trouvées : "+str[5]);
				    }
				}
			    }
			}
		    }
		    if(str[0].equals("CONN")){
			if(str.length >= 6){
			    System.out.println("Connexion de : "+str[1]+"\n Points de partie : "+str[2]+" | Points total : "+str[3]+"\n Cases correctement deminées : "+str[4]+" | Mines trouvées : "+str[5]);
			}
			val = 1 ; 
		    }
		    if(str[0].equals("OORG")){
			if(str.length >= 3){ 
			    int wabs = Integer.parseInt(str[1]);
			    int word = Integer.parseInt(str[2]);
			    if(wabs > nbLig || wabs < nbLig || word < nbCol || word > nbCol){
				System.out.println("Vous êtes en dehors du plateau !");
			    }
			}
			val = 1 ; 
		    }
		    if(str[0].equals("RUOK")){
			ecrireIMOK() ;  
		    }
		    if(str[0].equals("SQRD")){
			if(str.length == 6){ 
			    int wabs = Integer.parseInt(str[2]);
			    int word = Integer.parseInt(str[1]);
			    grille.uptchar(wabs,word,str[3].charAt(0));
			    //grille.grille[wabs][word] = str[3].charAt(0);
			    int wa = wabs+1; int wo = word+1;
			    System.out.println("Case cliquée : ("+wa+','+wo+')'+"\nContenu de la case : "+str[3]+"\nNombre de points : "+str[4]+"\nUtilisateur -> "+str[5]);
			    grille.afficher();	
			}
			val = 1 ; 
		    }
		    if(str[0].equals("SCPC")){
			if(str.length >= 6){
			    System.out.println("Joueur : "+str[1]+"\n Points de partie : "+str[2]+" | Points total : "+str[3]+"\n Cases correctement deminées : "+str[4]+" | Mines trouvées : "+str[5]);
			}
		    }
		    if(str[0].equals("ENDC")){
			if(str.length >= 2){
			    int nbPlay = Integer.parseInt(str[1]); 
			    System.out.println("Terminée");
			    sockH.close();
			}
		    }
		    //	grille.afficher();
		}
		else {
		    ouvert = false  ; 
		}
	    }
	    br.close();
	}catch(IOException ioe){
	    ouvert = false;
	}
    }
}

public class Client{
    static int port = 5555;

    public static void host(String IP){
	InetAddress ipH = null;
	Socket sockH = null;
	Thread t = null;
	String rep, username ="", password="";
	int x = 0, y = 0, portH = 0;
	Scanner sc = new Scanner(System.in);
	LireHost l = new LireHost() ;	
	PrintWriter pw = null;	

	try{
			
	    while(!l.connexion){
		if(l.val == 1){
		    ipH = InetAddress.getByName(IP);
		    System.out.print("Quel port voulez-vous ? ");
		    try{
			portH = sc.nextInt();
			sc.nextLine() ;
		    }catch(InputMismatchException e){
			System.out.println("Port invalide");
			sc.nextLine();
		    }
		    
		    sockH = new Socket(ipH, portH);
		    pw = new PrintWriter(new OutputStreamWriter(sockH.getOutputStream()), true);
        
		
		    l.ajoutSocket(sockH,pw);
		    t = new Thread(l);
		    t.start();
		 
		    System.out.print("Votre username : ");
		    username = sc.nextLine();
		    System.out.print("Votre password : ");
		    password = sc.nextLine();
		    if((!username.contains("#"))&&(!password.contains("#"))) {
		    l.val = 0;
		    pw.println("JOIN#"+username+"#"+password);
		    }
		    else {
			System.out.println("Username ou Password incorrect") ;  
		    }
		}else {
		    try {
			Thread.sleep(1500) ;
		    }catch(InterruptedException ie ) {
			System.out.println("sleep interrompu") ;
		    }
		}
	    }
	    while(!sockH.isClosed()){
		if(l.val == 1 ){
		    System.out.println("Donnez la position souhaitée : ");
		    System.out.print("Abscisse : ");
		    try{
			x = sc.nextInt();
		    }catch (InputMismatchException e){
			System.out.print("Entier x invalide");
			sc.nextLine() ;
		    }
		    System.out.print("Ordonnée : ");
		    try{
			y = sc.nextInt();
		    }catch (InputMismatchException e){
			System.out.print("Entier y invalide");
			sc.nextLine() ;
		    }
		    x = x-1; y = y-1;
		    l.val = 0;
		    pw.println("CLIC#"+x+"#"+y);
		}
	    }
		
	    pw.close();
	}catch(IOException io){
	    System.out.println("Erreur du Host");
	}
    }

    public static void serveur(String IP){
	InetAddress IPAddress;
	Socket sock;
	Thread t;
	Scanner sc = new Scanner(System.in);
	String rep, username, password;;
	int choix=0, type=0;
	try{
	    IPAddress = InetAddress.getByName(IP);
	    sock = new Socket(IPAddress, port);
	    PrintWriter pw = new PrintWriter(new OutputStreamWriter(sock.getOutputStream()), true);

	    Lire l = new Lire(sock,pw);
	    t = new Thread(l);
	    t.start();

	    System.out.println("\n     Bienvenue !\n\nVoulez-vous jouer ? - Oui/Non ");
	    rep = sc.nextLine();
	    if(rep.equals("Oui")){
		//System.out.println("ici " + l.connexion + " " + l.val) ; 
		while(!l.connexion){
		    if(l.val == 1){
			System.out.print("Votre username : ");
			username = sc.nextLine();
			System.out.print("Votre password : ");
			password = sc.nextLine();
			if((!username.contains("#"))&&(!password.contains("#"))) {
			    l.val = 0;
			    pw.println("REGI#"+username+"#"+password);
			}
			else {
			    System.out.println("Username ou Password incorrect") ;  
			}
			    
		    }
		    else {
			try {
			    Thread.sleep(500) ;
			}catch(InterruptedException ie ) {
			    System.out.println("sleep interrompu") ;
			}
		    }
		}
	        
		if(l.val == 2 ) {
		    
		    pw.println("LEAV") ;
		    sock.close() ; 
		}
		while(!sock.isClosed()) {
		    
		    if(l.val == 1){
				    	
			System.out.println("\nQue souhaitez-vous faire ? - Tapez le numéro correspondant ");
			System.out.println("       1. Liste des parties en cours");
			System.out.println("       2. Liste des joueurs disponibles");
			System.out.println("       3. Liste des utilisateurs");
			System.out.println("       4. Nouvelle partie");
			System.out.println("       5. Quitter");

                        try{
			    choix = sc.nextInt();
                        }catch (InputMismatchException e){
                            System.out.print("Entier invalide");
                            sc.nextLine() ;
                        }
			switch(choix){
			case 1:
			    l.val = 0;
			    pw.println("LSMA");
			    break;
			case 2:
			    l.val = 0;
			    pw.println("LSAV");
			    break;
			case 3:
			    l.val = 0;
			    pw.println("LSUS");
			    break;
			case 4:
			    l.val = 0 ;
			    System.out.println("\nType de partie:  - Tapez le numéro correspondant");
			    System.out.println("      1. Public");
			    System.out.println("      2. Privé");
			    try{
				type = sc.nextInt() ;
			    }catch (InputMismatchException e){
				System.out.print("Entier invalide ");
				sc.nextLine();
				l.val = 1 ;
			    }
			    if(type == 1 ) {
				pw.println("NWMA#ALL") ;
			    }else if(type == 2 ) {
				String ajout = "NWMA" ;
				System.out.println("Entrer ligne par ligne le nom des joueurs, avec END a la derniere ligne") ;
				String lu = "";
				while(!lu.equals("END")) {
				    lu = sc.nextLine() ;
				    if((!lu.equals("END"))&&(!lu.equals(""))){
					ajout +="#"+lu ;
				    }
				}
				pw.println(ajout) ;
				break ;
			    }else{
				System.out.println("Mauvais choix !");
			    }
			    break ;
			case 5:
			    pw.println("LEAV");
			    sock.close() ; 
			    break;
			default:
			    System.out.println("Mauvais choix !");
			    break;
			}
		    }
		}
	    }else{
		pw.println("LEAV");
	    }
	    pw.close() ;
	}catch(IOException io){
	    System.out.println("Erreur de Serveur") ;
	}
    }

    public static void main(String[] args){
	Scanner sc0 = new Scanner (System.in) ;
	boolean quit = false ; 
	int choix  = 0 ;
	String IP = "localhost"  ; 
	if(args.length == 1 ) 
	    IP = args[0] ; 
	while(!quit) { 		    	
	    System.out.println("\nA quoi voulez vous vous connecter ? - Tapez le numéro correspondant ");
	    System.out.println("       1. Serveur");
	    System.out.println("       2. Host");
	    System.out.println("       3. Quitter");
	    try{
		choix = sc0.nextInt();
	    }catch (InputMismatchException e){
		System.out.print("Entier invalide");
		sc0.nextLine() ;
	    }
	    switch(choix){
	    case 1:
		serveur(IP);
		break ;
	    case 2 : 
		host(IP);
		break ;
	    case 3 : 
		quit = true ; 
		break ; 
	    default : 
		break ; 
	    }
	}
    }
}
