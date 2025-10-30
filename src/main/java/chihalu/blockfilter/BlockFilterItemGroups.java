package chihalu.blockfilter;

import java.util.List;
import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemGroups;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

/**
 * Registers creative tabs that are managed by this mod.
 */
public final class BlockFilterItemGroups {
	private static final List<ItemGroup> FILTER_GROUPS_VIEW = List.of();

	public static final RegistryKey<ItemGroup> RECENT_BLOCKS_KEY = RegistryKey.of(
		RegistryKeys.ITEM_GROUP,
		Identifier.of(BlockFilter.MOD_ID, "recent_blocks")
	);
	public static final RegistryKey<ItemGroup> BIOMES_KEY = RegistryKey.of(
		RegistryKeys.ITEM_GROUP,
		Identifier.of(BlockFilter.MOD_ID, "biomes")
	);

	private static ItemGroup recentBlocksGroup;
	private static ItemGroup biomesGroup;
	private static List<ItemGroup> cachedFilterGroups = FILTER_GROUPS_VIEW;

	private BlockFilterItemGroups() {
	}

	public static void bootstrap() {
		if (recentBlocksGroup != null && biomesGroup != null) {
			return;
		}

		recentBlocksGroup = Registry.register(
			Registries.ITEM_GROUP,
			RECENT_BLOCKS_KEY,
			FabricItemGroup.builder()
				.displayName(Text.translatable("itemGroup.blockfilter.recent_blocks"))
				.icon(BlockFilterRecents::getIconStack)
				.entries((context, entries) -> BlockFilterRecents.append(entries))
				.build()
		);

		biomesGroup = Registry.register(
			Registries.ITEM_GROUP,
			BIOMES_KEY,
			FabricItemGroup.builder()
				.displayName(Text.translatable("itemGroup.blockfilter.biomes"))
				.icon(BlockFilterBiomeLibrary::getIconStack)
				.entries((context, entries) -> BlockFilterBiomeLibrary.append(entries))
				.build()
		);

		cachedFilterGroups = List.of(recentBlocksGroup, biomesGroup);
	}

	public static List<ItemGroup> getFilterGroups() {
		return cachedFilterGroups;
	}

	public static boolean isFilterGroup(ItemGroup group) {
		return cachedFilterGroups.contains(group);
	}

	public static int indexOf(ItemGroup group) {
		return cachedFilterGroups.indexOf(group);
	}

	public static ItemGroup getRecentBlocksGroup() {
		return recentBlocksGroup;
	}

	public static ItemGroup getBiomesGroup() {
		return biomesGroup;
	}

	public static ItemGroup getDefaultVanillaTab() {
		return ItemGroups.getDefaultTab();
	}
}
