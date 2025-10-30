package chihalu.blockfilter;

import java.util.List;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;

/**
 * Provides representative block selections for biome-based filtering.
 */
public final class BlockFilterBiomeLibrary {
	private static final List<ItemStack> BIOME_STACKS = List.of(
		new ItemStack(Items.GRASS_BLOCK),      // Plains
		new ItemStack(Items.OAK_LOG),
		new ItemStack(Items.BIRCH_LOG),
		new ItemStack(Items.SUNFLOWER),
		new ItemStack(Items.SAND),             // Desert
		new ItemStack(Items.CACTUS),
		new ItemStack(Items.RED_SAND),
		new ItemStack(Items.SNOW_BLOCK),       // Snowy
		new ItemStack(Items.POWDER_SNOW_BUCKET),
		new ItemStack(Items.ICE),
		new ItemStack(Items.MANGROVE_LOG),     // Swamp
		new ItemStack(Items.MANGROVE_PROPAGULE),
		new ItemStack(Items.MUD),
		new ItemStack(Items.CHERRY_LOG),       // Cherry Grove
		new ItemStack(Items.PINK_PETALS),
		new ItemStack(Items.BAMBOO_BLOCK),     // Jungle
		new ItemStack(Items.COCOA_BEANS),
		new ItemStack(Items.JUNGLE_LEAVES),
		new ItemStack(Items.CRIMSON_NYLIUM),   // Nether
		new ItemStack(Items.WARPED_NYLIUM),
		new ItemStack(Items.NETHERRACK),
		new ItemStack(Items.END_STONE),        // The End
		new ItemStack(Items.PURPUR_BLOCK),
		new ItemStack(Items.SHULKER_BOX),
		new ItemStack(Items.MYCELIUM),         // Mushroom Fields
		new ItemStack(Items.RED_MUSHROOM_BLOCK),
		new ItemStack(Items.BROWN_MUSHROOM_BLOCK)
	);

	private BlockFilterBiomeLibrary() {
	}

	public static void append(ItemGroup.Entries entries) {
		entries.addAll(BIOME_STACKS);
	}

	public static ItemStack getIconStack() {
		return new ItemStack(Items.COMPASS);
	}
}
