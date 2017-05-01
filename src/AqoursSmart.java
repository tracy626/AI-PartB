/**
 * Yue Fang 715889
 * Zhe Tang 743398
 * COMP30024 Artificial Intelligence
 * Project Part B
 */

import java.util.ArrayList;
import java.util.HashMap;

import aiproj.slider.Move;
import aiproj.slider.SliderPlayer;

public class AqoursSmart implements SliderPlayer {

	private char [][] board;
	private int dimension;
	private char player;
	private HashMap<int[], ArrayList<Move.Direction>> availMove;
	
	@Override
	public void init(int dimension, String board, char player) {
		this.dimension = dimension;
		this.player = player;
		this.board = new char[dimension][];
		this.availMove = new HashMap();
		int count = 0, i = 0, j = dimension - 1;
		while (count < board.length()) {
			if (i == 0) {
				this.board[j] = new char[dimension];
			}
			if (i < dimension) {
				if (board.charAt(count) == player) {
//					System.out.println("Read in: " + j + " " + i );
					int[] position = {j, i};
					availMove.put(position, new ArrayList<Move.Direction>());
				}
				this.board[j][i++] = board.charAt(count);
				count += 2;
			} else {
				j--;
				i = 0;
			}
		}
//		printH(availMove);
	}

	@Override
	public void update(Move move) {
		if (move != null) {
			int i = move.i, j = move.j;
			char player = this.board[j][i];
			int toi = newPosition(j, i, move.d)[1], toj = newPosition(j, i, move.d)[0];

			if(toi < this.dimension && toj < this.dimension){
				this.board[toj][toi] = player;
			}
			this.board[j][i] = '+';
			System.out.println("j: " + j + " i: "+i+" d: "+ move.d+" new pos: "+toj+" "+toi + " player: " + player);
		}
	}

	@Override
	public Move move() {
		Move move;
		getAvailMove(this.player, this.availMove);
		for (int[] key: this.availMove.keySet()) {
			System.out.println(this.availMove.get(key));
			if (this.availMove.get(key).isEmpty()) {
				continue;
			}
			printH(availMove);
			move = new Move(key[1], key[0], this.availMove.get(key).get(0));
			
			int npos[] = newPosition(key[0], key[1], move.d);
			this.availMove.remove(key);
			if (npos[0] < dimension && npos[1] < dimension) {
				this.availMove.put(npos, new ArrayList<Move.Direction>());
			}
			update(move);
//			printH(availMove);
			return move;
		}
		return null;
	}
	
	/**
     * get available move for player indicated in the parameter,
     * and save the position as key and available move into an ArrayList as value of the HashMap,
     * @param player indicate which piece is querying, 'H' for horizontal, 'V' for vertical
     *        availmove indicate the HashMap which maps the position of all pieces on the board 
     *        			and the available move of each piece
     */
	private void getAvailMove(char player, HashMap<int[], ArrayList<Move.Direction>> availmove) {
		for (int[] key: availmove.keySet()) {
			availmove.put(key, new ArrayList<Move.Direction>());
			for (Move.Direction d: Move.Direction.values()) {
				if (player == 'H' && d == Move.Direction.LEFT) {
					continue;
				}
				if (player == 'V' && d == Move.Direction.DOWN) {
					continue;
				}
//				availmove.get(key).remove(d);
				int npos[] = newPosition(key[0], key[1], d);
				if (inBound(npos[0], npos[1]) && this.board[npos[0]][npos[1]] == '+') {
					availmove.get(key).add(d);
//					System.out.println("HERE??? "+key[0]+" "+key[1]+ ": "+d);
				} else if (!inBound(npos[0], npos[1]) && ((player == 'H' && d == Move.Direction.RIGHT) || (player == 'V' && d == Move.Direction.UP))){
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

    private void printH(HashMap<int[], ArrayList<Move.Direction>> h) {
    	System.out.println("Ava Move:");
    	for (int[] key: h.keySet()) {
    		System.out.println(key[0] + " " + key[1]);
    	}
    }
    
    private void printB() {
    	System.out.println("BOARD: ");
    	for (int j = dimension -1; j>=0; j--) {
    		for (int i =0; i<dimension; i++) {
    			System.out.print(board[j][i]+" ");
    		}
    		System.out.println("");
    	}
    }
    
    
    private int[] newPosition(int j, int i, Move.Direction d) {
    	int toi = i, toj = j;
    	int pos[] = new int[2];
		switch(d){
			case UP:	toj++; break;
			case DOWN:	toj--; break;
			case RIGHT:	toi++; break;
			case LEFT:	toi--; break;
		}
		pos[0] = toj;
		pos[1] = toi;
		return pos;
    }
    
    private Move bestMove(){
    	return null;
    }
    
}
