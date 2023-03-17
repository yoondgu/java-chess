package chess.domain;

import chess.domain.piece.Pawn;
import chess.domain.piece.Piece;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ChessBoard {

    private static final String WRONG_START_ERROR_MESSAGE = "시작 위치에 말이 없습니다.";
    private static final String OBSTACLE_IN_PATH_ERROR_MESSAGE = "경로에 다른 말이 있어서 이동할 수 없습니다.";
    private static final String WRONG_PAWN_PATH_ERROR_MESSAGE = "폰은 공격 할 때는 대각선으로만, 아닐 떄는 직진으로만 이동할 수 있습니다.";
    private static final String WRONG_PIECE_COLOR_ERROR_MESSAGE = "자신 팀의 말만 이동시킬 수 있습니다.";
    private static final String WRONG_ATTACK_TARGET_ERROR_MESSAGE = "상대 팀의 말만 공격할 수 있습니다.";

    private final Map<Position, Piece> piecesByPosition = PieceInitializer.createPiecesWithPosition();

    public void move(List<Integer> sourceCoords, List<Integer> destinationCoords, final Camp camp) {
        Position source = Position.from(sourceCoords);
        Position destination = Position.from(destinationCoords);

        Piece movingPiece = findPieceAtSourcePosition(Position.from(sourceCoords), camp);
        CheckablePaths checkablePaths = movingPiece.findCheckablePaths(source);
        Path pathToDestination = checkablePaths.findPathContainingPosition(destination);

        progressIfPossible(pathToDestination, source, destination, movingPiece, camp);
    }

    private Piece findPieceAtSourcePosition(final Position source, final Camp camp) {
        validatePieceAtSourcePosition(source);
        Piece piece = piecesByPosition.get(source);
        validatePieceColor(piece, camp);
        return piece;
    }

    private void validatePieceAtSourcePosition(final Position start) {
        if (piecesByPosition.containsKey(start)) {
            return;
        }
        throw new IllegalArgumentException(WRONG_START_ERROR_MESSAGE);
    }

    private void validatePieceColor(final Piece piece, final Camp camp) {
        if (piece.isSameColor(camp)) {
            return;
        }
        throw new IllegalArgumentException(WRONG_PIECE_COLOR_ERROR_MESSAGE);
    }

    private void progressIfPossible(final Path pathToDestination, final Position source, final Position destination,
                                    final Piece movingPiece, final Camp camp) {
        validatePath(pathToDestination, source, destination, movingPiece, camp);
        piecesByPosition.put(destination, movingPiece);
        piecesByPosition.remove(source);
    }

    private void validatePath(final Path path, final Position source, final Position destination,
                              final Piece movingPeace, final Camp camp) {
        validateObstacleInPath(path, destination);
        validatePawnAttack(source, destination, movingPeace);
        validateObstacleInDestination(destination, camp);
    }

    private void validateObstacleInPath(final Path path, final Position destination) {
        List<Position> positions = path.positions();
        for (int index = 0; index < path.findPositionIndex(destination); index++) {
            validateObstacleAtIndex(positions, index);
        }
    }

    private void validateObstacleAtIndex(final List<Position> positions, final int index) {
        if (piecesByPosition.containsKey(positions.get(index))) {
            throw new IllegalArgumentException(OBSTACLE_IN_PATH_ERROR_MESSAGE);
        }
    }

    private void validatePawnAttack(final Position source, final Position destination, final Piece movingPiece) {
        if (!movingPiece.isPawn()) {
            return;
        }
        Pawn pawn = (Pawn) movingPiece;
        if (pawn.isAttack(source, destination) && isEmptyPosition(destination)) {
            throw new IllegalArgumentException(WRONG_PAWN_PATH_ERROR_MESSAGE);
        }
        if (!pawn.isAttack(source, destination) && !isEmptyPosition(destination)) {
            throw new IllegalArgumentException(WRONG_PAWN_PATH_ERROR_MESSAGE);
        }
    }

    private boolean isEmptyPosition(final Position destination) {
        return !piecesByPosition.containsKey(destination);
    }

    private void validateObstacleInDestination(final Position destination, final Camp camp) {
        if (!piecesByPosition.containsKey(destination)) {
            return;
        }
        Piece pieceAtDestination = piecesByPosition.get(destination);
        if (pieceAtDestination.isSameColor(camp)) {
            throw new IllegalArgumentException(WRONG_ATTACK_TARGET_ERROR_MESSAGE);
        }
    }

    public Map<Position, Piece> piecesByPosition() {
        return new HashMap<>(piecesByPosition);
    }

}
