package net.brybry.block;

import kotlin.reflect.jvm.internal.impl.descriptors.Visibilities;
import net.brybry.forces.ForceApplier;
import net.brybry.forces.ForceInducer;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.phys.BlockHitResult;
import org.jetbrains.annotations.Nullable;
import org.valkyrienskies.core.api.ships.Ship;
import org.valkyrienskies.mod.common.VSGameUtilsKt;
import org.valkyrienskies.mod.common.ValkyrienSkiesMod;

import javax.annotation.Nonnull;

import static org.spongepowered.asm.util.Annotations.setValue;


public class EvilGooberBlock extends Block {

    public static final BooleanProperty POWERED = BlockStateProperties.POWERED;

    public EvilGooberBlock(Properties properties) {
        super(properties);
        this.registerDefaultState(this.stateDefinition.any()
                .setValue(POWERED, Boolean.FALSE)
        );

    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(POWERED);
    }


    @Override
    public @Nullable BlockState getStateForPlacement(BlockPlaceContext context) {
        boolean powered = context.getLevel().hasNeighborSignal(context.getClickedPos());

        if (!context.getLevel().isClientSide) {
            ServerLevel serverLevel = (ServerLevel) context.getLevel();
            var shipObjectWorld = VSGameUtilsKt.getShipObjectWorld(serverLevel);

            boolean isOnShip = shipObjectWorld.isBlockInShipyard(
                    context.getClickedPos().getX(),
                    context.getClickedPos().getY(),
                    context.getClickedPos().getZ(),
                    serverLevel.dimension().location().toString()
            );

            if (isOnShip) {

                for (var ship : shipObjectWorld.getQueryableShipData()) {
                    if (ship.getChunkClaim().contains(context.getClickedPos().getX() >> 4, context.getClickedPos().getZ() >> 4)) {

                        break;
                    }
                }
            }

            return super.getStateForPlacement(context);
        }
        return this.defaultBlockState()
                .setValue(POWERED, powered);
    }
        @Override
        public void neighborChanged(BlockState state, Level neighborChangedLevel, BlockPos pos, Block block, BlockPos fromPos, boolean isMoving) {
            if (!neighborChangedLevel.isClientSide) {
                boolean wasPowered = isPowered(state);
                boolean isPowered = neighborChangedLevel.hasNeighborSignal(pos);
                System.out.println("Magnet at " + pos + " neighbor changed. Was powered: " + wasPowered + ", Is powered: " + isPowered);

                if (isPowered != wasPowered) {
                    BlockState newState = state.setValue(POWERED, isPowered);
                    neighborChangedLevel.setBlock(pos, newState, 3);

                    if (isPowered) {
                        System.out.println("Magnet at " + pos + " just got powered - activating");
                        neighborChangedLevel.scheduleTick(pos, this, 1);
                    }
                }
            }
            super.neighborChanged(state, neighborChangedLevel, pos, block, fromPos, isMoving);
        }



    @Override
    public void onPlace(BlockState state, Level onPlaceLevel, BlockPos pos, BlockState oldState, boolean isMoving) {
        super.onPlace(state, onPlaceLevel, pos, oldState, isMoving);
        if (!onPlaceLevel.isClientSide) {

           if (isPowered(state)) {
               System.out.println("powering an Evil Goober Block at:" + pos + " (onPlace function)");
               onPlaceLevel.scheduleTick(pos, this, 2);
                ForceApplier.onEvilGooberBlockPowered((ServerLevel) onPlaceLevel, pos );
            }
        }
    }




    @Override
    public void tick(BlockState pState, ServerLevel tickLevel, BlockPos pos, RandomSource pRandom) {
        if(isPowered(pState)){
            System.out.println("powering an Evil Goober Block at:" + pos + " (Tick function)");

            var shipObjectWorld = VSGameUtilsKt.getShipObjectWorld(tickLevel);
            boolean isOnShip = shipObjectWorld.isBlockInShipyard(
                    pos.getX(), pos.getY(), pos.getZ(),
                    tickLevel.dimension().location().toString()
            );

            if (isOnShip) {
                for (var ship : shipObjectWorld.getQueryableShipData()) {
                    if (ship.getChunkClaim().contains(pos.getX() >> 4, pos.getZ() >> 4)) {
            }break;
                }
            }

            ForceApplier.onEvilGooberBlockPowered(tickLevel, pos);

        }


        super.tick(pState, tickLevel, pos, pRandom);
    }

    @Override
    public void onRemove(BlockState state, Level onRemoveLevel, BlockPos pos, BlockState newState, boolean isMoving) {
        if (!onRemoveLevel.isClientSide && !newState.is(this)) {

        }
        super.onRemove(state, onRemoveLevel, pos, newState, isMoving);
    }


    public static boolean isPowered(BlockState state) {
        return state.getValue(POWERED);
    }


}

