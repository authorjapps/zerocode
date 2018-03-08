# How to contribute #

Thank you for your interest in contributing to this project.

Contribution should be easy with small guidelines you need to follow.  We would love 
to accept your pull requests to this project by raising an issue in the 
[issue list](https://github.com/authorjapps/zerocode/issues).

The issue clearly explains about the new or enhanced feature, or a bug fix.

## How to submit a Pull Request i.e. the change you want to merge ##

 
The below commit message start with an issue number e.g. `ISSUE-14`, Then a `space`, 
    then `#`, then a `space`, then a `short` commit message `max of 70` chars.
    A good and simple sample below.
    
```    
ISSUE-14 # SSL enabled http client 
```

  1. Best to start with opening a new issue describing the feature or a bug fix
     you intend to be available in the framework.
     
  1. Follow the usual process of [forking][] the repo, and create a new
     branch to work in.  Make sure you dont add any other feture or code which are
     not related to this issue. This makes things clear and great time saver.

  1. Cover your feature or bug fix code by good tests which are easy to understand. The
     project has already good amount of unit an dintegration test coverage, so please 
     refer at some of the existing tests if you are unsure how to go about it.

  1. All contributions must be licensed Apache 2.0 and all files must have
     a copy of the boilerplate licence comment (can be copied from an existing
     file.  Files should be formatted according to Google's [java style guide][].

  1. Please maintain well-formed-ness and consistency for each commit. 
     Avoid including any special chars in the commit message. Do not make it too verbose

  1. Finally, push the commits to your fork and submit a [pull request][].

[forking]: https://help.github.com/articles/fork-a-repo
[pull request]: https://help.github.com/articles/creating-a-pull-request
