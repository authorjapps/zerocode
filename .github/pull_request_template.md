# <Feature Title>

## Fixed Which Issue?
- [ ] Which issue or ticket was(will be) fixed by this PR? (capture the issue link here)

PR Branch
**_#ADD LINK TO THE PR BRANCH_**

## Motivation and Context

## Checklist:

* [ ] New Unit tests were added
  * [ ] Covered in existing Unit tests

* [ ] Integration tests were added
  * [ ] Covered in existing Integration tests

* [ ] Test names are meaningful

* [ ] Feature manually tested and outcome is successful

* [ ] PR doesn't break any of the earlier features for end users
  * [ ] WARNING! This might break one or more earlier earlier features, hence left a comment tagging all reviewrs

* [ ] PR doesn't break the HTML report features directly
  * [ ] Yes! I've manually run it locally and seen the HTML reports are generated perfectly fine
  * [ ] Yes! I've opened the generated HTML reports from the `/target` folder and they look fine

* [ ] PR doesn't break any HTML report features indirectly
  * [ ] I have not added or amended any dependencies in this PR
  * [ ] I have double checked, the new dependency added or removed has not affected the report generation indirectly
  * [ ] Yes! I've seen the Sample report screenshots [here](https://github.com/authorjapps/zerocode/issues/694#issuecomment-2505958433), and HTML report of the current PR looks simillar.

* [ ] Branch build passed in CI

* [ ] No 'package.*' in the imports

* [ ] Relevant DOcumentation page added or updated with clear instructions and examples for the end user
  * [ ] Not applicable. This was only a code refactor change, no functional or behaviourial changes were introduced

* [ ] Http test added to `http-testing` module(if applicable) ?
  * [ ] Not applicable. The changes did not affect HTTP automation flow

* [ ] Kafka test added to `kafka-testing` module(if applicable) ?
  * [ ] Not applicable. The changes did not affect Kafka automation flow
