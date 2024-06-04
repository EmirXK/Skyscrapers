import javax.swing.*;
import javax.swing.border.LineBorder;
import java.awt.*;

public class Skyscrapers {

    private static final int SIZE = 4;

    private static final JButton[] outerButtons = new JButton[SIZE * SIZE];
    private static final JButton[][] innerButtons = new JButton[SIZE][SIZE];
    private static final JLabel statusLabel = new JLabel("Status: Unsolved");
    private static final JButton checkSolution = new JButton("Check Solution");
    private static final JButton resetButton = new JButton("Reset Table");

    private static int[] current_clues;

    public static void main(String[] args) {

        int[][] my_board = new int[SIZE][SIZE];
        int[] clues = generateClues();

        while (!solve(clues,my_board)) {
            clues = generateClues();
        }
        current_clues = clues;
        // if we have reached this point, the solution is possible.

        // Create a new JFrame
        JFrame frame = new JFrame("Skyscrapers");

        // Set the default close operation
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // Create a main panel with a BorderLayout
        JPanel mainPanel = new JPanel(new BorderLayout());

        // Create a JPanel with a GridLayout for the button grid
        JPanel gridPanel = new JPanel(new GridLayout(6, 6));


        int ind = 0;

        // Create and add buttons to the grid panel
        for (int row = 0; row < 6; row++) {
            for (int col = 0; col < 6; col++) {
                JButton button = new JButton();
                button.setBackground(Color.WHITE);
                button.setOpaque(true);
                button.setBorder(new LineBorder(Color.BLACK)); // Add borders to separate buttons
                button.setFont(button.getFont().deriveFont(Font.BOLD, 30)); // Use bold font for text, font size 20

                // Make the outer shell unclickable
                if (row == 0 || row == 5 || col == 0 || col == 5) {
                    if (!((row == 0 && col == 0) || (row == 0 && col == 5) || (row == 5 && col == 5) || (row == 5 && col == 0))) {
                        outerButtons[ind++] = button;
                        button.setBackground(Color.lightGray);
                        button.setContentAreaFilled(false); // Make the content area transparent
                        button.setOpaque(true); // Ensure that the button is opaque
                        button.setForeground(Color.BLACK); // Set the foreground color for the text
                    }
                    button.setBackground(Color.lightGray);
                    //button.setEnabled(false);
                } else {
                    button.setText("1");
                    // Add action listener to increment button value
                    button.addActionListener(e -> {
                        String text = button.getText();
                        int value = text.isEmpty() ? 0 : Integer.parseInt(text);
                        value = (value % 4) + 1; // Increment value and wrap around after 4
                        button.setText(String.valueOf(value));
                        // Set different colors for the numbers inside the inner 4x4 matrix
                        colorButton(button);
                    });

                    innerButtons[row - 1][col - 1] = button;
                }

                gridPanel.add(button);
            }
        }

        int index = 4;
        int left = 15;
        int right = 4;
        for (int i = 0; i < 4; i++) {
            outerButtons[i].setText(Integer.toString(clues[i]));
            outerButtons[index++].setText(Integer.toString(clues[left--]));
            outerButtons[index++].setText(Integer.toString(clues[right++]));
        }
        int k = 1;
        for (int i = 12; i < 16; i++) {
            outerButtons[i].setText(Integer.toString(clues[i - k]));
            k += 2;
        }
        for (JButton btn : outerButtons) {
            if (btn.getText().equals("0")) {
                btn.setText("");
            }
        }

        // Create a JPanel for the top bar
        JPanel topBar = new JPanel(new FlowLayout(FlowLayout.CENTER));
        statusLabel.setVerticalAlignment(JLabel.CENTER);
        topBar.add(statusLabel);

        // Create a "Reset Table" button
        resetButton.setBackground(new Color(240, 240, 240)); // Set background color
        resetButton.setBorder(new LineBorder(Color.BLACK, 2)); // Adjust the border
        resetButton.setMargin(new Insets(10, 20, 10, 20)); // Add padding
        resetButton.addActionListener(e -> resetTable());

        // new puzzle button
        JButton newPuzzle = new JButton("New Puzzle");
        newPuzzle.setBackground(new Color(240, 240, 240)); // Set background color
        newPuzzle.setBorder(new LineBorder(Color.BLACK, 2)); // Adjust the border
        newPuzzle.setMargin(new Insets(10, 20, 10, 20)); // Add padding
        newPuzzle.addActionListener(e -> {
            resetTable();
            // randomize the clues
            int[] new_clues = generateClues();
            while (!solve(new_clues,my_board)) {
                new_clues = generateClues();
            }
            // new clues generated.
            updateClues(new_clues);
            checkSolution.setEnabled(true);
            resetButton.setEnabled(true);
        });

// Create a JPanel for the bottom bar
        JPanel bottomBar = new JPanel(new FlowLayout(FlowLayout.CENTER));

// Create the "Check Solution" button
        checkSolution.setBackground(new Color(240, 240, 240));
        checkSolution.setBorder(new LineBorder(Color.BLACK, 2)); // Adjust the border
        checkSolution.setMargin(new Insets(10, 20, 10, 20)); // Add padding
        int[] finalClues = clues;
        checkSolution.addActionListener(e -> {
            int[][] board = new int[SIZE][SIZE];
            for (int i=0; i<SIZE; i++) {
                for (int j=0; j<SIZE; j++) {
                    board[i][j] = Integer.parseInt(innerButtons[i][j].getText());
                }
            }
            if (isSolved(finalClues,board)) {
                statusLabel.setText("Status: Solved");
            }
        });

        JButton giveUp = new JButton("Give Up");
        giveUp.setBackground(new Color(240, 240, 240));
        giveUp.setBorder(new LineBorder(Color.BLACK, 2)); // Adjust the border
        giveUp.setMargin(new Insets(10, 20, 10, 20)); // Add padding
        giveUp.addActionListener(e -> giveUp());

        // Add space between buttons
        bottomBar.add(checkSolution);
        bottomBar.add(Box.createHorizontalStrut(80)); // Add space between buttons
        bottomBar.add(resetButton);
        bottomBar.add(Box.createHorizontalStrut(80)); // Add space between buttons
        bottomBar.add(newPuzzle);
        bottomBar.add(Box.createHorizontalStrut(80)); // Add space between buttons
        bottomBar.add(giveUp);

        // Add the components to the main panel
        mainPanel.add(topBar, BorderLayout.NORTH);
        mainPanel.add(gridPanel, BorderLayout.CENTER);
        mainPanel.add(bottomBar, BorderLayout.SOUTH);

        // Add the main panel to the frame
        frame.add(mainPanel);

        // Set the frame size
        frame.setSize(600, 650);
        frame.setResizable(false);

        // Make the frame visible
        frame.setVisible(true);
    }

    // random clues generation
    private static int[] generateClues() {
        int[] randomClues = new int[SIZE*SIZE];
        // a random number of indeces will be changed
        int n = (int)(Math.random()*(SIZE*SIZE-8)+8);
        int total = 0;
        int randIndex = (int)(Math.random()*SIZE*SIZE);
        while (total < n) {
            while (randomClues[randIndex] != 0) {
                randIndex = (int)(Math.random()*SIZE*SIZE);
            }
            randomClues[randIndex] = (int)(Math.random()*4)+1;
            total++;
        }
        current_clues = randomClues;
        return randomClues;
    }

    // game logic

    static boolean isValid(int r, int c, int val, int[][] board) {
        // Check if the value is already present in the row or column
        for (int i = 0; i < SIZE; i++) {
            if (board[r][i] == val) return false;
            if (board[i][c] == val) return false;
        }

        return true; // Valid value
    }

    static boolean isSolved(int[] clues, int[][] board) {
        int ind = 0;
        for (int i=0; i<SIZE; i++) {
            for (int j=0; j<SIZE; j++) {
                if (clues[ind] != 0) {
                    int max = 0;
                    int counter = 0;
                    if (ind < SIZE) {
                        for (int k=0; k<SIZE; k++) {
                            if (board[k][ind] > max) {
                                max = board[k][ind];
                                counter++;
                            }
                        }
                    }
                    else if (ind < SIZE*2) {
                        for (int k=SIZE-1; k>=0; k--) {
                            if (board[ind-SIZE][k] > max) {
                                max = board[ind-SIZE][k];
                                counter++;
                            }
                        }
                    }
                    else if (ind < SIZE*3) {
                        for (int k=SIZE-1; k>=0; k--) {
                            if (board[k][SIZE*3-1-ind] > max) {
                                max = board[k][SIZE*3-1-ind];
                                counter++;
                            }
                        }
                    }
                    else {
                        for (int k=0; k<SIZE; k++) {
                            if (board[SIZE*4-1-ind][k] > max) {
                                max = board[SIZE*4-1-ind][k];
                                counter++;
                            }
                        }
                    }
                    if (counter != clues[ind]) return false;
                }
                ind++;
            }
        }
        return true;
    }

    static boolean solve(int[] clues, int[][] board) {
        try {
            for (int i = 0; i < SIZE; i++) {
                for (int j = 0; j < SIZE; j++) {
                    if (board[i][j] == 0) {
                        for (int k = 1; k < SIZE + 1; k++) {
                            if (isValid(i, j, k, board)) {
                                board[i][j] = k;
                                if (solve(clues, board)) return true;
                                // if not, backtrack
                                board[i][j] = 0;
                            }
                        }
                        return false;
                    }
                }
            }
            return isSolved(clues, board);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    private static void updateClues(int[] clues) {
        int index = 4;
        int left = 15;
        int right = 4;
        for (int i = 0; i < 4; i++) {
            outerButtons[i].setText(Integer.toString(clues[i]));
            outerButtons[index++].setText(Integer.toString(clues[left--]));
            outerButtons[index++].setText(Integer.toString(clues[right++]));
        }
        int k = 1;
        for (int i = 12; i < 16; i++) {
            outerButtons[i].setText(Integer.toString(clues[i - k]));
            k += 2;
        }
        for (JButton btn : outerButtons) {
            if (btn.getText().equals("0")) {
                btn.setText("");
            }
        }
    }

    private static void resetTable() {
        // Reset the text of all inner buttons to "0"
        for (JButton[] row : innerButtons) {
            for (JButton button : row) {
                button.setText("1");
                button.setForeground(new Color(0x0));
            }
        }
        statusLabel.setText("Status: Unsolved");
    }

    private static void giveUp() {
        resetTable();
        int[][] matrix = new int[SIZE][SIZE];
        solve(current_clues, matrix);
        // matrix now holds the solution
        // all we need to do is display it
        for (int i=0; i<SIZE; i++) {
            for (int j=0; j<SIZE; j++) {
                innerButtons[i][j].setText(Integer.toString(matrix[i][j]));
                colorButton(innerButtons[i][j]);
            }
        }
        statusLabel.setText("Status: Gave up");
        checkSolution.setEnabled(false);
        resetButton.setEnabled(false);
    }

    private static void colorButton(JButton button) {
        switch (Integer.parseInt(button.getText())) {
            case 1 -> button.setForeground(new Color(0x0));
            case 2 -> button.setForeground(new Color(0x7E0000));
            case 3 -> button.setForeground(new Color(0x236C00));
            case 4 -> button.setForeground(new Color(0x0025A9));
        }
    }
}
