package app;

import model.Admin;
import model.Bet;
import model.Game;
import model.Participant;
import model.Player;
import model.PostGame;
import model.Score;
import model.Team;
import model.User;
import model.enums.Position;
import repository.InMemoryRepository;
import repository.Repository;
import service.PointsConfig;
import service.RankingEntry;
import service.RankingService;
import service.ScoreCalculator;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Main {
    private final Scanner scanner = new Scanner(System.in);

    private final Repository<User> users = new InMemoryRepository<>();
    private final Repository<Team> teams = new InMemoryRepository<>();
    private final Repository<Game> games = new InMemoryRepository<>();

    private final ScoreCalculator calculator = new ScoreCalculator();
    private final RankingService rankingService = new RankingService();
    private final PointsConfig pointsConfig = new PointsConfig();

    private long userSeq = 1;
    private long teamSeq = 1;
    private long gameSeq = 1;

    public static void main(String[] args) {
        new Main().run();
    }

    private void run() {
        // todo bolão começa com um admin
        users.save(new Admin(userSeq++, "Administrador"));

        boolean running = true;
        while (running) {
            System.out.println("\n=== BOLÃO DA COPA 2026 ===");
            System.out.println("1 - Entrar como Admin");
            System.out.println("2 - Entrar como Participante");
            System.out.println("9 - Carregar dados de exemplo");
            System.out.println("0 - Sair");
            switch (readInt("Opção: ")) {
                case 1 -> adminMenu();
                case 2 -> participantMenu();
                case 9 -> seedDemo();
                case 0 -> running = false;
                default -> System.out.println("Opção inválida.");
            }
        }
        System.out.println("Até mais!");
    }

    // ===================== ADMIN =====================

    private void adminMenu() {
        boolean back = false;
        while (!back) {
            System.out.println("\n--- ADMIN ---");
            System.out.println("1 - Cadastrar seleção (com jogadores)");
            System.out.println("2 - Cadastrar participante");
            System.out.println("3 - Cadastrar jogo");
            System.out.println("4 - Finalizar jogo (lançar resultado)");
            System.out.println("5 - Ver ranking");
            System.out.println("0 - Voltar");
            switch (readInt("Opção: ")) {
                case 1 -> createTeam();
                case 2 -> createParticipant();
                case 3 -> createGame();
                case 4 -> finishGame();
                case 5 -> showRanking();
                case 0 -> back = true;
                default -> System.out.println("Opção inválida.");
            }
        }
    }

    private void createTeam() {
        String name = readLine("Nome da seleção: ");
        Team team = teams.save(new Team(teamSeq++, name));
        System.out.println("Adicione jogadores (nome vazio para terminar).");
        while (true) {
            String playerName = readLine("Nome do jogador: ");
            if (playerName.isEmpty()) {
                break;
            }
            Position position = choosePosition();
            team.addPlayer(new Player(playerName, team, position));
        }
        System.out.println("Seleção cadastrada: " + team + " (" + team.getPlayers().size() + " jogadores)");
    }

    private void createParticipant() {
        String name = readLine("Nome do participante: ");
        User participant = users.save(new Participant(userSeq++, name));
        System.out.println("Cadastrado: " + participant);
    }

    private void createGame() {
        List<Team> all = teams.findAll();
        if (all.size() < 2) {
            System.out.println("Cadastre ao menos 2 seleções primeiro.");
            return;
        }
        System.out.println("Seleção A:");
        Team teamA = chooseFrom(all, "Escolha: ");
        System.out.println("Seleção B:");
        Team teamB = chooseFrom(all, "Escolha: ");
        if (teamA == null || teamB == null || teamA.getId().equals(teamB.getId())) {
            System.out.println("Seleções inválidas.");
            return;
        }
        Game game = games.save(new Game(gameSeq++, teamA, teamB));
        System.out.println("Jogo criado: " + game);
    }

    private void finishGame() {
        List<Game> open = openGames();
        if (open.isEmpty()) {
            System.out.println("Nenhum jogo aberto para finalizar.");
            return;
        }
        Game game = chooseFrom(open, "Jogo a finalizar: ");
        if (game == null) {
            System.out.println("Jogo inválido.");
            return;
        }
        System.out.println("Informe os gols reais do jogo:");
        List<Score> scores = readScores(game);
        Team winner = deriveWinner(game, scores);
        PostGame result = new PostGame(game, winner, scores);
        game.finish(result);

        // calcula e armazena a pontuação de cada palpite desse jogo
        for (User user : users.findAll()) {
            if (user instanceof Participant participant) {
                for (Bet bet : participant.getBets()) {
                    if (bet.getGame().getId().equals(game.getId())) {
                        bet.setPoints(calculator.calculate(bet, result, pointsConfig));
                    }
                }
            }
        }
        System.out.println(result);
        System.out.println("Pontuações atualizadas.");
    }

    // ===================== PARTICIPANTE =====================

    private void participantMenu() {
        List<Participant> participants = participants();
        if (participants.isEmpty()) {
            System.out.println("Nenhum participante cadastrado. Peça ao admin para cadastrar.");
            return;
        }
        Participant participant = chooseFrom(participants, "Quem é você? ");
        if (participant == null) {
            System.out.println("Participante inválido.");
            return;
        }
        boolean back = false;
        while (!back) {
            System.out.println("\n--- " + participant.getName() + " ---");
            System.out.println("1 - Fazer palpite");
            System.out.println("2 - Ver meus palpites e pontos");
            System.out.println("3 - Ver ranking");
            System.out.println("0 - Voltar");
            switch (readInt("Opção: ")) {
                case 1 -> placeBet(participant);
                case 2 -> showBets(participant);
                case 3 -> showRanking();
                case 0 -> back = true;
                default -> System.out.println("Opção inválida.");
            }
        }
    }

    private void placeBet(Participant participant) {
        List<Game> open = openGames();
        if (open.isEmpty()) {
            System.out.println("Nenhum jogo aberto para palpitar.");
            return;
        }
        Game game = chooseFrom(open, "Jogo: ");
        if (game == null) {
            System.out.println("Jogo inválido.");
            return;
        }
        System.out.println("Quem você acha que vai marcar?");
        List<Score> scores = readScores(game);
        Team winner = deriveWinner(game, scores);
        Bet bet = new Bet(participant, game, scores, winner);
        participant.addBet(bet);
        System.out.println("Palpite registrado para o " + game);
    }

    private void showBets(Participant participant) {
        List<Bet> bets = participant.getBets();
        if (bets.isEmpty()) {
            System.out.println("Você ainda não fez nenhum palpite.");
            return;
        }
        System.out.println("--- Seus palpites ---");
        for (Bet bet : bets) {
            System.out.println(bet);
        }
        System.out.println("Total: " + participant.getTotalPoints() + " ponto(s)");
    }

    private void showRanking() {
        List<RankingEntry> entries = rankingService.ranking(participants());
        if (entries.isEmpty()) {
            System.out.println("Sem participantes no ranking.");
            return;
        }
        System.out.println("\n=== RANKING ===");
        int position = 1;
        for (RankingEntry entry : entries) {
            System.out.println(position++ + "º " + entry);
        }
    }

    // ===================== HELPERS =====================

    /** Coleta uma lista de gols (jogador + quantidade) para um jogo. */
    private List<Score> readScores(Game game) {
        List<Player> players = new ArrayList<>();
        players.addAll(game.getTeamA().getPlayers());
        players.addAll(game.getTeamB().getPlayers());

        List<Score> scores = new ArrayList<>();
        if (players.isEmpty()) {
            System.out.println("(As seleções não têm jogadores cadastrados.)");
            return scores;
        }
        while (true) {
            System.out.println("Jogadores:");
            for (int i = 0; i < players.size(); i++) {
                System.out.println((i + 1) + " - " + players.get(i));
            }
            int idx = readInt("Marcou gol (0 para terminar): ");
            if (idx == 0) {
                break;
            }
            if (idx < 1 || idx > players.size()) {
                System.out.println("Jogador inválido.");
                continue;
            }
            Player player = players.get(idx - 1);
            int quantity = readInt("Quantos gols? ");
            if (quantity <= 0) {
                System.out.println("Quantidade inválida.");
                continue;
            }
            scores.add(new Score(game, player.getTeam(), player, quantity));
        }
        return scores;
    }

    /** Define o vencedor a partir do placar (soma dos gols por equipe). */
    private Team deriveWinner(Game game, List<Score> scores) {
        int goalsA = ScoreCalculator.goalsOf(scores, game.getTeamA());
        int goalsB = ScoreCalculator.goalsOf(scores, game.getTeamB());
        if (goalsA > goalsB) {
            return game.getTeamA();
        }
        if (goalsB > goalsA) {
            return game.getTeamB();
        }
        return null; // empate
    }

    private Position choosePosition() {
        Position[] values = Position.values();
        for (int i = 0; i < values.length; i++) {
            System.out.println((i + 1) + " - " + values[i]);
        }
        int idx = readInt("Posição: ");
        if (idx >= 1 && idx <= values.length) {
            return values[idx - 1];
        }
        System.out.println("Posição inválida, assumindo " + values[0]);
        return values[0];
    }

    private List<Participant> participants() {
        List<Participant> list = new ArrayList<>();
        for (User user : users.findAll()) {
            if (user instanceof Participant participant) {
                list.add(participant);
            }
        }
        return list;
    }

    private List<Game> openGames() {
        List<Game> list = new ArrayList<>();
        for (Game game : games.findAll()) {
            if (game.isOpen()) {
                list.add(game);
            }
        }
        return list;
    }

    private <T> T chooseFrom(List<T> list, String prompt) {
        if (list.isEmpty()) {
            return null;
        }
        for (int i = 0; i < list.size(); i++) {
            System.out.println((i + 1) + " - " + list.get(i));
        }
        int idx = readInt(prompt);
        if (idx >= 1 && idx <= list.size()) {
            return list.get(idx - 1);
        }
        return null;
    }

    private int readInt(String prompt) {
        System.out.print(prompt);
        String line = scanner.nextLine().trim();
        try {
            return Integer.parseInt(line);
        } catch (NumberFormatException e) {
            return -1;
        }
    }

    private String readLine(String prompt) {
        System.out.print(prompt);
        return scanner.nextLine().trim();
    }

    /** Popula dados de exemplo para facilitar os testes / a demonstração. */
    private void seedDemo() {
        Team brasil = teams.save(new Team(teamSeq++, "Brasil"));
        brasil.addPlayer(new Player("Alisson", brasil, Position.GOALKEEPER));
        brasil.addPlayer(new Player("Marquinhos", brasil, Position.DEFENDER));
        brasil.addPlayer(new Player("Neymar", brasil, Position.FORWARD));

        Team argentina = teams.save(new Team(teamSeq++, "Argentina"));
        argentina.addPlayer(new Player("Martinez", argentina, Position.GOALKEEPER));
        argentina.addPlayer(new Player("Otamendi", argentina, Position.DEFENDER));
        argentina.addPlayer(new Player("Messi", argentina, Position.FORWARD));

        games.save(new Game(gameSeq++, brasil, argentina));

        users.save(new Participant(userSeq++, "João"));
        users.save(new Participant(userSeq++, "Maria"));

        System.out.println("Dados de exemplo carregados (2 seleções, 1 jogo, 2 participantes).");
    }
}
