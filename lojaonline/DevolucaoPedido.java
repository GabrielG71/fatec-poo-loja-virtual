package lojaonline;

public class DevolucaoPedido implements Rastreavel {

    private String codigoRastreio;
    private RemessaPedido remessaOriginal;
    private String motivo;
    private StatusRastreamento status;

    public DevolucaoPedido(String codigoRastreio, RemessaPedido remessaOriginal, String motivo) {
        if (codigoRastreio == null || codigoRastreio.trim().isEmpty()) {
            throw new IllegalArgumentException("Código de rastreio é obrigatório.");
        }
        if (remessaOriginal == null) {
            throw new IllegalArgumentException("Remessa original não pode ser nula.");
        }
        if (motivo == null || motivo.trim().isEmpty()) {
            throw new IllegalArgumentException("Motivo da devolução é obrigatório.");
        }
        if (remessaOriginal.getStatus() != StatusRastreamento.CONCLUIDO) {
            throw new IllegalStateException("Só é possível devolver uma remessa já entregue.");
        }
        this.codigoRastreio = codigoRastreio.trim();
        this.remessaOriginal = remessaOriginal;
        this.motivo = motivo.trim();
        this.status = StatusRastreamento.PREPARANDO;
    }

    public RemessaPedido getRemessaOriginal() {
        return remessaOriginal;
    }

    public String getMotivo() {
        return motivo;
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
                return "Devolução em preparação";
            case EM_TRANSITO:
                return "Devolução em trânsito";
            default:
                return "Devolução recebida pela loja";
        }
    }
}
