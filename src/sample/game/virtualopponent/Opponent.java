package sample.game.virtualopponent;

import javafx.animation.ScaleTransition;
import javafx.application.Platform;
import javafx.geometry.Point2D;
import javafx.util.Duration;
import sample.betterJFX.prettystage.PrettyStage;
import sample.chess.*;
import sample.game.Game;
import sample.game.Move;
import sample.game.NextTurnEvent;
import sample.game.components.Promotion;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Opponent {

    Game game;

    Team team;
    double toX = 0.14;
    double toY = 0.14;
    int time = 120;
    int cycleCount = 6;
    protected boolean enabled = true;
    private NextTurnEvent event;

    public Opponent(Game game, Team team) {
        this.game = game;
        this.team = team;
        this.init(false);
    }
    public Opponent(Game game, Team team, boolean runLater) {
        this.game = game;
        this.team = team;
        this.init(runLater);
    }

    private void init(boolean runLater) {
        this.event = (game, newMove) -> {
            if (this.enabled) {
                if (this.game.getCurrentTurn() == this.team) {
                    if (runLater) {
                        Platform.runLater(() -> this.thisTurn(newMove));
                    } else {
                        this.thisTurn(newMove);
                    }
                } else {
                    this.oppTurn();
                }
            }
        };
        this.game.getNextTurnEventListeners().add(this.event);
        this.game.setOpponent(this);
    }

    public void thisTurn(Move newMove) {

    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
        this.game.nextTurnEvents.remove(this.event);
    }

    public void oppTurn() {
        ChessPiece piece = this.game.getMoves().get(this.game.getMoves().size() - 1).getPiece();
        if (piece != null) {
            ScaleTransition scaleTransition = new ScaleTransition(new Duration(time), piece);
            scaleTransition.setToX(toX);
            scaleTransition.setToY(toY);
            scaleTransition.setAutoReverse(true);
            scaleTransition.setCycleCount(cycleCount);
            scaleTransition.play();
        }
    }

    public ChessBoard getBoard() {
        return this.game.getChessBoard();
    }
    public Game getGame() {
        return this.game;
    }
    public Team getTeam() {
        return this.team;
    }

    public void virtuallyMove(ChessPiece movePiece, Point2D newSpot) {
        this.game.getChessBoard().virtualMoveChessPiece(movePiece, movePiece.getLocation(), newSpot);
    }

    public int goodAttack(ChessPiece piece, Point2D newPoint) {
        ChessPiece takePiece = this.game.getChessBoard().getSpotFromLoc(newPoint).getPiece();
        Point2D oldPoint = piece.getLocation();
        int pieceValue = Chess.getValue(piece);
        int takePieceValue = Chess.getValue(takePiece);

        int oldDanger = inDanger(oldPoint, piece) ? pieceValue : 0;
        int newDanger = (inDanger(newPoint, piece) ? pieceValue : 0) - takePieceValue;

        return oldDanger - (newDanger - 1);
    }

    public boolean testMoveSafety(Point2D moveTo, ChessPiece move, ChessPiece keepSafe) {
        ChessPiece moveBack = this.game.getChessBoard().getSpotFromLoc(moveTo).getPiece();
        Point2D movePieceTo = move.getLocation();
        this.virtuallyMove(move, moveTo);
        boolean returnSafety = testSafety(keepSafe);
        this.virtuallyMove(move, movePieceTo);
        if (moveBack != null) {
            this.virtuallyMove(moveBack, moveTo);
        }
        return returnSafety;
    }

    public boolean testSafety(ChessPiece piece) {
        for (Map.Entry<Point2D, ChessPiece> entry : Chess.getPieceMapForTeam(this.game.getChessBoard(), Chess.flipTeam(this.team)).entrySet()) {
            if (entry.getValue().getMoveBuilder().getMoves().contains(piece.getLocation())) {
                return false;
            }
        }
        return true;
    }

    public int movePieceValue(Point2D moveTo, ChessPiece move, ChessPiece testSafety) {
        ChessPiece moveBack = this.game.getChessBoard().getSpotFromLoc(moveTo).getPiece();
        Point2D movePieceTo = move.getLocation();
        int currentValue = 0;
        boolean inDangerMain = false;
        boolean inDangerSecondary = false;
        if (inDanger(testSafety.getLocation())) {
            inDangerMain = true;
        }
        if (testSafety != move && inDanger(move.getLocation())) {
            inDangerSecondary = true;
        }
        this.virtuallyMove(move, moveTo);
        if (!inDanger(testSafety.getLocation()) && inDangerMain) {
            currentValue = currentValue + Chess.getValue(testSafety);
        }
        if (testSafety != move) {
            if (!inDanger(move.getLocation())) {
                if (inDangerSecondary) {
                    currentValue = currentValue + Chess.getValue(move);
                }
            } else if (!inDangerSecondary) {
                currentValue = currentValue - Chess.getValue(move);
            }
        }
        this.virtuallyMove(move, movePieceTo);
        if (moveBack != null) {
            this.virtuallyMove(moveBack, moveTo);
        }
        return currentValue;
    }


    public boolean inDanger(Point2D point) {
        for (ChessPiece piece : Chess.getPieceMapForTeam(this.game.getChessBoard(), Chess.flipTeam(this.team)).values()) {
            if (piece.getMoveBuilder().getMoves().contains(point)) {
//                System.out.println(point + " is in danger");
                return true;
            }
        }
        return false;
    }

    public boolean inDanger(Point2D point, ChessPiece chessPiece) {
        ChessPiece moveBack = this.game.getChessBoard().getSpotFromLoc(point).getPiece();
        Point2D moveMainBack = chessPiece.getLocation();
        this.game.getChessBoard().moveChessPiece(chessPiece, moveMainBack, point, false, true);
        for (ChessPiece piece : Chess.getPieceMapForTeam(this.game.getChessBoard(), Chess.flipTeam(this.team)).values()) {
            if (piece.getMoveBuilder().getMoves().contains(point)) {
                this.game.getChessBoard().moveChessPiece(chessPiece, chessPiece.getLocation(), moveMainBack, false, true);
                if (moveBack != null) {
                    this.game.getChessBoard().moveChessPiece(moveBack, null, point, false, true);
                }
                return true;
            }
        }
        this.game.getChessBoard().moveChessPiece(chessPiece, chessPiece.getLocation(), moveMainBack, false, true);
        if (moveBack != null) {
            this.game.getChessBoard().moveChessPiece(moveBack, null, point, false, true);
        }
        return false;
    }

    public int dangerValue(ChessPiece piece) {
        if (inDanger(piece.getLocation(), piece)) {
            return Chess.getValue(piece);
        } else {
            return 0;
        }
    }

    public void runBetweenMove(ChessPiece move, Point2D moveTo, Runnable run) {
        ChessPiece moveBack = this.game.getChessBoard().getSpotFromLoc(moveTo).getPiece();
        Point2D oldLoc = move.getLocation();
        this.virtuallyMove(move, moveTo);
        run.run();
        this.virtuallyMove(move, oldLoc);
        if (moveBack != null) {
            this.virtuallyMove(moveBack, moveTo);
        }
    }

    protected void checkPS() {
        if (this.game.getPromotionStage() != null && ((Promotion) ((PrettyStage) this.game.getPromotionStage()).getAddPane()).getTeam() == this.team) {
            this.getGame().promotedPressed(PieceType.QUEEN, ((Promotion) ((PrettyStage) this.game.getPromotionStage()).getAddPane()).getPiece());
        }
    }

    protected int getSmartThreatValue(Team team) {
        ChessBoard board = this.game.getChessBoard();
        int loss = 0;
        ArrayList<Point2D> addedMoves = new ArrayList<>();
        for (Map.Entry<Point2D, ChessPiece> entry : Chess.getPieceMapForTeam(board, Chess.flipTeam(team)).entrySet()) {
            for (Point2D point : entry.getValue().getMoveBuilder().getMoves()) {
                ChessPiece piece = board.getSpotFromLoc(point).getPiece();
                if (piece != null && piece.getTeam() == team && !addedMoves.contains(point)) {
                    loss = loss + Chess.getValue(piece);
                    addedMoves.add(point);
                }
            }
        }
        return loss;
    }

    protected MovePosition generateMovePosition(int depth) {
        return generateMovePosition(depth, this.team);
    }
    protected MovePosition generateMovePosition(int depth, Team team) {
        MovePosition bestPos = null;
        if (team == Team.WHITE) {
            int maxEval = -1000;
            for (ChessPiece piece : Chess.getPieceMapForTeam(this.game.getChessBoard(), team).values()) {
                if (piece.getTeam() == team) {
                    for (Point2D point2D : piece.getMoveBuilder().getMoves()) {
                        ChessPiece moveBack = this.game.getChessBoard().getSpotFromLoc(point2D).getPiece();
                        Point2D previousPoint = piece.getLocation();
                        this.virtuallyMove(piece, point2D);
                        int eval = minimax(depth - 1, 0, 0, Chess.flipTeam(team));
                        this.virtuallyMove(piece, previousPoint);
                        if (moveBack != null) {
                            this.virtuallyMove(moveBack, point2D);
                        }
                        if (eval > maxEval) {
                            bestPos = new MovePosition(piece, point2D);
                            maxEval = eval;
                        }
                    }
                }
            }
            return bestPos;
        } else {
            int minEval = 1000;
            for (ChessPiece piece : Chess.getPieceMapForTeam(this.game.getChessBoard(), team).values()) {
                if (piece.getTeam() == team) {
                    for (Point2D point2D : piece.getMoveBuilder().getMoves()) {
                        ChessPiece moveBack = this.game.getChessBoard().getSpotFromLoc(point2D).getPiece();
                        Point2D previousPoint = piece.getLocation();
                        this.virtuallyMove(piece, point2D);
                        int eval = minimax(depth - 1, 0, 0, Chess.flipTeam(team));
                        this.virtuallyMove(piece, previousPoint);
                        if (moveBack != null) {
                            this.virtuallyMove(moveBack, point2D);
                        }
                        if (eval < minEval) {
                            bestPos = new MovePosition(piece, point2D);
                            minEval = eval;
                        }
                        minEval = Math.min(eval, minEval);
                    }
                }
            }
            return bestPos;
        }
    }

    protected int minimax(int depth, int alpha, int beta, Team team) {

        if (depth == 0) {
            return calculatedBoardValueForTeam(Team.WHITE) - calculatedBoardValueForTeam(Team.BLACK);
        }
        if (team == Team.WHITE) {
            int maxEval = -1000;
            for (ChessPiece piece : Chess.getPieceMapForTeam(this.game.getChessBoard(), team).values()) {
                if (piece.getTeam() == team) {
                    for (Point2D point2D : piece.getMoveBuilder().getMoves()) {
                        ChessPiece moveBack = this.game.getChessBoard().getSpotFromLoc(point2D).getPiece();
                        Point2D previousPoint = piece.getLocation();
                        this.virtuallyMove(piece, point2D);
                        int eval = minimax(depth - 1, alpha, beta, Chess.flipTeam(team));
                        this.virtuallyMove(piece, previousPoint);
                        if (moveBack != null) {
                            this.virtuallyMove(moveBack, point2D);
                        }
                        maxEval = Math.max(eval, maxEval);
                        alpha = Math.max(alpha, eval);
                        if (beta <= alpha) {
                            break;
                        }
                    }
                }
            }
            return maxEval;
        } else {
            int minEval = 1000;
            for (ChessPiece piece : Chess.getPieceMapForTeam(this.game.getChessBoard(), team).values()) {
                if (piece.getTeam() == team) {
                    for (Point2D point2D : piece.getMoveBuilder().getMoves()) {
                        ChessPiece moveBack = this.game.getChessBoard().getSpotFromLoc(point2D).getPiece();
                        Point2D previousPoint = piece.getLocation();
                        this.virtuallyMove(piece, point2D);
                        int eval = minimax(depth - 1, alpha, beta, Chess.flipTeam(team));
                        this.virtuallyMove(piece, previousPoint);
                        if (moveBack != null) {
                            this.virtuallyMove(moveBack, point2D);
                        }
                        minEval = Math.min(eval, minEval);
                        beta = Math.min(beta, eval);
                        if (beta <= alpha) {
                            break;
                        }
                    }
                }
            }
            return minEval;
        }
    }

    // Returns an integer based on the value of the ChessBoard.
    // What this takes into account:
    //   - Piece values
    //   - Endangered pieces and how their protected by other pieces. *1
    //   - *Pieces we can safely attack. *1
    //   - *If the opponent can checkmate us next turn.
    // *Not implemented yet | *1 Only takes into account the highest value
    protected int calculatedBoardValueForTeam(Team teamFor) {
        int value = 0;
        value = value + Chess.getTotalValueForTeam(teamFor, this.game.getChessBoard());

        if (this.game.getChessBoard().getCheckMateStatus(teamFor)) {
            return -999;
        } else if (this.game.getChessBoard().getCheckMateStatus(Chess.flipTeam(teamFor))) {
            return 999;
        }

        int biggestDanger = 0;
        HashMap<Byte, ArrayList<ChessPiece>> endangeredByValue = new HashMap<>(); // Remove highest from value MIGHT NOT BE NEEDED
//         Get all enemy pieces and loop through their attacks
        for (ChessPiece enemyPiece : ((HashMap<Point2D, ChessPiece>) this.game.getChessBoard().getChessPieceMap().clone()).values()) {
            if (enemyPiece.getTeam() != teamFor) {
                for (Point2D loc : enemyPiece.getMoveBuilder().getMoves(false, false)) {
                    // Test if there's a piece at that location
                    ChessPiece atLoc = this.game.getChessBoard().getSpotFromLoc(loc).getPiece();
                    if (atLoc != null) {
                        Byte pieceDanger = (byte) Chess.getValue(atLoc); // The value of the endangered piece, remember highest gets removed.
                        // Go through the pieces protecting that piece, place in endangeredByValue based on the value of the endangered piece - the value of attacking piece
                        for (ChessPiece protectingPiece : ((HashMap<Point2D, ChessPiece>) this.game.getChessBoard().getChessPieceMap().clone()).values()) {
                            if (protectingPiece.getTeam() != teamFor) {
                                ArrayList<Point2D> pieceMoves = protectingPiece.getMoveBuilder().getMoves();
                                if (pieceMoves.contains(loc)) {
                                    pieceDanger = (byte) (pieceDanger - Chess.getValue(enemyPiece));
                                }
                            }
                        }
                        if (pieceDanger > biggestDanger) {
                            biggestDanger = pieceDanger;
                        }
                        // Put into endangeredByValue list MIGHT NOT BE NEEDED
//                        endangeredByValue.computeIfAbsent(pieceDanger, k -> new ArrayList<>());
//                        endangeredByValue.get(pieceDanger).add(atLoc);
                    }
                }
            }
        }
        value = value - biggestDanger;

        byte divideBy = 2;
        for (ChessPiece ourPiece : this.game.getChessBoard().getChessPieceMap().values()) {
            if (ourPiece.getTeam() == teamFor) {
                int attackValue = 0;
                ArrayList<ChessPiece> threatening = new ArrayList<>();
                for (Point2D loc : ourPiece.getMoveBuilder().getMoves(false, false)) {
                    ChessPiece atLoc = this.game.getChessBoard().getPieceFromLoc(loc);
                    if (atLoc != null) {
                        Point2D moveBack = ourPiece.getLocation();
                        this.virtuallyMove(ourPiece, loc);
                        if (Chess.getAllMovesForTeam(this.game.getChessBoard(), true).contains(loc)) {

                        }
                        this.virtuallyMove(ourPiece, moveBack);
                        this.virtuallyMove(atLoc, loc);
                    }
                }
            }
        }

        return value;
    }

    protected ArrayList<ChessPiece> generatePiecesProtectionPoint(Point2D spot) {
        ArrayList<ChessPiece> pieces = new ArrayList<>();
        for (ChessPiece piece : this.game.getChessBoard().getChessPieceMap().values()) {
            if (piece.getTeam() != this.team) {
                if (piece.getMoveBuilder().getMoves().contains(spot)) {
                    pieces.add(piece);
                }
            }
        }
        return pieces;
    }


}
