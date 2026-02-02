package org.milbru.mbtech.gui;

import net.minecraft.component.DataComponentTypes;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.screen.GenericContainerScreenHandler;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.screen.SimpleNamedScreenHandlerFactory;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MBGuis {

    public enum ItemType { RECIPE, UI_LINK, STANDARD }

    public static class GetItem {
        private final ItemStack item;
        private final ItemType type;

        public GetItem(ItemStack item, String name, List<Formatting> formats, ItemType type) {
            this.item = item;
            this.type = type;
            if (name != null) {
                MutableText text = Text.literal(name);
                if (formats != null) for (Formatting f : formats) text.formatted(f);
                this.item.set(DataComponentTypes.CUSTOM_NAME, text);
            }
        }
        public ItemStack getItem() { return item; }
        public ItemType getType() { return type; }
    }

    public static abstract class MBBaseGui extends GenericContainerScreenHandler {
        protected final Inventory inventory;
        protected final Map<Integer, GetItem> wrappedItems = new HashMap<>();

        public MBBaseGui(int syncId, PlayerInventory playerInventory) {
            super(ScreenHandlerType.GENERIC_9X6, syncId, playerInventory, new SimpleInventory(54), 6);
            this.inventory = getInventory();
        }

        public void addGuiItem(int slot, GetItem wrapper) {
            this.wrappedItems.put(slot, wrapper);
            this.inventory.setStack(slot, wrapper.getItem());
        }

        @Override
        public void onSlotClick(int slotIndex, int button, SlotActionType actionType, PlayerEntity player) {
            if (slotIndex >= 0 && slotIndex < 54) {
                GetItem wrapper = wrappedItems.get(slotIndex);
                if (wrapper != null && player instanceof ServerPlayerEntity sp) {
                    if (wrapper.type == ItemType.UI_LINK) onLinkClick(slotIndex, sp);
                    else if (wrapper.type == ItemType.RECIPE) onRecipeClick(slotIndex, sp);
                }
                return;
            }
            super.onSlotClick(slotIndex, button, actionType, player);
        }

        public abstract void onLinkClick(int slot, ServerPlayerEntity player);
        public abstract void onRecipeClick(int slot, ServerPlayerEntity player);
        @Override
        public boolean canUse(PlayerEntity player) { return true; }
    }

    public static class MBMainGui extends MBBaseGui {
        public MBMainGui(int syncId, PlayerInventory inv) {
            super(syncId, inv);
            setupItems();
        }
        public void setupItems() {
            ItemStack glass = new ItemStack(Items.GRAY_STAINED_GLASS_PANE);
            for (int i = 0; i < 54; i++) addGuiItem(i, new GetItem(glass, "", null, ItemType.STANDARD));
            addGuiItem(10, new GetItem(new ItemStack(Items.CRAFTING_TABLE), "Basic Machines", List.of(Formatting.YELLOW), ItemType.UI_LINK));
        }
        @Override
        public void onLinkClick(int slot, ServerPlayerEntity player) {
            if (slot == 10) player.openHandledScreen(new SimpleNamedScreenHandlerFactory((id, inv, p) -> new MBBasicMachinesGUI(id, inv), Text.literal("Basic Machines")));
        }
        @Override
        public void onRecipeClick(int slot, ServerPlayerEntity player) {}
    }

    public static class MBBasicMachinesGUI extends MBBaseGui {
        public MBBasicMachinesGUI(int syncId, PlayerInventory inv) { super(syncId, inv); setupItems(); }
        public void setupItems() { addGuiItem(9, new GetItem(new ItemStack(Items.CRAFTING_TABLE), "Tech Table", List.of(Formatting.YELLOW), ItemType.RECIPE)); }
        @Override public void onLinkClick(int slot, ServerPlayerEntity player) {}
        @Override public void onRecipeClick(int slot, ServerPlayerEntity player) {
            // Logic to open Recipe GUI here...
        }
    }
}