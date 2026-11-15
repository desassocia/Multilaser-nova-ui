# Multilaser Nova UI

Launcher Android nativo com identidade visual escura, acento verde-lima e foco em aparelhos de entrada.

**Estado:** 1.0.0-alpha01 (base inicial). **Android mínimo:** 6.0 / API 23. **Alvo de teste:** Android 13.

## Recursos já implementados
- Tela inicial e gaveta de aplicativos.
- Pesquisa local dos aplicativos instalados.
- Dock de acesso rápido e abertura de apps.
- Toque longo para abrir as informações do aplicativo.
- Personalização de tema claro/escuro, cor de destaque, número de colunas, tamanho dos ícones e rótulos.
- Preferências persistidas no dispositivo.
- Launcher selecionável pelo menu de aplicativo de início do Android.
- GitHub Actions para compilar e publicar o APK de depuração como artefato.

## Compilação
Requer JDK 17, Android SDK Platform 35 e Gradle 8.9.

```bash
gradle --no-daemon assembleDebug
```

APK: `app/build/outputs/apk/debug/`.

## Instalação
Instale o APK e selecione **Multilaser Nova UI** ao pressionar Início. Para voltar ao launcher anterior: Configurações do Android → Aplicativos padrão → App de início.

## Privacidade
A lista de apps é consultada localmente para montar a interface e não é enviada a servidores. `QUERY_ALL_PACKAGES` é usada para descobrir apps lançáveis; a distribuição na Play Store exige análise das políticas aplicáveis.

## Próximas versões
Widgets hospedados pelo Android, páginas múltiplas, gestos configuráveis, seleção de papel de parede e backup/restauração do layout serão implementados em etapas após validar a estabilidade da base.

Este projeto está em fase alpha: recursos e compatibilidade ainda precisam ser testados em dispositivos reais.
