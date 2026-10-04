# 014 — Superfície em linguagem de quadrinhos para Deadpool: Design

`ComicPanelDrawable` desenha fundo em gradiente dos tokens, borda comum e dois filetes angulares de movimento em opacidade baixa usando `accent_cyan`/`accent_gold`. Um recorte inclinado no canto superior direito sugere vinheta de painel. Canvas faz clip na borda arredondada; coordenadas dependem dos bounds e terminam antes da área de descrição do hero. Shader é cacheado enquanto bounds não mudam; alpha opaco usa save/restore simples sem camada offscreen.

Aplicar como background do cartão hero apenas em Deadpool. O ícone/destaque/texto existente permanece na frente e unchanged semanticamente; a arte abstrata não vira View nem descrição. Sem animação, frase ou conteúdo narrativo.

Validar compilação/tests/lint e AVD em viewport normal, compacto e fonte 1.3; comparar Nexus/Forja para confirmar isolamento.
