package Game.strategy;

import UI.DummyBoard;
import UI.Edge;
import UI.GameBoard;

import java.awt.*;
import java.util.ArrayList;

public class AlphaBeta extends GameStrategy{

    private int maxDepth;
    private  int MIN = -2147483647;
    private int MAX = 2147483647;

    public AlphaBeta( boolean isPlayer1, Color color) {
        super("ab", isPlayer1, color);
    }

    public int alphaBeta(DummyBoard b, int depth, Boolean maximizingPlayer,int alpha,int beta) {

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
                    score = alphaBeta(temp, depth-1 , true,alpha,beta);
                }else {
                    score = alphaBeta(temp, depth - 1, false,alpha,beta);
                }
                maxEva = Math.max(maxEva, score);
                if(maxEva <= alpha){
                    beta = Math.min(maxEva,score);
                    return maxEva;
                }

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
                    score = alphaBeta(temp, depth -1, false,alpha,beta);
                }
                else {
                    score = alphaBeta(temp, depth - 1, true,alpha,beta);
                }
                minEva = Math.min(minEva, score);
            }
            if(minEva <= beta){
                beta = Math.max(minEva,score);
                return minEva;
            }
            return minEva;
        }


    }

    public Edge AlphaBetaStart(DummyBoard board,int depth,int alpha , int beta) {
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
                eval = alphaBeta(temp, depth, true,alpha,beta);
            }else {
                eval = alphaBeta(temp, depth, false,alpha,beta);
            }


            if (eval > max) {
                max = eval;
                pos = edge;
            }
        }
        //System.out.println("Best move x : " + pos.getX() +  "y : " + pos.getY() );
        return pos;
    }
    @Override
    public Edge makeMove(DummyBoard gameBoard) {
        maxDepth = 3;
        return AlphaBetaStart(gameBoard, maxDepth,MIN,MAX);


    }


}
