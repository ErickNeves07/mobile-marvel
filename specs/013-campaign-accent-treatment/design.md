# 013 — Acentos visuais nos cartões de campanha: Design

Adicionar no topo do cartão um `View` decorativo de largura interna total e altura `border_width` ampliada a 2dp. X-Men recebe token `accent_gold`; Quarteto Fantástico, `accent_cyan`, refletindo as direções documentadas de âmbar/azul. Marcar a View `IMPORTANT_FOR_ACCESSIBILITY_NO` e não clicável. Abaixo dela, a estrutura atual e os estilos de texto permanecem iguais.

Um filete simples evita competir com título, equipe, chefe e status e não codifica progresso/disponibilidade.

Validação: suite unitária e Android build/lint; comparar ambos os cards no AVD a 1080×2400, 1024×2216 e fonte 1.3.
