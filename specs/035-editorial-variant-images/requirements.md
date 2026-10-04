# 035 — Imagens editoriais distintas por variante

Status: Erick aprovou em 2026-10-04 usar capas distintas da Comic Vine por variante, com crédito, mesmo quando outros personagens aparecerem.

## Confirmado

- O catálogo mantém o personagem e suas cinco variantes authored; imagem é editorial e não define atributos/poderes.
- Cada variante deve mostrar imagem diferente do mesmo herói quando a API oferecer fonte verificável, com crédito Comic Vine e link da ficha/edição; evitar repetir a mesma URL sob nomes diferentes.
- Não incorporar binários Comic Vine ao APK nem expor chave; backend usa cache/throttle e Android carrega HTTPS com fallback/retry.

## Q-051 resolvida

O registro de personagem Comic Vine expõe um único campo `image`; teste da ficha Homem-Aranha com `image,images,associated_images` devolveu apenas `image`. A busca de edições oferece capas distintas. Erick aceitou capas de edições do herói mesmo quando outros personagens aparecerem; o backend deve selecionar edições cujo título/volume vincula o herói e registrar crédito. Não afirmar que a capa retrata visualmente só o herói.

## Aceite após decisão

- Cinco URLs HTTPS diferentes por herói onde houver cinco imagens validadas; imagem principal é fallback quando alternativa não existir ou não carregar.
- Detalhe e comparação escolhem URL pela variante, mostram fonte apropriada e preservam uso de dados authored para o jogo.
- Testes do proxy validam vínculo/editorial e segurança; Android testa seleção/fallback.
