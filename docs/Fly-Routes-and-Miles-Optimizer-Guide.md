# Fly Routes and Miles Optimizer — Guia Mestre

> Documento vivo: atualize decisões, hipóteses, marcos e fontes conforme o projeto evoluir.

## 1. Visão do produto

O Fly Routes and Miles Optimizer encontra e compara alternativas para uma viagem de ida, considerando voos pagos e emissões com milhas. O problema é encontrar itinerários executáveis, calcular seu custo efetivo e apresentar alternativas justificáveis.

### Pergunta central

Dada uma origem, um destino e uma data, o sistema deve:

1. gerar itinerários temporalmente viáveis;
2. associar ofertas em dinheiro ou milhas;
3. normalizar as ofertas em uma métrica monetária comum;
4. respeitar o budget informado;
5. ordenar e explicar as alternativas.

### Formulação do MVP

~~~
Entrada:
origem + destino + data exata + máximo de segmentos + budget
+ valuation por programa de milhas

Saída:
itinerários e ofertas viáveis, custo efetivo e recomendações
~~~

### Escopo do MVP

- viagem de ida;
- origem, destino e data de partida exatos;
- dados controlados antes de fontes reais;
- ofertas pagas em dinheiro e milhas;
- budget aplicado ao custo efetivo;
- recomendação mais barata e recomendação equilibrada.

Fora do MVP:

- ida e volta e múltiplas cidades;
- datas flexíveis;
- alertas de queda de preço;
- aeroportos alternativos, posicionamento e skiplagging;
- scraping de várias fontes e interface web completa.

### Custo efetivo

~~~
Oferta em dinheiro:
tarifa + taxas

Oferta em milhas:
(milhas / 1.000 × preço do milheiro) + taxas
~~~

Premissas já definidas:

- valuation é específica de um programa;
- valuation registra o instante em que foi observada;
- programa da emissão e programa da valuation devem ser iguais;
- moeda das taxas e moeda da valuation devem ser iguais;
- preço do milheiro é estritamente positivo;
- Money pode ser zero, pois taxas podem ser gratuitas;
- resultados monetários usam duas casas e RoundingMode.CEILING.

## 2. Princípios de design

### Separar fatos, regras e preferências

| Categoria | Exemplos | Onde pertence |
|---|---|---|
| Fatos operacionais | aeroportos, segmentos, horários | Airport, FlightSegment, Itinerary |
| Fatos comerciais | tarifa, milhas, taxas | CashOffer, MilesOffer |
| Preferências/premissas | valuation, budget, perfil de ranking | MilesValuation, SearchQuery |
| Regras | MCT, moeda compatível, ciclos | validadores e serviços de domínio |

### Itinerário não é oferta

O mesmo itinerário pode possuir mais de uma oferta.

~~~
Itinerary: GRU → CNF → LIS
├── CashOffer: R$ 2.000,00 + R$ 180,00 de taxas
└── MilesOffer: 45.000 milhas + R$ 180,00 de taxas
~~~

### TDD

Para cada regra:

1. escrever um teste que descreve o comportamento;
2. observar o estado vermelho;
3. implementar o mínimo para ficar verde;
4. refatorar preservando a suíte;
5. documentar a decisão quando ela afetar produto ou arquitetura.

## 3. Arquitetura-alvo

~~~
Usuário / CLI / API
        │
        ▼
SearchQuery ─────────────► planejador de consultas (futuro)
        │
        ▼
fontes controladas / APIs / scrapers
        │
        ▼
FlightSegment → FlightGraph → FlightFinderViability
                                      │
                                      ▼
                               List<Itinerary>
                                      │
                                      ▼
                    CashOffer / MilesOffer / FlightOffer
                                      │
                                      ▼
                       EffectiveCostCalculator
                                      │
                                      ▼
                 BudgetFilter + OfferRanker (futuro)
                                      │
                                      ▼
                      resultado explicado ao usuário
~~~

## 4. Árvore de pacotes e classes

~~~
src/main/java/com/flightoptimizer/
├── Main.java
├── domain/
│   ├── Airport.java
│   ├── AirportGroup.java                         # futuro: aeroportos próximos
│   ├── FlightSegment.java
│   ├── ConnectionValidator.java
│   ├── Itinerary.java
│   ├── Money.java
│   ├── MilesAmount.java
│   ├── MilesValuation.java
│   ├── FlightOffer.java
│   ├── CashOffer.java
│   ├── MilesOffer.java
│   ├── EffectiveCostCalculator.java
│   ├── SearchQuery.java                          # próxima classe
│   ├── BudgetFilter.java                         # futuro próximo
│   ├── OfferRanker.java                          # futuro próximo
│   ├── Recommendation.java                       # futuro próximo
│   ├── SearchResult.java                         # futuro
│   ├── TravelStrategy.java                       # futuro
│   └── HiddenCityEligibility.java                # futuro opcional
├── graph/
│   └── FlightGraph.java
├── search/
│   ├── FlightFinder.java                         # protótipo inicial
│   ├── FlightFinderViability.java                # BFS com rota viável
│   ├── CandidateGenerator.java                   # futuro
│   └── SearchPlanner.java                        # futuro
├── data/
│   ├── FlightDataSource.java                     # porta para dados
│   ├── OfferDataSource.java
│   ├── ControlledDataSource.java
│   └── dto/
├── integration/                                  # dados reais, depois
│   ├── scraper/
│   ├── api/
│   └── mapper/
└── application/
    ├── SearchFlightsUseCase.java
    └── SearchResponse.java

src/test/java/com/flightoptimizer/
├── domain/
├── graph/
├── search/
├── data/
└── integration/
~~~

## 5. Estado atual

### Já implementado e validado

- Maven, Java 21 e JUnit 5;
- Airport, FlightSegment, FlightGraph e ConnectionValidator;
- busca BFS limitada por segmentos;
- prevenção de ciclos por rota;
- Itinerary imutável, com origem e destino derivados;
- Money com BigDecimal e Currency;
- MilesAmount com programa e quantidade;
- FlightOffer, CashOffer e MilesOffer;
- MilesValuation com preço do milheiro e Instant;
- EffectiveCostCalculator para dinheiro e milhas;
- testes para regras de domínio e busca.

### Limitações intencionais do código atual

- BFS encontra rotas viáveis, não a rota economicamente ótima;
- MCT é uma constante simplificada;
- dados são controlados, não reais;
- não há filtro por budget, ranking ou SearchQuery;
- não há cache, persistência, scraping, API ou interface.

### Ajustes de manutenção

- manter valores de resultado com escala 2;
- preferir Instant fixo nos testes, não Instant.now();
- melhorar mensagens de exceção;
- renomear docs/produc-spec.md para docs/product-spec.md;
- registrar as definições de MVP no documento;
- fazer commits pequenos e coerentes.

## 6. Roadmap de produto e aprendizado

### Fase 0 — Grafo e rota viável — concluída

**Objetivo:** encontrar todas as rotas viáveis sob MCT e limite de segmentos.

**Aprendizado:** BFS, fila, estado de rota, ciclos, collections e TDD.

**Critério:** List<Itinerary> retornada e testes de rota, MCT, ciclo e limite verdes.

### Fase 1 — Especificação — em andamento

**Objetivo:** manter o contrato do MVP legível e testável.

**Entregável:** docs/product-spec.md com entradas, saídas, regras, premissas e exclusões.

### Fase 2 — Domínio e precificação — em andamento

**Objetivo:** representar dinheiro, milhas, ofertas e custo efetivo.

**Concluído:** Money, MilesAmount, Itinerary, FlightOffer, CashOffer, MilesOffer, MilesValuation e EffectiveCostCalculator.

### Fase 3 — Consulta, budget e ranking — próximo marco

**Classes a construir:**

~~~
SearchQuery
- origin: Airport
- destination: Airport
- departureDate: LocalDate
- maximumSegments: int
- budget: Money

BudgetFilter
- mantém ofertas com custo efetivo menor ou igual ao budget

OfferRanker
- ordena por custo efetivo
- perfil equilibrado: menor número de segmentos; empate por menor custo

Recommendation
- cheapestOffer
- balancedOffer
- premissas e critérios usados
~~~

**Decisões a tomar com testes:**

- origem e destino podem ser iguais? (não);
- budget zero pode ser permitido? (definir);
- o que acontece se valuation estiver ausente?;
- como comparar moeda do custo com moeda do budget?;
- como ordenar empates?;
- o que retornar para lista vazia ou quando todas ofertas excedem o budget?

### Fase 4 — Fluxo completo com dados controlados

**Objetivo:** demonstrar uma busca de ponta a ponta sem web.

Implementar:

- ControlledDataSource;
- SearchFlightsUseCase;
- SearchResult;
- Main demonstrável.

Cenário mínimo:

1. receber SearchQuery;
2. carregar voos e ofertas em memória;
3. encontrar itinerários;
4. calcular custo efetivo;
5. filtrar budget;
6. mostrar mais barata e equilibrada;
7. exibir milhas, taxas, valuation, duração e segmentos.

### Fase 5 — Qualidade e arquitetura

- testes de integração;
- README com execução e diagrama;
- GitHub Actions executando mvn test;
- logs;
- cache;
- persistência inicial (JSON ou SQLite);
- Architecture Decision Records;
- relatório de cobertura e cenários.

### Fase 6 — Dados reais e scraping responsável

Arquitetura:

~~~
site/API → adaptador/scraper → DTO bruto → mapper → domínio
~~~

Regras:

- priorizar APIs oficiais e ler termos de uso;
- respeitar rate limits, sessão e robots quando aplicável;
- não deixar HTML ou seletores entrarem no domínio;
- registrar source, observedAt e versão do parser;
- usar cache, retries e tratamento explícito de falhas;
- isolar cada fonte em adaptador próprio;
- testar parsers com respostas salvas quando permitido.

### Fase 7 — Busca avançada

- hubs, alianças e aeroportos alternativos;
- posicionamento;
- múltiplos programas;
- orçamento de consultas;
- heurísticas;
- Dijkstra/A* quando a formulação de custo permitir;
- fronteira de Pareto para custo, duração, conexões e risco.

### Fase 8 — Hidden-city/skiplagging — opcional

Somente depois do núcleo estável. Regras incluem destino como conexão, ausência de bagagem despachada, trechos posteriores não necessários e aviso explícito sobre riscos operacionais e contratuais.

### Fase 9 — Portfólio

- CLI, API ou interface;
- exemplos reproduzíveis;
- vídeo/capturas do fluxo;
- benchmarks de busca;
- README para recrutadores;
- arquitetura, trade-offs, testes e limitações explicadas.

## 7. Checklist de qualidade

### Para cada classe

- responsabilidade única;
- invariantes validados;
- testes de caso válido, inválido e borda;
- nenhuma dependência de UI, scraper ou banco dentro de domain;
- nome que revela a intenção.

### Para cada funcionalidade

- requisito escrito;
- teste vermelho pelo motivo correto;
- implementação mínima;
- mvn test verde;
- decisão documentada quando relevante.

### Antes de um commit

~~~bash
mvn test
git status --short
git diff --check
git add <arquivos-alvo>
git commit -m "feat: descreve a mudança"
~~~

Exemplos:

~~~
feat: calculate effective costs for cash and miles offers
feat: add search query domain model
test: cover budget filtering edge cases
docs: document MVP cost assumptions
~~~

## 8. Decisões já tomadas

| Decisão | Motivo |
|---|---|
| Java 21 + Maven + JUnit | base atual e suporte a records/testes |
| BFS para viabilidade | aprendizagem de grafos e enumeração por segmentos |
| BFS não é otimizador final | preço, milhas e risco exigem avaliação posterior |
| BigDecimal para dinheiro | evita erro de double |
| Currency em Money | evita conversão implícita entre moedas |
| long para milhas | modelo atual usa unidades inteiras |
| records para value objects | imutabilidade e igualdade por valor |
| interface para ofertas | CashOffer e MilesOffer não precisam de campos nulos artificiais |
| valuation por milheiro | linguagem comum no domínio e leitura simples |
| Instant para observedAt | referência pode mudar dentro do dia/fuso |
| CEILING para custo | estimativa conservadora |
| scraping posterior | algoritmo permanece testável com dados controlados |

## 9. Perguntas de raciocínio para cada decisão

- Isto é fato do domínio, preferência do usuário ou detalhe da fonte?
- Qual classe deve validar essa regra?
- Como provarei isso por teste?
- O que ocorre com dado ausente, inválido ou desatualizado?
- A solução é reprodutível sem web?
- A abstração reduz acoplamento ou só adiciona complexidade?
- O resultado explica por que venceu outra alternativa?

## 10. Plano educacional

### Rotina de sessão

1. escolher uma regra pequena;
2. explicar em palavras próprias;
3. escrever testes;
4. implementar a menor solução;
5. analisar a falha e refatorar;
6. registrar aprendizado e fazer commit ao fechar um marco.

### Mapa de estudo

| Tema | Uso no projeto | Evidência de domínio |
|---|---|---|
| Java e OO | classes, construtores, encapsulamento | explicar responsabilidade de cada classe |
| Records e interfaces | Money, Itinerary, ofertas | justificar record vs. serviço |
| Collections | segmentos e rotas | cópia defensiva e imutabilidade |
| JUnit/TDD | toda regra de domínio | teste falha pelo motivo certo |
| BigDecimal | custos e arredondamento | explicar escala e RoundingMode |
| Grafos/BFS | rotas viáveis | explicar fila, fronteira e ciclos |
| Caminho mínimo | evolução futura | comparar BFS, Dijkstra e A* |
| Design de domínio | ofertas e itinerários | separar operação, comércio e preferência |
| Scraping | fontes reais | normalizar dado sem contaminar domínio |
| Arquitetura | portas/adaptadores | trocar fonte sem alterar algoritmo |

## 11. Referências de estudo

### Java, Maven e testes

- [Java records — Oracle](https://docs.oracle.com/en/java/javase/25/language/records.html): records, acessores, construtor compacto e igualdade por valor.
- [BigDecimal — Oracle API](https://docs.oracle.com/en/java/javase/17/docs/api/java.base/java/math/BigDecimal.html): precisão decimal, escala e arredondamento.
- [Java Language Specification](https://docs.oracle.com/javase/specs/jls/se26/html/index.html): interfaces, records e sintaxe.
- [JUnit 5 User Guide](https://junit.org/junit5/docs/current/user-guide/): assertions, ciclo de vida e testes.
- [Maven Surefire](https://maven.apache.org/surefire/maven-surefire-plugin/usage.html): execução de mvn test.
- Livro: Effective Java, Joshua Bloch.
- Livro: Growing Object-Oriented Software, Guided by Tests, Steve Freeman e Nat Pryce.

### Algoritmos e grafos

- [Dijkstra, A Note on Two Problems in Connexion with Graphs (1959)](https://doi.org/10.1007/BF01386390).
- [Hart, Nilsson e Raphael, A Formal Basis for the Heuristic Determination of Minimum Cost Paths (1968)](https://doi.org/10.1109/TSSC.1968.300136).
- Livro: Introduction to Algorithms, Cormen, Leiserson, Rivest e Stein.
- Livro: Algorithms, Sedgewick e Wayne.

### Domínio e arquitetura

- Livro: Domain-Driven Design, Eric Evans.
- Livro: Implementing Domain-Driven Design, Vaughn Vernon.
- Livro: Clean Architecture, Robert C. Martin.
- [Designing Data-Intensive Applications](https://martin.kleppmann.com/2017/03/27/designing-data-intensive-applications.html), Martin Kleppmann.

### Dados reais e scraping

- [MDN Fetch API](https://developer.mozilla.org/en-US/docs/Web/API/Fetch_API): fundamentos de HTTP e respostas web.
- [OWASP Input Validation Cheat Sheet](https://cheatsheetseries.owasp.org/cheatsheets/Input_Validation_Cheat_Sheet.html): validação de dados externos.
- Documentação e termos de uso da fonte escolhida devem ser a referência principal para cada integração.

## 12. Definição de MVP concluído

O MVP estará concluído quando puder:

1. receber SearchQuery válida;
2. carregar voos e ofertas de fonte controlada;
3. encontrar itinerários viáveis;
4. calcular custo efetivo de dinheiro e milhas;
5. filtrar por budget;
6. retornar alternativa mais barata e equilibrada;
7. explicar preço, taxas, milhas, valuation, duração e segmentos;
8. executar testes verdes com mvn test;
9. ser executado e entendido por outra pessoa a partir do README.

## 13. Narrativa de portfólio

> Comecei com um problema pessoal de comparação entre tarifas e milhas. Modelei voos como grafo, separei itinerários de ofertas comerciais, normalizei dinheiro e milhas com valuation explícita, apliquei regras de viabilidade e construí o sistema de forma testável antes de integrar fontes reais. Depois evoluí a coleta com adaptadores para não acoplar o algoritmo a sites específicos.

Essa narrativa demonstra algoritmos, design de domínio, TDD, modelagem financeira, integração de dados, arquitetura evolutiva e comunicação de trade-offs.