# 010 — Prévia informativa da Forja: Design

## Dados/contrato

`InfinityStone` declara `SPACE`, `MIND`, `REALITY`, `POWER`, `TIME`, `SOUL` na ordem do catálogo aprovado. Cada enum aponta para string Android; não tem inventário, ID de usuário, quantidade ou operação. Unit test fixa a lista/ordem.

## Apresentação

No destino Forja, após o conteúdo hero compartilhado, mostrar seção `Joias do Infinito`, texto da cadeia em um único cartão e seis cartões com nome da Joia e estado `Progressão em desenvolvimento`. Nenhum nível/quantidade fica marcado como possuído ou ausente.

Cartões reutilizam cores/estilos/tokens atuais e ficam dentro do ScrollView já usado pelas telas. Não são clicáveis.

## Segurança e limites

Sem economia, estado salvo, rede, asset externo ou ação. O aviso não substitui nem resolve perguntas abertas da spec 005.

## Verificação

Build/unit/lint e inspeção AVD no padrão, fonte 1.3 e 390×844 dp. Validar nomes, sequência, scroll até Alma/status e bounds acima da barra inferior.
