package sample.chess;

public enum Team {
    WHITE,
    BLACK;

    Team() {
    }

    @Override
    public String toString() {
        if (this.equals(Team.WHITE)) {
            return "WHITE";
        } else {
            return "BLACK";
        }
    }
}
