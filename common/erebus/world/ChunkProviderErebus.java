package erebus.world;

import java.util.List;
import java.util.Random;
import net.minecraft.block.Block;
import net.minecraft.block.BlockSand;
import net.minecraft.entity.EnumCreatureType;
import net.minecraft.util.IProgressUpdate;
import net.minecraft.world.ChunkPosition;
import net.minecraft.world.World;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.IChunkProvider;
import net.minecraft.world.gen.MapGenBase;
import net.minecraft.world.gen.NoiseGeneratorOctaves;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.Event.Result;
import net.minecraftforge.event.terraingen.ChunkProviderEvent;
import erebus.ModBlocks;
import erebus.core.handler.ConfigHandler;
import erebus.world.feature.structure.WorldGenSpiderDungeons;
import erebus.world.structure.MapGenErebusCaves;
import erebus.world.structure.MapGenErebusRavine;

public class ChunkProviderErebus implements IChunkProvider{

	private final World worldObj;

	private final Random rand;

	private final NoiseGeneratorOctaves noiseGen1;
	private final NoiseGeneratorOctaves noiseGen2;
	private final NoiseGeneratorOctaves noiseGen3;
	private final NoiseGeneratorOctaves noiseGen4;
	private final NoiseGeneratorOctaves noiseGen5;
	private final NoiseGeneratorOctaves noiseGen6;
	private double[] noiseArray;
	private double[] stoneNoise;
	private double[] noiseData1;
	private double[] noiseData2;
	private double[] noiseData3;
	private double[] noiseData4;
	private double[] noiseData5;

	private BiomeGenBase[] biomesForGeneration;

	private final MapGenBase caveGenerator;
	private final MapGenBase ravineGenerator;

	public ChunkProviderErebus(World world, long seed){
		worldObj = world;

		rand = new Random(seed + 1);

		noiseGen1 = new NoiseGeneratorOctaves(rand,16);
		noiseGen2 = new NoiseGeneratorOctaves(rand,16);
		noiseGen3 = new NoiseGeneratorOctaves(rand,8);
		noiseGen4 = new NoiseGeneratorOctaves(rand,4);
		noiseGen5 = new NoiseGeneratorOctaves(rand,10);
		noiseGen6 = new NoiseGeneratorOctaves(rand,16);
		stoneNoise = new double[256];

		caveGenerator = new MapGenErebusCaves();
		ravineGenerator = new MapGenErebusRavine();
	}

	public void generateTerrain(int x, int z, byte[] blocks){
		byte byte0 = 4;
		byte byte1 = 32;
		int i = byte0 + 1;
		byte byte2 = 17;
		int j = byte0 + 1;
		biomesForGeneration = worldObj.getWorldChunkManager().loadBlockGeneratorData(biomesForGeneration,x * 16,z * 16,16,16);

		noiseArray = initializeNoiseField(noiseArray,x * byte0,0,z * byte0,i,byte2,j);

		for(int k = 0; k < byte0; k++)
			for(int l = 0; l < byte0; l++)
				for(int i1 = 0; i1 < 16; i1++){
					double d = 0.125D;
					double d1 = noiseArray[((k + 0) * j + l + 0) * byte2 + i1 + 0];
					double d2 = noiseArray[((k + 0) * j + l + 1) * byte2 + i1 + 0];
					double d3 = noiseArray[((k + 1) * j + l + 0) * byte2 + i1 + 0];
					double d4 = noiseArray[((k + 1) * j + l + 1) * byte2 + i1 + 0];
					double d5 = (noiseArray[((k + 0) * j + l + 0) * byte2 + i1 + 1] - d1) * d;
					double d6 = (noiseArray[((k + 0) * j + l + 1) * byte2 + i1 + 1] - d2) * d;
					double d7 = (noiseArray[((k + 1) * j + l + 0) * byte2 + i1 + 1] - d3) * d;
					double d8 = (noiseArray[((k + 1) * j + l + 1) * byte2 + i1 + 1] - d4) * d;

					for(int j1 = 0; j1 < 8; j1++){
						double d9 = 0.25D;
						double d10 = d1;
						double d11 = d2;
						double d12 = (d3 - d1) * d9;
						double d13 = (d4 - d2) * d9;

						for(int k1 = 0; k1 < 4; k1++){
							int l1 = k1 + k * 4 << 11 | 0 + l * 4 << 7 | i1 * 8 + j1;
							char c = '\200';
							double d14 = 0.25D;
							double d15 = d10;
							double d16 = (d11 - d10) * d14;

							for(int i2 = 0; i2 < 4; i2++){
								int j2 = 0;

								// Underground Water
								if (i1 * 8 + j1 < byte1){
									j2 = 0;
								}

								if (d15 > 0.0D) j2 = ModBlocks.umberstone.blockID;

								blocks[l1] = (byte)j2;
								l1 += c;
								d15 += d16;
							}

							d10 += d12;
							d11 += d13;
						}

						d1 += d5;
						d2 += d6;
						d3 += d7;
						d4 += d8;
					}
				}
	}

	@Override
	public Chunk loadChunk(int x, int z){
		return provideChunk(x,z);
	}

	@Override
	public Chunk provideChunk(int x, int z){
		rand.setSeed(x * 341873128712L + z * 132897987541L);
		byte[] blocks = new byte[32768];
		biomesForGeneration = worldObj.getWorldChunkManager().loadBlockGeneratorData(biomesForGeneration,x * 16,z * 16,16,16);

		generateTerrain(x,z,blocks);
		replaceBlocksForBiome(x,z,blocks,biomesForGeneration);

		caveGenerator.generate(this,worldObj,x,z,blocks);
		ravineGenerator.generate(this,worldObj,x,z,blocks);

		Chunk chunk = new Chunk(worldObj,blocks,x,z);
		byte[] biomeArrayReference = chunk.getBiomeArray();

		for(int a = 0; a < biomeArrayReference.length; ++a){
			biomeArrayReference[a] = (byte)biomesForGeneration[a].biomeID;
		}

		chunk.generateSkylightMap();
		chunk.resetRelightChecks();
		return chunk;
	}

	private double[] initializeNoiseField(double[] noise, int x, int y, int z, int sizeX, int sizeY, int sizeZ){
		if (noise == null) noise = new double[sizeX * sizeY * sizeZ];

		double d = 684.412D;
		double d1 = 2053.236D;
		noiseData4 = noiseGen5.generateNoiseOctaves(noiseData4,x,y,z,sizeX,1,sizeZ,1D,0D,1D);
		noiseData5 = noiseGen6.generateNoiseOctaves(noiseData5,x,y,z,sizeX,1,sizeZ,100D,0D,100D);
		noiseData1 = noiseGen3.generateNoiseOctaves(noiseData1,x,y,z,sizeX,sizeY,sizeZ,d * 0.0125D,d1 / 60D,d * 0.0125D);
		noiseData2 = noiseGen1.generateNoiseOctaves(noiseData2,x,y,z,sizeX,sizeY,sizeZ,d,d1,d);
		noiseData3 = noiseGen2.generateNoiseOctaves(noiseData3,x,y,z,sizeX,sizeY,sizeZ,d,d1,d);
		int index = 0;
		int j = 0;
		double ad[] = new double[sizeY];
		double oneOver512 = 1D / 512D;
		double groundNoiseMp = 1D / 2048D;

		for(int k = 0; k < sizeY; k++){
			ad[k] = Math.cos(k * Math.PI * 6D / sizeY) * 2D;
			double d2 = k;

			if (k > sizeY / 2) d2 = sizeY - 1 - k;

			if (d2 < 4D){
				d2 = 4D - d2;
				ad[k] -= d2 * d2 * d2 * 10D;
			}
		}

		for(int xx = 0; xx < sizeX; xx++){
			for(int zz = 0; zz < sizeZ; zz++){
				double d3 = (noiseData4[j] + 256D) * oneOver512;

				if (d3 > 1.0D) d3 = 1.0D;

				double d4 = 0.0D;
				double d5 = noiseData5[j] * 0.000125D;

				if (d5 < 0.0D) d5 = -d5;

				d5 = d5 * 3D - 3D;

				if (d5 < 0.0D){
					d5 /= 2D;

					if (d5 < -1D) d5 = -1D;

					d5 /= 1.4D;
					d5 *= 0.5D;
					d3 = 0.0D;
				}
				else{
					if (d5 > 1.0D) d5 = 1.0D;

					d5 /= 6D;
				}

				d3 += 0.5D;
				d5 = d5 * sizeY * 0.0625D;
				j++;

				for(int yy = 0; yy < sizeY; yy++){
					double d6 = 0.0D;
					double d7 = ad[yy];
					double d8 = noiseData2[index] * groundNoiseMp;
					double d9 = noiseData3[index] * groundNoiseMp;
					double d10 = (noiseData1[index] * 0.1D + 1.0D) * 0.5D;

					if (d10 < 0.0D) d6 = d8;
					else if (d10 > 1.0D) d6 = d9;
					else d6 = d8 + (d9 - d8) * d10;

					d6 -= d7;

					if (yy > sizeY - 4){
						double d11 = (yy - (sizeY - 4)) / 3F;
						d6 = d6 * (1.0D - d11) + -10D * d11;
					}

					if (yy < d4){
						double d12 = (d4 - yy) * 0.25D;
						if (d12 < 0.0D) d12 = 0.0D;
						if (d12 > 1.0D) d12 = 1.0D;

						d6 = d6 * (1.0D - d12) + -10D * d12;
					}

					noise[index] = d6;
					index++;
				}
			}
		}

		return noise;
	}

	public void replaceBlocksForBiome(int x, int z, byte[] blocks, BiomeGenBase[] biomes){
		ChunkProviderEvent.ReplaceBiomeBlocks event = new ChunkProviderEvent.ReplaceBiomeBlocks(this,x,z,blocks,biomes);
		MinecraftForge.EVENT_BUS.post(event);
		if (event.getResult() == Result.DENY) return;

		byte var5 = 0;
		stoneNoise = noiseGen4.generateNoiseOctaves(stoneNoise,x * 16,z * 16,0,16,16,1,0.0625D,0.0625D,0.0625D);

		for(int xInChunk = 0; xInChunk < 16; ++xInChunk)
			for(int zInChunk = 0; zInChunk < 16; ++zInChunk){
				BiomeGenBase biome = biomes[zInChunk + xInChunk * 16];
				float temperature = biome.getFloatTemperature();
				int var12 = (int)(stoneNoise[xInChunk + zInChunk * 16] / 3D + 3D + rand.nextDouble() * 0.25D);
				int var13 = -1;
				byte topBlock = biome.topBlock;
				byte fillerBlock = biome.fillerBlock;

				for(int yInChunk = 127; yInChunk >= 0; --yInChunk){
					int index = (zInChunk * 16 + xInChunk) * 128 + yInChunk;

					if ((yInChunk <= 5 && yInChunk <= 0 + rand.nextInt(5)) || (yInChunk >= 122 && yInChunk >= 127 - rand.nextInt(5))){
						blocks[index] = (byte)Block.bedrock.blockID;
					}
					else{
						byte block = blocks[index];

						if (block == 0) var13 = -1;
						else if (block == ModBlocks.umberstone.blockID || block == ConfigHandler.umberstoneID - 256){
							if (var13 == -1){
								if (var12 <= 0){
									topBlock = 0;
									fillerBlock = (byte)ModBlocks.umberstone.blockID;
								}
								else if (yInChunk >= var5 - 4 && yInChunk <= var5 + 1){
									topBlock = biome.topBlock;
									fillerBlock = biome.fillerBlock;
								}

								if (yInChunk < var5 && topBlock == 0) if (temperature < 0.15F) topBlock = (byte)Block.ice.blockID;
								else topBlock = (byte)Block.waterStill.blockID;

								var13 = var12;

								if (yInChunk >= var5 - 1) blocks[index] = topBlock;
								else blocks[index] = fillerBlock;
							}
						}
						else if (var13 > 0){
							--var13;
							blocks[index] = fillerBlock;

							if (var13 == 0 && fillerBlock == Block.sand.blockID){
								var13 = rand.nextInt(4);
								fillerBlock = (byte)Block.sandStone.blockID;
							}
						}
					}
				}
			}
	}

	@Override
	public void populate(IChunkProvider chunkProvider, int x, int z){
		BlockSand.fallInstantly = true;

		int worldCoordX = x * 16;
		int worldCoordZ = z * 16;

		BiomeGenBase biome = worldObj.getBiomeGenForCoords(worldCoordX + 16,worldCoordZ + 16);
		rand.setSeed(worldObj.getSeed());
		rand.setSeed(x * (rand.nextLong() / 2L * 2L + 1L) + z * (rand.nextLong() / 2L * 2L + 1L) ^ worldObj.getSeed());
		biome.decorate(worldObj,rand,worldCoordX,worldCoordZ);

		for(int attempt = 0; attempt < 14; ++attempt){
			new WorldGenSpiderDungeons().generate(worldObj,rand,worldCoordX + rand.nextInt(16) + 8,rand.nextInt(128),worldCoordZ + rand.nextInt(16) + 8);
		}

		BlockSand.fallInstantly = false;
	}

	@Override
	public void recreateStructures(int x, int z){}

	@Override
	public ChunkPosition findClosestStructure(World world, String structureIdentifier, int x, int y, int z){
		return null;
	}

	@Override
	public List getPossibleCreatures(EnumCreatureType creatureType, int x, int y, int z){
		BiomeGenBase biome = worldObj.getBiomeGenForCoords(x,z);
		return biome == null?null:biome.getSpawnableList(creatureType);
	}

	@Override
	public String makeString(){
		return "ErebusRandomLevelSource";
	}

	@Override
	public boolean chunkExists(int x, int z){
		return true;
	}

	@Override
	public boolean saveChunks(boolean mode, IProgressUpdate progressUpdate){
		return true;
	}

	@Override
	public boolean canSave(){
		return true;
	}

	@Override
	public int getLoadedChunkCount(){
		return 0;
	}

	@Override
	public boolean unloadQueuedChunks(){
		return false;
	}

	@Override
	public void saveExtraData(){}
}
