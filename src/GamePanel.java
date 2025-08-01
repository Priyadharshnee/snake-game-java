import java.awt.*;
import java.awt.event.*;
import java.util.Random;
import javax.swing.*;

public class GamePanel extends JPanel implements ActionListener {
    static final int SCREEN_WIDTH = 600;
    static final int SCREEN_HEIGHT = 600;
    static final int UNIT_SIZE = 25;
    static final int GAME_UNITS = (SCREEN_WIDTH * SCREEN_HEIGHT) / (UNIT_SIZE * UNIT_SIZE);
    static final int DELAY = 150;
    final int x[] = new int[GAME_UNITS];
    final int y[] = new int[GAME_UNITS];
    int bodyParts = 1;
    int applesEaten;
    int appleX;
    int appleY;
    char direction = 'R';
    boolean running = false;
    Timer timer;
    Random random;
    long startTime;
    int[][] obstacles = new int[4][2];

    GamePanel() {
        random = new Random();
        this.setPreferredSize(new Dimension(SCREEN_WIDTH, SCREEN_HEIGHT));
        this.setBackground(new Color(0xF5F5DC));
        this.setFocusable(true);
        this.addKeyListener(new MyKeyAdapter());
        startGame();
    }

    public void startGame() {
        newApple();
        newObstacles();
        running = true;
        timer = new Timer(DELAY, this);
        timer.start();
        startTime = System.currentTimeMillis();
    }

    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        draw(g);
    }

    public void draw(Graphics g) {
        if (running) {
            g.setColor(Color.red);
            g.fillOval(appleX, appleY, UNIT_SIZE, UNIT_SIZE);

            g.setColor(new Color(150, 75, 0));
            for (int[] obs : obstacles) {
                for (int i = 0; i < 3; i++) {
                    g.fillRect(obs[0] + i * UNIT_SIZE, obs[1], UNIT_SIZE, UNIT_SIZE);
                }
            }

            for (int i = 0; i < bodyParts; i++) {
                g.setColor(Color.green);
                g.fillOval(x[i], y[i], UNIT_SIZE, UNIT_SIZE);
            }

            g.setColor(Color.black);
            g.setFont(new Font("Ink Free", Font.BOLD, 20));
            FontMetrics metrics = getFontMetrics(g.getFont());
            g.drawString("Score: " + applesEaten, 10, g.getFont().getSize());
            long elapsedTime = (System.currentTimeMillis() - startTime) / 1000;
            g.drawString("Time: " + elapsedTime + "s", SCREEN_WIDTH - 100, g.getFont().getSize());

        } else {
            gameOver(g);
        }
    }

    public void newApple() {
        appleX = random.nextInt((int)(SCREEN_WIDTH / UNIT_SIZE)) * UNIT_SIZE;
        appleY = random.nextInt((int)(SCREEN_HEIGHT / UNIT_SIZE)) * UNIT_SIZE;
    }

    public void newObstacles() {
        for (int i = 0; i < obstacles.length; i++) {
            obstacles[i][0] = random.nextInt((int)(SCREEN_WIDTH / UNIT_SIZE - 3)) * UNIT_SIZE;
            obstacles[i][1] = random.nextInt((int)(SCREEN_HEIGHT / UNIT_SIZE)) * UNIT_SIZE;
        }
    }

    public void move() {
        for (int i = bodyParts; i > 0; i--) {
            x[i] = x[i - 1];
            y[i] = y[i - 1];
        }

        switch (direction) {
            case 'U':
                y[0] = y[0] - UNIT_SIZE;
                break;
            case 'D':
                y[0] = y[0] + UNIT_SIZE;
                break;
            case 'L':
                x[0] = x[0] - UNIT_SIZE;
                break;
            case 'R':
                x[0] = x[0] + UNIT_SIZE;
                break;
        }
    }

    public void checkApple() {
        if ((x[0] == appleX) && (y[0] == appleY)) {
            bodyParts++;
            applesEaten++;
            newApple();
        }
    }

    public void checkCollisions() {
        for (int i = bodyParts; i > 0; i--) {
            if ((x[0] == x[i]) && (y[0] == y[i])) {
                running = false;
                break;
            }
        }

        for (int[] obs : obstacles) {
            for (int i = 0; i < 3; i++) {
                int obsX = obs[0] + i * UNIT_SIZE;
                if (x[0] == obsX && y[0] == obs[1]) {
                    running = false;
                    break;
                }
            }
        }

        if (x[0] < 0 || x[0] >= SCREEN_WIDTH || y[0] < 0 || y[0] >= SCREEN_HEIGHT) {
            running = false;
        }

        if (!running) {
            timer.stop();
        }
    }

    public void gameOver(Graphics g) {
        g.setColor(Color.red);
        g.setFont(new Font("Ink Free", Font.BOLD, 75));
        FontMetrics metrics1 = getFontMetrics(g.getFont());
        g.drawString("Game Over", (SCREEN_WIDTH - metrics1.stringWidth("Game Over")) / 2, SCREEN_HEIGHT / 2);

        g.setFont(new Font("Ink Free", Font.BOLD, 30));
        FontMetrics metrics2 = getFontMetrics(g.getFont());
        g.drawString("Score: " + applesEaten, (SCREEN_WIDTH - metrics2.stringWidth("Score: " + applesEaten)) / 2, SCREEN_HEIGHT / 2 + 50);

        long elapsedTime = (System.currentTimeMillis() - startTime) / 1000;
        g.drawString("Time: " + elapsedTime + "s", (SCREEN_WIDTH - metrics2.stringWidth("Time: " + elapsedTime + "s")) / 2, SCREEN_HEIGHT / 2 + 100);

        g.setFont(new Font("Ink Free", Font.BOLD, 30));
        g.drawString("Press Enter to Retry", (SCREEN_WIDTH - metrics2.stringWidth("Press Enter to Retry")) / 2, SCREEN_HEIGHT / 2 + 150);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (running) {
            move();
            checkApple();
            checkCollisions();
        }
        repaint();
    }

    public class MyKeyAdapter extends KeyAdapter {
        public void keyPressed(KeyEvent e) {
            switch (e.getKeyCode()) {
                case KeyEvent.VK_LEFT:
                    if (direction != 'R') direction = 'L';
                    break;
                case KeyEvent.VK_RIGHT:
                    if (direction != 'L') direction = 'R';
                    break;
                case KeyEvent.VK_UP:
                    if (direction != 'D') direction = 'U';
                    break;
                case KeyEvent.VK_DOWN:
                    if (direction != 'U') direction = 'D';
                    break;
                case KeyEvent.VK_ENTER:
                    if (!running) {
                        bodyParts = 1;
                        applesEaten = 0;
                        direction = 'R';
                        for (int i = 0; i < x.length; i++) {
                            x[i] = 0;
                            y[i] = 0;
                        }
                        startGame();
                    }
                    break;
            }
        }
    }
}