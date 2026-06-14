package refatorado;

/**
 * Classe utilitária responsável exclusivamente pela avaliação
 * do SLA (Service Level Agreement) de um chamado.
 *
 * Princípio aplicado: Single Responsibility Principle (SRP).
 */
public final class SlaEvaluator {

    private static final int SLA_REQUISICAO_PADRAO = 72;
    private static final int SLA_REQUISICAO_URGENTE = 24;
    private static final int SLA_MUDANCA = 96;
    private static final int SLA_DESCONHECIDO = 120;

    private SlaEvaluator() {
        // Classe utilitária — construtor privado impede instanciação.
    }

    /**
     * Calcula o SLA em horas para o chamado fornecido.
     *
     * @param chamado Chamado a ser avaliado.
     * @return SLA em horas.
     */
    public static int calcularSlaHoras(Chamado chamado) {
        switch (chamado.getTipo()) {
            case INCIDENTE:
                return calcularSlaIncidente(chamado);
            case REQUISICAO:
                return chamado.isUrgente() ? SLA_REQUISICAO_URGENTE : SLA_REQUISICAO_PADRAO;
            case MUDANCA:
                return SLA_MUDANCA;
            default:
                return SLA_DESCONHECIDO;
        }
    }

    /**
     * Determina o nível de prioridade para um chamado do tipo Incidente.
     *
     * @param chamado Chamado do tipo INCIDENTE.
     * @return NivelPrioridade correspondente ao impacto.
     */
    public static NivelPrioridade determinarPrioridade(Chamado chamado) {
        if (chamado.getTipo() != TipoChamado.INCIDENTE) {
            return null;
        }
        return NivelPrioridade.avaliarPorImpacto(chamado.getImpacto());
    }

    private static int calcularSlaIncidente(Chamado chamado) {
        NivelPrioridade nivel = NivelPrioridade.avaliarPorImpacto(chamado.getImpacto());
        return chamado.isUrgente() ? nivel.getSlaUrgenteHoras() : nivel.getSlaPadraoHoras();
    }
}
