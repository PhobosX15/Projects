package Game.strategy;

import UI.DummyBoard;
import UI.Edge;
import UI.GameBoard;

import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;


public class MinimaxAI extends GameStrategy {

    private int maxDepth;
    private  int MIN = -1000000000;
    private int MAX = 1000000000;

    public MinimaxAI( boolean isPlayer1, Color color) {
        super("Minimax", isPlayer1, color);
    }


    @Override
    public Edge makeMove(DummyBoard gameBoard) {
        maxDepth = 3;
        return AiMinmaxStart(gameBoard, maxDepth);


    }


    //Minimax  AI
    public int minimax(DummyBoard b, int depth, Boolean maximizingPlayer) {

        if (depth == 0 || b.getMoves().size()==0) {//Depth reached or game is over.{
            return b.calculateScorePlayer2()-b.calculateScorePlayer1();
        }

        if (maximizingPlayer) {//AI
            int maxEva = -2147483647;//Negative infinity
            int score = 0;
            //Checks every possible move AI can make
            ArrayList<Edge> moves = b.getMoves();
            DummyBoard temp;
            for(Edge edge : moves){
                temp= new DummyBoard(b.n);
                temp.copyGameBoard(b);
                temp.processAIMove2(edge);
                if (!temp.getCurrentPlayer().isPlayer1) {
                    score = minimax(temp, depth-1 , true);
                }else {
                    score = minimax(temp, depth - 1, false);
                }
                maxEva = Math.max(maxEva, score);

            }
            return maxEva;

        }
        else {//Players turn calculation

            int minEva = 2147483647;//Positive infinity
            int score = 0;
            //Checks every possible move Human player can make
            ArrayList<Edge> moves = b.getMoves();
            DummyBoard temp;

            for(Edge edge : moves){
                temp= new DummyBoard(b.n);
                temp.copyGameBoard(b);
                temp.processAIMove2(edge);
                if (temp.getCurrentPlayer().isPlayer1) {
                    score = minimax(temp, depth -1, false);
                }
                else {
                    score = minimax(temp, depth - 1, true);
                }
                minEva = Math.min(minEva, score);
            }

            return minEva;
        }


    }


    public Edge AiMinmaxStart(DummyBoard board,int depth) {
        Edge pos=null;
        int max = -2147483647;
        int eval = 0;

        ArrayList<Edge> moves = board.getMoves();
        DummyBoard temp;

        for(Edge edge : moves){
            temp= new DummyBoard(board.n);
            temp.copyGameBoard(board);
            //temp.fillEdge(edge);
            temp.processAIMove2(edge);
            if (!temp.getCurrentPlayer().isPlayer1) {
                eval = minimax(temp, depth, true);
            }else {
                eval = minimax(temp, depth, false);
            }

            if (eval > max) {
                max = eval;
                pos = edge;
            }
        }
        //System.out.println("Best move x : " + pos.getX() +  "y : " + pos.getY() );
        return pos;
    }
}
