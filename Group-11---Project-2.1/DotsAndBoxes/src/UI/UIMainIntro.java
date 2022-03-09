package UI;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class UIMainIntro implements Runnable{

    public static final int SCREEN_WIDTH = 1170;
    public static final int SCREEN_HEIGHT = 720;

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new UIMainIntro());
    }

    public void initializeIntroScreen(JFrame frame) {
        //Add what we need to this menu here
        JPanel panel = getBackgroundPanel();
        JButton start = getStartButton(frame);
        frame.setContentPane(panel);
        frame.add(start);

        frame.setResizable(false);

        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }

    private JPanel getBackgroundPanel() {
        JPanel panel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {

                super.paintComponent(g);
                g.drawImage(new ImageIcon(UIMainIntro.class.getResource("../images/introBackground.jpg")).getImage(), 0, 0, SCREEN_WIDTH, SCREEN_HEIGHT, this);
            }
        };
        panel.setLayout(null);
        return panel;
    }

    private JButton getStartButton(JFrame frame) {
        ImageIcon startB = new ImageIcon(UIMainIntro.class.getResource("../images/start.png"));
        JButton start = new JButton(startB);
        start.setBounds(990, 550, 100, 100);
        start.setBorderPainted(false);
        start.setContentAreaFilled(false);
        start.setFocusPainted(false);
        start.setVisible(true);
        start.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                start.setIcon(startB);
                start.setCursor(new Cursor(Cursor.HAND_CURSOR));
            }

            @Override
            public void mouseExited(MouseEvent e) {
                start.setIcon(startB);
                start.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
            }

            @Override
            public void mousePressed(MouseEvent e) {
                start.setVisible(false);
                UISelectionMenu selectionMenu = new UISelectionMenu(frame);
                selectionMenu.initializeSelectionMenu();

            }
        });
        return start;
    }

    @Override
    public void run() {
        JFrame frame = new JFrame();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(SCREEN_WIDTH, SCREEN_HEIGHT);
        frame.setTitle("Dots-And-Boxes");
        frame.setResizable(true);
        initializeIntroScreen(frame);
    }

}
