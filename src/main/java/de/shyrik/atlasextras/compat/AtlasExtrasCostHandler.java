package de.shyrik.atlasextras.compat;

import de.shyrik.atlasextras.Configuration;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;

public class AtlasExtrasCostHandler implements TravelHandler.ICostHandler {

    @Override
    public boolean tryPay(EntityPlayer player, BlockPos destination) {
        double distance = player.getPosition().getDistance(destination.getX(), destination.getY(), destination.getZ());
        if (Configuration.COSTPROVIDER.maxTravelDistance <= distance) {
            switch (Configuration.COSTPROVIDER.costUnit) {
                case HUNGER:
                    int hungerCost = Configuration.COSTPROVIDER.blocksPerUnit == 0 ? 1 : (int) (distance / Configuration.COSTPROVIDER.blocksPerUnit);
                    if (player.getFoodStats().getFoodLevel() + player.getFoodStats().getSaturationLevel() > hungerCost) {
                        player.getFoodStats().addStats(-1 * hungerCost, 0F);
                        return true;
                    }
                    break;
                case XP:
                    int xpCost = Configuration.COSTPROVIDER.blocksPerUnit == 0 ? Configuration.COSTPROVIDER.xpAmount : (int) (distance / Configuration.COSTPROVIDER.blocksPerUnit);
                    if (player.experienceTotal > xpCost) {
                        player.addExperience(xpCost * -1);
                        return true;
                    }
                    break;
                case ITEM:
                    int itemCost = Configuration.COSTPROVIDER.blocksPerUnit == 0 ? 1 : (int) (distance / Configuration.COSTPROVIDER.blocksPerUnit);
                    int playerItemCount = 0;
                    for (ItemStack now : player.inventory.mainInventory) {
                        if (ItemStack.areItemsEqual(now, Configuration.COSTPROVIDER.itemCost)) {
                            playerItemCount += now.getCount();
                        }
                    }
                    if (playerItemCount > itemCost) {
                        ItemStack goaway = Configuration.COSTPROVIDER.itemCost;
                        player.inventory.clearMatchingItems(goaway.getItem(), goaway.getMetadata(), playerItemCount, null);
                        return true;
                    }
                    break;
                case NOTHING:
                    return true;
            }
        }
        return false;
    }
}
