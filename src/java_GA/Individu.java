package java_GA;
import java.util.Random;
import java.util.ArrayList;

public class Individu {
	
	int size;
	int[] binchain;
	ArrayList<Integer> evolution_fitness = new ArrayList<Integer>();
	
	public int eval(int[] benchmark) {
		
		int value = 0;
		for(int i = 0; i < this.binchain.length; i++) {
			if (this.binchain[i] == benchmark[i]) {
				value++;
			}
		}
		
		this.evolution_fitness.add(value);
		return value;
	}
	
	
	public Individu(int size) {
		
		Random oRandom = new Random();
		
		this.binchain = new int[size];
		
		for(int i = 0; i< this.binchain.length; i++) {
			this.binchain[i] = oRandom.nextInt(1+1);;
			this.size++;
		}
		
	}
	
	public Individu(int[] force_pattern) {
		this.binchain = force_pattern;
		this.size = this.binchain.length;
	}
	
	
	public void draw() {
		for(int i = 0; i < this.binchain.length; i++) {
			System.out.print(this.binchain[i]);
		}
		
	}
	
	public String toString() {
		String tmpstr = new String();
		for(int i = 0; i < this.binchain.length; i++) {
			tmpstr = tmpstr + this.binchain[i];
		}
		
		return tmpstr;
	}
	
	
	
	public static void main(String[] args) {
		
		int[] tmp_table = {0,0,1,0,1,0,1,0,1,0};
		
		for (int i = 0; i < 4; i++) {
			Individu oI1 = new Individu(tmp_table.length);
			oI1.draw();
			
			System.out.print(" " + oI1.eval(tmp_table));
			
			System.out.println();
		}
	}
	

}
