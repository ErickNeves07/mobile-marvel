# 012 — Assinatura visual de ruptura no Nexus: Design

## Tratamento

Criar `RuptureSurfaceDrawable`, um drawable Android sem estado. Ele preenche o retângulo arredondado com gradiente derivado dos tokens `surface_primary`/`surface_elevated`, desenha borda existente e acrescenta arcos/filetes diagonais de baixa opacidade derivados de `accent_cyan` e `accent_gold`. Geometria se escala pelos bounds do cartão; `clipPath` arredondado mantém o desenho interno. O shader deve ser recriado apenas quando os bounds mudarem e não usar camada offscreen no caso opaco.

Usar somente no hero quando `destination == NEXUS`; todos os outros destinos continuam com o `GradientDrawable` atual. Canvas restaura estado e a decoração é puramente visual, sem animação ou conteúdo semântico.

## Acessibilidade e validação

Texto e ícone permanecem acima do drawable e usam os mesmos estilos. Comparar screenshot do Nexus com destinos de controle em viewport normal, compacto e fonte 1.3; checar que a linha decorativa não cruza texto e não muda bounds/click targets.

Sem teste de negócio novo: unit tests existentes mais build/lint/assemble e inspeção AVD.
