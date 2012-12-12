package com.github.majora2007.blockcrusher;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.PistonMoveReaction;
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
	BlockCrusher plugin;
	private transient BlockFace[] blockFaces = new BlockFace[] { BlockFace.NORTH, BlockFace.SOUTH, BlockFace.EAST, BlockFace.WEST, BlockFace.UP, BlockFace.DOWN };

	public BlockCrusherBlockListener(final BlockCrusher plugin) {
		this.plugin = plugin;
	}
	
	private Boolean CheckRealPower(Block pistonBlock, BlockFace pistonFace) {
		int power = 0;
		for (BlockFace face : blockFaces) 
		{
			// if the test face is not the face we are facing and the test face is not air, get the blocks power 
			// and add it to power
			if (face != pistonFace && pistonBlock.getRelative(face).getType() != Material.AIR) {
				power += pistonBlock.getRelative(face).getBlockPower();
			}
		}
		
		// if block behind us is powered and the piston is as well, then there is power (NOTE: Does this need to be AND? Can't it be OR?)
		if (power > 0 && pistonBlock.getBlockPower() > 0) {
			return true;
		} else {
			return false;
		}
	}

	private BlockFace CheckFacing(byte blk) 
	{
		switch (blk)
		{
			case (0):
				return BlockFace.DOWN;
			case (1):
				return BlockFace.UP;
			case (2):
				return BlockFace.EAST;
			case (3):
				return BlockFace.WEST;
			case (4):
				return BlockFace.NORTH;
			case (5):
				return BlockFace.SOUTH;
			case (8):
				return BlockFace.DOWN;
			case (9):
				return BlockFace.UP;
			case (10):
				return BlockFace.EAST;
			case (11):
				return BlockFace.WEST;
			case (12):
				return BlockFace.NORTH;
			case (13):
				return BlockFace.SOUTH;
			default:
				return null;
		}
	}

	private boolean isMoveable(Block block)
	{
		// PistonMoveReaction does not work, it will cause exceptions to be thrown.
//		PistonMoveReaction pistonMoveReaction = block.getPistonMoveReaction();
//		if (pistonMoveReaction == PistonMoveReaction.BREAK || pistonMoveReaction == PistonMoveReaction.BLOCK)
//		{ 
//			return false;
//		}
//		return true;
		
		return (block.getType() == Material.OBSIDIAN) || (block.getType() == Material.BEDROCK);
		
	}
	// FIXME I found an issue. It breaks block if next to Obsidian, but no new block is being pushed into it.
	/**
	 * Call checkBreakable here to calculate the new block which will be broken along <code>face</code> up to <code>MAX_PUSH_DIST</code>.
	 * 
	 * @param possibleBreakableBlock
	 * @param face
	 * @return
	 */
	private Block checkBreakable(Block possibleBreakableBlock, BlockFace face) {
		
		// To check if breakable, need to move from current block along face until MAX_PUSH_DIST 
		// is met OR an unbreakable (47) block is found (must be bellow MAX_PUSH_DIST).
		
		breakBlocks = plugin.getConfig().getStringList("breakable_blocks");
		
		Block bBlock = possibleBreakableBlock;
		Block pBlock = null;
		boolean isAir = false;
		
		
		
		
//		if (bBlock.getPistonMoveReaction() == PistonMoveReaction.MOVE)
//		{
//			BlockCrusher.logAdd("bBlock can move.");
//		} else if (bBlock.getPistonMoveReaction() == PistonMoveReaction.BREAK)
//		{ 
//			BlockCrusher.logAdd("bBlock can break.");
//		} else
//			BlockCrusher.logAdd("bBlock cannot move.");
		
		
		for (int i = 0; i < MAX_PUSH_DIST; i++)
		{

			// Check each block until we find Obsidian or Bedrock
			if (isMoveable(bBlock) )
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
		if (event.isCancelled() || event.getBlock().getType() == Material.OBSIDIAN)
		{
			return;
		}
		
		boolean breakBlocks = plugin.getConfig().getBoolean("settings.break_blocks", false);
		
		
		if ( breakBlocks )
		{
			Block pistonBlock = event.getBlock();
			
			
			if (pistonBlock != process) // QUESTION Can this cause a problem?
			{
				process = pistonBlock; // process is the piston base
				Block blockToBeMoved = null;
				
				
				if ( isPistonBase(pistonBlock) ) 
				{
					BlockFace pistonFace = CheckFacing(pistonBlock.getData());
					
					if (pistonFace != null) 
					{
						// Checks if the piston is powered.
						if (CheckRealPower(pistonBlock, pistonFace)) {
							
							blockToBeMoved = pistonBlock.getRelative(pistonFace, 1);
							
							if ( isValidBlock(blockToBeMoved) )
							{
								blockToBeMoved = checkBreakable(blockToBeMoved, pistonFace);
							} else {
								process = null;
								return;
							}
							
							if ((blockToBeMoved.getType() != Material.AIR)) 
							{
								breakBlock(blockToBeMoved);
							}
						}
					}
				} 
				process = null;
			}
		}
	}
	
	boolean isValidBlock(Block blockToBeMoved)
	{
		return (blockToBeMoved.getType() != Material.AIR && blockToBeMoved.getType() != Material.PISTON_EXTENSION && blockToBeMoved.getType() != Material.PISTON_MOVING_PIECE);
	}
	void breakBlock(Block blockToBeMoved)
	{
		Collection<ItemStack> bDrops = blockToBeMoved.getDrops();
		
		for (ItemStack is : bDrops)
		{
			blockToBeMoved.getWorld().dropItemNaturally(blockToBeMoved.getLocation(), is);
		}
		
		//if (!pis.getType().equals(Material.PISTON_STICKY_BASE))
		//{
			blockToBeMoved.setType(Material.AIR); // DEBUG
		//}
	}
	
	boolean isPistonBase(Block block)
	{
		assert (block != null);
		
		return ((block.getType() == Material.PISTON_BASE) || (block.getType() == Material.PISTON_STICKY_BASE));
	}
}