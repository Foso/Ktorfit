Releasing
=========

# Publish new version

1. Create new branch `release/X.Y.Z` from `master` branch
2. Update **ktorfit** version inside `gradle/libs.versions.toml`
3. Update **ktorfitGradlePlugin** version inside `gradle/libs.versions.toml`
4. Update Compatibility table in Readme.md
5. Update KtorfitCompilerSubPlugin.defaultCompilerPluginVersion if necessary 
6. Update ktorfit release version in mkdocs.yml
7. Update version in KtorfitGradleConfiguration
8. Set the release date in docs/changelog.md
9. `git commit -am "X.Y.Z."` (where X.Y.Z is the new version)
10. Push and create a PR to the `master` branch
11. When all checks successful, run GitHub Action `Publish Release` from your branch
12. Set the Git tag `git tag -a X.Y.Z -m "X.Y.Z"` (where X.Y.Z is the new version)
13. Merge the PR
14. Create a new release with for the Tag on GitHub
15. Run "deploy to GitHub pages" action
16. Put the relevant changelog in the release description
