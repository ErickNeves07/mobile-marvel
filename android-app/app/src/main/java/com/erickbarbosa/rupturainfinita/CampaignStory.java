package com.erickbarbosa.rupturainfinita;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/** Authored story beats grounded in the project's product lore. */
final class CampaignStory {
    static final class Line {
        final String speakerId;
        final String speakerName;
        final String text;
        final boolean opponent;

        Line(String speakerId, String speakerName, String text, boolean opponent) {
            this.speakerId = speakerId;
            this.speakerName = speakerName;
            this.text = text;
            this.opponent = opponent;
        }
    }

    static final class Scene {
        final String title;
        final String place;
        final List<Line> lines;
        final boolean finale;

        Scene(String title, String place, boolean finale, Line... lines) {
            this.title = title;
            this.place = place;
            this.finale = finale;
            this.lines = Collections.unmodifiableList(Arrays.asList(lines));
        }
    }

    private CampaignStory() { }

    static Scene opening() {
        return new Scene("PRÓLOGO · A CÂMARA ACORDA", "NEXUS MULTIVERSAL", false,
                new Line("senhor-fantastico", "REED RICHARDS",
                        "A Câmara captou seis assinaturas de energia. A estrutura consegue mapear cada variante, mas ainda não consegue mantê-las separadas.", false),
                new Line("doutor-estranho", "DOUTOR ESTRANHO",
                        "Meus selos mantêm as realidades isoladas enquanto a máquina de Reed estabiliza uma assinatura por vez.", false),
                new Line("senhor-fantastico", "REED RICHARDS",
                        "As Joias fragmentadas sustentam a prisão de Thanos. Cada ressonância restaurada também chega até ele.", false),
                new Line("deadpool", "DEADPOOL",
                        "Então vamos salvar o multiverso sem apertar o botão vermelho. Alguém marcou qual é o botão vermelho?", false));
    }

    static Scene afterMission(int chapter) {
        switch (chapter) {
            case 1:
                return new Scene("ECO EM NOVA YORK", "NOVA YORK", false,
                        team("A fenda fechou, mas o mesmo pulso aparece em outros pontos do mapa."),
                        reed("A ruptura não nasceu aqui. Nova York foi só o primeiro lugar onde a leitura ficou forte o bastante."),
                        strange("Vou reforçar os selos. O sinal continua seguindo para o próximo nó."));
            case 2:
                return new Scene("REGISTRO DO COMPLEXO", "COMPLEXO DE ULTRON", false,
                        team("Ultron estava acompanhando a mesma frequência que a Câmara."),
                        opponent("ultron", "ULTRON", "Uma prisão pode ser estudada. Uma prisão também pode ser aberta."),
                        reed("O registro confirma que a ressonância das Joias está alcançando sistemas muito distantes."));
            case 3:
                return new Scene("SINAL SOB CERCO", "WAKANDA", false,
                        team("A ameaça perdeu força quando interrompemos o sinal que atravessava o campo."),
                        reed("A leitura da Câmara caiu. Não era uma fonte isolada: as rupturas estão conectadas."),
                        strange("A Dimensão Espelhada está recebendo o mesmo eco. Vou preparar os selos."));
            case 4:
                return new Scene("O ESPELHO RESPONDE", "DIMENSÃO ESPELHADA", false,
                        team("A energia da fenda tentou copiar nossa formação."),
                        opponent("dormammu", "DORMAMMU", "Toda barreira revela a forma do mundo que tenta esconder."),
                        strange("Ele percebeu a costura entre as realidades. Mantenham o selo ativo."));
            case 5:
                return new Scene("RASTRO ATÉ KNOWHERE", "KNOWHERE", false,
                        team("Ronan estava protegendo o mesmo rastro de energia."),
                        opponent("ronan", "RONAN", "As Joias mudam de mãos. O poder delas nunca fica em silêncio."),
                        reed("A leitura leva ao Instituto Xavier. O próximo pulso está ligado a uma mente poderosa."));
            case 6:
                return new Scene("A RUPTURA GENÉTICA", "INSTITUTO XAVIER", false,
                        team("Magneto sentiu a ruptura antes que nossos instrumentos a localizassem."),
                        opponent("magneto", "MAGNETO", "Vocês chamam isso de contenção. Eu chamo de outra corrente."),
                        strange("A prisão de Thanos continua fechada, mas a ressonância está desgastando os selos."));
            case 7:
                return new Scene("ALÉM DA FRONTEIRA", "ZONA NEGATIVA", false,
                        team("A fenda atravessa até uma realidade que não deveria tocar a nossa."),
                        opponent("annihilus", "ANNIHILUS", "A passagem permanecerá aberta para a conquista."),
                        reed("Restam duas leituras. A mais intensa vem de Latveria."));
            case 8:
                return new Scene("A VERDADE DE DOOM", "LATVERIA", false,
                        opponent("doutor-destino", "DOUTOR DESTINO",
                                "Eu fragmentei as Joias para conter Thanos. Também sei o que farei quando a prisão ceder."),
                        reed("Ele construiu as barreiras. A intenção de fundir realidades nunca foi contenção."),
                        strange("Titã é o último foco. A Câmara precisa manter nossas realidades firmes."));
            case 9:
                return new Scene("EPÍLOGO · A RUPTURA CONTIDA", "TITÃ", true,
                opponent("thanos", "THANOS",
                                "Vocês fecharam a passagem. Não confundam isso com silêncio."),
                        reed("A ruptura imediata está estabilizada. As seis assinaturas voltaram a ficar isoladas."),
                        strange("A Câmara segura as realidades. A prisão de Thanos permanece — por enquanto."),
                        team("A Manopla pulsa. Uma corrente se parte dentro da prisão, e Thanos sorri."),
                        new Line("deadpool", "DEADPOOL",
                                "Fim da crise, começo do próximo problema. Pelo menos desta vez temos um botão vermelho identificado.", false));
            default:
                throw new IllegalArgumentException("Unknown story chapter");
        }
    }

    private static Line team(String text) {
        return new Line("team", "EQUIPE", text, false);
    }

    private static Line reed(String text) {
        return new Line("senhor-fantastico", "REED RICHARDS", text, false);
    }

    private static Line strange(String text) {
        return new Line("doutor-estranho", "DOUTOR ESTRANHO", text, false);
    }

    private static Line opponent(String id, String name, String text) {
        return new Line(id, name, text, true);
    }
}
