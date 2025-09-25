<div align="center">
  
![header](https://capsule-render.vercel.app/api?type=waving&color=0:ff7eb3,100:65c7f7&height=220&section=header&text=농구있네+축구싶냐+야구르징&fontSize=34&fontColor=ffffff&desc=·플링톡+PlayLinkTalk-project·&descAlignY=70&descSize=18&animation=fadeIn)

![Typing SVG](https://readme-typing-svg.herokuapp.com?font=Fira+Code&size=32&pause=1000&color=000000&center=true&vCenter=true&width=1500&lines=⏱️시간+기반으로+사용자를+실시간+매칭해+운동+활동을+중개하는+서비스)

<img src="/readme_images/SportMatch_LOGO.png" alt="PlayLinkTalk Logo" width="260"/>
  
</div>

<br /><br />

---

## 👥 팀원 소개

<table align="center">
  <tr>
    <td align="center"><strong>김민수</strong><br>
      <a href="https://github.com/minsu47722" target="_blank">
        <img src="readme_images/퉁퉁이.jpg" width="100px"><br>@minsu47722
      </a>
    </td>
    <td align="center"><strong>김재상</strong><br>
      <a href="https://github.com/jaesangE" target="_blank">
        <img src="readme_images/도라에몽.jpg" width="100px"><br>@jaesangE
      </a>
    </td>
    <td align="center"><strong>이승진</strong><br>
      <a href="https://github.com/Jintory" target="_blank">
        <img src="readme_images/도라미2.jpg" width="100px"><br>@Jintory
      </a>
    </td>
    <td align="center"><strong>이진구</strong><br>
      <a href="https://github.com/LeeJingu01" target="_blank">
        <img src="readme_images/노진구.jpg" width="100px"><br>@LeeJingu01
      </a>
    </td>
    <td align="center"><strong>임성민</strong><br>
      <a href="https://github.com/baechuking" target="_blank">
        <img src="readme_images/비실이.jpg" width="100px"><br>@baechuking
      </a>
    </td>
    <td align="center"><strong>최유경</strong><br>
      <a href="https://github.com/kyounggg" target="_blank">
        <img src="readme_images/이슬이2.jpg" width="100px"><br>@kyounggg
      </a>
    </td>
  </tr>
</table>


---

## 📌 목차

1. [개요](#1-개요)
2. [프로젝트 주요기능](#2-프로젝트-주요기능)
3. [프로젝트 기대 효과](#3-프로젝트-기대-효과)
4. [기술 스택](#4-기술-스택)
5. [아키텍쳐](#5-아키텍쳐)
6. [요구사항 정의서](#6-요구사항-정의서)
7. [테이블 명세서](#7-테이블-명세서)
8. [ERD](#8-ERD)
9. [API 명세서](#9-API-명세서)

---

# 1. 개요

본 프로젝트는 사용자가 운동 가능한 시간대를 입력하면 실시간으로 함께할 파트너를 매칭해주는 운동 매칭 서비스이다. 정기적인 모임에 참여하기 어려운 사람들도 부담 없이 운동할 수 있도록, 일회성·즉시성 기반의 운동 경험을 제공한다. 

## 배경

<img src="/readme_images/{49E2E0A0-8005-473C-A1FA-6B130751604A}.png" alt="1"/>

현대인들은 **건강 유지와 체력 증진**을 위해 운동을 한다. 그러나 실제로 **체육활동에 참여하지 않는 가장 큰 이유는 시간적 여유 부족**이다. 일과 학업, 개인 일정 등으로 인해 정기적인 운동 시간을 확보하기 어렵고, 설령 시간이 나더라도 함께할 파트너를 찾는 것은 쉽지 않다.

<img src="/readme_images/image.png" alt="2"/>

<img src="/readme_images/image (1).png" alt="3"/>

실제로 국민의 **88.2%가 생활체육 동호회에 가입하지 않았으며**, 그 이유 중 상당수가 **시간적 제약과 정기 모임 참여의 부담** 때문이다. 이는 현대인들이 **자유롭게 운동할 기회를 얻기 어렵다는 점**을 보여준다.

## 기존 플랫폼과의 차별점

**우리 서비스는 ‘정기 모임’이 아니라 ‘가벼운 만남’을 위한 번개 매칭**에 초점을 맞춘다. 시간과 약속에 얽매이지 않고, 남는 시간에 바로 운동할 수 있도록 설계했다.

<img src="/readme_images/image (2).png" alt="4"/>

## 핵심 가치 제안

- **간편한 참여**: 가입·소개글·승인 절차 없이 원하는 조건 중심.
- **원활한 소통**: 매칭이 성사되면 원할한 소통을 위해 자동으로 채팅방 생성
- **순수 운동 지향**: 만남/네트워킹보다 *운동 자체의 몰입*을 우선.
- **번개 문화에 최적화**: “오늘 갑자기 땡기는데, 근처에서 배드민턴?” 같은 *즉흥 수요*를 바로 연결.
- **사용자 친화적 서비스**: 커뮤니티, 친구 기능 등으로 사용자 친화적 중심

## 프로젝트 목적

- **시간적 제약 해소**: 현대인은 일과 학업, 개인 일정 등으로 인해 운동 시간을 확보하기 어렵다.
- **파트너 확보 용이**: 함께 운동할 파트너를 찾기 어려운 문제를 해결한다.
- **정기 모임 부담 완화**: 동호회나 동아리에 가입하지 않아도, 사용자가 원하는 시간에 운동 가능.
- **순수 운동 경험 제공**: 기존 남녀 만남 중심의 서비스와 달리, 순수하게 운동만 즐기고자 하는 사용자 요구 충족.

---

# 2. 프로젝트 주요기능

- **⚡ 실시간 매칭**  
&nbsp;&nbsp;&nbsp;- 사용자가 입력한 시간대·종목 조건에 맞는 파트너 자동 매칭<br>
&nbsp;&nbsp;&nbsp;- 원하는 시간대에 즉시 운동 인원 확보 가능  

- **💬 실시간 채팅**  
&nbsp;&nbsp;&nbsp;- 매칭된 사용자 혹은 친구 간 1:1 / 그룹 대화 가능<br>
&nbsp;&nbsp;&nbsp;- 약속 시간·장소 조율 및 세부사항 공유  

- **📝 게시판**  
&nbsp;&nbsp;&nbsp;- 자유 게시판을 통한 소통 및 정보 공유<br>
&nbsp;&nbsp;&nbsp;- 운동 후기, 모임 모집, 꿀팁 작성 가능  

- **👤 마이페이지**  
&nbsp;&nbsp;&nbsp;- 개인 정보 및 선호 종목 관리<br>
&nbsp;&nbsp;&nbsp;- 매칭·참여 이력 조회 및 활동 기록 확인  

- **🔐 회원관리**  
&nbsp;&nbsp;&nbsp;- 회원가입, 로그인, 권한 관리 제공<br>
&nbsp;&nbsp;&nbsp;- 신고 등 안전한 서비스 운영 지원  

- **🤝 친구 기능**  
&nbsp;&nbsp;&nbsp;- 관심 있는 사용자 친구 추가 가능<br>
&nbsp;&nbsp;&nbsp;- 재매칭 및 활동 알림 제공  

---

# 3. 프로젝트 기대 효과

## 1. 시간적 제약 해소

사용자는 별도의 정기 모임이나 동호회에 얽매이지 않고, **자신이 가능한 시간에 맞춰 즉시 운동 파트너를 찾을 수 있다.**

---

## 2. 참여 장벽 완화

- 복잡한 가입 절차, 모임 승인, 지속적인 출석 부담이 없다.
- 원하는 조건(종목·시간·지역·성별)만 입력하면 **자동 매칭 → 자동 채팅방 생성 → 바로 소통**이 가능해 **참여 진입 장벽이 크게 낮아진다.**

---

## 3. 사회적 연결망 확장

- 일회성 매칭에 그치지 않고, **친구 등록·커뮤니티 기능**을 통해 자연스러운 네트워킹 기회를 제공한다.
- 기존 동호회 중심의 배타적 관계와 달리, **유연하면서도 선택적인 연결**을 만들 수 있다.


# 4. 기술 스택

### BACKEND


![java](https://github.com/user-attachments/assets/a9cd03e7-07d6-477e-b3dd-32e7a6ae1e08)
![jpa](https://github.com/user-attachments/assets/dd9fdaec-6850-4401-9c67-af2da34ddf5d) 
<img src="https://img.shields.io/badge/Maven-C71A36?style=for-the-badge&logo=apachemaven&logoColor=white">
![jwt](https://github.com/user-attachments/assets/83bddf8b-d556-4e60-8391-2074704103c4)
<img src="https://img.shields.io/badge/SpringBoot-10B146?style=for-the-badge&logo=SpringBoot&logoColor=white">
<img src="https://img.shields.io/badge/SpringSecurity-3B66BC?style=for-the-badge&logo=SpringSecurity&logoColor=white">

<br>

### FRONTEND
  
<img src="https://img.shields.io/badge/HTML5-E34F26?style=for-the-badge&logo=html5&logoColor=white"> <img src="https://img.shields.io/badge/CSS3-1572B6?style=for-the-badge&logo=css3&logoColor=white">
<img src="https://img.shields.io/badge/JavaScript-F7DF1E?style=for-the-badge&logo=javascript&logoColor=black">
<img src="https://img.shields.io/badge/Vue.js-4FC08D?style=for-the-badge&logo=vue.js&logoColor=white">
<img src="https://img.shields.io/badge/Pinia-F8E162?style=for-the-badge&logo=pinia&logoColor=black">
<img src="https://img.shields.io/badge/Bootstrap-7952B3?style=for-the-badge&logo=bootstrap&logoColor=white">
<img src="https://img.shields.io/badge/VS%20Code-007ACC?style=for-the-badge&logo=visualstudiocode&logoColor=white">

<br>


### DATABASE


![redis](https://github.com/user-attachments/assets/df929d81-ce2f-4853-97fd-cdf7bf45907e) ![mariadb](https://github.com/user-attachments/assets/19a0ad09-804d-4303-80bd-32cafdae0e6f)



<br>

### ETC


![postman](https://github.com/user-attachments/assets/4bcd5043-6841-4cd1-b864-dec4dc39f918)
<img src="https://img.shields.io/badge/SSE-FF6F00?style=for-the-badge&logo=serverfault&logoColor=white">
<img src="https://img.shields.io/badge/WebSocket-008080?style=for-the-badge&logo=socket.io&logoColor=white">


<br>

### IDE


![intellij](https://github.com/user-attachments/assets/25d426ed-e30e-4619-9968-11375adba8b9)

<br><br>

# 5. 아키텍쳐

<img width="1930" height="1172" alt="image" src="https://github.com/user-attachments/assets/17988b3a-8b03-4572-8746-1618733b0e08" />

---

# 6. 요구사항 정의서

[요구사항 정의서](https://docs.google.com/spreadsheets/d/1293Cmz0EkIeH163VswqcNQPK-0b8Cr8gXvtHyckqLN8/edit?gid=0#gid=0&fvid=1857363008)

<img width="1188" height="753" alt="image" src="https://github.com/user-attachments/assets/a27574d1-482f-4735-bb4f-5f8843e8fd44" />

---

# 7. 테이블 명세서

[테이블 명세서](https://docs.google.com/spreadsheets/d/1293Cmz0EkIeH163VswqcNQPK-0b8Cr8gXvtHyckqLN8/edit?gid=99972625#gid=99972625)

<img width="924" height="691" alt="image" src="https://github.com/user-attachments/assets/50838fc5-ea4f-454b-8a4c-05bd6161605c" />

---

# 8. ERD

[ERD](https://www.erdcloud.com/d/mrc7T5gfD8iZbYr8P)

<img width="1506" height="714" alt="image" src="https://github.com/user-attachments/assets/61614c7d-d75d-4fed-b36b-e297d1ece750" />

---

# 9. API 명세서

[API 명세서](https://www.notion.so/API-24f80955955380be8f0deade40d16847)

<img width="1543" height="856" alt="image" src="https://github.com/user-attachments/assets/9eaf974a-32ce-4e7c-ae53-a2fdedb28a8d" />

---
