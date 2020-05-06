<template>
  <div class="home-tag">
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
    <SearchQuizList :tag="tag"></SearchQuizList>
  </div>
</template>

<script>
import SearchQuizList from 'src/components/search/SearchQuizList';

export default {
  name: 'home-tag',
  components: {
    SearchQuizList,
  },
  computed: {
    tag() {
      return this.$route.params.tag;
    },
  },
};
</script>
