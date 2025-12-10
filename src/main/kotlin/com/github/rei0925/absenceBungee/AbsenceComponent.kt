package com.github.rei0925.absenceBungee

import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import kotlin.text.append

internal object AbsenceComponent {
    private const val ABSENCE_TEXT = "長期不在届提出者情報》"
    private val HEADER = Component.text()
        .append(Component.text(ABSENCE_TEXT, NamedTextColor.GOLD))
        .appendSpace()

    val LIST_HEADER = HEADER

    val LIST_LINE = { name: String, date: String ->
        Component.text()
            .append(Component.text(name).color(NamedTextColor.GREEN))
            .append(Component.text(" ~ ").color(NamedTextColor.GRAY))
            .append(Component.text(date).color(NamedTextColor.RED))
            .build()
    }

    val PLAYER_CHECK ={ name:String,date:String ->
        val builder = Component.text()
        builder.append(HEADER)
        builder.append(Component.text(name, NamedTextColor.GREEN))
        builder.append(Component.text("は", NamedTextColor.WHITE))
        builder.append(Component.text(" ~", NamedTextColor.RED))
        builder.append(Component.text(date, NamedTextColor.RED))
        builder.append(Component.text("で長期不在届を受理しています。", NamedTextColor.WHITE))
    }

    val DATA_NOT_FOUND ={ player: String ->
        val builder = Component.text()
        builder.append(HEADER)
        builder.append(Component.text("プレイヤー ", NamedTextColor.RED))
        builder.append(Component.text(player, NamedTextColor.AQUA))
        builder.append(Component.text(" の情報が見つかりませんでした。", NamedTextColor.RED))
        builder.build()
    }

    val DATE_FORMAT_INVALID = Component.text()
        .append(HEADER)
        .append(Component.text("日付の形式が不正です。YYYY-MM-DDで入力してください。", NamedTextColor.RED))

    val PLAYER_ADDED = { name: String, date: String ->
        Component.text()
            .append(HEADER)
            .append(Component.text(name, NamedTextColor.GREEN))
            .append(Component.text(" の不在届を追加しました。終了日: ", NamedTextColor.WHITE))
            .append(Component.text(date, NamedTextColor.RED))
            .build()
    }

    val PLAYER_ALREADY_EXISTS = { name: String ->
        Component.text()
            .append(HEADER)
            .append(Component.text("プレイヤー ", NamedTextColor.YELLOW))
            .append(Component.text(name, NamedTextColor.AQUA))
            .append(Component.text(" の不在届は既に存在します。", NamedTextColor.YELLOW))
            .build()
    }

    val PLAYER_NOT_FOUND = { name: String ->
        Component.text()
            .append(HEADER)
            .append(Component.text("プレイヤー ", NamedTextColor.RED))
            .append(Component.text(name, NamedTextColor.AQUA))
            .append(Component.text(" の情報が見つかりませんでした。", NamedTextColor.RED))
            .build()
    }

    val PLAYER_DELETED = { name: String ->
        Component.text()
            .append(HEADER)
            .append(Component.text(name, NamedTextColor.GREEN))
            .append(Component.text(" の不在届情報を削除しました。", NamedTextColor.WHITE))
            .build()
    }

    val DELETE_FAILED = Component.text()
        .append(HEADER)
        .append(Component.text("削除に失敗しました。", NamedTextColor.RED))

    val ERROR_OCCURED = { msg: String ->
        Component.text()
            .append(HEADER)
            .append(Component.text("削除中にエラーが発生しました: $msg", NamedTextColor.RED))
    }

    val JOIN_MESSAGE = { date: String ->
        Component.text()
            .append(Component.text("あなたは現在、不在届を提出しています（").color(NamedTextColor.RED))
            .append(Component.text(date).color(NamedTextColor.YELLOW))
            .append(Component.text(" まで）。").color(NamedTextColor.RED))
    }

    val JOIN_MESSAGE_EXPIRED = { date: String ->
        Component.text()
            .append(Component.text("不在届の期限（").color(NamedTextColor.RED))
            .append(Component.text(date).color(NamedTextColor.YELLOW))
            .append(Component.text("）が切れていたため、記録を削除しました。").color(NamedTextColor.RED))
    }
}