# Artes dos personagens e variantes

## Plano vigente para a entrega de 2026-10-05

Erick adiou a produção de 105 artes próprias. O Android consulta, via backend, o retrato editorial da Comic Vine para cada um dos 21 personagens e o reutiliza visualmente nas cinco variantes. O nome/tier e os atributos continuam sendo dados do jogo; a interface identifica a imagem como retrato editorial do personagem, que pode não mostrar aquele traje. O arquivo de imagem não é incorporado ao APK. Sem host HTTPS ou conexão, aparece estado explícito de retrato indisponível. Consulte `specs/030-comic-vine-character-art/`.

O restante deste guia descreve apenas a substituição futura opcional por artes próprias.

## Decisão de fonte

Use a Comic Vine para consulta editorial de personagens, com crédito e link para a fonte quando os dados forem exibidos. O serviço pode devolver `image_url`, mas a interface Android atual ainda não exibe essa imagem; a URL não é uma licença para empacotar retratos no APK ou enviá-los para edição em um gerador. Os [termos da Comic Vine](https://comicvine.gamespot.com/api/) restringem uso comercial e proíbem editar, manipular ou reproduzir o conteúdo em outro meio.

Para os 21 personagens × cinco variantes próprias do jogo, a melhor cobertura é criar arte específica e revisá-la, mantendo o personagem reconhecível e a linguagem visual do Lovable. Uma API editorial pode servir como referência de identidade ou para sua própria tela editorial, conforme os termos aplicáveis; não garante todas as variantes nem o direito de republicar imagens.

## Caminho gratuito recomendado nesta máquina: Leonardo.Ai Free

A máquina de desenvolvimento tem Intel Iris Xe integrada (~2 GB reportados) e 16 GB de RAM; geração local de 105 retratos é incerta e tende a ser lenta. O [plano Free do Leonardo.Ai](https://leonardo.ai/pricing) oferece geração no navegador com **150 tokens diários** e referência de imagem limitada, sem exigir GPU local. As criações gratuitas são públicas; o serviço retém direitos e concede uma licença não exclusiva de uso comercial, conforme sua página de planos. A quota diária e o custo variável por geração tornam improvável concluir e revisar 105 artes de qualidade em um único dia.

1. Erick cria/abre sua conta no [Leonardo.Ai](https://app.leonardo.ai/) e confirma que o plano exibido é **Free**, sem adicionar pagamento.
2. Em geração de imagens, use formato vertical **2:3** e produza **uma** arte do Wolverine Origem com o prompt abaixo. Confira antes de gerar quantos tokens a operação consome; recursos de referência/edição podem gastar mais.
3. Revise identidade, traje, rosto, mãos, contraste e legibilidade no tamanho do card. Se aprovada, use o recurso de referência de imagem disponível no plano para as quatro variantes seguintes do mesmo personagem, preservando enquadramento e traços.
4. Baixe os arquivos aprovados em PNG ou WebP, nomeando por ID do catálogo e tier. Registre prompt, modelo, data e termos; então integro no Android e confiro as telas.

Se usar a [referência de imagem do Leonardo](https://intercom.help/leonardo-ai/en/articles/9656648-how-to-upload-images-to-leonardo-ai), envie somente uma imagem que você tenha direito de editar e reutilizar no app. A geração do GPT integrada recusou três tentativas de Wolverine em outro chat; isso não garante nem impede o resultado no Leonardo. Uma imagem transformada também precisa de revisão de direitos e qualidade antes de entrar no projeto.

### Prompt de teste — Wolverine: Arma X Rebelde (`wolverine-origin`)

> Wolverine (Logan), “Rebel Weapon X” variant, unmistakably recognizable Marvel comic-book character, muscular compact build, rugged face with thick sideburns, dark swept-back hair, intense expression, three metal adamantium claws extended from each hand. Battle-worn yellow and dark blue suit with black accents, subtle Weapon X laboratory scars and restrained tactical details. Waist-up heroic pose, both hands and all six claws clearly visible, anatomically correct. Premium cinematic comic-book illustration for a mobile collectible card, dramatic amber and icy blue rim lighting, dark near-black background (#05070c) with a faint dimensional rift, strong separation from the background, detailed face and costume, sharp focus, high contrast, vertical 2:3 composition, centered character, room around head and claws for cropping. No silhouette, no text, no logo, no watermark, no card frame, no extra fingers, no extra claws.

O serviço pode recusar uma solicitação envolvendo personagens conhecidos; um plano gratuito não garante saída para todos os 105 designs. O direito de uso de uma saída da ferramenta também não substitui os direitos relativos aos próprios personagens.

## Caminho hospedado pago: OpenAI Image API

1. Acesse o [painel de desenvolvedor OpenAI](https://platform.openai.com/) com sua conta e configure chave e limite de gasto do projeto. A Image API é cobrada por uso; confirme o valor atual antes de iniciar lote.
2. Guarde a chave somente no ambiente do seu computador ou gerenciador de segredos. Não a coloque em Git, `.env` que possa ser enviado, Android, screenshots ou conversa.
3. Siga o [guia oficial de geração de imagens](https://developers.openai.com/api/docs/guides/image-generation). Gere **uma amostra** antes do lote, com o modelo disponível na sua conta, formato vertical, personagem inteiro ou meio corpo, fundo transparente se o modelo suportar e sem texto sobreposto.
4. Use a primeira imagem aprovada como referência para editar as cinco versões do mesmo personagem. Preserve rosto, silhueta, traje-base e enquadramento; varie traje, Joia, energia e detalhes conforme os nomes das variantes em `shared/game_catalog.json`.
5. Revise cada resultado por identidade, mãos, acessórios, legibilidade em tamanho de card e consistência com o protótipo. Registre modelo, prompt, data, personagem, variante, termos e resultado da revisão. Depois de aprovadas, entregue as imagens em PNG ou WebP para integração no app.

Prompt inicial sugerido, para adaptar a cada personagem e variante: `Retrato vertical de [personagem], visual reconhecível dos quadrinhos, traje [descrição da variante], composição cinematográfica de card, personagem centralizado, contraste alto no fundo escuro, luz de recorte nas cores [cores], corpo da cintura para cima, mãos e rosto legíveis, sem letras, sem logotipos, sem moldura. Preserve a identidade visual entre as cinco variantes.`

## Caminho local: ComfyUI

Instale o [ComfyUI Desktop oficial](https://www.comfy.org/) para Windows se o computador tiver recursos suficientes. Escolha um modelo e workflow de geração/edição compatível com seu hardware e leia os termos desse modelo antes de usar a saída no projeto. Gere uma amostra e, se qualidade/tempo forem adequados, reutilize o mesmo workflow e referências no lote. A máquina atual não apresentou `nvidia-smi` nem instalação ComfyUI na verificação de 2026-10-04; isso não confirma ausência de GPU, mas exige checagem antes de escolher o fluxo local.

## Integração pendente

A criação do conjunto de 105 artes depende da ferramenta/acesso escolhido e da revisão das imagens. A tela da Coleção ainda usa apresentação sem os 105 retratos finais; gerar arquivos fora do app, por si só, não os instala. A integração incluirá mapeamento por ID/tier, otimização de tamanho, fallback, créditos/registro de procedência e teste visual em Android.
