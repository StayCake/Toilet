import commands.Toilet
import hazae41.minecraft.kutils.get
import io.github.monun.kommand.kommand
import org.bukkit.configuration.file.YamlConfiguration
import org.bukkit.plugin.Plugin
import org.bukkit.plugin.java.JavaPlugin
import java.io.File

class Main : JavaPlugin() {

    companion object {
        lateinit var instance: Plugin
            private set
        lateinit var signs: YamlConfiguration
            private set
        lateinit var signsloc: File
            private set
        lateinit var kspYml: YamlConfiguration
            private set
        lateinit var ksp: Plugin
            private set
    }

    override fun onEnable() {

        println(String.format("[%s] - 가동 시작!", description.name))
        val pl = server.pluginManager.getPlugin("KoiSupport")
        if (pl == null) {
            println(String.format("[%s] - KoiSupport 연결 실패!", description.name))
            server.pluginManager.disablePlugin(this)
            return
        }
        server.pluginManager.registerEvents(Events(), this)

        val kspFile = pl.dataFolder["data"]["stats.yml"]
        val kspYaml = YamlConfiguration()
        if (!kspFile.canRead()) {
            println("스텟 파일 연동에 실패했습니다.")
        } else {
            kspYaml.load(kspFile)
        }
        kspYml = kspYaml
        ksp = pl

        instance = this
        signs = YamlConfiguration.loadConfiguration(dataFolder["signs.yml"])
        signsloc = dataFolder["signs.yml"]

        if (!dataFolder["signs.yml"].canRead()) {
            dataFolder.mkdir()
            signs.save(signsloc)
        }

        kommand {
            register("Toilet") {
                Toilet.register(this)
            }
        }
    }

    override fun onDisable() {
        println(String.format("[%s] - 가동 중지.", description.name))
    }
}