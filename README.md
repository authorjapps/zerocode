<img width="135"  height="120" alt="Zerocode" src="https://user-images.githubusercontent.com/12598420/51964581-e5a78e80-245e-11e9-9400-72c4c02ac555.png"> Zerocode
===
Kafka Data streams and Micro-services API automated regression testing via JSON or YAML


[![API](https://img.shields.io/badge/api-automation-blue)](https://github.com/authorjapps/zerocode/wiki/What-is-Zerocode-Testing)
[![Performance Testing](https://img.shields.io/badge/performance-testing-8A2BE2)](https://github.com/authorjapps/zerocode/wiki/Load-or-Performance-Testing-(IDE-based))
[![Kafka Testing](https://img.shields.io/badge/kafka-testing-blue)](https://zerocode-tdd.tddfy.com/kafka/Kafka-Testing-Introduction)


**Latest release:🏹** [![Maven](https://maven-badges.herokuapp.com/maven-central/org.jsmart/zerocode-tdd/badge.svg)](https://maven-badges.herokuapp.com/maven-central/org.jsmart/zerocode-tdd/) <br/>
**CI Testing:** ![example workflow](https://github.com/authorjapps/zerocode/actions/workflows/main.yml/badge.svg)
<br/>
**Issue Discussions:** [Slack](https://join.slack.com/t/zerocode-workspace/shared_invite/enQtNzYxMDAwNTQ3MjY1LTA2YmJjODJhNzQ4ZjBiYTQwZDBmZmNkNmExYjA3ZDk2OGFiZWFmNWJlNGRkOTdiMDQ4ZmQyNzcyNzVjNWQ4ODQ) <br/> 
**Mailing List:** [Mailing List](https://groups.google.com/forum/#!forum/zerocode-automation) <br/>
**License:** [Apache 2.0](http://www.apache.org/licenses/LICENSE-2.0) <br/>


Zerocode makes it easy to create and maintain automated tests with absolute minimum overhead for [REST](https://github.com/authorjapps/zerocode/wiki/User-journey:-Create,-Update-and-GET-Employee-Details),[SOAP](https://github.com/authorjapps/zerocode/blob/master/README.md#soap-method-invocation-example-with-xml-input), [Kafka Real Time Data Streams](https://github.com/authorjapps/zerocode/wiki/Kafka-Testing-Introduction) and much more. 
It has the best of best ideas and practices from the community to keep it super simple, and the adoption is rapidly growing among the developers & testers community.

Documentation
===
Visit here : 
+ [Documentation](https://zerocode-tdd.tddfy.com) - Indexed & instantly finds you the results
+ Want to amend or improve any documentation? Steps and guidelines are [here](https://github.com/authorjapps/zerocode/wiki/Documentation-Steps)

IDE Support By 
===
[<img width="135"  height="120" alt="Jetbrains IDE" src="images/jetbrains.svg">](https://www.jetbrains.com/idea/)


Maven Dependency
===
+ [New releases - zerocode-tdd](https://maven-badges.herokuapp.com/maven-central/org.jsmart/zerocode-tdd/) 
[![Maven](https://maven-badges.herokuapp.com/maven-central/org.jsmart/zerocode-tdd/badge.svg)](https://maven-badges.herokuapp.com/maven-central/org.jsmart/zerocode-tdd/)
+ _[Older releases - zerocode-rest-bdd](https://maven-badges.herokuapp.com/maven-central/org.jsmart/zerocode-rest-bdd/)_ 
[![Maven](https://maven-badges.herokuapp.com/maven-central/org.jsmart/zerocode-rest-bdd/badge.svg)](https://maven-badges.herokuapp.com/maven-central/org.jsmart/zerocode-rest-bdd/)

Introduction
===
Zerocode is a modern, lightweight, and extensible open-source framework designed for writing executable test scenarios using simple JSON or YAML formats. It supports both declarative configuration and automation, making it user-friendly and efficient.

In essence, Zerocode simplifies the complexities of modern API and data-streaming automation, including Kafka. The framework seamlessly handles response validations, target API invocations, load/stress testing, and security testing, all through straightforward YAML/JSON/Fluent steps.

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

+ Using JSON

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
      "type": "Premium Visa",
      "addresses": [
        {
          "type": "Billing",
          "line1": "10 Random St"
        }
      ]
    }    
  },
  "verifyMode": "LENIENT"
}
```

+ or Using YAML

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
    type: Premium Visa
    addresses:
    - type: Billing
      line1: 10 Random St
verifyMode: LENIENT
```


> _The beauty here is, we can use the payload/headers structure for validation as it is without any manipulation
> or
> use a flat JSON path to skip the hassles of the entire object hierarchies._


Looks simple & easy? Why not give it a try? Visit the [quick-start guide](https://github.com/authorjapps/zerocode/wiki/Getting-Started) or [user's guide](https://github.com/authorjapps/zerocode/wiki#developer-guide) for more insight.

Zerocode-TDD is used by many companies such as Vocalink, HSBC, HomeOffice(Gov) and [many others](https://github.com/authorjapps/zerocode/wiki#smart-projects-using-zerocode) to achieve accurate production drop of their micro-services, data-pipelines etc. 

Also, learn more about [Validators Vs Matchers](https://github.com/authorjapps/zerocode/wiki/Validators-and-Matchers) here.

Happy Testing! <g-emoji class="g-emoji" alias="panda_face" fallback-src="https://github.githubassets.com/images/icons/emoji/unicode/1f43c.png">🐼</g-emoji>

🔆 Visit [Documentation](https://zerocode-tdd.tddfy.com) - Indexed, searchable & instantly finds you the results
