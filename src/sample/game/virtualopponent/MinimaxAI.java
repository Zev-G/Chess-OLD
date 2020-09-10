package sample.game.virtualopponent;

import sample.chess.Team;
import sample.game.Game;
import sample.game.Move;

public class MinimaxAI extends Opponent {


    public MinimaxAI(Game game, Team team) {
        super(game, team);
    }

    public MinimaxAI(Game game, Team team, boolean runLater) {
        super(game, team, runLater);
    }

    @Override
    public void thisTurn(Move newMove) {
        MovePosition minimax = this.generateMovePosition(3);
        this.game.getChessBoard().moveChessPiece(minimax.getPiece(), minimax.getPiece().getLocation(), minimax.getPoint2D());
    }
}
