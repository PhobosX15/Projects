package Game.strategy;

import UI.DummyBoard;
import UI.Edge;
import UI.GameBoard;


import java.awt.*;
import java.util.ArrayList;
import java.util.Random;


public class RandomAI  extends GameStrategy {

    public RandomAI(boolean isPlayer1, Color color) {
        super( "Random AI", isPlayer1, color);
    }

    /**
     * Each Strategy needs to receive a graph to be able to decide where to place an edge
     *
     * @param board
     * @return ##todo should we return edge or void, should we pass the graph or a copy of the graph ??
     */
    @Override
    public Edge makeMove(DummyBoard board) {
        ArrayList<Edge> moves = board.getMoves();

        ArrayList<Edge> bestMove = new ArrayList<>();
        DummyBoard temp;

        for(Edge edge : moves){
            temp= new DummyBoard(board.n);
            temp.copyGameBoard(board);
            //temp.processAIMove(edge);
            temp.fillEdge(edge);
            temp.onlyFillBoxIfPossible();


            if(temp.calculateScorePlayer2() > board.calculateScorePlayer2()){

                bestMove.add(edge);

            }
        }
        if(bestMove.isEmpty()){

            return moves.get(new Random().nextInt(moves.size()));
        }


        Edge output = bestMove.get(new Random().nextInt(bestMove.size()));
        System.out.println("Best move x : " + output.getX() +  "y : " + output.getY() );
        //return moves.get(new Random().nextInt(moves.size())) ;
        return output;
    }


}
