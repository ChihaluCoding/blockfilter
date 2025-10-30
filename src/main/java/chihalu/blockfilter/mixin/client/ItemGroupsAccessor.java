package chihalu.blockfilter.mixin.client;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

import net.minecraft.item.ItemGroups;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.resource.featuretoggle.FeatureSet;

@Mixin(ItemGroups.class)
public interface ItemGroupsAccessor {
	@Invoker("updateDisplayContext")
	static boolean blockfilter$invokeUpdateDisplayContext(FeatureSet features, boolean operatorEnabled,
			RegistryWrapper.WrapperLookup lookup) {
		throw new AssertionError("Mixin invoker not applied");
	}
}
