package com.github.rei0925.absenceBungee

import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor

internal object AbsenceComponent {
    private const val ABSENCE_TEXT = "長期不在届提出者情報》"
    private val HEADER = Component.text()
        .append(Component.text(ABSENCE_TEXT, NamedTextColor.GOLD))

    val PLAYER_CHECK ={ name:String,date:String ->
        val builder = Component.text()
        builder.append(HEADER)
        builder.append(Component.newline())
        builder.append(Component.text(name, NamedTextColor.GREEN))
        builder.append(Component.text("は", NamedTextColor.WHITE))
        builder.append(Component.text(" ~", NamedTextColor.RED))
        builder.append(Component.text(date, NamedTextColor.RED))
        builder.append(Component.text("で長期不在届を受理しています。", NamedTextColor.WHITE))
    }

    val PLAYER_NOT_FOUND ={ player: String ->
        val builder = Component.text()
        builder.append(HEADER)
        builder.append(Component.newline())
        builder.append(Component.text("プレイヤー ", NamedTextColor.RED))
        builder.append(Component.text(player, NamedTextColor.AQUA))
        builder.append(Component.text(" の情報が見つかりませんでした。", NamedTextColor.RED))
        builder.build()
    }
}