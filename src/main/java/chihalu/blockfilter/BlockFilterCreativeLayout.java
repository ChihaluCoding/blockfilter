package chihalu.blockfilter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Arrays;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.function.UnaryOperator;

import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.registry.Registries;
import net.minecraft.resource.featuretoggle.FeatureSet;
import net.minecraft.util.Identifier;

/**
 * Generates partitioned creative tab contents that re-use vanilla inventories while presenting clearer categories.
 */
final class BlockFilterCreativeLayout {
	private static final String GROUP_BUILDING_BLOCKS = "minecraft:building_blocks";
	private static final String GROUP_COLORED_BLOCKS = "minecraft:colored_blocks";
	private static final String GROUP_NATURAL_BLOCKS = "minecraft:natural_blocks";
        private static final String GROUP_FUNCTIONAL_BLOCKS = "minecraft:functional_blocks";
	private static final String[] COMBAT_KEYWORDS = {
			"sword", "bow", "crossbow", "trident", "shield", "helmet", "chestplate", "leggings", "boots",
			"horse_armor", "mace", "arrow", "totem"
	};
	private static final String[] POTION_KEYWORDS = {
			"potion", "bottle", "suspicious_stew", "tipped_arrow", "lingering"
	};
	private static final String[] BOOK_KEYWORDS = {
			"book", "scroll", "atlas"
	};
	private static final String[] PATTERN_KEYWORDS = {
			"pattern", "trim", "template", "sherd", "fragment", "banner"
	};
        private static final String[] HARNESS_KEYWORDS = {
                        "saddle", "lead", "name_tag", "rein", "harness"
        };
        private static final String[] FOOD_KEYWORDS = {
			"apple", "bread", "carrot", "potato", "beetroot", "melon", "pumpkin", "cake", "pie", "cookie", "berries",
			"sweet_berries", "glow_berries", "chorus_fruit", "golden_apple", "honey", "meat", "pork", "beef", "chicken",
			"mutton", "rabbit", "cod", "salmon", "tropical_fish", "pufferfish", "stew", "soup", "suspicious_stew",
			"beef", "steak", "rotten_flesh", "spider_eye", "saturation", "baked_potato", "dried_kelp", "mushroom_stew",
			"bread", "cookie", "beetroot_soup", "beetroot_seed", "carrot_on_a_stick", "warped_fungus_on_a_stick", "egg"
	};
	private static final String[] MATERIAL_KEYWORDS = {
			"ingot", "nugget", "scrap", "rod", "dust", "pearl", "slime_ball", "ghast_tear", "magma_cream",
			"blaze_powder", "blaze_rod", "rabbit_foot", "phantom_membrane", "prismarine_shard", "prismarine_crystals",
			"heart_of_the_sea", "nether_star", "echo_shard", "amethyst_shard"
	};
        private static final List<String> WOOD_BASES = List.of(
                        "oak", "spruce", "birch", "jungle", "acacia", "dark_oak", "mangrove", "crimson", "warped", "bamboo",
                        "cherry", "pale_oak"
        );
        private static final List<String> WOOD_SHAPE_SUFFIXES = List.of("stairs", "slab", "planks");
        private static final Set<String> VARIANT_PREFIXES = Set.of("waxed", "exposed", "weathered", "oxidized");
        private static final String[] NETHER_KEYWORDS = {
                        "nether", "crimson", "warped", "basalt", "blackstone", "quartz", "soul", "magma", "shroomlight",
                        "ancient_debris", "nylium", "fungus", "roots", "wart"
        };
        private static final String[] END_KEYWORDS = {
                        "end", "purpur", "chorus", "shulker", "dragon", "end_rod"
        };
        private static final String[] SAND_KEYWORDS = {
                        "sand", "sandstone", "terracotta", "clay", "mud", "gravel"
        };

        private static final List<CategoryDefinition> DEFINITIONS = List.of(
                        CategoryDefinition.builder("dimension_nether", () -> new ItemStack(Items.NETHERRACK))
                                        .source(GROUP_BUILDING_BLOCKS, BlockFilterCreativeLayout::isNetherBlock)
                                        .source(GROUP_NATURAL_BLOCKS, BlockFilterCreativeLayout::isNetherBlock)
                                        .arranger(BlockFilterCreativeLayout::arrangeVariants)
                                        .build(),
                        CategoryDefinition.builder("dimension_end", () -> new ItemStack(Items.END_STONE))
                                        .source(GROUP_BUILDING_BLOCKS, BlockFilterCreativeLayout::isEndBlock)
                                        .source(GROUP_NATURAL_BLOCKS, BlockFilterCreativeLayout::isEndBlock)
                                        .arranger(BlockFilterCreativeLayout::arrangeVariants)
                                        .build(),
                        CategoryDefinition.builder("structure_wood", () -> new ItemStack(Items.OAK_PLANKS))
                                        .source(GROUP_BUILDING_BLOCKS, BlockFilterCreativeLayout::isWoodConstruction)
                                        .source(GROUP_NATURAL_BLOCKS, BlockFilterCreativeLayout::isWoodFromNature)
                                        .arranger(BlockFilterCreativeLayout::arrangeWoodStructures)
                                        .build(),
                        CategoryDefinition.builder("structure_stone", () -> new ItemStack(Items.STONE_BRICKS))
                                        .source(GROUP_BUILDING_BLOCKS, BlockFilterCreativeLayout::isStoneBlock)
                                        .arranger(BlockFilterCreativeLayout::arrangeVariants)
                                        .build(),
                        CategoryDefinition.builder("structure_sand", () -> new ItemStack(Items.SANDSTONE))
                                        .source(GROUP_BUILDING_BLOCKS, BlockFilterCreativeLayout::isSandBlock)
                                        .source(GROUP_NATURAL_BLOCKS, BlockFilterCreativeLayout::isSandBlock)
                                        .arranger(BlockFilterCreativeLayout::arrangeVariants)
                                        .build(),
                        CategoryDefinition.builder("materials_copper", () -> new ItemStack(Items.COPPER_BLOCK))
                                        .source(GROUP_BUILDING_BLOCKS, BlockFilterCreativeLayout::isCopperItem)
                                        .source(GROUP_FUNCTIONAL_BLOCKS, BlockFilterCreativeLayout::isCopperItem)
                                        .arranger(BlockFilterCreativeLayout::arrangeVariants)
                                        .build(),
                        CategoryDefinition.builder("structure_misc", () -> new ItemStack(Items.QUARTZ_BRICKS))
                                        .source(GROUP_BUILDING_BLOCKS, stack -> true)
                                        .arranger(BlockFilterCreativeLayout::arrangeVariants)
                                        .build(),
                        CategoryDefinition.builder("structure_colored", () -> new ItemStack(Items.RED_CONCRETE))
                                        .source(GROUP_COLORED_BLOCKS, stack -> true)
                                        .arranger(BlockFilterCreativeLayout::arrangeVariants)
                                        .build(),
                        CategoryDefinition.builder("nature_resources", () -> new ItemStack(Items.GRASS_BLOCK))
                                        .source(GROUP_NATURAL_BLOCKS, BlockFilterCreativeLayout::isOverworldNature)
                                        .arranger(BlockFilterCreativeLayout::arrangeVariants)
                                        .build(),
                        CategoryDefinition.builder("workstations_storage", () -> new ItemStack(Items.CRAFTING_TABLE))
                                        .source(GROUP_FUNCTIONAL_BLOCKS, BlockFilterCreativeLayout::isWorkstationOrStorage)
                                        .arranger(BlockFilterCreativeLayout::arrangeVariants)
                                        .build(),
                        CategoryDefinition.builder("utility_mobility", () -> new ItemStack(Items.SCAFFOLDING))
                                        .source(GROUP_FUNCTIONAL_BLOCKS, BlockFilterCreativeLayout::isUtilityBlock)
                                        .arranger(BlockFilterCreativeLayout::arrangeVariants)
                                        .build());

	private static LayoutSnapshot snapshot;

	private BlockFilterCreativeLayout() {
	}

	static List<CategoryDefinition> definitions() {
		return DEFINITIONS;
	}

	static synchronized List<ItemStack> stacksFor(CategoryDefinition definition, ItemGroup.DisplayContext context) {
		if (snapshot == null || !snapshot.matches(context.enabledFeatures(), context.hasPermissions())) {
			snapshot = LayoutSnapshot.build(context);
		}
		return snapshot.stacks(definition.identifier());
	}

	static synchronized void invalidate() {
		snapshot = null;
	}

                static final class CategoryDefinition {
		private final Identifier identifier;
		private final Supplier<ItemStack> iconSupplier;
		private final List<SourceRule> sources;
		private final UnaryOperator<List<ItemStack>> arranger;

		private CategoryDefinition(Identifier identifier, Supplier<ItemStack> iconSupplier, List<SourceRule> sources,
				UnaryOperator<List<ItemStack>> arranger) {
			this.identifier = identifier;
			this.iconSupplier = iconSupplier;
			this.sources = List.copyOf(sources);
			this.arranger = arranger;
		}

		Identifier identifier() {
			return identifier;
		}

		String translationKey() {
			return "itemGroup." + BlockFilter.MOD_ID + "." + identifier.getPath();
		}

		ItemStack icon() {
			return iconSupplier.get().copy();
		}

		List<ItemStack> extract(Map<String, List<ItemStack>> sourcePools) {
			List<ItemStack> collected = new ArrayList<>();
			for (SourceRule rule : sources) {
				List<ItemStack> pool = sourcePools.get(rule.groupId());
				if (pool == null || pool.isEmpty()) {
					continue;
				}

				List<ItemStack> matches = rule.extract(pool);
				for (ItemStack stack : matches) {
					if (shouldOmit(stack)) {
						continue;
					}
					collected.add(stack);
				}
			}

			List<ItemStack> arranged = arranger.apply(new ArrayList<>(collected));
			return List.copyOf(arranged);
		}

		static Builder builder(String path, Supplier<ItemStack> iconSupplier) {
			return new Builder(Identifier.of(BlockFilter.MOD_ID, path), iconSupplier);
		}

		static Builder builder(Identifier id, Supplier<ItemStack> iconSupplier) {
			return new Builder(id, iconSupplier);
		}

		static final class Builder {
			private static final UnaryOperator<List<ItemStack>> IDENTITY = list -> list;

			private final Identifier identifier;
			private final Supplier<ItemStack> iconSupplier;
			private final List<SourceRule> sources = new ArrayList<>();
			private UnaryOperator<List<ItemStack>> arranger = IDENTITY;

			private Builder(Identifier identifier, Supplier<ItemStack> iconSupplier) {
				this.identifier = identifier;
				this.iconSupplier = iconSupplier;
			}

			Builder source(String groupId, Predicate<ItemStack> predicate) {
				sources.add(new SourceRule(groupId, predicate));
				return this;
			}

			Builder arranger(UnaryOperator<List<ItemStack>> arranger) {
				this.arranger = arranger;
				return this;
			}

			CategoryDefinition build() {
				return new CategoryDefinition(identifier, iconSupplier, sources, arranger);
			}
		}
	}

private static final class SourceRule {
		private final String groupId;
		private final Predicate<ItemStack> predicate;

		private SourceRule(String groupId, Predicate<ItemStack> predicate) {
			this.groupId = groupId;
			this.predicate = predicate;
		}

		String groupId() {
			return groupId;
		}

		List<ItemStack> extract(List<ItemStack> pool) {
			if (pool.isEmpty()) {
				return List.of();
			}

			List<ItemStack> collected = new ArrayList<>();
			for (int i = 0; i < pool.size(); i++) {
				ItemStack stack = pool.get(i);
				if (stack.isEmpty()) {
					continue;
				}

				if (shouldOmit(stack)) {
					pool.set(i, ItemStack.EMPTY);
					continue;
				}

				if (predicate == null || predicate.test(stack)) {
					collected.add(stack.copy());
					pool.set(i, ItemStack.EMPTY);
				}
			}

			// Remove consumed stacks from the pool.
			pool.removeIf(ItemStack::isEmpty);
			return collected;
		}
	}

	private static final class LayoutSnapshot {
		private final FeatureSet features;
		private final boolean hasPermissions;
		private final Map<Identifier, List<ItemStack>> stacksByCategory;

		private LayoutSnapshot(FeatureSet features, boolean hasPermissions, Map<Identifier, List<ItemStack>> stacksByCategory) {
			this.features = features;
			this.hasPermissions = hasPermissions;
			this.stacksByCategory = stacksByCategory;
		}

		static LayoutSnapshot build(ItemGroup.DisplayContext context) {
			Map<String, List<ItemStack>> pools = VanillaCreativeContent.capture(context);
			Map<Identifier, List<ItemStack>> byCategory = new LinkedHashMap<>();

			for (CategoryDefinition definition : DEFINITIONS) {
				List<ItemStack> extracted = definition.extract(pools);
				byCategory.put(definition.identifier(), List.copyOf(extracted));
			}

                        return new LayoutSnapshot(context.enabledFeatures(), context.hasPermissions(), Collections.unmodifiableMap(byCategory));
                }

		boolean matches(FeatureSet currentFeatures, boolean currentPermissions) {
			return hasPermissions == currentPermissions && features.equals(currentFeatures);
		}

		List<ItemStack> stacks(Identifier categoryId) {
			return stacksByCategory.getOrDefault(categoryId, List.of());
		}
	}

		static boolean shouldOmit(ItemStack stack) {
		if (stack.isEmpty()) {
			return true;
		}

		Item item = stack.getItem();
		String path = pathOf(item);
		if (path.isEmpty()) {
			return false;
		}

		if (path.contains("copper")) {
			return false;
		}

		if (containsAny(path, FOOD_KEYWORDS)) {
			return true;
		}

		if (containsAny(path, "spawn_egg")) {
			return true;
		}

		if (containsAny(path, COMBAT_KEYWORDS)) {
			return true;
		}

		if (containsAny(path, POTION_KEYWORDS)) {
			return true;
		}

		if (containsAny(path, BOOK_KEYWORDS)) {
			return true;
		}

		if (containsAny(path, PATTERN_KEYWORDS)) {
			return true;
		}

		if (containsAny(path, HARNESS_KEYWORDS)) {
			return true;
		}

		if (containsAny(path, MATERIAL_KEYWORDS)) {
			return true;
		}

		return false;
	}

	// === filters ====================================================================================================

	private static boolean isWoodConstruction(ItemStack stack) {
		if (shouldOmit(stack)) {
			return false;
		}
		Item item = stack.getItem();
		if (!(item instanceof net.minecraft.item.BlockItem blockItem)) {
			return false;
		}

		Block block = blockItem.getBlock();
		String path = pathOf(item);

		return containsAny(path, "planks", "stairs", "slab", "fence", "gate", "door", "trapdoor", "pressure_plate",
				"button", "log", "wood", "hyphae", "stem", "bamboo", "palisade", "railing", "beam")
				|| block == Blocks.BOOKSHELF || block == Blocks.CHISELED_BOOKSHELF;
	}

	private static boolean isWoodFromNature(ItemStack stack) {
		if (shouldOmit(stack)) {
			return false;
		}
		Item item = stack.getItem();
		String path = pathOf(item);
		return containsAny(path, "log", "wood", "leaves", "sapling", "fungus", "hyphae", "propagule", "bamboo", "vine", "moss", "azalea");
	}

        private static boolean isStoneBlock(ItemStack stack) {
                if (shouldOmit(stack)) {
                        return false;
                }
		Item item = stack.getItem();
		if (!(item instanceof net.minecraft.item.BlockItem)) {
			return false;
		}

                String path = pathOf(item);
                return containsAny(path, "stone", "brick", "deepslate", "granite", "andesite", "diorite", "blackstone", "basalt",
                                "calcite", "tuff", "slate", "marble", "tile", "pillar", "cobblestone", "mud_brick", "resin", "prismarine",
                                "quartz") && !containsAny(path, SAND_KEYWORDS);
        }

        private static boolean isWorkstationOrStorage(ItemStack stack) {
		if (shouldOmit(stack)) {
			return false;
		}
		Item item = stack.getItem();
		if (!(item instanceof net.minecraft.item.BlockItem)) {
			return false;
		}

		String path = pathOf(item);
		return containsAny(path, "table", "furnace", "anvil", "stonecutter", "grindstone", "cartography", "smithing",
				"loom", "campfire", "smoker", "blast_furnace", "composter", "hopper", "dropper", "dispenser", "target",
				"chest", "barrel", "shulker_box", "jukebox", "note_block", "lectern", "bell", "armor_stand", "banner_pattern")
				|| item == Items.BREWING_STAND || item == Items.ENCHANTING_TABLE || item == Items.CAULDRON
				|| item == Items.ENDER_CHEST;
	}

	private static boolean isUtilityBlock(ItemStack stack) {
		if (shouldOmit(stack)) {
			return false;
		}
		Item item = stack.getItem();
		if (!(item instanceof net.minecraft.item.BlockItem)) {
			return false;
		}

		String path = pathOf(item);
		return containsAny(path, "scaffolding", "ladder", "rail", "path", "door_mat", "bell", "waystone", "sign",
				"hanging_sign", "crate") || item == Items.BEACON;
	}

        private static boolean isCopperItem(ItemStack stack) {
                return pathOf(stack.getItem()).contains("copper");
        }

        private static boolean isNetherBlock(ItemStack stack) {
                if (shouldOmit(stack)) {
                        return false;
                }
                Item item = stack.getItem();
                if (!(item instanceof net.minecraft.item.BlockItem)) {
                        return false;
                }

                String path = pathOf(item);
                return containsAny(path, NETHER_KEYWORDS);
        }

        private static boolean isEndBlock(ItemStack stack) {
                if (shouldOmit(stack)) {
                        return false;
                }
                Item item = stack.getItem();
                if (!(item instanceof net.minecraft.item.BlockItem)) {
                        return false;
                }

                String path = pathOf(item);
                return containsAny(path, END_KEYWORDS);
        }

        private static boolean isSandBlock(ItemStack stack) {
                if (shouldOmit(stack)) {
                        return false;
                }
                Item item = stack.getItem();
                if (!(item instanceof net.minecraft.item.BlockItem)) {
                        return false;
                }

                String path = pathOf(item);
                return containsAny(path, SAND_KEYWORDS);
        }

        private static boolean isOverworldNature(ItemStack stack) {
                if (shouldOmit(stack)) {
                        return false;
                }
                Item item = stack.getItem();
                String path = pathOf(item);
                return !containsAny(path, NETHER_KEYWORDS) && !containsAny(path, END_KEYWORDS);
        }

        private static String pathOf(Item item) {
                Identifier id = Registries.ITEM.getId(item);
                return id == null ? "" : id.getPath().toLowerCase(Locale.ROOT);
        }

        private static List<ItemStack> arrangeWoodStructures(List<ItemStack> stacks) {
		Map<String, Map<String, ItemStack>> table = new LinkedHashMap<>();
		for (String base : WOOD_BASES) {
			table.put(base, new LinkedHashMap<>());
		}

		List<ItemStack> leftovers = new ArrayList<>();

		for (ItemStack stack : stacks) {
			String path = pathOf(stack.getItem());
			boolean assigned = false;

			for (String base : WOOD_BASES) {
				if (!path.contains(base)) {
					continue;
				}

				for (String suffix : WOOD_SHAPE_SUFFIXES) {
					if (path.endsWith("_" + suffix) || path.equals(base + "_" + suffix)) {
						table.get(base).putIfAbsent(suffix, stack);
						assigned = true;
						break;
					}
				}

				if (assigned) {
					break;
				}
			}

			if (!assigned) {
				leftovers.add(stack);
			}
		}

		List<ItemStack> arranged = new ArrayList<>();
		for (String suffix : WOOD_SHAPE_SUFFIXES) {
			for (String base : WOOD_BASES) {
				ItemStack stack = table.get(base).get(suffix);
				if (stack != null) {
					arranged.add(stack);
				}
			}
		}

                arranged.addAll(arrangeVariants(leftovers));
                return arranged;
        }

        private static List<ItemStack> arrangeVariants(List<ItemStack> stacks) {
                List<ItemStack> arranged = new ArrayList<>(stacks);
                arranged.sort(Comparator.comparing((ItemStack stack) -> normalizedVariantKey(pathOf(stack.getItem())))
                                .thenComparing(stack -> pathOf(stack.getItem())));
                return arranged;
        }

        private static String normalizedVariantKey(String path) {
                if (path.isEmpty()) {
                        return path;
                }

                String[] parts = path.split("_");
                int start = 0;
                while (start < parts.length && VARIANT_PREFIXES.contains(parts[start])) {
                        start++;
                }

                if (start == 0) {
                        return path;
                }

                return String.join("_", Arrays.copyOfRange(parts, start, parts.length));
        }

        private static boolean containsAny(String haystack, String... needles) {
                for (String needle : needles) {
			if (haystack.contains(needle)) {
				return true;
			}
		}
		return false;
	}
}




