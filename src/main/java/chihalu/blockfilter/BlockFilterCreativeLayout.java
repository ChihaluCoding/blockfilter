package chihalu.blockfilter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.function.UnaryOperator;

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
        private static final List<String> WOOD_SHAPE_ORDER = List.of(
                        "log", "wood", "stem", "hyphae",
                        "planks", "mosaic", "stairs", "slab",
                        "fence", "fence_gate", "door", "trapdoor", "pressure_plate", "button",
                        "sign", "wall_sign", "hanging_sign",
                        "boat", "chest_boat", "raft", "chest_raft",
                        "leaves", "sapling", "propagule", "roots", "fungus"
        );
        private static final List<String> COMMON_SHAPE_SUFFIXES = List.of(
                        "pressure_plate", "fence_gate", "hanging_sign", "wall_sign", "trapdoor", "door",
                        "stairs", "slab", "button", "fence", "gate", "wall", "pillar", "planks",
                        "mosaic", "log", "wood", "stem", "hyphae", "boat", "chest_boat",
                        "raft", "chest_raft", "leaves", "sapling", "propagule", "roots", "fungus",
                        "panel", "tile", "tiles", "bricks", "brick", "pane", "glass", "bars",
                        "chain", "torch", "lantern", "campfire", "beacon", "grate", "bulb"
        );
        private static final List<String> STONE_BASES = List.of(
                        "stone", "smooth_stone", "stone_brick", "stone_bricks", "cobblestone", "granite", "polished_granite",
                        "diorite", "polished_diorite", "andesite", "polished_andesite", "tuff", "polished_tuff", "calcite",
                        "dripstone_block", "basalt", "smooth_basalt", "blackstone", "polished_blackstone",
                        "polished_blackstone_brick", "polished_blackstone_bricks", "blackstone_brick", "blackstone_bricks",
                        "deepslate", "cobbled_deepslate", "polished_deepslate", "deepslate_brick", "deepslate_bricks",
                        "deepslate_tile", "deepslate_tiles", "mud_brick", "mud_bricks", "packed_mud", "quartz", "smooth_quartz",
                        "quartz_brick", "quartz_bricks", "cut_quartz", "end_stone", "end_stone_bricks", "purpur", "prismarine",
                        "prismarine_bricks", "dark_prismarine", "resin_bricks"
        );
        private static final List<String> STONE_SHAPE_ORDER = List.of(
                        "", "bricks", "tiles", "pillar", "stairs", "slab", "wall", "pressure_plate", "button"
        );
        private static final List<String> COPPER_BASES = List.of(
                        "copper_block", "cut_copper", "chiseled_copper", "copper", "raw_copper_block"
        );
        private static final List<String> COPPER_SHAPE_ORDER = List.of(
                        "", "stairs", "slab", "door", "trapdoor", "bars", "chain", "grate", "bulb"
        );
        private static final List<String> COPPER_VARIANT_ORDER = List.of(
                        "", "exposed", "weathered", "oxidized", "waxed", "waxed_exposed", "waxed_weathered", "waxed_oxidized"
        );
        private static final Set<String> COPPER_STAGE_PREFIXES = Set.of(
                        "exposed", "weathered", "oxidized"
        );
        private static final Set<String> VARIANT_PREFIXES = Set.of(
                        "waxed", "exposed", "weathered", "oxidized", "stripped", "mossy", "cracked", "infested",
                        "chiseled", "smooth", "polished", "cut"
        );
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
                        CategoryDefinition.builder("structure_wood", () -> new ItemStack(Items.OAK_PLANKS))
                                        .source(GROUP_BUILDING_BLOCKS, BlockFilterCreativeLayout::isWoodOrBambooStructure)
                                        .source(GROUP_NATURAL_BLOCKS, BlockFilterCreativeLayout::isWoodOrBambooStructure)
                                        .arranger(BlockFilterCreativeLayout::arrangeWoodStructures)
                                        .build(),
                        CategoryDefinition.builder("structure_stone", () -> new ItemStack(Items.STONE))
                                        .source(GROUP_BUILDING_BLOCKS, BlockFilterCreativeLayout::isStoneBlock)
                                        .source(GROUP_NATURAL_BLOCKS, BlockFilterCreativeLayout::isStoneBlock)
                                        .arranger(BlockFilterCreativeLayout::arrangeStoneBlocks)
                                        .build(),
                        CategoryDefinition.builder("structure_copper", () -> new ItemStack(Items.COPPER_BLOCK))
                                        .source(GROUP_BUILDING_BLOCKS, BlockFilterCreativeLayout::isCopperBlock)
                                        .source(GROUP_FUNCTIONAL_BLOCKS, BlockFilterCreativeLayout::isCopperBlock)
                                        .arranger(BlockFilterCreativeLayout::arrangeCopperBlocks)
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

        private static boolean isWoodOrBambooStructure(ItemStack stack) {
                if (shouldOmit(stack)) {
                        return false;
                }
                Item item = stack.getItem();
                String path = pathOf(item);
                if (detectWoodBase(path).isEmpty()) {
                        return false;
                }

                if (item instanceof net.minecraft.item.BlockItem) {
                        return true;
                }

                return !detectWoodShape(path).isEmpty();
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

        private static boolean isCopperBlock(ItemStack stack) {
                if (shouldOmit(stack)) {
                        return false;
                }
                Item item = stack.getItem();
                if (!(item instanceof net.minecraft.item.BlockItem)) {
                        return false;
                }

                String path = pathOf(item);
                if (!path.contains("copper") || path.contains("ore")) {
                        return false;
                }

                return !detectCopperBase(path).isEmpty();
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
                Map<String, Map<String, Map<String, List<ItemStack>>>> byShape = new LinkedHashMap<>();
                Map<String, List<ItemStack>> byBaseFallback = new LinkedHashMap<>();
                List<ItemStack> noBase = new ArrayList<>();

                for (ItemStack stack : stacks) {
                        String path = pathOf(stack.getItem());
                        String base = detectWoodBase(path);
                        if (base.isEmpty()) {
                                noBase.add(stack);
                                continue;
                        }

                        String shape = detectWoodShape(path);
                        if (shape.isEmpty()) {
                                byBaseFallback.computeIfAbsent(base, ignored -> new ArrayList<>()).add(stack);
                                continue;
                        }

                        String variant = detectVariantPrefix(path);

                        byShape.computeIfAbsent(shape, ignored -> new LinkedHashMap<>())
                                        .computeIfAbsent(variant, ignored -> new LinkedHashMap<>())
                                        .computeIfAbsent(base, ignored -> new ArrayList<>()).add(stack);
                }

                List<ItemStack> arranged = new ArrayList<>();

                for (String shape : WOOD_SHAPE_ORDER) {
                        Map<String, Map<String, List<ItemStack>>> variantBuckets = byShape.remove(shape);
                        if (variantBuckets == null || variantBuckets.isEmpty()) {
                                continue;
                        }
                        appendVariantBuckets(arranged, variantBuckets, WOOD_BASES);
                }

                for (Map<String, Map<String, List<ItemStack>>> remaining : byShape.values()) {
                        appendVariantBuckets(arranged, remaining, WOOD_BASES);
                }

                appendBaseBuckets(arranged, byBaseFallback, WOOD_BASES);

                noBase.sort(Comparator.comparing(stack -> pathOf(stack.getItem())));
                arranged.addAll(noBase);
                return arranged;
        }

        private static List<ItemStack> arrangeStoneBlocks(List<ItemStack> stacks) {
                Map<String, Map<String, Map<String, List<ItemStack>>>> byShape = new LinkedHashMap<>();
                List<ItemStack> noBase = new ArrayList<>();

                for (ItemStack stack : stacks) {
                        String path = pathOf(stack.getItem());
                        String base = detectStoneBase(path);
                        if (base.isEmpty()) {
                                noBase.add(stack);
                                continue;
                        }

                        String shape = detectStoneShape(path);
                        String variant = detectVariantPrefix(path);

                        byShape.computeIfAbsent(shape, ignored -> new LinkedHashMap<>())
                                        .computeIfAbsent(variant, ignored -> new LinkedHashMap<>())
                                        .computeIfAbsent(base, ignored -> new ArrayList<>()).add(stack);
                }

                List<ItemStack> arranged = new ArrayList<>();

                for (String shape : STONE_SHAPE_ORDER) {
                        Map<String, Map<String, List<ItemStack>>> variantBuckets = byShape.remove(shape);
                        if (variantBuckets == null || variantBuckets.isEmpty()) {
                                continue;
                        }
                        appendVariantBuckets(arranged, variantBuckets, STONE_BASES);
                }

                for (Map<String, Map<String, List<ItemStack>>> remaining : byShape.values()) {
                        appendVariantBuckets(arranged, remaining, STONE_BASES);
                }

                noBase.sort(Comparator.comparing(stack -> pathOf(stack.getItem())));
                arranged.addAll(noBase);
                return arranged;
        }

        private static List<ItemStack> arrangeCopperBlocks(List<ItemStack> stacks) {
                Map<String, Map<String, Map<String, List<ItemStack>>>> byShape = new LinkedHashMap<>();
                List<ItemStack> noBase = new ArrayList<>();

                for (ItemStack stack : stacks) {
                        String path = pathOf(stack.getItem());
                        String base = detectCopperBase(path);
                        if (base.isEmpty()) {
                                noBase.add(stack);
                                continue;
                        }

                        String shape = detectCopperShape(path);
                        String variant = detectCopperVariant(path);

                        byShape.computeIfAbsent(shape, ignored -> new LinkedHashMap<>())
                                        .computeIfAbsent(variant, ignored -> new LinkedHashMap<>())
                                        .computeIfAbsent(base, ignored -> new ArrayList<>()).add(stack);
                }

                List<ItemStack> arranged = new ArrayList<>();

                for (String shape : COPPER_SHAPE_ORDER) {
                        Map<String, Map<String, List<ItemStack>>> variantBuckets = byShape.remove(shape);
                        if (variantBuckets == null || variantBuckets.isEmpty()) {
                                continue;
                        }
                        appendCopperVariantBuckets(arranged, variantBuckets);
                }

                for (Map<String, Map<String, List<ItemStack>>> remaining : byShape.values()) {
                        appendCopperVariantBuckets(arranged, remaining);
                }

                noBase.sort(Comparator.comparing(stack -> pathOf(stack.getItem())));
                arranged.addAll(noBase);
                return arranged;
        }

        private static void appendCopperVariantBuckets(List<ItemStack> arranged,
                        Map<String, Map<String, List<ItemStack>>> variantBuckets) {
                if (variantBuckets == null || variantBuckets.isEmpty()) {
                        return;
                }

                Map<String, Map<String, List<ItemStack>>> workingBuckets = new LinkedHashMap<>(variantBuckets);

                for (String variant : COPPER_VARIANT_ORDER) {
                        Map<String, List<ItemStack>> baseBuckets = workingBuckets.remove(variant);
                        if (baseBuckets == null || baseBuckets.isEmpty()) {
                                continue;
                        }
                        appendBaseBuckets(arranged, new LinkedHashMap<>(baseBuckets), COPPER_BASES);
                }

                List<Map.Entry<String, Map<String, List<ItemStack>>>> leftovers = new ArrayList<>(workingBuckets.entrySet());
                leftovers.sort(Map.Entry.comparingByKey());
                for (Map.Entry<String, Map<String, List<ItemStack>>> entry : leftovers) {
                        Map<String, List<ItemStack>> baseBuckets = entry.getValue();
                        if (baseBuckets == null || baseBuckets.isEmpty()) {
                                continue;
                        }
                        appendBaseBuckets(arranged, new LinkedHashMap<>(baseBuckets), COPPER_BASES);
                }
        }

        private static void appendVariantBuckets(List<ItemStack> arranged,
                        Map<String, Map<String, List<ItemStack>>> variantBuckets, List<String> baseOrder) {
                Map<String, List<ItemStack>> baseBuckets = variantBuckets.remove("");
                if (baseBuckets != null && !baseBuckets.isEmpty()) {
                        appendBaseBuckets(arranged, baseBuckets, baseOrder);
                }

                List<Map.Entry<String, Map<String, List<ItemStack>>>> leftovers = new ArrayList<>(variantBuckets.entrySet());
                leftovers.sort(Map.Entry.comparingByKey());
                for (Map.Entry<String, Map<String, List<ItemStack>>> entry : leftovers) {
                        Map<String, List<ItemStack>> buckets = entry.getValue();
                        if (buckets == null || buckets.isEmpty()) {
                                continue;
                        }
                        appendBaseBuckets(arranged, buckets, baseOrder);
                }
        }

        private static void appendBaseBuckets(List<ItemStack> arranged, Map<String, List<ItemStack>> baseBuckets,
                        List<String> preferredOrder) {
                for (String base : preferredOrder) {
                        List<ItemStack> bucket = baseBuckets.remove(base);
                        if (bucket == null || bucket.isEmpty()) {
                                continue;
                        }
                        sortByPath(bucket);
                        arranged.addAll(bucket);
                }

                List<Map.Entry<String, List<ItemStack>>> leftovers = new ArrayList<>(baseBuckets.entrySet());
                leftovers.sort(Map.Entry.comparingByKey());
                for (Map.Entry<String, List<ItemStack>> entry : leftovers) {
                        List<ItemStack> bucket = entry.getValue();
                        if (bucket == null || bucket.isEmpty()) {
                                continue;
                        }
                        sortByPath(bucket);
                        arranged.addAll(bucket);
                }
        }

        private static void sortByPath(List<ItemStack> bucket) {
                bucket.sort(Comparator.comparing(stack -> pathOf(stack.getItem())));
        }

        private static List<ItemStack> arrangeVariants(List<ItemStack> stacks) {
                List<ItemStack> arranged = new ArrayList<>(stacks);
                arranged.sort(Comparator
                                .comparing((ItemStack stack) -> familyKey(pathOf(stack.getItem())))
                                .thenComparing(stack -> shapeKey(pathOf(stack.getItem())))
                                .thenComparing(stack -> normalizedVariantKey(pathOf(stack.getItem())))
                                .thenComparing(stack -> pathOf(stack.getItem())));
                return arranged;
        }

        private static String detectWoodBase(String path) {
		String best = "";
		int bestLength = -1;
		for (String base : WOOD_BASES) {
			if (matchesSegment(path, base) && base.length() > bestLength) {
				best = base;
				bestLength = base.length();
			}
		}
		return best;
	}

        private static String detectWoodShape(String path) {
                for (String shape : WOOD_SHAPE_ORDER) {
                        if (matchesSegment(path, shape)) {
                                return shape;
                        }
                }
                return "";
        }

        private static String detectStoneBase(String path) {
                if (path.isEmpty()) {
                        return "";
                }

                String normalized = normalizedVariantKey(path);
                String baseCandidate = normalized;
                for (String suffix : COMMON_SHAPE_SUFFIXES) {
                        if (baseCandidate.endsWith("_" + suffix)) {
                                baseCandidate = baseCandidate.substring(0, baseCandidate.length() - suffix.length() - 1);
                                break;
                        }
                }

                String best = "";
                int bestLength = -1;
                List<String> candidates = List.of(path, normalized, baseCandidate);
                for (String candidate : candidates) {
                        if (candidate.isEmpty()) {
                                continue;
                        }
                        for (String base : STONE_BASES) {
                                if (matchesSegment(candidate, base) && base.length() > bestLength) {
                                        best = base;
                                        bestLength = base.length();
                                }
                        }
                }

                if (!best.isEmpty()) {
                        return best;
                }
                if (!baseCandidate.isEmpty()) {
                        return baseCandidate;
                }
                return normalized;
        }

        private static String detectStoneShape(String path) {
                String shape = shapeKey(path);
                return shape.isEmpty() ? "" : shape;
        }

        private static String detectCopperBase(String path) {
                if (path.isEmpty()) {
                        return "";
                }

                String normalized = normalizedVariantKey(path);
                String baseCandidate = normalized;
                for (String suffix : COMMON_SHAPE_SUFFIXES) {
                        if (baseCandidate.endsWith("_" + suffix)) {
                                baseCandidate = baseCandidate.substring(0, baseCandidate.length() - suffix.length() - 1);
                                break;
                        }
                }

                String best = "";
                int bestLength = -1;
                List<String> candidates = List.of(path, normalized, baseCandidate);
                for (String candidate : candidates) {
                        if (candidate.isEmpty()) {
                                continue;
                        }
                        for (String base : COPPER_BASES) {
                                if (matchesSegment(candidate, base) && base.length() > bestLength) {
                                        best = base;
                                        bestLength = base.length();
                                }
                        }
                }

                return best;
        }

        private static String detectCopperShape(String path) {
                String shape = shapeKey(path);
                return shape.isEmpty() ? "" : shape;
        }

        private static String detectCopperVariant(String path) {
                if (path.isEmpty()) {
                        return "";
                }

                String[] parts = path.split("_");
                if (parts.length == 0) {
                        return "";
                }

                List<String> prefixes = new ArrayList<>();
                for (String part : parts) {
                        if (VARIANT_PREFIXES.contains(part)) {
                                prefixes.add(part);
                        } else {
                                break;
                        }
                }

                if (prefixes.isEmpty()) {
                        return "";
                }

                String first = prefixes.get(0);
                if ("waxed".equals(first)) {
                        if (prefixes.size() >= 2 && COPPER_STAGE_PREFIXES.contains(prefixes.get(1))) {
                                return "waxed_" + prefixes.get(1);
                        }
                        return "waxed";
                }

                if (COPPER_STAGE_PREFIXES.contains(first)) {
                        return first;
                }

                return first;
        }

        private static String detectVariantPrefix(String path) {
                if (path.isEmpty()) {
                        return "";
                }

                String[] parts = path.split("_");
                if (parts.length == 0) {
                        return "";
                }

                String candidate = parts[0];
                return VARIANT_PREFIXES.contains(candidate) ? candidate : "";
        }

        private static String familyKey(String path) {
                String normalized = normalizedVariantKey(path);
                for (String suffix : COMMON_SHAPE_SUFFIXES) {
                        if (normalized.endsWith("_" + suffix)) {
				return normalized.substring(0, normalized.length() - suffix.length() - 1);
			}
		}
		return normalized;
	}

	private static String shapeKey(String path) {
		String normalized = normalizedVariantKey(path);
		for (String suffix : COMMON_SHAPE_SUFFIXES) {
			if (normalized.endsWith("_" + suffix)) {
				return suffix;
			}
		}
		return "";
	}

	private static boolean matchesSegment(String path, String segment) {
		if (segment.isEmpty()) {
			return false;
		}
		if (path.equals(segment)) {
			return true;
		}
		if (path.startsWith(segment + "_") || path.endsWith("_" + segment)) {
			return true;
		}
		return path.contains("_" + segment + "_");
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




