package UI;

import java.util.ArrayList;

import Game.strategy.GameStrategy;

public class DummyBoard {

    public GameStrategy player1;
    public GameStrategy player2;
    public GameStrategy currentPlayer;
    public int n;
    public int possibleBoxCount;
    public int scorePlayer1 = 0;
    public int scorePlayer2 = 0;
    public int comboBoxIndex;
    Edge move;
    public boolean[][] isSetHEdge;
    public boolean[][] isSetVEdge;
    public boolean[][] isSetBox;
    public int[][] boxOwner;
    public int[][] hEdgeOwner;
    public int[][] vEdgeOwner;

    public DummyBoard(int n) {
        this.n = n;
        isSetHEdge = new boolean[n - 1][n];
        hEdgeOwner = new int[n - 1][n];
        isSetVEdge = new boolean[n][n - 1];
        vEdgeOwner = new int[n][n - 1];
        isSetBox = new boolean[n - 1][n - 1];
        boxOwner = new int[n - 1][n - 1];
        for (int i = 0; i < isSetHEdge.length; i++) {
            for (int j = 0; j < isSetHEdge[0].length; j++) {
                isSetHEdge[i][j] = false;
                hEdgeOwner[i][j] = -1;
            }
        }
        for (int i = 0; i < isSetVEdge.length; i++) {
            for (int j = 0; j < isSetVEdge[0].length; j++) {
                isSetVEdge[i][j] = false;
                vEdgeOwner[i][j] = -1;
            }
        }
        for (int i = 0; i < isSetBox.length; i++) {
            for (int j = 0; j < isSetBox[0].length; j++) {
                isSetBox[i][j] = false;
                boxOwner[i][j] = -1;
            }
        }
        this.possibleBoxCount = (n - 1) * (n - 1);
    }

    public void copyGameBoard(DummyBoard realboard) {
        for (int i = 0; i < isSetHEdge.length; i++) {
            for (int j = 0; j < isSetHEdge[0].length; j++) {
                this.isSetHEdge[i][j] = realboard.isSetHEdge[i][j];
                this.hEdgeOwner[i][j] = realboard.hEdgeOwner[i][j];
            }
        }
        for (int i = 0; i < isSetVEdge.length; i++) {
            for (int j = 0; j < isSetVEdge[0].length; j++) {
                this.isSetVEdge[i][j] = realboard.isSetVEdge[i][j];
                this.vEdgeOwner[i][j] = realboard.vEdgeOwner[i][j];
            }
        }
        for (int i = 0; i < isSetBox.length; i++) {
            for (int j = 0; j < isSetBox[0].length; j++) {
                this.isSetBox[i][j] = realboard.isSetBox[i][j];
            }
        }
        for (int i = 0; i < boxOwner.length; i++) {
            for (int j = 0; j < boxOwner[0].length; j++) {
                this.boxOwner[i][j] = realboard.boxOwner[i][j];
            }
        }
        this.scorePlayer1 = realboard.scorePlayer1;
        this.scorePlayer2 = realboard.scorePlayer2;
        this.currentPlayer = realboard.getCurrentPlayer();
        this.player1 = realboard.player1;
        this.player2 = realboard.player2;
    }

    public boolean processMove(Edge location) {
        fillEdge(location);
        boolean boxFilled = onlyFillBoxIfPossible();
        if (!boxFilled) {
            switchPlayers();
        }
        return  boxFilled;
    }

    public void processAIMove2(Edge location) {
        fillEdge(location);
        if (!onlyFillBoxIfPossible()) {
            if (currentPlayer.isPlayer1) {
                currentPlayer = player2;
            } else {
                currentPlayer = player1;
            }
        } else {
        }
    }

    public boolean fillEdge(Edge edge) {
        //mark the edge within given coordinates as filled
        int playernumber = currentPlayer.isPlayer1 ? 1 : 2;
        if (edge.isHorizontal() && !isSetHEdge[edge.getX()][edge.getY()]) {
            isSetHEdge[edge.getX()][edge.getY()] = true;
            hEdgeOwner[edge.getX()][edge.getY()] = playernumber;
            return true;
        } else if (!edge.isHorizontal() && !isSetVEdge[edge.getX()][edge.getY()]) {
            isSetVEdge[edge.getX()][edge.getY()] = true;
            vEdgeOwner[edge.getX()][edge.getY()] = playernumber;
            return true;
        } else {
            return false;
        }
    }

    public boolean onlyFillBoxIfPossible() {
        boolean boxUpdated = false;
        for (int i = 0; i < isSetBox.length; i++) {
            for (int j = 0; j < isSetBox[0].length; j++) {
                if ((isSetHEdge[i][j]) && (isSetVEdge[i][j]) && (isSetHEdge[i][j + 1]) && (isSetVEdge[i + 1][j]) && !isSetBox[i][j]) {
                    isSetBox[i][j] = true;
                    possibleBoxCount--;
                    currentPlayer.setScore(currentPlayer.getScore() + 1);
                    if (currentPlayer.isPlayer1) {
                        boxOwner[i][j] = 1;
                    } else {
                        boxOwner[i][j] = 2;
                    }
                    boxUpdated = true;
                }
            }
        }
        return boxUpdated;
    }

    public void switchPlayers() {
        if(currentPlayer.isPlayer1){
            currentPlayer = player2;
        }else{
            currentPlayer = player1;
        }
    }
    public ArrayList<Edge> getMoves() {
        ArrayList<Edge> moves = new ArrayList<Edge>();
        for (int i = 0; i < isSetHEdge.length; i++) {
            for (int j = 0; j < isSetHEdge[0].length; j++) {
                if (!isSetHEdge[i][j]) {
                    moves.add(new Edge(i, j, true));
                }
            }
        }
        for (int i = 0; i < isSetVEdge.length; i++) {
            for (int j = 0; j < isSetVEdge[0].length; j++) {
                if (!isSetVEdge[i][j]) {
                    moves.add(new Edge(i, j, false));
                }
            }
        }
        return moves;
    }

    public GameStrategy getCurrentPlayer() {
        return this.currentPlayer;
    }

    public int getNumberOfBoxes(int missingLine) {
        int numBox = 0;
        for (int i = 0; i < isSetBox.length; i++) {
            for (int j = 0; j < isSetBox[0].length; j++) {
                if (getMissingLines(i, j) == missingLine) {
                    numBox++;
                }
            }
        }
        return numBox;
    }

    public int getMissingLines(int x, int y) {
        int missingLines = 0;
        missingLines = 4 - countEdges(x, y);
        return missingLines;
    }

    private int countEdges(int x, int y) {
        int numberEdges = 0;
        if (isSetHEdge[x][y]) {
            numberEdges++;
        }
        if (isSetHEdge[x][y + 1]) {
            numberEdges++;
        }
        if (isSetVEdge[x][y]) {
            numberEdges++;
        }
        if (isSetVEdge[x + 1][y]) {
            numberEdges++;
        }
        return numberEdges;
    }

    public int getScore(boolean isPlayer1) {
        if (isPlayer1) {
            return player1.getScore();
        }
        return player2.getScore();
    }

    public int calculateScorePlayer1() {
        int score = 0;
        for (int i = 0; i < boxOwner.length; i++) {
            for (int j = 0; j < boxOwner[0].length; j++) {
                if (boxOwner[i][j] == 1) {
                    score++;
                } else if (boxOwner[i][j] == 2) {
                }
            }
        }
        return score;
    }

    public int calculateScorePlayer2() {
        int score = 0;
        for (int i = 0; i < boxOwner.length; i++) {
            for (int j = 0; j < boxOwner[0].length; j++) {
                if (boxOwner[i][j] == 1) {

                } else if (boxOwner[i][j] == 2) {
                    score++;
                }
            }
        }
        return score;
    }
}

