# ![Zerocode Logo](https://user-images.githubusercontent.com/12598420/86005149-287ee480-ba0c-11ea-91a0-d0811f15be75.png)

Automated API testing has never been so easy


[![API](https://img.shields.io/badge/api-automation-blue)](https://github.com/authorjapps/zerocode/wiki/What-is-Zerocode-Testing)
[![Performance Testing](https://img.shields.io/badge/performance-testing-ff69b4.svg)](https://github.com/authorjapps/zerocode/wiki/Load-or-Performance-Testing-(IDE-based))
[![Twitter Follow](https://img.shields.io/twitter/follow/Zerocodeio.svg)](https://twitter.com/Zerocodeio)


**Latest release:🏹** [![Maven](https://maven-badges.herokuapp.com/maven-central/org.jsmart/zerocode-tdd/badge.svg)](https://maven-badges.herokuapp.com/maven-central/org.jsmart/zerocode-tdd/) <br/>
**Continuous Integration:** [![Build Status](https://travis-ci.org/authorjapps/zerocode.svg?branch=master)](https://travis-ci.org/authorjapps/zerocode) <br/>
**Issue Discussions:** [Slack](https://join.slack.com/t/zerocode-workspace/shared_invite/enQtNzYxMDAwNTQ3MjY1LTA2YmJjODJhNzQ4ZjBiYTQwZDBmZmNkNmExYjA3ZDk2OGFiZWFmNWJlNGRkOTdiMDQ4ZmQyNzcyNzVjNWQ4ODQ) <br/> 
**Mailing List:** [Mailing List](https://groups.google.com/forum/#!forum/zerocode-automation) <br/>
**License:** [Apache 2.0](http://www.apache.org/licenses/LICENSE-2.0) <br/>
**LinkedIn:** [Zerocode](https://www.linkedin.com/company/49160481)

Zerocode Open Source makes it easy to create, change, orchestrate and maintain automated tests with the absolute minimum overhead for [REST](https://knowledge.zerocode.io/en/knowledge/automation-of-user-journey-create-update-and-get-employee-rest-apis), [SOAP](https://knowledge.zerocode.io/en/knowledge/soap-testing-automation-with-xml-input), [Kafka Real Time Data Streams](https://knowledge.zerocode.io/knowledge/kafka-testing-introduction) and much more. Tests created in Zerocode Open Source can be easily shared between teams for reviewing, editing, and versioning. The platform incorporates the best feedback and suggestions from the community to make it incredibly powerful, and we’re seeing rapid adoption across the developer/tester community

Quick Links
===
To get started with Zerocode Open Source and its features, visit 
+ [Zerocode Documentation](https://knowledge.zerocode.io/knowledge)
+ [Quick Start guide](https://knowledge.zerocode.io/en/knowledge/zerocode-quick-start-guide)
+ [Release frequency](https://github.com/authorjapps/zerocode/wiki/Zerocode-release-frequency-and-schedule)

Maven Dependency
===
+ [New releases - zerocode-tdd](https://maven-badges.herokuapp.com/maven-central/org.jsmart/zerocode-tdd/) 
[![Maven](https://maven-badges.herokuapp.com/maven-central/org.jsmart/zerocode-tdd/badge.svg)](https://maven-badges.herokuapp.com/maven-central/org.jsmart/zerocode-tdd/)
+ _[Older releases - zerocode-rest-bdd](https://maven-badges.herokuapp.com/maven-central/org.jsmart/zerocode-rest-bdd/)_ 
[![Maven](https://maven-badges.herokuapp.com/maven-central/org.jsmart/zerocode-rest-bdd/badge.svg)](https://maven-badges.herokuapp.com/maven-central/org.jsmart/zerocode-rest-bdd/)

Introduction
===
Zerocode Open Source is a lightweight, simple and extensible framework for writing test intentions in a simple JSON or YAML format that facilitates both declarative configuration and automation.

Put simply, Zerocode is a sollution for all API Development pain points. The objective is to bring simplicity to API automation. The framework provides a unified solution to manage response validations, target API invocations, perform load/stress testing and perform security testing using a the simple domain specific languages (DSL) JSON and YAML.

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

then, Zerocode Open Source can be easily used to validate API using as follows:

> _The beauty here is, we can use the payload/headers structure for validation as it is without any manipulation or use a flat JSON path to skip the hassles of the entire object hierarchies._

## Validators

Using YAML

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
validators:
- field: "$.status"
  value: 200
- field: "$.body.type"
  value: Premium High Value
- field: "$.body.addresses[0].line1"
  value: 10 Random St
```

or

Using JSON

```JSON
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
  "validators": [
    {
      "field": "$.status",
      "value": 200
    },
    {
      "field": "$.body.type",
      "value": "Premium High Value"
    },
    {
      "field": "$.body.addresses[0].line1",
      "value": "10 Random St"
    }
  ]
}
```

## Matchers

Using YAML

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
    Content-Type:
    - application/json; charset=utf-8
  body:
    id: 123
    type: Premium High Value
    addresses:
    - type: Billing
      line1: 10 Random St
verifyMode: LENIENT
```

or

Using JSON

```JSON
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
      "Content-Type" : [ "application/json; charset=utf-8" ]
    },
    "body": {
      "id": 123,
      "type": "Premium High Value",
      "addresses": [
        {
          "type": "Billing",
          "line1": "10 Random St"
        }
      ]
    }    
  },
  "verifyMode": "STRICT"
}
```

The test can then be run simply by pointing to the above JSON/YAML file from a Junit `@Test` method.

```java
   @Test
   @Scenario("test_customer_get_api.yml")
   public void getCustomer_happyCase(){
        // No code goes here
   }
```

The bottom line is that Zerocode Open Source makes automated API testing declarative and simple. If you’d like to learn more, visit the [quick-start guide](https://knowledge.zerocode.io/en/knowledge/zerocode-quick-start-guide) to get started testing - fast!

Zerocode Open Source is used by many companies such as Vocalink, HSBC, HomeOffice(Gov) and [many others](https://knowledge.zerocode.io/knowledge/smart-projects-using-zerocode) to achieve an accurate production drop of their microservices. Learn more about [Validators Vs Matchers](https://knowledge.zerocode.io/knowledge/validators-and-matchers) here.

Happy testing!
