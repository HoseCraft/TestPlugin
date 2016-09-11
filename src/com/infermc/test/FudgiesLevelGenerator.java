package com.infermc.test;

import java.util.logging.Logger;

import com.mojang.minecraft.level.Level;
import com.mojang.minecraft.level.generator.LevelGenerator;
import com.mojang.minecraft.level.generator.noise.PerlinNoise;
import com.mojang.minecraft.level.tile.Block;

public final class FudgiesLevelGenerator extends LevelGenerator {
    private Logger log;
    private int width;
    private int depth;
    private int height;
    private byte[] blocks;
    private int waterLevel;

    public FudgiesLevelGenerator(Logger var1) {
        log = var1;
    }

    /**
     * Generates a level
     *
     * @param creator
     * @param width Width of the level
     * @param depth Height of the Level vertically
     * @param height Same as width
     * @return
     */
    public final Level generate(String creator, int width, int depth, int height) {

        PerlinNoise perlin = new PerlinNoise();

        log.info("Generating level");
        this.width = width;
        this.depth = depth;
        this.height = 64;
        waterLevel = 32;
        blocks = new byte[width * depth << 6];

        log.info("Soiling..");

        int z; //var 26
        int x; // var24
        int y; // var11
        int block_hash;
        int blok = 0;

        // Width
        for (x = 0; x < this.width; ++x) {
            // Depth
            for (y = 0; y < this.depth; ++y) {
                // Height
                for (z = 0; z < this.height; ++z) {
                    block_hash = (z * this.depth + y) * this.width + x;

                    if (z == this.waterLevel-1) {
                        blok = Block.GRASS.id;
                    }
                    if (z <= this.waterLevel-2) {
                        blok = Block.DIRT.id;
                    }
                    this.blocks[block_hash] = (byte) blok;
                    blok = 0; // Reset it.
                }
            }
        }

        int tree_amount = (int) Math.floor(width/2);
        for (int i=1; i!=tree_amount; i++) {
            int tx = (int) getRand(5,width-5);
            int tz = (int) getRand(5,width-5);
            blocks = place_tree(tx,tz,blocks);
        }


        Level level = new Level();
        level.waterLevel = waterLevel;
        level.setData(width, 64, depth, blocks);
        level.createTime = System.currentTimeMillis();
        level.creator = creator;
        level.name = "A Nice Flat World";

        return level;
    }
    public double getRand(int min, int max) {
        return Math.floor(Math.random() * (max - min + 1)) + min;
    }
    public byte[] place_tree(int x, int z, byte[] blocks) {
        int block_hash;
        block_hash = (z * this.depth + this.depth/2) * this.width + x;
        blocks[block_hash] = (byte) Block.WOOD.id;
        return blocks;
    }
}