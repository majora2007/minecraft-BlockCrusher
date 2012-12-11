package com.github.majora2007.blockcrusher;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPhysicsEvent;
import org.bukkit.event.block.BlockPistonEvent;
import org.bukkit.inventory.ItemStack;

/**
 * TODO: Fix Redstone Teleporters blocking this listener
 * 
 * @author Majora2007
 */
public class BlockCrusherBlockListener implements Listener {
	public static Block process = null;
	final int MAX_PUSH_DIST = 13; // Pistons push up to 12 blocks
	List<String> breakBlocks = new ArrayList<String>();
	boolean isPistonSticky;

	public BlockCrusherBlockListener(final BlockCrusher plugin) {
	}
	
	private Boolean CheckRealPower(Block blk, BlockFace pis) {
		BlockFace[] blockFaces = new BlockFace[] { BlockFace.NORTH, BlockFace.SOUTH,
				BlockFace.EAST, BlockFace.WEST, BlockFace.UP, BlockFace.DOWN };
		
		int pwr = 0;
		for (BlockFace face : blockFaces) {
			if (face != pis && blk.getRelative(face).getType() != Material.AIR) {
				pwr += blk.getRelative(face).getBlockPower();
			}
		}
		if (pwr > 0 && blk.getBlockPower() > 0) {
			return true;
		} else {
			return false;
		}
	}

	private BlockFace CheckFacing(byte blk) {
		if (blk == 0) {
			return BlockFace.DOWN;
		} else if (blk == 1) {
			return BlockFace.UP;
		} else if (blk == 2) {
			return BlockFace.EAST;
		} else if (blk == 3) {
			return BlockFace.WEST;
		} else if (blk == 4) {
			return BlockFace.NORTH;
		} else if (blk == 5) {
			return BlockFace.SOUTH;
		} else if (blk == 8) {
			return BlockFace.DOWN;
		} else if (blk == 9) {
			return BlockFace.UP;
		} else if (blk == 10) {
			return BlockFace.EAST;
		} else if (blk == 11) {
			return BlockFace.WEST;
		} else if (blk == 12) {
			return BlockFace.NORTH;
		} else if (blk == 13) {
			return BlockFace.SOUTH;
		} else {
			return null;
		}
	}

	
	// FIXME I found an issue. It breaks block if next to Obsidian, but no new block is being pushed into it.
	private Block checkBreakable(Block blk, BlockFace face) {
		
		// To check if breakable, need to move from current block along face until MAX_PUSH_DIST 
		// is met OR an unbreakable (47) block is found (must be bellow MAX_PUSH_DIST).
		
		breakBlocks = BlockCrusher.config.getStringList("breakable_blocks");
		
		Block bBlock = blk;
		Block pBlock = null;
		boolean isAir = false;
		
		/*if (bBlock.getPistonMoveReaction() == PistonMoveReaction.MOVE)
		{
			BlockCrusher.logAdd("bBlock can move.");
		} else
			BlockCrusher.logAdd("bBlock cannot move.");*/
		
		
		
		for (int i = 0; i < MAX_PUSH_DIST; i++)
		{
			//BlockCrusher.logAdd("Block[" + i + "] = " + bBlock.getType().name());
			// Check each block until we find Obsidian or Bedrock
			if ( (bBlock.getType() == Material.OBSIDIAN) // TODO Replace check with PistonMoveREation.BLOCK
					|| (bBlock.getType() == Material.BEDROCK) )
			//if (bBlock.getPistonMoveReaction() == PistonMoveReaction.BLOCK)
			{
				// The first block is unbreakable
				if (pBlock == null) 
				{
					//BlockCrusher.logAdd("First Block (" + bBlock.getType().name() + ") is unbreakable.");
					return null;
				}
				else
				{
					// Set bBlock to block before unmovable block
					//BlockCrusher.logAdd("bBlock -> pBlock: " + bBlock.getType().name() + "->" + pBlock.getType().name());
					bBlock = pBlock;
					
					// Ensure bBlock is a "breakable_block"
					for (String str : breakBlocks)
					{
						if (bBlock.getTypeId() == Integer.parseInt(str))
						{	
							//BlockCrusher.logAdd("Block " + bBlock.getType().name() + " can be broken.");
							if (isAir)
							{
								// Check that there is no air blocks 
								return null;
							}
							else
								return bBlock;
						}
					}
					
				}
				
			} else
			{
				// Check if an AIR Block exists
				if (bBlock.getType() == Material.AIR)
				{
					isAir = true;
				}
				pBlock = bBlock;
				bBlock = bBlock.getRelative(face); 
			}
		}

		return null;
	}
	
	
	/***
	 * Upon a <code>BlockPistonExtendEvent</code>, check to see if the blocks being moved are pushed against 
	 * Obsidian or Bedrock. If so, break the block. If not, the pistons and blocks behave normally.
	 * 
	 * @param event 
	 * @see BlockPistonEvent
	 */
	@EventHandler(priority=EventPriority.HIGHEST)
	public void onPistonEvent(BlockPhysicsEvent event)
	{
		//TODO QUESTION Should I cancel the event so I handle say?
		if (event.isCancelled())
		{
			return;
		}
		
		
		
		if ( BlockCrusher.config.getBoolean("settings.break_blocks", false) )
		{
			Block pis = event.getBlock();
			if (pis != process) // QUESTION Can this cause a problem?
			{
				process = pis; // process is the block being pushed (JVM: It's actually PISTON_BASE)
				Block bBlock = null;
				
				
				if ((pis.getType() == Material.PISTON_BASE) || (pis.getType() == Material.PISTON_STICKY_BASE)) 
				{
					BlockFace face = CheckFacing(pis.getData());
					
					if (face != null) 
					{
						if (CheckRealPower(pis, face)) {
							bBlock = pis.getRelative(face, 1);
							//BlockCrusher.logAdd("[-]bBlock = " + bBlock.getType().name());
							
							// Call checkBreakable here to calc an ItemStack along <code>face</code> 
							// up to <code>MAX_PUSH_DIST</code>.
							if (bBlock.getType() != Material.AIR
									&& bBlock.getType() != Material.PISTON_EXTENSION
									&& bBlock.getType() != Material.PISTON_MOVING_PIECE)
							{
								bBlock = checkBreakable(bBlock, face);
							} else
								bBlock = null;
					
							if ((bBlock != null) && (bBlock.getType() != Material.AIR)) 
							{
									Collection<ItemStack> bDrops = bBlock.getDrops();
									
									for (ItemStack is : bDrops)
									{
										bBlock.getWorld().dropItemNaturally(bBlock.getLocation(), is);
									}
									
									//if (!pis.getType().equals(Material.PISTON_STICKY_BASE))
									//{
										bBlock.setType(Material.AIR); // DEBUG
									//}
							}
						}
					}
				}
				process = null;
			}
		}
	}
}