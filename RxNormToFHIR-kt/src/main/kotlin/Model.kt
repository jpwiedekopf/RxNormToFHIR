data class RxConcept(
    val cui: Int,
    val languageOfTerm: String,
    val status: String?,
    val lui: String?,
    val stringType: String?,
    val sui: String?,
    val isPref: String?,
    val aui: String?,
    val sAui: String?,
    val sCui: String?,
    val sDui: String?,
    val sourceAbbr: String,
    val termType: String,
    val code: String,
    val conceptString: String,
    val sourceRestrictionLevel: String?,
    val suppress: SuppressibleFlag?,
    val contentViewFlag: String?,
) {

    companion object {
        fun fromLine(line: Array<String>) = RxConcept(
            cui = line[0].toInt(),
            languageOfTerm = line[1],
            status = line[2].valueOrNull(),
            lui = line[3].valueOrNull(),
            stringType = line[4].valueOrNull(),
            sui = line[5].valueOrNull(),
            isPref = line[6].valueOrNull(),
            aui = line[7].valueOrNull(),
            sAui = line[8].valueOrNull(),
            sCui = line[9].valueOrNull(),
            sDui = line[10].valueOrNull(),
            sourceAbbr = line[11],
            termType = line[12],
            code = line[13],
            conceptString = line[14],
            sourceRestrictionLevel = line[15].valueOrNull(),
            suppress = line[16].valueOrNull()?.let { SuppressibleFlag.valueOf(it) },
            contentViewFlag = line[17].valueOrNull()
        )
    }

    enum class SuppressibleFlag {
        O, E, Y, N
    }
}

fun String.valueOrNull() : String? = when {
    this.isBlank() -> null
    else -> this
}