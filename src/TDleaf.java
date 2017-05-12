import java.awt.Point;
import java.util.ArrayList;
import java.util.HashMap;

import aiproj.slider.Move;
import aiproj.slider.Move.Direction;

public class TDleaf {
	public double[] weights = {1, 10, 2, 8, 4, 3};
	public ArrayList<Integer[]> features;
	private ArrayList<Double> diff;
	private double sumD;
	private double sum_lambda;
	private static final double ALPHA = 0.3;
	private static final double LAMBDA = 0.8;
	public int count;
	private ArrayList<Point> block;
	
	public TDleaf(ArrayList<Point> block) {
		this.features = new ArrayList<Integer[]>();
		this.diff = new ArrayList<Double>();
		this.sumD = 0;
		this.sum_lambda = 0;
		this.count = 0;
		this.block = block;
	}

	public Integer[] detect_f(HashMap<Point, ArrayList<Direction>> curP, HashMap<Point, ArrayList<Direction>> opP, Direction dir, char player) {
		Integer[] feature = {0, 0, 0, 0, 0, 0};
		if(opP.size()<curP.size()){
			feature[0] -=1;
		}
		if(player == 'H') {
			if(dir == Direction.RIGHT){
				feature[1] += 1;
			}else{
				feature[2] -= 1;
			}
		}else{
			if(dir == Direction.UP){
				feature[1] += 1;
			}else{
				feature[2] -= 1;
			}
		}
		for(Point key: curP.keySet()){
			if(player == 'H'){
				if(opP.containsKey(new Point(key.x,key.y-1))){
					feature[3] += 1;
				}else if(opP.containsKey(new Point(key.x+1,key.y))){
					feature[4] -= 1;
				}else if(this.block.contains(new Point(key.x+1,key.y))){
					feature[5] -= 1;
				}
			}else{
				if(opP.containsKey(new Point(key.x-1,key.y))){
					feature[3] += 1;
				}else if(opP.containsKey(new Point(key.x,key.y+1))){
					feature[4] -= 1;
				}else if(this.block.contains(new Point(key.x,key.y+1))){
					feature[5] -= 1;
				}
				
			}
		}
//		features.add(feature);
		return feature;
	}
	
	public void prove_w() {
		this.diff.add(1 - Math.pow(tanh(count - 1), 2));
		if(count > 1){
//			this.sumD += tanh(count - 1);
//			this.sum_lambda += Math.pow(1/LAMBDA, count -1);
		//Math.pow(LAMBDA, count - 2)*
			this.sumD += Math.pow(LAMBDA, count - 2) * (tanh(count - 1) - tanh(count - 2));
//			this.sum_lambda += Math.pow(1/LAMBDA, count -1);
			
			for(int i=0; i < 6; i++){
				double sumDiff = 0;
				for(int j=0; j < count-1; j++){
					sumDiff += this.diff.get(count - 1) * features.get(count - 1)[i] * Math.pow(LAMBDA, 1-i) * this.sumD;
				}
//				this.sumDiff[i] += this.diff.get(count - 1)*features.get(count - 1)[i];
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
		for(int i=0; i < 6; i++){
//			System.out.println(count+ " "+features.size());
			eval += f[i] * weights[i];
		}
		return eval;
	}
	
	public double tanh(int n) {
		return Math.tanh(convert_r(features.get(n)));
	}
}
