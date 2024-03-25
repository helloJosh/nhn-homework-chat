# nhn-homework-chat

### 코드리뷰
* JSON 파일 : 순서가 달라진다 -> 따라서 라인매치는 안된다 -> 내가 테스트할때만 동작한다.
  * JSON 파싱 에러가 어차피 잡아주기때문에
* if, else if 사용처를 확실해야한다. -> 쓸데없이 코드가 돌수가있다. Else if 문에 if를 쓰면 모든 코드를 돌기때문에 효율적이지 않다.
* ClientRepository : 값의 제거 추가