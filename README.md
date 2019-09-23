<img width="135"  height="120" alt="Zerocode" src="https://user-images.githubusercontent.com/12598420/51964581-e5a78e80-245e-11e9-9400-72c4c02ac555.png"> Zerocode
===
Automated API testing was never so easy before


[![API](https://img.shields.io/badge/api-automation-blue)](https://github.com/authorjapps/zerocode/wiki/What-is-Zerocode-Testing)
[![Performance Testing](https://img.shields.io/badge/performance-testing-ff69b4.svg)](https://github.com/authorjapps/zerocode/wiki/Load-or-Performance-Testing-(IDE-based))
[![Twitter Follow](https://img.shields.io/twitter/follow/ZerocodeEasyTDD.svg?style=social&label=Follow)](https://twitter.com/ZerocodeEasyTDD)


**Latest release: [1.3.x](https://search.maven.org/search?q=a:zerocode-tdd)** 🏹

**Continuous Integration:** [![Build Status](https://travis-ci.org/authorjapps/zerocode.svg?branch=master)](https://travis-ci.org/authorjapps/zerocode) <br/>
**Issue Discussions:** [Slack](https://join.slack.com/t/zerocode-workspace/shared_invite/enQtNzIyNDUwOTg2NDUzLWQ3ZTM1YTBhNjJmNzY3NmU0Y2I4NWIwZDVjYjk4M2JhYWY5NzA3ZWEwMWIwOTIwOWFjNTg2YzFmNzZhYTUyYzI) <br/>
**Mailing List:** [Mailing List](https://groups.google.com/forum/#!forum/zerocode-automation) <br/>
**License:** [Apache 2.0](http://www.apache.org/licenses/LICENSE-2.0) <br/>


Zerocode makes it easy to create and maintain automated tests with absolute minimum overhead for [REST](https://github.com/authorjapps/zerocode/wiki/User-journey:-Create,-Update-and-GET-Employee-Details),[SOAP](https://github.com/authorjapps/zerocode/blob/master/README.md#soap-method-invocation-example-with-xml-input), [Kafka](https://github.com/authorjapps/zerocode/wiki/Kafka-Testing-Introduction), [DB services](https://github.com/authorjapps/zerocode/wiki/Sample-DB-SQL-Executor) and more. It has got best of best ideas and practices from the community to keep it super simple and the adoption is rapidly growing among the developer/tester community.

Quick Links
===
For a quick introduction to Zerocode and its features, visit the 
+ [Zerocode Wiki](https://github.com/authorjapps/zerocode/wiki)
+ [User's guide](https://github.com/authorjapps/zerocode/wiki#developer-guide)

Introduction
===
Zerocode is a light-weight, simple and extensible open-source framework for writing test intentions in simple JSON or YAML format that facilitates both declarative configuration and automation. The [framework manages](https://github.com/authorjapps/zerocode/wiki/What-is-Zerocode-Testing) the response validations, target API invocations with  payload and test-scenario steps-chaining at the same time, same place using [Jayway JsonPath](https://github.com/json-path/JsonPath/blob/master/README.md#path-examples).

For example, if your REST API URL `https://localhost:8080/api/v1/customers/123` with `GET` method and `"Content-Type": "application/json"` returns the following payload and a `http` status code `200(OK)` ,
```javaScript
Response:
{
    "id": 123,
    "type": "Premium High Value",
    "addresses": [
        {
            "type":"home",
            "line1":"10 Random St"
        }
    ]
}
```

then, we can easily validate the above API using `Zerocode` like below.

+ Using YAML described as below,

> _The beauty here is, we can use the payload structure as it is without any manipulation._

```yaml
---
url: api/v1/customers/123
method: GET
request:
  headers:
    Content-Type: application/json
retry:
  max: 3
  delay: 1000
verify:
  status: 200
  headers:
    corr-id: corr_uuid
  body:
    id: 123
    type: Premium High Value
    addresses:
    - type: home
      line1: 10 Random St
```

+ Using JSON DSL described as below,

```javaScript
{
  "url": "api/v1/customers/123",
  "method": "GET",
  "request": {
    "headers": {
      "Content-Type": "application/json"
    }
  },
  "retry": {
    "max": 3,
    "delay": 1000
  },
  "verify": {
    "status": 200,
    "headers": {
      "corr-id": "corr_uuid"
    },
    "body": {
      "id": 123,
      "type": "Premium High Value",
      "addresses": [
        {
          "type": "home",
          "line1": "10 Random St"
        }
      ]
    }
  }
}
```

and run it simply by pointing to the above JSON/YAML file from a JUnit `@Test` method.

```java
   @Test
   @Scenario("test_customer_get_api.yml")
   public void getCustomer_happyCase(){
        // No code goes here. This remains empty.
   }
```

Looks simple n easy? Why not give a try? Visit the [quick-start guide](https://github.com/authorjapps/zerocode/wiki/Getting-Started) or [user's guide](https://github.com/authorjapps/zerocode/wiki#developer-guide) for more insight.

Zerocode is used by many companies such as Vocalink, HSBC, HomeOffice(Gov) and [many others](https://github.com/authorjapps/zerocode/wiki#smart-projects-using-zerocode) to achieve accurate production drop of their micro-services. 

Happy testing! <g-emoji class="g-emoji" alias="panda_face" fallback-src="https://github.githubassets.com/images/icons/emoji/unicode/1f43c.png">🐼</g-emoji>
