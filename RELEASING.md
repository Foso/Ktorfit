Releasing
=========

# Publish new version

1. Create new branch `release/X.Y.Z` from `master` branch
2. Update **ktorfit** version inside `gradle/libs.versions.toml`
3. Update Compatibility table in Readme.md
4. Update ktorfit release version in mkdocs.yml
5. `git commit -am "Release X.Y.Z."` (where X.Y.Z is the new version)
6. Push and create a PR to the `master` branch
7. When all checks successful, run GitHub Action `Publish Release` from your branch
8. Set the Git tag `git tag -a X.Y.Z -m "X.Y.Z"` (where X.Y.Z is the new version)
9. Merge the PR
10. Create a new release with for the Tag on GitHub
11. Put the relevant changelog in the release description