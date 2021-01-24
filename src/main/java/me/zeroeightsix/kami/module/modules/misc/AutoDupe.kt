
/*
package me.zeroeightsix.kami.module.modules.misc

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import me.zeroeightsix.kami.KamiMod
import me.zeroeightsix.kami.module.Module
import me.zeroeightsix.kami.setting.ModuleConfig.setting
import me.zeroeightsix.kami.util.items.*
import me.zeroeightsix.kami.util.TickTimer
import me.zeroeightsix.kami.util.TimeUnit
import me.zeroeightsix.kami.util.text.MessageDetection
import me.zeroeightsix.kami.util.text.MessageSendHelper
import me.zeroeightsix.kami.util.text.MessageSendHelper.sendServerMessage
import me.zeroeightsix.kami.util.threads.defaultScope
import me.zeroeightsix.kami.util.threads.safeListener
import net.minecraftforge.fml.common.gameevent.TickEvent
import java.io.File
import java.net.URL
import java.util.*
import kotlin.random.Random


object AutoDupe : Module(
    name = "AutoDupe",
    description = "automatically preforms the crafting dupe",
    category = Category.MISC,
    modulePriority = 100
) {
    private val timer = TickTimer(TimeUnit.SECONDS)
    private val timeOut = 5000L
    private var timeStamp = System.currentTimeMillis()
    //TODO stuff here

    private val currentItemStack = mc.player.inventory.getCurrentItem()
    private val currentItem = currentItemStack.item
    private val currentId = currentItem.id
    private val currentCount = currentItemStack.count
    
    private var phase = "START"


    init {
        onEnable {

            if(currentItemStack.isEmpty()) {
                MessageSendHelper.sendChatMessage("$chatName Failed dupe - empty slot.")
                disable()

            }else {
                MessageSendHelper.sendChatMessage("$chatName Attempting to dupe ${currentItemStack.displayName}")

        }

        safeListener<TickEvent.ClientTickEvent> {
            if (phase == "START") {

                MessageSendHelper.sendChatMessage("$chatName Dropping item...")

                player.dropItem(currentItemStack, true)

                phase = "DUPE"

            }
            if(phase == "DUPE"){


                if(currentItemStack.count > currentCount) {
                    phase = "DONE"

                }
            }
            if(phase == "DONE"){

                MessageSendHelper.sendChatMessage("$chatName Dupe Sucessful!")


            }




            }

        }
    }

}







*/





