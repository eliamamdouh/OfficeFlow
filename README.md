# OfficeFlow

A mobile application that automates work schedules, sends reminders, handles schedule change requests, and leverages generative AI for enhanced functionality.

## Badges

[![Kotlin](https://img.shields.io/badge/kotlin-%237F52FF.svg?style=for-the-badge&logo=kotlin&logoColor=white)](https://kotlinlang.org/)
[![Jetpack Compose](https://img.shields.io/badge/Jetpack_Compose-%4285F4.svg?style=for-the-badge&logo=JetpackCompose&logoColor=white)](https://www.jetpackcompose.net/)
[![JavaScript](https://img.shields.io/badge/JavaScript-yellow?style=for-the-badge&logo=JavaScript&logoColor=white)](https://www.javascript.com/)
[![NodeJS](https://img.shields.io/badge/node.js-6DA55F?style=for-the-badge&logo=node.js&logoColor=white)](https://nodejs.org/en/)
[![Python](https://img.shields.io/badge/python-3670A0?style=for-the-badge&logo=python&logoColor=white)](https://www.python.org/)
[![Firebase](https://img.shields.io/badge/firebase-a08021?style=for-the-badge&logo=firebase&logoColor=white)](https://firebase.google.com/)
[![ChatGPT API](https://img.shields.io/badge/chatGPT_API-74aa9c?style=for-the-badge&logo=openai&logoColor=white)](https://chatgpt.com)
[![Git](https://img.shields.io/badge/git-%23F05033.svg?style=for-the-badge&logo=git&logoColor=white)](https://git-scm.com)
[![GitHub](https://img.shields.io/badge/github-%23121011.svg?style=for-the-badge&logo=github&logoColor=white)](https://github.com)
[![JWT](https://img.shields.io/badge/JWT-black?style=for-the-badge&logo=JSON%20web%20tokens)](https://jwt.io)
[![Gradle](https://img.shields.io/badge/Gradle-02303A.svg?style=for-the-badge&logo=Gradle&logoColor=white)](https://gradle.org/)
[![NPM](https://img.shields.io/badge/NPM-%23CB3837.svg?style=for-the-badge&logo=npm&logoColor=white)](https://www.npmjs.com/)
[![Express.js](https://img.shields.io/badge/express.js-%23404d59.svg?style=for-the-badge&logo=express&logoColor=%2361DAFB)](https://expressjs.com/)
[![Nodemon](https://img.shields.io/badge/NODEMON-%23323330.svg?style=for-the-badge&logo=nodemon&logoColor=%BBDEAD)](https://nodemon.io/)
[![Flask](https://img.shields.io/badge/flask-%23000.svg?style=for-the-badge&logo=flask&logoColor=white)]([https://prettier.io](https://flask.palletsprojects.com/))
[![VSCode](https://custom-icon-badges.demolab.com/badge/-VSCode-blue?style=for-the-badge&logo=vscode-alt&logoColor=white)](https://code.visualstudio.com/)
[![Android Studio](https://img.shields.io/badge/android%20studio-346ac1?style=for-the-badge&logo=android%20studio&logoColor=white)](https://developer.android.com/studio)
[![Postman](https://img.shields.io/badge/Postman-FF6C37?style=for-the-badge&logo=postman&logoColor=white)](https://www.postman.com/)
[![Docker](https://img.shields.io/badge/docker-%230db7ed.svg?style=for-the-badge&logo=docker&logoColor=white)](https://www.docker.com/)
[![Microsoft Azure](https://custom-icon-badges.demolab.com/badge/-Microsoft_Azure-0080ff?style=for-the-badge&logo=azure-2&logoColor=white)](https://azure.microsoft.com/en-us)
[![Ubuntu](https://img.shields.io/badge/Ubuntu-E95420?style=for-the-badge&logo=ubuntu&logoColor=white)](https://ubuntu.com/)
[![Figma](https://img.shields.io/badge/figma-%23F24E1E.svg?style=for-the-badge&logo=figma&logoColor=white)](https://www.figma.com/)
[![Canva](https://img.shields.io/badge/Canva-%2300C4CC.svg?style=for-the-badge&logo=Canva&logoColor=white)](https://www.canva.com/)
[![Prettier](https://img.shields.io/badge/prettier-1A2C34?style=for-the-badge&logo=prettier&logoColor=F7BA3E)](https://prettier.io)
[![Stack Overflow](https://img.shields.io/badge/-Stack_Overflow-FE7A16?style=for-the-badge&logo=stack-overflow&logoColor=white)](https://www.stackoverflow.com)
[![Medium.com](https://img.shields.io/badge/Medium.com-12100E?style=for-the-badge&logo=medium&logoColor=white)](https://medium.com/)
[![Dev Community](https://img.shields.io/badge/Dev_Community-0A0A0A?style=for-the-badge&logo=dev.to&logoColor=white)](https://dev.to/)
[![Youtube](https://img.shields.io/badge/YouTube-FF0000?style=for-the-badge&logo=youtube&logoColor=white)](https://www.youtube.com)

## Motivation

Bringing _convenience_, _speed_, and _ease of use_ to the forefront of viewing and changing schedules is our primary objective.

Our application brings a very straight-forward way for Employees to interact with Managers and submit change date requests for themselves. We also provide the necessary tools for Managers to operate the system effectively and efficiently. All of our features are implemented with the user's convenience at mind, and are designed to be intuitive and easy to learn.

We provide all the necessary features that our user types require and eliminate any and all unnecessary clutter, simplifying the processes as much as possible and making it possible to learn and use the system at an extremely rapid rate.

## Screenshots

<details>
<summary>Login</summary>

![Login](Docs/Images/Screenshots/Login.png)

</details>

<details>
<summary>Schedule</summary>

![Schedule](Docs/Images/Screenshots/Schedule.png)

</details>

<details>
<summary>Schedule Change Requests</summary>

![ScheduleChangeRequests](Docs/Images/Screenshots/ScheduleChangeRequests.png)

</details>

<details>
<summary>My Requests</summary>

![My Requests](Docs/Images/Screenshots/MyRequests.png)

</details>

<details>
<summary>Notification</summary>

![Notification](Docs/Images/Screenshots/Notification.png)

</details>

<details>
<summary>Chat Bot</summary>

![Chat Bot](Docs/Images/Screenshots/ChatBot.png)

</details>

<details>
<summary>Team Schedule</summary>

![Team Schedule](Docs/Images/Screenshots/TeamSchedule.png)

</details>

<details>
<summary>Requests</summary>

![Requests](Docs/Images/Screenshots/Requests.png)

</details>

<details>
<summary>Analytics</summary>

![Analytics](Docs/Images/Screenshots/Analytics.png)

</details>

## Features

The system has three user types: **_Employee_**, **_Manager_**, and **_Super Manager_**.

<details>

 <summary> Employee features </summary>

- **Schedule Management:**
  - View personal work schedule with details of days in the office and work-from-home days.
  - Submit/Cancel schedule change requests between a day in office and a day in home.
  - Receive notifications about the status of schedule change requests.

- **NLP Chatbot Interaction:**
  - Use the chatbot to handle schedule change requests in natural language.
  - Get automated responses to common scheduling questions.

- **Notification Management:**
  - Receive personalized notifications when schedule change requests are accepted or rejected.

</details>

<details>

 <summary> Manager features </summary>

- **Personal Schedule Management:**
  - View personal work schedule with details of days in the office and work-from-home days.
  - Submit/Cancel schedule change requests between a day in office and a day in home.
  - Receive notifications about the status of schedule change requests.

- **Team Schedule Management:**
  - View and manage the schedules of team members.
  - Adjust team members' schedules as needed.

- **Request Management:**
  - Approve or reject schedule change requests from employees within the team.
  - Submit personal schedule change requests that can be approved or rejected by the Super Manager.

- **NLP Chatbot Interaction:**
  - Use the chatbot to handle personal chedule change requests.
  - Get automated responses to common scheduling questions.

- **Notification Management:**
  - Receive personalized notifications when schedule change requests are accepted or rejected.

- **Dashboard:**
  - Access a dashboard to view team schedules and manage requests efficiently.

</details>

<details>

 <summary> Super Manager features </summary>

- **Personal Schedule Management:**
  - View personal work schedule with details of days in the office and work-from-home days.
  - Submit/Cancel schedule change requests between a day in office and a day in home.
  - Receive notifications about the status of schedule change requests.

- **Global Schedule Management:**
  - View and manage schedules for all employees and managers in the company.
  - Adjust company-wide work-from-home and office-day patterns.

- **Request Management:**
  - Approve or reject schedule change requests from both employees and managers.
  - Submit personal schedule change requests.

- **NLP Chatbot Interaction:**
  - Use the chatbot to handle personal schedule change requests.
  - Get automated responses to common scheduling questions.

- **Sentiment Analysis and Feedback:**
  - Analyze feedback to improve scheduling processes.
  - Access analytics on the number of accepted, rejected, and pending requests, as well as the current office occupancy.

- **Dashboard:**
  - Access a comprehensive dashboard to manage schedules and requests across the entire organization.
  - View detailed analytics and reports.

</details>

## Installation & Running the App

To install the project with `npm`, run the following commands in order.

```bash
> git clone https://github.com/eliamamdouh/OfficeFlow.git
> cd Backend
> cd src
> npm i package.json
> npm i
```
To run the backebnd, run the following command:

```bash
> node app.js
```

or this command:

```bash
> npm run dev
```
To run the frontend, you can use android studio UI (run button).

## Authors

- [@OmarAhmedAdel](https://github.com/OmarAhmedAdel)
- [@eliamamdouh](https://github.com/eliamamdouh)
- [@sama241](https://github.com/sama241)
- [@rawanelashmawy](https://github.com/rawanelashmawy)
- [@Engy13](https://github.com/Engy13)
