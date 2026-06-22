# Diagrama de Classes — Bolão da Copa 2026

Diagrama fiel ao código em `src/`. Recursos de POO exigidos pelo desafio:
**[H]** herança · **[P]** polimorfismo · **[I]** interface · **[AGR]** agregação · **[COMP]** composição · **[G]** generics

> **Como visualizar bonito no VSCode:** instale a extensão
> **"Markdown Preview Mermaid Support"** (`bierner.markdown-mermaid`) ou
> **"Mermaid Chart"** (`MermaidChart.vscode-mermaid-chart`) e abra o preview do Markdown
> (`Cmd+Shift+V`). O bloco abaixo é renderizado automaticamente como diagrama.

```mermaid
classDiagram
    direction LR

    %% ---------- CONTRATOS ----------
    class Identifiable {
        <<interface>>
        +getId() Long
    }

    class Repository~T~ {
        <<interface>>
        +save(T) T
        +findAll() List~T~
        +findById(Long) Optional~T~
    }
    class InMemoryRepository~T~ {
        -items List~T~
        +save(T) T
        +findAll() List~T~
        +findById(Long) Optional~T~
    }

    %% ---------- USUÁRIOS [H][P] ----------
    class User {
        <<abstract>>
        -id Long
        -name String
        +getId() Long
        +getName() String
        +getRole()* String
        +toString() String
    }
    class Admin {
        +getRole() String
    }
    class Participant {
        -bets List~Bet~
        +getRole() String
        +getBets() List~Bet~
        +addBet(Bet) void
        +getTotalPoints() int
    }

    %% ---------- SELEÇÃO E JOGADORES ----------
    class Team {
        -id Long
        -name String
        -players List~Player~
        +getId() Long
        +getName() String
        +getPlayers() List~Player~
        +addPlayer(Player) void
    }
    class Player {
        -name String
        -team Team
        -position Position
        +getName() String
        +getTeam() Team
        +getPosition() Position
    }
    class Position {
        <<enum>>
        GOALKEEPER(5)
        DEFENDER(4)
        FULLBACK(3)
        MIDFIELDER(2)
        FORWARD(1)
        -weight int
        +getWeight() int
    }

    %% ---------- JOGOS ----------
    class Game {
        -id Long
        -teamA Team
        -teamB Team
        -status GameStatus
        -result PostGame
        +finish(PostGame) void
        +isOpen() boolean
        +getResult() PostGame
    }
    class GameStatus {
        <<enum>>
        OPEN
        FINISHED
    }
    class PostGame {
        -game Game
        -winner Team
        -scores List~Score~
        +getWinner() Team
        +getScores() List~Score~
    }
    class Score {
        -game Game
        -team Team
        -player Player
        -quantity int
        +getTeam() Team
        +getPlayer() Player
        +getQuantity() int
    }

    %% ---------- PALPITE ----------
    class Bet {
        -participant Participant
        -game Game
        -predictedScores List~Score~
        -predictedWinner Team
        -points int
        +getPredictedScores() List~Score~
        +getPredictedWinner() Team
        +getPoints() int
        +setPoints(int) void
    }

    %% ---------- PONTUAÇÃO [I][P] ----------
    class ScoringRule {
        <<interface>>
        +calculate(Bet, PostGame, PointsConfig) int
    }
    class WinnerRule
    class TeamGoalsRule
    class ExactScoreRule
    class ScorerRule
    class PointsConfig {
        -winnerPoints int
        -teamGoalsPoints int
        -exactScorePoints int
        -scorerBasePoints int
    }
    class ScoreCalculator {
        -rules List~ScoringRule~
        +calculate(Bet, PostGame, PointsConfig) int
        +goalsOf(List~Score~, Team)$ int
    }

    %% ---------- RANKING ----------
    class RankingService {
        +ranking(List~Participant~) List~RankingEntry~
    }
    class RankingEntry {
        -participant Participant
        -totalPoints int
        +getTotalPoints() int
    }

    %% ---------- RELAÇÕES ----------
    User ..|> Identifiable
    Team ..|> Identifiable
    Game ..|> Identifiable
    Repository ..|> Identifiable : T extends
    InMemoryRepository ..|> Repository : [G][P]

    User <|-- Admin : [H]
    User <|-- Participant : [H]
    Participant o-- "*" Bet : [AGR]

    Team *-- "*" Player : [COMP]
    Player --> Position
    Player --> Team

    Game --> GameStatus
    Game o-- "2" Team : [AGR] teamA/teamB
    Game *-- "0..1" PostGame : [COMP] result
    PostGame o-- "0..1" Team : winner
    PostGame *-- "*" Score : [COMP]
    Score --> Player
    Score --> Team

    Bet --> Participant
    Bet --> Game
    Bet o-- "0..1" Team : predictedWinner
    Bet *-- "*" Score : [COMP] predictedScores

    ScoringRule <|.. WinnerRule : [I][P]
    ScoringRule <|.. TeamGoalsRule : [I][P]
    ScoringRule <|.. ExactScoreRule : [I][P]
    ScoringRule <|.. ScorerRule : [I][P]
    ScoreCalculator o-- "*" ScoringRule : [AGR][P]
    ScoreCalculator ..> PointsConfig
    ScorerRule ..> PointsConfig
    ScorerRule ..> Position : usa weight

    RankingService ..> Participant
    RankingService ..> RankingEntry
    RankingEntry --> Participant
```

---

## Mapa rápido dos pacotes (`src/`)

| Pacote | Classes |
|---|---|
| `model` | `Identifiable` (interface) |
| `model.user` | `User` (abstract), `Admin`, `Participant` |
| `model.team` | `Team`, `Player` |
| `model.game` | `Game`, `PostGame`, `Score` |
| `model.bet` | `Bet` |
| `model.enums` | `Position`, `GameStatus` |
| `service` | `ScoringRule` (interface), `WinnerRule`, `TeamGoalsRule`, `ExactScoreRule`, `ScorerRule`, `PointsConfig`, `ScoreCalculator`, `RankingService`, `RankingEntry` |
| `repository` | `Repository<T>` (interface), `InMemoryRepository<T>` |
| `app` | `Main` |

## Onde cada conceito de POO aparece

- **[H] Herança** — `Admin` e `Participant` estendem `User` (abstrata).
- **[P] Polimorfismo** — `getRole()` sobrescrito; as 4 `ScoringRule` intercambiáveis em `ScoreCalculator`; `InMemoryRepository` via interface `Repository`.
- **[I] Interface** — `Identifiable`, `Repository<T>`, `ScoringRule`.
- **[AGR] Agregação** — `Participant` ↔ `Bet`; `Game` ↔ `Team`; `ScoreCalculator` ↔ `ScoringRule`.
- **[COMP] Composição** — `Team` → `Player`; `Game` → `PostGame`; `PostGame`/`Bet` → `Score`.
- **[G] Generics** — `Repository<T extends Identifiable>` e `InMemoryRepository<T>`.

## Regras de pontuação (resumo)

| Regra | Pontua quando | Valor padrão (`PointsConfig`) |
|---|---|---|
| `WinnerRule` | acertou a seleção vencedora (ou o empate) | `winnerPoints = 5` |
| `TeamGoalsRule` | acertou o nº de gols de cada equipe (soma por equipe) | `teamGoalsPoints = 3` |
| `ExactScoreRule` | cravou o placar completo das duas equipes | `exactScorePoints = 10` |
| `ScorerRule` | acertou gols de um jogador → `base × peso da posição` | `scorerBasePoints = 2` |
