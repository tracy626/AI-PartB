package aiproj.slider;

import aiproj.slider.Move;
import aiproj.slider.SliderPlayer;

import java.util.*;

/**
 * Created by mwang on 1/05/2017.
 */
public class Stupid2 implements SliderPlayer{
    private char myName,oppoName;
    private HashMap<Integer, Position> myHash = new HashMap();
    private HashMap<Integer, Position> opponentHash = new HashMap();
    private HashMap<Integer, Position> bHash = new HashMap();
    private HashMap<Integer, Position> pHash = new HashMap();
    private ArrayList<HashMap<Integer, Character>> boardHash = new ArrayList<>();
//    private HashMap<Position, Character> boardHash = new HashMap();

    public void init(int dimension, String board, char player){
        /** read through the board from left top corner which is (dim-1,0) */
        int i = 0, row = dimension-1, col = 0, bNum = 0, myNum = 0, oppoNum = 0, pNum = 0;
        /** know myself whether i am a H piece or V piece */
        myName = player;
        /** know my opponent name */
        if (myName == 'V'){
            oppoName = 'H';
        }
        else{
            oppoName = 'V';
        }
        /** create dimensions row hashmaps for board */
        for (i = 0; i <= dimension; i++) {
            boardHash.add(new HashMap<>());
        }
        i = 0;
        while (i < board.length()){
            if ((board.charAt(i) != ' ')&& (board.charAt(i) != '\n')) {
                // only if the there is a piece or block or +
                if (board.charAt(i) == '+') {
                    pHash.put(pNum ++, new Position(row, col));
                } else if (board.charAt(i) == 'B') {
                    bHash.put(bNum ++, new Position(row, col));
                } else if (board.charAt(i) == player) {
                    myHash.put(myNum ++, new Position(row, col));
                } else {
                    opponentHash.put(oppoNum ++, new Position(row, col));
                }
                /** no matter what kind of piece it is put on the boardHash */
                boardHash.get(row).put(col, board.charAt(i));

                /** if the piece is the last one of this row */
                if (col == dimension - 1) {
                    row--;
                    col = 0;
                } else {
                    /** go to next one */
                    col++;

                }
            }
            i++;
        }
        /** make boardHash a bit broader
         * ------------
         * |B B B B B |
         * |H + + + + |
         * |H + + + + |
         * |H + + + + |
         * |  V V V + |
         * ------------
         * This is based on H piece
         */
        if (myName == 'H') {
            for (i = 0; i <= dimension - 1; i++) {
                boardHash.get(i).put(dimension, '+');
            }
            for (i = 0; i <= dimension - 1; i++) {
                boardHash.get(dimension).put(i, 'B');
            }
        }
        else{
            for (i = 0; i <= dimension - 1; i++) {
                boardHash.get(dimension).put(i, '+');
            }
            for (i = 0; i <= dimension - 1; i++) {
                boardHash.get(i).put(dimension, 'B');
            }
        }
//        Set set = myHash.entrySet();
//        Iterator it = set.iterator();
//        while (it.hasNext()) {
//            Map.Entry me = (Map.Entry) it.next();
//            System.out.printf("The Position is (%d,%d), the value on it is %c\n", ((Position) me.getKey()).getRow(), ((Position) me.getKey()).getColumn(), me.getValue());
//            System.out.printf("The Position is (%d,%d), the value on it is %c\n", ((Position) me.getValue()).getRow(), ((Position) me.getValue()).getColumn(), me.getKey());
//        }
//        System.out.printf("finished!\n");
//        Set set;
//        Iterator it;
//        System.out.printf("Board is :\n\n\n");
//        for ( i = 0; i < boardHash.size(); i++){
//            set = boardHash.get(i).entrySet();
//            it = set.iterator();
//            while (it.hasNext()) {
//                Map.Entry me = (Map.Entry) it.next();
//                System.out.printf("The Position is (%d,%d), the value on it is %c\n", i, me.getKey(), me.getValue());
//            }
//        }
//        System.out.printf("finished!\n");
    }

    public void update(Move move){
        if (move != null) {
            Position oppoPos = new Position(move.j, move.i);
            Position tmp;
            Move.Direction direction = move.d;
            if (direction == Move.Direction.RIGHT){
                tmp = new Position(move.j,move.i + 1);
            }
            else if (direction == Move.Direction.LEFT){
                tmp = new Position(move.j,move.i - 1);
            }
            else if (direction == Move.Direction.UP){
                tmp = new Position(move.j + 1,move.i);
            }
            else {
                tmp = new Position(move.j - 1,move.i);
            }
            boardHash.get(oppoPos.getRow()).replace(oppoPos.getColumn(), '+');
            boardHash.get(tmp.getRow()).replace(tmp.getColumn(), oppoName);
        }
    }


    public Move move(){

        boolean tryR = false, tryL = false, tryU = false, tryD = false, canR = false,canL = false, canU = false, canD = false;
        Move movement;
        Set set = myHash.entrySet();
        Iterator it = set.iterator();
//        System.out.printf("my name is %c\n",myName);
        if (myName == 'H') {
            tryR = true;
        }
        else {
            tryU = true;
        }
        while (it.hasNext()){
            Map.Entry me = (Map.Entry)it.next();
            Position tmp = (Position) me.getValue();
//            System.out.printf("tmp is (%d, %d)\n",tmp.getRow(),tmp.getColumn());
//            System.out.printf("The Position is (%d,%d), the value on it is %c\n", ((Position) me.getValue()).getRow(), ((Position) me.getValue()).getColumn(), me.getKey());
            /** if it is H and it is out of the edge of the winning side of the board */
            if ((myName == 'H') && tmp.getColumn() == boardHash.size() - 1){
                it.remove();
                /** change it back to + */
                boardHash.get(tmp.getRow()).replace(tmp.getColumn(),'+');
                /** go back to the first element and loop again */
                it = set.iterator();
                continue;
            }
            /** if it is V and it is out of the edge of the winning side of the board */
            else if ((myName == 'V') && tmp.getRow() == boardHash.size() - 1){
                it.remove();
                /** change it back to + */
                boardHash.get(tmp.getRow()).replace(tmp.getColumn(),'+');
                /** go back to the first element and loop again */
                it = set.iterator();
                continue;
            }

            if (myName == 'H') {
                if (tryR) {
                    // if can go right
                    if (canRight(tmp, boardHash)) {
                        movement = goRight(tmp, boardHash, me);
                        return movement;
                    }
                    else{
                        // and if it is the last one
                        if (!it.hasNext()){
                            tryR = false;
                            tryU = true;
                            it = set.iterator();
                        }
                        continue;
                    }
                }
                else if (tryU) {
                    // if can go up
                    if (canUp((Position) me.getValue(), boardHash)) {
                        movement = goUp(tmp, boardHash, me);
                        return movement;
                    }
                    else{
                        // and if it is the last one
                        if (!it.hasNext()){
                            tryU = false;
                            tryD = true;
                            it = set.iterator();
                        }
                        continue;
                    }
                }
                else if (tryD) {
                    // if can go down
                    /** if it is not at the edge of board */
                    if (canDown((Position) me.getValue(), boardHash)) {
                        movement = goDown(tmp, boardHash, me);
                        return movement;
                    }
                    else{
                        // and if it is the last one
                        if (!it.hasNext()){
                            tryD = false;
                            it = set.iterator();
                        }
                        continue;
                    }
                }
                // if cannot move
                else{
                    return null;
                }
            }
            else {
                if (tryU) {
                    // if can go up
                    if (canUp((Position) me.getValue(), boardHash)) {
                        movement = goUp(tmp, boardHash, me);
                        return movement;
                    }
                    else{
                        // and if it is the last one
                        if (!it.hasNext()){
                            tryU = false;
                            tryR = true;
                            it = set.iterator();
                        }
                        continue;
                    }
                }
                else if (tryR) {
                    // if can go right
                    if (canRight(tmp, boardHash)) {
                        movement = goRight(tmp, boardHash, me);
                        return movement;
                    }
                    else{
                        // and if it is the last one
                        if (!it.hasNext()){
                            tryR = false;
                            tryL = true;
                            it = set.iterator();
                        }
                        continue;
                    }
                }
                else if (tryL) {
                    // if can go left
                    /** if it is not at the edge of board */
                    if (canLeft(tmp, boardHash)) {
                        movement = goLeft(tmp, boardHash, me);
                        return movement;
                    }
                    else{
                        // and if it is the last one
                        if (!it.hasNext()){
                            tryL = false;
                            it = set.iterator();
                        }
                        continue;
                    }
                }
                // if cannot move
                else{
                    return null;
                }
            }
        }
//        System.out.printf("Board is :\n\n\n");
//        for (int i = 0; i < boardHash.size(); i++){
//            set = boardHash.get(i).entrySet();
//            it = set.iterator();
//            while (it.hasNext()) {
//                Map.Entry me = (Map.Entry) it.next();
//                System.out.printf("The Position is (%d,%d), the value on it is %c\n", i, me.getKey(), me.getValue());
//            }
//        }
//        System.out.printf("finished!\n");
        return null;
    }

    public boolean canLeft(Position pos,ArrayList<HashMap<Integer,Character>> boardMap){
        if (pos.getColumn() != 0 && boardMap.get(pos.getRow()).get(pos.getColumn() - 1) == '+'){
            return true;
        }
        else{
            return false;
        }
    }

    public boolean canRight(Position pos,ArrayList<HashMap<Integer,Character>> boardMap){
        if (boardMap.get(pos.getRow()).get(pos.getColumn() + 1) == '+'){
            return true;
        }
        else{
            return false;
        }
    }

    public boolean canUp(Position pos,ArrayList<HashMap<Integer,Character>> boardMap){
        if (boardMap.get(pos.getRow() + 1).get(pos.getColumn()) == '+'){
            return true;
        }
        else{
            return false;
        }
    }

    public boolean canDown(Position pos,ArrayList<HashMap<Integer,Character>> boardMap){
        if (pos.getRow() != 0 && boardMap.get(pos.getRow() - 1).get(pos.getColumn()) == '+'){
            return true;
        }
        else{
            return false;
        }
    }

    public Move goRight(Position pos,ArrayList<HashMap<Integer,Character>> boardMap, Map.Entry me) {
        boardMap.get(pos.getRow()).replace(pos.getColumn(), '+');
        boardMap.get(pos.getRow()).replace(pos.getColumn() + 1, myName);
        /** if it is H and it is at the edge of the winning side of the board */
        myHash.replace((int) me.getKey(), new Position(pos.getRow(), pos.getColumn() + 1));
        return new Move(pos.getColumn(), pos.getRow(), Move.Direction.RIGHT);
    }

    public Move goUp(Position pos,ArrayList<HashMap<Integer,Character>> boardMap, Map.Entry me) {
        boardMap.get(pos.getRow()).replace(pos.getColumn(), '+');
        boardMap.get(pos.getRow() + 1).replace(pos.getColumn(), myName);
        /** if it is V and it is at the edge of the winning side of the board */
        myHash.replace((int) me.getKey(), new Position(pos.getRow() + 1, pos.getColumn()));
        return new Move(pos.getColumn(), pos.getRow(), Move.Direction.UP);
    }
    public Move goLeft(Position pos,ArrayList<HashMap<Integer,Character>> boardMap, Map.Entry me) {
        boardMap.get(pos.getRow()).replace(pos.getColumn(), '+');
        boardMap.get(pos.getRow()).replace(pos.getColumn() - 1, myName);
        myHash.replace((int)me.getKey(), new Position(pos.getRow(), pos.getColumn() - 1));
        return new Move(pos.getColumn(), pos.getRow(), Move.Direction.LEFT);

    }
    public Move goDown(Position pos,ArrayList<HashMap<Integer,Character>> boardMap, Map.Entry me) {
        boardMap.get(pos.getRow()).replace(pos.getColumn(), '+');
        boardMap.get(pos.getRow() - 1).replace(pos.getColumn(), myName);
        myHash.replace((int)me.getKey(), new Position(pos.getRow() - 1, pos.getColumn()));
        return new Move(pos.getColumn(), pos.getRow(), Move.Direction.DOWN);
    }
}
