package edu.stanford.spezi.questionnaire.composables

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import edu.stanford.spezi.questionnaire.QuestionnaireComposable
import edu.stanford.spezi.questionnaire.QuestionnaireResult

@Composable
fun QuestionnaireTestComposable() {
    val result =
        remember { mutableStateOf<QuestionnaireResult?>(null) }
    val resultValue = result.value
    if (resultValue != null) {
        Column {
            when (resultValue) {
                is QuestionnaireResult.Completed -> {
                    Text("Completed")
                }

                QuestionnaireResult.Failed -> {
                    Text("Failed")
                }

                QuestionnaireResult.Cancelled -> {
                    Text("Cancelled")
                }
            }
            Button(onClick = {
                result.value = null
            }) {
                Text("Reset")
            }
        }
    } else {
        QuestionnaireComposable(
            questionnaireJson = testQuestionnaireJson,
            onResult = { result.value = it }
        )
    }
}

const val testQuestionnaireJson = """
{
  "title": "KCCQ-12",
  "resourceType": "Questionnaire",
  "language": "en-US",
  "status": "draft",
  "publisher": "Stanford Biodesign Digital Health",
  "meta": {
    "profile": [
      "http://spezi.health/fhir/StructureDefinition/sdf-Questionnaire"
    ],
    "tag": [
      {
        "system": "urn:ietf:bcp:47",
        "code": "en-US",
        "display": "English"
      }
    ]
  },
  "useContext": [
    {
      "code": {
        "system": "http://hl7.org/fhir/ValueSet/usage-context-type",
        "code": "focus",
        "display": "Clinical Focus"
      },
      "valueCodeableConcept": {
        "coding": [
          {
            "system": "urn:oid:2.16.578.1.12.4.1.1.8655",
            "display": "KCCQ-12"
          }
        ]
      }
    }
  ],
  "contact": [
    {
      "name": "http://spezi.health"
    }
  ],
  "subjectType": [
    "Patient"
  ],
  "url": "http://spezi.health/fhir/questionnaire/9528ccc2-d1be-4c4c-9c3c-19f78e51ec19",
  "item": [
    {
      "linkId": "73490535-203f-44b2-d1b7-7c0a786c16f9",
      "type": "display",
      "text": "The following questions refer to your heart failure and how it may affect your life. Please read and complete the following questions. There are no right or wrong answers. Please mark the answer that best applies to you.\n\nThese results will be send to your care team.",
      "required": false
    },
    {
      "linkId": "c0b3bef6-1e2d-4621-d82e-b73069574dc4",
      "type": "group",
      "item": [
        {
          "linkId": "a459b804-35bf-4792-f1eb-0b52c4e176e1",
          "type": "choice",
          "text": "Showering/bathing",
          "required": true,
          "answerOption": [
            {
              "valueCoding": {
                "id": "c973b297-4561-4b8c-c8c6-fb559ca15169",
                "code": "1",
                "system": "urn:uuid:8290e1d8-8141-4982-deb9-57f9d2e13a14",
                "display": "Extremely Limited"
              }
            },
            {
              "valueCoding": {
                "id": "1a8c0f4f-12a7-458d-8881-0a0750117ff0",
                "code": "2",
                "system": "urn:uuid:8290e1d8-8141-4982-deb9-57f9d2e13a14",
                "display": "Quite a bit Limited"
              }
            },
            {
              "valueCoding": {
                "id": "1e372729-1834-41c4-8cda-9a8196a77971",
                "code": "3",
                "system": "urn:uuid:8290e1d8-8141-4982-deb9-57f9d2e13a14",
                "display": "Moderately Limited"
              }
            },
            {
              "valueCoding": {
                "id": "cb3c686a-42a0-4c68-8b8d-f2621129adef",
                "code": "4",
                "system": "urn:uuid:8290e1d8-8141-4982-deb9-57f9d2e13a14",
                "display": "Slightly Limited"
              }
            },
            {
              "valueCoding": {
                "id": "4c04162f-e529-406b-a0fa-d636ad1bf6d7",
                "code": "5",
                "system": "urn:uuid:8290e1d8-8141-4982-deb9-57f9d2e13a14",
                "display": "Not at all Limited"
              }
            },
            {
              "valueCoding": {
                "id": "c4f351f5-33d6-477a-f9ba-f68a1ae0df6d",
                "code": "6",
                "system": "urn:uuid:8290e1d8-8141-4982-deb9-57f9d2e13a14",
                "display": "Limited for other reasons or did not do the activity"
              }
            }
          ]
        },
        {
          "linkId": "cf9c5031-1ed5-438a-fc7d-dc69234015a0",
          "type": "choice",
          "text": "Walking 1 block on level ground",
          "required": true,
          "answerOption": [
            {
              "valueCoding": {
                "id": "c973b297-4561-4b8c-c8c6-fb559ca15169",
                "code": "1",
                "system": "urn:uuid:8290e1d8-8141-4982-deb9-57f9d2e13a14",
                "display": "Extremely Limited"
              }
            },
            {
              "valueCoding": {
                "id": "1a8c0f4f-12a7-458d-8881-0a0750117ff0",
                "code": "2",
                "system": "urn:uuid:8290e1d8-8141-4982-deb9-57f9d2e13a14",
                "display": "Quite a bit Limited"
              }
            },
            {
              "valueCoding": {
                "id": "1e372729-1834-41c4-8cda-9a8196a77971",
                "code": "3",
                "system": "urn:uuid:8290e1d8-8141-4982-deb9-57f9d2e13a14",
                "display": "Moderately Limited"
              }
            },
            {
              "valueCoding": {
                "id": "cb3c686a-42a0-4c68-8b8d-f2621129adef",
                "code": "4",
                "system": "urn:uuid:8290e1d8-8141-4982-deb9-57f9d2e13a14",
                "display": "Slightly Limited"
              }
            },
            {
              "valueCoding": {
                "id": "4c04162f-e529-406b-a0fa-d636ad1bf6d7",
                "code": "5",
                "system": "urn:uuid:8290e1d8-8141-4982-deb9-57f9d2e13a14",
                "display": "Not at all Limited"
              }
            },
            {
              "valueCoding": {
                "id": "c4f351f5-33d6-477a-f9ba-f68a1ae0df6d",
                "code": "6",
                "system": "urn:uuid:8290e1d8-8141-4982-deb9-57f9d2e13a14",
                "display": "Limited for other reasons or did not do the activity"
              }
            }
          ]
        },
        {
          "linkId": "1fad0f81-b2a9-4c8f-9a78-4b2a5d7aef07",
          "type": "choice",
          "text": "Hurrying or jogging (as if to catch a bus)",
          "required": true,
          "answerOption": [
            {
              "valueCoding": {
                "id": "c973b297-4561-4b8c-c8c6-fb559ca15169",
                "code": "1",
                "system": "urn:uuid:8290e1d8-8141-4982-deb9-57f9d2e13a14",
                "display": "Extremely Limited"
              }
            },
            {
              "valueCoding": {
                "id": "1a8c0f4f-12a7-458d-8881-0a0750117ff0",
                "code": "2",
                "system": "urn:uuid:8290e1d8-8141-4982-deb9-57f9d2e13a14",
                "display": "Quite a bit Limited"
              }
            },
            {
              "valueCoding": {
                "id": "1e372729-1834-41c4-8cda-9a8196a77971",
                "code": "3",
                "system": "urn:uuid:8290e1d8-8141-4982-deb9-57f9d2e13a14",
                "display": "Moderately Limited"
              }
            },
            {
              "valueCoding": {
                "id": "cb3c686a-42a0-4c68-8b8d-f2621129adef",
                "code": "4",
                "system": "urn:uuid:8290e1d8-8141-4982-deb9-57f9d2e13a14",
                "display": "Slightly Limited"
              }
            },
            {
              "valueCoding": {
                "id": "4c04162f-e529-406b-a0fa-d636ad1bf6d7",
                "code": "5",
                "system": "urn:uuid:8290e1d8-8141-4982-deb9-57f9d2e13a14",
                "display": "Not at all Limited"
              }
            },
            {
              "valueCoding": {
                "id": "c4f351f5-33d6-477a-f9ba-f68a1ae0df6d",
                "code": "6",
                "system": "urn:uuid:8290e1d8-8141-4982-deb9-57f9d2e13a14",
                "display": "Limited for other reasons or did not do the activity"
              }
            }
          ]
        }
      ],
      "required": false,
      "text": "Heart failure affects different people in different ways. Some feel shortness of breath while others feel fatigue. Please indicate how much you are limited by heart failure (shortness of breath or fatigue) in your ability to do the following activities over the past 2 weeks."
    },
    {
      "linkId": "692bda7d-a616-43d1-8dc6-8291f6460ab2",
      "type": "choice",
      "text": "Over the past 2 weeks, how many times did you have swelling in your feet, ankles or legs when you woke up in the morning?",
      "required": true,
      "answerOption": [
        {
          "valueCoding": {
            "id": "80df26e0-55a6-4d50-90de-b60cd851c4fb",
            "code": "1",
            "system": "urn:uuid:2b2f9a9a-e721-495a-82d6-fbc1b22a27b2",
            "display": "Every morning"
          }
        },
        {
          "valueCoding": {
            "id": "473f99f2-b9d2-4e92-85ed-ca78f3e0141c",
            "code": "2",
            "system": "urn:uuid:2b2f9a9a-e721-495a-82d6-fbc1b22a27b2",
            "display": "3 or more times per week but not every day"
          }
        },
        {
          "valueCoding": {
            "id": "e3eab124-44f9-43a4-b1f6-e42b0199e39b",
            "code": "3",
            "system": "urn:uuid:2b2f9a9a-e721-495a-82d6-fbc1b22a27b2",
            "display": "1-2 times per week"
          }
        },
        {
          "valueCoding": {
            "id": "253f487a-f26b-442f-8757-8a2401465fed",
            "code": "4",
            "system": "urn:uuid:2b2f9a9a-e721-495a-82d6-fbc1b22a27b2",
            "display": "Less than once a week"
          }
        },
        {
          "valueCoding": {
            "id": "7de45367-76b2-4b9b-8e4e-050bae6b309c",
            "code": "5",
            "system": "urn:uuid:2b2f9a9a-e721-495a-82d6-fbc1b22a27b2",
            "display": "Never over the past 2 weeks"
          }
        }
      ]
    },
    {
      "linkId": "b1734b9e-1d16-4238-8556-5ae3fa0ba913",
      "type": "choice",
      "text": "Over the past 2 weeks, on average, how many times has fatigue limited your ability to do what you wanted",
      "required": true,
      "answerOption": [
        {
          "valueCoding": {
            "id": "80df26e0-55a6-4d50-90de-b60cd851c4fb",
            "code": "1",
            "system": "urn:uuid:2b2f9a9a-e721-495a-82d6-fbc1b22a27b2",
            "display": "All of the time"
          }
        },
        {
          "valueCoding": {
            "id": "473f99f2-b9d2-4e92-85ed-ca78f3e0141c",
            "code": "2",
            "system": "urn:uuid:2b2f9a9a-e721-495a-82d6-fbc1b22a27b2",
            "display": "Several times per day"
          }
        },
        {
          "valueCoding": {
            "id": "e3eab124-44f9-43a4-b1f6-e42b0199e39b",
            "code": "3",
            "system": "urn:uuid:2b2f9a9a-e721-495a-82d6-fbc1b22a27b2",
            "display": "At least once a day"
          }
        },
        {
          "valueCoding": {
            "id": "253f487a-f26b-442f-8757-8a2401465fed",
            "code": "4",
            "system": "urn:uuid:2b2f9a9a-e721-495a-82d6-fbc1b22a27b2",
            "display": "3 or more times per week but not every day"
          }
        },
        {
          "valueCoding": {
            "id": "7de45367-76b2-4b9b-8e4e-050bae6b309c",
            "code": "5",
            "system": "urn:uuid:2b2f9a9a-e721-495a-82d6-fbc1b22a27b2",
            "display": "1-2 times per week"
          }
        },
        {
          "valueCoding": {
            "id": "e174ffbc-23d4-4c44-94c5-1864bf2afa18",
            "code": "6",
            "system": "urn:uuid:2b2f9a9a-e721-495a-82d6-fbc1b22a27b2",
            "display": "Less than once a week"
          }
        },
        {
          "valueCoding": {
            "id": "853fb552-dd2c-48f6-86b9-1bb923d7fd2d",
            "code": "7",
            "system": "urn:uuid:2b2f9a9a-e721-495a-82d6-fbc1b22a27b2",
            "display": "Never over the past 2 weeks"
          }
        }
      ]
    },
    {
      "linkId": "57f37fb3-a0ad-4b1f-844e-3f67d9b76946",
      "type": "choice",
      "text": "Over the past 2 weeks, on average, how many times has shortness of breath limited your ability to do what you wanted",
      "required": true,
      "answerOption": [
        {
          "valueCoding": {
            "id": "80df26e0-55a6-4d50-90de-b60cd851c4fb",
            "code": "1",
            "system": "urn:uuid:2b2f9a9a-e721-495a-82d6-fbc1b22a27b2",
            "display": "All of the time"
          }
        },
        {
          "valueCoding": {
            "id": "473f99f2-b9d2-4e92-85ed-ca78f3e0141c",
            "code": "2",
            "system": "urn:uuid:2b2f9a9a-e721-495a-82d6-fbc1b22a27b2",
            "display": "Several times per day"
          }
        },
        {
          "valueCoding": {
            "id": "e3eab124-44f9-43a4-b1f6-e42b0199e39b",
            "code": "3",
            "system": "urn:uuid:2b2f9a9a-e721-495a-82d6-fbc1b22a27b2",
            "display": "At least once a day"
          }
        },
        {
          "valueCoding": {
            "id": "253f487a-f26b-442f-8757-8a2401465fed",
            "code": "4",
            "system": "urn:uuid:2b2f9a9a-e721-495a-82d6-fbc1b22a27b2",
            "display": "3 or more times per week but not every day"
          }
        },
        {
          "valueCoding": {
            "id": "7de45367-76b2-4b9b-8e4e-050bae6b309c",
            "code": "5",
            "system": "urn:uuid:2b2f9a9a-e721-495a-82d6-fbc1b22a27b2",
            "display": "1-2 times per week"
          }
        },
        {
          "valueCoding": {
            "id": "e174ffbc-23d4-4c44-94c5-1864bf2afa18",
            "code": "6",
            "system": "urn:uuid:2b2f9a9a-e721-495a-82d6-fbc1b22a27b2",
            "display": "Less than once a week"
          }
        },
        {
          "valueCoding": {
            "id": "853fb552-dd2c-48f6-86b9-1bb923d7fd2d",
            "code": "7",
            "system": "urn:uuid:2b2f9a9a-e721-495a-82d6-fbc1b22a27b2",
            "display": "Never over the past 2 weeks"
          }
        }
      ]
    },
    {
      "linkId": "396164df-d045-4c56-d710-513297bdc6f2",
      "type": "choice",
      "text": "Over the past 2 weeks, on average, how many times have you been forced to sleep sitting up in a chair or with at least 3 pillows to prop you up because of shortness of breath?",
      "required": true,
      "answerOption": [
        {
          "valueCoding": {
            "id": "80df26e0-55a6-4d50-90de-b60cd851c4fb",
            "code": "1",
            "system": "urn:uuid:2b2f9a9a-e721-495a-82d6-fbc1b22a27b2",
            "display": "Every night"
          }
        },
        {
          "valueCoding": {
            "id": "473f99f2-b9d2-4e92-85ed-ca78f3e0141c",
            "code": "2",
            "system": "urn:uuid:2b2f9a9a-e721-495a-82d6-fbc1b22a27b2",
            "display": "3 or more times per week but not every day"
          }
        },
        {
          "valueCoding": {
            "id": "e3eab124-44f9-43a4-b1f6-e42b0199e39b",
            "code": "3",
            "system": "urn:uuid:2b2f9a9a-e721-495a-82d6-fbc1b22a27b2",
            "display": "1-2 times per week"
          }
        },
        {
          "valueCoding": {
            "id": "253f487a-f26b-442f-8757-8a2401465fed",
            "code": "4",
            "system": "urn:uuid:2b2f9a9a-e721-495a-82d6-fbc1b22a27b2",
            "display": "Less than once a week"
          }
        },
        {
          "valueCoding": {
            "id": "7de45367-76b2-4b9b-8e4e-050bae6b309c",
            "code": "5",
            "system": "urn:uuid:2b2f9a9a-e721-495a-82d6-fbc1b22a27b2",
            "display": "Never over the past 2 weeks"
          }
        }
      ]
    },
    {
      "linkId": "75e3f62e-e37d-48a2-f4d9-af2db8922da0",
      "type": "choice",
      "text": "Over the past 2 weeks, how much has your heart failure limited your enjoyment of life?",
      "required": true,
      "answerOption": [
        {
          "valueCoding": {
            "id": "80df26e0-55a6-4d50-90de-b60cd851c4fb",
            "code": "1",
            "system": "urn:uuid:2b2f9a9a-e721-495a-82d6-fbc1b22a27b2",
            "display": "It has extremely limited my enjoyment of life"
          }
        },
        {
          "valueCoding": {
            "id": "473f99f2-b9d2-4e92-85ed-ca78f3e0141c",
            "code": "2",
            "system": "urn:uuid:2b2f9a9a-e721-495a-82d6-fbc1b22a27b2",
            "display": "It has limited my enjoyment of life quite a bit"
          }
        },
        {
          "valueCoding": {
            "id": "e3eab124-44f9-43a4-b1f6-e42b0199e39b",
            "code": "3",
            "system": "urn:uuid:2b2f9a9a-e721-495a-82d6-fbc1b22a27b2",
            "display": "It has moderately limited my enjoyment of life"
          }
        },
        {
          "valueCoding": {
            "id": "253f487a-f26b-442f-8757-8a2401465fed",
            "code": "4",
            "system": "urn:uuid:2b2f9a9a-e721-495a-82d6-fbc1b22a27b2",
            "display": "It has slightly limited my enjoyment of life"
          }
        },
        {
          "valueCoding": {
            "id": "7de45367-76b2-4b9b-8e4e-050bae6b309c",
            "code": "5",
            "system": "urn:uuid:2b2f9a9a-e721-495a-82d6-fbc1b22a27b2",
            "display": "It has not limited my enjoyment of life"
          }
        }
      ]
    },
    {
      "linkId": "fce3a16e-c6d8-4bac-8ab5-8f4aee4adc08",
      "type": "choice",
      "text": "If you had to spend the rest of your life with your heart failure the way it is right now, how would you feel about this?",
      "required": true,
      "answerOption": [
        {
          "valueCoding": {
            "id": "80df26e0-55a6-4d50-90de-b60cd851c4fb",
            "code": "1",
            "system": "urn:uuid:2b2f9a9a-e721-495a-82d6-fbc1b22a27b2",
            "display": "Not at all satisfied"
          }
        },
        {
          "valueCoding": {
            "id": "473f99f2-b9d2-4e92-85ed-ca78f3e0141c",
            "code": "2",
            "system": "urn:uuid:2b2f9a9a-e721-495a-82d6-fbc1b22a27b2",
            "display": "Mostly dissatisfied"
          }
        },
        {
          "valueCoding": {
            "id": "e3eab124-44f9-43a4-b1f6-e42b0199e39b",
            "code": "3",
            "system": "urn:uuid:2b2f9a9a-e721-495a-82d6-fbc1b22a27b2",
            "display": "Somewhat satisfied"
          }
        },
        {
          "valueCoding": {
            "id": "253f487a-f26b-442f-8757-8a2401465fed",
            "code": "4",
            "system": "urn:uuid:2b2f9a9a-e721-495a-82d6-fbc1b22a27b2",
            "display": "Mostly satisfied"
          }
        },
        {
          "valueCoding": {
            "id": "7de45367-76b2-4b9b-8e4e-050bae6b309c",
            "code": "5",
            "system": "urn:uuid:2b2f9a9a-e721-495a-82d6-fbc1b22a27b2",
            "display": "Completely satisfied"
          }
        }
      ]
    },
    {
      "linkId": "8649bc8c-f908-487d-87a4-a97106b1a4c3",
      "type": "group",
      "item": [
        {
          "linkId": "8b022e69-127d-4447-8190-39ac645e60e1",
          "type": "choice",
          "text": "Hobbies, recreational activities",
          "required": true,
          "answerOption": [
            {
              "valueCoding": {
                "id": "5df54746-3a58-4153-8f07-1f13fdc09902",
                "code": "1",
                "system": "urn:uuid:90ab9a5a-0ed7-43e0-9131-75ab9d8b94cf",
                "display": "Severely Limited"
              }
            },
            {
              "valueCoding": {
                "id": "a57b3a46-a005-4204-a07f-b2d535a6d5ab",
                "code": "2",
                "system": "urn:uuid:90ab9a5a-0ed7-43e0-9131-75ab9d8b94cf",
                "display": "Limited quite a bit"
              }
            },
            {
              "valueCoding": {
                "id": "632d08fb-b521-4511-8887-b7c375d901f3",
                "code": "3",
                "system": "urn:uuid:90ab9a5a-0ed7-43e0-9131-75ab9d8b94cf",
                "display": "Moderately limited"
              }
            },
            {
              "valueCoding": {
                "id": "fcbe715a-b86c-4fdc-8855-8aaa353d06d7",
                "code": "4",
                "system": "urn:uuid:90ab9a5a-0ed7-43e0-9131-75ab9d8b94cf",
                "display": "Slightly limited"
              }
            },
            {
              "valueCoding": {
                "id": "d9cf6b00-5fed-48ac-a2be-a2e29688c5fa",
                "code": "5",
                "system": "urn:uuid:90ab9a5a-0ed7-43e0-9131-75ab9d8b94cf",
                "display": "Did not limit at all"
              }
            },
            {
              "valueCoding": {
                "id": "8a20b488-aad4-4676-86d3-c494b979db3f",
                "code": "6",
                "system": "urn:uuid:90ab9a5a-0ed7-43e0-9131-75ab9d8b94cf",
                "display": "Does not apply or did not do for other reasons"
              }
            }
          ]
        },
        {
          "linkId": "1eee7259-da1c-4cba-80a9-e67e684573a1",
          "type": "choice",
          "text": "Working or doing household chores",
          "required": true,
          "answerOption": [
            {
              "valueCoding": {
                "id": "5df54746-3a58-4153-8f07-1f13fdc09902",
                "code": "1",
                "system": "urn:uuid:90ab9a5a-0ed7-43e0-9131-75ab9d8b94cf",
                "display": "Severely Limited"
              }
            },
            {
              "valueCoding": {
                "id": "a57b3a46-a005-4204-a07f-b2d535a6d5ab",
                "code": "2",
                "system": "urn:uuid:90ab9a5a-0ed7-43e0-9131-75ab9d8b94cf",
                "display": "Limited quite a bit"
              }
            },
            {
              "valueCoding": {
                "id": "632d08fb-b521-4511-8887-b7c375d901f3",
                "code": "3",
                "system": "urn:uuid:90ab9a5a-0ed7-43e0-9131-75ab9d8b94cf",
                "display": "Moderately limited"
              }
            },
            {
              "valueCoding": {
                "id": "fcbe715a-b86c-4fdc-8855-8aaa353d06d7",
                "code": "4",
                "system": "urn:uuid:90ab9a5a-0ed7-43e0-9131-75ab9d8b94cf",
                "display": "Slightly limited"
              }
            },
            {
              "valueCoding": {
                "id": "d9cf6b00-5fed-48ac-a2be-a2e29688c5fa",
                "code": "5",
                "system": "urn:uuid:90ab9a5a-0ed7-43e0-9131-75ab9d8b94cf",
                "display": "Did not limit at all"
              }
            },
            {
              "valueCoding": {
                "id": "8a20b488-aad4-4676-86d3-c494b979db3f",
                "code": "6",
                "system": "urn:uuid:90ab9a5a-0ed7-43e0-9131-75ab9d8b94cf",
                "display": "Does not apply or did not do for other reasons"
              }
            }
          ]
        },
        {
          "linkId": "883a22a8-2f6e-4b41-84b7-0028ed543192",
          "type": "choice",
          "text": "Visiting family or friends out of your home",
          "required": true,
          "answerOption": [
            {
              "valueCoding": {
                "id": "5df54746-3a58-4153-8f07-1f13fdc09902",
                "code": "1",
                "system": "urn:uuid:90ab9a5a-0ed7-43e0-9131-75ab9d8b94cf",
                "display": "Severely Limited"
              }
            },
            {
              "valueCoding": {
                "id": "a57b3a46-a005-4204-a07f-b2d535a6d5ab",
                "code": "2",
                "system": "urn:uuid:90ab9a5a-0ed7-43e0-9131-75ab9d8b94cf",
                "display": "Limited quite a bit"
              }
            },
            {
              "valueCoding": {
                "id": "632d08fb-b521-4511-8887-b7c375d901f3",
                "code": "3",
                "system": "urn:uuid:90ab9a5a-0ed7-43e0-9131-75ab9d8b94cf",
                "display": "Moderately limited"
              }
            },
            {
              "valueCoding": {
                "id": "fcbe715a-b86c-4fdc-8855-8aaa353d06d7",
                "code": "4",
                "system": "urn:uuid:90ab9a5a-0ed7-43e0-9131-75ab9d8b94cf",
                "display": "Slightly limited"
              }
            },
            {
              "valueCoding": {
                "id": "d9cf6b00-5fed-48ac-a2be-a2e29688c5fa",
                "code": "5",
                "system": "urn:uuid:90ab9a5a-0ed7-43e0-9131-75ab9d8b94cf",
                "display": "Did not limit at all"
              }
            },
            {
              "valueCoding": {
                "id": "8a20b488-aad4-4676-86d3-c494b979db3f",
                "code": "6",
                "system": "urn:uuid:90ab9a5a-0ed7-43e0-9131-75ab9d8b94cf",
                "display": "Does not apply or did not do for other reasons"
              }
            }
          ]
        }
      ],
      "required": false,
      "text": "How much does your heart failure affect your lifestyle? Please indicate how your heart failure may have limited your participation in the following activities over the past 2 weeks."
    },
    {
      "linkId": "24108967-2ff3-40d0-c54f-a7b97bb84d05",
      "type": "choice",
      "text": "In the last two weeks, how much has your dizziness affected you?",
      "required": true,
      "answerOption": [
        {
          "valueCoding": {
            "id": "80df26e0-55a6-4d50-90de-b60cd851c4fb",
            "code": "1",
            "system": "urn:uuid:2b2f9a9a-e721-495a-82d6-fbc1b22a27b2",
            "display": "Extremely bothersome"
          }
        },
        {
          "valueCoding": {
            "id": "473f99f2-b9d2-4e92-85ed-ca78f3e0141c",
            "code": "2",
            "system": "urn:uuid:2b2f9a9a-e721-495a-82d6-fbc1b22a27b2",
            "display": "Quite a bit bothersome"
          }
        },
        {
          "valueCoding": {
            "id": "e3eab124-44f9-43a4-b1f6-e42b0199e39b",
            "code": "3",
            "system": "urn:uuid:2b2f9a9a-e721-495a-82d6-fbc1b22a27b2",
            "display": "Moderately bothersome"
          }
        },
        {
          "valueCoding": {
            "id": "253f487a-f26b-442f-8757-8a2401465fed",
            "code": "4",
            "system": "urn:uuid:2b2f9a9a-e721-495a-82d6-fbc1b22a27b2",
            "display": "Slightly bothersome"
          }
        },
        {
          "valueCoding": {
            "id": "7de45367-76b2-4b9b-8e4e-050bae6b309c",
            "code": "5",
            "system": "urn:uuid:2b2f9a9a-e721-495a-82d6-fbc1b22a27b2",
            "display": "Not at all bothersome"
          }
        },
        {
          "valueCoding": {
            "id": "3d1a8f9b-e666-4ad4-9202-571313a2b5d1",
            "code": "6",
            "system": "urn:uuid:2b2f9a9a-e721-495a-82d6-fbc1b22a27b2",
            "display": "I've had no dizziness"
          }
        }
      ]
    }
  ]
}
"""
