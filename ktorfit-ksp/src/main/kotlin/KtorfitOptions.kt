class KtorfitOptions(options: Map<String, String>) {
    /**
     * 0: Turn off all Ktorfit related error checking
     *
     * 1: Check for errors
     *
     * 2: Turn errors into warnings
     */
    val errorsLoggingType: Int = (options["Ktorfit_Errors"]?.toIntOrNull()) ?: 1
}