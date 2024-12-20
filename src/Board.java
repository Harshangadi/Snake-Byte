
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import javax.swing.ImageIcon;
import javax.swing.JPanel;
import javax.swing.Timer;

public class Board extends JPanel implements ActionListener {

    private final int B_WIDTH = 300;
    private final int B_HEIGHT = 300;
    private final int DOT_SIZE = 10;
    private final int ALL_DOTS = 900;
    private final int RAND_POS = 29;
    private final int DELAY = 140;

    private final int x[] = new int[ALL_DOTS];
    private final int y[] = new int[ALL_DOTS];

    private int dots;
    private int apple_x;
    private int apple_y;

    private boolean leftDirection = false;
    private boolean rightDirection = true;
    private boolean upDirection = false;
    private boolean downDirection = false;
    private boolean inGame = true;
    private boolean paused = false; // Pause state

    private Timer timer;
    private Image ball;
    private Image apple;
    private Image head;

    public Board() {
        initBoard();
    }

    private void initBoard() {
        addKeyListener(new TAdapter());
        setBackground(Color.black);
        setFocusable(true);

        setPreferredSize(new Dimension(B_WIDTH, B_HEIGHT));
        loadImages();
        initGame();
    }

    private void loadImages() {
        ImageIcon iid = new ImageIcon("src/resources/dot.png");
        ball = iid.getImage();

        ImageIcon iia = new ImageIcon("src/resources/apple.png");
        apple = iia.getImage();

        ImageIcon iih = new ImageIcon("src/resources/head.png");
        head = iih.getImage();
    }

    private void initGame() {
        dots = 3;

        for (int z = 0; z < dots; z++) {
            x[z] = 50 - z * 10;
            y[z] = 50;
        }

        locateApple();

        timer = new Timer(DELAY, this);
        timer.start();
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);

        doDrawing(g);
    }

    private void doDrawing(Graphics g) {
        if (inGame) {
            if (!paused) {
                g.drawImage(apple, apple_x, apple_y, this);

                for (int z = 0; z < dots; z++) {
                    if (z == 0) {
                        g.drawImage(head, x[z], y[z], this);
                    } else {
                        g.drawImage(ball, x[z], y[z], this);
                    }
                }

                Toolkit.getDefaultToolkit().sync();
            } else {
                showPausedMessage(g);
            }
        } else {
            gameOver(g);
        }
    }

    private void showPausedMessage(Graphics g) {
        String pauseMsg = "Game Paused";
        String resumeMsg = "Press P to Resume";

        Font pauseFont = new Font("Helvetica", Font.BOLD, 20);
        Font resumeFont = new Font("Helvetica", Font.PLAIN, 14);
        FontMetrics metrPause = getFontMetrics(pauseFont);
        FontMetrics metrResume = getFontMetrics(resumeFont);

        g.setColor(Color.yellow);
        g.setFont(pauseFont);
        g.drawString(pauseMsg, (B_WIDTH - metrPause.stringWidth(pauseMsg)) / 2, B_HEIGHT / 2 - 10);

        g.setFont(resumeFont);
        g.drawString(resumeMsg, (B_WIDTH - metrResume.stringWidth(resumeMsg)) / 2, B_HEIGHT / 2 + 20);
    }

    private void gameOver(Graphics g) {
        String msg = "Game Over";
        String restartMsg = "Press R to Restart";
        String exitMsg = "Press E to Exit";

        Font largeFont = new Font("Helvetica", Font.BOLD, 20);
        FontMetrics metrLarge = getFontMetrics(largeFont);

        Font smallFont = new Font("Helvetica", Font.PLAIN, 14);
        FontMetrics metrSmall = getFontMetrics(smallFont);

        g.setColor(Color.white);
        g.setFont(largeFont);
        g.drawString(msg, (B_WIDTH - metrLarge.stringWidth(msg)) / 2, B_HEIGHT / 2 - 20);

        g.setColor(Color.MAGENTA.darker());
        g.setFont(smallFont);
        g.drawString(restartMsg, (B_WIDTH - metrSmall.stringWidth(restartMsg)) / 2, B_HEIGHT / 2 + 10);
        g.drawString(exitMsg, (B_WIDTH - metrSmall.stringWidth(exitMsg)) / 2, B_HEIGHT / 2 + 30);
    }

    private void checkApple() {
        if ((x[0] == apple_x) && (y[0] == apple_y)) {
            dots++;
            locateApple();
        }
    }

    private void move() {
        for (int z = dots; z > 0; z--) {
            x[z] = x[(z - 1)];
            y[z] = y[(z - 1)];
        }

        if (leftDirection) {
            x[0] -= DOT_SIZE;
        }

        if (rightDirection) {
            x[0] += DOT_SIZE;
        }

        if (upDirection) {
            y[0] -= DOT_SIZE;
        }

        if (downDirection) {
            y[0] += DOT_SIZE;
        }
    }

    private void checkCollision() {
        for (int z = dots; z > 0; z--) {
            if ((z > 4) && (x[0] == x[z]) && (y[0] == y[z])) {
                inGame = false;
            }
        }

        if (y[0] >= B_HEIGHT || y[0] < 0 || x[0] >= B_WIDTH || x[0] < 0) {
            inGame = false;
        }

        if (!inGame) {
            timer.stop();
        }
    }

    private void locateApple() {
        int r = (int) (Math.random() * RAND_POS);
        apple_x = ((r * DOT_SIZE));

        r = (int) (Math.random() * RAND_POS);
        apple_y = ((r * DOT_SIZE));
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (inGame && !paused) {
            checkApple();
            checkCollision();
            move();
        }

        repaint();
    }

    private class TAdapter extends KeyAdapter {

        @Override
        public void keyPressed(KeyEvent e) {
            int key = e.getKeyCode();

            if ((key == KeyEvent.VK_LEFT) && (!rightDirection)) {
                leftDirection = true;
                upDirection = false;
                downDirection = false;
            }

            if ((key == KeyEvent.VK_RIGHT) && (!leftDirection)) {
                rightDirection = true;
                upDirection = false;
                downDirection = false;
            }

            if ((key == KeyEvent.VK_UP) && (!downDirection)) {
                upDirection = true;
                rightDirection = false;
                leftDirection = false;
            }

            if ((key == KeyEvent.VK_DOWN) && (!upDirection)) {
                downDirection = true;
                rightDirection = false;
                leftDirection = false;
            }

            // Pause or resume the game if 'P' is pressed
            if (key == KeyEvent.VK_P) {
                paused = !paused;
            }

            // Restart the game if 'R' is pressed
            if (!inGame && key == KeyEvent.VK_R) {
                inGame = true;
                initGame();
                repaint();
            }

            // Exit the game if 'E' is pressed
            if (!inGame && key == KeyEvent.VK_E) {
                System.exit(0);
            }
        }
    }
}
