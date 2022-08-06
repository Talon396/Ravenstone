package sh.talonfox.ravenstone.network;

import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import sh.talonfox.ravenstone.blocks.ComputerBlockEntity;

public class ComputerPackets {
    public static final Identifier COMPUTER_C2S_SYNC_ID = new Identifier("ravenstone", "computer_c2s_sync");
    public static final Identifier COMPUTER_STEP_ID = new Identifier("ravenstone", "computer_step");
    public static void ComputerC2SSyncReceiver(MinecraftServer server, ServerPlayerEntity player, ServerPlayNetworkHandler handler, PacketByteBuf buf, PacketSender responseSender) {
        BlockPos target = buf.readBlockPos();
        NbtCompound new_data = buf.readNbt();
        server.execute(() -> {
            ComputerBlockEntity blockEntity = (ComputerBlockEntity)(player.getWorld().getBlockEntity(target));
            assert new_data != null;
            assert blockEntity != null;
            blockEntity.readNbt(new_data);
            blockEntity.markDirty();
        });
    }
    public static void ComputerStepReceiver(MinecraftServer server, ServerPlayerEntity player, ServerPlayNetworkHandler handler, PacketByteBuf buf, PacketSender responseSender) {
        BlockPos target = buf.readBlockPos();
        server.execute(() -> {
            ComputerBlockEntity blockEntity = (ComputerBlockEntity)(player.getWorld().getBlockEntity(target));
            assert blockEntity != null;
            blockEntity.CPU.next();
            blockEntity.readNbt(blockEntity.createNbt());
            blockEntity.markDirty();
        });
    }
}