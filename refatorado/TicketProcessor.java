package refatorado;

import java.time.format.DateTimeFormatter;
import java.util.Locale;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Classe orquestradora responsável pelo processamento de chamados
 * de suporte de TI. Delega responsabilidades específicas para
 * classes coesas (SlaEvaluator, BillingCalculator, AuditLogger).
 *
 * Princípios aplicados:
 * - Single Responsibility Principle (SRP)
 * - Separação de Interesses (Separation of Concerns)
 * - Baixo acoplamento e alta coesão
 */
public class TicketProcessor {

    private static final DateTimeFormatter FORMATO_DATA =
            DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");

    private final AtomicInteger contadorChamados = new AtomicInteger(0);
    private boolean bancoConectado;

    public TicketProcessor() {
        this.bancoConectado = true;
    }

    /**
     * Processa um chamado de suporte, realizando a avaliação de SLA,
     * cálculo de cobrança, registro de auditoria e persistência.
     *
     * @param chamado Objeto tipado que representa o chamado recebido.
     * @return Resultado do processamento formatado.
     */
    public ResultadoProcessamento processar(Chamado chamado) {
        int idChamado = contadorChamados.incrementAndGet();

        String rotuloprioridade = definirRotuloPrioridade(chamado);
        int slaHoras = SlaEvaluator.calcularSlaHoras(chamado);
        double valorAntesTaxa = BillingCalculator.calcularValorAntesTaxaHorario(chamado);
        double valorTotal = BillingCalculator.calcularValorTotal(chamado);

        AuditLogger.registrarAbertura(idChamado, chamado, rotuloprioridade, slaHoras, valorAntesTaxa);

        if (BillingCalculator.isForaDoHorarioComercial(chamado.getDataAbertura())) {
            AuditLogger.registrarRecalculoForaHorario(idChamado, chamado, valorTotal);
        }

        boolean persistido = persistirNoBanco(idChamado, chamado, rotuloprioridade, slaHoras, valorTotal);

        if (!persistido) {
            return ResultadoProcessamento.erro(idChamado, "Banco de dados desconectado");
        }

        String flagHorario = BillingCalculator.isForaDoHorarioComercial(chamado.getDataAbertura())
                ? "FORA_HORARIO" : "HORARIO_COMERCIAL";

        return ResultadoProcessamento.sucesso(idChamado, rotuloprioridade, slaHoras, valorTotal, flagHorario);
    }

    // --- Métodos privados auxiliares ---

    private String definirRotuloPrioridade(Chamado chamado) {
        switch (chamado.getTipo()) {
            case INCIDENTE:
                NivelPrioridade nivel = SlaEvaluator.determinarPrioridade(chamado);
                String rotulo = nivel != null ? nivel.name() : "INDEFINIDO";
                return chamado.isUrgente() ? rotulo + "-URGENTE" : rotulo;
            case REQUISICAO:
                return "REQUISICAO";
            case MUDANCA:
                return "MUDANCA";
            default:
                return "DESCONHECIDO";
        }
    }

    private boolean persistirNoBanco(int idChamado, Chamado chamado,
                                      String prioridade, int slaHoras, double valor) {
        if (!bancoConectado) {
            System.err.printf("ERRO CRITICO: Banco de dados desconectado. Chamado #%d perdido!%n", idChamado);
            return false;
        }

        String dataFormatada = chamado.getDataAbertura().format(FORMATO_DATA);
        String flagHorario = BillingCalculator.isForaDoHorarioComercial(chamado.getDataAbertura())
                ? "FORA_HORARIO" : "HORARIO_COMERCIAL";

        System.out.printf(
                Locale.US,
                "INSERT INTO chamados (id, titulo, descricao, usuario, prioridade, sla_horas, valor, horario_flag, data_abertura) "
                + "VALUES (%d, '%s', '%s', '%s', '%s', %d, %.2f, '%s', '%s')%n",
                idChamado, chamado.getTitulo(), chamado.getDescricao(),
                chamado.getUsuario(), prioridade, slaHoras, valor, flagHorario, dataFormatada
        );
        System.out.println("UPDATE dashboard SET total_abertos = total_abertos + 1 WHERE setor = 'TI'");
        System.out.printf("CHAMADO PERSISTIDO COM SUCESSO. ID=%d%n", idChamado);

        return true;
    }

    // --- Classe interna para resultado estruturado ---

    /**
     * Value Object imutável que encapsula o resultado do processamento,
     * substituindo a string concatenada ("OK|1|CRITICO|4|1000.0|HORARIO_COMERCIAL").
     */
    public static class ResultadoProcessamento {

        private final boolean sucesso;
        private final int idChamado;
        private final String prioridade;
        private final int slaHoras;
        private final double valorTotal;
        private final String flagHorario;
        private final String mensagemErro;

        private ResultadoProcessamento(boolean sucesso, int idChamado, String prioridade,
                                        int slaHoras, double valorTotal, String flagHorario,
                                        String mensagemErro) {
            this.sucesso = sucesso;
            this.idChamado = idChamado;
            this.prioridade = prioridade;
            this.slaHoras = slaHoras;
            this.valorTotal = valorTotal;
            this.flagHorario = flagHorario;
            this.mensagemErro = mensagemErro;
        }

        public static ResultadoProcessamento sucesso(int id, String prioridade,
                                                      int sla, double valor, String flag) {
            return new ResultadoProcessamento(true, id, prioridade, sla, valor, flag, null);
        }

        public static ResultadoProcessamento erro(int id, String mensagem) {
            return new ResultadoProcessamento(false, id, null, 0, 0, null, mensagem);
        }

        public boolean isSucesso() { return sucesso; }
        public int getIdChamado() { return idChamado; }
        public String getPrioridade() { return prioridade; }
        public int getSlaHoras() { return slaHoras; }
        public double getValorTotal() { return valorTotal; }
        public String getFlagHorario() { return flagHorario; }
        public String getMensagemErro() { return mensagemErro; }

        @Override
        public String toString() {
            if (sucesso) {
                return String.format(Locale.US, "OK | ID=%d | Prioridade=%s | SLA=%dh | Valor=R$%.2f | %s",
                        idChamado, prioridade, slaHoras, valorTotal, flagHorario);
            }
            return String.format(Locale.US, "ERRO | ID=%d | %s", idChamado, mensagemErro);
        }
    }

    // --- Ponto de entrada para demonstração ---

    public static void main(String[] args) {
        TicketProcessor processador = new TicketProcessor();

        Chamado chamadoCritico = new Chamado(
                "Servidor fora do ar",
                "O servidor de producao parou de responder",
                "joao.silva", 9,
                TipoChamado.INCIDENTE, true
        );

        Chamado chamadoRequisicao = new Chamado(
                "Novo monitor",
                "Solicitar monitor adicional para estacao 12",
                "maria.santos", 2,
                TipoChamado.REQUISICAO, false
        );

        ResultadoProcessamento resultado1 = processador.processar(chamadoCritico);
        System.out.println("Retorno 1: " + resultado1);

        ResultadoProcessamento resultado2 = processador.processar(chamadoRequisicao);
        System.out.println("Retorno 2: " + resultado2);
    }
}
