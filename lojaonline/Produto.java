package lojaonline;

public class Produto {

    private int codigo;
    private String nome;
    private double preco;
    private Categoria categoria;

    public Produto(int codigo, String nome, double preco, Categoria categoria) {
        if (codigo <= 0) {
            throw new IllegalArgumentException("Código do produto deve ser positivo.");
        }
        if (nome == null || nome.trim().isEmpty()) {
            throw new IllegalArgumentException("Nome do produto é obrigatório.");
        }
        if (!Double.isFinite(preco) || preco <= 0) {
            throw new IllegalArgumentException("Preço deve ser positivo e finito.");
        }
        if (categoria == null) {
            throw new IllegalArgumentException("Produto deve ter uma categoria.");
        }
        this.codigo = codigo;
        this.nome = nome;
        this.preco = preco;
        this.categoria = categoria;
    }

    public int getCodigo() {
        return codigo;
    }

    public String getNome() {
        return nome;
    }

    public double getPreco() {
        return preco;
    }

    public Categoria getCategoria() {
        return categoria;
    }
}
