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

public class Queen extends Piece {

    private static final Map<Camp, Queen> CACHE = new EnumMap<>(Camp.class);
    private static final List<Direction> directions;

    static {
        directions = List.of(
                Direction.EAST, Direction.WEST, Direction.SOUTH, Direction.NORTH,
                Direction.NORTH_EAST, Direction.NORTH_WEST,
                Direction.SOUTH_EAST, Direction.SOUTH_WEST
        );

        CACHE.put(Camp.WHITE, new Queen(Camp.WHITE));
        CACHE.put(Camp.BLACK, new Queen(Camp.BLACK));
    }

    private Queen(final Camp camp) {
        super(camp, PieceType.QUEEN);
    }

    public static Queen of(final Camp camp) {
        validateCampSetting(camp);
        return CACHE.get(camp);
    }

    @Override
    public CheckablePaths findCheckablePaths(final Position current) {
        List<Path> paths = new ArrayList<>();
        for (Direction direction : directions) {
            paths.add(Path.ofMultiPath(current, direction));
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
