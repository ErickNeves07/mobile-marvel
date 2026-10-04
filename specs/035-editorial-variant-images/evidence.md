# 035 — Imagens editoriais distintas por variante: evidências

2026-10-04: chamada autenticada `character/4005-1443` com `field_list=id,name,image,images,associated_images` respondeu HTTP 200 e somente `id,image,name`. Busca `resources=issue&query=Spider-Man&limit=6` respondeu 200 com seis edições distintas da série Spider-Man, cada uma com `image`. Amostra `issue/4000-1143006` respondeu com imagem e `character_credits=[]`; portanto o vínculo visual do herói na capa ainda exige seleção/inspeção, não pode ser inferido somente da API.

Fonte primária pública consultada: https://comicvine.gamespot.com/forums/api-developers-2334/the-new-api-same-as-the-old-api-except-for-the-dif-1449264/ .
