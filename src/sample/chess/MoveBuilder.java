package sample.chess;

import javafx.geometry.Point2D;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class MoveBuilder {

    ChessBoard board;
    ChessPiece piece;
    ArrayList<MoveSequence> moveSequences;
    ArrayList<ComplexMoveSequence> complexMoveSequences = null;

    public MoveBuilder(ChessBoard chessBoard, ChessPiece chessPiece, ArrayList<MoveSequence> moveSequences) {
        this.board = chessBoard;
        this.piece = chessPiece;
        this.moveSequences = moveSequences;
    }
    public MoveBuilder(ChessBoard chessBoard, ChessPiece chessPiece, ArrayList<MoveSequence> moveSequences, ArrayList<ComplexMoveSequence> complexMoveSequences) {
        this.board = chessBoard;
        this.piece = chessPiece;
        this.moveSequences = moveSequences;
        this.complexMoveSequences = complexMoveSequences;
    }
    public ArrayList<Point2D> getAllRawMoves() {
        ArrayList<Point2D> arrayList = new ArrayList<>(this.getMoves());
        arrayList.addAll(this.getComplexMoves().values());
        arrayList.add(this.getEnPassant());

        return arrayList;
    }
    public ArrayList<Point2D> getMoves() {
        return getMoves(false,true, true);
    }
    public HashMap<ComplexMoveSequence, Point2D> getComplexMoves() {
        HashMap<ComplexMoveSequence, Point2D> moves = new HashMap<>();

        if (this.complexMoveSequences == null) { return moves; }

        for (ComplexMoveSequence complexMoveSequence : this.complexMoveSequences) {
            complexMoveSequence.initSecondPiece();
            Point2D putPoint = getFullyCalculatedMovesFromComplexSequence(complexMoveSequence, false);
            if (putPoint != null) {
                moves.put(complexMoveSequence, putPoint);
            }
        }

        return moves;
    }

    public Point2D getEnPassant() {
        Point2D validMove;
        if (this.piece.getEnPassantTake() != null && this.board.getSpotFromLoc(this.piece.getEnPassantTake()).getPiece().getTeam() == this.piece.getTeam()) {
            return null;
        }
        if (this.piece.getPieceType() == PieceType.PAWN && this.piece.getEnPassant() != null && this.board.getSpotFromLoc(this.piece.getEnPassant()).getPiece() == null) {
            validMove = this.piece.getEnPassant();
        } else {
            return null;
        }
        Point2D moveBack = this.piece.getLocation();
//        this.board.moveChessPiece(this.piece, this.piece.getLocation(), validMove, false, true);
        Chess.virtuallyMove(this.board, this.piece, validMove);
        ArrayList<Point2D> opponentMoves = new ArrayList<>();
        for (Map.Entry<Point2D, ChessPiece> entry : this.board.getChessPieceMap().entrySet()) {
            if (entry.getValue().getTeam() != this.piece.getTeam()) {
                opponentMoves.addAll(entry.getValue().getMoveBuilder().getMoves(true, false));
            }
        }
        Point2D kingLoc = null;
        for (Map.Entry<Point2D, ChessPiece> entry : this.board.getChessPieceMap().entrySet()) {
            if (entry.getValue().getPieceType() == PieceType.KING && entry.getValue().getTeam() == this.piece.getTeam()) {
                kingLoc = entry.getKey();
                break;
            }
        }
        for (Point2D opponentMove : opponentMoves) {
            assert kingLoc != null;
            if (opponentMove.toString().equals(kingLoc.toString())) {
//                this.board.moveChessPiece(this.piece, this.piece.getLocation(), moveBack, false, true);
                Chess.virtuallyMove(this.board, this.piece, moveBack);
                return null;
            }
        }
//        this.board.moveChessPiece(this.piece, this.piece.getLocation(), moveBack, false, true);
        Chess.virtuallyMove(this.board, this.piece, moveBack);

        return validMove;
    }

    public ArrayList<Point2D> getMoves(boolean ignore, boolean getNonAttack) {
        return getMoves(ignore, getNonAttack, true);
    }
    public ArrayList<Point2D> getMoves(boolean ignore, boolean getNonAttack, boolean ignoreTeam) {
        ArrayList<Point2D> validMoves = new ArrayList<>();

        for (MoveSequence currentSequence : moveSequences) {
            ArrayList<ArrayList<Point2D>> arrayListArrayList = getAllMovesFromSequence(currentSequence, ignore, ignoreTeam);
            assert arrayListArrayList != null;
            validMoves.addAll(arrayListArrayList.get(0));
            if (getNonAttack) {
                validMoves.addAll(arrayListArrayList.get(1));
            }
        }

        if (!ignore) {
            if (this.piece.getPieceType() != PieceType.KING) {
                Point2D kingLoc = null;
                for (Map.Entry<Point2D, ChessPiece> entry : this.board.getChessPieceMap().entrySet()) {
                    if (entry.getValue().getPieceType() == PieceType.KING && entry.getValue().getTeam() == this.piece.getTeam()) {
                        kingLoc = entry.getKey();
                        break;
                    }
                }


                Point2D moveBack = this.piece.getLocation();
                ArrayList<Point2D> removePoints = new ArrayList<>();
                for (Point2D move : validMoves) {
                    ChessPiece returnPiece = this.board.getSpotFromLoc(move).getPiece();
                    this.board.virtualMoveChessPiece(this.piece, move);
                    ArrayList<Point2D> opponentMoves = new ArrayList<>();
                    for (Map.Entry<Point2D, ChessPiece> entry : this.board.getChessPieceMap().entrySet()) {
                        if (entry.getValue().getTeam() != this.piece.getTeam()) {
                            opponentMoves.addAll(entry.getValue().getMoveBuilder().getMoves(true, false));
                        }
                    }
                    for (Point2D opponentMove : opponentMoves) {
                        assert kingLoc != null;
                        if (opponentMove.toString().equals(kingLoc.toString())) {
                            removePoints.add(move);
                        }
                    }
                    if (returnPiece != null) {
                        this.board.virtualMoveChessPiece(returnPiece, null, move);
                    }
                }
                validMoves.removeAll(removePoints);
                this.board.virtualMoveChessPiece(this.piece, this.piece.getLocation(), moveBack);
            } else {
                ArrayList<Point2D> removePoints = new ArrayList<>();
                Point2D moveBack = this.piece.getLocation();
                for (Point2D move : validMoves) {
                    ChessPiece returnPiece = this.board.getSpotFromLoc(move).getPiece();
                    this.board.virtualMoveChessPiece(this.piece, this.piece.getLocation(), move);
                    ArrayList<Point2D> opponentMoves = new ArrayList<>();
                    for (Map.Entry<Point2D, ChessPiece> entry : this.board.getChessPieceMap().entrySet()) {
                        if (entry.getValue().getTeam() != this.piece.getTeam()) {
                            ArrayList<Point2D> addMoves = entry.getValue().getMoveBuilder().getMoves(true, false);
                            opponentMoves.addAll(addMoves);
                        }
                    }
                    for (Point2D opponentMove : opponentMoves) {
                        if (opponentMove.toString().equals(move.toString())) {
                            removePoints.add(move);
                        }
                    }
                    if (returnPiece != null) {
                        this.board.virtualMoveChessPiece(returnPiece, null, move);
                    }
                }

                validMoves.removeAll(removePoints);
                this.board.virtualMoveChessPiece(this.piece, this.piece.getLocation(), moveBack);
            }
        }

        return validMoves;
    }
    private Point2D getFullyCalculatedMovesFromComplexSequence(ComplexMoveSequence sequence, boolean ignore) {
        Point2D validMoves;
        if (ignore && sequence.getKingSafety()) { return null; }
        if (sequence.getSecondPiece() == null) { return null; }
        if (sequence.getSecondPiece() != null && ((sequence.getMoveSequence().getRequireNeverMove() && !this.piece.getNeverMoved()) || (sequence.getSecondPieceRequireNeverMoved() && !sequence.getSecondPiece().getNeverMoved()))) {
            return null;
        }

        Point2D thisPoint = this.piece.getLocation();
        Point2D secondaryPoint = sequence.getMoveSecondTo();
        Point2D newFirstPoint = new Point2D(
                thisPoint.getX() + sequence.getMoveSequence().getPoint().getX(),
                thisPoint.getY() + sequence.getMoveSequence().getPoint().getY()
        );

        ArrayList<Point2D> betweenPoints = new ArrayList<>();
        int bigLoopNum = (int) newFirstPoint.getX(); int smallLoopNum = (int) secondaryPoint.getX();
        if (secondaryPoint.getX() > newFirstPoint.getX()) { bigLoopNum = (int) secondaryPoint.getX(); smallLoopNum = (int) newFirstPoint.getX(); }
        for (int i = (smallLoopNum + 1); i < bigLoopNum; i++) {
            Point2D addPoint = new Point2D(i, newFirstPoint.getY());
            betweenPoints.add(addPoint);

        }

        if (this.board.getSpotFromLoc(newFirstPoint).getPiece() == null && this.board.getSpotFromLoc(secondaryPoint).getPiece() == null) {
            for (Point2D point : betweenPoints) {
                if (this.board.getSpotFromLoc(point).getPiece() != null) {
                    return null;
                }
            }
            validMoves = newFirstPoint;
        } else {
            return null;
        }

        ArrayList<Point2D> opponentMoves = new ArrayList<>();
        for (Map.Entry<Point2D, ChessPiece> entry : this.board.getChessPieceMap().entrySet()) {
            if (entry.getValue().getTeam() != this.piece.getTeam()) {
                opponentMoves.addAll(entry.getValue().getMoveBuilder().getMoves(true, false));
            }
        }
        if (sequence.getKingSafety()) {
            for (Point2D opponentMove : opponentMoves) {
                for (Point2D betweenMove : betweenPoints) {
                    if (opponentMove.toString().equals(betweenMove.toString())) {
                        return null;
                    }
                }
            }
            for (Point2D opponentPoint : opponentMoves) {
                if (validMoves.toString().equals(opponentPoint.toString())) {
                    return null;
                }
            }
        }

        return newFirstPoint;
    }
    private ArrayList<ArrayList<Point2D>> getAllMovesFromSequence(MoveSequence sequence, boolean ignore) {
        return getAllMovesFromSequence(sequence, ignore, true);
    }
    private ArrayList<ArrayList<Point2D>> getAllMovesFromSequence(MoveSequence sequence, boolean ignore, boolean ignoreTeam) {
        ArrayList<Point2D> attack = new ArrayList<>();
        ArrayList<Point2D> notAttack = new ArrayList<>();

        if (sequence.getRepeat()) {
            Point2D currentPoint = this.piece.getLocation();
            do {
                Point2D addPoint = new Point2D(
                        currentPoint.getX() + sequence.getPoint().getX(),
                        currentPoint.getY() + sequence.getPoint().getY()
                );
                if (!this.board.getIsValidLoc(addPoint)) {
                    break; }
                if (this.board.getSpotFromLoc(addPoint).getPiece() != null && this.board.getSpotFromLoc(addPoint).getPiece().getTeam() == this.piece.getTeam() && ignoreTeam) {
                    break;
                }
                if (!addPoint.toString().equals(currentPoint.toString())) {
                    if (sequence.getAttack()) {
                        attack.add(addPoint);
                    } else {
                        notAttack.add(addPoint);
                    }
                    currentPoint = addPoint;
                    if (this.board.getSpotFromLoc(addPoint).getPiece() != null && (this.board.getSpotFromLoc(addPoint).getPiece().getTeam() != this.piece.getTeam() || (this.board.getSpotFromLoc(addPoint).getPiece().getTeam() == this.piece.getTeam() && !ignoreTeam))) {
                        break;
                    }
                }
            } while (this.board.getSpotFromLoc(currentPoint) != null);
        } else {
            Point2D thisPoint = this.piece.getLocation();
            if (thisPoint == null) {
                return null;
            }
            Point2D addPoint = new Point2D(
                    thisPoint.getX() + sequence.getPoint().getX(),
                    thisPoint.getY() + sequence.getPoint().getY()
            );
            if (sequence.getRequirePiece()) {
                if (ignore || (this.board.getIsValidLoc(addPoint) && (this.board.getSpotFromLoc(addPoint).getPiece() != null && this.board.getSpotFromLoc(addPoint).getPiece().getTeam() != piece.getTeam()))) {
                    if (sequence.getAttack()) {
                        attack.add(addPoint);
                    } else {
                        notAttack.add(addPoint);
                    }
                }
            } else if (sequence.getRequireNeverMove()) {
                if (this.piece.getNeverMoved()) {
                    if (this.board.getIsValidLoc(addPoint) && !(this.board.getSpotFromLoc(addPoint).getPiece() != null && (this.board.getSpotFromLoc(addPoint).getPiece().getTeam() == this.piece.getTeam() && ignoreTeam))) {
                        if (this.piece.getPieceType() != PieceType.PAWN || !(this.board.getSpotFromLoc(addPoint).getPiece() != null && this.board.getSpotFromLoc(addPoint).getPiece().getTeam() != this.piece.getTeam())) {
                            if (!(this.piece.getPieceType() == PieceType.PAWN && this.piece.getTeam() == Team.WHITE && (this.board.getSpotFromLoc(new Point2D(this.piece.getLocation().getX(), this.piece.getLocation().getY() - 1)).getPiece() != null))) {
                                if (!(this.piece.getPieceType() == PieceType.PAWN && this.piece.getTeam() == Team.BLACK && (this.board.getSpotFromLoc(new Point2D(this.piece.getLocation().getX(), this.piece.getLocation().getY() + 1)).getPiece() != null))) {
                                    if (sequence.getAttack()) {
                                        attack.add(addPoint);
                                    } else {
                                        notAttack.add(addPoint);
                                    }
                                }
                            }
//                            if (this.piece.getPieceType() == PieceType.PAWN && this.piece.getTeam() == Team.WHITE && (this.board.getSpotFromLoc(new Point2D(this.piece.getLocation().getX(), this.piece.getLocation().getY() - 1)).getPiece() != null)) {
//
//                            } else if (this.piece.getPieceType() == PieceType.PAWN && this.piece.getTeam() == Team.BLACK && (this.board.getSpotFromLoc(new Point2D(this.piece.getLocation().getX(), this.piece.getLocation().getY() + 1)).getPiece() != null)) {
//
//                            } else {
//                                if (sequence.getAttack()) {
//                                    attack.add(addPoint);
//                                } else {
//                                    notAttack.add(addPoint);
//                                }
//                            }
                        }
                    }
                }
            }else if ((this.board.getIsValidLoc(addPoint) && !(this.board.getSpotFromLoc(addPoint).getPiece() != null && this.board.getSpotFromLoc(addPoint).getPiece().getTeam() == this.piece.getTeam()))) {
                if (this.piece.getPieceType() != PieceType.PAWN || !(this.board.getSpotFromLoc(addPoint).getPiece() != null && this.board.getSpotFromLoc(addPoint).getPiece().getTeam() != this.piece.getTeam())) {
                    if (sequence.getAttack()) {
                        attack.add(addPoint);
                    } else {
                        notAttack.add(addPoint);
                    }
                }
            }
        }

        ArrayList<ArrayList<Point2D>> returnArray = new ArrayList<>();
        returnArray.add(attack);
        returnArray.add(notAttack);
        return returnArray;
    }

    private MoveSequence getMoveSequence(Point2D input) {
        for (MoveSequence moveSequence : moveSequences) {
            if (moveSequence.getPoint() == input) {
                return moveSequence;
            }
        }
        return null;
    }

}
