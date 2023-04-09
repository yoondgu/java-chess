package chess.domain.piece;

import chess.domain.Camp;
import chess.domain.CheckablePaths;
import chess.domain.Direction;
import chess.domain.Path;
import chess.domain.Position;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

public class Pawn extends Piece {

    public static final double CONTINUOUS_PAWNS_POINT = 0.5;
    private static final Map<Camp, Pawn> CACHE = new EnumMap<>(Camp.class);
    private static final int WHITE_START_RANK = 2;
    private static final int BLACK_START_RANK = 7;

    static {
        CACHE.put(Camp.WHITE, new Pawn(Camp.WHITE));
        CACHE.put(Camp.BLACK, new Pawn(Camp.BLACK));
    }

    private final List<Direction> directions;

    private Pawn(Camp camp) {
        super(camp, PieceType.PAWN);
        this.directions = getDirectionsByColor();
    }

    public static Pawn of(final Camp camp) {
        validateCampSetting(camp);
        return CACHE.get(camp);
    }

    private List<Direction> getDirectionsByColor() {
        if (camp == Camp.BLACK) {
            return List.of(Direction.SOUTH, Direction.SOUTH_EAST, Direction.SOUTH_WEST);
        }
        return List.of(Direction.NORTH, Direction.NORTH_EAST, Direction.NORTH_WEST);
    }

    @Override
    public CheckablePaths findCheckablePaths(final Position current) {
        List<Path> paths = new ArrayList<>();
        for (Direction direction : directions) {
            paths.add(Path.ofSinglePath(current, direction));
        }

        if (isStartRank(current)) {
            paths.add(Path.ofPawnStartPath(current, getForwardDirectionByColor()));
        }
        return new CheckablePaths(paths);
    }

    private boolean isStartRank(final Position current) {
        if (camp == Camp.WHITE) {
            return current.isInExpectedRank(WHITE_START_RANK);
        }
        return current.isInExpectedRank(BLACK_START_RANK);
    }

    private Direction getForwardDirectionByColor() {
        if (camp == Camp.WHITE) {
            return Direction.NORTH;
        }
        return Direction.SOUTH;
    }

    @Override
    public boolean canMoveToEmpty(final Position source, final Position dest) {
        return !isAttack(source, dest);
    }

    @Override
    public boolean canAttack(Position source, Position dest, Piece target) {
        return isAttack(source, dest) && isDifferentCamp(target.camp);
    }

    private boolean isAttack(Position source, Position dest) {
        return Direction.find(source, dest)
                .map(this::isRightAttackDirectionByCamp)
                .orElse(false);
    }

    private boolean isRightAttackDirectionByCamp(Direction direction) {
        if (camp == Camp.WHITE) {
            return direction == Direction.NORTH_EAST || direction == Direction.NORTH_WEST;
        }
        return direction == Direction.SOUTH_EAST || direction == Direction.SOUTH_WEST;
    }

    @Override
    public double sumPointsOf(final List<Position> existingPositions) {
        if (existingPositions.isEmpty()) {
            return 0;
        }
        return calculatePoints(existingPositions.size(), countSameFilePositions(existingPositions));
    }

    private double calculatePoints(int totalPositionCounts, int sameFilePositionCounts) {
        double defaultPoints = (type.getPoint()) * (totalPositionCounts - sameFilePositionCounts);
        double decreasedPoints = CONTINUOUS_PAWNS_POINT * (sameFilePositionCounts);
        return defaultPoints + decreasedPoints;
    }

    private int countSameFilePositions(final List<Position> existingPositions) {
        return (int) existingPositions.stream()
                .filter(position -> hasSameFilePosition(position, existingPositions))
                .count();
    }

    private boolean hasSameFilePosition(Position self, List<Position> existingPositions) {
        return existingPositions.stream()
                .filter(position -> !position.equals(self))
                .anyMatch(position -> position.isInSameFile(self));
    }

}
