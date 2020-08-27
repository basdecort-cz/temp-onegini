The following steps must be executed to create a release:

1. Update the release notes
2. Update the version number in the `plugin.xml`
3. Commit and push the version update to GitHub
4. Mare sure you have your AWS access key in your env to allow uploading the documentation.
5. Merge the development branch to master.
6. Remove the Onegini NPM registry from your `~/.npmrc` file. We must release to the public NPM repo and not the Onegini repo.
7. Perform NPM login (`npm login`) with the `onegininpm` account.
8. Publish the NPM package using the tool `np`. execute `np` to trigger an NPM release. In case you are not releasing from master you need to append `--any-branch` to the np command.
9. Run `invoke documentation` to upload the documentation to S3. (Don't forget to re-enable the Onegini NPM registry first).
10. Draft a new GitHub release
11. Update the version number in the `plugin.xml` and `package.json` files for the next development iteration (bump the version and append `-SNAPSHOT`)
