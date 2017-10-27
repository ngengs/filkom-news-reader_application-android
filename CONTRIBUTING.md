# Contributing to Filkom News Reader Android

First off, thanks for taking the time to contribute!

The following is a set of guidelines for contributing to [Filkom News Reader Android](https://github.com/ngengs/filkom-news-reader_application-android) on GitHub. These are mostly guidelines, not rules. Use your best judgment, and feel free to propose changes to this document in a pull request.

## Code of Conduct

This project and everyone participating in it is governed by the [Code of Conduct](.github/CODE_OF_CONDUCT.md). By participating, you are expected to uphold this code.

# How Can I Contribute?
## Help With Bug Reporting
This project use [github issues tracker](https://github.com/ngengs/filkom-news-reader_application-android/issues) for bug reporting.
* Please use search before create new issue to see if the problem has already been reported.
* If it has already been reported and the issue is still open, add a comment to the existing issue instead of opening a new one.
* If it has already been reported and the issue is close, [open a new issue](#open-new-issue) and include a link or issue number ``#number`` to the original issue in the body of your new one.

#### Open New Issue
Use [this](https://github.com/ngengs/filkom-news-reader_application-android/issues/new) to create new issue, and fill the given issue template.

## Help With Code
If you are coders you can help us with change the code of application.
This is guideline for helping us with code:
* Fork the repo
* Read [Build guide](https://github.com/ngengs/filkom-news-reader_application-android#build) except you must clone your repo instead this repo
* Change code style to Project code style that included in the repo and read about this project code style [here](#code-style)
* Make change to fix existing bugs on the Issue tracker or for create new feature
* Commit your change
* Push your change to your repo
* Make pull request to this [development branch](#branching) but make sure your repo is [up to date](#keeping-your-fork-up-to-date) before make the pull request

#### Code Style
This project has own code style that included in .idea. But if you want to know that code style is imported from [Google Java Code Style](https://github.com/google/styleguide/blob/gh-pages/intellij-java-google-style.xml) with some change.
When you write code you can read about android code style [here](https://source.android.com/source/code-style) except the copyright code style.
Use android java 8 feature if possible. [Supported Java 8 in Android](https://developer.android.com/studio/write/java8-support.html)

#### Branching
This project uses the [Git-Flow](http://nvie.com/posts/a-successful-git-branching-model/) branching model which requires all pull requests to be sent to the "development" branch. This is
where the next planned version will be developed. The "master" branch will always contain the latest stable version and is kept clean so a "hotfix" (e.g: an emergency security patch) can be applied to master to create a new version, without worrying about other features holding it up. For this reason all commits need to be made to "develop" and any sent to "master" will be closed automatically. If you have multiple changes to submit, please place all changes into their own branch on your fork.

One thing at a time: A pull request should only contain one change. That does not mean only one commit, but one change - however many commits it took. The reason for this is that if you change X and Y but send a pull request for both at the same time, we might really want X but disagree with Y, meaning we cannot merge the request. Using the Git-Flow branching model you can create new branches for both of these features and send two requests.

#### Commiting
When you make commit you must sign your work, certifying that you either wrote the work or otherwise have the right to pass it on to an open source project. git makes this trivial as you merely have to use `--signoff` on your commits to your project fork.
`git commit --signoff`
or simply
`git commit -s`

This will sign your commits with the information setup in your git config, e.g.
`Signed-off-by: Your Name <email@example.com>`

If you are using Android Studio there is a "Sign-Off" checkbox in the commit window. You could even alias git commit to use the `-s` flag so you don’t have to think about it.

#### Keeping your fork up-to-date
Unlike systems like Subversion, Git can have multiple remotes. A remote is the name for a URL of a Git repository. By default your fork will have a remote named "origin" which points to your fork, but you can add another remote named "codeigniter" which points to `https://github.com/ngengs/filkom-news-reader_application-android.git`. This is a read-only remote but you can pull from this develop branch to update your own.

If you are using command-line you can do the following:

1. `git remote add upstream https://github.com/ngengs/filkom-news-reader_application-android.git`
2. `git pull upstream development`
3. `git push origin development`
