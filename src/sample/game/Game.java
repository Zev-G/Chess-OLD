package sample.game;

import javafx.animation.FadeTransition;
import javafx.geometry.Insets;
import javafx.geometry.Point2D;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import sample.Main;
import sample.betterJFX.prettystage.PrettyStage;
import sample.chess.*;
import sample.game.components.*;
import sample.game.virtualopponent.Opponent;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;
import java.time.*;

public class Game {

    private boolean gameRunning;

    private MoveTracker moveTracker;
    private ChessBoard chessBoard;
    private GameManager gameManager;
    private Team currentTurn;

    private Stage chessBoardStage = null;
    private Stage moveTrackerStage = null;
    private Stage promotionStage = null;

    private ArrayList<Move> moves = new ArrayList<>();
    private Label attached;

    private Instant startTime;
    private Instant endTime = null;

    private Instant turnStartTime;
    private Duration whiteTime;
    private Duration blackTime;

    private Team winner = null;

    private boolean showPopupAfterGame = true;
    private boolean rotateGame = false;

    private int drawCounter;

    private ArrayList<HashMap<Point2D, ChessPiece>> pastBoards = new ArrayList<>();

    private Opponent whiteOpp;
    private Opponent blackOpp;

    private MediaPlayer playSound;
    private boolean useSound = true;

    private String defaultChessBoard;
    private File saveFile;

    private float gameTimeOffset;
    private float whiteTeamOffset;
    private float blackTeamOffset;
    private float currentTurnOffset;

    //Events
    public List<GameOverEvent> gameOverEventList;
    public List<NewGamePressed> newGamePressedList;
    public List<NextTurnEvent> nextTurnEvents;

    public Game(ChessBoard board) {
        this.chessBoard = board;
        this.chessBoard.setGame(this);
        init("");
    }
    public Game() {
        this(new ChessBoard());
    }

    public static File saveFolder() {
        return new File(Chess.getGameFile().getPath() + "/saves");
    }

    public static Game loadFromFolder(File file) {
        File gameFile = null; String gameText = "";
        File boardFile = null; String boardText = "";
        File moveFile = null; String moveText = "";
        for (File currentFile : Objects.requireNonNull(file.listFiles())) {
            if (currentFile.getName().contains("defaultboard")) {
                boardFile = currentFile;
                boardText = Chess.readFile(currentFile);
            } else if (currentFile.getName().contains("gameinfo")) {
                gameFile = currentFile;
                gameText = Chess.readFile(currentFile);
            } else if (currentFile.getName().contains("moves")) {
                moveFile = currentFile;
                moveText = Chess.readFile(currentFile);
            }
        }
        if (gameFile == null || boardFile == null) {
            return null;
        }
        ChessBoard gameBoard = ChessBoard.getFormattedBoard(boardText);
        Game game = new Game(gameBoard);

        if (!moveText.equals("")) {
            for (String moveLine : moveText.split("\n")) {
                String line = moveLine.replaceAll("\t", "");
                if (line.startsWith("Moves:")) {
                    continue;
                }
                String[] spaceSplit = line.split(" ");
                Point2D from = new Point2D(Double.parseDouble(spaceSplit[2].split("-")[0]), Double.parseDouble(spaceSplit[2].split("-")[1]));
                Point2D to = new Point2D(Double.parseDouble(spaceSplit[4].split("-")[0]), Double.parseDouble(spaceSplit[4].split("-")[1]));
                gameBoard.moveChessPiece(gameBoard.getSpotFromLoc(from).getPiece(), from, to);
            }
        }
        if (!gameText.equals("")) {
            for (String line : gameText.split("\n")) {
                if (line.startsWith("\t")) {
                    if (line.startsWith("\tTotalLength: ")) {
                        game.setGameTimeOffset(Float.parseFloat(line.replaceAll("\tTotalLength: ", "")));
                    } else if (line.startsWith("\tTurnLength: ")) {
                        game.setCurrentTurnOffset(Float.parseFloat(line.replaceAll("\tTurnLength: ", "")));
                    } else if (line.startsWith("\tWhiteLength: ")) {
                        game.setWhiteTeamOffset(Float.parseFloat(line.replaceAll("\tWhiteLength: ", "")));
                    } else if (line.startsWith("\tBlackLength: ")) {
                        game.setBlackTeamOffset(Float.parseFloat(line.replaceAll("\tBlackLength: ", "")));
                    }
                }
            }
        }

        return game;
    }

    public File getThisSaveFolder() {
        return this.saveFile;
    }

    public void init(String name) {
        this.gameRunning = true;
        if (!Chess.getGameFile().exists()) {
            Chess.getGameFile().mkdir();
        }
        if (!Game.saveFolder().exists()) {
            Game.saveFolder().mkdir();
        }
        if (name.equals("")) {
            this.saveFile = new File(Game.saveFolder() + "/" + Instant.now().toEpochMilli());
        } else {
            this.saveFile = new File(Game.saveFolder() + "/" + name);
        }
        this.defaultChessBoard = this.chessBoard.getFormatted();
        this.gameOverEventList = new ArrayList<>();
        this.newGamePressedList = new ArrayList<>();
        this.nextTurnEvents = new ArrayList<>();
        this.startTime = Instant.now();
        this.turnStartTime = Instant.now();
        this.currentTurn = Team.WHITE;
        this.moveTracker = new MoveTracker(this);
        try {
            this.gameManager = new GameManager(this);
        } catch (IOException e) { e.printStackTrace(); }
        Media playNoise = new Media(Main.class.getResource("Sounds/move_sound_1.mp3").toExternalForm());
        this.playSound = new MediaPlayer(playNoise);
    }

    public List<GameOverEvent> getGameOverListeners() {return this.gameOverEventList;}
    public List<NewGamePressed> getNewGamePressedListeners() {return this.newGamePressedList;}
    public List<NextTurnEvent> getNextTurnEventListeners() {return this.nextTurnEvents;}

    public void setUseSound(boolean input) {
        this.useSound = input;
    }
    public boolean getUseSound() {
        return this.useSound;
    }

    public Team getWinner() {
        return this.winner;
    }
    public Team getCurrentTurn() {
        return this.currentTurn;
    }
    public ChessBoard getChessBoard() {
        return this.chessBoard;
    }
    public MoveTracker getMoveTracker() {
        return this.moveTracker;
    }
    public GameManager getGameManager() {
        return this.gameManager;
    }
    public void attachToMoveChange(Label input) {
        this.attached = input;
    }
    public int getMovesCount() { return moves.size(); }

    public void setRotateGame(boolean input) {this.rotateGame = input;}
    public void setShowPopupAfterGame(boolean input) {this.showPopupAfterGame = input;}

    public void addMove(Move input) {
        this.currentTurnOffset = 0;
        this.playSound.stop();
        if (this.useSound) {
            this.playSound.play();
        }
        this.moves.add(input);
        if (input.getTakePiece() == null) {
            this.drawCounter++;
            if (this.drawCounter == 50) {
                endGame(null);
            }
        } else {
            this.drawCounter = 0;
        }
        for (Map.Entry<Point2D, ChessPiece> entry : this.chessBoard.getChessPieceMap().entrySet()) {
            if (entry.getValue() != null && entry.getValue().getPieceType() == PieceType.PAWN) {
                entry.getValue().setEnPassant(null);
                entry.getValue().setEnPassantTake(null);
            }
        }
        if (input.getPiece().getPieceType() == PieceType.PAWN) {

            double x = input.getTo().getX() - input.getFrom().getX();
            double y = input.getTo().getY() - input.getFrom().getY();
            if (x == 0.0 && (y == 2 || y == -2)) {
                Point2D left = new Point2D(input.getTo().getX() - 1, input.getTo().getY());
                Point2D right = new Point2D(input.getTo().getX() + 1, input.getTo().getY());
                Point2D newPoint = new Point2D(input.getTo().getX(), y == -2 ? input.getTo().getY() + 1 : input.getTo().getY() - 1);
                if (this.chessBoard.getSpotFromLoc(newPoint).getPiece() == null) {
                    if (this.chessBoard.getIsValidLoc(left) && this.chessBoard.getSpotFromLoc(left).getPiece() != null && this.chessBoard.getSpotFromLoc(left).getPiece().getPieceType() == PieceType.PAWN) {
                        this.chessBoard.getSpotFromLoc(left).getPiece().setEnPassant(newPoint);
                        this.chessBoard.getSpotFromLoc(left).getPiece().setEnPassantTake(input.getTo());
                    }
                    if (this.chessBoard.getIsValidLoc(right) && this.chessBoard.getSpotFromLoc(right).getPiece() != null && this.chessBoard.getSpotFromLoc(right).getPiece().getPieceType() == PieceType.PAWN) {
                        this.chessBoard.getSpotFromLoc(right).getPiece().setEnPassant(newPoint);
                        this.chessBoard.getSpotFromLoc(right).getPiece().setEnPassantTake(input.getTo());
                    }
                }
            }
        }
        this.chessBoard.resetMoveButtons();
        this.chessBoard.getSpotFromLoc(input.getTo()).setBackground(new Background(new BackgroundFill(Paint.valueOf("#5fcf68"), CornerRadii.EMPTY, Insets.EMPTY)));
        try {
            this.moveTracker.addMove(input);
        } catch (IOException e) {
            e.printStackTrace();
        }
        this.chessBoard.getSpotFromLoc(input.getTo()).restoreBg();
        if (this.chessBoard.getCheckMateStatus(Team.WHITE)) {
            endGame(Team.BLACK);
        } else if (this.chessBoard.getCheckMateStatus(Team.BLACK)) {
            endGame(Team.WHITE);
        } else if (this.chessBoard.getStalemateStatus(Chess.flipTeam(input.piece.getTeam()))) {
            endGame(null);
        } else {
            Team winner = this.chessBoard.kingIsAlive();
            if (winner == null) {
                nextTurn();
                Team team = input.getPiece().getTeam() == Team.WHITE ? Team.BLACK : Team.WHITE;
                if (this.chessBoard.getCheckStatus(team)) {
                    ChessPiece pulsePiece = this.chessBoard.getSpotFromLoc(this.chessBoard.getKingLoc(team)).getPiece();
                    FadeTransition pulse = new FadeTransition(new javafx.util.Duration(150), pulsePiece);
                    pulse.setFromValue(1.0);
                    pulse.setToValue(0.1);
                    pulse.setCycleCount(6);
                    pulse.setAutoReverse(true);
                    pulse.play();

                }
                this.chessBoard.resetMoveButtons();
            } else {
                endGame(winner);
            }
        }
    }

    public void fireGameOver() {for (GameOverEvent listener : this.gameOverEventList) {
        listener.onGameOver(this);
    }}
    public void fireNewGamePressed() {for (NewGamePressed listener : this.newGamePressedList) {
        listener.onNewGamePressed(this);
    }}
    public void fireNextTurn() { for (NextTurnEvent listener : this.nextTurnEvents) {
        listener.onTurnChange(this, null);
    }}

    public ArrayList<Move> getMoves() {return this.moves;}

    public void endGame(Team win) {
        this.winner = win;
        this.endTime = Instant.now();
        this.currentTurn = null;
        if (this.showPopupAfterGame) {
            try {
                WinnerPopup winnerPopup = new WinnerPopup(this);
                winnerPopup.initOwner(this.gameManager.getScene().getWindow());
                winnerPopup.initModality(Modality.WINDOW_MODAL);
                winnerPopup.show();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        for (GameOverEvent listener : gameOverEventList) {
            listener.onGameOver(this);
        }
    }

    public void nextTurn() {
        if (this.currentTurn == Team.WHITE) {
            this.currentTurn = Team.BLACK;
            Duration soFar = this.getTurnDuration();
            if (this.whiteTime != null) {
                this.whiteTime = soFar.plus(this.whiteTime);
            } else {
                this.whiteTime = soFar;
            }
        } else {
            this.currentTurn = Team.WHITE;
            Duration soFar = this.getTurnDuration();
            if (this.blackTime != null) {
                this.blackTime = soFar.plus(this.blackTime);
            } else {
                this.blackTime = soFar;
            }
        }
        this.turnStartTime = Instant.now();
        this.chessBoard.resetMoveButtons();
        if (this.rotateGame && this.whiteOpp == null && this.blackOpp == null) {
            this.chessBoard.setOrientationWithTransition(this.currentTurn, new javafx.util.Duration(450));
        }
        for (NextTurnEvent nextTurnEvent : this.nextTurnEvents) {
            nextTurnEvent.onTurnChange(this, this.moves.get(this.moves.size() - 1));
        }
    }


    public void setBlackTeamOffset(float blackTeamOffset) {
        this.blackTime = Duration.ofSeconds((long) blackTeamOffset);
    }
    public void setWhiteTeamOffset(float whiteTeamOffset) {
        this.whiteTime = Duration.ofSeconds((long) whiteTeamOffset);
    }
    public void setCurrentTurnOffset(float currentTurnOffset) {
        this.currentTurnOffset = currentTurnOffset;
    }
    public void setGameTimeOffset(float gameTimeOffset) {
        this.gameTimeOffset = gameTimeOffset;
    }

    public void restart() {
        PrettyStage chessBoardStage = (PrettyStage) this.chessBoard.getScene().getWindow();
        this.chessBoard = new ChessBoard(); chessBoardStage.setPane(this.chessBoard);
        this.chessBoard.setGame(this);
        this.moves.clear();
        this.whiteTime = null;
        this.blackTime = null;
        this.turnStartTime = Instant.now();
        this.currentTurn = Team.WHITE;
        PrettyStage prettyStage = (PrettyStage) this.moveTracker.getScene().getWindow();
        this.moveTracker = new MoveTracker(this);
        prettyStage.setPane(this.moveTracker);
        try {
            PrettyStage gameManagerStage = (PrettyStage) this.gameManager.getScene().getWindow();
            this.gameManager = new GameManager(this);
            gameManagerStage.setPane(this.gameManager);
        } catch (IOException e) {
            e.printStackTrace();
        }
        this.startTime = Instant.now();
        this.winner = null;
    }

    public void setOpponent(Opponent opponent) {
        Team team = opponent.getTeam();
        if (team == Team.WHITE) {
            this.whiteOpp = opponent;
        } else if (team == Team.BLACK) {
            this.blackOpp = opponent;
        }
        if (team == this.currentTurn && this.moves.size() > 0) {
            opponent.thisTurn(null);
        }
    }
    public void initOpponent(Opponent opponent) {
        setOpponent(opponent);
        if (opponent.getTeam() == Team.WHITE) {
            this.gameManager.selectWhiteOpp("Computer");
        } else {
            this.gameManager.selectBlackOpp("Computer");
        }
    }
    public void clearOpponent(Team clearFor) {
        if (clearFor == Team.WHITE) {
            if (this.whiteOpp != null) {
                this.whiteOpp.setEnabled(false);
            }
            this.whiteOpp = null;
        } else {
            if (this.blackOpp != null) {
                this.blackOpp.setEnabled(false);
            }
            this.blackOpp = null;
        }
    }
    public Opponent getOpponent(Team getFor) {
        if (getFor == Team.WHITE) {
            return this.whiteOpp;
        } else if (getFor == Team.BLACK) {
            return this.blackOpp;
        }
        return null;
    }

    public Duration getTimeDifference() {
        return Duration.ofMillis(Duration.between(this.startTime, Instant.now()).toMillis() + Duration.ofSeconds((long) this.gameTimeOffset).toMillis());
    }
    public Duration getGameLength() {
        if (this.gameRunning) { return getTimeDifference(); }
        else { return Duration.ofMillis(Duration.between(this.startTime, this.endTime).toMillis() + Duration.ofSeconds((long) this.gameTimeOffset).toMillis()); }
    }
    public Duration getTurnDuration() {
        return Duration.ofMillis(Duration.between(this.turnStartTime, Instant.now()).toMillis() + Duration.ofSeconds((long) this.currentTurnOffset).toMillis());
    }
    public Duration getWhiteTime() {
        if (this.currentTurn == Team.WHITE) {
            if (this.whiteTime != null) {
                return this.whiteTime.plus(this.getTurnDuration());
            } else {
                return this.getTurnDuration();
            }
        } else {
            return this.whiteTime;
        }
    }
    public Duration getBlackTime() {
        if (this.currentTurn == Team.BLACK) {
            if (this.blackTime != null) {
                return this.blackTime.plus(this.getTurnDuration());
            } else {
                return this.getTurnDuration();
            }
        } else {
            return this.blackTime;
        }
    }
    public void setBlackTime(Duration blackTime) {
        this.blackTime = blackTime;
    }
    public void setWhiteTime(Duration whiteTime) {
        this.whiteTime = whiteTime;
    }

    public void showPromotion(Point2D input) {
        ChessPiece promotionPiece = this.chessBoard.getSpotFromLoc(input).getPiece();
        Promotion promotion = new Promotion(promotionPiece);
        PrettyStage promotionStage = new PrettyStage(Paint.valueOf("#858585"), promotion);
        promotionStage.setWidth(115);
        promotionStage.setHeight(480);
        promotionStage.setMaxWidth(115);
        promotionStage.setMaxHeight(480);
        promotionStage.setText("Promote");
        promotionStage.getPinButton().fire();
        promotionStage.getPinButton().setDisable(true);
        promotionStage.getCloseButton().setDisable(true);
        promotionStage.getPinButton().setVisible(false);
        promotionStage.getCloseButton().setVisible(false);
        promotionStage.initOwner(this.chessBoardStage);
        promotionStage.initModality(Modality.WINDOW_MODAL);

        this.promotionStage = promotionStage;

        promotionStage.show();
    }
    public void promotedPressed(PieceType pieceType, ChessPiece edit) {
        Point2D loc = edit.getLocation();
        this.chessBoard.moveChessPiece(edit, edit.getLocation(), null, false, true);
        ChessPiece newPiece = new ChessPiece(edit.getTeam(), pieceType, this.chessBoard);
        newPiece.setRotate(this.chessBoard.getRotate());
        this.chessBoard.moveChessPiece(newPiece, null, loc, false, true);
        this.promotionStage.hide();
    }

    public void safeRemoveMove(int index) {
        if (index < 0) { return; }
        this.moveTracker.safeRemove(0);
        if (this.moves.size() >= index) {
            this.moves.remove(index);
        }
        this.currentTurn = this.currentTurn == Team.WHITE ? Team.BLACK : Team.WHITE;
        this.turnStartTime = Instant.now();
        this.chessBoard.resetMoveButtons();
        if (this.rotateGame) {
            this.chessBoard.setOrientation(this.currentTurn);
        }
        this.fireNextTurn();

    }
    public ArrayList<HashMap<Point2D, ChessPiece>> getPastChessBoards() {
        return this.pastBoards;
    }
    public void backwardOneChessBoard() {
        this.chessBoard.resetMoveButtons();
        this.chessBoard.setAndResetAllBoardItemsPlus(this.pastBoards.get(this.pastBoards.size() - 1), this.moves.size() - 1);
        this.pastBoards.remove(this.pastBoards.size() - 1);
    }

    public void setSaveFile(File file) {
        this.saveFile = file;
    }

    public Stage getPromotionStage() {
        return this.promotionStage;
    }
    public Stage getChessBoardStage() {
        return this.chessBoardStage;
    }
    public Stage getMoveTrackerStage() {
        return this.moveTrackerStage;
    }
    public void setMoveTrackerStage(Stage moveTrackerStage) {
        this.moveTrackerStage = moveTrackerStage;
        this.moveTrackerStage.setOnHidden(windowEvent -> this.gameManager.toggleMoveTracker(false));
        this.moveTrackerStage.setOnShown(windowEvent -> this.gameManager.toggleMoveTracker(true));
    }
    public void setChessBoardStage(Stage chessBoardStage) {
        this.chessBoardStage = chessBoardStage;
        this.chessBoardStage.setOnHidden(windowEvent -> this.gameManager.toggleChessBoard(false));
        this.chessBoardStage.setOnShown(windowEvent -> this.gameManager.toggleChessBoard(true));
    }
    public void showSaveDialog() {
        GameSelector selector = new GameSelector(this);
        selector.setBackground(new Background(new BackgroundFill(Color.TRANSPARENT, new CornerRadii(15), Insets.EMPTY)));
        selector.setTextFieldText(this.saveFile.getName());
        Stage stage = new Stage();
        Scene scene = new Scene(selector);
        scene.setFill(null);
        stage.initOwner(this.gameManager.getScene().getWindow());
        stage.initModality(Modality.WINDOW_MODAL);
        stage.initStyle(StageStyle.TRANSPARENT);
        stage.setScene(scene);
        stage.show();
    }
    public void showOpenDialog() {
        this.showOpenDialog(false);
    }
    public void showOpenDialog(boolean disableTop) {
        if (Objects.requireNonNull(Game.saveFolder().listFiles()).length == 0) {
            return;
        }
        GameSelector selector = new GameSelector(null);
        if (disableTop) {
            selector.removeTop();
            selector.getTopHBox().setSpacing(15);
        }
        Stage stage = new Stage();
        selector.setOnClick(chosen -> {
            if (chosen.getPath().length() > 1) {
                Game newGame = null;
                if (chosen.exists()) {
                    newGame = Game.loadFromFolder(chosen);
                } else {
                    newGame = new Game();
                    newGame.setSaveFile(chosen);
                }
                assert newGame != null;
                ((PrettyStage) this.gameManager.getScene().getWindow()).setPane(newGame.getGameManager());
                ((PrettyStage) this.chessBoard.getScene().getWindow()).setPane(newGame.getChessBoard());
                ((PrettyStage) this.moveTracker.getScene().getWindow()).setPane(newGame.getMoveTracker());

                stage.hide();
                this.stop();
            }
        });
        selector.setName("Load");
        selector.setBackground(new Background(new BackgroundFill(Color.TRANSPARENT, new CornerRadii(15), Insets.EMPTY)));
        Scene scene = new Scene(selector);
        scene.setFill(null);
        stage.initOwner(this.gameManager.getScene().getWindow());
        stage.initModality(Modality.WINDOW_MODAL);
        stage.initStyle(StageStyle.TRANSPARENT);
        stage.setScene(scene);
        stage.show();
    }


    public boolean save() throws IOException {
        return save(this.saveFile);
    }
    public void stop() {
        this.gameManager.getScheduledExecutor().shutdown();
        this.gameManager = null;
        this.chessBoard = null;
        this.moveTracker = null;
    }
    public boolean save(File saveTo) throws IOException {
//        if (!saveTo.isDirectory()) {
//            return false;
//        }
//        File images = new File(saveTo.getPath() + "/screenshots");
//        images.mkdir();
        if (!saveTo.exists()) {
            saveTo.mkdir();
        }
        this.saveFile = saveTo;
        File chessBoardFile = new File(saveTo.getPath() + "/defaultboard.chess");
        if (!chessBoardFile.exists()) {
            chessBoardFile.createNewFile();
        }
        FileWriter boardWriter = new FileWriter(chessBoardFile);
        boardWriter.write(this.defaultChessBoard);
        boardWriter.close();

        File gameFile = new File(saveTo.getPath() + "/gameinfo.chess");
        if (!gameFile.exists()) {
            gameFile.createNewFile();
        }
        String gameText = "Game:\n\tTotalLength: " + this.getTurnDuration().toSeconds() + "\n\tTurnLength: ";
        if (this.getTurnDuration() != null) {
            gameText = gameText + this.getTurnDuration().toSeconds();
        } else {
            gameText = gameText + "0";
        }
        gameText = gameText + "\n\tWhiteLength: ";
        if (this.getWhiteTime() != null) {
            if (this.currentTurn == Team.WHITE) {
                gameText = gameText + (this.getWhiteTime().toSeconds() - this.getTurnDuration().toSeconds());
            } else {
                gameText = gameText + this.getWhiteTime().toSeconds();
            }
        } else {
            gameText = gameText + "0";
        }
        gameText = gameText + "\n\tBlackLength: ";
        if (this.getBlackTime() != null) {
            if (this.currentTurn == Team.BLACK) {
                gameText = gameText + (this.getBlackTime().toSeconds() - this.getTurnDuration().toSeconds());
            } else {
                gameText = gameText + this.getBlackTime().toSeconds();
            }
        } else {
            gameText = gameText + "0";
        }

        FileWriter gameWriter = new FileWriter(gameFile);
        gameWriter.write(gameText);
        gameWriter.close();

        if (this.moves.size() > 0) {
            File movesFile = new File(saveTo.getPath() + "/moves.chess");
            if (!movesFile.exists()) {
                movesFile.createNewFile();
            }
            String movesText = "Moves:";
            int loopTime = 0;
            for (Move move : this.moves) {
                loopTime++;
                movesText = movesText + "\n\t" + loopTime + ": from " + move.getFrom().getX() + "-" + move.getFrom().getY() + " to " + move.getTo().getX() + "-" + move.getTo().getY();
            }
            FileWriter movesWriter = new FileWriter(movesFile);
            movesWriter.write(movesText);
            movesWriter.close();
        }

        return true;
    }
}
