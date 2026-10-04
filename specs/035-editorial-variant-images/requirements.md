# 035 — Imagens editoriais distintas por variante

Status: Erick pediu em 2026-10-04 uma imagem diferente do mesmo herói para cada variante, mesmo que a arte não represente sua variante fictícia. A fonte alternativa exata aguarda Q-051.

## Confirmado

- O catálogo mantém o personagem e suas cinco variantes authored; imagem é editorial e não define atributos/poderes.
- Cada variante deve mostrar imagem diferente do mesmo herói quando a API oferecer fonte verificável, com crédito Comic Vine e link da ficha/edição; evitar repetir a mesma URL sob nomes diferentes.
- Não incorporar binários Comic Vine ao APK nem expor chave; backend usa cache/throttle e Android carrega HTTPS com fallback/retry.

## Dúvida crítica Q-051

O registro de personagem Comic Vine expõe um único campo `image`; teste da ficha Homem-Aranha com `image,images,associated_images` devolveu apenas `image`. A busca de edições oferece capas distintas, mas uma capa pode incluir outros personagens ou não mostrar o herói. Erick precisa confirmar se capas de edições associadas/solo são aceitáveis como arte da variante com crédito da edição, ou indicar outra fonte de retratos isolados. Até lá não afirmar cobertura de 105 artes distintas.

## Aceite após decisão

- Cinco URLs HTTPS diferentes por herói onde houver cinco imagens validadas; imagem principal é fallback quando alternativa não existir ou não carregar.
- Detalhe e comparação escolhem URL pela variante, mostram fonte apropriada e preservam uso de dados authored para o jogo.
- Testes do proxy validam vínculo/editorial e segurança; Android testa seleção/fallback.
