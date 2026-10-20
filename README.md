# Rocket With Spear

Cliente/servidor (opcional) mod para **Minecraft 26.3** (Fabric, Java 25) que permite usar
foguetes de fogo-de-artifício enquanto a spear/tridente está a carregar, através de uma
keybind dedicada.

## Porquê

Em vanilla, enquanto o jogador está a "usar" um item (a carregar a spear com o botão
direito), o cliente ignora novos cliques de usar — logo não consegue usar um foguete
para manter a velocidade (necessária para o dano da spear).

A keybind chama diretamente `MultiPlayerGameMode#useItem`, que contorna o bloqueio
do lado do cliente. O servidor vanilla **aceita** o pacote resultante, porque
`ServerPlayerGameMode#useItem` não verifica se o jogador já está a usar outro item.

## Como funciona

- **Fase 1 (client-only):** funciona em servidores vanilla **sem** o mod.
- **Fase 2 (opcional):** se o servidor também tiver o mod (detetado com
  `ClientPlayNetworking.canSend`), é enviado um pacote custom que o servidor valida
  e replica o comportamento vanilla.

O mod **não** adiciona itens, blocos, entidades nem registos: quem não tem o mod
entra normalmente.

## Keybind

- `V` por defeito (reconfigurável em Opções → Controlos).

## Condições (configuráveis)

- Só funciona a voar com élytra (`isFallFlying`).
- Só funciona enquanto a spear/tridente está a carregar.
- O foguete tem de estar na mão livre (por defeito a mão secundária/offhand).

## Configuração

Ficheiro `config/rocketwithspear.json`. Opções: `enabled`, `preferredHand`,
`requireFlying`, `requireSpearCharging`, `cooldownTicks`, `allowServerMod`.
Ecrã nativo disponível pelo **Mod Menu** (só `compileOnly`/`localRuntime` — o mod
não exige o Mod Menu).

## Build

```
./gradlew build
```

Jar em `build/libs/rocketwithspear-1.0.0.jar`.
