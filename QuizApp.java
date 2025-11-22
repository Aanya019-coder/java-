import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

class Question implements Serializable {
    private String question;
    private String[] options;
    private int correctAnswer;
    private int timeLimit;
    
    public Question(String question, String[] options, int correctAnswer, int timeLimit) {
        this.question = question;
        this.options = options;
        this.correctAnswer = correctAnswer;
        this.timeLimit = timeLimit;
    }
    
    public String getQuestion() { return question; }
    public String[] getOptions() { return options; }
    public int getCorrectAnswer() { return correctAnswer; }
    public int getTimeLimit() { return timeLimit; }
}

class QuizScore implements Serializable {
    private String playerName;
    private int score;
    private int totalQuestions;
    private String date;
    private double accuracy;
    
    public QuizScore(String playerName, int score, int totalQuestions, String date, double accuracy) {
        this.playerName = playerName;
        this.score = score;
        this.totalQuestions = totalQuestions;
        this.date = date;
        this.accuracy = accuracy;
    }
    
    public String getPlayerName() { return playerName; }
    public int getScore() { return score; }
    public int getTotalQuestions() { return totalQuestions; }
    public String getDate() { return date; }
    public double getAccuracy() { return accuracy; }
}

class QuizManager {
    private List<Question> questions;
    private List<QuizScore> leaderboard;
    private static final String SCORES_FILE = "quiz_scores.dat";
    
    public QuizManager() {
        questions = new ArrayList<>();
        leaderboard = new ArrayList<>();
        initializeQuestions();
        loadScores();
    }
    
    private void initializeQuestions() {
        questions.add(new Question(
            "What is the size of int in Java?",
            new String[]{"16 bits", "32 bits", "64 bits", "8 bits"},
            1, 15
        ));
        
        questions.add(new Question(
            "Which keyword is used for inheritance in Java?",
            new String[]{"implements", "extends", "inherits", "super"},
            1, 15
        ));
        
        questions.add(new Question(
            "What is the default value of boolean in Java?",
            new String[]{"true", "false", "null", "0"},
            1, 12
        ));
        
        questions.add(new Question(
            "Which collection does not allow duplicates?",
            new String[]{"ArrayList", "LinkedList", "HashSet", "HashMap"},
            2, 15
        ));
        
        questions.add(new Question(
            "What is the parent class of all classes in Java?",
            new String[]{"System", "Object", "Class", "Main"},
            1, 12
        ));
        
        questions.add(new Question(
            "Which operator is used to compare two values?",
            new String[]{"=", "==", "equals", "compare"},
            1, 10
        ));
        
        questions.add(new Question(
            "What does JVM stand for?",
            new String[]{"Java Virtual Machine", "Java Visual Method", "Java Variable Memory", "Java Version Manager"},
            0, 15
        ));
        
        questions.add(new Question(
            "Which loop is guaranteed to execute at least once?",
            new String[]{"for", "while", "do-while", "foreach"},
            2, 12
        ));
        
        questions.add(new Question(
            "What is the output of 5 / 2 in Java?",
            new String[]{"2.5", "2", "3", "2.0"},
            1, 15
        ));
        
        questions.add(new Question(
            "Which access modifier is most restrictive?",
            new String[]{"public", "protected", "private", "default"},
            2, 12
        ));
    }
    
    public List<Question> getShuffledQuestions() {
        List<Question> shuffled = new ArrayList<>(questions);
        Collections.shuffle(shuffled);
        return shuffled;
    }
    
    public void saveScore(QuizScore score) {
        leaderboard.add(score);
        Collections.sort(leaderboard, (s1, s2) -> Integer.compare(s2.getScore(), s1.getScore()));
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(SCORES_FILE))) {
            oos.writeObject(leaderboard);
        } catch (IOException e) {
            System.err.println("Error saving scores: " + e.getMessage());
        }
    }
    
    @SuppressWarnings("unchecked")
    private void loadScores() {
        File file = new File(SCORES_FILE);
        if (file.exists()) {
            try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(SCORES_FILE))) {
                leaderboard = (ArrayList<QuizScore>) ois.readObject();
                if (leaderboard == null) leaderboard = new ArrayList<>();
            } catch (IOException | ClassNotFoundException e) {
                System.err.println("Error loading scores: " + e.getMessage());
            }
        }
    }
    
    public List<QuizScore> getLeaderboard() {
        return leaderboard;
    }
}

class QuizPanel extends JPanel {
    private JLabel questionLabel;
    private JLabel timerLabel;
    private JLabel scoreLabel;
    private ButtonGroup optionsGroup;
    private JRadioButton[] optionButtons;
    private JButton submitButton;
    private JButton nextButton;
    private javax.swing.Timer timer;
    private int timeRemaining;
    private int currentQuestionIndex;
    private int score;
    private int correctAnswers;
    private int wrongAnswers;
    private List<Question> questions;
    private QuizManager quizManager;
    private String playerName;
    
    public QuizPanel(QuizManager quizManager, String playerName) {
        this.quizManager = quizManager;
        this.playerName = playerName;
        this.questions = quizManager.getShuffledQuestions();
        this.currentQuestionIndex = 0;
        this.score = 0;
        this.correctAnswers = 0;
        this.wrongAnswers = 0;
        
        setLayout(new BorderLayout(10, 10));
        setBackground(new Color(240, 248, 255));
        setBorder(new EmptyBorder(20, 20, 20, 20));
        
        initializeComponents();
        loadQuestion();
    }
    
    private void initializeComponents() {
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setOpaque(false);
        
        JPanel infoPanel = new JPanel(new GridLayout(1, 2, 10, 0));
        infoPanel.setOpaque(false);
        
        timerLabel = new JLabel("Time: 00:00", JLabel.CENTER);
        timerLabel.setFont(new Font("Arial", Font.BOLD, 24));
        timerLabel.setForeground(new Color(220, 20, 60));
        timerLabel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(220, 20, 60), 2),
            new EmptyBorder(10, 20, 10, 20)
        ));
        
        scoreLabel = new JLabel("Score: 0", JLabel.CENTER);
        scoreLabel.setFont(new Font("Arial", Font.BOLD, 24));
        scoreLabel.setForeground(new Color(34, 139, 34));
        scoreLabel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(34, 139, 34), 2),
            new EmptyBorder(10, 20, 10, 20)
        ));
        
        infoPanel.add(timerLabel);
        infoPanel.add(scoreLabel);
        topPanel.add(infoPanel, BorderLayout.NORTH);
        
        JLabel titleLabel = new JLabel("Question " + (currentQuestionIndex + 1) + " of " + questions.size(), JLabel.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18));
        titleLabel.setBorder(new EmptyBorder(15, 0, 10, 0));
        topPanel.add(titleLabel, BorderLayout.SOUTH);
        
        add(topPanel, BorderLayout.NORTH);
        
        JPanel centerPanel = new JPanel(new BorderLayout(10, 10));
        centerPanel.setOpaque(false);
        
        questionLabel = new JLabel();
        questionLabel.setFont(new Font("Arial", Font.BOLD, 20));
        questionLabel.setBorder(new EmptyBorder(20, 20, 20, 20));
        questionLabel.setBackground(Color.WHITE);
        questionLabel.setOpaque(true);
        centerPanel.add(questionLabel, BorderLayout.NORTH);
        
        JPanel optionsPanel = new JPanel(new GridLayout(4, 1, 10, 10));
        optionsPanel.setOpaque(false);
        optionsPanel.setBorder(new EmptyBorder(10, 20, 10, 20));
        
        optionsGroup = new ButtonGroup();
        optionButtons = new JRadioButton[4];
        
        for (int i = 0; i < 4; i++) {
            optionButtons[i] = new JRadioButton();
            optionButtons[i].setFont(new Font("Arial", Font.PLAIN, 16));
            optionButtons[i].setBackground(Color.WHITE);
            optionButtons[i].setBorder(new EmptyBorder(10, 10, 10, 10));
            optionButtons[i].setFocusPainted(false);
            optionsGroup.add(optionButtons[i]);
            optionsPanel.add(optionButtons[i]);
        }
        
        centerPanel.add(optionsPanel, BorderLayout.CENTER);
        add(centerPanel, BorderLayout.CENTER);
        
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        buttonPanel.setOpaque(false);
        
        submitButton = new JButton("Submit Answer");
        submitButton.setFont(new Font("Arial", Font.BOLD, 16));
        submitButton.setBackground(new Color(30, 144, 255));
        submitButton.setForeground(Color.WHITE);
        submitButton.setFocusPainted(false);
        submitButton.setBorder(new EmptyBorder(10, 30, 10, 30));
        submitButton.addActionListener(e -> submitAnswer());
        
        nextButton = new JButton("Next Question");
        nextButton.setFont(new Font("Arial", Font.BOLD, 16));
        nextButton.setBackground(new Color(34, 139, 34));
        nextButton.setForeground(Color.WHITE);
        nextButton.setFocusPainted(false);
        nextButton.setBorder(new EmptyBorder(10, 30, 10, 30));
        nextButton.setVisible(false);
        nextButton.addActionListener(e -> nextQuestion());
        
        buttonPanel.add(submitButton);
        buttonPanel.add(nextButton);
        add(buttonPanel, BorderLayout.SOUTH);
    }
    
    private void loadQuestion() {
        if (currentQuestionIndex >= questions.size()) {
            finishQuiz();
            return;
        }
        
        Question q = questions.get(currentQuestionIndex);
        questionLabel.setText("<html><body style='width: 100%; padding: 10px;'>" + q.getQuestion() + "</body></html>");
        
        String[] options = q.getOptions();
        for (int i = 0; i < 4; i++) {
            optionButtons[i].setText(options[i]);
            optionButtons[i].setEnabled(true);
            optionButtons[i].setBackground(Color.WHITE);
        }
        
        optionsGroup.clearSelection();
        submitButton.setVisible(true);
        nextButton.setVisible(false);
        
        timeRemaining = q.getTimeLimit();
        startTimer();
    }
    
    private void startTimer() {
        if (timer != null) timer.stop();
        
        timer = new javax.swing.Timer(1000, new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                timeRemaining--;
                timerLabel.setText(String.format("Time: %02d:%02d", timeRemaining / 60, timeRemaining % 60));
                
                if (timeRemaining <= 5) {
                    timerLabel.setForeground(Color.RED);
                }
                
                if (timeRemaining <= 0) {
                    timer.stop();
                    JOptionPane.showMessageDialog(QuizPanel.this, 
                        "Time's up! Moving to next question.", 
                        "Timeout", JOptionPane.WARNING_MESSAGE);
                    wrongAnswers++;
                    score -= 1;
                    updateScore();
                    nextQuestion();
                }
            }
        });
        timer.start();
    }
    
    private void submitAnswer() {
        timer.stop();
        
        int selectedOption = -1;
        for (int i = 0; i < 4; i++) {
            if (optionButtons[i].isSelected()) {
                selectedOption = i;
                break;
            }
        }
        
        if (selectedOption == -1) {
            JOptionPane.showMessageDialog(this, 
                "Please select an answer!", 
                "No Selection", JOptionPane.WARNING_MESSAGE);
            timer.start();
            return;
        }
        
        Question q = questions.get(currentQuestionIndex);
        
        for (int i = 0; i < 4; i++) {
            optionButtons[i].setEnabled(false);
        }
        
        if (selectedOption == q.getCorrectAnswer()) {
            optionButtons[selectedOption].setBackground(new Color(144, 238, 144));
            score += 4;
            correctAnswers++;
            JOptionPane.showMessageDialog(this, 
                "Correct! +4 points", 
                "Correct Answer", JOptionPane.INFORMATION_MESSAGE);
        } else {
            optionButtons[selectedOption].setBackground(new Color(255, 182, 193));
            optionButtons[q.getCorrectAnswer()].setBackground(new Color(144, 238, 144));
            score -= 1;
            wrongAnswers++;
            JOptionPane.showMessageDialog(this, 
                "Wrong! -1 point\nCorrect answer: " + q.getOptions()[q.getCorrectAnswer()], 
                "Wrong Answer", JOptionPane.ERROR_MESSAGE);
        }
        
        updateScore();
        submitButton.setVisible(false);
        nextButton.setVisible(true);
    }
    
    private void nextQuestion() {
        currentQuestionIndex++;
        loadQuestion();
    }
    
    private void updateScore() {
        scoreLabel.setText("Score: " + score);
    }
    
    private void finishQuiz() {
        timer.stop();
        
        double accuracy = (correctAnswers * 100.0) / questions.size();
        String date = new java.text.SimpleDateFormat("dd-MM-yyyy HH:mm").format(new Date());
        
        QuizScore quizScore = new QuizScore(playerName, score, questions.size(), date, accuracy);
        quizManager.saveScore(quizScore);
        
        String message = String.format(
            "Quiz Completed!\n\n" +
            "Player: %s\n" +
            "Total Score: %d\n" +
            "Correct Answers: %d\n" +
            "Wrong Answers: %d\n" +
            "Accuracy: %.1f%%",
            playerName, score, correctAnswers, wrongAnswers, accuracy
        );
        
        JOptionPane.showMessageDialog(this, message, "Quiz Results", JOptionPane.INFORMATION_MESSAGE);
        
        SwingUtilities.getWindowAncestor(this).dispose();
        new LeaderboardFrame(quizManager).setVisible(true);
    }
}

class LeaderboardFrame extends JFrame {
    
    public LeaderboardFrame(QuizManager quizManager) {
        
        setTitle("Quiz Leaderboard");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBackground(new Color(240, 248, 255));
        mainPanel.setBorder(new EmptyBorder(20, 20, 20, 20));
        
        JLabel titleLabel = new JLabel("🏆 LEADERBOARD 🏆", JLabel.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 32));
        titleLabel.setForeground(new Color(218, 165, 32));
        titleLabel.setBorder(new EmptyBorder(10, 0, 20, 0));
        mainPanel.add(titleLabel, BorderLayout.NORTH);
        
        String[] columnNames = {"Rank", "Player Name", "Score", "Questions", "Accuracy %", "Date"};
        DefaultTableModel model = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        List<QuizScore> scores = quizManager.getLeaderboard();
        for (int i = 0; i < scores.size(); i++) {
            QuizScore s = scores.get(i);
            model.addRow(new Object[]{
                i + 1,
                s.getPlayerName(),
                s.getScore(),
                s.getTotalQuestions(),
                String.format("%.1f%%", s.getAccuracy()),
                s.getDate()
            });
        }
        
        JTable table = new JTable(model);
        table.setFont(new Font("Arial", Font.PLAIN, 14));
        table.setRowHeight(30);
        table.getTableHeader().setFont(new Font("Arial", Font.BOLD, 14));
        table.getTableHeader().setBackground(new Color(30, 144, 255));
        table.getTableHeader().setForeground(Color.WHITE);
        
        JScrollPane scrollPane = new JScrollPane(table);
        mainPanel.add(scrollPane, BorderLayout.CENTER);
        
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        buttonPanel.setOpaque(false);
        
        JButton playAgainButton = new JButton("Play Again");
        playAgainButton.setFont(new Font("Arial", Font.BOLD, 16));
        playAgainButton.setBackground(new Color(34, 139, 34));
        playAgainButton.setForeground(Color.WHITE);
        playAgainButton.setFocusPainted(false);
        playAgainButton.setBorder(new EmptyBorder(10, 30, 10, 30));
        playAgainButton.addActionListener(e -> {
            dispose();
            new QuizApp().setVisible(true);
        });
        
        JButton exitButton = new JButton("Exit");
        exitButton.setFont(new Font("Arial", Font.BOLD, 16));
        exitButton.setBackground(new Color(220, 20, 60));
        exitButton.setForeground(Color.WHITE);
        exitButton.setFocusPainted(false);
        exitButton.setBorder(new EmptyBorder(10, 30, 10, 30));
        exitButton.addActionListener(e -> System.exit(0));
        
        buttonPanel.add(playAgainButton);
        buttonPanel.add(exitButton);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);
        
        add(mainPanel);
    }
}

public class QuizApp extends JFrame {
    private QuizManager quizManager;
    
    public QuizApp() {
        quizManager = new QuizManager();
        
        setTitle("Online Quiz System");
        setSize(900, 700);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        
        showWelcomeScreen();
    }
    
    private void showWelcomeScreen() {
        JPanel welcomePanel = new JPanel(new BorderLayout());
        welcomePanel.setBackground(new Color(240, 248, 255));
        welcomePanel.setBorder(new EmptyBorder(50, 50, 50, 50));
        
        JPanel centerPanel = new JPanel(new GridBagLayout());
        centerPanel.setOpaque(false);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridwidth = GridBagConstraints.REMAINDER;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(10, 50, 10, 50);
        
        JLabel titleLabel = new JLabel("📚 ONLINE QUIZ SYSTEM 📚", JLabel.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 36));
        titleLabel.setForeground(new Color(30, 144, 255));
        centerPanel.add(titleLabel, gbc);
        
        JLabel infoLabel = new JLabel("<html><body style='text-align: center; padding: 20px;'>" +
            "⏰ Each question has a time limit<br>" +
            "✅ Correct answer: +4 points<br>" +
            "❌ Wrong answer: -1 point<br>" +
            "🎯 Questions are randomized<br><br>" +
            "Good Luck!</body></html>", JLabel.CENTER);
        infoLabel.setFont(new Font("Arial", Font.PLAIN, 18));
        centerPanel.add(infoLabel, gbc);
        
        JLabel nameLabel = new JLabel("Enter Your Name:", JLabel.CENTER);
        nameLabel.setFont(new Font("Arial", Font.BOLD, 20));
        centerPanel.add(nameLabel, gbc);
        
        JTextField nameField = new JTextField(20);
        nameField.setFont(new Font("Arial", Font.PLAIN, 18));
        nameField.setHorizontalAlignment(JTextField.CENTER);
        centerPanel.add(nameField, gbc);
        
        JButton startButton = new JButton("START QUIZ");
        startButton.setFont(new Font("Arial", Font.BOLD, 20));
        startButton.setBackground(new Color(34, 139, 34));
        startButton.setForeground(Color.WHITE);
        startButton.setFocusPainted(false);
        startButton.setBorder(new EmptyBorder(15, 40, 15, 40));
        startButton.addActionListener(e -> {
            String name = nameField.getText().trim();
            if (name.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Please enter your name!", "Error", JOptionPane.ERROR_MESSAGE);
            } else {
                getContentPane().removeAll();
                add(new QuizPanel(quizManager, name));
                revalidate();
                repaint();
            }
        });
        centerPanel.add(startButton, gbc);
        
        JButton leaderboardButton = new JButton("VIEW LEADERBOARD");
        leaderboardButton.setFont(new Font("Arial", Font.BOLD, 16));
        leaderboardButton.setBackground(new Color(218, 165, 32));
        leaderboardButton.setForeground(Color.WHITE);
        leaderboardButton.setFocusPainted(false);
        leaderboardButton.setBorder(new EmptyBorder(10, 30, 10, 30));
        leaderboardButton.addActionListener(e -> {
            dispose();
            new LeaderboardFrame(quizManager).setVisible(true);
        });
        centerPanel.add(leaderboardButton, gbc);
        
        welcomePanel.add(centerPanel, BorderLayout.CENTER);
        add(welcomePanel);
    }
    
    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        SwingUtilities.invokeLater(() -> {
            new QuizApp().setVisible(true);
        });
    }
}
