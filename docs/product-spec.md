# Fly Routes and Miles Optimizer — MVP

## Objetivo
Encontrar itinerários de ida viáveis para uma origem, destino e data exatos,
respeitando um budget máximo de custo efetivo.

## Entradas
- origem
- destino
- data de partida
- máximo de segmentos
- budget máximo em reais
- valor de referência da milha por programa

## Saídas
- rota de menor custo efetivo
- rota de melhor equilíbrio entre custo e número de conexões
- preço em reais, milhas, taxas e custo efetivo
- número de segmentos e duração total

## Regras
- custo efetivo não pode ultrapassar o budget;
- conexões devem respeitar o MCT;
- aeroportos não podem se repetir em uma rota;
- busca inicial usa dados controlados;
- skiplagging não faz parte desta primeira versão.

## Fora do MVP
- ida e volta e múltiplas cidades;
- flexibilidade de datas;
- alertas de queda de preço.

## Dados e premissas
- uma emissão em milhas usa um valor de referência configurável;
- esse valor deve registrar programa e data de referência;
- fontes reais e web scraping serão integrados posteriormente por adaptadores.
