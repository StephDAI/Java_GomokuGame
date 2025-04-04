package com.example.gomokuexample;

import javafx.animation.PauseTransition;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.util.Duration;
import javafx.scene.control.Button;
import javafx.application.Platform;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.scene.text.Text;
import javafx.util.Duration;


public class GomokuGameFX extends Application {
    private static final int CELL_SIZE = 30;
    private static final int BOARD_SIZE = 20;
    private GomokuGame game;
    private Text invalidMoveText;
    private Text failText;
    private Text moveCountText;
    private Text winnerText;
    private Timeline timer;
    private static final int TIME_LIMIT = 10; // 10 seconds
    private int timeLeft;
    private Text timerText;
    private Text timeExceededText;

    @Override
    public void start(Stage primaryStage) {
        game = new GomokuGame(BOARD_SIZE);
        StackPane root = new StackPane();

        // Add background image
        Image image = new Image("780.jpg");
        ImageView imageView = new ImageView(image);
        root.getChildren().add(imageView);

        // Display game title
        Text text = new Text("Gomoku Game");
        text.setFont(Font.font("Arial", FontWeight.BOLD,40));
        text.setFill(Color.BLACK);
        root.getChildren().add(text);
        StackPane.setAlignment(text, Pos.TOP_CENTER);
        StackPane.setMargin(text, new Insets(20, 0, 0, 10));

        // Add canvas
        Canvas canvas = new Canvas(CELL_SIZE * BOARD_SIZE+15, CELL_SIZE * BOARD_SIZE+15);
        root.getChildren().add(canvas);

        drawBoard(canvas.getGraphicsContext2D());

        // Add invalid move text
        invalidMoveText = new Text("Invalid move!");
        invalidMoveText.setFont(Font.font("Arial", FontWeight.BOLD, 20));
        invalidMoveText.setFill(Color.RED);
        invalidMoveText.setVisible(false);
        root.getChildren().add(invalidMoveText);
        StackPane.setAlignment(invalidMoveText, Pos.TOP_CENTER);
        StackPane.setMargin(invalidMoveText, new Insets(120, 0, 20, 0));
        failText = new Text("No move to undo/redo!");
        failText.setFont(Font.font("Arial", FontWeight.BOLD, 20));
        failText.setFill(Color.RED);
        failText.setVisible(false);
        root.getChildren().add(failText);
        StackPane.setAlignment(failText, Pos.TOP_CENTER);
        StackPane.setMargin(failText, new Insets(140, 0, 20, 0));

        // Add time exceeded text
        timeExceededText = new Text("Time exceeded! Switch player!");
        timeExceededText.setFont(Font.font("Arial", FontWeight.BOLD, 20));
        timeExceededText.setFill(Color.RED);
        timeExceededText.setVisible(false);
        root.getChildren().add(timeExceededText);
        StackPane.setAlignment(timeExceededText, Pos.TOP_CENTER);
        StackPane.setMargin(timeExceededText, new Insets(160, 0, 20, 0));

        // Add move count text
        moveCountText = new Text("Moves: " + game.getMovements());
        moveCountText.setFont(Font.font("Arial", FontWeight.BOLD, 20));
        moveCountText.setFill(Color.BLACK);
        root.getChildren().add(moveCountText);
        StackPane.setAlignment(moveCountText, Pos.TOP_RIGHT);
        StackPane.setMargin(moveCountText, new Insets(20, 20, 0, 0));

        // Add current player text
        Text currentPlayerText = new Text("Current player: Black");
        currentPlayerText.setFont(Font.font("Arial", FontWeight.BOLD, 20));
        currentPlayerText.setFill(Color.BLACK);
        root.getChildren().add(currentPlayerText);
        StackPane.setAlignment(currentPlayerText, Pos.TOP_LEFT);
        StackPane.setMargin(currentPlayerText, new Insets(20, 0, 0, 20));

        // Add maximum length text
        Text maxLengthText = new Text("Maximum length of black: " + game.maximumLength[0][1] + "\nMaximum length of white: " + game.maximumLength[0][2]);
        maxLengthText.setFont(Font.font("Arial", FontWeight.BOLD, 20));
        maxLengthText.setFill(Color.BLACK);
        root.getChildren().add(maxLengthText);
        StackPane.setAlignment(maxLengthText, Pos.TOP_RIGHT);
        StackPane.setMargin(maxLengthText, new Insets(50, 20, 0, 0));

        // Add restart button
        Button restartButton = new Button("Restart");
        restartButton.setOnAction(e -> {
            game = new GomokuGame(BOARD_SIZE);
            drawBoard(canvas.getGraphicsContext2D());
            moveCountText.setText("Moves: 0");
            currentPlayerText.setText("Current player: Black");
            maxLengthText.setText("Maximum length of black: 0\nMaximum length of white: 0");
            invalidMoveText.setVisible(false);
            timeLeft = TIME_LIMIT; // Reset the time left
            timer.playFromStart(); // Restart the timer
            timerText.setVisible(true);
            if(winnerText != null){
                root.getChildren().remove(winnerText);
            }

        });
        root.getChildren().add(restartButton);
        StackPane.setAlignment(restartButton, Pos.BOTTOM_CENTER);
        StackPane.setMargin(restartButton, new Insets(0, 50, 50, 0));

        // Add exit button
        Button exitButton = new Button("Exit");
        exitButton.setOnAction(e -> Platform.exit());
        root.getChildren().add(exitButton);
        StackPane.setAlignment(exitButton, Pos.BOTTOM_CENTER);
        StackPane.setMargin(exitButton, new Insets(0, 0, 50, 50));

        // Add undo button
        Button undoButton = new Button("Undo");
        undoButton.setOnAction(e -> {
            if(game.undo()){
                drawBoard(canvas.getGraphicsContext2D());
                int move = game.getMovements();
                moveCountText.setText("Moves: " + move);
                maxLengthText.setText("Maximum length of black: " + game.maximumLength[move][1] + "\nMaximum length of white: " + game.maximumLength[move][2]);
                if (game.getCurrentPlayer() == 1) {
                    currentPlayerText.setText("Current player: Black");
                } else {
                    currentPlayerText.setText("Current player: White");
                }
                timeLeft = TIME_LIMIT; // Reset the time left
                timer.playFromStart(); // Restart the timer
            }
            else{
                showfailMessage();
            }
        });
        root.getChildren().add(undoButton);
        StackPane.setAlignment(undoButton, Pos.BOTTOM_CENTER);
        StackPane.setMargin(undoButton, new Insets(0, 180, 50, 0));

        // Add redo button
        Button redoButton = new Button("Redo");
        redoButton.setOnAction(e -> {
            if(game.redo()){
                drawBoard(canvas.getGraphicsContext2D());
                int move = game.getMovements();
                moveCountText.setText("Moves: " + move);
                maxLengthText.setText("Maximum length of black: " + game.maximumLength[move][1] + "\nMaximum length of white: " + game.maximumLength[move][2]);
                if (game.getCurrentPlayer() == 1) {
                    currentPlayerText.setText("Current player: Black");
                } else {
                    currentPlayerText.setText("Current player: White");
                }
                timeLeft = TIME_LIMIT; // Reset the time left
                timer.playFromStart(); // Restart the timer
            }
            else{
                showfailMessage();
            }
        });
        root.getChildren().add(redoButton);
        StackPane.setAlignment(redoButton, Pos.BOTTOM_CENTER);
        StackPane.setMargin(redoButton, new Insets(0, 0, 50, 170));

        // Initialize the timer and timerText
        timerText = new Text("Time left: " + TIME_LIMIT + "s");
        timerText.setFont(Font.font("Arial", FontWeight.BOLD, 30));
        root.getChildren().add(timerText);
        StackPane.setAlignment(timerText, Pos.TOP_LEFT);
        StackPane.setMargin(timerText, new Insets(40, 0, 0, 20));

        timer = new Timeline(new KeyFrame(Duration.seconds(1), e -> {
            timeLeft--;
            timerText.setText("Time left: " + timeLeft + "s");
            if (timeLeft <= 0) {
                game.switchPlayer();
                showTimeExceededMessage();
                timeLeft = TIME_LIMIT; // Reset the time left
                timer.playFromStart();// Restart the timer
                currentPlayerText.setText("Current player: " + (game.getCurrentPlayer() == 1 ? "Black" : "White"));
            }
        }));
        timer.setCycleCount(TIME_LIMIT);

        //mouse control
        canvas.setOnMouseClicked(e -> {
            int x = (int) (e.getX() / CELL_SIZE);
            int y = (int) (e.getY() / CELL_SIZE);
            if (game.move(x, y)) {
                drawBoard(canvas.getGraphicsContext2D());

                //update the move count, current player and maximun length
                int move = game.getMovements();
                moveCountText.setText("Moves: " + move);
                maxLengthText.setText("Maximum length of black: " + game.maximumLength[move][1] + "\nMaximum length of white: " + game.maximumLength[move][2]);
                if (game.getCurrentPlayer() == 1) {
                    currentPlayerText.setText("Current player: Black");
                } else {
                    currentPlayerText.setText("Current player: White");
                }

                timeLeft = TIME_LIMIT; // Reset the time left
                timer.playFromStart(); // Restart the timer
                if (game.isGameOver()) {
                    //display the winner
                    System.out.println("Game over! The winner is player " + game.getWinner() + "!");
                    timer.pause();
                    timerText.setVisible(false);
                    if(game.getWinner() == 1){
                        maxLengthText.setText("Maximum length of black: 5" + "\nMaximum length of white: " + game.maximumLength[move][2]);
                        winnerText = new Text("Black wins!");
                        winnerText.setFont(Font.font("Arial", FontWeight.BOLD, 45));
                        winnerText.setFill(Color.BLACK);
                        root.getChildren().add(winnerText);
                        StackPane.setAlignment(winnerText, Pos.CENTER);
                    }
                    else{
                        maxLengthText.setText("Maximum length of black: " + game.maximumLength[move][1] + "\nMaximum length of white: 5");
                        winnerText = new Text("White wins!");
                        winnerText.setFont(Font.font("Arial", FontWeight.BOLD, 45));
                        winnerText.setFill(Color.WHITE);
                        root.getChildren().add(winnerText);
                        StackPane.setAlignment(winnerText, Pos.CENTER);
                    }
                }

            } else {
                System.out.println("Invalid move!");
                showInvalidMoveMessage();
            }
        });

        Scene scene = new Scene(root, 1000, 800);
        primaryStage.setTitle("Gomoku Game");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private void drawBoard(GraphicsContext gc) {
        Color board_color = Color.rgb(212, 148, 58);
        gc.setFill(board_color);

        gc.clearRect(0, 0, CELL_SIZE * BOARD_SIZE+15, CELL_SIZE * BOARD_SIZE+15);
        gc.fillRect(0, 0, CELL_SIZE * BOARD_SIZE+15, CELL_SIZE * BOARD_SIZE+15);
        gc.setStroke(Color.BLACK);
        for (int i = 0; i < BOARD_SIZE; i++) {
            gc.strokeLine(i * CELL_SIZE+15, 15, i * CELL_SIZE+15, CELL_SIZE * (BOARD_SIZE-1)+15);
            gc.strokeLine(15, i * CELL_SIZE+15, CELL_SIZE * (BOARD_SIZE-1)+15, i * CELL_SIZE+15);
        }
        int[][] board = game.getBoard();
        for (int i = 0; i < BOARD_SIZE; i++) {
            for (int j = 0; j < BOARD_SIZE; j++) {
                double x = (i + 0.1) * CELL_SIZE;
                double y = (j + 0.1) * CELL_SIZE;
                double w = CELL_SIZE * 0.8;
                double h = CELL_SIZE * 0.8;
                if (board[i][j] == 1) {
                    gc.setFill(Color.BLACK);
                    gc.fillOval(x, y, w, h);
                    gc.strokeOval(x, y, w, h);
                } else if (board[i][j] == 2) {
                    gc.setFill(Color.WHITE);
                    gc.fillOval(x, y, w, h);
                    gc.strokeOval(x, y, w, h);
                }
            }
        }
        // Add numbers of rows and columns
        gc.setFill(Color.BLACK);
        gc.setFont(new Font(15));
        for (int i = 1; i < BOARD_SIZE; i++) {
            gc.fillText(Integer.toString(i), (i-1) * CELL_SIZE+11, 602);
            gc.fillText(Integer.toString(i), 593, (i-1) * CELL_SIZE+19);
        }
        gc.fillText("20",591,600);
    }

    private void showInvalidMoveMessage() {
        invalidMoveText.setVisible(true);
        PauseTransition pause = new PauseTransition(Duration.seconds(1));
        pause.setOnFinished(event -> invalidMoveText.setVisible(false));
        pause.play();
    }

    private void showfailMessage() {
        failText.setVisible(true);
        PauseTransition pause = new PauseTransition(Duration.seconds(1));
        pause.setOnFinished(event -> failText.setVisible(false));
        pause.play();
    }
    private void showTimeExceededMessage() {
        timeExceededText.setVisible(true);
        PauseTransition pause = new PauseTransition(Duration.seconds(1));
        pause.setOnFinished(event -> timeExceededText.setVisible(false));
        pause.play();
    }
    public static void main(String[] args) {
        launch(args);
    }
}