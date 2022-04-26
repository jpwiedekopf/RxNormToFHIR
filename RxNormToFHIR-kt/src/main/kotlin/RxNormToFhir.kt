@file:Suppress("HttpUrlsUsage")

import ca.uhn.fhir.context.FhirContext
import com.opencsv.CSVParserBuilder
import com.opencsv.CSVReaderBuilder
import kotlinx.cli.ArgParser
import kotlinx.cli.ArgType
import kotlinx.cli.required
import mu.KotlinLogging
import java.io.File

const val RXNORM_SYSTEM_URL = "http://www.nlm.nih.gov/research/umls/rxnorm"
const val NDC_URL = "http://hl7.org/fhir/sid/ndc"

private val logger = KotlinLogging.logger { }

class RxNormToFhir(val fhirContext: FhirContext, args: Array<String>) {

    private val parser = ArgParser("RxNormToFhir-kt")
    val inputPath by parser.option(ArgType.String, shortName = "i", description = "Path to RxNorm files").required()
    val outputPath by parser.option(ArgType.String, shortName = "o", description = "Path to write FHIR resources")
    val version by parser.option(
        ArgType.String,
        shortName = "v",
        description = "Version of resources to write, e.g. version of RxNorm file"
    )

    init {
        parser.parse(args)
    }

    fun run() {
        var currentCui = 0
        val parser = CSVParserBuilder().withSeparator('|').build()
        val reader = CSVReaderBuilder(File(inputPath).bufferedReader()).withCSVParser(parser).build()
        reader.forEachIndexed { index, line ->
            val concept = RxConcept.fromLine(line)
            when (currentCui) {
                concept.cui -> {}
                !in (currentCui + 1..currentCui + 1000) -> logger.warn { "For concept with CUI ${concept.cui}, the increase is greater than 1000 (from $currentCui to ${concept.cui}, Delta ${concept.cui - currentCui}" }
            }
            currentCui = concept.cui
            if (index % 1000 == 0) {
                logger.info { "Processing #$index" }
            }
            when {
                concept.sourceAbbr != "RXNORM" -> return@forEachIndexed
                concept.suppress != RxConcept.SuppressibleFlag.N -> return@forEachIndexed
            }
        }
    }

}

fun main(args: Array<String>) {
    val fhirContext = FhirContext.forR4()
    RxNormToFhir(fhirContext, args).run()
}