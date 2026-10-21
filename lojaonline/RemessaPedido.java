package lojaonline;

public class RemessaPedido implements Rastreavel {

    private String codigoRastreio;
    private Pedido pedido;
    private StatusRastreamento status;

    public RemessaPedido(String codigoRastreio, Pedido pedido) {
        if (codigoRastreio == null || codigoRastreio.trim().isEmpty()) {
            throw new IllegalArgumentException("Código de rastreio é obrigatório.");
        }
        if (pedido == null) {
            throw new IllegalArgumentException("Pedido não pode ser nulo.");
        }
        if (pedido.contarItens() == 0) {
            throw new IllegalStateException("Não é possível enviar um pedido vazio.");
        }
        this.codigoRastreio = codigoRastreio.trim();
        this.pedido = pedido;
        this.status = StatusRastreamento.PREPARANDO;
    }

    public Pedido getPedido() {
        return pedido;
    }

    // Só aceita PREPARANDO -> EM_TRANSITO -> CONCLUIDO, uma etapa por vez
    public void atualizarStatus(StatusRastreamento novoStatus) {
        if (novoStatus == null) {
            throw new IllegalArgumentException("Status não pode ser nulo.");
        }
        if (novoStatus.ordinal() != status.ordinal() + 1) {
            throw new IllegalStateException("Transição proibida: " + status + " -> " + novoStatus);
        }
        this.status = novoStatus;
    }

    @Override
    public String getCodigoRastreio() {
        return codigoRastreio;
    }

    @Override
    public StatusRastreamento getStatus() {
        return status;
    }

    @Override
    public String consultarRastreamento() {
        switch (status) {
            case PREPARANDO:
                return "Remessa em preparação";
            case EM_TRANSITO:
                return "Remessa em trânsito";
            default:
                return "Remessa entregue ao cliente";
        }
    }
}
