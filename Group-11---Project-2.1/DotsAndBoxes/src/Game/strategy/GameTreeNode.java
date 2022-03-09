package Game.strategy;

import UI.Edge;
import UI.GameBoard;

public class GameTreeNode {

    private GameBoard gameBoard;
    private  GameTreeNode parent;
    private int evaluation_score;
    private boolean isPlayer1;
    private Edge edge;

    public GameTreeNode(GameBoard gameBoard, GameTreeNode parent, boolean isPlayer1, Edge edge){
        this.gameBoard = gameBoard;
        this.parent = parent;
        this.isPlayer1 = isPlayer1;
        this.edge = edge;
        this.evaluation_score = Integer.MIN_VALUE;
    }

    public GameBoard getState(){
        return this.gameBoard;
    }

    public int getEvaluation_score(){
        return this.evaluation_score;
    }

    public void setEvaluation_score(int evaluation_score){
        this.evaluation_score = evaluation_score;
    }

    public Edge getEdge(){
        return this.edge;
    }

    public void setEdge(Edge edge){
        this.edge = edge;
    }

    public boolean isPlayer1(){
        return this.isPlayer1;
    }

    public GameTreeNode getParent(){
        return this.parent;
    }

}
