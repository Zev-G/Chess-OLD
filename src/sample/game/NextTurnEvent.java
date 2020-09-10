package sample.game;

public interface NextTurnEvent {
    void onTurnChange(Game game, Move newMove);
}
