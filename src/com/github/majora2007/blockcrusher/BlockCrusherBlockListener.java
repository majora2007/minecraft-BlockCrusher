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
 * @author Majora2007
 */
public class BlockCrusherBlockListener implements Listener {
	public static Block process = null;
	final int MAX_PUSH_DIST = 12; // Pistons push up to 12 blocks
	List<String> breakBlocks = new ArrayList<String>();
	BlockCrusher plugin;
	
	public BlockCrusherBlockListener(final BlockCrusher plugin) {
		this.plugin = plugin;
	}
	
	private Boolean isBlockPowered(Block pistonBlock, BlockFace pistonFace) {
		int powerBlockRecieves = 0;
		
		if (pistonBlock.isBlockPowered() || pistonBlock.isBlockIndirectlyPowered())
		{
			powerBlockRecieves = 1;
		}
		
		// if block behind us is powered and the piston is as well, then there is power (NOTE: Does this need to be AND? Can't it be OR?)
		if (powerBlockRecieves > 0 && pistonBlock.getBlockPower() > 0) {
			return true;
		} else {
			return false;
		}
	}

	private BlockFace getBlockFace(byte blockData) 
	{
		switch (blockData)
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

	private boolean isUnpushableBlock(Block block)
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
	
	private boolean isBreakableBlock(Block block)
	{
		breakBlocks = plugin.getConfig().getStringList("breakable_blocks");
		
		for (String str : breakBlocks)
		{
			if (block.getTypeId() == Integer.parseInt(str))
			{	
				return true;
			}
		}
		
		return false;
	}

	/**
	 * Call checkBreakable here to calculate the new block which will be broken along <code>face</code> up to <code>MAX_PUSH_DIST</code>.
	 * 
	 * @param possibleBreakableBlock
	 * @param face
	 * @return
	 */
	private Block findBreakableBlockAlongFace(Block possibleBreakableBlock, BlockFace face) 
	{
		// The first block should not be considered, as it will only be pushed, never broken.
		Block currentBlock = possibleBreakableBlock.getRelative(face);
		Block previousBlock = possibleBreakableBlock;	
		
		for (int i = 0; i < MAX_PUSH_DIST; i++)
		{
			if ( isUnpushableBlock(currentBlock) )
			{
				// Set bBlock to block before un-pushable block
				currentBlock = previousBlock;
				
				if (isBreakableBlock(currentBlock))
				{
					return currentBlock;
				}
			} else
			{
				// Check if an AIR Block exists; exit early
				if (currentBlock.getType() == Material.AIR)
				{
					return null;
				}
				
				previousBlock = currentBlock;
				currentBlock = getNextBlockAlongFace(currentBlock, face);
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
	@EventHandler(priority=EventPriority.LOWEST)
	public void onPistonEvent(BlockPhysicsEvent event)
	{
		if (event.isCancelled() || event.getBlock().getType() == Material.OBSIDIAN)
		{
			return;
		}
		
		boolean breakBlocks = plugin.getConfig().getBoolean("settings.break_blocks", false);
		
		if ( breakBlocks )
		{
			Block pistonBlock = event.getBlock();
			Block blockToBeMoved = null;
			
			
			if ( isPistonBase(pistonBlock) ) 
			{
				BlockFace pistonFace = getBlockFace(pistonBlock.getData());
				
				if (pistonFace != null) 
				{
					// Checks if the piston is powered.
					if (isBlockPowered(pistonBlock, pistonFace)) {
						
						blockToBeMoved = pistonBlock.getRelative(pistonFace, 1);
						
						if ( isValidBlock(blockToBeMoved) )
						{
							blockToBeMoved = findBreakableBlockAlongFace(blockToBeMoved, pistonFace);
						} else {
							return;
						}
						
						if (blockToBeMoved != null && (blockToBeMoved.getType() != Material.AIR)) 
						{
							breakBlock(blockToBeMoved);
						}
					}
				}
			} 
		}
	}
	
	private Block getNextBlockAlongFace(Block currentBlock, BlockFace face)
	{
		return currentBlock.getRelative(face);
	}
	
	private boolean isValidBlock(Block blockToBeMoved)
	{
		return (blockToBeMoved.getType() != Material.AIR && blockToBeMoved.getType() != Material.PISTON_EXTENSION && blockToBeMoved.getType() != Material.PISTON_MOVING_PIECE);
	}
	
	private void breakBlock(Block blockToBeMoved)
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
	
	private boolean isPistonBase(Block block)
	{
		assert (block != null);
		
		return ((block.getType() == Material.PISTON_BASE) || (block.getType() == Material.PISTON_STICKY_BASE));
	}
}