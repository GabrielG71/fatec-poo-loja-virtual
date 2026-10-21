package lojaonline;

import java.util.ArrayList;
import java.util.Locale;

public class Main {

    private static final Locale BR = Locale.forLanguageTag("pt-BR");
    private static final double TOLERANCIA = 0.001;

    private static int cenarios = 0;
    private static int falhas = 0;

    public static void main(String[] args) {
        etapa1();
        etapa2();

        System.out.println();
        System.out.println("==================================================");
        System.out.println("Verificações executadas: " + cenarios + " | falhas: " + falhas);
    }

    // ------------------------------------------------------------------
    // Etapa 1 — catálogo, pedidos e modalidades de entrega
    // ------------------------------------------------------------------
    private static void etapa1() {
        titulo("ETAPA 1 — Catálogo, pedidos e entregas");

        Categoria perifericos = new Categoria(1, "Periféricos");
        Categoria papelaria = new Categoria(2, "Papelaria");
        Categoria armazenamento = new Categoria(3, "Armazenamento");
        Catalogo catalogo = new Catalogo();
        catalogo.cadastrarCategoria(perifericos);
        catalogo.cadastrarCategoria(papelaria);
        catalogo.cadastrarCategoria(armazenamento);

        catalogo.cadastrarProduto(new Produto(101, "Teclado", 120.0, perifericos));
        catalogo.cadastrarProduto(new Produto(102, "Mouse", 50.0, perifericos));
        catalogo.cadastrarProduto(new Produto(103, "Caderno", 30.0, papelaria));
        catalogo.cadastrarProduto(new Produto(104, "SSD de teste", 199.99, armazenamento));

        // Os pedidos usam os objetos obtidos do catálogo
        Produto teclado = catalogo.buscarProduto(101);
        Produto mouse = catalogo.buscarProduto(102);
        Produto ssd = catalogo.buscarProduto(104);

        ModalidadeEntrega economica = new EntregaEconomica();
        ModalidadeEntrega[] modalidades = {
            economica,
            new EntregaExpressa(),
            new RetiradaNaLoja()
        };

        // E1-01: exemplo do enunciado — sobrecarga, repetição e polimorfismo
        subtitulo("E1-01 Teclado + duas unidades de mouse");
        Pedido pedido = new Pedido();
        pedido.adicionarProduto(teclado);
        pedido.adicionarProduto(mouse);
        pedido.adicionarProduto(mouse); // aumenta a quantidade da linha do mouse
        System.out.println("Linhas: " + pedido.contarItens());
        System.out.println("Subtotal: " + moeda(pedido.calcularSubtotal()));
        double[] totaisEsperados = {220.0, 240.0, 220.0};
        for (int i = 0; i < modalidades.length; i++) {
            ModalidadeEntrega modalidade = modalidades[i];
            double total = pedido.calcularTotal(modalidade);
            System.out.println(modalidade.getNome() + ": " + moeda(total));
            verificarValor("E1-01", "Total com " + modalidade.getNome(), totaisEsperados[i], total);
        }
        verificar("E1-01", "Linhas do pedido", "2", String.valueOf(pedido.contarItens()));
        verificarValor("E1-01", "Subtotal do pedido", 220.0, pedido.calcularSubtotal());

        // E1-02: calcularTotal não altera o pedido (RN12)
        subtitulo("E1-02 Simular todas as modalidades não altera o pedido");
        verificarValor("E1-02", "Subtotal após simulações", 220.0, pedido.calcularSubtotal());
        verificar("E1-02", "Linhas após simulações", "2", String.valueOf(pedido.contarItens()));

        // E1-03: subtotal do item (RN08)
        subtitulo("E1-03 ItemPedido.calcularSubtotal");
        ItemPedido itemMouse = new ItemPedido(mouse, 2);
        verificarValor("E1-03", "Mouse de R$ 50,00 x 2", 100.0, itemMouse.calcularSubtotal());

        // E1-04: uma unidade de mouse
        subtitulo("E1-04 Uma unidade de mouse");
        pedido = new Pedido();
        pedido.adicionarProduto(mouse);
        verificarValor("E1-04", "Subtotal", 50.0, pedido.calcularSubtotal());
        verificarValor("E1-04", "Total econômica", 60.0, pedido.calcularTotal(economica));

        // E1-05: quatro unidades de mouse (limite de R$ 200,00 inclusive)
        subtitulo("E1-05 Quatro unidades de mouse");
        pedido = new Pedido();
        pedido.adicionarProduto(mouse, 4);
        verificarValor("E1-05", "Subtotal", 200.0, pedido.calcularSubtotal());
        verificarValor("E1-05", "Total econômica", 200.0, pedido.calcularTotal(economica));

        // E1-06: subtotal de R$ 199,99
        subtitulo("E1-06 Subtotal de R$ 199,99");
        pedido = new Pedido();
        pedido.adicionarProduto(ssd);
        verificarValor("E1-06", "Frete econômico", 10.0, economica.calcularFrete(pedido.calcularSubtotal()));
        verificarValor("E1-06", "Total econômica", 209.99, pedido.calcularTotal(economica));

        // E1-07: sobrecarga + repetição do mesmo produto (RN06, RN07)
        subtitulo("E1-07 Mouse sem quantidade e depois com quantidade 2");
        pedido = new Pedido();
        pedido.adicionarProduto(mouse);
        pedido.adicionarProduto(mouse, 2);
        verificar("E1-07", "Linhas", "1", String.valueOf(pedido.contarItens()));
        verificarValor("E1-07", "Subtotal (3 unidades)", 150.0, pedido.calcularSubtotal());

        // E1-08 e E1-09: quantidade zero ou negativa preserva o pedido
        subtitulo("E1-08/E1-09 Quantidade inválida em pedido com mouse");
        pedido = new Pedido();
        pedido.adicionarProduto(mouse);
        int[] quantidadesInvalidas = {0, -3};
        for (int i = 0; i < quantidadesInvalidas.length; i++) {
            String id = "E1-0" + (8 + i);
            try {
                pedido.adicionarProduto(mouse, quantidadesInvalidas[i]);
                verificar(id, "Quantidade " + quantidadesInvalidas[i], "IllegalArgumentException", "nenhuma exceção");
            } catch (RuntimeException e) {
                verificar(id, "Quantidade " + quantidadesInvalidas[i], "IllegalArgumentException", tipo(e));
            }
            verificarValor(id, "Subtotal preservado", 50.0, pedido.calcularSubtotal());
            verificar(id, "Linhas preservadas", "1", String.valueOf(pedido.contarItens()));
        }

        // E1-10: produto nulo
        subtitulo("E1-10 Adicionar produto nulo");
        try {
            pedido.adicionarProduto(null);
            verificar("E1-10", "Produto nulo", "IllegalArgumentException", "nenhuma exceção");
        } catch (RuntimeException e) {
            verificar("E1-10", "Produto nulo", "IllegalArgumentException", tipo(e));
        }
        verificarValor("E1-10", "Subtotal preservado", 50.0, pedido.calcularSubtotal());
        verificar("E1-10", "Linhas preservadas", "1", String.valueOf(pedido.contarItens()));

        // E1-11: pedido vazio
        subtitulo("E1-11 Pedido vazio");
        Pedido vazio = new Pedido();
        verificarValor("E1-11", "Subtotal", 0.0, vazio.calcularSubtotal());
        verificar("E1-11", "Linhas", "0", String.valueOf(vazio.contarItens()));
        try {
            vazio.calcularTotal(economica);
            verificar("E1-11", "calcularTotal", "IllegalStateException", "nenhuma exceção");
        } catch (RuntimeException e) {
            verificar("E1-11", "calcularTotal", "IllegalStateException", tipo(e));
        }

        // E1-12: modalidade nula
        subtitulo("E1-12 Pedido não vazio com modalidade nula");
        try {
            pedido.calcularTotal(null);
            verificar("E1-12", "calcularTotal(null)", "IllegalArgumentException", "nenhuma exceção");
        } catch (RuntimeException e) {
            verificar("E1-12", "calcularTotal(null)", "IllegalArgumentException", tipo(e));
        }

        // E1-13: filtro por categoria (RN05)
        subtitulo("E1-13 Filtrar Periféricos");
        ArrayList<Produto> listaPerifericos = catalogo.listarPorCategoria(perifericos);
        verificar("E1-13", "Produtos de Periféricos", "[Teclado, Mouse]", nomes(listaPerifericos));

        // E1-14: alterar a lista retornada não altera o catálogo
        subtitulo("E1-14 Alterar o resultado do filtro");
        listaPerifericos.clear();
        verificar("E1-14", "Filtro após limpar a lista anterior", "[Teclado, Mouse]",
                nomes(catalogo.listarPorCategoria(perifericos)));

        // E1-15: categoria sem produtos
        subtitulo("E1-15 Categoria Livros sem produtos");
        Categoria livros = new Categoria(4, "Livros");
        catalogo.cadastrarCategoria(livros);
        verificar("E1-15", "Produtos de Livros", "[]", nomes(catalogo.listarPorCategoria(livros)));

        // E1-16: filtro com categoria nula
        subtitulo("E1-16 Filtro com categoria nula");
        try {
            catalogo.listarPorCategoria(null);
            verificar("E1-16", "listarPorCategoria(null)", "IllegalArgumentException", "nenhuma exceção");
        } catch (RuntimeException e) {
            verificar("E1-16", "listarPorCategoria(null)", "IllegalArgumentException", tipo(e));
        }

        // E1-17 e E1-18: aumentarQuantidade com incremento inválido (RN14)
        subtitulo("E1-17/E1-18 aumentarQuantidade com incremento inválido");
        int[] incrementosInvalidos = {0, -1};
        for (int i = 0; i < incrementosInvalidos.length; i++) {
            String id = "E1-1" + (7 + i);
            try {
                itemMouse.aumentarQuantidade(incrementosInvalidos[i]);
                verificar(id, "Incremento " + incrementosInvalidos[i], "IllegalArgumentException", "nenhuma exceção");
            } catch (RuntimeException e) {
                verificar(id, "Incremento " + incrementosInvalidos[i], "IllegalArgumentException", tipo(e));
            }
            verificar(id, "Quantidade preservada", "2", String.valueOf(itemMouse.getQuantidade()));
        }

        // E1-19: incremento válido soma, sem substituir
        subtitulo("E1-19 aumentarQuantidade(3) em item com 2 unidades");
        itemMouse.aumentarQuantidade(3);
        verificar("E1-19", "Quantidade", "5", String.valueOf(itemMouse.getQuantidade()));

        // E1-20 e E1-21: verificações do código fornecido (RN03, RN04)
        subtitulo("E1-20/E1-21 Validações do catálogo");
        try {
            catalogo.cadastrarCategoria(new Categoria(1, "Outra"));
            verificar("E1-20", "Código de categoria repetido", "IllegalArgumentException", "nenhuma exceção");
        } catch (RuntimeException e) {
            verificar("E1-20", "Código de categoria repetido", "IllegalArgumentException", tipo(e));
        }
        try {
            Categoria naoCadastrada = new Categoria(9, "Games");
            catalogo.cadastrarProduto(new Produto(901, "Controle", 150.0, naoCadastrada));
            verificar("E1-21", "Produto de categoria não cadastrada", "IllegalArgumentException", "nenhuma exceção");
        } catch (RuntimeException e) {
            verificar("E1-21", "Produto de categoria não cadastrada", "IllegalArgumentException", tipo(e));
        }
    }

    // ------------------------------------------------------------------
    // Etapa 2 — interfaces: rastreamento de remessas e devoluções
    // ------------------------------------------------------------------
    private static void etapa2() {
        titulo("ETAPA 2 — Rastreamento de remessas e devoluções");

        Categoria perifericos = new Categoria(1, "Periféricos");
        Produto teclado = new Produto(101, "Teclado", 120.0, perifericos);
        Produto mouse = new Produto(102, "Mouse", 50.0, perifericos);

        Pedido pedido = new Pedido();
        pedido.adicionarProduto(teclado);
        pedido.adicionarProduto(mouse, 2);

        RegistroRastreamento registro = new RegistroRastreamento();

        // E2-01: remessa válida
        subtitulo("E2-01 Criar remessa válida");
        RemessaPedido remessa = registro.criarRemessa("ENV-001", pedido, new EntregaEconomica());
        verificar("E2-01", "Código", "ENV-001", remessa.getCodigoRastreio());
        verificar("E2-01", "Status", "PREPARANDO", String.valueOf(remessa.getStatus()));
        verificar("E2-01", "Descrição", "Remessa em preparação", remessa.consultarRastreamento());

        // E2-02: avançar remessa pelas duas etapas
        subtitulo("E2-02 Avançar remessa");
        remessa.atualizarStatus(StatusRastreamento.EM_TRANSITO);
        verificar("E2-02", "Descrição em trânsito", "Remessa em trânsito", remessa.consultarRastreamento());
        remessa.atualizarStatus(StatusRastreamento.CONCLUIDO);
        verificar("E2-02", "Status", "CONCLUIDO", String.valueOf(remessa.getStatus()));
        verificar("E2-02", "Descrição", "Remessa entregue ao cliente", remessa.consultarRastreamento());

        // E2-03: devolução da remessa
        subtitulo("E2-03 Criar devolução da remessa");
        DevolucaoPedido devolucao =
                registro.criarDevolucao("DEV-001", remessa, "  Produto diferente do solicitado  ");
        verificar("E2-03", "Código", "DEV-001", devolucao.getCodigoRastreio());
        verificar("E2-03", "Status", "PREPARANDO", String.valueOf(devolucao.getStatus()));
        verificar("E2-03", "Descrição", "Devolução em preparação", devolucao.consultarRastreamento());
        verificar("E2-03", "Motivo sem espaços", "Produto diferente do solicitado", devolucao.getMotivo());
        verificar("E2-03", "Pedido pela remessa original", "true",
                String.valueOf(devolucao.getRemessaOriginal().getPedido() == pedido));

        devolucao.atualizarStatus(StatusRastreamento.EM_TRANSITO);
        verificar("E2-03", "Descrição em trânsito", "Devolução em trânsito", devolucao.consultarRastreamento());

        // E2-04: consulta polimórfica pela interface
        subtitulo("E2-04 Consulta pelo array de Rastreavel");
        Rastreavel[] transportes = { remessa, devolucao };
        String[] esperados = {
            "ENV-001: Remessa entregue ao cliente",
            "DEV-001: Devolução em trânsito"
        };
        for (int i = 0; i < transportes.length; i++) {
            Rastreavel transporte = transportes[i];
            String linha = transporte.getCodigoRastreio() + ": " + transporte.consultarRastreamento();
            System.out.println(linha);
            verificar("E2-04", "Transporte " + (i + 1), esperados[i], linha);
        }

        // E2-05: concluir devolução
        subtitulo("E2-05 Concluir devolução");
        devolucao.atualizarStatus(StatusRastreamento.CONCLUIDO);
        verificar("E2-05", "Status", "CONCLUIDO", String.valueOf(devolucao.getStatus()));
        verificar("E2-05", "Descrição", "Devolução recebida pela loja", devolucao.consultarRastreamento());
        verificar("E2-05", "Remessa original continua concluída", "CONCLUIDO", String.valueOf(remessa.getStatus()));

        // E2-06: consultar várias vezes não altera nada (RT08)
        subtitulo("E2-06 Consultar várias vezes");
        for (int i = 0; i < 3; i++) {
            for (Rastreavel transporte : transportes) {
                transporte.getCodigoRastreio();
                transporte.getStatus();
                transporte.consultarRastreamento();
            }
        }
        verificarValor("E2-06", "Subtotal do pedido preservado", 220.0, pedido.calcularSubtotal());
        verificar("E2-06", "Linhas do pedido preservadas", "2", String.valueOf(pedido.contarItens()));
        verificar("E2-06", "Status da remessa preservado", "CONCLUIDO", String.valueOf(remessa.getStatus()));
        verificar("E2-06", "Status da devolução preservado", "CONCLUIDO", String.valueOf(devolucao.getStatus()));

        // E2-07 a E2-10: criação inválida de remessa (registro independente)
        subtitulo("E2-07 a E2-10 Criação inválida de remessa");
        RegistroRastreamento registroErros = new RegistroRastreamento();
        try {
            registroErros.criarRemessa("ENV-100", null, new EntregaExpressa());
            verificar("E2-07", "Pedido nulo", "IllegalArgumentException", "nenhuma exceção");
        } catch (RuntimeException e) {
            verificar("E2-07", "Pedido nulo", "IllegalArgumentException", tipo(e));
        }
        try {
            registroErros.criarRemessa("ENV-101", new Pedido(), new EntregaExpressa());
            verificar("E2-08", "Pedido vazio", "IllegalStateException", "nenhuma exceção");
        } catch (RuntimeException e) {
            verificar("E2-08", "Pedido vazio", "IllegalStateException", tipo(e));
        }
        String[] codigosInvalidos = {null, "   "};
        for (int i = 0; i < codigosInvalidos.length; i++) {
            String id = "E2-" + String.format("%02d", 9 + i);
            String descricao = codigosInvalidos[i] == null ? "Código nulo" : "Código em branco";
            try {
                registroErros.criarRemessa(codigosInvalidos[i], novoPedido(mouse), new EntregaExpressa());
                verificar(id, descricao, "IllegalArgumentException", "nenhuma exceção");
            } catch (RuntimeException e) {
                verificar(id, descricao, "IllegalArgumentException", tipo(e));
            }
        }
        verificar("E2-10", "Nenhuma remessa registrada", "0", String.valueOf(registroErros.contarRemessas()));

        // E2-11 a E2-16: criação inválida de devolução
        subtitulo("E2-11 a E2-16 Criação inválida de devolução");
        RemessaPedido concluida = registroErros.criarRemessa("ENV-200", novoPedido(mouse), new EntregaExpressa());
        concluida.atualizarStatus(StatusRastreamento.EM_TRANSITO);
        concluida.atualizarStatus(StatusRastreamento.CONCLUIDO);

        RegistroRastreamento outroRegistro = new RegistroRastreamento();
        RemessaPedido externa = outroRegistro.criarRemessa("ENV-900", novoPedido(mouse), new EntregaExpressa());
        externa.atualizarStatus(StatusRastreamento.EM_TRANSITO);
        externa.atualizarStatus(StatusRastreamento.CONCLUIDO);

        try {
            registroErros.criarDevolucao("DEV-200", null, "Defeito");
            verificar("E2-11", "Remessa nula", "IllegalArgumentException", "nenhuma exceção");
        } catch (RuntimeException e) {
            verificar("E2-11", "Remessa nula", "IllegalArgumentException", tipo(e));
        }
        try {
            registroErros.criarDevolucao("DEV-201", externa, "Defeito");
            verificar("E2-12", "Remessa externa ao registro", "IllegalArgumentException", "nenhuma exceção");
        } catch (RuntimeException e) {
            verificar("E2-12", "Remessa externa ao registro", "IllegalArgumentException", tipo(e));
        }
        try {
            registroErros.criarDevolucao("DEV-202", concluida, null);
            verificar("E2-13", "Motivo nulo", "IllegalArgumentException", "nenhuma exceção");
        } catch (RuntimeException e) {
            verificar("E2-13", "Motivo nulo", "IllegalArgumentException", tipo(e));
        }
        try {
            registroErros.criarDevolucao("DEV-203", concluida, "   ");
            verificar("E2-14", "Motivo em branco", "IllegalArgumentException", "nenhuma exceção");
        } catch (RuntimeException e) {
            verificar("E2-14", "Motivo em branco", "IllegalArgumentException", tipo(e));
        }
        try {
            registroErros.criarDevolucao("  ", concluida, "Defeito");
            verificar("E2-15", "Código da devolução em branco", "IllegalArgumentException", "nenhuma exceção");
        } catch (RuntimeException e) {
            verificar("E2-15", "Código da devolução em branco", "IllegalArgumentException", tipo(e));
        }
        RemessaPedido emTransito = registroErros.criarRemessa("ENV-201", novoPedido(mouse), new EntregaExpressa());
        emTransito.atualizarStatus(StatusRastreamento.EM_TRANSITO);
        try {
            registroErros.criarDevolucao("DEV-204", emTransito, "Defeito");
            verificar("E2-16", "Remessa não concluída", "IllegalStateException", "nenhuma exceção");
        } catch (RuntimeException e) {
            verificar("E2-16", "Remessa não concluída", "IllegalStateException", tipo(e));
        }
        verificar("E2-16", "Nenhuma devolução registrada", "0", String.valueOf(registroErros.contarDevolucoes()));

        // E2-17 a E2-20: transições proibidas preservam o status
        subtitulo("E2-17 a E2-20 Transições inválidas");
        RemessaPedido nova = registroErros.criarRemessa("ENV-202", novoPedido(mouse), new EntregaExpressa());
        try {
            nova.atualizarStatus(StatusRastreamento.CONCLUIDO);
            verificar("E2-17", "Pular PREPARANDO -> CONCLUIDO", "IllegalStateException", "nenhuma exceção");
        } catch (RuntimeException e) {
            verificar("E2-17", "Pular PREPARANDO -> CONCLUIDO", "IllegalStateException", tipo(e));
        }
        verificar("E2-17", "Status preservado", "PREPARANDO", String.valueOf(nova.getStatus()));

        nova.atualizarStatus(StatusRastreamento.EM_TRANSITO);
        try {
            nova.atualizarStatus(StatusRastreamento.PREPARANDO);
            verificar("E2-18", "Retroceder EM_TRANSITO -> PREPARANDO", "IllegalStateException", "nenhuma exceção");
        } catch (RuntimeException e) {
            verificar("E2-18", "Retroceder EM_TRANSITO -> PREPARANDO", "IllegalStateException", tipo(e));
        }
        verificar("E2-18", "Status preservado", "EM_TRANSITO", String.valueOf(nova.getStatus()));

        try {
            nova.atualizarStatus(StatusRastreamento.EM_TRANSITO);
            verificar("E2-19", "Repetir EM_TRANSITO", "IllegalStateException", "nenhuma exceção");
        } catch (RuntimeException e) {
            verificar("E2-19", "Repetir EM_TRANSITO", "IllegalStateException", tipo(e));
        }
        verificar("E2-19", "Status preservado", "EM_TRANSITO", String.valueOf(nova.getStatus()));

        try {
            nova.atualizarStatus(null);
            verificar("E2-20", "Status nulo", "IllegalArgumentException", "nenhuma exceção");
        } catch (RuntimeException e) {
            verificar("E2-20", "Status nulo", "IllegalArgumentException", tipo(e));
        }
        verificar("E2-20", "Status preservado", "EM_TRANSITO", String.valueOf(nova.getStatus()));

        // E2-21 a E2-23: unicidade no mesmo registro (usa o registro do fluxo principal)
        subtitulo("E2-21 a E2-23 Unicidade");
        try {
            registro.criarRemessa("ENV-001", novoPedido(mouse), new EntregaExpressa());
            verificar("E2-21", "Código repetido", "IllegalStateException", "nenhuma exceção");
        } catch (RuntimeException e) {
            verificar("E2-21", "Código repetido", "IllegalStateException", tipo(e));
        }
        try {
            registro.criarRemessa("ENV-002", pedido, new EntregaExpressa());
            verificar("E2-22", "Segunda remessa do pedido", "IllegalStateException", "nenhuma exceção");
        } catch (RuntimeException e) {
            verificar("E2-22", "Segunda remessa do pedido", "IllegalStateException", tipo(e));
        }
        try {
            registro.criarDevolucao("DEV-002", remessa, "Arrependimento");
            verificar("E2-23", "Segunda devolução da remessa", "IllegalStateException", "nenhuma exceção");
        } catch (RuntimeException e) {
            verificar("E2-23", "Segunda devolução da remessa", "IllegalStateException", tipo(e));
        }
        verificar("E2-23", "Registros preservados (remessas/devoluções)", "1/1",
                registro.contarRemessas() + "/" + registro.contarDevolucoes());

        // E2-24 e E2-25: modalidade nula e retirada na loja
        subtitulo("E2-24/E2-25 Modalidade da remessa");
        RegistroRastreamento registroModalidade = new RegistroRastreamento();
        try {
            registroModalidade.criarRemessa("ENV-300", novoPedido(mouse), null);
            verificar("E2-24", "Modalidade nula", "IllegalArgumentException", "nenhuma exceção");
        } catch (RuntimeException e) {
            verificar("E2-24", "Modalidade nula", "IllegalArgumentException", tipo(e));
        }
        try {
            registroModalidade.criarRemessa("ENV-301", novoPedido(mouse), new RetiradaNaLoja());
            verificar("E2-25", "Retirada na loja", "IllegalStateException", "nenhuma exceção");
        } catch (RuntimeException e) {
            verificar("E2-25", "Retirada na loja", "IllegalStateException", tipo(e));
        }
        verificar("E2-25", "Nenhuma remessa registrada", "0", String.valueOf(registroModalidade.contarRemessas()));

        // E2-26: código com espaços nas extremidades (RT01)
        subtitulo("E2-26 Código com espaços");
        RegistroRastreamento registroCodigo = new RegistroRastreamento();
        RemessaPedido comEspacos = registroCodigo.criarRemessa("  ENV-400  ", novoPedido(mouse), new EntregaExpressa());
        verificar("E2-26", "Código normalizado", "ENV-400", comEspacos.getCodigoRastreio());
        try {
            registroCodigo.criarRemessa("ENV-400", novoPedido(mouse), new EntregaExpressa());
            verificar("E2-26", "Repetir código normalizado", "IllegalStateException", "nenhuma exceção");
        } catch (RuntimeException e) {
            verificar("E2-26", "Repetir código normalizado", "IllegalStateException", tipo(e));
        }
    }

    // ------------------------------------------------------------------
    // Utilitários de impressão e verificação
    // ------------------------------------------------------------------
    private static Pedido novoPedido(Produto produto) {
        Pedido pedido = new Pedido();
        pedido.adicionarProduto(produto);
        return pedido;
    }

    private static void verificar(String id, String descricao, String esperado, String obtido) {
        cenarios++;
        boolean ok = esperado.equals(obtido);
        if (!ok) {
            falhas++;
        }
        System.out.println((ok ? "  [OK]     " : "  [FALHOU] ") + id + " " + descricao
                + " | esperado: " + esperado + " | obtido: " + obtido);
    }

    private static void verificarValor(String id, String descricao, double esperado, double obtido) {
        cenarios++;
        boolean ok = Math.abs(esperado - obtido) < TOLERANCIA;
        if (!ok) {
            falhas++;
        }
        System.out.println((ok ? "  [OK]     " : "  [FALHOU] ") + id + " " + descricao
                + " | esperado: " + moeda(esperado) + " | obtido: " + moeda(obtido));
    }

    private static String tipo(RuntimeException e) {
        return e.getClass().getSimpleName();
    }

    private static String nomes(ArrayList<Produto> produtos) {
        ArrayList<String> nomes = new ArrayList<>();
        for (Produto produto : produtos) {
            nomes.add(produto.getNome());
        }
        return nomes.toString();
    }

    private static String moeda(double valor) {
        return String.format(BR, "R$ %.2f", valor);
    }

    private static void titulo(String texto) {
        System.out.println();
        System.out.println("==================================================");
        System.out.println(texto);
        System.out.println("==================================================");
    }

    private static void subtitulo(String texto) {
        System.out.println();
        System.out.println("-- " + texto);
    }
}
