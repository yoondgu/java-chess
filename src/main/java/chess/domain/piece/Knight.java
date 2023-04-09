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

public class Knight extends Piece {

    private static final Map<Camp, Knight> CACHE = new EnumMap<>(Camp.class);
    private static final List<Direction> directions;

    static {
        directions = List.of(
                Direction.EAST_EAST_SOUTH, Direction.EAST_EAST_NORTH,
                Direction.WEST_SOUTH_SOUTH, Direction.EAST_SOUTH_SOUTH,
                Direction.WEST_WEST_SOUTH, Direction.WEST_WEST_NORTH,
                Direction.WEST_NORTH_NORTH, Direction.EAST_NORTH_NORTH
        );

        CACHE.put(Camp.WHITE, new Knight(Camp.WHITE));
        CACHE.put(Camp.BLACK, new Knight(Camp.BLACK));
    }

    private Knight(final Camp camp) {
        super(camp, PieceType.KNIGHT);
    }

    public static Knight of(final Camp camp) {
        validateCampSetting(camp);
        return CACHE.get(camp);
    }

    @Override
    public CheckablePaths findCheckablePaths(final Position current) {
        List<Path> paths = new ArrayList<>();
        for (Direction direction : directions) {
            paths.add(Path.ofSinglePath(current, direction));
        }
        return new CheckablePaths(paths);
    }

    @Override
    public boolean canMoveToEmpty(final Position source, final Position dest) {
        return true;
    }

    @Override
    public boolean canAttack(Position source, Position dest, Piece target) {
        return isDifferentCamp(target.camp);
    }

    @Override
    public double sumPointsOf(final List<Position> existingPositions) {
        return (type.getPoint()) * (existingPositions.size());
    }

}
