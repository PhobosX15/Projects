package UI;

import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

public class MouseListenerHuman implements MouseListener {
    public GameBoard gameBoard;

    public MouseListenerHuman(GameBoard gameBoard){
        this.gameBoard = gameBoard;
    }

    @Override
    public void mouseClicked(MouseEvent mouseEvent) {
        if (!gameBoard.mouseEnabled) return;
        gameBoard.boardstate.move = gameBoard.getSourceEdge(mouseEvent.getSource());
        if(!gameBoard.boardstate.processMove(gameBoard.boardstate.move)&&!gameBoard.boardstate.currentPlayer.title.equals("Human")){
            gameBoard.mouseEnabled = false;
        }
        gameBoard.updateLabels();

    }

    @Override
    public void mousePressed(MouseEvent mouseEvent) {

    }

    @Override
    public void mouseReleased(MouseEvent mouseEvent) {

    }

    @Override
    public void mouseEntered(MouseEvent mouseEvent) {
        if (!gameBoard.mouseEnabled) return;
        Edge location = gameBoard.getSourceEdge(mouseEvent.getSource());
        int x = location.getX(), y = location.getY();
        if (location.isHorizontal()) {

            if (gameBoard.boardstate.isSetHEdge[x][y]) return;
            gameBoard.hEdge[x][y].setCursor(new Cursor(Cursor.HAND_CURSOR));
            gameBoard.hEdge[x][y].setBackground(gameBoard.boardstate.currentPlayer.color);
        } else {
            if (gameBoard.boardstate.isSetVEdge[x][y]) return;
            gameBoard.vEdge[x][y].setCursor(new Cursor(Cursor.HAND_CURSOR));
            gameBoard.vEdge[x][y].setBackground(gameBoard.boardstate.currentPlayer.color);
        }
    }

    @Override
    public void mouseExited(MouseEvent mouseEvent) {
        if (!gameBoard.mouseEnabled) return;
        Edge location = gameBoard.getSourceEdge(mouseEvent.getSource());
        int x = location.getX(), y = location.getY();
        if (location.isHorizontal()) {
            if (gameBoard.boardstate.isSetHEdge[x][y]) return;
            gameBoard.hEdge[x][y].setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
            gameBoard.hEdge[x][y].setBackground(Color.WHITE);
        } else {
            if (gameBoard.boardstate.isSetVEdge[x][y]) return;
            gameBoard.vEdge[x][y].setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
            gameBoard.vEdge[x][y].setBackground(Color.WHITE);
        }
    }
}
