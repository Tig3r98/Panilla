# PanillaX
PanillaX (the name) is a combination of the word Packet and Vanilla (as in Vanilla Minecraft).

PanillaX is a fork of [Panilla](https://www.spigotmc.org/resources/65694/) by ds58 that only supports recent Paper servers and is easier to compile.

![bStats](https://bstats.org/signatures/bukkit/PanillaX.svg)

## Overview
PanillaX is software to prevent abusive NBT and packets on Minecraft servers.

With this software, you will be able to prevent:

- Unobtainable Enchantments (eg. Sharpness X)
- Unobtainable Potions (eg. Insta-kill)
- Unobtainable Fireworks
- Crash Books
- Crash Signs
- Crash Chests/Shulker Boxes
- Crash Potions (invalid CustomPotionColor\s)
- Oversized packets (which crash the client)
- Long item names/item lore
- Additional "AttributeModifiers" on items (eg. Speed)
- Unbreakable items
- and more abusive NBT

## Supported Platforms

**PanillaX is provided as-is, with no guarantees. At the moment, it just adds compatibility for newer versions to old checks.**

Currently PanillaX supports:
- Paper 1.21.4 (2.1.0 recommended)
- Paper 1.21.5
- Paper 1.21.6
- Paper 1.21.7

**This fork does NOT support CraftBukkit/Spigot**. It supports Paper derivatives (Purpur, etc.) including Folia. 

## Commands
`panilla`: Information about your PanillaX instance (permission: panilla.command)\
`panilla debug`: Show debug information (permission: panilla.command.debug)

## Permissions
`panilla.log.chat`: View warning logs in chat if enabled in the config\
`panilla.bypass`: Bypass packet checks

## Compiling
This fork uses Paperweight, so you won't need to compile Spigot or Paper beforehand.
You can compile the project with the shadowJar task or by running `./gradlew build`. The output plugin jars file will located in the `target/` directory.

Java 21 is required to build PanillaX.