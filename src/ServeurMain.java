//AUTEUR  : Navarna 
public class ServeurMain {
	public static void main (String [] args ){
		String IP = "localhost";
		if(args.length == 1 ) 
			IP = args[0] ;
		Serveur s = new Serveur(IP) ; 
	}
}
