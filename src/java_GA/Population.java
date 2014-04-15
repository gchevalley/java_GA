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
	
	//private Individu tmp_selection[] = new Individu[size_pop];
	
	
	
	public Population(int size_pop) {
		//pop vide pour selection
		this.size_pop = 0;
		this.oIndividus = new Individu[size_pop];
	}
	
	public Population(int size_pop, int[] target) {
		
		this(size_pop);
		
		this.target = target;
		
		for (int i = 0; i < oIndividus.length; i++) {
			oIndividus[i] = new Individu(this.target.length); // se sert dirctement de la taille du benchmark pour etablir size des individus
			this.size_pop++;
		}
	}
	
	
	public void generation() {
		
		//selection
		Population popselect = selection_tournoi();
		
		Random oRandom = new Random();
		
		for (int i=0; i < popselect.size_pop; i++) {
			
			//crossover
			int idx_parent1= oRandom.nextInt(popselect.size_pop+1);
			int idx_parent2= oRandom.nextInt(popselect.size_pop+1);
			Individu parent1 = popselect.oIndividus[idx_parent1];
			Individu parent2 = popselect.oIndividus[idx_parent2];
			
			Individu kid1;
			Individu kid2;
			Individu[] two_kids;
			if (oRandom.nextFloat() <= this.p_crossover) {
				two_kids = crossover(parent1, parent2);
				kid1 = two_kids[0];
				kid2 = two_kids[1];
			} else {
				//mutation
				kid1 =  parent1;
				kid2 = parent2;
			}
			
			//les 2 enfants remplacent les parents
			popselect.oIndividus[idx_parent1] = kid1;
			popselect.oIndividus[idx_parent2] = kid2;		
			
		}
		
		
		this.oIndividus = popselect.oIndividus;
		
		
	}
	
	
	public Population selection_tournoi() {
		int i=0;
		
		Population popselect = new Population(this.size_pop);
		
		while (popselect.size_pop < this.size_pop) {
			popselect.oIndividus[i] = tournoi(2);
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
			tmp_idx = oRandom.nextInt(tsize+1);
			
			System.out.println("just selected " + tmp_idx);
			
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
		
		System.out.println("cross between " + oI1 + " and " + oI2 + " -> " + new Individu(newbinchar1) + " and " + new Individu(newbinchar2));
		
		Individu[] two_kids = new Individu[2];
		two_kids[0] = new Individu(newbinchar1);
		two_kids[1] = new Individu(newbinchar2);
		
		return two_kids;
	
	}
	
	
	public void mutation() {
		
	}
	
	
	public void drawPop() {
		
		for (int i = 0; i < oIndividus.length; i++) {
			oIndividus[i].draw();
			System.out.print(" " + this.oIndividus[i].eval(this.target));
			System.out.println();
		}
		
	}
	
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		
		int size_pop = 10;
		double p_crossover = 0.7;
		double p_mutation = 0.05;
		int nbre_generation = 300;
		
		int[] tmp_table = {1,1,1,1,1,1,1,1,1,1};
		
		Population oPop = new Population(size_pop, tmp_table);
		oPop.drawPop();
	}

}
