package sh.talonfox.ravenstone.blocks;

import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.Packet;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.explosion.Explosion;
import org.jetbrains.annotations.Nullable;
import sh.talonfox.ravenstone.processor.Processor;
import sh.talonfox.ravenstone.processor.ProcessorHost;

import java.util.Objects;

import static sh.talonfox.ravenstone.blocks.ComputerBlock.RUNNING;

/*

*/

public class ComputerBlockEntity extends BlockEntity implements ProcessorHost {
    public Processor CPU = new Processor(this);
    public byte[] RAM = new byte[16384];

    public ComputerBlockEntity(BlockPos pos, BlockState state) {
        super(BlockRegister.RAVEN_COMPUTER_ENTITY, pos, state);
    }

    @Override
    public void resetBusState() {

    }

    @Override
    public byte memRead(short at) {
        if(Short.toUnsignedInt(at) < RAM.length) {
            return RAM[Short.toUnsignedInt(at)];
        } else {
            CPU.Error = true;
            return (byte)0xFF;
        }
    }

    @Override
    public void memStore(short at, byte data) {
        if(Short.toUnsignedInt(at) < RAM.length) {
            RAM[Short.toUnsignedInt(at)] = data;
        }
    }
    public void explode() {
        BlockPos pos = this.getPos();
        Objects.requireNonNull(this.getWorld()).createExplosion(null, DamageSource.GENERIC.setExplosive(), null,(double)pos.getX()+0.5,(double)pos.getY()+0.5,(double)pos.getZ()+0.5,2F,false, Explosion.DestructionType.NONE);
    }
    public static void tick(World world, BlockPos pos, BlockState state, ComputerBlockEntity blockEntity) {
        if(!blockEntity.CPU.Stop) {
            for(int i=0; i < (100000/20); i++) {
                blockEntity.CPU.next();
                if(blockEntity.CPU.Stop) {
                    world.setBlockState(pos, state.with(RUNNING, !state.get(RUNNING)));
                    blockEntity.markDirty();
                    break;
                }
            }
        }
    }

    @Override
    public void writeNbt(NbtCompound tag) {
        var processor = new NbtCompound();
        processor.putByte("A",CPU.A);
        processor.putByte("X",CPU.X);
        processor.putByte("Y",CPU.Y);
        processor.putShort("PC",CPU.PC);
        processor.putShort("SP",CPU.SP);
        processor.putShort("BrkAddr",CPU.BrkAddr);
        processor.putShort("ResetAddr",CPU.ResetAddr);
        processor.putInt("BusOffset",CPU.BusOffset);
        processor.putBoolean("Error",CPU.Error);
        processor.putBoolean("Stop",CPU.Stop);
        processor.putBoolean("Wait",CPU.Wait);
        processor.putBoolean("BusEnabled",CPU.BusEnabled);
        processor.putBoolean("FlagC",CPU.FlagC);
        processor.putBoolean("FlagZ",CPU.FlagZ);
        processor.putBoolean("FlagI",CPU.FlagI);
        processor.putBoolean("FlagD",CPU.FlagD);
        processor.putBoolean("FlagV",CPU.FlagV);
        processor.putBoolean("FlagN",CPU.FlagN);

        tag.put("Processor",processor);
        tag.putByteArray("RAM", RAM);

        super.writeNbt(tag);
    }

    @Override
    public void readNbt(NbtCompound tag) {
        var processor = tag.getCompound("Processor");
        CPU.A = processor.getByte("A");
        CPU.X = processor.getByte("X");
        CPU.Y = processor.getByte("Y");
        CPU.PC = processor.getShort("PC");
        CPU.SP = processor.getShort("SP");
        CPU.BrkAddr = processor.getShort("BrkAddr");
        CPU.ResetAddr = processor.getShort("ResetAddr");
        CPU.BusOffset = processor.getInt("BusOffset");
        CPU.Error = processor.getBoolean("Error");
        CPU.Stop = processor.getBoolean("Stop");
        CPU.Wait = processor.getBoolean("Wait");
        CPU.BusEnabled = processor.getBoolean("BusEnabled");
        CPU.FlagC = processor.getBoolean("FlagC");
        CPU.FlagZ = processor.getBoolean("FlagZ");
        CPU.FlagI = processor.getBoolean("FlagI");
        CPU.FlagD = processor.getBoolean("FlagD");
        CPU.FlagV = processor.getBoolean("FlagV");
        CPU.FlagN = processor.getBoolean("FlagN");
        if(tag.getByteArray("RAM").length == RAM.length) {
            RAM = tag.getByteArray("RAM");
        }
    }

    @Nullable
    @Override
    public Packet<ClientPlayPacketListener> toUpdatePacket() {
        return BlockEntityUpdateS2CPacket.create(this);
    }
    @Override
    public NbtCompound toInitialChunkDataNbt() {
        return createNbt();
    }
}