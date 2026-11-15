# Multilaser Nova UI

Launcher Android nativo, feito em Java, com visual escuro premium, detalhes verde-lima e foco em aparelhos de entrada.

**Versão:** 1.1.0-preview  
**Android mínimo:** 6.0 (API 23)  
**Alvo prioritário de teste:** Multilaser M8 4G / Android 13 / 2 GB RAM.

## Recursos desta prévia
- Tela inicial e gaveta de aplicativos.
- Pesquisa local com atualização da grade sem reconstruir toda a tela a cada tecla.
- Favoritos: toque longo em um aplicativo para adicionar ou remover.
- Atalhos para Wi-Fi, Bluetooth e configurações de tela.
- Navegação inferior própria entre Início, Apps e Ajustes.
- Gestos horizontais para alternar entre Início e Apps.
- Tema claro/escuro e quatro cores de destaque.
- Grade configurável de 3 a 5 colunas.
- Tamanho dos ícones, rótulos e animações configuráveis.
- Acesso às informações dos aplicativos instalados.
- Preferências salvas localmente.
- Barras de status e navegação com cores adaptadas ao tema.

## Instalação
Baixe o APK na seção **Releases**, instale e escolha **Multilaser Nova UI** como aplicativo de início quando o Android perguntar.

Para voltar ao launcher anterior: Configurações do Android → Aplicativos padrão → App de início.

## Build
O GitHub Actions compila o APK com JDK 17 e Gradle 8.9. O workflow de release publica uma prévia automaticamente após atualizações na branch principal.

## Desempenho e compatibilidade
A interface usa componentes nativos do Android e evita bibliotecas visuais pesadas. A pesquisa não recria mais toda a tela ao digitar. Fluidez real, consumo de memória e compatibilidade ainda precisam ser avaliados no aparelho físico.

## Limitações do Android
Um launcher comum não pode substituir a navegação protegida do sistema (Início, Voltar, Recentes) nem controlar diretamente a navegação por gestos. A Nova UI fornece navegação dentro do próprio launcher e adapta as cores das barras do sistema quando permitido.

## Privacidade
A lista de aplicativos é consultada localmente. Favoritos e preferências ficam no armazenamento privado do aplicativo; não há conta, telemetria ou envio desses dados para servidor.

**Estado:** versão preview. Teste antes de definir como launcher padrão e mantenha o launcher original disponível para retorno.
