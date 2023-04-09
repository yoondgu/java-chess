package chess.config;

import chess.controller.ChessGameController;

public class ChessGameControllerFactory {

    private ChessGameControllerFactory() {
    }

    public static ChessGameController create() {
        return new ChessGameController(ChessGameServiceFactory.create());
    }

}
