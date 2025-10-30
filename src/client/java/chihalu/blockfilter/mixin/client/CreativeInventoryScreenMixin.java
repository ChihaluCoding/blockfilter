package chihalu.blockfilter.mixin.client;

import chihalu.blockfilter.BlockFilterItemGroups;
import chihalu.blockfilter.BlockFilterRecents;
import java.util.List;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.ingame.CreativeInventoryScreen;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.slot.Slot;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(CreativeInventoryScreen.class)
public abstract class CreativeInventoryScreenMixin extends HandledScreen<CreativeInventoryScreen.CreativeScreenHandler> {
	@Shadow
	protected abstract void setSelectedTab(ItemGroup group);

	@Shadow
	private static SimpleInventory INVENTORY;

	@Unique
	private ButtonWidget blockfilter$filterButton;

	@Unique
	private ButtonWidget blockfilter$prevPageButton;

	@Unique
	private ButtonWidget blockfilter$nextPageButton;

	@Unique
	private boolean blockfilter$filterActive;

	@Unique
	private int blockfilter$currentFilterIndex;

	protected CreativeInventoryScreenMixin(CreativeInventoryScreen.CreativeScreenHandler handler, PlayerInventory inventory, Text title) {
		super(handler, inventory, title);
	}

	@Inject(method = "init", at = @At("TAIL"))
	private void blockfilter$init(CallbackInfo ci) {
		blockfilter$createFilterControls();
	}

	@Inject(method = "render", at = @At("TAIL"))
	private void blockfilter$render(DrawContext context, int mouseX, int mouseY, float delta, CallbackInfo ci) {
		blockfilter$drawPageIndicator(context);
	}

	@Inject(method = "setSelectedTab", at = @At("TAIL"))
	private void blockfilter$afterSetSelectedTab(ItemGroup group, CallbackInfo ci) {
		if (BlockFilterItemGroups.isFilterGroup(group)) {
			blockfilter$filterActive = true;
			int index = BlockFilterItemGroups.indexOf(group);
			blockfilter$currentFilterIndex = Math.max(0, index);
		} else {
			blockfilter$filterActive = false;
			blockfilter$currentFilterIndex = 0;
		}

		blockfilter$updateButtonState();
	}

	@Inject(method = "onMouseClick", at = @At("TAIL"))
	private void blockfilter$recordRecents(Slot slot, int slotId, int button, SlotActionType actionType, CallbackInfo ci) {
		if (slot == null || slot.getStack().isEmpty()) {
			return;
		}

		if (slot.inventory != INVENTORY) {
			return;
		}

		ItemStack stack = slot.getStack();
		BlockFilterRecents.record(stack);
	}

	private void blockfilter$createFilterControls() {
		List<ItemGroup> groups = BlockFilterItemGroups.getFilterGroups();
		if (groups.isEmpty()) {
			return;
		}

		int centerX = this.x + this.backgroundWidth / 2;
		int navigationY = this.y - 26;

		blockfilter$prevPageButton = ButtonWidget.builder(Text.literal("<"), button -> blockfilter$navigate(false))
			.dimensions(centerX - 62, navigationY, 20, 20)
			.build();

		blockfilter$nextPageButton = ButtonWidget.builder(Text.literal(">"), button -> blockfilter$navigate(true))
			.dimensions(centerX + 42, navigationY, 20, 20)
			.build();

		blockfilter$filterButton = ButtonWidget.builder(Text.translatable("gui.blockfilter.creative.filter_off"), button -> blockfilter$toggleFilter())
			.dimensions(centerX - 36, navigationY - 22, 72, 20)
			.build();

		this.addDrawableChild(blockfilter$filterButton);
		this.addDrawableChild(blockfilter$prevPageButton);
		this.addDrawableChild(blockfilter$nextPageButton);
		blockfilter$updateButtonState();
	}

	private void blockfilter$toggleFilter() {
		if (BlockFilterItemGroups.getFilterGroups().isEmpty()) {
			return;
		}

		if (blockfilter$filterActive) {
			this.setSelectedTab(BlockFilterItemGroups.getDefaultVanillaTab());
		} else {
			blockfilter$openFilterTab(0);
		}
	}

	private void blockfilter$navigate(boolean forward) {
		List<ItemGroup> groups = BlockFilterItemGroups.getFilterGroups();
		if (groups.isEmpty()) {
			return;
		}

		if (forward) {
			if (!blockfilter$filterActive) {
				blockfilter$openFilterTab(0);
				return;
			}

			int nextIndex = (blockfilter$currentFilterIndex + 1) % groups.size();
			blockfilter$openFilterTab(nextIndex);
		} else {
			if (!blockfilter$filterActive) {
				return;
			}

			if (blockfilter$currentFilterIndex == 0) {
				this.setSelectedTab(BlockFilterItemGroups.getDefaultVanillaTab());
			} else {
				blockfilter$openFilterTab(blockfilter$currentFilterIndex - 1);
			}
		}
	}

	private void blockfilter$openFilterTab(int index) {
		List<ItemGroup> groups = BlockFilterItemGroups.getFilterGroups();
		if (groups.isEmpty()) {
			return;
		}

		int clampedIndex = Math.floorMod(index, groups.size());
		this.setSelectedTab(groups.get(clampedIndex));
	}

	private void blockfilter$drawPageIndicator(DrawContext context) {
		if (BlockFilterItemGroups.getFilterGroups().isEmpty()) {
			return;
		}

		int totalPages = 2;
		int currentPage = blockfilter$filterActive ? 2 : 1;
		Text text = Text.literal(currentPage + " / " + totalPages);
		int textWidth = this.textRenderer.getWidth(text);
		int xPos = this.x + this.backgroundWidth / 2 - textWidth / 2;
		int yPos = this.y - 20;
		context.drawTextWithShadow(this.textRenderer, text, xPos, yPos, 0xFFFFFF);
	}

	private void blockfilter$updateButtonState() {
		if (blockfilter$filterButton == null || blockfilter$prevPageButton == null || blockfilter$nextPageButton == null) {
			return;
		}

		boolean hasFilterTabs = !BlockFilterItemGroups.getFilterGroups().isEmpty();
		blockfilter$filterButton.visible = hasFilterTabs;
		blockfilter$prevPageButton.visible = hasFilterTabs;
		blockfilter$nextPageButton.visible = hasFilterTabs;

		if (!hasFilterTabs) {
			return;
		}

		blockfilter$filterButton.setMessage(
			Text.translatable(blockfilter$filterActive ? "gui.blockfilter.creative.filter_on" : "gui.blockfilter.creative.filter_off")
		);

		blockfilter$prevPageButton.active = blockfilter$filterActive;
		blockfilter$nextPageButton.active = true;
	}
}
