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
	
	
	public void mutation(double p_mut) {
		Random oRandom = new Random();
		
		for(int i = 0; i < this.binchain.length; i++) {
			if (oRandom.nextDouble() <= p_mut) {
				this.binchain[i] = 1-this.binchain[i];
				//System.out.println("apply mutation");
			}
		}
	}
	
	public void draw() {
		for(int i = 0; i < this.binchain.length; i++) {
			System.out.print(this.binchain[i]);
		}
		
	}
	
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
	
	
	public String toString() {
		String tmpstr = new String();
		for(int i = 0; i < this.binchain.length; i++) {
			tmpstr = tmpstr + this.binchain[i];
		}
		
		return tmpstr;
	}
	
}
