# Release process

1. Update the `version` property in each module's `package.json`.
2. Update the `version` property in each module's `plugin.xml`.
3. Run `lerna clean -y && lerna bootstrap`.
4. Run `npm publish --dry-run` on each module to ensure there are no issues.
5. Update the `CHANGELOG.md`.
6. Push the changes to the repo.
7. Run `lerna publish from-package`.
8. Create a GitHub release with the contents of the `CHANGELOG.md`.
