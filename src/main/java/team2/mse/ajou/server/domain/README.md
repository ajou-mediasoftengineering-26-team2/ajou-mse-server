# `domain` 패키지
여기에는 각 도메인 (EX: 로그인/authentication, 손동작 입력 받기, 상점페이지 입력 등)별로 패키지를 나누고, 그 패키지 아래에 역할별로 세부 패키지를 만들어서 사용하시면 됩니다.
# 예시
```
+ team2.mse.ajou.server
    + domain
        + gameplay (게임 관련 도메인)
            + model (Model / DTO 클래스 모음)
                - PostHandInputRequest.java (손동작 Request Body 모델)
                - PostHandInputResponse.java (손동작 Response(응답) Body 모델)
                ...
            + controller (@Controller 클래스 모음)
                - HandInputController.java (손동작 입력값 REST API)
                - ShopInputController.java (상접 입력값 REST API)
                ...
        + matchmaking (로그인 및 로비 관련 도메인)
            + model
                - PostLoginRequest.java (로그인 Request Body 모델)
                - PostLoginResponse.java (로그인 Response Body 모델)
                ...
            + controller
                - LoginController.java (로그인 REST API)
                - LobbyController.java (로비 입장 등 REST API)
    + apiresponse (공통 API response 클래스)
        ...
    + <이외 기타 공통 유틸/클래스 등>
```