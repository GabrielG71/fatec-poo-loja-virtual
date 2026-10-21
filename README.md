# Loja Virtual — Trabalho de recuperação da P1 (POO)

Implementação das **Etapas 1 e 2** da atividade `a06-trabalho`: catálogo, pedidos e modalidades de entrega (herança e polimorfismo), e rastreamento de remessas e devoluções (interfaces).

## Como compilar e executar

Requer JDK 17 ou superior. Na raiz do projeto:

```bash
javac -d out lojaonline/*.java
java -cp out lojaonline.Main
```

O `Main` executa todos os cenários sem parar no primeiro erro esperado. Cada verificação é impressa como `[OK]` ou `[FALHOU]`, com o valor esperado e o obtido. Na última execução: **90 verificações, 0 falhas**.

## Estrutura

| Arquivo | Papel |
|---|---|
| `Categoria`, `Produto` | Dados do catálogo, com validações nos construtores (RN01, RN02). |
| `Catalogo` | Cadastro, busca e `listarPorCategoria` (RN03–RN05). |
| `ItemPedido` | Linha do pedido: `calcularSubtotal` e `aumentarQuantidade` (RN08, RN14). |
| `Pedido` | `adicionarProduto` (duas versões), `calcularSubtotal`, `calcularTotal`, `contarItens` (RN06–RN13). |
| `ModalidadeEntrega` | Classe abstrata com `calcularFrete(double)` abstrato. |
| `EntregaEconomica`, `EntregaExpressa`, `RetiradaNaLoja` | Subclasses que sobrescrevem `calcularFrete` (RN09, RN10). |
| `Rastreavel` | Interface com `getCodigoRastreio`, `getStatus` e `consultarRastreamento`. |
| `StatusRastreamento` | Enum `PREPARANDO`, `EM_TRANSITO`, `CONCLUIDO`. |
| `RemessaPedido`, `DevolucaoPedido` | Transportes que implementam `Rastreavel` (RT01–RT08, RT11). |
| `RegistroRastreamento` | Cria os transportes e controla a unicidade (RT02, RT09). |
| `Main` | Demonstração e cenários de verificação. |

## Registro dos resultados

### Etapa 1

| Cenário | Etapa / regra / método | Entrada ou operação | Esperado | Obtido |
|---|---|---|---|---|
| E1-01 | 1 / RN07, RN11 / Pedido | Teclado R$ 120 + mouse + mouse; laço sobre as 3 modalidades | 2 linhas; subtotal R$ 220,00; totais R$ 220,00 / R$ 240,00 / R$ 220,00 | 2 linhas; R$ 220,00; R$ 220,00 / R$ 240,00 / R$ 220,00 |
| E1-02 | 1 / RN12 / Pedido.calcularTotal | Mesmo pedido após as 3 simulações | Subtotal R$ 220,00 e 2 linhas (inalterado) | R$ 220,00; 2 linhas |
| E1-03 | 1 / RN08 / ItemPedido.calcularSubtotal | Mouse de R$ 50,00; quantidade 2 | R$ 100,00 | R$ 100,00 |
| E1-04 | 1 / RN09 / Pedido.calcularTotal | Uma unidade de mouse, econômica | Subtotal R$ 50,00; total R$ 60,00 | R$ 50,00; R$ 60,00 |
| E1-05 | 1 / RN09 / Pedido.calcularTotal | Quatro unidades de mouse, econômica | Subtotal R$ 200,00; total R$ 200,00 | R$ 200,00; R$ 200,00 |
| E1-06 | 1 / RN09 / EntregaEconomica.calcularFrete | Produto de teste de R$ 199,99, econômica | Frete R$ 10,00; total R$ 209,99 | R$ 10,00; R$ 209,99 |
| E1-07 | 1 / RN06, RN07 / Pedido.adicionarProduto | Mouse sem quantidade e depois com quantidade 2 | 1 linha (3 unidades); subtotal R$ 150,00 | 1 linha; R$ 150,00 |
| E1-08 | 1 / RN06 / Pedido.adicionarProduto | Quantidade 0 em pedido de R$ 50,00 | IllegalArgumentException; subtotal e linhas preservados | IllegalArgumentException; R$ 50,00; 1 linha |
| E1-09 | 1 / RN06 / Pedido.adicionarProduto | Quantidade -3 em pedido de R$ 50,00 | IllegalArgumentException; subtotal e linhas preservados | IllegalArgumentException; R$ 50,00; 1 linha |
| E1-10 | 1 / RN06 / Pedido.adicionarProduto | Produto nulo | IllegalArgumentException; pedido preservado | IllegalArgumentException; R$ 50,00; 1 linha |
| E1-11 | 1 / RN08, RN11, RN13 / Pedido | Pedido vazio | Subtotal R$ 0,00; 0 linhas; calcularTotal lança IllegalStateException | R$ 0,00; 0 linhas; IllegalStateException |
| E1-12 | 1 / RN11 / Pedido.calcularTotal | Pedido não vazio com modalidade nula | IllegalArgumentException | IllegalArgumentException |
| E1-13 | 1 / RN05 / Catalogo.listarPorCategoria | Filtrar Periféricos | [Teclado, Mouse] (sem Caderno) | [Teclado, Mouse] |
| E1-14 | 1 / RN05 / Catalogo.listarPorCategoria | Limpar a lista retornada e filtrar de novo | [Teclado, Mouse] (catálogo intacto) | [Teclado, Mouse] |
| E1-15 | 1 / RN05 / Catalogo.listarPorCategoria | Categoria "Livros" sem produtos | Lista vazia | [] |
| E1-16 | 1 / RN05 / Catalogo.listarPorCategoria | Categoria nula | IllegalArgumentException | IllegalArgumentException |
| E1-17 | 1 / RN14 / ItemPedido.aumentarQuantidade | Incremento 0 em item com 2 unidades | IllegalArgumentException; quantidade 2 | IllegalArgumentException; 2 |
| E1-18 | 1 / RN14 / ItemPedido.aumentarQuantidade | Incremento -1 em item com 2 unidades | IllegalArgumentException; quantidade 2 | IllegalArgumentException; 2 |
| E1-19 | 1 / RN14 / ItemPedido.aumentarQuantidade | Incremento 3 em item com 2 unidades | Quantidade 5 (soma, não substitui) | 5 |
| E1-20 | 1 / RN03 / Catalogo.cadastrarCategoria | Código de categoria repetido | IllegalArgumentException | IllegalArgumentException |
| E1-21 | 1 / RN04 / Catalogo.cadastrarProduto | Produto com categoria não cadastrada | IllegalArgumentException | IllegalArgumentException |

### Etapa 2

| Cenário | Etapa / regra / método | Entrada ou operação | Esperado | Obtido |
|---|---|---|---|---|
| E2-01 | 2 / RT04, RT11 / RemessaPedido | criarRemessa("ENV-001", pedido, econômica) | ENV-001; PREPARANDO; "Remessa em preparação" | ENV-001; PREPARANDO; Remessa em preparação |
| E2-02 | 2 / RT07 / RemessaPedido.consultarRastreamento | Avançar para EM_TRANSITO e CONCLUIDO | "Remessa em trânsito"; CONCLUIDO; "Remessa entregue ao cliente" | Remessa em trânsito; CONCLUIDO; Remessa entregue ao cliente |
| E2-03 | 2 / RT06, RT07 / DevolucaoPedido | criarDevolucao("DEV-001", remessa, "  Produto diferente do solicitado  ") e avançar para EM_TRANSITO | DEV-001; PREPARANDO; "Devolução em preparação"; motivo sem espaços; "Devolução em trânsito" | DEV-001; PREPARANDO; Devolução em preparação; Produto diferente do solicitado; Devolução em trânsito |
| E2-04 | 2 / RT07 / laço sobre Rastreavel[] | `{ remessa, devolucao }` | ENV-001: Remessa entregue ao cliente / DEV-001: Devolução em trânsito | ENV-001: Remessa entregue ao cliente / DEV-001: Devolução em trânsito |
| E2-05 | 2 / RT07 / DevolucaoPedido.consultarRastreamento | Concluir a devolução | CONCLUIDO; "Devolução recebida pela loja"; remessa continua CONCLUIDO | CONCLUIDO; Devolução recebida pela loja; CONCLUIDO |
| E2-06 | 2 / RT08 / consultas de Rastreavel | Consultar os dois transportes 3 vezes | Subtotal R$ 220,00, 2 linhas e status preservados | R$ 220,00; 2 linhas; CONCLUIDO / CONCLUIDO |
| E2-07 | 2 / RT03 / criarRemessa | Pedido nulo | IllegalArgumentException; não registrada | IllegalArgumentException |
| E2-08 | 2 / RT03 / criarRemessa | Pedido vazio | IllegalStateException; não registrada | IllegalStateException |
| E2-09 | 2 / RT01 / criarRemessa | Código nulo | IllegalArgumentException; não registrada | IllegalArgumentException |
| E2-10 | 2 / RT01 / criarRemessa | Código "   " | IllegalArgumentException; nenhuma remessa registrada | IllegalArgumentException; 0 remessas |
| E2-11 | 2 / RT06 / criarDevolucao | Remessa nula | IllegalArgumentException | IllegalArgumentException |
| E2-12 | 2 / RT02 / criarDevolucao | Remessa criada em outro registro | IllegalArgumentException | IllegalArgumentException |
| E2-13 | 2 / RT06 / criarDevolucao | Motivo nulo | IllegalArgumentException | IllegalArgumentException |
| E2-14 | 2 / RT06 / criarDevolucao | Motivo "   " | IllegalArgumentException | IllegalArgumentException |
| E2-15 | 2 / RT01 / criarDevolucao | Código "  " | IllegalArgumentException | IllegalArgumentException |
| E2-16 | 2 / RT06 / criarDevolucao | Remessa ainda EM_TRANSITO | IllegalStateException; nenhuma devolução registrada | IllegalStateException; 0 devoluções |
| E2-17 | 2 / RT04 / atualizarStatus | PREPARANDO → CONCLUIDO (pular) | IllegalStateException; status PREPARANDO | IllegalStateException; PREPARANDO |
| E2-18 | 2 / RT04 / atualizarStatus | EM_TRANSITO → PREPARANDO (retroceder) | IllegalStateException; status EM_TRANSITO | IllegalStateException; EM_TRANSITO |
| E2-19 | 2 / RT04 / atualizarStatus | EM_TRANSITO → EM_TRANSITO (repetir) | IllegalStateException; status EM_TRANSITO | IllegalStateException; EM_TRANSITO |
| E2-20 | 2 / RT05 / atualizarStatus | Status nulo | IllegalArgumentException; status EM_TRANSITO | IllegalArgumentException; EM_TRANSITO |
| E2-21 | 2 / RT02 / criarRemessa | Código "ENV-001" repetido no mesmo registro | IllegalStateException | IllegalStateException |
| E2-22 | 2 / RT02 / criarRemessa | Segunda remessa do mesmo pedido | IllegalStateException | IllegalStateException |
| E2-23 | 2 / RT02 / criarDevolucao | Segunda devolução da mesma remessa | IllegalStateException; registros preservados (1 remessa / 1 devolução) | IllegalStateException; 1/1 |
| E2-24 | 2 / RT09 / criarRemessa | Modalidade nula | IllegalArgumentException | IllegalArgumentException |
| E2-25 | 2 / RT09 / criarRemessa | RetiradaNaLoja | IllegalStateException; não registrada | IllegalStateException; 0 remessas |
| E2-26 | 2 / RT01 / criarRemessa | Código "  ENV-400  " e depois "ENV-400" | Código "ENV-400"; repetição lança IllegalStateException | ENV-400; IllegalStateException |

## Respostas — Etapa 1

**Por que categoria é uma classe?**
Uma categoria tem identidade e dados próprios (código e nome), é cadastrada no catálogo de forma independente e é compartilhada por vários produtos. Se fosse só uma `String` em `Produto`, não daria para validar o código, impedir categorias duplicadas, exigir que o produto use uma categoria já cadastrada (RN04) nem filtrar por código. Como é uma classe, também dá para cadastrar novas categorias ("Livros") sem mudar o código.

**Onde há composição?**
Entre `Pedido` e `ItemPedido`. O pedido cria os próprios itens dentro de `adicionarProduto`: não recebe um `ItemPedido` pronto, e um item não faz sentido fora do pedido. Já `Catalogo`–`Produto`/`Categoria` é agregação, e `ItemPedido`/`Produto` → `Categoria` são associações: o produto existe sozinho e pode estar em vários pedidos.

**Diferença entre sobrecarga e sobrescrita neste programa?**
- *Sobrecarga* é ter dois métodos com o mesmo nome e parâmetros diferentes na mesma classe. É o caso de `adicionarProduto(Produto)` e `adicionarProduto(Produto, int)`. O compilador escolhe qual chamar pelos argumentos, e a versão sem quantidade delega para a outra com `1`.
- *Sobrescrita* é quando a subclasse redefine um método herdado com a mesma assinatura. É o caso de `calcularFrete(double)` em `EntregaEconomica`, `EntregaExpressa` e `RetiradaNaLoja`, marcado com `@Override`. A versão executada é decidida em tempo de execução, pelo objeto recebido como `ModalidadeEntrega` em `Pedido.calcularTotal`.

**O que precisa mudar para acrescentar uma modalidade de entrega?**
Só criar uma nova subclasse de `ModalidadeEntrega` que chame `super("Nome")` e sobrescreva `calcularFrete`. `Pedido` não muda nada, porque `calcularTotal` chama `modalidade.calcularFrete(subtotal)` sem `instanceof`, `switch` nem teste de nome.

## Respostas — Etapa 2

**Por que remessa e devolução implementam a mesma interface?**
As duas são transportes que precisam ser consultados do mesmo jeito: código, status e descrição. Com o contrato `Rastreavel`, quem consulta trata os dois de forma uniforme, por exemplo no laço sobre `Rastreavel[]`, e cada classe dá a sua descrição ("entregue ao cliente" × "recebida pela loja"). Elas não precisam ter uma superclasse em comum para isso.

**Diferença entre `Rastreavel` e a classe abstrata `ModalidadeEntrega`?**
`ModalidadeEntrega` é uma classe abstrata: guarda estado (`nome`), tem construtor e `getNome()` implementado, e as subclasses herdam isso numa relação "é um" (uma só superclasse). `Rastreavel` é uma interface: só declara um contrato de consultas, sem atributos nem implementação. Classes sem parentesco, como `RemessaPedido` e `DevolucaoPedido`, podem implementá-la, e uma classe pode implementar várias interfaces. Além disso, a modalidade *calcula frete*, enquanto o rastreável *representa um envio* que pode ser acompanhado.

**Por que `Pedido` não precisa implementar `Rastreavel`?**
Porque o pedido é a compra (itens e valores), não um transporte. Ele não tem código de rastreio nem status de envio. Quem é rastreado é a `RemessaPedido`, que *referencia* o pedido. Um mesmo pedido pode até não gerar transporte (retirada na loja), ou gerar uma remessa e depois uma devolução, cada uma com seu próprio rastreio.

**Como acrescentar outro transporte sem mudar o laço de consulta?**
Criar uma nova classe (por exemplo, `TrocaPedido`) que implemente `Rastreavel` e os três métodos com `@Override`, e colocar o objeto no array `Rastreavel[]`. O laço só usa `getCodigoRastreio()` e `consultarRastreamento()`, então funciona com o novo tipo sem nenhuma alteração.
