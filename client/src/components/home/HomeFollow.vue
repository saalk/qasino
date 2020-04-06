<template>
  <div class="home-follow">
    <!-- List of quizzes filtered either
    - ALL | FAV | FOLLOWED
    - home-all: list All with no filter             (quiz all)
    - home-favorite: list Favorited quizzes         (quiz is on your favorite list)
    - home-follow: list by Followed authors by you  (quiz.author is in your authors)

    - TAG
    - home-tag: list by specific Tag                (quiz.tagList contains tag)

    - MINE | AUTHOR
    - profile-user: list for you                    (quiz.author is you)
    - profile-quizzes: list for an author           (quiz.author is specific author)
    -->
    <QuizList type="following" />
  </div>
</template>

<script>
import QuizList from 'src/components/quiz/QuizList';

export default {
  name: 'home-follow',
  components: {
    QuizList,
  },
};
</script>
