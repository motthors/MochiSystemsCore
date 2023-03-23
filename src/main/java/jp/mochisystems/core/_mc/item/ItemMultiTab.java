package jp.mochisystems.core._mc.item;

import jp.mochisystems.core._mc._core._Core;
import net.minecraft.block.Block;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;

public class ItemMultiTab{

    public static class _Item extends Item {
        public CreativeTabs[] getCreativeTabs()
        {
            return _Core.Instance.TabGroup;
        }
    }

    public static class _ItemBlock extends ItemBlock {
        public CreativeTabs[] getCreativeTabs()
        {
            return _Core.Instance.TabGroup;
        }
        public _ItemBlock(Block block) {
            super(block);
        }
    }
}

