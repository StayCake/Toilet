import hazae41.minecraft.kutils.bukkit.msg
import hazae41.minecraft.kutils.get
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.TextComponent
import net.kyori.adventure.text.format.TextColor
import org.bukkit.ChatColor
import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.block.BlockFace
import org.bukkit.block.Sign
import org.bukkit.block.data.type.WallSign
import org.bukkit.configuration.file.FileConfiguration
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.block.Action
import org.bukkit.event.block.BlockBreakEvent
import org.bukkit.event.block.SignChangeEvent
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.event.player.PlayerQuitEvent
import org.bukkit.inventory.EquipmentSlot
import java.io.File

val Signs = listOf(
    Material.ACACIA_SIGN,
    Material.ACACIA_WALL_SIGN,
    Material.BIRCH_SIGN,
    Material.BIRCH_WALL_SIGN,
    Material.CRIMSON_SIGN,
    Material.CRIMSON_WALL_SIGN,
    Material.DARK_OAK_SIGN,
    Material.DARK_OAK_WALL_SIGN,
    Material.SPRUCE_SIGN,
    Material.SPRUCE_WALL_SIGN,
    Material.JUNGLE_SIGN,
    Material.JUNGLE_WALL_SIGN,
    Material.OAK_SIGN,
    Material.OAK_WALL_SIGN,
    Material.WARPED_SIGN,
    Material.WARPED_WALL_SIGN
)

class Events : Listener {

    private fun getSigns() : FileConfiguration {
        return Main.signs
    }

    private fun getSignsloc() : File {
        return Main.signsloc
    }

    private fun getYaw(side: BlockFace?) : Float {
        return when (side) {
            BlockFace.EAST -> -90F
            BlockFace.WEST -> 90F
            BlockFace.SOUTH -> 0F
            BlockFace.NORTH -> 180F
            else -> 0F
        }
    }

    @EventHandler
    private fun signPlace(e: SignChangeEvent) {
        val p = e.player
        val line1 = (e.line(0) as TextComponent).content()
        val line2 = (e.line(1) as TextComponent).content()
        val line3 = (e.line(2) as TextComponent).content()
        val line4 = (e.line(3) as TextComponent).content()
        var fn = 0
        val fs = line4.filter { it.isDigit() }
        if (fs != "") fn = fs.toInt()
        if (line1 == "[??????]" && p.hasPermission("admin.setup")) {
            if (line2 == "") {
                e.block.breakNaturally()
                p.msg("????????? ??????????????????.")
            } else {
                val bl = e.block.location
                when (line3) {
                    "??????" -> {
                        if (getSigns().getLocation("signs.$line2.join") != null) {
                            e.block.breakNaturally()
                            p.msg("?????? ?????????????????????!")
                        } else {
                            getSigns().set("signs.$line2.join", bl)
                            e.line(
                                0, Component.text("[??????]")
                                    .color(TextColor.color(85, 255, 255))
                            )
                            e.line(
                                1, Component.text(line2)
                                    .color(TextColor.color(255, 170, 0))
                            )
                            e.line(
                                2, Component.text("???????????? ????????????")
                                    .color(TextColor.color(85, 255, 85))
                            )
                            if (fn > 0 && line4 != "-") {
                                getSigns().set("signs.$line2.limit",fn)
                                e.line(
                                    3, Component.text("Lv.$line4 ??????")
                                )
                            } else if (line4 == "-") {
                                getSigns().set("signs.$line2.persistent",true)
                                e.line(
                                    3, Component.text("-")
                                )
                            }
                            p.msg("$line2 ?????? ?????? ??????!")
                        }
                    }
                    "??????" -> {
                        if (getSigns().getLocation("signs.$line2.quit") != null) {
                            e.block.breakNaturally()
                            p.msg("?????? ?????????????????????!")
                        } else {
                            getSigns().set("signs.$line2.quit",bl)
                            e.line(0, Component.text("[??????]")
                                .color(TextColor.color(85,255,255))
                            )
                            e.line(1, Component.text(line2)
                                .color(TextColor.color(255,170,0))
                            )
                            e.line(2, Component.text("???????????? ?????????")
                                .color(TextColor.color(255,85,85))
                            )
                            p.msg("$line2 ?????? ?????? ??????!")
                        }
                    }
                    else -> {
                        e.block.breakNaturally()
                        p.msg("????????? ????????? ???????????????. [????????? ???]")
                    }
                }
                getSigns().save(getSignsloc())
            }
        }
        else if (line1 == "[??????]" && p.hasPermission("admin.setup")) {
            if (line2 == "") {
                e.block.breakNaturally()
                p.msg("????????? ??????????????????.")
            } else {
                val bl = e.block.location
                when {
                    listOf("1","2").contains(line3) -> {
                        if (getSigns().getLocation("gates.$line2.join") != null) {
                            e.block.breakNaturally()
                            p.msg("?????? ?????????????????????!")
                        } else {
                            getSigns().set("gates.$line2.$line3", bl)
                            e.line(
                                0, Component.text("[??????]")
                                    .color(TextColor.color(85, 255, 255))
                            )
                            e.line(
                                1, Component.text(line2)
                                    .color(TextColor.color(255, 170, 0))
                            )
                            e.line(
                                2, Component.text(if (line3 == "1") "- ???????????? ???????????? -" else "???????????? ????????????")
                                    .color(TextColor.color(85, 255, 85))
                            )
                            if (fn > 0) {
                                getSigns().set("gates.$line2.limit",fn)
                                e.line(
                                    3, Component.text("Lv.$line4 ??????")
                                )
                            }
                            p.msg("$line2 ?????? $line3 ??? ?????? ??????!")
                        }
                    }
                    else -> {
                        e.block.breakNaturally()
                        p.msg("1??? ?????? 2?????? ???????????????.. [????????? ???]")
                    }
                }
                getSigns().save(getSignsloc())
            }
        }
    }

    @EventHandler
    private fun act1(e: BlockBreakEvent) {
        val p = e.player
        val b = e.block
        if (Signs.contains(b.type)) {
            val sign = b.state as Sign
            val line1 = (sign.line(0) as TextComponent).content()
            val line2 = (sign.line(1) as TextComponent).content()
            val line3 = (sign.line(2) as TextComponent).content()
            val line4 = (sign.line(3) as TextComponent).content()
            if (line1 == "[??????]" && p.hasPermission("admin.setup")) {
                when (line3) {
                    "???????????? ????????????" -> getSigns().set("signs.$line2.join", null)
                    "?????? ???" -> getSigns().set("signs.$line2.join", null)
                    "???????????? ?????????" -> getSigns().set("signs.$line2.quit", null)
                }
                if (line4 != "") {
                    getSigns().set("signs.$line2.limit",null)
                    getSigns().set("signs.$line2.persistent",null)
                }
                p.msg("?????? ??????!")
                getSigns().save(getSignsloc())
            }
            else if (line1 == "[??????]"&& p.hasPermission("admin.setup")) {
                getSigns().set("gates.$line2.${if (line3 == "???????????? ????????????") 2 else 1}",null)
                if (line4 != "") {
                    getSigns().set("gates.$line2.limit",null)
                }
                p.msg("?????? ??????!")
                getSigns().save(getSignsloc())
            }
        }
    }

    @EventHandler
    private fun act2(e: PlayerInteractEvent) {
        val p = e.player
        val clb = e.clickedBlock
        if (Signs.contains(clb?.type) && e.action == Action.RIGHT_CLICK_BLOCK && e.hand == EquipmentSlot.HAND) {
            val kspFile = Main.ksp.dataFolder["data"]["stats.yml"]
            if (!kspFile.canRead()) {
                p.msg("?????? ?????? ????????? ????????? ????????????.\n??????????????? ???????????????.")
            } else {
                Main.kspYml.load(kspFile)
            }
            val lv = Main.kspYml.getInt("${e.player.uniqueId}.MineLV")
            val sign = e.clickedBlock?.state as Sign
            val line1 = (sign.line(0) as TextComponent).content()
            val line2 = (sign.line(1) as TextComponent).content()
            val line3 = (sign.line(2) as TextComponent).content()
            val line4 = (sign.line(3) as TextComponent).content()
            var fn = 0
            val fs = line4.filter { it.isDigit() }
            if (fs != "") fn = fs.toInt()
            if (line1 == "[??????]") {
                when (line3) {
                    "???????????? ????????????" -> {
                        val warpl = getSigns().getLocation("signs.$line2.quit")
                        if (warpl != null) {
                            val wallsign = warpl.block.blockData as WallSign
                            val face = wallsign.facing
                            when {
                                line4 != "-" && lv >= fn -> {
                                    getSigns().set("joined.${p.uniqueId}", line2)
                                    sign.line(3, Component.text(e.player.name))
                                    p.teleportAsync(Location(warpl.world, warpl.x + 0.5, warpl.y, warpl.z + 0.5, getYaw(face), 0F))
                                    sign.line(
                                        2, Component.text("?????? ???")
                                            .color(TextColor.color(255, 85, 85))
                                    )
                                    sign.update()
                                }
                                line4 != "-" && lv < fn -> {
                                    p.msg("?????? ????????? ???????????? ???????????????.")
                                }
                                line4 == "-" -> {
                                    getSigns().set("joined.${p.uniqueId}", line2)
                                    p.teleportAsync(Location(warpl.world, warpl.x + 0.5, warpl.y, warpl.z + 0.5, getYaw(face), 0F))
                                }
                            }
                        } else {
                            p.msg("????????? ???????????? ?????? ????????????.")
                        }
                    }
                    "?????? ???" -> {
                        p.msg("?????? ??????????????????.")
                    }
                    "???????????? ?????????" -> {
                        getSigns().set("joined.${p.uniqueId}",null)
                        val warpl = getSigns().getLocation("signs.$line2.join")
                        val limit = getSigns().getInt("signs.$line2.limit")
                        val persistent = getSigns().getBoolean("signs.$line2.persistent")
                        if (warpl != null) {
                            val wallsign = warpl.block.blockData as WallSign
                            val face = wallsign.facing
                            val entrysign = warpl.block.state as Sign
                            entrysign.line(3, Component.text(""))
                            p.teleportAsync(Location(warpl.world,warpl.x+0.5,warpl.y,warpl.z+0.5, getYaw(face), 0F))
                            entrysign.line(2, Component.text("???????????? ????????????")
                                .color(TextColor.color(85,255,85))
                            )
                            if (limit > 0) entrysign.line(3, Component.text("Lv.$limit ??????"))
                            if (persistent) entrysign.line(3, Component.text("-"))
                            entrysign.update()
                        } else {
                            p.msg("????????? ???????????? ?????? ????????????.")
                        }
                    }
                }
                getSigns().save(getSignsloc())
            }
            else if (line1 == "[??????]"){
                val out = if (line3 == "???????????? ????????????") 1 else 2
                val warpl = getSigns().getLocation("gates.$line2.$out")
                when {
                    warpl != null && lv >= fn -> {
                        val wallsign = warpl.block.blockData as WallSign
                        val face = wallsign.facing
                        p.teleportAsync(Location(warpl.world, warpl.x + 0.5, warpl.y, warpl.z + 0.5, getYaw(face), 0F))
                    }
                    warpl != null && lv < fn -> {
                        p.msg("?????? ????????? ???????????? ???????????????.")
                    }
                    else -> {
                        p.msg("????????? ???????????? ?????? ????????????.")
                    }
                }
                getSigns().save(getSignsloc())
            }
        }
    }

    @EventHandler
    private fun quitprevent(e: PlayerQuitEvent) {
        val p = e.player
        val joined = getSigns().getString("joined.${p.uniqueId}")
        if (joined != null) {
            val entstate = getSigns().getLocation("signs.$joined.join")?.block?.state
            if (entstate != null) {
                val entsign = entstate as Sign
                entsign.line(3, Component.text(""))
                entsign.line(2, Component.text("???????????? ????????????")
                    .color(TextColor.color(85,255,85))
                )
                entsign.update()
            }
        }
    }

    @EventHandler
    private fun joinretrun(e: PlayerJoinEvent) {
        val p = e.player
        val joined = getSigns().getString("joined.${p.uniqueId}")
        if (joined != null) {
            val loc = getSigns().getLocation("signs.$joined.join")
            getSigns().set("joined.${p.uniqueId}",null)
            if (loc != null) {
                p.teleport(Location(loc.world,loc.x+0.5,loc.y,loc.z+0.5))
            } else {
                p.teleport(p.world.spawnLocation)
            }
            getSigns().save(getSignsloc())
            p.msg("${ChatColor.RED}?????? ?????? ??? ????????? ????????? ????????? ?????????!")
        }
    }
}