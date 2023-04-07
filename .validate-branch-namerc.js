module.exports = {
    pattern: '^(main|develop|production)$|^(bump|feat|fix|rel(?:ease)?)/.+$',
    errorMsg:
      '🤨 The branch you try to push does not respect the naming convention, please rename it using `git branch -m <actual-name> <new-name>`',
  }