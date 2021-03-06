package com.resourcefulbees.resourcefulbees.registry;

import com.google.common.collect.ImmutableSet;
import com.resourcefulbees.resourcefulbees.ResourcefulBees;
import com.resourcefulbees.resourcefulbees.api.beedata.CustomBeeData;
import com.resourcefulbees.resourcefulbees.block.*;
import com.resourcefulbees.resourcefulbees.block.multiblocks.apiary.ApiaryBlock;
import com.resourcefulbees.resourcefulbees.block.multiblocks.apiary.ApiaryBreederBlock;
import com.resourcefulbees.resourcefulbees.block.multiblocks.apiary.ApiaryStorageBlock;
import com.resourcefulbees.resourcefulbees.block.multiblocks.centrifuge.CentrifugeCasingBlock;
import com.resourcefulbees.resourcefulbees.block.multiblocks.centrifuge.CentrifugeControllerBlock;
import com.resourcefulbees.resourcefulbees.config.Config;
import com.resourcefulbees.resourcefulbees.container.*;
import com.resourcefulbees.resourcefulbees.entity.passive.CustomBeeEntity;
import com.resourcefulbees.resourcefulbees.entity.passive.ResourcefulBee;
import com.resourcefulbees.resourcefulbees.item.*;
import com.resourcefulbees.resourcefulbees.lib.BeeConstants;
import com.resourcefulbees.resourcefulbees.lib.NBTConstants;
import com.resourcefulbees.resourcefulbees.recipe.CentrifugeRecipe;
import com.resourcefulbees.resourcefulbees.tileentity.*;
import com.resourcefulbees.resourcefulbees.tileentity.multiblocks.apiary.ApiaryBreederTileEntity;
import com.resourcefulbees.resourcefulbees.tileentity.multiblocks.apiary.ApiaryStorageTileEntity;
import com.resourcefulbees.resourcefulbees.tileentity.multiblocks.apiary.ApiaryTileEntity;
import com.resourcefulbees.resourcefulbees.tileentity.multiblocks.centrifuge.CentrifugeCasingTileEntity;
import com.resourcefulbees.resourcefulbees.tileentity.multiblocks.centrifuge.CentrifugeControllerTileEntity;
import com.resourcefulbees.resourcefulbees.utils.TooltipBuilder;
import com.resourcefulbees.resourcefulbees.utils.color.Color;
import com.resourcefulbees.resourcefulbees.world.BeeNestFeature;
import net.minecraft.block.*;
import net.minecraft.block.material.Material;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.EntityClassification;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ai.attributes.GlobalEntityTypeAttributes;
import net.minecraft.entity.merchant.villager.VillagerProfession;
import net.minecraft.entity.passive.BeeEntity;
import net.minecraft.fluid.Fluid;
import net.minecraft.inventory.container.ContainerType;
import net.minecraft.item.*;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Effects;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.village.PointOfInterestType;
import net.minecraft.world.World;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.NoFeatureConfig;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.extensions.IForgeContainerType;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;

@SuppressWarnings("ConstantConditions")
public class RegistryHandler {
	private static final AbstractBlock.Properties HIVE_PROPERTIES = Block.Properties.create(Material.WOOD).hardnessAndResistance(2).sound(SoundType.WOOD);
	private static final AbstractBlock.Properties NEST_PROPERTIES = Block.Properties.create(Material.WOOD).hardnessAndResistance(0.3F).sound(SoundType.WOOD);
	private static final AbstractBlock.Properties CENTRIFUGE_PROPERTIES = Block.Properties.create(Material.IRON).hardnessAndResistance(2).sound(SoundType.METAL);

	public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, ResourcefulBees.MOD_ID);
	public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, ResourcefulBees.MOD_ID);
	public static final DeferredRegister<Fluid> FLUIDS = DeferredRegister.create(ForgeRegistries.FLUIDS, ResourcefulBees.MOD_ID);
	public static final DeferredRegister<EntityType<?>> ENTITY_TYPES = DeferredRegister.create(ForgeRegistries.ENTITIES, ResourcefulBees.MOD_ID);
	public static final DeferredRegister<TileEntityType<?>>	TILE_ENTITY_TYPES = DeferredRegister.create(ForgeRegistries.TILE_ENTITIES, ResourcefulBees.MOD_ID);
	public static final DeferredRegister<PointOfInterestType> POIS = DeferredRegister.create(ForgeRegistries.POI_TYPES, ResourcefulBees.MOD_ID);
	public static final DeferredRegister<ContainerType<?>> CONTAINER_TYPES = DeferredRegister.create(ForgeRegistries.CONTAINERS, ResourcefulBees.MOD_ID);
	public static final DeferredRegister<IRecipeSerializer<?>> RECIPE_SERIALIZERS = DeferredRegister.create(ForgeRegistries.RECIPE_SERIALIZERS, ResourcefulBees.MOD_ID);
	public static final DeferredRegister<VillagerProfession> PROFESSIONS = DeferredRegister.create(ForgeRegistries.PROFESSIONS, ResourcefulBees.MOD_ID);
	public static final DeferredRegister<Feature<?>> FEATURES = DeferredRegister.create(ForgeRegistries.FEATURES, ResourcefulBees.MOD_ID);

    public static void init() {
		IEventBus bus = FMLJavaModLoadingContext.get().getModEventBus();
		ITEMS.register(bus);
		BLOCKS.register(bus);
		FLUIDS.register(bus);
		ENTITY_TYPES.register(bus);
		TILE_ENTITY_TYPES.register(bus);
		POIS.register(bus);
		CONTAINER_TYPES.register(bus);
		RECIPE_SERIALIZERS.register(bus);
		PROFESSIONS.register(bus);
		FEATURES.register(bus);
	}

	//region**************BLOCKS********************************************

	public static final RegistryObject<Block> T1_BEEHIVE = BLOCKS.register("t1_beehive", () -> new TieredBeehiveBlock(1,1.0F, HIVE_PROPERTIES));
	public static final RegistryObject<Block> T2_BEEHIVE = BLOCKS.register("t2_beehive", () -> new TieredBeehiveBlock(2, 1.5F, HIVE_PROPERTIES));
	public static final RegistryObject<Block> T3_BEEHIVE = BLOCKS.register("t3_beehive", () -> new TieredBeehiveBlock(3, 2.0F, HIVE_PROPERTIES));
	public static final RegistryObject<Block> T4_BEEHIVE = BLOCKS.register("t4_beehive", () -> new TieredBeehiveBlock(4, 4.0F, HIVE_PROPERTIES));
	public static final RegistryObject<Block> CENTRIFUGE = BLOCKS.register("centrifuge", () -> new CentrifugeBlock(CENTRIFUGE_PROPERTIES));
	public static final RegistryObject<Block> WAX_BLOCK = BLOCKS.register("wax_block", () -> new Block(Block.Properties.create(Material.CLAY).sound(SoundType.SNOW).hardnessAndResistance(0.3F)));
	public static final RegistryObject<Block> PREVIEW_BLOCK = BLOCKS.register("preview_block", () -> new Block(Block.Properties.create(Material.MISCELLANEOUS).sound(SoundType.GLASS)));
	public static final RegistryObject<Block> ERRORED_PREVIEW_BLOCK = BLOCKS.register("error_preview_block", () -> new Block(Block.Properties.create(Material.MISCELLANEOUS).sound(SoundType.GLASS)));
	public static final RegistryObject<Block> GOLD_FLOWER = BLOCKS.register("gold_flower", () -> new FlowerBlock(Effects.INVISIBILITY, 10, Block.Properties.create(Material.PLANTS).doesNotBlockMovement().hardnessAndResistance(0).sound(SoundType.PLANT)));
	public static final RegistryObject<Block> OAK_BEE_NEST = BLOCKS.register("bee_nest", () -> new TieredBeehiveBlock(0, 0.5F, NEST_PROPERTIES));
	public static final RegistryObject<Block> BIRCH_BEE_NEST = BLOCKS.register("birch_bee_nest", () -> new TieredBeehiveBlock(0, 0.5F, NEST_PROPERTIES));
	public static final RegistryObject<Block> BROWN_MUSHROOM_BEE_NEST = BLOCKS.register("brown_mushroom_bee_nest", () -> new TieredBeehiveBlock(0, 0.5F, NEST_PROPERTIES));
	public static final RegistryObject<Block> CRIMSON_BEE_NEST = BLOCKS.register("crimson_bee_nest", () -> new TieredBeehiveBlock(0, 0.5F, NEST_PROPERTIES));
	public static final RegistryObject<Block> CRIMSON_NYLIUM_BEE_NEST = BLOCKS.register("crimson_nylium_bee_nest", () -> new TieredBeehiveBlock(0, 0.5F, NEST_PROPERTIES));
	public static final RegistryObject<Block> DARK_OAK_BEE_NEST = BLOCKS.register("dark_oak_bee_nest", () -> new TieredBeehiveBlock(0, 0.5F, NEST_PROPERTIES));
	public static final RegistryObject<Block> RED_MUSHROOM_BEE_NEST = BLOCKS.register("red_mushroom_bee_nest", () -> new TieredBeehiveBlock(0, 0.5F, NEST_PROPERTIES));
	public static final RegistryObject<Block> SPRUCE_BEE_NEST = BLOCKS.register("spruce_bee_nest", () -> new TieredBeehiveBlock(0, 0.5F, NEST_PROPERTIES));
	public static final RegistryObject<Block> WARPED_BEE_NEST = BLOCKS.register("warped_bee_nest", () -> new TieredBeehiveBlock(0, 0.5F, NEST_PROPERTIES));
	public static final RegistryObject<Block> WARPED_NYLIUM_BEE_NEST = BLOCKS.register("warped_nylium_bee_nest", () -> new TieredBeehiveBlock(0, 0.5F, NEST_PROPERTIES));
	public static final RegistryObject<Block> ACACIA_BEE_NEST = BLOCKS.register("acacia_bee_nest", () -> new TieredBeehiveBlock(0, 0.5F, NEST_PROPERTIES));
	public static final RegistryObject<Block> GRASS_BEE_NEST = BLOCKS.register("grass_bee_nest", () -> new TieredBeehiveBlock(0, 0.5F, NEST_PROPERTIES));
	public static final RegistryObject<Block> JUNGLE_BEE_NEST = BLOCKS.register("jungle_bee_nest", () -> new TieredBeehiveBlock(0, 0.5F, NEST_PROPERTIES));
	public static final RegistryObject<Block> NETHER_BEE_NEST = BLOCKS.register("nether_bee_nest", () -> new TieredBeehiveBlock(0, 0.5F, NEST_PROPERTIES));
	public static final RegistryObject<Block> PRISMARINE_BEE_NEST = BLOCKS.register("prismarine_bee_nest", () -> new TieredBeehiveBlock(0, 0.5F, NEST_PROPERTIES));
	public static final RegistryObject<Block> PURPUR_BEE_NEST = BLOCKS.register("purpur_bee_nest", () -> new TieredBeehiveBlock(0, 0.5F, NEST_PROPERTIES));
	public static final RegistryObject<Block> WITHER_BEE_NEST = BLOCKS.register("wither_bee_nest", () -> new TieredBeehiveBlock(0, 0.5F, NEST_PROPERTIES));
	public static final RegistryObject<Block> T1_APIARY_BLOCK = BLOCKS.register("t1_apiary", () -> new ApiaryBlock(5, 5, 6));
	public static final RegistryObject<Block> T2_APIARY_BLOCK = BLOCKS.register("t2_apiary", () -> new ApiaryBlock(6, 5, 6));
	public static final RegistryObject<Block> T3_APIARY_BLOCK = BLOCKS.register("t3_apiary", () -> new ApiaryBlock(7, 6, 8));
	public static final RegistryObject<Block> T4_APIARY_BLOCK = BLOCKS.register("t4_apiary", () -> new ApiaryBlock(8, 6, 8));
	public static final RegistryObject<Block> APIARY_STORAGE_BLOCK = BLOCKS.register("apiary_storage", () -> new ApiaryStorageBlock(NEST_PROPERTIES));
	public static final RegistryObject<Block> APIARY_BREEDER_BLOCK = BLOCKS.register("apiary_breeder", () -> new ApiaryBreederBlock(NEST_PROPERTIES));
	public static final RegistryObject<Block> MECHANICAL_CENTRIFUGE = BLOCKS.register("mechanical_centrifuge", () -> new MechanicalCentrifugeBlock(CENTRIFUGE_PROPERTIES.nonOpaque()));
	public static final RegistryObject<Block> HONEY_GENERATOR = BLOCKS.register("honey_generator", () -> new HoneyGenerator(CENTRIFUGE_PROPERTIES));
	public static final RegistryObject<FlowingFluidBlock> HONEY_FLUID_BLOCK = BLOCKS.register("honey_fluid_block", () -> new FlowingFluidBlock(FluidRegistry.HONEY_FLUID, Block.Properties.create(Material.WATER).doesNotBlockMovement().hardnessAndResistance(100.0F).noDrops()));
	public static final RegistryObject<Block> CENTRIFUGE_CONTROLLER = BLOCKS.register("centrifuge_controller", () -> new CentrifugeControllerBlock(CENTRIFUGE_PROPERTIES));
	public static final RegistryObject<Block> CENTRIFUGE_CASING = BLOCKS.register("centrifuge_casing", () -> new CentrifugeCasingBlock(CENTRIFUGE_PROPERTIES));
	public static final RegistryObject<Block> CREATIVE_GEN = BLOCKS.register("creative_gen", () -> new CreativeGen(CENTRIFUGE_PROPERTIES));

	//endregion

	//region**************ITEMS*********************************************

	public static final RegistryObject<Item> OREO_COOKIE = ITEMS.register("oreo_cookie", () -> new Item(new Item.Properties().group(ItemGroupResourcefulBees.RESOURCEFUL_BEES).food(new Food.Builder()
			.effect(new EffectInstance(Effects.REGENERATION, 600, 1), 1)
			.effect(new EffectInstance(Effects.ABSORPTION, 2400, 3), 1)
			.effect(new EffectInstance(Effects.SATURATION, 2400, 1), 1)
			.effect(new EffectInstance(Effects.LUCK, 600, 3), 1)
			.effect(new EffectInstance(Effects.FIRE_RESISTANCE, 6000, 0), 1)
			.effect(new EffectInstance(Effects.RESISTANCE, 6000, 0), 1)
			.effect(new EffectInstance(Effects.WATER_BREATHING, 6000, 0), 1)
			.effect(new EffectInstance(Effects.NIGHT_VISION, 1200, 0), 1)
			.hunger(8)
			.saturation(8)
			.setAlwaysEdible()
			.build())
			.rarity(Rarity.EPIC)));
	public static final RegistryObject<Item> T1_BEEHIVE_ITEM = ITEMS.register("t1_beehive",() -> new BlockItem(T1_BEEHIVE.get(), new Item.Properties().group(ItemGroupResourcefulBees.RESOURCEFUL_BEES)));
	public static final RegistryObject<Item> T2_BEEHIVE_ITEM = ITEMS.register("t2_beehive",() -> new BlockItem(T2_BEEHIVE.get(), new Item.Properties().group(ItemGroupResourcefulBees.RESOURCEFUL_BEES)));
	public static final RegistryObject<Item> T3_BEEHIVE_ITEM = ITEMS.register("t3_beehive",() -> new BlockItem(T3_BEEHIVE.get(), new Item.Properties().group(ItemGroupResourcefulBees.RESOURCEFUL_BEES)));
	public static final RegistryObject<Item> T4_BEEHIVE_ITEM = ITEMS.register("t4_beehive",() -> new BlockItem(T4_BEEHIVE.get(), new Item.Properties().group(ItemGroupResourcefulBees.RESOURCEFUL_BEES)));
	public static final RegistryObject<Item> SCRAPER = ITEMS.register("scraper", () ->  new Item(new Item.Properties().group(ItemGroupResourcefulBees.RESOURCEFUL_BEES).maxStackSize(1)) {
		@OnlyIn(Dist.CLIENT)
		@Override
		public void addInformation(@Nonnull ItemStack stack, @Nullable World worldIn, @Nonnull List<ITextComponent> tooltip, @Nonnull ITooltipFlag flagIn) {
			tooltip.add(new StringTextComponent(TextFormatting.GOLD + I18n.format("item.resourcefulbees.scraper.tooltip")));
			tooltip.add(new StringTextComponent(TextFormatting.GOLD + I18n.format("item.resourcefulbees.scraper.tooltip.1")));
			super.addInformation(stack, worldIn, tooltip, flagIn);
		}
	});
	public static final RegistryObject<Item> SMOKER = ITEMS.register("smoker", () -> new Smoker(new Item.Properties().group(ItemGroupResourcefulBees.RESOURCEFUL_BEES).setNoRepair().maxDamage(Config.SMOKER_DURABILITY.get())));
	public static final RegistryObject<Item> BELLOW = ITEMS.register("bellow", () -> new Item(new Item.Properties().group(ItemGroupResourcefulBees.RESOURCEFUL_BEES)));
	public static final RegistryObject<Item> SMOKERCAN = ITEMS.register("smoker_can", () -> new Item(new Item.Properties().group(ItemGroupResourcefulBees.RESOURCEFUL_BEES)));
	public static final RegistryObject<Item> WAX = ITEMS.register("wax", () -> new Item(new Item.Properties().group(ItemGroupResourcefulBees.RESOURCEFUL_BEES)));
	public static final RegistryObject<Item> CENTRIFUGE_ITEM = ITEMS.register("centrifuge", () -> new BlockItem(CENTRIFUGE.get(), new Item.Properties().group(ItemGroupResourcefulBees.RESOURCEFUL_BEES)));
	public static final RegistryObject<Item> MECHANICAL_CENTRIFUGE_ITEM = ITEMS.register("mechanical_centrifuge", () -> new BlockItem(MECHANICAL_CENTRIFUGE.get(), new Item.Properties().group(ItemGroupResourcefulBees.RESOURCEFUL_BEES)));
	public static final RegistryObject<Item> CENTRIFUGE_CONTROLLER_ITEM = ITEMS.register("centrifuge_controller", () -> new BlockItem(CENTRIFUGE_CONTROLLER.get(), new Item.Properties().group(ItemGroupResourcefulBees.RESOURCEFUL_BEES)));
	public static final RegistryObject<Item> CENTRIFUGE_CASING_ITEM = ITEMS.register("centrifuge_casing", () -> new BlockItem(CENTRIFUGE_CASING.get(), new Item.Properties().group(ItemGroupResourcefulBees.RESOURCEFUL_BEES)));
	public static final RegistryObject<Item> HONEY_GENERATOR_ITEM = ITEMS.register("honey_generator", () -> new BlockItem(HONEY_GENERATOR.get(), new Item.Properties().group(ItemGroupResourcefulBees.RESOURCEFUL_BEES)));
	public static final RegistryObject<Item> WAX_BLOCK_ITEM = ITEMS.register("wax_block", () -> new BlockItem(WAX_BLOCK.get(), new Item.Properties().group(ItemGroupResourcefulBees.RESOURCEFUL_BEES)));
	public static final RegistryObject<Item> GOLD_FLOWER_ITEM = ITEMS.register("gold_flower", () -> new BlockItem(GOLD_FLOWER.get(), new Item.Properties().group(ItemGroupResourcefulBees.RESOURCEFUL_BEES)));
	public static final RegistryObject<Item> BEE_JAR = ITEMS.register("bee_jar", () -> new BeeJar(new Item.Properties().group(ItemGroupResourcefulBees.RESOURCEFUL_BEES).maxDamage(0).maxStackSize(16)));
	public static final RegistryObject<Item> OAK_BEE_NEST_ITEM = ITEMS.register("bee_nest",() -> new BlockItem(OAK_BEE_NEST.get(), new Item.Properties().group(ItemGroupResourcefulBees.RESOURCEFUL_BEES)));
	public static final RegistryObject<Item> ACACIA_BEE_NEST_ITEM = ITEMS.register("acacia_bee_nest",() -> new BlockItem(ACACIA_BEE_NEST.get(), new Item.Properties().group(ItemGroupResourcefulBees.RESOURCEFUL_BEES)));
	public static final RegistryObject<Item> GRASS_BEE_NEST_ITEM = ITEMS.register("grass_bee_nest",() -> new BlockItem(GRASS_BEE_NEST.get(), new Item.Properties().group(ItemGroupResourcefulBees.RESOURCEFUL_BEES)));
	public static final RegistryObject<Item> JUNGLE_BEE_NEST_ITEM = ITEMS.register("jungle_bee_nest",() -> new BlockItem(JUNGLE_BEE_NEST.get(), new Item.Properties().group(ItemGroupResourcefulBees.RESOURCEFUL_BEES)));
	public static final RegistryObject<Item> NETHER_BEE_NEST_ITEM = ITEMS.register("nether_bee_nest",() -> new BlockItem(NETHER_BEE_NEST.get(), new Item.Properties().group(ItemGroupResourcefulBees.RESOURCEFUL_BEES)));
	public static final RegistryObject<Item> PRISMARINE_BEE_NEST_ITEM = ITEMS.register("prismarine_bee_nest",() -> new BlockItem(PRISMARINE_BEE_NEST.get(), new Item.Properties().group(ItemGroupResourcefulBees.RESOURCEFUL_BEES)));
	public static final RegistryObject<Item> PURPUR_BEE_NEST_ITEM = ITEMS.register("purpur_bee_nest",() -> new BlockItem(PURPUR_BEE_NEST.get(), new Item.Properties().group(ItemGroupResourcefulBees.RESOURCEFUL_BEES)));
	public static final RegistryObject<Item> BIRCH_BEE_NEST_ITEM = ITEMS.register("birch_bee_nest",() -> new BlockItem(BIRCH_BEE_NEST.get(), new Item.Properties().group(ItemGroupResourcefulBees.RESOURCEFUL_BEES)));
	public static final RegistryObject<Item> WITHER_BEE_NEST_ITEM = ITEMS.register("wither_bee_nest",() -> new BlockItem(WITHER_BEE_NEST.get(), new Item.Properties().group(ItemGroupResourcefulBees.RESOURCEFUL_BEES)));
	public static final RegistryObject<Item> BROWN_MUSHROOM_NEST_ITEM = ITEMS.register("brown_mushroom_bee_nest",() -> new BlockItem(BROWN_MUSHROOM_BEE_NEST.get(), new Item.Properties().group(ItemGroupResourcefulBees.RESOURCEFUL_BEES)));
	public static final RegistryObject<Item> CRIMSON_BEE_NEST_ITEM = ITEMS.register("crimson_bee_nest",() -> new BlockItem(CRIMSON_BEE_NEST.get(), new Item.Properties().group(ItemGroupResourcefulBees.RESOURCEFUL_BEES)));
	public static final RegistryObject<Item> CRIMSON_NYLIUM_BEE_NEST_ITEM = ITEMS.register("crimson_nylium_bee_nest",() -> new BlockItem(CRIMSON_NYLIUM_BEE_NEST.get(), new Item.Properties().group(ItemGroupResourcefulBees.RESOURCEFUL_BEES)));
	public static final RegistryObject<Item> DARK_OAK_NEST_ITEM = ITEMS.register("dark_oak_bee_nest",() -> new BlockItem(DARK_OAK_BEE_NEST.get(), new Item.Properties().group(ItemGroupResourcefulBees.RESOURCEFUL_BEES)));
	public static final RegistryObject<Item> RED_MUSHROOM_NEST_ITEM = ITEMS.register("red_mushroom_bee_nest",() -> new BlockItem(RED_MUSHROOM_BEE_NEST.get(), new Item.Properties().group(ItemGroupResourcefulBees.RESOURCEFUL_BEES)));
	public static final RegistryObject<Item> SPRUCE_BEE_NEST_ITEM = ITEMS.register("spruce_bee_nest",() -> new BlockItem(SPRUCE_BEE_NEST.get(), new Item.Properties().group(ItemGroupResourcefulBees.RESOURCEFUL_BEES)));
	public static final RegistryObject<Item> WARPED_BEE_NEST_ITEM = ITEMS.register("warped_bee_nest",() -> new BlockItem(WARPED_BEE_NEST.get(), new Item.Properties().group(ItemGroupResourcefulBees.RESOURCEFUL_BEES)));
	public static final RegistryObject<Item> WARPED_NYLIUM_BEE_NEST_ITEM = ITEMS.register("warped_nylium_bee_nest",() -> new BlockItem(WARPED_NYLIUM_BEE_NEST.get(), new Item.Properties().group(ItemGroupResourcefulBees.RESOURCEFUL_BEES)));
	public static final RegistryObject<Item> T1_APIARY_ITEM = ITEMS.register("t1_apiary",() -> new BlockItem(T1_APIARY_BLOCK.get(), new Item.Properties().group(ItemGroupResourcefulBees.RESOURCEFUL_BEES)));
	public static final RegistryObject<Item> T2_APIARY_ITEM = ITEMS.register("t2_apiary",() -> new BlockItem(T2_APIARY_BLOCK.get(), new Item.Properties().group(ItemGroupResourcefulBees.RESOURCEFUL_BEES)));
	public static final RegistryObject<Item> T3_APIARY_ITEM = ITEMS.register("t3_apiary",() -> new BlockItem(T3_APIARY_BLOCK.get(), new Item.Properties().group(ItemGroupResourcefulBees.RESOURCEFUL_BEES)));
	public static final RegistryObject<Item> T4_APIARY_ITEM = ITEMS.register("t4_apiary",() -> new BlockItem(T4_APIARY_BLOCK.get(), new Item.Properties().group(ItemGroupResourcefulBees.RESOURCEFUL_BEES)));
	public static final RegistryObject<Item> APIARY_STORAGE_ITEM = ITEMS.register("apiary_storage",() -> new BlockItem(APIARY_STORAGE_BLOCK.get(), new Item.Properties().group(ItemGroupResourcefulBees.RESOURCEFUL_BEES)));
	public static final RegistryObject<Item> APIARY_BREEDER_ITEM = ITEMS.register("apiary_breeder",() -> new BlockItem(APIARY_BREEDER_BLOCK.get(), new Item.Properties().group(ItemGroupResourcefulBees.RESOURCEFUL_BEES)));
	public static final RegistryObject<Item> IRON_STORAGE_UPGRADE = ITEMS.register("iron_storage_upgrade", () -> new UpgradeItem(new Item.Properties().group(ItemGroupResourcefulBees.RESOURCEFUL_BEES).maxDamage(0).maxStackSize(16),
			UpgradeItem.builder()
			.upgradeType(NBTConstants.NBT_STORAGE_UPGRADE)
			.upgradeModification(NBTConstants.NBT_SLOT_UPGRADE, 27F)
			.build()));
	public static final RegistryObject<Item> GOLD_STORAGE_UPGRADE = ITEMS.register("gold_storage_upgrade", () -> new UpgradeItem(new Item.Properties().group(ItemGroupResourcefulBees.RESOURCEFUL_BEES).maxDamage(0).maxStackSize(16),
			UpgradeItem.builder()
			.upgradeType(NBTConstants.NBT_STORAGE_UPGRADE)
			.upgradeModification(NBTConstants.NBT_SLOT_UPGRADE, 54F)
			.build()));
	public static final RegistryObject<Item> DIAMOND_STORAGE_UPGRADE = ITEMS.register("diamond_storage_upgrade", () -> new UpgradeItem(new Item.Properties().group(ItemGroupResourcefulBees.RESOURCEFUL_BEES).maxDamage(0).maxStackSize(16),
			UpgradeItem.builder()
			.upgradeType(NBTConstants.NBT_STORAGE_UPGRADE)
			.upgradeModification(NBTConstants.NBT_SLOT_UPGRADE, 81F)
			.build()));
	public static final RegistryObject<Item> EMERALD_STORAGE_UPGRADE = ITEMS.register("emerald_storage_upgrade", () -> new UpgradeItem(new Item.Properties().group(ItemGroupResourcefulBees.RESOURCEFUL_BEES).maxDamage(0).maxStackSize(16),
			UpgradeItem.builder()
			.upgradeType(NBTConstants.NBT_STORAGE_UPGRADE)
			.upgradeModification(NBTConstants.NBT_SLOT_UPGRADE, 108F)
			.build()));
	public static final RegistryObject<Item> APIARY_BREEDER_UPGRADE = ITEMS.register("apiary_breeder_upgrade", () -> new UpgradeItem(new Item.Properties().group(ItemGroupResourcefulBees.RESOURCEFUL_BEES).maxDamage(0).maxStackSize(16),
			UpgradeItem.builder()
			.upgradeType(NBTConstants.NBT_BREEDER_UPGRADE)
			.upgradeModification(NBTConstants.NBT_BREEDER_COUNT, 1)
			.build()) {
		@Override
		public void addInformation(@Nonnull ItemStack stack, @Nullable World worldIn, @Nonnull List<ITextComponent> tooltip, @Nonnull ITooltipFlag flagIn) {
			tooltip.addAll(new TooltipBuilder()
					.addTip(I18n.format("item.resourcefulbees.apiary_breeder_upgrade.tooltip.info"), TextFormatting.GOLD)
					.build());
			super.addInformation(stack, worldIn, tooltip, flagIn);
		}
	});
	public static final RegistryObject<Item> APIARY_BREED_TIME_UPGRADE = ITEMS.register("apiary_breed_time_upgrade", () -> new UpgradeItem(new Item.Properties().group(ItemGroupResourcefulBees.RESOURCEFUL_BEES).maxDamage(0).maxStackSize(16),
			UpgradeItem.builder()
			.upgradeType(NBTConstants.NBT_BREEDER_UPGRADE)
			.upgradeModification(NBTConstants.NBT_BREED_TIME, 300)
			.build()) {
		@Override
		public void addInformation(@Nonnull ItemStack stack, @Nullable World worldIn, @Nonnull List<ITextComponent> tooltip, @Nonnull ITooltipFlag flagIn) {
			tooltip.addAll(new TooltipBuilder()
					.addTip(I18n.format("item.resourcefulbees.apiary_breed_time_upgrade.tooltip.info"), TextFormatting.GOLD)
					.build());
			super.addInformation(stack, worldIn, tooltip, flagIn);
		}
	});
	public static final RegistryObject<Item> HONEY_FLUID_BUCKET = ITEMS.register("honey_fluid_bucket", () -> new BucketItem(FluidRegistry.HONEY_FLUID, new Item.Properties().group(ItemGroupResourcefulBees.RESOURCEFUL_BEES).containerItem(Items.BUCKET).maxStackSize(1)));
	//endregion

	//region**************TILE ENTITIES*************************************

	public static final RegistryObject<TileEntityType<?>> TIERED_BEEHIVE_TILE_ENTITY = TILE_ENTITY_TYPES.register("tiered_beehive", () -> TileEntityType.Builder
			.create(TieredBeehiveTileEntity::new, T1_BEEHIVE.get(), T2_BEEHIVE.get(), T3_BEEHIVE.get(), T4_BEEHIVE.get(), ACACIA_BEE_NEST.get(), GRASS_BEE_NEST.get(), JUNGLE_BEE_NEST.get(), NETHER_BEE_NEST.get(),
					PRISMARINE_BEE_NEST.get(), PURPUR_BEE_NEST.get(), WITHER_BEE_NEST.get(), OAK_BEE_NEST.get(), BIRCH_BEE_NEST.get(), BROWN_MUSHROOM_BEE_NEST.get(),CRIMSON_BEE_NEST.get(),
					CRIMSON_NYLIUM_BEE_NEST.get(), DARK_OAK_BEE_NEST.get(), RED_MUSHROOM_BEE_NEST.get(), SPRUCE_BEE_NEST.get(), WARPED_BEE_NEST.get(), WARPED_NYLIUM_BEE_NEST.get())
			.build(null));

	public static final RegistryObject<TileEntityType<?>> CENTRIFUGE_ENTITY = TILE_ENTITY_TYPES.register("centrifuge", () -> TileEntityType.Builder
			.create(CentrifugeTileEntity::new, CENTRIFUGE.get())
			.build(null));

	public static final RegistryObject<TileEntityType<?>> MECHANICAL_CENTRIFUGE_ENTITY = TILE_ENTITY_TYPES.register("mechanical_centrifuge", () -> TileEntityType.Builder
			.create(MechanicalCentrifugeTileEntity::new, MECHANICAL_CENTRIFUGE.get())
			.build(null));

	public static final RegistryObject<TileEntityType<?>> CENTRIFUGE_CONTROLLER_ENTITY = TILE_ENTITY_TYPES.register("centrifuge_controller", () -> TileEntityType.Builder
			.create(CentrifugeControllerTileEntity::new, CENTRIFUGE_CONTROLLER.get())
			.build(null));

	public static final RegistryObject<TileEntityType<?>> CENTRIFUGE_CASING_ENTITY = TILE_ENTITY_TYPES.register("centrifuge_casing", () -> TileEntityType.Builder
			.create(CentrifugeCasingTileEntity::new, CENTRIFUGE_CASING.get())
			.build(null));

	public static final RegistryObject<TileEntityType<?>> HONEY_GENERATOR_ENTITY = TILE_ENTITY_TYPES.register("honey_generator", () -> TileEntityType.Builder
			.create(HoneyGeneratorTileEntity::new, HONEY_GENERATOR.get())
			.build(null));

	public static final RegistryObject<TileEntityType<?>> CREATIVE_GEN_ENTITY = TILE_ENTITY_TYPES.register("creative_gen", () -> TileEntityType.Builder
			.create(CreativeGenTileEntity::new, CREATIVE_GEN.get())
			.build(null));

	public static final RegistryObject<TileEntityType<?>> APIARY_TILE_ENTITY = TILE_ENTITY_TYPES.register("apiary", () -> TileEntityType.Builder
			.create(ApiaryTileEntity::new, T1_APIARY_BLOCK.get(), T2_APIARY_BLOCK.get(), T3_APIARY_BLOCK.get(), T4_APIARY_BLOCK.get())
			.build(null));

	public static final RegistryObject<TileEntityType<?>> APIARY_STORAGE_TILE_ENTITY = TILE_ENTITY_TYPES.register("apiary_storage", () -> TileEntityType.Builder
			.create(ApiaryStorageTileEntity::new, APIARY_STORAGE_BLOCK.get())
			.build(null));

	public static final RegistryObject<TileEntityType<?>> APIARY_BREEDER_TILE_ENTITY = TILE_ENTITY_TYPES.register("apiary_breeder", () -> TileEntityType.Builder
			.create(ApiaryBreederTileEntity::new, APIARY_BREEDER_BLOCK.get())
			.build(null));
	//endregion

	//region**************POINT OF INTEREST*********************************

	public static final RegistryObject<PointOfInterestType> TIERED_BEEHIVE_POI = POIS.register("tiered_beehive_poi", () -> new PointOfInterestType("tiered_beehive_poi", ImmutableSet.copyOf(T1_BEEHIVE.get().getStateContainer().getValidStates()), 1, 1));
	//endregion

	//region**************CONTAINERS****************************************

	public static final RegistryObject<ContainerType<CentrifugeContainer>> CENTRIFUGE_CONTAINER = CONTAINER_TYPES.register("centrifuge", () -> IForgeContainerType
			.create((id,inv,c) -> new CentrifugeContainer(id, inv.player.world, c.readBlockPos(), inv)));

	public static final RegistryObject<ContainerType<MechanicalCentrifugeContainer>> MECHANICAL_CENTRIFUGE_CONTAINER = CONTAINER_TYPES.register("mechanical_centrifuge", () -> IForgeContainerType
			.create((id,inv,c) -> new MechanicalCentrifugeContainer(id, inv.player.world, c.readBlockPos(), inv)));

	public static final RegistryObject<ContainerType<CentrifugeMultiblockContainer>> CENTRIFUGE_MULTIBLOCK_CONTAINER = CONTAINER_TYPES.register("centrifuge_multiblock", () -> IForgeContainerType
			.create((id,inv,c) -> new CentrifugeMultiblockContainer(id, inv.player.world, c.readBlockPos(), inv)));

	public static final RegistryObject<ContainerType<HoneyGeneratorContainer>> HONEY_GENERATOR_CONTAINER = CONTAINER_TYPES.register("honey_generator", () -> IForgeContainerType
			.create((id,inv,c) -> new HoneyGeneratorContainer(id, inv.player.world, c.readBlockPos(), inv)));

	public static final RegistryObject<ContainerType<UnvalidatedApiaryContainer>> UNVALIDATED_APIARY_CONTAINER = CONTAINER_TYPES.register("unvalidated_apiary", () -> IForgeContainerType
			.create((id,inv,c) -> new UnvalidatedApiaryContainer(id, inv.player.world, c.readBlockPos(), inv)));

	public static final RegistryObject<ContainerType<ValidatedApiaryContainer>> VALIDATED_APIARY_CONTAINER = CONTAINER_TYPES.register("validated_apiary", () -> IForgeContainerType
			.create((id,inv,c) -> new ValidatedApiaryContainer(id, inv.player.world, c.readBlockPos(), inv)));

	public static final RegistryObject<ContainerType<ApiaryStorageContainer>> APIARY_STORAGE_CONTAINER = CONTAINER_TYPES.register("apiary_storage", () -> IForgeContainerType
			.create((id,inv,c) -> new ApiaryStorageContainer(id, inv.player.world, c.readBlockPos(), inv)));

	public static final RegistryObject<ContainerType<ApiaryBreederContainer>> APIARY_BREEDER_CONTAINER = CONTAINER_TYPES.register("apiary_breeder", () -> IForgeContainerType
			.create((id,inv,c) -> new ApiaryBreederContainer(id, inv.player.world, c.readBlockPos(), inv)));
	//endregion

	//region****************RECIPES*****************************************

	public static final RegistryObject<IRecipeSerializer<?>> CENTRIFUGE_RECIPE = RECIPE_SERIALIZERS.register("centrifuge",
			() -> new CentrifugeRecipe.Serializer<>(CentrifugeRecipe::new));
	//endregion

	//region****************VILLAGER PROFESSIONS****************************

	public static final RegistryObject<VillagerProfession> BEEKEEPER = PROFESSIONS.register("beekeeper", () -> new VillagerProfession("beekeeper", TIERED_BEEHIVE_POI.get(), ImmutableSet.of(), ImmutableSet.of(), SoundEvents.ITEM_BOTTLE_FILL));
	//endregion

	//region****************FEATURES****************************************

	public static final RegistryObject<Feature<NoFeatureConfig>> BEE_NEST_FEATURE = FEATURES.register("bee_nest_feature", () -> new BeeNestFeature(NoFeatureConfig.CODEC));
	//endregion

	public static void addEntityAttributes() {
		BeeRegistry.MOD_BEES.forEach((s, customBee) -> GlobalEntityTypeAttributes.put(customBee.get(), BeeEntity.createBeeAttributes().build()));
	}

	public static void registerDynamicBees() {
		BeeRegistry.getRegistry().getBees().forEach((name, customBee) -> {
			if (customBee.shouldResourcefulBeesDoForgeRegistration) {
				if (customBee.hasHoneycomb() && !customBee.hasCustomDrop()) {
					registerHoneycomb(name, customBee);
				} else if (customBee.hasHoneycomb() && customBee.hasCustomDrop() && !customBee.getName().equals(BeeConstants.OREO_BEE)) {
					fillCustomDrops(customBee);
				}
				registerBee(name, customBee);
			}
		});
	}

	private static void registerHoneycomb(String name, CustomBeeData customBeeData) {
		final RegistryObject<Block> customHoneycombBlock = BLOCKS.register(name + "_honeycomb_block", () -> new HoneycombBlock(name, customBeeData.getColorData(), Block.Properties.from(Blocks.HONEYCOMB_BLOCK)));
		final RegistryObject<Item> customHoneycomb = ITEMS.register(name + "_honeycomb", () -> new HoneycombItem(name, customBeeData.getColorData(), new Item.Properties().group(ItemGroupResourcefulBees.RESOURCEFUL_BEES)));
		final RegistryObject<Item> customHoneycombBlockItem = ITEMS.register(name + "_honeycomb_block", () -> new BlockItem(customHoneycombBlock.get(), new Item.Properties().group(ItemGroupResourcefulBees.RESOURCEFUL_BEES)));

		customBeeData.setCombBlockRegistryObject(customHoneycombBlock);
		customBeeData.setCombRegistryObject(customHoneycomb);
		customBeeData.setCombBlockItemRegistryObject(customHoneycombBlockItem);
	}

	private static void registerBee(String name, CustomBeeData customBeeData) {
		final RegistryObject<EntityType<? extends CustomBeeEntity>> customBeeEntity = ENTITY_TYPES.register(name + "_bee", () -> EntityType.Builder
				.<ResourcefulBee>create((type, world) -> new ResourcefulBee(type, world, customBeeData), EntityClassification.CREATURE)
				.size(0.7F, 0.6F)
				.build(name + "_bee"));

		final RegistryObject<Item> customBeeSpawnEgg = ITEMS.register(name + "_bee_spawn_egg",
				() -> new BeeSpawnEggItem(customBeeEntity, Color.parseInt(BeeConstants.VANILLA_BEE_COLOR), 0x303030, customBeeData.getColorData(), new Item.Properties().group(ItemGroupResourcefulBees.RESOURCEFUL_BEES)));

		BeeRegistry.MOD_BEES.put(name, customBeeEntity);
		customBeeData.setEntityTypeRegistryID(customBeeEntity.getId());
		customBeeData.setSpawnEggItemRegistryObject(customBeeSpawnEgg);
	}

	private static void fillCustomDrops(CustomBeeData customBeeData) {
		RegistryObject<Item> customComb = RegistryObject.of(new ResourceLocation(customBeeData.getCustomCombDrop()), ForgeRegistries.ITEMS);
		RegistryObject<Item> customCombBlock = RegistryObject.of(new ResourceLocation(customBeeData.getCustomCombBlockDrop()), ForgeRegistries.ITEMS);

		customBeeData.setCombRegistryObject(customComb);
		customBeeData.setCombBlockItemRegistryObject(customCombBlock);
	}

	@SubscribeEvent
	public void onRegisterBlocks(RegistryEvent.Register<Block> event) {
		//Exists to disallow further bee registrations
		//preventing potential for NPE's
		BeeRegistry.getRegistry().denyRegistration();
	}
}
