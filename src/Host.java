//AUTEUR  : Navarna 
import java.io.* ;
import java.net.*;
import java.util.* ; 
import java.awt.event.*;
import javax.swing.Timer ;
public class Host {
    private int port ;
    private String IP ; 
    final int NUM_JOUEUR_MAX = 10 ; 
    private List <JoueurConneter>  listeJoueur; 
    private List <Joueur> enCours ; 
    private int compteur ;
    private int nb_joueur ; 
    private int completion ;  
    private String nom ; 
    private Demineur  jeu ; 
    private int PORTSERV = 7777 ; 
    private String IPSERV  ;
    private connectionServeur liaisonServ  = null ; 
    private boolean fini = false ; 
    private ServerSocket host  =null; 
    private String mdp = "Minehive" ; 
    public Host (String ipServ , int portServ , String nom ,String ip, int port , String mdp) {
	this.port = port ; 
	this.listeJoueur = Collections.synchronizedList(new ArrayList<JoueurConneter>());
	this.enCours = Collections.synchronizedList(new ArrayList<Joueur>());
	this.compteur = 0 ;
	this.completion =  0 ;
	this.nom = nom ;
	this.PORTSERV = portServ ; 
	this.IPSERV = ipServ;
	this.jeu = new Demineur () ; 
	if(mdp != null) {
	    this.mdp = mdp ; 
	}
	System.out.println("IP : " + ip + " | " + "IPSERV : " + ipServ ) ; 
	try {
	    InetAddress adresse =InetAddress.getByName(ip);
	    this.IP = ip ; 
	}
	catch (UnknownHostException ue) {
	    System.out.println("host inconnue") ;
	    System.exit(0) ; 
	}
    }

    public int getPort (){
	return this.port ;
    }

    public String getIP (){
	return this.IP ;
    }

    public void affichEncours () {
	for (int i = 0 ; i <enCours.size() ; i ++) {
	    System.out.println(enCours.get(i) ) ; 
	}

    }

    public void affichListJoueur () {
	for (int i = 0 ; i < listeJoueur.size() ;i++) {
	    System.out.println("listJoueur " + listeJoueur.get(i) ) ; 
	}
    }
    public String information () {
	setCompletion() ;
	String info  = this.IP +"#" + this.port + "#" + this.nom+"#"+ this.completion;
	for (int i  = 0 ; i < this.enCours.size() ; i ++){
	    info += "#"+this.enCours.get(i).getPseudo() +"#"+ this.enCours.get(i).getPointPartie() ;
	} 
	return info ; 
    }

    public void setCompletion (){
	this.completion = jeu.completion() ;
    }

    public int getCompletion () {
	return this.completion ;
    }

    public void setNom (String nom){
	this.nom = nom ;
    }

    public String getNom(){
	return this.nom ; 
    }

    public void decoJoueur (Joueur j){
	for (int i = 0 ; i < enCours.size() ; i++ ){
	    Joueur a = enCours.get(i);
	    if(a.getPseudo() == j.getPseudo())
		enCours.get(i).setActif(false);
	}
	String mess = "DECO#"+j.getPseudo() ; 
	messageAtous(mess) ; 
	jeu.decrementeMultiplieur() ; 
    }

    public void coJoueur (Joueur j ){
	for (int i = 0 ; i < enCours.size() ; i++ ){
	    Joueur a = enCours.get(i);
	    if(a.getPseudo() == j.getPseudo())
		enCours.get(i).setActif(true);
	}
    }

    public void ajoutPoint (Joueur j , int point, int caseOK , int caseMine ) {
	for(int i = 0 ; i < enCours.size() ; i ++) {
	    Joueur a = enCours.get(i);
	    if(a.getPseudo() == j.getPseudo()){
		enCours.get(i).ajouterPointPartie(point) ; 
		enCours.get(i).caseIncremente(caseOK); 
		enCours.get(i).mineIncrement(caseMine) ; 
	    }
	}
    }

    public void messageAtous (String message){
	for (int i = 0 ; i < listeJoueur.size(); i ++){
	    if(this.listeJoueur.get(i).client != null)
		this.listeJoueur.get(i).messageEnvoi(message); 
	}
    }
    
    public void messageAuAutre (String message, Joueur j){
	for (int i = 0 ; i < listeJoueur.size(); i ++){
	    if(this.listeJoueur.get(i).client != null)
		if(!j.getPseudo().equals(listeJoueur.get(i).client.getPseudo() )) 
		    this.listeJoueur.get(i).messageEnvoi(message); 
	}
    }

    public void finition () {
	if(enCours.size() > 0) {
	    int max = 0 ;
	    int indiceMax = 0 ; 
	    int max2 = 0 ;
	    int indiceMax2 = 0; 
	    for( int i = 0 ; i < enCours.size();i++) {
		if(enCours.get(i).getCaseTrouver() >max ) {
		    max = enCours.get(i).getCaseTrouver () ; 
		    indiceMax = i  ; 
		}
	        if(enCours.get(i).getMine() > max2 ) {
		    max2 = enCours.get(i).getMine () ; 
		    indiceMax2 = i  ; 
		}
	    }
	    enCours.get(indiceMax).ajouterPointPartie(50);
	    enCours.get(indiceMax2).ajouterPointPartie(-50);
	}
    }
    public void fermetureHost() {
	System.out.println("HOST : dans fermeture" ) ; 
	finition() ; 
	System.out.println("HOST : apres finition" ) ; 
	for (int i = 0 ; i < enCours.size() ; i ++ ) {
	    System.out.println("HOST :  i :  " + i ) ; 
	    int totalPoint = enCours.get(i).getPoint()+ enCours.get(i).getPointPartie() ;
	    String mess0= "SCPS#" + enCours.get(i).getPseudo()+"#"+totalPoint ; 
	    if(liaisonServ != null) 
		liaisonServ.ecrire(mess0) ; 
	    String mess1 = "SCPC#"+ enCours.get(i).getPseudo() +"#"+ enCours.get(i).getPointPartie() +"#"+enCours.get(i).getPoint() +"#"+enCours.get(i).getCaseTrouver() + "#"+enCours.get(i).getMine();
	    messageAtous(mess1) ; 
	}
	if(liaisonServ != null ) 
	    liaisonServ.ecrire("ENDS#"+nom);
	messageAtous("ENDC#" + enCours.size() ) ; 
	liaisonServ.quitter() ;
	for(int i = listeJoueur.size()-1 ;i > -1  ;i--) {
	    listeJoueur.get(i).quitter() ; 
	    }
	fini = false ;
	try {
	    host.close() ;
	}
	catch (Exception e) {
	    System.exit(0) ;
	}
    }

    public void lancer () {
    	try {
	    host = new ServerSocket (this.port) ;
	    InetAddress adresse =InetAddress.getByName(IPSERV);
	    Socket serv = new Socket (adresse, PORTSERV) ; 
	    liaisonServ  = new connectionServeur(serv) ; 
	    Thread t0 = new Thread(liaisonServ); 
	    t0.start() ; 
	    Timer lancer =go() ;
	    lancer.start() ;
	    while(!fini){
		Socket sock = host.accept() ;
		JoueurConneter client = new JoueurConneter(sock) ;
		listeJoueur.add(client);
		Thread t = new Thread(listeJoueur.get(listeJoueur.indexOf(client))) ;
		t.start() ;
	    }
	}
	catch(IOException ie) {
	    System.out.println("HOST : fermeture") ; 
	    //ie.printStackTrace() ;
	}

    }

    class connectionServeur implements Runnable {
    	private Socket sock   = null ;
	private BufferedWriter bw = null ; 
	private BufferedReader br = null ;
	private boolean connection = false ; 
	private String reponseConnection = "" ;

    	public connectionServeur (Socket sock ) {
	    this.sock = sock ; 
	    try {
		this.br = new BufferedReader(new InputStreamReader(sock.getInputStream()));
		this.bw = new BufferedWriter(new OutputStreamWriter(sock.getOutputStream()));
		connection = true ; 
	    }
	    catch (IOException io ) {
		System.out.println("HOST : erreur creation Buffered Host " ) ;
	    }
    	}

	public boolean ecrire (String envoie){
	    try{
		bw.write(envoie) ;
		bw.newLine();
		bw.flush() ;
		return true ;
	    }
	    catch (IOException io){
		System.out.println("HOST : erreur lors de l'ecriture");
		try {
		    liaisonServ = null ; 
		    this.connection  = false ;
		    sock.close() ;
		    br.close() ;
		    bw.close() ;
		}
		catch(IOException io2) {
		    System.out.println("HOST :exception dans exception") ;
		}
		return false ;
	    }
	}

	public String lire () {
	    String message = "";
	    try {
		message = br.readLine() ;
		System.out.println("HOST : j ai recu : " + message);
		return message ;
	    }
	    catch(IOException io){
		System.out.println("HOST :erreur lors de la lecture ");
		try {
		    this.connection = false ;
		    liaisonServ = null ; 
		    sock.close() ;
		    br.close() ;
		    bw.close() ;
		}
		catch(IOException io2) {
		    System.out.println("HOST :exception dans exception") ;
		}
		return  null ;
	    }
	}

	public void connectionJoueur (String pseudo  , String mdp ) {
	    String envoie  = "PLIN#"+nom+"#"+pseudo+"#"+mdp ; 
	    ecrire (envoie) ; 
	}

	public void chercheClient (String [] decomposer) {
	    boolean fini = false ; 
	    System.out.println("HOST : dans chercheClient " + decomposer.length) ; 
	    if (decomposer.length ==3 ) {
		System.out.println("HOST : entrer") ;  
		for (int i = 0 ; i < enCours.size() ; i++) {
		    System.out.println("HOST : " + enCours.get(i).getPseudo() + " | " + decomposer[1]) ; 
		    if(enCours.get(i).getPseudo().equals(decomposer[1])){
			System.out.println("HOST  : j'ecrit IDIG " ) ;
			ecrire("IDIG#" +IP+"#"+port) ; 
			fini = true  ;
			break ; 
		    }

		}
	    }
	    if(!fini){ 
		System.out.println("HOST  : j'ecrit INIG " ) ; 
		ecrire ("INIG#") ; 
	    }
	}

	public void remplirReponseConnection(String recu , String [] decomposer ) {
	    if(decomposer.length > 1 ) {
		System.out.println("HOST : IN") ; 
		for (int i = 0 ; i < listeJoueur.size() ; i ++) {
		    System.out.println("HOST : listeJoueur " + i )  ; 
		    if(listeJoueur.get(i).essaie(decomposer[1])) {
			System.out.println("HOST : in ") ; 
			listeJoueur.get(i).reponse(recu) ; 
		    }
		}
	    }
	}

	public void quitter () {
	    this.connection = false  ;
	    liaisonServ = null ; 
	    try {
		
		sock.close() ;
		br.close () ;
		bw.close () ; 
	    }
	    catch (IOException io ) {
		System.out.println("HOST :erreur de quitter dans lexception ") ;
	    }
	}
	public void reponse (String recu ) {
	    String [] decomposer  = recu.split("#") ;
	    if(decomposer.length < 1 ) System.out.println("HOST :recu erreur ") ; 
	    else if(decomposer[0].equals("RQDT")) {
		if(decomposer.length > 1 ) {
		   ecrire ("SDDT#"+decomposer[1]+"#" + information());  
		}
		else ecrire ("SDDT#"+information()); 
	    }
	    else if((decomposer[0].equals("PLNO"))||(decomposer[0].equals("PLOK"))){
		System.out.println("HOST : Je remplie la connextion ") ;
		remplirReponseConnection(recu, decomposer ) ; 
	    }
	    else if(decomposer[0].equals("ID")) {
		System.out.println("HOST :cherche un client ") ; 
		chercheClient(decomposer) ; 
	    }
	    else if (decomposer[0].equals("IDKS")) {
		System.out.println("HOST : Serveur ne comprend pas " ) ; 
	    }
	    else if (decomposer[0].equals("RUOK") ) {
		System.out.println("HOST : recu RUOK") ; 
		ecrire ("IMOK") ; 
	    }
	}

	public void run () {
	    ecrire("LOGI#"+nom+"#"+mdp+"#"+port);
	    while (connection) {
		System.out.println("HOST  : Dans attente Serveur ") ; 
		String recu = lire () ; 
		if(recu != null) reponse(recu);
		else {
		    this.connection = false  ;
		    liaisonServ = null ; 
		    try {
			sock.close() ;
			br.close () ;
			bw.close () ; 
		    }
		    catch (IOException io ) {
			System.out.println("HOST :erreur de quitter dans lexception ") ;
		    }
		}
				     
	    }
	}

    }

    class JoueurConneter implements Runnable {
	private Socket sock ; 
	private BufferedWriter bw ; 
	private BufferedReader br ;
	private boolean connection ;  
	private Joueur client ; 
	public String essaieConnexion = null ;
	private String remMdp = ""; 
	private int temps = 0 ; 
	public JoueurConneter(Socket sock){
	    this.sock = sock ; 
	    this.connection = true ; 
	    try {
		this.br = new BufferedReader(new InputStreamReader(sock.getInputStream()));
		this.bw = new BufferedWriter(new OutputStreamWriter(sock.getOutputStream()));
		this.client = null ; 
	    }
	    catch (IOException io){
		System.out.println("HOST : erreur lors de la creation dun client host ");
	    }
	}

	public boolean estUnClient (){
	    if(this.client == null){
		return false ;
	    }
	    return true ;
	}

	public void ajouteTemps() {
	    this.temps ++ ; 
	    if(this.temps == 30 ) {
		try {
		    this.connection  = false ;
		    listeJoueur.remove(this);
		    if(estUnClient()){	 
			decoJoueur(client) ; 
			client = null ; 
		    }  
		    sock.close() ;
		    br.close() ;
		    bw.close() ;
		}
		catch(IOException io2) {
		    System.out.println("HOST : exception dans exception") ;
		}
	    }
	    else if (this.temps% 10 == 0){
		ecrire("RUOK") ; 
	    }
	}

	public void tempsIMOK() {
	    this.temps = 0 ; 
	}

	public String toString () {
	    String affich ="" ; 
	    if (estUnClient())
		affich += client.toString () ;
	    affich += " "+ connection ;
	    return affich ; 
	}
	public boolean essaie (String pseudo ) {
	    if (pseudo.equals(essaieConnexion) ) 
		return true ;
	    return false ; 
	}

	public boolean ecrire (String envoie){
	    try{
		bw.write(envoie) ;
		bw.newLine();
		bw.flush() ;
		return true ;
	    }
	    catch (IOException io){
		System.out.println("HOST : erreur lors de l'ecriture");
		try {
		    this.connection  = false ;
		    listeJoueur.remove(this);
		    if(estUnClient()){	 
			decoJoueur(client) ; 
			client= null ; 
		    }  
		    sock.close() ;
		    br.close() ;
		    bw.close() ;
		}
		catch(IOException io2) {
		    System.out.println("HOST : exception dans exception") ;
		}
		return false ;
	    }
	}

	public String lire () {
	    String message = "";
	    try {
		message = br.readLine() ;
		System.out.println("j ai recu : " + message);
		return message ;
	    }
	    catch(IOException io){
		System.out.println("HOST : erreur lors de la lecture ");
		try {
		    this.connection = false ;
		    listeJoueur.remove(this) ; 
		    if(estUnClient()){
			decoJoueur(client) ; 
			client = null ; 
		    }
		    sock.close() ;
		    br.close() ;
		    bw.close() ;
		}
		catch(IOException io2) {
		    System.out.println("HOST : exception dans exception") ;
		}
		return  null ;
	    }
	}

	public void messageEnvoi (String message){
	    ecrire(message) ; 
	}

	public void reconnexion (String [] decomposer) {
	    String reponse = "JNOK#" + jeu.getLargeur() ; 
	    ecrire(reponse) ;

	    for (int i = 0 ; i < jeu.getLargeur() ; i++){
		String lectureCase  ="BDIT#"+i;
		for (int j = 0 ; j < jeu.getLongueur() ; j ++)
		    lectureCase+= "#"+jeu.afficheCase(j,i) ;
		ecrire(lectureCase); 
	    }
	    String player = "IGNB#"+ enCours.size() ; 
	    ecrire(player);
	    for (int i  = 0 ; i < enCours.size() ;  i++ ) {
		Joueur j = enCours.get(i); 
		int totalPoint = j.getPoint() + j.getPointPartie() ; 
		String message = "IGPL#"+ j.getPseudo() +"#"+ j.getPointPartie() +"#" + totalPoint +"#" + j.getCaseTrouver() +"#"+j.getMine()  ;
		ecrire(message); 
	    }
	    int totalPoint = client.getPoint() + client.getPointPartie() ;
	    String nouveau = "CONN#"+client.getPseudo() + "#"+client.getPointPartie()+ "#"+ totalPoint + "#" +  client.getCaseTrouver() +"#"+client.getMine()  ;
	    messageAuAutre(nouveau , client) ; 
	    jeu.incrementMultiplieur() ; 
	}

	public void inscription (String [] decomposer){
	    boolean fini = false ; 
	    if(decomposer.length != 3){
		ecrire("JNNO" ); 
		fini = true ; 
	    }
	    if (liaisonServ == null){
		ecrire("JNNO" ); 
		fini = true ; 
	    } 
	    if(!fini) 
	    for (int i = 0 ; i < enCours.size() ; i++){
		if(enCours.get(i).getPseudo().equals(decomposer[1])){
		    if(enCours.get(i).getMdp().equals(decomposer[2]) ){
			if(enCours.get(i).getActif()){
			}
			else {
			    enCours.get(i).setActif(true) ;
			    client = enCours.get(i) ; 
			    reconnexion (decomposer) ; 
			    fini = true ;
			    
			}
		    }
		    if(!fini) {
		    ecrire("JNNO"); 
		    fini = true ;
		    }
		}
	    }
	    if (!fini) {
		essaieConnexion = decomposer[1] ;
		remMdp = decomposer[2] ; 
		liaisonServ.connectionJoueur(decomposer[1],decomposer[2]) ; 
		
	    }
	}
	
	public void inscriptionOK (String [] decomposer ) {
	    essaieConnexion = null ; 
	    if(decomposer.length >= 3 ){  
		try {
		    int p = Integer.parseInt(decomposer[2] ) ; 
		    System.out.println("HOST  : creation client");
		    client = new Joueur (decomposer[1], remMdp,p ) ;
		    enCours.add(client) ;
		    reconnexion(decomposer) ; 
		}
		catch(Exception e) {
		    ecrire("JNNO") ;
		}
	    }
	    else {
		ecrire("JNNO") ; 
	    }
	}
	public void autre () {
	    ecrire("IDKH") ; 
	}
	
	public synchronized void jouer (String [] decomposer ) {
	    if (decomposer.length == 3 ) {
		try {
		    int x = Integer.parseInt(decomposer[1]) ;
		    int y = Integer.parseInt(decomposer[2]) ; 
		    if (( x < 0) ||(x >= jeu.getLongueur())||(y < 0 )||(y>=jeu.getLargeur())){
			ecrire("OORG#"+decomposer[1] + "#"+decomposer[2]);
		    }
		    else {
			int c = jeu.jouer(x,y,client.getPseudo()) ; 
			if ( c == 1) {
			    ecrire("LATE") ; 
			}
			else if (c == 2 ) {
			    ecrire("OORG#"+decomposer[1] + "#"+decomposer[2]);
			}
			else { 
			    int point = 0 ; 
			    int caseOK = 0 ;
			    int caseMine = 0 ; 
			    for(int i = 0 ;  i < jeu.envoie.size()  ; i++ ) {
				String []  couper = jeu.envoie.get(i).split("#") ;
				int u = Integer.parseInt(couper[4]) ; 
				int ca = Integer.parseInt(couper[3]) ; 
				point += u ; 
				if(ca != -1 )
				    caseOK ++; 
				else 
				    caseMine ++ ; 
				System.out.println("HOST " + jeu.envoie.get(i) );
				messageAtous(jeu.envoie.get(i)) ;
				//jeu.envoie.remove(i) ; 
			    }
			    for(int i =0 ; i < jeu.envoie.size() ; i++){
				jeu.envoie.remove(i);
			    }
			    client.ajouterPointPartie(point) ; 
			    client.caseIncremente (caseOK); 
			    client.mineIncrement (caseMine) ; 
			    if(jeu.estfini()) {
				fermetureHost() ; 
			    }
			}
		    }
		}
		catch (Exception e) {
		    ecrire("OORG#"+decomposer[1] + "#"+decomposer[2]);
		    e.printStackTrace() ; 
		}
	    }
	    else {
		autre();
	    }
	}

	public void quitter () {
	    this.connection = false  ;
	    try {
		sock.close() ;
		br.close () ;
		bw.close () ; 
	    }
	    catch (IOException io ) {
		System.out.println("HOST :erreur de quitter dans lexception ") ;
	    }
	}

	public void reponse (String recu){
	    String [] couper = recu.split("#");
	    if(couper.length < 1) System.out.println("HOST : Mauvaise Reponse du client") ; 
	    if((couper[0].equals("JOIN"))&&(!estUnClient())){
		System.out.println("HOST : s'enregistrer a la partie ");
		inscription(couper);
	    }
	    else if( (couper[0].equals("PLOK"))&&(!estUnClient())) {
		System.out.println("HOST : ajout joueur " ) ;
		inscriptionOK(couper);  
		affichEncours();
		affichListJoueur() ;
	    }
	    else if( (couper[0].equals("PLNO"))&&(!estUnClient())) {
		System.out.println("HOST : refus joueur " ) ;
		ecrire("JNNO") ;   
		affichEncours() ;
		affichListJoueur() ;
	    }
	    else if ((couper[0].equals("CLIC"))&&(estUnClient())){
		jouer(couper) ; 
	    }
	    else if (couper[0].equals("IMOK")){
		tempsIMOK() ; 
	    }
	    else if(couper[0].equals("IDKE") ){
	    }
	    else {
		ecrire("IDKH") ; 
	    }
	}

	public void run () {
	    while (connection) {
		System.out.println("HOST : je suis dans attente HOST ");
		String recu = lire();
		if(recu != null) reponse(recu);
		else {
		    this.connection = false ;
		    try {
			sock.close () ;
			br.close() ;
			bw.close() ;
			listeJoueur.remove(this);
			if(estUnClient()){
			    decoJoueur(client);
			    client = null ; 
			}
		    }
		    catch(IOException io){
			System.out.println("HOST  : fermeture bug");
		    }
		}
	    }	
	}
    }

    public Timer go () {
	ActionListener action = new ActionListener ()
	    {
		public void actionPerformed (ActionEvent event)
		{
		    for (int i  = 0 ; i < listeJoueur.size() ; i ++){
			listeJoueur.get(i).ajouteTemps() ;
		    }
		}
	    };
	return new Timer(1000,action) ;
    }
}
