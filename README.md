## REST BDD - ZeroCode Testing Framework
[![Build Status](https://travis-ci.org/authorjapps/zerocode.svg?branch=master)](https://travis-ci.org/authorjapps/zerocode)

Execute your complex business scenario steps with simple jsons which defines your RESTful service behaviour

### Easy! Simple! Readable! JSON Based!

#### Define a Scenario with [Given, When, Then] Steps, Then Run. See examples next to the table


### examples:

#### 1:
Download this help and usage project to try it yourself.

Link: https://github.com/authorjapps/helpme/tree/master/zerocode-rest-help

Baby steps: https://github.com/authorjapps/helpme/blob/master/zerocode-rest-help/README.md

Git Clone: https://github.com/authorjapps/helpme.git

#### 2:
A scenario might consists of one or more steps. Let's start with single step Test Case:
```
{
  "scenarioName": "Vanilla - Will Get Google Web Bath Room", 
  "steps": [
    {
      "name": "step1_get_google_web_bath_room",
      "url": "http://localhost:9999/google-bath-services/home/bathroom/999",
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
The above JSON block is a test case where we asked the BDD framework to hit the 
> REST end point : http://localhost:9999/google-bath-services/home/bathroom/999

> with method: GET

> and asserting the REST response with an 

> expected status: 200

> where, step "name" is a meaningful step name, which is significant when multiple steps are run. See a multi-step example.


Note:
> scenarioname : is free text

> step name: text without any space


The above Test Case will PASS as the end point actually available is as below. Look at the response the end point returns.
```
    {
      "operation": "GET",
      "url": "/google-bath-services/home/bathroom/999",
      "response": {
        "status": 200,
        "body": {
          "id": 999,
          "name": "Shower-Basics",
          "availability": true,
          "rooms":[
            {
              "name": "Bed Room"
            },
            {
              "name": "Guest Room"
            }
          ]
        }
      }
    }
```

The following Test Case will fail. Why? 

Because you are asserting with an expected status as 500, but the end point actually returns 200.

```
{
  "scenarioName": "Vanilla - Will Get Google Web Bath Room",
  "steps": [
    {
      "name": "step1_get_google_web_bath_room",
      "url": "http://localhost:9999/google-bath-services/home/bathroom/999",
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


#### 3:
Single step with more assertions

```
{
    "scenarioName": "Vanilla - Will Get Google Web Bath Room",
    "steps": [
        {
            "name": "step1_get_google_web_bath_room",
            "url": "http://localhost:9999/google-bath-services/home/bathroom/999",
            "operation": "GET",
            "request": {},
            "assertions": {
                "status": 200,
                "body": {
                    "id": 999,
                    "name": "Shower-Basics",
                    "availability": true,
                    "rooms.SIZE": 2
                }
            }
        }
    ]
}
```

The above Test Case will PASS as the assertions section has all expected values matching the end point's response.

#### 4:
Running with step _loop_
Usage: See here: [Step loop](https://github.com/authorjapps/helpme/blob/master/zerocode-rest-help/src/test/resources/tests/00_sample_test_scenarios/02_using_step_loop.json)

```
{
  "scenarioName": "Vanilla - Execute multiple times - Step",
  "steps": [
    {
      "loop": 2,
      "name": "get_room_details",
      "url": "http://localhost:9999/google-bath-services/home/bathroom/101",
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
Running with scenario _loop_
Usage: See here: [Scenario loop](https://github.com/authorjapps/helpme/blob/master/zerocode-rest-help/src/test/resources/tests/00_sample_test_scenarios/03_using_scenario_loop.json)
Runs the entire scenario two times i.e. executing both the steps once for each time.

```
{
  "scenarioName": "Vanilla - Execute multiple times - Scenario",
  "loop": 2,
  "steps": [
    {
      "name": "get_room_details",
      "url": "http://localhost:9999/google-bath-services/home/bathroom/101",
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
      "url": "http://localhost:9999/google-bath-services/home/bathroom/102",
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
Generated test statistics reports

Spike Chart: See here e.g. 

[1. All results Delay Spike - Chart](http://htmlpreview.github.io/?https://github.com/authorjapps/helpme/blob/master/zerocode-rest-help/src/test/resources/zz_reports/zerocode_results_chart_2016-07-30T09-55-53.056.html)

[2. More Test Case results Delay Spike - Chart](http://htmlpreview.github.io/?https://github.com/authorjapps/helpme/blob/master/zerocode-rest-help/src/test/resources/zz_reports/zerocode_results_chart_more_2016-07-30T09-57-53.0567.html)

CSV Report: See here : [Full coverage CSV report](https://github.com/authorjapps/helpme/blob/master/zerocode-rest-help/src/test/resources/zz_reports/zerocode_full_report_2016-07-30T11-44-14.512.csv)


#### 4:
Step with more assertions place holders


#### 5:
Step with more general place holders


#### 5:
Step dealing with arrays


#### 6:
Multi Step running with earlier response output


#### 6:
Generating static and random IDs with available place holders


#### 7:
Asserting with $CONTAINS.STRING


#### 8:
Asserting with $GT.99


#### 9:
Asserting empty array with $[]


#### Place holders for End Point Mocking

| Place Holder  | Output        | More  |
| ------------- |:-------------| -----|
| /$MOCK       | Signifies that this step will be used for mocking end points | Start with a front slash |
| $USE.WIREMOCK      | Framework will use wiremock APIs to mock the end points defined in "mocks" section | Can use other mechanisms e.g. local simulators |

#### General place holders

| Place Holder  | Output        | More  |
| ------------- |:-------------| -----|
| ${RANDOM.NUMBER}       | Replaces with a random number | Random number is generated using current timestamp in milli-sec |
| ${RANDOM.STRING:10}       | Replaces a random string consists of ten english alpphabets | The length can be dynamic |
| ${RANDOM.STRING:4}       | Replaces with a random string consists of four english alpphabets | The length can be dynamic |
| ${STATIC.ALPHABET:5}       | Replaces with abcde ie Static string of length 5| String starts from "a" and continues, repeats after "z"|
| ${STATIC.ALPHABET:7}       | Replaces with abcdefg ie Static string of length 7| String starts from a"" and continues, repeats after "z"|

#### Assertion place holders

| Place Holder  | Output        | More  |
| ------------- |:-------------| -----|
| $NOT.NULL       | Assertion passes if a not null value was present in the response | Otherwise fails |
| $NULL      | Assertion passes if a null value was present in the response | Otherwise fails |
| $[]       | Assertion passes if an empty array was present in the response | Otherwise fails |
| $GT.99       | Assertion passes if a value greater than 99 was present in the response | Can be any int, long, float etc |
| $LT.99       | Assertion passes if a value lesser than 99 was present in the response | Can be any int, long, float etc |
| $CONTAINS.STRING:can not create       | Assertion passes if the node value conatins string "can not create" in the response | Otherwise fails |

#### Assertion Path holders

| Place Holder  | Output        | More  |
| ------------- |:-------------| -----|
| $<path.to.array>.SIZE       | Assertion passes if the array size matches with value | See usage in the test json |








