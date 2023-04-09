package chess.domain;

import chess.domain.piece.Bishop;
import chess.domain.piece.Empty;
import chess.domain.piece.King;
import chess.domain.piece.Knight;
import chess.domain.piece.Pawn;
import chess.domain.piece.Piece;
import chess.domain.piece.Queen;
import chess.domain.piece.Rook;
import java.util.AbstractMap;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;

public enum PieceInitializer {

    WHITE_ROOK(List.of(1, 8), Camp.WHITE.startingRank(), Rook.of(Camp.WHITE)),
    WHITE_KNIGHT(List.of(2, 7), Camp.WHITE.startingRank(), Knight.of(Camp.WHITE)),
    WHITE_BISHOP(List.of(3, 6), Camp.WHITE.startingRank(), Bishop.of(Camp.WHITE)),
    WHITE_QUEEN(List.of(4), Camp.WHITE.startingRank(), Queen.of(Camp.WHITE)),
    WHITE_KING(List.of(5), Camp.WHITE.startingRank(), King.of(Camp.WHITE)),
    WHITE_PAWN(List.of(1, 2, 3, 4, 5, 6, 7, 8), Camp.WHITE.startingPawnRank(), Pawn.of(Camp.WHITE)),

    BLACK_ROOK(List.of(1, 8), Camp.BLACK.startingRank(), Rook.of(Camp.BLACK)),
    BLACK_KNIGHT(List.of(2, 7), Camp.BLACK.startingRank(), Knight.of(Camp.BLACK)),
    BLACK_BISHOP(List.of(3, 6), Camp.BLACK.startingRank(), Bishop.of(Camp.BLACK)),
    BLACK_QUEEN(List.of(4), Camp.BLACK.startingRank(), Queen.of(Camp.BLACK)),
    BLACK_KING(List.of(5), Camp.BLACK.startingRank(), King.of(Camp.BLACK)),
    BLACK_PAWN(List.of(1, 2, 3, 4, 5, 6, 7, 8), Camp.BLACK.startingPawnRank(), Pawn.of(Camp.BLACK)),

    EMPTY_3_RANK(List.of(1, 2, 3, 4, 5, 6, 7, 8), Camp.EMPTY.startingRank(), Empty.getInstance()),
    EMPTY_4_RANK(List.of(1, 2, 3, 4, 5, 6, 7, 8), Camp.EMPTY.startingRank() + 1, Empty.getInstance()),
    EMPTY_5_RANK(List.of(1, 2, 3, 4, 5, 6, 7, 8), Camp.EMPTY.startingRank() + 2, Empty.getInstance()),
    EMPTY_6_RANK(List.of(1, 2, 3, 4, 5, 6, 7, 8), Camp.EMPTY.startingRank() + 3, Empty.getInstance());

    private final List<Integer> files;
    private final int rank;
    private final Piece piece;

    PieceInitializer(final List<Integer> files, final int rank, final Piece piece) {
        this.files = files;
        this.rank = rank;
        this.piece = piece;
    }

    public static Map<Position, Piece> createPiecesWithPosition() {
        return Arrays.stream(values())
                .map(PieceInitializer::placePieces)
                .flatMap(Collection::stream)
                .collect(Collectors.toMap(Entry::getKey, Entry::getValue, (existing, add) -> add,
                        HashMap::new));
    }

    private List<Entry<Position, Piece>> placePieces() {
        return files.stream()
                .map(file -> new AbstractMap.SimpleEntry<>(Position.of(file, rank), piece))
                .collect(Collectors.toList());
    }
}
