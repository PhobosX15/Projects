package UI;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class StartGameMouseAdapter extends MouseAdapter {
    JButton startGameBoard;
    ImageIcon startButton = new ImageIcon(UIMainIntro.class.getResource("../images/start.png"));
    UISelectionMenu menu;
    public StartGameMouseAdapter(JButton x, UISelectionMenu menu) {
        startGameBoard=x;
        this.menu=menu;
    }

    @Override
    public void mouseEntered(MouseEvent e) {
        startGameBoard.setIcon(startButton);
        startGameBoard.setCursor(new Cursor(Cursor.HAND_CURSOR));
    }

    @Override
    public void mouseExited(MouseEvent e) {
        startGameBoard.setIcon(startButton);
        startGameBoard.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
    }

    @Override
    public void mousePressed(MouseEvent e) {
        if (menu.length < 1 || menu.player1 == null || menu.player2 == null) {
            //todo when selection is not made
        } else {
            try {
                menu.comboBoxIndex = menu.gridSizeComboBox.getSelectedIndex();
                menu.gameBoard = new GameBoard(menu.comboBoxIndex, menu.frame, menu.player1, menu.player2);
                System.out.println(menu.player1.title);
                System.out.println(menu.player2.title);
            } catch (NumberFormatException hsdkf) {
            }
        }
    }
}
