package sh.talonfox.ravenstone.blocks.peripherals;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityTicker;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import sh.talonfox.ravenstone.blocks.BlockRegister;

import java.util.Objects;

public class FloppyDriveBlock extends PeripheralBlock {
    public static final BooleanProperty HAS_DISK = BooleanProperty.of("has_disk");
    public static final BooleanProperty LIGHT = BooleanProperty.of("light");
    public FloppyDriveBlock(Settings settings) {
        super(settings);
        setDefaultState(this.stateManager.getDefaultState().with(HAS_DISK,false).with(LIGHT,false));
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> stateManager) {
        super.appendProperties(stateManager);
        stateManager.add(HAS_DISK);
        stateManager.add(LIGHT);
    }

    @Nullable
    @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new FloppyDriveBlockEntity(pos, state);
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(World world, BlockState state, BlockEntityType<T> type) {
        return world.isClient() ? null : checkType(type, BlockRegister.RAVEN_FLOPPY_DRIVE_ENTITY, FloppyDriveBlockEntity::tick);
    }

    @Override
    public void onStateReplaced(BlockState state, World world, BlockPos pos, BlockState newState, boolean a) {
        if (state.getBlock() != newState.getBlock()) {
            if(world != null) {
                ((FloppyDriveBlockEntity)Objects.requireNonNull(world.getBlockEntity(pos))).ejectDisk(true);
            }
        }
        super.onStateReplaced(state, world, pos, newState, a);
    }

    @Override
    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
        var result = super.onUse(state,world,pos,player,hand,hit);
        if(result != ActionResult.PASS)
            return result;
        if(world == null)
            return ActionResult.FAIL;
        var blockEntity = world.getBlockEntity(pos);
        if(!(blockEntity instanceof FloppyDriveBlockEntity))
            return ActionResult.FAIL;
        var ent = (FloppyDriveBlockEntity)blockEntity;
        if(state.get(HAS_DISK)) {
            return ent.ejectDisk(false)?ActionResult.SUCCESS:ActionResult.PASS;
        } else {
            return ent.insertDisk(player.getStackInHand(hand))?ActionResult.SUCCESS:ActionResult.PASS;
        }
    }
}
