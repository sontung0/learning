/*
 * Reusable self-checking quiz widget for Spring Integration lessons.
 *
 * Usage in a lesson:
 *
 *   <div class="quiz" data-quiz>
 *     <p class="quiz-q">What decouples producers from consumers?</p>
 *     <ul class="quiz-options">
 *       <li data-correct="true">A message channel</li>
 *       <li>A service activator</li>
 *       <li>A message endpoint</li>
 *       <li>A transformer</li>
 *     </ul>
 *     <p class="quiz-explain">Channels are the "pipes" of pipes-and-filters.</p>
 *   </div>
 *
 * Then include this script once, near the end of the <body>:
 *   <script src="../assets/quiz.js"></script>
 *
 * Behaviour:
 *  - Click an option to answer. Correct = green, wrong = red, and the
 *    correct answer is revealed. The explanation (if present) appears.
 *  - Answers can be changed until correct; a small progress line at the top
 *    tracks how many questions have been answered correctly.
 */
(function () {
  "use strict";

  function markCorrect(el) {
    el.classList.add("quiz-correct");
    el.setAttribute("aria-label", "correct answer");
  }

  function markWrong(el) {
    el.classList.add("quiz-wrong");
  }

  function setupQuiz(quiz) {
    var options = Array.prototype.slice.call(
      quiz.querySelectorAll(".quiz-options li")
    );
    var explain = quiz.querySelector(".quiz-explain");
    if (explain) {
      explain.style.display = "none";
    }
    var solved = false;

    options.forEach(function (opt) {
      opt.setAttribute("role", "button");
      opt.setAttribute("tabindex", "0");

      function choose() {
        if (solved) {
          return;
        }
        var isCorrect = opt.getAttribute("data-correct") === "true";
        if (isCorrect) {
          markCorrect(opt);
          solved = true;
          quiz.classList.add("quiz-solved");
          if (explain) {
            explain.style.display = "block";
          }
          updateProgress();
        } else {
          markWrong(opt);
          // Reveal the correct one so the learner sees the target.
          options.forEach(function (o) {
            if (o.getAttribute("data-correct") === "true") {
              markCorrect(o);
            }
          });
          if (explain) {
            explain.style.display = "block";
          }
        }
      }

      opt.addEventListener("click", choose);
      opt.addEventListener("keydown", function (e) {
        if (e.key === "Enter" || e.key === " ") {
          e.preventDefault();
          choose();
        }
      });
    });
  }

  var progressEl = null;
  var total = 0;

  function updateProgress() {
    if (!progressEl) {
      return;
    }
    var solvedCount = document.querySelectorAll(".quiz.quiz-solved").length;
    progressEl.textContent =
      "Retrieval progress: " + solvedCount + " / " + total + " correct";
    if (solvedCount === total && total > 0) {
      progressEl.textContent += " — all clear. Well done.";
      progressEl.classList.add("quiz-progress-done");
    }
  }

  document.addEventListener("DOMContentLoaded", function () {
    var quizzes = Array.prototype.slice.call(
      document.querySelectorAll("[data-quiz]")
    );
    total = quizzes.length;

    if (total > 0) {
      var host = document.querySelector(".assessment") || document.body;
      progressEl = document.createElement("p");
      progressEl.className = "quiz-progress";
      progressEl.textContent = "Retrieval progress: 0 / " + total + " correct";
      host.insertBefore(progressEl, host.firstChild);
    }

    quizzes.forEach(setupQuiz);
  });
})();
