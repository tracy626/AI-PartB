/**
 * Yue Fang 715889 (fang1)
 * Zhe Tang 743398 (zhet1)
 * COMP30024 Artificial Intelligence
 * Project Part B
 */

import java.awt.Point;
import java.util.ArrayList;
import java.util.HashMap;

import aiproj.slider.Move;
import aiproj.slider.Move.Direction;
import aiproj.slider.SliderPlayer;

/**
 * class of our slideron AI
 */
public class AqoursSmart implements SliderPlayer {
	private int dimension;
	private char player;
	private char opplayer;
	private HashMap<Point, ArrayList<Direction>> availMove, op_availMove; 
	// HashMaps to store position and available move of player and opponent player
	private ArrayList<Point> block; // ArrayList to store the position of block(s)
	private TDleaf tdleaf;
	private Integer[] temp_f;
	// Integer arrays to store the features of the state help to do alpha-beta pruning in minimax research
	
	/** 
	 * Override the init function in SliderPlayer.
	 * 
	 * @param dimension The width and height of the board in cells
	 * @param board A string representation of the initial state of the board,
	 * as described in the part B specification
	 * @param player 'H' or 'V', corresponding to which pieces the player will
	 * control for this game ('H' = Horizontal, 'V' = Vertical)
	 */
	@Override
	public void init(int dimension, String board, char player) {
		this.dimension = dimension;
		this.player = player;
		this.op_availMove = new HashMap();
		this.block = new ArrayList<Point>();
		this.availMove = new HashMap();
		int count = 0, i = 0, j = dimension - 1;
		if(player == 'H'){
			this.opplayer = 'V';
		}else{
			this.opplayer = 'H';
		}
		while (count < board.length()) {
			if (i < dimension) {
				Point pos = new Point(i,j);
				if (board.charAt(count) == player) {
					availMove.put(pos, new ArrayList<Direction>());
				} else if (board.charAt(count) == 'B') {
					this.block.add(pos);
				} else if (board.charAt(count) != '+') {
					op_availMove.put(pos, new ArrayList<Direction>());
				}
				i++;
				count += 2;
			} else {
				j--;
				i = 0;
			}
		}
	this.tdleaf = new TDleaf(this.block, this.dimension);
	}

	/**
	 * Override the update function in SliderPlayer.
	 *
	 * @param move A Move object representing the previous move made by the 
	 * opponent, which may be null (indicating a pass). Also, before the first
	 * move at the beginning of the game, move = null.
	 */
	@Override
	public void update(Move move) {
		if (move != null) {
			Point pos = new Point(move.i, move.j);
			this.op_availMove.remove(pos);
			Point newpos = newPosition(pos.x, pos.y, move.d);
			if(newpos.x < this.dimension && newpos.y < this.dimension) {
				this.op_availMove.put(newpos, new ArrayList<Direction>());
			}
		}
	}

	/** 
	 * Override the move function in SliderPlayer.
	 *
	 * @return a Move object representing the move you would like to make
	 * at this point of the game, or null if there are no legal moves.
	 */
	@Override
	public Move move() {
		getAvailMove(this.player, this.availMove, this.op_availMove);
		getAvailMove(this.opplayer, this.op_availMove, this.availMove);
		this.tdleaf.count += 1;
		return minimax_Decision();
	}
	
	/**
     * get available move for player indicated in the parameter,
     * and save the position as key and available move into an ArrayList as value of the HashMap,
     * @param player indicate which piece is querying, 'H' for horizontal, 'V' for vertical
     *        availmove indicate the HashMap which maps the position of all pieces on the board 
     *        			and the available move of each piece
     */
	private void getAvailMove(char player, HashMap<Point, ArrayList<Direction>> availmove, HashMap<Point, ArrayList<Direction>> op_availmove) {
		for (Point key: availmove.keySet()) {
			availmove.put(key, new ArrayList<Direction>());
			for (Direction d: Direction.values()) {
				if (player == 'H' && d == Direction.LEFT) {
					continue;
				}
				if (player == 'V' && d == Direction.DOWN) {
					continue;
				}
				Point npos = newPosition(key.x, key.y, d);
				if (inBound(npos.x, npos.y) && !availmove.containsKey(npos) && !op_availmove.containsKey(npos) && !block.contains(npos)) {
					availmove.get(key).add(d);
				} else if (!inBound(npos.x, npos.y) && ((player == 'H' && d == Direction.RIGHT) || (player == 'V' && d == Direction.UP))){
					availmove.get(key).add(d);
				}
			}
		}
	}

    /**
     * a helper function to check whether a location is in bound
     * @param row row number
     * @param col column number
     * @return true or false
     */
    private boolean inBound(int row, int col){
        if (row < 0 || col < 0 || row >= this.dimension || col >= this.dimension){
        	return false;
        }
        return true;
    }
  
    /**
     * a helper function to create the new point of the given position and direction
     * @param i x in Cartesian
     * @param j y in 
	 * @param d Direction
     * @return Point of the new position
     */
    private Point newPosition(int i, int j, Direction d) {
    	int toi = i, toj = j;
    	Point pos;
		switch(d){
			case UP:	toj++; break;
			case DOWN:	toj--; break;
			case RIGHT:	toi++; break;
			case LEFT:	toi--; break;
		}
    	pos = new Point(toi, toj);
		return pos;
    }
  
    /**
     * Make decision of Minimax-algorithms
     * @return a legal move
     */
    protected Move minimax_Decision() {
    	HashMap<Point, ArrayList<Direction>> ncurP, nopP;
    	Integer[] feature = {0, 0, 0, 0, 0, 0, 0, 0, 0, 0};
    	Move move = null;
    	Integer max_i = 0, max_j = 0;
    	Pair<Double, Integer[]> max_value = new Pair(Double.NEGATIVE_INFINITY, null), value;
    	Direction max_dir = null;
    	boolean flag=true;
    	
    	for (Point key: this.availMove.keySet()) {
    		for (Direction d: this.availMove.get(key)) {
    			ncurP = new HashMap(this.availMove);
				nopP = new HashMap(this.op_availMove);
				Point newpos = newPosition(key.x, key.y, d);

				ncurP.remove(key);
				if (newpos.x < this.dimension && newpos.y < this.dimension){
					ncurP.put(newpos, new ArrayList<Direction>());
				}
				getAvailMove(this.player, ncurP, nopP);
				getAvailMove(this.opplayer, nopP, ncurP);
    			value = min_Value(new Pair(Double.NEGATIVE_INFINITY, null), new Pair(Double.POSITIVE_INFINITY, null), ncurP, nopP, 4);
    			if (flag) {
					max_value = new Pair(value.getValue(), value.getFeature().clone());
    				max_i = key.x;
    				max_j = key.y;
    				max_dir = d;
    				flag = false;
    			}else if(max_value.getValue() < value.getValue()) {
    				max_value = new Pair(value.getValue(), value.getFeature().clone());
    				max_i = key.x;
    				max_j = key.y;
    				max_dir = d;
    			}
    		}
    	}
    	if (!flag) {
    		move = new Move(max_i, max_j, max_dir);
    		this.availMove.remove(new Point(max_i, max_j));
    		Point newpos = newPosition(max_i, max_j, max_dir);
    		if (newpos.x < this.dimension && newpos.y < this.dimension) {
    			this.availMove.put(newpos, new ArrayList<Direction>());
    		}
    		
    	}
    	if(max_value.getFeature()==null){
    		max_value.setFeature(this.tdleaf.detect_f(this.availMove, this.op_availMove, player));
		}
		this.tdleaf.features.add(max_value.getFeature().clone());
		this.tdleaf.prove_w();
    	return move;
    }

    /**
     * Min-value function
     * @param alpha a number help to pruning the useless path and the feature of that leaf
     * @param beta a number help to pruning the useless path and the feature of that leaf
     * @param curP the HashMap store the player's pieces position map to the available move's direction
     * @param opP the HashMap store the opponent player's pieces position and its available move
     * @param depth a number represent the depth of minimax searching
     * @return a double number and feature of the determined leaf
     */
	private Pair<Double, Integer[]> min_Value(Pair<Double, Integer[]> alpha, Pair<Double, Integer[]> beta, HashMap<Point, ArrayList<Direction>> curP, HashMap<Point, ArrayList<Direction>> opP, int depth) {
		HashMap<Point, ArrayList<Direction>> ncurP, nopP;
		depth--;
		if (cutoff_test(depth,curP,opP)) {
			this.temp_f = this.tdleaf.detect_f(curP, opP, player);
			return new Pair(this.tdleaf.evaluation(this.temp_f), this.temp_f.clone());
		}
		for(Point key: opP.keySet()){
			for(Direction d: opP.get(key)){
				ncurP = new HashMap(curP);
				nopP = new HashMap(opP);
				Point newpos = newPosition(key.x, key.y, d);

				nopP.remove(key);
				if (newpos.x < this.dimension && newpos.y < this.dimension){
					nopP.put(newpos, new ArrayList<Direction>());
				}

				getAvailMove(this.opplayer, nopP, ncurP);
				getAvailMove(this.player, ncurP, nopP);
				Pair<Double, Integer[]> max_v = max_Value(alpha, beta, ncurP, nopP, depth);
				
				if(max_v.getValue() < beta.getValue()){
					beta = new Pair(max_v.getValue(), max_v.getFeature().clone());
				}
				if(alpha.getValue() >= beta.getValue()){
					if(alpha.getFeature()==null){
						alpha.setFeature(this.tdleaf.detect_f(curP, opP, player));
					}
					return alpha;
				}
			}
		}
		if(beta.getFeature()==null){
			beta.setFeature(this.tdleaf.detect_f(curP, opP, player));
		}
		return beta;
	}

    /**
     * Max-value function
     * @param alpha a number help to pruning the useless path and the feature of that leaf
     * @param beta a number help to pruning the useless path and the feature of that leaf
     * @param curP the HashMap store the player's pieces position map to the available move's direction
     * @param opP the HashMap store the opponent player's pieces position and its available move
     * @param depth a number represent the depth of minimax searching
     * @return a double number and feature of the determined leaf
     */
	private Pair<Double, Integer[]> max_Value(Pair<Double, Integer[]> alpha, Pair<Double, Integer[]> beta, HashMap<Point, ArrayList<Direction>> curP, HashMap<Point, ArrayList<Direction>> opP, int depth) {
		HashMap<Point, ArrayList<Direction>> ncurP, nopP;
		depth--;
		if (cutoff_test(depth,curP,opP)) {
			this.temp_f = this.tdleaf.detect_f(curP, opP, player);
			return new Pair(this.tdleaf.evaluation(this.temp_f), this.temp_f.clone());
		}
		for(Point key: curP.keySet()){
			for(Direction d: curP.get(key)){
				ncurP = new HashMap(curP);
				nopP = new HashMap(opP);
				Point newpos = newPosition(key.x, key.y, d);

				ncurP.remove(key);
				if (newpos.x < this.dimension && newpos.y < this.dimension) {
					ncurP.put(newpos, new ArrayList<Direction>());
				}
				getAvailMove(this.player, ncurP, nopP);
				getAvailMove(this.opplayer, nopP, ncurP);
				Pair<Double, Integer[]> min_v = min_Value(alpha, beta, ncurP, nopP, depth);
				
				if(min_v.getValue() > alpha.getValue()){
					alpha = new Pair(min_v.getValue(), min_v.getFeature().clone());
				}
				if(alpha.getValue() >= beta.getValue()){
					if(beta.getFeature()==null){
						beta.setFeature(this.tdleaf.detect_f(curP, opP, player));
					}
					return beta;
				}
			}
		}
		if(alpha.getFeature()==null){
			alpha.setFeature(this.tdleaf.detect_f(curP, opP, player));
		}
		return alpha;
	}

    /**
     * a helper function to determine whether the board should be cot off
     * @param depth a number represent the depth of minimax searching
     * @param curP the HashMap contain the player's pieces position map to the available move's direction
	 * @param opP the HashMap contain the opponent player's pieces position and its available move
     * @return true or false
     */
	private boolean cutoff_test(int depth, HashMap<Point, ArrayList<Direction>> curP, HashMap<Point, ArrayList<Direction>> opP) {
		if (depth == 0){
			return true;
		}
		if(curP.size() == 0 || opP.size() == 0){
			return true;
		}
		return false;
	}
}
