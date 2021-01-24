/*
package me.zeroeightsix.kami.module.modules.combat

import me.zeroeightsix.kami.event.events.PacketEvent
import me.zeroeightsix.kami.manager.managers.CombatManager
import me.zeroeightsix.kami.module.Module
import me.zeroeightsix.kami.util.*
import me.zeroeightsix.kami.util.combat.CombatUtils
import me.zeroeightsix.kami.util.combat.CrystalUtils
import me.zeroeightsix.kami.util.text.MessageSendHelper
import me.zeroeightsix.kami.util.items.*
import net.minecraft.client.gui.inventory.GuiContainer
import net.minecraft.entity.item.EntityEnderCrystal
import net.minecraft.entity.monster.EntityMob
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.init.MobEffects
import net.minecraft.item.*
import net.minecraft.network.play.server.SPacketConfirmTransaction
import net.minecraft.potion.PotionUtils
import net.minecraftforge.fml.common.gameevent.InputEvent
import net.minecraftforge.fml.common.gameevent.TickEvent
import org.kamiblue.event.listener.listener
import org.lwjgl.input.Keyboard
import kotlin.math.ceil
import kotlin.math.max

@Module.Info(
    name = "HotBarManager",
    description = "Manages items in your hotbar",
    category = Module.Category.COMBAT
)
object hotBarManager : Module() {
    //private val type = register(Settings.enumBuilder(Type::class.java, "Type").withValue(Type.SLOTONE))

    //the inventory manager best loadout
    val hb1 = Item.getByNameOrId("diamond_sword")
    val hb2 = Item.getByNameOrId("obsidian")
    val hb3 = Item.getByNameOrId("end_crystal")
    val hb4 = Item.getByNameOrId("end_crystal")
    val hb5 = Item.getByNameOrId("enchanted_golden_apple")
    val hb6 = Item.getByNameOrId("diamond_pickaxe")
    val hb7 = Item.getByNameOrId("experience_bottle")
    val hb8 = Item.getByNameOrId("experience_bottle")
    val hb9 = Item.getByNameOrId("chorus_fruit")

    val hotbar = arrayOf(hb1,hb2,hb3,hb4,hb5,hb6,hb7,hb8,hb9)


    // General

    private enum class Type(val filter: (ItemStack) -> Boolean) {

         SLOTONE({ it.item.id == 276 }),
         SLOTTWO({ it.item.id == 49 }),
         SLOTTHREE({ it.item.id == 426 }),
         SLOTFOUR({ it.item.id == 426 }),
         SLOTFIVE({ it.item is ItemAppleGold }),
         SLOTSIX({ it.item.id == 278 }),
         SLOTSEVEN({ it.item is ItemExpBottle }),
         SLOTEIGHT({ it.item is ItemExpBottle }),
         SLOTNINE({ it.item.id == 432 }),



    }

    private val transactionLog = HashMap<Short, Boolean>()
    private val movingTimer = TickTimer()
    private var maxDamage = 0f

    init {

        listener<PacketEvent.Receive> {
            if (mc.player == null || it.packet !is SPacketConfirmTransaction || it.packet.windowId != 0 || !transactionLog.containsKey(it.packet.actionNumber)) return@listener
            transactionLog[it.packet.actionNumber] = it.packet.wasAccepted()
            if (!transactionLog.containsValue(false)) movingTimer.reset(-175L) // If all the click packets were accepted then we reset the timer for next moving
        }

        listener<TickEvent.ClientTickEvent>(1100) {
            if (mc.player.isDead || !movingTimer.tick(200L, false)) return@listener // Delays 4 ticks by default
            if (!mc.player.inventory.itemStack.isEmpty) { // If player is holding an in inventory
                if (mc.currentScreen is GuiContainer) {// If inventory is open (playing moving item)
                    movingTimer.reset() // delay for 5 ticks
                } else { // If inventory is not open (ex. inventory desync)
                    InventoryUtils.removeHoldingItem()
                }
            } else { // If player is not holding an item in inventory
                switchToType(getType(), true)
            }
        }
    }

    private fun getType() = when {
        1==1 -> Type.SLOTONE
        else -> null
    }

    private fun switchToType(type1: Type?, alternativeType: Boolean = false) {
        // First check for whether player is holding the right item already or not

            val sublist = mc.player.inventoryContainer.inventory.subList(36, 44)
            val slot1 = mc.player.inventoryContainer.inventory.get(36).item
            val slot2 = mc.player.inventoryContainer.inventory.get(37).item
            val slot3 = mc.player.inventoryContainer.inventory.get(38).item
            val slot4 = mc.player.inventoryContainer.inventory.get(39).item
            val slot5 = mc.player.inventoryContainer.inventory.get(40).item
            val slot6 = mc.player.inventoryContainer.inventory.get(41).item
            val slot7 = mc.player.inventoryContainer.inventory.get(42).item
            val slot8 = mc.player.inventoryContainer.inventory.get(43).item
            val slot9 = mc.player.inventoryContainer.inventory.get(44).item

            if( slot1 != hb1) {
                //getslot()
                getItemSlot(Type.SLOTONE)?.let { (slot, type2) ->
                    transactionLog.clear()
                    transactionLog.putAll(InventoryUtils.moveToSlot(0, slot, 36).associate { it to false })
                    mc.playerController.updateController()
                    movingTimer.reset()
                    if (switchMessage.value) MessageSendHelper.sendChatMessage("$chatName Changed 1st slot to ${slot1.toString().toLowerCase()}!")
                }
            }
            if( slot2 != hb2) {
                //getslot()
                getItemSlot(Type.SLOTTWO)?.let { (slot, type2) ->
                    transactionLog.clear()
                    transactionLog.putAll(InventoryUtils.moveToSlot(0, slot, 37).associate { it to false })
                    mc.playerController.updateController()
                    movingTimer.reset()
                    if (switchMessage.value) MessageSendHelper.sendChatMessage("$chatName Changed 2nd slot to ${slot2.toString().toLowerCase()}!")
                }
            }
        if( slot3 != hb3) {
            //getslot()
            getItemSlot(Type.SLOTTHREE)?.let { (slot, type2) ->
                transactionLog.clear()
                transactionLog.putAll(InventoryUtils.moveToSlot(0, slot, 38).associate { it to false })
                mc.playerController.updateController()
                movingTimer.reset()
                if (switchMessage.value) MessageSendHelper.sendChatMessage("$chatName Changed 3nd slot to ${slot3.toString().toLowerCase()}!")
            }
        }
        if( slot4 != hb4) {
            //getslot()
            getItemSlot(Type.SLOTFOUR)?.let { (slot, type2) ->
                transactionLog.clear()
                transactionLog.putAll(InventoryUtils.moveToSlot(0, slot, 39).associate { it to false })
                mc.playerController.updateController()
                movingTimer.reset()
                if (switchMessage.value) MessageSendHelper.sendChatMessage("$chatName Changed 4th slot to ${slot4.toString().toLowerCase()}!")
            }
        }
        if( slot5 != hb5) {
            //getslot()
            getItemSlot(Type.SLOTFIVE)?.let { (slot, type2) ->
                transactionLog.clear()
                transactionLog.putAll(InventoryUtils.moveToSlot(0, slot, 40).associate { it to false })
                mc.playerController.updateController()
                movingTimer.reset()
                if (switchMessage.value) MessageSendHelper.sendChatMessage("$chatName Changed 5th slot to ${slot5.toString().toLowerCase()}!")
            }
        }
        if( slot6 != hb6) {
            //getslot()
            getItemSlot(Type.SLOTSIX)?.let { (slot, type2) ->
                transactionLog.clear()
                transactionLog.putAll(InventoryUtils.moveToSlot(0, slot, 41).associate { it to false })
                mc.playerController.updateController()
                movingTimer.reset()
                if (switchMessage.value) MessageSendHelper.sendChatMessage("$chatName Changed 6th slot to ${slot6.toString().toLowerCase()}!")
            }
        }
        if( slot7 != hb7) {
            //getslot()
            getItemSlot(Type.SLOTSEVEN)?.let { (slot, type2) ->
                transactionLog.clear()
                transactionLog.putAll(InventoryUtils.moveToSlot(0, slot, 42).associate { it to false })
                mc.playerController.updateController()
                movingTimer.reset()
                if (switchMessage.value) MessageSendHelper.sendChatMessage("$chatName Changed 7th slot to ${slot7.toString().toLowerCase()}!")
            }
        }
        if( slot8 != hb8) {
            //getslot()
            getItemSlot(Type.SLOTEIGHT)?.let { (slot, type2) ->
                transactionLog.clear()
                transactionLog.putAll(InventoryUtils.moveToSlot(0, slot, 43).associate { it to false })
                mc.playerController.updateController()
                movingTimer.reset()
                if (switchMessage.value) MessageSendHelper.sendChatMessage("$chatName Changed 8th slot to ${slot8.toString().toLowerCase()}!")
            }
        }
        if( slot9 != hb9) {
            //getslot()
            getItemSlot(Type.SLOTNINE)?.let { (slot, type2) ->
                transactionLog.clear()
                transactionLog.putAll(InventoryUtils.moveToSlot(0, slot, 44).associate { it to false })
                mc.playerController.updateController()
                movingTimer.reset()
                if (switchMessage.value) MessageSendHelper.sendChatMessage("$chatName Changed 9th slot to ${slot9.toString().toLowerCase()}!")
            }
        }







        /*
        if (type1 != null && !checkOffhandItem(type1)) getItemSlot(type1)?.let { (slot, type2) ->
            // Second check is for case of when player ran out of the original type of item
            if ((!alternativeType && type2 != type1) || slot == 45 || checkOffhandItem(type2)) return@let
            transactionLog.clear()
            transactionLog.putAll(InventoryUtils.moveToSlot(0, slot, 45).associate { it to false })
            mc.playerController.updateController()
            movingTimer.reset()
            if (switchMessage.value) MessageSendHelper.sendChatMessage("$chatName hotbar managed!")
        }
        */

    }


    private fun checkOffhandItem(type: Type) = type.filter(mc.player.heldItemOffhand)

    private fun getItemSlot(type: Type, loopTime: Int = 1): Pair<Int, Type>? = getSlot(type)?.to(type)
        ?: if (loopTime <= 3) getItemSlot(getNextType(type), loopTime + 1)
        else null

    private fun getSlot(type: Type): Int? {
        val sublist = mc.player.inventoryContainer.inventory.subList(9, 46)

        // 9 - 35 are main inventory, 36 - 44 are hotbar. So finding last one will result in prioritize hotbar
        val slot =  sublist.indexOfFirst(type.filter)

        // Add 9 to it because it is the sub list's index
        return if (slot != -1) slot + 9 else null
    }

    private fun getNextType(type: Type) = with(Type.values()) { this[(type.ordinal + 1) % this.size] }



    private val nextFallDist get() = mc.player.fallDistance - mc.player.motionY.toFloat()
}

 */