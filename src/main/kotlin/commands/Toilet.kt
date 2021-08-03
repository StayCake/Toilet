package commands

import Main
import hazae41.minecraft.kutils.bukkit.msg
import io.github.monun.kommand.getValue
import io.github.monun.kommand.node.LiteralNode
import org.bukkit.Sound
import org.bukkit.configuration.file.FileConfiguration
import org.bukkit.entity.Player
import java.io.File

object Toilet {

    private fun getSigns() : FileConfiguration {
        return Main.signs
    }

    private fun getSignsloc() : File {
        return Main.signsloc
    }

    fun register(builder: LiteralNode) {
        builder.requires { playerOrNull != null }
        builder.then("reload") {
            executes {
                if (getSignsloc().canRead()) {
                    getSigns().load(getSignsloc())
                }
                player.sendMessage("리로드 완료!")
                player.playSound(player.location, Sound.ENTITY_PLAYER_LEVELUP, 1.0F, 1.0F)
            }
        }
        builder.then("delete") {
            then("name" to string()) {
                executes { ctx ->
                    val name : String by ctx
                    getSigns().set("signs.$name",null)
                    getSigns().save(getSignsloc())
                    player.msg("통로 $name 삭제 완료!")
                }
            }
            requires { playerOrNull != null }
            executes {
                player.msg("대상을 입력하세요.")
            }
        }
    }
}