# 035 — Imagens editoriais distintas por variante: design preliminar

- Comic Vine `character/4005-{id}` possui somente `image` para a ficha. Recurso `search` com `resources=issue`, query do nome do herói, `field_list=id,name,image,volume,site_detail_url` devolveu capas distintas para Homem-Aranha e links da edição. `issue/4000-{id}` pode devolver `character_credits`, mas a primeira edição amostrada tinha lista vazia; título solo é indício, não prova visual.
- Caso Erick aprove capas, o backend curará/validará até quatro edições por personagem com título/volume relacionados, URL HTTPS, identidade editorial e atribuição. O Android pedirá retrato por `(game_id, tier_id)`; Origem usa a ficha principal. Resultados incompletos retornam fallback explícito e nunca inventam URL.
- Não usar tamanhos diferentes da mesma imagem como imagens distintas; não alterar nem empacotar o binário Comic Vine.
- Testar com dados de API simulados e alguns recursos reais amostrados, respeitando orçamento de chamadas e cache.
