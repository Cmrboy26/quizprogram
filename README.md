# SimpleQuizApp

## Objective

Create a web app that allows the user to take a quiz on a topic of their choice.
The app should fetch questions and answers from an external API, such as 
<a href="https://opentdb.com/">Open Trivia Database</a>. The app should also
keep track of the user's score and display feeback at the end of the quiz.

## Approach

I decided to design this program using Spring Boot, a Java framework intended for
building production-grade web applications and microservices. The application will be accessible on the web. Persistent information will be stored
in an in-memory H2 relational database.

# Design

## Table Schema

<img src="github_img/quiz_schema.drawio.png">

# Usage

```POST quiz/generate```
<ul>
    <li>Generates a new quiz to be played</li>
</ul>