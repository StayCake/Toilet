import hazae41.minecraft.kutils.bukkit.msg
import hazae41.minecraft.kutils.get
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.TextComponent
import net.kyori.adventure.text.format.TextColor
import org.bukkit.Bukkit
import org.bukkit.ChatColor
import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.block.Sign
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
        if (line1 == "[광산]" && p.hasPermission("admin.setup")) {
            if (line2 == "") {
                e.block.breakNaturally()
                p.msg("이름을 지정해주세요.")
            } else {
                val bl = e.block.location
                when (line3) {
                    "입구" -> {
                        if (getSigns().getLocation("signs.$line2.join") != null) {
                            e.block.breakNaturally()
                            p.msg("이미 지정되었습니다!")
                        } else {
                            getSigns().set("signs.$line2.join", bl)
                            e.line(
                                0, Component.text("[광산]")
                                    .color(TextColor.color(85, 255, 255))
                            )
                            e.line(
                                1, Component.text(line2)
                                    .color(TextColor.color(255, 170, 0))
                            )
                            e.line(
                                2, Component.text("클릭해서 입장하기")
                                    .color(TextColor.color(85, 255, 85))
                            )
                            if (fn > 0 && line4 != "-") {
                                getSigns().set("signs.$line2.limit",fn)
                                e.line(
                                    3, Component.text("Lv.$line4 이상")
                                )
                            } else if (line4 == "-") {
                                getSigns().set("signs.$line2.persistent",true)
                                e.line(
                                    3, Component.text("-")
                                )
                            }
                            p.msg("$line2 입구 생성 완료!")
                        }
                    }
                    "출구" -> {
                        if (getSigns().getLocation("signs.$line2.quit") != null) {
                            e.block.breakNaturally()
                            p.msg("이미 지정되었습니다!")
                        } else {
                            getSigns().set("signs.$line2.quit",bl)
                            e.line(0, Component.text("[광산]")
                                    .color(TextColor.color(85,255,255))
                            )
                            e.line(1, Component.text(line2)
                                    .color(TextColor.color(255,170,0))
                            )
                            e.line(2, Component.text("클릭해서 나가기")
                                    .color(TextColor.color(255,85,85))
                            )
                            p.msg("$line2 출구 생성 완료!")
                        }
                    }
                    else -> {
                        e.block.breakNaturally()
                        p.msg("입구나 출구를 적어주세요. [세번째 줄]")
                    }
                }
                getSigns().save(getSignsloc())
            }
        }
        else if (line1 == "[이동]" && p.hasPermission("admin.setup")) {
            if (line2 == "") {
                e.block.breakNaturally()
                p.msg("이름을 지정해주세요.")
            } else {
                val bl = e.block.location
                when {
                    listOf("1","2").contains(line3) -> {
                        if (getSigns().getLocation("gates.$line2.join") != null) {
                            e.block.breakNaturally()
                            p.msg("이미 지정되었습니다!")
                        } else {
                            getSigns().set("gates.$line2.$line3", bl)
                            e.line(
                                0, Component.text("[이동]")
                                    .color(TextColor.color(85, 255, 255))
                            )
                            e.line(
                                1, Component.text(line2)
                                    .color(TextColor.color(255, 170, 0))
                            )
                            e.line(
                                2, Component.text(if (line3 == "1") "- 클릭해서 이동하기 -" else "클릭해서 이동하기")
                                    .color(TextColor.color(85, 255, 85))
                            )
                            if (fn > 0) {
                                getSigns().set("gates.$line2.limit",fn)
                                e.line(
                                    3, Component.text("Lv.$line4 이상")
                                )
                            }
                            p.msg("$line2 통로 $line3 번 생성 완료!")
                        }
                    }
                    else -> {
                        e.block.breakNaturally()
                        p.msg("1번 또는 2번을 적어주세요.. [세번째 줄]")
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
            if (line1 == "[광산]" && p.hasPermission("admin.setup")) {
                when (line3) {
                    "클릭해서 입장하기" -> getSigns().set("signs.$line2.join", null)
                    "사용 중" -> getSigns().set("signs.$line2.join", null)
                    "클릭해서 나가기" -> getSigns().set("signs.$line2.quit", null)
                }
                if (line4 != "") {
                    getSigns().set("signs.$line2.limit",null)
                    getSigns().set("signs.$line2.persistent",null)
                }
                p.msg("제거 완료!")
                getSigns().save(getSignsloc())
            }
            else if (line1 == "[이동]"&& p.hasPermission("admin.setup")) {
                getSigns().set("gates.$line2.${if (line3 == "클릭해서 이동하기") 2 else 1}",null)
                if (line4 != "") {
                    getSigns().set("gates.$line2.limit",null)
                }
                p.msg("제거 완료!")
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
                p.msg("스텟 파일 연동에 문제가 있습니다.\n관리자에게 문의하세요.")
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
            if (line1 == "[광산]") {
                when (line3) {
                    "클릭해서 입장하기" -> {
                        val warpl = getSigns().getLocation("signs.$line2.quit")
                        if (warpl != null && line4 != "-" && lv >= fn) {
                            getSigns().set("joined.${p.uniqueId}", line2)
                            sign.line(3, Component.text(e.player.name))
                            p.teleportAsync(Location(warpl.world, warpl.x + 0.5, warpl.y, warpl.z + 0.5))
                            sign.line(
                                2, Component.text("사용 중")
                                    .color(TextColor.color(255, 85, 85))
                            )
                            sign.update()
                        } else if (warpl != null && line4 != "-" && lv < fn) {
                            p.msg("입장 조건을 만족하지 않았습니다.")
                        } else if (warpl != null && line4 == "-") {
                            getSigns().set("joined.${p.uniqueId}", line2)
                            p.teleportAsync(Location(warpl.world, warpl.x + 0.5, warpl.y, warpl.z + 0.5))
                        } else {
                            p.msg("출구가 설정되어 있지 않습니다.")
                        }
                    }
                    "사용 중" -> {
                        p.msg("이미 사용중입니다.")
                    }
                    "클릭해서 나가기" -> {
                        getSigns().set("joined.${p.uniqueId}",null)
                        val warpl = getSigns().getLocation("signs.$line2.join")
                        val limit = getSigns().getInt("signs.$line2.limit")
                        val persistent = getSigns().getBoolean("signs.$line2.persistent")
                        if (warpl != null) {
                            val entrysign = warpl.block.state as Sign
                            entrysign.line(3, Component.text(""))
                            p.teleportAsync(Location(warpl.world,warpl.x+0.5,warpl.y,warpl.z+0.5))
                            entrysign.line(2, Component.text("클릭해서 입장하기")
                                    .color(TextColor.color(85,255,85))
                            )
                            if (limit > 0) entrysign.line(3, Component.text("Lv.$limit 이상"))
                            if (persistent) entrysign.line(3, Component.text("-"))
                            entrysign.update()
                        } else {
                            p.msg("입구가 설정되어 있지 않습니다.")
                        }
                    }
                }
                getSigns().save(getSignsloc())
            }
            else if (line1 == "[이동]"){
                val out = if (line3 == "클릭해서 이동하기") 1 else 2
                val warpl = getSigns().getLocation("signs.$line2.$out")
                if (warpl != null && lv >= fn) {
                    p.teleportAsync(Location(warpl.world, warpl.x + 0.5, warpl.y, warpl.z + 0.5))
                } else if (warpl != null && lv < fn) {
                    p.msg("이동 조건을 만족하지 않았습니다.")
                } else {
                    p.msg("출구가 설정되어 있지 않습니다.")
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
                entsign.line(2, Component.text("클릭해서 입장하기")
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
            p.msg("${ChatColor.RED}광산 이용 중 나가는 행위를 자제해 주세요!")
        }
    }
}