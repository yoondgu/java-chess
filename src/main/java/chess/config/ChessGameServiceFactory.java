package chess.config;

import chess.service.ChessBoardService;
import chess.service.ChessGameService;

public class ChessGameServiceFactory {

    private ChessGameServiceFactory() {
    }

    public static ChessGameService create() {
        return new ChessGameService(ChessBoardService.ofJDBCDao());
    }

}
