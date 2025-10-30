package chihalu.blockfilter;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.text.Text;

/**
 * Registers the custom creative layout defined by {@link BlockFilterCreativeLayout}.
 */
public final class BlockFilterItemGroups {
	private static boolean registered = false;
	private static final List<ItemGroup> GROUPS = new ArrayList<>();

	private BlockFilterItemGroups() {
	}

	public static void register() {
		if (registered) {
			return;
		}

		for (BlockFilterCreativeLayout.CategoryDefinition definition : BlockFilterCreativeLayout.definitions()) {
			ItemGroup group = Registry.register(
					Registries.ITEM_GROUP,
					definition.identifier(),
					FabricItemGroup.builder()
							.icon(definition::icon)
							.displayName(Text.translatable(definition.translationKey()))
							.entries((context, entries) -> {
								Set<Item> seen = new HashSet<>();
								for (ItemStack original : BlockFilterCreativeLayout.stacksFor(definition, context)) {
									if (seen.add(original.getItem())) {
										entries.add(original.copy(), ItemGroup.StackVisibility.PARENT_AND_SEARCH_TABS);
									}
								}
							})
							.build());

			GROUPS.add(group);
		}

		registered = true;
	}

	public static List<ItemGroup> getGroups() {
		return List.copyOf(GROUPS);
	}
}
