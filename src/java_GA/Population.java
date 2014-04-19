package java_GA;
import java.util.Random;

public class Population {
	
	int size_pop; //nombre d individu completement initialise
	double p_crossover;
	double p_mutation;
	int nbre_generation;
	
	int generation_state = 0;
	Individu oIndividus[];
	int[] target;
	int[] evals;
	int[] individu_idx_evals;
	
	int nbre_crossover = 0;
	int nbre_mutation = 0;
	

	
	public Population(int size_pop) {
		//pop vide pratique pour selection
		this.size_pop = 0;
		this.oIndividus = new Individu[size_pop];
	}
	
	public Population(int size_pop, int[] target, double p_crossover, double p_mutation) {
		
		this(size_pop);
		
		this.target = target;
		this.p_crossover = p_crossover;
		this.p_mutation = p_mutation;
		
		for (int i = 0; i < oIndividus.length; i++) {
			oIndividus[i] = new Individu(this.target.length); // se sert dirctement de la taille du benchmark pour etablir size des individus
			this.size_pop++;
		}
	}
	
	
	public void generation() {
		
		//selection
		//Population popselect = selection_tournoi(5);
		Population popselect = selection_tournoi_with_elite(5, 0.1);
		
		Random oRandom = new Random();
		
		for (int i=0; i < popselect.size_pop; i++) {
			
			int idx_parent1= oRandom.nextInt(popselect.size_pop);
			int idx_parent2= oRandom.nextInt(popselect.size_pop);
			Individu parent1 = popselect.oIndividus[idx_parent1];
			Individu parent2 = popselect.oIndividus[idx_parent2];
			
			Individu kid1;
			Individu kid2;
			Individu[] two_kids;
			if (oRandom.nextDouble() <= this.p_crossover) {
				//crossover
				this.nbre_crossover++;
				two_kids = crossover(parent1, parent2);
				kid1 = two_kids[0];
				kid2 = two_kids[1];
			} else {
				//mutation
				this.nbre_mutation++;
				parent1.mutation(this.p_mutation);
				parent2.mutation(this.p_mutation);
				
				kid1 =  parent1;
				kid2 = parent2;
			}
			
			//les 2 enfants remplacent les parents
			popselect.oIndividus[idx_parent1] = kid1;
			popselect.oIndividus[idx_parent2] = kid2;		
			
		}
		
		this.nbre_generation++;
		this.oIndividus = popselect.oIndividus;
		
		//stats
		System.out.println("Fitness best individu after " + this.nbre_generation + " generation(s): " + this.getBestIndividu().eval(this.target));
		
	}
	
	
	public Population selection_tournoi(int tournament_size) {
		
		Population popselect = new Population(this.size_pop);
		
		popselect.size_pop = 0;
		
		while (popselect.size_pop < this.size_pop) {
			popselect.oIndividus[popselect.size_pop] = tournoi(tournament_size);
			popselect.size_pop++;
		}
		
		return popselect;
	}
	
	
	
public Population selection_tournoi_with_elite(int tournament_size, double pct_to_keep) {
		
		Population popselect = new Population(this.size_pop);
		
		popselect.size_pop = 0;
		
		
		//conserve automatiquement les 10% les meilleurs
		this.evals = new int[this.size_pop];
		this.individu_idx_evals = new int[this.size_pop];
		
		
		for (int i=0; i<this.size_pop; i++) {
			this.evals[i] = this.oIndividus[i].eval(this.target);
			this.individu_idx_evals[i] = i;
		}
		
		
		//sort
		int max_idx;
		int max_fitness;
		for (int i=0; i<this.size_pop; i++) {
			
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
		
		
		for (int i=0; i<this.size_pop; i++) {
			
			if (((i+1) / (double)this.size_pop) > pct_to_keep)  {
				break;
			} else {
				popselect.oIndividus[popselect.size_pop] = this.oIndividus[this.individu_idx_evals[i]];
				popselect.size_pop++;
			}
			
		}
		
		
		
		while (popselect.size_pop < this.size_pop) {
			popselect.oIndividus[popselect.size_pop] = tournoi(tournament_size);
			popselect.size_pop++;
		}
		
		return popselect;
	}
	
	
	
	public Individu tournoi(int tsize) {
		
		Random oRandom = new Random();
		
		int tmp_idx;
		int idx_max = -1;
		int value_max = -1;
		for (int i = 0; i < tsize; i++) {
			tmp_idx = oRandom.nextInt(this.size_pop);
			
			//System.out.println("just selected " + tmp_idx);
			
			if (this.oIndividus[tmp_idx].eval(this.target) > value_max) {
				idx_max = tmp_idx;
				value_max = this.oIndividus[tmp_idx].eval(this.target);
			}
		}
		
		return this.oIndividus[idx_max];
	}
	
	
	public Individu[] crossover(Individu oI1, Individu oI2) {
		return crossover_1part(oI1, oI2);
	}
	
	
	public Individu[] crossover_1part(Individu oI1, Individu oI2) {
		
		Random oRandom = new Random();
		
		int separator =  oRandom.nextInt(oI1.binchain.length-1)+1;
		
		int[] newbinchar1 = new int[oI1.binchain.length];
		int[] newbinchar2 = new int[oI1.binchain.length];
		
		for (int i=0; i<oI1.binchain.length; i++) {
			if (i < separator) {
				newbinchar1[i] = oI1.binchain[i];
				newbinchar2[i] = oI2.binchain[i];
			} else {
				newbinchar1[i] = oI2.binchain[i];
				newbinchar2[i] = oI1.binchain[i];
			}
		}
		
		//System.out.println("cross between " + oI1 + " and " + oI2 + " -> " + new Individu(newbinchar1) + " and " + new Individu(newbinchar2));
		
		Individu[] two_kids = new Individu[2];
		two_kids[0] = new Individu(newbinchar1);
		two_kids[1] = new Individu(newbinchar2);
		
		return two_kids;
	
	}
	
	
	public Individu getBestIndividu() {
		int idx_max = 0;
		int max_eval;
		int tmp_eval;
		
		max_eval = oIndividus[0].eval(this.target);
		
		for (int i = 1; i < oIndividus.length; i++) {
			tmp_eval =  oIndividus[i].eval(this.target);
			if (tmp_eval > max_eval) {
				max_eval = tmp_eval;
				idx_max = i;
			}
				
		}
		
		return oIndividus[idx_max];
		
	}
	
	public void drawPop() {
		
		for (int i = 0; i < oIndividus.length; i++) {
			oIndividus[i].draw();
			System.out.print(" " + this.oIndividus[i].eval(this.target));
			System.out.println();
		}
		
	}
	
	
	public static void main(String[] args) {
		
		int size_pop = 1000;
		double p_crossover = 0.7;
		double p_mutation = 0.05;
		int nbre_generation = 300;
		
		int[] tmp_table = {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
				0, 0, 0, 0, 0, 1, 1, 1, 1, 1, 1, 0, 0, 0, 0, 0,
				0, 0, 0, 1, 1, 0, 0, 0, 0, 0, 0, 1, 1, 0, 0, 0,
				0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0,
				0, 0, 1, 0, 0, 1, 1, 0, 0, 1, 1, 0, 0, 1, 0, 0,
				0, 1, 0, 0, 0, 1, 1, 0, 0, 1, 1, 0, 0, 0, 1, 0,
				0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0,
				0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0,
				0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0,
				0, 0, 1, 0, 1, 0, 0, 0, 0, 0, 0, 1, 0, 1, 0, 0,
				0, 0, 1, 0, 0, 1, 1, 1, 1, 1, 1, 0, 0, 1, 0, 0,
				0, 0, 0, 1, 1, 0, 0, 0, 0, 0, 0, 1, 1, 0, 0, 0,
				0, 0, 0, 0, 0, 1, 1, 1, 1, 1, 1, 0, 0, 0, 0, 0,
				0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0};
		
		Population oPop = new Population(size_pop, tmp_table,p_crossover, p_mutation);
		
		for (int i=0; i<nbre_generation; i++) {
			oPop.generation();
		}
		
		System.out.println("last population after " + nbre_generation + " rounds: ");
		//oPop.drawPop();
		
		oPop.getBestIndividu().draw_with_transformation(16, " ", "*");
	}

}
