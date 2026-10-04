# Avaliação de fontes de arte — 2026-10-02

## Critérios

Cada uma das 105 variantes precisa ter personagem reconhecível, traje/acessórios e efeitos próprios da descrição no catálogo, resolução adequada ao retrato do card e procedência com direito de uso no APK documentado. Uma imagem pública em alta resolução, sozinha, não satisfaz a cobertura nem a autorização de redistribuição.

## Fontes examinadas

| Fonte | Qualidade observada | Cobertura e uso |
|---|---|---|
| [Marvel Rivals, página oficial de heróis](https://www.marvelrivals.com/heroes/) e [PNG promocional de Wolverine](https://r.res.easebar.com/pic/20241204/86971e07-8ecf-4655-9630-94b5c28210a0.png) | Amostra 1920 × 728 px, 1.052.158 bytes, ilustração nítida e personagem reconhecível. Arte deslocada para a direita; recorte para card exigiria enquadramento. Cópia de pesquisa ignorada em `reports/lovable-research/marvel-rivals-sample.png`. | Apresenta um visual promocional, sem cobrir os cinco designs de Wolverine nem as 105 variantes. Página pública traz aviso © Marvel/NetEase; não foi identificada licença para incorporar em outro APK. Não incorporada. |
| [Comic Vine API](https://comicvine.gamespot.com/api/) | Consulta live de Wolverine em 2026-10-04 retornou JPEG editorial 781 × 1200 px, 89.200 bytes, personagem/traje reconhecíveis e enquadramento vertical adequado a card. Amostra em `reports/lovable-research/comicvine-wolverine-sample.jpg` (ignorada pelo Git). | Boa qualidade para exibição editorial, mas representa um único visual. Os termos da API limitam uso e redistribuição; não serve como biblioteca estática de 105 retratos no APK. Chave segue somente no `.env` ignorado. |
| Silhueta `CharacterArt` do protótipo Lovable | Enquadramento, fundo e efeitos servem como referência de composição. | Um caminho SVG genérico não diferencia personagem/variante e viola AC-06 como arte final. |

## Conclusão operacional

As fontes públicas avaliadas não entregam o conjunto de 105 artes específicas com procedência suficiente para empacotamento. A ferramenta integrada de geração foi acionada quatro vezes e retornou `moderation_blocked`, sem arquivo aproveitável. Para seguir, preparar uma ferramenta alternativa escolhida por Erick ou importar arquivos licenciados que ele fornecer. Cada arquivo final terá registro de prompt/fonte, ferramenta, data, ID de personagem, tier, licença/termos e revisão visual. Nenhuma arte de terceiro analisada foi incluída no app.

Opções técnicas em pesquisa: [ComfyUI oficial](https://github.com/Comfy-Org/ComfyUI) oferece fluxo local para vários tipos de GPU e API para lote; [OpenAI Image API](https://developers.openai.com/api/docs/guides/image-generation) oferece geração hospedada cobrada por uso. Instalação ou chamada paga aguardará a escolha/autorização correspondente. A escolha técnica não elimina a necessidade de verificar direitos de personagens e uso final do projeto.

Atualização 2026-10-04, após Erick pedir uma opção gratuita: a máquina informa Intel Iris Xe integrada (~2 GB) e ~16 GB de RAM. Como primeira amostra gratuita, recomendar [Leonardo.Ai Free](https://leonardo.ai/pricing): geração hospedada no navegador, 150 tokens diários, imagens públicas e licença não exclusiva de uso comercial conforme o próprio serviço. Quota, custo variável por imagem e revisão manual impedem prometer 105 artes em um dia. ComfyUI local permanece opção sem mensalidade, porém nesta máquina o desempenho/compatibilidade precisam de ensaio; [FLUX.1 Schnell](https://github.com/black-forest-labs/flux) tem pesos Apache-2.0, mas sua exigência de recursos pode ser inadequada à Iris Xe. Nenhuma conta foi criada e nenhuma imagem foi gerada por serviço externo nesta etapa.

Verificação local em 2026-10-04: ComfyUI não foi encontrado nos caminhos usuais (`C:\ComfyUI`, pasta pessoal, Downloads ou Programas locais), e `nvidia-smi` não está disponível no PATH. Isso não prova ausência de GPU, mas significa que o caminho local exigiria instalação/configuração antes de produzir o lote. Nenhuma instalação ou chamada paga foi feita.
