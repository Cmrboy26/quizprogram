console.log('playscript.js loaded');

const responseMap = new Map();
const categoryMap = new Map();
const difficulties = new Map();

var quizForm;
var quizData;
var submitButton;

fetch('/api/response-types')
    .then(response => response.json())
    .then(data => {
        Object.entries(data).forEach(([id, name]) => {
            responseMap.set(Number(id), name);
            console.log(`Response Type Loaded: ID=${id}, Name=${name}`);
        });
    })
    .catch(error => {
        console.error('Error fetching response types:', error);
    });

fetch('/api/difficulties')
    .then(response => response.json())
    .then(data => {
        Object.entries(data).forEach(([id, name]) => {
            difficulties.set(Number(id), name);
            console.log(`Difficulty Loaded: ID=${id}, Name=${name}`);
        });
    })
    .catch(error => {
        console.error('Error fetching difficulties:', error);
    });

fetch('/api/categories')
    .then(response => response.json())
    .then(data => {
        Object.entries(data).forEach(([id, name]) => {
            categoryMap.set(Number(id), name);
            console.log(`Category Loaded: ID=${id}, Name=${name}`);
        });
    })
    .catch(error => {
        console.error('Error fetching categories:', error);
    });

document.addEventListener('DOMContentLoaded', function() {
    const playRoot = document.getElementById('play-root');
    const pathVariables = getPathVariables();
    const quizId = pathVariables['id'];
    console.log(pathVariables);
    if (quizId) {
        fetch(`/api/quiz/get/${quizId}`)
            .then(response => {
                if (!response.ok) {
                    throw new Error(`${response.status}`);
                }
                return response.json();
            })
            .then(quizJson => {
                onQuizLoaded(quizJson);
            })
            .catch(error => {
                console.error('Error fetching quiz data:', error);
                playRoot.innerHTML = `<p>Error: ${error.message || error}. Please try again later.</p>`;
            });
    } else {
        playRoot.innerHTML = '<p>Error loading quiz: No quiz ID provided in the URL.</p>';
    }
});

function onQuizLoaded(quizJson) {
    console.log('Quiz data loaded:', quizJson);

    quizData = quizJson;

    const playRoot = document.getElementById('play-root');
    playRoot.innerHTML = `<h2>${quizJson.name}</h2>
                          <p>${quizJson.description}</p>`;
    const questionsContainer = document.createElement('form');
    questionsContainer.id = 'questions-container';
    quizForm = questionsContainer;

    quizJson.questions.forEach(question => {
        const questionElement = createQuestionElement(question);
        questionsContainer.appendChild(questionElement);
    });
    playRoot.appendChild(questionsContainer);

    submitButton = document.createElement('button');
    submitButton.textContent = 'Submit Quiz';
    submitButton.type = 'submit';
    submitButton.addEventListener('click', onQuizSubmit);

    questionsContainer.appendChild(submitButton);
}

function onQuizSubmit(event) {
    event.preventDefault();
    console.log('Quiz submitted');
    
    // Show all the form data
    const formData = new FormData(quizForm);
    const formObject = {};
    for (const [key, value] of formData.entries()) {
        const longValue = Number(value);
        if (formObject[key]) {
            formObject[key].push(longValue);
        } else {
            formObject[key] = [longValue];
        }
    }
    console.log('Form data:', JSON.stringify(formObject, null, 2));

    const requiredKeys = [];
    for (let i = 0; i < quizData.questions.length; i++) {
        requiredKeys.push(`${quizData.questions[i].order}`);
    }

    for (const key of requiredKeys) {
        if (!formObject.hasOwnProperty(key)) {
            alert('Please answer all questions before submitting the quiz. '+key+' is missing.');
            return;
        }
    }

    const submittionData = {
        quizId: quizData.quiz_id,
        answers: formObject
    };

    console.log("Submitting " + JSON.stringify(submittionData));

    submitButton.disabled = true;
    fetch('/api/quiz/submit', {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json'
        },
        body: JSON.stringify(submittionData)
    })
    .then(response => {
        if (!response.ok) {
            submitButton.disabled = false;
            throw new Error(`${response.status}`);
        }
        return response.json();
    })
    .then(data => {
        displayAnswerSummary(data);
    })
    .catch(error => {
        console.error('Error submitting quiz:', error);
        submitButton.disabled = false;
    });
}

function displayAnswerSummary(summary) {
    console.log('Answer Summary:', summary);

    score = summary.score;
    incorrectQuestions = summary.incorrect_questions;
    correctQuestions = summary.correct_questions;
    total = incorrectQuestions.length + correctQuestions.length;

    const questionDiv = document.getElementById('questions-container');
    questionDiv.innerHTML = '<h2>Quiz Results</h2>';
    incorrectQuestions.forEach(questionId => {
        const fullQuestion = quizData.questions.find(q => q.question_id === questionId);
        if (!fullQuestion) return;
        const questionElement = createQuestionElement(fullQuestion, false);
        questionDiv.appendChild(questionElement);
        questionDiv.innerHTML += '<p class="incorrect">Incorrect</p><hr>';
    });
    correctQuestions.forEach(questionId => {
        const fullQuestion = quizData.questions.find(q => q.question_id === questionId);
        if (!fullQuestion) return;
        const questionElement = createQuestionElement(fullQuestion, false);
        questionDiv.appendChild(questionElement);
        questionDiv.innerHTML += '<p class="correct">Correct</p><hr>';
    });
    questionDiv.innerHTML += `<p>Your Score: ${summary.score} (${correctQuestions.length} / ${total})</p>`;
    if (summary.score_submitted) {
        questionDiv.innerHTML += `<p>Your score has been recorded.</p>`;
    } else {
        questionDiv.innerHTML += `<p>Your score was NOT recorded. Please log in to record scores.</p>`;
    }

    const restartButton = document.createElement('button');
    restartButton.textContent = 'Retake Quiz';
    restartButton.onclick = (event) => {
        event.preventDefault();
        window.location.href = '/play.html?id=' + quizData.quiz_id;
    };
    questionDiv.appendChild(restartButton);

    const homeButton = document.createElement('button');
    homeButton.textContent = 'Back to Home';
    homeButton.onclick = (event) => {
        event.preventDefault();
        window.location.href = '/home.html';
    };
    questionDiv.appendChild(homeButton);
}

function createQuestionElement(question, allowAnswers = true) {
    const questionDiv = document.createElement('div');
    questionDiv.className = 'question';
    
    const informationDiv = document.createElement('div');
    questionDiv.appendChild(informationDiv);

    questionDiv.innerHTML = `<h3>${question.order}. ${question.prompt}</h3>`;

    if (allowAnswers) {
        const responseTypeId = question.response_type_id;
        console.log('Response Type ID:', responseTypeId);
        const responseTypeName = responseMap.get(responseTypeId) || '?';
        informationDiv.innerHTML += `<p>Response Type: ${responseTypeName}</p>`;

        const categoryId = question.category_id;
        console.log('Category ID:', categoryId);
        const categoryName = categoryMap.get(categoryId) || '?';
        informationDiv.innerHTML += `<p>Category: ${categoryName}</p>`;

        const difficultyId = question.difficulty_id;
        console.log('Difficulty ID:', difficultyId);
        var difficultyName = difficulties.get(difficultyId) || '?';
        difficultyName = difficultyName.toUpperCase();
        informationDiv.innerHTML += `<p>Difficulty: ${difficultyName}</p>`;

        const responseDiv = document.createElement('div');
        questionDiv.appendChild(responseDiv);

        if (responseTypeName === 'Multiple Choice') {
            // Format with list of responses

            const multipleChoiceFieldset = document.createElement('fieldset');
            multipleChoiceFieldset.id = `${question.order}`;
            responseDiv.appendChild(multipleChoiceFieldset);

            question.responses.forEach(response => {
                const responseLabel = document.createElement('label');
                responseLabel.innerHTML = `<input type="radio" name="${question.order}" value="${response.response_id}"> ${response.text}`;
                multipleChoiceFieldset.appendChild(responseLabel);
                multipleChoiceFieldset.appendChild(document.createElement('br'));
            });
        } else if (responseTypeName === 'True False') {
            // Format with radio buttons

            const trueFalseFieldset = document.createElement('fieldset');
            trueFalseFieldset.id = `${question.order}`;
            responseDiv.appendChild(trueFalseFieldset);

            question.responses.forEach(response => {
                const responseLabel = document.createElement('label');
                responseLabel.innerHTML = `<input type="radio" name="${question.order}" value="${response.response_id}"> ${response.text}`;
                trueFalseFieldset.appendChild(responseLabel);
                trueFalseFieldset.appendChild(document.createElement('br'));
            });
        } else if (responseTypeName === 'Multiple Select') {
            // Format with check boxes
            const multipleSelectFieldset = document.createElement('fieldset');
            multipleSelectFieldset.id = `${question.order}`;
            responseDiv.appendChild(multipleSelectFieldset);

            question.responses.forEach(response => {
                const responseLabel = document.createElement('label');
                responseLabel.innerHTML = `<input type="checkbox" name="${question.order}" value="${response.response_id}"> ${response.text}`;
                multipleSelectFieldset.appendChild(responseLabel);
                multipleSelectFieldset.appendChild(document.createElement('br'));
            });
        } else {
            // The response type is not supported
            responseDiv.innerHTML += '<p>Unsupported response type.</p>';
        }
    }

    return questionDiv;
}

function getPathVariables() {
    const params = new URLSearchParams(window.location.search);
    const pathVariables = {};
    
    for (const [key, value] of params.entries()) {
        pathVariables[key] = value;
    }
    
    return pathVariables;
}