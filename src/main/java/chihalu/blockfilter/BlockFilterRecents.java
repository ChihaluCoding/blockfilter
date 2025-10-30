package chihalu.blockfilter;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;

/**
 * Tracks block selections made in the creative inventory so we can surface them later.
 */
public final class BlockFilterRecents {
	private static final int MAX_HISTORY = 48;
	private static final Deque<Item> RECENT_BLOCKS = new ArrayDeque<>();

	private BlockFilterRecents() {
	}

	public static void record(ItemStack stack) {
		if (!(stack.getItem() instanceof BlockItem blockItem)) {
			return;
		}

		Item item = blockItem;
		if (RECENT_BLOCKS.remove(item)) {
			// Item already existed in the deque; we move it to the front.
		}

		RECENT_BLOCKS.addFirst(item);

		while (RECENT_BLOCKS.size() > MAX_HISTORY) {
			RECENT_BLOCKS.removeLast();
		}
	}

	public static void append(ItemGroup.Entries entries) {
		List<ItemStack> snapshot = snapshot();

		if (snapshot.isEmpty()) {
			entries.add(new ItemStack(Items.GRASS_BLOCK));
			entries.add(new ItemStack(Items.COBBLESTONE));
			entries.add(new ItemStack(Items.OAK_PLANKS));
			return;
		}

		for (ItemStack stack : snapshot) {
			entries.add(stack);
		}
	}

	public static ItemStack getIconStack() {
		Item first = RECENT_BLOCKS.peekFirst();
		return first != null ? new ItemStack(first) : new ItemStack(Items.BOOKSHELF);
	}

	private static List<ItemStack> snapshot() {
		List<ItemStack> items = new ArrayList<>(RECENT_BLOCKS.size());

		for (Item item : RECENT_BLOCKS) {
			items.add(new ItemStack(item));
		}

		return items;
	}
}
