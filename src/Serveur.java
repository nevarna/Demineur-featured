//AUTEUR  : Navarna 
import java.io.*;
import java.net.*;
import java.util.*;
import java.awt.event.*;
import javax.swing.Timer ;
import java.lang.ProcessBuilder.Redirect;
public class Serveur {

    final static  int port = 5555 ;
    final static int [] portHost = {7000 ,7001,7002,7003,7004,7005,7006,7007,7008,7009};
    final static int NUM_HOST  =10;
    final static int portEcouteHost = 7777 ; 
    final static int NUM_JOUEUR_MAX = 10 ; 
    static int portSecours = 7010 ; 
    static InetAddress IPServeur  = null ; 
    List <LiaisonHost> tabHost ;
    List <Joueur>  listeJoueur ;
    List <Joueur> connecter ;
    List <Attente> listeClient ;
    protected int nb_partie ;
    protected int nb_partieNom = 0 ;
    static String mdp = "Minehive";
    public Serveur (String IP){
    	InitialisationJoueur();
    	this.nb_partie = 0 ; 
    	this.connecter = Collections.synchronizedList(new ArrayList<Joueur>()) ;
    	this.tabHost = Collections.synchronizedList(new ArrayList <LiaisonHost>());
	try {
	    IPServeur = InetAddress.getByName(IP) ; 
	}
	catch(Exception e) {} 
	demarrerServeur() ; 
    }

    public int hostLibre () {
    	int i = this.tabHost.size () ;
    	if(i  >=  10)
	    return -1 ;
    	return i ; 
    }

    public boolean trouverString (String [] tableau , String mot){
    	for (int i = 0 ; i < tableau.length; i++) {
	    if(mot.equals(tableau[i])){
		return true ;
	    }
    	}
    	return false ;
    }

    public synchronized void ajoutePartie (String [] inviter) {
    	int indiceHost = hostLibre() ;
	int nbPartieCourant  = this.nb_partie ; 

	try {
	    nb_partieNom ++;
	    String nom = "Partie_"+ (nb_partieNom) ; 
	    lancerHost(nom,indiceHost) ;
	   
	    System.out.println("SYSTEM : part " + nb_partie + " / " + nbPartieCourant ) ; 
	    while(nb_partie == nbPartieCourant ){
		System.out.println("SYSTEM : part " + nb_partie + " / " + nbPartieCourant ) ;
		try {
		Thread.sleep(1000) ;
		}
		catch(Exception e) {
		    System.out.println("erreur" );
		}
	    } 
	    this.tabHost.get(nbPartieCourant).setPort(portHost[indiceHost]) ; 
	    String invitation = "NWOK#"+this.tabHost.get(nbPartieCourant).getIP() +"#"+portHost[indiceHost];
	    if(inviter != null) {
		this.tabHost.get(nbPartieCourant).setPrive(true); 
	    }
	    System.out.println("SERVEUR je suis dedans ") ; 
	    for(int i = 0 ; i < this.listeClient.size(); i++){
		 System.out.println("SERVEUR i :  " + i) ;
		if(this.listeClient.get(i).estUnClient() ) {
		    if((inviter == null)||(trouverString(inviter,this.listeClient.get(i).client.getPseudo()))){
			this.listeClient.get(i).recuInvitation(invitation);
			if(inviter != null) {
			    this.tabHost.get(nbPartieCourant).setAutorise(this.listeClient.get(i).client.getPseudo() , this.listeClient.get(i).client.getMdp());
			}
		    }
		}
	    }
	    System.out.println("SERVEUR : invitation Fini ") ; 
	}
	catch (Exception e ) {
	    System.out.println("SERVEUR  : erreur de lancement d'host" ) ;
	    e.printStackTrace() ; 
	}
    }

    public synchronized void supprimePartie () {
    	this.nb_partie -- ;
    }

    public void ChercheIMOK(InetAddress ipIMOK , int portIMOK){
    	for (int i =0 ; i < this.listeClient.size() ; i ++){
	    if(this.listeClient.get(i).clientTrouver(ipIMOK,portIMOK)){
		this.listeClient.get(i).tempsIMOK() ;
		i = this.listeClient.size() ;
	    }
    	}
    }

    public synchronized String chercheClientHost (String pseudo , String mdp){
    	String info = null ;
    	for (int i = 0 ; i < tabHost.size() ; i++){
	    System.out.println("SERVEUR  : j'attend un host  "+ i ) ; 
	    info = tabHost.get(i).chercheClient( pseudo , mdp) ; 
	    System.out.println("SERVEUR : information recu " )  ; 
	    if(info != null ) return info ; 
    	}
    	return info ;
    }

    public  void InitialisationJoueur () {
    	xml fichier = new xml() ;
    	this.listeJoueur = fichier.lireElement();
    	this.listeClient = Collections.synchronizedList(new ArrayList<Attente>());
    	System.out.println("Chargement de la liste de joueurs");
    	
    }

    public void demarrerServeur(){
	try {
	    ServerSocket serv = new ServerSocket (this.port) ;
	    //IPServeur = InetAdress.getAdress()  ; 
	System.out.println(IPServeur) ;
	    Timer lancer =go() ;
	    lancer.start() ;
	    AttenteHost ah = new AttenteHost (this.portEcouteHost) ; 
	    Thread t0 = new Thread(ah);
	    t0.start()  ;  
	    while(true){
		Socket sock = serv.accept() ;
		Attente client = new Attente(sock) ;
		listeClient.add(client);
		Thread t = new Thread(listeClient.get(listeClient.indexOf(client))) ;
		t.start() ;
	    }
	}
	catch(IOException ie) {
	    ie.printStackTrace() ;
	}
    }

    public void afficheJoueur() {
	System.out.println("Liste des joueurs");
	for (int i =0 ; i < listeJoueur.size() ; i++){
	    System.out.println(listeJoueur.get(i));
	}
    }

    public void afficheConnecter() {
	System.out.println("Liste des connecter");
	for(int i =0 ; i < connecter.size(); i++){
	    System.out.println(connecter.get(i));
	}
    }

    public void lancerHost(String nom ,int indiceHost) {
	boolean fini  = false ; 
	while (!fini) {
	    System.out.println("SERVEUR : creation de l host port : " +portHost[indiceHost] ) ; 
	    try {
		ServerSocket test = new ServerSocket(portHost[indiceHost])  ;
		test.close () ; 
		//	Runtime runTime = Runtime.getRuntime();
		List <String> arg = new ArrayList<String>() ;
		arg.add("java") ;
		arg.add("-jar") ; 
		arg.add("Host.jar"); 
		System.out.println(IPServeur.getHostAddress()); 
		arg.add(IPServeur.getHostAddress()) ;
		arg.add(""+portEcouteHost)  ;
		arg.add( nom ) ;
		arg.add(IPServeur.getHostAddress() ) ;
		arg.add(""+portHost[indiceHost] ) ; 
		arg.add(mdp) ; 
		//	ProcessBuilder pb = new ProcessBuilder("java HostMain " +IPServeur.getHostAddress() +" " +portEcouteHost +" " + nom + " "+IPServeur.getHostAddress() + " " +portHost[indiceHost] + " " + mdp);
		ProcessBuilder pb = new ProcessBuilder(arg) ; 
		//	pb.directory(new File("./Dir"));
		 File log = new File("log.txt");
		 pb.redirectErrorStream(true);
		 pb.redirectOutput(Redirect.appendTo(log));


		final Process process = pb.start() ; 

		 
		 // Process p = pb.start();
		 assert pb.redirectInput() == Redirect.PIPE;
		 assert pb.redirectOutput().file() == log;
		 assert process.getInputStream().read() == -1;
		    //	final Process process = runTime.exec("java HostMain " +IPServeur.getHostAddress() +" " +portEcouteHost +" " + nom + " "+IPServeur.getHostAddress() + " " +portHost[indiceHost] + " " + mdp);
		/*	new Thread() {
		    public void run() {
			try {
			    BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
			    String line = "";
			    try {
				while((line = reader.readLine()) != null) {
				    System.out.println(line) ; 
				}
			    } finally {
				reader.close();
			    }
			} catch(IOException ioe) {
			    ioe.printStackTrace();
			}
		    }
		}.start();

		// Consommation de la sortie d'erreur de l'application externe dans un Thread separe
		new Thread() {
		    public void run() {
			try {
			    BufferedReader reader = new BufferedReader(new InputStreamReader(process.getErrorStream()));
			    String line = "";
			    try {
				while((line = reader.readLine()) != null) {
				    System.out.println(line ) ; 
				}
			    } finally {
				reader.close();
			    }
			} catch(IOException ioe) {
			    ioe.printStackTrace();
			}
		    }
		    }.start();*/
		fini = true ; 
	    }
	    catch (BindException be) {
		//be.printStackTrace() ;
		portHost[indiceHost] = portSecours ;
		portSecours ++ ; 
	    }
	    catch(IOException io ) {
		System.out.println("SERVEUR  : erreur IO") ;
		io.printStackTrace();
		fini = true ; 
		
	    }
	    System.out.println("SERVEUR  : j ai finin ") ; 
	}
    }

    class AttenteHost implements Runnable {
    	private int port  = 0 ;

    	public AttenteHost (int p){
	    this.port  = p ; 
    	}

    	public void run () {
	    try  {
		ServerSocket servHost = new ServerSocket(this.port);
		while (true){
		    Socket host  = servHost.accept() ; 
		    LiaisonHost nouveauHost = new LiaisonHost(host);
		    //tabHost.add(nouveauHost) ;
		    Thread t  = new Thread(nouveauHost);
		    t.start() ;  
		}
	    }
	    catch (IOException io){
		System.out.println("erreur boucle connection host"); 
	    }
    	}
    }

    class Attente implements Runnable{
	private Socket sock  ;
	private InetAddress IPClient ;
	private int portClient ;
	BufferedWriter bw ;
	BufferedReader br ;
	boolean creationOK = false ;
	boolean connection ;
	protected Joueur client ;
	private int [] temps ;
	public Attente (Socket sock) {
	    this.sock = sock ;
	    try {
		this.br = new BufferedReader(new InputStreamReader(sock.getInputStream()));
		this.bw = new BufferedWriter(new OutputStreamWriter(sock.getOutputStream()));
		IPClient = sock.getInetAddress() ;
		portClient = sock.getPort();
		this.creationOK = true ;
		this.connection = true ;
		this.client = null;
		this.temps = new int [2];
		tempsACTIF();
		tempsIMOK() ;
	    }
	    catch (IOException io){
	    	System.out.println("erreur lors de la creation des BufferedWriter et BufferredReader");
	    }
	}


	public boolean getConnection () {
	    return this.connection ;
	}

	public void setConnection (boolean connection){
	    this.connection = connection ;
	}

	public boolean estUnClient (){
	    if(this.client == null){
		return false ;
	    }
	    return true ;
	}

	public void ajouteTemps (){
	    this.temps[0] ++ ;
	    this.temps[1] ++ ;
	    if((temps[0] == 30 )||(temps[1] == 300)){
		quitter(true) ;
	    }
	    else if (this.temps[0] % 10 == 0 ) {
		ecrire("RUOK") ; 
	    }

	}

	public void tempsIMOK() {
	    this.temps[0]= 0 ;
	}

	public void tempsACTIF() {
	    this.temps[1] = 0 ;
	}

	public boolean clientTrouver (InetAddress ipIMOK , int portIMOK){
	    if((this.IPClient.equals(ipIMOK))&&(portClient == portClient)){
		return true ;
	    }
	    else return false ;
	}

	public boolean ecrire (String envoie){
	    try{
		bw.write(envoie) ;
		bw.newLine();
		bw.flush() ;
		return true ;
	    }
	    catch (IOException io){
		System.out.println("SERVEUR : erreur lors de l'ecriture");
		try {
		    this.connection  = false ;
		    if(estUnClient())
			connecter.remove(client);
		    listeClient.remove(this);
	       
		    sock.close() ;
		    br.close() ;
		    bw.close() ;
		}
		catch(IOException io2) {
		    System.out.println("SERVEUR  : exception dans exception") ;
		}
		return false ;
	    }
	}

	public String lire () {
	    String message = "";
	    try {
		message = br.readLine() ;
		System.out.println("SERVEUR  : j ai recu : " + message);
		return message ;
	    }
	    catch(IOException io){
		System.out.println("SERVEUR : erreur lors de la lecture ");
		try {
		    this.connection = false ;
		    if(estUnClient())
			connecter.remove(client);
		    listeClient.remove(this);
		    
		    sock.close() ;
		    br.close() ;
		    bw.close() ;
		}
		catch(IOException io2) {
		    System.out.println("exception dans exception") ;
		}
		return  null ;
	    }

	}

	public String inscription (String [] decomposer){
	    String reponse = "IDNO";
	    if (decomposer.length == 3 ) {
		for (int i = 0 ; i < connecter.size() ; i ++){
		    if (connecter.get(i).getPseudo().equals(decomposer[1])){
			if(connecter.get(i).getMdp().equals(decomposer[2])){
			    reponse = "IDNO";
			    return reponse ;
			}
		    }
		}
		String rep =  chercheClientHost(decomposer[1] , decomposer[2]) ; 
		if(rep != null) return rep ; 
		for (int i = 0 ; i < listeJoueur.size(); i++){
		    if (listeJoueur.get(i).getPseudo().equals(decomposer[1])){
			if(listeJoueur.get(i).getMdp().equals(decomposer[2])){
			    reponse = "IDOK#"+decomposer[1]+"#"+decomposer[2];
			    connecter.add(listeJoueur.get(i));
			    this.client = listeJoueur.get(i);
			}
			return reponse ;
		    }
		}
		Joueur nouveau  = new Joueur (decomposer[1], decomposer[2], 0 ) ;
		listeJoueur.add(nouveau);
		connecter.add(nouveau);
		reponse = "IDOK#"+decomposer[1]+"#"+decomposer[2];
		this.client = nouveau ;
	    }
	    return reponse ;
	}

	public void testegalite (String [] decomposer){
	    String reponse = "IDNO";
	    if (decomposer.length == 3 ) {
		for (int i = 0 ; i < listeJoueur.size(); i++){
		    System.out.print("in");
		    System.out.print(listeJoueur.get(i).getPseudo() + " ");
		    System.out.print (decomposer[1] + "|" );
		    System.out.print ((listeJoueur.get(i).getMdp().equals(decomposer[2])) + " , ");
		    System.out.println(listeJoueur.get(i).getPseudo().equals(decomposer[1] ));
		}
	    }
	}

	public void ListeMatch () {
	    String envoie = "LMNB#"+nb_partie;
	    ecrire(envoie) ;
	    envoie = "" ;
	    for (int i = 0 ; i < tabHost.size() ; i++){
		envoie = "MATC#" + tabHost.get(i).information() ;
		ecrire(envoie);
		envoie = "";
	    }
	}

	public void JoueurLibre () {
	    ArrayList<String> envoie = new ArrayList<String>();
	    int compteur = 0;
	    for(int i = 0 ; i < connecter.size() ; i ++){
		if(connecter.get(i).getEtat()){
		    String nouveau = "AVAI#"+connecter.get(i).getPseudo() +"#"+ connecter.get(i).getPoint();
		    envoie.add(nouveau);
		    compteur ++ ;
		}
	    }
	    String debut  = "LANB#"+compteur;
	    ecrire(debut);
	    for (int i = 0 ; i < envoie.size(); i++){
		ecrire(envoie.get(i));
	    }
	}

	public void JoueurInscrit() {
	    String envoie = "LUNB#"+listeJoueur.size();
	    ecrire(envoie);
	    for(int i = 0 ; i < listeJoueur.size() ; i++) {
		envoie = "USER#"+listeJoueur.get(i).getPseudo()+"#"+listeJoueur.get(i).getPoint();
		ecrire(envoie);
	    }
	}

	public void quitter (boolean kick ) {
	    if(estUnClient()){
		connecter.remove(client);
		listeClient.remove(this);
	    }
	    if(kick) 
		ecrire("KICK");
	    this.connection  = false ;
	    try {
		System.out.println("fermeture de la socket");
		sock.close() ;
		br.close() ;
		bw.close() ;
	    }
	    catch (IOException io){
		System.out.println("erreur lors de la fermeture");
	    }
	}

	public void autre() {
	    String reponse = "IDKS";
	    if(!estUnClient()){
		reponse = "IDNO#Pas connecter";
	    }
	    ecrire(reponse);
	}

	public void nouvellePartie(String [] decomposer){
	    String envoie = "";
	    if (nb_partie < 10){
		if (decomposer.length < 2){
		    envoie ="IDKS";
		    ecrire(envoie);
		}
		else if (decomposer.length == 2){
		    if( decomposer[1].equals("ALL")){
			ajoutePartie(null);
		    }
                    else {
                        String [] arguments = new String[decomposer.length];
                        arguments[0] = client.getPseudo() ;
                        for (int i = 1 ; i < decomposer.length; i++){
			    arguments[i] = decomposer[i];
                        }
                        ajoutePartie(arguments);
                    }
		}
		else {
		    String [] arguments = new String[decomposer.length];
		    arguments[0] = client.getPseudo() ;
		    for (int i = 1 ; i < decomposer.length; i++){
			arguments[i] = decomposer[i];
		    }
		    ajoutePartie(arguments);
		}
	    }
	    else {
		ecrire("FULL");
	    }

	}

	public void recuInvitation (String invitation ) {
	    ecrire(invitation);
	}

	public void reponse(String recu){
	    String [] couper = recu.split("#");
	    tempsACTIF() ;
	    if(couper.length < 1 ) autre() ; 
	    else if((couper[0].equals("REGI"))&&(!estUnClient())){
		System.out.println("s'enregistrer au jeu ");
		String reponse = inscription(couper);
		ecrire (reponse);
	    }
	    else if ((couper[0].equals("LSAV"))&&(estUnClient())){
		System.out.println("joueur libre");
		JoueurLibre() ;
	    }
	    else if ((couper[0].equals("LSUS"))&&(estUnClient())){
		System.out.println("liste des joueur");
		JoueurInscrit();
	    }
	    else if (couper[0].equals("LEAV")){
		System.out.println("quitter le serveur");
		quitter(false) ;
	    }
	    else if ((couper[0].equals("LSMA"))&&(estUnClient())){
		System.out.println("liste des match en cours ");
		ListeMatch() ;
	    }
	    else if ((couper[0].equals("NWMA"))&&(estUnClient())){
		System.out.println("nouvelle partie");
		nouvellePartie(couper);
	    }
	    else if (couper[0].equals("IDKE")){}
	    else if (couper[0].equals("IMOK") ){
		tempsIMOK() ; 
	    }
	    
	    else
		autre();
	}



	public void run () {

	    while (connection) {
		System.out.println("je suis dans attente");
		String recu = lire();
		if(recu != null) reponse(recu);
		else {
		    this.connection = false ;
		    try {
			sock.close () ;
			br.close() ;
			bw.close() ;
			if(estUnClient()){
			    connecter.remove(client);
			    listeClient.remove(this);
			}
		    }
		    catch(IOException io){
			System.out.println("fermeture bug");
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
		    for (int i  = 0 ; i < listeClient.size() ; i ++){
			listeClient.get(i).ajouteTemps() ;
		    }
		    for (int i = 0 ; i < tabHost.size() ; i++) {
			tabHost.get(i).ajouteTemps() ; 
		    }
		    
		}
	    };
	return new Timer(1000,action) ;
    }


    class LiaisonHost implements Runnable {
    	Socket sock ; 
    	boolean creationOK  = false ;
    	boolean connection = false ; 
    	private BufferedReader br =  null ; 
    	private BufferedWriter bw = null ; 
	private InetAddress IPHost = null ;
	private int portHost = 0 ; 
    	private String info = "" ;
    	private String  [][] autorise ; 
	private boolean prive = false ; 
	private boolean essaieConnexion = false ; 
	private String searchClient = "" ; 
	private int temps = 0 ; 
	private List <Joueur> ingame ; 
    	public LiaisonHost(Socket sock) {
	    this.sock  = sock ; 
	    try {
		this.br = new BufferedReader(new InputStreamReader(sock.getInputStream()));
		this.bw = new BufferedWriter(new OutputStreamWriter(sock.getOutputStream()));
		this.ingame = Collections.synchronizedList(new ArrayList<Joueur>()) ;
		IPHost = sock.getInetAddress() ;
		portHost = sock.getPort();
		this.creationOK = true ;
		this.connection = true ;
		autorise = new String [10][2] ;
		
	    }
	    catch (IOException io){
	    	System.out.println("erreur lors de la creation des BufferedWriter et BufferredReader");
	    }
	}

	public void setPrive (boolean prive) {
	    this.prive = prive ; 
	}
	
	public boolean getPrive () {
	    return this.prive ; 
	} 
	public String getIP() {
	    String rep  = IPHost.toString() ; 
	    if (rep.charAt(0) == '/')
		rep = rep.substring(1,rep.length());
	    return rep ; 
	}

	public int getPort () {
	    return this.portHost ; 
	}

	public void setPort(int p ) {
	    this.portHost = p ; 
	}
	   		
	public boolean setAutorise (String pseudo , String mdp ){
	    for (int i  = 0 ;i < autorise.length ; i ++){
	    	if (autorise[i][0] == null){
		    autorise[i][0] = pseudo ;
		    autorise[i][1] = mdp ; 
		    return true ; 
	    	}
	    }
	    return false ; 
	}

	public boolean estAutorise (String pseudo, String mdp){
	    for (int i = 0 ; i < NUM_JOUEUR_MAX ; i++){
		if(autorise[i][0] == null) 
		    break ; 
		else 
		    if (this.autorise[i][0].equals(pseudo) ){
		    if(this.autorise[i][1].equals(mdp))
			return true ;
		}
	    }
	    return false ; 
	}

	public void ajouteTemps () {
	    this.temps ++ ;
	    if(this.temps == 45) {
		try {
		    sock.close () ;
		    br.close() ;
		    bw.close() ;
		    nb_partie -- ; 
		    tabHost.remove(this);	
		}
		catch(IOException io){
		    System.out.println("fermeture IMOK host");
		}
	    }
	    else if (this.temps % 15 == 0 ) {
		ecrire("RUOK"); 
	    }
	}

	public void tempsIMOK () {
	    this.temps =  0 ; 
	}

	public synchronized String chercheClient ( String pseudo , String mdp) {
	    /*ecrire("ID#"+pseudo+"#"+mdp) ; 
	    while (searchClient == "") {
		if(essaieConnexion) {
		    System.out.println("SERVEUR : je suis dans chercheClient (serveur)");
		    String recu = lire();
		    if(recu != null) reponse(recu);
		    else {
			this.connection = false ;
			try {
			    sock.close () ;
			    br.close() ;
			    bw.close() ;
			    nb_partie -- ; 
			    tabHost.remove(this);	
			}
			catch(IOException io){
			    System.out.println("fermeture bug host");
			}
		    }
		}
		else {
		    try {
			Thread.sleep(200) ; 
		    }
		    catch(Exception e ) {
			System.out.println("SERVEUR  : attente de l host ") ; 
		    }
		}
		}*/
	    String rep = null ;
	    for (int i = 0 ;  i  < ingame.size() ; i++) {
		if(ingame.get(i).getPseudo().equals(pseudo )) {
		    if(ingame.get(i).getMdp().equals(mdp))
			rep = "IDIG#" +getIP()+"#"+portHost;
		} 
	    }
	    //String rep = searchClient ; 
	    //searchClient = "" ;
	    return rep ; 
	}

	public boolean ecrire (String envoie){
	    try{
		bw.write(envoie) ;
		bw.newLine();
		bw.flush() ;
		return true ;
	    }
	    catch (IOException io){
		System.out.println("erreur lors de l'ecriture");
		try {
		    this.connection  = false ;
		    tabHost.remove(this) ; 
		    sock.close() ;
		    br.close() ;
		    bw.close() ;
		}
		catch(IOException io2) {
		    System.out.println("exception dans exception") ;
		}
		return false ;
	    }
	}

	public String lire () {
	    String message = "";
	    try {
		message = br.readLine() ;
		System.out.println("SERVEUR  : j ai recu : " + message);
		return message ;
	    }
	    catch(IOException io){
		System.out.println("erreur lors de la lecture ");
		try {
		    this.connection = false ;
		    tabHost.remove(this) ;
		    sock.close() ;
		    br.close() ;
		    bw.close() ;
		}
		catch(IOException io2) {
		    System.out.println("exception dans exception") ;
		}
		return  null ;
	    }

	}

    	public String  information () {
	    ecrire("RQDT#none") ;  
	    while (info.equals("")){
	    }
	    String rep = info ; 
	   
	    info = "" ;
	    return rep ; 
    	}

	public void remplirInfo (String recu ) {
	    if(recu.length() > 10 ) {
		
		    String rep = recu.substring(10) ; 
		    info = rep ;  
		}
	}

	public void remplirSearch (boolean ok , String recu) {
	    if(ok) 
		searchClient =  recu ; 
	    else 
		searchClient = null ;
	}

	public String connexionHost (String [] decomposer  ) {
	    String reponse = "IDKS";
	    if (decomposer.length == 4 ) {
		reponse = "PLNO#"+decomposer[2] ; 
		for (int i = 0 ; i < connecter.size() ; i ++){
		    if (connecter.get(i).getPseudo().equals(decomposer[2])){
			if(connecter.get(i).getMdp().equals(decomposer[3])){
			    if((!prive)||(estAutorise(decomposer[2],decomposer[3]))){
				reponse ="PLOK#"+decomposer[2] + "#" + connecter.get(i).getPoint();
				ingame.add(connecter.get(i)); 
				for(i = 0 ; i < listeClient.size() ; i++ ) {
				    if(decomposer[2].equals(listeClient.get(i).client.getPseudo())){
					listeClient.get(i).quitter(false) ; 
				    }
				} 
			    }
			    return reponse ;
			}
		    }
		}
		this.essaieConnexion = true; 
		String rep =  chercheClientHost(decomposer[2] , decomposer[3]) ;
		this.essaieConnexion = false ; 
		if(rep != null) return "PLNO#"+decomposer[2] ; 
		for (int i = 0 ; i < listeJoueur.size(); i++){
		    if (listeJoueur.get(i).getPseudo().equals(decomposer[2])){
			if(listeJoueur.get(i).getMdp().equals(decomposer[3])){
			     if((!prive)||(estAutorise(decomposer[2],decomposer[3]))){
				 reponse = "PLOK#"+decomposer[2]+"#"+listeJoueur.get(i).getPoint();
				 ingame.add(listeJoueur.get(i)) ; 
			     }
			}
			return reponse ;
		    }
		}
		return reponse  ;
	    }
	    return reponse ;
	}

	public void changementPoint (String [] decomposer) {
	    if(decomposer.length> 2 ) {
		String nom = decomposer[1] ; 
		try {
		    int p = Integer.parseInt(decomposer[2]) ; 
		    for (int i = 0 ; i < listeJoueur.size() ; i++) {
			if(nom.equals(listeJoueur.get(i).getPseudo())) {
			    listeJoueur.get(i).setPoint(p) ;
			    break ; 
			}
		    }
		}
		catch(Exception e) {
		    System.out.println("SERVEUR :  erreur int en string "  ) ;   
		}
	    }
	    
	}
	public void fermeturePartie (String [] decomposer) {
	    nb_partie -- ; 
	    quitter() ; 
	} 

	public void quitter (){
	    this.connection = false ;
	    try {
		sock.close () ;
		br.close() ;
		bw.close() ;
		tabHost.remove(this);	
	    }
	    catch(IOException io){
		System.out.println("fermeture bug host");
	    }
	} 
	
    	public void reponse (String recu ){
	    String [] decomposer = recu.split("#" ) ;
	    if(decomposer.length < 1 ) ecrire("IDKS") ; 
	    else if (decomposer[0].equals("SDDT")) {
		System.out.println("SERVEUR : arrive information " ) ; 
		remplirInfo(recu) ; 
	    }
	    else if (decomposer[0].equals("IDIG")){
		System.out.println("SERVEUR  : search arrive ok  ") ; 
		remplirSearch(true , recu) ;  
	    }
	    else if (decomposer[0].equals("INIG")) {
		System.out.println("SERVEUR : search arrive no " ) ;  
		remplirSearch(false , recu ); 
	    }

	    else if (decomposer[0].equals("PLIN")) {
		System.out.println("SERVEUR : demande Connexion Host " ) ; 
		String rep = connexionHost(decomposer)  ;
		ecrire(rep ) ; 
	    }
	    else if (decomposer[0].equals("SCPS")) {
		System.out.println("SERVEUR : mise a jour Point ") ; 
		changementPoint(decomposer) ; 
	    }
	    else if(decomposer[0].equals("ENDS")) {
		System.out.println("SERVEUR : fin partie") ; 
		fermeturePartie(decomposer) ; 
	    }
	    else if (decomposer[0].equals("IMOK") ) {
		System.out.println("SERVEUR : recu IMOK" ) ; 
		tempsIMOK() ; 
	    }
	    else if (decomposer[0].equals("IDKH")) {
	    }
	    else {
		ecrire("IDKS") ; 
	    }
    	}

	public void testHost (String recu) {
	    String [] decomposer = recu.split("#") ;
	    boolean ok = false ; 
	    if(decomposer.length == 2 ) {
		if( decomposer[0].equals("LOGI") ){ 
		    try{
			//	this.portHost = Integer.parseInt(decomposer[3]) ; 
			tabHost.add(this) ; 
			System.out.println("SYSTEM : partie incremente " ) ;
			nb_partie ++ ;
			ok = true;
		    }
		    catch (Exception e) {
			System.out.println("SERVEUR  : erreur LOGI " ) ; 
		    }
		}
	    }
	    else if (decomposer.length == 3 ) {
		if( decomposer[0].equals("LOGI") ){ 
		    if(decomposer[2].equals(mdp)) {
			try{
			    
			    tabHost.add(this) ; 
			    System.out.println("SYSTEM : partie incremente " ) ;
			    nb_partie ++ ;
			    ok = true;
			}
			catch (Exception e) {
			    System.out.println("SERVEUR  : erreur LOGI " ) ; 
			}
		    }
		}

	    }
	    else if(decomposer.length > 3 ){
		if( decomposer[0].equals("LOGI") ){ 
		    if(decomposer[2].equals(mdp)) {
			try{
			    this.portHost = Integer.parseInt(decomposer[3]) ; 
			    tabHost.add(this) ; 
			    System.out.println("SYSTEM : partie incremente " ) ;
			    nb_partie ++ ;
			    ok = true;
			}
			catch (Exception e) {
			    System.out.println("SERVEUR  : erreur LOGI " ) ; 
			}
		    }
		}
	    }
	    if(!ok) {
		this.connection = false ;
		try {
		    sock.close () ;
		    br.close() ;
		    bw.close() ;	
		}
		catch(IOException io){
		    System.out.println("SERVEUR : LOGI Incorrect");
		}
	    }
	}

    	public void run (){
	    if(creationOK) {
		String identifie = lire() ; 
		if(identifie != null) testHost(identifie)  ; 
		while (connection) {
		    System.out.println("SERVEUR : je suis dans attente host (serveur)");
		    String recu = lire();
		    if(recu != null) reponse(recu);
		    else {
			this.connection = false ;
			try {
			    nb_partie -- ; 
			    sock.close () ;
			    br.close() ;
			    bw.close() ;
			    tabHost.remove(this);	
			}
			catch(IOException io){
			    System.out.println("fermeture bug host");
			}
		    }
		}
	    }
	}
    }
}
