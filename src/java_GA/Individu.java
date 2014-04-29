package java_GA;
import java.util.Random;


public class Individu {
	
	int[] binchain; // Array represenant la chaine binaire de l'objet Individu
	int fitness = 0;
	
	/**
	 * Constructor d un objet Individu avec une chaine binaire aleatoire
	 * @param size : nombre d elements dans la chaine binaire constituant l'individu, generalement connue grace a la taille du tableau de solution optimale
	 */
	public Individu(int size) {
		
		Random oRandom = new Random();
		
		this.binchain = new int[size];
		
		for(int i = 0; i< this.binchain.length; i++) {
			this.binchain[i] = oRandom.nextInt(1+1);
		}
		
	}
	
	/**
	 * constructor d un objet Individu avec un chaine binaire connue
	 * utilise pour cree les enfants issus des parents
	 * 
	 * @param force_pattern Array de binaires represenant l'individu
	 */
	public Individu(int[] force_pattern) {
		this.binchain = force_pattern;
	}
	
	
	/**
	 * fonction de fitness, permettant l'evaluation d'un individu
	 * somme des constituants identiques entre l'objet et la solution optimale fournie en parametre
	 * 
	 * @param benchmark la solution optimale sous forme d un array de binaires, 
	 * @return sommes des elements concordant entre l'objet et la solution optimale
	 */
	public int eval(int[] benchmark) {
		
		int value = 0;
		for(int i = 0; i < this.binchain.length; i++) {
			if (this.binchain[i] == benchmark[i]) {
				value++;
			}
		}
		
		this.fitness = value;
		
		return value;
	}
	
	
	/**
	 * appliquer une fonction de mutation sur la representation de chaine binaire de l objet
	 * 
	 * @param p_mut probabilite de mutation
	 */
	public void mutation(double p_mut) {
		Random oRandom = new Random();
		
		for(int i = 0; i < this.binchain.length; i++) {
			if (oRandom.nextDouble() <= p_mut) {
				this.binchain[i] = 1-this.binchain[i]; //inspire de la ligne 99 de tiny_ga.py
			}
		}
		
	}
	
	
	/**
	 * affichage de la solution binaire, chaine de 0 et de 1 de l objet
	 */
	public void draw() {
		for(int i = 0; i < this.binchain.length; i++) {
			System.out.print(this.binchain[i]);
		}
		
	}
	
	
	/**
	 * 
	 * @param modulo tous les combien de caracteres faut-il faire un retour a la ligne
	 * @param char_for_0 caractere de substitution pour les 0 de la chaine binaire
	 * @param char_for_1 caractere de substitution pour les 1 de la chaine binaire
	 */
	public void draw_with_transformation(int modulo, String char_for_0, String char_for_1) {
		String tmpstr = new String();
		
		for(int i = 0; i < this.binchain.length; i++) {
			
			if (((i+1) % 16) == 0 && i != 0) {
				tmpstr = tmpstr + System.getProperty("line.separator"); 
			}
			
			if (this.binchain[i] == 0) {
				tmpstr = tmpstr + char_for_0;
			} else {
				tmpstr = tmpstr + char_for_1;
			}
			
		}
		
		System.out.println(tmpstr);
		
	}
	
	
	/**
	 * override de la fonction derive de la class Object
	 * http://stackoverflow.com/questions/13498179/output-of-system-out-printlnobject
	 * fonction qui est appelee lors d un cast en String d'un object de la class
	 * par exemple : System.out.prinln(oIndividu)
	 * @return la chaine de binaire de l'Individu
	 */
	public String toString() {
		String tmpstr = new String();
		for(int i = 0; i < this.binchain.length; i++) {
			tmpstr = tmpstr + this.binchain[i];
		}
		
		return tmpstr;
	}
	
}
