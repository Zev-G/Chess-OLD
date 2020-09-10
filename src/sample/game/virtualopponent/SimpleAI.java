package sample.game.virtualopponent;

import javafx.geometry.Point2D;
import sample.betterJFX.prettystage.PrettyStage;
import sample.chess.*;
import sample.game.Game;
import sample.game.Move;
import sample.game.components.Promotion;

import java.util.*;

public class SimpleAI extends Opponent {

//    private Game game;
//    private Team team;

    public SimpleAI(Game game, Team team) {
        super(game, team);
//        this.game = game;
//        this.team = team;
    }
    public SimpleAI(Game game, Team team, boolean nextTurn) {
        super(game, team, nextTurn);
//        this.game = game;
//        this.team = team;
    }

    @Override
    public void thisTurn(Move newMove) {
        //Pass to Opponent.java
        super.thisTurn(newMove);

        if (this.enabled) {
            //Promotion Stage checking
            if (this.game.getPromotionStage() != null && ((Promotion) ((PrettyStage) this.game.getPromotionStage()).getAddPane()).getTeam() == this.team) {
                this.getGame().promotedPressed(PieceType.QUEEN, ((Promotion) ((PrettyStage) this.game.getPromotionStage()).getAddPane()).getPiece());
            }
            if (this.game.getPromotionStage() != null && this.game.getPromotionStage().isShowing()) {
                this.game.getPromotionStage().setOnHidden(windowEvent -> this.thisTurn(newMove));
                return;
            }


            //Initializing important variables
            HashMap<ChessPiece, ArrayList<Point2D>> compMap = new HashMap<>();
            HashMap<ChessPiece, ArrayList<Point2D>> compOppMap = new HashMap<>();
            ArrayList<Point2D> rawOppMoves = new ArrayList<>();

//        HashMap<PieceType, ArrayList<ChessPiece>> pieceTypeHashMap = new HashMap<>();
//        HashMap<PieceType, ArrayList<ChessPiece>> oppPieceTypeHashMap = new HashMap<>();

            //Update values of important variables
            HashMap<Point2D, ChessPiece> clonedHashMap = (HashMap<Point2D, ChessPiece>) this.game.getChessBoard().getChessPieceMap().clone();
            for (Map.Entry<Point2D, ChessPiece> entry : clonedHashMap.entrySet()) {
                if (entry.getValue().getTeam() == this.team) {
                    ArrayList<Point2D> addMoves = new ArrayList<>(entry.getValue().getMoveBuilder().getAllRawMoves());
                    addMoves.removeAll(Collections.singleton(null));
                    if (addMoves.size() > 0) {
                        compMap.put(entry.getValue(), addMoves);
                    }
                } else {
                    ArrayList<Point2D> addMoves = new ArrayList<>(entry.getValue().getMoveBuilder().getAllRawMoves());
                    addMoves.removeAll(Collections.singleton(null));
                    if (addMoves.size() > 0) {
                        compOppMap.put(entry.getValue(), addMoves);
                    }
                }
            }

            //Easier access for Opponent's moves
            for (ArrayList<Point2D> point2DS : compOppMap.values()) {
                rawOppMoves.addAll(point2DS);
            }


            //Attempt to check mate
            for (Map.Entry<ChessPiece, ArrayList<Point2D>> entry : compMap.entrySet()) {
                for (Point2D currentMove : entry.getValue()) {
                    Point2D moveBack = entry.getKey().getLocation();
                    ChessPiece moveBackPiece = this.game.getChessBoard().getSpotFromLoc(currentMove).getPiece();
                    this.virtuallyMove(entry.getKey(), currentMove);
                    if (this.game.getChessBoard().getCheckMateStatus(Chess.flipTeam(this.team))) {
                        this.virtuallyMove(entry.getKey(), moveBack);
                        if (moveBackPiece != null) {
                            this.virtuallyMove(moveBackPiece, currentMove);
                        }
                        System.out.println("Moved because: Check Mate");
                        this.game.getChessBoard().moveChessPiece(entry.getKey(), moveBack, currentMove);
                        this.checkPS();
                        return;
                    } else {
                        this.virtuallyMove(entry.getKey(), moveBack);
                        if (moveBackPiece != null) {
                            this.virtuallyMove(moveBackPiece, currentMove);
                        }
                    }
                }
            }

            //Get a list of spots where the opponent can't check mate us next turn (very useful later on)
            HashMap<ChessPiece, ArrayList<Point2D>> kingSafety = new HashMap<>();
            for (Map.Entry<ChessPiece, ArrayList<Point2D>> entry : compMap.entrySet()) {
                kingSafety.put(entry.getKey(), (ArrayList<Point2D>) entry.getValue().clone());
            }
            int lt = 0;
            for (Map.Entry<ChessPiece, ArrayList<Point2D>> entry : compMap.entrySet()) {
                for (Point2D currentMove : entry.getValue()) {
                    Point2D moveBack = entry.getKey().getLocation();
                    ChessPiece moveBackPiece = this.game.getChessBoard().getSpotFromLoc(currentMove).getPiece();
                    this.virtuallyMove(entry.getKey(), currentMove);
                    Point2D kingLoc = Chess.getKingLoc(this.game.getChessBoard(), this.team);
                    boolean stopIf = false;
                    for (Map.Entry<Point2D, ChessPiece> entry1 : Chess.getPieceMapForTeam(this.game.getChessBoard(), Chess.flipTeam(this.team)).entrySet()) {
                        if (!stopIf) {
                            for (Point2D currentOppMove : entry1.getValue().getMoveBuilder().getMoves(false, false)) {
                                Point2D moveOppPieceBack = entry1.getValue().getLocation();
                                ChessPiece pieceThatWasThere = this.game.getChessBoard().getSpotFromLoc(currentOppMove).getPiece();
                                this.virtuallyMove(entry1.getValue(), currentOppMove);
                                if (this.game.getChessBoard().getCheckMateStatus(this.team)) {
                                    kingSafety.get(entry.getKey()).remove(currentMove);
                                    this.virtuallyMove(entry1.getValue(), moveOppPieceBack);
                                    if (pieceThatWasThere != null) {
                                        this.virtuallyMove(pieceThatWasThere, currentOppMove);
                                    }
                                    stopIf = true;
                                    break;
                                }
                                this.virtuallyMove(entry1.getValue(), moveOppPieceBack);
                                if (pieceThatWasThere != null) {
                                    this.virtuallyMove(pieceThatWasThere, currentOppMove);
                                }
                            }
                        }
                    }

                    this.virtuallyMove(entry.getKey(), moveBack);
                    if (moveBackPiece != null) {
                        this.virtuallyMove(moveBackPiece, currentMove);
                    }
                }
            }
            if (kingSafety.isEmpty()) {
                System.out.println("Can be checkmated nt");
            }

            //Getting new dangered piece move
            ArrayList<ChessPiece> endangeredPieces = new ArrayList<>();
            ArrayList<ChessPiece> pieces = new ArrayList<>(this.game.getChessBoard().getChessPieceMap().values());
            HashMap<ChessPiece, ChessPiece> moveThatAttacksX = new HashMap<>(); //First value is the piece that is being attacked
            for (ChessPiece pce : pieces) {
                assert pce.getTeam() != this.team;
                for (Point2D attack : pce.getMoveBuilder().getMoves()) {
                    if (this.game.getChessBoard().getChessPieceMap().get(attack) != null && this.game.getChessBoard().getChessPieceMap().get(attack).getTeam() == this.team) {
                        ChessPiece piece = this.game.getChessBoard().getChessPieceMap().get(attack);
                        assert piece != null && piece.getPieceType() != PieceType.KING;
                        endangeredPieces.add(piece);
                        if (moveThatAttacksX.get(piece) == null || Chess.getValue(moveThatAttacksX.get(piece)) < Chess.getValue(pce)) {
                            moveThatAttacksX.put(piece, pce);
                        }
                    }
                }
            }
            HashMap<Integer, ArrayList<ChessPiece>> orderedByPieceLvl = new HashMap<>();
            for (ChessPiece piece : endangeredPieces) {
                Integer loc = Chess.getValue(piece);
                orderedByPieceLvl.computeIfAbsent(loc, k -> new ArrayList<>());
                orderedByPieceLvl.get(loc).add(piece);
            }
            ArrayList<Integer> order = new ArrayList<>();
            order.add(9);
            order.add(5);
            order.add(3);
            order.add(1);
            HashMap<Integer, HashMap<ChessPiece, Point2D>> moveValueMap = new HashMap<>();
            HashMap<ArrayList<Point2D>, ChessPiece> attackOwn = new HashMap<>();
            for (ChessPiece piece : ((HashMap<Point2D, ChessPiece>) this.game.getChessBoard().getChessPieceMap().clone()).values()) {
                if (piece.getTeam() == this.team) {
                    ArrayList<Point2D> addMoves = piece.getMoveBuilder().getMoves(false, true, false);
                    attackOwn.put(addMoves, piece);
                }
            }
            ArrayList<Point2D> allAttackOwn = new ArrayList<>();
            attackOwn.keySet().forEach(allAttackOwn::addAll);
            for (Integer i : order) {
                if (orderedByPieceLvl.get(i) != null) {
                    for (ChessPiece piece : orderedByPieceLvl.get(i)) {
                        if (allAttackOwn.contains(piece.getLocation())) {
                            if (Chess.getValue(moveThatAttacksX.get(piece)) >= Chess.getValue(piece)) {
                                System.out.println("hmm " + Chess.getValue(moveThatAttacksX.get(piece)) + " " + Chess.getValue(piece) + " " + piece.getPieceType() + " " + piece.getLocation());
                                continue;
                            }
                        }
                        for (Map.Entry<ChessPiece, ArrayList<Point2D>> entry : ((HashMap<ChessPiece, ArrayList<Point2D>>) kingSafety.clone()).entrySet()) {
                            for (Point2D point : entry.getValue()) {
                                Integer moveValue = this.movePieceValue(point, entry.getKey(), piece);
                                if (moveValue > 0) {
                                    moveValueMap.computeIfAbsent(moveValue, k -> new HashMap<>());
                                    moveValueMap.get(moveValue).put(entry.getKey(), point);
                                }
                            }
                        }
                    }
                }
            }


            //Setting up take pieces variables
            HashMap<Integer, ChessPiece> pieceHashMap = new HashMap<>();
            Point2D pawnMove = null;
            int pawnValue = -100;
            Point2D knightMove = null;
            int knightValue = -100;
            Point2D bishopMove = null;
            int bishopValue = -100;
            Point2D rookMove = null;
            int rookValue = -100;
            Point2D queenMove = null;
            int queenValue = -100;
            boolean go = false;
            for (Map.Entry<ChessPiece, ArrayList<Point2D>> entry : kingSafety.entrySet()) {
                for (Point2D point2D : entry.getValue()) {
                    if (this.game.getChessBoard().getSpotFromLoc(point2D).getPiece() != null) {
                        go = true;
                        PieceType type = this.game.getChessBoard().getSpotFromLoc(point2D).getPiece().getPieceType();
                        int newValue = this.goodAttack(entry.getKey(), point2D);
//                    System.out.println("New Value: " + newValue + " (" + entry.getKey().getPieceType() + ")");
                        if (newValue >= 0) {
                            if (type == PieceType.PAWN) {
                                if (newValue > pawnValue) {
                                    pawnMove = point2D;
                                    pieceHashMap.put(0, entry.getKey());
                                } else {
                                    System.out.println("Bad move...");
                                }
                                pawnValue = newValue;
                            } else if (type == PieceType.KNIGHT) {
                                if (newValue > knightValue) {
                                    System.out.println(newValue - knightValue);
                                    knightMove = point2D;
                                    pieceHashMap.put(1, entry.getKey());
                                    knightValue = newValue;
                                } else {
                                    System.out.println("Bad move...");
                                }
                            } else if (type == PieceType.BISHOP) {
                                if (newValue > bishopValue) {
                                    System.out.println(newValue - bishopValue);
                                    bishopMove = point2D;
                                    pieceHashMap.put(2, entry.getKey());
                                    bishopValue = newValue;
                                } else {
                                    System.out.println("Bad move...");
                                }
                            } else if (type == PieceType.ROOK) {
                                if (newValue > rookValue) {
                                    System.out.println(newValue - rookValue);
                                    rookMove = point2D;
                                    pieceHashMap.put(3, entry.getKey());
                                    rookValue = newValue;
                                } else {
                                    System.out.println("Bad move...");
                                }
                            } else if (type == PieceType.QUEEN) {
                                if (newValue > queenValue) {
                                    System.out.println(newValue - queenValue);
                                    queenValue = newValue;
                                    queenMove = point2D;
                                    pieceHashMap.put(4, entry.getKey());
                                } else {
                                    System.out.println("Bad move...");
                                }
                            }
                        }
                    }
                }

            }

            if (go) {
                if (queenMove != null) {
                    if (queenValue < rookValue) {
                        queenMove = null;
                    }
                } else if (rookMove != null) {
                    if (rookValue < bishopValue) {
                        rookMove = null;
                    }
                } else if (bishopMove != null) {
                    if (bishopValue < knightValue) {
                        bishopMove = null;
                    }
                } else if (knightMove != null) {
                    if (knightValue < pawnValue) {
                        knightMove = null;
                    }
                }
                if (queenMove != null) {
                    Integer putLocation = queenValue;
                    moveValueMap.computeIfAbsent(putLocation, k -> new HashMap<>());
                    moveValueMap.get(putLocation).put(pieceHashMap.get(4), queenMove);
                } else if (rookMove != null) {
                    Integer putLocation = rookValue;
                    moveValueMap.computeIfAbsent(putLocation, k -> new HashMap<>());
                    moveValueMap.get(putLocation).put(pieceHashMap.get(3), rookMove);
                } else if (bishopMove != null) {
                    Integer putLocation = bishopValue;
                    moveValueMap.computeIfAbsent(putLocation, k -> new HashMap<>());
                    moveValueMap.get(putLocation).put(pieceHashMap.get(2), bishopMove);
                } else if (knightMove != null) {
                    Integer putLocation = knightValue;
                    moveValueMap.computeIfAbsent(putLocation, k -> new HashMap<>());
                    moveValueMap.get(putLocation).put(pieceHashMap.get(1), knightMove);
                } else if (pawnMove != null) {
                    Integer putLocation = pawnValue;
                    moveValueMap.computeIfAbsent(putLocation, k -> new HashMap<>());
                    moveValueMap.get(putLocation).put(pieceHashMap.get(0), pawnMove);
                }
            }


            //Parse down best moves
            HashMap<Integer, HashMap<ChessPiece, Point2D>> dupeMVM = new HashMap<>();
            for (Map.Entry<Integer, HashMap<ChessPiece, Point2D>> entry : moveValueMap.entrySet()) {
                dupeMVM.put(entry.getKey(), (HashMap<ChessPiece, Point2D>) entry.getValue().clone());
            }
            for (Integer currentValue : dupeMVM.keySet()) {
                for (Map.Entry<ChessPiece, Point2D> entry : dupeMVM.get(currentValue).entrySet()) {
                    int threatBefore = Chess.getTeamThreatValue(this.game.getChessBoard(), this.team);
                    ChessPiece moveBack = this.game.getChessBoard().getSpotFromLoc(entry.getValue()).getPiece();
                    Point2D moveTo = entry.getKey().getLocation();
                    this.virtuallyMove(entry.getKey(), entry.getValue());
                    int threatAfter = Chess.getTeamThreatValue(this.game.getChessBoard(), this.team);
                    this.virtuallyMove(entry.getKey(), moveTo);
                    if (moveBack != null) {
                        this.virtuallyMove(moveBack, entry.getValue());
                    }
                    if (threatAfter > threatBefore) {
                        Integer put = currentValue - threatAfter + threatAfter;
                        moveValueMap.get(currentValue).remove(entry.getKey());
                        moveValueMap.computeIfAbsent(put, k -> new HashMap<>());
                        moveValueMap.get(put).put(entry.getKey(), entry.getValue());
                    }
                }
            }

            //Doing best move
            Integer highest = null;
            for (Integer currentValue : moveValueMap.keySet()) {
                if (highest == null) {
                    highest = currentValue;
                }
                if (currentValue > highest) {
                    highest = currentValue;
                }
            }
            //Do random of highest moves
            if (highest != null && highest > 0) {
                System.out.println("Moved because highest move (" + highest + ")");
                ArrayList<ChessPiece> topPieces = new ArrayList<>(moveValueMap.get(highest).keySet());
                Integer rand = ((int) (Math.random() * topPieces.size()));
                this.game.getChessBoard().moveChessPiece(topPieces.get(rand), topPieces.get(rand).getLocation(), moveValueMap.get(highest).get(topPieces.get(rand)));
                this.checkPS();
                return;
            }

            //Get a random safe location
            //Setup completeClone
            HashMap<ChessPiece, ArrayList<Point2D>> completeClone = (HashMap<ChessPiece, ArrayList<Point2D>>) kingSafety.clone();
            //Setup removePoints
            HashMap<ChessPiece, ArrayList<Point2D>> removePoints = new HashMap<>();
            for (Map.Entry<ChessPiece, ArrayList<Point2D>> entry : kingSafety.entrySet()) {
                removePoints.put(entry.getKey(), new ArrayList<>());
            }
            //Parse out bad moves
            for (Map.Entry<ChessPiece, ArrayList<Point2D>> entry : completeClone.entrySet()) {
                for (Point2D point : entry.getValue()) {
                    ChessPiece moveBack = this.game.getChessBoard().getSpotFromLoc(point).getPiece();
                    Point2D currentPoint = entry.getKey().getLocation();
                    this.virtuallyMove(entry.getKey(), point);
                    if (moveBack != null) {
                        removePoints.get(entry.getKey()).add(point);
                    } else
                        for (Map.Entry<Point2D, ChessPiece> entry1 : Chess.getPieceMapForTeam(this.game.getChessBoard(), Chess.flipTeam(this.team)).entrySet()) {
                            if (entry1.getValue().getMoveBuilder().getMoves(false, false).contains(point) && (Chess.getValue(entry.getKey()) > 2 || this.game.getChessBoard().getChessPieceMap().values().size() < 25)) {
                                removePoints.get(entry.getKey()).add(point);
                            }
                        }
                    this.virtuallyMove(entry.getKey(), currentPoint);
                    if (moveBack != null) {
                        this.virtuallyMove(moveBack, point);
                    }
                }
            }
            //Remove bad moves
            for (Map.Entry<ChessPiece, ArrayList<Point2D>> entry : removePoints.entrySet()) {
                kingSafety.get(entry.getKey()).removeAll(entry.getValue());
                if (kingSafety.get(entry.getKey()).isEmpty()) {
                    kingSafety.remove(entry.getKey());
                }
            }

            HashMap<ChessPiece, Point2D> advancePawnMap = new HashMap<>();
            for (Map.Entry<ChessPiece, ArrayList<Point2D>> entry : kingSafety.entrySet()) {
                if (entry.getKey().getPieceType() == PieceType.PAWN) {
                    for (Point2D point : entry.getValue()) {
                        int beforeThreat = Chess.getTeamThreatValue(this.game.getChessBoard(), this.team);
                        ChessPiece moveBack = this.game.getChessBoard().getSpotFromLoc(point).getPiece();
                        Point2D movePieceTo = entry.getKey().getLocation();
                        this.virtuallyMove(entry.getKey(), point);
                        int afterThreat = Chess.getTeamThreatValue(this.game.getChessBoard(), this.team);
                        this.virtuallyMove(entry.getKey(), movePieceTo);
                        if (moveBack != null) {
                            this.virtuallyMove(moveBack, point);
                        }
                        if (beforeThreat - afterThreat >= 0) {
                            advancePawnMap.put(entry.getKey(), point);
                        }
                    }
                }
            }
            if (!advancePawnMap.isEmpty()) {
                int randomPawn = ((int) (Math.random() * (advancePawnMap.keySet().size() - 1)));
                int loopTimesSafe = 0;
                for (Map.Entry<ChessPiece, Point2D> entry : advancePawnMap.entrySet()) {
                    if (loopTimesSafe == randomPawn) {
                        System.out.println("Moved because: Advance Pawn");
                        this.game.getChessBoard().moveChessPiece(entry.getKey(), entry.getKey().getLocation(), entry.getValue());
                        this.checkPS();
                        return;
                    }
                    loopTimesSafe++;
                }
            }

            if (kingSafety.size() > 0) {
                //Do safe random move
                int randomSafe = ((int) (Math.random() * (kingSafety.keySet().size() - 1)));
                int loopTimesSafe = 0;
                for (Map.Entry<ChessPiece, ArrayList<Point2D>> entry : kingSafety.entrySet()) {
                    //Do a safe move
                    if (loopTimesSafe == randomSafe) {
                        int rand = ((int) (Math.random() * (entry.getValue().size() - 1)));
//                    int rand = 0;
                        System.out.println("Moved because: Safe Random");
                        this.game.getChessBoard().moveChessPiece(entry.getKey(),
                                entry.getKey().getLocation(),
                                entry.getValue().get(rand));
                        this.checkPS();
                        return;
                    }
                    loopTimesSafe++;
                }
            }

            //Random (non safe) location
            int random = ((int) (Math.random() * compMap.keySet().size()));
            int loopTimes = 0;
            ArrayList<ChessPiece> remove = new ArrayList<>();
            for (ChessPiece piece : compMap.keySet()) {
                if (compMap.get(piece).isEmpty()) {
                    remove.add(piece);
                }
            }
            for (ChessPiece piece : remove) {
                compMap.remove(piece);
            }
            for (Map.Entry<ChessPiece, ArrayList<Point2D>> entry : compMap.entrySet()) {
                if (loopTimes == random) {
                    int rand = ((int) (Math.random() * entry.getValue().size()));
//                int rand = 0;
                    System.out.println("Moved because: Random");
                    this.game.getChessBoard().moveChessPiece(entry.getKey(), entry.getKey().getLocation(), entry.getValue().get(rand));
                    this.checkPS();
                    return;

                }
                loopTimes++;
            }

            for (Map.Entry<Point2D, ChessPiece> entry : Chess.getPieceMapForTeam(this.game.getChessBoard(), this.team).entrySet()) {
                ArrayList<Point2D> moves = entry.getValue().getMoveBuilder().getMoves();
                if (!moves.isEmpty()) {
                    System.out.println("Moved because: 0Random");
                    this.game.getChessBoard().moveChessPiece(entry.getValue(), entry.getValue().getLocation(), moves.get(0));
                    this.checkPS();
                    return;
                }
            }
        }

    }

}
