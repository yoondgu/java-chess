package chess.domain.piece;

import chess.domain.Camp;

public class PieceFixture {

    public static final Piece WHITE_ROOK = Rook.of(Camp.WHITE);
    public static final Piece WHITE_KNIGHT = Knight.of(Camp.WHITE);
    public static final Piece WHITE_BISHOP = Bishop.of(Camp.WHITE);
    public static final Piece WHITE_QUEEN = Queen.of(Camp.WHITE);
    public static final Piece WHITE_KING = King.of(Camp.WHITE);
    public static final Piece WHITE_PAWN = Pawn.of(Camp.WHITE);

    public static final Piece BLACK_ROOK = Rook.of(Camp.BLACK);
    public static final Piece BLACK_KNIGHT = Knight.of(Camp.BLACK);
    public static final Piece BLACK_BISHOP = Bishop.of(Camp.BLACK);
    public static final Piece BLACK_QUEEN = Queen.of(Camp.BLACK);
    public static final Piece BLACK_KING = King.of(Camp.BLACK);
    public static final Piece BLACK_PAWN = Pawn.of(Camp.BLACK);

}
