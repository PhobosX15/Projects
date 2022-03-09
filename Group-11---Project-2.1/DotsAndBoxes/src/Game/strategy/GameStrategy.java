package Game.strategy;

import UI.DummyBoard;
import UI.Edge;
import UI.GameBoard;

import java.awt.*;

public abstract class GameStrategy {
    public Color color;
    public boolean isPlayer1;
    public String title;
    int score = 0;
    int twoMissingLine = -3;
    int oneMissingLine = 5;
    int notFilled = 7;

    /**
     * Each Strategy needs to receive a graph to be able to decide where to place an edge
     *
     * @return ##todo should we return edge or void, should we pass the graph or a copy of the graph ??
     */
    //should return edge
    public Edge makeMove(DummyBoard board) {
        return null;
    }


    public GameStrategy(String title, boolean isPlayer1, Color color) {
        this.title = title;
        this.isPlayer1 = isPlayer1;
        this.color = color;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        GameStrategy that = (GameStrategy) o;
        return isPlayer1 == that.isPlayer1 &&
                title.equals(that.title);
    }

    public void setScore(int i) {
        this.score = i;
    }

    public int getScore() {
        return score;
    }

    /**
     * heuristic function
     * @param board
     * @return
     * we can improve this by considering the length of the chains (for 1st
     * player even number of chains is a plus e.g.)
     */
    public int evaluationFunction(DummyBoard board) {
        score = 0;
        //initialize the score with some weight
        if (board.getCurrentPlayer().isPlayer1) {
            score = 20 * board.getScore(true) - 20 * board.getScore(false);
        } else {
            score = 20 * board.getScore(false) - 20 * board.getScore(true);
        }

        if (board.getCurrentPlayer().isPlayer1) {


            score += notFilled * board.getNumberOfBoxes(4) + twoMissingLine * board.getNumberOfBoxes(2)
                    + oneMissingLine * board.getNumberOfBoxes(1);

        } else {
            score -= notFilled * board.getNumberOfBoxes(4) + twoMissingLine * board.getNumberOfBoxes(2)
                    + oneMissingLine * board.getNumberOfBoxes(1);

        }

        return score;

    }

    public int evaluationFunctionGreedy(GameBoard board, boolean isPlayer1){
        return 0;
    }

    public boolean isApplicable(DummyBoard board){
        if(board.getNumberOfBoxes(1) == 0){
            return true;
        }
        return false;
    }

    /**
     * to add:
     * is applicable
     * chain length - odd or even
     * check if any chain could be extended - assign a score to it.
     */
}
