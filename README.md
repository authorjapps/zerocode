## Zerocode - TDD and BDD <img width="84"  height="103" alt="logo BnW" src="https://user-images.githubusercontent.com/12598420/46505720-e2dabb00-c829-11e8-943d-f967fa1f2a98.png"> 

An open source lib enables API testing via simple declarative JSON steps - REST, SOAP and DB services

Zerocode brings the simplicity in testing and validating APIs by eliminating repetitive code for test assertions, http calls and payload parsing. See an example [how](https://github.com/authorjapps/zerocode/wiki/User-journey:-Create,-Update-and-GET-Employee-Details). It's powerful JSON comparison and assertions make the testing cycle a lot easy and clean.

It also helps in mocking/stubbing interfacing APIs during the testing cycle. Its approach to IDE based performance testing to generate load/stress on the target application is quite simple, flexible and efficient - It goes a step further enabling you to simply reuse the test(s) from your regression pack.

[![License](https://img.shields.io/hexpm/l/plug.svg)](https://github.com/authorjapps/zerocode/blob/master/LICENSE) 
[![Code Coverage](https://img.shields.io/badge/coverage-80%25-brightgreen.svg)](https://github.com/authorjapps/zerocode/blob/master/img/code_coverage/code_coverage_granular.png)
[![zerocode REST API Automation](https://img.shields.io/badge/REST%20API-automation-green.svg)](https://github.com/authorjapps/zerocode-hello-world) 
[![zerocode SOAP Testing Automation API Automation](https://img.shields.io/badge/SOAP%20testing-automation-blue.svg)](https://github.com/authorjapps/zerocode/issues/28) 
[![Gitter](https://img.shields.io/gitter/room/nwjs/nw.js.svg)](https://gitter.im/zerocode-testing/help-and-usage)
[![Performance Testing](https://img.shields.io/badge/performance-testing-ff69b4.svg)](https://github.com/authorjapps/zerocode/wiki/Load-or-Performance-Testing-(IDE-based))

>Testing was _never_ so easy before.

e.g. Your below AC(Acceptance Criteria) or an `user journey` scenario ,
```java
GIVEN- The GitHub REST api GET end point, 
WHEN- I invoke the API, 
THEN- I will receive 200(OK) status with the body 
AND- assert the response
```
translates to the below executable JSON in `Zerocode` - As simple as that ! <br/>
_(See here [a full blown CRUD operation scenario](https://github.com/authorjapps/zerocode/wiki/User-journey:-Create,-Update-and-GET-Employee-Details) with POST, PUT, GET, DELETE example.)_ <br/>

Keep in mind: It's simple JSON. <br/>
~~_No feature files, no extra plugins, no statements or grammar syntax overhead._~~ 

```javaScript
{
    "scenarioName": "Invoke the GET api and assert the response",
    "steps": [
        {
            "name": "get_user_details",
            "url": "https://api.github.com/users/octocat",
            "operation": "GET",
            "request": {
            },
            "assertions": {
                "status": 200,
                "body": {
                    "login" : "octocat",
                    "id" : 33847731,
                    "type" : "User"
                }
            }
        }
    ]
}
```
and it is **declarative** DSL, with the `request/response` fields available for the next steps via the `JSON Path`.

See the [Table Of Contents](https://github.com/authorjapps/zerocode#table-of-contents--) for usages and examples.

Maven and CI
====
**Latest release: [1.2.x](https://mvnrepository.com/artifact/org.jsmart/zerocode-rest-bdd)**

**Continuous Integration:** [![Build Status](https://travis-ci.org/authorjapps/zerocode.svg?branch=master)](https://travis-ci.org/authorjapps/zerocode) <br/>
**HelloWorld:** [Calling a GitHub api](https://github.com/authorjapps/zerocode-hello-world/blob/master/src/test/resources/helloworld/hello_world_status_ok_assertions.json) step and executing [Test](https://github.com/authorjapps/zerocode-hello-world/blob/master/src/test/java/org/jsmart/zerocode/testhelp/tests/helloworld/JustHelloWorldTest.java) code. <br/>
**Help and Usage:** [Table of Contents](https://github.com/authorjapps/zerocode#table-of-contents--) <br/>
**Wiki:** [About Zerocode](https://github.com/authorjapps/zerocode/wiki) <br/>
**License:** [Apache 2.0](http://www.apache.org/licenses/LICENSE-2.0) <br/>


> The purpose of Zerocode lib is to make your API tests easy to **write**, easy to **change**, easy to **share**.

Hello World
====

#### Clone or download the below quick-start repos to run them in local IDE or maven command. 

 * Quick start - [**Hello World** examples](https://github.com/authorjapps/zerocode-hello-world) <br/> 
  
 * Quick start - [**API Contracts testing** - Interfacing applications](https://github.com/authorjapps/consumer-contract-tests) <br/> 
 
 *  Quick start - [**Performance** testing -  Varying **Load/Stress** generation](https://github.com/authorjapps/performance-tests) <br/> 
 
 * Quick start - [**Spring Boot** application - **Integration testing** - In-Memory](https://github.com/authorjapps/spring-boot-integration-test) <br/>

 * Quick start - [**Performance testing** - Resusing Spring JUnit tests(`less common`) - JUnit-Spring-Zerocode](https://github.com/authorjapps/zerocode-spring-junit) <br/>

To build any of the above projects, you can use the following command
```
mvn clean install -DskipTests
```

#### Latest news/releases/features

#### Follow us(Twitter) - 
<a href="https://twitter.com/ZercodeEasyTDD"><img width="57" alt="download" src="https://user-images.githubusercontent.com/5318345/45001240-22bf4000-afe9-11e8-8695-f6791b69e07c.png"></a>

Zerocode empowers the automation testers as well as developers to build up test scenario steps effortlessly, with sending/receiving payload and asserting the response as JSON. The repititive tasks of the everyday automation have been taken care optimally at the framework level, enabling you to focus on the business scenarios, user journeys and acceptance criterias acuurately. 

Supported testing frameworks:
 * [JUnit](http://junit.org)

Latest maven release:
```
<dependency>
    <groupId>org.jsmart</groupId>
    <artifactId>zerocode-rest-bdd</artifactId>
    <version>1.2.x</version> 
</dependency>
```
Check here for the latest- 
https://github.com/authorjapps/zerocode/releases -or- [Maven Central](https://mvnrepository.com/artifact/org.jsmart/zerocode-rest-bdd)

Zerocode helps you to design better Test Cases for your business features, maintain and update them easily, avoiding sleepless nights. It is built on extending the **Junit core runners**. You simply annotate your test method with JUnit **@Test** and run like unit tests, as well optionally you can use`Suite` Runner for the CI builds. 

Testing becomes an easy and effortless job due to the **simplicity** nature of JSON and the native support by popular IDEs e.g. Eclipse /IntelliJ /NetBeans etc with no extra plugin need. Your tests will not be cumbersome and complex anymore. Zerocode makes your tests independent, complete and structured, and easily maintainable by the team or the new comers. It enables you to write your `API End Point tests`, `Consumer Contract tests`, `End to End tests` as well as `Performance tests` etc, at the **speed** of writing **JUnit** tests.

- Browse or clone `contract tests` examples from [here](https://github.com/authorjapps/consumer-contract-tests)
- Browse or clone `performance tests`(load, stress) examples [here]()


[More >>](https://github.com/authorjapps/zerocode/wiki)

Who uses Zerocode?
--------------------
 + [Vocalink (A Mastercard company)](https://www.vocalink.com/) - REST API testing for virtualization software 
 + [HSBC Bank](https://www.hsbc.co.uk/) - MuleSoft application REST API Contract testing, E2E Integration Testing, Oracle DB API testing, SOAP testing and Load/Stress aka Performance testing
 + [Home Office(GOV.UK)](https://www.gov.uk/government/organisations/home-office) - Micro-Services REST API Contract testing, HDFS/Hbase REST end point testing, Authentication testing

## REST BDD Testing Framework

Develop and test applications with TDD and BDD approach while easily building up your regression suites. 

Execute your complex business scenario steps with simple declarative jsons which defines your RESTful service behaviour.

~~Testing no more a harder, slower and sleepless task~~

See the [HelloWorldTest](https://github.com/authorjapps/zerocode-hello-world/blob/master/src/test/java/org/jsmart/zerocode/testhelp/tests/helloworld/JustHelloWorldTest.java) and [more](https://github.com/authorjapps/zerocode-hello-world/blob/master/src/test/java/org/jsmart/zerocode/testhelp/tests/helloworldmore/JustHelloWorldMoreTest.java)

## Getting started

Add these `two` maven dependencies:
```xml
<dependency>
    <groupId>org.jsmart</groupId>
    <artifactId>zerocode-rest-bdd</artifactId>
    <version>1.2.x</version> 
    <scope>test</scope>
</dependency>

<dependency>
    <groupId>junit</groupId>
    <artifactId>junit</artifactId>
    <version>4.12</version>
    <scope>test</scope>
</dependency>
```

Then annotate your `JUnit` test method pointing to the JSON file as below and `run` as a unit test. 
That's it. Done.

```java
@TargetEnv("github_host.properties")
@RunWith(ZeroCodeUnitRunner.class)
public class JustHelloWorldTest {

    @Test
    @JsonTestCase("helloworld/hello_world_status_ok_assertions.json")
    public void testGet() throws Exception {

    }
}
```
Where,
You need not have to do any GIVEN-WHEN-THEN like below. <br/>

~~GIVEN- the GitHub REST api GET end point,~~ <br/>
~~WHEN- I invoke the API,~~ <br/>
~~THEN- I will receive 200(OK) status with body and assert the response~~ <br/>

or

~~GIVEN- the GitHub REST url and the method GET,~~ <br/>
~~WHEN- I invoke the API,~~ <br/>
~~THEN- I will receive 200(OK) status with body~~ <br/>
~~AND assert the response~~ <br/>

or

~~GIVEN- the GET methos~~ <br/>
~~AND the http url of GitHub api~~ <br/>
~~WHEN- I invoke the API using a HTTP client,~~ <br/>
~~THEN- I will receive 200(OK) status with body~~ <br/>
~~AND assert the response~~ <br/>

or


~~HttpResponse<User> response =~~

~~aHttpClient.get("https://<host_post_externalized>/users/octocat")~~

  ~~.header("accept", "application/json")~~

  ~~.execute();~~

~~User user = response.getUser();~~

~~assertThat(response.getStatusCode(), is(200))~~

~~assertThat(user.getId(), is(33847731))~~

~~assertThat(user.getLogin(), is("octocat"))~~

~~assertThat(user.getType(), is("user"))~~

</br>

You just need the below `hello_world_status_ok_assertions.json`.
```javaScript
{
    "scenarioName": "Invoke the GET api and assert the response",
    "steps": [
        {
            "name": "get_user_details",
            "url": "/users/octocat",
            "operation": "GET",
            "request": {
            },
            "assertions": {
                "status": 200,
                "body": {
                    "login" : "octocat",
                    "id" : 33847731,
                    "type" : "User"
                }
            }
        }
    ]
}
```

the `github_host.properties` looks as below:
```
web.application.endpoint.host=https://api.github.com
web.application.endpoint.port=443
web.application.endpoint.context=
```

See more usages and examples below.

## Table of Contents - 
- [Help and usage](#1)
- [Overriding with Custom HttpClient with Project demand, See also SSL Trusted Http Client](#16)
- [Externalize host and port to properties files](#17)
- [Single Scenario with single step](#2)
- [Generating Load or stress for performance testing](#27)
- [Step with more assertions](#3)
- [Running with step loop](#4)
- [Running with scenario loop](#5)
- [Generated reports and charts](#6)
- [More assertion with handy place holders](#7)
- [General Place holders](#8)
- [Chaining multiple steps for a scenario](#10)
- [Generating random strings, random numbers and static strings](#11)
- [Asserting general and exception messages](#12)
- [Asserting with LT(lesser than) and GT(greater than)](#13)
- [Dealing with arrays](#9) 
- [Asserting an empty array](#14)
- [Asserting an array SIZE](#141)
- [Calling java methods(apis) for specific tasks)](#15)
- [Generating IDs and sharing across steps](#18)
- [Bare JSON String without curly braces, still a valid JSON](#19)
- [Passing Headers to the REST API](#20) 
- [Setting Jenkins env propperty and picking environment specific properties file](#21)
- [LocalDate and LocalDateTime format example](#22)
- [SOAP method invocation example using xml input](#23)
- [SOAP method invocation where Corporate Proxy enabled](#24)
- [MIME Type Converters- XML to JSON, prettyfy XML etc](#25)
- [Using WireMock for mocking dependent end points](#26)
- [Basic http authentication step using zerocode](#28)
- [Sending query params in URL or separately](#29)
- [General place holders and assertion place holder table](#99)
- [References and Dicussions](#100)


### examples:

#### 1:
#### Help and usage

Download this help and usage project to try it yourself.

- HelloWorld project: https://github.com/authorjapps/zerocode-hello-world

- Simple steps to run: https://github.com/authorjapps/zerocode-hello-world#zerocode-hello-world

- Git [Clone](https://github.com/authorjapps/zerocode-hello-world) or [Download](https://github.com/authorjapps/zerocode-hello-world/archive/master.zip) the zip file(contains a maven project) to run locally 


#### 2:
#### Single Scenario with single step

A scenario might consists of one or more steps. Let's start with single step Test Case:
```javaScript
{
  "scenarioName": "Vanilla - Will Get Google Employee Details",
  "steps": [
    {
      "name": "step1_get_google_emp_details",
      "url": "http://localhost:9998/google-emp-services/home/employees/999",
      "operation": "GET",
      "request": {
      },
      "assertions": {
        "status": 200
      }
    }
  ]
}
```

Note:
The above JSON block is a test case where we asked the test framework to hit the 
> REST end point : http://localhost:9998/google-emp-services/home/employees/999

> with method: GET 

> and asserting the REST response with an  

> expected status: 200

> where, step "name" is a meaningful step name, which is significant when multiple steps are run. See a multi-step example.

Note:
> scenarioname : is free text

> step name: free text without any space


The above test case will PASS as the end point actually responds as below. Look at the "response" section below.
```javaScript
        {
          "name": "Sample_Get_Employee_by_Id",
          "operation": "GET",
          "url": "/google-emp-services/home/employees/999",
          "response": {
            "status": 200,
            "body": {
              "id": 999,
              "name": "Larry P",
              "availability": true,
              "addresses":[
                {
                  "gpsLocation": "x3000-y5000z-70000"
                },
                {
                  "gpsLocation": "x3000-y5000z-70000S"
                }
              ]
            }
          }
        }
```

The following Test Case will fail. Why? 

Because you are asserting with an expected status as 500, but the end point actually returns 200.

```javaScript
{
  "scenarioName": "Vanilla - Will Get Google Employee Details",
  "steps": [
    {
      "name": "step1_get_google_emp_details",
      "url": "http://localhost:9998/google-emp-services/home/employees/999",
      "operation": "GET",
      "request": {
      },
      "assertions": {
        "status": 500
      }
    }
  ]
}
```

#### 27:
#### Generating load for performance testing aka stress testing
+ Browse or clone this [sample performance-tests repo](https://github.com/authorjapps/performance-tests) with examples.
   + Take advantage of the following two extended Junit load runners from the lib-

> @RunWith(ZeroCodeLoadRunner.class)

and

> @RunWith(ZeroCodeMultiLoadRunner.class)

- Load a single scenario using `ZeroCodeLoadRunner` (See example of [ZeroCodeMultiLoadRunner here](https://github.com/authorjapps/performance-tests#multi-scenario-parallel-load))

```java
@LoadWith("load_config_sample.properties")
@TestMapping(testClass = TestGitGubEndPoint.class, testMethod = "testGitHubGET_load")
@RunWith(ZeroCodeLoadRunner.class)
public class LoadGetEndPointTest {
}
```
- The load generation properties are set here `load_config_sample.properties`. Learn [more >>](https://github.com/authorjapps/zerocode/wiki/Load-or-Performance-Testing-(IDE-based)#how-to-run-tests-in-parallel-in-context-of-one-or-more-scenarios-)
```properties
number.of.threads=2
ramp.up.period.in.seconds=10
loop.count=1
abort.after.time.lapsed.in.seconds=600
```
- The test case for GET api is mapped or fed into the load runner as below:

> @TestMapping(testClass = TestGitGubEndPoint.class, testMethod = "testGitHubGET_load") 

which verifies the response in the `assertions` section -

```javascript
{
    "scenarioName": "Load testing- Git Hub GET API",
    "steps": [
        {
            "name": "get_user_details",
            "url": "/users/octocat",
            "operation": "GET",
            "request": {
            },
            "assertions": {
                "status": 200,
                "body": {
                    "login" : "octocat",
                    "id" : 583231,
                    "avatar_url" : "https://avatars3.githubusercontent.com/u/583231?v=4",
                    "type" : "User",
                    "name" : "The Octocat",
                    "company" : "GitHub"
                }
            }
        }
    ]
}
```
- In one of the response during the load, if the `actual response` does not match the `expected response` i.e. in the `assertions` section above, then the test will fail.
- [Browse the above example](https://github.com/authorjapps/zerocode-hello-world) in GitHub.
or 
- [Download as zip](https://github.com/authorjapps/zerocode-hello-world/archive/master.zip) the above maven project to run from your IDE. 

[More (Learn advantages of load testing using your IDE(Eclipse or Intellij etc)) >>](https://github.com/authorjapps/zerocode/wiki/Load-or-Performance-Testing-(IDE-based))

#### 3:
#### Single step with more assertions

```javaScript
{
  "scenarioName": "Vanilla - Will Get Google Employee Details",
  "steps": [
    {
      "name": "step1_get_google_emp_details",
      "url": "http://localhost:9998/google-emp-services/home/employees/999",
      "operation": "GET",
      "request": {
      },
      "assertions": {
        "status": 200,
        "body": {
          "id": 999,
          "name": "Larry P",
          "availability": true,
          "addresses":[
            {
              "gpsLocation": "x3000-y5000z-70000"
            },
            {
              "gpsLocation": "x3000-y5000z-70000S"
            }
          ]
        }
      }
    }
  ]
}
```

The above Test Case will PASS as the assertions section has all expected values matching the end point's response.

#### 4:
#### Running with step _loop_

- Usage: See here: [Step loop](https://github.com/authorjapps/helpme/blob/master/zerocode-rest-help/src/test/resources/tests/00_sample_test_scenarios/02_using_step_loop.json)

- _loop_ field in a step will execute the step that many number of time.

```javaScript
{
  "scenarioName": "Vanilla - Execute multiple times - Step",
  "steps": [
    {
      "loop": 2,
      "name": "get_room_details",
      "url": "http://localhost:9998/google-emp-services/home/employees/101",
      "operation": "GET",
      "request": {
      },
      "assertions": {
        "status": 200,
        "body": {
          "id": 101
        }
      }
    }
  ]
}
```


#### 5:
#### Running with scenario _loop_

- Usage: See here: [Scenario loop](https://github.com/authorjapps/helpme/blob/master/zerocode-rest-help/src/test/resources/tests/00_sample_test_scenarios/03_using_scenario_loop.json)
Runs the entire scenario two times i.e. executing both the steps once for each time.

```javaScript
{
  "scenarioName": "Vanilla - Execute multiple times - Scenario",
  "loop": 2,
  "steps": [
    {
      "name": "get_room_details",
      "url": "http://localhost:9998/google-emp-services/home/employees/101",
      "operation": "GET",
      "request": {
      },
      "assertions": {
        "status": 200,
        "body": {
          "id": 101
        }
      }
    },
    {
      "name": "get_another_room_details",
      "url": "http://localhost:9998/google-emp-services/home/employees/102",
      "operation": "GET",
      "request": {
      },
      "assertions": {
        "status": 200,
        "body": {
          "id": 102
        }
      }
    }
  ]
}
```


#### 6:
#### Generated reports and charts

Generated test statistics reports. See the '/target' folder after every run. 
e.g. Look for-

> target/zerocode-junit-granular-report.csv

> target/zerocode-junit-interactive-fuzzy-search.html

See some sample reports below:

##### Spike Chart:

1. [Full coverage CSV report](https://github.com/authorjapps/helpme/blob/master/zerocode-rest-help/src/test/resources/zz_reports/zerocode_full_report_2016-07-30T11-44-14.512.csv)

1. [Interactive - Chart(Filter by Author, Test name, status etc)](http://htmlpreview.github.io/?https://github.com/authorjapps/helpme/blob/master/zerocode-rest-help/src/test/resources/zz_reports/zerocode-interactive.html)


##### CSV Report:

- See here : [Full coverage CSV report](https://github.com/authorjapps/helpme/blob/master/zerocode-rest-help/src/test/resources/zz_reports/zerocode_full_report_2016-07-30T11-44-14.512.csv)

```
If target folder has permission issue, the library alerts with-
----------------------------------------------------------------------------------------
Somehow the 'target/zerocode-test-reports' is not present or has no report JSON files. 
Possible reasons- 
   1) No tests were activated or made to run via ZeroCode runner. -or- 
   2) You have simply used @RunWith(...) and ignored all tests -or- 
   3) Permission issue to create/write folder/files 
   4) Please fix it by adding/activating at least one test case or fix the file permission issue
----------------------------------------------------------------------------------------
```

#### 7:
#### More assertion with handy place holders

- Link: [See test cases folder](https://github.com/authorjapps/helpme/tree/master/zerocode-rest-help/src/test/resources/tests/00_sample_test_scenarios)


#### 8:
#### REST endpoint calls with General Place holders


- Link: [See test cases folder](https://github.com/authorjapps/helpme/tree/master/zerocode-rest-help/src/test/resources/tests/00_sample_test_scenarios)


#### 9:
#### Step dealing with arrays

- Link: [See test cases folder](https://github.com/authorjapps/helpme/tree/master/zerocode-rest-help/src/test/resources/tests/00_sample_test_scenarios)

##### Finding the occurance of an element in the array response
e.g. your actual response is like below, 
Your use-case is, `Dan` and `Mike` might not be returned in the same order always, but they appear only once in the array.
```
Url: "/api/v1/screening/persons",
Operation: "GET",
Response: 
{
                "status": 200,
                "body": {
                    "type" : "HIGH-VALUE",
                    "persons":[
                        {
                            "id": "120.100.80.03",
                            "name": "Dan"
                        },
                        {
                            "id": "120.100.80.11",
                            "name": "Mike"
                        }
                    ]
                }
}
```
To assert the above situation, you can find the element using `JSON path` as below and verify 'Dan' was returned only once in the array and 'Emma' was present in the 'persons' array.
(See more JSON paths [here](https://github.com/json-path/JsonPath))
```
{
    "scenarioName": "Scenario- Get all person details",
    "steps": [
        {
            "name": "get_screening_details",
            "url": "/api/v1/screening/persons",
            "operation": "GET",
            "request": {
            },
            "assertions": {
                "status": 200,
                "body": {
                    "type": "HIGH-VALUE",
                    "persons.SIZE": 2,
                    "persons[?(@.name=='Dan')].id.SIZE": 1,
                    "persons[?(@.name=='Mike')].id.SIZE": 1,
                    "persons[?(@.name=='Emma')].id.SIZE": 0
                }
            }
        }
    ]
}
```
What `persons[?(@.name=='Dan')].id.SIZE` means is-
> In the `persons` array check every element with the name `Dan`, if found pick the `id` of element and return all of the `id`s as an array, then do `.SIZE` on the `id`s array and return a count.

Note-
Even if a single matching element is found, the return is always an array type. Also if you do a `.length()` on the returned `id`s e.g. `persons[?(@.name=='Dan')].id.length()`, that's also an array i.e. `[2]` instead of simple `2`. That's how JSON path behaves. Hence `.SIZE` helps to achieve this.

Run [the above test case](https://github.com/authorjapps/consumer-contract-tests/blob/master/src/test/resources/contract_tests/screeningservice/find_element_in_array_via_jsonpath.json) from [here - testFindElementInArray()](https://github.com/authorjapps/consumer-contract-tests/blob/master/src/test/java/org/jsmart/zerocode/testhelp/tests/screeningservice/ScreeningServiceContractTest.java).

#### 10:
#### Chaining multiple steps for a scenario

Chaining steps: Multi-Step REST calls with earlier response(IDs etc) as input to next step

```javaScript
{
    "scenarioName": "12_chaining_multiple_steps_using_previous_response",
    "steps": [
        {
            "name": "create_new_employee",
            "url": "http://localhost:9998/google-emp-services/home/employees",
            "operation": "POST",
            "request": {},
            "assertions": {
                "status": 201,
                "body": {
                    "id": 1000
                }
            }
        },
        {
            "name": "get_and_verify_created_employee",
            "url": "http://localhost:9998/google-emp-services/home/employees/${$.create_new_employee.response.body.id}", //<--- ID from previous response //
            "operation": "GET",
            "request": {},
            "assertions": {
                "status": 200,
                "body": {
                    "id": 1000,
                    "name": "${$.create_new_employee.response.body.name}",
                    "addresses": [
                        {
                            "gpsLocation": "${$.create_new_employee.response.body.addresses[0].gpsLocation}"
                        },
                        {
                            "gpsLocation": "${$.create_new_employee.response.body.addresses[1].gpsLocation}"
                        }
                    ]
                }
            }
        }
    ]
}
```

- Example : [Scenario with two steps - 1st create and then get](https://github.com/authorjapps/helpme/blob/master/zerocode-rest-help/src/test/resources/tests/00_sample_test_scenarios/12_chaining_multiple_steps_with_prev_response.json)
- Link: [See test cases folder](https://github.com/authorjapps/helpme/tree/master/zerocode-rest-help/src/test/resources/tests/00_sample_test_scenarios)


#### 11:
#### Generating random strings, random numbers and static strings

Random UUID-
```javaScript
{
  "scenarioName": "random_UUID",
  "steps": [
    {
      "name": "create_new_employee",
      "url": "http://localhost:9998/google-emp-services/home/employees",
      "operation": "POST",
      "request": {
        "body": {
          "id": "${RANDOM.UUID}", //<-- Everytime it creates unique uuid. See below example.
          "name": "Elen M"   
        }
      },
      "assertions": {
        "status": 201
      }
    }
  ]
}

Resolves to-
{
  "scenarioName": "random_UUID",
  "steps": [
    {
      "name": "create_new_employee",
      "url": "http://localhost:9998/google-emp-services/home/employees",
      "operation": "POST",
      "request": {
        "body": {
          "id": "94397df8-0e9e-4479-a2f9-9af509fb5998", //<-- Every time it runs, it creates an unique uuid
          "name": "Elen M"   
        }
      },
      "assertions": {
        "status": 201
      }
    }
  ]
}
```

Random String of specific length-
```javaScript
{
  "scenarioName": "13_random_and_static_string_number_place_holders",
  "steps": [
    {
      "name": "create_new_employee",
      "url": "http://localhost:9998/google-emp-services/home/employees",
      "operation": "POST",
      "request": {
        "body": {
          "id": 1000,
          "name": "Larry ${RANDOM.STRING:5}",   //<-- Random number of length 5 chars
          "password": "${RANDOM.STRING:10}"     //<-- Random number of length 10 chars
        }
      },
      "assertions": {
        "status": 201
      }
    }
  ]
}
```

resolves to the below POST request to the end point:
```javaScript
step:create_new_employee
url:http://localhost:9998/google-emp-services/home/employees
method:POST
request:
{
  "body" : {
    "id" : 1000,
    "name" : "Larry tzezq",
    "password" : "czljtmzotu"
  }
} 

```

See full log in the log file, looks like this:
```javaScript
requestTimeStamp:2016-08-01T15:37:20.555
step:create_new_employee
url:http://localhost:9998/google-emp-services/home/employees
method:POST
request:
{
  "body" : {
    "id" : 1000,
    "name" : "Larry tzezq",
    "password" : "czljtmzotu"
  }
} 

Response:
{
  "status" : 201,
  ...
}
*responseTimeStamp:2016-08-01T15:37:20.707 
*Response delay:152.0 milli-secs 
---------> Assertion: <----------
{
  "status" : 201
} 
-done-
 
--------- RELATIONSHIP-ID: 4cfd3bfb-a537-49a2-84a2-0457c4e65803 ---------
requestTimeStamp:2016-08-01T15:37:20.714
step:again_try_to_create_employee_with_same_name_n_password
url:http://localhost:9998/google-emp-services/home/employees
method:POST
request:
{
  "body" : {
    "id" : 1000,
    "name" : "Larry tzezq",
    "password" : "czljtmzotu"
  }
} 
--------- RELATIONSHIP-ID: 4cfd3bfb-a537-49a2-84a2-0457c4e65803 ---------
Response:
{
  "status" : 201,
  ...
}
*responseTimeStamp:2016-08-01T15:37:20.721 
*Response delay:7.0 milli-secs 
---------> Assertion: <----------
{
  "status" : 201
} 
-done-

```

- Link: [See test cases folder](https://github.com/authorjapps/helpme/tree/master/zerocode-rest-help/src/test/resources/tests/00_sample_test_scenarios)


#### 12:
#### Asserting general and exception messages

Asserting with $CONTAINS.STRING:

```javaScript
{
      ...
      ...
      "assertions": {
        "status": 200,
        "body": {
          "name": "$CONTAINS.STRING:Larry"   //<-- PASS: If the "name" field in the response contains "Larry".
        }
      }
}
```

- Similar way exception messages can be asserted for part or full message.

- Link: [See test cases folder](https://github.com/authorjapps/helpme/tree/master/zerocode-rest-help/src/test/resources/tests/00_sample_test_scenarios)


#### 13:
#### Asserting with $GT or $LT

$GT.<any_number>

```javaScript
{
  ...
  ...
  "assertions": {
    "status": "$GT.198"   //<--- PASS: 200 is greater than 198
  }
}

```

$LT.<any_number>
```javaScript
{
  ...
  ...
  "assertions": {
      "status": "$LT.500"   //<--- PASS: 200 is lesser than 500
  }
}

```

- Link: [See full examples](https://github.com/authorjapps/helpme/tree/master/zerocode-rest-help/src/test/resources/tests/00_sample_test_scenarios)


#### 14:
#### Asserting an empty array with $[]

```javaScript
    {
      ...
      ...
      "assertions": {
        "status": 200,
        "body": {
          "id": "$NOT.NULL",
          "vehicles": "$[]"         //<--- PASS: if the response has empty "vehicles"
        }
      }
    }
```

- Link: [See full examples](https://github.com/authorjapps/helpme/tree/master/zerocode-rest-help/src/test/resources/tests/00_sample_test_scenarios)

#### 141:
#### Asserting an array SIZE
If your response contains the below:
```
e.g. http response body:
{
                "results": [
                    {
                        "id": 1,
                        "name": "Elon Musk"
                    },
                    {
                        "id": 2,
                        "name": "Jeff Bezos"
                    }
                ]
}
```

Then you can assert many ways for the desired result-

```javaScript
        {
	    ...
            "assertions": {
                "results.SIZE": 2
            }
        }

-or-
        {
	    ...
            "assertions": {
                "results.SIZE": "$GT.1"
            }
        }
-or-
        {
	    ...
            "assertions": {
                "results.SIZE": "$LT.3"
            }
        }
etc
```

See more SIZE examples [here](https://github.com/authorjapps/zerocode-hello-world/blob/master/src/test/resources/helloworld_array_size/hello_world_array_size_assertions_test.json) in the [hello-world repo](https://github.com/authorjapps/zerocode-hello-world).


#### 15:
#### Calling java methods(apis) for doing specific tasks:
+ Sample tests are [here](https://github.com/authorjapps/zerocode-hello-world/blob/master/src/test/java/org/jsmart/zerocode/testhelp/tests/helloworldjavaexec/HelloWorldJavaMethodExecTest.java)
    + Example of request response as JSON - [See here](https://github.com/authorjapps/zerocode-hello-world/blob/master/src/test/resources/helloworldjavaexec/hello_world_javaexec_req_resp_as_json.json)
    + Example of passing a simple string e.g. SQL query etc - [See here](https://github.com/authorjapps/zerocode-hello-world/blob/master/src/test/resources/helloworldjavaexec/hello_world_java_method_return_assertions.json)

- You can clone and execute from this repo [here](https://github.com/authorjapps/zerocode-hello-world)

```javaScript
{
    "scenarioName": "Java method return as JSON assertions",
    "steps": [
        {
            "name": "execute_java_method",
            "url": "org.jsmart.zerocode.zerocodejavaexec.DbSqlExecutor", //<--- class name
            "operation": "fetchDbCustomers",              //<-- method name
            "request": "select id, name from customers",     //<--- parameter to the method
            "assertions": {
                "dbResults": [
                    {
                        "id": 1,
                        "name": "Elon Musk"
                    },
                    {
                        "id": 2,
                        "name": "Jeff Bezos"
                    }
                ]

            }
        }
    ]
}
```

In case of - Java method request, response as JSON:
```javaScript
{
    "scenarioName": "Java method request, response as JSON",
    "steps": [
        {
            "name": "execute_java_method",
            "url": "org.jsmart.zerocode.zerocodejavaexec.OrderCreator",
            "operation": "createOrder",
            "request": {
                "itemName" : "Mango",
                "quantity" : 15000
            },
            "assertions": {
                "orderId" : 1020301,
                "itemName" : "Mango",
                "quantity" : 15000
            }
        }
    ]
}
```

Sample Java class and method used in the above step-
```java
public class OrderCreator {

    public Order createOrder(Order order){
        /**
         * TODO- Suppose you process the "order" received, and finally return the "orderProcessed".
         * Here it is hardcoded for simplicity and understanding purpose only
         */
	 
        Order orderProcessed = new Order(1020301, order.getItemName(), order.getQuantity());

        return orderProcessed;
    }
}
```
Order pojo looks like below, [full pojo src here](https://github.com/authorjapps/zerocode-hello-world/blob/master/src/main/java/org/jsmart/zerocode/zerocodejavaexec/pojo/Order.java)-
```java
public class Order {
    private Integer orderId;
    private String itemName;
    private Long quantity;

    @JsonCreator
    public Order(
            @JsonProperty("orderId")Integer orderId,
            @JsonProperty("itemName")String itemName,
            @JsonProperty("quantity")Long quantity) {
        this.orderId = orderId;
        this.itemName = itemName;
        this.quantity = quantity;
    }

    public Integer getOrderId() {
        return orderId;
    }

    public String getItemName() {
        return itemName;
    }

    public Long getQuantity() {
        return quantity;
    }

```


More examples here-

- Link: [See here an example test](https://github.com/authorjapps/helpme/blob/master/zerocode-rest-help/src/test/resources/tests/00_sample_test_scenarios/11_execute_local_java_program.json)

- Link: [All examples root folder](https://github.com/authorjapps/helpme/blob/master/zerocode-rest-help/src/test/resources/tests/00_sample_test_scenarios)


#### 16:
#### Overriding with Custom HttpClient with Project demand

See here how to Use SSL HttpClient : [See usage of @UseHttpClient](https://github.com/authorjapps/zerocode/blob/master/src/test/java/org/jsmart/zerocode/core/verification/SslTrustUseHttpClientTest.java)

See here custom one : [See usage of @UseHttpClient](https://github.com/authorjapps/helpme/blob/master/zerocode-rest-help/src/test/java/org/jsmart/zerocode/testhelp/zcmore/ZeroCodeUnitRunnerWithCustomHttpClient.java)

e.g.
```java
@UseHttpClient(SslTrustHttpClient.class)
@TargetEnv("hosts_ci.properties")
@RunWith(ZeroCodeUnitRunner.class)
public class SslTrustUseHttpClientTest {

    @Test
    @JsonTestCase("foo/bar/test_case_file.json")
    public void testASmartTestCase_createUpdate() throws Exception {

    }
}
```

#### 17:
#### Externalizing RESTful host and port into properties file(s).

Note:
Each runner is capable of running with a properties file which can have host and port for specific to this runner.
- So one can have a single properties file per runner which means you can run the tests against multiple environments
-OR-
- can have a single properties file shared across all the runners means all tests run against the same environment.

** Note - As per Latest config update, we have updated endpoint configuration fields.
From the release 1.2.8 onwards we will be allowing `web.` and deprecating `restful.` in endpoint configurations.
We will take away support for `restful.` from endpoint configuration in the future releases.
Version 1.2.8 will work for both as we have made the framework backward compatible.

e.g.

"config_hosts_sample.properties"

```
web.application.endpoint.host=http://{host-name-or-ip}

web.application.endpoint.port=9998

web.application.endpoint.context=/google-emp-services
```

The runner looks like this:
```
@TargetEnv("config_hosts_sample.properties")
@RunWith(ZeroCodeUnitRunner.class)
public class ScreeningServiceContractTest {

    @Test
    @JsonTestCase("contract_tests/screeningservice/get_screening_details_by_custid.json")
    public void testScreeningLocalAndGlobal() throws Exception {
    }
}
```

- See example here of a test scenario: [hello-world test scenario](https://github.com/authorjapps/zerocode-hello-world/blob/master/src/test/resources/helloworld/hello_world_status_ok_assertions.json)
```
{
    "scenarioName": "GIVEN- the GitHub REST api, WHEN- I invoke GET, THEN- I will receive the 200 status with body",
    "steps": [
        {
            "name": "get_user_details",
            "url": "/users/octocat",
            "operation": "GET",
            "request": {
            },
            "assertions": {
                "status": 200,
                "body": {
                    "login" : "octocat",
                    "type" : "User"
                }
            }
        }
    ]
}
```

- See tests here using `ZeroCodeUnitRunner.class`: [hello-world via JUnit @Test](https://github.com/authorjapps/zerocode-hello-world/blob/master/src/test/java/org/jsmart/zerocode/testhelp/tests/helloworld/JustHelloWorldTest.java)
```
@TargetEnv("github_host.properties")
@RunWith(ZeroCodeUnitRunner.class)
public class JustHelloWorldTest {

    @Test
    @JsonTestCase("helloworld/hello_world_status_ok_assertions.json")
    public void testGet() throws Exception {

    }
}
```

- See tests here using `ZeroCodePackageRunner.class`: [hello-world suite](https://github.com/authorjapps/zerocode-hello-world/blob/master/src/test/java/org/jsmart/zerocode/testhelp/tests/HelloWorldGitHubSuite.java)
```
@TargetEnv("github_host.properties")
@UseHttpClient(SslTrustHttpClient.class) //<--- Optional, Needed for https/ssl connections.
@RunWith(ZeroCodePackageRunner.class)
@TestPackageRoot("helloworld_github_REST_api") //<--- Root of the package to pick all tests including sub-folders
public class HelloWorldGitHubSuite {

}
```

- See tests here using `@RunWith(Suite.class)`: [Contract-test suite](https://github.com/authorjapps/consumer-contract-tests/blob/master/src/test/java/org/jsmart/zerocode/testhelp/tests/ContractTestSuite.java)
```
@Suite.SuiteClasses({
        RegulatoryServiceContractTest.class,
        IdCheckServiceContractTest.class,
        CorpLoanServiceContractTest.class,
        ScreeningServiceContractTest.class
})
@RunWith(Suite.class)
public class ContractTestSuite {

}
```



#### 18:
#### Generating IDs and sharing across steps

- [See a running example](https://github.com/authorjapps/helpme/tree/master/zerocode-rest-help/src/test/resources/tests/01_vanila_placeholders)


#### 19:
#### Bare JSON String, still a valid JSON

- [See a running example](https://github.com/authorjapps/helpme/blob/master/zerocode-rest-help/src/test/resources/tests/00_sample_test_scenarios/14_bare_string_json.json)


#### 20:
#### Passing Headers to the REST API

- [See a running example](https://github.com/authorjapps/helpme/blob/master/zerocode-rest-help/src/test/resources/tests/00_sample_test_scenarios/16_passing_headers_to_rest_apis.json)


#### 21:
#### Passing environment param via Jenkins and dynamically picking environment specific properties file in CI
- [See a running example of passing envronment param and value](https://github.com/authorjapps/helpme/blob/master/zerocode-rest-help/src/test/java/org/jsmart/zerocode/testhelp/tests/EnvPropertyHelloWorldTest.java)
```java
package org.jsmart.zerocode.testhelp.tests;

import org.jsmart.zerocode.core.domain.EnvProperty;
import org.jsmart.zerocode.core.domain.JsonTestCase;
import org.jsmart.zerocode.core.domain.TargetEnv;
import org.jsmart.zerocode.core.runner.ZeroCodeUnitRunner;
import org.junit.Test;
import org.junit.runner.RunWith;

@EnvProperty("_${env}") //any meaningful string e.g. `env.name` or `envName` or `app.env` etc
@TargetEnv("hello_world_host.properties")
@RunWith(ZeroCodeUnitRunner.class)
public class EnvPropertyHelloWorldTest {

    @Test
    @JsonTestCase("hello_world/hello_world_get.json")
    public void testRunAgainstConfigPropertySetViaJenkins() throws Exception {
        
    }
}

/**
 Set "env=ci" in Jenkins (or via .profile in a Unix machine, System/User properties in Windows)
 then the runner picks "hello_world_host_ci.properties" and runs.
 if -Denv=sit, then runner looks for and picks "hello_world_host_sit.properties" and runs.

If `env` not supplied, then defaults to "hello_world_host.properties" which by default mentioned mentioned via @TargetEnv
 
 -or-
 
 Configure the below `mvn goal` when you run via Jenkins goal in the specific environment e.g. -
 
 For CI :
 mvn clean install -Denv=ci
 
 For SIT:
 mvn clean install -Denv=sit
 
 and make sure:
 hello_world_host_ci.properties and hello_world_host_sit.properties etc are available in the resources folder or class path.
 */
```


#### 22:

#### LocalDate and LocalDateTime format example

```javaScript
{
	"id": 1000,
	"createdDay": "${LOCAL.DATE.TODAY:yyyy-MM-dd}",
	"createdDayTimeStamp": "${LOCAL.DATETIME.NOW:yyyy-MM-dd'T'HH:mm:ss.nnnnnnnnn}",
	"randomUniqueValue": "${LOCAL.DATETIME.NOW:yyyyMMdd'T'HHmmssnnnnnnnnn}"
}

resolved to ===> below date and datetime

{
	"id": 1000,
	"createdDay": "2018-02-14",
	"createdDayTimeStamp": "2018-02-14T21:52:45.180000000",
	"randomUniqueValue": "20180214T215245180000000"
}

```

e.g formats:
```
output: 2018-02-11  // "uuuu-MM-dd"
output: 2018 02 11  // "uuuu MM dd"
output: 2018        // "yyyy"
output: 2018-Feb-11 // "uuuu-MMM-dd"
output: 2018-02-11  // "uuuu-LL-dd"
Default: date.toString(): 2018-02-11
```

Note:
`uuuu` prints same as `yyyy`

```
output: 2018-02-11T21:31:21.041000000    // "uuuu-MM-dd'T'HH:mm:ss.SSSSSSSSS"
output: 2018-02-11T21:31:21.41000000     // "uuuu-MM-dd'T'HH:mm:ss.n"
output: 2018-02-11T21:31:21.041000000    // "uuuu-MM-dd'T'HH:mm:ss.nnnnnnnnn"
output: 2018-02-11T21:31:21.77481041     // "uuuu-MM-dd'T'HH:mm:ss.A"
output: 2018-02-14                       // "uuuu-MM-dd" or "yyyy-MM-dd"
Default: date.toString(): 2018-02-11T21:31:20.989          // .toString()
```
### See here more-
https://docs.oracle.com/javase/8/docs/api/java/time/format/DateTimeFormatter.html

```
     H       hour-of-day (0-23)          number            0
     m       minute-of-hour              number            30
     s       second-of-minute            number            55
     S       fraction-of-second          fraction          978
     A       milli-of-day                number            1234
     n       nano-of-second              number            987654321
     N       nano-of-day                 number            1234000000
```
All letters 'A' to 'Z' and 'a' to 'z' are reserved as pattern letters. The following pattern letters are defined:
```
 Symbol  Meaning                     Presentation      Examples
 ------  -------                     ------------      -------
 G       era                         text              AD; Anno Domini; A
 u       year                        year              2004; 04
 y       year-of-era                 year              2004; 04
 D       day-of-year                 number            189
 M/L     month-of-year               number/text       7; 07; Jul; July; J
 d       day-of-month                number            10
```

#### 23:

#### SOAP method invocation example with xml input

You can invoke SOAP as below which is already supported by zerocode lib, or you can write your own SOAP executor using Java(if 
you want to, but you don't have to). 
(If you want- Then, in the README file go to section -> "Calling java methods(apis) for specific tasks" )

```javaScript
{
    "scenarioName": "GIVEN a SOAP end poinr WHEN I invoke a method with a request XML, THEN I will ge the SOAP response in XML",
    "steps": [
        {
            "name": "invoke_currency_conversion",
            "url": "http://<target-domain.com>/<path etc>",
            "operation": "POST",
            "request": {
                "headers": {
                    "Content-Type": "text/xml; charset=utf-8",
                    "SOAPAction": "<get this from WSDL file, this has the port or method or action name in the url>"
                    //"SOAPAction": "\"<or wrap it in double quotes as some SOAP servers understand it>\""
                },
                "body": "escaped request XML message ie the soap:Envelope message"
                -or- // pick from- src/test/resources/soap_requests/xml_files/soap_request.xml
                "body": "${XML.FILE:soap_requests/xml_files/soap_request.xml}" 
            },
            "assertions": {
                "status": 200
            }
        }
    ]
}
```

e.g. below-
This example invokes a free SOAP service over internet.
Note:
If this service is down, the invocation might fail.
So better to test against an available SOAP service to you or a local stub service.

```javaScript
{
    "scenarioName": "GIVEN a SOAP end point WHEN I invoke a method with a request XML, THEN I will get response in XML",
    "steps": [
        {
            "name": "invoke_currency_conversion",
            "url": "http://www.webservicex.net/CurrencyConvertor.asmx",
            "operation": "POST",
            "request": {
                "headers": {
                    "Content-Type": "text/xml; charset=utf-8",
                    "SOAPAction": "http://www.webserviceX.NET/ConversionRate"
                    //"SOAPAction": "\"http://www.webserviceX.NET/ConversionRate\""
                },
                "body": "<?xml version=\"1.0\" encoding=\"utf-8\"?>\n<soap:Envelope xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" xmlns:soap=\"http://schemas.xmlsoap.org/soap/envelope/\">\n  <soap:Body>\n    <ConversionRate xmlns=\"http://www.webserviceX.NET/\">\n      <FromCurrency>AFA</FromCurrency>\n      <ToCurrency>GBP</ToCurrency>\n    </ConversionRate>\n  </soap:Body>\n</soap:Envelope>"
                // -or- 
                // "body": "${XML.FILE:soap_requests/xml_files/soap_request.xml}"
            },
            "assertions": {
                "status": 200
            }
        }
    ]
}
```

You should received the below-
```
Response:
{
  "status" : 200,
  "headers" : {
    "Date" : [ "Fri, 16 Feb 2018 05:38:27 GMT" ],
    "Server" : [ "Microsoft-IIS/7.0" ]
  },
  
  "rawBody" : "<?xml version=\"1.0\" encoding=\"utf-8\"?><soap:Envelope xmlns:soap=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\"><soap:Body><ConversionRateResponse xmlns=\"http://www.webserviceX.NET/\"><ConversionRateResult>-1</ConversionRateResult></ConversionRateResponse></soap:Body></soap:Envelope>"
}
*responseTimeStamp:2018-02-16T05:38:35.254
*Response delay:653.0 milli-secs
 ```


#### 24:
#### SOAP method invocation where Corporate Proxy enabled
You need to use a HttpClient ie override the BasicHttpClient and set proxies to it as below-
```java
        Step-1)
        CredentialsProvider credsProvider = createProxyCredentialsProvider(proxyHost, proxyPort, proxyUserName, proxyPassword);

        Step-2)
        HttpHost proxy = new HttpHost(proxyHost, proxyPort);
 
        Step-3) method Step-1
        private CredentialsProvider createProxyCredentialsProvider(String proxyHost, int proxyPort, String proxyUserName, String proxyPassword) {

                CredentialsProvider credsProvider = new BasicCredentialsProvider();
        
                credsProvider.setCredentials(
        
                        new AuthScope(proxyHost, proxyPort),
        
                        new UsernamePasswordCredentials(proxyUserName, proxyPassword));
        
                return credsProvider;
        }
 
        Step-4) 
        Set the values from Step-1 and Step-2
        
        HttpClients.custom()

                .setSSLContext(sslContext)

                .setSSLHostnameVerifier(new NoopHostnameVerifier())

                .setDefaultCookieStore(cookieStore)

                .setDefaultCredentialsProvider(credsProvider)    //<------------- From Step-1

                .setProxy(proxy)                                 //<------------- From Step-2

                .build();
```

You can inject the Corporate Proxy details to the custom {{HttpClient}} li below from a config file simply by annotating 
the key names from the host config file which is used by the runner for mentioning host and port.
e.g. below:
See an example here-
https://github.com/authorjapps/zerocode/blob/master/src/main/java/org/jsmart/zerocode/core/httpclient/soap/SoapCorporateProxySslHttpClient.java

Usage example here:
https://github.com/authorjapps/zerocode/blob/master/src/test/java/org/jsmart/zerocode/core/soap/SoapCorpProxySslHttpClientTest.java

How to use?
```java
@UseHttpClient(SoapCorporateProxySslHttpClient.class)
@TargetEnv("soap_host_with_corp_proxy.properties")
@RunWith(ZeroCodeUnitRunner.class)
public class SoapCorpProxySslHttpClientTest {

    @Ignore
    @Test
    @JsonTestCase("foo/bar/soap_test_case_file.json")
    public void testSoapWithCorpProxyEnabled() throws Exception {

    }
}
```

Explanation below- 

```java
@TargetEnv("hello_world_host.properties")
@RunWith(ZeroCodeUnitRunner.class)
public class HelloWorldTest {
     // @Test
     // tests here
}

soap_host_with_corp_proxy.properties
---------------------------
# Web Server host and port
web.application.endpoint.host=https://soap-server-host/ServiceName
web.application.endpoint.port=443

# Web Service context; Leave it blank in case you do not have a common context
web.application.endpoint.context=

#sample test purpose - if you remove this from ehre, then make sure to remove from Java file
corporate.proxy.host=http://exam.corporate-proxy-host.co.uk
corporate.proxy.port=80
corporate.proxy.username=HAVYSTARUSER
corporate.proxy.password=i#am#here#for#soap#


Your HttpClient:
----------------
See-
https://hc.apache.org/httpcomponents-client-ga/httpclient/examples/org/apache/http/examples/client/ClientProxyAuthentication.java

public class YourHttpClient {

    @Inject
    @Named("corporate.proxy.host")
    private String proxyHost;

    @Inject
    @Named("corporate.proxy.port")
    private String proxyPort;

    @Inject
    @Named("corporate.proxy.username")
    private String proxyUserName;

    @Inject
    @Named("corporate.proxy.password")
    private String proxyPassword;

    // Build the client using these.
}
```

#### 25:
#### MIME Type Converters- XML to JSON, prettyfy XML etc
e.g.
##### xmlToJson
```javaScript
{
            "name": "xml_to_json",
            "url": "org.jsmart.zerocode.converter.MimeTypeConverter",
            "operation": "xmlToJson",
            "request": "<?xml version=\"1.0\" encoding=\"utf-8\"?>\n<soap:Envelope xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" xmlns:soap=\"http://schemas.xmlsoap.org/soap/envelope/\">\n  <soap:Body>\n    <ConversionRate xmlns=\"http://www.webserviceX.NET/\">\n      <FromCurrency>AFA</FromCurrency>\n      <ToCurrency>GBP</ToCurrency>\n    </ConversionRate>\n  </soap:Body>\n</soap:Envelope>",
            "assertions": {
                "soap:Envelope": {
                    "xmlns:xsd": "http://www.w3.org/2001/XMLSchema",
                    "xmlns:soap": "http://schemas.xmlsoap.org/soap/envelope/",
                    "xmlns:xsi": "http://www.w3.org/2001/XMLSchema-instance",
                    "soap:Body": {
                        "ConversionRate": {
                            "xmlns": "http://www.webserviceX.NET/",
                            "FromCurrency": "AFA",
                            "ToCurrency": "GBP"
                        }
                    }
                }
            }
        }
```

##### jsonToJson
Various input and output. Depending upon the usecase, you can use that method.

```javaScript
{
    "scenarioName": "Given a json string or json block, convert to equivalent json block",
    "steps": [
        {
            "name": "json_block_to_json",
            "url": "org.jsmart.zerocode.converter.MimeTypeConverter",
            "operation": "jsonBlockToJson",
            "request": {
                "headers": {
                    "hdrX": "valueX"
                },
                "body": {
                    "id": 1001,
                    "addresses": [
                        {
                            "postCode": "PXY"
                        },
                        {
                            "postCode": "LMZ DDD"
                        }
                    ]
                }
            },
            "assertions": {
                "headers": {
                    "hdrX": "valueX"
                },
                "body": {
                    "id": 1001,
                    "addresses": [
                        {
                            "postCode": "PXY"
                        },
                        {
                            "postCode": "${$.json_block_to_json.request.body.addresses[1].postCode}"
                        }
                    ]
                }
            }
        },
        {
            "name": "json_to_json",
            "url": "org.jsmart.zerocode.converter.MimeTypeConverter",
            "operation": "jsonToJson",
            "request": "${$.json_block_to_json.request.headers}",
            "assertions": {
                "hdrX": "valueX"
            }
        },
        {
            "name": "body_json_to_json",
            "url": "org.jsmart.zerocode.converter.MimeTypeConverter",
            "operation": "jsonToJson",
            "request": "${$.json_block_to_json.request.body}",
            "assertions": {
                "id": 1001,
                "addresses": [
                    {
                        "postCode": "PXY"
                    },
                    {
                        "postCode": "LMZ DDD"
                    }
                ]
            }
        },
        {
            "name": "json_node_to_json",
            "url": "org.jsmart.zerocode.converter.MimeTypeConverter",
            "operation": "jsonBlockToJson",
            "request": {
                "headers": {
                    "hdrX": "valueX"
                },
                "body": {
                    "id": 1001,
                    "addresses": [
                        {
                            "postCode": "PXY"
                        }
                    ]
                }
            },
            "assertions": {
                "headers": {
                    "hdrX": "valueX"
                },
                "body": {
                    "id": 1001,
                    "addresses": [
                        {
                            "postCode": "${$.json_block_to_json.request.body.addresses[0].postCode}"
                        }
                    ]
                }
            }
        }
    ]
}
```
Available methods are- 
* xmlToJson
* jsonToJson
* jsonBlockToJson
* jsonNodeToJson
* prettyXml

#### 26:
#### Using WireMock for mocking dependent end points
See Issue #47 for the scenarios when WireMock becomes handy. 
See examples here- 
https://github.com/authorjapps/zerocode-hello-world/blob/master/src/test/resources/wiremock_tests/mock_via_wiremock_then_test_the_end_point.json


The below JSON block step will mock two end points using WireMock.
1. GET: /api/v1/amazon/customers/UK001   (no headers)
2. GET: /api/v1/amazon/customers/cust-007  (with headers)

```javaScript
        {
            "name": "setup_mocks",
            "url": "/$MOCK",
            "operation": "$USE.WIREMOCK",
            "request": {
                "mocks": [
                    {
                        "name": "mocking_a_GET_endpoint",
                        "operation": "GET",
                        "url": "/api/v1/amazon/customers/UK001",
                        "response": {
                            "status": 200,
                            "headers": {
                                "Accept": "application/json"
                            },
                            "body": {
                                "id": "UK001",
                                "name": "Adam Smith",
                                "Age": "33"
                            }
                        }
                    },
                    {
                        "name": "mocking_a_GET_endpoint_with_headers",
                        "operation": "GET",
                        "url": "/api/v1/amazon/customers/cust-007",
                        "request": {
                            "headers": {
                                "api_key": "key-01-01",
                                "api_secret": "secret-01-01"
                            }
                        },
                        "response": {
                            "status": 200,
                            "body": {
                                "id": "cust-007",
                                "type": "Premium"
                            }
                        }
                    }
                ]
            },
            "assertions": {
                "status": 200
            }
        }

```

#### 28:
#### Http Basic authentication step using zerocode
+ How can I do basic http authentication in ZeroCode ?
   + Ans: You can do this in so many ways, it depends on your project requirement. Most simplest one is to pass the base64 basicAuth in the request headers as below - e.g. `USERNAME/PASSWORD` as `charaanuser/passtwitter`

Note-
Zerocode framework helps you to achieve this, but has nothing to do with Basic-Auth. It uses `Apache Http Client` behind the scenes, this means whatever you can do using `Apache Http Client`, you can do it simply using `Zerocode`.

+ Positive scenario
```javaScript
{
    "name": "get_book_using_basic_auth",
    "url": "http://localhost:8088/api/v1/white-papers/WP-001",
    "operation": "GET",
    "request": {
        "headers": {
            "Authorization": "Basic Y2hhcmFhbnVzZXI6cGFzc3R3aXR0ZXI=" // You can generate this using Postman or java code
        }
    },
    "assertions": {
        "status": 200, // 401 - if unauthorised. See negatibe test below
        "body": {
            "id": "WP-001",
            "type": "pdf",
            "category": "Mule System API"
        }
    }
}        
```

+ Negative scenario
```
{
    "name": "get_book_using_wrong_auth",
    "url": "http://localhost:8088/api/v1/white-papers/WP-001",
    "operation": "GET",
    "request": {
        "headers": {
            "Authorization": "Basic aWRONG-PASSWORD"
        }
    },
    "assertions": {
        "status": 401 //401(or simillar code whatever the server responds), you can assert here.
        "body": {
            "message": "Unauthorised" 
        }
    }
}        
```
+ If your requirement is to put basic auth for all the API tests e.g. GET, POST, PUT, DELETE etc commonly in the regression suite, then you can put this `"Authorization"` header into your SSL client code. 
You can refer to an example [test here](https://github.com/authorjapps/consumer-contract-tests/blob/master/src/test/java/org/jsmart/zerocode/testhelp/tests/basicauth/BasicAuthContractTest.java).

+ In your custom http client, you add the header to the request at one place, which is common to all the API tests.
See: `org.jsmart.zerocode.httpclient.CorpBankApcheHttpClient#addBasicAuthHeader` in the [http-client code](https://github.com/authorjapps/consumer-contract-tests/blob/master/src/main/java/org/jsmart/zerocode/httpclient/CorpBankApcheHttpClient.java) it uses.

#### 29:
#### Sending query params in URL or separately
You can pass query params in the usual way in the URL e.g. `?page=1&page_size=5` -or-
You can pass them in the request as below.
```
...
            "request": {
                "queryParams":{
                    "page":1,
                    "per_page":6
                }
            }
...
```
See below both the examples( See this in the hello-world repo in action i.e. the [Test-Case](https://github.com/authorjapps/zerocode-hello-world/blob/master/src/test/resources/helloworld_queryparams/github_get_repos_by_query_params.json) and the [JUnit Test](https://github.com/authorjapps/zerocode-hello-world/blob/master/src/test/java/org/jsmart/zerocode/testhelp/tests/helloworldqueryparams/HelloWorldQueryParamsTest.java) )
```
{
    "scenarioName": "Git Hub GET API - Fetch by queryParams",
    "steps": [
        {
            "name": "get_repos_by_query",
            "url": "https://api.github.com/users/octocat/repos?page=1&per_page=6",
            "operation": "GET",
            "request": {
            },
            "assertions": {
                "status": 200,
                "body.SIZE": 6
            }
        },
        {
            "name": "get_repos_by_query_params",
            "url": "https://api.github.com/users/octocat/repos",
            "operation": "GET",
            "request": {
                "queryParams":{
                    "page":1,
                    "per_page":6
                }
            },
            "assertions": {
                "status": 200,
                "body.SIZE": 6
            }
        },
        {
            "name": "get_all_reposs_without_query", // without the query params, which fetches everything.
            "url": "https://api.github.com/users/octocat/repos",
            "operation": "GET",
            "request": {
            },
            "assertions": {
                "status": 200,
                "body.SIZE": 8
            }
        }
    ]
}
```


#### 99:
#### Place holders for End Point Mocking

| Place Holder  | Output        | More  |
| ------------- |:-------------| -----|
| /$MOCK       | Signifies that this step will be used for mocking end points | Start with a front slash |
| $USE.WIREMOCK      | Framework will use wiremock APIs to mock the end points defined in "mocks" section | Can use other mechanisms e.g. local REST api simulators |

#### General place holders

| Place Holder  | Output        | More  |
| ------------- |:-------------| -----|
| ${RANDOM.NUMBER}       | Replaces with a random number | Random number is generated using current timestamp in milli-sec |
| ${RANDOM.UUID}       | Replaces with a random UUID | Random number is generated using java.util.UUID e.g. 077e6162-3b6f-4ae2-a371-2470b63dgg00 |
| ${RANDOM.STRING:10}       | Replaces a random string consists of ten english alpphabets | The length can be dynamic |
| ${RANDOM.STRING:4}       | Replaces with a random string consists of four english alpphabets | The length can be dynamic |
| ${STATIC.ALPHABET:5}       | Replaces with abcde ie Static string of length 5| String starts from "a" and continues, repeats after "z"|
| ${STATIC.ALPHABET:7}       | Replaces with abcdefg ie Static string of length 7| String starts from a"" and continues, repeats after "z"|
| ${LOCAL.DATE.TODAY:yyyy-MM-dd}       | Resolves this today's date in the format yyyy-MM-dd or any suppliedformat| See format examples here https://github.com/authorjapps/helpme/blob/master/zerocode-rest-help/src/test/resources/tests/00_sample_test_scenarios/18_date_and_datetime_today_generator.json |
| ${LOCAL.DATETIME.NOW:yyyy-MM-dd'T'HH:mm:ss.nnnnnnnnn}       | Resolves this today's datetime stamp in any supplied format| See format examples here https://github.com/authorjapps/helpme/blob/master/zerocode-rest-help/src/test/resources/tests/00_sample_test_scenarios/18_date_and_datetime_today_generator.json |

#### Assertion place holders

| Place Holder  | Output        | More  |
| ------------- |:-------------| -----|
| $NOT.NULL       | Assertion passes if a not null value was present in the response | Otherwise fails |
| $NULL      | Assertion passes if a null value was present in the response | Otherwise fails |
| $[]       | Assertion passes if an empty array was present in the response | Otherwise fails |
| $EQ.99       | Assertion passes if a numeric value equals to 99 was present in the response | Can be any int, long, float etc |
| $NOT.EQ.99       | Assertion passes if a numeric value is not equals to 99 was present in the response | Can be any int, long, float etc |
| $GT.99       | Assertion passes if a value greater than 99 was present in the response | Can be any int, long, float etc |
| $LT.99       | Assertion passes if a value lesser than 99 was present in the response | Can be any int, long, float etc |
| $CONTAINS.STRING:id was cust-001       | Assertion passes if the node response contains string "id was cust-001" | Otherwise fails |
| $CONTAINS.STRING.IGNORECASE:id WaS CuSt-001       | Assertion passes if the response value contains string "id was cust-001" with case insensitive | Otherwise fails |
| $MATCHES.STRING:`\\d{4}-\\d{2}-\\d{2}`       | Assertion passes if the response value contains e.g. `"1989-07-09"` matching regex `\\d{4}-\\d{2}-\\d{2}` | Otherwise fails |

#### Assertion Path holders

| Place Holder  | Output        | More  |
| ------------- |:-------------| -----|
| $<path.to.array>.SIZE       | e.g. `"persons.SIZE" : 3` - Assertion passes if the array size matches with value(3) | Search for `dealing with arrays` in this README for more usages |
| $<path.to.array>.SIZE       | e.g. `"persons.SIZE" : "$GT.2"` - Assertion passes if the array size is greater than the value(2) | Search for `dealing with arrays` in this README for more usages |
| $<path.to.array>.SIZE       | e.g. `"persons.SIZE" : "$LT.4"` - Assertion passes if the array size is lesser than the value(4) | Search for `dealing with arrays` in this README for more usages |


#### 100:
#### Video tutorials
* [RESTful testing with test cases in JSON](https://youtu.be/nSWq5SuyqxE) - YouTube
* [Zerocode - Simple and powerful testing library - HelloWorld](https://www.youtube.com/watch?v=YCV1cqGt5e0) - YouTube
* [Zerocode Query Params Demo](https://www.youtube.com/watch?v=a7JhwMxVcCM) - YouTube

#### References, Dicussions and articles
* [Performance testing using JUnit and maven](https://www.codeproject.com/Articles/1251046/How-to-do-performance-testing-using-JUnit-and-Mave) - Codeproject
* [REST API or SOAP End Point Testing](https://www.codeproject.com/Articles/1242569/REST-API-or-SOAP-End-Point-Testing-with-ZeroCode-J) - Codeproject
* [DZone- MuleSoft API Testing With Zerocode Test Framework](https://dzone.com/articles/zerocode-test-framework-for-restsoap-api-tddbdd-ap) - DZone
* [Testing need not be harder or slower, it should be easier and faster](https://dzone.com/articles/rest-api-testing-using-the-zerocode-json-based-bdd) - DZone




[Zerocode UUID or Zerocode uuid random UUID]: https://github.com/authorjapps/zerocode#zerocode

[restful web services]: https://github.com/authorjapps/zerocode
[restful]: https://github.com/authorjapps/zerocode
[what is automation testing]: https://github.com/authorjapps/zerocode
[rest client]: https://github.com/authorjapps/zerocode
[qa and testing]: https://github.com/authorjapps/zerocode
[bdd testing]: https://github.com/authorjapps/zerocode
[test url]: https://github.com/authorjapps/zerocode
[api performance testing]: https://github.com/authorjapps/zerocode
[java test automation tools]: https://github.com/authorjapps/zerocode
[test automation framework]: https://github.com/authorjapps/zerocode
[black box testing]: https://github.com/authorjapps/zerocode
[software testing framework]: https://github.com/authorjapps/zerocode
[https test]: https://github.com/authorjapps/zerocode
[automation framework]: https://github.com/authorjapps/zerocode
[regression]: https://github.com/authorjapps/zerocode
[Zerocode zero code JSON based testing test cases]: https://github.com/authorjapps/zerocode
[functional testing]: https://github.com/authorjapps/zerocode
[postman rest client]: https://github.com/authorjapps/zerocode
[java automated testing tools]: https://github.com/authorjapps/zerocode
[qa automation]: https://github.com/authorjapps/zerocode
[soap ui testing]: https://github.com/authorjapps/zerocode
[rest api testing]: https://github.com/authorjapps/zerocode
[cucumber framework]: https://github.com/authorjapps/zerocode
[cucumber java]: https://github.com/authorjapps/zerocode
[bdd]: https://github.com/authorjapps/zerocode
[microservices example]: https://github.com/authorjapps/zerocode
[microservices testing]: https://github.com/authorjapps/zerocode
[Performance testing]: https://github.com/authorjapps/zerocode
[junit performance testing]: https://github.com/authorjapps/zerocode
[feature files]: https://github.com/authorjapps/zerocode
[restful web services example]: https://github.com/authorjapps/zerocode
[open source api testing tools]: https://github.com/authorjapps/zerocode
[automated app testing]: https://github.com/authorjapps/zerocode
[api contract testing]: https://github.com/authorjapps/zerocode
[json compare]: https://github.com/authorjapps/zerocode
[spring boot integration test]: https://github.com/authorjapps/zerocode
[json assertion]: https://github.com/authorjapps/zerocode
[mulesoft api]: https://github.com/authorjapps/zerocode

