package tankrotationexample.menus;

import tankrotationexample.Launcher;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;

public class EndGamePanel extends JPanel {

    private BufferedImage menuBackground;
    private final Launcher lf;
    private String winnerText = "";  // Field to store the winner text


    public EndGamePanel(Launcher lf) {
        this.lf = lf;
        try {
            menuBackground = ImageIO.read(this.getClass().getClassLoader().getResource("title/title.png"));
        } catch (IOException e) {
            System.out.println("Error cant read menu background");
            e.printStackTrace();
            System.exit(-3);
        }
        this.setBackground(Color.BLACK);
        this.setLayout(null);

        JButton start = new JButton("Restart Game");
        start.setFont(new Font("Courier New", Font.BOLD, 24));
        start.setBounds(150, 400, 250, 50);
        start.addActionListener((actionEvent ->
        {
            lf.getGameWorld().resetGame();
            this.lf.setFrame("game");
        }));

        JButton exit = new JButton("Exit");
        exit.setFont(new Font("Courier New", Font.BOLD, 24));
        exit.setBounds(150, 500, 250, 50);
        exit.addActionListener((actionEvent -> this.lf.closeGame()));

        this.add(start);
        this.add(exit);
    }

    public void setWinnerText(String winnerText) {
        this.winnerText = winnerText;
        repaint();  // Request a repaint to show the updated winner text
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);  // Ensure the background is drawn
        Graphics2D g2 = (Graphics2D) g;
        g2.drawImage(this.menuBackground, 0, 0, null);

        // Draw the winner text
        g2.setFont(new Font("Courier New", Font.BOLD, 36));
        g2.setColor(Color.WHITE);
        g2.drawString(this.winnerText, 150, 350);  // Adjust coordinates as needed
    }

}
