package chess.domain.piece;

import chess.domain.Camp;
import java.util.function.Function;

public enum PieceType {

    ROOK(5, Rook::of),
    KNIGHT(2.5, Knight::of),
    BISHOP(3, Bishop::of),
    QUEEN(9, Queen::of),
    KING(0, King::of),
    PAWN(1, Pawn::of),
    EMPTY(0, camp -> Empty.getInstance());


    private final double point;
    private final Function<Camp, Piece> pieceSupplier;

    PieceType(final double point, final Function<Camp, Piece> pieceSupplier) {
        this.point = point;
        this.pieceSupplier = pieceSupplier;
    }

    public double getPoint() {
        return point;
    }

    public Piece createPiece(Camp camp) {
        return pieceSupplier.apply(camp);
    }
}
