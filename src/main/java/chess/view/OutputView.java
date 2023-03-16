package chess.view;

import java.util.List;

public class OutputView {

    private static final String GAME_START = "> 체스 게임을 시작합니다.";
    private static final String GAME_COMMAND_MOVE_DESCRIPTION = "move source위치 target위치 - 예. move b2 b3";
    private static final String GAME_COMMAND_REQUEST = String.format("> 게임 시작: %s\n게임 종료: %s\n게임 이동: %s\n",
            Command.START.getAnswer(), Command.END.getAnswer(), GAME_COMMAND_MOVE_DESCRIPTION);

    public void printStartMessage() {
        System.out.println(GAME_START);
        System.out.println(GAME_COMMAND_REQUEST);
    }

    public void printBoard(List<List<String>> board) {
        System.out.println();
        for (List<String> rank : board) {
            printRank(rank);
            System.out.println();
        }
        System.out.println();
    }

    private void printRank(final List<String> rank) {
        for (String value : rank) {
            System.out.print(value);
        }
    }

}
