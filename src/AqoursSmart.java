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
					System.out.println("Read in: " + j + " " + i );
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
		printH(availMove);
	}

	@Override
	public void update(Move move) {
		if (move != null) {
			char player = board[move.j][move.i];
			int toi = move.i, toj = move.j;
			switch(move.d){
				case UP:	toj--; break;
				case DOWN:	toj++; break;
				case RIGHT:	toi++; break;
				case LEFT:	toi--; break;
			}
			board[toj][toi] = player;
			board[move.j][move.i] = '+';
		}
	}

	@Override
	public Move move() {
		Move move;
		this.availMove = getAvailMove(this.player, this.availMove);
		for (int[] key: this.availMove.keySet()) {
			if (this.availMove.get(key).isEmpty()) {
				continue;
			}
			move = new Move(key[1], this.dimension - key[0] - 1, this.availMove.get(key).get(0));
			return move;
		}
		return null;
	}
	
	public HashMap<int[], ArrayList<Move.Direction>> getAvailMove(char player, HashMap<int[], ArrayList<Move.Direction>> availmove) {
		for (int[] key: availmove.keySet()) {
			for (Move.Direction d: Move.Direction.values()) {
				if (player == 'H' && d == Move.Direction.LEFT) {
					continue;
				}
				if (player == 'V' && d == Move.Direction.DOWN) {
					continue;
				}
				int toi = key[1], toj = key[0];
				switch(d){
					case UP:	toj--; break;
					case DOWN:	toj++; break;
					case RIGHT:	toi++; break;
					case LEFT:	toi--; break;
				}
				if (inBound(toj, toi, player) && this.board[toj][toi] == '+') {
					availmove.get(key).add(d);
				}
			}
		}
		return availmove;
	}

    /**
     * a helper function to check whether a location is in bound
     * @param row row number
     * @param col col number
     * @return true of false
     */
    private boolean inBound(int col, int row, char player){
        if (row < 0 || col < 0 || row >= this.dimension || col >= this.dimension){
        	if ((player == 'H' && col >= this.dimension) || (player == 'V' && row < 0)) {
        		return true;
        	}
        	return false;
        }
        return true;
    }

    public void printH(HashMap<int[], ArrayList<Move.Direction>> h) {
    	for (int[] key: h.keySet()) {
    		System.out.println(key[0] + " " + key[1]);
    	}
    }
}
