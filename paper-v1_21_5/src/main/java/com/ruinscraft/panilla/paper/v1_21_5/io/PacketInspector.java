package com.ruinscraft.panilla.paper.v1_21_5.io;

import com.ruinscraft.panilla.api.IPanilla;
import com.ruinscraft.panilla.api.IPanillaPlayer;
import com.ruinscraft.panilla.api.exception.EntityNbtNotPermittedException;
import com.ruinscraft.panilla.api.exception.FailedNbt;
import com.ruinscraft.panilla.api.exception.FailedNbtList;
import com.ruinscraft.panilla.api.exception.NbtNotPermittedException;
import com.ruinscraft.panilla.api.io.IPacketInspector;
import com.ruinscraft.panilla.api.nbt.INbtTagCompound;
import com.ruinscraft.panilla.api.nbt.checks.NbtChecks;
import com.ruinscraft.panilla.paper.v1_21_5.nbt.NbtTagCompound;
import de.tr7zw.changeme.nbtapi.NBT;
import net.minecraft.core.component.TypedDataComponent;
import net.minecraft.network.protocol.game.*;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import org.bukkit.craftbukkit.entity.CraftPlayer;
import org.bukkit.craftbukkit.util.CraftChatMessage;

import java.lang.reflect.Field;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;

public class PacketInspector implements IPacketInspector {

    private final IPanilla panilla;

    public PacketInspector(IPanilla panilla) {
        this.panilla = panilla;
    }

    @Override
    public void checkPacketPlayInClickContainer(Object packetHandle) throws NbtNotPermittedException {
        // Since 1.21.5, ServerboundContainerClickPacket doesn't handle an ItemStack but an HashedItem.
        // The player can't inject stuff here, nor can we read it at this stage.
        return;
    }

    @Override
    public void checkPacketPlayInSetCreativeSlot(Object packetHandle) throws NbtNotPermittedException {
        if (!(packetHandle instanceof ServerboundSetCreativeModeSlotPacket)) return;
        ServerboundSetCreativeModeSlotPacket packet = (ServerboundSetCreativeModeSlotPacket) packetHandle;

        int slot = packet.slotNum();
        ItemStack item = packet.itemStack();
        if (item == null || item.isEmpty() || item.getComponents().isEmpty()) return;

        NbtTagCompound tag = new NbtTagCompound(NBT.itemStackToNBT(item.getBukkitStack()).getCompound("components"));
        String itemClass = item.getItem().getDescriptionId();
        String packetClass = "PacketPlayInSetCreativeSlot";

        NbtChecks.checkPacketPlayIn(slot, tag, itemClass, packetClass, panilla);
    }

    @Override
    public void checkPacketPlayOutSetSlot(Object packetHandle) throws NbtNotPermittedException {
        if (!(packetHandle instanceof ClientboundContainerSetSlotPacket)) return;
        ClientboundContainerSetSlotPacket packet = (ClientboundContainerSetSlotPacket) packetHandle;

        int windowId = packet.getContainerId();

        // check if window is not player inventory and we are ignoring non-player inventories
        if (windowId != 0 && panilla.getPConfig().ignoreNonPlayerInventories) {
            return;
        }

        int slot = packet.getSlot();

        ItemStack item = packet.getItem();

        if (item == null || item.isEmpty() || item.getComponents().isEmpty()) {
            return;
        }

        NbtTagCompound tag = new NbtTagCompound(NBT.itemStackToNBT(item.getBukkitStack()).getCompound("components"));
        String itemClass = item.getClass().getSimpleName();
        String packetClass = packet.getClass().getSimpleName();

        NbtChecks.checkPacketPlayOut(slot, tag, itemClass, packetClass, panilla);
    }

    @Override
    public void checkPacketPlayOutWindowItems(Object packetHandle) throws NbtNotPermittedException {
        if (!(packetHandle instanceof ClientboundContainerSetContentPacket)) return;
        ClientboundContainerSetContentPacket packet = (ClientboundContainerSetContentPacket) packetHandle;

        int windowId = packet.containerId();

        // check if window is not player inventory
        if (windowId != 0) {
            return;
        }

        List<ItemStack> itemStacks = packet.items();

        for (ItemStack itemStack : itemStacks) {
            if (!itemStack.isEmpty() || itemStack.getComponents().isEmpty()) {
                continue;
            }

            NbtTagCompound tag = new NbtTagCompound(NBT.itemStackToNBT(itemStack.asBukkitCopy()).getCompound("components"));
            String itemClass = itemStack.getClass().getSimpleName();
            String packetClass = packet.getClass().getSimpleName();

            NbtChecks.checkPacketPlayOut(0, tag, itemClass, packetClass, panilla); // TODO: set slot?
        }
    }

    @Override
    public void checkPacketPlayOutSpawnEntity(Object packetHandle) throws EntityNbtNotPermittedException {
        if ((!(packetHandle instanceof ClientboundAddEntityPacket))) return;

        ClientboundAddEntityPacket packet = (ClientboundAddEntityPacket) packetHandle;

        UUID entityId = packet.getUUID();
        Entity entity = null;

        for (ServerLevel worldServer : MinecraftServer.getServer().getAllLevels()) {
            entity = worldServer.moonrise$getEntityLookup().get(entityId);
            if (entity != null) break;
        }

        if (!(entity instanceof ItemEntity)) return;

        ItemEntity item = (ItemEntity) entity;
        ItemStack itemStack = item.getItem();

        if (itemStack == null) {
            return;
        }

        if (itemStack.isEmpty() || itemStack.getComponents().isEmpty()) {
            return;
        }

        INbtTagCompound tag = new NbtTagCompound(NBT.itemStackToNBT(itemStack.getBukkitStack()).getCompound("components"));
        String itemName = itemStack.getItem().getDescriptionId();
        String worldName = "";

        try {
            Field worldField = Entity.class.getDeclaredField("level");
            worldField.setAccessible(true);
            Level world = (Level) worldField.get(entity);
            worldName = world.getWorld().getName();
        } catch (NoSuchFieldException | IllegalAccessException e) {
            e.printStackTrace();
        }

        FailedNbtList failedNbtList = NbtChecks.checkAll(tag, itemName, panilla);

        if (failedNbtList.containsCritical()) {
            throw new EntityNbtNotPermittedException(packet.getClass().getSimpleName(), false, failedNbtList.getCritical(), entityId, worldName);
        }

        FailedNbt failedNbt = failedNbtList.findFirstNonCritical();

        if (failedNbt != null) {
            throw new EntityNbtNotPermittedException(packet.getClass().getSimpleName(), false, failedNbt, entityId, worldName);
        }
    }

    @Override
    public void sendPacketPlayOutSetSlotAir(IPanillaPlayer player, int slot) {
        CraftPlayer craftPlayer = (CraftPlayer) player.getHandle();
        ServerPlayer entityPlayer = craftPlayer.getHandle();
        ClientboundContainerSetSlotPacket packet = new ClientboundContainerSetSlotPacket(entityPlayer.containerMenu.containerId, entityPlayer.containerMenu.incrementStateId(), slot, new ItemStack(Blocks.AIR));
        entityPlayer.connection.send(packet);
    }

    @Override
    public void stripNbtFromItemEntity(UUID entityId) {
        Entity entity = null;

        for (ServerLevel worldServer : MinecraftServer.getServer().getAllLevels()) {
            entity = worldServer.moonrise$getEntityLookup().get(entityId);
            if (entity != null) break;
        }

        if (entity instanceof ItemEntity) {
            ItemEntity item = (ItemEntity) entity;
            ItemStack itemStack = item.getItem();
            if (itemStack == null || itemStack.isEmpty() || itemStack.getComponents().isEmpty()) return;
            Iterator<TypedDataComponent<?>> iter = itemStack.getComponents().iterator();
            while (iter.hasNext()) iter.remove();
        }
    }

    @Override
    public void stripNbtFromItemEntityLegacy(int entityId) {
        throw new RuntimeException("cannot use #stripNbtFromItemEntityLegacy on 1.20.6");
    }

    @Override
    public void validateBaseComponentParse(String string) throws Exception {
        CraftChatMessage.fromJSON(string);
    }

}
