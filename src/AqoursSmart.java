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
		int count = 0, i = 0, j = 0;
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
				j++;
				i = 0;
			}
		}
//		printH(availMove);
	}

	@Override
	public void update(Move move) {
		if (move != null) {
			int i = move.i, j = this.dimension - move.j - 1;
			char player = board[j][i];
			int toi = newPosition(j, i, move.d)[1], toj = newPosition(j, i, move.d)[0];
			if(toi >= 0 || toj >= 0 || toi < this.dimension || toj > this.dimension){
				board[toj][toi] = player;
			}
			board[j][i] = '+';
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
			move = new Move(key[1], this.dimension - key[0] - 1, this.availMove.get(key).get(0));
			
			int npos[] = newPosition(key[0], key[1], move.d);
			this.availMove.remove(key);
			this.availMove.put(npos, new ArrayList<Move.Direction>());
			return move;
		}
		return null;
	}
	
	public void getAvailMove(char player, HashMap<int[], ArrayList<Move.Direction>> availmove) {
		for (int[] key: availmove.keySet()) {
			for (Move.Direction d: Move.Direction.values()) {
				if (player == 'H' && d == Move.Direction.LEFT) {
					continue;
				}
				if (player == 'V' && d == Move.Direction.DOWN) {
					continue;
				}
				availmove.get(key).remove(d);
				int npos[] = newPosition(key[0], key[1], d);
				if (inBound(npos[0], npos[1]) && this.board[npos[0]][npos[1]] == '+') {
					availmove.get(key).add(d);
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
     * @return true of false
     */
    private boolean inBound(int row, int col){
        if (row < 0 || col < 0 || row >= this.dimension || col >= this.dimension){
        	return false;
        }
        return true;
    }

    public void printH(HashMap<int[], ArrayList<Move.Direction>> h) {
    	for (int[] key: h.keySet()) {
    		System.out.println(key[0] + " " + key[1]);
    	}
    }
    
    public int[] newPosition(int j, int i, Move.Direction d) {
    	int toi = i, toj = j;
    	int pos[] = new int[2];
		switch(d){
			case UP:	toj--; break;
			case DOWN:	toj++; break;
			case RIGHT:	toi++; break;
			case LEFT:	toi--; break;
		}
		pos[0] = toj;
//		System.out.println("j: "+ j +" toj: " + toj + " d: " + d);
		pos[1] = toi;
//		System.out.println("i: "+ i +" toi: " + toi + " d: " + d);
		return pos;
    }
}
