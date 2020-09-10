package sample.chess;

import javafx.geometry.Point2D;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public final class Chess {

    public static File getGameFile() {
        return new File(System.getenv("LOCALAPPDATA") + "/ZevChess");
    }
    public static String readFile(File file) {
        try {
            StringBuilder text = new StringBuilder();
            Scanner sc = new Scanner(file);
            while (sc.hasNextLine()) {
                text.append(sc.nextLine()).append("\n");
            }
            return text.toString();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return "";
    }

    public static ArrayList<Point2D> getAllMovesForTeam(ChessBoard board, boolean attack) {
        ArrayList<Point2D> moves = new ArrayList<>();
        ArrayList<ChessPiece> pieces = new ArrayList<>(board.getChessPieceMap().values());
        for (ChessPiece piece : pieces) {
            moves.addAll(piece.getMoveBuilder().getMoves(false, attack));
        }
        return moves;
    }

    public static Team flipTeam(Team flipFor) {
        return flipFor == Team.WHITE ? Team.BLACK : Team.WHITE;
    }
    public static HashMap<Point2D, ChessPiece> getPieceMapForTeam(ChessBoard getFrom, Team teamFor) {
        HashMap<Point2D, ChessPiece> point2DHashMap = new HashMap<>();
        for (Map.Entry<Point2D, ChessPiece> entry : getFrom.getChessPieceMap().entrySet()) {
            if (entry.getValue().getTeam() == teamFor) {
                point2DHashMap.put(entry.getKey(), entry.getValue());
            }
        }
        return point2DHashMap;
    }
    public static Point2D getKingLoc(ChessBoard getFrom, Team teamOf) {
        for (Map.Entry<Point2D, ChessPiece> entry : getFrom.chessPieceMap.entrySet()) {
            if (entry.getValue().getTeam() == teamOf && entry.getValue().getPieceType() == PieceType.KING) {
                return entry.getKey();
            }
        }
        return null;
    }
    public static int getValue(ChessPiece piece) { return getValue(piece.getPieceType()); }
    public static int getValue(PieceType pieceType) {
        if (pieceType == PieceType.PAWN) {
            return 1;
        }
        if (pieceType == PieceType.KNIGHT || pieceType == PieceType.BISHOP) {
            return 3;
        }
        if (pieceType == PieceType.ROOK) {
            return 5;
        }
        if (pieceType == PieceType.QUEEN) {
            return 9;
        }
        if (pieceType == PieceType.KING) {
            return 3;
        }
        return 0;
    }
    public static PieceType compare(PieceType pieceType1, PieceType pieceType2) {
        if (getValue(pieceType1) == getValue(pieceType2)) return null;
        if (getValue(pieceType1) > getValue(pieceType2)) {
            return pieceType1;
        } else {
            return pieceType2;
        }
    }
    public static int compare(int pieceType1, int pieceType2) {
        return pieceType1 - pieceType2;
    }
    public static int getTotalValueForTeam(Team team, ChessBoard board) {
        int value = 0;
        for (ChessPiece piece : board.getChessPieceMap().values()) {
            if (piece.getTeam() == team) {
                value = value + Chess.getValue(piece);
            }
        }
        return value;
    }
    public static int getTeamThreatValue(ChessBoard board, Team team) {
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
    public static void virtuallyMove(ChessBoard forBoard, ChessPiece movePiece, Point2D newSpot) {
        forBoard.virtualMoveChessPiece(movePiece, movePiece.getLocation(), newSpot);
    }

}
