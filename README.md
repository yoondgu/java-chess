# java-chess

콘솔을 통해 체스 게임을 할 수 있는 자바 어플리케이션 저장소

- 여러 개의 게임 방을 만들고, 입장할 수 있다.
- 게임 도중 방을 나간 뒤 다른 방에 입장할 수 있다.
- 게임 도중 방을 나가거나, 어플리케이션을 종료해도 재입장해 게임을 정상 재개할 수 있다.
- 게임의 승패가 결정나 OVER 되면, 진영 별 점수와 이긴 진영을 확인할 수 있다.
- OVER 상태의 게임 방에는 재입장할 수 없다.

## 실행 환경 참고 사항

### 데이터베이스

- 어플리케이션 실행 전, `java-chess/docker` 경로에서 아래 명령어로 MySQL DB 서버를 실행해야 한다.

```
docker-compose -p chess up -d
```

- DB 서버 중지 시에는 아래 명령어를 사용한다.

```
docker-compose -p chess down
```

- docker 파일 빌드로 db 생성 시 `docker/init/init.sql`이 자동 실행된다.
- `chess` DB에 테이블 `board_pieces`, `board_statuses`가 생성되었다면 성공적으로 초기화된 것이다.

### 콘솔 스크립트

- `WHITE` 진영이 King을 잡아 승리하도록 하는 스크립트
- 아래 명령어를 순서대로 실행할 수 있다. (DB에 999번 방 정보가 없다고 가정)

```
start
999
move d2 d4
move e7 e5
move d4 d5
move e8 e7
move d5 d6
move a7 a6
move d6 e7
status
```

## 기능 목록

### 체스 규칙

- 두 개의 진영이 있다.
    - 각 진영은 하얀색, 검정색으로 구분한다.
- 진영 당 8개의 폰, 2개의 룩, 2개의 나이트, 2개의 비숍, 킹, 퀸을 가지고 있다.
    - 각 말 타입마다 다른 이동 규칙을 가진다.
    - 각 말 타입마다 점수를 가진다.
        - 룩
            - 동서남북으로 장애물이 없을 때까지 원하는 만큼 이동 가능하다.
            - 점수: 5점
        - 나이트
            - 동서남북 1칸 이동 후 대각선으로 1칸 이동 가능하다.
            - 나이트는 장애물을 무시할 수 있다.
            - 점수: 2.5점
        - 비숍
            - 대각선으로 장애물이 없을 때까지 원하는 만큼 이동 가능하다.
            - 점수: 3점
        - 퀸
            - 동서남북, 대각선으로 장애물이 없을 때까지 원하는 만큼 이동 가능하다.
            - 점수: 9점
        - 킹
            - 동서남북, 대각선으로 1칸만 이동 가능하다.
            - 점수: 없음(0)
        - 폰
            - 처음에는 앞으로 1칸 또는 2칸 이동 가능, 이후에는 앞으로만 1칸 이동 가능하다.
            - 말을 잡을 때에는 앞으로 대각선 1칸만 가능하다.
            - 점수: 기본 1점, 같은 세로줄에 같은 색의 폰이 있는 경우 0.5점
- 자신의 진영의 King 이 잡히면 게임에서 진다.
- 게임의 승패가 결정되면(King 이 잡히면) 게임을 종료한다.

### 시퀀스 다이어그램

- move 명령에 대한 도메인 시퀀스 다이어그램 (DB 적용 로직 생략)

```mermaid
sequenceDiagram

	InputView -->> Controller: move 명령어 입력

	Controller ->> ChessGameService: MOVE 명령어, 출발지, 도착지 위치 전달
	ChessGameService ->> ChessBoard: 출발지, 도착지 위치 전달, move 명령

    ChessBoard ->> Piece: 출발지에 위치한 말 확인

	ChessBoard ->> Piece: 확인한 말에 출발지와 이동 방향을 전달

	Piece ->> Path: 출발지에서 이동 가능한 모든 Path 요청

	Path ->> Position: 특정 방향으로 다음 위치 요청
	Position -->> Path: 특정 방향으로 한 칸 이동한 새로운 Position 반환

	Path -->> Piece: 출발지에서 이동 가능한 모든 Path 반환 

	Piece -->> ChessBoard: 출발지에서 말이 이동 가능한 모든 Path 반환

	ChessBoard ->> ChessBoard: 도착지가 포함된 Path가 있는지 확인
	ChessBoard ->> ChessBoard: 도착지로 이동 가능한지 장애물 확인, 잡을 수 있는 말 있는지 확인

	Piece -->> ChessBoard: 이동 가능 여부, 공격 가능 여부 반환

	Piece -->> ChessBoard: 잡은 말이 King인지 반환
	ChessBoard ->> ChessBoard: 체크 확인(체크이면 게임 종료 처리)

	ChessBoard ->> ChessBoard: 다른 말들의 위치를 고려해 체스판에 이동 결과 반영
	ChessBoard -->> ChessGameService: 게임 진행 중이면 체스판 정보 반환

	ChessGameService -->> Controller: 체스판 정보 반환

```

- status 명령에 대한 도메인 시퀀스 다이어그램 (DB 적용 로직 생략)

```mermaid
sequenceDiagram

	InputView -->> Controller: status 명령어 입력
	Controller ->> ChessGameService: 게임 현황 정보 요청

	ChessGameService ->> ChessGameService: 게임 상태 검증
	ChessGameService ->> ChessBoard: 진영 별 점수 요청
	Piece -->> ChessBoard: 말의 점수 반환
	ChessBoard ->> ChessBoard: 진영 별 말의 점수 합산
	ChessBoard -->> ChessGameService: 진영 별 점수 반환
	ChessBoard -->> ChessGameService: 승리한 진영 반환

	ChessGameService -->> Controller: 게임 결과 정보 반환
```

### 도메인 기능

- ChessGameService
    - [x] 게임 상태를 관리한다. (READY, RUNNING, OVER)
    - [x] 요청을 받아 상태를 확인하고 기능을 수행한다.
        - [x] 데이터베이스로부터 필요한 정보를 가져오거나, 반영한다.
    - [x] 한 턴의 이동을 수행한 뒤, 게임을 끝내야 하는지 확인한다.
    - [x] 게임을 끝내야 하면 게임 상태를 OVER 로 변경한다.
    - [x] 게임 결과 정보를 반환한다.
        - [x] 이긴 진영을 확인한다.
        - [x] 진영 별 점수를 확인한다.
- ChessBoardService
    - [x] 데이터베이스에서 입장 가능한 방 번호를 모두 조회한다.
    - [x] 데이터베이스에 체스판 정보를 업데이트한다. (위치 정보, 상태 정보)
    - [X] 방 번호에 따른 체스판 정보를 조회한다.
- ChessBoard
    - [x] 비어 있는 체스판을 생성한다.
        - [x] 체스판에 초기 말을 세팅한다.
    - [x] 출발지에 있는 말을 주어진 도착지로 움직인다.
        - [x] 출발지에 있는 말을 찾는다.
            - [x] `예외` 출발 위치에 말이 없으면 이동할 수 없다.
        - [x] 도착지에 상대방의 말이 있다면 잡는다.
            - [x] `예외` 출발지와 도착지가 동일할 수 없다.
        - [x] 도착지로의 경로를 따라 이동이 가능한지 확인한다.
            - [x] `예외` 이동 경로에 다른 말이 있으면(장애물) 이동할 수 없다.
        - [x] 현재 플레이어 턴을 관리한다.
    - [x] 이동 시 말을 잡았다면, 해당 말이 King 인지 여부를 확인한다.
    - [x] 현재 남아있는 말의 점수를 진영 별로 합산한다.
        - [x] 같은 세로줄에 같은 색의 폰이 있는 경우 점수 계산이 다르다.
- Piece
    - [x] 말은 움직일 수 있고, 종류 별로 구현할 수 있다.
        - [x] 종류에는 킹(King), 퀸(Queen), 룩(Rook), 비숍(Bishop), 나이트(Knight), 폰(Pawn)이 있다.
        - [x] 종류마다 이동 규칙이 다르다.
        - [x] 말은 이동 가능한 모든 방향을 가지고 있다.
        - [x] 말은 점수를 가진다.
    - [x] 말은 출발지를 전달받아서 이동 가능성이 있는 모든 경로를 반환한다.
        - [x] 이동 가능성이 있는 모든 경로는 특정 위치를 가진 경로만 반환할 수 있다.
    - [x] 말은 소속 진영을 가진다.
        - [x] 폰의 경우, 하얀색 팀이면 북행이 전진이고, 검정색 팀이면 남행이 전진이다.
    - [x] 말은 출발지와 도착지를 전달받아 빈 위치로 이동 가능 여부를 반환한다.
        - [x] 다른 말은 항상 이동 가능하고, 폰은 공격을 위한 이동 방향일 경우 불가능하다.
    - [x] 말은 출발지와 도착지, 타겟 말을 전달받아 공격 가능 여부를 반환한다.
        - [x] 타겟 말이 같은 팀이면 공격 불가능하다.
        - [x] 폰은 타겟 말이 다른 팀이어도, 공격을 위한 이동 방향이 아니면 불가능하다.
- Path
    - [x] 한 방향에서 갈 수 있는 모든 도착 가능한 위치들의 목록을 가지고 있다.
        - [x] 출발지와 방향을 전달받아 도착 가능한 위치들의 목록을 생성한다.
            - [x] 단일 칸 이동경로 생성 (경로를 이루는 칸이 하나 뿐이다.)
            - [x] 다중 칸 이동경로 생성 (경로를 이루는 칸이 여러가지일 수 있다.)
- Position
    - [x] 말의 위치는 랭크(y축), 파일(x축)로 이루어져 있다.
        - [x] `예외` 가로 및 세로 좌표값가 정해진 범위(8)를 벗어날 수 없다.
    - [x] 위치는 시작 위치에서 현재 방향으로 이동할 수 있는 다음 위치을 반환한다.
- Direction
    - [x] 모든 이동 가능 방향을 가지고 있다.

### 입출력 기능

- 출력
    - [x] 체스 게임 시작 안내문을 출력한다.
    - [x] 초기 세팅 된 체스판을 출력한다.
    - [x] 이동 결과를 출력한다.
    - [x] 말의 타입과 색에 따라 이름을 출력한다.
    - [x] 게임 오버 시 안내문을 출력한다.
    - [x] 게임 결과 정보를 출력한다.
    - [x] 게임 시작 시 입장 가능한 방 번호를 출력한다.
- 입력
    - [x] 게임 명령어를 입력받는다. (start, end, move)
        - [x] `예외` 명령어가 유효하지 않으면 예외를 발생시킨다.
        - [x] 입력이 start 이면 체스판을 초기화한다.
        - [x] 입력이 move 이면 출발지와 도착지를 함께 입력받는다.
            - [x] `예외` 입력이 move 일 때, 출발지나 도착지 정보가 유효하지 않으면 예외를 발생시킨다.
        - [x] 입력이 end 이면 대기 상태로 돌아가 안내 화면을 출력한다.
        - [x] 입력이 status 이면 게임 결과 정보(진영 별 점수, 이긴 진영)를 출력한다.
            - [x] `예외` 게임이 종료되지 않은 상태이면 예외를 발생시킨다.
    - [x] 게임 상태에 맞지 않는 명령어 입력 시 예외를 발생시킨다.
    - [x] 게임 시작 시 입장할 방 번호를 입력받는다.
        - [x] `예외` 입력이 정수가 아니면 예외를 발생시킨다.

## 리뷰 반영 및 리팩터링 목록

- 요구사항 관련
    - [ ] 방 입장 시 방 번호 선택 / 0 입력 시 새 방 개설하도록 변경
- 설계 관련
    - [ ] Piece 인스턴스 캐싱
    - [x] Piece.canMove~() 에서 이동방향 검증까지 직접 할 수 있도록 개선
        - Piece 로 체스판의 이동 검증 로직 책임 위임 및 통합
    - [ ] ActionMapper `Enum 요소 조회 비용` vs `Map 클래스로 변경 후, static 상수로 인한 메모리 사용` 비교
    - [x] GameStatus 패키지 이동, 검증 로직 의존관계 없애기
    - [ ] King 공격 확인 메서드 분리
- 코드 관련
    - [ ] Dao 템플릿 적용하여 중복 코드 제거
    - [ ] JDBC 자원 close 해주기
    - [ ] Connection 사용 최적화
    - [ ] Dao SqlException 런타임예외로 전환, 예외메시지 구체화
    - [ ] Dao 메서드명 일반적인 방식으로 변경
    - [ ] 모호한 메서드명 수정
    - [ ] 부적절한 Dao 클래스명 수정
    - [ ] 필요 시 Optional orElse 대신 orElseGet 쓰기
- DB 관련
    - [ ] 서비스 계층 테스트는 InMemoryDao 주입하여 작성하기
    - [ ] DB 작동 테스트는 테스트용 테이블 만들어서 작성하기
    - [ ] 사용자 방 개설 로직 변경에 따라, board_id를 별도 Key로 지정하지 않고 인덱스 사용
    - [ ] is_over 컬럼 boolean / tinyint 타입으로 변경
    - [ ] 새로운 방 개설 시 바로 board_statuses 반영
    - [ ] insert + update 단일 책임 위배일까?
