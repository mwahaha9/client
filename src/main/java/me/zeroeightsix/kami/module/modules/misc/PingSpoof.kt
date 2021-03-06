package me.zeroeightsix.kami.module.modules.misc

import me.zeroeightsix.kami.event.events.PacketEvent
import me.zeroeightsix.kami.module.Module
import me.zeroeightsix.kami.setting.ModuleConfig.setting
import net.minecraft.network.play.client.CPacketKeepAlive
import net.minecraft.network.play.server.SPacketKeepAlive
import org.kamiblue.event.listener.listener
import java.util.*

object PingSpoof : Module(
    name = "PingSpoof",
    category = Category.MISC,
    description = "Cancels or adds delay to your ping packets"
) {
    private val cancel = setting("Cancel", false) // most servers will kick/time you out for this
    private val delay = setting("Delay", 100, 0..2000, 25, { !cancel.value })

    init {
        listener<PacketEvent.Receive> {
            if (it.packet !is SPacketKeepAlive || mc.player == null) return@listener
            it.cancel()
            if (!cancel.value) {
                Timer().schedule(object : TimerTask() {
                    override fun run() {
                        mc.connection?.sendPacket(CPacketKeepAlive(it.packet.id))
                    }
                }, delay.value.toLong())
            }
        }
    }
}
