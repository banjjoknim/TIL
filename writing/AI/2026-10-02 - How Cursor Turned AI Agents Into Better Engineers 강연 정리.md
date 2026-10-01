# How Cursor Turned AI Agents Into Better Engineers 강연 정리

Cursor 의 Lauren Tan 이 진행한 강연 「How Cursor Turned AI Agents Into Better Engineers」의 내용을 정리한 문서입니다.

## 출처
- 영상 : [How Cursor Turned AI Agents Into Better Engineers (YouTube)](https://www.youtube.com/watch?v=Cmoh-yR-usA)
- Maven 소개 페이지 : [무료 라이트닝 레슨, 60분, 2026-08-12](https://maven.com/p/e23d9c/how-cursor-turned-ai-agents-into-better-engineers)
- 발표자 : Lauren Tan (Cursor, 닉네임 poteto. 전 Meta React 컴파일러 팀, 전 Netflix 소속)
- 진행자 : Colin Matthews (Lenny's Newsletter 교육 담당)

> **참고** : 이 정리는 영상을 직접 시청하지 않고 아래의 2차 자료를 바탕으로 작성했습니다. 따라서 세부 표현은 영상과 다를 수 있습니다.
> - The Neuron : [pstack explained](https://www.theneuron.ai/explainer-articles/pstack-explained-lauren-tans-system-for-trustworthy-ai-agents/)
> - GitHub : [agent-coding-stack-playbook](https://github.com/cappellaadrian/agent-coding-stack-playbook)
> - 영상 요약 : [Cmoh-yR-usA](https://youtubesummary.com/summary/Cmoh-yR-usA), [EWSUvEyFwjc](https://youtubesummary.com/summary/EWSUvEyFwjc)

## 챕터 (Maven 페이지 기준)
| 시각 | 챕터 |
| --- | --- |
| 3:58 | Agent Trust Curve |
| 8:35 | 검증 기술과 Feature Maps |
| 16:20 | pstack 소개 |
| 20:04 | Evals 로 유지보수 |
| 26:34 | 클라우드 에이전트로 확장 |
| 38:38 | 코드베이스 리팩토링과 가드레일 |
| 41:00 | PR 크기와 작업 구조 |
| 50:38 | CI 제약과 Dune 아키텍처 |
| 55:58 | 토큰 사용량, ROI, 비용 |
| - | Grokbot 으로 제품팀에 권한 주기 |

## 핵심 내용

### 1. 신뢰 곡선 (Agent Trust Curve)
- 에이전트에 대한 신뢰가 낮으면, 사람이 에이전트를 마이크로 관리하게 되고 작업을 병렬화하지 못합니다.
- 검증 능력을 쌓을수록 다음 단계로 올라갑니다.
  - 사람이 하나하나 확인 → 로컬 검증 → 클라우드 에이전트 → 자동 병합 → 병합 뒤 감사
- Cursor 는 약 5개월 만에 사람이 완전히 개입하는 단계에서 PR 을 자동으로 병합하는 단계까지 도달했습니다. 하룻밤 사이에 PR 약 20개가 문제없이 병합된 사례도 소개했습니다.

### 2. 검증 스킬
- 「테스트 통과」는 증거가 아닙니다.
- 에이전트는 다음 순서로 검증합니다.
  - **Doctor** (환경 점검) → **Launch** (앱 실행) → **Drive** (기능 조작) → **Evidence** (증거 수집) → **Cleanup** (정리)
- 증거로는 스크린샷, 로그, CPU 트레이스, 힙 스냅샷 등을 남깁니다.
- 웹과 Electron 앱은 Chrome DevTools Protocol 을, iOS 앱은 Apple 시뮬레이터 도구를 사용합니다. Cursor 내부에서는 「Glass」라는 UI 제어 창을 사용합니다.
- 인용 취지 : 검증이 좋은 코드를 보장하지는 않지만, 「실제로 동작하는 것」과 「환각된 의도」를 구분해 주기 때문에 정확성을 크게 높입니다.

### 3. 기능 지도 (Feature Map)
- 사용자에게 보이는 기능마다 다음 내용을 짧게 기록한 색인입니다.
  - 도달 방법
  - 조작 방법 (키보드 단축키, DOM 선택자 등)
  - 에이전트가 빠지기 쉬운 함정
- 「구체화한 기억」이라는 개념을 따릅니다. 제품의 전체 이력을 프롬프트에 넣지 않고 지금 존재하는 기능만 기록합니다. 앱 자체가 정본이고, 기능 지도는 그 앱을 가리키는 값싼 색인입니다.
- 기능 지도가 없으면 에이전트가 기능의 위치를 찾느라 헤맵니다.
- 사용자의 버그 보고를 실행 가능한 재현 단계로 바꾸는 데에도 활용합니다.

### 4. pstack (Potato Stack)
- 여러 스킬을 묶은 플러그인입니다. 공식 소스는 Cursor 의 공개 플러그인 저장소 안에 있는 [pstack 폴더](https://github.com/cursor/plugins/tree/main/pstack)입니다.
- 철학 : 「추측하지 말고 실제 코드를 찾아서 확인하고, 서브에이전트를 활용하라」
- 스킬 예시 : `/how`, `/why`, `/recall`, `/teach`, `/architect`, `/arena`, `/swarm`, `/interrogate`, `/tdd`, `/create-verification-skill`, `/unslop`
- Cursor 팀에서 주당 1만 번 사용되고 있으며, Lauren 은 월 약 2,000개의 PR 을 제출했습니다(8월에는 2,462개).
- 한 외부 시험에서는 기본 설정이 30분 걸린 작업에 60분과 더 많은 토큰을 소모했지만, 기본 설정이 놓친 환각 오류 3개를 잡아냈습니다.

### 5. Evals
- Evals 는 「에이전트 스킬을 위한 단위 테스트」입니다.
- 코디네이터 에이전트가 채점표(루브릭)를 만들고, 서브에이전트를 격리된 폴더에서 실행해서 서브에이전트가 평가 중이라는 사실을 모르게 합니다.
- 여러 모델로 성능 표를 만들고, 점수가 목표(예 : 10/10)에 도달할 때까지 스킬을 고칩니다.

### 6. 클라우드 에이전트
- 로컬에서 검증 결과를 직접 확인하여 신뢰를 쌓은 뒤에 클라우드로 확장합니다.
- 각 작업자는 자신의 클라우드 데스크톱에서 의존성 설치, 앱 실행, 증거 수집을 수행합니다.
- 예시 : **Benny** 는 버그 보고를 받으면 재현하고 수정 PR 을 생성합니다. 이미 main 브랜치에서 고쳐진 버그도 찾아냅니다.
- 경고 : 검증 없이 에이전트를 수천 개 띄우면 토큰만 낭비합니다.

### 7. 강한 가드와 약한 규칙
- 되풀이되는 실패 패턴은 규칙 문서(약한 규칙 : 규칙 파일, 리뷰 봇)로 막지 말고, CI, 린트, 정적 검사(강한 가드)로 바꿉니다.
- 사람이 PR 마다 불변 조건을 손으로 지적하는 것은 안티패턴입니다.
- Grokbot 의 Dune 아키텍처 예시
  - React `useEffect` 사용 금지
  - 코드 주석 금지 (에이전트가 엉뚱한 주석을 달기 때문입니다)
  - Electron main 프로세스와 renderer 프로세스의 격리를 의존 그래프 검사로 강제합니다. 렌더러에서 무거운 코드가 실행되면 60 FPS 기준 프레임 예산인 약 16ms 를 넘겨 화면이 끊기기 때문입니다.
- CI 는 일부러 「매우 귀찮게」 만듭니다.

### 8. 리팩토링 가드레일
- 구조가 잘 잡힌 기존 앱(brownfield)에서는 에이전트가 비교적 안전하게 작업하고, 제약이 없는 새 앱이나 바이브 코딩(greenfield)이 가장 위험합니다.
- 에이전트는 지름길을 택해서 구조를 무너뜨리는 경향이 있습니다.
- Grokbot 도 처음에는 바이브 코딩으로 만들었지만, 엄격한 아키텍처와 CI 제약을 도입하도록 리팩토링한 뒤에 리뷰 부담이 크게 줄었습니다.

### 9. 작은 PR
- PR 크기에 상한을 두지는 않지만, 원자적인 단위로 쪼갭니다.
  - 되돌리기 쉽습니다.
  - git 이력이 그대로 에이전트의 맥락이 됩니다.
  - 회귀를 찾기 쉽습니다.
- 코드를 작성하는 주체와 검증하는 주체를 분리합니다.

### 10. 비용
- 가장 큰 모델을 고르기보다 「지능 대비 비용」의 균형을 고려합니다.
- 발표 당일 Grok 4.6 이 공개되었으며, 토큰당 비용은 4.5 와 비슷하다고 언급했습니다.

### 11. Grokbot
- PM 과 디자이너가 메신저와 비슷한 화면에서 작업이나 버그 수정을 요청하면, 에이전트가 작업을 수행하고 PM 이 결과를 검토하고 승인합니다.
- 강한 제약이 걸려 있기 때문에, 기술 역량이 높지 않은 기여자도 안정적으로 배포할 수 있었습니다.

## 핵심 교훈
- 검증 스킬을 만듭니다.
- 기능 지도를 유지합니다.
- 스킬을 evals 로 관리합니다.
- 로컬에서 신뢰를 쌓은 뒤에 규모를 넓힙니다.
- 수동 리뷰를 자동 규칙(CI)으로 바꿉니다.
- 아키텍처 경계를 강제합니다.

## 참고 : 에이전트 게임 스튜디오에 적용할 거리
> agent-game-studio 레포의 세션이 CEO 에게 제출한 분석을 참고용으로 함께 남깁니다.

### 이미 갖춘 것
- QA 리그, 테스트 봇, 프레임 모음표
- 쓰기 훅, 커밋 주인 검사, 코드 자동 검사
- 리뷰어와 QA 분리 (코드를 쓰는 주체 ≠ 검증하는 주체)
- 기간 산정 실측값과 비용 회고

### 빈틈과 적용안 (추천 순서)
1. 게임의 기능 지도를 작성합니다. (기능마다 도달 방법, 조작 방법, 함정)
2. 어댑터마다 게임 검증 스킬(점검 → 실행 → 조작 → 증거 → 정리)을 만들고, 화면에 보이는 변경의 완료 보고에는 증거를 요구합니다.
3. 되풀이되는 반려 사유를 자동 검사(강한 가드)로 바꾸는 경로를 마련합니다.
4. 역할 파일과 스킬을 평가(evals)합니다. 과거에 발생한 사고를 평가 장면으로 사용합니다.
5. 부서와 일감 종류별로 신뢰 단계를 두고, 반려율과 되돌림 횟수에 따라 단계를 올리거나 내립니다.
- 클라우드 에이전트 도입은 보류합니다.

### 대시보드 후보
- **신뢰 판** : 반려율, 되돌림 횟수
- **증거 판** : 그날 병합한 결과물의 캡처
- **검증 범위 판** : 기능 지도 가운데 검증이 닿는 기능의 비율

## 참고 링크
| 구분 | 설명 | 링크 |
| --- | --- | --- |
| 원본 영상 | How Cursor Turned AI Agents Into Better Engineers (YouTube) | https://www.youtube.com/watch?v=Cmoh-yR-usA |
| 강연 소개 | Maven 무료 라이트닝 레슨 소개 페이지 (60분, 2026-08-12) | https://maven.com/p/e23d9c/how-cursor-turned-ai-agents-into-better-engineers |
| 관련 저장소 | pstack 공식 소스 (cursor/plugins 저장소의 pstack 폴더) | https://github.com/cursor/plugins/tree/main/pstack |
| 관련 저장소 | pstack-claude: pstack 을 Claude Code, Codex, OpenCode, Gemini 등에서 쓸 수 있도록 옮긴 비공식 포트 | https://github.com/michael-denyer/pstack-claude |
| 2차 자료 | The Neuron, 「pstack explained: Lauren Tan's system for trustworthy AI agents」 | https://www.theneuron.ai/explainer-articles/pstack-explained-lauren-tans-system-for-trustworthy-ai-agents/ |
| 2차 자료 | GitHub, agent-coding-stack-playbook | https://github.com/cappellaadrian/agent-coding-stack-playbook |
| 2차 자료 | 영상 요약 (Cmoh-yR-usA) | https://youtubesummary.com/summary/Cmoh-yR-usA |
| 2차 자료 | 영상 요약 (EWSUvEyFwjc) | https://youtubesummary.com/summary/EWSUvEyFwjc |
