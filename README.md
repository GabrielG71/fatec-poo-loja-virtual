# Loja Virtual - Trabalho de recuperação da P1

**Aluno:** Gabriel Gonçalves
**Disciplina:** Programação Orientada a Objetos - Fatec

Trabalho da atividade `a06-trabalho`, com as duas etapas: na primeira, catálogo, pedidos e modalidades de entrega (herança e polimorfismo); na segunda, rastreamento de remessas e devoluções usando interface.

## Como compilar e executar

Precisa do JDK 17 ou mais novo. Na raiz do projeto:

```bash
javac -d out lojaonline/*.java
java -cp out lojaonline.Main
```

O `Main` roda todos os cenários em sequência, sem parar quando uma exceção esperada acontece. Cada verificação aparece como `[OK]` ou `[FALHOU]`, com o valor esperado e o obtido. Na última execução deram 90 verificações e nenhuma falha.

## Estrutura

| Arquivo | Papel |
|---|---|
| `Categoria`, `Produto` | Dados do catálogo, com validações nos construtores (RN01, RN02). |
| `Catalogo` | Cadastro, busca e `listarPorCategoria` (RN03 a RN05). |
| `ItemPedido` | Linha do pedido: `calcularSubtotal` e `aumentarQuantidade` (RN08, RN14). |
| `Pedido` | `adicionarProduto` (duas versões), `calcularSubtotal`, `calcularTotal`, `contarItens` (RN06 a RN13). |
| `ModalidadeEntrega` | Classe abstrata com `calcularFrete(double)` abstrato. |
| `EntregaEconomica`, `EntregaExpressa`, `RetiradaNaLoja` | Subclasses que sobrescrevem `calcularFrete` (RN09, RN10). |
| `Rastreavel` | Interface com `getCodigoRastreio`, `getStatus` e `consultarRastreamento`. |
| `StatusRastreamento` | Enum com `PREPARANDO`, `EM_TRANSITO` e `CONCLUIDO`. |
| `RemessaPedido`, `DevolucaoPedido` | Transportes que implementam `Rastreavel` (RT01 a RT08, RT11). |
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
| E2-17 | 2 / RT04 / atualizarStatus | PREPARANDO para CONCLUIDO (pulando etapa) | IllegalStateException; status PREPARANDO | IllegalStateException; PREPARANDO |
| E2-18 | 2 / RT04 / atualizarStatus | EM_TRANSITO para PREPARANDO (voltando) | IllegalStateException; status EM_TRANSITO | IllegalStateException; EM_TRANSITO |
| E2-19 | 2 / RT04 / atualizarStatus | EM_TRANSITO para EM_TRANSITO (repetindo) | IllegalStateException; status EM_TRANSITO | IllegalStateException; EM_TRANSITO |
| E2-20 | 2 / RT05 / atualizarStatus | Status nulo | IllegalArgumentException; status EM_TRANSITO | IllegalArgumentException; EM_TRANSITO |
| E2-21 | 2 / RT02 / criarRemessa | Código "ENV-001" repetido no mesmo registro | IllegalStateException | IllegalStateException |
| E2-22 | 2 / RT02 / criarRemessa | Segunda remessa do mesmo pedido | IllegalStateException | IllegalStateException |
| E2-23 | 2 / RT02 / criarDevolucao | Segunda devolução da mesma remessa | IllegalStateException; registros preservados (1 remessa / 1 devolução) | IllegalStateException; 1/1 |
| E2-24 | 2 / RT09 / criarRemessa | Modalidade nula | IllegalArgumentException | IllegalArgumentException |
| E2-25 | 2 / RT09 / criarRemessa | RetiradaNaLoja | IllegalStateException; não registrada | IllegalStateException; 0 remessas |
| E2-26 | 2 / RT01 / criarRemessa | Código "  ENV-400  " e depois "ENV-400" | Código "ENV-400"; repetição lança IllegalStateException | ENV-400; IllegalStateException |

## Respostas da Etapa 1

**Por que categoria é uma classe?**

Porque a categoria tem dados próprios (código e nome), é cadastrada no catálogo separada dos produtos e vários produtos apontam para a mesma categoria. Se fosse só uma `String` dentro do `Produto`, eu não teria como validar o código, impedir categoria repetida, exigir que o produto use uma categoria já cadastrada (RN04) nem filtrar por código. Sendo classe, também dá pra cadastrar uma categoria nova, como "Livros", sem mexer em código nenhum.

**Onde há composição?**

Entre `Pedido` e `ItemPedido`. O próprio pedido cria os itens dentro do `adicionarProduto`, ele não recebe um `ItemPedido` pronto de fora, e o item só faz sentido dentro daquele pedido. Já o `Catalogo` com `Produto` e `Categoria` é agregação, e as ligações de `ItemPedido` com `Produto` e de `Produto` com `Categoria` são associações, porque o produto existe sozinho e pode aparecer em vários pedidos.

**Qual a diferença entre sobrecarga e sobrescrita neste programa?**

A sobrecarga está no `Pedido`: tem dois `adicionarProduto`, um que recebe só o produto e outro que recebe produto e quantidade. Quem decide qual chamar é o compilador, olhando os argumentos. A versão sem quantidade só chama a outra passando 1, assim a regra fica num lugar só.

A sobrescrita está nas modalidades de entrega: `EntregaEconomica`, `EntregaExpressa` e `RetiradaNaLoja` reescrevem o `calcularFrete` que vem da `ModalidadeEntrega`, com `@Override`. Aqui a escolha acontece na execução: o `calcularTotal` recebe uma `ModalidadeEntrega`, e o frete calculado depende de qual objeto foi passado.

**O que precisa mudar para acrescentar uma modalidade de entrega?**

Só criar uma classe nova que estende `ModalidadeEntrega`, chama o `super` com o nome e sobrescreve o `calcularFrete`. O `Pedido` não muda, porque o `calcularTotal` só chama `modalidade.calcularFrete(subtotal)`, sem `instanceof`, `switch` ou comparação de nome.

## Respostas da Etapa 2

**Por que remessa e devolução implementam a mesma interface?**

Porque as duas são transportes e precisam ser consultadas do mesmo jeito: código, status e descrição. Com a interface `Rastreavel`, dá pra colocar as duas num mesmo array e consultar num laço só, e cada uma responde do seu jeito (a remessa fala em "entregue ao cliente" e a devolução em "recebida pela loja"). E elas não precisam herdar de uma classe em comum pra isso.

**Qual a diferença entre `Rastreavel` e a classe abstrata `ModalidadeEntrega`?**

A `ModalidadeEntrega` é uma classe abstrata: tem atributo (`nome`), construtor e o `getNome()` já pronto, e as subclasses herdam isso. É uma relação de "é um", e cada classe só pode estender uma. A `Rastreavel` é uma interface, só define quais métodos a classe tem que ter, sem atributo e sem implementação. Classes que não têm nada a ver uma com a outra podem implementar a mesma interface, e uma classe pode implementar várias. Além disso, a modalidade serve pra calcular frete, e o rastreável representa um envio que dá pra acompanhar.

**Por que `Pedido` não precisa implementar `Rastreavel`?**

Porque o pedido é a compra em si, com itens e valores, e não um transporte. Ele não tem código de rastreio nem status de envio. Quem é rastreado é a `RemessaPedido`, que guarda uma referência ao pedido. Um pedido pode nem ter transporte (retirada na loja), ou pode ter uma remessa e depois uma devolução, cada uma com o seu rastreio.

**Como acrescentar outro transporte sem mudar o laço de consulta?**

Criando uma classe nova, por exemplo `TrocaPedido`, que implementa `Rastreavel` e os três métodos com `@Override`, e colocando o objeto no array de `Rastreavel`. Como o laço só usa `getCodigoRastreio()` e `consultarRastreamento()`, ele funciona com a classe nova sem precisar mudar nada.
