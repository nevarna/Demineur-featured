JC=javac
EXEC=Client.jar 
EXEC2=Serveur.jar
EXEC3=Host.jar 

all:$(EXEC) $(EXEC2) $(EXEC3) 

$(EXEC): MANIFESTC.MF Client.class Grille.class Lire.class LireHost.class 
	jar cvmf MANIFESTC.MF Client.jar Client.class Grille.class Lire.class LireHost.class

$(EXEC2) : MANIFESTS.MF Serveur*.class xml.class Joueur.class
	jar cvmf MANIFESTS.MF Serveur.jar Serveur*.class xml.class Joueur.class

$(EXEC3):MANIFESTH.MF Host*.class Joueur.class Case.class Demineur.class 

	jar cvmf MANIFESTH.MF Host.jar Host*.class Joueur.class Case.class Demineur.class 

Client.class : *.java
	javac *.java

Serveur.class: *.java
	javac *.java 

Case.class : *.java
	javac *.java

Host*.class : 
	javac Host*.java

Serveur*.class:
	javac Serveur*.java

clean:
	rm -f *.class

mrproper :
	rm *.class
	rm *.jar

