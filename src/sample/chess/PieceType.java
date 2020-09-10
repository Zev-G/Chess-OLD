package sample.chess;

public enum PieceType {
    PAWN,
    KNIGHT,
    BISHOP,
    ROOK,
    QUEEN,
    KING;

    PieceType() {
    }

    @Override
    public String toString() {
        if (this.equals(PieceType.PAWN)) {
            return "PAWN";
        } else if (this.equals(PieceType.KNIGHT)) {
            return "KNIGHT";
        } else if (this.equals(PieceType.BISHOP)) {
            return "BISHOP";
        } else if (this.equals(PieceType.ROOK)) {
            return "ROOK";
        } else if (this.equals(PieceType.QUEEN)) {
            return "QUEEN";
        } else if (this.equals(PieceType.KING)) {
            return "KING";
        } else {
            return "";
        }
    }


//    public static PieceType valueOf(String string) {
//        if (string.contains("PAWN")) {
//            return PieceType.PAWN;
//        }
//        if (string.contains("KNIGHT")) {
//            return PieceType.KNIGHT;
//        }
//        if (string.contains("BISHOP")) {
//            return PieceType.BISHOP;
//        }
//        if (string.contains("ROOK")) {
//            return PieceType.ROOK;
//        }
//        if (string.contains("QUEEN")) {
//            return PieceType.QUEEN;
//        }
//        if (string.contains("KING")) {
//            return PieceType.KING;
//        }
//        return null;
//    }
}
