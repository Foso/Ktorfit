# Development

# Update Ktorfit for new Kotlin version
- Bump **kotlin** in libs.versions
- Change **ktorfitCompiler** in libs.versions to KTORFIT_VERSION-NEW_KOTLIN_VERSION
- Run tests in :ktorfit-compiler-plugin
- Create a PR against master
- Merge PR
- Run GitHub Actions "publish" workflow

## 👷 Project Structure

* <kbd>compiler plugin</kbd> - module with source for the compiler plugin
* <kbd>ktorfit-annotations</kbd> - module with annotations for the Ktorfit
* <kbd>ktorfit-ksp</kbd> - module with source for the KSP plugin
* <kbd>ktorfit-lib-core</kbd> - module with source for the Ktorfit lib
* <kbd>ktorfit-lib</kbd> - ktorfit-lib-core + dependencies on platform specific clients
* <kbd>sandbox</kbd> - experimental test module to try various stuff

* <kbd>example</kbd> - contains example projects that use Ktorfit
* <kbd>docs</kbd> - contains the source for the GitHub page
