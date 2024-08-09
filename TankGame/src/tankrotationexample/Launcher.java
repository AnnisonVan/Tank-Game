package tankrotationexample;

import tankrotationexample.game.GameWorld;
import tankrotationexample.game.ResourceManager;
import tankrotationexample.game.Sound;
import tankrotationexample.menus.EndGamePanel;
import tankrotationexample.menus.StartMenuPanel;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowEvent;
import java.io.IOException;

public class Launcher {

    private JPanel mainPanel;
    private GameWorld gamePanel;
    private EndGamePanel endPanel;  // Define the endPanel field
    private final JFrame jf;
    private CardLayout cl;

    public Launcher() {
        this.jf = new JFrame();
        this.jf.setTitle("Tank Wars Game");
        this.jf.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }

    private void initUIComponents() {
        this.mainPanel = new JPanel();
        JPanel startPanel = new StartMenuPanel(this);
        this.gamePanel = new GameWorld(this);
        this.gamePanel.InitializeGame();

        this.endPanel = new EndGamePanel(this);  // Initialize the endPanel with a reference to Launcher

        cl = new CardLayout();
        this.mainPanel.setLayout(cl);
        this.mainPanel.add(startPanel, "start");
        this.mainPanel.add(gamePanel, "game");
        this.mainPanel.add(endPanel, "end");
        this.jf.add(mainPanel);
        this.jf.setResizable(false);
        this.setFrame("start");
    }

    public void setFrame(String type) {
        this.jf.setVisible(false);
        switch (type) {
            case "start" -> this.jf.setSize(GameConstants.START_MENU_SCREEN_WIDTH, GameConstants.START_MENU_SCREEN_HEIGHT);
            case "game" -> {
                this.jf.setSize(GameConstants.GAME_SCREEN_WIDTH + 15, GameConstants.GAME_SCREEN_HEIGHT);
                (new Thread(this.gamePanel)).start();
            }
            case "end" -> this.jf.setSize(GameConstants.END_MENU_SCREEN_WIDTH, GameConstants.END_MENU_SCREEN_HEIGHT);
        }
        this.cl.show(mainPanel, type);
        this.jf.setVisible(true);
    }

    public EndGamePanel getEndGamePanel() {
        return endPanel;  // Provide access to the EndGamePanel for setting winner text
    }

    public GameWorld getGameWorld() {
        return gamePanel;  // Provide access to the GameWorld for resetting the game
    }

    public JFrame getJf() {
        return jf;
    }

    public void closeGame() {
        this.jf.dispatchEvent(new WindowEvent(this.jf, WindowEvent.WINDOW_CLOSING));
    }

    public static void main(String[] args) {
        try {
            ResourceManager.init(); // Initialize resources
        } catch (IOException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Failed to load resources. Exiting game.");
            System.exit(1);
        }
        (new Launcher()).initUIComponents();
        Sound bg = ResourceManager.getSound("bg");
        bg.loopContinuously();
        bg.play();
    }
}
