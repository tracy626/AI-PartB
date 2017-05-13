/**
 * Yue Fang 715889
 * Zhe Tang 743398
 * COMP30024 Artificial Intelligence
 * Project Part B
 */

import java.awt.Point;
import java.util.ArrayList;
import java.util.HashMap;

import aiproj.slider.Move;
import aiproj.slider.Move.Direction;

public class TDleaf {
	public double[] weights = {7, 7, 1, 1, 1, 1, 1, 1, 1, 1};
	public ArrayList<Integer[]> features;
	private ArrayList<Double> diff;
	private double sumD;
	private ArrayList<Double> td;
	private static final double ALPHA = 0.2;
	private static final double LAMBDA = 0.8;
	public int count;
	private ArrayList<Point> block;
	private int dimension;
	
	public TDleaf(ArrayList<Point> block, int dimension) {
		this.features = new ArrayList<Integer[]>();
		this.diff = new ArrayList<Double>();
		this.td = new ArrayList<Double>();
		this.sumD = 0;
		this.count = 0;
		this.block = block;
		this.dimension = dimension;
	}

	public Integer[] detect_f(HashMap<Point, ArrayList<Direction>> curP, HashMap<Point, ArrayList<Direction>> opP, Direction dir, char player) {
		Integer[] feature = {0, 0, 0, 0, 0, 0, 0, 0, 0, 0};
		feature[0] += this.dimension * (this.dimension - 1 - curP.size());
		feature[5] -= this.dimension * (this.dimension - 1 - opP.size());
		for (Point key: curP.keySet()) {
			if (player == 'H') {
				feature[1] += key.x;
				for(int i = key.x + 1; i < this.dimension; i++){
					if(curP.containsKey(new Point(i, key.y))){
						feature[2] -= 1;
					}
					if(opP.containsKey(new Point(i, key.y))){
						feature[3] -= 1;
					}
					if(this.block.contains(new Point(i, key.y))){
						feature[4] -= 1;
					}
				}
			}else{
				feature[1] += key.y;
				for(int i = key.y + 1; i < this.dimension; i ++){
					if(curP.containsKey(new Point(key.x, i))){
						feature[2] -= 1;
					}
					if(opP.containsKey(new Point(key.x, i))){
						feature[3] -= 1;
					}
					if(this.block.contains(new Point(key.x, i))){
						feature[4] -= 1;
					}
				}
			}
		}
		for (Point key: opP.keySet()){
			if (player == 'H') {
				feature[6] -= key.y;
				for(int i = key.y + 1; i < this.dimension; i ++){
					if(curP.containsKey(new Point(key.x, i))){
						feature[7] += 1;
					}
					if(opP.containsKey(new Point(key.x, i))){
						feature[8] += 1;
					}
					if(this.block.contains(new Point(key.x, i))){
						feature[9] += 1;
					}
				}
			}else{
				feature[6] -= key.x;
				for(int i = key.x + 1; i < this.dimension; i ++){
					if(curP.containsKey(new Point(i, key.y))){
						feature[7] += 1;
					}
					if(opP.containsKey(new Point(i, key.y))){
						feature[8] += 1;
					}
					if(this.block.contains(new Point(i, key.y))){
						feature[9] += 1;
					}
				}
			}
		}
		return feature;
	}
	
	public void prove_w() {
		this.diff.add(1 - Math.pow(tanh(count - 1), 2));
		if(count > 1){
			this.td.add(tanh(count - 1) - tanh(count - 2));
			
			for(int i=0; i < weights.length; i++){
				double sumDiff = 0;
				for(int j=0; j < count - 1; j++){
					double sumD = 0;
					for(int k=j; k < count - 1; k++){
						sumD += Math.pow(LAMBDA, k - j) * this.td.get(j);
					}
					sumDiff += this.diff.get(j) * features.get(j)[i] * sumD;
				}
				weights[i] += ALPHA * sumDiff;
			}
		}
		for (double i: weights){
			System.out.print(i+" ");
		}
		System.out.println();
	}
	
	public double convert_r(Integer[] f){
		double eval = 0;
		for(int i=0; i < weights.length; i++){
			eval += f[i] * weights[i];
		}
		return eval;
	}
	
	public double tanh(int n) {
		return Math.tanh(convert_r(features.get(n)));
	}
}
