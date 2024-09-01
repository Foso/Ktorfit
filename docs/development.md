# Development

# Update Ktorfit for new Kotlin version
- Bump **kotlin** in libs.versions
- Change **ktorfitCompiler** in libs.versions to KTORFIT_VERSION-NEW_KOTLIN_VERSION
- Run tests in :ktorfit-compiler-plugin
- Create a PR against master
- Merge PR
- Run GitHub Actions "publish" workflow