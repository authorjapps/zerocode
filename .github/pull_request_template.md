# <Feature Title>

## Fixed Which Issue?
- [ ] Which issue or ticket was(will be) fixed by this PR? (capture the issue link here)

PR Branch
**_#ADD LINK TO THE PR BRANCH_**

## Motivation and Context

## Checklist:

* [ ] 1. New Unit tests were added
  * [ ] 1.1 Covered in existing Unit tests

* [ ] 2. Integration tests were added
  * [ ] 2.1 Covered in existing Integration tests

* [ ] 3. Test names are meaningful

* [ ] 3.1 Feature manually tested and outcome is successful

* [ ] 4. PR doesn't break any of the earlier features for end users
  * [ ] 4.1 WARNING! This might break one or more earlier earlier features, hence left a comment tagging all reviewrs

* [ ] 5. PR doesn't break the HTML report features directly
  * [ ] 5.1 Yes! I've manually run it locally and seen the HTML reports are generated perfectly fine
  * [ ] 5.2 Yes! I've opened the generated HTML reports from the `/target` folder and they look fine

* [ ] 6. PR doesn't break any HTML report features indirectly
  * [ ] 6.1 I have not added or amended any dependencies in this PR
  * [ ] 6.2 I have double checked, the new dependency added or removed has not affected the report generation indirectly
  * [ ] 6.3 Yes! I've seen the Sample report screenshots [here](https://github.com/authorjapps/zerocode/issues/694#issuecomment-2505958433), and HTML report of the current PR looks simillar.

* [ ] 7. Branch build passed in CI

* [ ] 8. No 'package.*' in the imports

* [ ] 9. Relevant DOcumentation page added or updated with clear instructions and examples for the end user
  * [ ] 9.1 Not applicable. This was only a code refactor change, no functional or behaviourial changes were introduced

* [ ] 10. Http test added to `http-testing-examples` module(if applicable) ?
  * [ ] 10.1 Not applicable. The changes did not affect HTTP automation flow

* [ ] 11. Kafka test added to `kafka-testing-examples` module(if applicable) ?
  * [ ] 11.1 Not applicable. The changes did not affect Kafka automation flow
