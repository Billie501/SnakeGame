
/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

/**
 *
 * @author billi
 */

import java.awt.*;
import java.awt.event.*;
import java.util.LinkedList;
import javax.swing.*;
import java.util.Random;

public class GamePanel extends JPanel implements ActionListener {

    static final int SCREEN_WIDTH = 700;
    static final int SCREEN_HEIGHT = 700;
    static final int UNIT_SIZE = 25;
    static final int GAME_UNITS = (SCREEN_WIDTH * SCREEN_HEIGHT) / UNIT_SIZE;
    static final int DELAY = 350; // the higher the number, the slower the game
    static int x[] = new int[GAME_UNITS];
    static int y[] = new int[GAME_UNITS];
    int bodyParts = 6;
    int lettersEaten;
    int letterX;
    int letterY;
    char currentLetter;
    int numbersEaten;
    int[][] numberPositions = new int[10][2];
    int[] currentNumbers = new int[10];
    char direction = 'R';
    boolean running = false;
    Timer timer;
    Random random;
    LinkedList<Character> eatenLetters;

    GamePanel() {
        random = new Random();
        this.setPreferredSize(new Dimension(SCREEN_WIDTH, SCREEN_HEIGHT));
        this.setBackground(Color.white);
        this.setFocusable(true);
        this.addKeyListener(new MyKeyAdapter());
        eatenLetters = new LinkedList<>();
        startGame();
    }

    public void startGame() {
        newLetter();
        newNumbers();
        running = true;
        timer = new Timer(DELAY, this);
        timer.start();
    }

    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        draw(g);
    }

    public void draw(Graphics g) {
        
        if(running) {
            // Draw the oval for letter
            g.setColor(Color.blue);
            g.fillOval(letterX, letterY, UNIT_SIZE, UNIT_SIZE);

            // Set the color and font for the letter
            g.setColor(Color.white); // Assuming white looks good on blue
            g.setFont(new Font("Arial", Font.BOLD, UNIT_SIZE / 2));

            // Draw the letter in the center of the oval
            g.drawString(String.valueOf(currentLetter), letterX + UNIT_SIZE / 4, letterY + (3 * UNIT_SIZE / 4));

            // Draw the ovals for numbers
            for (int i = 0; i < 10; i++) {
                g.setColor(Color.orange);
                g.fillOval(numberPositions[i][0], numberPositions[i][1], UNIT_SIZE, UNIT_SIZE);

                // Set the color and font for the numbers
                g.setColor(Color.white); // Assuming white looks good on orange
                g.setFont(new Font("Arial", Font.BOLD, UNIT_SIZE / 2));

                // Draw the numbers in the center of the ovals
                g.drawString(String.valueOf(currentNumbers[i]), numberPositions[i][0] + UNIT_SIZE / 4, numberPositions[i][1] + (3 * UNIT_SIZE / 4));
            }

            // Drawing the bodyParts
            for (int i = 0; i < bodyParts; i++) {
                if (i == 0) {
                    g.setColor(Color.green);
                    g.fillRect(x[i], y[i], UNIT_SIZE, UNIT_SIZE);
                } else {
                    g.setColor(new Color(45, 180, 0));
                    g.fillRect(x[i], y[i], UNIT_SIZE, UNIT_SIZE);
                    if (i - 1 < eatenLetters.size()) {
                        g.setColor(Color.white);
                        g.setFont(new Font("Arial", Font.BOLD, UNIT_SIZE / 2));
                        g.drawString(String.valueOf(eatenLetters.get(i - 1)), x[i] + UNIT_SIZE / 4, y[i] + (3 * UNIT_SIZE / 4));
                    }
                }
            }
        }
        else {
            gameOver(g);
        }
    }

    public void newLetter() {
        letterX = random.nextInt(SCREEN_WIDTH / UNIT_SIZE) * UNIT_SIZE;
        letterY = random.nextInt(SCREEN_HEIGHT / UNIT_SIZE) * UNIT_SIZE;
        currentLetter = (char) ('A' + random.nextInt(26)); // Generate a random uppercase letter
    }

    public void newNumbers() {
        for (int i = 0; i < 10; i++) {
            numberPositions[i][0] = random.nextInt(SCREEN_WIDTH / UNIT_SIZE) * UNIT_SIZE;
            numberPositions[i][1] = random.nextInt(SCREEN_HEIGHT / UNIT_SIZE) * UNIT_SIZE;
            currentNumbers[i] = random.nextInt(10); // Generate a random number from 0 to 9
        }
    }

    public void generateNewNumber() {
        // Generate a new number to replace one of the existing numbers
        int indexToReplace = random.nextInt(10);
        numberPositions[indexToReplace][0] = random.nextInt(SCREEN_WIDTH / UNIT_SIZE) * UNIT_SIZE;
        numberPositions[indexToReplace][1] = random.nextInt(SCREEN_HEIGHT / UNIT_SIZE) * UNIT_SIZE;
        currentNumbers[indexToReplace] = random.nextInt(10); // Generate a random number from 0 to 9
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

    public void checkLetter() {
        if ((x[0] == letterX) && (y[0] == letterY)) {
            bodyParts++;
            lettersEaten++;
            storeEatenLetter(currentLetter);
            newLetter();
        }
    }

    public void checkNumbers() {
        for (int i = 0; i < 10; i++) {
            if ((x[0] == numberPositions[i][0]) && (y[0] == numberPositions[i][1])) {
                int indexToRemove = currentNumbers[i];
                if (indexToRemove >= eatenLetters.size()) {
                    eatenLetters.removeLast();
                } else {
                    eatenLetters.remove(indexToRemove);
                }
                bodyParts--; // Decrease bodyParts when a number is eaten
                generateNewNumber(); // Generate a new number to replace the eaten one
                break;
            }
        }
    }

    public void storeEatenLetter(char letter) {
        if (eatenLetters.isEmpty()) {
            eatenLetters.add(letter);
        } else {
            int index = 0;
            for (char c : eatenLetters) {
                if (letter < c) {
                    break;
                }
                index++;
            }
            eatenLetters.add(index, letter);
        }
    }

    public void checkCollisions() {
        // Checks if head collides with body
        for (int i = bodyParts; i > 0; i--) {
            if ((x[0] == x[i]) && (y[0] == y[i])) {
                running = false;
            }
        }
        // Check if head touches left border
        if (x[0] < 0) {
            running = false;
        }

        // Check if head touches right border
        if (x[0] >= SCREEN_WIDTH) {
            running = false;
        }

        // Check if head touches top border
        if (y[0] < 0) {
            running = false;
        }
        // Check if head touches bottom border
        if (y[0] >= SCREEN_HEIGHT) {
            running = false;
        }
        if (!running) {
            timer.stop();
        }
    }

    public void gameOver(Graphics g) {
        // game over text
        g.setColor(Color.black);
        g.setFont(new Font("Arial", Font.BOLD, 75));
        FontMetrics metrics = getFontMetrics(g.getFont());
        g.drawString("Game Over", (SCREEN_WIDTH - metrics.stringWidth("Game Over"))/2, SCREEN_HEIGHT/2);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (running) {
            move();
            checkLetter();
            checkNumbers();
            checkCollisions();
        }
        repaint();
    }

    public class MyKeyAdapter extends KeyAdapter {
        @Override
        public void keyPressed(KeyEvent e) {
            switch (e.getKeyCode()) {
                case KeyEvent.VK_LEFT:
                    if (direction != 'R') {
                        direction = 'L';
                    }
                    break;
                case KeyEvent.VK_RIGHT:
                    if (direction != 'L') {
                        direction = 'R';
                    }
                    break;
                case KeyEvent.VK_UP:
                    if (direction != 'D') {
                        direction = 'U';
                    }
                    break;
                case KeyEvent.VK_DOWN:
                    if (direction != 'U') {
                        direction = 'D';
                    }
                    break;
            }
        }
    }
}
