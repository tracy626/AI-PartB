/**
 * Yue Fang 715889 (fang1)
 * Zhe Tang 743398 (zhet1)
 * COMP30024 Artificial Intelligence
 * Project Part B
 */

import java.awt.Point;
import java.util.ArrayList;
import java.util.HashMap;

import aiproj.slider.Move.Direction;

/**
 * class of tdleaf(lambda) for our AI to choose next move
 */
public class TDleaf {
	public double[] weights = {7, 5.5, 1, 0.9, 1, 1, 0.9, 1, 1.1, 1};
	public ArrayList<Integer[]> features; 
	// ArrayList to store all the features of the leaves state which determined the moves in happened states
	private ArrayList<Double> diff; 
	// ArrayList to store the differential number of the d[r(si,w)]/d[wj] part
	private ArrayList<Double> td; // ArrayList to store the temporal differences between states
	private static final double ALPHA = 1; // Learning rate in tdleaf(lambda)
	private static final double LAMBDA = 0.7; // Lambda value of tdleaf(lambda)
	public int count; // count of current number of state from the begin of the game
	private ArrayList<Point> block; // ArrayList stored the block's position
	private int dimension; // Number of the dimension of current game
	
	/**
     * default constructor, read dimension and the block details
     */
	public TDleaf(ArrayList<Point> block, int dimension) {
		this.features = new ArrayList<Integer[]>();
		this.diff = new ArrayList<Double>();
		this.td = new ArrayList<Double>();
		this.count = 0;
		this.block = block;
		this.dimension = dimension;
	}

	/**
     * get features of player using the given HashMap as the state of game,
     * @param curP the HashMap store the player's pieces position map to the available move's direction
     * @param opP the HashMap store the opponent player's pieces position and its available move
     * @param dir Direction
     * @param player our AI player
     * @return Integer array contain all feature points
     */
	public Integer[] detect_f(HashMap<Point, ArrayList<Direction>> curP, HashMap<Point, ArrayList<Direction>> opP, char player) {
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
	
	/**
	 * a helper function to update vector w according to the formula of tdleaf(lambda)
	 */
	public void prove_w() {
		this.diff.add(1 - Math.pow(convert_r(count - 1), 2));
		if(count > 1){
			// temperal difference of state i-1 equal to reward of i substance reward of i-1
			// since count is recorded from one but the index of ArrayList counts from 0
			// we use reward of count-1 minus reward of count-2
			this.td.add(convert_r(count - 1) - convert_r(count - 2));
			for(int i=0; i < weights.length; i++){
				// initial sum of differential of reward of determined leaf of state j to w(i) times sum of d
				double sumDiff = 0;
				for(int j=0; j < count - 1; j++){
					// initial sum of lambda's power of k-j times dj
					double sumD = 0;
					for(int k=j; k < count - 1; k++){
						sumD += Math.pow(LAMBDA, k - j) * this.td.get(j);
					}
					sumDiff += this.diff.get(j) * features.get(j)[i] * sumD;
				}
				// update the vector w
				weights[i] += ALPHA * sumDiff;
			}
		}
	}
	
	/**
	 * a helper function to calculate evaluation value
	 * @param f features
	 * @return a double, the value of the evaluation
	 */
	public double evaluation(Integer[] f){
		double eval = 0;
		for(int i=0; i < weights.length; i++){
			eval += f[i] * weights[i];
		}
		return eval;
	}
	
	/**
	 * a helper function to squash the evaluation score into range of [-1,+1] using tanh(x)
	 * @param n the number of current state
	 * @return a double between -1 and +1 
	 */
	public double convert_r(int n) {
		// first use conver_r to calculate the evaluation value
		return Math.tanh(evaluation(features.get(n)));
	}
}
