import java.util.Date;
import java.text.SimpleDateFormat;

/**
 * Classe responsavel pelo processamento de chamados de suporte de TI.
 * (Versao legada - codigo original com problemas estruturais)
 */
public class TicketService {

    // Simula conexao com banco de dados
    public static String DB_STATUS = "CONECTADO";
    public static int contadorGlobal = 0;

    public String processarChamadoObj(Object object, int tipo, boolean urgente) {
        // cast sem seguranca
        String[] dados = (String[]) object;
        String t = dados[0]; // titulo
        String d = dados[1]; // descricao
        String u = dados[2]; // usuario
        int imp = Integer.parseInt(dados[3]); // impacto

        double p = 0; // preco
        int sla = 0;
        String flag = "";
        String aux = "";
        String log = "";
        Date dt = new Date();
        SimpleDateFormat fmt = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");

        contadorGlobal++;

        // calculo de prioridade e SLA
        if (tipo == 1) {
            if (imp >= 8) {
                flag = "CRITICO";
                sla = 4;
                p = 500.0;
                if (urgente) {
                    sla = 2;
                    p = p * 2.0;
                    flag = flag + "-URGENTE";
                    // log de auditoria
                    log = "[" + fmt.format(dt) + "] CHAMADO #" + contadorGlobal + " | Titulo: " + t + " | Usuario: " + u + " | Prioridade: " + flag + " | SLA: " + sla + "h | Valor: R$" + p;
                    System.out.println(log);
                } else {
                    // log de auditoria duplicado
                    log = "[" + fmt.format(dt) + "] CHAMADO #" + contadorGlobal + " | Titulo: " + t + " | Usuario: " + u + " | Prioridade: " + flag + " | SLA: " + sla + "h | Valor: R$" + p;
                    System.out.println(log);
                }
            } else if (imp >= 5) {
                flag = "ALTO";
                sla = 8;
                p = 300.0;
                if (urgente) {
                    sla = 4;
                    p = p * 1.5;
                    flag = flag + "-URGENTE";
                }
                log = "[" + fmt.format(dt) + "] CHAMADO #" + contadorGlobal + " | Titulo: " + t + " | Usuario: " + u + " | Prioridade: " + flag + " | SLA: " + sla + "h | Valor: R$" + p;
                System.out.println(log);
            } else if (imp >= 3) {
                flag = "MEDIO";
                sla = 24;
                p = 150.0;
                if (urgente) {
                    sla = 12;
                    p = p * 1.5;
                    flag = flag + "-URGENTE";
                }
                log = "[" + fmt.format(dt) + "] CHAMADO #" + contadorGlobal + " | Titulo: " + t + " | Usuario: " + u + " | Prioridade: " + flag + " | SLA: " + sla + "h | Valor: R$" + p;
                System.out.println(log);
            } else {
                flag = "BAIXO";
                sla = 48;
                p = 75.0;
                log = "[" + fmt.format(dt) + "] CHAMADO #" + contadorGlobal + " | Titulo: " + t + " | Usuario: " + u + " | Prioridade: " + flag + " | SLA: " + sla + "h | Valor: R$" + p;
                System.out.println(log);
            }
        } else if (tipo == 2) {
            flag = "REQUISICAO";
            sla = 72;
            p = 50.0;
            if (urgente) {
                sla = 24;
                p = p * 1.5;
            }
            log = "[" + fmt.format(dt) + "] CHAMADO #" + contadorGlobal + " | Titulo: " + t + " | Usuario: " + u + " | Prioridade: " + flag + " | SLA: " + sla + "h | Valor: R$" + p;
            System.out.println(log);
        } else if (tipo == 3) {
            flag = "MUDANCA";
            sla = 96;
            p = 200.0;
            log = "[" + fmt.format(dt) + "] CHAMADO #" + contadorGlobal + " | Titulo: " + t + " | Usuario: " + u + " | Prioridade: " + flag + " | SLA: " + sla + "h | Valor: R$" + p;
            System.out.println(log);
        } else {
            flag = "DESCONHECIDO";
            sla = 120;
            p = 0;
            log = "[" + fmt.format(dt) + "] CHAMADO #" + contadorGlobal + " | Titulo: " + t + " | Usuario: " + u + " | Prioridade: " + flag + " | SLA: " + sla + "h | Valor: R$" + p;
            System.out.println(log);
        }

        // verificacao de horario comercial (fora do expediente = taxa extra)
        int hora = dt.getHours(); // metodo deprecated
        if (hora < 8 || hora > 18) {
            p = p + (p * 0.35); // taxa de 35% fora do horario
            aux = "FORA_HORARIO";
            // recalcula log com nova taxa
            log = "[" + fmt.format(dt) + "] RECALCULO CHAMADO #" + contadorGlobal + " | Titulo: " + t + " | Taxa extra aplicada: 35% | Novo Valor: R$" + p + " | Obs: " + aux;
            System.out.println(log);
        } else {
            aux = "HORARIO_COMERCIAL";
        }

        // simula persistencia no banco
        if (DB_STATUS.equals("CONECTADO")) {
            System.out.println("INSERT INTO chamados (id, titulo, descricao, usuario, prioridade, sla_horas, valor, horario_flag, data_abertura) VALUES (" + contadorGlobal + ", '" + t + "', '" + d + "', '" + u + "', '" + flag + "', " + sla + ", " + p + ", '" + aux + "', '" + fmt.format(dt) + "')");
            System.out.println("UPDATE dashboard SET total_abertos = total_abertos + 1 WHERE setor = 'TI'");
            System.out.println("CHAMADO PERSISTIDO COM SUCESSO. ID=" + contadorGlobal);
        } else {
            System.out.println("ERRO CRITICO: Banco de dados desconectado. Chamado #" + contadorGlobal + " perdido!");
            return "ERRO";
        }

        // monta retorno
        String resultado = "OK|" + contadorGlobal + "|" + flag + "|" + sla + "|" + p + "|" + aux;
        return resultado;
    }

    // metodo auxiliar mal nomeado e sem utilidade clara
    public void aux(int x) {
        for (int i = 0; i < x; i++) {
            System.out.println("---");
        }
    }

    public static void main(String[] args) {
        TicketService ts = new TicketService();
        String[] chamado1 = {"Servidor fora do ar", "O servidor de producao parou de responder", "joao.silva", "9"};
        String[] chamado2 = {"Novo monitor", "Solicitar monitor adicional para estacao 12", "maria.santos", "2"};

        String r1 = ts.processarChamadoObj(chamado1, 1, true);
        System.out.println("Retorno 1: " + r1);

        String r2 = ts.processarChamadoObj(chamado2, 2, false);
        System.out.println("Retorno 2: " + r2);
    }
}
