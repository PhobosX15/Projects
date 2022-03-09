package Game.strategy;

import UI.DummyBoard;
import UI.Edge;
import UI.GameBoard;

import java.awt.*;
import java.util.ArrayList;
import java.util.Random;

public class GreedyAI extends GameStrategy {

    public GreedyAI(boolean isPlayer1, Color color) {
        super("Greedy", isPlayer1, color);
    }


    @Override
    public Edge makeMove(DummyBoard board) {
        ArrayList<Edge> moves = board.getMoves();
        int[] ev_score = new int[moves.size()];
        ArrayList<Edge> bestMove = new ArrayList<>();
        ArrayList<DummyBoard> boards = new ArrayList<>();
        DummyBoard temp;

        for(Edge edge : moves){
            temp= new DummyBoard(board.n);
            temp.copyGameBoard(board);
            //temp.processAIMove(edge);
            temp.fillEdge(edge);
            temp.onlyFillBoxIfPossible();

            boards.add(temp);
            if(temp.calculateScorePlayer2() > board.calculateScorePlayer2()){

                bestMove.add(edge);

            }
        }

        for(int i = 0; i < bestMove.size(); i++){
            ev_score[i] = evaluationFunction(boards.get(i));
        }
        if(bestMove.isEmpty()){

            return moves.get(new Random().nextInt(moves.size()));
        }
        int max = 0;
        for(int i = 0; i < bestMove.size();i++){
            if(ev_score[i] > ev_score[max]){
                max = i;
            }
        }

        Edge output = bestMove.get(max);
        System.out.println("Best move x : " + output.getX() +  "y : " + output.getY() );
        //return moves.get(new Random().nextInt(moves.size())) ;
        return output;
    }


}
