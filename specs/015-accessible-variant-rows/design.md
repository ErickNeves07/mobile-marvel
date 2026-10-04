# 015 — Variantes como linhas acessíveis na Coleção: Design

Substituir o `StringBuilder` multilinha por um TextView de corpo para cada `GameCatalogVariant`. Cada linha monta rótulo de tier já localizado (`GameVariantTier.labelRes`), dois-pontos e o nome do JSON. A primeira linha usa o espaçamento atual `space_2`; linhas seguintes usam `space_1`. Não definir contentDescription manual, clique ou seleção: o texto visível é o próprio anúncio acessível.

O nome do personagem continua antes das linhas e o próximo personagem sucede o quinto tier na ordem natural da árvore. Android/AVD confere 105 nós e passagem pelo primeiro/último grupo; escala de fonte exige rolagem normal, sem perda de nó.

Sem mudança de schema/dados/API/rede. Testar parser/contrato por suite atual e inspeção de hierarquia.
