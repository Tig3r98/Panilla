# Panilla
Panilla (the name) is a combination of the word Packet and Vanilla (as in Vanilla Minecraft).

This is a fork of the original [original project](https://www.spigotmc.org/resources/65694/) that only supports recent Paper servers and is easier to compile.

## Overview
Panilla is software to prevent abusive NBT and packets on Minecraft servers.

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
Currently Panilla supports:
- Paper 1.20.6
- Paper 1.21-1.21.1
- Paper 1.21-3 (untested)

**This fork does NOT support Spigot**. It supports Paper derivatives (Purpur, etc.) including Folia. 

## Compiling
This fork uses Paperweight, so you won't need to compile Spigot or Paper beforehand.
You can compile the project with the shadowJar task or by running `./gradlew build`. The output plugin jars file will located in the `target/` directory.

Java 21 is required to build Panilla.