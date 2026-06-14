package refatorado;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Classe responsável pela formatação e registro de logs de auditoria.
 * Centraliza toda a lógica de construção de mensagens de log,
 * eliminando duplicação de código de concatenação de strings.
 */
public final class AuditLogger {

    private static final DateTimeFormatter FORMATO_DATA =
            DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");

    private AuditLogger() {
        // Classe utilitária — construtor privado impede instanciação.
    }

    /**
     * Registra o log padrão de abertura do chamado.
     *
     * @param idChamado   Identificador sequencial do chamado.
     * @param chamado     Dados do chamado.
     * @param prioridade  Rótulo da prioridade atribuída.
     * @param slaHoras    SLA calculado em horas.
     * @param valorTotal  Valor total de cobrança.
     */
    public static void registrarAbertura(int idChamado, Chamado chamado,
                                          String prioridade, int slaHoras,
                                          double valorTotal) {
        String mensagem = String.format(
                "[%s] CHAMADO #%d | Titulo: %s | Usuario: %s | Prioridade: %s | SLA: %dh | Valor: R$%.2f",
                chamado.getDataAbertura().format(FORMATO_DATA),
                idChamado,
                chamado.getTitulo(),
                chamado.getUsuario(),
                prioridade,
                slaHoras,
                valorTotal
        );
        System.out.println(mensagem);
    }

    /**
     * Registra o log de recálculo por taxa fora do horário comercial.
     *
     * @param idChamado  Identificador sequencial do chamado.
     * @param chamado    Dados do chamado.
     * @param valorFinal Valor final após a aplicação da taxa.
     */
    public static void registrarRecalculoForaHorario(int idChamado, Chamado chamado,
                                                      double valorFinal) {
        String mensagem = String.format(
                "[%s] RECALCULO CHAMADO #%d | Titulo: %s | Taxa extra aplicada: 35%% | Novo Valor: R$%.2f | Obs: FORA_HORARIO",
                chamado.getDataAbertura().format(FORMATO_DATA),
                idChamado,
                chamado.getTitulo(),
                valorFinal
        );
        System.out.println(mensagem);
    }
}
