package me.zeroeightsix.kami.module.modules.render

import me.zeroeightsix.kami.event.Phase
import me.zeroeightsix.kami.event.events.ChunkEvent
import me.zeroeightsix.kami.event.events.PacketEvent
import me.zeroeightsix.kami.event.events.RenderEntityEvent
import me.zeroeightsix.kami.module.Module
import me.zeroeightsix.kami.setting.ModuleConfig.setting
import me.zeroeightsix.kami.util.Wrapper.minecraft
import me.zeroeightsix.kami.util.threads.safeListener
import net.minecraft.block.BlockSnow
import net.minecraft.client.entity.EntityOtherPlayerMP
import net.minecraft.client.renderer.Tessellator
import net.minecraft.client.renderer.vertex.DefaultVertexFormats
import net.minecraft.entity.item.*
import net.minecraft.entity.monster.EntityMob
import net.minecraft.entity.passive.IAnimals
import net.minecraft.init.Blocks
import net.minecraft.network.play.server.*
import net.minecraft.tileentity.*
import net.minecraft.util.ResourceLocation
import net.minecraft.util.math.BlockPos
import net.minecraftforge.fml.common.gameevent.TickEvent
import net.minecraftforge.registries.GameData
import org.kamiblue.event.listener.listener
import org.lwjgl.opengl.GL11

object NoRender : Module(
    name = "NoRender",
    category = Category.RENDER,
    description = "Ignore entity spawn packets"
) {

    private val packets = setting("CancelPackets", true)
    private val page = setting("Page", Page.OTHER)

    // Entities
    private val paint = setting("Paintings", false, { page.value == Page.ENTITIES })
    private val animals = setting("Animals", false, { page.value == Page.ENTITIES })
    private val mobs = setting("Mobs", false, { page.value == Page.ENTITIES })
    private val player = setting("Players", false, { page.value == Page.ENTITIES })
    private val sign = setting("Signs", false, { page.value == Page.ENTITIES })
    private val skull = setting("Heads", false, { page.value == Page.ENTITIES })
    private val armorStand = setting("ArmorStands", false, { page.value == Page.ENTITIES })
    private val endPortal = setting("EndPortals", false, { page.value == Page.ENTITIES })
    private val banner = setting("Banners", false, { page.value == Page.ENTITIES })
    private val itemFrame = setting("ItemFrames", false, { page.value == Page.ENTITIES })
    private val xp = setting("XP", false, { page.value == Page.ENTITIES })
    private val items = setting("Items", false, { page.value == Page.ENTITIES })
    private val crystal = setting("Crystals", false, { page.value == Page.ENTITIES })

    // Others
    val map = setting("Maps", false, { page.value == Page.OTHER })
    private val explosion = setting("Explosions", true, { page.value == Page.OTHER })
    val signText = setting("SignText", false, { page.value == Page.OTHER })
    val particles = setting("Particles", true, { page.value == Page.OTHER })
    private val falling = setting("FallingBlocks", true, { page.value == Page.OTHER })
    val beacon = setting("BeaconBeams", true, { page.value == Page.OTHER })
    val skylight = setting("SkyLightUpdates", true, { page.value == Page.OTHER })
    private val enchantingTable = setting("EnchantingBooks", true, { page.value == Page.OTHER })
    private val enchantingTableSnow = setting("EnchantTableSnow", false, { page.value == Page.OTHER })
    private val projectiles = setting("Projectiles", false, { page.value == Page.OTHER })
    private val lightning = setting("Lightning", true, { page.value == Page.OTHER })

    private enum class Page {
        OTHER, ENTITIES
    }

    private val kamiMap = ResourceLocation("kamiblue/kamimap.png")

    private val settingMap = mapOf(
        player to EntityOtherPlayerMP::class.java,
        xp to EntityXPOrb::class.java,
        paint to EntityPainting::class.java,
        enchantingTable to TileEntityEnchantmentTable::class.java,
        sign to TileEntitySign::class.java,
        skull to TileEntitySkull::class.java,
        falling to EntityFallingBlock::class.java,
        armorStand to EntityArmorStand::class.java,
        endPortal to TileEntityEndPortal::class.java,
        banner to TileEntityBanner::class.java,
        itemFrame to EntityItemFrame::class.java,
        items to EntityItem::class.java,
        crystal to EntityEnderCrystal::class.java
    )

    var entityList = HashSet<Class<*>>(); private set

    init {
        onEnable {
            updateList()
        }

        listener<PacketEvent.Receive> {
            if (lightning.value && it.packet is SPacketSpawnGlobalEntity ||
                explosion.value && it.packet is SPacketExplosion ||
                particles.value && it.packet is SPacketParticles ||
                packets.value && xp.value && it.packet is SPacketSpawnExperienceOrb ||
                packets.value && paint.value && it.packet is SPacketSpawnPainting
            ) it.cancel()

            if (it.packet is SPacketSpawnObject) {
                it.cancelled = when (it.packet.type) {
                    71 -> packets.value && itemFrame.value
                    78 -> packets.value && armorStand.value
                    51 -> packets.value && crystal.value
                    2 -> packets.value && items.value
                    70 -> packets.value && falling.value
                    else -> projectiles.value
                }
            }

            if (packets.value && it.packet is SPacketSpawnMob) {
                val entityClass = GameData.getEntityRegistry().getValue(it.packet.entityType).entityClass
                if (EntityMob::class.java.isAssignableFrom(entityClass)) {
                    if (mobs.value) it.cancel()
                } else if (IAnimals::class.java.isAssignableFrom(entityClass)) {
                    if (animals.value) it.cancel()
                }
            }
        }

        listener<RenderEntityEvent> {
            if (it.phase != Phase.PRE) return@listener

            if (entityList.contains(it.entity::class.java)
                || animals.value && it.entity !is EntityMob && it.entity is IAnimals
                || mobs.value && it.entity is EntityMob) {
                it.cancel()
            }
        }

        listener<ChunkEvent> {
            if (enchantingTableSnow.value) { // replaces enchanting tables with snow
                val blockState = Blocks.SNOW_LAYER.defaultState.withProperty(BlockSnow.LAYERS, 7)
                val xRange = it.chunk.x * 16..it.chunk.x * 16 + 15
                val zRange = it.chunk.z * 16..it.chunk.z * 16 + 15

                for (y in 0..256) for (x in xRange) for (z in zRange) {
                    val blockPos = BlockPos(it.chunk.x * 16 + x, y, it.chunk.z * 16 + z)
                    if (it.chunk.getBlockState(blockPos).block == Blocks.ENCHANTING_TABLE) {
                        it.chunk.setBlockState(blockPos, blockState)
                    }
                }
            }
        }

        safeListener<TickEvent.ClientTickEvent> {
            if (it.phase == TickEvent.Phase.END && items.value) {
                for (entity in world.loadedEntityList) {
                    if (entity !is EntityItem) continue
                    entity.setDead()
                }
            }
        }
    }

    fun renderFakeMap() {
        val tessellator = Tessellator.getInstance()
        val bufBuilder = tessellator.buffer
        minecraft.textureManager.bindTexture(kamiMap)

        bufBuilder.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX)
        bufBuilder.pos(0.0, 128.0, -0.009999999776482582).tex(0.0, 1.0).endVertex()
        bufBuilder.pos(128.0, 128.0, -0.009999999776482582).tex(1.0, 1.0).endVertex()
        bufBuilder.pos(128.0, 0.0, -0.009999999776482582).tex(1.0, 0.0).endVertex()
        bufBuilder.pos(0.0, 0.0, -0.009999999776482582).tex(0.0, 0.0).endVertex()

        tessellator.draw()
    }

    private fun updateList() {
        entityList = HashSet<Class<*>>().apply {
            settingMap.forEach {
                if (it.key.value) add(it.value)
            }
            // needed because there are 2 entities, the gateway and the portal
            if (endPortal.value) {
                add(TileEntityEndGateway::class.java)
            }
        }
    }

    init {
        val listener = { updateList() }
        settingList.forEach { it.listeners.add(listener) }
    }

}