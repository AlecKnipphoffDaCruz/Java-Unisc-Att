package app;

import model.bet.Bet;
import model.game.Game;
import model.game.PostGame;
import model.game.Score;
import model.team.Player;
import model.team.Team;
import model.user.Admin;
import model.user.Participant;
import model.user.User;
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
        // já deixa o bolão pré-preenchido (seleções, jogadores, jogos e participantes)
        seedDemo();

        printBanner();

        boolean running = true;
        while (running) {
            System.out.println("\n=== BOLÃO DA COPA 2026 ===");
            System.out.println("1 - Entrar como Admin");
            System.out.println("2 - Entrar como Participante");
            System.out.println("0 - Sair");
            switch (readInt("Opção: ")) {
                case 1 -> adminMenu();
                case 2 -> participantMenu();
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
            System.out.println("5 - Ver seleções e jogadores");
            System.out.println("6 - Ver jogos");
            System.out.println("7 - Ver ranking");
            System.out.println("0 - Voltar");
            switch (readInt("Opção: ")) {
                case 1 -> createTeam();
                case 2 -> createParticipant();
                case 3 -> createGame();
                case 4 -> finishGame();
                case 5 -> listTeams();
                case 6 -> listGames();
                case 7 -> showRanking();
                case 0 -> back = true;
                default -> System.out.println("Opção inválida.");
            }
        }
    }

    private void createTeam() {
        String name = readLine("Nome da seleção (vazio para cancelar): ");
        if (name.isEmpty()) {
            System.out.println("Cadastro cancelado.");
            return;
        }
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
        String name = readLine("Nome do participante (vazio para cancelar): ");
        if (name.isEmpty()) {
            System.out.println("Cadastro cancelado.");
            return;
        }
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
        Team teamA = chooseFrom(all, "Escolha (0 para cancelar): ");
        if (teamA == null) {
            System.out.println("Cadastro cancelado.");
            return;
        }
        System.out.println("Seleção B:");
        Team teamB = chooseFrom(all, "Escolha (0 para cancelar): ");
        if (teamB == null) {
            System.out.println("Cadastro cancelado.");
            return;
        }
        if (teamA.getId().equals(teamB.getId())) {
            System.out.println("As duas seleções não podem ser iguais.");
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
        Game game = chooseFrom(open, "Jogo a finalizar (0 para cancelar): ");
        if (game == null) {
            System.out.println("Operação cancelada.");
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
            System.out.println("3 - Ver seleções e jogadores");
            System.out.println("4 - Ver jogos");
            System.out.println("5 - Ver ranking");
            System.out.println("0 - Voltar");
            switch (readInt("Opção: ")) {
                case 1 -> placeBet(participant);
                case 2 -> showBets(participant);
                case 3 -> listTeams();
                case 4 -> listGames();
                case 5 -> showRanking();
                case 0 -> back = true;
                default -> System.out.println("Opção inválida.");
            }
        }
    }

    private void placeBet(Participant participant) {
        // [REGRA] 1 palpite por jogo/pessoa: só mostra jogos abertos ainda não palpitados
        List<Game> available = new ArrayList<>();
        for (Game game : openGames()) {
            if (!hasBet(participant, game)) {
                available.add(game);
            }
        }
        if (available.isEmpty()) {
            System.out.println("Não há jogos abertos para você palpitar (você já palpitou em todos).");
            return;
        }
        Game game = chooseFrom(available, "Jogo (0 para cancelar): ");
        if (game == null) {
            System.out.println("Palpite cancelado.");
            return;
        }
        // trava extra: nunca dois palpites para o mesmo jogo
        if (hasBet(participant, game)) {
            System.out.println("Você já fez um palpite para este jogo. Apenas 1 por jogo.");
            return;
        }
        System.out.println("Quem você acha que vai marcar? (escolha o time e depois o jogador)");
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
            Game game = bet.getGame();
            int goalsA = ScoreCalculator.goalsOf(bet.getPredictedScores(), game.getTeamA());
            int goalsB = ScoreCalculator.goalsOf(bet.getPredictedScores(), game.getTeamB());
            System.out.println(
                    game.getTeamA().getName() + " " + goalsA + " x " + goalsB + " " + game.getTeamB().getName()
                            + "  ->  " + bet.getPoints() + " ponto(s)");
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

    // ===================== LISTAGENS (VER) =====================

    /** Mostra todas as seleções com seus jogadores. */
    private void listTeams() {
        List<Team> all = teams.findAll();
        if (all.isEmpty()) {
            System.out.println("Nenhuma seleção cadastrada.");
            return;
        }
        System.out.println("\n=== SELEÇÕES E JOGADORES ===");
        for (Team team : all) {
            System.out.println("\n" + team.getName() + " (#" + team.getId() + ")");
            if (team.getPlayers().isEmpty()) {
                System.out.println("  (sem jogadores)");
                continue;
            }
            for (Player player : team.getPlayers()) {
                System.out.println("  - " + player);
            }
        }
    }

    /** Mostra todos os jogos, com status e — se finalizado — o resultado. */
    private void listGames() {
        List<Game> all = games.findAll();
        if (all.isEmpty()) {
            System.out.println("Nenhum jogo cadastrado.");
            return;
        }
        System.out.println("\n=== JOGOS ===");
        for (Game game : all) {
            StringBuilder line = new StringBuilder(game.toString());
            if (!game.isOpen() && game.getResult() != null) {
                PostGame result = game.getResult();
                int goalsA = ScoreCalculator.goalsOf(result.getScores(), game.getTeamA());
                int goalsB = ScoreCalculator.goalsOf(result.getScores(), game.getTeamB());
                Team winner = result.getWinner();
                line.append("  ")
                        .append(goalsA).append(" x ").append(goalsB)
                        .append(" -> ")
                        .append(winner == null ? "Empate" : "Vencedor: " + winner.getName());
            }
            System.out.println(line);
        }
    }

    // ===================== HELPERS =====================

    /** O participante já tem palpite para este jogo? */
    private boolean hasBet(Participant participant, Game game) {
        for (Bet bet : participant.getBets()) {
            if (bet.getGame().getId().equals(game.getId())) {
                return true;
            }
        }
        return false;
    }

    /**
     * Coleta os gols de um jogo. Primeiro escolhe-se o TIME, depois o JOGADOR
     * daquele time, e por fim a quantidade de gols.
     */
    private List<Score> readScores(Game game) {
        List<Score> scores = new ArrayList<>();
        boolean noPlayers = game.getTeamA().getPlayers().isEmpty()
                && game.getTeamB().getPlayers().isEmpty();
        if (noPlayers) {
            System.out.println("(As seleções não têm jogadores cadastrados.)");
            return scores;
        }
        while (true) {
            System.out.println("\nDe qual time saiu o gol?");
            System.out.println("1 - " + game.getTeamA().getName());
            System.out.println("2 - " + game.getTeamB().getName());
            int teamChoice = readInt("Time (0 para terminar): ");
            if (teamChoice == 0) {
                break;
            }
            Team team;
            if (teamChoice == 1) {
                team = game.getTeamA();
            } else if (teamChoice == 2) {
                team = game.getTeamB();
            } else {
                System.out.println("Time inválido.");
                continue;
            }
            if (team.getPlayers().isEmpty()) {
                System.out.println("Esse time não tem jogadores cadastrados.");
                continue;
            }
            System.out.println("Jogador de " + team.getName() + ":");
            Player player = chooseFrom(team.getPlayers(), "Jogador: ");
            if (player == null) {
                System.out.println("Jogador inválido.");
                continue;
            }
            int quantity = readInt("Quantos gols? ");
            if (quantity <= 0) {
                System.out.println("Quantidade inválida.");
                continue;
            }
            scores.add(new Score(game, team, player, quantity));
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

    /** Taça da Copa em ASCII art, exibida na abertura. */
    private void printBanner() {
        System.out.println();
        System.out.println("            ___________");
        System.out.println("           '._==_==_=_.'");
        System.out.println("           .-\\:      /-.");
        System.out.println("          | (|:.     |) |");
        System.out.println("           '-|:.     |-'");
        System.out.println("             \\::.    /");
        System.out.println("              '::. .'");
        System.out.println("                ) (");
        System.out.println("              _.' '._");
        System.out.println("             '-------'");
        System.out.println("     =============================");
        System.out.println("        BOLÃO DA COPA 2026  *");
        System.out.println("     =============================");
    }

    /** Popula dados de exemplo para facilitar os testes / a demonstração. */
    private void seedDemo() {
        Team brasil = teams.save(new Team(teamSeq++, "Brasil"));
        brasil.addPlayer(new Player("Alisson", brasil, Position.GOALKEEPER));
        brasil.addPlayer(new Player("Marquinhos", brasil, Position.DEFENDER));
        brasil.addPlayer(new Player("Vinicius Jr", brasil, Position.MIDFIELDER));
        brasil.addPlayer(new Player("Neymar", brasil, Position.FORWARD));

        Team argentina = teams.save(new Team(teamSeq++, "Argentina"));
        argentina.addPlayer(new Player("Martinez", argentina, Position.GOALKEEPER));
        argentina.addPlayer(new Player("Otamendi", argentina, Position.DEFENDER));
        argentina.addPlayer(new Player("De Paul", argentina, Position.MIDFIELDER));
        argentina.addPlayer(new Player("Messi", argentina, Position.FORWARD));

        Team franca = teams.save(new Team(teamSeq++, "França"));
        franca.addPlayer(new Player("Maignan", franca, Position.GOALKEEPER));
        franca.addPlayer(new Player("Saliba", franca, Position.DEFENDER));
        franca.addPlayer(new Player("Tchouameni", franca, Position.MIDFIELDER));
        franca.addPlayer(new Player("Mbappe", franca, Position.FORWARD));

        Team alemanha = teams.save(new Team(teamSeq++, "Alemanha"));
        alemanha.addPlayer(new Player("Neuer", alemanha, Position.GOALKEEPER));
        alemanha.addPlayer(new Player("Rudiger", alemanha, Position.DEFENDER));
        alemanha.addPlayer(new Player("Musiala", alemanha, Position.MIDFIELDER));
        alemanha.addPlayer(new Player("Havertz", alemanha, Position.FORWARD));

        // jogos já pré-preenchidos
        games.save(new Game(gameSeq++, brasil, argentina));
        games.save(new Game(gameSeq++, franca, alemanha));
        games.save(new Game(gameSeq++, brasil, franca));

        users.save(new Participant(userSeq++, "João"));
        users.save(new Participant(userSeq++, "Maria"));

        System.out.println("Dados de exemplo carregados (4 seleções, 3 jogos, 2 participantes).");
    }
}
