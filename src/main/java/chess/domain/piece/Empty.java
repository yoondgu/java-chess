package chess.domain.piece;

import chess.domain.Camp;
import chess.domain.CheckablePaths;
import chess.domain.Position;
import java.util.List;

public class Empty extends Piece {

    private static final Empty CACHE = new Empty();
    private static final String EMPTY_PIECE_ERROR_MESSAGE = "선택한 말이 존재하지 않습니다.(EMPTY)";

    private Empty() {
        super(Camp.EMPTY, PieceType.EMPTY);
    }

    public static Piece getInstance() {
        return CACHE;
    }

    @Override
    public CheckablePaths findCheckablePaths(final Position current) {
        throw new IllegalArgumentException(EMPTY_PIECE_ERROR_MESSAGE);
    }

    @Override
    public boolean canMoveToEmpty(final Position source, final Position dest) {
        throw new IllegalArgumentException(EMPTY_PIECE_ERROR_MESSAGE);
    }

    @Override
    public boolean canAttack(final Position source, final Position dest, final Piece target) {
        throw new IllegalArgumentException(EMPTY_PIECE_ERROR_MESSAGE);
    }

    @Override
    public double sumPointsOf(final List<Position> existingPositions) {
        throw new IllegalArgumentException(EMPTY_PIECE_ERROR_MESSAGE);
    }
}
