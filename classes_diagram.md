# Diagrama de Classes — Bolão da Copa 2026

Guia de modelagem. Os pesos/valores e a lógica ficam por sua conta na implementação.
Marquei onde cada recurso de POO exigido pelo desafio aparece:
[H] herança · [P] polimorfismo · [I] interface · [AGR] agregação · [COMP] composição


## USUÁRIOS  [H] [P]

Usuario  (abstract)
    id
    nome
    + acoesDoMenu()        // abstract -> cada subclasse mostra opções diferentes [P]

    Admin  extends Usuario
        // cadastra seleções, jogadores, jogos; adiciona participantes; lança placar real

    Participante  extends Usuario
        listaPalpites : List<Palpite>     [AGR]
        getPontuacaoTotal()               // = soma dos pontos dos palpites (calculado)


## SELEÇÃO E JOGADORES

Selecao
    nome / nacao
    jogadores : List<Jogador>     [COMP]  // jogador não existe sem a seleção

Jogador
    id
    nome
    posicao : Posicao             // enum

Posicao  (enum)
    GOLEIRO, ZAGUEIRO, LATERAL, MEIA, ATACANTE
    peso                          // posição menos provável de marcar -> peso MAIOR


## JOGOS

Jogo
    id
    status : StatusJogo           // enum: ABERTO / FINALIZADO
    timeA : Selecao               [AGR]  // a seleção existe independente do jogo
    timeB : Selecao               [AGR]
    resultado : ResultadoReal     // null enquanto status == ABERTO

StatusJogo  (enum)
    ABERTO, FINALIZADO


## RESULTADO REAL  (preenchido pelo admin ao finalizar o jogo)

ResultadoReal
    golsTimeA
    golsTimeB
    goleadores : List<Gol>        [COMP]
    getVencedor()                 // derivado do placar (pode dar empate)


## PALPITE  (entidade própria — 1 por jogo, por participante)

Palpite
    jogo : Jogo                   [AGR]
    golsTimeA                     // placar palpitado
    golsTimeB
    goleadores : List<Gol>        [COMP]
    pontos                        // preenchido pelo cálculo após o jogo finalizar

Gol                               // reaproveitado no Palpite e no ResultadoReal
    jogador : Jogador             [AGR]
    quantidade                    // quantos gols (o enunciado pede "e quantos gols farão")


## PONTUAÇÃO  [I] [P]

ConfiguracaoPontuacao             // o grupo define os valores
    pontosVencedor
    pontosNumeroGolsEquipe        // acertou o nº de gols de UMA das equipes
    pontosPlacarExato
    pontosGoleadorBase            // base; multiplicada pelo peso da Posicao

RegraPontuacao  (interface)       [I]
    + calcular(palpite, resultado, config) : int

    // implementações intercambiáveis [P]:
    RegraVencedor          implements RegraPontuacao
    RegraNumeroGolsEquipe  implements RegraPontuacao
    RegraPlacarExato       implements RegraPontuacao
    RegraGoleador          implements RegraPontuacao   // usa posicao.peso * pontosGoleadorBase

CalculadoraPontuacao
    regras : List<RegraPontuacao>     // soma o resultado de todas as regras [P]
    calcularPalpite(palpite, resultado, config) : int


## SERVIÇOS / APP

RankingService
    gerarRanking(participantes) : lista ordenada por pontuação total

Main  (app)
    monta os dados, exibe o menu e direciona Admin/Participante
