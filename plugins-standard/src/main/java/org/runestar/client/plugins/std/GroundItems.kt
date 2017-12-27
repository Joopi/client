package org.runestar.client.plugins.std

import org.runestar.client.game.api.SceneTile
import org.runestar.client.game.api.live.Game
import org.runestar.client.game.api.live.LiveCanvas
import org.runestar.client.game.api.live.LiveViewport
import org.runestar.client.game.raw.Client
import org.runestar.client.game.raw.access.XItemDefinition
import org.runestar.client.plugins.PluginSettings
import org.runestar.client.utils.ColorForm
import org.runestar.client.utils.DisposablePlugin
import org.runestar.client.utils.FontForm
import java.awt.Font
import org.runestar.client.game.api.live.GroundItems as LiveGroundItems

class GroundItems : DisposablePlugin<GroundItems.Settings>() {

    companion object {
        const val MAX_QUANTITY = 65535
    }

    override val defaultSettings = Settings()

    val tiles = HashSet<SceneTile>()

    override fun start() {
        super.start()
        LiveGroundItems.getOnPlaneFlat(Game.plane).forEach { gi ->
            tiles.add(gi.location)
        }
        add(LiveGroundItems.pileChanges.subscribe { st ->
            tiles.add(st)
        })
        add(LiveGroundItems.pileRemovals.subscribe { st ->
            tiles.remove(st)
        })

        add(LiveCanvas.repaints.subscribe { g ->
            g.color = settings.color.get()
            g.font = settings.font.get()
            g.clip(LiveViewport.shape)
            val height = g.fontMetrics.height

            val itr = tiles.iterator()
            while (itr.hasNext()) {
                val tile = itr.next()
                if (tile.plane != Game.plane) continue
                val pt = tile.center.toScreen()
                if (pt == null || pt !in g.clip) continue
                val gis = LiveGroundItems.getAt(tile)
                if (gis.isEmpty()) {
                    itr.remove()
                    continue
                }
                val items = LinkedHashMap<XItemDefinition, Int>()
                for (i in gis.lastIndex.downTo(0)) {
                    val gi = gis[i]
                    val def = Client.accessor.getItemDefinition(gi.id)
                    if (def != null) {
                        items.merge(def, gi.quantity) { old, new -> old + new }
                    }
                }
                val x = pt.x
                var y = pt.y - settings.initialOffset
                for ((def, count) in items) {
                    val string = when {
                        count == 1 -> def.name
                        count >= MAX_QUANTITY -> def.name + " x Lots!"
                        else -> def.name + " x $count"
                    }
                    val width = g.fontMetrics.stringWidth(string)
                    val leftX = x - (width / 2)
                    g.drawString(string, leftX, y)
                    y -= height + settings.spacing

                }
            }
        })
    }

    override fun stop() {
        super.stop()
        tiles.clear()
    }

    class Settings : PluginSettings() {
        val color = ColorForm()
        val font = FontForm(Font.SANS_SERIF, FontForm.PLAIN, 13f)
        val spacing = -4
        val initialOffset = 9
    }
}