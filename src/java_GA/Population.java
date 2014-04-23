package java_GA;
import java.util.Random;
import java.util.ArrayList;;

public class Population {
	
	int size_pop; //nombre d individu completement initialise
	double p_crossover; // probabilite de reproduction
	double p_mutation; // probabilite de mutation
	
	int generation_state = 0; // nombre de fois que la function .generation() a ete appelee
	Individu oIndividus[]; //Array contenant les objets Individu (solutions)
	int[] target; // solution optimale, pratique pour connaitre la taille des objets Individus a generer et pour calculer leur fitness
	
	// pour faciliter les stats
	int[] evals; //Array de fitness, les index ne correspondent pas forcement a ceux de la variable de class oIndividus mais a ceux de la variable de class individu_idx_evals suite par exemple a un tri
	int[] individu_idx_evals;
	
	public enum Selection_Methods {
	    tournoi, roulette_roportionnelle, rang
	}
	
	Selection_Methods selection_method;
	
	ArrayList<Individu[]> histo_pop = new ArrayList<Individu[]>();
	
	
	/**
	 * Constructeur de la class Population avec creation des Individus aleatoire
	 * 
	 * @param size_pop nombre d'objet Individu dans l'Array oIndividus
	 * @param target solution optimale, pratique car permet de connaitre la taille de l'Array de la representation binaire individus, sera egalement transmise a la fonction d'evaluation du fitness des individus
	 * @param selection_method  
	 * @param p_crossover probabilite de crossover
	 * @param p_mutation probabilite de mutation
	 */
	public Population(int size_pop, int[] target, Selection_Methods selection_method, double p_crossover, double p_mutation) {
		
		this(size_pop); // demarre avec l initialisation des variables de class en appelant un autre constructeur
		
		this.target = target;
		this.selection_method = selection_method;
		this.p_crossover = p_crossover;
		this.p_mutation = p_mutation;
		
		//creation des objet Individus aleatoire
		for (int i = 0; i < oIndividus.length; i++) {
			oIndividus[i] = new Individu(this.target.length); // se sert dirctement de la taille du benchmark pour etablir size des individus
			this.size_pop++;
		}
	}
	
	
	
	/**
	 * Constructeur de la class Population, initialisant uniquement tableau des Individus, ne genere pas les individus
	 * 
	 * @param size_pop permet de la construction avec une taille approprie de l'Array qui contiendra les Individus
	 */
	public Population(int size_pop) {
		//pop sans individu, pratique pour selection
		this.size_pop = 0;
		this.oIndividus = new Individu[size_pop];
	}
	
	
	/**
	 * 
	 * Alias qui imposera la selection par tournoi
	 */
	public Population(int size_pop, int[] target, double p_crossover, double p_mutation) {
		this(size_pop, target, Selection_Methods.tournoi, p_crossover, p_mutation);
	}
	
	
	/**
	 * effectue le cycle complet d une genration
	 * 1) selection d une population temporaire avant la reproduction
	 * 2)a reproduction, 2 parents produisent 2 enfants qui les remplacent dans la population temporaire
	 * 2)b si les parents n'ont pas ete retenus pour la reproduction, on les passe a la fonction de mututation des individus
	 * 3) la population temporaire des enfants écrase la précédente
	 */
	public void generation() {
		
		//selection
		Population popselect = this.selection(); //population temporaire retenue pour reproduction / mutation
		
		Random oRandom = new Random();
		
		for (int i=0; i < popselect.size_pop; i++) { //autant de tour que d'objet d'Individu constituant la population
			
			int idx_parent1 = oRandom.nextInt(popselect.size_pop); //tire au sort une maman grace a l index de l Array des individus de la population temporaire selectionnee pour reproduction / mutation
			int idx_parent2 = oRandom.nextInt(popselect.size_pop); //tire au sort un papa grace a l index de l Array des individus de la population temporaire selectionnee pour reproduction / mutation
			
			Individu parent1 = popselect.oIndividus[idx_parent1];
			Individu parent2 = popselect.oIndividus[idx_parent2];
			
			Individu kid1;
			Individu kid2;
			Individu[] two_kids; // la fonction crossver retourne un Array de 2 objets Individu
			if (oRandom.nextDouble() <= this.p_crossover) {
				//crossover
				two_kids = crossover(parent1, parent2, 2); // la fonction crossver retourne un Array de 2 objets Individu
				kid1 = two_kids[0];
				kid2 = two_kids[1];
			} else {
				//mutation
				parent1.mutation(this.p_mutation);
				parent2.mutation(this.p_mutation);
				
				kid1 =  parent1;
				kid2 = parent2;
			}
			
			//les 2 enfants remplacent les parents
			popselect.oIndividus[idx_parent1] = kid1;
			popselect.oIndividus[idx_parent2] = kid2;		
			
		}
		
		this.generation_state++;
		this.histo_pop.add(this.oIndividus);
		this.oIndividus = popselect.oIndividus;
		
		//stats
		System.out.println("Fitness of the best individu after " + this.generation_state + " generation(s): " + this.getBestIndividu().eval(this.target) + " avg fitness of the whole population: " + this.get_avg_fitness());
		
	}
	
	
	/**
	 * switch/case a travers les differentes methodes de selections possibles
	 * 
	 * @return Population retenue avant la reproduction / mutation
	 */
	public Population selection() {
		
		switch (this.selection_method) {
			case tournoi:
				return this.selection_tournoi_with_elite(5, 0.10);
			case roulette_roportionnelle:
				return this.selection_roulette_proportionnelle();
			case rang:
				return this.selection_rang();
			default:
				return this.selection_tournoi_with_elite(5, 0.10);
		}
	}
	
	
	/**
	 * rempli 2 variables de class helpers .evals et .individu_idx_evals, pratique pour effectuer stats et trier les fitness des differents objets Individu constituant la population
	 */
	private void eval_current_state() {
		
		this.evals = new int[this.size_pop];
		this.individu_idx_evals = new int[this.size_pop];
		
		for (int i=0; i<this.size_pop; i++) {
			this.evals[i] = this.oIndividus[i].eval(this.target);
			this.individu_idx_evals[i] = i;
		}
	}
	
	
	/**
	 * 
	 * @return fitness moyen de la population
	 */
	public double get_avg_fitness() {
		//this.eval_current_state();
		
		double tmp_sum_fitness;
		tmp_sum_fitness = 0.0;
		for (int i=0; i<this.size_pop; i++) {
			tmp_sum_fitness+= this.oIndividus[i].eval(this.target);
		}
		
		return tmp_sum_fitness / this.size_pop;
	}
	
	
	
	public Population selection_roulette_proportionnelle() {
		
		Population popselect = new Population(this.size_pop);
		popselect.size_pop = 0;
		
		//rempli un tableau avec les fitness actuels pour chaque individu
		this.eval_current_state();
		
		//calcul la sum de tous les fitness pour construire la roulette biaisee
		int sum_fitness;
		sum_fitness = 0;
		for (int i=0; i<this.size_pop; i++) {
			sum_fitness += this.evals[i];
		}
		
		
		int[] roulette = new int[sum_fitness];
		int k=0;
		for (int i=0; i<this.size_pop; i++) {
			for(int j=0; j<this.evals[i]; j++) {
				roulette[k] = this.individu_idx_evals[i];
				k++;
			}
		}
		
		Random oRandom = new Random();
		
		while (popselect.size_pop < this.size_pop) {
			popselect.oIndividus[popselect.size_pop] = this.oIndividus[roulette[oRandom.nextInt(roulette.length)]];
			popselect.size_pop++;
		}
		
		return popselect;
		
	}
	
	
	public Population selection_rang() {
		Population popselect = new Population(this.size_pop);
		popselect.size_pop = 0;
		
		//rempli un tableau avec les fitness actuels pour chaque individu
		this.eval_current_state();
		
		
		//sort des fitness, higher first
		int min_idx;
		int min_fitness;
		
		//tri par bulle
		for (int i=0; i<this.size_pop; i++) {
			
			min_idx = i;
			min_fitness = this.evals[i];
			
			
			for (int j=i+1; j<this.size_pop; j++) {
				if (this.evals[j] < min_fitness) {
					min_fitness = this.evals[j];
					min_idx = j;
				}
			}
			
			int tmp_fitness;
			int tmp_idx;
			if (min_idx != i) {
				tmp_idx = individu_idx_evals[i];
				tmp_fitness = this.evals[i];
				
				this.individu_idx_evals[i] =  this.individu_idx_evals[min_idx];
				this.evals[i] =  this.evals[min_idx];
				
				this.individu_idx_evals[min_idx] = tmp_idx;
				this.evals[min_idx] = tmp_fitness;
			}
		}
		
		
		int count_rang = 0;
		for (int i=0; i<this.size_pop; i++) {
			count_rang+= i+1;
		}
		
		int[] roulette_rang = new int[count_rang];
		
		int k=0;
		for (int i=0; i<this.size_pop; i++) {
			for (int j=0; j<=i; j++) {
				roulette_rang[k] = this.individu_idx_evals[i];
				k++;
			}
		}
		
		Random oRandom = new Random();
		
		while (popselect.size_pop < this.size_pop) {
			popselect.oIndividus[popselect.size_pop] = this.oIndividus[roulette_rang[oRandom.nextInt(roulette_rang.length)]];
			popselect.size_pop++;
		}
		
		
		return popselect;
	}
	
	/**
	 * 
	 * @deprecated remplacee par selection_tournoi_with_elite
	 * 
	 * @param tournament_size le nombre d'individus que l on fait combattre, seul le meilleur sera retenu base sur sa fitness
	 * @return un nouvel objet Population contenant la population selectionnee pour la reproduction et la mutation
	 */
	public Population selection_tournoi(int tournament_size) {
		
		Population popselect = new Population(this.size_pop);
		
		popselect.size_pop = 0;
		
		while (popselect.size_pop < this.size_pop) {
			popselect.oIndividus[popselect.size_pop] = tournoi(tournament_size);
			popselect.size_pop++;
		}
		
		return popselect;
	}
	
	
	/**
	 * Creation d une population temporaire avant la phase de reproduction / mutation
	 * Un certain pourcentage des meilleurs individus est automatiquement retenu
	 * Pour avoir la meme taille de population qu'au depart, on la complete ensuite avec des vainqueurs de tournoi
	 * 
	 * @param tournament_size le nombre d'individus que l on fait combattre, seul le meilleur sera retenu base sur sa fitness
	 * @param pct_to_keep pourcentage d individu automatiquement retenu pour la population de reproduction / mutation
	 * @return un nouvel objet Population contenant la population selectionnee pour la reproduction et la mutation
	 */
	public Population selection_tournoi_with_elite(int tournament_size, double pct_to_keep) {
		
		Population popselect = new Population(this.size_pop);
		popselect.size_pop = 0;
		
		//rempli un tableau avec les fitness actuels pour chaque individu
		this.eval_current_state();
		
		
		//sort des fitness, higher first
		int max_idx;
		int max_fitness;
		
		//tri par bulle
		for (int i=0; i<this.size_pop; i++) {
			
			if (((i+1) / (double)this.size_pop) > pct_to_keep)  { // ne pas oublier le cast en double pour eviter d avoir le resulat d une division entiere
				break; //pas besoin de trier la totalite des fitness, on s arrete une fois le pourcentage choisi par l utilisateur est atteint
				
			} else {
			
				max_idx = i;
				max_fitness = this.evals[i];
				
				
				for (int j=i+1; j<this.size_pop; j++) {
					if (this.evals[j] > max_fitness) {
						max_fitness = this.evals[j];
						max_idx = j;
					}
				}
				
				int tmp_fitness;
				int tmp_idx;
				if (max_idx != i) {
					tmp_idx = individu_idx_evals[i];
					tmp_fitness = this.evals[i];
					
					this.individu_idx_evals[i] =  this.individu_idx_evals[max_idx];
					this.evals[i] =  this.evals[max_idx];
					
					this.individu_idx_evals[max_idx] = tmp_idx;
					this.evals[max_idx] = tmp_fitness;
				}
			}
		}
		
		
		// maintenant que les fitness sont tries, on ajoute automatiquement a la population temporaire les meilleurs Individus
		for (int i=0; i<this.size_pop; i++) {
			
			if (((i+1) / (double)this.size_pop) > pct_to_keep)  {
				break;
			} else {
				popselect.oIndividus[popselect.size_pop] = this.oIndividus[this.individu_idx_evals[i]];
				popselect.size_pop++;
			}
			
		}
		
		
		// complete la population pour qu'elle est la meme taille avec des tournois
		while (popselect.size_pop < this.size_pop) {
			popselect.oIndividus[popselect.size_pop] = tournoi(tournament_size);
			popselect.size_pop++;
		}
		
		return popselect;
	}
	
	
	/**
	 * 
	 * Retourne un seul vainqeur
	 * 
	 * @param nombre de d Individus combattant en meme temps
	 * @return objet Individu vainqueur du tournoi
	 */
	public Individu tournoi(int tsize) {
		
		Random oRandom = new Random();
		
		int tmp_idx;
		int idx_max = -1;
		int value_max = -1;
		for (int i = 0; i < tsize; i++) {
			tmp_idx = oRandom.nextInt(this.size_pop);
			
			if (this.oIndividus[tmp_idx].eval(this.target) > value_max) { // si dipose d une meilleure fitness devient le meilleur
				idx_max = tmp_idx;
				value_max = this.oIndividus[tmp_idx].eval(this.target);
			}
		}
		
		return this.oIndividus[idx_max];
	}
	
	
	/**
	 * 
	 * @param oI1 parent numero 1
	 * @param oI2 parent numero 2
	 * @param nbre_separator nombre de points choisis de maniere aleatoire ou les genes seront coupes
	 * @return Array de 2 objets Individu
	 */
	public Individu[] crossover(Individu oI1, Individu oI2, int nbre_separator) {
		
		Random oRandom = new Random();
		
		int[] idx_part = new int[nbre_separator];
		
		int tmp_separator=-1;
		for (int i=0; i<nbre_separator; i++) { // tirage aleatoire des emplacements des separateurs
			
			boolean found_dublicate;
			found_dublicate = true;
			
			while (found_dublicate) {
				tmp_separator =  oRandom.nextInt(oI1.binchain.length-1)+1;
				found_dublicate = false;
				
				for (int j=i-1; j>0; j--) {
					if (idx_part[j] == tmp_separator) {
						found_dublicate = true;
						break;
					}
				}
			}
			
			idx_part[i] = tmp_separator;
			
		}
		
		
		//sort idx_separator
		int idx_min_separator;
		int min_separator;
		for (int i=0; i<nbre_separator; i++) {
			
			idx_min_separator = i;
			min_separator = idx_part[i];
			
			for (int j=i+1; j<nbre_separator; j++) {
				if (idx_part[j] < min_separator) {
					min_separator = idx_part[j];
					idx_min_separator = j;
				}
			}
			
			if (idx_min_separator != i) {
				tmp_separator = idx_part[i];
				idx_part[i] = idx_part[idx_min_separator];
				idx_part[idx_min_separator] = tmp_separator;
			}
			
		}
		
		
		// creation des Array de binaire pour les 2 enfants
		int[] newbinchar1 = new int[oI1.binchain.length];
		int[] newbinchar2 = new int[oI1.binchain.length];
		
		
		int interval_from;
		int interval_to;
		// construction des Array de binaire pour les 2 enfants
		for (int i=0; i<nbre_separator+1; i++) { // il y a une intervalle de plus que de separtor
			
			if (i > 0) {
				interval_from = idx_part[i-1];
			} else {
				interval_from = 0; // cas particulier s il s agit du premier separateur [0 -> premier separateur ]
			}
			
			if ((i) == idx_part.length) {
				interval_to = oI1.binchain.length-1; // cas particulier s il s agit du dernier separateur [dernier separteur -> longeur de l'Array]
			}  else {
				interval_to = idx_part[i]-1;
			}
			
			//recoit les genes des parents
			for (int j=0; j<oI1.binchain.length; j++) {
				
				if (j >= interval_from && j <= interval_to) {
					
					// utilise l operateur modulo pour savoir si l enfant recoit un troncon du parent 1 ou 2
					if (((i+1) % 2) == 0) {
						newbinchar1[j] = oI1.binchain[j];
						newbinchar2[j] = oI2.binchain[j];
					} else {
						newbinchar1[j] = oI2.binchain[j];
						newbinchar2[j] = oI1.binchain[j];
					}
					
				} else {
					
					if (j > interval_to) {
						break; //evite perte de temps, passage a l interval suivant
					}
				}
			}
			
		}
		
		//creation de l output final qui sera retourne par la function
		Individu[] two_kids = new Individu[2];
		
		two_kids[0] = new Individu(newbinchar1); // impose le tableau de binaire de l'Individu pour eviter d'avoir un objet aleatoire
		two_kids[1] = new Individu(newbinchar2);
		
		return two_kids;

	}
	
	
	/**
	 * 
	 * @return l'objet Individu issu de la varaible de class Array oIndividus avec la meilleure fitness
	 */
	public Individu getBestIndividu() {
		int idx_max = 0;
		int max_eval;
		int tmp_eval;
		
		max_eval = this.oIndividus[0].eval(this.target);
		
		for (int i = 1; i < this.oIndividus.length; i++) {
			tmp_eval =  this.oIndividus[i].eval(this.target);
			if (tmp_eval > max_eval) {
				max_eval = tmp_eval;
				idx_max = i;
			}
				
		}
		
		return this.oIndividus[idx_max];
		
	}

}
