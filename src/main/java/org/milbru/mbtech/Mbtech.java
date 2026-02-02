package org.milbru.mbtech;

import net.fabricmc.api.ModInitializer;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.screen.SimpleNamedScreenHandlerFactory;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Identifier;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.milbru.mbtech.gui.MBGuis; // We will create this class next

public class Mbtech implements ModInitializer {
    public static final String MOD_ID = "mbtech";

    public static final Block TECH_TABLE_BLOCK = new Block(AbstractBlock.Settings.create().strength(2.5f)) {
        @Override
        protected ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, BlockHitResult hit) {
            if (!world.isClient() && player instanceof ServerPlayerEntity sp) {
                // This lambda prevents the server from loading the GUI classes until right-click
                sp.openHandledScreen(new SimpleNamedScreenHandlerFactory(
                        (syncId, inventory, p) -> new MBGuis.MBMainGui(syncId, inventory),
                        Text.literal("Tech Table")
                ));
            }
            return ActionResult.SUCCESS;
        }
    };

    @Override
    public void onInitialize() {
        Registry.register(Registries.BLOCK, Identifier.of(MOD_ID, "tech_table"), TECH_TABLE_BLOCK);
        Registry.register(Registries.ITEM, Identifier.of(MOD_ID, "tech_table"), new BlockItem(TECH_TABLE_BLOCK, new Item.Settings()));
    }
}