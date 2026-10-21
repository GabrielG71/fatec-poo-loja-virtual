package lojaonline;

public class RetiradaNaLoja extends ModalidadeEntrega {

    public RetiradaNaLoja() {
        super("Retirada na loja");
    }

    @Override
    public double calcularFrete(double subtotal) {
        return 0.0;
    }
}
