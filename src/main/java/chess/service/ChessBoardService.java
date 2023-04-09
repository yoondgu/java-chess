package chess.service;

import chess.dao.boardpieces.BoardPiecesDao;
import chess.dao.boardpieces.InMemoryBoardPiecesDao;
import chess.dao.boardpieces.JdbcBoardPiecesDao;
import chess.dao.boardstatuses.BoardStatusesDao;
import chess.dao.boardstatuses.InMemoryBoardStatusesDao;
import chess.dao.boardstatuses.JdbcBoardStatusesDao;
import chess.domain.Camp;
import chess.domain.ChessBoard;
import chess.domain.PieceInitializer;
import chess.domain.Position;
import chess.domain.piece.Piece;
import chess.dto.ChessBoardStatus;
import java.util.List;
import java.util.Map;

public class ChessBoardService {

    private final BoardPiecesDao boardPiecesDao;
    private final BoardStatusesDao boardStatusesDao;

    private ChessBoardService(final BoardPiecesDao boardPiecesDao, final BoardStatusesDao boardStatusesDao) {
        this.boardPiecesDao = boardPiecesDao;
        this.boardStatusesDao = boardStatusesDao;
    }

    public static ChessBoardService ofJDBCDao() {
        return new ChessBoardService(new JdbcBoardPiecesDao(), new JdbcBoardStatusesDao());
    }

    public static ChessBoardService ofInMemoryDao() {
        return new ChessBoardService(new InMemoryBoardPiecesDao(), new InMemoryBoardStatusesDao());
    }

    public static ChessBoardService of(BoardPiecesDao boardPiecesDao, BoardStatusesDao boardStatusesDao) {
        return new ChessBoardService(boardPiecesDao, boardStatusesDao);
    }

    public ChessBoard loadChessBoard(int boardId) {
        Map<Position, Piece> piecesByPosition = boardPiecesDao.find(boardId)
                .orElseGet(PieceInitializer::createPiecesWithPosition);
        ChessBoardStatus status = boardStatusesDao.findByBoardId(boardId)
                .orElseGet(() -> new ChessBoardStatus(Camp.WHITE, false));
        return new ChessBoard(boardId, piecesByPosition, status);
    }

    public void saveChessBoard(ChessBoard chessBoard) {
        boardStatusesDao.insertOrUpdate(chessBoard.getId(), chessBoard.status());
        boardPiecesDao.insertOrUpdate(chessBoard.getId(), chessBoard.piecesByPosition());
    }

    public List<Integer> findAllNotOverBoardIds() {
        return boardStatusesDao.findAllNotOverBoardIds();
    }
}
