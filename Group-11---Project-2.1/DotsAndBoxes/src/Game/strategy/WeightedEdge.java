package Game.strategy;

import UI.Edge;

public class WeightedEdge implements Comparable<WeightedEdge>{

    private  int weight;
    private Edge edge;

    public WeightedEdge(Edge edge, int weight){
        this.edge = edge;
        this.weight = weight;
    }

    public Edge getEdge(){
        return this.edge;
    }

    public int getWeight(){
        return this.weight;
    }

    public void setWeight(int weight){
        this.weight = weight;
    }

    public void setEdge(Edge edge){
        this.edge = edge;
    }

    public int compareTo(WeightedEdge edge){
        return this.weight - edge.weight;
    }






}

