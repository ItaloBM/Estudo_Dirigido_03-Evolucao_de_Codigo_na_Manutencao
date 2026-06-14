package refatorado;

import java.time.LocalDateTime;

/**
 * Classe utilitária responsável exclusivamente pelo cálculo
 * de cobrança (billing) de chamados de suporte.
 *
 * Princípio aplicado: Single Responsibility Principle (SRP).
 */
public final class BillingCalculator {

    private static final double VALOR_REQUISICAO = 50.0;
    private static final double VALOR_MUDANCA = 200.0;
    private static final double VALOR_DESCONHECIDO = 0.0;

    private static final double MULTIPLICADOR_URGENCIA_PADRAO = 1.5;

    private static final double TAXA_FORA_HORARIO_COMERCIAL = 0.35;
    private static final int INICIO_HORARIO_COMERCIAL = 8;
    private static final int FIM_HORARIO_COMERCIAL = 18;

    private BillingCalculator() {
        // Classe utilitária — construtor privado impede instanciação.
    }

    /**
     * Calcula o valor total de cobrança do chamado, considerando
     * tipo, urgência e taxa de horário não comercial.
     *
     * @param chamado Chamado a ser precificado.
     * @return Valor total em reais (R$).
     */
    public static double calcularValorTotal(Chamado chamado) {
        double valorBase = calcularValorBase(chamado);
        double valorComUrgencia = aplicarMultiplicadorUrgencia(valorBase, chamado);
        return aplicarTaxaForaHorarioComercial(valorComUrgencia, chamado.getDataAbertura());
    }

    /**
     * Verifica se o chamado foi registrado fora do horário comercial.
     *
     * @param dataHora Data/hora do registro do chamado.
     * @return {@code true} se estiver fora do expediente (antes das 08h ou após as 18h).
     */
    public static boolean isForaDoHorarioComercial(LocalDateTime dataHora) {
        int hora = dataHora.getHour();
        return hora < INICIO_HORARIO_COMERCIAL || hora > FIM_HORARIO_COMERCIAL;
    }

    // --- Métodos privados auxiliares ---

    private static double calcularValorBase(Chamado chamado) {
        switch (chamado.getTipo()) {
            case INCIDENTE:
                NivelPrioridade nivel = NivelPrioridade.avaliarPorImpacto(chamado.getImpacto());
                return nivel.getValorBase();
            case REQUISICAO:
                return VALOR_REQUISICAO;
            case MUDANCA:
                return VALOR_MUDANCA;
            default:
                return VALOR_DESCONHECIDO;
        }
    }

    private static double aplicarMultiplicadorUrgencia(double valor, Chamado chamado) {
        if (!chamado.isUrgente()) {
            return valor;
        }
        if (chamado.getTipo() == TipoChamado.INCIDENTE) {
            NivelPrioridade nivel = NivelPrioridade.avaliarPorImpacto(chamado.getImpacto());
            return valor * nivel.getMultiplicadorUrgencia();
        }
        return valor * MULTIPLICADOR_URGENCIA_PADRAO;
    }

    private static double aplicarTaxaForaHorarioComercial(double valor, LocalDateTime dataHora) {
        if (isForaDoHorarioComercial(dataHora)) {
            return valor + (valor * TAXA_FORA_HORARIO_COMERCIAL);
        }
        return valor;
    }
}
