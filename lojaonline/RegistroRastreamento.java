package lojaonline;

import java.util.ArrayList;

public class RegistroRastreamento {

    private ArrayList<RemessaPedido> remessas;
    private ArrayList<DevolucaoPedido> devolucoes;

    public RegistroRastreamento() {
        this.remessas = new ArrayList<>();
        this.devolucoes = new ArrayList<>();
    }

    public RemessaPedido criarRemessa(String codigoRastreio, Pedido pedido, ModalidadeEntrega modalidade) {
        if (modalidade == null) {
            throw new IllegalArgumentException("Modalidade de entrega não pode ser nula.");
        }
        // Permitido pelo enunciado: o registro pode verificar o tipo para rejeitar retirada
        if (modalidade instanceof RetiradaNaLoja) {
            throw new IllegalStateException("Retirada na loja não gera remessa rastreável.");
        }
        RemessaPedido remessa = new RemessaPedido(codigoRastreio, pedido);
        if (codigoEmUso(remessa.getCodigoRastreio())) {
            throw new IllegalStateException("Código de rastreio já utilizado: " + remessa.getCodigoRastreio());
        }
        for (RemessaPedido existente : remessas) {
            if (existente.getPedido() == pedido) {
                throw new IllegalStateException("Este pedido já possui uma remessa.");
            }
        }
        remessas.add(remessa);
        return remessa;
    }

    public DevolucaoPedido criarDevolucao(String codigoRastreio, RemessaPedido remessaOriginal, String motivo) {
        if (remessaOriginal == null) {
            throw new IllegalArgumentException("Remessa original não pode ser nula.");
        }
        if (!remessas.contains(remessaOriginal)) {
            throw new IllegalArgumentException("Remessa não pertence a este registro.");
        }
        DevolucaoPedido devolucao = new DevolucaoPedido(codigoRastreio, remessaOriginal, motivo);
        if (codigoEmUso(devolucao.getCodigoRastreio())) {
            throw new IllegalStateException("Código de rastreio já utilizado: " + devolucao.getCodigoRastreio());
        }
        for (DevolucaoPedido existente : devolucoes) {
            if (existente.getRemessaOriginal() == remessaOriginal) {
                throw new IllegalStateException("Esta remessa já possui uma devolução.");
            }
        }
        devolucoes.add(devolucao);
        return devolucao;
    }

    public int contarRemessas() {
        return remessas.size();
    }

    public int contarDevolucoes() {
        return devolucoes.size();
    }

    private boolean codigoEmUso(String codigo) {
        for (RemessaPedido remessa : remessas) {
            if (remessa.getCodigoRastreio().equals(codigo)) {
                return true;
            }
        }
        for (DevolucaoPedido devolucao : devolucoes) {
            if (devolucao.getCodigoRastreio().equals(codigo)) {
                return true;
            }
        }
        return false;
    }
}
