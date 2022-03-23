# Release process

1. Update the `version` property in each module's `package.json`.
3. Run `lerna clean -y && lerna bootstrap`.
5. Run `npm publish --dry-run` on each module to ensure there are no issues.
6. Update the `CHANGELOG.md`.
7. Push the changes to the repo.
8. Run `lerna publish from-package`.
9. Create a GitHub release with the contents of the `CHANGELOG.md`.
