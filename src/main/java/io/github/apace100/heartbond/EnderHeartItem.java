package io.github.apace100.heartbond;

import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.Hand;
import net.minecraft.util.Pair;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.UseAction;
import net.minecraft.world.World;
import org.apache.commons.lang3.tuple.ImmutableTriple;
import top.theillusivec4.curios.api.CuriosApi;
import top.theillusivec4.curios.api.type.inventory.ICurioStacksHandler;
import top.theillusivec4.curios.api.type.inventory.IDynamicStackHandler;
import top.theillusivec4.curios.common.inventory.CurioStacksHandler;

import java.util.Optional;
import java.util.UUID;

public class EnderHeartItem extends Item {

    public EnderHeartItem(Settings settings) {
        super(settings);
    }

    @Override
    public void onCraft(ItemStack stack, World world, PlayerEntity player) {
        stack.getOrCreateTag().putUuid("HeartUUID", UUID.randomUUID());
    }

    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        ItemStack itemStack = user.getStackInHand(hand);
        Optional<ICurioStacksHandler> stacksHandlerOptional = CuriosApi.getCuriosHelper().getCuriosHandler(user).flatMap(curiosHandler -> curiosHandler.getStacksHandler("ender_heart"));
        if(stacksHandlerOptional.isPresent()) {
            ICurioStacksHandler stacksHandler = stacksHandlerOptional.get();
            IDynamicStackHandler sh = stacksHandler.getStacks();
            for (int i = 0; i < sh.size(); i++) {
                if (sh.getStack(i).isEmpty()) {
                    sh.setStack(i, itemStack.copy());
                    user.playSound(SoundEvents.ITEM_ARMOR_EQUIP_GENERIC, 1F, 1F);
                    itemStack.setCount(0);
                    return TypedActionResult.success(itemStack, world.isClient());
                }
            }
        }
        return TypedActionResult.fail(itemStack);
    }
}
