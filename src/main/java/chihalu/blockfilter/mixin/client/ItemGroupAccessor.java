package chihalu.blockfilter.mixin.client;

import java.util.Collection;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;

import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;

@Mixin(ItemGroup.class)
public interface ItemGroupAccessor {
	@Invoker("updateEntries")
	void blockfilter$invokeUpdateEntries(ItemGroup.DisplayContext displayContext);

	@Accessor("displayStacks")
	Collection<ItemStack> blockfilter$getDisplayStacks();
}
