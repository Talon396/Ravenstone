package sh.talonfox.ravenstone.blocks.upgrades;

import net.minecraft.block.BlockState;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.math.BlockPos;

import static sh.talonfox.ravenstone.blocks.BlockRegister.RAVEN_32K_UPGRADE_ENTITY;

public class RAM32KBlockEntity extends RAMUpgradeBlockEntity {
    public byte[] RAM = new byte[(32768-8192)];
    public RAM32KBlockEntity(BlockPos pos, BlockState state) {
        super(RAVEN_32K_UPGRADE_ENTITY, pos, state);
    }
    @Override
    public byte readData(short at) {
        if(Short.toUnsignedInt(at) < RAM.length) {
            return RAM[Short.toUnsignedInt(at)];
        }
        return (byte)0xFF;
    }
    @Override
    public void storeData(short at, byte data) {
        if(Short.toUnsignedInt(at) < RAM.length) {
            RAM[Short.toUnsignedInt(at)] = data;
        }
    }
    @Override
    public void readNbt(NbtCompound tag) {
        super.readNbt(tag);
        RAM = tag.getByteArray("RAM");
    }
    @Override
    public void writeNbt(NbtCompound tag) {
        super.writeNbt(tag);
        tag.putByteArray("RAM", RAM);
    }
}