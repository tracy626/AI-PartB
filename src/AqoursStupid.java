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
import aiproj.slider.SliderPlayer;

public class AqoursStupid implements SliderPlayer {

	private int dimension;
	private char player;
	private char opplayer;
	private HashMap<Point, ArrayList<Move.Direction>> availMove, op_availMove;
	private ArrayList<Point> block;
	
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
					availMove.put(pos, new ArrayList<Move.Direction>());
				} else if (board.charAt(count) == 'B') {
					block.add(pos);
				} else if (board.charAt(count) != '+') {
					op_availMove.put(pos, new ArrayList<Move.Direction>());
				}
				i++;
				count += 2;
			} else {
				j--;
				i = 0;
			}
		}
	}

	@Override
	public void update(Move move) {
		if (move != null) {
			Point pos = new Point(move.i, move.j);
			this.op_availMove.remove(pos);
			Point newpos = newPosition(pos.x, pos.y, move.d);
			if(newpos.x < this.dimension && newpos.y < this.dimension) {
				this.op_availMove.put(newpos, new ArrayList<Move.Direction>());
			}
		}
	}

	@Override
	public Move move() {
//		Move move;
		getAvailMove(this.player, this.availMove);
		getAvailMove(this.opplayer, this.op_availMove);
	return minimax_Decision();
	}
	
	/**
     * get available move for player indicated in the parameter,
     * and save the position as key and available move into an ArrayList as value of the HashMap,
     * @param player indicate which piece is querying, 'H' for horizontal, 'V' for vertical
     *        availmove indicate the HashMap which maps the position of all pieces on the board 
     *        			and the available move of each piece
     */
	private void getAvailMove(char player, HashMap<Point, ArrayList<Move.Direction>> availmove) {
		for (Point key: availmove.keySet()) {
			availmove.put(key, new ArrayList<Move.Direction>());
			for (Move.Direction d: Move.Direction.values()) {
				if (player == 'H' && d == Move.Direction.LEFT) {
					continue;
				}
				if (player == 'V' && d == Move.Direction.DOWN) {
					continue;
				}
//				availmove.get(key).remove(d);
				Point npos = newPosition(key.x, key.y, d);
				Object t = npos;
				if (inBound(npos.x, npos.y) && !this.availMove.containsKey(npos) && !this.op_availMove.containsKey(npos) && !block.contains(npos)) {
					availmove.get(key).add(d);
				} else if (!inBound(npos.x, npos.y) && ((player == 'H' && d == Move.Direction.RIGHT) || (player == 'V' && d == Move.Direction.UP))){
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

    private void printH(HashMap<Point, ArrayList<Move.Direction>> h) {
    	System.out.println("Ava Move:");
    	for (Point key: h.keySet()) {
    		System.out.println(key.x + " " + key.y);
    	}
    }
    
    private Point newPosition(int i, int j, Move.Direction d) {
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
    
    protected Move minimax_Decision() {
    	Move move = null;
    	Integer max_i = 0, max_j = 0, max_value = 0, value;
    	Move.Direction max_dir = null;
    	boolean flag=true;
    	
    	for (Point key: this.availMove.keySet()) {
    		for (Move.Direction d: this.availMove.get(key)) {
    			value = min_Value(null,null,this.availMove, this.op_availMove, 5, d);
//    			if (value == null) {
//    				return null;
//    			}
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
    			this.availMove.put(newpos, new ArrayList<Move.Direction>());
    		}
    		
    	}
    	return move;
    }

	private Integer min_Value(Integer alpha, Integer beta, HashMap<Point, ArrayList<Move.Direction>> curP, HashMap<Point, ArrayList<Move.Direction>> opP, int depth, Move.Direction dir) {
		HashMap<Point, ArrayList<Move.Direction>> ncurP, nopP;
		depth--;
		if (cutoff_test(depth,curP,opP)) {
			return eval(curP,opP,dir);
		}
		for(Point key: opP.keySet()){
			for(Move.Direction d: opP.get(key)){
				ncurP = new HashMap(curP);
				nopP = new HashMap(opP);
				Point newpos = newPosition(key.x, key.y, d);
				nopP.remove(key);
				if (newpos.x < this.dimension && newpos.y < this.dimension){
					nopP.put(newpos, new ArrayList<Move.Direction>());
				}
				getAvailMove(this.opplayer, nopP);
				getAvailMove(this.player, ncurP);

				if(beta == null || max_Value(alpha, beta, ncurP, nopP, depth, dir) == null) {
					beta = max_Value(alpha, beta, ncurP, nopP, depth,dir);
				}else{
					beta = Math.min(max_Value(alpha, beta, ncurP, nopP, depth, dir), beta);
				}
				if(alpha != null && beta != null && alpha >= beta){
					return alpha;
				}
			}
		}

		return beta;
	}

	private Integer max_Value(Integer alpha, Integer beta, HashMap<Point, ArrayList<Move.Direction>> curP, HashMap<Point, ArrayList<Move.Direction>> opP, int depth, Move.Direction dir) {
		HashMap<Point, ArrayList<Move.Direction>> ncurP, nopP;
		depth--;
		if (cutoff_test(depth,curP,opP)) {
			return eval(curP,opP, dir);
		}
		for(Point key: curP.keySet()){
			for(Move.Direction d: curP.get(key)){
				ncurP = new HashMap(curP);
				nopP = new HashMap(opP);
				Point newpos = newPosition(key.x, key.y, d);
				ncurP.remove(key);
				if (newpos.x < this.dimension && newpos.y < this.dimension) {
					ncurP.put(newpos, new ArrayList<Move.Direction>());
				}
				getAvailMove(this.player, ncurP);
				getAvailMove(this.opplayer, nopP);
				if(alpha == null || min_Value(alpha, beta, ncurP, nopP, depth, dir) == null){
					alpha = min_Value(alpha, beta, ncurP, nopP, depth, dir);
				}else{
					alpha = Math.max(alpha, min_Value(alpha, beta, ncurP, nopP, depth, dir));
				}
				if(beta !=null && alpha != null && alpha >= beta){
					return beta;
				}
			}
		}
		return alpha;
	}

	private int eval(HashMap<Point, ArrayList<Move.Direction>> curP, HashMap<Point, ArrayList<Move.Direction>> opP, Move.Direction dir) {
		int score = 0;
		if(opP.size()<curP.size()){
			score -=10;
		}
		for(Point key: curP.keySet()){
			if(this.player == 'H'){
				for(Point opkey: opP.keySet()) {
					if (opkey.y == key.y && opkey.x > key.x) {
						score -= 20;
					}
					if (opkey.x == key.x && opkey.y < key.y) {
						score += 20;
					}
 				}
				if(block.contains(new Point(key.x+1,key.y))){
					score -= 25;
				}
				
				if(dir == Move.Direction.RIGHT){
					score += 40;
				}else{
					score -= 30;
				}
			}else{
				
				for(Point opkey: opP.keySet()) {
					if (opkey.x == key.x && opkey.y > key.y) {
						score -= 20;
					}
					if (opkey.y == key.y && opkey.x < key.x) {
						score += 20;
					}
 				}
				if(block.contains(new Point(key.x,key.y+1))){
					score -= 25;
				}
				if(dir == Move.Direction.UP){
					score += 50;
				}else{
					score -= 30;
				}
			}
		}
		return score;
	}
	
	private boolean cutoff_test(int depth, HashMap<Point, ArrayList<Move.Direction>> curP, HashMap<Point, ArrayList<Move.Direction>> opP) {
		if (depth == 0){
			return true;
		}
		if(curP.size() == 0 || opP.size() == 0){
			return true;
		}
		return false;
	}
}
