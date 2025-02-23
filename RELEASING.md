Releasing
=========

# Publish new version

1. Create new branch `release/X.Y.Z` from `master` branch
2. Update **ktorfit** version inside `gradle/libs.versions.toml`
3. Update **ktorfitGradlePlugin** version inside `gradle/libs.versions.toml`
4. Update Compatibility table in Readme.md
5. Update ktorfit release version in mkdocs.yml
6. Update version in KtorfitGradleConfiguration
7. Set the release date in docs/changelog.md
8. `git commit -am "X.Y.Z."` (where X.Y.Z is the new version)
9. Push and create a PR to the `master` branch
10. When all checks successful, run GitHub Action `Publish Release` from your branch
11. Set the Git tag `git tag -a X.Y.Z -m "X.Y.Z"` (where X.Y.Z is the new version)
12. Merge the PR
13. Create a new release with for the Tag on GitHub
14. Run "deploy to GitHub pages" action
15. Put the relevant changelog in the release description