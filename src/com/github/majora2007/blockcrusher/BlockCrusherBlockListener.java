package com.github.majora2007.blockcrusher;

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
 * @author Majora2007
 */
public class BlockCrusherBlockListener implements Listener {
	public static Block process = null;
	final int MAX_PUSH_DIST = 12; // Pistons push up to 12 blocks
	BlockCrusher plugin;
	
	public BlockCrusherBlockListener(final BlockCrusher plugin) {
		this.plugin = plugin;
	}
	
	private Boolean isBlockPowered(Block pistonBlock) {
		int powerBlockRecieves = 0;
		
		if (pistonBlock.isBlockPowered() || pistonBlock.isBlockIndirectlyPowered())
		{
			powerBlockRecieves = 1;
		}
		
		// if block behind us is powered and the piston is as well, then there is power
		if (powerBlockRecieves > 0 && pistonBlock.getBlockPower() > 0) {
			return true;
		} else {
			return false;
		}
	}

	private BlockFace getBlockFace(Block block) 
	{
		byte blockData = block.getData();
		
		switch (blockData)
		{
			case (0):
				return BlockFace.DOWN;
			case (1):
				return BlockFace.UP;
			case (2):
				return BlockFace.NORTH;
			case (3):
				return BlockFace.SOUTH;
			case (4):
				return BlockFace.WEST;
			case (5):
				return BlockFace.EAST;
			case (8):
				return BlockFace.DOWN;
			case (9):
				return BlockFace.UP;
			case (10):
				return BlockFace.NORTH;
			case (11):
				return BlockFace.SOUTH;
			case (12):
				return BlockFace.WEST;
			case (13):
				return BlockFace.EAST;
			default:
				return BlockFace.SELF;
		}
	}

	private boolean isUnpushableBlock(Block block)
	{	
		return (block.getType() == Material.OBSIDIAN) || (block.getType() == Material.BEDROCK);	
	}
	
	private boolean isBreakableBlock(Block block)
	{
		List<String> breakableBlocks = plugin.getConfig().getStringList("breakable_blocks");
		
		for (String str : breakableBlocks)
		{
			if (block.getTypeId() == Integer.parseInt(str))
			{	
				return true;
			}
		}
		
		return false;
	}

	/**
	 * From a block, check {@literal BlockCrusherBlockListener#MAX_PUSH_DIST} blocks along a face until 
	 * an unpushable block is found. Return the block before the unpushable block or null if a block is 
	 * <code>Material.AIR</code> or no unpushable block is found.
	 * 
	 * @param startingBlock The block that will be moved by the piston
	 * @param face The direction in which blocks should be checked.
	 * 
	 * @return The {@link Block} which will be broken. <code>null</code>  
	 */
	private Block findBreakableBlockAlongFace(Block startingBlock, BlockFace face) 
	{
		 if ( !isValidBlock(startingBlock) )
			 return null;
		 
		// The first block should not be considered, as it will only be pushed, never broken.
		Block currentBlock = getNextBlockAlongFace( startingBlock, face );
		Block previousBlock = startingBlock;	
		
		for (int i = 0; i < MAX_PUSH_DIST; i++)
		{
			if ( isUnpushableBlock(currentBlock) )
			{
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
		if (event.isCancelled())
		{
			return;
		}
		
		if ( !canBreakBlocks() ) return;

		Block pistonBlock = event.getBlock();
		BlockFace pistonFace = null;
		Block blockToBeMoved = null;
		Block blockBeingBroken = null;
		
		if ( isBlockPistonBase(pistonBlock) ) 
		{
			pistonFace = getBlockFace(pistonBlock);
			blockToBeMoved = getBlockToBePushed( pistonBlock, pistonFace );

			if (!pistonCanMoveBlock( blockToBeMoved )) return;
			
			blockBeingBroken = findBreakableBlockAlongFace(blockToBeMoved, pistonFace);
			breakBlock( blockBeingBroken );

		} 
	}

	private boolean pistonCanMoveBlock( Block blockToBeMoved )
	{
		if (!isValidBlock(blockToBeMoved)) return false;
		
		final PistonMoveReaction pistonMoveReaction = blockToBeMoved.getPistonMoveReaction();
		if ( (pistonMoveReaction == PistonMoveReaction.BLOCK) || (pistonMoveReaction == PistonMoveReaction.BREAK) || isUnpushableBlock( blockToBeMoved ) )
		{
			return false;
		}
		
		return true;
	}


	private void breakBlock( Block blockBeingBroken )
	{
		if ( isValidBlock(blockBeingBroken) ) 
		{
			breakBlockAndDropItems(blockBeingBroken);
		}
	}

	private Block getBlockToBePushed(Block pistonBase, BlockFace pistonFace)
	{
		Block blockToBeMoved = null;
	
		if (pistonFace != null) 
		{
			if (isBlockPowered(pistonBase)) 
			{
				blockToBeMoved = pistonBase.getRelative(pistonFace, 1);
			}
		}
		
		return blockToBeMoved;
	}
	
	private boolean canBreakBlocks()
	{
		final boolean breakBlocks = plugin.getConfig().getBoolean("settings.break_blocks", false);
		return breakBlocks;
	}
	
	private Block getNextBlockAlongFace(Block currentBlock, BlockFace face)
	{
		return currentBlock.getRelative(face);
	}
	
	private boolean isValidBlock(Block block)
	{
		return (block != null && block.getType() != Material.AIR && block.getType() != Material.PISTON_EXTENSION && block.getType() != Material.PISTON_MOVING_PIECE);
	}
	
	private void breakBlockAndDropItems(Block blockToBeBroken)
	{
		Collection<ItemStack> bDrops = blockToBeBroken.getDrops();
		
		for (ItemStack is : bDrops)
		{
			blockToBeBroken.getWorld().dropItemNaturally(blockToBeBroken.getLocation(), is);
		}
		

		blockToBeBroken.setType(Material.AIR); // This sets the broken block to AIR as dropItemNaturally doesn't do this for us

	}
	
	private boolean isBlockPistonBase(Block block)
	{
		assert (block != null);
		
		return ((block.getType() == Material.PISTON_BASE) || (block.getType() == Material.PISTON_STICKY_BASE));
	}
}