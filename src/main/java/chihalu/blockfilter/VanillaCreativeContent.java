package chihalu.blockfilter;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import chihalu.blockfilter.mixin.client.ItemGroupAccessor;
import chihalu.blockfilter.mixin.client.ItemGroupsAccessor;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.resource.featuretoggle.FeatureSet;
import net.minecraft.util.Identifier;

/**
 * Captures vanilla creative tab contents so they can be reorganised by the mod.
 */
	final class VanillaCreativeContent {
		private static final String NAMESPACE_MINECRAFT = "minecraft";
		private static final Set<String> IGNORED_GROUP_PATHS = Set.of("hotbar", "search", "inventory", "op_blocks");
		private static final Set<String> EXCLUDED_GROUP_PATHS = Set.of(
				"combat",
				"spawn_eggs",
				"food_and_drinks",
				"ingredients");

	private VanillaCreativeContent() {
	}

	static Map<String, List<ItemStack>> capture(ItemGroup.DisplayContext context) {
		FeatureSet features = context.enabledFeatures();
		boolean hasPermissions = context.hasPermissions();
		RegistryWrapper.WrapperLookup lookup = context.lookup();

		// Ensure vanilla groups are up-to-date for the current feature flags.
		ItemGroupsAccessor.blockfilter$invokeUpdateDisplayContext(features, hasPermissions, lookup);

		Map<String, List<ItemStack>> captured = new LinkedHashMap<>();

			for (ItemGroup group : Registries.ITEM_GROUP) {
				Identifier id = Registries.ITEM_GROUP.getId(group);
				if (id == null || !NAMESPACE_MINECRAFT.equals(id.getNamespace())
						|| IGNORED_GROUP_PATHS.contains(id.getPath())
						|| EXCLUDED_GROUP_PATHS.contains(id.getPath())) {
					continue;
				}

			// Skip any custom group we may have registered in this mod namespace.
			if (BlockFilter.MOD_ID.equals(id.getNamespace())) {
				continue;
			}

			// Rebuild the group's entries so we always snapshot fresh stacks for this context.
			((ItemGroupAccessor) group).blockfilter$invokeUpdateEntries(context);

				List<ItemStack> stacks = new ArrayList<>();
				for (ItemStack stack : ((ItemGroupAccessor) group).blockfilter$getDisplayStacks()) {
					if (!stack.getItem().isEnabled(features) || BlockFilterCreativeLayout.shouldOmit(stack)) {
						continue;
					}
					stacks.add(stack.copy());
				}

			captured.put(id.toString(), stacks);
		}

		return captured;
	}
}

