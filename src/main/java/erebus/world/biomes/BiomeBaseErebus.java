package erebus.world.biomes;

import java.util.Random;

import erebus.ModBiomes;
import erebus.ModBlocks;
import erebus.core.handler.configs.ConfigHandler;
import erebus.world.SpawnerErebus.SpawnEntry;
import erebus.world.biomes.decorators.BiomeDecoratorBaseErebus;
import erebus.world.loot.IWeightProvider;
import erebus.world.loot.WeightedList;
import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public abstract class BiomeBaseErebus extends Biome implements IWeightProvider {

	private final BiomeDecoratorBaseErebus decorator;
	public static short biomeWeight;
	public int grassColor, foliageColor;
	private short[] fogColorRGB = new short[] { 255, 255, 255 };

	public byte topBlockMeta;
	public byte fillerBlockMeta;

	protected final WeightedList<SpawnEntry> spawningGradual = new WeightedList<SpawnEntry>();
	protected final WeightedList<SpawnEntry> spawningPopulate = new WeightedList<SpawnEntry>();

	public BiomeBaseErebus(BiomeProperties properties, BiomeDecoratorBaseErebus decorator) {
		super(properties);
		this.decorator = decorator;
		spawnableMonsterList.clear();
		spawnableCreatureList.clear();
		spawnableWaterCreatureList.clear();
		spawnableCaveCreatureList.clear();
		spawningPopulate.clear();
		spawningGradual.clear();
		topBlockMeta = 0;
		fillerBlockMeta = 0;
	}
	
	@Override
    public boolean canRain() {
        return false;
    }

	protected final BiomeBaseErebus setColors(int grassAndFoliage) {
		setColors(grassAndFoliage, grassAndFoliage);
		return this;
	}

	protected final BiomeBaseErebus setColors(int grass, int foliage) {
		grassColor = grass;
		foliageColor = foliage;
		return this;
	}

	protected final BiomeBaseErebus setFog(int red, int green, int blue) {
		if(ConfigHandler.INSTANCE.biomeFogColours)
			fogColorRGB = new short[] { (short) red, (short) green, (short) blue };
		else
			fogColorRGB = new short[] { 0, 0, 0 };
		return this;
	}

	protected final BiomeBaseErebus setWeight(int weight) {
		if (biomeWeight != 0)
			throw new RuntimeException("Cannot set biome weight twice!");
		biomeWeight = (short) weight;
		if (getClass().getGenericSuperclass() == BiomeBaseErebus.class)
			ModBiomes.BIOME_LIST.add(this); // add to list once weight is known
		return this;
	}

	public SpawnEntry getRandomSpawnGradual(Random rand) {
		return spawningGradual.getRandomItem(rand);
	}

	public SpawnEntry getRandomSpawnPopulate(Random rand) {
		return spawningPopulate.getRandomItem(rand);
	}

	@Override
	@SideOnly(Side.CLIENT)
    public int getModdedBiomeGrassColor(int original) {
		return grassColor;
    }

	@Override
	@SideOnly(Side.CLIENT)
	public int getGrassColorAtPos(BlockPos pos) {
		return grassColor;
	}

	@Override
	@SideOnly(Side.CLIENT)
    public int getModdedBiomeFoliageColor(int original) {
       return foliageColor;
    }

    @SideOnly(Side.CLIENT)
    public int getFoliageColorAtPos(BlockPos pos) {
    	return foliageColor;
    }

	@SideOnly(Side.CLIENT)
	public final short[] getFogRGB() {
		return fogColorRGB;
	}

	@Override
	public final short getWeight() {
		return biomeWeight;
	}

	public void populate(World world, Random rand, int x, int z) {
		decorator.populate(world, rand, x, z);
	}

	public void decorate(World world, Random rand, int x, int z) {
		// TimeMeasurement.start(id);

		decorator.decorate(world, rand, x, z);

		// TimeMeasurement.finish(id);
	}

	public Block placeCaveBlock(Block block, int x, int y, int z, Random rand) {
		return block == ModBlocks.UMBERSTONE || block == topBlock || block == fillerBlock || block == Blocks.SANDSTONE ? Blocks.AIR : block;
	}
}