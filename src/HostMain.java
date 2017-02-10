//AUTEUR  : Navarna 
public class HostMain {

	public static void main (String  [] args) {
		if(args.length < 5 ){
			System.out.println("pas assez d'argument") ; 
		}
		else  {
			try {
				int portServ = Integer.parseInt(args[1]) ;
				int port = Integer.parseInt(args[4]) ;
				Host h = null ; 
				if(args.length == 6 ) 
				    h = new Host (args[0] , portServ  , args[2] , args[3] , port , args[5]); 
				else 
				    h = new Host (args[0] , portServ  , args[2] , args[3] , port , null);  
				h.lancer () ; 
			}
			catch (NumberFormatException un ){
				System.out.println("ce n'est pas un nombre "); 
			}
		}
	}

}
