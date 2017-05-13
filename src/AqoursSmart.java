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
import aiproj.slider.SliderPlayer;

/**
 * class for our slideron AI
 */
public class AqoursSmart implements SliderPlayer {
	private int dimension;
	private char player;
	private char opplayer;
	private HashMap<Point, ArrayList<Direction>> availMove, op_availMove;
	private ArrayList<Point> block;
	private TDleaf tdleaf;
	private Integer[] temp_f, feature_a, feature_b;
	
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
     * @param col col number
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
    	Move move = null;
    	Integer max_i = 0, max_j = 0;
    	Double max_value = 0.0, value;
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
    			value = min_Value(null, null, ncurP, nopP, 4, d);
    			if (flag) {
    				max_value = value;
    				max_i = key.x;
    				max_j = key.y;
    				max_dir = d;
    				flag = false;
    			}else if(max_value != null && value != null && max_value < value) {
    				max_value = value;
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
    	this.tdleaf.features.add(feature_a);
		this.tdleaf.prove_w();
    	return move;
    }

    /**
     * Min-value function
     * @param alpha a number help to pruning the useless path
     * @param beta a number help to pruning the useless path
     * @param curP the HashMap contain the player's pieces position map to the available move's direction
     * @param opP the HashMap contain the opponent player's pieces position and its available move
     * @param depth a number represent the depth of minimax searching
	 * @param d Direction
     * @return a double
     */
	private Double min_Value(Double alpha, Double beta, HashMap<Point, ArrayList<Direction>> curP, HashMap<Point, ArrayList<Direction>> opP, int depth, Direction dir) {
		HashMap<Point, ArrayList<Direction>> ncurP, nopP;
		depth--;
		if (cutoff_test(depth,curP,opP)) {
			this.temp_f = this.tdleaf.detect_f(curP, opP, dir, player);
			return this.tdleaf.convert_r(this.temp_f);
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

				Double max_v = max_Value(alpha, beta, ncurP, nopP, depth, dir);
				if(beta == null || max_v == null) {
					beta = max_v;
					this.feature_b = this.temp_f.clone();
				}else{
					if(max_v < beta){
						beta = max_v;
						this.feature_b = this.temp_f.clone();
					}
				}
				if(alpha != null && beta != null && alpha >= beta){
					return alpha;
				}
			}
		}
		return beta;
	}

	private Double max_Value(Double alpha, Double beta, HashMap<Point, ArrayList<Direction>> curP, HashMap<Point, ArrayList<Direction>> opP, int depth, Direction dir) {
		HashMap<Point, ArrayList<Direction>> ncurP, nopP;
		depth--;
		if (cutoff_test(depth,curP,opP)) {
			this.temp_f = this.tdleaf.detect_f(curP, opP, dir, player);
			return this.tdleaf.convert_r(this.temp_f);
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
				Double min_v = min_Value(alpha, beta, ncurP, nopP, depth, dir);
				if(alpha == null || min_v == null){
					alpha = min_v;
					this.feature_a = this.feature_b.clone();
				}else{
					if(min_v > alpha){
						alpha = min_v;
						this.feature_a = this.feature_b.clone();
					}
				}
				if(beta !=null && alpha != null && alpha >= beta){
					return beta;
				}
			}
		}
		return alpha;
	}
	
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
