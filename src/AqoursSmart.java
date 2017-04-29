import aiproj.slider.Move;
import aiproj.slider.SliderPlayer;

public class AqoursSmart implements SliderPlayer {

	private char [][] board;
	
	@Override
	public void init(int dimension, String board, char player) {
		this.board = new char[dimension][];
		int count = 0;
		int period = 0;
		int i = 0;
		int j = 0;
		while (count < board.length()) {
			if (period == 0) {
				this.board[j] = new char[dimension];
				i = 0;
			}
			if (period < dimension) {
				this.board[j][i++] = board.charAt(count);
				period++;
			} else {
				j++;
				period = 0;
			}
			count += 2;
		}
	}

	@Override
	public void update(Move move) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public Move move() {
		// TODO Auto-generated method stub
		return null;
	}

}
