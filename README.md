# 서울과기대 공지사항 알리미 앱 

## 개발 기간 
 
- 2021.11.01 ~ 2021.12.03 
- 2학년 2학기 모바일 프로그래밍, 개인 프로젝트 

## 앱의 목적 및 사용 대상 

### 앱의 목적 

**1. 기존 스마트캠퍼스 앱의 문제점을 개선하자.** 

- 다이얼로그 내의 이미지 크기가 제대로 조정되지 않는 문제를 해결하기 위해, 아예 새로운 액티비티 창을 띄워서 보여주자. 
- 뒤로가기 버튼을 누를 때마다 종료 확인 다이얼로그가 뜨는 게 아니라, 정상적으로 이전 화면으로 돌아가게 만들자. 

![image](https://user-images.githubusercontent.com/68090939/196992389-f62ce86b-9acf-4640-9f1a-d987f62a1e16.png)

2. **조회수가 높은 순으로 공지사항 리스트를 정렬**하여 인기가 많은 프로그램을 먼저 확인할 수 있게 하자. (선착순으로 마감되는 프로그램을 놓치지 않을 수 있도록) 

3. 매번 공지사항에 들어가서 원하는 정보를 직접 찾는 게 매우 번거롭기 때문에, **유저가 키워드를 설정해두면 그와 관련된 공지사항이 떴을 때 핸드폰에 푸시 알림이 오도록** 만들자. 

### 사용 대상 

대학 공지사항, 학사공지, 장학공지를 편하게 보고 싶은 서울과기대 학생들 

## UI 구성 

![image](https://user-images.githubusercontent.com/68090939/196992860-63903506-22e0-40ff-9cb0-693744e7a56e.png)

![image](https://user-images.githubusercontent.com/68090939/196992903-e519019d-a5c3-495c-a058-17125a95c601.png)

## 시스템 구성도 

![image](https://user-images.githubusercontent.com/68090939/196993037-9ab05c40-5117-4846-a6e5-4d94bea9598e.png)

![image](https://user-images.githubusercontent.com/68090939/196993216-8bc94db1-70e4-4784-8411-8e6e37a097b5.png)

![image](https://user-images.githubusercontent.com/68090939/196993268-07b9f2ba-ecb1-4995-b34a-ac9340ac27ff.png)

![image](https://user-images.githubusercontent.com/68090939/196993292-039cc942-700a-473e-80f7-fdceae4f0fa6.png)

## 어려웠던 기술적 과제 및 해결 과정 

1. Jsoup 라이브러리와 AsyncTask 사용해서 웹 크롤링 해오기

- 웹 사이트의 테이블 데이터를 가져오기 위해 tr, td 태그를 일일이 select() 메소드로 파싱 해오는 것이 까다로웠다. → 스택오버플로우와 [Jsoup 공식 문서](https://jsoup.org/cookbook/extracting-data/selector-syntax)를 통해 해결

2. 리사이클러뷰의 모든 아이템을 업데이트 할 때, 어댑터를 새로 생성하는 것이 아니라 [기존 리스트의 모든 내용을 지우고 새로운 리스트로 addAll](https://suragch.medium.com/updating-data-in-an-android-recyclerview-842e56adbfd8) 해야 한다.

3. Pagination 

- 첫번째 시도: 화살표 모양의 ImageButton을 클릭하면 이전/다음 페이지로 이동하도록 → 계속 동일한 페이지가 뜨는 문제 발생 
- 두번째 시도: ViewPager 사용 → 뷰페이저와 리사이클러뷰 모두 어댑터를 사용하다 보니 코드가 매우 복잡해졌다. 처음에는 CustomPagerAdapter의 instantiateItem에서 페이지 하나를 생성할 때마다 웹 크롤링을 하도록 코드를 짰더니, 페이지를 넘길 때마다 로딩 시간이 걸리는 문제가 발생했다. 그래서 한꺼번에 5개의 페이지를 크롤링 한 뒤에, 그 데이터를 각 페이지의 리사이클러뷰에 적용하려고 했으나, 실패했다.
- 결국 최종적으로는 3개의 페이지 내용을 하나의 리스트에 이어 붙이고, 중복되는 상단의 공지사항들은 제거하는 방법을 택했다. 다음에 리사이클러뷰를 뷰페이저로 넘기는 방법을 다시 시도해보고 싶다.

4. 곳곳에서 발생하는 예외적인 상황들 

- 웹 페이지 3개를 이어 붙였더니, 상단에 고정된 공지사항이 페이지 개수만큼 중복되는 문제 발생 & 대학 공지사항과 장학공지는 상단에 고정된 공지사항이 6개인데, 학사공지는 4개
- 검색어를 입력하지 않고 엔터키를 눌렀을 때 → 토스트 메시지 띄우기 
- 검색 결과로 아무것도 나오지 않을 때  → 처리를 안 해주니까 앱 자체가 처음부터 재실행 되는 문제 발생 
- 버튼이 아니라 엔터키로 아이템을 추가하고 싶을 때 → setOnKeyListener의 리턴값 처리를 잘 해줘야 함. 
- 조회수가 높은 순으로 정렬했다가 다시 원상 복구하고 싶을 때 → 리스트를 clear 하기 전에 정렬 전의 상태를 임시 리스트에 저장해두더라도, 버튼을 다시 클릭하면 임시 리스트가 정렬된 리스트로 바뀌어 버림. 아직 해결하지 못한 문제.

5. 작성자 리스트의 크기가 더 늘어나면, 외부 파일이나 DB에서 데이터를 가져와야 한다. 현재는 임시적으로 values 폴더 아래에 author_data.xml 파일을 생성하여, string 배열에 작성자 이름을 저장했다.

6. context가 분명하지 않아서 다이얼로그가 안 뜨는 문제 발생 → context 인자에 getApplicationContext() 말고, 명확하게 AuthorActivity.this 전달하기

결과적으로 NoticeActivity를 완전하게 구현하는 데 너무 많은 시간을 들여서, KeywordActivity와 AuthorActivity의 푸시 알림 기능은 구현하지 못했다. 

## 시연 영상 

https://youtu.be/Sm0mXClZRZk 

## 향후 계획 

- 키워드 푸시 알림 기능 완성하기 (웹 스케줄링) 
  - [서울과기대 키워드 알림봇을 개발하신 선배님의 인터뷰 글 참고하기](https://studies.seoultech.ac.kr/activity/news/?do=commonview&searchtext=&searchtype=&nowpage=1&bnum=3596&bidx=507147&cate=&profboardidx=)
  - 알림봇에 사용되는 핵심 기술은 웹 사이트의 데이터를 가져오는 스크래핑과 일정 시간마다 반복하는 스케줄링입니다. 공지사항이 올라오는 평일 am 9:00 ~ pm 18:00 사이에 주기적으로 학교 웹사이트를 방문하여 공지사항 데이터를 수집합니다. 너무 자주 웹사이트에 접속하면 학교 서버에 악영향을 줄 수 있다고 생각하여 주기는 1시간으로 정했습니다. 수집한 공지사항 데이터를 적절히 가공하여 학우분들이 등록해주신 키워드가 포함되었는지 확인을 거친 후, 알림을 보내게 됩니다.
- 코드의 가독성을 높이고 유지보수가 편하도록 리팩토링 하기 
- 밋밋한 디자인 개선하기 
- 서울과기대 학생들에게 홍보하고, 구글 플레이 스토어에 배포까지 해보는 걸 목표로! 
